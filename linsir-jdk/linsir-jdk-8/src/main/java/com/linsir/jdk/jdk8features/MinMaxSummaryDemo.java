package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;

/**
 * min, max, summaryStatistics示例
 */
public class MinMaxSummaryDemo {

    public void demonstrate() {
        System.out.println("\n=== min, max, summaryStatistics ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // min: 最小值
        System.out.println("min: 最小值: " + 
                numbers.stream().min(Integer::compare).orElse(0));
        
        // max: 最大值
        System.out.println("max: 最大值: " + 
                numbers.stream().max(Integer::compare).orElse(0));
        
        // summaryStatistics: 统计摘要
        System.out.println("summaryStatistics: 统计摘要:");
        java.util.IntSummaryStatistics stats = numbers.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
        System.out.println("统计结果: " + stats);
        System.out.println("平均值: " + stats.getAverage());
        System.out.println("总和: " + stats.getSum());
    }
}
