package com.linsir.spring.framework.spring_core.resource.service;

import com.linsir.spring.framework.spring_core.resource.core.Resource;
import com.linsir.spring.framework.spring_core.resource.loader.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * 配置加载器服务
 * 用于加载配置文件并解析为 Properties 对象
 *
 * <p>使用示例：</p>
 * <pre>
 * ConfigLoader loader = new ConfigLoader(resourceLoader);
 * Properties props = loader.loadApplicationConfig();
 * String value = props.getProperty("key");
 * </pre>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class ConfigLoader {

    /**
     * 默认配置文件路径
     */
    private static final String DEFAULT_CONFIG_PATH = "classpath:application.properties";

    /**
     * 资源加载器
     */
    private final ResourceLoader resourceLoader;

    /**
     * 配置文件路径
     */
    private String configPath;

    /**
     * 默认构造方法
     * 使用默认资源加载器
     */
    public ConfigLoader() {
        this.resourceLoader = new com.linsir.spring.framework.spring_core.resource.loader.DefaultResourceLoader();
        this.configPath = DEFAULT_CONFIG_PATH;
    }

    /**
     * 通过资源加载器构造
     *
     * @param resourceLoader 资源加载器
     */
    public ConfigLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader != null ? resourceLoader : 
            new com.linsir.spring.framework.spring_core.resource.loader.DefaultResourceLoader();
        this.configPath = DEFAULT_CONFIG_PATH;
    }

    /**
     * 通过资源加载器和配置文件路径构造
     *
     * @param resourceLoader 资源加载器
     * @param configPath 配置文件路径
     */
    public ConfigLoader(ResourceLoader resourceLoader, String configPath) {
        this.resourceLoader = resourceLoader != null ? resourceLoader : 
            new com.linsir.spring.framework.spring_core.resource.loader.DefaultResourceLoader();
        this.configPath = configPath != null ? configPath : DEFAULT_CONFIG_PATH;
    }

    /**
     * 加载默认配置文件
     *
     * @return Properties 对象
     * @throws IOException 当加载失败时抛出
     */
    public Properties loadApplicationConfig() throws IOException {
        return loadConfig(configPath);
    }

    /**
     * 加载指定路径的配置文件
     *
     * @param path 配置文件路径
     * @return Properties 对象
     * @throws IOException 当加载失败时抛出
     */
    public Properties loadConfig(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);

        if (!resource.exists()) {
            throw new IOException("配置文件不存在: " + path);
        }

        Properties props = new Properties();

        try (InputStream is = resource.getInputStream()) {
            // 根据文件扩展名选择加载方式
            String filename = resource.getFilename();
            if (filename != null && filename.endsWith(".xml")) {
                props.loadFromXML(is);
            } else {
                props.load(is);
            }
        }

        return props;
    }

    /**
     * 加载配置文件内容为字符串
     *
     * @param path 配置文件路径
     * @return 文件内容字符串
     * @throws IOException 当加载失败时抛出
     */
    public String loadConfigAsString(String path) throws IOException {
        Resource resource = resourceLoader.getResource(path);

        if (!resource.exists()) {
            throw new IOException("配置文件不存在: " + path);
        }

        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /**
     * 加载 YAML 配置文件（简化实现）
     *
     * @param path YAML 文件路径
     * @return Properties 对象
     * @throws IOException 当加载失败时抛出
     */
    public Properties loadYamlConfig(String path) throws IOException {
        String content = loadConfigAsString(path);
        Properties props = new Properties();

        // 简化的 YAML 解析：只处理 key: value 格式
        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            int colonIndex = line.indexOf(':');
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                props.setProperty(key, value);
            }
        }

        return props;
    }

    /**
     * 获取配置文件路径
     *
     * @return 配置文件路径
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * 设置配置文件路径
     *
     * @param configPath 配置文件路径
     */
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    /**
     * 获取资源加载器
     *
     * @return 资源加载器
     */
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
