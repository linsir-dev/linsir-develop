package com.linsir.spring.framework.spring_core.resource;

import com.linsir.spring.framework.spring_core.resource.core.*;
import com.linsir.spring.framework.spring_core.resource.loader.DefaultResourceLoader;
import com.linsir.spring.framework.spring_core.resource.loader.ResourceLoader;
import com.linsir.spring.framework.spring_core.resource.pattern.PathMatchingResourcePatternResolver;
import com.linsir.spring.framework.spring_core.resource.pattern.ResourcePatternResolver;
import com.linsir.spring.framework.spring_core.resource.service.ConfigLoader;
import com.linsir.spring.framework.spring_core.resource.utils.ResourceUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 资源抽象集成测试类
 * 测试资源抽象各组件的协同工作
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("资源抽象集成测试")
public class ResourceIntegrationTest {

    @TempDir
    File tempDir;

    /**
     * 测试完整的资源加载流程
     */
    @Test
    @DisplayName("测试完整的资源加载流程")
    void testCompleteResourceLoadingFlow() throws IOException {
        // 1. 创建资源加载器
        ResourceLoader loader = new DefaultResourceLoader();

        // 2. 加载类路径资源
        Resource classpathResource = loader.getResource("classpath:test-config/application.properties");
        assertTrue(classpathResource.exists(), "类路径资源应该存在");

        // 3. 使用 ResourceUtils 读取内容
        String content = ResourceUtils.readAsString(classpathResource);
        assertNotNull(content, "内容不应该为 null");
        assertTrue(content.contains("app.name"), "内容应该包含 app.name");

        // 4. 验证资源类型
        assertTrue(ResourceUtils.isTextFile(classpathResource), "应该是文本文件");
        assertEquals("properties", ResourceUtils.getFileExtension(classpathResource), "扩展名应该正确");
    }

    /**
     * 测试资源模式解析器与资源加载器的集成
     */
    @Test
    @DisplayName("测试资源模式解析器与资源加载器的集成")
    void testResourcePatternResolverIntegration() throws IOException {
        // 1. 创建资源模式解析器
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // 2. 加载单个资源
        Resource resource = resolver.getResource("classpath:test-config/test.txt");
        assertTrue(resource.exists(), "资源应该存在");

        // 3. 读取资源内容
        List<String> lines = ResourceUtils.readAsLines(resource);
        assertFalse(lines.isEmpty(), "行列表不应该为空");
    }

    /**
     * 测试配置加载器与资源加载器的集成
     */
    @Test
    @DisplayName("测试配置加载器与资源加载器的集成")
    void testConfigLoaderIntegration() throws IOException {
        // 1. 创建配置加载器
        ConfigLoader configLoader = new ConfigLoader();

        // 2. 加载配置文件
        Properties props = configLoader.loadConfig("classpath:test-config/application.properties");

        // 3. 验证配置值
        assertEquals("TestApplication", props.getProperty("app.name"));
        assertEquals("1.0.0", props.getProperty("app.version"));

        // 4. 验证所有配置项
        assertNotNull(props.getProperty("database.url"));
        assertNotNull(props.getProperty("server.port"));
    }

    /**
     * 测试多种资源类型的统一处理
     */
    @Test
    @DisplayName("测试多种资源类型的统一处理")
    void testMultipleResourceTypesUnifiedHandling() throws IOException {
        // 1. 创建不同类型的资源
        Resource classpathResource = new ClassPathResource("test-config/test.txt");

        File tempFile = new File(tempDir, "temp.txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Temporary file content");
        }
        Resource fileSystemResource = new FileSystemResource(tempFile);

