package com.linsir.abc.pdai.io.zerocopy;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 传统IO方式的实现，作为对比
 */
public class TraditionalIODemo {

    /**
     * 使用传统IO方式复制文件
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @throws IOException 可能的IO异常
     */
    public static void copyFile(String sourcePath, String destPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(sourcePath);
             FileOutputStream fos = new FileOutputStream(destPath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 测试传统IO方式的性能
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
