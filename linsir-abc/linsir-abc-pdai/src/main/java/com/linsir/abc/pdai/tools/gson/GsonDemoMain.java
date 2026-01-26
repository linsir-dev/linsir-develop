package com.linsir.abc.pdai.tools.gson;

/**
 * Gson 示例代码主类
 * 用于演示 Gson 库的各种功能
 */
public class GsonDemoMain {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("Gson 示例代码演示");
        System.out.println("===========================================");

        // 1. 运行基本示例
        System.out.println("\n1. 运行基本 JSON 序列化和反序列化示例:");
        System.out.println("-------------------------------------------");
        GsonBasicDemo.demonstrateBasicFeatures();

        // 2. 运行高级示例
        System.out.println("\n2. 运行高级特性示例:");
        System.out.println("-------------------------------------------");
        GsonAdvancedDemo.demonstrateAdvancedFeatures();

        // 3. 运行 GsonBuilder 示例
        System.out.println("\n3. 运行 GsonBuilder 配置示例:");
        System.out.println("-------------------------------------------");
        GsonBuilderDemo.demonstrateGsonBuilder();

        System.out.println("\n===========================================");
        System.out.println("Gson 示例代码演示完成！");
        System.out.println("===========================================");
    }
}
