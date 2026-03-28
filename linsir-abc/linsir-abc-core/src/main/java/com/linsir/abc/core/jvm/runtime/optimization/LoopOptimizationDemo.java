package com.linsir.abc.core.jvm.runtime.optimization;

/**
 * 循环优化（Loop Optimization）演示类
 *
 * <p>循环优化是JIT编译器的重要优化技术，包括：</p>
 * <ul>
 *   <li>循环展开（Loop Unrolling）：减少循环控制开销</li>
 *   <li>循环不变量外提（Loop Invariant Code Motion）：将不变计算移到循环外</li>
 *   <li>数组边界检查消除（Bounds Check Elimination）：消除不必要的边界检查</li>
 *   <li>向量化（Vectorization）：使用SIMD指令并行处理</li>
 * </ul>
 *
 * <p>相关JVM参数：</p>
 * <pre>
 * -XX:+UseLoopPredicate          # 循环优化（默认开启）
 * -XX:+RangeCheckElimination     # 范围检查消除（默认开启）
 * -XX:+UseSuperWord              # 向量化优化（默认开启）
 * -XX:LoopUnrollLimit=60         # 循环展开阈值
 * </pre>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see MethodInliningDemo
 * @see EscapeAnalysisDemo
 */
public class LoopOptimizationDemo {

