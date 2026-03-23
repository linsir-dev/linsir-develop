package com.linsir.spring.framework.spring_core.env.core;

import com.linsir.spring.framework.spring_core.env.resolver.PropertyResolver;

/**
 * 环境接口
 *
 * 表示应用程序运行的环境，整合属性解析和 Profile 管理。
 * 是 Spring 环境抽象的核心接口。
 *
 * @author linsir
 * @since 1.0.0
 */
public interface Environment extends PropertyResolver {

    /**
     * 获取默认的 Profile 名称
     */
    String[] DEFAULT_PROFILES = {"default"};

    /**
     * 获取激活的 Profile 数组
     *
     * @return 激活的 Profile 数组
     */
    String[] getActiveProfiles();

    /**
     * 获取默认的 Profile 数组
     *
     * @return 默认的 Profile 数组
     */
    String[] getDefaultProfiles();

    /**
     * 判断指定的 Profile 是否激活
     *
     * @param profile Profile 名称
     * @return 如果激活则返回 true
     */
    boolean acceptsProfiles(String profile);

    /**
     * 判断是否至少有一个指定的 Profile 激活
     *
     * @param profiles Profile 名称数组
     * @return 如果至少有一个激活则返回 true
     */
    boolean acceptsProfiles(String... profiles);
}
