package com.linsir.abc.pdai.io.byteio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileInputStream和FileOutputStream示例
 * 演示文件字节流的基本操作
 */
public class FileStreamDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_file_stream.txt";
    
    /**
     * 写入数据到文件
     */
    public static void writeToFile() {
        System.out.println("1. FileOutputStream写入示例:");
        try (FileOutputStream fos = new FileOutputStream(TEST_FILE)) {
            String content = "Hello, File Stream!\nThis is a test for FileOutputStream.";
            byte[] data = content.getBytes();
            fos.write(data);
            System.out.println("成功写入数据到文件:");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 从文件读取数据
     */
    public static void readFromFile() {
        System.out.println("2. FileInputStream读取示例:");
        try (FileInputStream fis = new FileInputStream(TEST_FILE)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder content = new StringBuilder();
            while ((bytesRead = fis.read(buffer)) != -1) {
                content.append(new String(buffer, 0, bytesRead));
            }
            System.out.println("成功从文件读取数据:");
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