package com.linsir.abc.core.jvm.classloading;

import com.linsir.abc.core.jvm.classloading.initialization.PassiveReferenceDemo;
import com.linsir.abc.core.jvm.classloading.loader.ClassLoaderHierarchy;
import com.linsir.abc.core.jvm.classloading.loader.CustomClassLoader;
import com.linsir.abc.core.jvm.classloading.process.ClinitDemo;
import com.linsir.abc.core.jvm.classloading.spi.SPIDemo;

import java.util.logging.Logger;

/**
 * 类加载机制统一测试类
 * 测试第7章类加载机制的所有代码示例
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class ClassLoadingTest {

    private static final Logger LOGGER = Logger.getLogger(ClassLoadingTest.class.getName());

    /**
     * 测试7.2 类加载时机 - 被动引用示例
     */
    private static void testPassiveReference() {
        LOGGER.info("\n========== 测试7.2 类加载时机 - 被动引用示例 ==========\n");

        PassiveReferenceDemo demo = new PassiveReferenceDemo();
        demo.runAllDemos();
    }

    /**
     * 测试7.3 类加载过程 - <clinit>()方法
     */
    private static void testClinit() {
        LOGGER.info("\n========== 测试7.3 类加载过程 - <clinit>()方法 ==========\n");

        ClinitDemo demo = new ClinitDemo();
        demo.runAllDemos();
    }

    /**
     * 测试7.4 类加载器 - 层次结构
     */
    private static void testClassLoaderHierarchy() {
        LOGGER.info("\n========== 测试7.4 类加载器 - 层次结构 ==========\n");

        ClassLoaderHierarchy demo = new ClassLoaderHierarchy();
        demo.runAllDemos();
    }

    /**
     * 测试7.4 类加载器 - 自定义类加载器
     */
    private static void testCustomClassLoader() throws Exception {
        LOGGER.info("\n========== 测试7.4 类加载器 - 自定义类加载器 ==========\n");

        CustomClassLoader.demonstrateClassLoader();
        CustomClassLoader.demonstrateClassIdentity();
    }

    /**
     * 测试7.4 类加载器 - SPI机制
     */
    private static void testSPI() {
        LOGGER.info("\n========== 测试7.4 类加载器 - SPI机制 ==========\n");

        SPIDemo demo = new SPIDemo();
        demo.runAllDemos();
    }

    /**
     * 主方法 - 运行所有测试
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        LOGGER.info("\n##################################################");
        LOGGER.info("#                                                #");
        LOGGER.info("#     JVM类加载机制测试 - 第7章                  #");
        LOGGER.info("#                                                #");
        LOGGER.info("##################################################\n");

        try {
            // 测试7.2 类加载时机
            testPassiveReference();

            // 测试7.3 类加载过程
            testClinit();

            // 测试7.4 类加载器
            testClassLoaderHierarchy();
            testCustomClassLoader();
            testSPI();

            LOGGER.info("\n##################################################");
            LOGGER.info("#                                                #");
            LOGGER.info("#     所有测试完成！                             #");
            LOGGER.info("#                                                #");
            LOGGER.info("##################################################\n");

        } catch (Exception e) {
            LOGGER.severe("测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
