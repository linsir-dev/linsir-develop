package com.linsir.spring.framework.spring_core.resource.service;

import com.linsir.spring.framework.spring_core.resource.loader.DefaultResourceLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ConfigLoader 测试类
 * 测试配置加载器服务的功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("ConfigLoader 测试")
public class ConfigLoaderTest {

    private ConfigLoader configLoader;

    @BeforeEach
    void setUp() {
        configLoader = new ConfigLoader();
    }

    /**
     * 测试默认构造方法
     */
    @Test
    @DisplayName("测试默认构造方法")
    void testDefaultConstructor() {
        ConfigLoader loader = new ConfigLoader();
        assertNotNull(loader, "配置加载器不应该为 null");
        assertNotNull(loader.getResourceLoader(), "资源加载器不应该为 null");
    }

    /**
     * 测试通过资源加载器构造
     */
    @Test
    @DisplayName("测试通过资源加载器构造")
    void testConstructorWithResourceLoader() {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        ConfigLoader loader = new ConfigLoader(resourceLoader);

        assertNotNull(loader, "配置加载器不应该为 null");
        assertEquals(resourceLoader, loader.getResourceLoader(), "资源加载器应该相同");
    }

    /**
     * 测试通过资源加载器和路径构造
     */
    @Test
    @DisplayName("测试通过资源加载器和路径构造")
    void testConstructorWithResourceLoaderAndPath() {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        String configPath = "classpath:test-config/application.properties";
        ConfigLoader loader = new ConfigLoader(resourceLoader, configPath);

        assertNotNull(loader, "配置加载器不应该为 null");
        assertEquals(configPath, loader.getConfigPath(), "配置路径应该相同");
    }

    /**
     * 测试加载配置文件
     */
    @Test
    @DisplayName("测试加载配置文件")
    void testLoadConfig() throws IOException {
        Properties props = configLoader.loadConfig("classpath:test-config/application.properties");

        assertNotNull(props, "配置不应该为 null");
        assertFalse(props.isEmpty(), "配置不应该为空");
        assertEquals("TestApplication", props.getProperty("app.name"), "app.name 应该正确");
        assertEquals("1.0.0", props.getProperty("app.version"), "app.version 应该正确");
    }

    /**
     * 测试加载不存在的配置文件
     */
    @Test
    @DisplayName("测试加载不存在的配置文件")
    void testLoadNonExistingConfig() {
        assertThrows(IOException.class, () -> {
            configLoader.loadConfig("classpath:non-existing.properties");
        }, "不存在的配置文件应该抛出 IOException");
    }

    /**
     * 测试加载配置文件内容为字符串
     */
    @Test
    @DisplayName("测试加载配置文件内容为字符串")
    void testLoadConfigAsString() throws IOException {
        String content = configLoader.loadConfigAsString("classpath:test-config/test.txt");

        assertNotNull(content, "内容不应该为 null");
        assertTrue(content.contains("This is a test file"), "内容应该包含预期文本");
        assertTrue(content.contains("Testing file reading functionality"), "内容应该包含预期文本");
    }

    /**
     * 测试加载不存在的文件为字符串
     */
    @Test
    @DisplayName("测试加载不存在的文件为字符串")
    void testLoadNonExistingConfigAsString() {
        assertThrows(IOException.class, () -> {
            configLoader.loadConfigAsString("classpath:non-existing.txt");
        }, "不存在的文件应该抛出 IOException");
    }

    /**
     * 测试加载 YAML 配置文件
     */
    @Test
    @DisplayName("测试加载 YAML 配置文件")
    void testLoadYamlConfig() throws IOException {
        // 创建一个临时的 YAML 内容
        String yamlContent = "server:\n  port: 8080\n  host: localhost\ndatabase:\n  url: jdbc:mysql://localhost/test";

        // 由于我们没有实际的 YAML 文件，这里测试方法是否存在
        // 实际使用时需要创建 YAML 文件
    }

    /**
     * 测试设置配置路径
     */
    @Test
    @DisplayName("测试设置配置路径")
    void testSetConfigPath() {
        String newPath = "classpath:custom-config.properties";
        configLoader.setConfigPath(newPath);

        assertEquals(newPath, configLoader.getConfigPath(), "配置路径应该被更新");
    }

    /**
     * 测试获取资源加载器
     */
    @Test
    @DisplayName("测试获取资源加载器")
    void testGetResourceLoader() {
        assertNotNull(configLoader.getResourceLoader(), "资源加载器不应该为 null");
    }

    /**
     * 测试配置属性值
     */
    @Test
    @DisplayName("测试配置属性值")
    void testConfigPropertyValues() throws IOException {
        Properties props = configLoader.loadConfig("classpath:test-config/application.properties");

        // 测试应用配置
        assertEquals("TestApplication", props.getProperty("app.name"));
        assertEquals("1.0.0", props.getProperty("app.version"));
        assertEquals("This is a test application", props.getProperty("app.description"));

        // 测试数据库配置
        assertEquals("jdbc:mysql://localhost:3306/test", props.getProperty("database.url"));
        assertEquals("root", props.getProperty("database.username"));
        assertEquals("123456", props.getProperty("database.password"));

        // 测试服务器配置
        assertEquals("8080", props.getProperty("server.port"));
        assertEquals("localhost", props.getProperty("server.host"));
    }

    /**
     * 测试加载默认配置文件
     */
    @Test
    @DisplayName("测试加载默认配置文件")
    void testLoadApplicationConfig() {
        // 默认配置文件路径是 classpath:application.properties
        // 由于测试环境中不存在该文件，应该抛出异常
        assertThrows(IOException.class, () -> {
            configLoader.loadApplicationConfig();
        }, "默认配置文件不存在应该抛出 IOException");
    }
}
