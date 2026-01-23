package com.linsir.abc.pdai.data-structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Trie树示例代码
 * 
 * 说明：
 * 1. Trie树（字典树）是一种用于高效存储和检索字符串数据集中键的树形数据结构
 * 2. Trie树的特点：
 *    - 根节点不包含字符
 *    - 每个节点的子节点代表不同的字符
 *    - 从根节点到某一节点的路径代表一个字符串
 *    - 每个节点可以标记是否为一个单词的结束
 * 3. Trie树的优势：
 *    - 高效的字符串查找，时间复杂度为O(m)，其中m是字符串长度
 *    - 支持前缀查询
 *    - 节省空间，相同前缀的字符串共享存储
 * 4. 应用场景：
 *    - 自动补全
 *    - 拼写检查
 *    - IP路由
 *    - 字典实现
 *    - 单词游戏（如Scrabble）
 */
public class TrieTreeDemo {

    /**
     * Trie树节点类
     */
    private static class TrieNode {
        private static final int ALPHABET_SIZE = 26; // 假设只处理小写字母
        TrieNode[] children;
        boolean isEndOfWord;

        TrieNode() {
            children = new TrieNode[ALPHABET_SIZE];
            isEndOfWord = false;
        }

        /**
         * 获取子节点
         */
        TrieNode getChild(char c) {
            return children[c - 'a'];
        }

        /**
         * 设置子节点
         */
        void setChild(char c, TrieNode node) {
            children[c - 'a'] = node;
        }

        /**
         * 检查是否有子节点
         */
        boolean hasChild(char c) {
            return children[c - 'a'] != null;
        }

        /**
         * 标记为单词结束
         */
        void markAsEndOfWord() {
            isEndOfWord = true;
        }

        /**
         * 标记为非单词结束
         */
        void unmarkAsEndOfWord() {
            isEndOfWord = false;
        }

        /**
         * 检查是否为单词结束
         */
        boolean isEndOfWord() {
            return isEndOfWord;
        }

