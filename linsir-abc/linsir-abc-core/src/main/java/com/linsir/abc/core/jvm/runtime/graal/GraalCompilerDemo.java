package com.linsir.abc.core.jvm.runtime.graal;

import java.util.ArrayList;
import java.util.List;

/**
 * Graal编译器演示类
 *
 * <p>Graal是用Java编写的新一代JIT编译器，通过JVMCI接口与HotSpot虚拟机集成。
 * 相比C2编译器，Graal具有更好的模块化、可维护性和多语言支持能力。</p>
 *
 * <p>Graal编译器特性：</p>
 * <ul>
 *   <li>用Java编写，易于理解和修改</li>
 *   <li>模块化设计，便于扩展</li>
 *   <li>支持部分逃逸分析（Partial Escape Analysis）</li>
 *   <li>原生支持AOT编译（Native Image）</li>
 *   <li>通过Truffle框架支持多语言</li>
 * </ul>
 *
 * <p>使用Graal作为JIT编译器：</p>
 * <pre>
 * java -XX:+UnlockExperimentalVMOptions \
 *      -XX:+UseJVMCICompiler \
 *      -XX:+EnableJVMCI \
 *      MyApplication
 * </pre>
 *
 * <p>GraalVM Native Image：</p>
 * <pre>
 * # 安装native-image工具
 * gu install native-image
 *
 * # 编译为本地可执行文件
 * native-image -cp . HelloWorld
 *
 * # 运行（无需JVM）
 * ./helloworld
 * </pre>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see GraalPerformanceTest
 */
public class GraalCompilerDemo {

    /**
     * 检查是否使用Graal编译器
     *
     * @return 是否使用Graal
     */
    public static boolean isGraalCompiler() {
        String vmName = System.getProperty("java.vm.name");
        return vmName != null && vmName.toLowerCase().contains("graal");
    }

    /**
     * 检查是否使用JVMCI
     *
     * @return 是否使用JVMCI
     */
    public static boolean isJVMCIEnabled() {
        // 检查JVMCI是否启用
        String jvmci = System.getProperty("jvmci.Compiler");
        return jvmci != null && !jvmci.isEmpty();
    }

