package com.linsir.jdk.evaluation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 惰性求值和及早求值测试类
 */
public class EvaluationTest {

    public static void main(String[] args) {
        System.out.println("=== 惰性求值和及早求值测试 ===");
        
        // 测试数据
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("测试数据: " + numbers);
        
        // 测试及早求值
        testEagerEvaluation(numbers);
        
        // 测试惰性求值
        testLazyEvaluation(numbers);
        
        // 测试性能对比
        testPerformanceComparison(numbers);
        
        System.out.println("\n=== 测试完成 ===");
    }

    /**
     * 测试及早求值
     */
    private static void testEagerEvaluation(List<Integer> numbers) {
        System.out.println("\n1. 测试及早求值:");
        EagerEvaluationDemo eagerDemo = new EagerEvaluationDemo();
        
        // 测试传统集合操作
        System.out.println("\n1.1 传统集合操作:");
        List<Integer> traditionalResult = eagerDemo.traditionalCollectionOperations(numbers);
        System.out.println("结果: " + traditionalResult);
        
        // 测试Stream API及早求值
        System.out.println("\n1.2 Stream API及早求值:");
        List<Integer> streamEagerResult = eagerDemo.streamEagerEvaluation(numbers);
        System.out.println("结果: " + streamEagerResult);
    }

    /**
     * 测试惰性求值
     */
    private static void testLazyEvaluation(List<Integer> numbers) {
        System.out.println("\n2. 测试惰性求值:");
        LazyEvaluationDemo lazyDemo = new LazyEvaluationDemo();
        
        // 测试自定义惰性求值
        System.out.println("\n2.1 自定义惰性求值:");
        Supplier<Integer> lazyValue = lazyDemo.lazy(() -> {
            System.out.println("执行耗时计算");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 42;
        });
        
        System.out.println("第一次获取值:");
        System.out.println("结果: " + lazyValue.get());
        
        System.out.println("\n第二次获取值:");
        System.out.println("结果: " + lazyValue.get());
        
        // 测试Stream API惰性求值
        System.out.println("\n2.2 Stream API惰性求值:");
        Stream<Integer> lazyStream = lazyDemo.streamLazyEvaluation(numbers);
        
        // 此时才会执行操作
        System.out.println("\n调用终端操作forEach():");
        lazyStream.forEach(value -> System.out.println("结果元素: " + value));
        
        // 测试惰性链式调用
        System.out.println("\n2.3 惰性链式调用:");
        lazyDemo.lazyChaining();
    }

    /**
     * 测试性能对比
     */
    private static void testPerformanceComparison(List<Integer> numbers) {
        System.out.println("\n3. 性能对比测试:");
        EagerEvaluationDemo eagerDemo = new EagerEvaluationDemo();
        LazyEvaluationDemo lazyDemo = new LazyEvaluationDemo();
        
        // 测试及早求值性能
        System.out.println("\n3.1 测试及早求值性能:");
        long eagerStartTime = System.currentTimeMillis();
        long eagerResult = eagerDemo.expensiveCalculationEager(numbers);
        long eagerEndTime = System.currentTimeMillis();
        System.out.println("结果: " + eagerResult);
        System.out.println("执行时间: " + (eagerEndTime - eagerStartTime) + "ms");
        
        // 测试惰性求值性能
        System.out.println("\n3.2 测试惰性求值性能:");
        long lazyCreationTime = System.currentTimeMillis();
        Supplier<Long> lazyCalculation = lazyDemo.lazyExpensiveCalculation(numbers);
        long lazyCreationEndTime = System.currentTimeMillis();
        System.out.println("创建惰性计算时间: " + (lazyCreationEndTime - lazyCreationTime) + "ms");
        
        // 执行惰性计算
        long lazyExecutionStartTime = System.currentTimeMillis();
        long lazyResult = lazyCalculation.get();
        long lazyExecutionEndTime = System.currentTimeMillis();
        System.out.println("结果: " + lazyResult);
        System.out.println("执行时间: " + (lazyExecutionEndTime - lazyExecutionStartTime) + "ms");
        
        // 再次获取结果（使用缓存）
        System.out.println("\n3.3 测试缓存性能:");
        long cachedStartTime = System.currentTimeMillis();
        long cachedResult = lazyCalculation.get();
        long cachedEndTime = System.currentTimeMillis();
        System.out.println("结果: " + cachedResult);
        System.out.println("执行时间: " + (cachedEndTime - cachedStartTime) + "ms");
    }
}
