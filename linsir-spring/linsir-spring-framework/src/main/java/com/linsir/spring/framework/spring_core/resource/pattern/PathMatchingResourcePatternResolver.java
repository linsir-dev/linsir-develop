package com.linsir.spring.framework.spring_core.resource.pattern;

import com.linsir.spring.framework.spring_core.resource.core.ClassPathResource;
import com.linsir.spring.framework.spring_core.resource.core.Resource;
import com.linsir.spring.framework.spring_core.resource.loader.DefaultResourceLoader;
import com.linsir.spring.framework.spring_core.resource.loader.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 路径匹配资源模式解析器
 * 支持 Ant 风格的路径模式匹配
 *
 * <p>支持的通配符:</p>
 * <ul>
 *   <li>? - 匹配单个字符</li>
 *   <li>* - 匹配零个或多个字符</li>
 *   <li>** - 匹配任意层级的目录</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver {

    /**
     * 资源加载器
     */
    private final ResourceLoader resourceLoader;

    /**
     * 路径分隔符
     */
    private static final String PATH_SEPARATOR = "/";

    /**
     * 默认构造方法
     */
    public PathMatchingResourcePatternResolver() {
        this.resourceLoader = new DefaultResourceLoader();
    }

    /**
     * 通过资源加载器构造
     *
     * @param resourceLoader 资源加载器
     */
    public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
    }

    @Override
    public Resource getResource(String location) {
        return resourceLoader.getResource(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return resourceLoader.getClassLoader();
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        if (locationPattern == null || locationPattern.isEmpty()) {
            return new Resource[0];
        }

        // 处理 classpath*: 前缀(搜索所有类路径)
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            String pattern = locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length());
            return findClassPathResources(pattern);
        }

        // 处理普通路径模式
        int prefixEnd = locationPattern.indexOf(":") + 1;
        String pattern = locationPattern.substring(prefixEnd);

        // 检查是否包含通配符
        if (containsPattern(pattern)) {
            // 包含通配符,需要解析
            return findPathMatchingResources(locationPattern);
        } else {
            // 不包含通配符,直接加载单个资源
            Resource resource = getResource(locationPattern);
            return resource.exists() ? new Resource[]{resource} : new Resource[0];
        }
    }

    /**
     * 查找类路径资源
     *
     * @param pattern 路径模式
     * @return 资源数组
     * @throws IOException 当查找失败时抛出
     */
    private Resource[] findClassPathResources(String pattern) throws IOException {
        List<Resource> resources = new ArrayList<>();
        ClassLoader classLoader = getClassLoader();

        // 处理 /**/ 开头的模式
        if (pattern.startsWith("**/")) {
            pattern = pattern.substring(3);
        }

        // 获取根路径
        int rootDirEnd = pattern.indexOf("*");
        String rootDirPath = rootDirEnd != -1 ? pattern.substring(0, rootDirEnd) : pattern;

        // 去掉末尾的 /
        if (rootDirPath.endsWith("/")) {
            rootDirPath = rootDirPath.substring(0, rootDirPath.length() - 1);
        }

        // 获取所有匹配的 URL
        Enumeration<URL> urls = classLoader.getResources(rootDirPath);

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String urlPath = url.getPath();

            // 如果是文件系统路径
            if ("file".equals(url.getProtocol())) {
                File rootDir = new File(urlPath);
                if (rootDir.exists() && rootDir.isDirectory()) {
                    findResourcesInDirectory(rootDir, pattern, resources);
                }
            }
        }

        return resources.toArray(new Resource[0]);
    }

    /**
     * 在目录中查找匹配的资源
     *
     * @param rootDir 根目录
     * @param pattern 路径模式
     * @param resources 资源列表
     */
    private void findResourcesInDirectory(File rootDir, String pattern, List<Resource> resources) {
        // 简化处理:递归查找所有文件
        findResourcesRecursively(rootDir, rootDir, pattern, resources);
    }

    /**
     * 递归查找资源
     *
     * @param rootDir 根目录
     * @param currentDir 当前目录
     * @param pattern 路径模式
     * @param resources 资源列表
     */
    private void findResourcesRecursively(File rootDir, File currentDir, String pattern, List<Resource> resources) {
        File[] files = currentDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findResourcesRecursively(rootDir, file, pattern, resources);
            } else {
                // 获取相对路径
                String relativePath = getRelativePath(rootDir, file);

                // 检查是否匹配模式
                if (matchPattern(relativePath, pattern)) {
                    resources.add(new com.linsir.spring.framework.spring_core.resource.core.FileSystemResource(file));
                }
            }
        }
    }

    /**
     * 获取相对路径
     *
     * @param rootDir 根目录
     * @param file 文件
     * @return 相对路径
     */
    private String getRelativePath(File rootDir, File file) {
        String rootPath = rootDir.getAbsolutePath();
        String filePath = file.getAbsolutePath();

        if (filePath.startsWith(rootPath)) {
            String relative = filePath.substring(rootPath.length());
            return relative.replace("\\", "/");
        }
        return filePath;
    }

    /**
     * 查找路径匹配的资源
     *
     * @param locationPattern 位置模式
     * @return 资源数组
     * @throws IOException 当查找失败时抛出
     */
    private Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        // 简化实现:对于非 classpath*: 模式,尝试直接加载
        Resource resource = getResource(locationPattern);
        return resource.exists() ? new Resource[]{resource} : new Resource[0];
    }

    /**
     * 检查路径是否包含模式通配符
     *
     * @param path 路径
     * @return true 如果包含通配符
     */
    private boolean containsPattern(String path) {
        return path.indexOf('*') != -1 || path.indexOf('?') != -1;
    }

    /**
     * 匹配路径模式
     * 简化的 Ant 风格模式匹配
     *
     * @param path 路径
     * @param pattern 模式
     * @return true 如果匹配
     */
    private boolean matchPattern(String path, String pattern) {
        // 处理 ** 通配符
        if (pattern.contains("**")) {
            String[] patternParts = pattern.split("\\*\\*");
            if (patternParts.length == 2) {
                String prefix = patternParts[0];
                String suffix = patternParts[1];

                // 去掉开头的 /
                if (prefix.startsWith("/")) {
                    prefix = prefix.substring(1);
                }
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }

                return path.startsWith(prefix) && path.endsWith(suffix);
            }
        }

        // 处理 * 通配符
        if (pattern.contains("*")) {
            String regex = pattern.replace(".", "\\.").replace("*", ".*").replace("?", ".");
            return path.matches(regex);
        }

        // 精确匹配
        return path.equals(pattern);
    }
}
