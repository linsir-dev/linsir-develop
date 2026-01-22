package com.linsir.jdk.jdk8features;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 内置四大函数接口示例
 */
public class BuiltInFunctionalInterfacesDemo {

    public void demonstrate() {
        System.out.println("\n=== 内置四大函数接口 ===");
        
        // 1. Function<T, R>: 接收T类型参数，返回R类型结果
        System.out.println("1. Function<T, R>:");
        Function<Integer, Integer> square = n -> n * n;
        System.out.println("平方值: " + square.apply(5));
        
        // 2. Consumer<T>: 接收T类型参数，无返回值
        System.out.println("\n2. Consumer<T>:");
        Consumer<String> printer = s -> System.out.println("消费: " + s);
        printer.accept("Hello Consumer");
        
        // 3. Supplier<T>: 无参数，返回T类型结果
        System.out.println("\n3. Supplier<T>:");
        Supplier<Double> random = Math::random;
        System.out.println("生成随机数: " + random.get());
        
        // 4. Predicate<T>: 接收T类型参数，返回boolean值
        System.out.println("\n4. Predicate<T>:");
        Predicate<Integer> isPositive = n -> n > 0;
        System.out.println("是否为正数: " + isPositive.test(5));
        System.out.println("是否为正数: " + isPositive.test(-1));
    }
}
