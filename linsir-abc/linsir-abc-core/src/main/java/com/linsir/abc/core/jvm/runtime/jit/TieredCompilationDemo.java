package com.linsir.abc.core.jvm.runtime.jit;

import java.util.HashMap;
import java.util.Map;

/**
 * 分层编译演示类
 *
 * <p>演示HotSpot虚拟机的分层编译（Tiered Compilation）机制。
 * 分层编译结合了C1和C2编译器的优势，在启动速度和峰值性能之间取得平衡。</p>
 *
 * <p>分层编译层级：</p>
 * <ul>
 *   <li>第0层：解释执行，不开启性能监控</li>
 *   <li>第1层：C1编译，简单优化，不开启性能监控</li>
 *   <li>第2层：C1编译，开启部分性能监控</li>
 *   <li>第3层：C1编译，开启全部性能监控</li>
 *   <li>第4层：C2编译，激进优化</li>
 * </ul>
 *
 * <p>相关JVM参数：</p>
 * <pre>
 * -XX:+TieredCompilation          # 开启分层编译（JDK 8+默认开启）
 * -XX:TieredStopAtLevel=1         # 只使用C1编译（快速启动）
 * -XX:TieredStopAtLevel=4         # 使用全部分层（默认）
 * -XX:-TieredCompilation          # 关闭分层编译，使用传统C2
 * </pre>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see HotSpotDetector
 * @see JITAnalysisDemo
 */
public class TieredCompilationDemo {

    /**
     * 编译层级枚举
     */
    public enum CompilationLevel {
        /**
         * 解释执行
         */
        INTERPRETED(0, "解释执行", "无优化"),
        /**
         * C1简单编译
         */
        C1_SIMPLE(1, "C1简单编译", "基础优化"),
        /**
         * C1有限性能监控
         */
        C1_LIMITED(2, "C1有限监控", "部分性能数据"),
        /**
         * C1完全性能监控
         */
        C1_FULL(3, "C1完全监控", "全部性能数据"),
        /**
         * C2优化编译
         */
        C2_OPTIMIZED(4, "C2优化编译", "激进优化");

        private final int level;
        private final String name;
        private final String description;

        CompilationLevel(int level, String name, String description) {
            this.level = level;
            this.name = name;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 模拟不同编译层级的性能特征
     */
    private static final Map<Integer, Long> LEVEL_OVERHEAD = new HashMap<>();

    static {
        // 模拟各层级的执行开销（相对值，越小越好）
        LEVEL_OVERHEAD.put(0, 100L);  // 解释执行最慢
        LEVEL_OVERHEAD.put(1, 30L);   // C1简单编译较快
        LEVEL_OVERHEAD.put(2, 35L);   // C1有限监控稍慢
        LEVEL_OVERHEAD.put(3, 40L);   // C1完全监控更慢
        LEVEL_OVERHEAD.put(4, 10L);   // C2优化编译最快
    }

    /**
     * 计算密集型任务 - 用于演示编译优化效果
     *
     * @param n 计算规模
     * @return 计算结果
     */
    public static long computeTask(int n) {
        long sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sum += i * j;
            }
        }
        return sum;
    }

    /**
     * 字符串处理任务 - 演示对象创建优化
     *
     * @param iterations 迭代次数
     * @return 处理后的字符串长度
     */
    public static int stringTask(int iterations) {
        int totalLength = 0;
        for (int i = 0; i < iterations; i++) {
            String s = "Item" + i;
            totalLength += s.length();
        }
        return totalLength;
    }

