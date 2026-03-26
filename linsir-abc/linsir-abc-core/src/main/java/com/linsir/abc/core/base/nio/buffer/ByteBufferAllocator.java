package com.linsir.abc.core.base.nio.buffer;

import java.nio.*;
import java.nio.channels.*;
import java.io.*;

/**
 * ByteBuffer分配器
 * 演示堆缓冲区与直接缓冲区的区别
 *
 * 设计要点：
 * 1. 堆缓冲区：在JVM堆上分配，受GC管理
 * 2. 直接缓冲区：在堆外内存分配，不受GC管理，适合IO操作
 * 3. 直接缓冲区创建和销毁开销大，但IO性能更好
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class ByteBufferAllocator {

    /**
     * 分配堆缓冲区
     *
     * @param capacity 容量
     * @return ByteBuffer
     */
    public ByteBuffer allocateHeap(int capacity) {
        return ByteBuffer.allocate(capacity);
    }

    /**
     * 分配直接缓冲区
     *
     * @param capacity 容量
     * @return ByteBuffer
     */
    public ByteBuffer allocateDirect(int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }

    /**
     * 比较堆缓冲区和直接缓冲区的性能
     */
    public static void comparePerformance() {
        System.out.println("=== 堆缓冲区 vs 直接缓冲区性能比较 ===\n");

        int capacity = 1024 * 1024; // 1MB
        int iterations = 10000;

        // 堆缓冲区分配性能
        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(capacity);
            buffer.putInt(123);
        }
        long heapAllocateTime = System.currentTimeMillis() - start;

        // 直接缓冲区分配性能
        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(capacity);
            buffer.putInt(123);
        }
        long directAllocateTime = System.currentTimeMillis() - start;

        System.out.println("分配性能 (" + iterations + " 次, " + (capacity / 1024) + "KB):");
        System.out.println("  堆缓冲区:   " + heapAllocateTime + " ms");
        System.out.println("  直接缓冲区: " + directAllocateTime + " ms");
        System.out.println("  堆缓冲区更快: " + String.format("%.2f", (double)directAllocateTime / heapAllocateTime) + "x\n");

        // 读写性能比较
        ByteBuffer heapBuffer = ByteBuffer.allocate(capacity);
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(capacity);

        // 堆缓冲区写性能
        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            heapBuffer.clear();
            for (int j = 0; j < 1000; j++) {
                heapBuffer.putInt(j);
            }
        }
        long heapWriteTime = System.currentTimeMillis() - start;

        // 直接缓冲区写性能
        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            directBuffer.clear();
            for (int j = 0; j < 1000; j++) {
                directBuffer.putInt(j);
            }
        }
        long directWriteTime = System.currentTimeMillis() - start;

        System.out.println("写入性能 (" + iterations + " 次, 每次1000个int):");
        System.out.println("  堆缓冲区:   " + heapWriteTime + " ms");
        System.out.println("  直接缓冲区: " + directWriteTime + " ms");
        System.out.println("  直接缓冲区更快: " + String.format("%.2f", (double)heapWriteTime / directWriteTime) + "x\n");
    }

    /**
     * 演示缓冲区的特性
     */
    public static void demonstrateBufferProperties() {
        System.out.println("=== 缓冲区特性演示 ===\n");

        // 堆缓冲区
        ByteBuffer heapBuffer = ByteBuffer.allocate(1024);
        System.out.println("1. 堆缓冲区:");
        System.out.println("   isDirect: " + heapBuffer.isDirect());
        System.out.println("   hasArray: " + heapBuffer.hasArray());
        if (heapBuffer.hasArray()) {
            System.out.println("   arrayOffset: " + heapBuffer.arrayOffset());
        }

        // 直接缓冲区
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
        System.out.println("\n2. 直接缓冲区:");
        System.out.println("   isDirect: " + directBuffer.isDirect());
        System.out.println("   hasArray: " + directBuffer.hasArray());

        // 只读缓冲区
        ByteBuffer readOnly = heapBuffer.asReadOnlyBuffer();
        System.out.println("\n3. 只读缓冲区:");
        System.out.println("   isReadOnly: " + readOnly.isReadOnly());

        // 切片缓冲区
        heapBuffer.position(10);
        heapBuffer.limit(100);
        ByteBuffer slice = heapBuffer.slice();
        System.out.println("\n4. 切片缓冲区 (position=10, limit=100):");
        System.out.println("   原buffer position: " + heapBuffer.position());
        System.out.println("   slice position: " + slice.position());
        System.out.println("   slice limit: " + slice.limit());
        System.out.println("   slice capacity: " + slice.capacity());
    }

    /**
     * 演示文件IO中的缓冲区使用
     */
    public static void demonstrateFileIO() throws IOException {
        System.out.println("\n=== 文件IO中的缓冲区演示 ===\n");

        File tempDir = new File(System.getProperty("java.io.tmpdir"), "nio_buffer_demo");
        tempDir.mkdirs();
        File testFile = new File(tempDir, "test.bin");

        // 使用直接缓冲区写入文件
        System.out.println("1. 使用直接缓冲区写入文件:");
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw");
             FileChannel channel = raf.getChannel()) {

            ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
            directBuffer.put("Hello, NIO Direct Buffer!".getBytes());
            directBuffer.flip();

            channel.write(directBuffer);
            System.out.println("   写入完成");
        }

        // 使用直接缓冲区读取文件
        System.out.println("\n2. 使用直接缓冲区读取文件:");
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "r");
             FileChannel channel = raf.getChannel()) {

            ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
            int bytesRead = channel.read(directBuffer);
            directBuffer.flip();

            byte[] data = new byte[bytesRead];
            directBuffer.get(data);
            System.out.println("   读取内容: " + new String(data));
        }

        // 清理
        testFile.delete();
        tempDir.delete();
    }

    /**
     * 演示内存映射文件
     */
    public static void demonstrateMemoryMappedFile() throws IOException {
        System.out.println("\n=== 内存映射文件演示 ===\n");

        File tempDir = new File(System.getProperty("java.io.tmpdir"), "nio_mmap_demo");
        tempDir.mkdirs();
        File testFile = new File(tempDir, "mapped.bin");

        int size = 1024;

        // 创建内存映射文件
        System.out.println("1. 创建内存映射文件 (" + size + " bytes):");
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw");
             FileChannel channel = raf.getChannel()) {

            MappedByteBuffer mappedBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);

            // 写入数据
            mappedBuffer.put("Memory Mapped Buffer Data".getBytes());
            mappedBuffer.putInt(12345);
            mappedBuffer.putDouble(3.14159);

            System.out.println("   数据已写入映射缓冲区");
            System.out.println("   isDirect: " + mappedBuffer.isDirect());

            // 强制写入磁盘
            mappedBuffer.force();
            System.out.println("   已强制刷新到磁盘");
        }

        // 读取内存映射文件
        System.out.println("\n2. 读取内存映射文件:");
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "r");
             FileChannel channel = raf.getChannel()) {

            MappedByteBuffer mappedBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, testFile.length());

            byte[] stringData = new byte[25];
            mappedBuffer.get(stringData);
            int intValue = mappedBuffer.getInt();
            double doubleValue = mappedBuffer.getDouble();

            System.out.println("   字符串: " + new String(stringData));
            System.out.println("   整数: " + intValue);
            System.out.println("   浮点数: " + doubleValue);
        }

        // 清理
        testFile.delete();
        tempDir.delete();
    }

    /**
     * 主演示方法
     */
    public static void demonstrate() {
        comparePerformance();
        demonstrateBufferProperties();

        try {
            demonstrateFileIO();
            demonstrateMemoryMappedFile();
        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
