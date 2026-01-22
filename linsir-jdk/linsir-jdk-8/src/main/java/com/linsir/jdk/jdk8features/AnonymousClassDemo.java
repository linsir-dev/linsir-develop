package com.linsir.jdk.jdk8features;

/**
 * 匿名类简写示例
 */
public class AnonymousClassDemo {

    public void demonstrate() {
        System.out.println("=== 匿名类简写示例 ===");
        
        // 传统匿名类
        Runnable traditionalRunnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("传统匿名类: Hello World");
            }
        };
        traditionalRunnable.run();
        
        // Lambda表达式简写
        Runnable lambdaRunnable = () -> System.out.println("Lambda表达式: Hello World");
        lambdaRunnable.run();
        
        // 带参数的Lambda表达式
        java.util.function.Consumer<String> consumer = (s) -> System.out.println("带参数的Lambda: " + s);
        consumer.accept("Hello Lambda");
    }
}
