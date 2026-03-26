package com.linsir.abc.core.base.io.decorator;

import java.io.*;

/**
 * 缓冲流装饰器
 * 演示BufferedInputStream/BufferedOutputStream的实现原理
 *
 * 设计要点：
 * 1. 内部维护缓冲区减少系统调用
 * 2. 批量读写提高性能
 * 3. 支持mark/reset功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class BufferedStreamDecorator {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 简化版缓冲输入流实现
     */
    public static class SimpleBufferedInputStream extends InputStream {
        private final InputStream in;
        private final byte[] buffer;
        private int pos = 0;
        private int count = 0;
        private int markPos = -1;
        private int markLimit = 0;

        public SimpleBufferedInputStream(InputStream in) {
            this(in, DEFAULT_BUFFER_SIZE);
        }

        public SimpleBufferedInputStream(InputStream in, int size) {
            this.in = in;
            this.buffer = new byte[size];
        }

        /**
         * 填充缓冲区
         */
        private void fill() throws IOException {
            // 如果有mark，需要保留标记位置之后的数据
            if (markPos >= 0) {
                int sz = pos - markPos;
                if (sz >= markLimit) {
                    // 超过mark限制，重置mark
                    markPos = -1;
                } else {
                    // 将标记位置之后的数据移到缓冲区开头
                    System.arraycopy(buffer, markPos, buffer, 0, sz);
                    pos = sz;
                    markPos = 0;
                }
            } else {
                pos = 0;
            }

            // 填充缓冲区剩余空间
            int n = in.read(buffer, pos, buffer.length - pos);
            count = (n > 0) ? pos + n : pos;
        }

        @Override
        public int read() throws IOException {
            if (pos >= count) {
                fill();
                if (pos >= count) {
                    return -1;
                }
            }
            return buffer[pos++] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }

            int totalRead = 0;

            while (len > 0) {
                // 如果缓冲区为空，填充缓冲区
                if (pos >= count) {
                    fill();
                    if (pos >= count) {
                        return totalRead > 0 ? totalRead : -1;
                    }
                }

                // 从缓冲区复制数据
                int available = count - pos;
                int toRead = Math.min(len, available);
                System.arraycopy(buffer, pos, b, off, toRead);
                pos += toRead;
                off += toRead;
                len -= toRead;
                totalRead += toRead;

                // 如果请求的数据量大于缓冲区大小，直接读取
                if (len > 0 && len >= buffer.length) {
                    int n = in.read(b, off, len);
                    if (n > 0) {
                        totalRead += n;
                    }
                    break;
                }
            }

            return totalRead;
        }

        @Override
        public long skip(long n) throws IOException {
            if (n <= 0) {
                return 0;
            }

            long available = count - pos;
            if (available >= n) {
                pos += n;
                return n;
            }

            pos = count;
            return available + in.skip(n - available);
        }

        @Override
        public int available() throws IOException {
            return (count - pos) + in.available();
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readLimit) {
            this.markPos = pos;
            this.markLimit = readLimit;
        }

        @Override
        public void reset() throws IOException {
            if (markPos < 0) {
                throw new IOException("Mark not set or invalidated");
            }
            pos = markPos;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }

    /**
     * 简化版缓冲输出流实现
     */
    public static class SimpleBufferedOutputStream extends OutputStream {
        private final OutputStream out;
        private final byte[] buffer;
        private int pos = 0;

        public SimpleBufferedOutputStream(OutputStream out) {
            this(out, DEFAULT_BUFFER_SIZE);
        }

        public SimpleBufferedOutputStream(OutputStream out, int size) {
            this.out = out;
            this.buffer = new byte[size];
        }

        @Override
        public void write(int b) throws IOException {
            if (pos >= buffer.length) {
                flushBuffer();
            }
            buffer[pos++] = (byte) b;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            // 如果数据量大于缓冲区，直接写入
            if (len >= buffer.length) {
                flushBuffer();
                out.write(b, off, len);
                return;
            }

            // 如果缓冲区空间不足，先刷新
            if (len > buffer.length - pos) {
                flushBuffer();
            }

            // 复制到缓冲区
            System.arraycopy(b, off, buffer, pos, len);
            pos += len;
        }

        /**
         * 刷新缓冲区到输出流
         */
        private void flushBuffer() throws IOException {
            if (pos > 0) {
                out.write(buffer, 0, pos);
                pos = 0;
            }
        }

        @Override
        public void flush() throws IOException {
            flushBuffer();
            out.flush();
        }

        @Override
        public void close() throws IOException {
            try {
                flush();
            } finally {
                out.close();
            }
        }
    }

    /**
     * 性能比较：使用缓冲 vs 不使用缓冲
     */
    public static void performanceComparison() throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "buffer_perf");
        tempDir.mkdirs();

        String noBufferFile = new File(tempDir, "no_buffer.bin").getAbsolutePath();
        String withBufferFile = new File(tempDir, "with_buffer.bin").getAbsolutePath();

        int dataSize = 10 * 1024 * 1024; // 10MB
        byte[] data = new byte[dataSize];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 256);
        }

        // 不使用缓冲的写入
        long start = System.currentTimeMillis();
        try (FileOutputStream fos = new FileOutputStream(noBufferFile)) {
            for (byte b : data) {
                fos.write(b);
            }
        }
        long noBufferWriteTime = System.currentTimeMillis() - start;

        // 使用缓冲的写入
        start = System.currentTimeMillis();
        try (SimpleBufferedOutputStream sbos = new SimpleBufferedOutputStream(
                new FileOutputStream(withBufferFile))) {
            for (byte b : data) {
                sbos.write(b);
            }
        }
        long withBufferWriteTime = System.currentTimeMillis() - start;

        // 不使用缓冲的读取
        start = System.currentTimeMillis();
        try (FileInputStream fis = new FileInputStream(noBufferFile)) {
            while (fis.read() != -1) {
                // 逐字节读取
            }
        }
        long noBufferReadTime = System.currentTimeMillis() - start;

        // 使用缓冲的读取
        start = System.currentTimeMillis();
        try (SimpleBufferedInputStream sbis = new SimpleBufferedInputStream(
                new FileInputStream(withBufferFile))) {
            while (sbis.read() != -1) {
                // 逐字节读取
            }
        }
        long withBufferReadTime = System.currentTimeMillis() - start;

        System.out.println("性能比较 (数据大小: " + dataSize + " bytes):");
        System.out.println("  写入 - 无缓冲: " + noBufferWriteTime + " ms");
        System.out.println("  写入 - 有缓冲: " + withBufferWriteTime + " ms");
        System.out.println("  写入性能提升: " + String.format("%.2f", (double)noBufferWriteTime / withBufferWriteTime) + "x");
        System.out.println("  读取 - 无缓冲: " + noBufferReadTime + " ms");
        System.out.println("  读取 - 有缓冲: " + withBufferReadTime + " ms");
        System.out.println("  读取性能提升: " + String.format("%.2f", (double)noBufferReadTime / withBufferReadTime) + "x");

        // 清理
        new File(noBufferFile).delete();
        new File(withBufferFile).delete();
        tempDir.delete();
    }

    /**
     * 演示mark/reset功能
     */
    public static void demonstrateMarkReset() throws IOException {
        byte[] data = "Hello, World! This is a test.".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        SimpleBufferedInputStream sbis = new SimpleBufferedInputStream(bais, 16);

        System.out.println("=== Mark/Reset 演示 ===");

        // 读取前10个字节
        byte[] buf1 = new byte[10];
        sbis.read(buf1);
        System.out.println("读取前10字节: " + new String(buf1));

        // 标记当前位置
        sbis.mark(20);
        System.out.println("设置mark");

        // 继续读取10个字节
        byte[] buf2 = new byte[10];
        sbis.read(buf2);
        System.out.println("继续读取10字节: " + new String(buf2));

        // 重置到标记位置
        sbis.reset();
        System.out.println("重置到mark位置");

        // 重新读取
        byte[] buf3 = new byte[10];
        sbis.read(buf3);
        System.out.println("重置后读取10字节: " + new String(buf3));

        sbis.close();
    }

    /**
     * 主演示方法
     */
    public static void demonstrate() {
        try {
            System.out.println("=== 缓冲流装饰器演示 ===\n");

            // 性能比较
            performanceComparison();

            System.out.println("\n" + "=".repeat(50) + "\n");

            // Mark/Reset演示
            demonstrateMarkReset();

        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
