package com.linsir.abc.core.base.net.socket;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * 数据报通信器
 * 演示DatagramSocket的使用，实现UDP通信
 *
 * 设计要点：
 * 1. UDP是无连接、不可靠的传输协议
 * 2. DatagramSocket发送和接收数据报
 * 3. 适合实时性要求高、可容忍丢包的场景
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class DatagramCommunicator {

    private DatagramSocket socket;

    /**
     * 绑定到指定端口
     */
    public void bind(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    /**
     * 发送数据报
     */
    public void send(String message, String host, int port) throws IOException {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, 
                InetAddress.getByName(host), port);
        socket.send(packet);
    }

    /**
     * 接收数据报
     */
    public String receive(int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
    }

    /**
     * 关闭Socket
     */
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * 演示UDP通信
     */
    public static void demonstrate() {
        System.out.println("=== UDP数据报通信演示 ===\n");

        int port = 12350;

        // 启动接收端
        Thread receiver = new Thread(() -> {
            try {
                DatagramCommunicator communicator = new DatagramCommunicator();
                communicator.bind(port);
                System.out.println("接收端启动，监听端口: " + port);

                for (int i = 0; i < 3; i++) {
                    String message = communicator.receive(1024);
                    System.out.println("接收端收到: " + message);
                }

                communicator.close();
            } catch (IOException e) {
                System.err.println("接收端错误: " + e.getMessage());
            }
        });
        receiver.start();

        // 等待接收端启动
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 发送端
        try {
            DatagramCommunicator sender = new DatagramCommunicator();
            sender.bind(0); // 自动分配端口

            String[] messages = {"Hello UDP", "Message 2", "Goodbye"};
            for (String msg : messages) {
                sender.send(msg, "localhost", port);
                System.out.println("发送端发送: " + msg);
                Thread.sleep(100);
            }

            sender.close();
        } catch (Exception e) {
            System.err.println("发送端错误: " + e.getMessage());
        }

        try {
            receiver.join(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
