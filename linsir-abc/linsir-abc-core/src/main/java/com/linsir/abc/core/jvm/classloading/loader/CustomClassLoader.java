package com.linsir.abc.core.jvm.classloading.loader;

import java.io.*;
import java.util.logging.Logger;

/**
 * 自定义类加载器
 * 演示如何实现一个自定义的类加载器，打破双亲委派模型
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class CustomClassLoader extends ClassLoader {

    private static final Logger LOGGER = Logger.getLogger(CustomClassLoader.class.getName());

    /**
     * 类文件根目录
     */
    private final String classPath;

    /**
     * 是否打破双亲委派模型
     */
    private final boolean breakDelegation;

    /**
     * 构造方法
     *
     * @param classPath       类文件根目录
     * @param breakDelegation 是否打破双亲委派模型
     */
    public CustomClassLoader(String classPath, boolean breakDelegation) {
        // 使用系统类加载器作为父类加载器
        super(ClassLoader.getSystemClassLoader());
        this.classPath = classPath;
        this.breakDelegation = breakDelegation;
    }

    /**
     * 构造方法，使用默认的双亲委派模型
     *
     * @param classPath 类文件根目录
     */
    public CustomClassLoader(String classPath) {
        this(classPath, false);
    }

    /**
     * 重写loadClass方法
     * 用于演示打破双亲委派模型
     *
     * @param name    类全限定名
     * @param resolve 是否解析
     * @return Class对象
     * @throws ClassNotFoundException 类未找到异常
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // 首先检查类是否已经被加载
        Class<?> c = findLoadedClass(name);

        if (c == null) {
            if (breakDelegation) {
                // 打破双亲委派模型：先尝试自己加载
                c = findClass(name);
                if (c == null) {
                    // 自己加载失败，再委托给父类加载器
                    c = getParent().loadClass(name);
                }
            } else {
                // 遵循双亲委派模型
                try {
                    if (getParent() != null) {
                        c = getParent().loadClass(name);
                    }
                } catch (ClassNotFoundException e) {
                    // 父类加载器无法加载，自己尝试加载
                }

                if (c == null) {
                    c = findClass(name);
                }
            }
        }

        if (resolve) {
            resolveClass(c);
        }

        return c;
    }

    /**
     * 重写findClass方法
     * 从自定义路径加载类文件
     *
     * @param name 类全限定名
     * @return Class对象
     * @throws ClassNotFoundException 类未找到异常
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 将类全限定名转换为文件路径
        String fileName = name.replace('.', File.separatorChar) + ".class";
        File classFile = new File(classPath, fileName);

        if (!classFile.exists()) {
            return null;
        }

        try (InputStream is = new FileInputStream(classFile);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }

            byte[] classData = baos.toByteArray();

            // 定义类
            return defineClass(name, classData, 0, classData.length);

        } catch (IOException e) {
            LOGGER.warning("Failed to load class " + name + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * 获取类路径
     *
     * @return 类路径
     */
    public String getClassPath() {
        return classPath;
    }

    /**
     * 是否打破双亲委派模型
     *
     * @return true表示打破双亲委派模型
     */
    public boolean isBreakDelegation() {
        return breakDelegation;
    }

    /**
     * 演示类加载器的使用
     */
    public static void demonstrateClassLoader() {
        LOGGER.info("=== 自定义类加载器演示 ===");
        System.out.println("\n--- 开始测试 ---");

        // 获取当前类的类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println("系统类加载器: " + systemClassLoader);

        ClassLoader extClassLoader = systemClassLoader.getParent();
        System.out.println("扩展类加载器: " + extClassLoader);

        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println("启动类加载器: " + bootstrapClassLoader);

        // 测试加载String类（由启动类加载器加载）
        try {
            Class<?> stringClass = Class.forName("java.lang.String");
            System.out.println("String类的类加载器: " + stringClass.getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("--- 测试结束 ---\n");
    }

    /**
     * 演示不同类加载器加载的类不相等
     */
    public static void demonstrateClassIdentity() throws Exception {
        LOGGER.info("=== 演示不同类加载器加载的类不相等 ===");
        System.out.println("\n--- 开始测试 ---");

        // 使用自定义类加载器加载当前类
        CustomClassLoader loader = new CustomClassLoader("target/classes");

        Class<?> clazz = loader.loadClass("com.linsir.abc.core.jvm.classloading.loader.CustomClassLoader");
        Object obj = clazz.getDeclaredConstructor(String.class).newInstance("test");

        System.out.println("obj.getClass(): " + obj.getClass());
        System.out.println("obj.getClass().getClassLoader(): " + obj.getClass().getClassLoader());
        System.out.println("obj instanceof CustomClassLoader: " + (obj instanceof CustomClassLoader));

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：即使来源于同一个Class文件，不同类加载器加载的类也不相等");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) throws Exception {
        demonstrateClassLoader();
        demonstrateClassIdentity();
    }
}
