package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * distinct常规用法
 */
public class DistinctDemo {

    public void demonstrate() {
        System.out.println("\n=== distinct常规用法 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        
        // 使用distinct去重
        System.out.println("使用distinct去重:");
        List<Integer> distinctNumbers = numbers.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("去重前: " + numbers);
        System.out.println("去重后: " + distinctNumbers);
        
        // 使用distinct处理字符串
        System.out.println("\n使用distinct处理字符串:");
        List<String> names = Arrays.asList("Alice", "Bob", "Alice", "Charlie", "Bob");
        List<String> distinctNames = names.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("去重前: " + names);
        System.out.println("去重后: " + distinctNames);
    }
}
