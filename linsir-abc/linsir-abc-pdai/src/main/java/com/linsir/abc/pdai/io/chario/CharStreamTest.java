package com.linsir.abc.pdai.io.chario;

/**
 * 字符流测试主类
 * 测试所有字符流示例代码
 */
public class CharStreamTest {
    public static void main(String[] args) {
        System.out.println("=== Java.io 字符流测试 ===\n");
        
        // 测试FileReader和FileWriter
        FileReaderWriterDemo.writeToFile();
        FileReaderWriterDemo.readFromFile();
        FileReaderWriterDemo.cleanTestFiles();
        
        // 测试BufferedReader和BufferedWriter
        BufferedReaderWriterDemo.writeWithBuffer();
        BufferedReaderWriterDemo.readWithBuffer();
        BufferedReaderWriterDemo.cleanTestFiles();
        
        // 测试InputStreamReader和OutputStreamWriter
        InputStreamReaderOutputStreamWriterDemo.writeWithEncoding();
        InputStreamReaderOutputStreamWriterDemo.readWithEncoding();
        InputStreamReaderOutputStreamWriterDemo.cleanTestFiles();
        
        // 测试StringReader和StringWriter
        StringReaderWriterDemo.writeToString();
        StringReaderWriterDemo.readFromString();
        StringReaderWriterDemo.demonstrateOtherMethods();
        
        // 测试PrintWriter
        PrintWriterDemo.writeWithPrintWriter();
        PrintWriterDemo.writeWithAutoFlush();
        PrintWriterDemo.cleanTestFiles();
        
        System.out.println("=== 字符流测试完成 ===");
    }
}