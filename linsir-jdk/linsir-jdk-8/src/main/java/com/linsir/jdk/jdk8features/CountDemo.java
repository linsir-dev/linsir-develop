package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;

/**
 * count常规用法
 */
public class CountDemo {

    public void demonstrate() {
        System.out.println("\n=== count常规用法 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 统计元素数量
        System.out.println("统计元素数量:");
        long count = numbers.stream().count();
        System.out.println("元素数量: " + count);
        
        // 统计满足条件的元素数量
        System.out.println("\n统计满足条件的元素数量:");
        long evenCount = numbers.stream()
                .filter(n -> n % 2 == 0)
                .count();
        System.out.println("偶数数量: " + evenCount);
    }
}
