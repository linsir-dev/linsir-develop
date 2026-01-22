package com.linsir.abc.pdai.io.byteio;

import java.io.*;

/**
 * @author linsir
 * @version 1.0
 * @description: 字节流工具类
 * @date 2026/1/22 12:00
 */
public class ByteStreamUtils {
    /**
     * 复制文件
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @return 是否复制成功
     */
    public static boolean copyFile(String sourcePath, String destPath) {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            System.out.println("源文件不存在: " + sourcePath);
            return false;
        }
        
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destPath))) {
            byte[] buffer = new byte[8192]; // 8KB缓冲区
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            System.out.println("成功复制文件: " + sourcePath + " -> " + destPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("复制文件失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 读取文件到字节数组
     * @param filePath 文件路径
     * @return 字节数组，如果读取失败返回null
     */
    public static byte[] readFileToByteArray(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("文件不存在: " + filePath);
            return null;
        }
        
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("读取文件失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 写入字节数组到文件
     * @param data 字节数组
     * @param filePath 文件路径
     * @return 是否写入成功
     */
    public static boolean writeByteArrayToFile(byte[] data, String filePath) {
        if (data == null) {
            System.out.println("字节数组为null");
            return false;
        }
        
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
            System.out.println("成功写入字节数组到文件: " + filePath);
            System.out.println("写入字节数: " + data.length);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写入字节数组到文件失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 读取文件内容为字符串
     * @param filePath 文件路径
     * @param charset 字符集
     * @return 文件内容字符串，如果读取失败返回null
     */
    public static String readFileToString(String filePath, String charset) {
        byte[] data = readFileToByteArray(filePath);
        if (data == null) {
            return null;
        }
        
        try {
            return new String(data, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("不支持的字符集: " + charset);
            return null;
        }
    }
    
    /**
     * 写入字符串到文件
     * @param content 字符串内容
     * @param filePath 文件路径
     * @param charset 字符集
     * @return 是否写入成功
     */
    public static boolean writeStringToFile(String content, String filePath, String charset) {
        if (content == null) {
            System.out.println("内容为null");
            return false;
        }
        
        try {
            byte[] data = content.getBytes(charset);
            return writeByteArrayToFile(data, filePath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("不支持的字符集: " + charset);
            return false;
        }
    }
    
    /**
     * 测试工具类
     */
    public static void main(String[] args) {
        System.out.println("测试ByteStreamUtils工具类:");
        
        // 测试文件路径
        String testFile = "test_utils.txt";
        String copyFile = "copy_test_utils.txt";
        String contentFile = "content_test_utils.txt";
        
        // 测试写入字符串到文件
        System.out.println("\n1. 测试写入字符串到文件:");
        String testContent = "Hello, ByteStreamUtils!\nThis is a test for the utility methods.\n";
        writeStringToFile(testContent, testFile, "UTF-8");
        
        // 测试读取文件到字符串
        System.out.println("\n2. 测试读取文件到字符串:");
        String readContent = readFileToString(testFile, "UTF-8");
        if (readContent != null) {
            System.out.println("读取到的文件内容:");
            System.out.println(readContent);
        }
        
        // 测试复制文件
        System.out.println("\n3. 测试复制文件:");
        copyFile(testFile, copyFile);
        
        // 测试读取文件到字节数组
        System.out.println("\n4. 测试读取文件到字节数组:");
        byte[] fileData = readFileToByteArray(testFile);
        if (fileData != null) {
            System.out.println("读取到的字节数组长度: " + fileData.length);
            System.out.println("字节数组内容: " + new String(fileData));
        }
        
        // 测试写入字节数组到文件
        System.out.println("\n5. 测试写入字节数组到文件:");
        if (fileData != null) {
            writeByteArrayToFile(fileData, contentFile);
        }
        
        // 清理测试文件
        System.out.println("\n6. 清理测试文件:");
        cleanupTestFiles(testFile, copyFile, contentFile);
    }
    
    /**
     * 清理测试文件
     */
    private static void cleanupTestFiles(String... files) {
        for (String file : files) {
            File f = new File(file);
            if (f.exists()) {
                if (f.delete()) {
                    System.out.println("删除文件: " + file);
                } else {
                    System.out.println("删除文件失败: " + file);
                }
            }
        }
    }
}
