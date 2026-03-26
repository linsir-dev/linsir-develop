package com.linsir.abc.core.base.io.stream;

import java.io.*;

/**
 * 字节流处理器
 * 演示InputStream/OutputStream的使用，包括文件拷贝、字节读写等
 *
 * 设计要点：
 * 1. 使用BufferedInputStream/BufferedOutputStream提高性能
 * 2. 正确处理资源关闭（try-with-resources）
 * 3. 支持大文件的分块读写
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class ByteStreamProcessor {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 拷贝文件（使用缓冲流）
     *
     * @param sourcePath 源文件路径
     * @param destPath 目标文件路径
     * @return 拷贝的字节数
     * @throws IOException 当IO操作失败时
     */
    public long copyFile(String sourcePath, String destPath) throws IOException {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            throw new FileNotFoundException("源文件不存在: " + sourcePath);
        }

        long bytesCopied = 0;

        try (InputStream fis = new FileInputStream(sourceFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             OutputStream fos = new FileOutputStream(destPath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                bytesCopied += bytesRead;
            }

            bos.flush();
        }

        return bytesCopied;
    }

    /**
     * 读取文件所有字节
     *
     * @param filePath 文件路径
     * @return 文件内容的字节数组
     * @throws IOException 当IO操作失败时
     */
    public byte[] readBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("文件太大，无法一次性读取: " + fileSize + " bytes");
        }

        try (InputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream((int) fileSize)) {

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        }
    }

    /**
     * 写入字节到文件
     *
     * @param filePath 文件路径
     * @param data 要写入的字节数据
     * @param append 是否追加模式
     * @throws IOException 当IO操作失败时
     */
    public void writeBytes(String filePath, byte[] data, boolean append) throws IOException {
        try (OutputStream fos = new FileOutputStream(filePath, append);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            bos.write(data);
            bos.flush();
        }
    }

    /**
     * 分块读取大文件
     *
     * @param filePath 文件路径
     * @param chunkSize 每个块的大小
     * @param handler 块处理器
     * @throws IOException 当IO操作失败时
     */
    public void readInChunks(String filePath, int chunkSize, ChunkHandler handler) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try (InputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] buffer = new byte[chunkSize];
            int bytesRead;
            int chunkNumber = 0;

            while ((bytesRead = bis.read(buffer)) != -1) {
                byte[] chunk = new byte[bytesRead];
                System.arraycopy(buffer, 0, chunk, 0, bytesRead);
                handler.handleChunk(chunkNumber++, chunk, bytesRead);
            }
        }
    }

    /**
     * 比较两个文件的内容是否相同
     *
     * @param filePath1 文件1路径
     * @param filePath2 文件2路径
     * @return 如果内容相同返回true
     * @throws IOException 当IO操作失败时
     */
    public boolean compareFiles(String filePath1, String filePath2) throws IOException {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);

        if (!file1.exists() || !file2.exists()) {
            return false;
        }

        if (file1.length() != file2.length()) {
            return false;
        }

        try (InputStream fis1 = new FileInputStream(file1);
             BufferedInputStream bis1 = new BufferedInputStream(fis1);
             InputStream fis2 = new FileInputStream(file2);
             BufferedInputStream bis2 = new BufferedInputStream(fis2)) {

            int byte1, byte2;
            while ((byte1 = bis1.read()) != -1) {
                byte2 = bis2.read();
                if (byte1 != byte2) {
                    return false;
                }
            }

            return bis2.read() == -1;
        }
    }

    /**
     * 计算文件的MD5哈希值
     *
     * @param filePath 文件路径
     * @return MD5哈希值的十六进制字符串
     * @throws IOException 当IO操作失败时
     */
    public String calculateFileHash(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        try (InputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;

            java.security.MessageDigest md;
            try {
                md = java.security.MessageDigest.getInstance("MD5");
            } catch (java.security.NoSuchAlgorithmException e) {
                throw new IOException("MD5算法不可用", e);
            }

            while ((bytesRead = bis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        }
    }

    /**
     * 块处理器接口
     */
    @FunctionalInterface
    public interface ChunkHandler {
        /**
         * 处理数据块
         *
         * @param chunkNumber 块编号
         * @param chunk 数据块
         * @param length 实际长度
         */
        void handleChunk(int chunkNumber, byte[] chunk, int length);
    }

    /**
     * 演示字节流的使用
     */
    public static void demonstrate() {
        ByteStreamProcessor processor = new ByteStreamProcessor();

        try {
            // 创建临时目录
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "byte_stream_demo");
            tempDir.mkdirs();

            String sourceFile = new File(tempDir, "source.txt").getAbsolutePath();
            String destFile = new File(tempDir, "dest.txt").getAbsolutePath();

            // 写入测试数据
            String testData = "Hello, World!\nThis is a test file.\n";
            processor.writeBytes(sourceFile, testData.getBytes(), false);
            System.out.println("写入文件: " + sourceFile);

            // 读取字节
            byte[] bytes = processor.readBytes(sourceFile);
            System.out.println("读取内容: " + new String(bytes));

            // 拷贝文件
            long copied = processor.copyFile(sourceFile, destFile);
            System.out.println("拷贝字节数: " + copied);

            // 比较文件
            boolean same = processor.compareFiles(sourceFile, destFile);
            System.out.println("文件相同: " + same);

            // 计算哈希
            String hash = processor.calculateFileHash(sourceFile);
            System.out.println("文件MD5: " + hash);

            // 分块读取
            System.out.println("\n分块读取:");
            processor.readInChunks(sourceFile, 10, (num, chunk, len) -> {
                System.out.println("  块 " + num + ": " + new String(chunk, 0, len));
            });

            // 清理
            new File(sourceFile).delete();
            new File(destFile).delete();
            tempDir.delete();

        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
