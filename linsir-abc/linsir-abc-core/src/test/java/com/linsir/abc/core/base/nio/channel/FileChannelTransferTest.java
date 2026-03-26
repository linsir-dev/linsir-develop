package com.linsir.abc.core.base.nio.channel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * FileChannelTransfer测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileChannelTransferTest {

    private FileChannelTransfer transfer;
    private Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        transfer = new FileChannelTransfer();
        tempDir = Files.createTempDirectory("file_channel_test");
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
    public void testZeroCopyTransfer() throws IOException {
        File sourceFile = tempDir.resolve("source.bin").toFile();
        File destFile = tempDir.resolve("dest.bin").toFile();

        // 创建源文件
        try (RandomAccessFile raf = new RandomAccessFile(sourceFile, "rw")) {
            raf.write("Hello, World!".getBytes());
        }

        // 执行零拷贝传输
        transfer.zeroCopyTransfer(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());

        // 验证目标文件
        assertTrue(destFile.exists());
        assertEquals(sourceFile.length(), destFile.length());

        // 验证内容
        try (RandomAccessFile raf = new RandomAccessFile(destFile, "r")) {
            byte[] data = new byte[(int) destFile.length()];
            raf.readFully(data);
            assertEquals("Hello, World!", new String(data));
        }
    }

    @Test
    public void testTransferFrom() throws IOException {
        File sourceFile = tempDir.resolve("source.bin").toFile();
        File destFile = tempDir.resolve("dest.bin").toFile();

        // 创建源文件
        try (RandomAccessFile raf = new RandomAccessFile(sourceFile, "rw")) {
            raf.write("Test Data".getBytes());
        }

        // 执行传输
        transfer.transferFrom(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());

        // 验证目标文件
        assertTrue(destFile.exists());
        assertEquals(sourceFile.length(), destFile.length());
    }

    @Test
    public void testMemoryMappedCopy() throws IOException {
        File sourceFile = tempDir.resolve("source.bin").toFile();
        File destFile = tempDir.resolve("dest.bin").toFile();

        // 创建源文件
        try (RandomAccessFile raf = new RandomAccessFile(sourceFile, "rw")) {
            raf.write("Memory Mapped Copy Test".getBytes());
        }

        // 执行内存映射拷贝
        transfer.memoryMappedCopy(sourceFile.getAbsolutePath(), destFile.getAbsolutePath());

        // 验证目标文件
        assertTrue(destFile.exists());
        assertEquals(sourceFile.length(), destFile.length());
    }

    @Test
    public void testScatteringRead() throws IOException {
        File testFile = tempDir.resolve("scatter.bin").toFile();

        // 创建测试文件
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "rw")) {
            raf.write("HelloWorldTest".getBytes());
        }

        // 准备多个缓冲区
        ByteBuffer buf1 = ByteBuffer.allocate(5);
        ByteBuffer buf2 = ByteBuffer.allocate(5);
        ByteBuffer buf3 = ByteBuffer.allocate(4);

        ByteBuffer[] buffers = {buf1, buf2, buf3};

        // 执行分散读取
        long bytesRead = transfer.scatteringRead(testFile.getAbsolutePath(), buffers);

        assertEquals(14, bytesRead);

        buf1.flip();
        buf2.flip();
        buf3.flip();

        byte[] data1 = new byte[buf1.remaining()];
        byte[] data2 = new byte[buf2.remaining()];
        byte[] data3 = new byte[buf3.remaining()];

        buf1.get(data1);
        buf2.get(data2);
        buf3.get(data3);

        assertEquals("Hello", new String(data1));
        assertEquals("World", new String(data2));
        assertEquals("Test", new String(data3));
    }

    @Test
    public void testGatheringWrite() throws IOException {
        File testFile = tempDir.resolve("gather.bin").toFile();

        // 准备多个缓冲区
        ByteBuffer buf1 = ByteBuffer.wrap("Hello".getBytes());
        ByteBuffer buf2 = ByteBuffer.wrap(" ".getBytes());
        ByteBuffer buf3 = ByteBuffer.wrap("World".getBytes());

        ByteBuffer[] buffers = {buf1, buf2, buf3};

        // 执行聚集写入
        long bytesWritten = transfer.gatheringWrite(testFile.getAbsolutePath(), buffers);

        assertEquals(11, bytesWritten);

        // 验证文件内容
        try (RandomAccessFile raf = new RandomAccessFile(testFile, "r")) {
            byte[] data = new byte[(int) testFile.length()];
            raf.readFully(data);
            assertEquals("Hello World", new String(data));
        }
    }

    @Test
    public void testCompareCopyPerformance() {
        assertDoesNotThrow(() -> FileChannelTransfer.compareCopyPerformance());
    }

    @Test
    public void testDemonstrateScatterGather() {
        assertDoesNotThrow(() -> FileChannelTransfer.demonstrateScatterGather());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> FileChannelTransfer.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> FileChannelTransfer.main(new String[]{}));
    }
}
