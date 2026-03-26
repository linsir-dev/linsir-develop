package com.linsir.abc.core.base.io.reader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * EncodingConverter测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class EncodingConverterTest {

    private EncodingConverter converter;
    private Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        converter = new EncodingConverter();
        tempDir = Files.createTempDirectory("encoding_test");
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
    public void testConvertStringEncoding() {
        String original = "Hello, 世界!";

        // UTF-8 -> GBK -> UTF-8
        String gbk = converter.convertStringEncoding(original, "UTF-8", "GBK");
        String restored = converter.convertStringEncoding(gbk, "GBK", "UTF-8");

        assertEquals(original, restored);
    }

    @Test
    public void testConvertStringEncodingAscii() {
        String original = "Hello, World!";

        // ASCII文本在不同编码间转换应该保持一致
        String iso8859 = converter.convertStringEncoding(original, "UTF-8", "ISO-8859-1");
        String restored = converter.convertStringEncoding(iso8859, "ISO-8859-1", "UTF-8");

        assertEquals(original, restored);
    }

    @Test
    public void testConvertEncoding() throws IOException {
        String sourcePath = tempDir.resolve("source.txt").toString();
        String destPath = tempDir.resolve("dest.txt").toString();

        // 创建UTF-8编码的源文件（使用ASCII字符避免编码问题）
        String content = "Hello, World!";
        try (FileOutputStream fos = new FileOutputStream(sourcePath)) {
            fos.write(content.getBytes("UTF-8"));
        }

        // 转换编码
        converter.convertEncoding(sourcePath, destPath, "UTF-8", "ISO-8859-1");

        // 验证目标文件存在
        assertTrue(new File(destPath).exists());

        // 读取并验证内容
        byte[] bytes = Files.readAllBytes(new File(destPath).toPath());
        String restored = new String(bytes, "ISO-8859-1");

        // 转换回UTF-8进行比较
        String utf8Restored = converter.convertStringEncoding(restored, "ISO-8859-1", "UTF-8");
        assertEquals(content, utf8Restored);
    }

    @Test
    public void testDetectEncodingUTF8() throws IOException {
        String filePath = tempDir.resolve("utf8_bom.txt").toString();

        // 创建带BOM的UTF-8文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(0xEF);
            fos.write(0xBB);
            fos.write(0xBF);
            fos.write("Hello".getBytes("UTF-8"));
        }

        String encoding = converter.detectEncoding(filePath);
        assertEquals("UTF-8", encoding);
    }

    @Test
    public void testDetectEncodingUTF16BE() throws IOException {
        String filePath = tempDir.resolve("utf16be_bom.txt").toString();

        // 创建带BOM的UTF-16BE文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(0xFE);
            fos.write(0xFF);
            fos.write("A".getBytes("UTF-16BE"));
        }

        String encoding = converter.detectEncoding(filePath);
        assertEquals("UTF-16BE", encoding);
    }

    @Test
    public void testDetectEncodingUTF16LE() throws IOException {
        String filePath = tempDir.resolve("utf16le_bom.txt").toString();

        // 创建带BOM的UTF-16LE文件
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(0xFF);
            fos.write(0xFE);
            fos.write("A".getBytes("UTF-16LE"));
        }

        String encoding = converter.detectEncoding(filePath);
        assertEquals("UTF-16LE", encoding);
    }

    @Test
    public void testDemonstrate() {
        // 测试演示方法不抛出异常
        assertDoesNotThrow(() -> EncodingConverter.demonstrate());
    }
}
