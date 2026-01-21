package com.linsir.abc.pdai.Base.reflectdemo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * 反射演示类
 * 展示Java反射的核心功能
 */
public class ReflectDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Java 反射示例 ===\n");

        // 1. 获取类对象
        Class<?> userClass = User.class;
        System.out.println("1. 类信息:");
        System.out.println("   类名: " + userClass.getName());
        System.out.println("   简单类名: " + userClass.getSimpleName());
        System.out.println("   包名: " + userClass.getPackage().getName());

        // 2. 获取构造方法
        System.out.println("\n2. 构造方法:");
        Constructor<?>[] constructors = userClass.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println("   构造方法: " + constructor);
            System.out.println("   参数类型: " + Arrays.toString(constructor.getParameterTypes()));
        }

        // 3. 创建对象实例
        System.out.println("\n3. 创建对象实例:");
        // 通过无参构造方法创建对象
        Object user1 = userClass.newInstance();
        System.out.println("   通过无参构造方法创建对象: " + user1);

        // 通过有参构造方法创建对象
        Constructor<?> constructor = userClass.getConstructor(String.class, int.class);
        Object user2 = constructor.newInstance("张三", 25);
        System.out.println("   通过有参构造方法创建对象: " + user2);

        // 4. 获取和修改字段
        System.out.println("\n4. 字段操作:");
        // 获取公共字段
        Field nameField = userClass.getField("name");
        System.out.println("   公共字段name的值: " + nameField.get(user2));

        // 修改公共字段
        nameField.set(user2, "李四");
        System.out.println("   修改后name的值: " + nameField.get(user2));

        // 获取私有字段（需要设置可访问）
        Field ageField = userClass.getDeclaredField("age");
        ageField.setAccessible(true); // 设置为可访问
        System.out.println("   私有字段age的值: " + ageField.get(user2));

        // 修改私有字段
        ageField.set(user2, 30);
        System.out.println("   修改后age的值: " + ageField.get(user2));

        // 5. 调用方法
        System.out.println("\n5. 方法调用:");
        // 调用无参方法
        Method sayHelloMethod = userClass.getMethod("sayHello");
        System.out.print("   调用无参sayHello方法: ");
        sayHelloMethod.invoke(user2);

        // 调用带参数的方法
        Method sayHelloWithParamMethod = userClass.getMethod("sayHello", String.class);
        System.out.print("   调用带参数的sayHello方法: ");
        sayHelloWithParamMethod.invoke(user2, "反射");

        // 调用私有方法（需要设置可访问）
        Method privateMethod = userClass.getDeclaredMethod("privateMethod");
        privateMethod.setAccessible(true); // 设置为可访问
        System.out.print("   调用私有方法privateMethod: ");
        privateMethod.invoke(user2);

        // 6. 获取类的所有方法
        System.out.println("\n6. 类的所有方法:");
        Method[] methods = userClass.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println("   方法: " + method.getName());
            System.out.println("   修饰符: " + Modifier.toString(method.getModifiers()));
            System.out.println("   参数类型: " + Arrays.toString(method.getParameterTypes()));
            System.out.println("   返回类型: " + method.getReturnType());
            System.out.println();
        }

        // 7. 获取类的所有字段
        System.out.println("\n7. 类的所有字段:");
        Field[] fields = userClass.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("   字段: " + field.getName());
            System.out.println("   修饰符: " + Modifier.toString(field.getModifiers()));
            System.out.println("   类型: " + field.getType().getName());
            System.out.println();
        }

        // 8. 反射的实际应用示例
        System.out.println("\n8. 反射的实际应用:");
        // 模拟框架中的依赖注入
        injectDependencies(user2);
        
        // 调用设置后的方法
        Method protectedMethod = userClass.getDeclaredMethod("protectedMethod");
        protectedMethod.setAccessible(true);
        System.out.print("   调用protectedMethod: ");
        protectedMethod.invoke(user2);

        Method defaultMethod = userClass.getDeclaredMethod("defaultMethod");
        defaultMethod.setAccessible(true);
        System.out.print("   调用defaultMethod: ");
        defaultMethod.invoke(user2);
    }

    /**
     * 模拟依赖注入
     */
    private static void injectDependencies(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        
        // 设置email字段
        Field emailField = clazz.getDeclaredField("email");
        emailField.setAccessible(true);
        emailField.set(obj, "zhangsan@example.com");
        
        // 设置address字段
        Field addressField = clazz.getDeclaredField("address");
        addressField.setAccessible(true);
        addressField.set(obj, "北京市朝阳区");
        
        System.out.println("   依赖注入完成，设置了email和address字段");
    }
}
