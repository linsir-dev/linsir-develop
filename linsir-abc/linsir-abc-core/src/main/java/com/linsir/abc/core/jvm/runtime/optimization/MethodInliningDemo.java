package com.linsir.abc.core.jvm.runtime.optimization;

/**
 * 方法内联（Method Inlining）演示类
 *
 * <p>方法内联是最重要的JIT编译优化技术之一，它将方法调用替换为方法体本身，
 * 消除了方法调用的开销（栈帧建立、参数传递、跳转等），并为其他优化技术提供基础。</p>
 *
 * <p>内联条件：</p>
 * <ul>
 *   <li>方法大小：不能超过阈值（默认35字节，可通过-XX:MaxInlineSize调整）</li>
 *   <li>调用频率：热点方法更容易被内联</li>
 *   <li>方法修饰符：private、final、static方法更容易被内联（没有多态性）</li>
 *   <li>内联深度：防止无限递归内联</li>
 * </ul>
 *
 * <p>相关JVM参数：</p>
 * <pre>
 * -XX:MaxInlineSize=35          # 最大内联方法大小（字节）
 * -XX:FreqInlineSize=325        # 频繁调用方法的最大内联大小
 * -XX:InlineSmallCode=1000      # 小代码内联阈值
 * -XX:+PrintInlining            # 打印内联决策（需UnlockDiagnosticVMOptions）
 * </pre>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see EscapeAnalysisDemo
 * @see LoopOptimizationDemo
 */
public class MethodInliningDemo {

    /**
     * 适合内联的小方法 - 简单的加法操作
     *
     * <p>该方法字节码很小，会被JIT编译器内联。</p>
     *
     * @param a 第一个加数
     * @param b 第二个加数
     * @return 和
     */
    private static int add(int a, int b) {
        return a + b;
    }

    /**
     * 适合内联的小方法 - 计算平方
     *
     * @param x 待计算的值
     * @return 平方值
     */
    private static int square(int x) {
        return x * x;
    }

    /**
     * 适合内联的小方法 - 计算绝对值
     *
     * @param x 待计算的值
     * @return 绝对值
     */
    private static int abs(int x) {
        return x < 0 ? -x : x;
    }

    /**
     * 较大的方法 - 可能不会被内联
     *
     * <p>该方法包含较多逻辑，可能超过内联大小阈值。</p>
     *
     * @param x 输入值
     * @return 计算结果
     */
    private static int largeMethod(int x) {
        int result = x;
        result = result * 2;
        result = result + 10;
        result = result / 2;
        result = result - 5;
        result = result * result;
        result = result % 100;
        result = result + 1;
        result = result * 3;
        result = result / 4;
        return result;
    }

    /**
     * final方法 - 容易被内联
     *
     * <p>final方法没有多态性，编译器可以确定具体实现。</p>
     *
     * @param x 输入值
     * @return 计算结果
     */
    public final int finalMethod(int x) {
        return x * 2 + 1;
    }

    /**
     * 虚方法 - 内联需要类型分析
     *
     * <p>虚方法可能存在多态性，编译器需要进行类型分析才能内联。</p>
     *
     * @param x 输入值
     * @return 计算结果
     */
    public int virtualMethod(int x) {
        return x + 10;
    }

    /**
     * 使用内联方法的计算 - 优化前
     *
     * <p>包含多个方法调用，有调用开销。</p>
     *
     * @param a 参数a
     * @param b 参数b
     * @return 计算结果
     */
    public static int calculateWithCalls(int a, int b) {
        int sum = add(a, b);
        int sq = square(sum);
        int result = abs(sq);
        return result;
    }

    /**
     * 内联后的计算 - 优化后（概念展示）
     *
     * <p>JIT编译器会将calculateWithCalls优化为类似下面的代码。</p>
     *
     * @param a 参数a
     * @param b 参数b
     * @return 计算结果
     */
    public static int calculateInlined(int a, int b) {
        // 内联后的代码（概念展示）
        // int sum = a + b;        // 内联add方法
        // int sq = sum * sum;     // 内联square方法
        // int result = sq < 0 ? -sq : sq;  // 内联abs方法
        // return result;

        // 实际代码仍然调用方法，JIT会在运行时内联
        return calculateWithCalls(a, b);
    }

