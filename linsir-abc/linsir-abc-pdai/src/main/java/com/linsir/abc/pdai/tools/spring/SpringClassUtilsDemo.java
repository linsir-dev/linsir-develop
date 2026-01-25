package com.linsir.abc.pdai.tools.spring;

import org.springframework.util.ClassUtils;

import java.util.Arrays;

/**
 * Spring ClassUtils 工具类示例
 * 演示 ClassUtils 的常用方法
 */
public class SpringClassUtilsDemo {

    /**
     * 演示 ClassUtils 的常用方法
     */
    public static void demonstrateClassUtils() {
        // 1. 获取默认类加载器
        System.out.println("=== 获取默认类加载器 ===");
        ClassLoader defaultClassLoader = ClassUtils.getDefaultClassLoader();
        System.out.println("默认类加载器: " + defaultClassLoader);
        
        // 2. 加载类
        System.out.println("\n=== 加载类 ===");
        try {
            Class<?> stringClass = ClassUtils.forName("java.lang.String", defaultClassLoader);
            System.out.println("加载的类: " + stringClass);
            
            Class<?> integerClass = ClassUtils.forName("java.lang.Integer", defaultClassLoader);
            System.out.println("加载的类: " + integerClass);
        } catch (ClassNotFoundException e) {
            System.err.println("加载类时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 3. 获取类的短名称
        System.out.println("\n=== 获取类的短名称 ===");
        String stringShortName = ClassUtils.getShortName(String.class);
        System.out.println("String.class 的短名称: " + stringShortName); // String
        
        String userShortName = ClassUtils.getShortName(User.class);
        System.out.println("User.class 的短名称: " + userShortName); // User
        
        String innerUserShortName = ClassUtils.getShortName(User.InnerUser.class);
        System.out.println("User.InnerUser.class 的短名称: " + innerUserShortName); // User.InnerUser
        
        // 4. 获取类的包名
        System.out.println("\n=== 获取类的包名 ===");
        String stringPackageName = ClassUtils.getPackageName(String.class);
        System.out.println("String.class 的包名: " + stringPackageName); // java.lang
        
        String userPackageName = ClassUtils.getPackageName(User.class);
        System.out.println("User.class 的包名: " + userPackageName); // com.linsir.abc.pdai.tools.spring
        
        // 5. 检查类型是否可赋值
        System.out.println("\n=== 检查类型是否可赋值 ===");
        boolean isAssignable1 = ClassUtils.isAssignable(Number.class, Integer.class);
        System.out.println("Number.class 是否可赋值给 Integer.class: " + isAssignable1); // true
        
        boolean isAssignable2 = ClassUtils.isAssignable(Integer.class, Number.class);
        System.out.println("Integer.class 是否可赋值给 Number.class: " + isAssignable2); // false
        
        boolean isAssignable3 = ClassUtils.isAssignable(String.class, Object.class);
        System.out.println("String.class 是否可赋值给 Object.class: " + isAssignable3); // false
        
        boolean isAssignable4 = ClassUtils.isAssignable(Object.class, String.class);
        System.out.println("Object.class 是否可赋值给 String.class: " + isAssignable4); // true
        
        // 6. 检查是否为内部类
        System.out.println("\n=== 检查是否为内部类 ===");
        boolean isInnerClass1 = ClassUtils.isInnerClass(User.class);
        System.out.println("User.class 是否为内部类: " + isInnerClass1); // false
        
        boolean isInnerClass2 = ClassUtils.isInnerClass(User.InnerUser.class);
        System.out.println("User.InnerUser.class 是否为内部类: " + isInnerClass2); // true
        
        // 7. 获取类实现的所有接口
        System.out.println("\n=== 获取类实现的所有接口 ===");
        Class<?>[] userInterfaces = ClassUtils.getAllInterfacesForClass(User.class);
        System.out.println("User.class 实现的接口: " + Arrays.toString(userInterfaces));
        
        Class<?>[] stringInterfaces = ClassUtils.getAllInterfacesForClass(String.class);
        System.out.println("String.class 实现的接口: " + Arrays.toString(stringInterfaces));
        
        // 8. 获取类的所有父类
        System.out.println("\n=== 获取类的所有父类 ===");
        System.out.println("User.class 的父类: " + User.class.getSuperclass());
        System.out.println("String.class 的父类: " + String.class.getSuperclass());
        
        // 9. 获取类的 canonical 名称
        System.out.println("\n=== 获取类的 canonical 名称 ===");
        String stringCanonicalName = String.class.getCanonicalName();
        System.out.println("String.class 的 canonical 名称: " + stringCanonicalName); // java.lang.String
        
        String userCanonicalName = User.class.getCanonicalName();
        System.out.println("User.class 的 canonical 名称: " + userCanonicalName); // com.linsir.abc.pdai.tools.spring.SpringClassUtilsDemo.User
        
        // 10. 检查类是否为数组类型
        System.out.println("\n=== 检查类是否为数组类型 ===");
        boolean isArray1 = String.class.isArray();
        System.out.println("String.class 是否为数组类型: " + isArray1); // false
        
        boolean isArray2 = String[].class.isArray();
        System.out.println("String[].class 是否为数组类型: " + isArray2); // true
        
        // 11. 检查类是否为基本类型
        System.out.println("\n=== 检查类是否为基本类型 ===");
        boolean isPrimitive1 = ClassUtils.isPrimitiveOrWrapper(Integer.class);
        System.out.println("Integer.class 是否为基本类型或包装类型: " + isPrimitive1); // true
        
        boolean isPrimitive2 = ClassUtils.isPrimitiveOrWrapper(String.class);
        System.out.println("String.class 是否为基本类型或包装类型: " + isPrimitive2); // false
        
        boolean isPrimitive3 = ClassUtils.isPrimitiveOrWrapper(int.class);
        System.out.println("int.class 是否为基本类型或包装类型: " + isPrimitive3); // true
    }

    /**
     * 测试用的 User 类
     */
    static class User implements Runnable {
        private String name;
        private int age;

        public User() {
        }

        public User(String name, int age) {
            this.name = name;
            this.age = age;
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

        @Override
        public void run() {
            System.out.println("User is running");
        }

        /**
         * 内部类
         */
        static class InnerUser {
            private String name;

            public InnerUser(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
