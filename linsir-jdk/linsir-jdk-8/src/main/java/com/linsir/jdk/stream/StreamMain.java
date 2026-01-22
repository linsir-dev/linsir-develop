package com.linsir.jdk.stream;

/**
 * Stream API示例运行类
 */
public class StreamMain {
    public static void main(String[] args) {
        StreamCommonOperations operations = new StreamCommonOperations();
        
        System.out.println("=============================================");
        System.out.println("Java Stream API 常用功能示例");
        System.out.println("=============================================");
        
        // 运行各种Stream操作示例
        operations.createStreams();
        operations.intermediateOperations();
        operations.terminalOperations();
        operations.numericStreams();
        operations.parallelStream();
        operations.comprehensiveExample();
        
        System.out.println("=============================================");
        System.out.println("Stream API 示例运行完成");
        System.out.println("=============================================");
    }
}
