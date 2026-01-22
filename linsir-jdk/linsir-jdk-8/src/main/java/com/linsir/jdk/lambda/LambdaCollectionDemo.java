package com.linsir.jdk.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lambda表达式集合操作示例
 */
public class LambdaCollectionDemo {

    /**
     * 遍历集合示例
     */
    public void testForEach() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        System.out.println("遍历集合:");
        names.forEach(name -> System.out.println(name));
    }

    /**
     * 过滤集合示例
     */
    public void testFilter() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        System.out.println("\n过滤集合（长度大于3）:");
        List<String> longNames = names.stream()
                .filter(name -> name.length() > 3)
                .collect(Collectors.toList());
        longNames.forEach(name -> System.out.println(name));
    }

    /**
     * 映射集合示例
     */
    public void testMap() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        System.out.println("\n映射集合（获取长度）:");
        List<Integer> nameLengths = names.stream()
                .map(name -> name.length())
                .collect(Collectors.toList());
        nameLengths.forEach(length -> System.out.println(length));
    }

    /**
     * 排序集合示例
     */
    public void testSort() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        System.out.println("\n排序集合（按长度）:");
        List<String> sortedNames = names.stream()
                .sorted((s1, s2) -> s1.length() - s2.length())
                .collect(Collectors.toList());
        sortedNames.forEach(name -> System.out.println(name));
    }

    /**
     * 聚合操作示例
     */
    public void testReduce() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("\n聚合操作（求和）:");
        int sum = numbers.stream()
                .reduce(0, (a, b) -> a + b);
        System.out.println("Sum: " + sum);
    }

    /**
     * 方法引用示例
     */
    public void testMethodReference() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        System.out.println("\n方法引用示例:");
        // 使用方法引用代替Lambda表达式
        names.forEach(System.out::println);
    }
}
