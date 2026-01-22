package com.linsir.jdk.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 及早求值(Eager Evaluation)演示
 * 及早求值会立即执行操作并返回结果
 */
public class EagerEvaluationDemo {

    /**
     * 传统集合操作 - 及早求值
     */
    public List<Integer> traditionalCollectionOperations(List<Integer> numbers) {
        System.out.println("开始传统集合操作（及早求值）");
        
        // 创建新集合存储结果
        List<Integer> result = new ArrayList<>();
        
        // 遍历所有元素
        for (Integer number : numbers) {
            System.out.println("处理元素: " + number);
            // 过滤条件
            if (number > 3) {
                // 转换操作
                int transformed = number * 2;
                // 添加到结果集
                result.add(transformed);
            }
        }
        
        System.out.println("传统集合操作完成");
        return result;
    }

    /**
     * Stream API 及早求值
     */
    public List<Integer> streamEagerEvaluation(List<Integer> numbers) {
        System.out.println("\n开始Stream API及早求值");
        
        // 使用collect()方法触发及早求值
        List<Integer> result = numbers.stream()
                .peek(number -> System.out.println("处理元素: " + number))
                .filter(number -> number > 3)
                .map(number -> number * 2)
                .collect(Collectors.toList());
        
        System.out.println("Stream API及早求值完成");
        return result;
    }

    /**
     * 计算密集型操作 - 及早求值
     */
    public long expensiveCalculationEager(List<Integer> numbers) {
        System.out.println("\n开始计算密集型操作（及早求值）");
        
        // 立即执行所有计算
        long sum = 0;
        for (Integer number : numbers) {
            System.out.println("计算元素: " + number);
            // 模拟计算密集型操作
            sum += expensiveOperation(number);
        }
        
        System.out.println("计算密集型操作完成");
        return sum;
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
}
