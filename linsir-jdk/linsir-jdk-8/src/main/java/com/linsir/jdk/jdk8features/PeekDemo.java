package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * peek示例
 */
public class PeekDemo {

    public void demonstrate() {
        System.out.println("\n=== peek ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // 使用peek调试
        System.out.println("使用peek调试:");
        List<Integer> result = numbers.stream()
                .peek(n -> System.out.println("原始值: " + n))
                .map(n -> n * 2)
                .peek(n -> System.out.println("映射后: " + n))
                .filter(n -> n > 5)
                .peek(n -> System.out.println("过滤后: " + n))
                .collect(Collectors.toList());
        System.out.println("最终结果: " + result);
    }
}