        /**
         * 获取所有非空子节点
         */
        List<Character> getNonEmptyChildren() {
            List<Character> chars = new ArrayList<>();
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                if (children[i] != null) {
                    chars.add((char) (i + 'a'));
                }
            }
            return chars;
        }
    }

    /**
     * Trie树类
     */
    private static class TrieTree {
        private TrieNode root;

        TrieTree() {
            root = new TrieNode();
        }

        /**
         * 插入字符串
         */
        public void insert(String word) {
            if (word == null || word.isEmpty()) {
                return;
            }

            TrieNode current = root;
            for (char c : word.toCharArray()) {
                if (!current.hasChild(c)) {
                    current.setChild(c, new TrieNode());
                }
                current = current.getChild(c);
            }
            current.markAsEndOfWord();
        }

        /**
         * 查找字符串
         */
        public boolean search(String word) {
            if (word == null || word.isEmpty()) {
                return false;
            }

            TrieNode current = root;
            for (char c : word.toCharArray()) {
                if (!current.hasChild(c)) {
                    return false;
                }
                current = current.getChild(c);
            }
            return current.isEndOfWord();
        }

        /**
         * 检查是否有以给定前缀开头的字符串
         */
        public boolean startsWith(String prefix) {
            if (prefix == null || prefix.isEmpty()) {
                return true;
            }

            TrieNode current = root;
            for (char c : prefix.toCharArray()) {
                if (!current.hasChild(c)) {
                    return false;
                }
                current = current.getChild(c);
            }
            return true;
        }

        /**
         * 删除字符串
         */
        public boolean delete(String word) {
            if (word == null || word.isEmpty() || !search(word)) {
                return false;
            }
            return delete(root, word, 0);
        }

        /**
         * 递归删除字符串
         */
        private boolean delete(TrieNode node, String word, int index) {
            if (index == word.length()) {
                if (!node.isEndOfWord()) {
                    return false;
                }
                node.unmarkAsEndOfWord();
                // 如果节点没有子节点，可以删除
                return node.getNonEmptyChildren().isEmpty();
            }

            char c = word.charAt(index);
            TrieNode child = node.getChild(c);
            if (child == null) {
                return false;
            }

            boolean shouldDeleteChild = delete(child, word, index + 1);
            if (shouldDeleteChild) {
                node.setChild(c, null);
                // 如果节点不是单词结束且没有其他子节点，可以删除
                return !node.isEndOfWord() && node.getNonEmptyChildren().isEmpty();
            }

            return false;
        }

        /**
         * 获取所有以给定前缀开头的字符串
         */
        public List<String> getWordsWithPrefix(String prefix) {
            List<String> result = new ArrayList<>();
            if (prefix == null) {
                return result;
            }

            TrieNode current = root;
            for (char c : prefix.toCharArray()) {
                if (!current.hasChild(c)) {
                    return result;
                }
                current = current.getChild(c);
            }

            collectWords(current, prefix, result);
            return result;
        }

        /**
         * 递归收集单词
         */
        private void collectWords(TrieNode node, String prefix, List<String> result) {
            if (node.isEndOfWord()) {
                result.add(prefix);
            }

            for (char c : node.getNonEmptyChildren()) {
                collectWords(node.getChild(c), prefix + c, result);
            }
        }

        /**
         * 获取Trie树中的所有单词
         */
        public List<String> getAllWords() {
            List<String> result = new ArrayList<>();
            collectWords(root, "", result);
            return result;
        }

        /**
         * 打印Trie树
         */
        public void print() {
            System.out.println("Trie树结构:");
            print(root, "");
        }

        /**
         * 递归打印Trie树
         */
        private void print(TrieNode node, String prefix) {
            if (node.isEndOfWord()) {
                System.out.println("Word: " + prefix);
            }

            for (char c : node.getNonEmptyChildren()) {
                print(node.getChild(c), prefix + c);
            }
        }

        /**
         * 计算Trie树中的单词数量
         */
        public int countWords() {
            List<String> words = getAllWords();
            return words.size();
        }

        /**
         * 检查Trie树是否为空
         */
        public boolean isEmpty() {
            return root.getNonEmptyChildren().isEmpty();
        }
    }

    /**
     * 测试Trie树
     */
    public void testTrieTree() {
        System.out.println("=== Trie树示例 ===");
        
        TrieTree trie = new TrieTree();
        
        // 插入单词
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        trie.insert("banana");
        trie.insert("bat");
        trie.insert("cat");
        
        // 打印Trie树
        trie.print();
        
        // 测试查找
        System.out.println("查找'apple': " + trie.search("apple"));
        System.out.println("查找'app': " + trie.search("app"));
        System.out.println("查找'application': " + trie.search("application"));
        System.out.println("查找'orange': " + trie.search("orange"));
        
        // 测试前缀查询
        System.out.println("前缀'app': " + trie.startsWith("app"));
        System.out.println("前缀'or': " + trie.startsWith("or"));
        
        // 测试获取以给定前缀开头的单词
        System.out.println("以'app'开头的单词: " + trie.getWordsWithPrefix("app"));
        
        // 测试获取所有单词
        System.out.println("所有单词: " + trie.getAllWords());
        
        // 测试删除
        System.out.println("删除'app': " + trie.delete("app"));
        System.out.println("查找'app': " + trie.search("app"));
        System.out.println("查找'apple': " + trie.search("apple"));
        
        // 测试单词数量
        System.out.println("单词数量: " + trie.countWords());
        
        // 测试Trie树是否为空
        System.out.println("Trie树是否为空: " + trie.isEmpty());
    }

    /**
     * Trie树的应用示例
     */
    public void applications() {
        System.out.println("\n=== Trie树应用示例 ===");
        
        // 示例1: 自动补全
        System.out.println("示例1: 自动补全");
        TrieTree autocompleteTrie = new TrieTree();
        String[] words = {"apple", "app", "application", "apparel", "banana", "bat", "cat"};
        for (String word : words) {
            autocompleteTrie.insert(word);
        }
        
        String prefix = "app";
        System.out.println("输入前缀'" + prefix + "'，可能的补全选项: " + autocompleteTrie.getWordsWithPrefix(prefix));
        
        // 示例2: 拼写检查
        System.out.println("\n示例2: 拼写检查");
        TrieTree dictionary = new TrieTree();
        for (String word : words) {
            dictionary.insert(word);
        }
        
        String[] testWords = {"apple", "app", "appl", "banan", "bat"};
        System.out.println("拼写检查结果:");
        for (String word : testWords) {
            System.out.println(word + ": " + (dictionary.search(word) ? "正确" : "错误"));
        }
        
        // 示例3: IP路由
        System.out.println("\n示例3: IP路由");
        System.out.println("Trie树可以用于IP路由表的存储和查找，其中每个节点代表IP地址的一位。");
        System.out.println("查找时，从根节点开始，根据IP地址的每一位选择子节点，直到找到匹配的路由。");
    }

    /**
     * Trie树的性能测试
     */
    public void performanceTest() {
        System.out.println("\n=== Trie树性能测试 ===");
        
        TrieTree trie = new TrieTree();
        int size = 10000;
        
        // 生成随机单词
        String[] randomWords = new String[size];
        for (int i = 0; i < size; i++) {
            randomWords[i] = generateRandomWord(5);
        }
        
        // 测试插入性能
        long startTime = System.currentTimeMillis();
        for (String word : randomWords) {
            trie.insert(word);
        }
        long insertTime = System.currentTimeMillis() - startTime;
        System.out.println("插入" + size + "个随机单词耗时: " + insertTime + "ms");
        
        // 测试查找性能
        startTime = System.currentTimeMillis();
        for (String word : randomWords) {
            trie.search(word);
        }
        long searchTime = System.currentTimeMillis() - startTime;
        System.out.println("查找" + size + "个单词耗时: " + searchTime + "ms");
        
        // 测试前缀查询性能
        startTime = System.currentTimeMillis();
        for (String word : randomWords) {
            if (word.length() > 2) {
                trie.startsWith(word.substring(0, 2));
            }
        }
        long prefixTime = System.currentTimeMillis() - startTime;
        System.out.println("前缀查询" + size + "次耗时: " + prefixTime + "ms");
        
        System.out.println("\n结论: Trie树在字符串查找和前缀查询方面表现优异，");
        System.out.println("特别是当有大量具有相同前缀的字符串时，效率更高。");
    }

    /**
     * 生成指定长度的随机单词
     */
    private String generateRandomWord(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = (char) ('a' + Math.random() * 26);
            sb.append(c);
        }
        return sb.toString();
    }
}
