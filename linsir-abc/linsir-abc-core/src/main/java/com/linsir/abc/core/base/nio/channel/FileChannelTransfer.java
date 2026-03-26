package com.linsir.abc.core.base.nio.channel;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;

/**
 * 文件通道传输器
 * 演示FileChannel的文件传输操作，包括零拷贝技术
 *
 * 设计要点：
 * 1. transferTo/transferFrom实现零拷贝文件传输
 * 2. 内存映射文件提高IO性能
 * 3. 分散读取和聚集写入
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class FileChannelTransfer {

    /**
     * 使用transferTo进行零拷贝文件传输
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @throws IOException 当IO操作失败时
     */
    public void zeroCopyTransfer(String sourcePath, String destPath) throws IOException {
        try (RandomAccessFile sourceFile = new RandomAccessFile(sourcePath, "r");
             FileChannel sourceChannel = sourceFile.getChannel();
             RandomAccessFile destFile = new RandomAccessFile(destPath, "rw");
             FileChannel destChannel = destFile.getChannel()) {

            long position = 0;
            long count = sourceChannel.size();

            while (position < count) {
                // transferTo实现零拷贝：文件 -> Socket/文件
                long transferred = sourceChannel.transferTo(position, count - position, destChannel);
                position += transferred;
            }
        }
    }

    /**
     * 使用transferFrom进行文件传输
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @throws IOException 当IO操作失败时
     */
    public void transferFrom(String sourcePath, String destPath) throws IOException {
        try (RandomAccessFile sourceFile = new RandomAccessFile(sourcePath, "r");
             FileChannel sourceChannel = sourceFile.getChannel();
             RandomAccessFile destFile = new RandomAccessFile(destPath, "rw");
             FileChannel destChannel = destFile.getChannel()) {

            long position = 0;
            long count = sourceChannel.size();

            destChannel.transferFrom(sourceChannel, position, count);
        }
    }

    /**
     * 使用内存映射进行大文件拷贝
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @throws IOException 当IO操作失败时
     */
    public void memoryMappedCopy(String sourcePath, String destPath) throws IOException {
        try (RandomAccessFile sourceFile = new RandomAccessFile(sourcePath, "r");
             FileChannel sourceChannel = sourceFile.getChannel();
             RandomAccessFile destFile = new RandomAccessFile(destPath, "rw");
             FileChannel destChannel = destFile.getChannel()) {

            long fileSize = sourceChannel.size();

            // 扩展目标文件大小
            destChannel.truncate(fileSize);

            // 内存映射大小限制（2GB）
            long maxMapSize = Integer.MAX_VALUE;
            long position = 0;

            while (position < fileSize) {
                long remaining = fileSize - position;
                long mapSize = Math.min(remaining, maxMapSize);

                MappedByteBuffer sourceBuffer = sourceChannel.map(
                        FileChannel.MapMode.READ_ONLY, position, mapSize);
                MappedByteBuffer destBuffer = destChannel.map(
                        FileChannel.MapMode.READ_WRITE, position, mapSize);

                destBuffer.put(sourceBuffer);

                position += mapSize;
            }
        }
    }

    /**
     * 分散读取（Scattering Read）
     *
     * @param filePath 文件路径
     * @param buffers 缓冲区数组
     * @throws IOException 当IO操作失败时
     */
    public long scatteringRead(String filePath, ByteBuffer[] buffers) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "r");
             FileChannel channel = file.getChannel()) {
            return channel.read(buffers);
        }
    }

    /**
     * 聚集写入（Gathering Write）
     *
     * @param filePath 文件路径
     * @param buffers 缓冲区数组
     * @throws IOException 当IO操作失败时
     */
    public long gatheringWrite(String filePath, ByteBuffer[] buffers) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "rw");
             FileChannel channel = file.getChannel()) {
            return channel.write(buffers);
        }
    }

    /**
     * 比较不同拷贝方式的性能
     */
    public static void compareCopyPerformance() throws IOException {
        System.out.println("=== 文件拷贝性能比较 ===\n");

        File tempDir = new File(System.getProperty("java.io.tmpdir"), "nio_copy_perf");
        tempDir.mkdirs();

        // 创建测试文件 (10MB)
        int fileSize = 10 * 1024 * 1024;
        File sourceFile = new File(tempDir, "source.bin");

        try (RandomAccessFile raf = new RandomAccessFile(sourceFile, "rw");
             FileChannel channel = raf.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(fileSize);
            for (int i = 0; i < fileSize / 4; i++) {
                buffer.putInt(i);
            }
            buffer.flip();
            channel.write(buffer);
        }

        System.out.println("测试文件大小: " + (fileSize / 1024 / 1024) + " MB\n");

        FileChannelTransfer transfer = new FileChannelTransfer();

        // 传统IO拷贝
        long start = System.currentTimeMillis();
        File dest1 = new File(tempDir, "dest_traditional.bin");
        traditionalCopy(sourceFile, dest1);
        long traditionalTime = System.currentTimeMillis() - start;
        System.out.println("传统IO拷贝: " + traditionalTime + " ms");

        // transferTo零拷贝
        start = System.currentTimeMillis();
        File dest2 = new File(tempDir, "dest_transferTo.bin");
        transfer.zeroCopyTransfer(sourceFile.getAbsolutePath(), dest2.getAbsolutePath());
        long transferToTime = System.currentTimeMillis() - start;
        System.out.println("transferTo零拷贝: " + transferToTime + " ms");

        // 内存映射拷贝
        start = System.currentTimeMillis();
        File dest3 = new File(tempDir, "dest_mmap.bin");
        transfer.memoryMappedCopy(sourceFile.getAbsolutePath(), dest3.getAbsolutePath());
        long mmapTime = System.currentTimeMillis() - start;
        System.out.println("内存映射拷贝: " + mmapTime + " ms");

        System.out.println("\n性能提升:");
        System.out.println("  transferTo vs 传统IO: " + String.format("%.2f", (double)traditionalTime / transferToTime) + "x");
        System.out.println("  内存映射 vs 传统IO: " + String.format("%.2f", (double)traditionalTime / mmapTime) + "x");

        // 清理
        sourceFile.delete();
        dest1.delete();
        dest2.delete();
        dest3.delete();
        tempDir.delete();
    }

    /**
     * 传统IO拷贝
     */
    private static void traditionalCopy(File source, File dest) throws IOException {
        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 演示分散读取和聚集写入
     */
    public static void demonstrateScatterGather() throws IOException {
        System.out.println("\n=== 分散读取和聚集写入演示 ===\n");

        File tempDir = new File(System.getProperty("java.io.tmpdir"), "nio_scatter_gather");
        tempDir.mkdirs();
        File testFile = new File(tempDir, "test.bin");

        FileChannelTransfer transfer = new FileChannelTransfer();

        // 准备多个缓冲区
        ByteBuffer header = ByteBuffer.allocate(8);
        ByteBuffer body = ByteBuffer.allocate(32);
        ByteBuffer footer = ByteBuffer.allocate(8);

        // 写入数据到缓冲区
        header.put("HEADER".getBytes());
        body.put("This is the body content of the message.".getBytes());
        footer.put("FOOTER".getBytes());

        // 聚集写入
        header.flip();
        body.flip();
        footer.flip();

        ByteBuffer[] writeBuffers = {header, body, footer};
        transfer.gatheringWrite(testFile.getAbsolutePath(), writeBuffers);

        System.out.println("聚集写入完成，文件大小: " + testFile.length() + " bytes");

        // 分散读取
        ByteBuffer readHeader = ByteBuffer.allocate(8);
        ByteBuffer readBody = ByteBuffer.allocate(32);
        ByteBuffer readFooter = ByteBuffer.allocate(8);

        ByteBuffer[] readBuffers = {readHeader, readBody, readFooter};
        long bytesRead = transfer.scatteringRead(testFile.getAbsolutePath(), readBuffers);

        System.out.println("分散读取完成，读取字节数: " + bytesRead);

        readHeader.flip();
        readBody.flip();
        readFooter.flip();

        byte[] headerBytes = new byte[readHeader.remaining()];
        byte[] bodyBytes = new byte[readBody.remaining()];
        byte[] footerBytes = new byte[readFooter.remaining()];

        readHeader.get(headerBytes);
        readBody.get(bodyBytes);
        readFooter.get(footerBytes);

        System.out.println("Header: " + new String(headerBytes).trim());
        System.out.println("Body: " + new String(bodyBytes).trim());
        System.out.println("Footer: " + new String(footerBytes).trim());

        // 清理
        testFile.delete();
        tempDir.delete();
    }

    /**
     * 主演示方法
     */
    public static void demonstrate() {
        try {
            compareCopyPerformance();
            demonstrateScatterGather();
        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
