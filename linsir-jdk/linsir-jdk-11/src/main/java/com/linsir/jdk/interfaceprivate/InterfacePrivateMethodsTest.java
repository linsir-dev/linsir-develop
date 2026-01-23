package com.linsir.jdk.interfaceprivate;

/**
 * 接口中使用私有方法的测试类
 */
public class InterfacePrivateMethodsTest {
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("接口中使用私有方法测试");
        System.out.println("=============================================");
        
        // 创建接口实现类实例
        InterfaceImpl impl = new InterfaceImpl();
        
        // 测试抽象方法
        System.out.println("\n1. 测试抽象方法:");
        impl.abstractMethod();
        
        // 测试默认方法1 - 会调用私有方法
        System.out.println("\n2. 测试默认方法1:");
        impl.defaultMethod1();
        
        // 测试默认方法2 - 会调用多个私有方法
        System.out.println("\n3. 测试默认方法2:");
        impl.defaultMethod2();
        
        // 测试静态方法1 - 会调用私有静态方法
        System.out.println("\n4. 测试静态方法1:");
        InterfaceWithPrivateMethods.staticMethod1();
        
        // 测试静态方法2 - 会调用多个私有静态方法
        System.out.println("\n5. 测试静态方法2:");
        InterfaceWithPrivateMethods.staticMethod2();
        
        System.out.println("\n=============================================");
        System.out.println("测试完成");
        System.out.println("=============================================");
        
        // 说明：
        // 1. 接口中的私有方法只能在接口内部被调用
        // 2. 实现类无法访问接口中的私有方法
        // 3. 私有方法的作用是在接口内部复用代码，提高代码的可维护性
    }
}
