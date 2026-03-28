package com.linsir.abc.core.jvm.runtime.optimization;

import java.util.concurrent.TimeUnit;

/**
 * 逃逸分析（Escape Analysis）演示类
 *
 * <p>逃逸分析是C2编译器的重要优化技术，用于分析对象的作用域和生命周期。
 * 通过判断对象是否"逃逸"出方法或线程，编译器可以进行以下优化：</p>
 *
 * <ul>
 *   <li>栈上分配（Stack Allocation）：对象在栈上分配，随栈帧销毁而销毁</li>
 *   <li>标量替换（Scalar Replacement）：将对象拆分为基本类型变量</li>
 *   <li>同步消除（Synchronization Elimination）：消除不必要的同步操作</li>
 * </ul>
 *
 * <p>逃逸类型：</p>
 * <ul>
 *   <li>方法逃逸：对象被其他方法访问（作为参数传递、作为返回值返回）</li>
 *   <li>线程逃逸：对象被其他线程访问（赋值给静态变量、存入共享集合）</li>
 *   <li>无逃逸：对象仅在方法内部使用，不会逃逸</li>
 * </ul>
 *
 * <p>相关JVM参数：</p>
 * <pre>
 * -XX:+DoEscapeAnalysis          # 开启逃逸分析（JDK 8+默认开启）
 * -XX:+EliminateAllocations       # 开启标量替换（默认开启）
 * -XX:+EliminateLocks             # 开启锁消除（默认开启）
 * -XX:+PrintEscapeAnalysis        # 打印逃逸分析结果（诊断模式）
 * </pre>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see MethodInliningDemo
 * @see LoopOptimizationDemo
 */
public class EscapeAnalysisDemo {

    /**
     * 用于测试线程逃逸的静态变量
     */
    private static Object staticRef;

    /**
     * 简单坐标点类 - 用于标量替换演示
     */
    public static class Point {
        /**
         * X坐标
         */
        public int x;
        /**
         * Y坐标
         */
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * 计算到原点的距离平方
         *
         * @return 距离平方
         */
        public int distanceSquared() {
            return x * x + y * y;
        }
    }

    /**
     * 无逃逸对象示例 - 可以进行栈上分配和标量替换
     *
     * <p>Point对象仅在方法内部使用，不会逃逸出方法。
     * JIT编译器可以将其分配在栈上，或进行标量替换。</p>
     *
     * @param x X坐标
     * @param y Y坐标
     * @return 距离平方
     */
    public static int noEscape(int x, int y) {
        // Point对象不会逃逸，可以进行标量替换
        Point p = new Point(x, y);
        return p.distanceSquared();
    }

    /**
     * 方法逃逸示例 - 对象作为返回值
     *
     * <p>Point对象作为返回值逃逸出方法，必须在堆上分配。</p>
     *
     * @param x X坐标
     * @param y Y坐标
     * @return Point对象
     */
    public static Point methodEscape(int x, int y) {
        // Point对象逃逸出方法，不能栈上分配
        return new Point(x, y);
    }

    /**
     * 线程逃逸示例 - 对象赋值给静态变量
     *
     * <p>对象被赋值给静态变量，可以被其他线程访问。</p>
     *
     * @param obj 对象
     */
    public static void threadEscape(Object obj) {
        // 对象逃逸到线程间共享的静态变量
        staticRef = obj;
    }

