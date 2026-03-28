package com.linsir.abc.core.jvm.classloading.spi;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * SPI机制演示类
 * 演示Java SPI（Service Provider Interface）机制
 * 以及线程上下文类加载器如何打破双亲委派模型
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class SPIDemo {

    private static final Logger LOGGER = Logger.getLogger(SPIDemo.class.getName());

    /**
     * 演示基本的SPI加载机制
     */
    public void demonstrateBasicSPI() {
        LOGGER.info("=== 演示基本的SPI加载机制 ===");
        System.out.println("\n--- 开始测试 ---");

        // 使用ServiceLoader加载DataSourceService的实现
        ServiceLoader<DataSourceService> loader = ServiceLoader.load(DataSourceService.class);

        System.out.println("发现的服务实现:");
        Iterator<DataSourceService> iterator = loader.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            DataSourceService service = iterator.next();
            count++;
            System.out.println("  " + count + ". " + service.getClass().getName());
            System.out.println("     数据源名称: " + service.getDataSourceName());
            System.out.println("     连接字符串: " + service.getConnection());
            System.out.println("     类加载器: " + service.getClass().getClassLoader());
        }

        if (count == 0) {
            System.out.println("  未发现服务实现");
            System.out.println("  提示：需要在META-INF/services/目录下创建服务配置文件");
        }

        System.out.println("--- 测试结束 ---\n");
    }

    /**
     * 演示线程上下文类加载器
     */
    public void demonstrateContextClassLoader() {
        LOGGER.info("=== 演示线程上下文类加载器 ===");
        System.out.println("\n--- 开始测试 ---");

        // 获取当前线程的上下文类加载器
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.println("当前线程上下文类加载器: " + contextClassLoader);

        // 获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println("系统类加载器: " + systemClassLoader);

        // 比较两者是否相同
        System.out.println("两者是否相同: " + (contextClassLoader == systemClassLoader));

        System.out.println("\n线程上下文类加载器的作用:");
        System.out.println("  1. SPI机制中，接口由启动类加载器加载");
        System.out.println("  2. 实现类在classpath中，由应用程序类加载器加载");
        System.out.println("  3. 启动类加载器无法加载classpath中的类");
        System.out.println("  4. 通过线程上下文类加载器打破双亲委派模型");

        System.out.println("--- 测试结束 ---\n");
    }

    /**
     * 演示自定义类加载器作为上下文类加载器
     */
    public void demonstrateCustomContextClassLoader() {
        LOGGER.info("=== 演示自定义上下文类加载器 ===");
        System.out.println("\n--- 开始测试 ---");

        // 保存原始上下文类加载器
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.println("原始上下文类加载器: " + originalClassLoader);

        try {
            // 创建自定义类加载器
            com.linsir.abc.core.jvm.classloading.loader.CustomClassLoader customLoader =
                    new com.linsir.abc.core.jvm.classloading.loader.CustomClassLoader("target/classes");

            // 设置自定义类加载器为上下文类加载器
            Thread.currentThread().setContextClassLoader(customLoader);
            System.out.println("设置后的上下文类加载器: " + Thread.currentThread().getContextClassLoader());

            // 使用新的上下文类加载器加载服务
            ServiceLoader<DataSourceService> loader = ServiceLoader.load(DataSourceService.class);
            System.out.println("使用自定义类加载器发现的服务:");
            int count = 0;
            for (DataSourceService service : loader) {
                count++;
                System.out.println("  " + count + ". " + service.getClass().getName());
            }
            if (count == 0) {
                System.out.println("  未发现服务实现");
            }

        } finally {
            // 恢复原始上下文类加载器
            Thread.currentThread().setContextClassLoader(originalClassLoader);
            System.out.println("恢复后的上下文类加载器: " + Thread.currentThread().getContextClassLoader());
        }

        System.out.println("--- 测试结束 ---\n");
    }

    /**
     * 演示SPI的工作原理
     */
    public void demonstrateSPIPrinciple() {
        LOGGER.info("=== 演示SPI的工作原理 ===");
        System.out.println("\n--- 开始测试 ---");

        System.out.println("SPI工作原理:");
        System.out.println("  1. 定义服务接口（如DataSourceService）");
        System.out.println("  2. 实现服务接口（如MySQLDataSource、OracleDataSource）");
        System.out.println("  3. 在META-INF/services/目录下创建配置文件");
        System.out.println("     文件名为接口全限定名");
        System.out.println("     内容为实现类全限定名，每行一个");
        System.out.println("  4. 使用ServiceLoader加载服务实现");

        System.out.println("\n配置文件示例:");
        System.out.println("  文件路径: META-INF/services/com.linsir.abc.core.jvm.classloading.spi.DataSourceService");
        System.out.println("  文件内容:");
        System.out.println("    com.linsir.abc.core.jvm.classloading.spi.MySQLDataSource");
        System.out.println("    com.linsir.abc.core.jvm.classloading.spi.OracleDataSource");

        System.out.println("\n双亲委派模型的问题:");
        System.out.println("  1. 服务接口由启动类加载器加载");
        System.out.println("  2. 服务实现在classpath中，由应用程序类加载器加载");
        System.out.println("  3. 启动类加载器无法加载classpath中的类");
        System.out.println("  4. 需要打破双亲委派模型");

        System.out.println("\n解决方案 - 线程上下文类加载器:");
        System.out.println("  1. ServiceLoader.load()使用线程上下文类加载器");
        System.out.println("  2. 默认线程上下文类加载器是应用程序类加载器");
        System.out.println("  3. 可以加载classpath中的服务实现类");

        System.out.println("--- 测试结束 ---\n");
    }

    /**
     * 运行所有演示
     */
    public void runAllDemos() {
        LOGGER.info("\n========== SPI机制演示 ==========\n");

        demonstrateBasicSPI();
        demonstrateContextClassLoader();
        demonstrateCustomContextClassLoader();
        demonstrateSPIPrinciple();

        LOGGER.info("\n========== 演示结束 ==========\n");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SPIDemo demo = new SPIDemo();
        demo.runAllDemos();
    }
}
