package com.linsir.abc.pdai.io.chario;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * FileReader和FileWriter示例
 * 演示文件字符流的基本操作
 */
public class FileReaderWriterDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_file_reader_writer.txt";
    
    /**
     * 使用FileWriter写入数据
     */
    public static void writeToFile() {
        System.out.println("1. FileWriter写入示例:");
        try (FileWriter fw = new FileWriter(TEST_FILE)) {
            String content = "Hello, FileReader and FileWriter!\nThis is a test for file character streams.\nFileReader and FileWriter are used for text files.";
            fw.write(content);
            System.out.println("成功使用FileWriter写入数据:");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 使用FileReader读取数据
     */
    public static void readFromFile() {
        System.out.println("2. FileReader读取示例:");
        try (FileReader fr = new FileReader(TEST_FILE)) {
            char[] buffer = new char[1024];
            int charsRead;
            StringBuilder content = new StringBuilder();
            while ((charsRead = fr.read(buffer)) != -1) {
                content.append(buffer, 0, charsRead);
            }
            System.out.println("成功使用FileReader读取数据:");
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