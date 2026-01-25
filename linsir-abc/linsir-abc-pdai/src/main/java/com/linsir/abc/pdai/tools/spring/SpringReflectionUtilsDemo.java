package com.linsir.abc.pdai.tools.spring;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Spring ReflectionUtils 工具类示例
 * 演示 ReflectionUtils 的常用方法
 */
public class SpringReflectionUtilsDemo {

    /**
     * 演示 ReflectionUtils 的常用方法
     */
    public static void demonstrateReflectionUtils() {
        // 创建测试对象
        User user = new User("John", 30, "john@example.com");
        System.out.println("原始用户对象: " + user);
        
        // 1. 获取声明的字段
        System.out.println("\n=== 获取声明的字段 ===");
        Field nameField = null;
        Field ageField = null;
        Field emailField = null;
        
        try {
            nameField = User.class.getDeclaredField("name");
            ageField = User.class.getDeclaredField("age");
            emailField = User.class.getDeclaredField("email");
        } catch (NoSuchFieldException e) {
            System.err.println("获取字段时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("获取到的字段 - name: " + nameField);
        System.out.println("获取到的字段 - age: " + ageField);
        System.out.println("获取到的字段 - email: " + emailField);
        
        // 2. 获取声明的方法
        System.out.println("\n=== 获取声明的方法 ===");
        Method getNameMethod = null;
        Method setNameMethod = null;
        Method getAgeMethod = null;
        Method setAgeMethod = null;
        Method getEmailMethod = null;
        Method setEmailMethod = null;
        
        try {
            getNameMethod = User.class.getDeclaredMethod("getName");
            setNameMethod = User.class.getDeclaredMethod("setName", String.class);
            getAgeMethod = User.class.getDeclaredMethod("getAge");
            setAgeMethod = User.class.getDeclaredMethod("setAge", int.class);
            getEmailMethod = User.class.getDeclaredMethod("getEmail");
            setEmailMethod = User.class.getDeclaredMethod("setEmail", String.class);
        } catch (NoSuchMethodException e) {
            System.err.println("获取方法时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("获取到的方法 - getName: " + getNameMethod);
        System.out.println("获取到的方法 - setName: " + setNameMethod);
        System.out.println("获取到的方法 - getAge: " + getAgeMethod);
        System.out.println("获取到的方法 - setAge: " + setAgeMethod);
        System.out.println("获取到的方法 - getEmail: " + getEmailMethod);
        System.out.println("获取到的方法 - setEmail: " + setEmailMethod);
        
        // 3. 调用方法
        System.out.println("\n=== 调用方法 ===");
        try {
            if (getNameMethod != null && getAgeMethod != null && getEmailMethod != null) {
                // 调用 getter 方法
                String name = (String) getNameMethod.invoke(user);
                int age = (int) getAgeMethod.invoke(user);
                String email = (String) getEmailMethod.invoke(user);
                
                System.out.println("调用 getter 方法结果:");
                System.out.println("Name: " + name);
                System.out.println("Age: " + age);
                System.out.println("Email: " + email);
                
                // 调用 setter 方法
                if (setNameMethod != null && setAgeMethod != null && setEmailMethod != null) {
                    setNameMethod.invoke(user, "Jane");
                    setAgeMethod.invoke(user, 25);
                    setEmailMethod.invoke(user, "jane@example.com");
                    
                    System.out.println("调用 setter 方法后:");
                    System.out.println("Updated User: " + user);
                }
            }
        } catch (Exception e) {
            System.err.println("调用方法时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 4. 设置字段值
        System.out.println("\n=== 设置字段值 ===");
        try {
            if (nameField != null && ageField != null && emailField != null) {
                // 设置字段可访问
                nameField.setAccessible(true);
                ageField.setAccessible(true);
                emailField.setAccessible(true);
                
                // 获取字段值
                String currentName = (String) nameField.get(user);
                int currentAge = (int) ageField.get(user);
                String currentEmail = (String) emailField.get(user);
                
                System.out.println("当前字段值:");
                System.out.println("Name: " + currentName);
                System.out.println("Age: " + currentAge);
                System.out.println("Email: " + currentEmail);
                
                // 设置字段值
                nameField.set(user, "Tom");
                ageField.set(user, 35);
                emailField.set(user, "tom@example.com");
                
                System.out.println("设置字段值后:");
                System.out.println("Updated User: " + user);
            }
        } catch (Exception e) {
            System.err.println("设置字段值时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 5. 遍历字段
        System.out.println("\n=== 遍历字段 ===");
        Field[] fields = User.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(user);
                System.out.println("Field: " + field.getName() + ", Value: " + value);
            } catch (IllegalAccessException e) {
                System.err.println("获取字段值时发生异常: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 6. 遍历方法
        System.out.println("\n=== 遍历方法 ===");
        Method[] methods = User.class.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println("Method: " + method.getName() + ", Parameters: " + method.getParameterCount());
        }
        
        // 7. 查找特定方法
        System.out.println("\n=== 查找特定方法 ===");
        try {
            Method method = User.class.getDeclaredMethod("setName", String.class);
            System.out.println("找到的方法: " + method);
        } catch (NoSuchMethodException e) {
            System.err.println("查找方法时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 8. 处理异常
        System.out.println("\n=== 处理异常 ===");
        try {
            // 尝试调用不存在的方法
            Method nonExistentMethod = User.class.getDeclaredMethod("nonExistentMethod");
        } catch (NoSuchMethodException e) {
            System.out.println("预期的异常: " + e.getMessage());
        }
    }

    /**
     * 测试用的 User 类
     */
    static class User {
        private String name;
        private int age;
        private String email;

        public User(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}
