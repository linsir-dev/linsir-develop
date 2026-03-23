package com.linsir.spring.framework.spring_core.resource.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassPathResource 测试类
 * 测试类路径资源的加载和访问功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("ClassPathResource 测试")
public class ClassPathResourceTest {

    /**
     * 测试资源存在性检查
     */
    @Test
    @DisplayName("测试资源存在性检查 - 存在的资源")
    void testExistsWithExistingResource() {
        // 使用测试资源文件
        ClassPathResource resource = new ClassPathResource("test-config/application.properties");
        assertTrue(resource.exists(), "存在的资源应该返回 true");
    }

    /**
     * 测试资源存在性检查 - 不存在的资源
     */
    @Test
    @DisplayName("测试资源存在性检查 - 不存在的资源")
    void testExistsWithNonExistingResource() {
        ClassPathResource resource = new ClassPathResource("non-existing-file.txt");
        assertFalse(resource.exists(), "不存在的资源应该返回 false");
    }

    /**
     * 测试获取输入流
     */
    @Test
    @DisplayName("测试获取输入流")
    void testGetInputStream() throws IOException {
        ClassPathResource resource = new ClassPathResource("test-config/test.txt");

        assertTrue(resource.exists(), "资源应该存在");

        try (InputStream is = resource.getInputStream()) {
            assertNotNull(is, "输入流不应该为 null");
            byte[] content = is.readAllBytes();
            assertTrue(content.length > 0, "内容不应该为空");
        }
    }

    /**
     * 测试获取输入流 - 不存在的资源
     */
    @Test
    @DisplayName("测试获取输入流 - 不存在的资源")
    void testGetInputStreamWithNonExistingResource() {
        ClassPathResource resource = new ClassPathResource("non-existing.txt");

        assertThrows(IOException.class, () -> {
            resource.getInputStream();
        }, "不存在的资源应该抛出 IOException");
    }

    /**
     * 测试获取文件名
     */
    @Test
    @DisplayName("测试获取文件名")
    void testGetFilename() {
        ClassPathResource resource = new ClassPathResource("test-config/application.properties");
        assertEquals("application.properties", resource.getFilename(), "文件名应该正确");
    }

    /**
     * 测试获取描述
     */
    @Test
    @DisplayName("测试获取描述")
    void testGetDescription() {
        ClassPathResource resource = new ClassPathResource("test-config/test.txt");
        String description = resource.getDescription();

        assertNotNull(description, "描述不应该为 null");
        assertTrue(description.contains("class path resource"), "描述应该包含资源类型");
        assertTrue(description.contains("test-config/test.txt"), "描述应该包含路径");
    }

    /**
     * 测试获取内容长度
     */
    @Test
    @DisplayName("测试获取内容长度")
    void testContentLength() throws IOException {
        ClassPathResource resource = new ClassPathResource("test-config/test.txt");

        long length = resource.contentLength();
        assertTrue(length > 0, "内容长度应该大于 0");
    }

    /**
     * 测试创建相对资源
     */
    @Test
    @DisplayName("测试创建相对资源")
    void testCreateRelative() throws IOException {
        ClassPathResource resource = new ClassPathResource("test-config/");
        Resource relativeResource = resource.createRelative("test.txt");

        assertNotNull(relativeResource, "相对资源不应该为 null");
        assertTrue(relativeResource.exists(), "相对资源应该存在");
    }

    /**
     * 测试使用类加载器构造
     */
    @Test
    @DisplayName("测试使用类加载器构造")
    void testConstructorWithClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassPathResource resource = new ClassPathResource("test-config/application.properties", classLoader);

        assertTrue(resource.exists(), "使用类加载器构造的资源应该存在");
        assertEquals(classLoader, resource.getClassLoader(), "类加载器应该相同");
    }

    /**
     * 测试使用类构造
     */
    @Test
    @DisplayName("测试使用类构造")
    void testConstructorWithClass() {
        ClassPathResource resource = new ClassPathResource("test-config/test.txt", ClassPathResourceTest.class);

        assertTrue(resource.exists(), "使用类构造的资源应该存在");
    }

    /**
     * 测试获取 URL
     */
    @Test
    @DisplayName("测试获取 URL")
    void testGetURL() throws IOException {
        ClassPathResource resource = new ClassPathResource("test-config/application.properties");

        assertNotNull(resource.getURL(), "URL 不应该为 null");
    }

    /**
     * 测试获取 URI
     */
    @Test
    @DisplayName("测试获取 URI")
    void testGetURI() throws IOException {
        ClassPathResource resource = new ClassPathResource("test-config/application.properties");

        assertNotNull(resource.getURI(), "URI 不应该为 null");
    }

    /**
     * 测试获取文件
     */
    @Test
    @DisplayName("测试获取文件")
    void testGetFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("test-config/test.txt");

        File file = resource.getFile();
        assertNotNull(file, "文件不应该为 null");
        assertTrue(file.exists(), "文件应该存在");
    }

    /**
     * 测试可读性
     */
    @Test
    @DisplayName("测试可读性")
    void testIsReadable() {
        ClassPathResource existingResource = new ClassPathResource("test-config/test.txt");
        assertTrue(existingResource.isReadable(), "存在的资源应该可读");

        ClassPathResource nonExistingResource = new ClassPathResource("non-existing.txt");
        assertFalse(nonExistingResource.isReadable(), "不存在的资源应该不可读");
    }

    /**
     * 测试是否为文件
     */
    @Test
    @DisplayName("测试是否为文件")
    void testIsFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("test-config/test.txt");
        assertTrue(resource.isFile(), "资源应该是文件");
    }

    /**
     * 测试获取路径
     */
    @Test
    @DisplayName("测试获取路径")
    void testGetPath() {
        ClassPathResource resource = new ClassPathResource("test-config/application.properties");
        assertEquals("test-config/application.properties", resource.getPath(), "路径应该正确");
    }

    /**
     * 测试去除开头的斜杠
     */
    @Test
    @DisplayName("测试去除开头的斜杠")
    void testRemoveLeadingSlash() {
        ClassPathResource resource = new ClassPathResource("/test-config/test.txt");
        assertEquals("test-config/test.txt", resource.getPath(), "应该去除开头的斜杠");
        assertTrue(resource.exists(), "资源应该存在");
    }

    /**
     * 测试相等性
     */
    @Test
    @DisplayName("测试相等性")
    void testEquals() {
        ClassPathResource resource1 = new ClassPathResource("test-config/test.txt");
        ClassPathResource resource2 = new ClassPathResource("test-config/test.txt");
        ClassPathResource resource3 = new ClassPathResource("test-config/other.txt");

        assertEquals(resource1, resource2, "相同路径的资源应该相等");
        assertNotEquals(resource1, resource3, "不同路径的资源不应该相等");
    }

    /**
     * 测试哈希码
     */
    @Test
    @DisplayName("测试哈希码")
    void testHashCode() {
        ClassPathResource resource1 = new ClassPathResource("test-config/test.txt");
        ClassPathResource resource2 = new ClassPathResource("test-config/test.txt");

        assertEquals(resource1.hashCode(), resource2.hashCode(), "相同路径的资源哈希码应该相同");
    }
}
