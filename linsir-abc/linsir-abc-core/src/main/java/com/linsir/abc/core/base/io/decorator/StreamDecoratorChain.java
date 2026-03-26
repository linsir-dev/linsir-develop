package com.linsir.abc.core.base.io.decorator;

import java.io.*;

/**
 * 流装饰器链
 * 演示装饰器模式在IO流中的应用
 *
 * 设计要点：
 * 1. 装饰器模式允许动态添加功能
 * 2. 通过包装原有流来增强功能
 * 3. 可以灵活组合各种装饰器
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class StreamDecoratorChain {

    /**
     * 创建带缓冲的输入流
     *
     * @param inputStream 原始输入流
     * @return 带缓冲的输入流
     */
    public InputStream decorateWithBuffer(InputStream inputStream) {
        return new BufferedInputStream(inputStream);
    }

    /**
     * 创建带缓冲的输出流
     *
     * @param outputStream 原始输出流
     * @return 带缓冲的输出流
     */
    public OutputStream decorateWithBuffer(OutputStream outputStream) {
        return new BufferedOutputStream(outputStream);
    }

    /**
     * 创建带数据功能的输入流
     *
     * @param inputStream 原始输入流
     * @return 数据输入流
     */
    public DataInputStream decorateWithData(InputStream inputStream) {
        return new DataInputStream(inputStream);
    }

    /**
     * 创建带数据功能的输出流
     *
     * @param outputStream 原始输出流
     * @return 数据输出流
     */
    public DataOutputStream decorateWithData(OutputStream outputStream) {
        return new DataOutputStream(outputStream);
    }

    /**
     * 创建带对象序列化功能的输出流
     *
     * @param outputStream 原始输出流
     * @return 对象输出流
     */
    public ObjectOutputStream decorateWithObject(OutputStream outputStream) throws IOException {
        return new ObjectOutputStream(outputStream);
    }

    /**
     * 创建带对象反序列化功能的输入流
     *
     * @param inputStream 原始输入流
     * @return 对象输入流
     */
    public ObjectInputStream decorateWithObject(InputStream inputStream) throws IOException {
        return new ObjectInputStream(inputStream);
    }

    /**
     * 构建完整的输入流装饰链
     * FileInputStream -> BufferedInputStream -> DataInputStream
     *
     * @param filePath 文件路径
     * @return 装饰后的数据输入流
     * @throws IOException 当IO操作失败时
     */
    public DataInputStream buildInputChain(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        BufferedInputStream bis = new BufferedInputStream(fis);
        return new DataInputStream(bis);
    }

    /**
     * 构建完整的输出流装饰链
     * FileOutputStream -> BufferedOutputStream -> DataOutputStream
     *
     * @param filePath 文件路径
     * @param append 是否追加
     * @return 装饰后的数据输出流
     * @throws IOException 当IO操作失败时
     */
    public DataOutputStream buildOutputChain(String filePath, boolean append) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath, append);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        return new DataOutputStream(bos);
    }

    /**
     * 带行号计数的输入流装饰器
     */
    public static class LineNumberInputStream extends FilterInputStream {
        private int lineNumber = 1;
        private int lastChar = -1;

        public LineNumberInputStream(InputStream in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            int c = super.read();
            if (c == '\n' || (lastChar == '\r' && c != '\n')) {
                lineNumber++;
            }
            lastChar = c;
            return c;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int n = super.read(b, off, len);
            if (n > 0) {
                for (int i = off; i < off + n; i++) {
                    if (b[i] == '\n') {
                        lineNumber++;
                    }
                }
            }
            return n;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }
    }

    /**
     * 带计数功能的输出流装饰器
     */
    public static class CountingOutputStream extends FilterOutputStream {
        private long byteCount = 0;

        public CountingOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(int b) throws IOException {
            super.write(b);
            byteCount++;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            super.write(b, off, len);
            byteCount += len;
        }

        public long getByteCount() {
            return byteCount;
        }

        public void resetCount() {
            byteCount = 0;
        }
    }

    /**
     * 带校验和功能的输出流装饰器
     */
    public static class ChecksumOutputStream extends FilterOutputStream {
        private java.util.zip.Checksum checksum;

        public ChecksumOutputStream(OutputStream out, java.util.zip.Checksum checksum) {
            super(out);
            this.checksum = checksum;
        }

        @Override
        public void write(int b) throws IOException {
            super.write(b);
            checksum.update(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            super.write(b, off, len);
            checksum.update(b, off, len);
        }

        public long getChecksumValue() {
            return checksum.getValue();
        }

        public void resetChecksum() {
            checksum.reset();
        }
    }

    /**
     * 演示流装饰器链的使用
     */
    public static void demonstrate() {
        StreamDecoratorChain chain = new StreamDecoratorChain();

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "decorator_demo");
            tempDir.mkdirs();

            String dataFile = new File(tempDir, "data.bin").getAbsolutePath();
            String textFile = new File(tempDir, "text.txt").getAbsolutePath();

            // 演示1: 使用装饰链写入数据
            System.out.println("=== 演示1: 装饰链写入数据 ===");
            try (DataOutputStream dos = chain.buildOutputChain(dataFile, false)) {
                dos.writeInt(42);
                dos.writeDouble(3.14159);
                dos.writeUTF("Hello, Decorator!");
            }
            System.out.println("数据已写入: " + dataFile);

            // 演示2: 使用装饰链读取数据
            System.out.println("\n=== 演示2: 装饰链读取数据 ===");
            try (DataInputStream dis = chain.buildInputChain(dataFile)) {
                int i = dis.readInt();
                double d = dis.readDouble();
                String s = dis.readUTF();
                System.out.println("读取: int=" + i + ", double=" + d + ", string=" + s);
            }

            // 演示3: 使用行号计数装饰器
            System.out.println("\n=== 演示3: 行号计数装饰器 ===");
            try (FileWriter fw = new FileWriter(textFile)) {
                fw.write("Line 1\nLine 2\nLine 3\nLine 4\n");
            }

            try (LineNumberInputStream lnis = new LineNumberInputStream(
                    new FileInputStream(textFile))) {
                int c;
                System.out.println("开始读取，当前行号: " + lnis.getLineNumber());
                while ((c = lnis.read()) != -1) {
                    if (c == '\n') {
                        System.out.println("遇到换行，当前行号: " + lnis.getLineNumber());
                    }
                }
                System.out.println("读取完成，总行数: " + (lnis.getLineNumber() - 1));
            }

            // 演示4: 使用字节计数装饰器
            System.out.println("\n=== 演示4: 字节计数装饰器 ===");
            String countFile = new File(tempDir, "count.bin").getAbsolutePath();
            try (CountingOutputStream cos = new CountingOutputStream(
                    new FileOutputStream(countFile))) {
                cos.write("Hello, World!".getBytes());
                System.out.println("写入字节数: " + cos.getByteCount());

                cos.write(" More data here.".getBytes());
                System.out.println("累计写入字节数: " + cos.getByteCount());
            }

            // 演示5: 使用校验和装饰器
            System.out.println("\n=== 演示5: 校验和装饰器 ===");
            String checksumFile = new File(tempDir, "checksum.bin").getAbsolutePath();
            try (ChecksumOutputStream ckos = new ChecksumOutputStream(
                    new FileOutputStream(checksumFile),
                    new java.util.zip.CRC32())) {
                String data = "Data to be checksummed";
                ckos.write(data.getBytes());
                System.out.println("数据: " + data);
                System.out.println("CRC32校验和: " + ckos.getChecksumValue());
            }

            // 清理
            new File(dataFile).delete();
            new File(textFile).delete();
            new File(countFile).delete();
            new File(checksumFile).delete();
            tempDir.delete();

        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
