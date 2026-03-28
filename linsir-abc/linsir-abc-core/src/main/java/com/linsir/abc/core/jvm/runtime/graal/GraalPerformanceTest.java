package com.linsir.abc.core.jvm.runtime.graal;

import java.util.Arrays;

/**
 * Graal编译器性能测试类
 *
 * <p>用于对比Graal编译器和C2编译器的性能差异。
 * 该类包含多种计算密集型任务，可以充分展示不同编译器的优化能力。</p>
 *
 * <p>运行方式：</p>
 * <pre>
 * # 使用C2编译器（默认）
 * java GraalPerformanceTest
 *
 * # 使用Graal编译器
 * java -XX:+UnlockExperimentalVMOptions -XX:+UseJVMCICompiler GraalPerformanceTest
 *
 * # 使用GraalVM Native Image
 * native-image GraalPerformanceTest
 * ./graalperformancetest
 * </pre>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see GraalCompilerDemo
 */
public class GraalPerformanceTest {

    /**
     * 记录程序启动时间
     */
    private static final long START_TIME = System.nanoTime();

    /**
     * 快速排序实现
     *
     * @param arr 数组
     * @param low 起始索引
     * @param high 结束索引
     */
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    /**
     * 素数筛法
     *
     * @param n 上限
     * @return 素数个数
     */
    public static int sieveOfEratosthenes(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;

        for (int i = 2; i * i <= n; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= n; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        int count = 0;
        for (int i = 2; i <= n; i++) {
            if (isPrime[i]) count++;
        }
        return count;
    }

    /**
     * 矩阵乘法
     *
     * @param a 矩阵a
     * @param b 矩阵b
     * @param c 结果矩阵
     * @param size 矩阵大小
     */
    public static void matrixMultiply(double[][] a, double[][] b, double[][] c, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double sum = 0.0;
                for (int k = 0; k < size; k++) {
                    sum += a[i][k] * b[k][j];
                }
                c[i][j] = sum;
            }
        }
    }

    /**
     * 计算圆周率（蒙特卡洛方法）
     *
     * @param iterations 迭代次数
     * @return 圆周率估算值
     */
    public static double calculatePi(int iterations) {
        int insideCircle = 0;
        for (int i = 0; i < iterations; i++) {
            double x = Math.random();
            double y = Math.random();
            if (x * x + y * y <= 1.0) {
                insideCircle++;
            }
        }
        return 4.0 * insideCircle / iterations;
    }

    /**
     * 计算斐波那契数列（迭代）
     *
     * @param n 索引
     * @return 斐波那契数
     */
    public static long fibonacci(int n) {
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
     * 字符串处理
     *
     * @param iterations 迭代次数
     * @return 结果字符串长度
     */
    public static int stringProcessing(int iterations) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("Item").append(i).append("-");
        }
        return sb.toString().length();
    }

    /**
     * 对象创建测试
     *
     * @param iterations 迭代次数
     * @return 对象字段和
     */
    public static long objectCreationTest(int iterations) {
        long sum = 0;
        for (int i = 0; i < iterations; i++) {
            Point p = new Point(i, i * 2);
            sum += p.getX() + p.getY();
        }
        return sum;
    }

    /**
     * 简单Point类
     */
    public static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    /**
     * 测量启动时间
     *
     * @return 启动时间（毫秒）
     */
    public static double getStartupTime() {
        return (System.nanoTime() - START_TIME) / 1_000_000.0;
    }

    /**
     * 运行完整测试套件
     */
    public static void runFullTest() {
        System.out.println("=== Graal编译器性能测试套件 ===\n");

        // 记录启动时间
        double startupTime = getStartupTime();
        System.out.printf("启动时间: %.3f ms%n%n", startupTime);

        // 预热
        System.out.println("预热中...");
        for (int i = 0; i < 1000; i++) {
            fibonacci(30);
            stringProcessing(100);
        }
        System.out.println("预热完成\n");

        long totalTime = 0;

        // 测试1：排序
        System.out.println("测试1: 快速排序");
        int[] arr = new int[100000];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr.length - i;
        }
        long start1 = System.nanoTime();
        quickSort(arr, 0, arr.length - 1);
        long time1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("  数组大小: 100000");
        System.out.println("  耗时: " + time1 + " ms");
        totalTime += time1;
        System.out.println();

        // 测试2：素数计算
        System.out.println("测试2: 素数筛法");
        long start2 = System.nanoTime();
        int primeCount = sieveOfEratosthenes(1000000);
        long time2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("  上限: 1000000");
        System.out.println("  素数个数: " + primeCount);
        System.out.println("  耗时: " + time2 + " ms");
        totalTime += time2;
        System.out.println();

        // 测试3：矩阵乘法
        System.out.println("测试3: 矩阵乘法");
        int size = 300;
        double[][] a = new double[size][size];
        double[][] b = new double[size][size];
        double[][] c = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                a[i][j] = Math.random();
                b[i][j] = Math.random();
            }
        }
        long start3 = System.nanoTime();
        matrixMultiply(a, b, c, size);
        long time3 = (System.nanoTime() - start3) / 1_000_000;
        System.out.println("  矩阵大小: " + size + "x" + size);
        System.out.println("  耗时: " + time3 + " ms");
        totalTime += time3;
        System.out.println();

        // 测试4：蒙特卡洛计算PI
        System.out.println("测试4: 蒙特卡洛计算PI");
        long start4 = System.nanoTime();
        double pi = calculatePi(10000000);
        long time4 = (System.nanoTime() - start4) / 1_000_000;
        System.out.println("  迭代次数: 10000000");
        System.out.printf("  PI估算值: %.6f%n", pi);
        System.out.println("  耗时: " + time4 + " ms");
        totalTime += time4;
        System.out.println();

        // 测试5：斐波那契
        System.out.println("测试5: 斐波那契数列");
        long start5 = System.nanoTime();
        long fibResult = 0;
        for (int i = 0; i < 100000; i++) {
            fibResult += fibonacci(40);
        }
        long time5 = (System.nanoTime() - start5) / 1_000_000;
        System.out.println("  迭代次数: 100000");
        System.out.println("  结果: " + fibResult);
        System.out.println("  耗时: " + time5 + " ms");
        totalTime += time5;
        System.out.println();

        // 测试6：字符串处理
        System.out.println("测试6: 字符串处理");
        long start6 = System.nanoTime();
        int strLength = stringProcessing(100000);
        long time6 = (System.nanoTime() - start6) / 1_000_000;
        System.out.println("  迭代次数: 100000");
        System.out.println("  结果长度: " + strLength);
        System.out.println("  耗时: " + time6 + " ms");
        totalTime += time6;
        System.out.println();

        // 测试7：对象创建
        System.out.println("测试7: 对象创建");
        long start7 = System.nanoTime();
        long objSum = objectCreationTest(1000000);
        long time7 = (System.nanoTime() - start7) / 1_000_000;
        System.out.println("  创建对象数: 1000000");
        System.out.println("  结果: " + objSum);
        System.out.println("  耗时: " + time7 + " ms");
        totalTime += time7;
        System.out.println();

        // 总结果
        System.out.println("=== 测试结果汇总 ===");
        System.out.println("总耗时: " + totalTime + " ms");
        System.out.println();

        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        System.out.println("内存使用:");
        System.out.println("  总内存: " + (runtime.totalMemory() / 1024 / 1024) + " MB");
        System.out.println("  空闲内存: " + (runtime.freeMemory() / 1024 / 1024) + " MB");
        System.out.println("  使用内存: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024) + " MB");
    }

    /**
     * 快速测试模式
     */
    public static void quickTest() {
        System.out.println("=== 快速测试模式 ===\n");

        double startupTime = getStartupTime();
        System.out.printf("启动时间: %.3f ms%n%n", startupTime);

        // 简单的计算任务
        long start = System.nanoTime();
        long sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += fibonacci(30);
        }
        long time = (System.nanoTime() - start) / 1_000_000;

        System.out.println("计算结果: " + sum);
        System.out.println("计算耗时: " + time + " ms");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("Graal编译器性能测试");
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println();

        if (args.length > 0 && args[0].equals("--quick")) {
            quickTest();
        } else {
            runFullTest();
        }

        System.out.println();
        System.out.println("测试完成。");
        System.out.println("提示: 使用不同编译器运行以对比性能差异");
    }
}