        String content = "Byte array content";
        Resource byteArrayResource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));

        // 2. 统一处理所有资源
        assertTrue(classpathResource.exists());
        assertTrue(fileSystemResource.exists());
        assertTrue(byteArrayResource.exists());

        // 3. 统一读取内容
        String classpathContent = ResourceUtils.readAsString(classpathResource);
        String fileSystemContent = ResourceUtils.readAsString(fileSystemResource);
        String byteArrayContent = ResourceUtils.readAsString(byteArrayResource);

        assertNotNull(classpathContent);
        assertNotNull(fileSystemContent);
        assertNotNull(byteArrayContent);
    }

    /**
     * 测试资源相对路径解析
     */
    @Test
    @DisplayName("测试资源相对路径解析")
    void testResourceRelativePathResolution() throws IOException {
        // 1. 创建基础资源
        ClassPathResource baseResource = new ClassPathResource("test-config/");

        // 2. 创建相对资源
        Resource relativeResource = baseResource.createRelative("application.properties");

        // 3. 验证相对资源
        assertTrue(relativeResource.exists(), "相对资源应该存在");
        assertEquals("application.properties", relativeResource.getFilename());
    }

    /**
     * 测试资源缓存功能
     */
    @Test
    @DisplayName("测试资源缓存功能")
    void testResourceCaching() throws IOException {
        // 1. 创建资源加载器
        ResourceLoader loader = new DefaultResourceLoader();

        // 2. 多次加载同一资源
        Resource resource1 = loader.getResource("classpath:test-config/application.properties");
        Resource resource2 = loader.getResource("classpath:test-config/application.properties");

        // 3. 验证资源内容一致性
        String content1 = ResourceUtils.readAsString(resource1);
        String content2 = ResourceUtils.readAsString(resource2);

        assertEquals(content1, content2, "相同资源的内容应该一致");
    }

    /**
     * 测试资源工具类的各种方法
     */
    @Test
    @DisplayName("测试资源工具类的各种方法")
    void testResourceUtilsMethods() throws IOException {
        // 1. 准备资源
        Resource textResource = new ClassPathResource("test-config/test.txt");
        Resource propertiesResource = new ClassPathResource("test-config/application.properties");

        // 2. 测试 readAsBytes
        byte[] bytes = ResourceUtils.readAsBytes(textResource);
        assertTrue(bytes.length > 0, "字节数组不应该为空");

        // 3. 测试 readAsLines
        List<String> lines = ResourceUtils.readAsLines(textResource);
        assertFalse(lines.isEmpty(), "行列表不应该为空");

        // 4. 测试 getFileExtension
        assertEquals("txt", ResourceUtils.getFileExtension(textResource));
        assertEquals("properties", ResourceUtils.getFileExtension(propertiesResource));

        // 5. 测试 isTextFile
        assertTrue(ResourceUtils.isTextFile(textResource));
        assertTrue(ResourceUtils.isTextFile(propertiesResource));

        // 6. 测试 getMimeType
        assertEquals("text/plain", ResourceUtils.getMimeType(textResource));
        assertEquals("text/plain", ResourceUtils.getMimeType(propertiesResource));
    }

    /**
     * 测试文件系统资源与类路径资源的互操作
     */
    @Test
    @DisplayName("测试文件系统资源与类路径资源的互操作")
    void testFileSystemAndClasspathResourceInteroperability() throws IOException {
        // 1. 从类路径加载资源
        ClassPathResource classpathResource = new ClassPathResource("test-config/application.properties");

        // 2. 获取资源的文件表示
        File file = classpathResource.getFile();

        // 3. 通过文件创建文件系统资源
        FileSystemResource fileSystemResource = new FileSystemResource(file);

        // 4. 验证两个资源的内容相同
        String classpathContent = ResourceUtils.readAsString(classpathResource);
        String fileSystemContent = ResourceUtils.readAsString(fileSystemResource);

        assertEquals(classpathContent, fileSystemContent, "两种资源的内容应该相同");
    }

    /**
     * 测试资源加载的错误处理
     */
    @Test
    @DisplayName("测试资源加载的错误处理")
    void testResourceLoadingErrorHandling() {
        // 1. 创建资源加载器
        ResourceLoader loader = new DefaultResourceLoader();

        // 2. 尝试加载不存在的资源
        Resource nonExistingResource = loader.getResource("classpath:non-existing-file.txt");

        // 3. 验证资源不存在
        assertFalse(nonExistingResource.exists(), "资源不应该存在");

        // 4. 验证读取时抛出异常
        assertThrows(IOException.class, () -> {
            ResourceUtils.readAsString(nonExistingResource);
        }, "读取不存在的资源应该抛出 IOException");
    }

    /**
     * 测试字节数组资源的特殊处理
     */
    @Test
    @DisplayName("测试字节数组资源的特殊处理")
    void testByteArrayResourceSpecialHandling() throws IOException {
        // 1. 创建字节数组资源
        String content = "Dynamic content generated at runtime";
        ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8), "dynamic content");

        // 2. 验证资源特性
        assertTrue(resource.exists(), "资源应该存在");
        assertTrue(resource.isReadable(), "资源应该可读");
        assertFalse(resource.isOpen(), "资源不应该被标记为已打开");

        // 3. 验证不支持的操作
        assertThrows(IOException.class, resource::getURL, "不应该支持 URL");
        assertThrows(IOException.class, resource::getFile, "不应该支持文件操作");

        // 4. 验证内容读取
        String readContent = ResourceUtils.readAsString(resource);
        assertEquals(content, readContent, "读取的内容应该与原始内容相同");
    }
}
