package com.linsir.abc.pdai.io.byteio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author linsir
 * @version 1.0
 * @description: BufferedInputStream和BufferedOutputStream示例
 * @date 2026/1/22 12:00
 */
public class BufferedStreamDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_buffered_stream.txt";
    private static final String COPY_FILE = "copy_test_buffered_stream.txt";
    
    public static void main(String[] args) {
        // 写入数据到文件（使用缓冲流）
        writeToFileWithBuffer();
        
        // 从文件读取数据（使用缓冲流）
        readFromFileWithBuffer();
        
        // 复制文件（使用缓冲流）
        copyFileWithBuffer();
        
        // 清理测试文件
        cleanTestFiles();
    }
    
    // 写入数据到文件（使用缓冲流）
    private static void writeToFileWithBuffer() {
        System.out.println("1. BufferedOutputStream写入示例:");
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(TEST_FILE))) {
            String content = "Hello, Buffered Output Stream!\nThis is a test for buffered streams.\nBuffered streams can improve IO performance.";
            bos.write(content.getBytes());
            System.out.println("成功使用BufferedOutputStream写入文件");
            System.out.println("写入内容:");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // 从文件读取数据（使用缓冲流）
    private static void readFromFileWithBuffer() {
        System.out.println("2. BufferedInputStream读取示例:");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(TEST_FILE))) {
            byte[] buffer = new byte[1024];
            int bytesRead = bis.read(buffer);
            String content = new String(buffer, 0, bytesRead);
            System.out.println("成功使用BufferedInputStream读取文件");
            System.out.println("读取内容:");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // 复制文件（使用缓冲流）
    private static void copyFileWithBuffer() {
        System.out.println("3. BufferedStream复制文件示例:");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(TEST_FILE));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(COPY_FILE))) {
            byte[] buffer = new byte[8192]; // 8KB缓冲区
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            System.out.println("成功使用BufferedStream复制文件到: " + COPY_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // 清理测试文件
    private static void cleanTestFiles() {
        java.io.File testFile = new java.io.File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
        
        java.io.File copyFile = new java.io.File(COPY_FILE);
        if (copyFile.exists()) {
            copyFile.delete();
        }
        
        System.out.println("清理测试文件完成");
    }
}
