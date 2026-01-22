package com.linsir.abc.pdai.io.zerocopy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用MappedByteBuffer实现内存映射零拷贝
 */
public class MappedByteBufferDemo {

    /**
     * 使用MappedByteBuffer复制文件
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @throws IOException 可能的IO异常
     */
    public static void copyFile(String sourcePath, String destPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourcePath);
             FileOutputStream fos = new FileOutputStream(destPath);
             FileChannel sourceChannel = fis.getChannel();
             FileChannel destChannel = fos.getChannel()) {
            // 使用内存映射
            long size = sourceChannel.size();
            MappedByteBuffer sourceBuffer = sourceChannel.map(FileChannel.MapMode.READ_ONLY, 0, size);
            MappedByteBuffer destBuffer = destChannel.map(FileChannel.MapMode.READ_WRITE, 0, size);
            
            // 直接从源缓冲区复制到目标缓冲区
            destBuffer.put(sourceBuffer);
        }
    }

    /**
     * 测试MappedByteBuffer的性能
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @return 执行时间（毫秒）
     * @throws IOException 可能的IO异常
     */
    public static long testPerformance(String sourcePath, String destPath) throws IOException {
        long startTime = System.currentTimeMillis();
        copyFile(sourcePath, destPath);
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
