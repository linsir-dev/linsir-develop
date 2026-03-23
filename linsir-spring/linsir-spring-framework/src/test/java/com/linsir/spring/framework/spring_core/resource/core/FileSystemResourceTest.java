package com.linsir.spring.framework.spring_core.resource.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FileSystemResource 测试类
 * 测试文件系统资源的加载和访问功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("FileSystemResource 测试")
public class FileSystemResourceTest {

    @TempDir
    File tempDir;

    /**
     * 创建测试文件
     */
    private File createTestFile(String filename, String content) throws IOException {
        File file = new File(tempDir, filename);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    /**
     * 测试通过路径构造
     */
    @Test
    @DisplayName("测试通过路径构造")
    void testConstructorWithPath() throws IOException {
        File testFile = createTestFile("test.txt", "Hello World");
        FileSystemResource resource = new FileSystemResource(testFile.getAbsolutePath());

        assertTrue(resource.exists(), "资源应该存在");
        assertEquals("test.txt", resource.getFilename(), "文件名应该正确");
    }

    /**
     * 测试通过 File 对象构造
     */
    @Test
    @DisplayName("测试通过 File 对象构造")
    void testConstructorWithFile() throws IOException {
        File testFile = createTestFile("test.txt", "Hello World");
        FileSystemResource resource = new FileSystemResource(testFile);

        assertTrue(resource.exists(), "资源应该存在");
        assertEquals(testFile, resource.getFile(), "File 对象应该相同");
    }

    /**
     * 测试资源存在性检查
     */
    @Test
    @DisplayName("测试资源存在性检查")
    void testExists() {
        FileSystemResource existingResource = new FileSystemResource(tempDir.getAbsolutePath());
        assertTrue(existingResource.exists(), "存在的目录应该返回 true");

        FileSystemResource nonExistingResource = new FileSystemResource("/non/existing/path/file.txt");
        assertFalse(nonExistingResource.exists(), "不存在的资源应该返回 false");
    }

    /**
     * 测试获取输入流
     */
    @Test
    @DisplayName("测试获取输入流")
    void testGetInputStream() throws IOException {
        String content = "Test content for file system resource";
        File testFile = createTestFile("test.txt", content);
        FileSystemResource resource = new FileSystemResource(testFile);

        try (InputStream is = resource.getInputStream()) {
            assertNotNull(is, "输入流不应该为 null");
            byte[] bytes = is.readAllBytes();
            String readContent = new String(bytes, StandardCharsets.UTF_8);
            assertEquals(content, readContent, "读取的内容应该与写入的内容相同");
        }
    }

    /**
     * 测试获取输入流 - 不存在的文件
     */
    @Test
    @DisplayName("测试获取输入流 - 不存在的文件")
    void testGetInputStreamWithNonExistingFile() {
        FileSystemResource resource = new FileSystemResource("/non/existing/file.txt");

        assertThrows(IOException.class, () -> {
            resource.getInputStream();
        }, "不存在的文件应该抛出 IOException");
    }

    /**
     * 测试获取文件名
     */
    @Test
    @DisplayName("测试获取文件名")
    void testGetFilename() throws IOException {
        File testFile = createTestFile("myfile.txt", "content");
        FileSystemResource resource = new FileSystemResource(testFile);

        assertEquals("myfile.txt", resource.getFilename(), "文件名应该正确");
    }

    /**
     * 测试获取描述
     */
    @Test
    @DisplayName("测试获取描述")
    void testGetDescription() throws IOException {
        File testFile = createTestFile("test.txt", "content");
        FileSystemResource resource = new FileSystemResource(testFile);

        String description = resource.getDescription();
        assertNotNull(description, "描述不应该为 null");
        assertTrue(description.startsWith("file ["), "描述应该以 'file [' 开头");
        assertTrue(description.contains(testFile.getAbsolutePath()), "描述应该包含文件绝对路径");
    }

    /**
     * 测试获取内容长度
     */
    @Test
    @DisplayName("测试获取内容长度")
    void testContentLength() throws IOException {
        String content = "Hello, FileSystemResource!";
        File testFile = createTestFile("test.txt", content);
        FileSystemResource resource = new FileSystemResource(testFile);

        long length = resource.contentLength();
        assertEquals(content.getBytes(StandardCharsets.UTF_8).length, length, "内容长度应该正确");
    }

    /**
     * 测试获取最后修改时间
     */
    @Test
    @DisplayName("测试获取最后修改时间")
    void testLastModified() throws IOException {
        File testFile = createTestFile("test.txt", "content");
        FileSystemResource resource = new FileSystemResource(testFile);

        long lastModified = resource.lastModified();
        assertTrue(lastModified > 0, "最后修改时间应该大于 0");
        assertTrue(lastModified <= System.currentTimeMillis(), "最后修改时间不应该超过当前时间");
    }

    /**
     * 测试创建相对资源
     */
    @Test
    @DisplayName("测试创建相对资源")
    void testCreateRelative() throws IOException {
        File parentDir = new File(tempDir, "parent");
        parentDir.mkdirs();
        File testFile = new File(parentDir, "test.txt");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("content");
        }

        FileSystemResource resource = new FileSystemResource(testFile);
        Resource relativeResource = resource.createRelative("../sibling.txt");

        assertNotNull(relativeResource, "相对资源不应该为 null");
    }

