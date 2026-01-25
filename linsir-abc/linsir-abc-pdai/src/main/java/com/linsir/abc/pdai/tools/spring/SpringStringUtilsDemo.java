package com.linsir.abc.pdai.tools.spring;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Spring StringUtils 工具类示例
 * 演示 StringUtils 的常用方法
 */
public class SpringStringUtilsDemo {

    /**
     * 演示 StringUtils 的常用方法
     */
    public static void demonstrateStringUtils() {
        // 1. 字符串判空方法
        System.out.println("=== 字符串判空方法 ===");
        String str1 = "Hello";
        String str2 = "";
        String str3 = "   ";
        String str4 = null;
        
        System.out.println("StringUtils.hasLength(str1): " + StringUtils.hasLength(str1)); // true
        System.out.println("StringUtils.hasLength(str2): " + StringUtils.hasLength(str2)); // false
        System.out.println("StringUtils.hasLength(str3): " + StringUtils.hasLength(str3)); // true
        System.out.println("StringUtils.hasLength(str4): " + StringUtils.hasLength(str4)); // false
        
        System.out.println("StringUtils.hasText(str1): " + StringUtils.hasText(str1)); // true
        System.out.println("StringUtils.hasText(str2): " + StringUtils.hasText(str2)); // false
        System.out.println("StringUtils.hasText(str3): " + StringUtils.hasText(str3)); // false
        System.out.println("StringUtils.hasText(str4): " + StringUtils.hasText(str4)); // false
        
        System.out.println("StringUtils.isEmpty(str1): " + StringUtils.isEmpty(str1)); // false
        System.out.println("StringUtils.isEmpty(str2): " + StringUtils.isEmpty(str2)); // true
        System.out.println("StringUtils.isEmpty(str4): " + StringUtils.isEmpty(str4)); // true
        
        // 2. 字符串修剪方法
        System.out.println("\n=== 字符串修剪方法 ===");
        String strWithWhitespace = "   Hello World   ";
        System.out.println("原始字符串: '" + strWithWhitespace + "'");
        System.out.println("StringUtils.trimWhitespace: '" + StringUtils.trimWhitespace(strWithWhitespace) + "'");
        
        String strWithAllWhitespace = "H e l l o   W o r l d";
        System.out.println("原始字符串: '" + strWithAllWhitespace + "'");
        System.out.println("StringUtils.trimAllWhitespace: '" + StringUtils.trimAllWhitespace(strWithAllWhitespace) + "'");
        
        // 3. 字符串分隔和连接方法
        System.out.println("\n=== 字符串分隔和连接方法 ===");
        String commaDelimited = "apple,banana,orange";
        String[] fruits = StringUtils.commaDelimitedListToStringArray(commaDelimited);
        System.out.println("逗号分隔字符串转数组: " + Arrays.toString(fruits));
        
        List<String> vegetables = Arrays.asList("carrot", "tomato", "potato");
        String delimitedString = StringUtils.collectionToDelimitedString(vegetables, ",");
        System.out.println("集合转分隔字符串: " + delimitedString);
        
        // 4. 字符串替换方法
        System.out.println("\n=== 字符串替换方法 ===");
        String template = "Hello {name}, welcome to {place}!";
        String replaced = StringUtils.replace(template, "{name}", "John");
        replaced = StringUtils.replace(replaced, "{place}", "Spring World");
        System.out.println("替换后的字符串: " + replaced);
        
        // 5. 字符串前缀和后缀检查方法
        System.out.println("\n=== 字符串前缀和后缀检查方法 ===");
        String filename = "example.txt";
        System.out.println("StringUtils.startsWithIgnoreCase(filename, 'EX'): " + StringUtils.startsWithIgnoreCase(filename, "EX")); // true
        System.out.println("StringUtils.endsWithIgnoreCase(filename, '.TXT'): " + StringUtils.endsWithIgnoreCase(filename, ".TXT")); // true
        
        // 6. 字符串大小写转换方法
        System.out.println("\n=== 字符串大小写转换方法 ===");
        String lowercase = "hello world";
        String uppercase = "HELLO WORLD";
        System.out.println("StringUtils.capitalize(lowercase): " + StringUtils.capitalize(lowercase)); // Hello world
        System.out.println("StringUtils.uncapitalize(uppercase): " + StringUtils.uncapitalize(uppercase)); // hELLO WORLD
        
        // 7. 其他常用方法
        System.out.println("\n=== 其他常用方法 ===");
        String nullString = null;
        String defaultString = nullString != null ? nullString : "Default Value";
        System.out.println("Default string for null: " + defaultString);
        
        String[] array = {"a", "b", "c"};
        String joined = StringUtils.arrayToDelimitedString(array, "-");
        System.out.println("StringUtils.arrayToDelimitedString: " + joined);
    }
}
