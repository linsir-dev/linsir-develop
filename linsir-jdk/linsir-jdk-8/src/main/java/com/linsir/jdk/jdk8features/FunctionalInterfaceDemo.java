package com.linsir.jdk.jdk8features;

import java.util.function.Function;

/**
 * FunctionalInterface示例
 */
public class FunctionalInterfaceDemo {

    public void demonstrate() {
        System.out.println("\n=== FunctionalInterface ===");
        
        // 使用内置函数接口
        System.out.println("使用内置函数接口:");
        Function<Integer, Integer> square = n -> n * n;
        System.out.println("平方值: " + square.apply(5));
        
        // 使用自定义函数接口
        System.out.println("\n使用自定义函数接口:");
        Calculator add = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;
        System.out.println("加法: " + add.calculate(5, 3));
        System.out.println("乘法: " + multiply.calculate(5, 3));
    }

    // 自定义函数接口
    @FunctionalInterface
    interface Calculator {
        int calculate(int a, int b);
    }
}
