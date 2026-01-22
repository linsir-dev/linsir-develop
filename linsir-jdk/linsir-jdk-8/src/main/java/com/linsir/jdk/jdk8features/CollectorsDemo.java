package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Collectors常规用法
 */
public class CollectorsDemo {

    public void demonstrate() {
        System.out.println("\n=== Collectors常规用法 ===");
        
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Alice", "Bob");
        
        // 收集到List
        System.out.println("收集到List:");
        List<String> nameList = names.stream()
                .collect(Collectors.toList());
        System.out.println("List: " + nameList);
        
        // 收集到Set（去重）
        System.out.println("\n收集到Set（去重）:");
        Set<String> nameSet = names.stream()
                .collect(Collectors.toSet());
        System.out.println("Set: " + nameSet);
        
        // 连接字符串
        System.out.println("\n连接字符串:");
        String joinedNames = names.stream()
                .collect(Collectors.joining(", "));
        System.out.println("连接结果: " + joinedNames);
        
        // 分组
        System.out.println("\n分组:");
        Map<String, List<String>> groupedNames = names.stream()
                .collect(Collectors.groupingBy(name -> name));
        System.out.println("分组结果: " + groupedNames);
        
        // 统计
        System.out.println("\n统计:");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        java.util.IntSummaryStatistics stats = numbers.stream()
                .collect(Collectors.summarizingInt(Integer::intValue));
        System.out.println("统计结果: " + stats);
    }
}
