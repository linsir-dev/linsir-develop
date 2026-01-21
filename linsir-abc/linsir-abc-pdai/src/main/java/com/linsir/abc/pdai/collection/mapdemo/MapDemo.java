package com.linsir.abc.pdai.collection.mapdemo;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author linsir
 * @version 1.0
 * @description: Map集合示例
 * @date 2026/1/21 22:10
 */
public class MapDemo {
    public static void main(String[] args) {
        // 1. HashMap示例 - 无序，键不重复，允许null键和null值
        System.out.println("1. HashMap示例:");
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("apple", 10);
        hashMap.put("banana", 20);
        hashMap.put("orange", 15);
        hashMap.put("apple", 12); // 键重复，值会被覆盖
        hashMap.put(null, 5); // 允许null键
        hashMap.put("grape", null); // 允许null值
        
        // 遍历HashMap
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            System.out.println("键: " + entry.getKey() + ", 值: " + entry.getValue());
        }
        System.out.println();
        
        // 2. TreeMap示例 - 有序（按键自然排序），键不重复，不允许null键
        System.out.println("2. TreeMap示例:");
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("banana", 20);
        treeMap.put("apple", 10);
        treeMap.put("orange", 15);
        treeMap.put("grape", 25);
        // treeMap.put(null, 5); // 不允许null键，会抛出NullPointerException
        
        // 遍历TreeMap（会按键的自然顺序排序）
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            System.out.println("键: " + entry.getKey() + ", 值: " + entry.getValue());
        }
        System.out.println();
        
        // 3. LinkedHashMap示例 - 有序（按插入顺序），键不重复，允许null键和null值
        System.out.println("3. LinkedHashMap示例:");
        Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("banana", 20);
        linkedHashMap.put("apple", 10);
        linkedHashMap.put("orange", 15);
        linkedHashMap.put("grape", 25);
        linkedHashMap.put(null, 5); // 允许null键
        linkedHashMap.put("pear", null); // 允许null值
        
        // 遍历LinkedHashMap（会按插入顺序显示）
        for (Map.Entry<String, Integer> entry : linkedHashMap.entrySet()) {
            System.out.println("键: " + entry.getKey() + ", 值: " + entry.getValue());
        }
        System.out.println();
        
        // 4. Hashtable示例 - 线程安全，键不重复，不允许null键和null值
        System.out.println("4. Hashtable示例:");
        Map<String, Integer> hashtable = new Hashtable<>();
        hashtable.put("banana", 20);
        hashtable.put("apple", 10);
        hashtable.put("orange", 15);
        // hashtable.put(null, 5); // 不允许null键，会抛出NullPointerException
        // hashtable.put("grape", null); // 不允许null值，会抛出NullPointerException
        
        // 遍历Hashtable
        for (Map.Entry<String, Integer> entry : hashtable.entrySet()) {
            System.out.println("键: " + entry.getKey() + ", 值: " + entry.getValue());
        }
        System.out.println();
        
        // 5. ConcurrentHashMap示例 - 线程安全，键不重复，不允许null键和null值
        System.out.println("5. ConcurrentHashMap示例:");
        Map<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put("banana", 20);
        concurrentHashMap.put("apple", 10);
        concurrentHashMap.put("orange", 15);
        // concurrentHashMap.put(null, 5); // 不允许null键，会抛出NullPointerException
        // concurrentHashMap.put("grape", null); // 不允许null值，会抛出NullPointerException
        
        // 遍历ConcurrentHashMap
        for (Map.Entry<String, Integer> entry : concurrentHashMap.entrySet()) {
            System.out.println("键: " + entry.getKey() + ", 值: " + entry.getValue());
        }
        System.out.println();
        
        // 6. Map的常用方法示例
        System.out.println("6. Map的常用方法示例:");
        Map<String, Integer> demoMap = new HashMap<>();
        demoMap.put("a", 1);
        demoMap.put("b", 2);
        demoMap.put("c", 3);
        
        System.out.println("获取键'a'的值: " + demoMap.get("a"));
        System.out.println("判断是否包含键'b': " + demoMap.containsKey("b"));
        System.out.println("判断是否包含值3: " + demoMap.containsValue(3));
        System.out.println("Map的大小: " + demoMap.size());
        System.out.println("移除键'c': " + demoMap.remove("c"));
        System.out.println("移除后Map的大小: " + demoMap.size());
        System.out.println("清空Map: ");
        demoMap.clear();
        System.out.println("清空后Map是否为空: " + demoMap.isEmpty());
    }
}