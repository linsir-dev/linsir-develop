package com.linsir.abc.core.base.nio.channel;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;

/**
 * Socket通道通信
 * 演示SocketChannel的使用，包括阻塞和非阻塞模式
 *
 * 设计要点：
 * 1. SocketChannel支持阻塞和非阻塞模式
 * 2. 可以直接读写ByteBuffer
 * 3. 配合Selector实现多路复用
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class SocketChannelCommunication {

    private static final int BUFFER_SIZE = 1024;

    /**
     * 创建客户端SocketChannel
     *
     * @param host 主机地址
     * @param port 端口号
     * @return SocketChannel
     * @throws IOException 当IO操作失败时
     */
    public SocketChannel createClient(String host, int port) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(true);
        channel.connect(new InetSocketAddress(host, port));
        return channel;
    }

    /**
     * 发送消息
     *
     * @param channel SocketChannel
     * @param message 消息内容
     * @throws IOException 当IO操作失败时
     */
    public void sendMessage(SocketChannel channel, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    /**
     * 接收消息
     *
     * @param channel SocketChannel
     * @return 消息内容
     * @throws IOException 当IO操作失败时
     */
    public String receiveMessage(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        StringBuilder message = new StringBuilder();

        int bytesRead;
        while ((bytesRead = channel.read(buffer)) > 0) {
            buffer.flip();
            message.append(StandardCharsets.UTF_8.decode(buffer));
            buffer.clear();
        }

        return message.toString();
    }

    /**
     * 创建服务端ServerSocketChannel
     *
     * @param port 端口号
     * @return ServerSocketChannel
     * @throws IOException 当IO操作失败时
     */
    public ServerSocketChannel createServer(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(true);
        serverChannel.bind(new InetSocketAddress(port));
        return serverChannel;
    }

    /**
     * 简单的Echo服务端
     *
     * @param port 端口号
     */
    public void startEchoServer(int port) {
        try (ServerSocketChannel serverChannel = createServer(port)) {
            System.out.println("Echo服务端启动，监听端口: " + port);

            while (true) {
                SocketChannel clientChannel = serverChannel.accept();
                System.out.println("客户端连接: " + clientChannel.getRemoteAddress());

                // 处理客户端请求
                handleClient(clientChannel);
            }
        } catch (IOException e) {
            System.err.println("服务端错误: " + e.getMessage());
        }
    }

    /**
     * 处理客户端连接
     */
    private void handleClient(SocketChannel clientChannel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (clientChannel.read(buffer) > 0) {
                buffer.flip();

                // 解码消息
                String received = StandardCharsets.UTF_8.decode(buffer).toString();
                System.out.println("收到: " + received.trim());

                // Echo回显
                buffer.rewind();
                clientChannel.write(buffer);

                buffer.clear();
            }

            clientChannel.close();
            System.out.println("客户端断开连接");
        } catch (IOException e) {
            System.err.println("处理客户端错误: " + e.getMessage());
        }
    }

    /**
     * 演示客户端-服务端通信
     */
    public static void demonstrateClientServer() {
        System.out.println("=== SocketChannel通信演示 ===\n");

        int port = 12345;

        // 在后台启动服务端
        Thread serverThread = new Thread(() -> {
            SocketChannelCommunication comm = new SocketChannelCommunication();
            try (ServerSocketChannel serverChannel = comm.createServer(port)) {
                System.out.println("服务端启动，等待连接...");

                // 只接受一个连接用于演示
                SocketChannel clientChannel = serverChannel.accept();
                System.out.println("服务端: 客户端已连接");

                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                int bytesRead = clientChannel.read(buffer);

                if (bytesRead > 0) {
                    buffer.flip();
                    String message = StandardCharsets.UTF_8.decode(buffer).toString();
                    System.out.println("服务端收到: " + message.trim());

                    // 发送响应
                    String response = "Hello from Server!";
                    clientChannel.write(ByteBuffer.wrap(response.getBytes()));
                }

                clientChannel.close();
            } catch (IOException e) {
                System.err.println("服务端错误: " + e.getMessage());
            }
        });

        serverThread.start();

        // 等待服务端启动
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 启动客户端
        try {
            SocketChannelCommunication comm = new SocketChannelCommunication();
            SocketChannel clientChannel = comm.createClient("localhost", port);

            // 发送消息
            String message = "Hello from Client!";
            comm.sendMessage(clientChannel, message);
            System.out.println("客户端发送: " + message);

            // 接收响应
            clientChannel.shutdownOutput(); // 告诉服务端数据发送完毕
            String response = comm.receiveMessage(clientChannel);
            System.out.println("客户端收到: " + response.trim());

            clientChannel.close();
        } catch (IOException e) {
            System.err.println("客户端错误: " + e.getMessage());
        }

        // 等待服务端结束
        try {
            serverThread.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 演示非阻塞模式
     */
    public static void demonstrateNonBlocking() {
        System.out.println("\n=== 非阻塞模式演示 ===\n");

        try {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);

            System.out.println("SocketChannel配置为非阻塞模式");
            System.out.println("isBlocking: " + channel.isBlocking());

            // 非阻塞连接
            boolean connected = channel.connect(new InetSocketAddress("localhost", 80));
            System.out.println("连接是否立即完成: " + connected);

            if (!connected) {
                // 在非阻塞模式下，连接可能需要时间
                while (!channel.finishConnect()) {
                    System.out.println("等待连接完成...");
                    Thread.sleep(100);
                }
            }

            System.out.println("连接完成");
            channel.close();

        } catch (IOException e) {
            System.out.println("预期中的连接错误 (可能没有服务在端口80): " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 主演示方法
     */
    public static void demonstrate() {
        demonstrateClientServer();
        demonstrateNonBlocking();
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
