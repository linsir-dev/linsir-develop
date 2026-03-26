package com.linsir.abc.core.grammar.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * 泛型类示例
 *
 * 本类演示 Java 泛型类的定义和使用
 * 对应 JDK: java.util 泛型集合
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 * @param <T> 泛型类型参数
 */
public class GenericClass<T> {

    private T data;

    public GenericClass(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 演示基本泛型类
     */
    public static void demonstrateGenericClass() {
        System.out.println("=== 泛型类基本用法 ===");

        // 泛型类存储 Integer
        GenericClass<Integer> intBox = new GenericClass<>(100);
        System.out.println("Integer 类型: " + intBox.getData());

        // 泛型类存储 String
        GenericClass<String> strBox = new GenericClass<>("Hello");
        System.out.println("String 类型: " + strBox.getData());

        // 泛型类存储自定义类型
        GenericClass<Person> personBox = new GenericClass<>(new Person("张三", 25));
        System.out.println("Person 类型: " + personBox.getData());
    }

    /**
     * 演示泛型集合
     */
    public static void demonstrateGenericCollection() {
        System.out.println("\n=== 泛型集合 ===");

        // 类型安全的 List
        List<String> names = new ArrayList<>();
        names.add("张三");
        names.add("李四");
        // names.add(123);  // 编译错误！

        System.out.println("String 列表:");
        for (String name : names) {
            System.out.println("  " + name);
        }

        // 类型安全的自定义对象列表
        List<Person> people = new ArrayList<>();
        people.add(new Person("王五", 30));
        people.add(new Person("赵六", 35));

        System.out.println("\nPerson 列表:");
        for (Person p : people) {
            System.out.println("  " + p);
        }
    }

    /**
     * 演示多个类型参数
     */
    public static void demonstrateMultipleTypeParameters() {
        System.out.println("\n=== 多个类型参数 ===");

        Pair<String, Integer> pair = new Pair<>("年龄", 25);
        System.out.println("Key: " + pair.getKey() + ", Value: " + pair.getValue());

        Triple<String, Integer, Double> triple = new Triple<>("张三", 25, 85.5);
        System.out.println("姓名: " + triple.getFirst() + 
                          ", 年龄: " + triple.getSecond() + 
                          ", 成绩: " + triple.getThird());
    }

    /**
     * 演示泛型边界
     */
    public static void demonstrateBoundedTypes() {
        System.out.println("\n=== 泛型边界 ===");

        // 上界通配符
        Box<Number> numberBox = new Box<>(100);
        Box<Integer> intBox = new Box<>(200);
        Box<Double> doubleBox = new Box<>(3.14);

        System.out.println("Number Box: " + numberBox.getValue());
        System.out.println("Integer Box: " + intBox.getValue());
        System.out.println("Double Box: " + doubleBox.getValue());

        // 使用上界
        printBox(numberBox);
        printBox(intBox);
        printBox(doubleBox);
    }

    private static void printBox(Box<? extends Number> box) {
        System.out.println("Box value: " + box.getValue());
    }

    /**
     * 泛型对类
     */
    static class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }
    }

    /**
     * 泛型三元组
     */
    static class Triple<A, B, C> {
        private A first;
        private B second;
        private C third;

        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        public A getFirst() { return first; }
        public B getSecond() { return second; }
        public C getThird() { return third; }
    }

    /**
     * 带边界的泛型类
     */
    static class Box<T extends Number> {
        private T value;

        public Box(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }

        public double doubleValue() {
            return value.doubleValue();
        }
    }

    /**
     * Person类
     */
    static class Person {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 泛型类演示                            ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        demonstrateGenericClass();
        demonstrateGenericCollection();
        demonstrateMultipleTypeParameters();
        demonstrateBoundedTypes();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
