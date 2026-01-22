package com.linsir.jdk.jdk8features;

import java.util.Arrays;
import java.util.List;

/**
 * forEach Lambdas示例
 */
public class ForEachLambdasDemo {

    public void demonstrate() {
        System.out.println("\n=== forEach Lambdas示例 ===");
        
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        // 传统for循环
        System.out.println("传统for循环:");
        for (int i = 0; i < names.size(); i++) {
            System.out.println(names.get(i));
        }
        
        // 增强for循环
        System.out.println("\n增强for循环:");
        for (String name : names) {
            System.out.println(name);
        }
        
        // forEach + Lambda
        System.out.println("\nforEach + Lambda:");
        names.forEach(name -> System.out.println(name));
        
        // forEach + 方法引用
        System.out.println("\nforEach + 方法引用:");
        names.forEach(System.out::println);
    }
}
