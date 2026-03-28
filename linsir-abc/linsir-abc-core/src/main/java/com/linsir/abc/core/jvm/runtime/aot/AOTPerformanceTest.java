package com.linsir.abc.core.jvm.runtime.aot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AOT编译性能测试类
 *
 * <p>用于对比AOT编译和JIT编译的性能差异。
 * 该类设计为可被jaotc工具编译为本地共享库。</p>
 *
 * <p>测试场景：</p>
 * <ul>
 *   <li>启动时间测试 - AOT优势最明显的场景</li>
 *   <li>计算密集型任务 - 对比峰值性能</li>
 *   <li>内存分配测试 - 对比内存占用</li>
 * </ul>
 *
 * <p>编译和运行：</p>
 * <pre>
 * # 编译为AOT库
 * jaotc --output libAOTPerformanceTest.so AOTPerformanceTest.class
 *
 * # 使用AOT库运行
 * java -XX:AOTLibrary=./libAOTPerformanceTest.so AOTPerformanceTest
 *
 * # 对比：不使用AOT库运行
 * java AOTPerformanceTest
 * </pre>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see AOTCompilationDemo
 */
public class AOTPerformanceTest {

    /**
     * 记录程序启动时间
     */
    private static final long START_TIME = System.currentTimeMillis();

    /**
     * 计算密集型任务 - 矩阵乘法
     *
     * @param size 矩阵大小
     * @return 计算结果
     */
    public static long matrixMultiplication(int size) {
        long result = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result += i * j * k;
                }
            }
        }
        return result;
    }

    /**
     * 计算密集型任务 - 素数计算
     *
     * @param limit 上限
     * @return 素数个数
     */
    public static int countPrimes(int limit) {
        int count = 0;
        for (int i = 2; i <= limit; i++) {
            if (isPrime(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 判断是否为素数
     *
     * @param n 待判断的数
     * @return 是否为素数
     */
    private static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    /**
     * 字符串处理任务
     *
     * @param iterations 迭代次数
     * @return 处理结果
     */
    public static String stringProcessing(int iterations) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            sb.append("String").append(i).append("-");
        }
        return sb.toString();
    }

    /**
     * 集合操作任务
     *
     * @param size 集合大小
     * @return 集合元素和
     */
    public static long collectionOperations(int size) {
        Map<Integer, List<String>> map = new HashMap<>();

        // 填充数据
        for (int i = 0; i < size; i++) {
            List<String> list = new ArrayList<>();
            list.add("Value" + i);
            map.put(i, list);
        }

        // 计算总和
        long sum = 0;
        for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
            sum += entry.getKey();
        }
        return sum;
    }

    /**
     * 排序任务
     *
     * @param size 数组大小
     * @return 排序后的数组和
     */
    public static long sortingTask(int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = size - i; // 逆序填充
        }

        // 冒泡排序
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }

        // 计算和
        long sum = 0;
        for (int value : arr) {
            sum += value;
        }
        return sum;
    }

    /**
     * 递归任务 - 斐波那契
     *
     * @param n 斐波那契索引
     * @return 斐波那契数
     */
    public static long recursiveFibonacci(int n) {
        if (n <= 1) return n;
        return recursiveFibonacci(n - 1) + recursiveFibonacci(n - 2);
    }

    /**
     * 测量启动时间
     *
     * @return 从类加载到当前时间的毫秒数
     */
    public static long measureStartupTime() {
        return System.currentTimeMillis() - START_TIME;
    }

    /**
     * 运行完整性能测试
     *
     * <p>执行所有测试用例并输出性能报告。</p>
     */
    public static void runFullBenchmark() {
        System.out.println("=== AOT性能测试 ===\n");

        // 记录启动时间
        long startupTime = measureStartupTime();
        System.out.println("启动时间: " + startupTime + " ms");
        System.out.println();

        // 测试1：矩阵乘法
        System.out.println("测试1: 矩阵乘法");
        long start1 = System.nanoTime();
        long result1 = matrixMultiplication(200);
        long time1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("  结果: " + result1);
        System.out.println("  耗时: " + time1 + " ms");
        System.out.println();

        // 测试2：素数计算
        System.out.println("测试2: 素数计算");
        long start2 = System.nanoTime();
        int result2 = countPrimes(100000);
        long time2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("  结果: " + result2 + " 个素数");
        System.out.println("  耗时: " + time2 + " ms");
        System.out.println();

        // 测试3：字符串处理
        System.out.println("测试3: 字符串处理");
        long start3 = System.nanoTime();
        String result3 = stringProcessing(10000);
        long time3 = (System.nanoTime() - start3) / 1_000_000;
        System.out.println("  结果长度: " + result3.length());
        System.out.println("  耗时: " + time3 + " ms");
        System.out.println();

        // 测试4：集合操作
        System.out.println("测试4: 集合操作");
        long start4 = System.nanoTime();
        long result4 = collectionOperations(10000);
        long time4 = (System.nanoTime() - start4) / 1_000_000;
        System.out.println("  结果: " + result4);
        System.out.println("  耗时: " + time4 + " ms");
        System.out.println();

        // 测试5：排序任务
        System.out.println("测试5: 排序任务");
        long start5 = System.nanoTime();
        long result5 = sortingTask(5000);
        long time5 = (System.nanoTime() - start5) / 1_000_000;
        System.out.println("  结果: " + result5);
        System.out.println("  耗时: " + time5 + " ms");
        System.out.println();

        // 测试6：递归任务（小数值避免栈溢出）
        System.out.println("测试6: 递归斐波那契");
        long start6 = System.nanoTime();
        long result6 = recursiveFibonacci(35);
        long time6 = (System.nanoTime() - start6) / 1_000_000;
        System.out.println("  结果: " + result6);
        System.out.println("  耗时: " + time6 + " ms");
        System.out.println();

        // 总耗时
        long totalTime = time1 + time2 + time3 + time4 + time5 + time6;
        System.out.println("总耗时: " + totalTime + " ms");
        System.out.println();

        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        System.out.println("内存使用:");
        System.out.println("  总内存: " + totalMemory + " MB");
        System.out.println("  已用内存: " + usedMemory + " MB");
        System.out.println("  空闲内存: " + freeMemory + " MB");
    }

    /**
     * 快速启动测试
     *
     * <p>模拟Serverless场景下的快速启动需求。</p>
     */
    public static void quickStartTest() {
        System.out.println("=== 快速启动测试 ===\n");

        // 模拟处理请求
        long start = System.currentTimeMillis();

        // 简单的业务逻辑
        int result = countPrimes(10000);
        String str = stringProcessing(1000);

        long end = System.currentTimeMillis();

        System.out.println("处理结果:");
        System.out.println("  素数个数: " + result);
        System.out.println("  字符串长度: " + str.length());
        System.out.println("  处理耗时: " + (end - start) + " ms");
        System.out.println();

        System.out.println("在Serverless场景下，启动时间至关重要。");
        System.out.println("AOT编译可以显著减少冷启动时间。");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("AOT性能测试程序");
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println();

        if (args.length > 0 && args[0].equals("--quick")) {
            quickStartTest();
        } else {
            runFullBenchmark();
        }

        System.out.println();
        System.out.println("提示：使用 jaotc 编译后重新运行以对比性能差异");
    }
}
