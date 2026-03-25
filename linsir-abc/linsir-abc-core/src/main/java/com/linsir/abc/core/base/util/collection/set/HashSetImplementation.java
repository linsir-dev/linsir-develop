package com.linsir.abc.core.base.util.collection.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 简化版HashSet实现
 *
 * 本类模拟JDK HashSet的核心实现：
 * 1. 基于HashMap实现（value为固定的Object）
 * 2. 不允许重复元素
 * 3. 允许null元素
 * 4. 非线程安全
 *
 * HashSet特点：
 * - 基于HashMap实现
 * - 不保证顺序
 * - 允许null元素
 * - 查找、插入、删除平均时间复杂度O(1)
 * - 非线程安全
 *
 * @param <E> 元素类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class HashSetImplementation<E> implements Collection<E> {

    /**
     * 虚拟对象，作为HashMap的value
     */
    private static final Object PRESENT = new Object();

    /**
     * 底层HashMap
     */
    private final java.util.HashMap<E, Object> map;

    /**
     * 默认构造方法
     */
    public HashSetImplementation() {
        map = new java.util.HashMap<>();
    }

    /**
     * 指定初始容量的构造方法
     *
     * @param initialCapacity 初始容量
     */
    public HashSetImplementation(int initialCapacity) {
        map = new java.util.HashMap<>(initialCapacity);
    }

    /**
     * 指定初始容量和负载因子的构造方法
     *
     * @param initialCapacity 初始容量
     * @param loadFactor 负载因子
     */
    public HashSetImplementation(int initialCapacity, float loadFactor) {
        map = new java.util.HashMap<>(initialCapacity, loadFactor);
    }

    /**
     * 从集合构造
     *
     * @param c 集合
     */
    public HashSetImplementation(Collection<? extends E> c) {
        map = new java.util.HashMap<>(Math.max((int) (c.size() / 0.75f) + 1, 16));
        addAll(c);
    }

    /**
     * 获取元素数量
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * 判断是否为空
     */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * 判断是否包含元素
     */
    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    /**
     * 添加元素
     */
    @Override
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    /**
     * 删除元素
     */
    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    /**
     * 清空所有元素
     */
    @Override
    public void clear() {
        map.clear();
    }

    /**
     * 获取迭代器
     */
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    /**
     * 转换为数组
     */
    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    /**
     * 转换为指定类型的数组
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return map.keySet().toArray(a);
    }

    /**
     * 判断是否包含所有元素
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加所有元素
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * 删除所有元素
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            if (remove(e)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * 只保留集合中的元素
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    /**
     * 演示HashSet的使用
     */
    public static void demonstrateUsage() {
        System.out.println("========== HashSet演示 ==========");

        HashSetImplementation<String> set = new HashSetImplementation<>();

        // 添加元素
        set.add("Apple");
        set.add("Banana");
        set.add("Cherry");
        set.add("Apple"); // 重复元素不会被添加

        System.out.println("添加元素后大小: " + set.size());
        System.out.println("是否包含Apple: " + set.contains("Apple"));
        System.out.println("是否包含Date: " + set.contains("Date"));

        // 添加null元素
        set.add(null);
        System.out.println("添加null后大小: " + set.size());
        System.out.println("是否包含null: " + set.contains(null));

        // 删除元素
        set.remove("Banana");
        System.out.println("删除Banana后大小: " + set.size());

        // 遍历
        System.out.println("遍历元素:");
        for (String s : set) {
            System.out.println("  " + s);
        }

        System.out.println();
    }

    /**
     * 演示集合操作
     */
    public static void demonstrateSetOperations() {
        System.out.println("========== 集合操作演示 ==========");

        HashSetImplementation<String> set1 = new HashSetImplementation<>();
        set1.add("A");
        set1.add("B");
        set1.add("C");

        HashSetImplementation<String> set2 = new HashSetImplementation<>();
        set2.add("B");
        set2.add("C");
        set2.add("D");

        System.out.println("Set1: " + set1);
        System.out.println("Set2: " + set2);

        // 并集
        HashSetImplementation<String> union = new HashSetImplementation<>(set1);
        union.addAll(set2);
        System.out.println("并集: " + union);

        // 交集
        HashSetImplementation<String> intersection = new HashSetImplementation<>(set1);
        intersection.retainAll(set2);
        System.out.println("交集: " + intersection);

        // 差集
        HashSetImplementation<String> difference = new HashSetImplementation<>(set1);
        difference.removeAll(set2);
        System.out.println("差集(Set1 - Set2): " + difference);

        System.out.println();
    }

    @Override
    public String toString() {
        return map.keySet().toString();
    }

    /**
     * 主方法：运行所有演示
     */
    public static void main(String[] args) {
        demonstrateUsage();
        demonstrateSetOperations();
    }
}
