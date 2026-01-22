package com.linsir.abc.pdai.io.patterns;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 阻塞IO（BIO）模式示例
 * 演示最传统的IO模式，每个连接一个线程
 */
public class BIODemo {
    private static final int PORT = 8081;
    
    /**
     * 启动BIO服务器
     */
    public static void startServer() {
        System.out.println("1. 阻塞IO（BIO）模式示例:");
        System.out.println("启动BIO服务器，端口: " + PORT);
        System.out.println("特点: 每个连接一个线程，读写操作阻塞");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("BIO服务器已启动，等待连接...");
            
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
            
            // 接受连接
            Socket socket = serverSocket.accept();
            System.out.println("接收到客户端连接: " + socket.getInetAddress());
            
            // 处理连接
            handleConnection(socket);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * 处理客户端连接
     */
    private static void handleConnection(Socket socket) {
        try (InputStream is = socket.getInputStream();
             OutputStream os = socket.getOutputStream()) {
            
            // 读取客户端数据（阻塞）
            byte[] buffer = new byte[1024];
            int len = is.read(buffer); // 阻塞，直到有数据
            String clientMessage = new String(buffer, 0, len);
            System.out.println("接收到客户端消息: " + clientMessage);
            
            // 发送响应（阻塞）
            String response = "BIO Server Response: " + clientMessage;
            os.write(response.getBytes());
            os.flush();
            System.out.println("发送响应: " + response);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 客户端测试
     */
    private static void clientTest() {
        try (Socket socket = new Socket("localhost", PORT);
             OutputStream os = socket.getOutputStream();
             InputStream is = socket.getInputStream()) {
            
            // 发送消息
            String message = "Hello BIO Server!";
            os.write(message.getBytes());
            os.flush();
            System.out.println("BIO客户端发送消息: " + message);
            
            // 读取响应
            byte[] buffer = new byte[1024];
            int len = is.read(buffer);
            String response = new String(buffer, 0, len);
            System.out.println("BIO客户端接收到响应: " + response);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}