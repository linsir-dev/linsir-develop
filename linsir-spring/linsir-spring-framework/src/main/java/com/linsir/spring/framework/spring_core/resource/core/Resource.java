package com.linsir.spring.framework.spring_core.resource.core;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * 资源抽象接口
 * 统一各类资源的访问方式，屏蔽底层资源类型的差异
 *
 * <p>核心能力：</p>
 * <ul>
 *   <li>资源存在性检查</li>
 *   <li>资源元数据获取（URL、URI、文件、大小、修改时间）</li>
 *   <li>相对资源创建</li>
 *   <li>资源描述信息</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public interface Resource extends InputStreamSource {

    /**
     * 判断资源是否存在
     *
     * @return true 如果资源存在，否则 false
     */
    boolean exists();

    /**
     * 判断资源是否可读
     * 默认实现：资源存在即可读
     *
     * @return true 如果资源可读，否则 false
     */
    default boolean isReadable() {
        return exists();
    }

    /**
     * 判断资源是否已打开
     * 用于检测资源是否已被消费（如 InputStream 已被读取）
     *
     * @return true 如果资源已打开，否则 false
     */
    default boolean isOpen() {
        return false;
    }

    /**
     * 判断资源是否为文件
     * 部分资源（如 URL 资源）可能不是文件
     *
     * @return true 如果资源是文件，否则 false
     */
    default boolean isFile() {
        return false;
    }

    /**
     * 获取资源的 URL
     *
     * @return 资源的 URL
     * @throws IOException 当无法获取 URL 时抛出
     */
    URL getURL() throws IOException;

    /**
     * 获取资源的 URI
     *
     * @return 资源的 URI
     * @throws IOException 当无法获取 URI 时抛出
     */
    URI getURI() throws IOException;

    /**
     * 获取资源对应的文件
     * 仅当资源是文件时才支持
     *
     * @return 资源对应的文件
     * @throws IOException 当资源不是文件或无法获取时抛出
     */
    File getFile() throws IOException;

    /**
     * 获取资源内容长度（字节数）
     *
     * @return 资源内容长度，如果无法确定则返回 -1
     * @throws IOException 当读取失败时抛出
     */
    long contentLength() throws IOException;

    /**
     * 获取资源最后修改时间
     *
     * @return 最后修改时间的时间戳（毫秒）
     * @throws IOException 当读取失败时抛出
     */
    long lastModified() throws IOException;

    /**
     * 创建相对资源
     * 基于当前资源路径创建相对路径的资源
     *
     * @param relativePath 相对路径
     * @return 相对路径对应的资源
     * @throws IOException 当创建失败时抛出
     */
    Resource createRelative(String relativePath) throws IOException;

    /**
     * 获取资源文件名
     *
     * @return 文件名，如果无法确定则返回 null
     */
    String getFilename();

    /**
     * 获取资源描述
     * 用于日志记录和调试
     *
     * @return 资源描述字符串
     */
    String getDescription();
}
