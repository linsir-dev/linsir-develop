package com.linsir.spring.framework.spring_core.resource.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.NoSuchFileException;

/**
 * 类路径资源实现
 * 用于加载类路径（classpath）下的资源文件
 *
 * <p>支持三种构造方式：</p>
 * <ul>
 *   <li>通过类加载器加载：new ClassPathResource("config.properties")</li>
 *   <li>通过指定类加载器加载：new ClassPathResource("config.xml", classLoader)</li>
 *   <li>相对于指定类的包路径加载：new ClassPathResource("config.xml", MyClass.class)</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class ClassPathResource implements Resource {

    /**
     * 资源路径
     */
    private final String path;

    /**
     * 类加载器
     */
    private final ClassLoader classLoader;

    /**
     * 用于相对路径解析的类
     */
    private final Class<?> clazz;

    /**
     * 通过路径创建类路径资源
     * 使用当前线程的上下文类加载器
     *
     * @param path 资源路径
     */
    public ClassPathResource(String path) {
        this(path, null, null);
    }

    /**
     * 通过路径和类加载器创建类路径资源
     *
     * @param path 资源路径
     * @param classLoader 类加载器
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        this(path, classLoader, null);
    }

    /**
     * 通过路径和类创建类路径资源
     * 路径相对于类的包路径
     *
     * @param path 资源路径
     * @param clazz 用于相对路径解析的类
     */
    public ClassPathResource(String path, Class<?> clazz) {
        this(path, null, clazz);
    }

    /**
     * 完整构造方法
     *
     * @param path 资源路径
     * @param classLoader 类加载器
     * @param clazz 用于相对路径解析的类
     */
    public ClassPathResource(String path, ClassLoader classLoader, Class<?> clazz) {
        // 处理路径，去除开头的 /
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = path;
        this.classLoader = classLoader != null ? classLoader : getDefaultClassLoader();
        this.clazz = clazz;
    }

    /**
     * 获取默认类加载器
     * 优先使用当前线程的上下文类加载器
     *
     * @return 类加载器
     */
    private ClassLoader getDefaultClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassPathResource.class.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
        }
        return cl;
    }

    @Override
    public boolean exists() {
        return resolveURL() != null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is;
        if (clazz != null) {
            // 使用类的 getResourceAsStream，支持相对路径
            is = clazz.getResourceAsStream(path);
        } else {
            // 使用类加载器的 getResourceAsStream
            is = classLoader.getResourceAsStream(path);
        }
        if (is == null) {
            throw new FileNotFoundException("类路径资源不存在: " + path);
        }
        return is;
    }

    @Override
    public URL getURL() throws IOException {
        URL url = resolveURL();
        if (url == null) {
            throw new FileNotFoundException("类路径资源不存在: " + path);
        }
        return url;
    }

    @Override
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return url.toURI();
        } catch (Exception e) {
            throw new IOException("无法将 URL 转换为 URI: " + url, e);
        }
    }

    @Override
    public File getFile() throws IOException {
        URL url = getURL();
        if (!"file".equals(url.getProtocol())) {
            throw new FileNotFoundException("类路径资源不是文件: " + path);
        }
        try {
            return new File(url.toURI());
        } catch (Exception e) {
            return new File(url.getFile());
        }
    }

    @Override
    public long contentLength() throws IOException {
        InputStream is = getInputStream();
        try {
            long size = 0;
            byte[] buffer = new byte[256];
            int read;
            while ((read = is.read(buffer)) != -1) {
                size += read;
            }
            return size;
        } finally {
            is.close();
        }
    }

    @Override
    public long lastModified() throws IOException {
        File file = getFile();
        if (file.exists()) {
            return file.lastModified();
        }
        return -1;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        // 处理相对路径
        String pathToUse = this.path;
        int lastSlashIndex = pathToUse.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            String newPath = pathToUse.substring(0, lastSlashIndex + 1) + relativePath;
            return new ClassPathResource(newPath, classLoader, clazz);
        }
        return new ClassPathResource(relativePath, classLoader, clazz);
    }

    @Override
    public String getFilename() {
        int lastSlashIndex = path.lastIndexOf('/');
        return lastSlashIndex != -1 ? path.substring(lastSlashIndex + 1) : path;
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder("class path resource [");
        String actualPath = path;
        if (clazz != null) {
            actualPath = clazz.getPackage().getName().replace('.', '/') + "/" + path;
        }
        builder.append(actualPath);
        builder.append("]");
        return builder.toString();
    }

    /**
     * 解析 URL
     *
     * @return URL 对象，如果不存在则返回 null
     */
    private URL resolveURL() {
        if (clazz != null) {
            return clazz.getResource(path);
        } else {
            return classLoader.getResource(path);
        }
    }

    /**
     * 获取资源路径
     *
     * @return 资源路径
     */
    public String getPath() {
        return path;
    }

    /**
     * 获取类加载器
     *
     * @return 类加载器
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ClassPathResource other = (ClassPathResource) obj;
        return path.equals(other.path) &&
               classLoader.equals(other.classLoader);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
