package com.linsir.abc.pdai.Base.keyword;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示 final 关键字的用法
 * 1. 修饰变量：一旦赋值，不可更改（对于引用类型，引用不可变，但对象内容可变）。
 * 2. 修饰方法：不可被子类重写。
 * 3. 修饰类：不可被继承。
 */
public class FinalDemo {

    // 1. 修饰成员变量
    private final int MAX_VALUE = 100; // 必须初始化
    private final String name; // 或者在构造器中初始化

    public FinalDemo(String name) {
        this.name = name;
    }

    public void testFinalVariable() {
        System.out.println("--- 演示 final 变量 ---");
        // MAX_VALUE = 200; // 编译错误：无法为 final 变量分配值

        final int localVal = 10;
        // localVal = 20; // 编译错误

        final List<String> list = new ArrayList<>();
        list.add("Hello"); // 合法：可以修改对象内容
        list.add("World");
        System.out.println("Final list content: " + list);

        // list = new ArrayList<>(); // 编译错误：无法更改引用指向
    }

    public void test() {
        testFinalVariable();
        new SubClass().display();
    }

    public static void main(String[] args) {
        new FinalDemo("Test").test();
    }

    // 2. 修饰类：ParentClass 不能被继承
    static final class ParentClass {
        public void say() {
            System.out.println("Parent say");
        }
    }

    // class Child extends ParentClass {} // 编译错误：无法从 final 类继承

    static class BaseClass {
        // 3. 修饰方法：不能被重写
        public final void finalMethod() {
            System.out.println("BaseClass finalMethod");
        }
        
        public void normalMethod() {
            System.out.println("BaseClass normalMethod");
        }
    }

    static class SubClass extends BaseClass {
        // @Override
        // public void finalMethod() { } // 编译错误：被覆盖的方法为 final

        @Override
        public void normalMethod() {
            System.out.println("SubClass normalMethod");
        }

        public void display() {
            System.out.println("--- 演示 final 方法和类 ---");
            finalMethod(); // 可以调用
            normalMethod();
        }
    }
}
