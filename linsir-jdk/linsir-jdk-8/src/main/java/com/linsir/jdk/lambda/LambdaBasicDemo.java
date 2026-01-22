package com.linsir.jdk.lambda;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Lambda表达式基本使用示例
 */
public class LambdaBasicDemo {

    /**
     * 无参数示例
     */
    public void testNoParameter() {
        // 使用Runnable接口
        Runnable r = () -> System.out.println("Hello Lambda");
        r.run();
    }

    /**
     * 单个参数示例
     */
    public void testSingleParameter() {
        // 使用Function接口
        Function<Integer, Integer> square = x -> x * x;
        int result = square.apply(5);
        System.out.println("Square of 5: " + result);
    }

    /**
     * 多个参数示例
     */
    public void testMultipleParameters() {
        // 使用BiFunction接口
        BiFunction<Integer, Integer, Integer> add = (x, y) -> x + y;
        int result = add.apply(3, 4);
        System.out.println("3 + 4 = " + result);
    }

    /**
     * 代码块示例
     */
    public void testCodeBlock() {
        // 使用Function接口，包含代码块
        Function<Integer, Integer> factorial = n -> {
            int result = 1;
            for (int i = 1; i <= n; i++) {
                result *= i;
            }
            return result;
        };
        int result = factorial.apply(5);
        System.out.println("Factorial of 5: " + result);
    }

    /**
     * 显式参数类型示例
     */
    public void testExplicitType() {
        // 显式声明参数类型
        BiFunction<Integer, Integer, Integer> multiply = (int x, int y) -> x * y;
        int result = multiply.apply(6, 7);
        System.out.println("6 * 7 = " + result);
    }

    /**
     * 无返回值示例
     */
    public void testNoReturnValue() {
        // 使用自定义函数式接口
        Greeting greeting = name -> System.out.println("Hello, " + name);
        greeting.sayHello("Alice");
    }

    /**
     * 自定义函数式接口
     */
    @FunctionalInterface
    interface Greeting {
        void sayHello(String name);
    }
}
