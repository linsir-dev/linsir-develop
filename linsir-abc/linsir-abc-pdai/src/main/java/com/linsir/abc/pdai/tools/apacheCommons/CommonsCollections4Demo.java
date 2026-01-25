package com.linsir.abc.pdai.tools.apacheCommons;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.map.LRUMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Apache Commons Collections4 示例代码
 * commons-collections4 提供了丰富的集合工具类和扩展集合实现
 */
public class CommonsCollections4Demo {

    /**
     * 演示 CollectionUtils 工具类的使用
     */
    public static void demonstrateCollectionUtils() {
        System.out.println("=== CollectionUtils 示例 ===");
        
        List<String> list1 = new ArrayList<>();
        list1.add("apple");
        list1.add("banana");
        list1.add("orange");
        
        List<String> list2 = new ArrayList<>();
        list2.add("banana");
        list2.add("grape");
        list2.add("pineapple");
        
        // 集合操作
        System.out.println("CollectionUtils.isEmpty(list1): " + CollectionUtils.isEmpty(list1));
        System.out.println("CollectionUtils.isNotEmpty(list1): " + CollectionUtils.isNotEmpty(list1));
        
        // 集合交集
        java.util.Collection<String> intersection = CollectionUtils.intersection(list1, list2);
        System.out.println("CollectionUtils.intersection(list1, list2): " + intersection);
        
        // 集合并集
        java.util.Collection<String> union = CollectionUtils.union(list1, list2);
        System.out.println("CollectionUtils.union(list1, list2): " + union);
        
        // 集合差集
        java.util.Collection<String> subtract = CollectionUtils.subtract(list1, list2);
        System.out.println("CollectionUtils.subtract(list1, list2): " + subtract);
        
        // 集合是否包含
        System.out.println("CollectionUtils.containsAny(list1, list2): " + CollectionUtils.containsAny(list1, list2));
        System.out.println("CollectionUtils.containsAll(list1, list2): " + CollectionUtils.containsAll(list1, list2));
    }

    /**
     * 演示 ListUtils 工具类的使用
     */
    public static void demonstrateListUtils() {
        System.out.println("\n=== ListUtils 示例 ===");
        
        List<String> list1 = new ArrayList<>();
        list1.add("apple");
        list1.add("banana");
        
        List<String> list2 = new ArrayList<>();
        list2.add("orange");
        list2.add("grape");
        
        // 列表操作
        List<String> combined = ListUtils.union(list1, list2);
        System.out.println("ListUtils.union(list1, list2): " + combined);
        
        // 列表分割
        List<List<String>> partitioned = ListUtils.partition(combined, 2);
        System.out.println("ListUtils.partition(combined, 2): " + partitioned);
        
        // 列表操作示例
        System.out.println("ListUtils.emptyIfNull(null): " + ListUtils.emptyIfNull(null));
        System.out.println("ListUtils.isEqualList(list1, list2): " + ListUtils.isEqualList(list1, list2));
    }

    /**
     * 演示 MapUtils 工具类的使用
     */
    public static void demonstrateMapUtils() {
        System.out.println("\n=== MapUtils 示例 ===");
        
        Map<String, Integer> map = new HashMap<>();
        map.put("apple", 10);
        map.put("banana", 20);
        
        // Map 操作
        System.out.println("MapUtils.isEmpty(map): " + MapUtils.isEmpty(map));
        System.out.println("MapUtils.isNotEmpty(map): " + MapUtils.isNotEmpty(map));
        
        // 获取值，带默认值
        System.out.println("MapUtils.getIntValue(map, \"apple\"): " + MapUtils.getIntValue(map, "apple"));
        System.out.println("MapUtils.getIntValue(map, \"orange\", 0): " + MapUtils.getIntValue(map, "orange", 0));
        
        // Map 操作示例
        Map<String, Integer> map2 = new HashMap<>();
        map2.put("banana", 30);
        map2.put("orange", 40);
        
        // 创建新的合并后的 Map
        Map<String, Integer> merged = new HashMap<>(map);
        merged.putAll(map2);
        System.out.println("Map merged with putAll: " + merged);
        
        System.out.println("MapUtils.emptyIfNull(null): " + MapUtils.emptyIfNull(null));
        System.out.println("MapUtils.isNotEmpty(map): " + MapUtils.isNotEmpty(map));
    }

    /**
     * 演示 BidiMap 的使用
     */
    public static void demonstrateBidiMap() {
        System.out.println("\n=== BidiMap 示例 ===");
        
        // 创建双向映射
        BidiMap<String, Integer> bidiMap = new TreeBidiMap<>();
        bidiMap.put("one", 1);
        bidiMap.put("two", 2);
        bidiMap.put("three", 3);
        
        // 正向查找
        System.out.println("bidiMap.get(\"one\"): " + bidiMap.get("one"));
        
        // 反向查找
        System.out.println("bidiMap.getKey(1): " + bidiMap.getKey(1));
        
        // 移除元素
        bidiMap.removeValue(2);
        System.out.println("bidiMap after removeValue(2): " + bidiMap);
    }

    /**
     * 演示 LRUMap 的使用
     */
    public static void demonstrateLRUMap() {
        System.out.println("\n=== LRUMap 示例 ===");
        
        // 创建 LRU 缓存，最大容量为 3
        LRUMap<String, String> lruMap = new LRUMap<>(3);
        
        // 添加元素
        lruMap.put("key1", "value1");
        lruMap.put("key2", "value2");
        lruMap.put("key3", "value3");
        System.out.println("LRUMap after adding 3 elements: " + lruMap);
        
        // 访问元素，使其成为最近使用的
        lruMap.get("key1");
        System.out.println("LRUMap after accessing key1: " + lruMap);
        
        // 添加第四个元素，会移除最久未使用的 key2
        lruMap.put("key4", "value4");
        System.out.println("LRUMap after adding key4: " + lruMap);
    }

    /**
     * 演示 HashedMap 的使用
     */
    public static void demonstrateHashedMap() {
        System.out.println("\n=== HashedMap 示例 ===");
        
        // 创建 HashedMap
        HashedMap<String, Integer> hashedMap = new HashedMap<>();
        hashedMap.put("apple", 10);
        hashedMap.put("banana", 20);
        hashedMap.put("orange", 30);
        
        System.out.println("HashedMap: " + hashedMap);
        System.out.println("hashedMap.containsKey(\"apple\"): " + hashedMap.containsKey("apple"));
        System.out.println("hashedMap.containsValue(20): " + hashedMap.containsValue(20));
    }
}
