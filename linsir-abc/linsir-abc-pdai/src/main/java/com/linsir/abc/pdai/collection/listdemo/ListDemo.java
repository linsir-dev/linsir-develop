package com.linsir.abc.pdai.collection.listdemo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * List 集合示例
 * 展示 ArrayList、LinkedList、Vector 的使用方法
 */
public class ListDemo {

    public static void main(String[] args) {
        System.out.println("=== List 集合示例 ===\n");

        // 1. ArrayList 示例
        System.out.println("1. ArrayList 示例:");
        System.out.println("   特点: 有序、可重复、基于动态数组实现、查询快、增删慢");
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Apple");
        arrayList.add("Banana");
        arrayList.add("Orange");
        arrayList.add("Apple"); // 允许重复元素
        System.out.println("   元素: " + arrayList);
        System.out.println("   大小: " + arrayList.size());
        System.out.println("   索引 1 的元素: " + arrayList.get(1));
        arrayList.set(1, "Pear"); // 修改元素
        System.out.println("   修改索引 1 后: " + arrayList);
        arrayList.remove(2); // 删除元素
        System.out.println("   删除索引 2 后: " + arrayList);
        System.out.println("   是否包含 Apple: " + arrayList.contains("Apple"));
        System.out.println("   Apple 的索引: " + arrayList.indexOf("Apple"));

        // 2. LinkedList 示例
        System.out.println("\n2. LinkedList 示例:");
        System.out.println("   特点: 有序、可重复、基于双向链表实现、查询慢、增删快");
        List<String> linkedList = new LinkedList<>();
        linkedList.add("Apple");
        linkedList.add("Banana");
        linkedList.add("Orange");
        linkedList.add("Apple"); // 允许重复元素
        System.out.println("   元素: " + linkedList);
        System.out.println("   大小: " + linkedList.size());
        System.out.println("   索引 1 的元素: " + linkedList.get(1));

        // 3. Vector 示例
        System.out.println("\n3. Vector 示例:");
        System.out.println("   特点: 有序、可重复、基于动态数组实现、线程安全、效率低");
        List<String> vector = new Vector<>();
        vector.add("Apple");
        vector.add("Banana");
        vector.add("Orange");
        vector.add("Apple"); // 允许重复元素
        System.out.println("   元素: " + vector);
        System.out.println("   大小: " + vector.size());

        // 4. 遍历 List
        System.out.println("\n4. 遍历 List 示例:");
        System.out.println("   使用 for-each 循环:");
        for (String fruit : arrayList) {
            System.out.println("   - " + fruit);
        }

        System.out.println("\n   使用普通 for 循环:");
        for (int i = 0; i < linkedList.size(); i++) {
            System.out.println("   - " + linkedList.get(i));
        }

        System.out.println("\n   使用迭代器:");
        for (var iterator = vector.iterator(); iterator.hasNext(); ) {
            System.out.println("   - " + iterator.next());
        }

        // 5. 列表操作
        System.out.println("\n5. 列表操作示例:");
        List<String> subList = arrayList.subList(0, 2); // 截取子列表
        System.out.println("   子列表 (0-2): " + subList);

        System.out.println("\n   清空列表:");
        System.out.println("   清空前大小: " + arrayList.size());
        arrayList.clear();
        System.out.println("   清空后大小: " + arrayList.size());
        System.out.println("   是否为空: " + arrayList.isEmpty());

        // 6. 批量操作
        System.out.println("\n6. 批量操作示例:");
        List<String> newFruits = new ArrayList<>();
        newFruits.add("Grape");
        newFruits.add("Mango");
        linkedList.addAll(newFruits);
        System.out.println("   添加新水果后: " + linkedList);

        System.out.println("\n=== List 集合总结 ===");
        System.out.println("1. ArrayList: 查询快、增删慢，适合频繁查询的场景");
        System.out.println("2. LinkedList: 查询慢、增删快，适合频繁增删的场景");
        System.out.println("3. Vector: 线程安全、效率低，适合多线程环境");
        System.out.println("4. 所有 List 都允许重复元素，并且保持插入顺序");
        System.out.println("5. List 接口提供了丰富的索引操作方法");
    }
}
