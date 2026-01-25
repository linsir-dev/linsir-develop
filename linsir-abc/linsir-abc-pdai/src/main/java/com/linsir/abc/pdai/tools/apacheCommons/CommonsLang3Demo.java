package com.linsir.abc.pdai.tools.apacheCommons;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;
import java.util.Arrays;
import java.util.List;

/**
 * Apache Commons Lang3 示例代码
 * commons-lang3 提供了丰富的字符串、数字、日期等常用工具类
 */
public class CommonsLang3Demo {

    /**
     * 演示 StringUtils 工具类的使用
     */
    public static void demonstrateStringUtils() {
        System.out.println("=== StringUtils 示例 ===");
        
        // 字符串判空
        String emptyStr = "";
        String nullStr = null;
        String normalStr = "Hello, Apache Commons!";
        
        System.out.println("StringUtils.isEmpty(emptyStr): " + StringUtils.isEmpty(emptyStr));
        System.out.println("StringUtils.isEmpty(nullStr): " + StringUtils.isEmpty(nullStr));
        System.out.println("StringUtils.isEmpty(normalStr): " + StringUtils.isEmpty(normalStr));
        
        System.out.println("StringUtils.isBlank('   '): " + StringUtils.isBlank("   "));
        System.out.println("StringUtils.isNotBlank(normalStr): " + StringUtils.isNotBlank(normalStr));
        
        // 字符串操作
        System.out.println("StringUtils.capitalize('hello'): " + StringUtils.capitalize("hello"));
        System.out.println("StringUtils.uncapitalize('HELLO'): " + StringUtils.uncapitalize("HELLO"));
        System.out.println("StringUtils.upperCase('hello'): " + StringUtils.upperCase("hello"));
        System.out.println("StringUtils.lowerCase('HELLO'): " + StringUtils.lowerCase("HELLO"));
        
        // 字符串截取和填充
        System.out.println("StringUtils.substring(normalStr, 7): " + StringUtils.substring(normalStr, 7));
        System.out.println("StringUtils.substring(normalStr, 7, 13): " + StringUtils.substring(normalStr, 7, 13));
        System.out.println("StringUtils.leftPad('123', 10, '0'): " + StringUtils.leftPad("123", 10, '0'));
        System.out.println("StringUtils.rightPad('123', 10, '0'): " + StringUtils.rightPad("123", 10, '0'));
        
        // 字符串分割和连接
        String csvStr = "apple,banana,orange";
        String[] fruits = StringUtils.split(csvStr, ",");
        System.out.println("StringUtils.split(csvStr, ','): " + Arrays.toString(fruits));
        
        List<String> fruitList = Arrays.asList(fruits);
        System.out.println("StringUtils.join(fruitList, '|'): " + StringUtils.join(fruitList, '|'));
    }

    /**
     * 演示 NumberUtils 工具类的使用
     */
    public static void demonstrateNumberUtils() {
        System.out.println("\n=== NumberUtils 示例 ===");
        
        // 字符串转数字
        System.out.println("NumberUtils.toInt('123'): " + NumberUtils.toInt("123"));
        System.out.println("NumberUtils.toInt('abc', 0): " + NumberUtils.toInt("abc", 0));
        System.out.println("NumberUtils.toDouble('123.45'): " + NumberUtils.toDouble("123.45"));
        
        // 数字判断
        System.out.println("NumberUtils.isDigits('123'): " + NumberUtils.isDigits("123"));
        System.out.println("NumberUtils.isDigits('123.45'): " + NumberUtils.isDigits("123.45"));
        System.out.println("NumberUtils.isParsable('123.45'): " + NumberUtils.isParsable("123.45"));
        
        // 数字比较
        int[] numbers = {5, 2, 8, 1, 9};
        System.out.println("NumberUtils.max(numbers): " + NumberUtils.max(numbers));
        System.out.println("NumberUtils.min(numbers): " + NumberUtils.min(numbers));
    }

    /**
     * 演示 DateUtils 工具类的使用
     */
    public static void demonstrateDateUtils() {
        System.out.println("\n=== DateUtils 示例 ===");
        
        Date now = new Date();
        System.out.println("当前时间: " + now);
        
        // 日期操作
        Date tomorrow = DateUtils.addDays(now, 1);
        System.out.println("明天同一时间: " + tomorrow);
        
        Date nextWeek = DateUtils.addWeeks(now, 1);
        System.out.println("下周同一时间: " + nextWeek);
        
        Date nextMonth = DateUtils.addMonths(now, 1);
        System.out.println("下月同一时间: " + nextMonth);
        
        // 日期比较
        System.out.println("DateUtils.isSameDay(now, tomorrow): " + DateUtils.isSameDay(now, tomorrow));
        System.out.println("DateUtils.truncatedCompareTo(now, tomorrow, java.util.Calendar.DAY_OF_MONTH): " + 
                DateUtils.truncatedCompareTo(now, tomorrow, java.util.Calendar.DAY_OF_MONTH));
    }

    /**
     * 演示 RandomStringUtils 工具类的使用
     */
    public static void demonstrateRandomStringUtils() {
        System.out.println("\n=== RandomStringUtils 示例 ===");
        
        // 生成随机字符串
        System.out.println("RandomStringUtils.randomAlphabetic(10): " + RandomStringUtils.randomAlphabetic(10));
        System.out.println("RandomStringUtils.randomNumeric(10): " + RandomStringUtils.randomNumeric(10));
        System.out.println("RandomStringUtils.randomAlphanumeric(10): " + RandomStringUtils.randomAlphanumeric(10));
        System.out.println("RandomStringUtils.random(10, 'abcdef123456'): " + RandomStringUtils.random(10, "abcdef123456"));
    }

    /**
     * 演示 ObjectUtils 工具类的使用
     */
    public static void demonstrateObjectUtils() {
        System.out.println("\n=== ObjectUtils 示例 ===");
        
        // 对象判空
        Object obj1 = null;
        Object obj2 = new Object();
        
        System.out.println("ObjectUtils.isEmpty(obj1): " + ObjectUtils.isEmpty(obj1));
        System.out.println("ObjectUtils.isEmpty(obj2): " + ObjectUtils.isEmpty(obj2));
        
        // 对象默认值
        System.out.println("ObjectUtils.defaultIfNull(obj1, '默认值'): " + ObjectUtils.defaultIfNull(obj1, "默认值"));
        System.out.println("ObjectUtils.defaultIfNull(obj2, '默认值'): " + ObjectUtils.defaultIfNull(obj2, "默认值"));
        
        // 对象比较
        Integer int1 = 10;
        Integer int2 = 20;
        Integer int3 = 10;
        
        System.out.println("ObjectUtils.compare(int1, int2): " + ObjectUtils.compare(int1, int2));
        System.out.println("ObjectUtils.compare(int1, int3): " + ObjectUtils.compare(int1, int3));
    }
}
