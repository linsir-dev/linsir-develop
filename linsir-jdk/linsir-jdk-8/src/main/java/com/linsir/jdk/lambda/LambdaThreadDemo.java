package com.linsir.jdk.lambda;

/**
 * Lambda表达式线程和事件处理示例
 */
public class LambdaThreadDemo {

    /**
     * 线程创建示例
     */
    public void testThreadCreation() {
        System.out.println("线程创建示例:");
        
        // 传统方式
        Thread traditionalThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Traditional thread running");
            }
        });
        traditionalThread.start();
        
        // 使用Lambda表达式
        Thread lambdaThread = new Thread(() -> System.out.println("Lambda thread running"));
        lambdaThread.start();
        
        // 等待线程完成
        try {
            traditionalThread.join();
            lambdaThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义函数式接口示例
     */
    public void testCustomFunctionalInterface() {
        System.out.println("\n自定义函数式接口示例:");
        
        // 使用自定义函数式接口
        Calculator add = (a, b) -> a + b;
        Calculator subtract = (a, b) -> a - b;
        Calculator multiply = (a, b) -> a * b;
        Calculator divide = (a, b) -> a / b;
        
        System.out.println("Addition: " + add.calculate(10, 5));
        System.out.println("Subtraction: " + subtract.calculate(10, 5));
        System.out.println("Multiplication: " + multiply.calculate(10, 5));
        System.out.println("Division: " + divide.calculate(10, 5));
    }

    /**
     * 自定义函数式接口
     */
    @FunctionalInterface
    interface Calculator {
        int calculate(int a, int b);
    }
}
