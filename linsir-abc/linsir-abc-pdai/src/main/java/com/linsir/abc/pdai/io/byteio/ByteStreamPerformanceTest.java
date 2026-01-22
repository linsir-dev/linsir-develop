package com.linsir.abc.pdai.io.byteio;

import java.io.*;

/**
 * @author linsir
 * @version 1.0
 * @description: 字节流性能测试
 * @date 2026/1/22 12:00
 */
public class ByteStreamPerformanceTest {
    // 测试文件路径
    private static final String TEST_FILE = "test_performance.txt";
    
    public static void main(String[] args) {
        // 测试不同大小的数据
        testWithDifferentDataSizes();
        
        // 清理测试文件
        cleanTestFiles();
    }
    
    // 测试不同大小的数据
    private static void testWithDifferentDataSizes() {
        System.out.println("字节流性能测试:");
        
        // 测试数据大小：1KB, 10KB, 100KB, 1MB
        int[] dataSizes = {1024, 10240, 102400, 1048576};
        
        for (int size : dataSizes) {
            System.out.println("\n测试数据大小: " + (size / 1024) + "KB");
            
            // 准备测试数据
            byte[] testData = new byte[size];
            for (int i = 0; i < testData.length; i++) {
                testData[i] = (byte) (i % 256);
            }
            
            // 测试FileOutputStream性能
            testFileOutputStream(testData);
            
            // 测试BufferedOutputStream性能
            testBufferedOutputStream(testData);
            
            // 测试FileInputStream性能
            testFileInputStream();
            
            // 测试BufferedInputStream性能
            testBufferedInputStream();
        }
    }
    
    // 测试FileOutputStream性能
    private static void testFileOutputStream(byte[] testData) {
        long startTime = System.currentTimeMillis();
        try (FileOutputStream fos = new FileOutputStream(TEST_FILE)) {
            fos.write(testData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() - startTime;
        System.out.println("FileOutputStream写入时间: " + time + "ms");
    }
    
    // 测试BufferedOutputStream性能
    private static void testBufferedOutputStream(byte[] testData) {
        long startTime = System.currentTimeMillis();
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(TEST_FILE))) {
            bos.write(testData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() - startTime;
        System.out.println("BufferedOutputStream写入时间: " + time + "ms");
    }
    
    // 测试FileInputStream性能
    private static void testFileInputStream() {
        long startTime = System.currentTimeMillis();
        try (FileInputStream fis = new FileInputStream(TEST_FILE)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                // 读取数据，不做处理
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() - startTime;
        System.out.println("FileInputStream读取时间: " + time + "ms");
    }
    
    // 测试BufferedInputStream性能
    private static void testBufferedInputStream() {
        long startTime = System.currentTimeMillis();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(TEST_FILE))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                // 读取数据，不做处理
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() - startTime;
        System.out.println("BufferedInputStream读取时间: " + time + "ms");
    }
    
    // 清理测试文件
    private static void cleanTestFiles() {
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            if (testFile.delete()) {
                System.out.println("\n清理测试文件完成");
            }
        }
    }
}
