package com.linsir.abc.pdai.tools.guava;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

/**
 * Guava 字符串工具类示例
 * 演示 Guava 提供的强大字符串处理工具
 */
public class GuavaStringsDemo {

    /**
     * 演示 CharMatcher 工具类
     */
    public static void demonstrateCharMatcher() {
        System.out.println("=== CharMatcher 工具类示例 ===");
        
        String text = "  Hello, Guava!  \n\t  ";
        System.out.println("原始文本: '" + text + "'");
        
        // 移除空白字符
        String stripped = CharMatcher.whitespace().removeFrom(text);
        System.out.println("移除空白字符: '" + stripped + "'");
        
        // 保留数字和字母
        String alphanumeric = CharMatcher.javaLetterOrDigit().retainFrom(text);
        System.out.println("保留数字和字母: '" + alphanumeric + "'");
        
        // 替换空白字符为单个空格
        String normalized = CharMatcher.whitespace().collapseFrom(text, ' ').trim();
        System.out.println("规范化空白字符: '" + normalized + "'");
        
        // 移除控制字符
        String noControl = CharMatcher.javaIsoControl().removeFrom(text);
        System.out.println("移除控制字符: '" + noControl + "'");
        
        // 保留 ASCII 字符
        String asciiOnly = CharMatcher.ascii().retainFrom(text);
        System.out.println("保留 ASCII 字符: '" + asciiOnly + "'");
    }

    /**
     * 演示 Splitter 工具类
     */
    public static void demonstrateSplitter() {
        System.out.println("\n=== Splitter 工具类示例 ===");
        
        // 基本分割
        String csv = "apple,banana,orange,grape";
        List<String> fruits = Splitter.on(",").splitToList(csv);
        System.out.println("基本分割: " + fruits);
        
        // 分割并忽略空字符串
        String csvWithEmpty = "apple,,banana,,orange";
        List<String> fruitsNoEmpty = Splitter.on(",").omitEmptyStrings().splitToList(csvWithEmpty);
        System.out.println("分割并忽略空字符串: " + fruitsNoEmpty);
        
        // 分割并去除空白
        String csvWithSpaces = "apple, banana, orange, grape";
        List<String> fruitsTrimmed = Splitter.on(",").trimResults().splitToList(csvWithSpaces);
        System.out.println("分割并去除空白: " + fruitsTrimmed);
        
        // 分割并同时忽略空字符串和去除空白
        List<String> fruitsClean = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(csvWithEmpty);
        System.out.println("分割并清理: " + fruitsClean);
        
        // 使用正则表达式分割
        String textWithDelimiters = "apple;banana,orange|grape";
        List<String> mixedFruits = Splitter.onPattern("[;,|]").splitToList(textWithDelimiters);
        System.out.println("使用正则表达式分割: " + mixedFruits);
        
        // 固定长度分割
        String longText = "HelloGuavaWorld";
        List<String> fixedLength = Splitter.fixedLength(5).splitToList(longText);
        System.out.println("固定长度分割: " + fixedLength);
        
        // 分割为映射
        String mapText = "a=1,b=2,c=3";
        Map<String, String> map = Splitter.on(",").withKeyValueSeparator("=").split(mapText);
        System.out.println("分割为映射: " + map);
    }

    /**
     * 演示 Joiner 工具类
     */
    public static void demonstrateJoiner() {
        System.out.println("\n=== Joiner 工具类示例 ===");
        
        // 基本连接
        List<String> fruits = Lists.newArrayList("apple", "banana", "orange");
        String joined = Joiner.on(", ").join(fruits);
        System.out.println("基本连接: " + joined);
        
        // 处理 null 值（替换为指定字符串）
        List<String> fruitsWithNull = Lists.newArrayList("apple", null, "banana", null, "orange");
        String joinedWithNullReplacement = Joiner.on(", ").useForNull("N/A").join(fruitsWithNull);
        System.out.println("处理 null 值（替换）: " + joinedWithNullReplacement);
        
        // 处理 null 值（跳过）
        String joinedSkippingNulls = Joiner.on(", ").skipNulls().join(fruitsWithNull);
        System.out.println("处理 null 值（跳过）: " + joinedSkippingNulls);
        
        // 连接映射
        Map<String, Integer> map = com.google.common.collect.Maps.newHashMap();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        String joinedMap = Joiner.on(", ").withKeyValueSeparator("=").join(map);
        System.out.println("连接映射: " + joinedMap);
        
        // 构建器模式
        StringBuilder sb = new StringBuilder("Fruits: ");
        Joiner.on(", ").appendTo(sb, fruits);
        System.out.println("使用构建器模式: " + sb.toString());
    }

