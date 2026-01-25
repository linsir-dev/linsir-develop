package com.linsir.abc.pdai.tools.guava;

import com.google.common.collect.*;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Guava 集合工具类示例
 * 演示 Guava 提供的强大集合工具和新集合类型
 */
public class GuavaCollectionsDemo {

    /**
     * 演示不可变集合
     */
    public static void demonstrateImmutableCollections() {
        System.out.println("=== 不可变集合示例 ===");
        
        // 创建不可变列表
        ImmutableList<String> immutableList = ImmutableList.of("a", "b", "c");
        System.out.println("不可变列表: " + immutableList);
        
        // 创建不可变集合
        ImmutableSet<String> immutableSet = ImmutableSet.of("x", "y", "z");
        System.out.println("不可变集合: " + immutableSet);
        
        // 创建不可变映射
        ImmutableMap<String, Integer> immutableMap = ImmutableMap.of(
                "one", 1,
                "two", 2,
                "three", 3
        );
        System.out.println("不可变映射: " + immutableMap);
        
        // 创建不可变映射（使用构建器）
        ImmutableMap<String, Integer> immutableMapBuilder = ImmutableMap.<String, Integer>builder()
                .put("four", 4)
                .put("five", 5)
                .put("six", 6)
                .build();
        System.out.println("不可变映射（构建器）: " + immutableMapBuilder);
    }

    /**
     * 演示新集合类型
     */
    public static void demonstrateNewCollectionTypes() {
        System.out.println("\n=== 新集合类型示例 ===");
        
        // 演示 Multiset（允许重复元素的集合）
        System.out.println("\n1. Multiset 示例:");
        Multiset<String> multiset = HashMultiset.create();
        multiset.add("apple");
        multiset.add("banana");
        multiset.add("apple");
        multiset.add("orange");
        multiset.add("apple");
        System.out.println("Multiset 内容: " + multiset);
        System.out.println("apple 出现次数: " + multiset.count("apple"));
        
        // 演示 Multimap（一个键对应多个值的映射）
        System.out.println("\n2. Multimap 示例:");
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("fruit", "apple");
        multimap.put("fruit", "banana");
        multimap.put("fruit", "orange");
        multimap.put("vegetable", "carrot");
        multimap.put("vegetable", "tomato");
        System.out.println("Multimap 内容: " + multimap);
        System.out.println("fruit 对应的值: " + multimap.get("fruit"));
        
        // 演示 BiMap（双向映射，键和值都唯一）
        System.out.println("\n3. BiMap 示例:");
        BiMap<String, Integer> biMap = HashBiMap.create();
        biMap.put("one", 1);
        biMap.put("two", 2);
        biMap.put("three", 3);
        System.out.println("BiMap 内容: " + biMap);
        System.out.println("通过值查找键: " + biMap.inverse().get(2));
        
        // 演示 Table（二维映射，键值对的集合）
        System.out.println("\n4. Table 示例:");
        Table<String, String, Integer> table = HashBasedTable.create();
        table.put("row1", "col1", 1);
        table.put("row1", "col2", 2);
        table.put("row2", "col1", 3);
        table.put("row2", "col2", 4);
        System.out.println("Table 内容: " + table);
        System.out.println("row1, col1 的值: " + table.get("row1", "col1"));
        System.out.println("row1 的所有值: " + table.row("row1"));
    }

    /**
     * 演示集合工具类
     */
    public static void demonstrateCollectionUtilities() {
        System.out.println("\n=== 集合工具类示例 ===");
        
        // 演示 Lists 工具类
        System.out.println("\n1. Lists 工具类示例:");
        List<String> list = Lists.newArrayList("a", "b", "c", "d", "e");
        System.out.println("原始列表: " + list);
        System.out.println("列表反转: " + Lists.reverse(list));
        System.out.println("列表分割（大小为 2）: " + Lists.partition(list, 2));
        
        // 演示 Sets 工具类
        System.out.println("\n2. Sets 工具类示例:");
        Set<String> set1 = Sets.newHashSet("a", "b", "c");
        Set<String> set2 = Sets.newHashSet("b", "c", "d");
        System.out.println("集合 1: " + set1);
        System.out.println("集合 2: " + set2);
        System.out.println("交集: " + Sets.intersection(set1, set2));
        System.out.println("并集: " + Sets.union(set1, set2));
        System.out.println("差集: " + Sets.difference(set1, set2));
        System.out.println("对称差集: " + Sets.symmetricDifference(set1, set2));
        
        // 演示 Maps 工具类
        System.out.println("\n3. Maps 工具类示例:");
        Map<String, Integer> map1 = Maps.newHashMap();
        map1.put("a", 1);
        map1.put("b", 2);
        Map<String, Integer> map2 = Maps.newHashMap();
        map2.put("b", 2);
        map2.put("c", 3);
        System.out.println("映射 1: " + map1);
        System.out.println("映射 2: " + map2);
        System.out.println("映射差异: " + Maps.difference(map1, map2));
        
        // 演示 Iterables 工具类
        System.out.println("\n4. Iterables 工具类示例:");
        Iterable<String> iterable = Lists.newArrayList("x", "y", "z");
        System.out.println("Iterable 内容: " + iterable);
        System.out.println("Iterable 大小: " + Iterables.size(iterable));
        System.out.println("第一个元素: " + Iterables.getFirst(iterable, "默认值"));
        System.out.println("最后一个元素: " + Iterables.getLast(iterable));
    }

    /**
     * 演示集合转换
     */
    public static void demonstrateCollectionTransformations() {
        System.out.println("\n=== 集合转换示例 ===");
        
        // 演示集合过滤
        System.out.println("\n1. 集合过滤示例:");
        List<Integer> numbers = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("原始数字列表: " + numbers);
        
        // 过滤出偶数
        List<Integer> evenNumbers = Lists.newArrayList(Collections2.filter(numbers, n -> n % 2 == 0));
        System.out.println("偶数列表: " + evenNumbers);
        
        // 演示集合转换
        System.out.println("\n2. 集合转换示例:");
        List<String> strings = Lists.newArrayList("1", "2", "3", "4", "5");
        System.out.println("原始字符串列表: " + strings);
        
        // 将字符串转换为整数
        List<Integer> integers = Lists.transform(strings, Integer::parseInt);
        System.out.println("转换后的整数列表: " + integers);
        
        // 演示原始类型集合
        System.out.println("\n3. 原始类型集合示例:");
        int[] primitiveArray = {1, 2, 3, 4, 5};
        System.out.println("原始 int 数组: " + Ints.asList(primitiveArray));
        System.out.println("int 数组最大值: " + Ints.max(primitiveArray));
        System.out.println("int 数组最小值: " + Ints.min(primitiveArray));
        System.out.println("int 数组是否包含 3: " + Ints.contains(primitiveArray, 3));
    }
}
