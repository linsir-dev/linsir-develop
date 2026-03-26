package com.linsir.abc.core.base.io.reader;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CharacterStreamProcessor 测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class CharacterStreamProcessorTest {

    private CharacterStreamProcessor processor;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        processor = new CharacterStreamProcessor();
    }

    @Test
    @DisplayName("测试写入和读取文本")
    void testWriteAndReadText() throws IOException {
        String filePath = tempDir.resolve("test.txt").toString();
        String content = "Hello, World!\nThis is a test.";

        processor.writeText(filePath, content, false);
        String readContent = processor.readText(filePath);

        assertEquals(content, readContent);
    }

    @Test
    @DisplayName("测试追加写入文本")
    void testAppendText() throws IOException {
        String filePath = tempDir.resolve("append.txt").toString();

        processor.writeText(filePath, "Hello", false);
        processor.writeText(filePath, " World", true);

        String readContent = processor.readText(filePath);
        assertEquals("Hello World", readContent);
    }

    @Test
    @DisplayName("测试按行读取")
    void testReadLines() throws IOException {
        String filePath = tempDir.resolve("lines.txt").toString();
        List<String> lines = Arrays.asList("Line 1", "Line 2", "Line 3");

        processor.writeLines(filePath, lines, "UTF-8", false);
        List<String> readLines = processor.readLines(filePath);

        assertEquals(lines, readLines);
    }

    @Test
    @DisplayName("测试写入和读取多行")
    void testWriteAndReadLines() throws IOException {
        String filePath = tempDir.resolve("multilines.txt").toString();
        List<String> lines = Arrays.asList("First line", "Second line", "Third line");

        processor.writeLines(filePath, lines, "UTF-8", false);
        List<String> readLines = processor.readLines(filePath);

        assertEquals(3, readLines.size());
        assertEquals("First line", readLines.get(0));
        assertEquals("Second line", readLines.get(1));
        assertEquals("Third line", readLines.get(2));
    }

    @Test
    @DisplayName("测试使用指定编码写入和读取")
    void testWriteAndReadWithEncoding() throws IOException {
        String filePath = tempDir.resolve("encoding.txt").toString();
        String content = "中文测试内容";

        processor.writeText(filePath, content, "UTF-8", false);
        String readContent = processor.readText(filePath, "UTF-8");

        assertEquals(content, readContent);
    }

    @Test
    @DisplayName("测试读取不存在的文件抛出异常")
    void testReadNonExistentFile() {
        String filePath = tempDir.resolve("nonexistent.txt").toString();

        assertThrows(IOException.class, () -> processor.readText(filePath));
    }
}
