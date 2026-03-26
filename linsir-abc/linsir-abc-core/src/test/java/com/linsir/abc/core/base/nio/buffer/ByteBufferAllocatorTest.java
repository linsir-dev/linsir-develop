package com.linsir.abc.core.base.nio.buffer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * ByteBufferAllocator测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ByteBufferAllocatorTest {

    private ByteBufferAllocator allocator;
    private Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        allocator = new ByteBufferAllocator();
        tempDir = Files.createTempDirectory("buffer_allocator_test");
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (tempDir != null) {
            Files.walk(tempDir)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // ignore
                    }
                });
        }
    }

    @Test
    public void testAllocateHeap() {
        ByteBuffer buffer = allocator.allocateHeap(1024);

        assertNotNull(buffer);
        assertEquals(1024, buffer.capacity());
        assertFalse(buffer.isDirect());
        assertTrue(buffer.hasArray());
    }

    @Test
    public void testAllocateDirect() {
        ByteBuffer buffer = allocator.allocateDirect(1024);

        assertNotNull(buffer);
        assertEquals(1024, buffer.capacity());
        assertTrue(buffer.isDirect());
        assertFalse(buffer.hasArray());
    }

    @Test
    public void testHeapBufferProperties() {
        ByteBuffer heapBuffer = allocator.allocateHeap(1024);

        assertFalse(heapBuffer.isDirect());
        assertTrue(heapBuffer.hasArray());
        assertEquals(0, heapBuffer.arrayOffset());
    }

    @Test
    public void testDirectBufferProperties() {
        ByteBuffer directBuffer = allocator.allocateDirect(1024);

        assertTrue(directBuffer.isDirect());
        assertFalse(directBuffer.hasArray());
    }

    @Test
    public void testReadOnlyBuffer() {
        ByteBuffer heapBuffer = allocator.allocateHeap(1024);
        ByteBuffer readOnly = heapBuffer.asReadOnlyBuffer();

        assertTrue(readOnly.isReadOnly());
        assertFalse(heapBuffer.isReadOnly());
    }

    @Test
    public void testSliceBuffer() {
        ByteBuffer heapBuffer = allocator.allocateHeap(1024);
        heapBuffer.position(10);
        heapBuffer.limit(100);

        ByteBuffer slice = heapBuffer.slice();

        assertEquals(0, slice.position());
        assertEquals(90, slice.limit());
        assertEquals(90, slice.capacity());
    }

    @Test
    public void testFileIOWithDirectBuffer() throws IOException {
        File testFile = tempDir.resolve("test.bin").toFile();

        // 使用直接缓冲区写入文件
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw");
             FileChannel channel = raf.getChannel()) {

            ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
            directBuffer.put("Hello, NIO Direct Buffer!".getBytes());
            directBuffer.flip();

            channel.write(directBuffer);
        }

        // 使用直接缓冲区读取文件
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "r");
             FileChannel channel = raf.getChannel()) {

            ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
            int bytesRead = channel.read(directBuffer);
            directBuffer.flip();

            byte[] data = new byte[bytesRead];
            directBuffer.get(data);

            assertEquals("Hello, NIO Direct Buffer!", new String(data));
        }
    }

    @Test
    public void testMemoryMappedFile() throws IOException {
        File testFile = tempDir.resolve("mapped.bin").toFile();
        int size = 1024;

        // 创建内存映射文件
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw");
             FileChannel channel = raf.getChannel()) {

            MappedByteBuffer mappedBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, size);

            mappedBuffer.put("Memory Mapped Buffer Data".getBytes());
            mappedBuffer.putInt(12345);
            mappedBuffer.putDouble(3.14159);

            assertTrue(mappedBuffer.isDirect());

            // 强制写入磁盘
            mappedBuffer.force();
        }

        // 读取内存映射文件
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "r");
             FileChannel channel = raf.getChannel()) {

            MappedByteBuffer mappedBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, testFile.length());

            byte[] stringData = new byte[25];
            mappedBuffer.get(stringData);
            int intValue = mappedBuffer.getInt();
            double doubleValue = mappedBuffer.getDouble();

            assertEquals("Memory Mapped Buffer Data", new String(stringData));
            assertEquals(12345, intValue);
            assertEquals(3.14159, doubleValue, 0.0001);
        }
    }

    @Test
    public void testComparePerformance() {
        assertDoesNotThrow(() -> ByteBufferAllocator.comparePerformance());
    }

    @Test
    public void testDemonstrateBufferProperties() {
        assertDoesNotThrow(() -> ByteBufferAllocator.demonstrateBufferProperties());
    }

    @Test
    public void testDemonstrateFileIO() {
        assertDoesNotThrow(() -> ByteBufferAllocator.demonstrateFileIO());
    }

    @Test
    public void testDemonstrateMemoryMappedFile() {
        assertDoesNotThrow(() -> ByteBufferAllocator.demonstrateMemoryMappedFile());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> ByteBufferAllocator.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> ByteBufferAllocator.main(new String[]{}));
    }
}
