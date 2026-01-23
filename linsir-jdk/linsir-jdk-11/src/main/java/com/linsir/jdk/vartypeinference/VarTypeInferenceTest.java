package com.linsir.jdk.vartypeinference;

/**
 * 局部变量类型推断测试类
 */
public class VarTypeInferenceTest {
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("JDK 10+ 局部变量类型推断测试");
        System.out.println("=============================================");
        
        VarTypeInferenceDemo demo = new VarTypeInferenceDemo();
        
        // 测试基本类型的局部变量类型推断
        demo.basicTypes();
        
        // 测试数组类型的局部变量类型推断
        demo.arrayTypes();
        
        // 测试集合类型的局部变量类型推断
        demo.collectionTypes();
        
        // 测试循环中的局部变量类型推断
        demo.loopTypes();
        
        // 测试方法返回值的局部变量类型推断
        demo.methodReturnType();
        
        // 测试Stream操作中的局部变量类型推断
        demo.streamOperations();
        
        // 测试局部变量类型推断的限制
        demo.varLimitations();
        
        System.out.println("\n=============================================");
        System.out.println("测试完成");
        System.out.println("=============================================");
        
        // 说明：
        // 1. var关键字是JDK 10+的特性，用于局部变量类型推断
        // 2. var关键字只能用于局部变量，不能用于字段、方法参数、返回类型等
        // 3. 使用var关键字时，编译器会根据初始化值推断变量的类型
        // 4. var关键字可以简化代码，减少重复的类型声明
        // 5. 在JDK 11+中，var还可以用于lambda参数
    }
}
