package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * flatMap常规用法
 */
public class FlatMapDemo {

    public void demonstrate() {
        System.out.println("\n=== flatMap常规用法 ===");
        
        List<List<Integer>> nestedList = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6),
                Arrays.asList(7, 8, 9)
        );
        
        // 使用flatMap扁平化
        System.out.println("使用flatMap扁平化嵌套列表:");
        List<Integer> flattenedList = nestedList.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println("扁平化结果: " + flattenedList);
        
        // 使用flatMap处理字符串
        System.out.println("\n使用flatMap处理字符串:");
        List<String> sentences = Arrays.asList(
                "Hello World",
                "Java 8 Features",
                "Lambda Expressions"
        );
        List<String> words = sentences.stream()
                .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                .collect(Collectors.toList());
        System.out.println("单词列表: " + words);
    }
}
