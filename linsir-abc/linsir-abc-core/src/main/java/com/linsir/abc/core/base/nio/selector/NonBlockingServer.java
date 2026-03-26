package com.linsir.abc.core.base.nio.selector;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 非阻塞服务器
 * 演示基于NIO的非阻塞服务器实现
 *
 * 设计要点：
 * 1. 使用Selector实现单线程处理多连接
 * 2. 非阻塞IO避免线程阻塞
 * 3. 可扩展的Reactor模式
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class NonBlockingServer {

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private ExecutorService executor;
    private volatile boolean running = false;
    private int port;

    // 客户端状态管理
    private Map<SocketChannel, ClientState> clientStates = new ConcurrentHashMap<>();

    public NonBlockingServer(int port) {
        this.port = port;
    }

    /**
     * 启动服务器
     */
    public void start() throws IOException {
        // 创建Selector
        selector = Selector.open();

        // 创建服务端Channel
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 创建工作线程池（用于处理业务逻辑）
        executor = Executors.newFixedThreadPool(4);

        running = true;
        System.out.println("非阻塞服务器启动，监听端口: " + port);

        // 主循环
        while (running) {
            try {
                // 阻塞等待就绪事件（超时100ms以便检查running状态）
                selector.select(100);

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    try {
                        if (key.isAcceptable()) {
                            accept(key);
                        } else if (key.isReadable()) {
                            read(key);
                        } else if (key.isWritable()) {
                            write(key);
                        }
                    } catch (IOException e) {
                        System.err.println("处理连接错误: " + e.getMessage());
                        closeChannel(key);
                    }
                }
            } catch (IOException e) {
                if (running) {
                    System.err.println("Selector错误: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 接受新连接
     */
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();

        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);

        clientStates.put(client, new ClientState());

        System.out.println("新连接: " + client.getRemoteAddress());
    }

    /**
     * 读取数据
     */
    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientState state = clientStates.get(channel);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(buffer);

        if (bytesRead == -1) {
            // 客户端关闭连接
            System.out.println("连接关闭: " + channel.getRemoteAddress());
            closeChannel(key);
            return;
        }

        if (bytesRead > 0) {
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            state.appendData(data);

            // 检查是否收到完整消息（以\n结尾）
            String message = state.getMessage();
            if (message != null) {
                System.out.println("收到消息 [" + channel.getRemoteAddress() + "]: " + message.trim());

                // 异步处理消息
                executor.submit(() -> {
                    String response = processMessage(message);
                    state.setPendingWrite(response);

                    // 注册写事件
                    key.interestOps(SelectionKey.OP_WRITE);
                    selector.wakeup();
                });
            }
        }
    }

    /**
     * 写入数据
     */
    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ClientState state = clientStates.get(channel);

        String pendingWrite = state.getPendingWrite();
        if (pendingWrite != null) {
            ByteBuffer buffer = ByteBuffer.wrap(pendingWrite.getBytes(StandardCharsets.UTF_8));
            channel.write(buffer);

            state.clearPendingWrite();

            // 写完后改回读模式
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    /**
     * 处理消息（业务逻辑）
     */
    private String processMessage(String message) {
        message = message.trim();

        if (message.equalsIgnoreCase("time")) {
            return new Date().toString() + "\n";
        } else if (message.equalsIgnoreCase("hello")) {
            return "Hello, Client!\n";
        } else if (message.equalsIgnoreCase("bye")) {
            return "Goodbye!\n";
        } else {
            return "Echo: " + message + "\n";
        }
    }

    /**
     * 关闭Channel
     */
    private void closeChannel(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        clientStates.remove(channel);
        key.cancel();

        try {
            channel.close();
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * 停止服务器
     */
    public void stop() {
        running = false;
        selector.wakeup();

        try {
            if (serverChannel != null) {
                serverChannel.close();
            }
            if (selector != null) {
                selector.close();
            }
            if (executor != null) {
                executor.shutdown();
            }
        } catch (IOException e) {
            System.err.println("关闭服务器错误: " + e.getMessage());
        }

        System.out.println("服务器已停止");
    }

    /**
     * 客户端状态
     */
    private static class ClientState {
        private StringBuilder readBuffer = new StringBuilder();
        private String pendingWrite;

        public void appendData(byte[] data) {
            readBuffer.append(new String(data, StandardCharsets.UTF_8));
        }

        public String getMessage() {
            int index = readBuffer.indexOf("\n");
            if (index >= 0) {
                String message = readBuffer.substring(0, index + 1);
                readBuffer.delete(0, index + 1);
                return message;
            }
            return null;
        }

        public void setPendingWrite(String message) {
            this.pendingWrite = message;
        }

        public String getPendingWrite() {
            return pendingWrite;
        }

        public void clearPendingWrite() {
            this.pendingWrite = null;
        }
    }

    /**
     * 演示非阻塞服务器
     */
    public static void demonstrate() {
        System.out.println("=== 非阻塞服务器演示 ===\n");

        NonBlockingServer server = new NonBlockingServer(12347);

        // 启动服务器
        Thread serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                System.err.println("服务器启动错误: " + e.getMessage());
            }
        });
        serverThread.start();

        // 等待服务器启动
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 启动测试客户端
        Thread clientThread = new Thread(() -> {
            try {
                // 客户端1
                testClient("Client1", new String[]{"hello", "time", "test message", "bye"});

                Thread.sleep(200);

                // 客户端2
                testClient("Client2", new String[]{"Hello from Client2", "time"});

            } catch (Exception e) {
                System.err.println("客户端错误: " + e.getMessage());
            }
        });
        clientThread.start();

        // 等待演示完成
        try {
            clientThread.join(5000);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 停止服务器
        server.stop();

        try {
            serverThread.join(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 测试客户端
     */
    private static void testClient(String name, String[] messages) throws IOException {
        try (SocketChannel channel = SocketChannel.open()) {
            channel.connect(new InetSocketAddress("localhost", 12347));

            for (String message : messages) {
                // 发送消息
                channel.write(ByteBuffer.wrap((message + "\n").getBytes()));

                // 读取响应
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                channel.read(buffer);
                buffer.flip();
                String response = StandardCharsets.UTF_8.decode(buffer).toString();

                System.out.println(name + " 收到: " + response.trim());

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
