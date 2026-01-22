package com.linsir.abc.pdai.io.byteio;

/**
 * 字节流测试主类
 * 测试所有字节流示例代码
 */
public class ByteStreamTest {
    public static void main(String[] args) {
        System.out.println("=== Java.io 字节流测试 ===\n");
        
        // 测试FileInputStream和FileOutputStream
        FileStreamDemo.writeToFile();
        FileStreamDemo.readFromFile();
        FileStreamDemo.cleanTestFiles();
        
        // 测试ByteArrayInputStream和ByteArrayOutputStream
        ByteArrayStreamDemo.writeToByteArray();
        ByteArrayStreamDemo.readFromByteArray();
        ByteArrayStreamDemo.demonstrateResetAndMark();
        
        // 测试BufferedInputStream和BufferedOutputStream
        BufferedStreamDemo.writeWithBuffer();
        BufferedStreamDemo.readWithBuffer();
        BufferedStreamDemo.cleanTestFiles();
        
        // 测试DataInputStream和DataOutputStream
        DataStreamDemo.writeData();
        DataStreamDemo.readData();
        DataStreamDemo.cleanTestFiles();
        
        // 测试SequenceInputStream
        SequenceStreamDemo.basicExample();
        SequenceStreamDemo.vectorExample();
        SequenceStreamDemo.combinedExample();
        
        System.out.println("=== 字节流测试完成 ===");
    }
}