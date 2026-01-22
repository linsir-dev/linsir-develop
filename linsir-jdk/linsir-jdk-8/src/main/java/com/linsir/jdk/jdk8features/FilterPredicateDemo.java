package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Filter & Predicate常规用法
 */
public class FilterPredicateDemo {

    public void demonstrate() {
        System.out.println("\n=== Filter & Predicate常规用法 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 使用filter过滤
        System.out.println("使用filter过滤偶数:");
        List<Integer> evenNumbers = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("偶数: " + evenNumbers);
        
        // 使用Predicate接口
        System.out.println("\n使用Predicate接口:");
        Predicate<Integer> isOdd = n -> n % 2 != 0;
        List<Integer> oddNumbers = numbers.stream()
                .filter(isOdd)
                .collect(Collectors.toList());
        System.out.println("奇数: " + oddNumbers);
        
        // 组合Predicate
        System.out.println("\n组合Predicate:");
        Predicate<Integer> isGreaterThan5 = n -> n > 5;
        Predicate<Integer> isEvenAndGreaterThan5 = isOdd.negate().and(isGreaterThan5);
        List<Integer> result = numbers.stream()
                .filter(isEvenAndGreaterThan5)
                .collect(Collectors.toList());
        System.out.println("大于5的偶数: " + result);
    }
}
