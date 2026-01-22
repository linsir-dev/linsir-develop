package com.linsir.abc.pdai.io.chario;

import java.io.*;

/**
 * InputStreamReader和OutputStreamWriter示例
 * 演示字节流到字符流的桥接操作
 */
public class InputStreamReaderOutputStreamWriterDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_input_output_stream_reader_writer.txt";
    
    /**
     * 使用OutputStreamWriter写入数据
     */
    public static void writeWithEncoding() {
        System.out.println("5. OutputStreamWriter写入示例:");
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(TEST_FILE), "UTF-8")) {
            String content = "Hello, InputStreamReader and OutputStreamWriter!\n这是一个测试，包含中文字符。\nOutputStreamWriter可以指定字符编码。";
            osw.write(content);
            System.out.println("成功使用OutputStreamWriter写入数据（UTF-8编码）:");
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 使用InputStreamReader读取数据
     */
    public static void readWithEncoding() {
        System.out.println("6. InputStreamReader读取示例:");
        try (InputStreamReader isr = new InputStreamReader(new FileInputStream(TEST_FILE), "UTF-8")) {
            char[] buffer = new char[1024];
            int charsRead;
            StringBuilder content = new StringBuilder();
            while ((charsRead = isr.read(buffer)) != -1) {
                content.append(buffer, 0, charsRead);
            }
            System.out.println("成功使用InputStreamReader读取数据（UTF-8编码）:");
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