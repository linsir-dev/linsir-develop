package com.linsir.abc.pdai.structure.dataStructure;

/**
 * 哈希表示例代码
 * 
 * 说明：
 * 1. 哈希表（Hash Table）是一种根据键（Key）直接访问内存存储位置的数据结构
 * 2. 哈希表的特点：
 *    - 平均查找、插入、删除的时间复杂度为O(1)
 *    - 通过哈希函数将键映射到存储位置
 *    - 可能会发生哈希冲突，需要解决冲突的方法
 *    - 适合存储键值对数据
 */
public class HashMapDemo {

    /**
     * 哈希表节点类
     */
    private static class HashNode {
        String key;
        int value;
        HashNode next;

        HashNode(String key, int value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    /**
     * 简单哈希表实现（使用链表解决冲突）
     */
    private static class HashMap {
        private static final int DEFAULT_CAPACITY = 16;
        private static final float LOAD_FACTOR = 0.75f;
        
        private HashNode[] table;
        private int size;
        private int capacity;

        HashMap() {
            this.capacity = DEFAULT_CAPACITY;
            this.table = new HashNode[capacity];
            this.size = 0;
        }

        /**
         * 哈希函数
         */
        private int hash(String key) {
            int hash = 0;
            for (char c : key.toCharArray()) {
                hash = 31 * hash + c;
            }
            return Math.abs(hash) % capacity;
        }

        /**
         * 添加键值对
         */
        public void put(String key, int value) {
            // 检查是否需要扩容
            if ((float) size / capacity > LOAD_FACTOR) {
                resize();
            }

            int index = hash(key);
            HashNode newNode = new HashNode(key, value);

            // 如果该位置为空，直接插入
            if (table[index] == null) {
                table[index] = newNode;
            } else {
                // 遍历链表，查找是否存在相同的键
                HashNode current = table[index];
                HashNode prev = null;
                
                while (current != null) {
                    if (current.key.equals(key)) {
                        // 更新值
                        current.value = value;
                        return;
                    }
                    prev = current;
                    current = current.next;
                }
                
                // 没有找到相同的键，添加到链表末尾
                prev.next = newNode;
            }
            
            size++;
        }

        /**
         * 获取值
         */
        public int get(String key) {
            int index = hash(key);
            HashNode current = table[index];

            // 遍历链表查找键
            while (current != null) {
                if (current.key.equals(key)) {
                    return current.value;
                }
                current = current.next;
            }

            // 键不存在
            throw new IllegalArgumentException("Key not found: " + key);
        }

        /**
         * 删除键值对
         */
        public void remove(String key) {
            int index = hash(key);
            HashNode current = table[index];
            HashNode prev = null;

            // 遍历链表查找键
            while (current != null) {
                if (current.key.equals(key)) {
                    // 找到键，删除节点
                    if (prev == null) {
                        // 是链表的第一个节点
                        table[index] = current.next;
                    } else {
                        prev.next = current.next;
                    }
                    size--;
                    return;
                }
                prev = current;
                current = current.next;
            }

            // 键不存在
            throw new IllegalArgumentException("Key not found: " + key);
        }

        /**
         * 检查键是否存在
         */
        public boolean containsKey(String key) {
            int index = hash(key);
            HashNode current = table[index];

            // 遍历链表查找键
            while (current != null) {
                if (current.key.equals(key)) {
                    return true;
                }
                current = current.next;
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
         * 扩容
         */
        private void resize() {
            int newCapacity = capacity * 2;
            HashNode[] newTable = new HashNode[newCapacity];

            // 重新哈希所有元素
            for (int i = 0; i < capacity; i++) {
                HashNode current = table[i];
                while (current != null) {
                    HashNode next = current.next;
                    int newIndex = Math.abs(hash(current.key)) % newCapacity;
                    
                    // 插入到新表的对应位置
                    current.next = newTable[newIndex];
                    newTable[newIndex] = current;
                    
                    current = next;
                }
            }

            table = newTable;
            capacity = newCapacity;
        }

        /**
         * 打印哈希表
         */
        public void print() {
            System.out.println("哈希表内容:");
            for (int i = 0; i < capacity; i++) {
                HashNode current = table[i];
                if (current != null) {
                    System.out.print("位置 " + i + ": ");
                    while (current != null) {
                        System.out.print("[" + current.key + ": " + current.value + "] -> ");
                        current = current.next;
                    }
                    System.out.println("null");
                }
            }
        }
    }

    /**
     * 测试自定义哈希表
     */
    public void testCustomHashMap() {
        System.out.println("=== 自定义哈希表示例 ===");
        
        HashMap map = new HashMap();
        
        // 添加键值对
        map.put("apple", 10);
        map.put("banana", 20);
        map.put("orange", 30);
        map.put("grape", 40);
        map.put("watermelon", 50);
        
        System.out.println("哈希表大小: " + map.size());
        map.print();
        
        // 获取值
        System.out.println("apple的值: " + map.get("apple"));
        System.out.println("banana的值: " + map.get("banana"));
        
        // 修改值
        map.put("apple", 15);
        System.out.println("修改后apple的值: " + map.get("apple"));
        
        // 检查键是否存在
        System.out.println("是否包含键orange: " + map.containsKey("orange"));
        System.out.println("是否包含键pear: " + map.containsKey("pear"));
        
        // 删除键值对
        map.remove("banana");
        System.out.println("删除banana后，哈希表大小: " + map.size());
        map.print();
        
        // 检查哈希表是否为空
        System.out.println("哈希表是否为空: " + map.isEmpty());
    }

    /**
     * 测试Java内置HashMap
     */
    public void testJavaHashMap() {
        System.out.println("\n=== Java内置HashMap示例 ===");
        
        java.util.HashMap<String, Integer> map = new java.util.HashMap<>();
        
        // 添加键值对
        map.put("apple", 10);
        map.put("banana", 20);
        map.put("orange", 30);
        map.put("grape", 40);
        map.put("watermelon", 50);
        
        System.out.println("HashMap大小: " + map.size());
        System.out.println("HashMap内容: " + map);
        
        // 获取值
        System.out.println("apple的值: " + map.get("apple"));
        System.out.println("banana的值: " + map.get("banana"));
        
        // 修改值
        map.put("apple", 15);
        System.out.println("修改后apple的值: " + map.get("apple"));
        
        // 检查键是否存在
        System.out.println("是否包含键orange: " + map.containsKey("orange"));
        System.out.println("是否包含键pear: " + map.containsKey("pear"));
        
        // 删除键值对
        map.remove("banana");
        System.out.println("删除banana后，HashMap大小: " + map.size());
        System.out.println("HashMap内容: " + map);
        
        // 检查HashMap是否为空
        System.out.println("HashMap是否为空: " + map.isEmpty());
        
        // 遍历HashMap
        System.out.println("遍历HashMap:");
        for (String key : map.keySet()) {
            System.out.println(key + ": " + map.get(key));
        }
        
        // 使用forEach遍历
        System.out.println("使用forEach遍历:");
        map.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    /**
     * 哈希表应用示例
     */
    public void hashMapApplications() {
        System.out.println("\n=== 哈希表应用示例 ===");
        
        // 示例1：统计字符串中每个字符出现的次数
        System.out.println("示例1：统计字符串中每个字符出现的次数");
        String str = "hello world";
        java.util.HashMap<Character, Integer> charCountMap = new java.util.HashMap<>();
        
        for (char c : str.toCharArray()) {
            if (c != ' ') { // 跳过空格
                charCountMap.put(c, charCountMap.getOrDefault(c, 0) + 1);
            }
        }
        
        System.out.println("字符出现次数: " + charCountMap);
        
        // 示例2：两数之和问题
        System.out.println("\n示例2：两数之和问题");
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
        
        // 示例3：分组单词
        System.out.println("\n示例3：分组单词（按首字母）");
        String[] words = {"apple", "banana", "orange", "grape", "watermelon", "pear", "peach"};
        java.util.HashMap<Character, java.util.List<String>> wordMap = new java.util.HashMap<>();
        
        for (String word : words) {
            char firstChar = word.charAt(0);
            wordMap.computeIfAbsent(firstChar, k -> new java.util.ArrayList<>()).add(word);
        }
        
        System.out.println("按首字母分组的单词: " + wordMap);
    }
}
