package com.linsir.abc.pdai.io.chario;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * StringReader和StringWriter示例
 * 演示字符串字符流的基本操作
 */
public class StringReaderWriterDemo {
    /**
     * 使用StringWriter写入数据
     */
    public static void writeToString() {
        System.out.println("7. StringWriter写入示例:");
        try (StringWriter sw = new StringWriter()) {
            sw.write("Hello, StringReader and StringWriter!");
            sw.write("\n");
            sw.write("This is a test for string character streams.");
            sw.write("\n");
            sw.write("StringWriter writes to a StringBuffer internally.");
            
            // 获取写入的字符串
            String content = sw.toString();
            System.out.println("成功使用StringWriter写入数据:");
            System.out.println(content);
            System.out.println("写入的字符串长度: " + content.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 使用StringReader读取数据
     */
    public static void readFromString() {
        System.out.println("8. StringReader读取示例:");
        
        // 准备测试数据
        String content = "Hello, StringReader and StringWriter!\nThis is a test for string character streams.\nStringReader reads from a string.";
        
        try (StringReader sr = new StringReader(content)) {
            char[] buffer = new char[1024];
            int charsRead;
            StringBuilder result = new StringBuilder();
            while ((charsRead = sr.read(buffer)) != -1) {
                result.append(buffer, 0, charsRead);
            }
            System.out.println("成功使用StringReader读取数据:");
            System.out.println(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 演示StringWriter的其他方法
     */
    public static void demonstrateOtherMethods() {
        System.out.println("9. StringWriter其他方法示例:");
        try (StringWriter sw = new StringWriter()) {
            // 写入部分字符串
            sw.write("Hello", 1, 4); // 从索引1开始，写入4个字符："ello"
            sw.write(' ');
            sw.write("World!");
            
            // 获取内部的StringBuffer
            StringBuffer sb = sw.getBuffer();
            sb.insert(0, "Hi, "); // 在开头插入
            sb.append(" How are you?"); // 在末尾追加
            
            String content = sw.toString();
            System.out.println("使用StringWriter的其他方法:");
            System.out.println("最终字符串: " + content);
            System.out.println("字符串长度: " + content.length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
}