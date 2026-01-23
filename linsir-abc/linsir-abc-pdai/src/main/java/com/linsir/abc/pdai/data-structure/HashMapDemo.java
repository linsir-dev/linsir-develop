package com.linsir.abc.pdai.data-structure;

import java.util.LinkedList;

/**
 * 哈希表示例代码
 * 
 * 说明：
 * 1. 哈希表是一种通过哈希函数将键映射到值的数据结构
 * 2. 哈希表的特点：
 *    - 平均查找、插入、删除时间复杂度为O(1)
 *    - 存储键值对
 *    - 通过哈希函数计算索引
 *    - 可能出现哈希冲突，需要处理冲突
 * 3. 常见的哈希冲突处理方法：
 *    - 链地址法（本示例使用）
 *    - 开放地址法
 *    - 再哈希法
 *    - 建立公共溢出区
 */
public class HashMapDemo {

    /**
     * 哈希表节点类
     */
    private static class HashNode<K, V> {
        K key;
        V value;
        HashNode<K, V> next;

        HashNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    /**
     * 简单哈希表实现
     */
    private static class HashMap<K, V> {
        private LinkedList<HashNode<K, V>>[] buckets;
        private int capacity;
        private int size;

        /**
         * 构造函数
         */
        @SuppressWarnings("unchecked")
        HashMap(int capacity) {
            this.capacity = capacity;
            this.size = 0;
            this.buckets = new LinkedList[capacity];
            
            // 初始化每个桶
            for (int i = 0; i < capacity; i++) {
                buckets[i] = new LinkedList<>();
            }
        }

        /**
         * 无参构造函数，默认容量为16
         */
        HashMap() {
            this(16);
        }

        /**
         * 哈希函数，计算键的哈希值
         */
        private int getBucketIndex(K key) {
            if (key == null) {
                return 0;
            }
            int hashCode = key.hashCode();
            return Math.abs(hashCode) % capacity;
        }

        /**
         * 添加键值对
         */
        public void put(K key, V value) {
            int bucketIndex = getBucketIndex(key);
            LinkedList<HashNode<K, V>> bucket = buckets[bucketIndex];

            // 检查键是否已存在
            for (HashNode<K, V> node : bucket) {
                if (node.key.equals(key)) {
                    node.value = value;
                    return;
                }
            }

            // 键不存在，添加新节点
            HashNode<K, V> newNode = new HashNode<>(key, value);
            bucket.add(newNode);
            size++;

            // 检查负载因子，超过0.75则扩容
            if ((double) size / capacity >= 0.75) {
                resize();
            }
        }

        /**
         * 获取值
         */
        public V get(K key) {
            int bucketIndex = getBucketIndex(key);
            LinkedList<HashNode<K, V>> bucket = buckets[bucketIndex];

            // 查找键
            for (HashNode<K, V> node : bucket) {
                if (node.key.equals(key)) {
                    return node.value;
                }
            }

            return null; // 键不存在
        }

        /**
         * 删除键值对
         */
        public V remove(K key) {
            int bucketIndex = getBucketIndex(key);
            LinkedList<HashNode<K, V>> bucket = buckets[bucketIndex];

            // 查找并删除键
            for (HashNode<K, V> node : bucket) {
                if (node.key.equals(key)) {
                    V value = node.value;
                    bucket.remove(node);
                    size--;
                    return value;
                }
            }

            return null; // 键不存在
        }

        /**
         * 检查键是否存在
         */
        public boolean containsKey(K key) {
            int bucketIndex = getBucketIndex(key);
            LinkedList<HashNode<K, V>> bucket = buckets[bucketIndex];

            // 查找键
            for (HashNode<K, V> node : bucket) {
                if (node.key.equals(key)) {
                    return true;
                }
            }

            return false;
        }

        /**
         * 获取哈希表大小
         */
        public int size() {
            return size;
        }

        /**
         * 检查哈希表是否为空
         */
        public boolean isEmpty() {
            return size == 0;
        }

        /**
         * 扩容哈希表
         */
        @SuppressWarnings("unchecked")
        private void resize() {
            int newCapacity = capacity * 2;
            LinkedList<HashNode<K, V>>[] newBuckets = new LinkedList[newCapacity];

            // 初始化新桶
            for (int i = 0; i < newCapacity; i++) {
                newBuckets[i] = new LinkedList<>();
            }

            // 重新哈希所有键值对
            for (int i = 0; i < capacity; i++) {
                LinkedList<HashNode<K, V>> bucket = buckets[i];
                for (HashNode<K, V> node : bucket) {
                    int newBucketIndex = Math.abs(node.key.hashCode()) % newCapacity;
                    newBuckets[newBucketIndex].add(node);
                }
            }

            // 更新哈希表
            this.buckets = newBuckets;
            this.capacity = newCapacity;
        }

        /**
         * 打印哈希表
         */
        public void print() {
            System.out.println("哈希表内容:");
            for (int i = 0; i < capacity; i++) {
                LinkedList<HashNode<K, V>> bucket = buckets[i];
                System.out.print("桶 " + i + ": ");
                for (HashNode<K, V> node : bucket) {
                    System.out.print("(" + node.key + ", " + node.value + ") -> ");
                }
                System.out.println("null");
            }
        }
    }

