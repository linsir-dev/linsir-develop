package com.linsir.abc.pdai.io.chario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * BufferedReader和BufferedWriter示例
 * 演示缓冲字符流的基本操作
 */
public class BufferedReaderWriterDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_buffered_reader_writer.txt";
    
    /**
     * 使用BufferedWriter写入数据
     */
    public static void writeWithBuffer() {
        System.out.println("3. BufferedWriter写入示例:");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(TEST_FILE))) {
            bw.write("Hello, BufferedReader and BufferedWriter!");
            bw.newLine(); // 使用newLine()方法换行，跨平台兼容
            bw.write("This is a test for buffered character streams.");
            bw.newLine();
            bw.write("Buffered streams improve performance.");
            bw.newLine();
            bw.write("They also support line operations.");
            
            System.out.println("成功使用BufferedWriter写入数据:");
            System.out.println("Hello, BufferedReader and BufferedWriter!");
            System.out.println("This is a test for buffered character streams.");
            System.out.println("Buffered streams improve performance.");
            System.out.println("They also support line operations.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 使用BufferedReader读取数据
     */
    public static void readWithBuffer() {
        System.out.println("4. BufferedReader读取示例:");
        try (BufferedReader br = new BufferedReader(new FileReader(TEST_FILE))) {
            String line;
            System.out.println("成功使用BufferedReader读取数据（逐行读取）:");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
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