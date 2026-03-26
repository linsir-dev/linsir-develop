package com.linsir.abc.core.base.net.socket;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

/**
 * 多播组管理器
 * 演示MulticastSocket的使用，实现组播通信
 *
 * 设计要点：
 * 1. 多播是一对多的通信方式
 * 2. 使用D类IP地址(224.0.0.0-239.255.255.255)
 * 3. 适合视频会议、在线直播等场景
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class MulticastGroupManager {

    private MulticastSocket socket;
    private InetAddress group;

    /**
     * 加入多播组
     */
    public void joinGroup(String multicastAddress, int port) throws IOException {
        group = InetAddress.getByName(multicastAddress);
        socket = new MulticastSocket(port);
        socket.joinGroup(group);
    }

    /**
     * 离开多播组
     */
    public void leaveGroup() throws IOException {
        if (socket != null && group != null) {
            socket.leaveGroup(group);
        }
    }

    /**
     * 发送多播消息
     */
    public void send(String message, int port) throws IOException {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
        socket.send(packet);
    }

    /**
     * 接收多播消息
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
     * 演示多播通信
     */
    public static void demonstrate() {
        System.out.println("=== 多播组通信演示 ===\n");

        String multicastAddress = "230.0.0.1";
        int port = 12351;

        // 启动接收者1
        Thread receiver1 = new Thread(() -> {
            try {
                MulticastGroupManager manager = new MulticastGroupManager();
                manager.joinGroup(multicastAddress, port);
                System.out.println("接收者1加入多播组: " + multicastAddress);

                String message = manager.receive(1024);
                System.out.println("接收者1收到: " + message);

                manager.leaveGroup();
                manager.close();
            } catch (IOException e) {
                System.err.println("接收者1错误: " + e.getMessage());
            }
        });

        // 启动接收者2
        Thread receiver2 = new Thread(() -> {
            try {
                MulticastGroupManager manager = new MulticastGroupManager();
                manager.joinGroup(multicastAddress, port);
                System.out.println("接收者2加入多播组: " + multicastAddress);

                String message = manager.receive(1024);
                System.out.println("接收者2收到: " + message);

                manager.leaveGroup();
                manager.close();
            } catch (IOException e) {
                System.err.println("接收者2错误: " + e.getMessage());
            }
        });

        receiver1.start();
        receiver2.start();

        // 等待接收者加入
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 发送多播消息
        try {
            MulticastGroupManager sender = new MulticastGroupManager();
            sender.joinGroup(multicastAddress, port);

            String message = "Hello Multicast Group!";
            sender.send(message, port);
            System.out.println("发送者发送: " + message);

            sender.leaveGroup();
            sender.close();
        } catch (IOException e) {
            System.err.println("发送者错误: " + e.getMessage());
        }

        try {
            receiver1.join(3000);
            receiver2.join(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
