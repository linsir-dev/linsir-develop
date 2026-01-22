package com.linsir.abc.pdai.io.byteio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ByteArrayInputStream和ByteArrayOutputStream示例
 * 演示内存字节流的基本操作
 */
public class ByteArrayStreamDemo {
    /**
     * 使用ByteArrayOutputStream写入数据
     */
    public static void writeToByteArray() {
        System.out.println("3. ByteArrayOutputStream写入示例:");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String content = "Hello, ByteArray Stream!\nThis is a test for ByteArrayOutputStream.";
            byte[] data = content.getBytes();
            baos.write(data);
            
            // 获取写入的字节数组
            byte[] result = baos.toByteArray();
            System.out.println("成功写入数据到字节数组:");
            System.out.println(new String(result));
            System.out.println("字节数组长度: " + result.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 使用ByteArrayInputStream读取数据
     */
    public static void readFromByteArray() {
        System.out.println("4. ByteArrayInputStream读取示例:");
        
        // 准备测试数据
        String content = "Hello, ByteArray Stream!\nThis is a test for ByteArrayInputStream.";
        byte[] data = content.getBytes();
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder result = new StringBuilder();
            while ((bytesRead = bais.read(buffer)) != -1) {
                result.append(new String(buffer, 0, bytesRead));
            }
            System.out.println("成功从字节数组读取数据:");
            System.out.println(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 演示重置和标记功能
     */
    public static void demonstrateResetAndMark() {
        System.out.println("5. ByteArrayInputStream重置和标记功能示例:");
        
        // 准备测试数据
        byte[] data = "Hello, ByteArray Stream!" .getBytes();
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            byte[] buffer = new byte[10];
            
            // 读取前10个字节
            int bytesRead = bais.read(buffer);
            System.out.println("读取前10个字节: " + new String(buffer, 0, bytesRead));
            
            // 标记当前位置
            bais.mark(0);
            
            // 读取接下来的5个字节
            bytesRead = bais.read(buffer, 0, 5);
            System.out.println("读取接下来的5个字节: " + new String(buffer, 0, bytesRead));
            
            // 重置到标记位置
            bais.reset();
            
            // 再次读取
            bytesRead = bais.read(buffer, 0, 10);
            System.out.println("重置后读取的字节: " + new String(buffer, 0, bytesRead));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}