package com.linsir.spring.framework.spring_core.env.source;

import java.util.Properties;

/**
 * 基于 Properties 的属性源实现
 *
 * 从 Properties 对象中获取属性值，兼容传统的 Java 属性配置。
 *
 * @author linsir
 * @since 1.0.0
 */
public class PropertiesPropertySource extends PropertySource<Properties> {

    /**
     * 创建一个新的 PropertiesPropertySource
     *
     * @param name 属性源名称
     * @param source Properties 对象
     */
    public PropertiesPropertySource(String name, Properties source) {
        super(name, source);
    }

    @Override
    public boolean containsProperty(String name) {
        return this.source.containsKey(name);
    }

    @Override
    public Object getProperty(String name) {
        return this.source.getProperty(name);
    }

    /**
     * 获取所有属性名称
     *
     * @return 属性名称数组
     */
    public String[] getPropertyNames() {
        return this.source.stringPropertyNames().toArray(new String[0]);
    }

    /**
     * 设置属性值
     *
     * @param name 属性名称
     * @param value 属性值
     */
    public void setProperty(String name, String value) {
        this.source.setProperty(name, value);
    }
}
