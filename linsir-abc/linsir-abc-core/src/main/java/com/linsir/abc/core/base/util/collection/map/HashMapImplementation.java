package com.linsir.abc.core.base.util.collection.map;

import java.util.Objects;

/**
 * 简化版HashMap实现
 * 
 * 本类模拟JDK HashMap的核心实现：
 * 1. 基于数组+链表/红黑树的存储结构
 * 2. 哈希冲突解决（链地址法）
 * 3. 扩容机制（rehash）
 * 4. 负载因子控制
 * 
 * HashMap特点：
 * - 允许null键和null值
 * - 非线程安全
 * - 不保证顺序
 * - 查找、插入、删除平均时间复杂度O(1)
 * 
 * @param <K> 键类型
 * @param <V> 值类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class HashMapImplementation<K, V> {
    
    /**
     * 默认初始容量（必须是2的幂）
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // 16
    
    /**
     * 最大容量
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    
    /**
     * 默认负载因子
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    /**
     * 链表转红黑树的阈值
     */
    private static final int TREEIFY_THRESHOLD = 8;
    
    /**
     * 哈希表数组
     */
    private Node<K, V>[] table;
    
    /**
     * 元素数量
     */
    private int size;
    
    /**
     * 扩容阈值
     */
    private int threshold;
    
    /**
     * 负载因子
     */
    private final float loadFactor;
    
    /**
     * 节点类（链表结构）
     */
    static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;
        
        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }
    }
    
    /**
     * 默认构造方法
     */
    public HashMapImplementation() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    }
    
    /**
     * 指定初始容量的构造方法
     * 
     * @param initialCapacity 初始容量
     */
    public HashMapImplementation(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    
    /**
     * 指定初始容量和负载因子的构造方法
     * 
     * @param initialCapacity 初始容量
     * @param loadFactor 负载因子
     */
    public HashMapImplementation(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }
        
        this.loadFactor = loadFactor;
        this.threshold = (int) (initialCapacity * loadFactor);
    }
    
    /**
     * 计算哈希值
     * 将高16位与低16位异或，减少哈希冲突
     * 
     * @param key 键
     * @return 哈希值
     */
    static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    
    /**
     * 获取元素数量
     * 
     * @return 元素数量
     */
    public int size() {
        return size;
    }
    
    /**
     * 判断是否为空
     * 
     * @return 是否为空
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * 根据键获取值
     * 
     * @param key 键
     * @return 值，如果不存在返回null
     */
    public V get(Object key) {
        Node<K, V> e = getNode(hash(key), key);
        return e == null ? null : e.value;
    }
    
    /**
     * 获取节点
     * 
     * @param hash 哈希值
     * @param key 键
     * @return 节点
     */
    private Node<K, V> getNode(int hash, Object key) {
        Node<K, V>[] tab;
        Node<K, V> first, e;
        int n;
        K k;
        
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            // 检查第一个节点
            if (first.hash == hash &&
                ((k = first.key) == key || (key != null && key.equals(k)))) {
                return first;
            }
            // 遍历链表
            if ((e = first.next) != null) {
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        return e;
                    }
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
    
    /**
     * 判断是否包含键
     * 
     * @param key 键
     * @return 是否包含
     */
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }
    
    /**
     * 添加键值对
     * 
     * @param key 键
     * @param value 值
     * @return 之前的值，如果不存在返回null
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
    
    /**
     * 添加键值对的核心方法
     * 
     * @param hash 哈希值
     * @param key 键
     * @param value 值
     * @param onlyIfAbsent 如果为true，只在不存在时添加
     * @param evict 如果为false，表示处于创建模式
     * @return 之前的值
     */
    private V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        Node<K, V>[] tab;
        Node<K, V> p;
        int n, i;
        
        // 初始化数组
        if ((tab = table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length;
        }
        
        // 计算索引位置
        i = (n - 1) & hash;
        
        // 如果该位置为空，直接插入
        if ((p = tab[i]) == null) {
            tab[i] = new Node<>(hash, key, value, null);
        } else {
            Node<K, V> e;
            K k;
            
            // 检查第一个节点
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k)))) {
                e = p;
            } else {
                // 遍历链表
                while (true) {
                    if ((e = p.next) == null) {
                        p.next = new Node<>(hash, key, value, null);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        break;
                    }
                    p = e;
                }
            }
            
            // 如果找到相同key，替换value
            if (e != null) {
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null) {
                    e.value = value;
                }
                return oldValue;
            }
        }
        
        // 增加size，检查是否需要扩容
        if (++size > threshold) {
            resize();
        }
        return null;
    }
    
    /**
     * 扩容方法
     * 当元素数量超过阈值时，将数组容量翻倍，并重新哈希所有元素
     * 
     * @return 新的数组
     */
    @SuppressWarnings("unchecked")
    private Node<K, V>[] resize() {
        Node<K, V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            newCap = oldCap << 1;
            newThr = (int) (newCap * loadFactor);
        } else {
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        }
        
        threshold = newThr;
        
        @SuppressWarnings({"rawtypes", "unchecked"})
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
        table = newTab;
        
        // 重新哈希
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K, V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null) {
                        newTab[e.hash & (newCap - 1)] = e;
                    } else {
                        // 拆分链表
                        Node<K, V> loHead = null, loTail = null;
                        Node<K, V> hiHead = null, hiTail = null;
                        Node<K, V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null) {
                                    loHead = e;
                                } else {
                                    loTail.next = e;
                                }
                                loTail = e;
                            } else {
                                if (hiTail == null) {
                                    hiHead = e;
                                } else {
                                    hiTail.next = e;
                                }
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
    
    /**
     * 根据键删除元素
     * 
     * @param key 键
     * @return 被删除的值，如果不存在返回null
     */
    public V remove(Object key) {
        Node<K, V> e = removeNode(hash(key), key, null, false, true);
        return e == null ? null : e.value;
    }
    
    /**
     * 删除节点的核心方法
     * 
     * @param hash 哈希值
     * @param key 键
     * @param value 值（用于匹配）
     * @param matchValue 是否匹配值
     * @param movable 是否可移动
     * @return 被删除的节点
     */
    private Node<K, V> removeNode(int hash, Object key, Object value,
                                   boolean matchValue, boolean movable) {
        Node<K, V>[] tab;
        Node<K, V> p;
        int n, index;
        
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (p = tab[index = (n - 1) & hash]) != null) {
            Node<K, V> node = null, e;
            K k;
            V v;
            
            // 检查第一个节点
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k)))) {
                node = p;
            } else if ((e = p.next) != null) {
                // 遍历链表
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }
            
            // 找到节点，执行删除
            if (node != null && (!matchValue || (v = node.value) == value ||
                (value != null && value.equals(v)))) {
                if (node == p) {
                    tab[index] = node.next;
                } else {
                    p.next = node.next;
                }
                --size;
                return node;
            }
        }
        return null;
    }
    
    /**
     * 清空所有元素
     */
    public void clear() {
        Node<K, V>[] tab;
        if ((tab = table) != null && size > 0) {
            size = 0;
            for (int i = 0; i < tab.length; i++) {
                tab[i] = null;
            }
        }
    }
    
    /**
     * 判断是否包含值
     * 
     * @param value 值
     * @return 是否包含
     */
    public boolean containsValue(Object value) {
        Node<K, V>[] tab;
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<K, V> e = tab[i]; e != null; e = e.next) {
                    V v = e.value;
                    if (v == value || (value != null && value.equals(v))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * 演示HashMap的使用
     */
    public static void demonstrateUsage() {
        System.out.println("========== HashMap演示 ==========");
        
        HashMapImplementation<String, Integer> map = new HashMapImplementation<>();
        
        // 添加元素
        map.put("Alice", 25);
        map.put("Bob", 30);
        map.put("Charlie", 35);
        System.out.println("添加元素后大小: " + map.size());
        
        // 获取元素
        System.out.println("Alice的年龄: " + map.get("Alice"));
        
        // 更新元素
        map.put("Alice", 26);
        System.out.println("更新后Alice的年龄: " + map.get("Alice"));
        
        // 判断是否包含键
        System.out.println("是否包含Bob: " + map.containsKey("Bob"));
        
        // 删除元素
        map.remove("Bob");
        System.out.println("删除Bob后大小: " + map.size());
        
        // null键和null值
        map.put(null, 0);
        map.put("NullValue", null);
        System.out.println("null键的值: " + map.get(null));
        System.out.println("NullValue的值: " + map.get("NullValue"));
        
        System.out.println();
    }
    
    /**
     * 演示哈希冲突
     */
    public static void demonstrateHashCollision() {
        System.out.println("========== 哈希冲突演示 ==========");
        
        // 创建一个小容量的HashMap，强制产生冲突
        HashMapImplementation<Integer, String> map = new HashMapImplementation<>(4);
        
        // 添加多个元素，会产生哈希冲突
        for (int i = 0; i < 10; i++) {
            map.put(i, "Value" + i);
        }
        
        System.out.println("元素数量: " + map.size());
        System.out.println("所有元素都能正确获取:");
        for (int i = 0; i < 10; i++) {
            System.out.println("  Key " + i + " -> " + map.get(i));
        }
        
        System.out.println();
    }
    
    /**
     * 演示扩容机制
     */
    public static void demonstrateResize() {
        System.out.println("========== 扩容机制演示 ==========");
        
        HashMapImplementation<Integer, String> map = new HashMapImplementation<>(4, 0.75f);
        
        System.out.println("初始容量: 4, 负载因子: 0.75, 阈值: 3");
        
        // 添加元素，触发扩容
        for (int i = 1; i <= 5; i++) {
            map.put(i, "Value" + i);
            System.out.println("添加元素 " + i + "，当前大小: " + map.size());
        }
        
        System.out.println("扩容后所有元素仍然可访问:");
        for (int i = 1; i <= 5; i++) {
            System.out.println("  Key " + i + " -> " + map.get(i));
        }
        
        System.out.println();
    }
    
    /**
     * 主方法：运行所有演示
     */
    public static void main(String[] args) {
        demonstrateUsage();
        demonstrateHashCollision();
        demonstrateResize();
    }
}