    /**
     * 集合操作任务 - 演示泛型优化
     *
     * @param size 集合大小
     * @return 集合元素和
     */
    public static int collectionTask(int size) {
        Map<Integer, String> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(i, "Value" + i);
        }
        int sum = 0;
        for (Integer key : map.keySet()) {
            sum += key;
        }
        return sum;
    }

    /**
     * 模拟分层编译的执行流程
     *
     * <p>展示方法从解释执行到C2编译的演进过程。</p>
     *
     * @param methodName 方法名称
     * @param callCount 调用次数
     */
    public static void simulateTieredCompilation(String methodName, int callCount) {
        System.out.println("\n模拟方法 '" + methodName + "' 的分层编译过程:");
        System.out.println("调用次数: " + callCount);
        System.out.println();

        int currentLevel = 0;
        int c1Threshold = 1500;   // C1编译阈值
        int c2Threshold = 10000;  // C2编译阈值

        System.out.println("阶段 | 调用次数 | 编译层级 | 执行方式 | 性能特征");
        System.out.println("-----|----------|----------|----------|----------");

        // 阶段1：解释执行
        System.out.printf("%-5s| %-9d| %-9s| %-9s| %-10s%n",
                "1", 0, "Level 0", "解释器", "启动快，执行慢");

        // 阶段2：达到C1阈值
        if (callCount >= c1Threshold) {
            currentLevel = 1;
            System.out.printf("%-5s| %-9d| %-9s| %-9s| %-10s%n",
                    "2", c1Threshold, "Level 1", "C1编译", "快速编译，基础优化");
        }

        // 阶段3：C1完全监控
        if (callCount >= c1Threshold * 2) {
            currentLevel = 3;
            System.out.printf("%-5s| %-9d| %-9s| %-9s| %-10s%n",
                    "3", c1Threshold * 2, "Level 3", "C1+监控", "收集性能数据");
        }

        // 阶段4：C2优化编译
        if (callCount >= c2Threshold) {
            currentLevel = 4;
            System.out.printf("%-5s| %-9d| %-9s| %-9s| %-10s%n",
                    "4", c2Threshold, "Level 4", "C2编译", "激进优化，峰值性能");
        }

        System.out.println();
        System.out.println("最终编译层级: Level " + currentLevel);
        System.out.println("性能提升: 约 " + (LEVEL_OVERHEAD.get(0) / LEVEL_OVERHEAD.get(currentLevel)) + "x");
    }

    /**
     * 演示不同编译策略的性能对比
     *
     * <p>对比解释执行、C1编译、C2编译的性能差异。</p>
     */
    public static void demonstrateCompilationStrategies() {
        System.out.println("\n=== 不同编译策略性能对比 ===\n");

        int iterations = 100000;

        // 策略1：纯解释执行（模拟）
        System.out.println("策略1: 纯解释执行 (-Xint)");
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            computeTask(100);
        }
        long time1 = System.currentTimeMillis() - start1;
        System.out.println("耗时: " + time1 + " ms");
        System.out.println("特点: 启动最快，执行最慢");
        System.out.println();

        // 策略2：纯编译执行（模拟）
        System.out.println("策略2: 纯编译执行 (-Xcomp)");
        long start2 = System.currentTimeMillis();
        // 首次调用即编译，有编译开销
        for (int i = 0; i < iterations; i++) {
            computeTask(100);
        }
        long time2 = System.currentTimeMillis() - start2;
        System.out.println("耗时: " + time2 + " ms");
        System.out.println("特点: 首次调用慢，后续执行快");
        System.out.println();

        // 策略3：分层编译（默认）
        System.out.println("策略3: 分层编译 (-Xmixed，默认)");
        // 先预热
        for (int i = 0; i < 10000; i++) {
            computeTask(100);
        }
        long start3 = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            computeTask(100);
        }
        long time3 = System.currentTimeMillis() - start3;
        System.out.println("耗时: " + time3 + " ms (预热后)");
        System.out.println("特点: 平衡启动速度和峰值性能");
        System.out.println();

        // 性能对比
        System.out.println("性能对比总结:");
        System.out.printf("解释执行 vs 分层编译: %.2fx%n", (double) time1 / time3);
        System.out.println();
    }

    /**
     * 打印分层编译配置信息
     */
    public static void printTieredCompilationConfig() {
        System.out.println("=== 分层编译配置 ===\n");

        System.out.println("编译层级说明:");
        for (CompilationLevel level : CompilationLevel.values()) {
            System.out.printf("  Level %d: %s - %s%n",
                    level.getLevel(), level.getName(), level.getDescription());
        }
        System.out.println();

        System.out.println("关键JVM参数:");
        System.out.println("  -XX:+TieredCompilation       开启分层编译（默认开启）");
        System.out.println("  -XX:TieredStopAtLevel=N      设置最高编译层级（0-4）");
        System.out.println("  -XX:CompileThreshold=10000   方法调用计数器阈值");
        System.out.println("  -XX:+PrintCompilation        打印编译信息");
        System.out.println();

        System.out.println("执行模式参数:");
        System.out.println("  -Xint                        纯解释执行");
        System.out.println("  -Xcomp                       纯编译执行");
        System.out.println("  -Xmixed                      混合模式（默认）");
        System.out.println();
    }

    /**
     * 主方法 - 演示分层编译机制
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== 分层编译（Tiered Compilation）演示 ===");
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println();

        // 打印配置信息
        printTieredCompilationConfig();

        // 模拟分层编译过程
        simulateTieredCompilation("computeTask", 500);
        simulateTieredCompilation("computeTask", 2000);
        simulateTieredCompilation("computeTask", 15000);

        // 演示不同编译策略
        demonstrateCompilationStrategies();

        // 实际性能测试
        System.out.println("=== 实际性能测试 ===\n");

        // 预热
        System.out.println("预热中...");
        for (int i = 0; i < 20000; i++) {
            computeTask(50);
            stringTask(100);
            collectionTask(50);
        }
        System.out.println("预热完成\n");

        // 测试1：计算任务
        System.out.println("测试1: 计算密集型任务");
        long start1 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            computeTask(100);
        }
        long time1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("执行 10000 次 computeTask(100)");
        System.out.println("耗时: " + time1 + " ms");
        System.out.println();

        // 测试2：字符串任务
        System.out.println("测试2: 字符串处理任务");
        long start2 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            stringTask(100);
        }
        long time2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("执行 100000 次 stringTask(100)");
        System.out.println("耗时: " + time2 + " ms");
        System.out.println();

        // 测试3：集合任务
        System.out.println("测试3: 集合操作任务");
        long start3 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            collectionTask(100);
        }
        long time3 = (System.nanoTime() - start3) / 1_000_000;
        System.out.println("执行 10000 次 collectionTask(100)");
        System.out.println("耗时: " + time3 + " ms");
        System.out.println();

        System.out.println("=== 演示完成 ===");
        System.out.println();
        System.out.println("建议:");
        System.out.println("1. 服务端应用：使用默认分层编译（TieredStopAtLevel=4）");
        System.out.println("2. 客户端应用：可考虑 TieredStopAtLevel=1 快速启动");
        System.out.println("3. 使用 -XX:+PrintCompilation 查看实际编译过程");
    }
}
