package com.linsir.abc.pdai.structure.dataStructure;

/**
 * Trie树（前缀树）示例代码
 * 
 * 说明：
 * 1. Trie树（也称为前缀树或字典树）是一种用于高效存储和检索字符串数据集中键的数据结构
 * 2. Trie树的特点：
 *    - 根节点不包含字符
 *    - 每个节点的子节点代表不同的字符
 *    - 从根节点到某一节点的路径代表一个字符串
 *    - 每个节点存储一个标志，表示该节点是否是一个字符串的结束
 * 3. Trie树的时间复杂度：
 *    - 插入、查找、删除的时间复杂度均为O(L)，其中L是字符串的长度
 * 4. 应用场景：
 *    - 自动补全
 *    - 拼写检查
 *    - 前缀匹配
 *    - IP路由
 *    - 字典查询
 */
public class TrieTreeDemo {

    /**
     * Trie树节点类
     */
    private static class TrieNode {
        private TrieNode[] children;
        private boolean isEndOfWord;
        private int count; // 记录以该节点结尾的单词数量

        TrieNode() {
            this.children = new TrieNode[26]; // 假设只处理小写字母
            this.isEndOfWord = false;
            this.count = 0;
        }
    }

    /**
     * Trie树类
     */
    private static class TrieTree {
        private TrieNode root;

        TrieTree() {
            this.root = new TrieNode();
        }

        /**
         * 插入单词到Trie树
         */
        public void insert(String word) {
            TrieNode current = root;
            
            for (char c : word.toCharArray()) {
                int index = c - 'a';
                if (current.children[index] == null) {
                    current.children[index] = new TrieNode();
                }
                current = current.children[index];
            }
            
            current.isEndOfWord = true;
            current.count++;
        }

        /**
         * 查找单词是否在Trie树中
         */
        public boolean search(String word) {
            TrieNode current = root;
            
            for (char c : word.toCharArray()) {
                int index = c - 'a';
                if (current.children[index] == null) {
                    return false;
                }
                current = current.children[index];
            }
            
            return current.isEndOfWord;
        }

        /**
         * 查找是否有以给定前缀开头的单词
         */
        public boolean startsWith(String prefix) {
            TrieNode current = root;
            
            for (char c : prefix.toCharArray()) {
                int index = c - 'a';
                if (current.children[index] == null) {
                    return false;
                }
                current = current.children[index];
            }
            
            return true;
        }

        /**
         * 删除单词从Trie树
         */
        public boolean delete(String word) {
            return delete(root, word, 0);
        }

        /**
         * 递归删除单词
         */
        private boolean delete(TrieNode current, String word, int index) {
            if (index == word.length()) {
                if (!current.isEndOfWord) {
                    return false;
                }
                current.isEndOfWord = false;
                current.count--;
                return isEmpty(current);
            }
            
            char c = word.charAt(index);
            int charIndex = c - 'a';
            TrieNode child = current.children[charIndex];
            
            if (child == null) {
                return false;
            }
            
            boolean shouldDeleteChild = delete(child, word, index + 1);
            
            if (shouldDeleteChild) {
                current.children[charIndex] = null;
                return isEmpty(current) && !current.isEndOfWord;
            }
            
            return false;
        }

        /**
         * 检查节点是否为空（没有子节点）
         */
        private boolean isEmpty(TrieNode node) {
            for (TrieNode child : node.children) {
                if (child != null) {
                    return false;
                }
            }
            return true;
        }

        /**
         * 获取以给定前缀开头的所有单词
         */
        public java.util.List<String> getWordsWithPrefix(String prefix) {
            java.util.List<String> words = new java.util.ArrayList<>();
            TrieNode current = root;
            
            // 导航到前缀的最后一个字符
            for (char c : prefix.toCharArray()) {
                int index = c - 'a';
                if (current.children[index] == null) {
                    return words; // 前缀不存在
                }
                current = current.children[index];
            }
            
            // 收集所有以该前缀开头的单词
            collectWords(current, prefix, words);
            return words;
        }

