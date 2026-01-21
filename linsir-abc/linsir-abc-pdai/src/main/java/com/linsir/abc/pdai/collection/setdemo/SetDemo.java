package com.linsir.abc.pdai.collection.setdemo;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Set 集合示例
 * 展示 HashSet、LinkedHashSet、TreeSet 的使用方法
 */
public class SetDemo {

    public static void main(String[] args) {
        System.out.println("=== Set 集合示例 ===\n");

        // 1. HashSet 示例
        System.out.println("1. HashSet 示例:");
        System.out.println("   特点: 无序、不重复、基于哈希表实现");
        Set<String> hashSet = new HashSet<>();
        hashSet.add("Apple");
        hashSet.add("Banana");
        hashSet.add("Orange");
        hashSet.add("Apple"); // 重复元素，不会添加
        System.out.println("   元素: " + hashSet);
        System.out.println("   大小: " + hashSet.size());
        System.out.println("   是否包含 Banana: " + hashSet.contains("Banana"));
        hashSet.remove("Orange");
        System.out.println("   删除 Orange 后: " + hashSet);

        // 2. LinkedHashSet 示例
        System.out.println("\n2. LinkedHashSet 示例:");
        System.out.println("   特点: 有序、不重复、基于链表和哈希表实现");
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("Apple");
        linkedHashSet.add("Banana");
        linkedHashSet.add("Orange");
        linkedHashSet.add("Apple"); // 重复元素，不会添加
        System.out.println("   元素: " + linkedHashSet);
        System.out.println("   大小: " + linkedHashSet.size());

        // 3. TreeSet 示例
        System.out.println("\n3. TreeSet 示例:");
        System.out.println("   特点: 有序、不重复、基于红黑树实现");
        Set<String> treeSet = new TreeSet<>();
        treeSet.add("Banana");
        treeSet.add("Apple");
        treeSet.add("Orange");
        treeSet.add("Apple"); // 重复元素，不会添加
        System.out.println("   元素: " + treeSet); // 自动排序
        System.out.println("   大小: " + treeSet.size());

        // 4. TreeSet 数字排序示例
        System.out.println("\n4. TreeSet 数字排序示例:");
        Set<Integer> numberTreeSet = new TreeSet<>();
        numberTreeSet.add(5);
        numberTreeSet.add(1);
        numberTreeSet.add(3);
        numberTreeSet.add(8);
        numberTreeSet.add(1); // 重复元素，不会添加
        System.out.println("   元素: " + numberTreeSet); // 自动排序

        // 5. 遍历 Set
        System.out.println("\n5. 遍历 Set 示例:");
        System.out.println("   使用 for-each 循环:");
        for (String fruit : linkedHashSet) {
            System.out.println("   - " + fruit);
        }

        System.out.println("\n   使用迭代器:");
        for (java.util.Iterator<String> iterator = hashSet.iterator(); iterator.hasNext(); ) {
            System.out.println("   - " + iterator.next());
        }

        // 6. 清空 Set
        System.out.println("\n6. 清空 Set 示例:");
        System.out.println("   清空前大小: " + hashSet.size());
        hashSet.clear();
        System.out.println("   清空后大小: " + hashSet.size());
        System.out.println("   是否为空: " + hashSet.isEmpty());

        System.out.println("\n=== Set 集合总结 ===");
        System.out.println("1. HashSet: 无序、高效，适合不需要排序的场景");
        System.out.println("2. LinkedHashSet: 有序（插入顺序）、略低于 HashSet，适合需要保持插入顺序的场景");
        System.out.println("3. TreeSet: 有序（自然排序）、效率较低，适合需要排序的场景");
        System.out.println("4. 所有 Set 都不允许重复元素");
        System.out.println("5. Set 接口没有索引方法，不能通过索引访问元素");
    }
}
