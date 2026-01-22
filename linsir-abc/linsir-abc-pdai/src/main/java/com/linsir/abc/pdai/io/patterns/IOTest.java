package com.linsir.abc.pdai.io.patterns;

/**
 * IO模式测试主类
 * 测试所有5种IO模式示例代码
 */
public class IOTest {
    public static void main(String[] args) {
        System.out.println("=== Java.io 5种IO模式测试 ===\n");
        
        // 测试阻塞IO（BIO）模式
        BIODemo.startServer();
        
        // 等待测试完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 测试非阻塞IO（NIO）模式
        NIODemo.startServer();
        
        // 等待测试完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 测试多路复用IO模式
        MultiplexedIODemo.startServer();
        
        // 等待测试完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 测试信号驱动IO模式
        SignalDrivenIODemo.startServer();
        
        // 等待测试完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 测试异步IO（AIO）模式
        AIODemo.startServer();
        
        System.out.println("=== IO模式测试完成 ===");
        System.out.println("\n总结:");
        System.out.println("1. 阻塞IO（BIO）: 简单直观，但并发性能差，适合连接数少的场景");
        System.out.println("2. 非阻塞IO（NIO）: 基于通道和选择器，性能优于BIO，适合连接数较多的场景");
        System.out.println("3. 多路复用IO: 利用操作系统多路复用机制，性能较高，适合高并发场景");
        System.out.println("4. 信号驱动IO: 基于信号机制，Java中使用较少");
        System.out.println("5. 异步IO（AIO）: 基于回调机制，性能最优，但编程复杂度高，适合高并发、大流量场景");
    }
}