    /**
     * 演示 Strings 工具类
     */
    public static void demonstrateStrings() {
        System.out.println("\n=== Strings 工具类示例 ===");
        
        // 判空
        String nullStr = null;
        String emptyStr = "";
        String normalStr = "Guava";
        System.out.println("Strings.isNullOrEmpty(null): " + Strings.isNullOrEmpty(nullStr));
        System.out.println("Strings.isNullOrEmpty(''): " + Strings.isNullOrEmpty(emptyStr));
        System.out.println("Strings.isNullOrEmpty('Guava'): " + Strings.isNullOrEmpty(normalStr));
        
        // 填充
        String paddedStart = Strings.padStart("123", 5, '0');
        System.out.println("左侧填充: '" + paddedStart + "'");
        
        String paddedEnd = Strings.padEnd("123", 5, '0');
        System.out.println("右侧填充: '" + paddedEnd + "'");
        
        // 重复
        String repeated = Strings.repeat("Guava", 3);
        System.out.println("重复字符串: '" + repeated + "'");
        
        // 公共前缀
        String commonPrefix = Strings.commonPrefix("Guava", "Google");
        System.out.println("公共前缀: '" + commonPrefix + "'");
        
        // 公共后缀
        String commonSuffix = Strings.commonSuffix("Guava", "Java");
        System.out.println("公共后缀: '" + commonSuffix + "'");
    }

    /**
     * 演示 CaseFormat 工具类
     */
    public static void demonstrateCaseFormat() {
        System.out.println("\n=== CaseFormat 工具类示例 ===");
        
        // 驼峰命名转下划线命名
        String camelCase = "helloGuavaWorld";
        String lowerUnderscore = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelCase);
        System.out.println("驼峰转下划线: '" + camelCase + "' -> '" + lowerUnderscore + "'");
        
        // 下划线命名转驼峰命名
        String upperUnderscore = "HELLO_GUAVA_WORLD";
        String upperCamel = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, upperUnderscore);
        System.out.println("下划线转驼峰: '" + upperUnderscore + "' -> '" + upperCamel + "'");
        
        // 驼峰命名转连字符命名
        String hyphenated = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, camelCase);
        System.out.println("驼峰转连字符: '" + camelCase + "' -> '" + hyphenated + "'");
        
        // 连字符命名转下划线命名
        String hyphenatedStr = "hello-guava-world";
        String underscoreStr = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_UNDERSCORE, hyphenatedStr);
        System.out.println("连字符转下划线: '" + hyphenatedStr + "' -> '" + underscoreStr + "'");
        
        // 常量命名转小驼峰命名
        String constant = "MAX_VALUE";
        String lowerCamel = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, constant);
        System.out.println("常量转小驼峰: '" + constant + "' -> '" + lowerCamel + "'");
    }

    /**
     * 演示字符串处理综合示例
     */
    public static void demonstrateStringProcessing() {
        System.out.println("\n=== 字符串处理综合示例 ===");
        
        // 处理用户输入
        String userInput = "  user_id=123,  name=John Doe,  email=john@example.com  ";
        System.out.println("原始用户输入: '" + userInput + "'");
        
        // 分割并转换为映射
        Map<String, String> userMap = Splitter.on(",")
                .trimResults()
                .omitEmptyStrings()
                .withKeyValueSeparator("=")
                .split(userInput);
        System.out.println("转换为映射: " + userMap);
        
        // 构建查询字符串
        String queryString = Joiner.on("&")
                .withKeyValueSeparator("=")
                .join(userMap);
        System.out.println("构建查询字符串: '" + queryString + "'");
        
        // 处理日志消息
        String logMessage = Strings.lenientFormat("User %s with ID %s logged in from %s", 
                userMap.get("name"), userMap.get("user_id"), "192.168.1.1");
        System.out.println("格式化日志消息: '" + logMessage + "'");
    }
}
