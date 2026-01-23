package com.linsir.jdk.interfaceprivate;

/**
 * JDK 9+ 接口中使用私有方法示例
 * 
 * 说明：
 * 1. JDK 9之前，接口中只能有抽象方法、默认方法和静态方法
 * 2. JDK 9引入了私有方法和私有静态方法，用于在接口内部复用代码
 * 3. 私有方法只能在接口内部被其他默认方法或静态方法调用
 */
public interface InterfaceWithPrivateMethods {
    
    // 抽象方法
    void abstractMethod();
    
    // 默认方法
    default void defaultMethod1() {
        System.out.println("执行defaultMethod1");
        // 调用私有方法
        privateHelperMethod();
    }
    
    // 默认方法
    default void defaultMethod2() {
        System.out.println("执行defaultMethod2");
        // 调用私有方法
        privateHelperMethod();
        // 调用另一个私有方法
        privateHelperMethodWithParam("defaultMethod2");
    }
    
    // 静态方法
    static void staticMethod1() {
        System.out.println("执行staticMethod1");
        // 调用私有静态方法
        privateStaticHelperMethod();
    }
    
    // 静态方法
    static void staticMethod2() {
        System.out.println("执行staticMethod2");
        // 调用私有静态方法
        privateStaticHelperMethod();
        // 调用另一个私有静态方法
        privateStaticHelperMethodWithParam("staticMethod2");
    }
    
    // 私有方法 - 只能在接口内部被默认方法调用
    private void privateHelperMethod() {
        System.out.println("执行私有方法 privateHelperMethod");
        // 可以包含复杂的业务逻辑
        System.out.println("这是一个私有的辅助方法，用于在默认方法之间共享代码");
    }
    
    // 带参数的私有方法
    private void privateHelperMethodWithParam(String caller) {
        System.out.println("执行私有方法 privateHelperMethodWithParam，调用者: " + caller);
    }
    
    // 私有静态方法 - 只能在接口内部被静态方法调用
    private static void privateStaticHelperMethod() {
        System.out.println("执行私有静态方法 privateStaticHelperMethod");
        // 可以包含复杂的业务逻辑
        System.out.println("这是一个私有的静态辅助方法，用于在静态方法之间共享代码");
    }
    
    // 带参数的私有静态方法
    private static void privateStaticHelperMethodWithParam(String caller) {
        System.out.println("执行私有静态方法 privateStaticHelperMethodWithParam，调用者: " + caller);
    }
}
