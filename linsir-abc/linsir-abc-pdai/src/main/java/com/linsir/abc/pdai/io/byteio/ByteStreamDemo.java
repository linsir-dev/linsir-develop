package com.linsir.abc.pdai.io.byteio;

import java.io.*;

/**
 * @author linsir
 * @version 1.0
 * @description: 字节流相关示例代码
 * @date 2026/1/22 12:00
 */
public class ByteStreamDemo {
    // 测试文件路径
    private static final String TEST_FILE = "test_byte_stream.txt";
    private static final String COPY_FILE = "copy_test_byte_stream.txt";
    
    // 1. FileInputStream和FileOutputStream示例
    static class FileStreamDemo {
        public static void test() {
            System.out.println("1. FileInputStream和FileOutputStream示例:");
            
            // 写入数据到文件
            try (FileOutputStream fos = new FileOutputStream(TEST_FILE)) {
                String content = "Hello, Byte Stream!";
                fos.write(content.getBytes());
                System.out.println("成功写入文件: " + TEST_FILE);
                System.out.println("写入内容: " + content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // 从文件读取数据
            try (FileInputStream fis = new FileInputStream(TEST_FILE)) {
                byte[] buffer = new byte[1024];
                int bytesRead = fis.read(buffer);
                String content = new String(buffer, 0, bytesRead);
                System.out.println("成功读取文件: " + TEST_FILE);
                System.out.println("读取内容: " + content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // 复制文件
            try (FileInputStream fis = new FileInputStream(TEST_FILE);
                 FileOutputStream fos = new FileOutputStream(COPY_FILE)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                System.out.println("成功复制文件到: " + COPY_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            System.out.println();
        }
    }
    
    // 2. ByteArrayInputStream和ByteArrayOutputStream示例
    static class ByteArrayStreamDemo {
        public static void test() {
            System.out.println("2. ByteArrayInputStream和ByteArrayOutputStream示例:");
            
            // 使用ByteArrayOutputStream写入数据
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                String content = "Hello, ByteArray Stream!";
                baos.write(content.getBytes());
                
                // 获取字节数组
                byte[] byteArray = baos.toByteArray();
                System.out.println("写入到ByteArrayOutputStream的内容: " + new String(byteArray));
                
                // 使用ByteArrayInputStream读取数据
                try (ByteArrayInputStream bais = new ByteArrayInputStream(byteArray)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead = bais.read(buffer);
                    String readContent = new String(buffer, 0, bytesRead);
                    System.out.println("从ByteArrayInputStream读取的内容: " + readContent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            System.out.println();
        }
    }
    
    // 3. BufferedInputStream和BufferedOutputStream示例
    static class BufferedStreamDemo {
        public static void test() {
            System.out.println("3. BufferedInputStream和BufferedOutputStream示例:");
            
            // 写入数据到文件（使用缓冲流）
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(TEST_FILE))) {
                String content = "Hello, Buffered Byte Stream!\nThis is a test for buffered streams.";
                bos.write(content.getBytes());
                System.out.println("成功使用BufferedOutputStream写入文件");
                System.out.println("写入内容: " + content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // 从文件读取数据（使用缓冲流）
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(TEST_FILE))) {
                byte[] buffer = new byte[1024];
                int bytesRead = bis.read(buffer);
                String content = new String(buffer, 0, bytesRead);
                System.out.println("成功使用BufferedInputStream读取文件");
                System.out.println("读取内容: " + content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            System.out.println();
        }
    }
    
    // 4. DataInputStream和DataOutputStream示例
    static class DataStreamDemo {
        public static void test() {
            System.out.println("4. DataInputStream和DataOutputStream示例:");
            
            // 写入各种类型的数据
            try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(TEST_FILE))) {
                dos.writeBoolean(true);
                dos.writeByte(100);
                dos.writeShort(2000);
                dos.writeInt(300000);
                dos.writeLong(4000000000L);
                dos.writeFloat(3.14f);
                dos.writeDouble(3.1415926535);
                dos.writeUTF("Hello, Data Stream!");
                System.out.println("成功使用DataOutputStream写入各种类型的数据");
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // 读取各种类型的数据
            try (DataInputStream dis = new DataInputStream(new FileInputStream(TEST_FILE))) {
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
    }
    
    // 5. SequenceInputStream示例
    static class SequenceStreamDemo {
        public static void test() {
            System.out.println("5. SequenceInputStream示例:");
            
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
    }
    
    // 6. 字节流性能测试
    static class ByteStreamPerformanceTest {
        public static void test() {
            System.out.println("6. 字节流性能测试:");
            
            // 准备测试数据
            byte[] testData = new byte[1024 * 1024]; // 1MB数据
            for (int i = 0; i < testData.length; i++) {
                testData[i] = (byte) (i % 256);
            }
            
            // 测试FileOutputStream性能
            long startTime = System.currentTimeMillis();
            try (FileOutputStream fos = new FileOutputStream(TEST_FILE)) {
                fos.write(testData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long fileOutputStreamTime = System.currentTimeMillis() - startTime;
            System.out.println("FileOutputStream写入1MB数据时间: " + fileOutputStreamTime + "ms");
            
            // 测试BufferedOutputStream性能
            startTime = System.currentTimeMillis();
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(TEST_FILE))) {
                bos.write(testData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long bufferedOutputStreamTime = System.currentTimeMillis() - startTime;
            System.out.println("BufferedOutputStream写入1MB数据时间: " + bufferedOutputStreamTime + "ms");
            
            // 测试FileInputStream性能
            startTime = System.currentTimeMillis();
            try (FileInputStream fis = new FileInputStream(TEST_FILE)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    // 读取数据，不做处理
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long fileInputStreamTime = System.currentTimeMillis() - startTime;
            System.out.println("FileInputStream读取1MB数据时间: " + fileInputStreamTime + "ms");
            
            // 测试BufferedInputStream性能
            startTime = System.currentTimeMillis();
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(TEST_FILE))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    // 读取数据，不做处理
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long bufferedInputStreamTime = System.currentTimeMillis() - startTime;
            System.out.println("BufferedInputStream读取1MB数据时间: " + bufferedInputStreamTime + "ms");
            
            System.out.println();
        }
    }
    
    // 7. 字节流工具方法
    static class ByteStreamUtils {
        // 复制文件的工具方法
        public static void copyFile(String sourcePath, String destPath) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourcePath));
                 BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destPath))) {
                byte[] buffer = new byte[8192]; // 8KB缓冲区
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                System.out.println("成功复制文件: " + sourcePath + " -> " + destPath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("复制文件失败: " + e.getMessage());
            }
        }
        
        // 读取文件到字节数组的工具方法
        public static byte[] readFileToByteArray(String filePath) {
            try (FileInputStream fis = new FileInputStream(filePath);
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        // 写入字节数组到文件的工具方法
        public static void writeByteArrayToFile(byte[] data, String filePath) {
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(data);
                System.out.println("成功写入字节数组到文件: " + filePath);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("写入字节数组到文件失败: " + e.getMessage());
            }
        }
    }
    
    // 8. 字节流综合示例
    static class ByteStream综合Example {
        public static void test() {
            System.out.println("8. 字节流综合示例:");
            
            // 创建测试数据
            String testContent = "This is a comprehensive test for byte streams.\n" +
                               "It demonstrates various byte stream operations.\n" +
                               "包括文件读写、字节数组操作、缓冲流等。";
            
            // 写入到文件
            ByteStreamUtils.writeByteArrayToFile(testContent.getBytes(), TEST_FILE);
            
            // 读取文件内容
            byte[] fileData = ByteStreamUtils.readFileToByteArray(TEST_FILE);
            if (fileData != null) {
                System.out.println("读取到的文件内容:");
                System.out.println(new String(fileData));
            }
            
            // 复制文件
            ByteStreamUtils.copyFile(TEST_FILE, COPY_FILE);
            
            // 读取复制后的文件
            byte[] copyData = ByteStreamUtils.readFileToByteArray(COPY_FILE);
            if (copyData != null) {
                System.out.println("复制后的文件内容:");
                System.out.println(new String(copyData));
            }
            
            System.out.println();
        }
    }
    
    // 清理测试文件
    private static void cleanTestFiles() {
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
        
        File copyFile = new File(COPY_FILE);
        if (copyFile.exists()) {
            copyFile.delete();
        }
        
        System.out.println("清理测试文件完成");
    }
    
    public static void main(String[] args) {
        try {
            FileStreamDemo.test();
            ByteArrayStreamDemo.test();
            BufferedStreamDemo.test();
            DataStreamDemo.test();
            SequenceStreamDemo.test();
            ByteStreamPerformanceTest.test();
            ByteStream综合Example.test();
        } finally {
            cleanTestFiles();
        }
    }
}
