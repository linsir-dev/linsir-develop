package com.linsir.abc.pdai.io.chario;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * PrintWriter示例
 * 演示打印字符流的基本操作
 */
public class PrintWriterDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_print_writer.txt";
    
    /**
     * 使用PrintWriter写入数据
     */
    public static void writeWithPrintWriter() {
        System.out.println("10. PrintWriter写入示例:");
        try (PrintWriter pw = new PrintWriter(new FileWriter(TEST_FILE))) {
            // 使用各种print方法
            pw.println("Hello, PrintWriter!");
            pw.println("This is a test for print character stream.");
            pw.println(); // 空行
            pw.println("PrintWriter supports various print methods:");
            pw.print("Printing an integer: ");
            pw.println(42);
            pw.print("Printing a double: ");
            pw.println(3.14159);
            pw.print("Printing a boolean: ");
            pw.println(true);
            pw.print("Printing a char: ");
            pw.println('A');
            pw.println("Printing an object: " + new Object());
            
            System.out.println("成功使用PrintWriter写入数据:");
            System.out.println("Hello, PrintWriter!");
            System.out.println("This is a test for print character stream.");
            System.out.println();
            System.out.println("PrintWriter supports various print methods:");
            System.out.println("Printing an integer: 42");
            System.out.println("Printing a double: 3.14159");
            System.out.println("Printing a boolean: true");
            System.out.println("Printing a char: A");
            System.out.println("Printing an object: " + new Object());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * 使用PrintWriter的自动刷新功能
     */
    public static void writeWithAutoFlush() {
        System.out.println("11. PrintWriter自动刷新示例:");
        try (PrintWriter pw = new PrintWriter(new FileWriter(TEST_FILE), true)) { // 第二个参数为true，启用自动刷新
            pw.println("Hello, PrintWriter with auto flush!");
            pw.println("This line is automatically flushed.");
            pw.println("Auto flush is enabled when the second parameter is true.");
            pw.println("Auto flush only works with println(), printf(), or format() methods.");
            
            System.out.println("成功使用PrintWriter自动刷新功能:");
            System.out.println("Hello, PrintWriter with auto flush!");
            System.out.println("This line is automatically flushed.");
            System.out.println("Auto flush is enabled when the second parameter is true.");
            System.out.println("Auto flush only works with println(), printf(), or format() methods.");
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