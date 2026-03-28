package com.linsir.abc.core.jvm.tuning.datastructure;

import java.util.*;

/**
 * 内存高效的Map实现
 * 演示如何优化Map的使用，减少内存占用
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class MemoryEfficientMap {

    /**
     * 默认加载因子
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 创建优化的HashMap
     * 根据预期元素数量计算合适的初始容量
     *
     * @param expectedSize 预期元素数量
     * @return HashMap
     */
    public static <K, V> HashMap<K, V> createOptimizedHashMap(int expectedSize) {
        // 计算合适的初始容量，避免扩容
        int initialCapacity = (int) (expectedSize / DEFAULT_LOAD_FACTOR) + 1;
        return new HashMap<>(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 创建优化的HashSet
     *
     * @param expectedSize 预期元素数量
     * @return HashSet
     */
    public static <T> HashSet<T> createOptimizedHashSet(int expectedSize) {
        int initialCapacity = (int) (expectedSize / DEFAULT_LOAD_FACTOR) + 1;
        return new HashSet<>(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 使用IntKeyMap存储int到对象的映射
     * 比HashMap<Integer, V>更节省内存
     *
     * @param <V> 值类型
     */
    public static class IntKeyMap<V> {
        private static final int DEFAULT_CAPACITY = 16;
        private static final float LOAD_FACTOR = 0.75f;

        private Entry<V>[] table;
        private int size;
        private int threshold;

        @SuppressWarnings("unchecked")
        public IntKeyMap() {
            this.table = new Entry[DEFAULT_CAPACITY];
            this.threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
        }

        public void put(int key, V value) {
            if (value == null) {
                throw new NullPointerException("Value cannot be null");
            }

            int index = indexFor(key, table.length);

            // 检查是否已存在
            for (Entry<V> e = table[index]; e != null; e = e.next) {
                if (e.key == key) {
                    e.value = value;
                    return;
                }
            }

            // 添加新条目
            table[index] = new Entry<>(key, value, table[index]);
            size++;

            // 扩容检查
            if (size >= threshold) {
                resize(table.length * 2);
            }
        }

        public V get(int key) {
            int index = indexFor(key, table.length);
            for (Entry<V> e = table[index]; e != null; e = e.next) {
                if (e.key == key) {
                    return e.value;
                }
            }
            return null;
        }

        public boolean containsKey(int key) {
            return get(key) != null;
        }

        public int size() {
            return size;
        }

        private int indexFor(int key, int length) {
            return (key ^ (key >>> 16)) & (length - 1);
        }

        @SuppressWarnings("unchecked")
        private void resize(int newCapacity) {
            Entry<V>[] newTable = new Entry[newCapacity];
            threshold = (int) (newCapacity * LOAD_FACTOR);

            for (Entry<V> e : table) {
                while (e != null) {
                    Entry<V> next = e.next;
                    int newIndex = indexFor(e.key, newCapacity);
                    e.next = newTable[newIndex];
                    newTable[newIndex] = e;
                    e = next;
                }
            }

            table = newTable;
        }

        private static class Entry<V> {
            final int key;
            V value;
            Entry<V> next;

            Entry(int key, V value, Entry<V> next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }
    }

    /**
     * 使用LongKeyMap存储long到对象的映射
     *
     * @param <V> 值类型
     */
    public static class LongKeyMap<V> {
        private static final int DEFAULT_CAPACITY = 16;
        private static final float LOAD_FACTOR = 0.75f;

        private Entry<V>[] table;
        private int size;
        private int threshold;

        @SuppressWarnings("unchecked")
        public LongKeyMap() {
            this.table = new Entry[DEFAULT_CAPACITY];
            this.threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);
        }

        public void put(long key, V value) {
            if (value == null) {
                throw new NullPointerException("Value cannot be null");
            }

            int index = indexFor(key, table.length);

            for (Entry<V> e = table[index]; e != null; e = e.next) {
                if (e.key == key) {
                    e.value = value;
                    return;
                }
            }

            table[index] = new Entry<>(key, value, table[index]);
            size++;

            if (size >= threshold) {
                resize(table.length * 2);
            }
        }

        public V get(long key) {
            int index = indexFor(key, table.length);
            for (Entry<V> e = table[index]; e != null; e = e.next) {
                if (e.key == key) {
                    return e.value;
                }
            }
            return null;
        }

        public boolean containsKey(long key) {
            return get(key) != null;
        }

        public int size() {
            return size;
        }

        private int indexFor(long key, int length) {
            int hash = (int) (key ^ (key >>> 32));
            return (hash ^ (hash >>> 16)) & (length - 1);
        }

        @SuppressWarnings("unchecked")
        private void resize(int newCapacity) {
            Entry<V>[] newTable = new Entry[newCapacity];
            threshold = (int) (newCapacity * LOAD_FACTOR);

            for (Entry<V> e : table) {
                while (e != null) {
                    Entry<V> next = e.next;
                    int newIndex = indexFor(e.key, newCapacity);
                    e.next = newTable[newIndex];
                    newTable[newIndex] = e;
                    e = next;
                }
            }

            table = newTable;
        }

        private static class Entry<V> {
            final long key;
            V value;
            Entry<V> next;

            Entry(long key, V value, Entry<V> next) {
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }
    }

    /**
     * 内存占用对比
     *
     * @param entryCount 条目数量
     * @return 对比报告
     */
    public static String getMemoryComparisonReport(int entryCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Map内存占用对比（条目数量：").append(entryCount).append("）===\n\n");

        // HashMap<Integer, String> 内存占用估算
        // 每个Entry：对象头(12) + key引用(4/8) + value引用(4/8) + hash(4) + next引用(4/8) + 对齐 = 约32字节
        // 每个Integer：对象头(12) + value(4) + 对齐(4) = 20字节
        // 数组：对象头(12) + length(4) + 引用数组(4/8 * capacity)
        long hashMapMemory = entryCount * (32 + 20) + entryCount * 16;
        sb.append("HashMap<Integer, String>: ~").append(hashMapMemory / 1024).append("KB\n");

        // IntKeyMap 内存占用估算
        // 每个Entry：key(int) + value引用 + next引用 = 约16字节
        // 数组：对象头 + int数组(4 * capacity)
        long intKeyMapMemory = entryCount * 16 + entryCount * 8;
        sb.append("IntKeyMap<String>: ~").append(intKeyMapMemory / 1024).append("KB\n");

        sb.append("\n节省内存: ")
                .append(String.format("%.1f%%", (1 - (double) intKeyMapMemory / hashMapMemory) * 100))
                .append("\n");

        return sb.toString();
    }
}
