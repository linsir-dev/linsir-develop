package com.linsir.spring.framework.spring_core.resource.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * 文件系统资源实现
 * 用于加载文件系统中的资源文件
 *
 * <p>支持两种构造方式：</p>
 * <ul>
 *   <li>通过文件路径创建：new FileSystemResource("/path/to/file.txt")</li>
 *   <li>通过 File 对象创建：new FileSystemResource(new File("/path/to/file.txt"))</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class FileSystemResource implements Resource {

    /**
     * 文件对象
     */
    private final File file;

    /**
     * 文件路径
     */
    private final String path;

    /**
     * 通过文件路径创建文件系统资源
     *
     * @param path 文件路径
     */
    public FileSystemResource(String path) {
        this.file = new File(path);
        this.path = path;
    }

    /**
     * 通过 File 对象创建文件系统资源
     *
     * @param file 文件对象
     */
    public FileSystemResource(File file) {
        this.file = file;
        this.path = file.getPath();
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public boolean isReadable() {
        return file.canRead();
    }

    @Override
    public boolean isFile() {
        return file.isFile();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + path);
        }
        if (!file.canRead()) {
            throw new IOException("文件不可读: " + path);
        }
        return new FileInputStream(file);
    }

    @Override
    public URL getURL() throws IOException {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new IOException("无法获取文件 URL: " + path, e);
        }
    }

    @Override
    public URI getURI() throws IOException {
        return file.toURI();
    }

    @Override
    public File getFile() throws IOException {
        return file;
    }

    @Override
    public long contentLength() throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + path);
        }
        return file.length();
    }

    @Override
    public long lastModified() throws IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("文件不存在: " + path);
        }
        return file.lastModified();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        // 获取父目录
        String parentPath = file.getParent();
        if (parentPath == null) {
            parentPath = ".";
        }
        // 创建相对路径文件
        File relativeFile = new File(parentPath, relativePath);
        return new FileSystemResource(relativeFile);
    }

    @Override
    public String getFilename() {
        return file.getName();
    }

    @Override
    public String getDescription() {
        return "file [" + file.getAbsolutePath() + "]";
    }

    /**
     * 获取文件路径
     *
     * @return 文件路径
     */
    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FileSystemResource other = (FileSystemResource) obj;
        return file.equals(other.file);
    }

    @Override
    public int hashCode() {
        return file.hashCode();
    }
}
