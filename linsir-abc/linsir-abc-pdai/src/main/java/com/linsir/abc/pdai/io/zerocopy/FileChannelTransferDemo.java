package com.linsir.abc.pdai.io.zerocopy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 使用FileChannel.transferTo方法实现零拷贝
 */
public class FileChannelTransferDemo {

    /**
     * 使用FileChannel.transferTo方法复制文件
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @throws IOException 可能的IO异常
     */
    public static void copyFile(String sourcePath, String destPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourcePath);
             FileOutputStream fos = new FileOutputStream(destPath);
             FileChannel sourceChannel = fis.getChannel();
             FileChannel destChannel = fos.getChannel()) {
            // 使用transferTo方法实现零拷贝
            long position = 0;
            long size = sourceChannel.size();
            while (position < size) {
                long transferred = sourceChannel.transferTo(position, size - position, destChannel);
                if (transferred == 0) {
                    break;
                }
                position += transferred;
            }
        }
    }

    /**
     * 测试FileChannel.transferTo方法的性能
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
