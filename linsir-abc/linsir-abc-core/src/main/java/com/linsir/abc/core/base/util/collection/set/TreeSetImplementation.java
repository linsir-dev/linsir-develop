package com.linsir.abc.core.base.util.collection.set;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 简化版TreeSet实现
 *
 * 本类模拟JDK TreeSet的核心实现：
 * 1. 基于TreeMap实现（value为固定的Object）
 * 2. 元素自动排序
 * 3. 不允许重复元素
 * 4. 不允许null元素（如果比较器不支持null）
 *
 * TreeSet特点：
 * - 基于TreeMap实现
 * - 元素自动排序
 * - 不允许null元素（默认情况下）
 * - 查找、插入、删除时间复杂度O(log n)
 * - 支持范围查询
 *
 * @param <E> 元素类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class TreeSetImplementation<E> implements Collection<E> {

    /**
     * 虚拟对象，作为TreeMap的value
     */
    private static final Object PRESENT = new Object();

    /**
     * 底层TreeMap
     */
    private final java.util.TreeMap<E, Object> map;

    /**
     * 默认构造方法（使用元素的自然顺序）
     */
    public TreeSetImplementation() {
        map = new java.util.TreeMap<>();
    }

    /**
     * 指定比较器的构造方法
     *
     * @param comparator 比较器
     */
    public TreeSetImplementation(Comparator<? super E> comparator) {
        map = new java.util.TreeMap<>(comparator);
    }

    /**
     * 从集合构造
     *
     * @param c 集合
     */
    public TreeSetImplementation(Collection<? extends E> c) {
        map = new java.util.TreeMap<>();
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
     * 获取第一个元素（最小的）
     *
     * @return 第一个元素
     * @throws NoSuchElementException 如果集合为空
     */
    public E first() {
        return map.firstKey();
    }

    /**
     * 获取最后一个元素（最大的）
     *
     * @return 最后一个元素
     * @throws NoSuchElementException 如果集合为空
     */
    public E last() {
        return map.lastKey();
    }

    /**
     * 获取小于指定元素的最大元素
     *
     * @param e 指定元素
     * @return 小于e的最大元素，如果不存在返回null
     */
    public E lower(E e) {
        return map.lowerKey(e);
    }

    /**
     * 获取小于等于指定元素的最大元素
     *
     * @param e 指定元素
     * @return 小于等于e的最大元素，如果不存在返回null
     */
    public E floor(E e) {
        return map.floorKey(e);
    }

    /**
     * 获取大于等于指定元素的最小元素
     *
     * @param e 指定元素
     * @return 大于等于e的最小元素，如果不存在返回null
     */
    public E ceiling(E e) {
        return map.ceilingKey(e);
    }

    /**
     * 获取大于指定元素的最小元素
     *
     * @param e 指定元素
     * @return 大于e的最小元素，如果不存在返回null
     */
    public E higher(E e) {
        return map.higherKey(e);
    }

    /**
     * 删除并返回第一个元素
     *
     * @return 第一个元素
     * @throws NoSuchElementException 如果集合为空
     */
    public E pollFirst() {
        if (isEmpty()) {
            return null;
        }
        E e = map.firstKey();
        remove(e);
        return e;
    }

    /**
     * 删除并返回最后一个元素
     *
     * @return 最后一个元素
     * @throws NoSuchElementException 如果集合为空
     */
    public E pollLast() {
        if (isEmpty()) {
            return null;
        }
        E e = map.lastKey();
        remove(e);
        return e;
    }

    /**
     * 演示TreeSet的使用
     */
    public static void demonstrateUsage() {
        System.out.println("========== TreeSet演示 ==========");

        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        // 添加元素（乱序）
        set.add(30);
        set.add(10);
        set.add(50);
        set.add(20);
        set.add(40);

        System.out.println("添加元素后大小: " + set.size());

        // 遍历（自动排序）
        System.out.println("按自然顺序遍历:");
        for (Integer i : set) {
            System.out.println("  " + i);
        }

        // 范围查询
        System.out.println("第一个元素: " + set.first());
        System.out.println("最后一个元素: " + set.last());
        System.out.println("小于25的最大元素: " + set.lower(25));
        System.out.println("大于等于25的最小元素: " + set.ceiling(25));

        System.out.println();
    }

    /**
     * 演示自定义比较器
     */
    public static void demonstrateCustomComparator() {
        System.out.println("========== 自定义比较器演示 ==========");

        // 按字符串长度排序
        TreeSetImplementation<String> set = new TreeSetImplementation<>(
            (s1, s2) -> {
                int lenDiff = s1.length() - s2.length();
                return lenDiff != 0 ? lenDiff : s1.compareTo(s2);
            }
        );

        set.add("Apple");
        set.add("Banana");
        set.add("Cat");
        set.add("Dog");
        set.add("Elephant");

        System.out.println("按长度排序后的元素:");
        for (String s : set) {
            System.out.println("  " + s + " (长度: " + s.length() + ")");
        }

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
        demonstrateCustomComparator();
    }
}
