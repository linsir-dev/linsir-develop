package com.linsir.abc.pdai.tools.spring;

import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Spring CollectionUtils 工具类示例
 * 演示 CollectionUtils 的常用方法
 */
public class SpringCollectionUtilsDemo {

    /**
     * 演示 CollectionUtils 的常用方法
     */
    public static void demonstrateCollectionUtils() {
        // 1. 集合判空方法
        System.out.println("=== 集合判空方法 ===");
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<String> list2 = new ArrayList<>();
        List<String> list3 = null;
        
        System.out.println("CollectionUtils.isEmpty(list1): " + CollectionUtils.isEmpty(list1)); // false
        System.out.println("CollectionUtils.isEmpty(list2): " + CollectionUtils.isEmpty(list2)); // true
        System.out.println("CollectionUtils.isEmpty(list3): " + CollectionUtils.isEmpty(list3)); // true
        
        System.out.println("!CollectionUtils.isEmpty(list1): " + !CollectionUtils.isEmpty(list1)); // true
        System.out.println("!CollectionUtils.isEmpty(list2): " + !CollectionUtils.isEmpty(list2)); // false
        System.out.println("!CollectionUtils.isEmpty(list3): " + !CollectionUtils.isEmpty(list3)); // false
        
        // 2. 集合包含方法
        System.out.println("\n=== 集合包含方法 ===");
        List<String> source = Arrays.asList("a", "b", "c", "d");
        List<String> candidates1 = Arrays.asList("a", "e");
        List<String> candidates2 = Arrays.asList("e", "f");
        
        boolean containsAny1 = false;
        for (String candidate : candidates1) {
            if (source.contains(candidate)) {
                containsAny1 = true;
                break;
            }
        }
        System.out.println("source 包含 candidates1 中的元素: " + containsAny1); // true
        
        boolean containsAny2 = false;
        for (String candidate : candidates2) {
            if (source.contains(candidate)) {
                containsAny2 = true;
                break;
            }
        }
        System.out.println("source 包含 candidates2 中的元素: " + containsAny2); // false
        
        Object firstMatch = null;
        for (String candidate : candidates1) {
            if (source.contains(candidate)) {
                firstMatch = candidate;
                break;
            }
        }
        System.out.println("source 中第一个匹配 candidates1 的元素: " + firstMatch); // a
        
        // 3. 数组合并到集合
        System.out.println("\n=== 数组合并到集合 ===");
        List<String> targetList = new ArrayList<>(Arrays.asList("a", "b"));
        Object[] array = new Object[]{"c", "d"};
        Collections.addAll(targetList, Arrays.copyOf(array, array.length, String[].class));
        System.out.println("合并后的集合: " + targetList); // [a, b, c, d]
        
        // 4. 属性合并到 Map
        System.out.println("\n=== 属性合并到 Map ===");
        Properties properties = new Properties();
        properties.setProperty("name", "Spring");
        properties.setProperty("version", "6.0");
        Map<String, Object> map = new HashMap<>();
        map.put("author", "Pivotal");
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            map.put((String) entry.getKey(), entry.getValue());
        }
        System.out.println("合并后的 Map: " + map); // {author=Pivotal, name=Spring, version=6.0}
        
        // 5. 创建指定初始容量的集合
        System.out.println("\n=== 创建指定初始容量的集合 ===");
        Map<String, Object> hashMap = new HashMap<>(16);
        hashMap.put("key1", "value1");
        System.out.println("新创建的 HashMap: " + hashMap);
        
        Map<String, Object> linkedHashMap = new LinkedHashMap<>(16);
        linkedHashMap.put("key1", "value1");
        linkedHashMap.put("key2", "value2");
        System.out.println("新创建的 LinkedHashMap: " + linkedHashMap);
        
        // 6. 集合差集
        System.out.println("\n=== 集合差集 ===");
        List<String> listA = Arrays.asList("a", "b", "c", "d");
        List<String> listB = Arrays.asList("c", "d", "e", "f");
        List<String> difference = new ArrayList<>(listA);
        difference.removeAll(listB);
        System.out.println("listA 与 listB 的差集: " + difference); // [a, b]
        
        // 7. 集合交集
        System.out.println("\n=== 集合交集 ===");
        List<String> intersection = new ArrayList<>(listA);
        intersection.retainAll(listB);
        System.out.println("listA 与 listB 的交集: " + intersection); // [c, d]
        
        // 8. 集合并集
        System.out.println("\n=== 集合并集 ===");
        List<String> union = new ArrayList<>(listA);
        for (String element : listB) {
            if (!union.contains(element)) {
                union.add(element);
            }
        }
        System.out.println("listA 与 listB 的并集: " + union); // [a, b, c, d, e, f]
    }
}
