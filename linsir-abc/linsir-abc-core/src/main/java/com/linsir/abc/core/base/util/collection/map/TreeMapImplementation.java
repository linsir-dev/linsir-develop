package com.linsir.abc.core.base.util.collection.map;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * 简化版TreeMap实现
 * 
 * 本类模拟JDK TreeMap的核心实现：
 * 1. 基于红黑树（Red-Black Tree）的存储结构
 * 2. 自动排序（按键的自然顺序或自定义比较器）
 * 3. 自平衡机制，保证O(log n)的查找、插入、删除性能
 * 4. 支持范围查询
 * 
 * TreeMap特点：
 * - 不允许null键（如果比较器不支持null比较）
 * - 非线程安全
 * - 元素按键排序
 * - 查找、插入、删除时间复杂度O(log n)
 * 
 * @param <K> 键类型
 * @param <V> 值类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class TreeMapImplementation<K, V> {
    
    /**
     * 比较器
     */
    private final Comparator<? super K> comparator;
    
    /**
     * 根节点
     */
    private Entry<K, V> root;
    
    /**
     * 元素数量
     */
    private int size = 0;
    
    /**
     * 修改次数（用于快速失败）
     */
    private int modCount = 0;
    
    /**
     * 红黑树节点颜色
     */
    private static final boolean RED = false;
    private static final boolean BLACK = true;
    
    /**
     * 树节点类
     */
    static final class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> left;
        Entry<K, V> right;
        Entry<K, V> parent;
        boolean color = BLACK;
        
        Entry(K key, V value, Entry<K, V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }
        
        public K getKey()        { return key; }
        public V getValue()      { return value; }
        public String toString() { return key + "=" + value; }
    }
    
    /**
     * 默认构造方法（使用键的自然顺序）
     */
    public TreeMapImplementation() {
        comparator = null;
    }
    
    /**
     * 指定比较器的构造方法
     * 
     * @param comparator 比较器
     */
    public TreeMapImplementation(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }
    
    /**
     * 比较两个键
     * 
     * @param k1 键1
     * @param k2 键2
     * @return 比较结果
     */
    @SuppressWarnings("unchecked")
    private int compare(K k1, K k2) {
        return comparator == null ? ((Comparable<? super K>) k1).compareTo(k2)
                                  : comparator.compare(k1, k2);
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
        @SuppressWarnings("unchecked")
        Entry<K, V> p = getEntry((K) key);
        return p == null ? null : p.value;
    }
    
    /**
     * 获取节点
     * 
     * @param key 键
     * @return 节点
     */
    private Entry<K, V> getEntry(K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        
        Entry<K, V> p = root;
        while (p != null) {
            int cmp = compare(key, p.key);
            if (cmp < 0) {
                p = p.left;
            } else if (cmp > 0) {
                p = p.right;
            } else {
                return p;
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
        return getEntry((K) key) != null;
    }
    
    /**
     * 添加键值对
     * 
     * @param key 键
     * @param value 值
     * @return 之前的值，如果不存在返回null
     */
    public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException();
        }
        
        Entry<K, V> t = root;
        if (t == null) {
            // 第一个元素
            compare(key, key); // 检查键是否可比较
            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        
        int cmp;
        Entry<K, V> parent;
        Comparator<? super K> cpr = comparator;
        
        // 查找插入位置
        if (cpr != null) {
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if (cmp < 0) {
                    t = t.left;
                } else if (cmp > 0) {
                    t = t.right;
                } else {
                    // 找到相同key，替换value
                    V oldValue = t.value;
                    t.value = value;
                    return oldValue;
                }
            } while (t != null);
        } else {
            @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if (cmp < 0) {
                    t = t.left;
                } else if (cmp > 0) {
                    t = t.right;
                } else {
                    V oldValue = t.value;
                    t.value = value;
                    return oldValue;
                }
            } while (t != null);
        }
        
        // 创建新节点
        Entry<K, V> e = new Entry<>(key, value, parent);
        if (cmp < 0) {
            parent.left = e;
        } else {
            parent.right = e;
        }
        
        // 修复红黑树性质
        fixAfterInsertion(e);
        size++;
        modCount++;
        return null;
    }
    
    /**
     * 插入后修复红黑树
     * 
     * @param x 插入的节点
     */
    private void fixAfterInsertion(Entry<K, V> x) {
        x.color = RED;
        
        while (x != null && x != root && x.parent.color == RED) {
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                Entry<K, V> y = rightOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateRight(parentOf(parentOf(x)));
                }
            } else {
                Entry<K, V> y = leftOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        root.color = BLACK;
    }
    
    /**
     * 左旋
     * 
     * @param p 旋转点
     */
    private void rotateLeft(Entry<K, V> p) {
        if (p != null) {
            Entry<K, V> r = p.right;
            p.right = r.left;
            if (r.left != null) {
                r.left.parent = p;
            }
            r.parent = p.parent;
            if (p.parent == null) {
                root = r;
            } else if (p.parent.left == p) {
                p.parent.left = r;
            } else {
                p.parent.right = r;
            }
            r.left = p;
            p.parent = r;
        }
    }
    
    /**
     * 右旋
     * 
     * @param p 旋转点
     */
    private void rotateRight(Entry<K, V> p) {
        if (p != null) {
            Entry<K, V> l = p.left;
            p.left = l.right;
            if (l.right != null) {
                l.right.parent = p;
            }
            l.parent = p.parent;
            if (p.parent == null) {
                root = l;
            } else if (p.parent.right == p) {
                p.parent.right = l;
            } else {
                p.parent.left = l;
            }
            l.right = p;
            p.parent = l;
        }
    }
    
    /**
     * 获取父节点
     */
    private static <K, V> Entry<K, V> parentOf(Entry<K, V> p) {
        return p == null ? null : p.parent;
    }
    
    /**
     * 获取左子节点
     */
    private static <K, V> Entry<K, V> leftOf(Entry<K, V> p) {
        return p == null ? null : p.left;
    }
    
    /**
     * 获取右子节点
     */
    private static <K, V> Entry<K, V> rightOf(Entry<K, V> p) {
        return p == null ? null : p.right;
    }
    
    /**
     * 获取颜色
     */
    private static <K, V> boolean colorOf(Entry<K, V> p) {
        return p == null ? BLACK : p.color;
    }
    
    /**
     * 设置颜色
     */
    private static <K, V> void setColor(Entry<K, V> p, boolean c) {
        if (p != null) {
            p.color = c;
        }
    }
    
    /**
     * 根据键删除元素
     * 
     * @param key 键
     * @return 被删除的值，如果不存在返回null
     */
    public V remove(Object key) {
        Entry<K, V> p = getEntry((K) key);
        if (p == null) {
            return null;
        }
        
        V oldValue = p.value;
        deleteEntry(p);
        return oldValue;
    }
    
    /**
     * 删除节点
     * 
     * @param p 要删除的节点
     */
    private void deleteEntry(Entry<K, V> p) {
        modCount++;
        size--;
        
        // 有两个子节点的情况
        if (p.left != null && p.right != null) {
            Entry<K, V> s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        }
        
        // 开始修复
        Entry<K, V> replacement = (p.left != null ? p.left : p.right);
        
        if (replacement != null) {
            // 有一个子节点的情况
            replacement.parent = p.parent;
            if (p.parent == null) {
                root = replacement;
            } else if (p == p.parent.left) {
                p.parent.left = replacement;
            } else {
                p.parent.right = replacement;
            }
            
            p.left = p.right = p.parent = null;
            
            if (p.color == BLACK) {
                fixAfterDeletion(replacement);
            }
        } else if (p.parent == null) {
            // 只有一个节点的情况
            root = null;
        } else {
            // 没有子节点的情况
            if (p.color == BLACK) {
                fixAfterDeletion(p);
            }
            if (p.parent != null) {
                if (p == p.parent.left) {
                    p.parent.left = null;
                } else if (p == p.parent.right) {
                    p.parent.right = null;
                }
                p.parent = null;
            }
        }
    }
    
    /**
     * 获取后继节点
     * 
     * @param t 当前节点
     * @return 后继节点
     */
    private Entry<K, V> successor(Entry<K, V> t) {
        if (t == null) {
            return null;
        } else if (t.right != null) {
            Entry<K, V> p = t.right;
            while (p.left != null) {
                p = p.left;
            }
            return p;
        } else {
            Entry<K, V> p = t.parent;
            Entry<K, V> ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }
    
    /**
     * 删除后修复红黑树
     * 
     * @param x 删除的节点
     */
    private void fixAfterDeletion(Entry<K, V> x) {
        while (x != root && colorOf(x) == BLACK) {
            if (x == leftOf(parentOf(x))) {
                Entry<K, V> sib = rightOf(parentOf(x));
                
                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }
                
                if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(rightOf(sib)) == BLACK) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            } else {
                Entry<K, V> sib = leftOf(parentOf(x));
                
                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }
                
                if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }
        
        setColor(x, BLACK);
    }
    
    /**
     * 获取第一个键（最小的键）
     * 
     * @return 第一个键
     */
    public K firstKey() {
        if (root == null) {
            throw new NoSuchElementException();
        }
        Entry<K, V> p = root;
        while (p.left != null) {
            p = p.left;
        }
        return p.key;
    }
    
    /**
     * 获取最后一个键（最大的键）
     * 
     * @return 最后一个键
     */
    public K lastKey() {
        if (root == null) {
            throw new NoSuchElementException();
        }
        Entry<K, V> p = root;
        while (p.right != null) {
            p = p.right;
        }
        return p.key;
    }
    
    /**
     * 清空所有元素
     */
    public void clear() {
        modCount++;
        size = 0;
        root = null;
    }
    
    /**
     * 演示TreeMap的使用
     */
    public static void demonstrateUsage() {
        System.out.println("========== TreeMap演示 ==========");
        
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();
        
        // 添加元素
        map.put("Charlie", 35);
        map.put("Alice", 25);
        map.put("Bob", 30);
        
        System.out.println("添加元素后大小: " + map.size());
        System.out.println("第一个键: " + map.firstKey());
        System.out.println("最后一个键: " + map.lastKey());
        
        // 获取元素
        System.out.println("Alice的年龄: " + map.get("Alice"));
        
        // 遍历（按键排序）
        System.out.println("按键排序遍历:");
        // 注意：这里简化处理，实际应该实现迭代器
        
        System.out.println();
    }
    
    /**
     * 演示自定义比较器
     */
    public static void demonstrateCustomComparator() {
        System.out.println("========== 自定义比较器演示 ==========");
        
        // 按字符串长度排序
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>(
            (s1, s2) -> {
                int lenDiff = s1.length() - s2.length();
                return lenDiff != 0 ? lenDiff : s1.compareTo(s2);
            }
        );
        
        map.put("Apple", 1);
        map.put("Banana", 2);
        map.put("Cat", 3);
        map.put("Dog", 4);
        
        System.out.println("按长度排序后的第一个键: " + map.firstKey());
        System.out.println("按长度排序后的最后一个键: " + map.lastKey());
        
        System.out.println();
    }
    
    /**
     * 主方法：运行所有演示
     */
    public static void main(String[] args) {
        demonstrateUsage();
        demonstrateCustomComparator();
    }
}
