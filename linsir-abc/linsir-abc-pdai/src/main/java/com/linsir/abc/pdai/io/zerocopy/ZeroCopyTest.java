package com.linsir.abc.pdai.io.zerocopy;

import java.io.*;
import java.util.Random;

/**
 * 零拷贝测试主类，用于测试不同零拷贝实现方式的性能
 */
public class ZeroCopyTest {

    private static final String TEST_FILE = "test_large_file.txt";
    private static final String DEST_FILE_TRADITIONAL = "dest_traditional.txt";
    private static final String DEST_FILE_TRANSFER = "dest_transfer.txt";
    private static final String DEST_FILE_MAPPED = "dest_mapped.txt";
    private static final String DEST_FILE_DIRECT = "dest_direct.txt";
    private static final long FILE_SIZE = 1024 * 1024 * 10; // 10MB测试文件

    public static void main(String[] args) {
        try {
            // 创建测试文件
            createTestFile();
            System.out.println("测试文件创建完成，大小: " + FILE_SIZE / (1024 * 1024) + "MB");
            
            // 测试传统IO方式
            System.out.println("\n测试传统IO方式...");
            long traditionalTime = TraditionalIODemo.testPerformance(TEST_FILE, DEST_FILE_TRADITIONAL);
            System.out.println("传统IO方式耗时: " + traditionalTime + "ms");
            
            // 测试FileChannel.transferTo方式
            System.out.println("\n测试FileChannel.transferTo方式...");
            long transferTime = FileChannelTransferDemo.testPerformance(TEST_FILE, DEST_FILE_TRANSFER);
            System.out.println("FileChannel.transferTo方式耗时: " + transferTime + "ms");
            
            // 测试MappedByteBuffer方式
            System.out.println("\n测试MappedByteBuffer方式...");
            long mappedTime = MappedByteBufferDemo.testPerformance(TEST_FILE, DEST_FILE_MAPPED);
            System.out.println("MappedByteBuffer方式耗时: " + mappedTime + "ms");
            
            // 测试DirectByteBuffer方式
            System.out.println("\n测试DirectByteBuffer方式...");
            long directTime = DirectByteBufferDemo.testPerformance(TEST_FILE, DEST_FILE_DIRECT);
            System.out.println("DirectByteBuffer方式耗时: " + directTime + "ms");
            
            // 性能对比
            System.out.println("\n性能对比:");
            System.out.println("传统IO方式: " + traditionalTime + "ms");
            System.out.println("FileChannel.transferTo方式: " + transferTime + "ms (" + String.format("%.2f%%", (1 - (double)transferTime / traditionalTime) * 100) + " 提升)");
            System.out.println("MappedByteBuffer方式: " + mappedTime + "ms (" + String.format("%.2f%%", (1 - (double)mappedTime / traditionalTime) * 100) + " 提升)");
            System.out.println("DirectByteBuffer方式: " + directTime + "ms (" + String.format("%.2f%%", (1 - (double)directTime / traditionalTime) * 100) + " 提升)");
            
            // 清理测试文件
            cleanTestFiles();
            System.out.println("\n测试文件清理完成");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建测试文件
     * @throws IOException 可能的IO异常
     */
    private static void createTestFile() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(TEST_FILE);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            Random random = new Random();
            byte[] buffer = new byte[1024];
            long written = 0;
            
            while (written < FILE_SIZE) {
                random.nextBytes(buffer);
                int toWrite = (int) Math.min(buffer.length, FILE_SIZE - written);
                bos.write(buffer, 0, toWrite);
                written += toWrite;
            }
            bos.flush();
        }
    }

    /**
     * 清理测试文件
     */
    private static void cleanTestFiles() {
        deleteFile(TEST_FILE);
        deleteFile(DEST_FILE_TRADITIONAL);
        deleteFile(DEST_FILE_TRANSFER);
        deleteFile(DEST_FILE_MAPPED);
        deleteFile(DEST_FILE_DIRECT);
    }

    /**
     * 删除文件
     * @param fileName 文件名
     */
    private static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
    }
}
