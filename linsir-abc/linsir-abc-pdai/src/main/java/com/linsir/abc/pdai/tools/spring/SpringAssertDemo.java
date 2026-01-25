package com.linsir.abc.pdai.tools.spring;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring Assert 工具类示例
 * 演示 Assert 的常用方法
 */
public class SpringAssertDemo {

    /**
     * 演示 Assert 的常用方法
     */
    public static void demonstrateAssert() {
        // 1. notNull 方法 - 断言对象不为 null
        System.out.println("=== notNull 方法 ===");
        try {
            String str1 = "Hello";
            Assert.notNull(str1, "String must not be null");
            System.out.println("Assert.notNull 测试通过 (非 null 值)");
            
            String str2 = null;
            Assert.notNull(str2, "String must not be null");
            System.out.println("Assert.notNull 测试通过 (null 值)"); // 不会执行到这里
        } catch (IllegalArgumentException e) {
            System.out.println("Assert.notNull 预期异常: " + e.getMessage());
        }
        
        // 2. isNull 方法 - 断言对象为 null
        System.out.println("\n=== isNull 方法 ===");
        try {
            String str1 = null;
            Assert.isNull(str1, "String must be null");
            System.out.println("Assert.isNull 测试通过 (null 值)");
            
            String str2 = "Hello";
            Assert.isNull(str2, "String must be null");
            System.out.println("Assert.isNull 测试通过 (非 null 值)"); // 不会执行到这里
        } catch (IllegalArgumentException e) {
            System.out.println("Assert.isNull 预期异常: " + e.getMessage());
        }
        
        // 3. isTrue 方法 - 断言表达式为 true
        System.out.println("\n=== isTrue 方法 ===");
        try {
            int age1 = 18;
            Assert.isTrue(age1 >= 18, "Age must be at least 18");
            System.out.println("Assert.isTrue 测试通过 (表达式为 true)");
            
            int age2 = 17;
            Assert.isTrue(age2 >= 18, "Age must be at least 18");
            System.out.println("Assert.isTrue 测试通过 (表达式为 false)"); // 不会执行到这里
        } catch (IllegalArgumentException e) {
            System.out.println("Assert.isTrue 预期异常: " + e.getMessage());
        }
        
        // 4. hasLength 方法 - 断言字符串有长度
        System.out.println("\n=== hasLength 方法 ===");
        try {
            String str1 = "Hello";
            Assert.hasLength(str1, "String must have length");
            System.out.println("Assert.hasLength 测试通过 (字符串有长度)");
            
            String str2 = "";
            Assert.hasLength(str2, "String must have length");
            System.out.println("Assert.hasLength 测试通过 (字符串无长度)"); // 不会执行到这里
        } catch (IllegalArgumentException e) {
            System.out.println("Assert.hasLength 预期异常: " + e.getMessage());
        }
        
        // 5. hasText 方法 - 断言字符串有文本内容
        System.out.println("\n=== hasText 方法 ===");
        try {
            String str1 = "Hello";
            Assert.hasText(str1, "String must have text");
            System.out.println("Assert.hasText 测试通过 (字符串有文本内容)");
            
            String str2 = "   ";
            Assert.hasText(str2, "String must have text");
            System.out.println("Assert.hasText 测试通过 (字符串只有空白)"); // 不会执行到这里
        } catch (IllegalArgumentException e) {
            System.out.println("Assert.hasText 预期异常: " + e.getMessage());
        }
        
        // 6. notEmpty 方法 - 断言集合不为空
        System.out.println("\n=== notEmpty 方法 (集合) ===");
        try {
            List<String> list1 = new ArrayList<>();
            list1.add("a");
            Assert.notEmpty(list1, "List must not be empty");
            System.out.println("Assert.notEmpty 测试通过 (集合非空)");
            
            List<String> list2 = new ArrayList<>();
            Assert.notEmpty(list2, "List must not be empty");
            System.out.println("Assert.notEmpty 测试通过 (集合为空)"); // 不会执行到这里
        } catch (IllegalArgumentException e) {
            System.out.println("Assert.notEmpty 预期异常: " + e.getMessage());
        }
        
        // 7. notEmpty 方法 - 断言 Map 不为空
        System.out.println("\n=== notEmpty 方法 (Map) ===");
        try {
            Map<String, String> map1 = new HashMap<>();
            map1.put("key", "value");
            Assert.notEmpty(map1, "Map must not be empty");
            System.out.println("Assert.notEmpty 测试通过 (Map 非空)");
            
            Map<String, String> map2 = new HashMap<>();
            Assert.notEmpty(map2, "Map must not be empty");
            System.out.println("Assert.notEmpty 测试通过 (Map 为空)"); // 不会执行到这里
        } catch (IllegalArgumentException e) {
            System.out.println("Assert.notEmpty 预期异常: " + e.getMessage());
        }
        
        // 8. isInstanceOf 方法 - 断言对象是指定类型
        System.out.println("\n=== isInstanceOf 方法 ===");
        try {
            Integer num1 = 10;
            Assert.isInstanceOf(Number.class, num1, "Object must be an instance of Number");
            System.out.println("Assert.isInstanceOf 测试通过 (类型匹配)");
            
            String str1 = "Hello";
            Assert.isInstanceOf(Number.class, str1, "Object must be an instance of Number");
            System.out.println("Assert.isInstanceOf 测试通过 (类型不匹配)"); // 不会执行到这里
        } catch (IllegalArgumentException e) {
            System.out.println("Assert.isInstanceOf 预期异常: " + e.getMessage());
        }
        
        // 9. 实际应用示例
        System.out.println("\n=== 实际应用示例 ===");
        try {
            // 模拟用户注册
            registerUser("John", "john@example.com", 25);
            System.out.println("用户注册成功");
            
            // 模拟无效用户注册
            registerUser(null, "john@example.com", 25);
            System.out.println("无效用户注册成功"); // 不会执行到这里
        } catch (IllegalArgumentException e) {
            System.out.println("用户注册失败: " + e.getMessage());
        }
    }

    /**
     * 模拟用户注册方法，使用 Assert 进行参数验证
     * @param username 用户名
     * @param email 邮箱
     * @param age 年龄
     */
    private static void registerUser(String username, String email, int age) {
        // 验证用户名
        Assert.notNull(username, "Username must not be null");
        Assert.hasText(username, "Username must not be blank");
        
        // 验证邮箱
        Assert.notNull(email, "Email must not be null");
        Assert.hasText(email, "Email must not be blank");
        Assert.isTrue(email.contains("@"), "Email must be valid");
        
        // 验证年龄
        Assert.isTrue(age >= 18, "Age must be at least 18");
        
        // 实际注册逻辑...
        System.out.println("注册用户: " + username + ", Email: " + email + ", Age: " + age);
    }
}
