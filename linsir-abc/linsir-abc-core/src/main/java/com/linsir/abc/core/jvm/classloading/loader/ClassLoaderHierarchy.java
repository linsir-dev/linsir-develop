package com.linsir.abc.core.jvm.classloading.loader;

import java.util.logging.Logger;

/**
 * 类加载器层次结构演示类
 * 演示三层类加载器（启动类加载器、扩展类加载器、应用程序类加载器）
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class ClassLoaderHierarchy {

    private static final Logger LOGGER = Logger.getLogger(ClassLoaderHierarchy.class.getName());

    /**
     * 类加载器类型枚举
     */
    public enum ClassLoaderType {
        /**
         * 启动类加载器（Bootstrap ClassLoader）
         */
        BOOTSTRAP("启动类加载器", "C++实现", "<JAVA_HOME>\\lib"),

        /**
         * 扩展类加载器（Extension ClassLoader）
         */
        EXTENSION("扩展类加载器", "Java实现", "<JAVA_HOME>\\lib\\ext"),

        /**
         * 应用程序类加载器（Application ClassLoader）
         */
        APPLICATION("应用程序类加载器", "Java实现", "classpath"),

        /**
         * 自定义类加载器
         */
        CUSTOM("自定义类加载器", "Java实现", "自定义路径");

        private final String name;
        private final String implementation;
        private final String loadRange;

        ClassLoaderType(String name, String implementation, String loadRange) {
            this.name = name;
            this.implementation = implementation;
            this.loadRange = loadRange;
        }

        public String getName() {
            return name;
        }

        public String getImplementation() {
            return implementation;
        }

        public String getLoadRange() {
            return loadRange;
        }
    }

    /**
     * 打印类加载器层次结构
     */
    public void printClassLoaderHierarchy() {
        LOGGER.info("=== 类加载器层次结构 ===");
        System.out.println("\n--- 开始测试 ---");

        // 获取当前类的类加载器（应用程序类加载器）
        ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println("当前类加载器: " + appClassLoader);

        // 获取父类加载器（扩展类加载器）
        ClassLoader extClassLoader = appClassLoader.getParent();
        System.out.println("父类加载器: " + extClassLoader);

        // 获取祖父类加载器（启动类加载器）
        ClassLoader bootstrapClassLoader = extClassLoader.getParent();
        System.out.println("祖父类加载器: " + bootstrapClassLoader);

        System.out.println("\n类加载器层次结构:");
        System.out.println("  Bootstrap ClassLoader (启动类加载器)");
        System.out.println("       ↑");
        System.out.println("  Extension ClassLoader (扩展类加载器)");
        System.out.println("       ↑");
        System.out.println("  Application ClassLoader (应用程序类加载器)");
        System.out.println("       ↑");
        System.out.println("  Custom ClassLoader (自定义类加载器)");

        System.out.println("--- 测试结束 ---\n");
    }

    /**
     * 演示不同类的类加载器
     */
    public void demonstrateClassLoaders() {
        LOGGER.info("=== 演示不同类的类加载器 ===");
        System.out.println("\n--- 开始测试 ---");

        try {
            // java.lang.String 由启动类加载器加载
            Class<?> stringClass = Class.forName("java.lang.String");
            System.out.println("java.lang.String 的类加载器: " + stringClass.getClassLoader());

            // java.util.ArrayList 由启动类加载器加载
            Class<?> arrayListClass = Class.forName("java.util.ArrayList");
            System.out.println("java.util.ArrayList 的类加载器: " + arrayListClass.getClassLoader());

            // 当前类由应用程序类加载器加载
            System.out.println("当前类的类加载器: " + this.getClass().getClassLoader());

            // 自定义类加载器加载的类
            CustomClassLoader customLoader = new CustomClassLoader("target/classes");
            Class<?> customClass = customLoader.loadClass("com.linsir.abc.core.jvm.classloading.loader.ClassLoaderHierarchy");
            System.out.println("自定义类加载器加载的类: " + customClass.getClassLoader());

        } catch (ClassNotFoundException e) {
            LOGGER.warning("类未找到: " + e.getMessage());
        }

        System.out.println("--- 测试结束 ---\n");
    }

    /**
     * 演示双亲委派模型
     */
    public void demonstrateParentDelegation() {
        LOGGER.info("=== 演示双亲委派模型 ===");
        System.out.println("\n--- 开始测试 ---");

        System.out.println("双亲委派模型工作流程:");
        System.out.println("1. 收到类加载请求");
        System.out.println("2. 委托给父类加载器");
        System.out.println("3. 父类加载器继续向上委托");
        System.out.println("4. 直到启动类加载器");
        System.out.println("5. 启动类加载器尝试加载");
        System.out.println("6. 无法加载则向下返回");
        System.out.println("7. 子类加载器尝试加载");
        System.out.println("8. 直到加载成功或抛出异常");

        System.out.println("\n双亲委派模型的优点:");
        System.out.println("1. 避免类的重复加载");
        System.out.println("2. 保证Java核心API的安全性");
        System.out.println("3. 防止核心类库被篡改");

        System.out.println("--- 测试结束 ---\n");
    }

    /**
     * 打印类加载器详细信息
     */
    public void printClassLoaderDetails() {
        LOGGER.info("=== 类加载器详细信息 ===");
        System.out.println("\n--- 开始测试 ---");

        for (ClassLoaderType type : ClassLoaderType.values()) {
            System.out.println("\n" + type.getName() + ":");
            System.out.println("  实现语言: " + type.getImplementation());
            System.out.println("  加载范围: " + type.getLoadRange());
        }

        System.out.println("\n--- 测试结束 ---\n");
    }

    /**
     * 运行所有演示
     */
    public void runAllDemos() {
        LOGGER.info("\n========== 类加载器层次结构演示 ==========\n");

        printClassLoaderHierarchy();
        demonstrateClassLoaders();
        demonstrateParentDelegation();
        printClassLoaderDetails();

        LOGGER.info("\n========== 演示结束 ==========\n");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        ClassLoaderHierarchy demo = new ClassLoaderHierarchy();
        demo.runAllDemos();
    }
}
