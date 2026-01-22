package com.linsir.jdk.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream与ParallelStream性能测试对比
 */
public class StreamPerformanceTest {

    /**
     * 生成测试数据
     */
    private List<Integer> generateTestData(int size) {
        List<Integer> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            data.add(i);
        }
        return data;
    }

    /**
     * 测试CPU密集型操作的性能
     */
    public void testCpuIntensiveOperation() {
        System.out.println("=== CPU密集型操作性能测试 ===");
        
        // 生成测试数据
        List<Integer> data = generateTestData(10000);
        System.out.println("测试数据大小: " + data.size() + " 个元素");
        
        // 测试串行Stream
        long startTime = System.currentTimeMillis();
        long serialCount = data.stream()
                .filter(this::isPrime)
                .count();
        long serialTime = System.currentTimeMillis() - startTime;
        System.out.println("串行Stream: 找到 " + serialCount + " 个质数, 耗时: " + serialTime + "ms");
        
        // 测试并行Stream
        startTime = System.currentTimeMillis();
        long parallelCount = data.parallelStream()
                .filter(this::isPrime)
                .count();
        long parallelTime = System.currentTimeMillis() - startTime;
        System.out.println("并行Stream: 找到 " + parallelCount + " 个质数, 耗时: " + parallelTime + "ms");
        
        // 计算性能提升
        double speedUp = (double) serialTime / parallelTime;
        System.out.println("性能提升: " + String.format("%.2f", speedUp) + "x");
        System.out.println();
    }

    /**
     * 测试大数据集处理的性能
     */
    public void testLargeDataSet() {
        System.out.println("=== 大数据集处理性能测试 ===");
        
        // 生成测试数据
        List<Integer> data = generateTestData(1000000);
        System.out.println("测试数据大小: " + data.size() + " 个元素");
        
        // 测试串行Stream
        long startTime = System.currentTimeMillis();
        List<Integer> serialResult = data.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * 2)
                .collect(Collectors.toList());
        long serialTime = System.currentTimeMillis() - startTime;
        System.out.println("串行Stream: 处理完成, 结果大小: " + serialResult.size() + ", 耗时: " + serialTime + "ms");
        
        // 测试并行Stream
        startTime = System.currentTimeMillis();
        List<Integer> parallelResult = data.parallelStream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * 2)
                .collect(Collectors.toList());
        long parallelTime = System.currentTimeMillis() - startTime;
        System.out.println("并行Stream: 处理完成, 结果大小: " + parallelResult.size() + ", 耗时: " + parallelTime + "ms");
        
        // 计算性能提升
        double speedUp = (double) serialTime / parallelTime;
        System.out.println("性能提升: " + String.format("%.2f", speedUp) + "x");
        System.out.println();
    }

    /**
     * 测试不同数据大小下的性能对比
     */
    public void testDifferentDataSizes() {
        System.out.println("=== 不同数据大小下的性能对比 ===");
        
        int[] sizes = {1000, 10000, 100000, 1000000};
        
        for (int size : sizes) {
            List<Integer> data = generateTestData(size);
            System.out.println("数据大小: " + size);
            
            // 测试串行Stream
            long startTime = System.currentTimeMillis();
            long serialCount = data.stream()
                    .filter(n -> n % 3 == 0 && n % 5 == 0)
                    .count();
            long serialTime = System.currentTimeMillis() - startTime;
            
            // 测试并行Stream
            startTime = System.currentTimeMillis();
            long parallelCount = data.parallelStream()
                    .filter(n -> n % 3 == 0 && n % 5 == 0)
                    .count();
            long parallelTime = System.currentTimeMillis() - startTime;
            
            System.out.println("  串行Stream: 耗时 " + serialTime + "ms");
            System.out.println("  并行Stream: 耗时 " + parallelTime + "ms");
            
            if (serialTime > 0) {
                double speedUp = (double) serialTime / parallelTime;
                System.out.println("  性能提升: " + String.format("%.2f", speedUp) + "x");
            }
            System.out.println();
        }
    }

    /**
     * 测试流式操作链的性能
     */
    public void testStreamOperationsChain() {
        System.out.println("=== 流式操作链性能测试 ===");
        
        // 生成测试数据
        List<Integer> data = generateTestData(100000);
        System.out.println("测试数据大小: " + data.size() + " 个元素");
        
        // 测试串行Stream
        long startTime = System.currentTimeMillis();
        double serialAverage = data.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(n -> n * 2)
                .average()
                .orElse(0);
        long serialTime = System.currentTimeMillis() - startTime;
        System.out.println("串行Stream: 平均值 = " + serialAverage + ", 耗时: " + serialTime + "ms");
        
        // 测试并行Stream
        startTime = System.currentTimeMillis();
        double parallelAverage = data.parallelStream()
                .filter(n -> n % 2 == 0)
                .mapToInt(n -> n * 2)
                .average()
                .orElse(0);
        long parallelTime = System.currentTimeMillis() - startTime;
        System.out.println("并行Stream: 平均值 = " + parallelAverage + ", 耗时: " + parallelTime + "ms");
        
        // 计算性能提升
        double speedUp = (double) serialTime / parallelTime;
        System.out.println("性能提升: " + String.format("%.2f", speedUp) + "x");
        System.out.println();
    }

    /**
     * 判断一个数是否为质数
     */
    private boolean isPrime(int number) {
        if (number <= 1) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }

    /**
     * 运行所有性能测试
     */
    public void runAllTests() {
        System.out.println("=============================================");
        System.out.println("Stream与ParallelStream性能测试对比");
        System.out.println("=============================================");
        
        testCpuIntensiveOperation();
        testLargeDataSet();
        testDifferentDataSizes();
        testStreamOperationsChain();
        
        System.out.println("=============================================");
        System.out.println("性能测试完成");
        System.out.println("=============================================");
    }
}
