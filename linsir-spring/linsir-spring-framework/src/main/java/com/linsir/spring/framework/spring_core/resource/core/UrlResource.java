package com.linsir.spring.framework.spring_core.resource.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * URL 资源实现
 * 用于加载 URL 资源（HTTP、HTTPS、FTP 等）
 *
 * <p>支持两种构造方式：</p>
 * <ul>
 *   <li>通过 URL 字符串创建：new UrlResource("https://example.com/config.json")</li>
 *   <li>通过 URL 对象创建：new UrlResource(new URL("https://example.com/config.json"))</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class UrlResource implements Resource {

    /**
     * URL 对象
     */
    private final URL url;

    /**
     * 原始路径
     */
    private final String path;

    /**
     * 通过 URL 字符串创建 URL 资源
     *
     * @param path URL 字符串
     * @throws MalformedURLException 当 URL 格式不正确时抛出
     */
    public UrlResource(String path) throws MalformedURLException {
        this.path = path;
        this.url = new URL(path);
    }

    /**
     * 通过 URL 对象创建 URL 资源
     *
     * @param url URL 对象
     */
    public UrlResource(URL url) {
        this.url = url;
        this.path = url.toString();
    }

    /**
     * 通过 URI 创建 URL 资源
     *
     * @param uri URI 对象
     * @throws MalformedURLException 当无法转换为 URL 时抛出
     */
    public UrlResource(URI uri) throws MalformedURLException {
        this.path = uri.toString();
        this.url = uri.toURL();
    }

    @Override
    public boolean exists() {
        try {
            URLConnection connection = url.openConnection();
            if (connection instanceof HttpURLConnection) {
                HttpURLConnection httpConn = (HttpURLConnection) connection;
                httpConn.setRequestMethod("HEAD");
                int responseCode = httpConn.getResponseCode();
                httpConn.disconnect();
                return responseCode == HttpURLConnection.HTTP_OK;
            } else {
                // 非 HTTP 连接，尝试获取输入流
                connection.getInputStream().close();
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isReadable() {
        return exists();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        return connection.getInputStream();
    }

    @Override
    public URL getURL() throws IOException {
        return url;
    }

    @Override
    public URI getURI() throws IOException {
        try {
            return url.toURI();
        } catch (Exception e) {
            throw new IOException("无法将 URL 转换为 URI: " + url, e);
        }
    }

    @Override
    public File getFile() throws IOException {
        if (!"file".equals(url.getProtocol())) {
            throw new IOException("URL 不是文件协议: " + url);
        }
        try {
            return new File(url.toURI());
        } catch (Exception e) {
            return new File(url.getFile());
        }
    }

    @Override
    public long contentLength() throws IOException {
        URLConnection connection = url.openConnection();
        long length = connection.getContentLengthLong();
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).disconnect();
        }
        return length;
    }

    @Override
    public long lastModified() throws IOException {
        URLConnection connection = url.openConnection();
        long lastModified = connection.getLastModified();
        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).disconnect();
        }
        return lastModified;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        // 处理相对路径
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        URL relativeUrl = new URL(url, relativePath);
        return new UrlResource(relativeUrl);
    }

    @Override
    public String getFilename() {
        String urlFile = url.getFile();
        if (urlFile == null || urlFile.isEmpty()) {
            return null;
        }
        int lastSlashIndex = urlFile.lastIndexOf('/');
        return lastSlashIndex != -1 ? urlFile.substring(lastSlashIndex + 1) : urlFile;
    }

    @Override
    public String getDescription() {
        return "URL [" + url + "]";
    }

    /**
     * 获取 URL 对象
     *
     * @return URL 对象
     */
    public URL getUrl() {
        return url;
    }

    /**
     * 获取原始路径
     *
     * @return 原始路径
     */
    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UrlResource other = (UrlResource) obj;
        return url.equals(other.url);
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
