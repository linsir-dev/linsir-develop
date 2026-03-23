package com.linsir.spring.framework.spring_core.env.source;

import java.util.Map;

/**
 * 系统环境变量属性源
 *
 * 从系统环境变量中获取属性值，支持环境变量的命名风格转换。
 * 例如：spring.profiles.active 可以匹配 SPRING_PROFILES_ACTIVE
 *
 * @author linsir
 * @since 1.0.0
 */
public class SystemEnvironmentPropertySource extends MapPropertySource {

    /**
     * 默认的属性源名称
     */
    public static final String SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME = "systemEnvironment";

    /**
     * 创建一个新的 SystemEnvironmentPropertySource
     *
     * @param name 属性源名称
     * @param source 环境变量 Map
     */
    public SystemEnvironmentPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    /**
     * 使用默认名称创建 SystemEnvironmentPropertySource
     *
     * @param source 环境变量 Map
     */
    public SystemEnvironmentPropertySource(Map<String, Object> source) {
        this(SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, source);
    }

    @Override
    public Object getProperty(String name) {
        // 首先尝试精确匹配
        Object value = super.getProperty(name);
        if (value != null) {
            return value;
        }

        // 尝试将属性名转换为环境变量风格
        String envName = resolvePropertyName(name);
        return super.getProperty(envName);
    }

    @Override
    public boolean containsProperty(String name) {
        if (super.containsProperty(name)) {
            return true;
        }
        String envName = resolvePropertyName(name);
        return super.containsProperty(envName);
    }

    /**
     * 将属性名解析为环境变量名
     * 例如：spring.profiles.active -> SPRING_PROFILES_ACTIVE
     *
     * @param name 属性名
     * @return 环境变量名
     */
    protected String resolvePropertyName(String name) {
        StringBuilder sb = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (c == '.' || c == '-') {
                sb.append('_');
            } else {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }
}
