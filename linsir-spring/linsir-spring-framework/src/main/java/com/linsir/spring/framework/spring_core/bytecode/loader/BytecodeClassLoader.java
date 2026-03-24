package com.linsir.spring.framework.spring_core.bytecode.loader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字节码类加载器
 *
 * <p>用于动态加载由字节码生成的类。这是实现动态代理、AOP等功能的基础。
 * 支持从字节码数组直接定义类，并提供缓存机制。
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建类加载器
 * BytecodeClassLoader loader = new BytecodeClassLoader();
 *
 * // 定义类
 * byte[] bytecode = generateBytecode();
 * Class<?> clazz = loader.defineClass("com.example.GeneratedClass", bytecode);
 *
 * // 创建实例
 * Object instance = clazz.getDeclaredConstructor().newInstance();
 * }</pre>
 *
 * <p>特性：
 * <ul>
 *   <li>从字节码定义类</li>
 *   <li>类缓存</li>
 *   <li>线程安全</li>
 *   <li>支持类卸载</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see ClassLoader
 */
public class BytecodeClassLoader extends ClassLoader {

    /**
     * 已定义的类缓存
     */
    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    /**
     * 默认构造函数
     *
     * <p>使用系统类加载器作为父加载器。
     */
    public BytecodeClassLoader() {
        this(getSystemClassLoader());
    }

    /**
     * 构造函数
     *
     * @param parent 父类加载器
     */
    public BytecodeClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * 从字节码定义类
     *
     * <p>将字节码数组转换为Class对象。
     *
     * @param name 类全限定名
     * @param bytecode 字节码数组
     * @return Class对象
     * @throws IllegalArgumentException 如果类名格式不正确
     */
    public Class<?> defineClass(String name, byte[] bytecode) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("类名不能为空");
        }
        if (bytecode == null || bytecode.length == 0) {
            throw new IllegalArgumentException("字节码不能为空");
        }

        // 检查缓存
        Class<?> cachedClass = classCache.get(name);
        if (cachedClass != null) {
            return cachedClass;
        }

        // 定义类
        Class<?> clazz = defineClass(name, bytecode, 0, bytecode.length);

        // 缓存类
        classCache.put(name, clazz);

        return clazz;
    }

    /**
     * 从字节码定义类（不缓存）
     *
     * @param name 类全限定名
     * @param bytecode 字节码数组
     * @return Class对象
     */
    public Class<?> defineClassWithoutCache(String name, byte[] bytecode) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("类名不能为空");
        }
        if (bytecode == null || bytecode.length == 0) {
            throw new IllegalArgumentException("字节码不能为空");
        }

        return defineClass(name, bytecode, 0, bytecode.length);
    }

    /**
     * 获取已定义的类
     *
     * @param name 类全限定名
     * @return Class对象，如果未定义返回null
     */
    public Class<?> getDefinedClass(String name) {
        return classCache.get(name);
    }

    /**
     * 检查类是否已定义
     *
     * @param name 类全限定名
     * @return true如果已定义
     */
    public boolean isDefined(String name) {
        return classCache.containsKey(name);
    }

    /**
     * 清除类缓存
     *
     * <p>注意：已加载的类不会真正卸载，直到类加载器被垃圾回收。
     */
    public void clearCache() {
        classCache.clear();
    }

    /**
     * 获取缓存大小
     *
     * @return 缓存中的类数量
     */
    public int getCacheSize() {
        return classCache.size();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 先从缓存查找
        Class<?> clazz = classCache.get(name);
        if (clazz != null) {
            return clazz;
        }

        // 委托给父类
        return super.findClass(name);
    }

    /**
     * 获取所有已定义类的名称
     *
     * @return 类名数组
     */
    public String[] getDefinedClassNames() {
        return classCache.keySet().toArray(new String[0]);
    }
}
