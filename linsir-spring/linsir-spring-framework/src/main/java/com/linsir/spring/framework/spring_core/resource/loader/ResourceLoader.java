package com.linsir.spring.framework.spring_core.resource.loader;

import com.linsir.spring.framework.spring_core.resource.core.Resource;

/**
 * 资源加载器接口
 * 定义根据位置字符串加载资源的统一方式
 *
 * <p>支持的前缀：</p>
 * <ul>
 *   <li>classpath: - 类路径资源</li>
 *   <li>file: - 文件系统资源</li>
 *   <li>http: / https: - URL 资源</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public interface ResourceLoader {

    /**
     * 类路径 URL 前缀
     */
    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 文件系统 URL 前缀
     */
    String FILE_URL_PREFIX = "file:";

    /**
     * URL 协议前缀分隔符
     */
    String URL_PROTOCOL_SEPARATOR = ":";

    /**
     * 根据位置加载资源
     *
     * @param location 资源位置（支持 classpath:、file:、http: 等前缀）
     * @return Resource 对象，不会返回 null
     */
    Resource getResource(String location);

    /**
     * 获取类加载器
     * 用于加载类路径资源
     *
     * @return 类加载器，可能为 null
     */
    ClassLoader getClassLoader();
}
