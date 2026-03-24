package com.linsir.spring.framework.spring_core.bytecode.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * 类加载器工具类
 *
 * <p>提供了类加载相关的工具方法，包括：
 * <ul>
 *   <li>获取默认类加载器</li>
 *   <li>加载类资源</li>
 *   <li>获取类字节码</li>
 *   <li>类加载器层次结构操作</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see BytecodeClassLoader
 * @see ClassLoadingStrategy
 */
public class ClassLoaderUtils {

    /**
     * 默认缓冲区大小
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * 获取默认类加载器
     *
     * <p>按以下优先级获取：
     * <ol>
     *   <li>线程上下文类加载器</li>
     *   <li>当前类的类加载器</li>
     *   <li>系统类加载器</li>
     * </ol>
     *
     * @return 类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 无法获取线程上下文类加载器
        }

        if (cl == null) {
            cl = ClassLoaderUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // 无法获取系统类加载器
                }
            }
        }

        return cl;
    }

    /**
     * 获取类加载器的层次结构
     *
     * @param classLoader 类加载器
     * @return 类加载器数组（从传入的到根）
     */
    public static ClassLoader[] getClassLoaderHierarchy(ClassLoader classLoader) {
        if (classLoader == null) {
            return new ClassLoader[0];
        }

        java.util.List<ClassLoader> hierarchy = new java.util.ArrayList<>();
        ClassLoader current = classLoader;

        while (current != null) {
            hierarchy.add(current);
            current = current.getParent();
        }

        return hierarchy.toArray(new ClassLoader[0]);
    }

    /**
     * 获取类的字节码
     *
     * @param clazz 类
     * @param classLoader 类加载器
     * @return 字节码数组，如果找不到返回null
     */
    public static byte[] getClassBytes(Class<?> clazz, ClassLoader classLoader) {
        String className = clazz.getName();
        String resourceName = className.replace('.', '/') + ".class";

        InputStream is = classLoader.getResourceAsStream(resourceName);
        if (is == null) {
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // 忽略
            }
        }
    }

    /**
     * 检查类是否由指定类加载器加载
     *
     * @param clazz 类
     * @param classLoader 类加载器
     * @return true如果是由指定类加载器加载
     */
    public static boolean isLoadedBy(Class<?> clazz, ClassLoader classLoader) {
        return clazz.getClassLoader() == classLoader;
    }

    /**
     * 获取类的加载器名称
     *
     * @param classLoader 类加载器
     * @return 类加载器名称
     */
    public static String getClassLoaderName(ClassLoader classLoader) {
        if (classLoader == null) {
            return "Bootstrap ClassLoader";
        }
        return classLoader.getClass().getName() + "@" + System.identityHashCode(classLoader);
    }

    /**
     * 打印类加载器层次结构
     *
     * @param classLoader 类加载器
     * @return 层次结构字符串
     */
    public static String printClassLoaderHierarchy(ClassLoader classLoader) {
        StringBuilder sb = new StringBuilder();
        ClassLoader[] hierarchy = getClassLoaderHierarchy(classLoader);

        for (int i = 0; i < hierarchy.length; i++) {
            for (int j = 0; j < i; j++) {
                sb.append("  ");
            }
            sb.append("-> ").append(getClassLoaderName(hierarchy[i])).append("\n");
        }

        return sb.toString();
    }

    /**
     * 创建字节码类加载器
     *
     * @return BytecodeClassLoader实例
     */
    public static BytecodeClassLoader createBytecodeClassLoader() {
        return new BytecodeClassLoader(getDefaultClassLoader());
    }

    /**
     * 私有构造函数，防止实例化
     */
    private ClassLoaderUtils() {
        throw new AssertionError("工具类不能实例化");
    }
}
