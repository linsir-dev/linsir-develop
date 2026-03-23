package com.linsir.spring.framework.spring_core.resource.loader;

import com.linsir.spring.framework.spring_core.resource.core.ClassPathResource;
import com.linsir.spring.framework.spring_core.resource.core.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultResourceLoader 测试类
 * 测试默认资源加载器的加载功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("DefaultResourceLoader 测试")
public class DefaultResourceLoaderTest {

    private DefaultResourceLoader resourceLoader;

    @BeforeEach
    void setUp() {
        resourceLoader = new DefaultResourceLoader();
    }

    /**
     * 测试加载类路径资源（使用 classpath: 前缀）
     */
    @Test
    @DisplayName("测试加载类路径资源 - classpath: 前缀")
    void testLoadClassPathResourceWithPrefix() {
        Resource resource = resourceLoader.getResource("classpath:test-config/application.properties");

        assertNotNull(resource, "资源不应该为 null");
        assertTrue(resource instanceof ClassPathResource, "应该是 ClassPathResource 类型");
        assertTrue(resource.exists(), "资源应该存在");
    }

    /**
     * 测试加载类路径资源（使用 / 开头的绝对路径）
     */
    @Test
    @DisplayName("测试加载类路径资源 - / 开头的绝对路径")
    void testLoadClassPathResourceWithAbsolutePath() {
        Resource resource = resourceLoader.getResource("/test-config/test.txt");

        assertNotNull(resource, "资源不应该为 null");
        assertTrue(resource instanceof ClassPathResource, "应该是 ClassPathResource 类型");
    }

    /**
     * 测试加载类路径资源（无前缀，默认作为类路径）
     */
    @Test
    @DisplayName("测试加载类路径资源 - 无前缀默认")
    void testLoadClassPathResourceWithoutPrefix() {
        Resource resource = resourceLoader.getResource("test-config/application.properties");

        assertNotNull(resource, "资源不应该为 null");
        assertTrue(resource instanceof ClassPathResource, "应该是 ClassPathResource 类型");
        assertTrue(resource.exists(), "资源应该存在");
    }

    /**
     * 测试加载文件系统资源（使用 file: 前缀）
     */
    @Test
    @DisplayName("测试加载文件系统资源 - file: 前缀")
    void testLoadFileResourceWithPrefix() {
        Resource resource = resourceLoader.getResource("file:/tmp/test.txt");

        assertNotNull(resource, "资源不应该为 null");
        // 即使文件不存在，也应该返回 UrlResource
    }

    /**
     * 测试加载 URL 资源（使用 http: 前缀）
     */
    @Test
    @DisplayName("测试加载 URL 资源 - http: 前缀")
    void testLoadUrlResourceWithHttpPrefix() {
        Resource resource = resourceLoader.getResource("https://www.example.com/test.txt");

        assertNotNull(resource, "资源不应该为 null");
    }

    /**
     * 测试加载不存在的资源
     */
    @Test
    @DisplayName("测试加载不存在的资源")
    void testLoadNonExistingResource() {
        Resource resource = resourceLoader.getResource("classpath:non-existing-file.txt");

        assertNotNull(resource, "资源不应该为 null");
        assertFalse(resource.exists(), "不存在的资源应该返回 false");
    }

    /**
     * 测试获取类加载器
     */
    @Test
    @DisplayName("测试获取类加载器")
    void testGetClassLoader() {
        ClassLoader classLoader = resourceLoader.getClassLoader();

        assertNotNull(classLoader, "类加载器不应该为 null");
    }

    /**
     * 测试设置类加载器
     */
    @Test
    @DisplayName("测试设置类加载器")
    void testSetClassLoader() {
        ClassLoader customClassLoader = new ClassLoader() {
            // 自定义类加载器
        };

        resourceLoader.setClassLoader(customClassLoader);

        assertEquals(customClassLoader, resourceLoader.getClassLoader(), "类加载器应该被设置");
    }

    /**
     * 测试使用自定义类加载器构造
     */
    @Test
    @DisplayName("测试使用自定义类加载器构造")
    void testConstructorWithClassLoader() {
        ClassLoader customClassLoader = Thread.currentThread().getContextClassLoader();
        DefaultResourceLoader loader = new DefaultResourceLoader(customClassLoader);

        assertEquals(customClassLoader, loader.getClassLoader(), "类加载器应该与构造时相同");
    }

    /**
     * 测试加载空路径 - 应该抛出异常
     */
    @Test
    @DisplayName("测试加载空路径 - 应该抛出异常")
    void testLoadEmptyPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            resourceLoader.getResource("");
        }, "空路径应该抛出 IllegalArgumentException");
    }

    /**
     * 测试加载 null 路径 - 应该抛出异常
     */
    @Test
    @DisplayName("测试加载 null 路径 - 应该抛出异常")
    void testLoadNullPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            resourceLoader.getResource(null);
        }, "null 路径应该抛出 IllegalArgumentException");
    }

    /**
     * 测试 CLASSPATH_URL_PREFIX 常量
     */
    @Test
    @DisplayName("测试 CLASSPATH_URL_PREFIX 常量")
    void testClasspathUrlPrefix() {
        assertEquals("classpath:", ResourceLoader.CLASSPATH_URL_PREFIX, "类路径前缀应该正确");
    }

    /**
     * 测试加载 jar 协议资源
     */
    @Test
    @DisplayName("测试加载 jar 协议资源")
    void testLoadJarResource() {
        Resource resource = resourceLoader.getResource("jar:file:/path/to/file.jar!/resource.txt");

        assertNotNull(resource, "资源不应该为 null");
    }

    /**
     * 测试加载 ftp 协议资源
     */
    @Test
    @DisplayName("测试加载 ftp 协议资源")
    void testLoadFtpResource() {
        Resource resource = resourceLoader.getResource("ftp://ftp.example.com/file.txt");

        assertNotNull(resource, "资源不应该为 null");
    }

    /**
     * 测试资源描述
     */
    @Test
    @DisplayName("测试资源描述")
    void testResourceDescription() {
        Resource resource = resourceLoader.getResource("classpath:test-config/application.properties");

        String description = resource.getDescription();
        assertNotNull(description, "描述不应该为 null");
        assertTrue(description.contains("class path resource"), "描述应该包含资源类型");
    }
}
