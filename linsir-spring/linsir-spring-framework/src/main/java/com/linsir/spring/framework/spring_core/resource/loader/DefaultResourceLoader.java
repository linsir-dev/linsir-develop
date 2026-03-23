package com.linsir.spring.framework.spring_core.resource.loader;

import com.linsir.spring.framework.spring_core.resource.core.ClassPathResource;
import com.linsir.spring.framework.spring_core.resource.core.Resource;
import com.linsir.spring.framework.spring_core.resource.core.UrlResource;

import java.net.MalformedURLException;

/**
 * 默认资源加载器实现
 * 支持多种资源类型的自动识别和加载
 *
 * <p>加载策略：</p>
 * <ol>
 *   <li>以 classpath: 开头 → 使用 ClassPathResource</li>
 *   <li>以 / 开头 → 使用 ClassPathResource（绝对路径）</li>
 *   <li>以 file: 开头 → 使用 UrlResource</li>
 *   <li>以 http: / https: / ftp: 开头 → 使用 UrlResource</li>
 *   <li>其他 → 尝试作为 ClassPathResource 加载</li>
 * </ol>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class DefaultResourceLoader implements ResourceLoader {

    /**
     * 类加载器
     */
    private ClassLoader classLoader;

    /**
     * 默认构造方法
     * 使用当前线程的上下文类加载器
     */
    public DefaultResourceLoader() {
        this.classLoader = getDefaultClassLoader();
    }

    /**
     * 通过类加载器构造
     *
     * @param classLoader 类加载器
     */
    public DefaultResourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : getDefaultClassLoader();
    }

    @Override
    public Resource getResource(String location) {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("资源位置不能为空");
        }

        // 1. 处理 classpath: 前缀
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            String path = location.substring(CLASSPATH_URL_PREFIX.length());
            return new ClassPathResource(path, classLoader);
        }

        // 2. 处理 / 开头的绝对路径（类路径）
        if (location.startsWith("/")) {
            return new ClassPathResource(location, classLoader);
        }

        // 3. 处理 file:、http:、https:、ftp: 等 URL 前缀
        int separatorIndex = location.indexOf(URL_PROTOCOL_SEPARATOR);
        if (separatorIndex != -1) {
            String protocol = location.substring(0, separatorIndex);
            if (isValidProtocol(protocol)) {
                try {
                    return new UrlResource(location);
                } catch (MalformedURLException e) {
                    // URL 格式不正确，继续尝试其他方式
                }
            }
        }

        // 4. 默认作为类路径资源
        return new ClassPathResource(location, classLoader);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * 设置类加载器
     *
     * @param classLoader 类加载器
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader != null ? classLoader : getDefaultClassLoader();
    }

    /**
     * 获取默认类加载器
     *
     * @return 类加载器
     */
    private ClassLoader getDefaultClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = DefaultResourceLoader.class.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
        }
        return cl;
    }

    /**
     * 判断是否为有效的协议
     *
     * @param protocol 协议名称
     * @return true 如果协议有效
     */
    private boolean isValidProtocol(String protocol) {
        return "file".equals(protocol) ||
               "http".equals(protocol) ||
               "https".equals(protocol) ||
               "ftp".equals(protocol) ||
               "jar".equals(protocol);
    }
}
