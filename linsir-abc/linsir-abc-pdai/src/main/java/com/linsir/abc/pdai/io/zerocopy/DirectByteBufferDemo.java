package com.linsir.abc.pdai.io.zerocopy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用DirectByteBuffer实现直接缓冲区零拷贝
 */
public class DirectByteBufferDemo {

    /**
     * 使用DirectByteBuffer复制文件
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @throws IOException 可能的IO异常
     */
    public static void copyFile(String sourcePath, String destPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourcePath);
             FileOutputStream fos = new FileOutputStream(destPath);
             FileChannel sourceChannel = fis.getChannel();
             FileChannel destChannel = fos.getChannel()) {
            // 分配直接缓冲区
            ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
            
            // 读取数据到直接缓冲区，然后写入目标通道
            while (sourceChannel.read(buffer) != -1) {
                buffer.flip();
                destChannel.write(buffer);
                buffer.clear();
            }
        }
    }

    /**
     * 测试DirectByteBuffer的性能
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
