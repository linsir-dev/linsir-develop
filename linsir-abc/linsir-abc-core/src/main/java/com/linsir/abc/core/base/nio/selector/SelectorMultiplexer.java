package com.linsir.abc.core.base.nio.selector;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

/**
 * 选择器多路复用器
 * 演示Selector的使用，实现单线程处理多个通道
 *
 * 设计要点：
 * 1. Selector可以同时监控多个Channel的IO事件
 * 2. 通过SelectionKey获取就绪的Channel
 * 3. 单线程可以处理大量连接（Reactor模式）
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class SelectorMultiplexer {

    private Selector selector;
    private boolean running = false;

    /**
     * 初始化Selector
     *
     * @throws IOException 当IO操作失败时
     */
    public void init() throws IOException {
        selector = Selector.open();
    }

    /**
     * 注册ServerSocketChannel
     *
     * @param serverChannel ServerSocketChannel
     * @throws IOException 当IO操作失败时
     */
    public void registerServerChannel(ServerSocketChannel serverChannel) throws IOException {
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 注册SocketChannel
     *
     * @param channel SocketChannel
     * @param ops 感兴趣的操作
     * @throws IOException 当IO操作失败时
     */
    public void registerChannel(SocketChannel channel, int ops) throws IOException {
        channel.configureBlocking(false);
        channel.register(selector, ops);
    }

    /**
     * 启动选择循环
     */
    public void start() {
        running = true;

        while (running) {
            try {
                // 阻塞等待就绪的通道（超时1秒）
                int readyChannels = selector.select(1000);

                if (readyChannels == 0) {
                    continue;
                }

                // 获取就绪的SelectionKey集合
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    try {
                        if (key.isAcceptable()) {
                            handleAccept(key);
                        } else if (key.isReadable()) {
                            handleRead(key);
                        } else if (key.isWritable()) {
                            handleWrite(key);
                        } else if (key.isConnectable()) {
                            handleConnect(key);
                        }
                    } catch (IOException e) {
                        System.err.println("处理通道错误: " + e.getMessage());
                        key.cancel();
                        try {
                            key.channel().close();
                        } catch (IOException ex) {
                            // ignore
                        }
                    }

                    // 移除已处理的key
                    keyIterator.remove();
                }

            } catch (IOException e) {
                System.err.println("Selector错误: " + e.getMessage());
            }
        }
    }

    /**
     * 处理接受连接事件
     */
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();

        System.out.println("接受连接: " + clientChannel.getRemoteAddress());

        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 处理读事件
     */
    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int bytesRead = channel.read(buffer);

        if (bytesRead == -1) {
            // 连接关闭
            System.out.println("连接关闭: " + channel.getRemoteAddress());
            key.cancel();
            channel.close();
            return;
        }

        if (bytesRead > 0) {
            buffer.flip();
            String message = StandardCharsets.UTF_8.decode(buffer).toString();
            System.out.println("收到消息: " + message.trim());

            // 准备写回响应
            key.attach("Echo: " + message.trim());
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    /**
     * 处理写事件
     */
    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        String response = (String) key.attachment();

        if (response != null) {
            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
            channel.write(buffer);

            // 写完后改回读模式
            key.interestOps(SelectionKey.OP_READ);
            key.attach(null);
        }
    }

    /**
     * 处理连接完成事件
     */
    private void handleConnect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }

        System.out.println("连接完成: " + channel.getRemoteAddress());
        channel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 停止选择循环
     */
    public void stop() {
        running = false;
        selector.wakeup();
    }

    /**
     * 关闭Selector
     */
    public void close() {
        stop();
        try {
            selector.close();
        } catch (IOException e) {
            System.err.println("关闭Selector错误: " + e.getMessage());
        }
    }

    /**
     * 获取Selector
     */
    public Selector getSelector() {
        return selector;
    }

    /**
     * 演示Selector的使用
     */
    public static void demonstrate() {
        System.out.println("=== Selector多路复用演示 ===\n");

        SelectorMultiplexer multiplexer = new SelectorMultiplexer();

        try {
            multiplexer.init();

            // 创建服务端Channel
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(12346));

            // 注册到Selector
            multiplexer.registerServerChannel(serverChannel);

            System.out.println("Selector服务端启动，监听端口: 12346");
            System.out.println("在另一个终端运行: telnet localhost 12346");
            System.out.println("或直接运行演示客户端...\n");

            // 启动客户端连接（用于演示）
            Thread clientThread = new Thread(() -> {
                try {
                    Thread.sleep(500);

                    SocketChannel client = SocketChannel.open();
                    client.connect(new InetSocketAddress("localhost", 12346));

                    // 发送消息
                    client.write(ByteBuffer.wrap("Hello from Client!".getBytes()));

                    // 接收响应
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    client.read(buffer);
                    buffer.flip();
                    String response = StandardCharsets.UTF_8.decode(buffer).toString();
                    System.out.println("客户端收到响应: " + response.trim());

                    client.close();
                } catch (Exception e) {
                    System.err.println("客户端错误: " + e.getMessage());
                }
            });
            clientThread.start();

            // 运行Selector一段时间
            Thread selectorThread = new Thread(multiplexer::start);
            selectorThread.start();

            // 运行3秒后停止
            Thread.sleep(3000);
            multiplexer.stop();
            selectorThread.join(1000);

            serverChannel.close();

        } catch (IOException | InterruptedException e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
