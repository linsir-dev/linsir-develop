package com.linsir.spring.framework.spring_core.resource.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UrlResource 测试类
 * 测试 URL 资源的加载和访问功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("UrlResource 测试")
public class UrlResourceTest {

    /**
     * 测试通过 URL 字符串构造
     */
    @Test
    @DisplayName("测试通过 URL 字符串构造")
    void testConstructorWithString() throws MalformedURLException {
        String urlString = "https://www.example.com/test.txt";
        UrlResource resource = new UrlResource(urlString);

        assertNotNull(resource, "资源不应该为 null");
        assertEquals(urlString, resource.getPath(), "路径应该相同");
    }

    /**
     * 测试通过 URL 对象构造
     */
    @Test
    @DisplayName("测试通过 URL 对象构造")
    void testConstructorWithUrl() throws MalformedURLException {
        URL url = new URL("https://www.example.com/test.txt");
        UrlResource resource = new UrlResource(url);

        assertNotNull(resource, "资源不应该为 null");
        assertEquals(url, resource.getUrl(), "URL 应该相同");
    }

    /**
     * 测试无效的 URL 字符串
     */
    @Test
    @DisplayName("测试无效的 URL 字符串")
    void testConstructorWithInvalidString() {
        assertThrows(MalformedURLException.class, () -> {
            new UrlResource("not a valid url");
        }, "无效的 URL 应该抛出 MalformedURLException");
    }

    /**
     * 测试获取 URL
     */
    @Test
    @DisplayName("测试获取 URL")
    void testGetURL() throws MalformedURLException, IOException {
        URL url = new URL("https://www.example.com/test.txt");
        UrlResource resource = new UrlResource(url);

        assertEquals(url, resource.getURL(), "获取的 URL 应该与构造时相同");
    }

    /**
     * 测试获取 URI
     */
    @Test
    @DisplayName("测试获取 URI")
    void testGetURI() throws Exception {
        URL url = new URL("https://www.example.com/test.txt");
        UrlResource resource = new UrlResource(url);

        assertNotNull(resource.getURI(), "URI 不应该为 null");
        assertEquals(url.toURI(), resource.getURI(), "URI 应该正确");
    }

    /**
     * 测试获取文件名
     */
    @Test
    @DisplayName("测试获取文件名")
    void testGetFilename() throws MalformedURLException {
        UrlResource resource1 = new UrlResource("https://www.example.com/path/to/file.txt");
        assertEquals("file.txt", resource1.getFilename(), "文件名应该正确");

        UrlResource resource2 = new UrlResource("https://www.example.com/");
        assertNull(resource2.getFilename(), "根路径应该返回 null");
    }

    /**
     * 测试获取描述
     */
    @Test
    @DisplayName("测试获取描述")
    void testGetDescription() throws MalformedURLException {
        String urlString = "https://www.example.com/test.txt";
        UrlResource resource = new UrlResource(urlString);

        String description = resource.getDescription();
        assertNotNull(description, "描述不应该为 null");
        assertTrue(description.startsWith("URL ["), "描述应该以 'URL [' 开头");
        assertTrue(description.contains(urlString), "描述应该包含 URL");
    }

    /**
     * 测试创建相对资源
     */
    @Test
    @DisplayName("测试创建相对资源")
    void testCreateRelative() throws MalformedURLException, IOException {
        UrlResource resource = new UrlResource("https://www.example.com/path/");
        Resource relativeResource = resource.createRelative("file.txt");

        assertNotNull(relativeResource, "相对资源不应该为 null");
        assertTrue(relativeResource instanceof UrlResource, "相对资源应该是 UrlResource");
    }

    /**
     * 测试相等性
     */
    @Test
    @DisplayName("测试相等性")
    void testEquals() throws MalformedURLException {
        String urlString = "https://www.example.com/test.txt";
        UrlResource resource1 = new UrlResource(urlString);
        UrlResource resource2 = new UrlResource(urlString);
        UrlResource resource3 = new UrlResource("https://www.example.com/other.txt");

        assertEquals(resource1, resource2, "相同 URL 的资源应该相等");
        assertNotEquals(resource1, resource3, "不同 URL 的资源不应该相等");
    }

    /**
     * 测试哈希码
     */
    @Test
    @DisplayName("测试哈希码")
    void testHashCode() throws MalformedURLException {
        String urlString = "https://www.example.com/test.txt";
        UrlResource resource1 = new UrlResource(urlString);
        UrlResource resource2 = new UrlResource(urlString);

        assertEquals(resource1.hashCode(), resource2.hashCode(), "相同 URL 的资源哈希码应该相同");
    }

    /**
     * 测试 HTTP 协议资源存在性检查
     * 注意：此测试依赖于网络连接，可能不稳定
     */
    @Test
    @DisplayName("测试 HTTP 协议资源存在性检查")
    void testExistsWithHttp() throws MalformedURLException {
        // 使用一个通常可访问的 URL 进行测试
        UrlResource resource = new UrlResource("https://www.example.com");

        // 不断言结果，因为网络状态不确定
        // 只验证方法不会抛出异常
        boolean exists = resource.exists();
        // 结果可能是 true 或 false，取决于网络状态
    }

    /**
     * 测试获取路径
     */
    @Test
    @DisplayName("测试获取路径")
    void testGetPath() throws MalformedURLException {
        String urlString = "https://www.example.com/path/to/file.txt";
        UrlResource resource = new UrlResource(urlString);

        assertEquals(urlString, resource.getPath(), "路径应该与构造时相同");
    }

    /**
     * 测试获取 URL 对象
     */
    @Test
    @DisplayName("测试获取 URL 对象")
    void testGetUrl() throws MalformedURLException {
        URL url = new URL("https://www.example.com/test.txt");
        UrlResource resource = new UrlResource(url);

        assertEquals(url, resource.getUrl(), "获取的 URL 对象应该与构造时相同");
    }
}
