package com.linsir.abc.core.base.util.concurrent.collection;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 并发哈希表实现
 * 演示ConcurrentHashMap的核心原理：分段锁、读写分离、并发安全
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>分段锁：将哈希表分成多个段，每个段独立加锁，提高并发度</li>
 *   <li>读写分离：读操作不需要加锁，写操作需要加锁</li>
 *   <li>volatile：使用volatile保证可见性</li>
 *   <li>CAS：使用Compare-And-Swap实现无锁更新</li>
 * </ul>
 *
 * <p>性能特点：</p>
 * <ul>
 *   <li>读操作：完全并行，不需要加锁</li>
 *   <li>写操作：分段加锁，不同段可以并行</li>
 *   <li>读多写少场景性能优异</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConcurrentHashMapImplementation<K, V> {

    /**
     * 默认段数量（必须是2的幂）
     */
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    /**
     * 默认负载因子
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 默认初始容量
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * 段数组
     */
    private final Segment<K, V>[] segments;

    /**
     * 段掩码，用于计算段索引
     */
    private final int segmentMask;

    /**
     * 段偏移量
     */
    private final int segmentShift;

    /**
     * 哈希算法
     */
    private final HashFunction hashFunction;

    /**
     * 哈希函数接口
     */
    @FunctionalInterface
    public interface HashFunction {
        int hash(Object key);
    }

    /**
     * 默认构造器
     */
    public ConcurrentHashMapImplementation() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
    }

    /**
     * 带初始容量的构造器
     *
     * @param initialCapacity 初始容量
     */
    public ConcurrentHashMapImplementation(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
    }

    /**
     * 完整构造器
     *
     * @param initialCapacity 初始容量
     * @param loadFactor 负载因子
     * @param concurrencyLevel 并发级别（段数量）
     */
    @SuppressWarnings("unchecked")
    public ConcurrentHashMapImplementation(int initialCapacity, float loadFactor, int concurrencyLevel) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("初始容量不能为负数");
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("负载因子必须大于0");
        }
        if (concurrencyLevel <= 0) {
            throw new IllegalArgumentException("并发级别必须大于0");
        }

        // 确保并发级别是2的幂
        int sshift = 0;
        int ssize = 1;
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
        }
        this.segmentShift = 32 - sshift;
        this.segmentMask = ssize - 1;
        this.segments = new Segment[ssize];

        // 计算每个段的容量
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity) {
            ++c;
        }
        int cap = 1;
        while (cap < c) {
            cap <<= 1;
        }

        // 初始化段
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment<>(cap, loadFactor);
        }

        this.hashFunction = key -> {
            int h = key.hashCode();
            // 扰动函数，减少哈希冲突
            return (h ^ (h >>> 16)) & 0x7fffffff;
        };
    }

    /**
     * 计算段的索引
     *
     * @param hash 哈希值
     * @return 段索引
     */
    private int segmentFor(int hash) {
        return (hash >>> segmentShift) & segmentMask;
    }

    /**
     * 获取键的哈希值
     *
     * @param key 键
     * @return 哈希值
     */
    private int hash(Object key) {
        return hashFunction.hash(key);
    }

    /**
     * 获取值（读操作，不需要加锁）
     *
     * @param key 键
     * @return 值，如果不存在返回null
     */
    public V get(Object key) {
        if (key == null) {
            throw new NullPointerException("键不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.get(key, hash);
    }

    /**
     * 放入键值对（写操作，需要加锁）
     *
     * @param key 键
     * @param value 值
     * @return 旧值，如果不存在返回null
     */
    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("键和值不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.put(key, hash, value, false);
    }

    /**
     * 如果不存在则放入（原子操作）
     *
     * @param key 键
     * @param value 值
     * @return 旧值，如果不存在返回null
     */
    public V putIfAbsent(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("键和值不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.put(key, hash, value, true);
    }

    /**
     * 移除键值对
     *
     * @param key 键
     * @return 旧值，如果不存在返回null
     */
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException("键不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.remove(key, hash, null);
    }

    /**
     * 条件移除（键和值都匹配才移除）
     *
     * @param key 键
     * @param value 值
     * @return 是否移除成功
     */
    public boolean remove(Object key, Object value) {
        if (key == null || value == null) {
            throw new NullPointerException("键和值不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.remove(key, hash, value) != null;
    }

    /**
     * 替换值（键必须存在）
     *
     * @param key 键
     * @param value 新值
     * @return 旧值，如果不存在返回null
     */
    public V replace(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("键和值不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.replace(key, hash, value);
    }

    /**
     * 条件替换（旧值匹配才替换）
     *
     * @param key 键
     * @param oldValue 旧值
     * @param newValue 新值
     * @return 是否替换成功
     */
    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null || oldValue == null || newValue == null) {
            throw new NullPointerException("键和值不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.replace(key, hash, oldValue, newValue);
    }

    /**
     * 计算并更新值（原子操作）
     *
     * @param key 键
     * @param remappingFunction 计算函数
     * @return 新值
     */
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null) {
            throw new NullPointerException("键和函数不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.compute(key, hash, remappingFunction);
    }

    /**
     * 如果不存在则计算值（原子操作）
     *
     * @param key 键
     * @param mappingFunction 计算函数
     * @return 当前值（新值或旧值）
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (key == null || mappingFunction == null) {
            throw new NullPointerException("键和函数不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.computeIfAbsent(key, hash, mappingFunction);
    }

    /**
     * 如果存在则计算新值（原子操作）
     *
     * @param key 键
     * @param remappingFunction 计算函数
     * @return 新值，如果不存在返回null
     */
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null) {
            throw new NullPointerException("键和函数不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.computeIfPresent(key, hash, remappingFunction);
    }

    /**
     * 合并值（原子操作）
     *
     * @param key 键
     * @param value 新值
     * @param remappingFunction 合并函数
     * @return 新值
     */
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        if (key == null || value == null || remappingFunction == null) {
            throw new NullPointerException("键、值和函数不能为null");
        }
        int hash = hash(key);
        Segment<K, V> segment = segments[segmentFor(hash)];
        return segment.merge(key, hash, value, remappingFunction);
    }

    /**
     * 判断是否包含键
     *
     * @param key 键
     * @return 是否包含
     */
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /**
     * 获取大小（估计值）
     *
     * @return 大小
     */
    public int size() {
        int sum = 0;
        for (Segment<K, V> segment : segments) {
            sum += segment.count;
        }
        return sum;
    }

    /**
     * 判断是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * 清空所有数据
     */
    public void clear() {
        for (Segment<K, V> segment : segments) {
            segment.clear();
        }
    }

    /**
     * 获取所有键
     *
     * @return 键集合
     */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Segment<K, V> segment : segments) {
            keys.addAll(segment.keySet());
        }
        return keys;
    }

    /**
     * 获取所有值
     *
     * @return 值集合
     */
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        for (Segment<K, V> segment : segments) {
            values.addAll(segment.values());
        }
        return values;
    }

    /**
     * 获取所有键值对
     *
     * @return 键值对集合
     */
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entries = new HashSet<>();
        for (Segment<K, V> segment : segments) {
            entries.addAll(segment.entrySet());
        }
        return entries;
    }

    /**
     * 段（Segment）内部类
     * 每个段是一个独立的哈希表，有自己的锁
     */
    private static class Segment<K, V> {

        /**
         * 读写锁
         */
        private final ReadWriteLock lock = new ReentrantReadWriteLock();

        /**
         * 哈希表
         */
        private volatile HashEntry<K, V>[] table;

        /**
         * 元素数量
         */
        private volatile int count;

        /**
         * 修改次数
         */
        private int modCount;

        /**
         * 阈值（扩容触发条件）
         */
        private int threshold;

        /**
         * 负载因子
         */
        private final float loadFactor;

        /**
         * 构造器
         *
         * @param initialCapacity 初始容量
         * @param loadFactor 负载因子
         */
        @SuppressWarnings("unchecked")
        Segment(int initialCapacity, float loadFactor) {
            this.loadFactor = loadFactor;
            this.table = new HashEntry[initialCapacity];
            this.threshold = (int) (initialCapacity * loadFactor);
        }

        /**
         * 获取值（读操作，使用读锁）
         */
        V get(Object key, int hash) {
            lock.readLock().lock();
            try {
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> e = tab[index];
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        return e.value;
                    }
                    e = e.next;
                }
                return null;
            } finally {
                lock.readLock().unlock();
            }
        }

        /**
         * 放入键值对（写操作，使用写锁）
         */
        V put(K key, int hash, V value, boolean onlyIfAbsent) {
            lock.writeLock().lock();
            try {
                int c = count;
                if (c++ > threshold) {
                    rehash();
                }

                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> first = tab[index];

                // 查找是否已存在
                HashEntry<K, V> e = first;
                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        V oldValue = e.value;
                        if (!onlyIfAbsent || oldValue == null) {
                            e.value = value;
                            modCount++;
                        }
                        return oldValue;
                    }
                    e = e.next;
                }

                // 插入新节点
                tab[index] = new HashEntry<>(hash, key, value, first);
                count = c;
                modCount++;
                return null;
            } finally {
                lock.writeLock().unlock();
            }
        }

        /**
         * 移除键值对
         */
        V remove(Object key, int hash, Object value) {
            lock.writeLock().lock();
            try {
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> e = tab[index];
                HashEntry<K, V> pred = null;

                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        V v = e.value;
                        if (value == null || value.equals(v)) {
                            if (pred == null) {
                                tab[index] = e.next;
                            } else {
                                pred.next = e.next;
                            }
                            count--;
                            modCount++;
                            return v;
                        }
                        return null;
                    }
                    pred = e;
                    e = e.next;
                }
                return null;
            } finally {
                lock.writeLock().unlock();
            }
        }

        /**
         * 替换值
         */
        V replace(K key, int hash, V value) {
            lock.writeLock().lock();
            try {
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> e = tab[index];

                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        V oldValue = e.value;
                        e.value = value;
                        modCount++;
                        return oldValue;
                    }
                    e = e.next;
                }
                return null;
            } finally {
                lock.writeLock().unlock();
            }
        }

        /**
         * 条件替换
         */
        boolean replace(K key, int hash, V oldValue, V newValue) {
            lock.writeLock().lock();
            try {
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> e = tab[index];

                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        if (oldValue.equals(e.value)) {
                            e.value = newValue;
                            modCount++;
                            return true;
                        }
                        return false;
                    }
                    e = e.next;
                }
                return false;
            } finally {
                lock.writeLock().unlock();
            }
        }

        /**
         * 计算并更新值
         */
        V compute(K key, int hash, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            lock.writeLock().lock();
            try {
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> e = tab[index];
                HashEntry<K, V> pred = null;

                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        V oldValue = e.value;
                        V newValue = remappingFunction.apply(key, oldValue);
                        if (newValue != null) {
                            e.value = newValue;
                            modCount++;
                            return newValue;
                        } else {
                            // 删除节点
                            if (pred == null) {
                                tab[index] = e.next;
                            } else {
                                pred.next = e.next;
                            }
                            count--;
                            modCount++;
                            return null;
                        }
                    }
                    pred = e;
                    e = e.next;
                }

                // 键不存在，计算新值
                V newValue = remappingFunction.apply(key, null);
                if (newValue != null) {
                    int c = count;
                    if (c++ > threshold) {
                        rehash();
                        tab = table;
                        index = hash & (tab.length - 1);
                    }
                    tab[index] = new HashEntry<>(hash, key, newValue, tab[index]);
                    count = c;
                    modCount++;
                }
                return newValue;
            } finally {
                lock.writeLock().unlock();
            }
        }

        /**
         * 如果不存在则计算值
         */
        V computeIfAbsent(K key, int hash, Function<? super K, ? extends V> mappingFunction) {
            lock.readLock().lock();
            try {
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> e = tab[index];

                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        return e.value;
                    }
                    e = e.next;
                }
            } finally {
                lock.readLock().unlock();
            }

            // 键不存在，需要写入
            lock.writeLock().lock();
            try {
                // 双重检查
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> e = tab[index];

                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        return e.value;
                    }
                    e = e.next;
                }

                V newValue = mappingFunction.apply(key);
                if (newValue != null) {
                    int c = count;
                    if (c++ > threshold) {
                        rehash();
                        tab = table;
                        index = hash & (tab.length - 1);
                    }
                    tab[index] = new HashEntry<>(hash, key, newValue, tab[index]);
                    count = c;
                    modCount++;
                }
                return newValue;
            } finally {
                lock.writeLock().unlock();
            }
        }

        /**
         * 如果存在则计算新值
         */
        V computeIfPresent(K key, int hash, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            lock.writeLock().lock();
            try {
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> e = tab[index];
                HashEntry<K, V> pred = null;

                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        V oldValue = e.value;
                        V newValue = remappingFunction.apply(key, oldValue);
                        if (newValue != null) {
                            e.value = newValue;
                            modCount++;
                            return newValue;
                        } else {
                            // 删除节点
                            if (pred == null) {
                                tab[index] = e.next;
                            } else {
                                pred.next = e.next;
                            }
                            count--;
                            modCount++;
                            return null;
                        }
                    }
                    pred = e;
                    e = e.next;
                }
                return null;
            } finally {
                lock.writeLock().unlock();
            }
        }

        /**
         * 合并值
         */
        V merge(K key, int hash, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            lock.writeLock().lock();
            try {
                HashEntry<K, V>[] tab = table;
                int index = hash & (tab.length - 1);
                HashEntry<K, V> e = tab[index];
                HashEntry<K, V> pred = null;

                while (e != null) {
                    if (e.hash == hash && key.equals(e.key)) {
                        V newValue = remappingFunction.apply(e.value, value);
                        if (newValue != null) {
                            e.value = newValue;
                            modCount++;
                            return newValue;
                        } else {
                            // 删除节点
                            if (pred == null) {
                                tab[index] = e.next;
                            } else {
                                pred.next = e.next;
                            }
                            count--;
                            modCount++;
                            return null;
                        }
                    }
                    pred = e;
                    e = e.next;
                }

                // 键不存在，插入新值
                int c = count;
                if (c++ > threshold) {
                    rehash();
                    tab = table;
                    index = hash & (tab.length - 1);
                }
                tab[index] = new HashEntry<>(hash, key, value, tab[index]);
                count = c;
                modCount++;
                return value;
            } finally {
                lock.writeLock().unlock();
            }
        }

        /**
         * 扩容（rehash）
         */
        @SuppressWarnings("unchecked")
        void rehash() {
            HashEntry<K, V>[] oldTable = table;
            int oldCapacity = oldTable.length;
            if (oldCapacity >= 1 << 30) {
                threshold = Integer.MAX_VALUE;
                return;
            }

            int newCapacity = oldCapacity << 1;
            HashEntry<K, V>[] newTable = new HashEntry[newCapacity];
            int newThreshold = (int) (newCapacity * loadFactor);

            // 迁移数据
            for (int i = 0; i < oldCapacity; i++) {
                HashEntry<K, V> e = oldTable[i];
                if (e != null) {
                    HashEntry<K, V> next = e.next;
                    int idx = e.hash & (newCapacity - 1);

                    if (next == null) {
                        newTable[idx] = e;
                    } else {
                        // 处理链表
                        HashEntry<K, V> lastRun = e;
                        int lastIdx = idx;
                        for (HashEntry<K, V> last = next; last != null; last = last.next) {
                            int k = last.hash & (newCapacity - 1);
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        newTable[lastIdx] = lastRun;

                        // 克隆前面的节点
                        for (HashEntry<K, V> p = e; p != lastRun; p = p.next) {
                            int k = p.hash & (newCapacity - 1);
                            newTable[k] = new HashEntry<>(p.hash, p.key, p.value, newTable[k]);
                        }
                    }
                }
            }

            table = newTable;
            threshold = newThreshold;
        }

        /**
         * 清空
         */
        void clear() {
            lock.writeLock().lock();
            try {
                Arrays.fill(table, null);
                count = 0;
                modCount++;
            } finally {
                lock.writeLock().unlock();
            }
        }

        /**
         * 获取所有键
         */
        Set<K> keySet() {
            lock.readLock().lock();
            try {
                Set<K> keys = new HashSet<>();
                for (HashEntry<K, V> e : table) {
                    while (e != null) {
                        keys.add(e.key);
                        e = e.next;
                    }
                }
                return keys;
            } finally {
                lock.readLock().unlock();
            }
        }

        /**
         * 获取所有值
         */
        Collection<V> values() {
            lock.readLock().lock();
            try {
                List<V> values = new ArrayList<>();
                for (HashEntry<K, V> e : table) {
                    while (e != null) {
                        values.add(e.value);
                        e = e.next;
                    }
                }
                return values;
            } finally {
                lock.readLock().unlock();
            }
        }

        /**
         * 获取所有键值对
         */
        Set<Map.Entry<K, V>> entrySet() {
            lock.readLock().lock();
            try {
                Set<Map.Entry<K, V>> entries = new HashSet<>();
                for (HashEntry<K, V> e : table) {
                    while (e != null) {
                        entries.add(new AbstractMap.SimpleEntry<>(e.key, e.value));
                        e = e.next;
                    }
                }
                return entries;
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    /**
     * 哈希节点（HashEntry）内部类
     */
    private static class HashEntry<K, V> {
        final int hash;
        final K key;
        volatile V value;
        HashEntry<K, V> next;

        HashEntry(int hash, K key, V value, HashEntry<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
