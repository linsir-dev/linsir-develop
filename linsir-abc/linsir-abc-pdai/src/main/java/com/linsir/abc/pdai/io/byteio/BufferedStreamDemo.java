package com.linsir.abc.pdai.io.byteio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * BufferedInputStream和BufferedOutputStream示例
 * 演示缓冲字节流的基本操作
 */
public class BufferedStreamDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_buffered_stream.txt";
    
    /**
     * 使用BufferedOutputStream写入数据
     */
    public static void writeWithBuffer() {
        System.out.println("6. BufferedOutputStream写入示例:");
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(TEST_FILE))) {
            String content = "Hello, Buffered Stream!\nThis is a test for BufferedOutputStream.\nBuffered streams improve performance.";
            byte[] data = content.getBytes();
            bos.write(data);
            System.out.println("成功使用BufferedOutputStream写入数据:");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 使用BufferedInputStream读取数据
     */
    public static void readWithBuffer() {
        System.out.println("7. BufferedInputStream读取示例:");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(TEST_FILE))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder content = new StringBuilder();
            while ((bytesRead = bis.read(buffer)) != -1) {
                content.append(new String(buffer, 0, bytesRead));
            }
            System.out.println("成功使用BufferedInputStream读取数据:");
            System.out.println(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 清理测试文件
     */
    public static void cleanTestFiles() {
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
    }
}