    /**
     * 同步消除示例 - 对象不会逃逸出线程
     *
     * <p>StringBuffer的同步操作可以被消除。</p>
     *
     * @param count 拼接次数
     * @return 拼接后的字符串
     */
    public static String lockElimination(int count) {
        // StringBuffer是线程安全的，使用synchronized
        // 但由于sb不会逃逸出方法，同步可以被消除
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append("Item").append(i);
        }
        return sb.toString();
    }

    /**
     * 无法消除同步的示例 - 对象逃逸出方法
     *
     * @param sb 外部的StringBuffer
     * @param count 拼接次数
     */
    public static void noLockElimination(StringBuffer sb, int count) {
        // sb是外部传入的，可能逃逸出线程
        // 同步不能被消除
        for (int i = 0; i < count; i++) {
            sb.append("Item").append(i);
        }
    }

    /**
     * 测试标量替换性能
     *
     * <p>创建大量不会逃逸的Point对象，测试标量替换效果。</p>
     *
     * @param iterations 迭代次数
     * @return 计算结果
     */
    public static long testScalarReplacement(int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            // Point对象不会逃逸，可以被标量替换为两个int变量
            Point p = new Point(i, i + 1);
            sum += p.distanceSquared();
        }
        return sum;
    }

    /**
     * 对比版本 - 使用基本类型（模拟标量替换后的代码）
     *
     * @param iterations 迭代次数
     * @return 计算结果
     */
    public static long testWithPrimitives(int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            // 直接使用基本类型，没有对象创建开销
            int x = i;
            int y = i + 1;
            sum += x * x + y * y;
        }
        return sum;
    }

    /**
     * 测试栈上分配性能
     *
     * @param iterations 迭代次数
     * @return 计算结果
     */
    public static long testStackAllocation(int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            // 创建大量临时对象
            TempObject obj = new TempObject(i, i * 2, i * 3);
            sum += obj.calculate();
        }
        return sum;
    }

    /**
     * 临时对象类 - 用于测试栈上分配
     */
    public static class TempObject {
        private final int a;
        private final int b;
        private final int c;

        public TempObject(int a, int b, int c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public int calculate() {
            return a + b + c;
        }
    }

    /**
     * 测试同步消除性能
     *
     * @param iterations 迭代次数
     * @return 计算结果
     */
    public static long testLockElimination(int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            // 创建线程安全的计数器（不会逃逸）
            SafeCounter counter = new SafeCounter();
            counter.increment();
            counter.increment();
            counter.increment();
            sum += counter.getCount();
        }
        return sum;
    }

    /**
     * 线程安全的计数器类
     */
    public static class SafeCounter {
        private int count = 0;

        public synchronized void increment() {
            count++;
        }

        public synchronized int getCount() {
            return count;
        }
    }

    /**
     * 对比版本 - 使用非同步计数器
     */
    public static class UnsafeCounter {
        private int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    /**
     * 性能对比测试
     */
    public static void performanceComparison() {
        System.out.println("=== 逃逸分析性能对比 ===\n");

        int iterations = 10000000;

        // 预热
        System.out.println("预热中...");
        for (int i = 0; i < 10000; i++) {
            testScalarReplacement(100);
            testWithPrimitives(100);
            testStackAllocation(100);
            testLockElimination(100);
        }
        System.out.println("预热完成\n");

        // 测试1：标量替换
        System.out.println("测试1: 标量替换优化");
        System.out.println("  使用对象版本（会被标量替换）:");
        long start1 = System.nanoTime();
        long result1 = testScalarReplacement(iterations);
        long time1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("    结果: " + result1);
        System.out.println("    耗时: " + time1 + " ms");

        System.out.println("  使用基本类型版本:");
        long start2 = System.nanoTime();
        long result2 = testWithPrimitives(iterations);
        long time2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("    结果: " + result2);
        System.out.println("    耗时: " + time2 + " ms");
        System.out.println("  性能比: " + String.format("%.2f", (double) time1 / time2) + "x");
        System.out.println();

        // 测试2：栈上分配
        System.out.println("测试2: 栈上分配优化");
        System.out.println("  创建大量临时对象:");
        long start3 = System.nanoTime();
        long result3 = testStackAllocation(iterations);
        long time3 = (System.nanoTime() - start3) / 1_000_000;
        System.out.println("    结果: " + result3);
        System.out.println("    耗时: " + time3 + " ms");
        System.out.println("  注意: 开启逃逸分析后，这些对象在栈上分配");
        System.out.println();

        // 测试3：同步消除
        System.out.println("测试3: 同步消除优化");
        System.out.println("  使用同步计数器（同步会被消除）:");
        long start4 = System.nanoTime();
        long result4 = testLockElimination(iterations);
        long time4 = (System.nanoTime() - start4) / 1_000_000;
        System.out.println("    结果: " + result4);
        System.out.println("    耗时: " + time4 + " ms");
        System.out.println();

        System.out.println("性能对比总结:");
        System.out.println("- 标量替换可以消除对象创建开销");
        System.out.println("- 栈上分配避免堆内存分配和GC压力");
        System.out.println("- 同步消除减少线程同步开销");
    }

    /**
     * 演示不同逃逸场景
     */
    public static void demonstrateEscapeScenarios() {
        System.out.println("=== 逃逸场景演示 ===\n");

        // 场景1：无逃逸
        System.out.println("场景1: 无逃逸（No Escape）");
        System.out.println("  代码: Point p = new Point(1, 2); return p.distanceSquared();");
        System.out.println("  分析: Point对象仅在方法内部使用");
        System.out.println("  优化: 可以进行标量替换和栈上分配");
        System.out.println("  结果: " + noEscape(1, 2));
        System.out.println();

        // 场景2：方法逃逸
        System.out.println("场景2: 方法逃逸（Method Escape）");
        System.out.println("  代码: return new Point(x, y);");
        System.out.println("  分析: Point对象作为返回值逃逸出方法");
        System.out.println("  优化: 必须在堆上分配，不能标量替换");
        Point p = methodEscape(3, 4);
        System.out.println("  结果: Point(" + p.x + ", " + p.y + ")");
        System.out.println();

        // 场景3：线程逃逸
        System.out.println("场景3: 线程逃逸（Thread Escape）");
        System.out.println("  代码: staticRef = obj;");
        System.out.println("  分析: 对象赋值给静态变量，可被其他线程访问");
        System.out.println("  优化: 必须在堆上分配，同步不能消除");
        threadEscape(new Object());
        System.out.println("  结果: 对象已赋值给静态变量");
        System.out.println();

        // 场景4：同步消除
        System.out.println("场景4: 同步消除（Lock Elimination）");
        System.out.println("  代码: StringBuffer sb = new StringBuffer(); sb.append(...);");
        System.out.println("  分析: StringBuffer不会逃逸出线程");
        System.out.println("  优化: synchronized方法调用可以消除");
        String result = lockElimination(5);
        System.out.println("  结果: " + result);
        System.out.println();
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== 逃逸分析（Escape Analysis）演示 ===");
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println();

        // 打印逃逸分析相关信息
        System.out.println("逃逸分析相关参数:");
        System.out.println("  -XX:+DoEscapeAnalysis      开启逃逸分析（默认开启）");
        System.out.println("  -XX:+EliminateAllocations   开启标量替换（默认开启）");
        System.out.println("  -XX:+EliminateLocks         开启锁消除（默认开启）");
        System.out.println("  -XX:+PrintEscapeAnalysis    打印逃逸分析结果");
        System.out.println();

        // 演示逃逸场景
        demonstrateEscapeScenarios();

        // 性能对比
        performanceComparison();

        System.out.println("=== 演示完成 ===");
        System.out.println();
        System.out.println("优化建议:");
        System.out.println("1. 尽量使用局部对象，避免逃逸");
        System.out.println("2. 不要返回新创建的对象（除非必要）");
        System.out.println("3. 避免将对象赋值给静态变量");
        System.out.println("4. 在单线程环境使用StringBuilder代替StringBuffer");
        System.out.println("5. 使用 -XX:+DoEscapeAnalysis 确保逃逸分析开启");
    }
}
