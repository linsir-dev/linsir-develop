package com.linsir.abc.core.base.io.decorator;

import java.io.*;

/**
 * 数据流装饰器
 * 演示DataInputStream/DataOutputStream的实现原理
 *
 * 设计要点：
 * 1. 提供基本类型的便捷读写
 * 2. 使用大端序（Big Endian）存储数据
 * 3. UTF-8字符串的特殊编码方式
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class DataStreamDecorator {

    /**
     * 简化版数据输入流实现
     */
    public static class SimpleDataInputStream extends FilterInputStream implements DataInput {

        public SimpleDataInputStream(InputStream in) {
            super(in);
        }

        @Override
        public void readFully(byte[] b) throws IOException {
            readFully(b, 0, b.length);
        }

        @Override
        public void readFully(byte[] b, int off, int len) throws IOException {
            int totalRead = 0;
            while (totalRead < len) {
                int read = in.read(b, off + totalRead, len - totalRead);
                if (read < 0) {
                    throw new EOFException();
                }
                totalRead += read;
            }
        }

        @Override
        public int skipBytes(int n) throws IOException {
            return (int) in.skip(n);
        }

        @Override
        public boolean readBoolean() throws IOException {
            int b = in.read();
            if (b < 0) throw new EOFException();
            return b != 0;
        }

        @Override
        public byte readByte() throws IOException {
            int b = in.read();
            if (b < 0) throw new EOFException();
            return (byte) b;
        }

        @Override
        public int readUnsignedByte() throws IOException {
            int b = in.read();
            if (b < 0) throw new EOFException();
            return b;
        }

        @Override
        public short readShort() throws IOException {
            int b1 = in.read();
            int b2 = in.read();
            if ((b1 | b2) < 0) throw new EOFException();
            return (short) ((b1 << 8) + b2);
        }

        @Override
        public int readUnsignedShort() throws IOException {
            int b1 = in.read();
            int b2 = in.read();
            if ((b1 | b2) < 0) throw new EOFException();
            return (b1 << 8) + b2;
        }

        @Override
        public char readChar() throws IOException {
            int b1 = in.read();
            int b2 = in.read();
            if ((b1 | b2) < 0) throw new EOFException();
            return (char) ((b1 << 8) + b2);
        }

        @Override
        public int readInt() throws IOException {
            int b1 = in.read();
            int b2 = in.read();
            int b3 = in.read();
            int b4 = in.read();
            if ((b1 | b2 | b3 | b4) < 0) throw new EOFException();
            return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
        }

        @Override
        public long readLong() throws IOException {
            return ((long) readInt() << 32) + (readInt() & 0xFFFFFFFFL);
        }

        @Override
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(readInt());
        }

        @Override
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(readLong());
        }

        @Override
        public String readLine() throws IOException {
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = in.read()) != -1) {
                if (c == '\n') {
                    break;
                }
                if (c == '\r') {
                    int next = in.read();
                    if (next != '\n' && next != -1) {
                        // 如果后面不是\n，需要把字符放回去
                        // 这里简化处理，实际应该使用PushbackInputStream
                    }
                    break;
                }
                sb.append((char) c);
            }
            return sb.length() > 0 || c != -1 ? sb.toString() : null;
        }

        @Override
        public String readUTF() throws IOException {
            int length = readUnsignedShort();
            byte[] bytes = new byte[length];
            readFully(bytes);
            return new String(bytes, "UTF-8");
        }
    }

    /**
     * 简化版数据输出流实现
     */
    public static class SimpleDataOutputStream extends FilterOutputStream implements DataOutput {

        public SimpleDataOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void writeBoolean(boolean v) throws IOException {
            out.write(v ? 1 : 0);
        }

        @Override
        public void writeByte(int v) throws IOException {
            out.write(v);
        }

        @Override
        public void writeShort(int v) throws IOException {
            out.write((v >>> 8) & 0xFF);
            out.write(v & 0xFF);
        }

        @Override
        public void writeChar(int v) throws IOException {
            out.write((v >>> 8) & 0xFF);
            out.write(v & 0xFF);
        }

        @Override
        public void writeInt(int v) throws IOException {
            out.write((v >>> 24) & 0xFF);
            out.write((v >>> 16) & 0xFF);
            out.write((v >>> 8) & 0xFF);
            out.write(v & 0xFF);
        }

        @Override
        public void writeLong(long v) throws IOException {
            writeInt((int) (v >>> 32));
            writeInt((int) v);
        }

        @Override
        public void writeFloat(float v) throws IOException {
            writeInt(Float.floatToIntBits(v));
        }

        @Override
        public void writeDouble(double v) throws IOException {
            writeLong(Double.doubleToLongBits(v));
        }

        @Override
        public void writeBytes(String s) throws IOException {
            for (int i = 0; i < s.length(); i++) {
                out.write((byte) s.charAt(i));
            }
        }

        @Override
        public void writeChars(String s) throws IOException {
            for (int i = 0; i < s.length(); i++) {
                writeChar(s.charAt(i));
            }
        }

        @Override
        public void writeUTF(String s) throws IOException {
            byte[] bytes = s.getBytes("UTF-8");
            writeShort(bytes.length);
            out.write(bytes);
        }

        @Override
        public void write(int b) throws IOException {
            out.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
        }
    }

    /**
     * 字节序转换工具
     */
    public static class ByteOrderConverter {

        /**
         * 短整型：大端序转小端序
         */
        public static short bigToLittleEndian(short value) {
            return (short) (((value & 0xFF) << 8) | ((value >> 8) & 0xFF));
        }

        /**
         * 整型：大端序转小端序
         */
        public static int bigToLittleEndian(int value) {
            return ((value & 0xFF) << 24)
                 | ((value & 0xFF00) << 8)
                 | ((value >> 8) & 0xFF00)
                 | ((value >> 24) & 0xFF);
        }

        /**
         * 长整型：大端序转小端序
         */
        public static long bigToLittleEndian(long value) {
            return ((value & 0xFFL) << 56)
                 | ((value & 0xFF00L) << 40)
                 | ((value & 0xFF0000L) << 24)
                 | ((value & 0xFF000000L) << 8)
                 | ((value >> 8) & 0xFF000000L)
                 | ((value >> 24) & 0xFF0000L)
                 | ((value >> 40) & 0xFF00L)
                 | ((value >> 56) & 0xFFL);
        }
    }

    /**
     * 演示数据流的使用
     */
    public static void demonstrate() {
        try {
            System.out.println("=== 数据流装饰器演示 ===\n");

            // 创建临时文件
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "data_stream_demo");
            tempDir.mkdirs();
            String dataFile = new File(tempDir, "data.bin").getAbsolutePath();

            // 使用简化版数据输出流写入数据
            System.out.println("--- 写入数据 ---");
            try (FileOutputStream fos = new FileOutputStream(dataFile);
                 SimpleDataOutputStream sdos = new SimpleDataOutputStream(fos)) {

                sdos.writeBoolean(true);
                sdos.writeByte(42);
                sdos.writeShort(1000);
                sdos.writeInt(123456789);
                sdos.writeLong(9876543210L);
                sdos.writeFloat(3.14159f);
                sdos.writeDouble(2.718281828459045);
                sdos.writeChar('A');
                sdos.writeUTF("Hello, SimpleDataStream!");

                System.out.println("已写入各种基本类型数据");
            }

            // 使用简化版数据输入流读取数据
            System.out.println("\n--- 读取数据 ---");
            try (FileInputStream fis = new FileInputStream(dataFile);
                 SimpleDataInputStream sdis = new SimpleDataInputStream(fis)) {

                System.out.println("boolean: " + sdis.readBoolean());
                System.out.println("byte: " + sdis.readByte());
                System.out.println("short: " + sdis.readShort());
                System.out.println("int: " + sdis.readInt());
                System.out.println("long: " + sdis.readLong());
                System.out.println("float: " + sdis.readFloat());
                System.out.println("double: " + sdis.readDouble());
                System.out.println("char: " + sdis.readChar());
                System.out.println("UTF: " + sdis.readUTF());
            }

            // 字节序演示
            System.out.println("\n--- 字节序转换 ---");
            int testValue = 0x12345678;
            System.out.printf("原始值 (大端): 0x%08X%n", testValue);
            int littleEndian = ByteOrderConverter.bigToLittleEndian(testValue);
            System.out.printf("转换后 (小端): 0x%08X%n", littleEndian);
            int backToBig = ByteOrderConverter.bigToLittleEndian(littleEndian);
            System.out.printf("转回大端: 0x%08X%n", backToBig);

            // 清理
            new File(dataFile).delete();
            tempDir.delete();

        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