    /**
     * 测试获取 URL
     */
    @Test
    @DisplayName("测试获取 URL")
    void testGetURL() throws IOException {
        File testFile = createTestFile("test.txt", "content");
        FileSystemResource resource = new FileSystemResource(testFile);

        assertNotNull(resource.getURL(), "URL 不应该为 null");
        assertEquals("file", resource.getURL().getProtocol(), "协议应该是 file");
    }

    /**
     * 测试获取 URI
     */
    @Test
    @DisplayName("测试获取 URI")
    void testGetURI() throws IOException {
        File testFile = createTestFile("test.txt", "content");
        FileSystemResource resource = new FileSystemResource(testFile);

        assertNotNull(resource.getURI(), "URI 不应该为 null");
    }

    /**
     * 测试获取文件
     */
    @Test
    @DisplayName("测试获取文件")
    void testGetFile() throws IOException {
        File testFile = createTestFile("test.txt", "content");
        FileSystemResource resource = new FileSystemResource(testFile);

        File retrievedFile = resource.getFile();
        assertNotNull(retrievedFile, "文件不应该为 null");
        assertEquals(testFile.getAbsolutePath(), retrievedFile.getAbsolutePath(), "文件路径应该相同");
    }

    /**
     * 测试可读性
     */
    @Test
    @DisplayName("测试可读性")
    void testIsReadable() throws IOException {
        File testFile = createTestFile("readable.txt", "content");
        FileSystemResource resource = new FileSystemResource(testFile);

        assertTrue(resource.isReadable(), "文件应该可读");
    }

    /**
     * 测试是否为文件
     */
    @Test
    @DisplayName("测试是否为文件")
    void testIsFile() throws IOException {
        File testFile = createTestFile("test.txt", "content");
        FileSystemResource fileResource = new FileSystemResource(testFile);
        assertTrue(fileResource.isFile(), "应该是文件");

        FileSystemResource dirResource = new FileSystemResource(tempDir);
        assertFalse(dirResource.isFile(), "目录不应该被认为是文件");
    }

    /**
     * 测试获取路径
     */
    @Test
    @DisplayName("测试获取路径")
    void testGetPath() throws IOException {
        File testFile = createTestFile("test.txt", "content");
        FileSystemResource resource = new FileSystemResource(testFile);

        assertEquals(testFile.getPath(), resource.getPath(), "路径应该相同");
    }

    /**
     * 测试相等性
     */
    @Test
    @DisplayName("测试相等性")
    void testEquals() throws IOException {
        File testFile = createTestFile("test.txt", "content");
        FileSystemResource resource1 = new FileSystemResource(testFile);
        FileSystemResource resource2 = new FileSystemResource(testFile);
        FileSystemResource resource3 = new FileSystemResource(tempDir);

        assertEquals(resource1, resource2, "相同文件的资源应该相等");
        assertNotEquals(resource1, resource3, "不同文件的资源不应该相等");
    }

    /**
     * 测试哈希码
     */
    @Test
    @DisplayName("测试哈希码")
    void testHashCode() throws IOException {
        File testFile = createTestFile("test.txt", "content");
        FileSystemResource resource1 = new FileSystemResource(testFile);
        FileSystemResource resource2 = new FileSystemResource(testFile);

        assertEquals(resource1.hashCode(), resource2.hashCode(), "相同文件的资源哈希码应该相同");
    }
}
