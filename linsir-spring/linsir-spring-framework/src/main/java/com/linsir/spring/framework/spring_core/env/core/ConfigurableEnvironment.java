package com.linsir.spring.framework.spring_core.env.core;

import com.linsir.spring.framework.spring_core.env.source.PropertySource;
import com.linsir.spring.framework.spring_core.env.support.MutablePropertySources;

/**
 * 可配置的环境接口
 *
 * 扩展 Environment 接口，提供配置环境的能力。
 * 允许设置激活的 Profile、添加属性源等。
 *
 * @author linsir
 * @since 1.0.0
 */
public interface ConfigurableEnvironment extends Environment {

    /**
     * 设置激活的 Profile
     *
     * @param profiles Profile 名称数组
     */
    void setActiveProfiles(String... profiles);

    /**
     * 添加激活的 Profile
     *
     * @param profile Profile 名称
     */
    void addActiveProfile(String profile);

    /**
     * 设置默认的 Profile
     *
     * @param profiles Profile 名称数组
     */
    void setDefaultProfiles(String... profiles);

    /**
     * 获取属性源集合
     *
     * @return 可变的属性源集合
     */
    MutablePropertySources getPropertySources();

    /**
     * 添加属性源
     *
     * @param propertySource 属性源
     */
    default void addPropertySource(PropertySource<?> propertySource) {
        getPropertySources().addLast(propertySource);
    }

    /**
     * 合并另一个环境
     *
     * @param environment 要合并的环境
     */
    void merge(ConfigurableEnvironment environment);
}
