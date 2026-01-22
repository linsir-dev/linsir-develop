package com.linsir.jdk.lambda;

/**
 * Lambda表达式测试类
 */
public class LambdaTest {

    public static void main(String[] args) {
        System.out.println("=== Lambda表达式测试 ===");
        
        // 测试基本使用
        System.out.println("\n1. 测试Lambda表达式基本使用:");
        LambdaBasicDemo basicDemo = new LambdaBasicDemo();
        basicDemo.testNoParameter();
        basicDemo.testSingleParameter();
        basicDemo.testMultipleParameters();
        basicDemo.testCodeBlock();
        basicDemo.testExplicitType();
        basicDemo.testNoReturnValue();
        
        // 测试集合操作
        System.out.println("\n2. 测试Lambda表达式集合操作:");
        LambdaCollectionDemo collectionDemo = new LambdaCollectionDemo();
        collectionDemo.testForEach();
        collectionDemo.testFilter();
        collectionDemo.testMap();
        collectionDemo.testSort();
        collectionDemo.testReduce();
        collectionDemo.testMethodReference();
        
        // 测试线程和自定义函数式接口
        System.out.println("\n3. 测试Lambda表达式线程和自定义函数式接口:");
        LambdaThreadDemo threadDemo = new LambdaThreadDemo();
        threadDemo.testThreadCreation();
        threadDemo.testCustomFunctionalInterface();
        
        System.out.println("\n=== Lambda表达式测试完成 ===");
    }
}
