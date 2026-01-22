package com.linsir.abc.pdai.io.byteio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author linsir
 * @version 1.0
 * @description: DataInputStream和DataOutputStream示例
 * @date 2026/1/22 12:00
 */
public class DataStreamDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_data_stream.txt";
    
    public static void main(String[] args) {
        // 写入各种类型的数据
        writeData();
        
        // 读取各种类型的数据
        readData();
        
        // 清理测试文件
        cleanTestFiles();
    }
    
    // 写入各种类型的数据
    private static void writeData() {
        System.out.println("1. DataOutputStream写入示例:");
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(TEST_FILE))) {
            // 写入各种数据类型
            dos.writeBoolean(true);
            dos.writeByte(100);
            dos.writeShort(2000);
            dos.writeInt(300000);
            dos.writeLong(4000000000L);
            dos.writeFloat(3.14f);
            dos.writeDouble(3.1415926535);
            dos.writeUTF("Hello, Data Stream!");
            
            System.out.println("成功使用DataOutputStream写入各种类型的数据:");
            System.out.println("Boolean: true");
            System.out.println("Byte: 100");
            System.out.println("Short: 2000");
            System.out.println("Int: 300000");
            System.out.println("Long: 4000000000");
            System.out.println("Float: 3.14");
            System.out.println("Double: 3.1415926535");
            System.out.println("String: Hello, Data Stream!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // 读取各种类型的数据
    private static void readData() {
        System.out.println("2. DataInputStream读取示例:");
        try (DataInputStream dis = new DataInputStream(new FileInputStream(TEST_FILE))) {
            // 按照写入的顺序读取数据
            boolean boolValue = dis.readBoolean();
            byte byteValue = dis.readByte();
            short shortValue = dis.readShort();
            int intValue = dis.readInt();
            long longValue = dis.readLong();
            float floatValue = dis.readFloat();
            double doubleValue = dis.readDouble();
            String stringValue = dis.readUTF();
            
            System.out.println("成功使用DataInputStream读取各种类型的数据:");
            System.out.println("Boolean: " + boolValue);
            System.out.println("Byte: " + byteValue);
            System.out.println("Short: " + shortValue);
            System.out.println("Int: " + intValue);
            System.out.println("Long: " + longValue);
            System.out.println("Float: " + floatValue);
            System.out.println("Double: " + doubleValue);
            System.out.println("String: " + stringValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    // 清理测试文件
    private static void cleanTestFiles() {
        java.io.File testFile = new java.io.File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
        
        System.out.println("清理测试文件完成");
    }
}
