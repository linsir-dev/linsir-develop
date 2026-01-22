package com.linsir.jdk.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 惰性求值(Lazy Evaluation)演示
 * 惰性求值会推迟执行操作，直到真正需要结果时才执行
 */
public class LazyEvaluationDemo {

    /**
     * 自定义惰性求值容器
     */
    public <T> Supplier<T> lazy(Supplier<T> supplier) {
        return new Supplier<T>() {
            private boolean evaluated = false;
            private T result;

            @Override
            public T get() {
                if (!evaluated) {
                    System.out.println("执行惰性计算");
                    result = supplier.get();
                    evaluated = true;
                } else {
                    System.out.println("使用缓存结果");
                }
                return result;
            }
        };
    }

    /**
     * Stream API 惰性求值
     */
    public Stream<Integer> streamLazyEvaluation(List<Integer> numbers) {
        System.out.println("\n创建Stream（惰性求值）");
        
        // 此时不会执行任何操作，只是创建Stream管道
        Stream<Integer> stream = numbers.stream()
                .peek(number -> System.out.println("处理元素: " + number))
                .filter(number -> number > 3)
                .map(number -> number * 2);
        
        System.out.println("Stream创建完成，但尚未执行任何操作");
        return stream;
    }

    /**
     * 惰性计算密集型操作
     */
    public Supplier<Long> lazyExpensiveCalculation(List<Integer> numbers) {
        System.out.println("\n创建惰性计算（计算密集型操作）");
        
        // 创建惰性计算，不会立即执行
        return () -> {
            System.out.println("开始执行计算密集型操作");
            long sum = 0;
            for (Integer number : numbers) {
                System.out.println("计算元素: " + number);
                // 模拟计算密集型操作
                sum += expensiveOperation(number);
            }
            System.out.println("计算密集型操作完成");
            return sum;
        };
    }

    /**
     * 模拟计算密集型操作
     */
    private int expensiveOperation(int number) {
        // 模拟耗时操作
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return number * number;
    }

    /**
     * 惰性链式调用
     */
    public void lazyChaining() {
        System.out.println("\n演示惰性链式调用");
        
        // 创建一个包含多个操作的Stream，但不执行
        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5)
                .peek(n -> System.out.println("第一步: " + n))
                .map(n -> n * 2)
                .peek(n -> System.out.println("第二步: " + n))
                .filter(n -> n > 5)
                .peek(n -> System.out.println("第三步: " + n));
        
        System.out.println("Stream创建完成，操作尚未执行");
        
        // 当调用终端操作时，才会执行所有操作
        System.out.println("\n调用终端操作，开始执行所有操作");
        long count = stream.count();
        System.out.println("操作执行完成，结果: " + count);
    }
}