    /**
     * 计算密集型任务 - 用于性能测试
     *
     * @param n 计算规模
     * @return 计算结果
     */
    public static long computeIntensive(int n) {
        long sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sum += i * j;
            }
        }
        return sum;
    }

    /**
     * 斐波那契数列 - 递归版本
     *
     * @param n 索引
     * @return 斐波那契数
     */
    public static long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    /**
     * 斐波那契数列 - 迭代版本
     *
     * @param n 索引
     * @return 斐波那契数
     */
    public static long fibonacciIterative(int n) {
        if (n <= 1) return n;
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }

    /**
     * 部分逃逸分析示例
     *
     * <p>Graal的部分逃逸分析允许对象在部分控制流路径上分配在栈上。</p>
     *
     * @param condition 条件
     * @return 结果
     */
    public static int partialEscapeAnalysis(boolean condition) {
        // 对象可能在部分路径上逃逸
        Point p = new Point(10, 20);

        if (condition) {
            // 此路径逃逸，需要在堆上分配
            storePoint(p);
        }
        // 其他路径可以栈上分配
        return p.x + p.y;
    }

    /**
     * 存储Point对象
     */
    private static Point storedPoint;

    private static void storePoint(Point p) {
        storedPoint = p;
    }

    /**
     * 简单Point类
     */
    public static class Point {
        int x, y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * 打印JVM信息
     */
    public static void printJVMInfo() {
        System.out.println("=== JVM信息 ===\n");

        System.out.println("Java版本: " + System.getProperty("java.version"));
        System.out.println("Java供应商: " + System.getProperty("java.vendor"));
        System.out.println("JVM名称: " + System.getProperty("java.vm.name"));
        System.out.println("JVM版本: " + System.getProperty("java.vm.version"));
        System.out.println("JVM供应商: " + System.getProperty("java.vm.vendor"));
        System.out.println();

        System.out.println("Graal编译器状态:");
        System.out.println("  使用Graal: " + isGraalCompiler());
        System.out.println("  JVMCI启用: " + isJVMCIEnabled());
        System.out.println();

        // 打印运行时信息
        Runtime runtime = Runtime.getRuntime();
        System.out.println("运行时信息:");
        System.out.println("  可用处理器: " + runtime.availableProcessors());
        System.out.println("  最大内存: " + (runtime.maxMemory() / 1024 / 1024) + " MB");
        System.out.println("  总内存: " + (runtime.totalMemory() / 1024 / 1024) + " MB");
        System.out.println("  空闲内存: " + (runtime.freeMemory() / 1024 / 1024) + " MB");
        System.out.println();
    }

    /**
     * 打印Graal使用指南
     */
    public static void printGraalGuide() {
        System.out.println("=== Graal编译器使用指南 ===\n");

        System.out.println("1. 使用Graal作为JIT编译器:");
        System.out.println("   java -XX:+UnlockExperimentalVMOptions \\");
        System.out.println("        -XX:+UseJVMCICompiler \\");
        System.out.println("        -XX:+EnableJVMCI \\");
        System.out.println("        MyApplication");
        System.out.println();

        System.out.println("2. 查看编译器信息:");
        System.out.println("   java -XX:+UnlockExperimentalVMOptions \\");
        System.out.println("        -XX:+PrintCompilation \\");
        System.out.println("        -XX:+UseJVMCICompiler \\");
        System.out.println("        MyApplication");
        System.out.println();

        System.out.println("3. GraalVM Native Image:");
        System.out.println("   # 安装native-image工具");
        System.out.println("   gu install native-image");
        System.out.println();
        System.out.println("   # 编译为本地可执行文件");
        System.out.println("   native-image -cp . HelloWorld");
        System.out.println();
        System.out.println("   # 运行（无需JVM）");
        System.out.println("   ./helloworld");
        System.out.println();

        System.out.println("4. Native Image优势:");
        System.out.println("   - 启动时间极快（毫秒级）");
        System.out.println("   - 内存占用极低");
        System.out.println("   - 可打包为独立可执行文件");
        System.out.println("   - 适合容器化部署");
        System.out.println();

        System.out.println("5. Native Image限制:");
        System.out.println("   - 反射需要配置");
        System.out.println("   - 动态代理需要配置");
        System.out.println("   - JNI需要配置");
        System.out.println("   - 不支持某些动态特性");
        System.out.println();

        System.out.println("6. Graal vs C2对比:");
        System.out.println("   ┌────────────────┬─────────────┬─────────────┐");
        System.out.println("   │     特性       │    Graal    │     C2      │");
        System.out.println("   ├────────────────┼─────────────┼─────────────┤");
        System.out.println("   │ 实现语言       │    Java     │    C++      │");
        System.out.println("   │ 代码可维护性   │     高      │    较低     │");
        System.out.println("   │ 模块化程度     │     高      │     低      │");
        System.out.println("   │ 编译速度       │    中等     │     慢      │");
        System.out.println("   │ 峰值性能       │   接近C2    │    最高     │");
        System.out.println("   │ AOT支持        │    原生     │    不支持   │");
        System.out.println("   │ 多语言支持     │    优秀     │    仅Java   │");
        System.out.println("   └────────────────┴─────────────┴─────────────┘");
        System.out.println();
    }

    /**
     * 性能测试
     */
    public static void performanceTest() {
        System.out.println("=== Graal编译器性能测试 ===\n");

        // 预热
        System.out.println("预热中...");
        for (int i = 0; i < 10000; i++) {
            computeIntensive(100);
            fibonacciIterative(30);
        }
        System.out.println("预热完成\n");

        // 测试1：计算密集型任务
        System.out.println("测试1: 计算密集型任务");
        long start1 = System.nanoTime();
        long result1 = computeIntensive(2000);
        long time1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("  结果: " + result1);
        System.out.println("  耗时: " + time1 + " ms");
        System.out.println();

        // 测试2：斐波那契
        System.out.println("测试2: 斐波那契计算");
        long start2 = System.nanoTime();
        long result2 = fibonacciIterative(50);
        long time2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("  结果: " + result2);
        System.out.println("  耗时: " + time2 + " ms");
        System.out.println();

        // 测试3：多次调用
        System.out.println("测试3: 多次调用性能");
        int iterations = 100000;
        long start3 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            fibonacciIterative(30);
        }
        long time3 = (System.nanoTime() - start3) / 1_000_000;
        System.out.println("  迭代次数: " + iterations);
        System.out.println("  总耗时: " + time3 + " ms");
        System.out.println("  平均每次: " + (time3 * 1000.0 / iterations) + " μs");
        System.out.println();

        // 测试4：内存分配
        System.out.println("测试4: 内存分配测试");
        Runtime runtime = Runtime.getRuntime();
        long memBefore = runtime.totalMemory() - runtime.freeMemory();

        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            list.add(new Object());
        }

        long memAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("  分配对象数: 100000");
        System.out.println("  内存增长: " + ((memAfter - memBefore) / 1024) + " KB");
        System.out.println();

        System.out.println("性能测试完成");
        System.out.println("提示: 使用Graal编译器重新运行以对比性能");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== Graal编译器演示 ===\n");

        // 打印JVM信息
        printJVMInfo();

        // 打印使用指南
        printGraalGuide();

        // 性能测试
        performanceTest();

        System.out.println("=== 演示完成 ===");
        System.out.println();

        if (!isGraalCompiler()) {
            System.out.println("当前未使用Graal编译器。");
            System.out.println("要使用Graal编译器，请:");
            System.out.println("1. 安装GraalVM");
            System.out.println("2. 使用 -XX:+UseJVMCICompiler 参数运行");
            System.out.println();
        }

        System.out.println("建议:");
        System.out.println("1. 服务端应用：使用Graal编译器获得更好的性能");
        System.out.println("2. 容器化部署：考虑使用Native Image减少启动时间");
        System.out.println("3. 多语言项目：利用GraalVM的Polyglot特性");
    }
}
