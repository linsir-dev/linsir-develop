package com.linsir.spring.framework.spring_core.resource.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ByteArrayResource 测试类
 * 测试字节数组资源的加载和访问功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("ByteArrayResource 测试")
public class ByteArrayResourceTest {

    /**
     * 测试通过字节数组构造
     */
    @Test
    @DisplayName("测试通过字节数组构造")
    void testConstructorWithByteArray() {
        byte[] data = "Hello World".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(data);

        assertNotNull(resource, "资源不应该为 null");
        assertTrue(resource.exists(), "资源应该存在");
    }

    /**
     * 测试通过字节数组和描述构造
     */
    @Test
    @DisplayName("测试通过字节数组和描述构造")
    void testConstructorWithDescription() {
        byte[] data = "Test data".getBytes(StandardCharsets.UTF_8);
        String description = "Test resource";
        ByteArrayResource resource = new ByteArrayResource(data, description);

        assertNotNull(resource, "资源不应该为 null");
        assertTrue(resource.getDescription().contains(description), "描述应该包含传入的描述");
    }

    /**
     * 测试资源存在性检查
     */
    @Test
    @DisplayName("测试资源存在性检查")
    void testExists() {
        ByteArrayResource resource = new ByteArrayResource(new byte[0]);
        assertTrue(resource.exists(), "空字节数组资源也应该存在");
    }

    /**
     * 测试获取输入流
     */
    @Test
    @DisplayName("测试获取输入流")
    void testGetInputStream() throws IOException {
        String content = "Byte array resource test content";
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(data);

        try (InputStream is = resource.getInputStream()) {
            assertNotNull(is, "输入流不应该为 null");
            byte[] readData = is.readAllBytes();
            String readContent = new String(readData, StandardCharsets.UTF_8);
            assertEquals(content, readContent, "读取的内容应该与原始内容相同");
        }
    }

    /**
     * 测试获取内容长度
     */
    @Test
    @DisplayName("测试获取内容长度")
    void testContentLength() throws IOException {
        String content = "Test content";
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(data);

        assertEquals(data.length, resource.contentLength(), "内容长度应该正确");
    }

    /**
     * 测试获取文件名
     */
    @Test
    @DisplayName("测试获取文件名")
    void testGetFilename() {
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);
        assertNull(resource.getFilename(), "字节数组资源不应该有文件名");
    }

    /**
     * 测试获取描述
     */
    @Test
    @DisplayName("测试获取描述")
    void testGetDescription() {
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);

        String description = resource.getDescription();
        assertNotNull(description, "描述不应该为 null");
        assertTrue(description.contains("byte array resource"), "描述应该包含资源类型");
    }

    /**
     * 测试获取字节数组
     */
    @Test
    @DisplayName("测试获取字节数组")
    void testGetByteArray() {
        byte[] originalData = {1, 2, 3, 4, 5};
        ByteArrayResource resource = new ByteArrayResource(originalData);

        byte[] retrievedData = resource.getByteArray();
        assertArrayEquals(originalData, retrievedData, "获取的字节数组应该与原始数据相同");

        // 验证返回的是副本，修改不影响原始数据
        retrievedData[0] = 99;
        assertEquals(1, resource.getByteArray()[0], "修改副本不应该影响原始数据");
    }

    /**
     * 测试获取 URL - 应该抛出异常
     */
    @Test
    @DisplayName("测试获取 URL - 应该抛出异常")
    void testGetURL() {
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);

        assertThrows(IOException.class, () -> {
            resource.getURL();
        }, "字节数组资源不应该支持 URL");
    }

    /**
     * 测试获取 URI - 应该抛出异常
     */
    @Test
    @DisplayName("测试获取 URI - 应该抛出异常")
    void testGetURI() {
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);

        assertThrows(IOException.class, () -> {
            resource.getURI();
        }, "字节数组资源不应该支持 URI");
    }

    /**
     * 测试获取文件 - 应该抛出异常
     */
    @Test
    @DisplayName("测试获取文件 - 应该抛出异常")
    void testGetFile() {
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);

        assertThrows(IOException.class, () -> {
            resource.getFile();
        }, "字节数组资源不应该支持文件操作");
    }

    /**
     * 测试创建相对资源 - 应该抛出异常
     */
    @Test
    @DisplayName("测试创建相对资源 - 应该抛出异常")
    void testCreateRelative() {
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);

        assertThrows(IOException.class, () -> {
            resource.createRelative("path");
        }, "字节数组资源不应该支持相对路径");
    }

    /**
     * 测试获取最后修改时间
     */
    @Test
    @DisplayName("测试获取最后修改时间")
    void testLastModified() throws IOException {
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);
        assertEquals(-1, resource.lastModified(), "最后修改时间应该返回 -1");
    }

    /**
     * 测试可读性
     */
    @Test
    @DisplayName("测试可读性")
    void testIsReadable() {
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);
        assertTrue(resource.isReadable(), "字节数组资源应该可读");
    }

    /**
     * 测试是否已打开
     */
    @Test
    @DisplayName("测试是否已打开")
    void testIsOpen() {
        ByteArrayResource resource = new ByteArrayResource(new byte[10]);
        assertFalse(resource.isOpen(), "字节数组资源不应该被认为是已打开的");
    }

    /**
     * 测试相等性
     */
    @Test
    @DisplayName("测试相等性")
    void testEquals() {
        byte[] data1 = {1, 2, 3};
        byte[] data2 = {1, 2, 3};
        byte[] data3 = {4, 5, 6};

        ByteArrayResource resource1 = new ByteArrayResource(data1);
        ByteArrayResource resource2 = new ByteArrayResource(data2);
        ByteArrayResource resource3 = new ByteArrayResource(data3);

        assertEquals(resource1, resource2, "相同数据的资源应该相等");
        assertNotEquals(resource1, resource3, "不同数据的资源不应该相等");
    }

    /**
     * 测试哈希码
     */
    @Test
    @DisplayName("测试哈希码")
    void testHashCode() {
        byte[] data1 = {1, 2, 3};
        byte[] data2 = {1, 2, 3};

        ByteArrayResource resource1 = new ByteArrayResource(data1);
        ByteArrayResource resource2 = new ByteArrayResource(data2);

        assertEquals(resource1.hashCode(), resource2.hashCode(), "相同数据的资源哈希码应该相同");
    }

    /**
     * 测试空字节数组
     */
    @Test
    @DisplayName("测试空字节数组")
    void testEmptyByteArray() throws IOException {
        ByteArrayResource resource = new ByteArrayResource(null);

        assertTrue(resource.exists(), "空资源应该存在");
        assertEquals(0, resource.contentLength(), "空资源长度应该为 0");

        try (InputStream is = resource.getInputStream()) {
            assertEquals(0, is.readAllBytes().length, "空资源的输入流应该为空");
        }
    }
}
