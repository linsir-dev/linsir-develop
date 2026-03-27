package com.linsir.abc.core.jvm.gc;

import com.linsir.abc.core.jvm.gc.allocation.*;
import com.linsir.abc.core.jvm.gc.finalize.FinalizeEscapeGC;
import com.linsir.abc.core.jvm.gc.finalize.ResourceCleanupExample;
import com.linsir.abc.core.jvm.gc.reference.*;
import com.linsir.abc.core.jvm.gc.utils.GCLogAnalyzer;

import java.io.IOException;
import java.util.Scanner;

/**
 * GC测试运行器
 *
 * 统一入口，用于运行和验证所有GC相关示例代码。
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class GCTestRunner {

    /**
     * 主菜单
     */
    private static final String MENU = """
        
        ========================================
           JVM垃圾收集器与内存分配策略测试
        ========================================
        1. 引用类型示例
           1.1 软引用示例 (SoftReferenceExample)
           1.2 弱引用示例 (WeakReferenceExample)
           1.3 虚引用示例 (PhantomReferenceExample)
           1.4 引用类型对比 (ReferenceTypeComparison)
        
        2. Finalize机制示例
           2.1 Finalize逃逸示例 (FinalizeEscapeGC)
           2.2 资源清理替代方案 (ResourceCleanupExample)
        
        3. 内存分配策略示例
           3.1 Eden区分配 (EdenAllocation)
           3.2 大对象直接进入老年代 (PretenureSizeThreshold)
           3.3 长期存活对象晋升 (TenuringThreshold)
           3.4 空间分配担保 (HandlePromotionFailure)
        
        4. 工具类
           4.1 GC日志分析器 (GCLogAnalyzer)
        
        0. 退出
        ========================================
        请输入选项（如：1.1）：
        """;

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println(MENU);
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    // 引用类型示例
                    case "1.1" -> runSoftReferenceExample();
                    case "1.2" -> runWeakReferenceExample();
                    case "1.3" -> runPhantomReferenceExample();
                    case "1.4" -> runReferenceTypeComparison();

                    // Finalize机制示例
                    case "2.1" -> runFinalizeEscapeGC();
                    case "2.2" -> runResourceCleanupExample();

                    // 内存分配策略示例
                    case "3.1" -> runEdenAllocation();
                    case "3.2" -> runPretenureSizeThreshold();
                    case "3.3" -> runTenuringThreshold();
                    case "3.4" -> runHandlePromotionFailure();

                    // 工具类
                    case "4.1" -> runGCLogAnalyzer();

                    // 退出
                    case "0", "exit", "quit" -> {
                        System.out.println("感谢使用，再见！");
                        return;
                    }

                    default -> System.out.println("无效选项，请重新输入");
                }
            } catch (Exception e) {
                System.err.println("执行出错: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("\n按回车键继续...");
            scanner.nextLine();
        }
    }

    /**
     * 运行软引用示例
     */
    private static void runSoftReferenceExample() {
        System.out.println("\n>>> 运行软引用示例 <<<\n");
        System.out.println("VM参数建议: -Xms20m -Xmx20m -XX:+PrintGCDetails");
        System.out.println();

        SoftReferenceExample.main(new String[]{});
    }

    /**
     * 运行弱引用示例
     */
    private static void runWeakReferenceExample() {
        System.out.println("\n>>> 运行弱引用示例 <<<\n");

        WeakReferenceExample.main(new String[]{});
    }

    /**
     * 运行虚引用示例
     */
    private static void runPhantomReferenceExample() {
        System.out.println("\n>>> 运行虚引用示例 <<<\n");

        PhantomReferenceExample.main(new String[]{});
    }

    /**
     * 运行引用类型对比
     */
    private static void runReferenceTypeComparison() {
        System.out.println("\n>>> 运行引用类型对比 <<<\n");
        System.out.println("VM参数建议: -Xms20m -Xmx20m");
        System.out.println();

        ReferenceTypeComparison.main(new String[]{});
    }

    /**
     * 运行Finalize逃逸示例
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void runFinalizeEscapeGC() throws InterruptedException {
        System.out.println("\n>>> 运行Finalize逃逸示例 <<<\n");
        System.out.println("注意: finalize()方法在JDK 9+中已被废弃");
        System.out.println();

        FinalizeEscapeGC.main(new String[]{});
    }

    /**
     * 运行资源清理替代方案示例
     *
     * @throws InterruptedException 当线程被中断时抛出
     */
    private static void runResourceCleanupExample() throws InterruptedException {
        System.out.println("\n>>> 运行资源清理替代方案示例 <<<\n");

        ResourceCleanupExample.main(new String[]{});
    }

    /**
     * 运行Eden区分配示例
     */
    private static void runEdenAllocation() {
        System.out.println("\n>>> 运行Eden区分配示例 <<<\n");
        System.out.println("VM参数: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8");
        System.out.println();

        EdenAllocation.main(new String[]{});
    }

    /**
     * 运行大对象直接进入老年代示例
     */
    private static void runPretenureSizeThreshold() {
        System.out.println("\n>>> 运行大对象直接进入老年代示例 <<<\n");
        System.out.println("VM参数: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8");
        System.out.println("        -XX:PretenureSizeThreshold=3145728");
        System.out.println("注意: 此参数只对Serial和ParNew收集器有效");
        System.out.println();

        PretenureSizeThreshold.main(new String[]{});
    }

    /**
     * 运行长期存活对象晋升示例
     */
    private static void runTenuringThreshold() {
        System.out.println("\n>>> 运行长期存活对象晋升示例 <<<\n");
        System.out.println("VM参数: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8");
        System.out.println("        -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution");
        System.out.println();

        TenuringThreshold.main(new String[]{});
    }

    /**
     * 运行空间分配担保示例
     */
    private static void runHandlePromotionFailure() {
        System.out.println("\n>>> 运行空间分配担保示例 <<<\n");
        System.out.println("VM参数: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8");
        System.out.println();

        HandlePromotionFailure.main(new String[]{});
    }

    /**
     * 运行GC日志分析器
     */
    private static void runGCLogAnalyzer() {
        System.out.println("\n>>> 运行GC日志分析器 <<<\n");

        GCLogAnalyzer.main(new String[]{});
    }

    /**
     * 打印使用说明
     */
    private static void printUsage() {
        System.out.println("""
            
            使用说明:
            
            1. 编译代码:
               mvn clean compile
            
            2. 运行特定示例:
               java -cp target/classes com.linsir.abc.core.jvm.gc.GCTestRunner
            
            3. 带VM参数运行示例:
               java -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails \
                    -XX:SurvivorRatio=8 \
                    -cp target/classes \
                    com.linsir.abc.core.jvm.gc.allocation.EdenAllocation
            
            4. 运行所有测试:
               mvn test
            
            注意事项:
            - 某些示例需要特定的VM参数才能观察到预期效果
            - 建议配合-XX:+PrintGCDetails参数查看详细GC信息
            - 内存分配示例建议在较小的堆内存下运行以便观察GC
            """);
    }
}
