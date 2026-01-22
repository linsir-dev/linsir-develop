package com.linsir.jdk.stream;

/**
 * Stream API综合测试类，包含所有示例
 */
public class StreamDemoTest {
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("Java Stream API 综合测试");
        System.out.println("=============================================");
        
        // 运行StreamCommonOperations示例
        System.out.println("\n1. Stream API 常用操作示例");
        System.out.println("---------------------------------------------");
        StreamCommonOperations commonOperations = new StreamCommonOperations();
        commonOperations.createStreams();
        commonOperations.intermediateOperations();
        commonOperations.terminalOperations();
        commonOperations.numericStreams();
        commonOperations.comprehensiveExample();
        
        // 运行StreamParallelDemo示例
        System.out.println("\n2. Stream 串行与并行模式示例");
        System.out.println("---------------------------------------------");
        StreamParallelDemo parallelDemo = new StreamParallelDemo();
        parallelDemo.sequentialStreamDemo();
        parallelDemo.parallelStreamDemo();
        parallelDemo.parallelStreamMethodDemo();
        parallelDemo.parallelStreamUnorderedDemo();
        parallelDemo.parallelStreamUseCases();
        
        // 运行StreamPerformanceTest示例
        System.out.println("\n3. Stream 性能测试示例");
        System.out.println("---------------------------------------------");
        StreamPerformanceTest performanceTest = new StreamPerformanceTest();
        performanceTest.runAllTests();
        
        System.out.println("=============================================");
        System.out.println("Stream API 综合测试完成");
        System.out.println("=============================================");
    }
}