    /**
     * 测试内联性能
     *
     * <p>通过大量调用小方法，触发JIT内联优化。</p>
     *
     * @param iterations 迭代次数
     * @return 计算结果
     */
    public static long testInliningPerformance(int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            // 这些小方法会被内联
            int a = add(i, i + 1);
            int b = square(a);
            int c = abs(b - 100);
            sum += c;
        }
        return sum;
    }

    /**
     * 测试内联深度
     *
     * <p>演示多层方法调用的内联。</p>
     *
     * @param x 输入值
     * @return 计算结果
     */
    public static int testInliningDepth(int x) {
        return level1(x);
    }

    private static int level1(int x) {
        return level2(x + 1);
    }

    private static int level2(int x) {
        return level3(x + 1);
    }

    private static int level3(int x) {
        return level4(x + 1);
    }

    private static int level4(int x) {
        return x + 1;
    }

    /**
     * 多态方法调用测试
     *
     * <p>演示单态内联、双态内联和多态内联的区别。</p>
     */
    public static void testPolymorphicInlining() {
        System.out.println("=== 多态内联测试 ===\n");

        // 单态内联：只有一种实际类型
        System.out.println("1. 单态内联（Monomorphic）:");
        System.out.println("   只有一种类型，直接内联");
        MethodInliningDemo obj1 = new MethodInliningDemo();
        for (int i = 0; i < 100000; i++) {
            obj1.virtualMethod(i);
        }
        System.out.println("   完成 100000 次调用");
        System.out.println();

        // 双态内联：两种实际类型
        System.out.println("2. 双态内联（Bimorphic）:");
        System.out.println("   两种类型，生成类型判断代码");
        MethodInliningDemo[] objs = new MethodInliningDemo[2];
        objs[0] = new MethodInliningDemo();
        objs[1] = new SubClass();
        for (int i = 0; i < 100000; i++) {
            objs[i % 2].virtualMethod(i);
        }
        System.out.println("   完成 100000 次调用（两种类型）");
        System.out.println();

        // 多态内联：多种类型
        System.out.println("3. 多态内联（Megamorphic）:");
        System.out.println("   类型过多，放弃内联");
        MethodInliningDemo[] manyObjs = new MethodInliningDemo[10];
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                manyObjs[i] = new MethodInliningDemo();
            } else {
                manyObjs[i] = new SubClass();
            }
        }
        for (int i = 0; i < 100000; i++) {
            manyObjs[i % 10].virtualMethod(i);
        }
        System.out.println("   完成 100000 次调用（多种类型）");
        System.out.println();
    }

    /**
     * 性能对比测试
     */
    public static void performanceComparison() {
        System.out.println("=== 方法内联性能对比 ===\n");

        int iterations = 10000000;

        // 预热
        System.out.println("预热中...");
        for (int i = 0; i < 10000; i++) {
            testInliningPerformance(100);
        }
        System.out.println("预热完成\n");

        // 测试1：内联优化后的方法
        System.out.println("测试1: 小方法调用（会被内联）");
        long start1 = System.nanoTime();
        long result1 = testInliningPerformance(iterations);
        long time1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("  迭代次数: " + iterations);
        System.out.println("  结果: " + result1);
        System.out.println("  耗时: " + time1 + " ms");
        System.out.println();

        // 测试2：较大方法调用（可能不会被内联）
        System.out.println("测试2: 较大方法调用（可能不会被内联）");
        long sum2 = 0;
        long start2 = System.nanoTime();
        for (int i = 0; i < iterations / 10; i++) { // 减少迭代次数
            sum2 += largeMethod(i);
        }
        long time2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("  迭代次数: " + (iterations / 10));
        System.out.println("  结果: " + sum2);
        System.out.println("  耗时: " + time2 + " ms");
        System.out.println();

        // 测试3：内联深度
        System.out.println("测试3: 多层方法调用（内联深度测试）");
        long start3 = System.nanoTime();
        int result3 = 0;
        for (int i = 0; i < iterations; i++) {
            result3 += testInliningDepth(i);
        }
        long time3 = (System.nanoTime() - start3) / 1_000_000;
        System.out.println("  迭代次数: " + iterations);
        System.out.println("  结果: " + result3);
        System.out.println("  耗时: " + time3 + " ms");
        System.out.println();

        System.out.println("性能对比总结:");
        System.out.println("- 小方法内联后性能显著提升");
        System.out.println("- 内联深度受JVM参数限制");
        System.out.println("- 使用 -XX:+PrintInlining 查看实际内联情况");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== 方法内联（Method Inlining）演示 ===");
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println();

        // 打印内联相关信息
        System.out.println("方法内联相关信息:");
        System.out.println("  -XX:MaxInlineSize=35       最大内联方法大小");
        System.out.println("  -XX:FreqInlineSize=325     频繁调用方法的最大内联大小");
        System.out.println("  -XX:+PrintInlining         打印内联决策");
        System.out.println();

        // 多态内联测试
        testPolymorphicInlining();

        // 性能对比
        performanceComparison();

        System.out.println("=== 演示完成 ===");
        System.out.println();
        System.out.println("优化建议:");
        System.out.println("1. 将热点代码拆分为小方法（<35字节）");
        System.out.println("2. 使用private/final/static修饰方法");
        System.out.println("3. 避免在热点循环中调用虚方法");
        System.out.println("4. 减少多态调用，使用单态或双态");
    }

    /**
     * 子类，用于多态内联测试
     */
    static class SubClass extends MethodInliningDemo {
        @Override
        public int virtualMethod(int x) {
            return x * 2;
        }
    }
}