    /**
     * 测试自定义哈希表
     */
    public void testCustomHashMap() {
        System.out.println("=== 自定义哈希表示例 ===");
        
        HashMap<String, Integer> map = new HashMap<>();
        
        // 添加键值对
        map.put("Alice", 25);
        map.put("Bob", 30);
        map.put("Charlie", 35);
        map.put("David", 40);
        map.print();
        System.out.println("哈希表大小: " + map.size());
        
        // 获取值
        System.out.println("Alice的年龄: " + map.get("Alice"));
        System.out.println("Bob的年龄: " + map.get("Bob"));
        System.out.println("Eve的年龄: " + map.get("Eve")); // 不存在的键
        
        // 检查键是否存在
        System.out.println("是否包含键Charlie: " + map.containsKey("Charlie"));
        System.out.println("是否包含键Eve: " + map.containsKey("Eve"));
        
        // 删除键值对
        map.remove("Bob");
        map.print();
        System.out.println("哈希表大小: " + map.size());
        
        // 检查哈希表是否为空
        System.out.println("哈希表是否为空: " + map.isEmpty());
        
        // 测试扩容
        System.out.println("\n测试扩容:");
        // 添加更多元素，触发扩容
        for (int i = 0; i < 20; i++) {
            map.put("Person" + i, i * 5);
        }
        map.print();
        System.out.println("哈希表大小: " + map.size());
    }

    /**
     * 测试Java内置HashMap
     */
    public void testJavaHashMap() {
        System.out.println("\n=== Java内置哈希表示例 ===");
        
        java.util.HashMap<String, Integer> map = new java.util.HashMap<>();
        
        // 添加键值对
        map.put("Alice", 25);
        map.put("Bob", 30);
        map.put("Charlie", 35);
        map.put("David", 40);
        System.out.println("HashMap内容: " + map);
        System.out.println("HashMap大小: " + map.size());
        
        // 获取值
        System.out.println("Alice的年龄: " + map.get("Alice"));
        System.out.println("Bob的年龄: " + map.get("Bob"));
        System.out.println("Eve的年龄: " + map.get("Eve")); // 不存在的键
        
        // 检查键是否存在
        System.out.println("是否包含键Charlie: " + map.containsKey("Charlie"));
        System.out.println("是否包含键Eve: " + map.containsKey("Eve"));
        
        // 删除键值对
        map.remove("Bob");
        System.out.println("删除Bob后: " + map);
        System.out.println("HashMap大小: " + map.size());
        
        // 遍历HashMap
        System.out.println("\n遍历HashMap:");
        // 方式1: 使用keySet()
        System.out.println("使用keySet()遍历:");
        for (String key : map.keySet()) {
            System.out.println(key + ": " + map.get(key));
        }
        
        // 方式2: 使用entrySet()
        System.out.println("\n使用entrySet()遍历:");
        for (java.util.Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // 方式3: 使用forEach (Java 8+)
        System.out.println("\n使用forEach遍历:");
        map.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    /**
     * 哈希表的应用示例
     */
    public void hashMapApplications() {
        System.out.println("\n=== 哈希表应用示例 ===");
        
        // 示例1: 统计字符串中字符出现的次数
        System.out.println("示例1: 统计字符串中字符出现的次数");
        String str = "hello world";
        java.util.HashMap<Character, Integer> charCount = new java.util.HashMap<>();
        
        for (char c : str.toCharArray()) {
            if (c != ' ') { // 忽略空格
                charCount.put(c, charCount.getOrDefault(c, 0) + 1);
            }
        }
        
        System.out.println("字符出现次数: " + charCount);
        
        // 示例2: 两数之和问题
        System.out.println("\n示例2: 两数之和问题");
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        java.util.HashMap<Integer, Integer> numMap = new java.util.HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (numMap.containsKey(complement)) {
                System.out.println("两数之和为" + target + "的索引: [" + numMap.get(complement) + ", " + i + "]");
                break;
            }
            numMap.put(nums[i], i);
        }
        
        // 示例3: 查找重复元素
        System.out.println("\n示例3: 查找重复元素");
        int[] array = {1, 2, 3, 4, 5, 2, 3};
        java.util.HashMap<Integer, Integer> countMap = new java.util.HashMap<>();
        
        for (int num : array) {
            countMap.put(num, countMap.getOrDefault(num, 0) + 1);
        }
        
        System.out.println("重复元素:");
        for (java.util.Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println(entry.getKey() + " 出现了 " + entry.getValue() + " 次");
            }
        }
    }

    /**
     * 哈希表性能测试
     */
    public void hashMapPerformanceTest() {
        System.out.println("\n=== 哈希表性能测试 ===");
        
        int size = 100000;
        java.util.HashMap<Integer, Integer> map = new java.util.HashMap<>();
        
        // 测试插入性能
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            map.put(i, i);
        }
        long insertTime = System.currentTimeMillis() - startTime;
        System.out.println("插入" + size + "个元素耗时: " + insertTime + "ms");
        
        // 测试查找性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            map.get(i);
        }
        long searchTime = System.currentTimeMillis() - startTime;
        System.out.println("查找" + size + "个元素耗时: " + searchTime + "ms");
        
        // 测试删除性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < size; i++) {
            map.remove(i);
        }
        long deleteTime = System.currentTimeMillis() - startTime;
        System.out.println("删除" + size + "个元素耗时: " + deleteTime + "ms");
    }
}
