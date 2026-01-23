package com.linsir.jdk.interfaceprivate;

/**
 * InterfaceWithPrivateMethods接口的实现类
 */
public class InterfaceImpl implements InterfaceWithPrivateMethods {

    @Override
    public void abstractMethod() {
        System.out.println("实现抽象方法 abstractMethod");
    }
    
    // 注意：实现类无法访问接口中的私有方法
    // 以下代码会编译错误
    /*
    public void callPrivateMethod() {
        privateHelperMethod(); // 无法访问，编译错误
    }
    */
}
