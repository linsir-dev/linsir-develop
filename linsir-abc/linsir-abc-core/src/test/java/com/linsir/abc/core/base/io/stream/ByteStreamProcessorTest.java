package com.linsir.abc.core.base.io.stream;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ByteStreamProcessor 测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class ByteStreamProcessorTest {

    private ByteStreamProcessor processor;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        processor = new ByteStreamProcessor();
    }

    @Test
    @DisplayName("测试写入和读取字节")
    void testWriteAndReadBytes() throws IOException {
        String filePath = tempDir.resolve("test.txt").toString();
        byte[] testData = "Hello, World!".getBytes();

        processor.writeBytes(filePath, testData, false);
        byte[] readData = processor.readBytes(filePath);

        assertArrayEquals(testData, readData);
    }

    @Test
    @DisplayName("测试追加写入")
    void testAppendWrite() throws IOException {
        String filePath = tempDir.resolve("append.txt").toString();

        processor.writeBytes(filePath, "Hello".getBytes(), false);
        processor.writeBytes(filePath, " World".getBytes(), true);

        byte[] readData = processor.readBytes(filePath);
        assertEquals("Hello World", new String(readData));
    }

    @Test
    @DisplayName("测试文件拷贝")
    void testCopyFile() throws IOException {
        String sourcePath = tempDir.resolve("source.txt").toString();
        String destPath = tempDir.resolve("dest.txt").toString();
        byte[] testData = "Test content for copying".getBytes();

        processor.writeBytes(sourcePath, testData, false);
        long copied = processor.copyFile(sourcePath, destPath);

        assertEquals(testData.length, copied);
        assertArrayEquals(testData, processor.readBytes(destPath));
    }

    @Test
    @DisplayName("测试文件比较 - 相同文件")
    void testCompareFilesSame() throws IOException {
        String filePath1 = tempDir.resolve("file1.txt").toString();
        String filePath2 = tempDir.resolve("file2.txt").toString();
        byte[] testData = "Same content".getBytes();

        processor.writeBytes(filePath1, testData, false);
        processor.writeBytes(filePath2, testData, false);

        assertTrue(processor.compareFiles(filePath1, filePath2));
    }

    @Test
    @DisplayName("测试文件比较 - 不同文件")
    void testCompareFilesDifferent() throws IOException {
        String filePath1 = tempDir.resolve("file1.txt").toString();
        String filePath2 = tempDir.resolve("file2.txt").toString();

        processor.writeBytes(filePath1, "Content A".getBytes(), false);
        processor.writeBytes(filePath2, "Content B".getBytes(), false);

        assertFalse(processor.compareFiles(filePath1, filePath2));
    }

    @Test
    @DisplayName("测试分块读取")
    void testReadInChunks() throws IOException {
        String filePath = tempDir.resolve("chunks.txt").toString();
        String testData = "ABCDEFGHIJ";
        processor.writeBytes(filePath, testData.getBytes(), false);

        List<String> chunks = new ArrayList<>();
        processor.readInChunks(filePath, 3, (num, chunk, len) -> {
            chunks.add(new String(chunk, 0, len));
        });

        assertEquals(4, chunks.size());
        assertEquals("ABC", chunks.get(0));
        assertEquals("DEF", chunks.get(1));
        assertEquals("GHI", chunks.get(2));
        assertEquals("J", chunks.get(3));
    }

    @Test
    @DisplayName("测试计算文件哈希")
    void testCalculateFileHash() throws IOException {
        String filePath = tempDir.resolve("hash.txt").toString();
        processor.writeBytes(filePath, "Test data".getBytes(), false);

        String hash = processor.calculateFileHash(filePath);

        assertNotNull(hash);
        assertEquals(32, hash.length()); // MD5哈希是32位十六进制字符串
    }

    @Test
    @DisplayName("测试读取不存在的文件抛出异常")
    void testReadNonExistentFile() {
        String filePath = tempDir.resolve("nonexistent.txt").toString();

        assertThrows(IOException.class, () -> processor.readBytes(filePath));
    }

    @Test
    @DisplayName("测试拷贝不存在的文件抛出异常")
    void testCopyNonExistentFile() {
        String sourcePath = tempDir.resolve("nonexistent.txt").toString();
        String destPath = tempDir.resolve("dest.txt").toString();

        assertThrows(IOException.class, () -> processor.copyFile(sourcePath, destPath));
    }
}
