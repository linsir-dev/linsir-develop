package com.linsir.abc.core.jvm.threadsafety.immutable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 不可变对象示例 - ImmutablePerson
 * 
 * 演示如何创建线程安全的不可变对象：
 * 1. 类声明为final，防止子类修改行为
 * 2. 所有字段声明为private final
 * 3. 不提供修改字段的方法（无setter）
 * 4. 对可变对象的引用进行防御性拷贝
 * 5. 返回不可修改的视图或拷贝
 * 
 * 不可变对象天然线程安全，无需同步措施
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-29
 */
public final class ImmutablePerson {
    
    /**
     * 姓名 - 不可变String
     */
    private final String name;
    
    /**
     * 年龄 - 基本类型
     */
    private final int age;
    
    /**
     * 爱好列表 - 可变对象，需要防御性处理
     */
    private final List<String> hobbies;
    
    /**
     * 构造函数 - 进行防御性拷贝
     * 
     * @param name 姓名
     * @param age 年龄
     * @param hobbies 爱好列表
     */
    public ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        // 防御性拷贝：创建新的ArrayList，防止外部修改影响内部状态
        this.hobbies = new ArrayList<>(hobbies);
    }
    
    /**
     * 获取姓名
     * 
     * @return 姓名，String不可变，直接返回
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取年龄
     * 
     * @return 年龄，基本类型直接返回
     */
    public int getAge() {
        return age;
    }
    
    /**
     * 获取爱好列表 - 返回不可修改的视图
     * 
     * @return 不可修改的爱好列表视图
     */
    public List<String> getHobbies() {
        return Collections.unmodifiableList(hobbies);
    }
    
    /**
     * 获取爱好列表的拷贝
     * 
     * @return 爱好列表的拷贝
     */
    public List<String> getHobbiesCopy() {
        return new ArrayList<>(hobbies);
    }
    
    /**
     * 创建新的ImmutablePerson对象（类似修改年龄）
     * 不可变对象的"修改"操作总是返回新对象
     * 
     * @param newAge 新年龄
     * @return 新的ImmutablePerson对象
     */
    public ImmutablePerson withAge(int newAge) {
        return new ImmutablePerson(this.name, newAge, this.hobbies);
    }
    
    /**
     * 创建新的ImmutablePerson对象（类似添加爱好）
     * 
     * @param hobby 新爱好
     * @return 新的ImmutablePerson对象
     */
    public ImmutablePerson withAdditionalHobby(String hobby) {
        List<String> newHobbies = new ArrayList<>(this.hobbies);
        newHobbies.add(hobby);
        return new ImmutablePerson(this.name, this.age, newHobbies);
    }
    
    @Override
    public String toString() {
        return "ImmutablePerson{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", hobbies=" + hobbies +
                '}';
    }
    
    /**
     * 主方法 - 演示不可变对象的使用
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建不可变对象
        List<String> hobbies = new ArrayList<>();
        hobbies.add("Reading");
        hobbies.add("Coding");
        
        ImmutablePerson person = new ImmutablePerson("Alice", 25, hobbies);
        System.out.println("Original: " + person);
        
        // 尝试通过原始列表修改（防御性拷贝阻止了这种修改）
        hobbies.add("Hacking");
        System.out.println("After external modify: " + person);
        
        // 尝试修改返回的列表（不可修改视图阻止了这种修改）
        List<String> returnedHobbies = person.getHobbies();
        try {
            returnedHobbies.add("Gaming");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify unmodifiable list: " + e.getMessage());
        }
        
        // "修改"操作返回新对象，原对象不变
        ImmutablePerson olderPerson = person.withAge(26);
        System.out.println("Original after withAge: " + person);
        System.out.println("New person: " + olderPerson);
        
        // 多线程环境下的安全性演示
        System.out.println("\n=== Multi-thread Safety Demo ===");
        for (int i = 0; i < 5; i++) {
            final int threadNum = i;
            new Thread(() -> {
                ImmutablePerson p = person.withAge(25 + threadNum);
                System.out.println(Thread.currentThread().getName() + ": " + p);
            }, "Thread-" + i).start();
        }
    }
}