    /**
     * 基础循环 - 未优化版本
     *
     * @param arr 数组
     * @return 元素和
     */
    public static int basicLoop(int[] arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];  // 每次都要检查边界
        }
        return sum;
    }

    /**
     * 优化版本 - 手动循环展开
     *
     * <p>JIT编译器会自动进行循环展开优化。</p>
     *
     * @param arr 数组
     * @return 元素和
     */
    public static int unrolledLoop(int[] arr) {
        int sum = 0;
        int len = arr.length;

        // 每次处理4个元素
        int i = 0;
        for (; i <= len - 4; i += 4) {
            sum += arr[i];
            sum += arr[i + 1];
            sum += arr[i + 2];
            sum += arr[i + 3];
        }

        // 处理剩余元素
        for (; i < len; i++) {
            sum += arr[i];
        }

        return sum;
    }

    /**
     * 循环不变量外提示例 - 优化前
     *
     * <p>array.length在循环中不变，应该移到循环外。</p>
     *
     * @param array 数组
     * @return 处理结果
     */
    public static int loopInvariantBefore(int[] array) {
        int result = 0;
        for (int i = 0; i < 1000; i++) {
            // array.length 是循环不变量
            result += array.length * i;
        }
        return result;
    }

    /**
     * 循环不变量外提示例 - 优化后（概念展示）
     *
     * <p>JIT编译器会自动进行此优化。</p>
     *
     * @param array 数组
     * @return 处理结果
     */
    public static int loopInvariantAfter(int[] array) {
        int result = 0;
        int len = array.length;  // 外提不变量
        for (int i = 0; i < 1000; i++) {
            result += len * i;
        }
        return result;
    }

    /**
     * 数组边界检查消除示例
     *
     * <p>当编译器能证明数组访问不会越界时，可以消除边界检查。</p>
     *
     * @param arr 数组
     * @return 元素和
     */
    public static int boundsCheckElimination(int[] arr) {
        int sum = 0;
        // 编译器可以证明 i < arr.length，消除边界检查
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];  // 无需检查边界
        }
        return sum;
    }

    /**
     * 无法消除边界检查的示例
     *
     * @param arr 数组
     * @param index 索引
     * @return 元素值
     */
    public static int noBoundsCheckElimination(int[] arr, int index) {
        // 编译器无法确定index是否越界
        return arr[index];  // 必须检查边界
    }

    /**
     * 向量化优化示例
     *
     * <p>C2编译器可以将循环转换为SIMD指令。</p>
     *
     * @param a 数组a
     * @param b 数组b
     * @param c 结果数组
     */
    public static void vectorizedLoop(int[] a, int[] b, int[] c) {
        // 编译器可能使用SIMD指令并行处理4个元素
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] + b[i];
        }
    }

    /**
     * 矩阵乘法 - 计算密集型循环
     *
     * @param a 矩阵a
     * @param b 矩阵b
     * @param c 结果矩阵
     * @param size 矩阵大小
     */
    public static void matrixMultiply(int[][] a, int[][] b, int[][] c, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int sum = 0;
                for (int k = 0; k < size; k++) {
                    sum += a[i][k] * b[k][j];
                }
                c[i][j] = sum;
            }
        }
    }

    /**
     * 优化的矩阵乘法 - 缓存友好
     *
     * <p>调整循环顺序提高缓存命中率。</p>
     *
     * @param a 矩阵a
     * @param b 矩阵b
     * @param c 结果矩阵
     * @param size 矩阵大小
     */
    public static void optimizedMatrixMultiply(int[][] a, int[][] b, int[][] c, int size) {
        // 初始化结果矩阵
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                c[i][j] = 0;
            }
        }

        // i-k-j顺序提高缓存命中率
        for (int i = 0; i < size; i++) {
            for (int k = 0; k < size; k++) {
                int aik = a[i][k];
                for (int j = 0; j < size; j++) {
                    c[i][j] += aik * b[k][j];
                }
            }
        }
    }

    /**
     * 测试循环展开性能
     *
     * @param iterations 迭代次数
     * @return 计算结果
     */
    public static long testLoopUnrolling(int iterations) {
        long sum = 0;
        // JIT编译器会自动展开此循环
        for (int i = 0; i < iterations; i++) {
            sum += i;
        }
        return sum;
    }

    /**
     * 测试数组求和性能
     *
     * @param arr 数组
     * @param iterations 迭代次数
     * @return 计算结果
     */
    public static long testArraySum(int[] arr, int iterations) {
        long sum = 0;
        for (int n = 0; n < iterations; n++) {
            // 边界检查会被消除
            for (int i = 0; i < arr.length; i++) {
                sum += arr[i];
            }
        }
        return sum;
    }

    /**
     * 测试向量化性能
     *
     * @param size 数组大小
     * @param iterations 迭代次数
     * @return 计算结果
     */
    public static long testVectorization(int size, int iterations) {
        int[] a = new int[size];
        int[] b = new int[size];
        int[] c = new int[size];

        // 初始化
        for (int i = 0; i < size; i++) {
            a[i] = i;
            b[i] = i * 2;
        }

        long sum = 0;
        for (int n = 0; n < iterations; n++) {
            // 向量化优化
            for (int i = 0; i < size; i++) {
                c[i] = a[i] + b[i];
            }

            for (int value : c) {
                sum += value;
            }
        }

        return sum;
    }

    /**
     * 性能对比测试
     */
    public static void performanceComparison() {
        System.out.println("=== 循环优化性能对比 ===\n");

        int[] arr = new int[10000];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }

        // 预热
        System.out.println("预热中...");
        for (int i = 0; i < 1000; i++) {
            basicLoop(arr);
            unrolledLoop(arr);
            testLoopUnrolling(1000);
        }
        System.out.println("预热完成\n");

        // 测试1：基础循环 vs 展开循环
        System.out.println("测试1: 基础循环 vs 手动展开");
        int iterations = 10000;

        long start1 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            basicLoop(arr);
        }
        long time1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("  基础循环: " + time1 + " ms");

        long start2 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            unrolledLoop(arr);
        }
        long time2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("  手动展开: " + time2 + " ms");
        System.out.println("  性能比: " + String.format("%.2f", (double) time1 / time2) + "x");
        System.out.println();

        // 测试2：数组求和
        System.out.println("测试2: 数组求和（边界检查消除）");
        long start3 = System.nanoTime();
        long result3 = testArraySum(arr, 1000);
        long time3 = (System.nanoTime() - start3) / 1_000_000;
        System.out.println("  结果: " + result3);
        System.out.println("  耗时: " + time3 + " ms");
        System.out.println();

        // 测试3：向量化
        System.out.println("测试3: 向量化优化");
        long start4 = System.nanoTime();
        long result4 = testVectorization(1000, 1000);
        long time4 = (System.nanoTime() - start4) / 1_000_000;
        System.out.println("  结果: " + result4);
        System.out.println("  耗时: " + time4 + " ms");
        System.out.println();

        // 测试4：矩阵乘法
        System.out.println("测试4: 矩阵乘法优化");
        int size = 200;
        int[][] a = new int[size][size];
        int[][] b = new int[size][size];
        int[][] c1 = new int[size][size];
        int[][] c2 = new int[size][size];

        // 初始化
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                a[i][j] = i + j;
                b[i][j] = i - j;
            }
        }

        long start5 = System.nanoTime();
        matrixMultiply(a, b, c1, size);
        long time5 = (System.nanoTime() - start5) / 1_000_000;
        System.out.println("  基础版本: " + time5 + " ms");

        long start6 = System.nanoTime();
        optimizedMatrixMultiply(a, b, c2, size);
        long time6 = (System.nanoTime() - start6) / 1_000_000;
        System.out.println("  优化版本: " + time6 + " ms");
        System.out.println("  性能比: " + String.format("%.2f", (double) time5 / time6) + "x");
        System.out.println();

        System.out.println("性能对比总结:");
        System.out.println("- 循环展开减少控制开销");
        System.out.println("- 边界检查消除减少运行时检查");
        System.out.println("- 向量化利用SIMD指令并行处理");
        System.out.println("- 缓存友好的循环顺序提高性能");
    }

    /**
     * 演示循环优化技术
     */
    public static void demonstrateOptimizations() {
        System.out.println("=== 循环优化技术演示 ===\n");

        // 循环展开
        System.out.println("1. 循环展开（Loop Unrolling）");
        System.out.println("   优化前:");
        System.out.println("     for (int i = 0; i < 100; i++) { sum += arr[i]; }");
        System.out.println("   优化后（概念）:");
        System.out.println("     for (int i = 0; i < 100; i += 4) {");
        System.out.println("       sum += arr[i];");
        System.out.println("       sum += arr[i+1];");
        System.out.println("       sum += arr[i+2];");
        System.out.println("       sum += arr[i+3];");
        System.out.println("     }");
        System.out.println("   效果: 减少循环控制开销");
        System.out.println();

        // 循环不变量外提
        System.out.println("2. 循环不变量外提（Loop Invariant Code Motion）");
        System.out.println("   优化前:");
        System.out.println("     for (int i = 0; i < 100; i++) { sum += arr.length * i; }");
        System.out.println("   优化后:");
        System.out.println("     int len = arr.length;");
        System.out.println("     for (int i = 0; i < 100; i++) { sum += len * i; }");
        System.out.println("   效果: 避免重复计算不变量");
        System.out.println();

        // 边界检查消除
        System.out.println("3. 数组边界检查消除（Bounds Check Elimination）");
        System.out.println("   代码:");
        System.out.println("     for (int i = 0; i < arr.length; i++) { sum += arr[i]; }");
        System.out.println("   分析: 编译器证明 i 始终小于 arr.length");
        System.out.println("   效果: 消除每次访问的边界检查");
        System.out.println();

        // 向量化
        System.out.println("4. 向量化（Vectorization）");
        System.out.println("   代码:");
        System.out.println("     for (int i = 0; i < n; i++) { c[i] = a[i] + b[i]; }");
        System.out.println("   优化: 使用SIMD指令同时处理4个元素");
        System.out.println("   效果: 利用CPU并行计算能力");
        System.out.println();
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== 循环优化（Loop Optimization）演示 ===");
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println();

        // 打印循环优化相关信息
        System.out.println("循环优化相关参数:");
        System.out.println("  -XX:+UseLoopPredicate       循环优化（默认开启）");
        System.out.println("  -XX:+RangeCheckElimination  范围检查消除（默认开启）");
        System.out.println("  -XX:+UseSuperWord           向量化优化（默认开启）");
        System.out.println("  -XX:LoopUnrollLimit=60      循环展开阈值");
        System.out.println();

        // 演示优化技术
        demonstrateOptimizations();

        // 性能对比
        performanceComparison();

        System.out.println("=== 演示完成 ===");
        System.out.println();
        System.out.println("优化建议:");
        System.out.println("1. 使用增强for循环（for-each）便于优化");
        System.out.println("2. 避免在循环中修改数组长度");
        System.out.println("3. 将循环不变量移到循环外");
        System.out.println("4. 使用局部变量缓存数组长度");
        System.out.println("5. 考虑数据访问模式，提高缓存命中率");
    }
}
