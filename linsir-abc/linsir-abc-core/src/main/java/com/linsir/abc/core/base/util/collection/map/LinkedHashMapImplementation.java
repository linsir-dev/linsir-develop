package com.linsir.abc.core.base.util.collection.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 简化版LinkedHashMap实现
 * 
 * 本类模拟JDK LinkedHashMap的核心实现：
 * 1. 继承HashMap的哈希表结构
 * 2. 维护双向链表保持插入顺序或访问顺序
 * 3. 支持按插入顺序或访问顺序遍历
 * 
 * LinkedHashMap特点：
 * - 保持插入顺序（默认）或访问顺序
 * - 允许null键和null值
 * - 非线程安全
 * - 迭代性能优于HashMap
 * - 适用于需要保持顺序的场景
 * 
 * @param <K> 键类型
 * @param <V> 值类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class LinkedHashMapImplementation<K, V> {

    /**
     * 默认初始容量
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * 默认负载因子
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 哈希表
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
     * 是否按访问顺序排序
     */
    private final boolean accessOrder;

    /**
     * 双向链表头节点（最老的）
     */
    Node<K, V> head;

    /**
     * 双向链表尾节点（最新的）
     */
    Node<K, V> tail;

    /**
     * 节点类（继承HashMap的节点，添加前后指针）
     */
    public static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;      // 哈希表链表
        Node<K, V> before;    // 双向链表前驱
        Node<K, V> after;     // 双向链表后继

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /**
     * 默认构造方法（按插入顺序）
     */
    public LinkedHashMapImplementation() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, false);
    }

    /**
     * 指定初始容量的构造方法
     * 
     * @param initialCapacity 初始容量
     */
    public LinkedHashMapImplementation(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, false);
    }

    /**
     * 指定初始容量和负载因子的构造方法
     * 
     * @param initialCapacity 初始容量
     * @param loadFactor 负载因子
     */
    public LinkedHashMapImplementation(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, false);
    }

    /**
     * 完整构造方法
     * 
     * @param initialCapacity 初始容量
     * @param loadFactor 负载因子
     * @param accessOrder true表示按访问顺序，false表示按插入顺序
     */
    public LinkedHashMapImplementation(int initialCapacity, float loadFactor, boolean accessOrder) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        this.loadFactor = loadFactor;
        this.threshold = (int) (initialCapacity * loadFactor);
        this.accessOrder = accessOrder;
    }

    /**
     * 计算哈希值
     */
    static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 获取元素数量
     */
    public int size() {
        return size;
    }

    /**
     * 判断是否为空
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 根据键获取值
     */
    public V get(Object key) {
        Node<K, V> e = getNode(hash(key), key);
        if (e == null) {
            return null;
        }
        // 如果按访问顺序，将节点移到尾部
        if (accessOrder) {
            afterNodeAccess(e);
        }
        return e.value;
    }

    /**
     * 获取节点
     */
    private Node<K, V> getNode(int hash, Object key) {
        Node<K, V>[] tab;
        Node<K, V> first, e;
        int n;
        K k;

        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash &&
                ((k = first.key) == key || (key != null && key.equals(k)))) {
                return first;
            }
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
     */
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    /**
     * 添加键值对
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    /**
     * 添加键值对的核心方法
     */
    private V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        Node<K, V>[] tab;
        Node<K, V> p;
        int n, i;

        if ((tab = table) == null || (n = tab.length) == 0) {
            n = (tab = resize()).length;
        }

        i = (n - 1) & hash;

        if ((p = tab[i]) == null) {
            tab[i] = newNode(hash, key, value, null);
        } else {
            Node<K, V> e;
            K k;

            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k)))) {
                e = p;
            } else {
                while (true) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        break;
                    }
                    p = e;
                }
            }

            if (e != null) {
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null) {
                    e.value = value;
                }
                afterNodeAccess(e);
                return oldValue;
            }
        }

        if (++size > threshold) {
            resize();
        }
        afterNodeInsertion(evict);
        return null;
    }

    /**
     * 创建新节点
     */
    private Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
        Node<K, V> p = new Node<>(hash, key, value, next);
        linkNodeLast(p);
        return p;
    }

    /**
     * 将节点链接到链表尾部
     */
    private void linkNodeLast(Node<K, V> p) {
        Node<K, V> last = tail;
        tail = p;
        if (last == null) {
            head = p;
        } else {
            p.before = last;
            last.after = p;
        }
    }

    /**
     * 访问节点后的处理（按访问顺序时移到尾部）
     */
    private void afterNodeAccess(Node<K, V> e) {
        Node<K, V> last;
        if (accessOrder && (last = tail) != e) {
            Node<K, V> p = e, b = p.before, a = p.after;
            p.after = null;
            if (b == null) {
                head = a;
            } else {
                b.after = a;
            }
            if (a != null) {
                a.before = b;
            } else {
                last = b;
            }
            if (last == null) {
                head = p;
            } else {
                p.before = last;
                last.after = p;
            }
            tail = p;
        }
    }

    /**
     * 插入节点后的处理
     */
    private void afterNodeInsertion(boolean evict) {
        // 可以在这里实现LRU缓存的删除逻辑
    }

    /**
     * 删除节点后的处理
     */
    private void afterNodeRemoval(Node<K, V> e) {
        Node<K, V> b = e.before, a = e.after;
        e.before = e.after = null;
        if (b == null) {
            head = a;
        } else {
            b.after = a;
        }
        if (a == null) {
            tail = b;
        } else {
            a.before = b;
        }
    }

    /**
     * 扩容方法
     */
    @SuppressWarnings("unchecked")
    private Node<K, V>[] resize() {
        Node<K, V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;

        if (oldCap > 0) {
            if (oldCap >= 1 << 30) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            newCap = oldCap << 1;
            newThr = (int) (newCap * loadFactor);
        } else {
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int) (DEFAULT_INITIAL_CAPACITY * loadFactor);
        }

        threshold = newThr;

        @SuppressWarnings({"rawtypes", "unchecked"})
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
        table = newTab;

        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K, V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null) {
                        newTab[e.hash & (newCap - 1)] = e;
                    } else {
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
     */
    public V remove(Object key) {
        Node<K, V> e = removeNode(hash(key), key, null, false, true);
        return e == null ? null : e.value;
    }

    /**
     * 删除节点的核心方法
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

            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k)))) {
                node = p;
            } else if ((e = p.next) != null) {
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }

            if (node != null && (!matchValue || (v = node.value) == value ||
                (value != null && value.equals(v)))) {
                if (node == p) {
                    tab[index] = node.next;
                } else {
                    p.next = node.next;
                }
                --size;
                afterNodeRemoval(node);
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
            head = tail = null;
        }
    }

    /**
     * 判断是否包含值
     */
    public boolean containsValue(Object value) {
        if (value == null) {
            for (Node<K, V> e = head; e != null; e = e.after) {
                if (e.value == null) {
                    return true;
                }
            }
        } else {
            for (Node<K, V> e = head; e != null; e = e.after) {
                if (value.equals(e.value)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 演示LinkedHashMap的使用
     */
    public static void demonstrateUsage() {
        System.out.println("========== LinkedHashMap演示 ==========");

        LinkedHashMapImplementation<String, Integer> map = new LinkedHashMapImplementation<>();

        // 添加元素
        map.put("First", 1);
        map.put("Second", 2);
        map.put("Third", 3);
        map.put("Fourth", 4);

        System.out.println("添加元素后大小: " + map.size());

        // 按插入顺序遍历
        System.out.println("按插入顺序遍历:");
        for (Node<String, Integer> e = map.head; e != null; e = e.after) {
            System.out.println("  " + e.key + " = " + e.value);
        }

        System.out.println();
    }

    /**
     * 演示访问顺序
     */
    public static void demonstrateAccessOrder() {
        System.out.println("========== 访问顺序演示 ==========");

        LinkedHashMapImplementation<String, Integer> map = 
            new LinkedHashMapImplementation<>(16, 0.75f, true);

        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.put("D", 4);

        System.out.println("初始顺序:");
        for (Node<String, Integer> e = map.head; e != null; e = e.after) {
            System.out.println("  " + e.key + " = " + e.value);
        }

        // 访问B
        map.get("B");
        System.out.println("\n访问B后的顺序:");
        for (Node<String, Integer> e = map.head; e != null; e = e.after) {
            System.out.println("  " + e.key + " = " + e.value);
        }

        // 访问D
        map.get("D");
        System.out.println("\n访问D后的顺序:");
        for (Node<String, Integer> e = map.head; e != null; e = e.after) {
            System.out.println("  " + e.key + " = " + e.value);
        }

        System.out.println();
    }

    /**
     * 主方法：运行所有演示
     */
    public static void main(String[] args) {
        demonstrateUsage();
        demonstrateAccessOrder();
    }
}
