package com.linsir.abc.core.jvm.remote.classloader;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 热替换类加载器
 *
 * 功能：加载动态编译的类，支持类的热替换
 *
 * 核心特性：
 * 1. 每个实例加载独立的类，实现隔离
 * 2. 支持从内存字节码加载类
 * 3. 优先加载本地类，打破双亲委派
 *
 * 使用场景：
 * 1. 动态代码执行
 * 2. 热部署
 * 3. 插件系统
 *
 * 注意事项：
 * 1. 系统类（java.*, javax.*）仍然委托给父类加载器
 * 2. 每个实例应该只使用一次，然后丢弃
 * 3. 需要手动调用clear()释放资源
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class HotSwapClassLoader extends ClassLoader {

    /**
     * 存储类名到字节码的映射
     */
    private final Map<String, byte[]> bytecodeMap = new ConcurrentHashMap<>();

    /**
     * 已加载的类缓存
     */
    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param parent 父类加载器
     */
    public HotSwapClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * 添加类字节码
     *
     * @param className 类名（全限定名）
     * @param bytecode 字节码数组
     */
    public void addClass(String className, byte[] bytecode) {
        if (className == null || bytecode == null) {
            throw new IllegalArgumentException("类名和字节码不能为空");
        }
        bytecodeMap.put(className, bytecode);
    }

    /**
     * 批量添加类字节码
     *
     * @param bytecodes Map<类名, 字节码>
     */
    public void addClasses(Map<String, byte[]> bytecodes) {
        if (bytecodes != null) {
            bytecodeMap.putAll(bytecodes);
        }
    }

    /**
     * 加载类（优先从本地加载）
     *
     * 打破双亲委派模型：
     * 1. 先检查类是否已加载
     * 2. 系统类委托给父类加载器
     * 3. 尝试从本地字节码加载
     * 4. 本地找不到再委托给父类加载器
     *
     * @param name 类名
     * @return Class对象
     * @throws ClassNotFoundException 如果找不到类
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    /**
     * 加载类（优先从本地加载）
     *
     * @param name 类名
     * @param resolve 是否解析类
     * @return Class对象
     * @throws ClassNotFoundException 如果找不到类
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 1. 检查类是否已加载
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }

        // 2. 如果是系统类，委托给父类加载器
        // 系统类必须由Bootstrap ClassLoader加载，确保安全
        if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("sun.")) {
            return super.loadClass(name, resolve);
        }

        // 3. 尝试从本地字节码加载（打破双亲委派）
        byte[] bytecode = bytecodeMap.get(name);
        if (bytecode != null) {
            clazz = defineClass(name, bytecode, 0, bytecode.length);
            loadedClasses.put(name, clazz);
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }

        // 4. 本地找不到，委托给父类加载器
        return super.loadClass(name, resolve);
    }

    /**
     * 查找类
     *
     * 仅供defineClass使用，从本地字节码中查找
     *
     * @param name 类名
     * @return Class对象
     * @throws ClassNotFoundException 如果找不到类
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytecode = bytecodeMap.get(name);
        if (bytecode != null) {
            return defineClass(name, bytecode, 0, bytecode.length);
        }
        throw new ClassNotFoundException(name);
    }

    /**
     * 获取已加载的所有类
     *
     * @return 已加载的Class对象集合
     */
    public Collection<Class<?>> getLoadedClasses() {
        return loadedClasses.values();
    }

    /**
     * 获取已加载类的数量
     *
     * @return 类数量
     */
    public int getLoadedClassCount() {
        return loadedClasses.size();
    }

    /**
     * 检查是否包含指定类的字节码
     *
     * @param className 类名
     * @return true表示包含
     */
    public boolean hasBytecode(String className) {
        return bytecodeMap.containsKey(className);
    }

    /**
     * 获取字节码映射中的类名集合
     *
     * @return 类名集合
     */
    public Collection<String> getBytecodeClassNames() {
        return bytecodeMap.keySet();
    }

    /**
     * 清除所有加载的类和字节码
     *
     * 注意：调用此方法后，此ClassLoader实例不应再使用
     */
    public void clear() {
        bytecodeMap.clear();
        loadedClasses.clear();
    }

    /**
     * 移除指定类的字节码和已加载的类
     *
     * @param className 类名
     */
    public void removeClass(String className) {
        bytecodeMap.remove(className);
        loadedClasses.remove(className);
    }
}
