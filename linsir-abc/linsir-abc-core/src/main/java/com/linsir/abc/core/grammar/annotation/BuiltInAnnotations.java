package com.linsir.abc.core.grammar.annotation;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 内置注解示例
 *
 * 本类演示 Java 内置注解的使用
 * 对应 JDK: java.lang 注解
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class BuiltInAnnotations {

    /**
     * 演示 @Override
     */
    public void demonstrateOverride() {
        System.out.println("=== @Override 注解 ===");

        Animal animal = new Dog();
        animal.makeSound();

        System.out.println("\n@Override 作用:");
        System.out.println("  - 表明方法重写了父类方法");
        System.out.println("  - 编译器会检查方法签名是否正确");
        System.out.println("  - 提高代码可读性");
    }

    /**
     * 演示 @Deprecated
     */
    @SuppressWarnings("deprecation")
    public void demonstrateDeprecated() {
        System.out.println("\n=== @Deprecated 注解 ===");

        OldClass old = new OldClass();
        old.oldMethod();  // 使用已过时的方法

        System.out.println("\n@Deprecated 作用:");
        System.out.println("  - 标记已过时的类、方法或字段");
        System.out.println("  - 编译器会产生警告");
        System.out.println("  - 建议用户使用替代方案");
    }

    /**
     * 演示 @SuppressWarnings
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void demonstrateSuppressWarnings() {
        System.out.println("\n=== @SuppressWarnings 注解 ===");

        // 未使用泛型的警告被抑制
        List list = new ArrayList();
        list.add("test");

        System.out.println("@SuppressWarnings 作用:");
        System.out.println("  - 抑制编译器警告");
        System.out.println("  - 常用值: unchecked, deprecation, rawtypes");
        System.out.println("  - 应谨慎使用，避免隐藏真正的问题");
    }

    /**
     * 演示 @FunctionalInterface
     */
    public void demonstrateFunctionalInterface() {
        System.out.println("\n=== @FunctionalInterface 注解 ===");

        // 使用 Lambda 表达式
        Calculator add = (a, b) -> a + b;
        Calculator multiply = (a, b) -> a * b;

        System.out.println("10 + 5 = " + add.calculate(10, 5));
        System.out.println("10 * 5 = " + multiply.calculate(10, 5));

        System.out.println("\n@FunctionalInterface 作用:");
        System.out.println("  - 标记函数式接口");
        System.out.println("  - 编译器检查是否只有一个抽象方法");
        System.out.println("  - 可以使用 Lambda 表达式");
    }

    /**
     * 演示 @SafeVarargs
     */
    @SafeVarargs
    public final void demonstrateSafeVarargs(String... args) {
        System.out.println("\n=== @SafeVarargs 注解 ===");

        for (String arg : args) {
            System.out.println("  参数: " + arg);
        }

        System.out.println("\n@SafeVarargs 作用:");
        System.out.println("  - 标记可变参数方法不会对其参数执行不安全的操作");
        System.out.println("  - 只能用于 static 或 final 方法");
    }

    /**
     * 演示通过反射读取注解
     */
    public void demonstrateReflection() {
        System.out.println("\n=== 通过反射读取注解 ===");

        try {
            Class<?> clazz = MyService.class;

            // 读取类上的注解
            if (clazz.isAnnotationPresent(Service.class)) {
                Service service = clazz.getAnnotation(Service.class);
                System.out.println("类注解 @Service:");
                System.out.println("  name: " + service.name());
                System.out.println("  version: " + service.version());
            }

            // 读取方法上的注解
            Method method = clazz.getMethod("doSomething");
            if (method.isAnnotationPresent(Deprecated.class)) {
                System.out.println("\n方法 doSomething() 被标记为 @Deprecated");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============ 辅助类和接口 ============

    /**
     * 动物抽象类
     */
    static abstract class Animal {
        public abstract void makeSound();
    }

    /**
     * 狗类
     */
    static class Dog extends Animal {
        @Override
        public void makeSound() {
            System.out.println("汪汪叫");
        }
    }

    /**
     * 旧类（演示 @Deprecated）
     */
    static class OldClass {
        @Deprecated(since = "2.0", forRemoval = true)
        public void oldMethod() {
            System.out.println("这是已过时的方法");
        }

        public void newMethod() {
            System.out.println("这是新的方法");
        }
    }

    /**
     * 函数式接口
     */
    @FunctionalInterface
    interface Calculator {
        int calculate(int a, int b);
    }

    // ============ 自定义注解 ============

    /**
     * 服务注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Service {
        String name();
        String version() default "1.0";
    }

    /**
     * 服务类
     */
    @Service(name = "MyService", version = "2.0")
    static class MyService {
        @Deprecated
        public void doSomething() {
            System.out.println("做某事");
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 内置注解演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        BuiltInAnnotations demo = new BuiltInAnnotations();
        demo.demonstrateOverride();
        demo.demonstrateDeprecated();
        demo.demonstrateSuppressWarnings();
        demo.demonstrateFunctionalInterface();
        demo.demonstrateSafeVarargs("A", "B", "C");
        demo.demonstrateReflection();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
