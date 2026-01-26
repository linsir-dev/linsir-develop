package com.linsir.abc.pdai.tools.jackson;

/**
 * Jackson 示例代码主类
 * 用于演示 Jackson 库的各种功能
 */
public class JacksonDemoMain {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("Jackson 示例代码演示");
        System.out.println("===========================================");

        // 1. 运行基本示例
        System.out.println("\n1. 运行基本 JSON 序列化和反序列化示例:");
        System.out.println("-------------------------------------------");
        JacksonBasicDemo.demonstrateBasicFeatures();

        // 2. 运行高级示例
        System.out.println("\n2. 运行高级特性示例:");
        System.out.println("-------------------------------------------");
        JacksonAdvancedDemo.demonstrateAdvancedFeatures();

        // 3. 运行树模型示例
        System.out.println("\n3. 运行树模型操作示例:");
        System.out.println("-------------------------------------------");
        JacksonTreeModelDemo.demonstrateTreeModel();

        // 4. 运行流式 API 示例
        System.out.println("\n4. 运行流式 API 操作示例:");
        System.out.println("-------------------------------------------");
        JacksonStreamingDemo.demonstrateStreamingAPI();

        System.out.println("\n===========================================");
        System.out.println("Jackson 示例代码演示完成！");
        System.out.println("===========================================");
    }
}
