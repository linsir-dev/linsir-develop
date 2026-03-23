package com.linsir.spring.framework.spring_core.resource.service;

import com.linsir.spring.framework.spring_core.resource.core.Resource;
import com.linsir.spring.framework.spring_core.resource.pattern.PathMatchingResourcePatternResolver;
import com.linsir.spring.framework.spring_core.resource.pattern.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板服务
 * 用于加载和管理模板文件
 *
 * <p>核心功能：</p>
 * <ul>
 *   <li>加载单个模板文件</li>
 *   <li>批量加载模板文件</li>
 *   <li>模板缓存管理</li>
 *   <li>简单的模板变量替换</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class TemplateService {

    /**
     * 模板目录前缀
     */
    private static final String TEMPLATE_PREFIX = "classpath:templates/";

    /**
     * 模板文件扩展名
     */
    private static final String TEMPLATE_SUFFIX = ".html";

    /**
     * 资源模式解析器
     */
    private final ResourcePatternResolver resolver;

    /**
     * 模板缓存
     */
    private final Map<String, String> templateCache;

    /**
     * 是否启用缓存
     */
    private boolean cacheEnabled = true;

    /**
     * 默认构造方法
     */
    public TemplateService() {
        this.resolver = new PathMatchingResourcePatternResolver();
        this.templateCache = new HashMap<>();
    }

    /**
     * 通过资源模式解析器构造
     *
     * @param resolver 资源模式解析器
     */
    public TemplateService(ResourcePatternResolver resolver) {
        this.resolver = resolver != null ? resolver : new PathMatchingResourcePatternResolver();
        this.templateCache = new HashMap<>();
    }

    /**
     * 加载模板
     *
     * @param templateName 模板名称（不含路径和扩展名）
     * @return 模板内容
     * @throws IOException 当加载失败时抛出
     */
    public String loadTemplate(String templateName) throws IOException {
        // 检查缓存
        if (cacheEnabled && templateCache.containsKey(templateName)) {
            return templateCache.get(templateName);
        }

        // 构建模板路径
        String templatePath = TEMPLATE_PREFIX + templateName + TEMPLATE_SUFFIX;
        Resource resource = resolver.getResource(templatePath);

        if (!resource.exists()) {
            throw new IOException("模板不存在: " + templateName);
        }

        String content = readResourceContent(resource);

        // 存入缓存
        if (cacheEnabled) {
            templateCache.put(templateName, content);
        }

        return content;
    }

    /**
     * 加载模板并进行变量替换
     *
     * @param templateName 模板名称
     * @param variables 变量映射
     * @return 替换后的模板内容
     * @throws IOException 当加载失败时抛出
     */
    public String renderTemplate(String templateName, Map<String, String> variables) throws IOException {
        String template = loadTemplate(templateName);

        // 简单的变量替换：${variableName}
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String placeholder = "${" + entry.getKey() + "}";
                template = template.replace(placeholder, entry.getValue());
            }
        }

        return template;
    }

    /**
     * 获取所有模板名称
     *
     * @return 模板名称列表
     * @throws IOException 当加载失败时抛出
     */
    public List<String> listAllTemplates() throws IOException {
        Resource[] resources = resolver.getResources(TEMPLATE_PREFIX + "*" + TEMPLATE_SUFFIX);

        List<String> templateNames = new ArrayList<>();
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename != null) {
                // 去掉扩展名
                String name = filename.substring(0, filename.lastIndexOf('.'));
                templateNames.add(name);
            }
        }

        return templateNames;
    }

    /**
     * 批量加载模板
     *
     * @param pattern 模板匹配模式（如 "user-*"）
     * @return 模板名称到内容的映射
     * @throws IOException 当加载失败时抛出
     */
    public Map<String, String> loadTemplates(String pattern) throws IOException {
        String pathPattern = TEMPLATE_PREFIX + pattern + TEMPLATE_SUFFIX;
        Resource[] resources = resolver.getResources(pathPattern);

        Map<String, String> templates = new HashMap<>();
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename != null) {
                String name = filename.substring(0, filename.lastIndexOf('.'));
                String content = readResourceContent(resource);
                templates.put(name, content);

                // 存入缓存
                if (cacheEnabled) {
                    templateCache.put(name, content);
                }
            }
        }

        return templates;
    }

    /**
     * 清除模板缓存
     */
    public void clearCache() {
        templateCache.clear();
    }

    /**
     * 清除指定模板的缓存
     *
     * @param templateName 模板名称
     */
    public void clearCache(String templateName) {
        templateCache.remove(templateName);
    }

    /**
     * 是否启用缓存
     *
     * @return true 如果启用缓存
     */
    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    /**
     * 设置是否启用缓存
     *
     * @param cacheEnabled 是否启用缓存
     */
    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
        if (!cacheEnabled) {
            clearCache();
        }
    }

    /**
     * 读取资源内容
     *
     * @param resource 资源对象
     * @return 内容字符串
     * @throws IOException 当读取失败时抛出
     */
    private String readResourceContent(Resource resource) throws IOException {
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
