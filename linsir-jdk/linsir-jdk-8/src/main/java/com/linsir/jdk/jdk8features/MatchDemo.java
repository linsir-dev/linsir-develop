package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;

/**
 * Match示例（anyMatch、allMatch、noneMatch）
 */
public class MatchDemo {

    public void demonstrate() {
        System.out.println("\n=== Match ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // anyMatch: 任意匹配
        System.out.println("anyMatch: 是否存在大于3的元素: " + 
                numbers.stream().anyMatch(n -> n > 3));
        
        // allMatch: 全部匹配
        System.out.println("allMatch: 是否所有元素都大于0: " + 
                numbers.stream().allMatch(n -> n > 0));
        
        // noneMatch: 无匹配
        System.out.println("noneMatch: 是否不存在大于10的元素: " + 
                numbers.stream().noneMatch(n -> n > 10));
    }
}
