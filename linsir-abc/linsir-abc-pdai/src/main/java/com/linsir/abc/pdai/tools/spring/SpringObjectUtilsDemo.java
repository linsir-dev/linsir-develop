package com.linsir.abc.pdai.tools.spring;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;

/**
 * Spring ObjectUtils 工具类示例
 * 演示 ObjectUtils 的常用方法
 */
public class SpringObjectUtilsDemo {

    /**
     * 演示 ObjectUtils 的常用方法
     */
    public static void demonstrateObjectUtils() {
        // 1. 对象判空方法
        System.out.println("=== 对象判空方法 ===");
        Object obj1 = "Hello";
        Object obj2 = new Object();
        Object obj3 = null;
        Object obj4 = new int[0];
        int[] obj5 = new int[]{1, 2, 3};
        
        System.out.println("ObjectUtils.isEmpty(obj1): " + ObjectUtils.isEmpty(obj1)); // false
        System.out.println("ObjectUtils.isEmpty(obj2): " + ObjectUtils.isEmpty(obj2)); // false
        System.out.println("ObjectUtils.isEmpty(obj3): " + ObjectUtils.isEmpty(obj3)); // true
        System.out.println("ObjectUtils.isEmpty(obj4): " + ObjectUtils.isEmpty(obj4)); // true
        System.out.println("ObjectUtils.isEmpty(obj5): " + ObjectUtils.isEmpty(obj5)); // false
        
        // 2. 对象比较方法
        System.out.println("\n=== 对象比较方法 ===");
        String str1 = "Hello";
        String str2 = "Hello";
        String str3 = "World";
        String str4 = null;
        
        System.out.println("ObjectUtils.nullSafeEquals(str1, str2): " + ObjectUtils.nullSafeEquals(str1, str2)); // true
        System.out.println("ObjectUtils.nullSafeEquals(str1, str3): " + ObjectUtils.nullSafeEquals(str1, str3)); // false
        System.out.println("ObjectUtils.nullSafeEquals(str1, str4): " + ObjectUtils.nullSafeEquals(str1, str4)); // false
        System.out.println("ObjectUtils.nullSafeEquals(str4, str4): " + ObjectUtils.nullSafeEquals(str4, str4)); // true
        
        // 3. 对象哈希码方法
        System.out.println("\n=== 对象哈希码方法 ===");
        System.out.println("ObjectUtils.nullSafeHashCode(str1): " + ObjectUtils.nullSafeHashCode(str1));
        System.out.println("ObjectUtils.nullSafeHashCode(str4): " + ObjectUtils.nullSafeHashCode(str4)); // 0
        
        // 4. 对象身份字符串方法
        System.out.println("\n=== 对象身份字符串方法 ===");
        Object obj = new Object();
        System.out.println("ObjectUtils.identityToString(obj): " + ObjectUtils.identityToString(obj));
        System.out.println("ObjectUtils.identityToString(null): " + ObjectUtils.identityToString(null)); // "null"
        
        // 5. 对象显示字符串方法
        System.out.println("\n=== 对象显示字符串方法 ===");
        System.out.println("ObjectUtils.getDisplayString(obj): " + ObjectUtils.getDisplayString(obj));
        System.out.println("ObjectUtils.getDisplayString(str1): " + ObjectUtils.getDisplayString(str1));
        System.out.println("ObjectUtils.getDisplayString(str4): " + ObjectUtils.getDisplayString(str4)); // "null"
        
        // 6. 向数组添加对象
        System.out.println("\n=== 向数组添加对象 ===");
        String[] array1 = {"a", "b", "c"};
        String[] array2 = (String[]) ObjectUtils.addObjectToArray(array1, "d");
        System.out.println("添加对象后的数组: " + Arrays.toString(array2)); // [a, b, c, d]
        
        // 7. 转换为对象数组
        System.out.println("\n=== 转换为对象数组 ===");
        int[] intArray = {1, 2, 3};
        Object[] objectArray1 = ObjectUtils.toObjectArray(intArray);
        System.out.println("基本类型数组转换为对象数组: " + Arrays.toString(objectArray1)); // [1, 2, 3]
        
        String str = "Hello";
        Object[] objectArray2 = new Object[]{str};
        System.out.println("字符串转换为对象数组: " + Arrays.toString(objectArray2)); // [Hello]
        
        Object singleObj = new Object();
        Object[] objectArray3 = new Object[]{singleObj};
        System.out.println("单个对象转换为对象数组: " + Arrays.toString(objectArray3)); // [java.lang.Object@...]
        
        // 8. 数组长度方法
        System.out.println("\n=== 数组长度方法 ===");
        System.out.println("字符串长度: " + ((String) obj1).length()); // 5 (字符串长度)
        System.out.println("空数组长度: " + ((int[]) obj4).length); // 0 (空数组长度)
        System.out.println("数组长度: " + obj5.length); // 3 (数组长度)
        
        // 9. 数组索引检查方法
        System.out.println("\n=== 数组索引检查方法 ===");
        try {
            if (2 >= 0 && 2 < obj5.length) {
                System.out.println("索引 2 在数组长度 3 范围内: 索引有效");
            }
        } catch (Exception e) {
            System.out.println("索引检查异常: " + e.getMessage());
        }
        
        try {
            if (5 >= 0 && 5 < obj5.length) {
                System.out.println("索引 5 在数组长度 3 范围内: 索引有效");
            } else {
                System.out.println("索引 5 不在数组长度 3 范围内: 索引无效");
            }
        } catch (Exception e) {
            System.out.println("索引检查异常: " + e.getMessage());
        }
    }
}
