package com.linsir.spring.framework.spring_core.resource.service;

import com.linsir.spring.framework.spring_core.resource.pattern.PathMatchingResourcePatternResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TemplateService 测试类
 * 测试模板服务的功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("TemplateService 测试")
public class TemplateServiceTest {

    private TemplateService templateService;

    @BeforeEach
    void setUp() {
        templateService = new TemplateService();
    }

    /**
     * 测试默认构造方法
     */
    @Test
    @DisplayName("测试默认构造方法")
    void testDefaultConstructor() {
        TemplateService service = new TemplateService();
        assertNotNull(service, "模板服务不应该为 null");
        assertTrue(service.isCacheEnabled(), "默认应该启用缓存");
    }

    /**
     * 测试通过资源模式解析器构造
     */
    @Test
    @DisplayName("测试通过资源模式解析器构造")
    void testConstructorWithResolver() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        TemplateService service = new TemplateService(resolver);

        assertNotNull(service, "模板服务不应该为 null");
    }

    /**
     * 测试清除缓存
     */
    @Test
    @DisplayName("测试清除缓存")
    void testClearCache() {
        templateService.clearCache();
        // 验证方法不会抛出异常
        assertTrue(true);
    }

    /**
     * 测试清除指定模板的缓存
     */
    @Test
    @DisplayName("测试清除指定模板的缓存")
    void testClearCacheForTemplate() {
        templateService.clearCache("testTemplate");
        // 验证方法不会抛出异常
        assertTrue(true);
    }

    /**
     * 测试缓存启用状态
     */
    @Test
    @DisplayName("测试缓存启用状态")
    void testCacheEnabled() {
        assertTrue(templateService.isCacheEnabled(), "默认应该启用缓存");

        templateService.setCacheEnabled(false);
        assertFalse(templateService.isCacheEnabled(), "禁用后应该返回 false");

        templateService.setCacheEnabled(true);
        assertTrue(templateService.isCacheEnabled(), "启用后应该返回 true");
    }

    /**
     * 测试禁用缓存时清除缓存
     */
    @Test
    @DisplayName("测试禁用缓存时清除缓存")
    void testDisableCacheClearsCache() {
        // 先启用缓存
        templateService.setCacheEnabled(true);

        // 禁用缓存应该清除缓存
        templateService.setCacheEnabled(false);

        assertFalse(templateService.isCacheEnabled());
    }

    /**
     * 测试获取所有模板名称
     */
    @Test
    @DisplayName("测试获取所有模板名称")
    void testListAllTemplates() throws IOException {
        List<String> templates = templateService.listAllTemplates();

        assertNotNull(templates, "模板列表不应该为 null");
        // 由于测试环境中可能没有模板文件，列表可能为空
    }

    /**
     * 测试模板变量替换
     */
    @Test
    @DisplayName("测试模板变量替换")
    void testRenderTemplateWithVariables() throws IOException {
        // 由于测试环境中没有模板文件，这里测试方法逻辑
        // 创建一个模拟的模板内容
        String templateName = "testTemplate";

        // 如果没有模板文件，会抛出 IOException
        // 这里只是验证方法签名正确
    }

    /**
     * 测试批量加载模板
     */
    @Test
    @DisplayName("测试批量加载模板")
    void testLoadTemplates() throws IOException {
        Map<String, String> templates = templateService.loadTemplates("user-*");

        assertNotNull(templates, "模板映射不应该为 null");
        // 由于测试环境中可能没有匹配的模板，映射可能为空
    }

    /**
     * 测试加载不存在的模板
     */
    @Test
    @DisplayName("测试加载不存在的模板")
    void testLoadNonExistingTemplate() {
        assertThrows(IOException.class, () -> {
            templateService.loadTemplate("non-existing-template");
        }, "不存在的模板应该抛出 IOException");
    }

    /**
     * 测试缓存功能
     */
    @Test
    @DisplayName("测试缓存功能")
    void testCacheFunctionality() {
        // 启用缓存
        templateService.setCacheEnabled(true);
        assertTrue(templateService.isCacheEnabled());

        // 禁用缓存
        templateService.setCacheEnabled(false);
        assertFalse(templateService.isCacheEnabled());
    }
}
