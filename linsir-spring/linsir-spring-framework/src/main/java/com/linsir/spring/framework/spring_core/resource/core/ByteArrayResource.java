package com.linsir.spring.framework.spring_core.resource.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

/**
 * 字节数组资源实现
 * 用于将内存中的字节数组作为资源使用
 *
 * <p>典型使用场景：</p>
 * <ul>
 *   <li>动态生成的内容作为资源</li>
 *   <li>缓存的资源数据</li>
 *   <li>测试时模拟资源</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class ByteArrayResource implements Resource {

    /**
     * 字节数组数据
     */
    private final byte[] byteArray;

    /**
     * 资源描述
     */
    private final String description;

    /**
     * 通过字节数组创建资源
     *
     * @param byteArray 字节数组
     */
    public ByteArrayResource(byte[] byteArray) {
        this(byteArray, "byte array");
    }

    /**
     * 通过字节数组和描述创建资源
     *
     * @param byteArray 字节数组
     * @param description 资源描述
     */
    public ByteArrayResource(byte[] byteArray, String description) {
        this.byteArray = byteArray != null ? byteArray : new byte[0];
        this.description = description != null ? description : "";
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(byteArray);
    }

    @Override
    public URL getURL() throws IOException {
        throw new IOException("字节数组资源不支持 URL");
    }

    @Override
    public URI getURI() throws IOException {
        throw new IOException("字节数组资源不支持 URI");
    }

    @Override
    public File getFile() throws IOException {
        throw new IOException("字节数组资源不支持文件操作");
    }

    @Override
    public long contentLength() throws IOException {
        return byteArray.length;
    }

    @Override
    public long lastModified() throws IOException {
        return -1;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        throw new IOException("字节数组资源不支持相对路径");
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public String getDescription() {
        return "byte array resource [" + description + "]";
    }

    /**
     * 获取字节数组
     *
     * @return 字节数组的副本
     */
    public byte[] getByteArray() {
        return Arrays.copyOf(byteArray, byteArray.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ByteArrayResource other = (ByteArrayResource) obj;
        return Arrays.equals(byteArray, other.byteArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(byteArray);
    }
}
