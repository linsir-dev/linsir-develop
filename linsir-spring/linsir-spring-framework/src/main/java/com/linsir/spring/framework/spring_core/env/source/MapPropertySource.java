package com.linsir.spring.framework.spring_core.env.source;

import java.util.Map;

/**
 * 基于 Map 的属性源实现
 *
 * 从 Map 对象中获取属性值，是最常用的属性源实现。
 * 支持从配置文件、内存配置等场景加载属性。
 *
 * @author linsir
 * @since 1.0.0
 */
public class MapPropertySource extends PropertySource<Map<String, Object>> {

    /**
     * 创建一个新的 MapPropertySource
     *
     * @param name 属性源名称
     * @param source 属性 Map
     */
    public MapPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }

    @Override
    public boolean containsProperty(String name) {
        return this.source.containsKey(name);
    }

    @Override
    public Object getProperty(String name) {
        return this.source.get(name);
    }

    /**
     * 获取所有属性名称
     *
     * @return 属性名称数组
     */
    public String[] getPropertyNames() {
        return this.source.keySet().toArray(new String[0]);
    }

    /**
     * 设置属性值
     *
     * @param name 属性名称
     * @param value 属性值
     */
    public void setProperty(String name, Object value) {
        this.source.put(name, value);
    }

    /**
     * 移除属性
     *
     * @param name 属性名称
     * @return 被移除的属性值
     */
    public Object removeProperty(String name) {
        return this.source.remove(name);
    }
}
