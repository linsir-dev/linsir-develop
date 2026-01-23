package com.linsir.abc.pdai.data-structure;

/**
 * 数据结构测试类
 * 
 * 测试所有数据结构的示例代码
 */
public class DataStructureTest {
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("数据结构示例代码测试");
        System.out.println("=============================================");
        
        // 测试数组
        System.out.println("\n=============================================");
        System.out.println("测试数组");
        System.out.println("=============================================");
        ArrayDemo arrayDemo = new ArrayDemo();
        arrayDemo.createAndInitArray();
        arrayDemo.arrayOperations();
        arrayDemo.arrayAlgorithms();
        arrayDemo.dynamicArrayDemo();
        
        // 测试链表
        System.out.println("\n=============================================");
        System.out.println("测试链表");
        System.out.println("=============================================");
        LinkedListDemo linkedListDemo = new LinkedListDemo();
        linkedListDemo.testLinkedList();
        linkedListDemo.testDoublyLinkedList();
        linkedListDemo.linkedListAlgorithms();
        
        // 测试哈希表
        System.out.println("\n=============================================");
        System.out.println("测试哈希表");
        System.out.println("=============================================");
        HashMapDemo hashMapDemo = new HashMapDemo();
        hashMapDemo.testCustomHashMap();
        hashMapDemo.testJavaHashMap();
        hashMapDemo.hashMapApplications();
        hashMapDemo.hashMapPerformanceTest();
        
        // 测试二叉树
        System.out.println("\n=============================================");
        System.out.println("测试二叉树");
        System.out.println("=============================================");
        BinaryTreeDemo binaryTreeDemo = new BinaryTreeDemo();
        binaryTreeDemo.testBinaryTree();
        binaryTreeDemo.binarySearchTreeApplications();
        binaryTreeDemo.testBalancedTree();
        
        // 测试VL树（Van Emde Boas树）
        System.out.println("\n=============================================");
        System.out.println("测试VL树（Van Emde Boas树）");
        System.out.println("=============================================");
        VLTreeDemo vlTreeDemo = new VLTreeDemo();
        vlTreeDemo.testVanEmdeBoasTree();
        vlTreeDemo.performanceComparison();
        vlTreeDemo.applications();
        
        // 测试红黑树
        System.out.println("\n=============================================");
        System.out.println("测试红黑树");
        System.out.println("=============================================");
        RedBlackTreeDemo redBlackTreeDemo = new RedBlackTreeDemo();
        redBlackTreeDemo.testRedBlackTree();
        redBlackTreeDemo.performanceTest();
        redBlackTreeDemo.applications();
        
        // 测试B树
        System.out.println("\n=============================================");
        System.out.println("测试B树");
        System.out.println("=============================================");
        BTreeDemo bTreeDemo = new BTreeDemo();
        bTreeDemo.testBTree();
        bTreeDemo.applications();
        bTreeDemo.compareWithOtherDataStructures();
        
        // 测试B+树
        System.out.println("\n=============================================");
        System.out.println("测试B+树");
        System.out.println("=============================================");
        BPlusTreeDemo bPlusTreeDemo = new BPlusTreeDemo();
        bPlusTreeDemo.testBPlusTree();
        bPlusTreeDemo.applications();
        bPlusTreeDemo.compareWithBTree();
        
        // 测试B*树
        System.out.println("\n=============================================");
        System.out.println("测试B*树");
        System.out.println("=============================================");
        BStarTreeDemo bStarTreeDemo = new BStarTreeDemo();
        bStarTreeDemo.testBStarTree();
        bStarTreeDemo.applications();
        bStarTreeDemo.compareWithBPlusTree();
        
        // 测试R树
        System.out.println("\n=============================================");
        System.out.println("测试R树");
        System.out.println("=============================================");
        RTreeDemo rTreeDemo = new RTreeDemo();
        rTreeDemo.testRTree();
        rTreeDemo.applications();
        rTreeDemo.compareWithOtherSpatialIndexes();
        
        // 测试Trie树
        System.out.println("\n=============================================");
        System.out.println("测试Trie树");
        System.out.println("=============================================");
        TrieTreeDemo trieTreeDemo = new TrieTreeDemo();
        trieTreeDemo.testTrieTree();
        trieTreeDemo.applications();
        trieTreeDemo.performanceTest();
        
        System.out.println("\n=============================================");
        System.out.println("测试完成");
        System.out.println("=============================================");
        
        // 说明：
        // 1. 本测试类测试了所有数据结构的示例代码
        // 2. 每个数据结构都有详细的示例和测试
        // 3. 可以根据需要单独运行某个数据结构的测试
        // 4. 每个数据结构的示例代码都包含了基本操作和常见算法
        // 5. 每个数据结构都有应用场景的说明
    }
}