        /**
         * 收集单词的辅助方法
         */
        private void collectWords(TrieNode node, String prefix, java.util.List<String> words) {
            if (node.isEndOfWord) {
                // 添加单词及其出现次数
                words.add(prefix + " (" + node.count + ")");
            }
            
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    char c = (char) ('a' + i);
                    collectWords(node.children[i], prefix + c, words);
                }
            }
        }

        /**
         * 计算Trie树中的单词数量
         */
        public int countWords() {
            return countWords(root);
        }

        /**
         * 递归计算单词数量
         */
        private int countWords(TrieNode node) {
            int count = 0;
            if (node.isEndOfWord) {
                count += node.count;
            }
            
            for (TrieNode child : node.children) {
                if (child != null) {
                    count += countWords(child);
                }
            }
            
            return count;
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
            if (node.isEndOfWord) {
                System.out.println(prefix + " (" + node.count + ")");
            }
            
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    char c = (char) ('a' + i);
                    print(node.children[i], prefix + c);
                }
            }
        }
    }

    /**
     * 测试Trie树
     */
    public void testTrieTree() {
        System.out.println("=== Trie树（前缀树）示例 ===");
        
        TrieTree trie = new TrieTree();
        
        // 插入单词
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        trie.insert("banana");
        trie.insert("band");
        trie.insert("cat");
        trie.insert("car");
        trie.insert("cartoon");
        trie.insert("app"); // 重复插入
        
        // 打印Trie树
        trie.print();
        
        // 查找单词
        System.out.println("查找'app': " + trie.search("app"));
        System.out.println("查找'apple': " + trie.search("apple"));
        System.out.println("查找'banana': " + trie.search("banana"));
        System.out.println("查找'band': " + trie.search("band"));
        System.out.println("查找'dog': " + trie.search("dog"));
        
        // 前缀查询
        System.out.println("以'app'开头的单词:");
        java.util.List<String> appWords = trie.getWordsWithPrefix("app");
        for (String word : appWords) {
            System.out.println("  " + word);
        }
        
        System.out.println("以'c'开头的单词:");
        java.util.List<String> cWords = trie.getWordsWithPrefix("c");
        for (String word : cWords) {
            System.out.println("  " + word);
        }
        
        // 检查前缀
        System.out.println("是否有以'app'开头的单词: " + trie.startsWith("app"));
        System.out.println("是否有以'd'开头的单词: " + trie.startsWith("d"));
        
        // 计算单词数量
        System.out.println("Trie树中的单词数量: " + trie.countWords());
        
        // 删除单词
        System.out.println("\n删除'app'后:");
        trie.delete("app");
        trie.print();
        System.out.println("查找'app': " + trie.search("app"));
        System.out.println("是否有以'app'开头的单词: " + trie.startsWith("app"));
        
        System.out.println("删除'banana'后:");
        trie.delete("banana");
        trie.print();
        System.out.println("查找'banana': " + trie.search("banana"));
        System.out.println("查找'band': " + trie.search("band"));
    }

    /**
     * Trie树应用示例
     */
    public void trieTreeApplications() {
        System.out.println("\n=== Trie树应用示例 ===");
        
        // 示例1：自动补全
        System.out.println("示例1：自动补全");
        TrieTree autocompleteTrie = new TrieTree();
        
        // 插入一些单词
        String[] words = {
            "apple", "app", "application", "appearance", "apparel",
            "banana", "band", "bank", "bar", "base",
            "cat", "car", "cartoon", "card", "care"
        };
        
        for (String word : words) {
            autocompleteTrie.insert(word);
        }
        
        // 测试自动补全
        String[] prefixes = {"app", "ba", "ca"};
        for (String prefix : prefixes) {
            System.out.println("\n输入前缀'" + prefix + "'时的自动补全建议:");
            java.util.List<String> suggestions = autocompleteTrie.getWordsWithPrefix(prefix);
            if (suggestions.isEmpty()) {
                System.out.println("  无建议");
            } else {
                for (String suggestion : suggestions) {
                    System.out.println("  " + suggestion);
                }
            }
        }
        
        // 示例2：拼写检查
        System.out.println("\n示例2：拼写检查");
        TrieTree spellCheckTrie = new TrieTree();
        
        // 插入一些正确的单词
        String[] dictionary = {
            "hello", "world", "java", "trie", "tree", "data", "structure", "algorithm"
        };
        
        for (String word : dictionary) {
            spellCheckTrie.insert(word);
        }
        
        // 测试拼写检查
        String[] testWords = {"hello", "world", "jav", "triee", "data", "algorithmm"};
        for (String word : testWords) {
            if (spellCheckTrie.search(word)) {
                System.out.println("单词'" + word + "'拼写正确");
            } else {
                System.out.println("单词'" + word + "'拼写错误");
                // 尝试提供建议
                java.util.List<String> suggestions = new java.util.ArrayList<>();
                // 简单的建议生成：尝试删除、替换或添加一个字符
                generateSuggestions(word, dictionary, suggestions);
                if (!suggestions.isEmpty()) {
                    System.out.println("  建议: " + suggestions);
                }
            }
        }
        
        // 示例3：IP路由
        System.out.println("\n示例3：IP路由（简化版）");
        // 注意：实际的IP路由使用更复杂的结构，但基本原理类似
        // 这里我们使用字符串形式的IP地址来演示
        TrieTree ipTrie = new TrieTree();
        
        // 插入一些IP前缀
        String[] ipPrefixes = {
            "192.168.1",
            "192.168.2",
            "10.0.0",
            "172.16.0"
        };
        
        for (String prefix : ipPrefixes) {
            // 将IP地址转换为适合Trie树的格式（移除点）
            String normalizedPrefix = prefix.replace(".", "");
            ipTrie.insert(normalizedPrefix);
        }
        
        // 测试IP路由
        String[] testIps = {
            "192.168.1.100",
            "192.168.2.200",
            "10.0.0.50",
            "172.16.0.75",
            "192.168.3.150"
        };
        
        for (String ip : testIps) {
            String normalizedIp = ip.replace(".", "");
            boolean found = false;
            
            // 尝试匹配最长前缀
            for (int i = 1; i <= normalizedIp.length(); i++) {
                String prefix = normalizedIp.substring(0, i);
                if (ipTrie.search(prefix)) {
                    System.out.println("IP地址'" + ip + "'匹配前缀'" + 
                        prefix.replaceAll("(\\d{3})(\\d{3})(\\d{1,3})", "$1.$2.$3") + "'");
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                System.out.println("IP地址'" + ip + "'无匹配前缀");
            }
        }
    }

    /**
     * 生成拼写建议的辅助方法
     */
    private void generateSuggestions(String misspelledWord, String[] dictionary, java.util.List<String> suggestions) {
        // 简单的建议生成：检查字典中是否有编辑距离为1的单词
        for (String word : dictionary) {
            if (editDistance(misspelledWord, word) == 1) {
                suggestions.add(word);
            }
        }
    }

    /**
     * 计算编辑距离（Levenshtein距离）的辅助方法
     */
    private int editDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[s1.length()][s2.length()];
    }

    /**
     * Trie树性能分析
     */
    public void trieTreePerformance() {
        System.out.println("\n=== Trie树性能分析 ===");
        
        TrieTree trie = new TrieTree();
        
        // 生成随机单词
        java.util.Random random = new java.util.Random();
        java.util.List<String> words = new java.util.ArrayList<>();
        int wordCount = 10000;
        int maxWordLength = 10;
        
        for (int i = 0; i < wordCount; i++) {
            StringBuilder sb = new StringBuilder();
            int length = random.nextInt(maxWordLength) + 1;
            for (int j = 0; j < length; j++) {
                char c = (char) ('a' + random.nextInt(26));
                sb.append(c);
            }
            words.add(sb.toString());
        }
        
        // 测试插入性能
        long startTime = System.nanoTime();
        for (String word : words) {
            trie.insert(word);
        }
        long insertTime = System.nanoTime() - startTime;
        
        // 测试查找性能
        startTime = System.nanoTime();
        for (String word : words) {
            trie.search(word);
        }
        long searchTime = System.nanoTime() - startTime;
        
        // 测试前缀查询性能
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            StringBuilder sb = new StringBuilder();
            int length = random.nextInt(5) + 1;
            for (int j = 0; j < length; j++) {
                char c = (char) ('a' + random.nextInt(26));
                sb.append(c);
            }
            trie.getWordsWithPrefix(sb.toString());
        }
        long prefixTime = System.nanoTime() - startTime;
        
        System.out.println("插入" + wordCount + "个单词的时间: " + insertTime + " ns");
        System.out.println("查找" + wordCount + "个单词的时间: " + searchTime + " ns");
        System.out.println("1000次前缀查询的时间: " + prefixTime + " ns");
        System.out.println("Trie树中的单词数量: " + trie.countWords());
        
        // 分析结果
        System.out.println("\n性能分析:");
        System.out.println("1. Trie树的插入和查找操作时间复杂度为O(L)，其中L是单词的长度");
        System.out.println("2. 对于前缀查询，Trie树比其他数据结构（如哈希表）更高效");
        System.out.println("3. Trie树的空间复杂度较高，因为需要为每个字符创建节点");
        System.out.println("4. 对于处理大量字符串的场景，Trie树是一种非常有效的数据结构");
    }
}
