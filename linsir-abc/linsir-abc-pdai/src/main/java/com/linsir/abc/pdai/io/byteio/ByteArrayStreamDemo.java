package com.linsir.abc.pdai.io.byteio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author linsir
 * @version 1.0
 * @description: ByteArrayInputStream和ByteArrayOutputStream示例
 * @date 2026/1/22 12:00
 */
public class ByteArrayStreamDemo {
    public static void main(String[] args) {
        // 使用ByteArrayOutputStream写入数据
        writeToByteArray();
        
        // 使用ByteArrayInputStream读取数据
        readFromByteArray();
        
        // 综合示例
        combinedExample();
    }
    
    // 使用ByteArrayOutputStream写入数据
    private static void writeToByteArray() {
        System.out.println("1. ByteArrayOutputStream写入示例:");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String content = "Hello, ByteArray Output Stream!";
            baos.write(content.getBytes());
            
            // 获取字节数组
            byte[] byteArray = baos.toByteArray();
            System.out.println("写入到ByteArrayOutputStream的内容: " + new String(byteArray));
            System.out.println("字节数组长度: " + byteArray.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // 使用ByteArrayInputStream读取数据
    private static void readFromByteArray() {
        System.out.println("2. ByteArrayInputStream读取示例:");
        byte[] byteArray = "Hello, ByteArray Input Stream!".getBytes();
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(byteArray)) {
            byte[] buffer = new byte[1024];
            int bytesRead = bais.read(buffer);
            String content = new String(buffer, 0, bytesRead);
            System.out.println("从ByteArrayInputStream读取的内容: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // 综合示例
    private static void combinedExample() {
        System.out.println("3. ByteArrayStream综合示例:");
        
        // 模拟数据处理过程
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 写入一些数据
            baos.write("Processing data...\n".getBytes());
            baos.write("Step 1: Initialization\n".getBytes());
            baos.write("Step 2: Calculation\n".getBytes());
            baos.write("Step 3: Finalization\n".getBytes());
            
            // 获取处理后的数据
            byte[] processedData = baos.toByteArray();
            
            // 读取并处理数据
            try (ByteArrayInputStream bais = new ByteArrayInputStream(processedData)) {
                byte[] buffer = new byte[1024];
                int bytesRead = bais.read(buffer);
                String content = new String(buffer, 0, bytesRead);
                System.out.println("处理后的数据:");
                System.out.println(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}
