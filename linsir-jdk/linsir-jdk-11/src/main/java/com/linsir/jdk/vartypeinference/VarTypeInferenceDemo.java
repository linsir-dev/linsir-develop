package com.linsir.jdk.vartypeinference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JDK 10+ 局部变量类型推断示例
 * 
 * 说明：
 * 1. JDK 10引入了var关键字，用于局部变量类型推断
 * 2. var关键字只能用于局部变量，不能用于字段、方法参数、返回类型等
 * 3. 使用var关键字时，编译器会根据初始化值推断变量的类型
 * 4. var关键字可以简化代码，减少重复的类型声明
 */
public class VarTypeInferenceDemo {

    /**
     * 基本类型的局部变量类型推断
     */
    public void basicTypes() {
        System.out.println("=== 基本类型的局部变量类型推断 ===");
        
        // 整数类型
        var intValue = 10;
        System.out.println("整数类型: " + intValue + ", 类型: " + intValue.getClass().getSimpleName());
        
        // 浮点类型
        var doubleValue = 3.14;
        System.out.println("浮点类型: " + doubleValue + ", 类型: " + doubleValue.getClass().getSimpleName());
        
        // 布尔类型
        var booleanValue = true;
        System.out.println("布尔类型: " + booleanValue + ", 类型: " + booleanValue.getClass().getSimpleName());
        
        // 字符类型
        var charValue = 'A';
        System.out.println("字符类型: " + charValue + ", 类型: " + charValue.getClass().getSimpleName());
        
        // 字符串类型
        var stringValue = "Hello, var!";
        System.out.println("字符串类型: " + stringValue + ", 类型: " + stringValue.getClass().getSimpleName());
    }

    /**
     * 数组类型的局部变量类型推断
     */
    public void arrayTypes() {
        System.out.println("\n=== 数组类型的局部变量类型推断 ===");
        
        // 整型数组
        var intArray = new int[]{1, 2, 3, 4, 5};
        System.out.println("整型数组长度: " + intArray.length);
        System.out.println("整型数组元素: " + java.util.Arrays.toString(intArray));
        
        // 字符串数组
        var stringArray = new String[]{"Java", "10", "var"};
        System.out.println("字符串数组长度: " + stringArray.length);
        System.out.println("字符串数组元素: " + java.util.Arrays.toString(stringArray));
    }

    /**
     * 集合类型的局部变量类型推断
     */
    public void collectionTypes() {
        System.out.println("\n=== 集合类型的局部变量类型推断 ===");
        
        // List
        var list = new ArrayList<String>();
        list.add("Java");
        list.add("Python");
        list.add("C++");
        System.out.println("List类型: " + list.getClass().getSimpleName());
        System.out.println("List元素: " + list);
        
        // Set
        var set = new HashSet<Integer>();
        set.add(1);
        set.add(2);
        set.add(3);
        System.out.println("Set类型: " + set.getClass().getSimpleName());
        System.out.println("Set元素: " + set);
        
        // Map
        var map = new HashMap<String, Integer>();
        map.put("Java", 10);
        map.put("Python", 3);
        map.put("C++", 4);
        System.out.println("Map类型: " + map.getClass().getSimpleName());
        System.out.println("Map元素: " + map);
    }

    /**
     * 循环中的局部变量类型推断
     */
    public void loopTypes() {
        System.out.println("\n=== 循环中的局部变量类型推断 ===");
        
        // for循环
        var numbers = List.of(1, 2, 3, 4, 5);
        System.out.println("for循环:");
        for (var i = 0; i < numbers.size(); i++) {
            System.out.println("索引 " + i + ": " + numbers.get(i));
        }
        
        // 增强for循环
        System.out.println("\n增强for循环:");
        for (var number : numbers) {
            System.out.println("元素: " + number);
        }
        
        // forEach循环
        System.out.println("\nforEach循环:");
        numbers.forEach(var number -> System.out.println("元素: " + number));
    }

    /**
     * 方法返回值的局部变量类型推断
     */
    public void methodReturnType() {
        System.out.println("\n=== 方法返回值的局部变量类型推断 ===");
        
        // 方法返回值
        var result = calculateSum(10, 20);
        System.out.println("方法返回值: " + result + ", 类型: " + result.getClass().getSimpleName());
        
        // 复杂返回值
        var person = createPerson("Alice", 30);
        System.out.println("复杂返回值: " + person + ", 类型: " + person.getClass().getSimpleName());
    }

    /**
     * Stream操作中的局部变量类型推断
     */
    public void streamOperations() {
        System.out.println("\n=== Stream操作中的局部变量类型推断 ===");
        
        var numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Stream操作
        var evenNumbers = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println("Stream操作结果: " + evenNumbers + ", 类型: " + evenNumbers.getClass().getSimpleName());
        
        // 复杂Stream操作
        var sum = numbers.stream()
                .filter(n -> n % 2 != 0)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("复杂Stream操作结果: " + sum + ", 类型: " + (sum.getClass() != null ? sum.getClass().getSimpleName() : "int"));
    }

    /**
     * 局部变量类型推断的限制
     */
    public void varLimitations() {
        System.out.println("\n=== 局部变量类型推断的限制 ===");
        System.out.println("1. var只能用于局部变量，不能用于字段");
        System.out.println("2. var不能用于方法参数");
        System.out.println("3. var不能用于方法返回类型");
        System.out.println("4. var不能用于数组声明的左侧");
        System.out.println("5. var变量必须初始化");
        System.out.println("6. var不能用于lambda参数（JDK 11+可以）");
        
        // 以下是错误示例（已注释）
        /*
        // 错误: var不能用于字段
        // private var field = "value";
        
        // 错误: var不能用于方法参数
        // public void method(var param) {}
        
        // 错误: var不能用于方法返回类型
        // public var method() { return 10; }
        
        // 错误: var不能用于数组声明的左侧
        // var[] array = new int[5]; // 正确写法: var array = new int[5];
        
        // 错误: var变量必须初始化
        // var uninitialized; // 必须初始化
        */
    }

    /**
     * 辅助方法：计算两个数的和
     */
    private int calculateSum(int a, int b) {
        return a + b;
    }

    /**
     * 辅助方法：创建Person对象
     */
    private Person createPerson(String name, int age) {
        return new Person(name, age);
    }

    /**
     * 内部类：Person
     */
    private static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
