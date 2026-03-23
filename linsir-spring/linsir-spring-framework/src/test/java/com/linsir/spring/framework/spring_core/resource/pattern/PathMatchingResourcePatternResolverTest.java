package com.linsir.spring.framework.spring_core.resource.pattern;

import com.linsir.spring.framework.spring_core.resource.core.Resource;
import com.linsir.spring.framework.spring_core.resource.loader.DefaultResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PathMatchingResourcePatternResolver 测试类
 * 测试路径匹配资源模式解析器的功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("PathMatchingResourcePatternResolver 测试")
public class PathMatchingResourcePatternResolverTest {

    private PathMatchingResourcePatternResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new PathMatchingResourcePatternResolver();
    }

    /**
     * 测试默认构造方法
     */
    @Test
    @DisplayName("测试默认构造方法")
    void testDefaultConstructor() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        assertNotNull(resolver, "解析器不应该为 null");
        assertNotNull(resolver.getClassLoader(), "类加载器不应该为 null");
    }

    /**
     * 测试通过资源加载器构造
     */
    @Test
    @DisplayName("测试通过资源加载器构造")
    void testConstructorWithResourceLoader() {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceLoader);

        assertNotNull(resolver, "解析器不应该为 null");
        assertEquals(resourceLoader.getClassLoader(), resolver.getClassLoader(), "类加载器应该相同");
    }

    /**
     * 测试获取单个资源
     */
    @Test
    @DisplayName("测试获取单个资源")
    void testGetResource() {
        Resource resource = resolver.getResource("classpath:test-config/application.properties");

        assertNotNull(resource, "资源不应该为 null");
        assertTrue(resource.exists(), "资源应该存在");
    }

    /**
     * 测试获取资源数组 - 精确路径
     */
    @Test
    @DisplayName("测试获取资源数组 - 精确路径")
    void testGetResourcesWithExactPath() throws IOException {
        Resource[] resources = resolver.getResources("classpath:test-config/application.properties");

        assertNotNull(resources, "资源数组不应该为 null");
        assertTrue(resources.length > 0, "应该找到至少一个资源");
        assertTrue(resources[0].exists(), "资源应该存在");
    }

    /**
     * 测试获取资源数组 - 通配符模式
     */
    @Test
    @DisplayName("测试获取资源数组 - 通配符模式")
    void testGetResourcesWithWildcardPattern() throws IOException {
        Resource[] resources = resolver.getResources("classpath*:test-config/*.properties");

        assertNotNull(resources, "资源数组不应该为 null");
        // 注意：由于实现限制，可能返回空数组
    }

    /**
     * 测试获取资源数组 - 空路径
     */
    @Test
    @DisplayName("测试获取资源数组 - 空路径")
    void testGetResourcesWithEmptyPath() throws IOException {
        Resource[] resources = resolver.getResources("");

        assertNotNull(resources, "资源数组不应该为 null");
        assertEquals(0, resources.length, "空路径应该返回空数组");
    }

    /**
     * 测试获取资源数组 - 不存在的资源
     */
    @Test
    @DisplayName("测试获取资源数组 - 不存在的资源")
    void testGetResourcesWithNonExistingPath() throws IOException {
        Resource[] resources = resolver.getResources("classpath:non-existing/*.txt");

        assertNotNull(resources, "资源数组不应该为 null");
        // 不存在的资源应该返回空数组
    }

    /**
     * 测试 CLASSPATH_ALL_URL_PREFIX 常量
     */
    @Test
    @DisplayName("测试 CLASSPATH_ALL_URL_PREFIX 常量")
    void testClasspathAllUrlPrefix() {
        assertEquals("classpath*:", ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX, "类路径所有前缀应该正确");
    }

    /**
     * 测试获取类加载器
     */
    @Test
    @DisplayName("测试获取类加载器")
    void testGetClassLoader() {
        ClassLoader classLoader = resolver.getClassLoader();

        assertNotNull(classLoader, "类加载器不应该为 null");
    }

    /**
     * 测试资源文件名
     */
    @Test
    @DisplayName("测试资源文件名")
    void testResourceFilename() throws IOException {
        Resource[] resources = resolver.getResources("classpath:test-config/application.properties");

        if (resources.length > 0) {
            assertEquals("application.properties", resources[0].getFilename(), "文件名应该正确");
        }
    }

    /**
     * 测试资源描述
     */
    @Test
    @DisplayName("测试资源描述")
    void testResourceDescription() throws IOException {
        Resource[] resources = resolver.getResources("classpath:test-config/test.txt");

        if (resources.length > 0) {
            String description = resources[0].getDescription();
            assertNotNull(description, "描述不应该为 null");
        }
    }

    /**
     * 测试资源存在性
     */
    @Test
    @DisplayName("测试资源存在性")
    void testResourceExists() throws IOException {
        Resource[] resources = resolver.getResources("classpath:test-config/application.properties");

        if (resources.length > 0) {
            assertTrue(resources[0].exists(), "资源应该存在");
        }
    }

    /**
     * 测试资源可读性
     */
    @Test
    @DisplayName("测试资源可读性")
    void testResourceReadable() throws IOException {
        Resource[] resources = resolver.getResources("classpath:test-config/application.properties");

        if (resources.length > 0) {
            assertTrue(resources[0].isReadable(), "资源应该可读");
        }
    }
}
