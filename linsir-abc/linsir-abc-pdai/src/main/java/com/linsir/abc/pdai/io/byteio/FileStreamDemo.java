package com.linsir.abc.pdai.io.byteio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author linsir
 * @version 1.0
 * @description: FileInputStream和FileOutputStream示例
 * @date 2026/1/22 12:00
 */
public class FileStreamDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_file_stream.txt";
    private static final String COPY_FILE = "copy_test_file_stream.txt";
    
    public static void main(String[] args) {
        // 写入数据到文件
        writeToFile();
        
        // 从文件读取数据
        readFromFile();
        
        // 复制文件
        copyFile();
        
        // 清理测试文件
        cleanTestFiles();
    }
    
    // 写入数据到文件
    private static void writeToFile() {
        System.out.println("1. FileOutputStream写入示例:");
        try (FileOutputStream fos = new FileOutputStream(TEST_FILE)) {
            String content = "Hello, File Output Stream!";
            fos.write(content.getBytes());
            System.out.println("成功写入文件: " + TEST_FILE);
            System.out.println("写入内容: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // 从文件读取数据
    private static void readFromFile() {
        System.out.println("2. FileInputStream读取示例:");
        try (FileInputStream fis = new FileInputStream(TEST_FILE)) {
            byte[] buffer = new byte[1024];
            int bytesRead = fis.read(buffer);
            String content = new String(buffer, 0, bytesRead);
            System.out.println("成功读取文件: " + TEST_FILE);
            System.out.println("读取内容: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // 复制文件
    private static void copyFile() {
        System.out.println("3. FileInputStream和FileOutputStream复制文件示例:");
        try (FileInputStream fis = new FileInputStream(TEST_FILE);
             FileOutputStream fos = new FileOutputStream(COPY_FILE)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("成功复制文件到: " + COPY_FILE);
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
