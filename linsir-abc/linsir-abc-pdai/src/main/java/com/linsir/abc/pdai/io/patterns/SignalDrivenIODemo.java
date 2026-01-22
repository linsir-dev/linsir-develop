package com.linsir.abc.pdai.io.patterns;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 信号驱动IO模式示例
 * 演示基于信号机制的IO模式（Java中模拟实现）
 */
public class SignalDrivenIODemo {
    private static final int PORT = 8084;
    
    /**
     * 启动信号驱动IO服务器
     */
    public static void startServer() {
        System.out.println("4. 信号驱动IO模式示例:");
        System.out.println("启动信号驱动IO服务器，端口: " + PORT);
        System.out.println("特点: 基于信号机制，IO就绪时操作系统发送信号通知");
        System.out.println("注: Java标准库未直接支持信号驱动IO，本示例模拟其概念");
        
        try {
            // 创建ServerSocketChannel
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false); // 设置为非阻塞
            
            // 创建Selector（模拟信号机制）
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            
            System.out.println("信号驱动IO服务器已启动，等待连接...");
            System.out.println("模拟信号驱动: 使用Selector监听事件（类似信号通知）");
            
            // 启动一个简单的客户端进行测试
            new Thread(() -> {
                try {
                    // 等待服务器启动
                    Thread.sleep(500);
                    clientTest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
            // 处理事件（模拟信号处理）
            boolean running = true;
            while (running) {
                // 阻塞直到有事件（模拟信号通知）
                int readyChannels = selector.select();
                if (readyChannels == 0) continue;
                
                // 处理事件（模拟信号处理函数）
                System.out.println("接收到IO事件通知（模拟信号）");
                
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    
                    if (key.isAcceptable()) {
                        // 处理连接事件
                        handleAccept(key, selector);
                    } else if (key.isReadable()) {
                        // 处理读取事件
                        boolean done = handleRead(key);
                        if (done) running = false;
                    }
                }
            }
            
            // 关闭资源
            selector.close();
            serverSocketChannel.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * 处理连接事件
     */
    private static void handleAccept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false); // 设置为非阻塞
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("接收到客户端连接: " + socketChannel.getRemoteAddress());
    }
    
    /**
     * 处理读取事件
     */
    private static boolean handleRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        
        int len = socketChannel.read(buffer);
        if (len > 0) {
            buffer.flip();
            String clientMessage = new String(buffer.array(), 0, len);
            System.out.println("接收到客户端消息: " + clientMessage);
            
            // 发送响应
            String response = "Signal-Driven IO Server Response: " + clientMessage;
            ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes());
            socketChannel.write(responseBuffer);
            System.out.println("发送响应: " + response);
            
            // 关闭连接
            socketChannel.close();
            return true; // 完成测试
        } else if (len < 0) {
            // 连接关闭
            socketChannel.close();
        }
        
        return false;
    }
    
    /**
     * 客户端测试
     */
    private static void clientTest() {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            socketChannel.connect(new InetSocketAddress("localhost", PORT));
            
            // 发送消息
            String message = "Hello Signal-Driven IO Server!";
            ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
            socketChannel.write(buffer);
            System.out.println("信号驱动IO客户端发送消息: " + message);
            
            // 读取响应
            ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
            int len = socketChannel.read(responseBuffer);
            if (len > 0) {
                responseBuffer.flip();
                String response = new String(responseBuffer.array(), 0, len);
                System.out.println("信号驱动IO客户端接收到响应: " + response);
            }
            
            socketChannel.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}