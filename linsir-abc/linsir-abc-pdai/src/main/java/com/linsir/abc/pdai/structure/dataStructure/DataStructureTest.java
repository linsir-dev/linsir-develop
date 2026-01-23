package com.linsir.abc.pdai.structure.dataStructure;

/**
 * 数据结构测试类
 * 
 * 说明：
 * 1. 本类用于测试所有数据结构的示例代码
 * 2. 包括数组、链表、哈希表、二叉树、VL树、红黑树、B树、B+树、B*树、R树、Trie树
 * 3. 每个数据结构都有详细的测试方法，展示其基本操作和应用场景
 */
public class DataStructureTest {

    /**
     * 主方法，用于运行所有数据结构的测试
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("           数据结构测试示例              ");
        System.out.println("========================================");
        
        // 测试数组
        testArray();
        
        // 测试链表
        testLinkedList();
        
        // 测试哈希表
        testHashMap();
        
        // 测试二叉树
        testBinaryTree();
        
        // 测试VL树
        testVLTree();
        
        // 测试红黑树
        testRedBlackTree();
        
        // 测试B树
        testBTree();
        
        // 测试B+树
        testBPlusTree();
        
        // 测试B*树
        testBStarTree();
        
        // 测试R树
        testRTree();
        
        // 测试Trie树
        testTrieTree();
        
        System.out.println("========================================");
        System.out.println("           测试完成                      ");
        System.out.println("========================================");
    }

    /**
     * 测试数组
     */
    private static void testArray() {
        System.out.println("\n1. 测试数组 (Array)");
        System.out.println("----------------------------------------");
        
        ArrayDemo arrayDemo = new ArrayDemo();
        
        // 测试创建和初始化数组
        arrayDemo.createAndInitArray();
        
        // 测试数组的基本操作
        arrayDemo.arrayOperations();
        
        // 测试数组的常见算法
        arrayDemo.arrayAlgorithms();
        
        // 测试动态数组
        arrayDemo.dynamicArrayDemo();
    }

    /**
     * 测试链表
     */
    private static void testLinkedList() {
        System.out.println("\n2. 测试链表 (Linked List)");
        System.out.println("----------------------------------------");
        
        LinkedListDemo linkedListDemo = new LinkedListDemo();
        
        // 测试单链表
        linkedListDemo.testLinkedList();
        
        // 测试双链表
        linkedListDemo.testDoublyLinkedList();
        
        // 测试链表的常见算法
        linkedListDemo.linkedListAlgorithms();
    }

    /**
     * 测试哈希表
     */
    private static void testHashMap() {
        System.out.println("\n3. 测试哈希表 (Hash Map)");
        System.out.println("----------------------------------------");
        
        HashMapDemo hashMapDemo = new HashMapDemo();
        
        // 测试自定义哈希表
        hashMapDemo.testCustomHashMap();
        
        // 测试Java内置HashMap
        hashMapDemo.testJavaHashMap();
        
        // 测试哈希表应用
        hashMapDemo.hashMapApplications();
    }

    /**
     * 测试二叉树
     */
    private static void testBinaryTree() {
        System.out.println("\n4. 测试二叉树 (Binary Tree)");
        System.out.println("----------------------------------------");
        
        BinaryTreeDemo binaryTreeDemo = new BinaryTreeDemo();
        
        // 测试二叉树
        binaryTreeDemo.testBinaryTree();
        
        // 测试二叉树应用
        binaryTreeDemo.binaryTreeApplications();
    }

    /**
     * 测试VL树
     */
    private static void testVLTree() {
        System.out.println("\n5. 测试VL树 (Van Emde Boas Tree)");
        System.out.println("----------------------------------------");
        
        VLTreeDemo vlTreeDemo = new VLTreeDemo();
        
        // 测试VL树
        vlTreeDemo.testVLTree();
        
        // 测试VL树应用
        vlTreeDemo.vebTreeApplications();
        
        // 测试VL树性能
        vlTreeDemo.performanceComparison();
    }

    /**
     * 测试红黑树
     */
    private static void testRedBlackTree() {
        System.out.println("\n6. 测试红黑树 (Red-Black Tree)");
        System.out.println("----------------------------------------");
        
        RedBlackTreeDemo redBlackTreeDemo = new RedBlackTreeDemo();
        
        // 测试红黑树
        redBlackTreeDemo.testRedBlackTree();
        
        // 测试红黑树应用
        redBlackTreeDemo.redBlackTreeApplications();
    }

    /**
     * 测试B树
     */
    private static void testBTree() {
        System.out.println("\n7. 测试B树 (B Tree)");
        System.out.println("----------------------------------------");
        
        BTreeDemo bTreeDemo = new BTreeDemo();
        
        // 测试B树
        bTreeDemo.testBTree();
        
        // 测试B树应用
        bTreeDemo.bTreeApplications();
        
        // 测试B树性能
        bTreeDemo.bTreePerformance();
    }

    /**
     * 测试B+树
     */
    private static void testBPlusTree() {
        System.out.println("\n8. 测试B+树 (B+ Tree)");
        System.out.println("----------------------------------------");
        
        BPlusTreeDemo bPlusTreeDemo = new BPlusTreeDemo();
        
        // 测试B+树
        bPlusTreeDemo.testBPlusTree();
        
        // 测试B+树应用
        bPlusTreeDemo.bPlusTreeApplications();
        
        // 测试B+树与B树的比较
        bPlusTreeDemo.compareWithBTree();
    }

    /**
     * 测试B*树
     */
    private static void testBStarTree() {
        System.out.println("\n9. 测试B*树 (B* Tree)");
        System.out.println("----------------------------------------");
        
        BStarTreeDemo bStarTreeDemo = new BStarTreeDemo();
        
        // 测试B*树
        bStarTreeDemo.testBStarTree();
        
        // 测试B*树应用
        bStarTreeDemo.bStarTreeApplications();
        
        // 测试B*树与B+树的比较
        bStarTreeDemo.compareWithBPlusTree();
    }

    /**
     * 测试R树
     */
    private static void testRTree() {
        System.out.println("\n10. 测试R树 (R Tree)");
        System.out.println("----------------------------------------");
        
        RTreeDemo rTreeDemo = new RTreeDemo();
        
        // 测试R树
        rTreeDemo.testRTree();
        
        // 测试R树应用
        rTreeDemo.rTreeApplications();
        
        // 测试R树性能
        rTreeDemo.rTreePerformance();
    }

    /**
     * 测试Trie树
     */
    private static void testTrieTree() {
        System.out.println("\n11. 测试Trie树 (Trie Tree)");
        System.out.println("----------------------------------------");
        
        TrieTreeDemo trieTreeDemo = new TrieTreeDemo();
        
        // 测试Trie树
        trieTreeDemo.testTrieTree();
        
        // 测试Trie树应用
        trieTreeDemo.trieTreeApplications();
        
        // 测试Trie树性能
        trieTreeDemo.trieTreePerformance();
    }
}
