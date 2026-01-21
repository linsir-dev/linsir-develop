package com.linsir.abc.pdai.base.reflectdemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * 动态代理示例
 * 展示反射在AOP等框架中的应用
 */
public class DynamicProxyDemo {

    // 定义一个接口
    interface Person {
        void sayHello();
        void sayGoodbye();
    }

    // 实现Person接口的类
    static class RealPerson implements Person {
        private String name;

        public RealPerson(String name) {
            this.name = name;
        }

        @Override
        public void sayHello() {
            System.out.println("Hello, my name is " + name);
        }

        @Override
        public void sayGoodbye() {
            System.out.println("Goodbye, see you later!");
        }
    }

    // 动态代理处理器
    static class PersonInvocationHandler implements InvocationHandler {
        private Object target;

        public PersonInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 前置增强
            System.out.println("[代理] 方法执行前: " + method.getName());
            
            // 调用目标对象的方法
            Object result = method.invoke(target, args);
            
            // 后置增强
            System.out.println("[代理] 方法执行后: " + method.getName());
            
            return result;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== 动态代理示例 ===\n");

        // 创建目标对象
        RealPerson realPerson = new RealPerson("王五");

        // 创建动态代理
        Person proxyPerson = (Person) Proxy.newProxyInstance(
                Person.class.getClassLoader(),
                new Class[]{Person.class},
                new PersonInvocationHandler(realPerson)
        );

        // 通过代理对象调用方法
        System.out.println("调用sayHello方法:");
        proxyPerson.sayHello();

        System.out.println("\n调用sayGoodbye方法:");
        proxyPerson.sayGoodbye();

        System.out.println("\n=== 动态代理原理 ===");
        System.out.println("1. 代理对象的类型: " + proxyPerson.getClass().getName());
        System.out.println("2. 代理对象实现的接口: " + Arrays.toString(proxyPerson.getClass().getInterfaces()));
        System.out.println("3. 代理对象是否是Person的实例: " + (proxyPerson instanceof Person));
        System.out.println("4. 代理对象是否是RealPerson的实例: " + (proxyPerson instanceof RealPerson));
    }
}
