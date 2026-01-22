package com.linsir.abc.pdai.io.byteio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Vector;

/**
 * SequenceInputStream示例
 * 演示序列字节流的基本操作
 */
public class SequenceStreamDemo {
    /**
     * 基本使用示例
     */
    public static void basicExample() {
        System.out.println("10. SequenceInputStream基本使用示例:");
        
        // 创建两个字节数组输入流
        byte[] array1 = "Hello, ".getBytes();
        byte[] array2 = "Sequence Stream!".getBytes();
        
        try (ByteArrayInputStream bais1 = new ByteArrayInputStream(array1);
             ByteArrayInputStream bais2 = new ByteArrayInputStream(array2);
             SequenceInputStream sis = new SequenceInputStream(bais1, bais2);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = sis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            
            String content = baos.toString();
            System.out.println("成功使用SequenceInputStream读取多个流:");
            System.out.println("读取内容: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 使用Vector合并多个流
     */
    public static void vectorExample() {
        System.out.println("11. SequenceInputStream使用Vector合并多个流示例:");
        
        // 创建多个字节数组输入流
        byte[] array1 = "First part. ".getBytes();
        byte[] array2 = "Second part. ".getBytes();
        byte[] array3 = "Third part. ".getBytes();
        byte[] array4 = "Fourth part.".getBytes();
        
        // 使用Vector存储多个输入流
        Vector<ByteArrayInputStream> vector = new Vector<>();
        vector.add(new ByteArrayInputStream(array1));
        vector.add(new ByteArrayInputStream(array2));
        vector.add(new ByteArrayInputStream(array3));
        vector.add(new ByteArrayInputStream(array4));
        
        try (SequenceInputStream sis = new SequenceInputStream(vector.elements());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = sis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            
            String content = baos.toString();
            System.out.println("成功使用SequenceInputStream读取多个流:");
            System.out.println("读取内容: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 综合示例
     */
    public static void combinedExample() {
        System.out.println("12. SequenceInputStream综合示例:");
        
        // 模拟多个数据源
        byte[] header = "=== Report Header ===\n".getBytes();
        byte[] content = "This is the main content of the report.\nIt contains important information.\n".getBytes();
        byte[] footer = "=== Report Footer ===".getBytes();
        
        try (ByteArrayInputStream headerStream = new ByteArrayInputStream(header);
             ByteArrayInputStream contentStream = new ByteArrayInputStream(content);
             ByteArrayInputStream footerStream = new ByteArrayInputStream(footer);
             // 合并前两个流
             SequenceInputStream firstCombine = new SequenceInputStream(headerStream, contentStream);
             // 再合并第三个流
             SequenceInputStream allCombine = new SequenceInputStream(firstCombine, footerStream);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = allCombine.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            
            String report = baos.toString();
            System.out.println("生成的报告:");
            System.out.println(report);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}