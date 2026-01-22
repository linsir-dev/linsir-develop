package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Map&Reduce常规用法
 */
public class MapReduceDemo {

    public void demonstrate() {
        System.out.println("\n=== Map&Reduce常规用法 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // 使用map转换
        System.out.println("使用map转换:");
        List<Integer> squaredNumbers = numbers.stream()
                .map(n -> n * n)
                .collect(Collectors.toList());
        System.out.println("平方值: " + squaredNumbers);
        
        // 使用reduce求和
        System.out.println("\n使用reduce求和:");
        int sum = numbers.stream()
                .reduce(0, (a, b) -> a + b);
        System.out.println("总和: " + sum);
        
        // 使用reduce求积
        System.out.println("\n使用reduce求积:");
        int product = numbers.stream()
                .reduce(1, (a, b) -> a * b);
        System.out.println("乘积: " + product);
        
        // 使用reduce求最大值
        System.out.println("\n使用reduce求最大值:");
        Optional<Integer> max = numbers.stream()
                .reduce(Integer::max);
        System.out.println("最大值: " + max.orElse(0));
    }
}
