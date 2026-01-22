package com.linsir.jdk.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stream两种模式（串行和并行）和parallelStream的功能演示
 */
public class StreamParallelDemo {

    /**
     * 串行Stream演示
     */
    public void sequentialStreamDemo() {
        System.out.println("=== 串行Stream演示 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 创建串行Stream
        Stream<Integer> sequentialStream = numbers.stream();
        
        // 执行操作
        List<Integer> result = sequentialStream
                .filter(n -> {
                    System.out.println("过滤: " + n + " (线程: " + Thread.currentThread().getName() + ")");
                    return n % 2 == 0;
                })
                .map(n -> {
                    System.out.println("映射: " + n + " (线程: " + Thread.currentThread().getName() + ")");
                    return n * 2;
                })
                .collect(Collectors.toList());
        
        System.out.println("串行Stream结果: " + result);
    }

    /**
     * 并行Stream演示（使用parallel()方法）
     */
    public void parallelStreamDemo() {
        System.out.println("\n=== 并行Stream演示（使用parallel()方法） ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 创建并行Stream
        Stream<Integer> parallelStream = numbers.stream().parallel();
        
        // 执行操作
        List<Integer> result = parallelStream
                .filter(n -> {
                    System.out.println("过滤: " + n + " (线程: " + Thread.currentThread().getName() + ")");
                    return n % 2 == 0;
                })
                .map(n -> {
                    System.out.println("映射: " + n + " (线程: " + Thread.currentThread().getName() + ")");
                    return n * 2;
                })
                .collect(Collectors.toList());
        
        System.out.println("并行Stream结果: " + result);
    }

    /**
     * parallelStream()方法演示
     */
    public void parallelStreamMethodDemo() {
        System.out.println("\n=== parallelStream()方法演示 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 使用parallelStream()方法
        List<Integer> result = numbers.parallelStream()
                .filter(n -> {
                    System.out.println("过滤: " + n + " (线程: " + Thread.currentThread().getName() + ")");
                    return n % 2 == 0;
                })
                .map(n -> {
                    System.out.println("映射: " + n + " (线程: " + Thread.currentThread().getName() + ")");
                    return n * 2;
                })
                .collect(Collectors.toList());
        
        System.out.println("parallelStream()结果: " + result);
    }

    /**
     * 并行Stream的无序性演示
     */
    public void parallelStreamUnorderedDemo() {
        System.out.println("\n=== 并行Stream的无序性演示 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        System.out.println("原始顺序: " + numbers);
        
        // 串行Stream保持顺序
        System.out.print("串行Stream处理顺序: ");
        numbers.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // 并行Stream可能无序
        System.out.print("并行Stream处理顺序: ");
        numbers.parallelStream().forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // 使用forEachOrdered保持顺序
        System.out.print("并行Stream使用forEachOrdered: ");
        numbers.parallelStream().forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();
    }

    /**
     * 并行Stream的使用场景演示
     */
    public void parallelStreamUseCases() {
        System.out.println("\n=== 并行Stream的使用场景演示 ===");
        
        // 场景1: CPU密集型操作
        System.out.println("场景1: CPU密集型操作（计算质数）");
        List<Integer> largeNumbers = Arrays.asList(1000000, 2000000, 3000000, 4000000, 5000000);
        
        largeNumbers.parallelStream()
                .forEach(n -> {
                    boolean isPrime = isPrime(n);
                    System.out.println(n + " 是质数: " + isPrime + " (线程: " + Thread.currentThread().getName() + ")");
                });
        
        // 场景2: 大数据集处理
        System.out.println("\n场景2: 大数据集处理");
        List<Integer> bigData = Stream.iterate(1, n -> n + 1).limit(10000).collect(Collectors.toList());
        
        long count = bigData.parallelStream()
                .filter(n -> n % 3 == 0 && n % 5 == 0)
                .count();
        
        System.out.println("1-10000中能被3和5同时整除的数的个数: " + count);
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
}
