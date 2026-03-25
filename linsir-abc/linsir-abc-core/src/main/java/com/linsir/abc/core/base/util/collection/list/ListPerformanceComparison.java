package com.linsir.abc.core.base.util.collection.list;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * List性能比较器
 * 
 * 本类比较ArrayList和LinkedList在不同操作下的性能差异：
 * 1. 随机访问性能
 * 2. 尾部添加性能
 * 3. 头部添加性能
 * 4. 中间插入性能
 * 5. 删除性能
 * 
 * 性能特点总结：
 * - ArrayList：随机访问O(1)，插入删除O(n)，内存连续
 * - LinkedList：随机访问O(n)，插入删除O(1)，内存分散
 * 
 * 选择建议：
 * - 频繁随机访问：选择ArrayList
 * - 频繁插入删除：选择LinkedList
 * - 一般场景：优先选择ArrayList（缓存友好）
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ListPerformanceComparison {
    
    // 测试数据规模
    private static final int SMALL_SIZE = 10_000;
    private static final int MEDIUM_SIZE = 100_000;
    private static final int LARGE_SIZE = 1_000_000;
    
    // 随机数生成器
    private final Random random = new Random();
    
    /**
     * 性能测试结果类
     */
    public static class BenchmarkResult {
        public final String operation;
        public final String listType;
        public final int size;
        public final long timeNanos;
        public final double timeMillis;
        
        public BenchmarkResult(String operation, String listType, int size, long timeNanos) {
            this.operation = operation;
            this.listType = listType;
            this.size = size;
            this.timeNanos = timeNanos;
            this.timeMillis = timeNanos / 1_000_000.0;
        }
        
        @Override
        public String toString() {
            return String.format("%-15s | %-12s | 数量: %,7d | 耗时: %,10.3f ms",
                operation, listType, size, timeMillis);
        }
    }
    
    /**
     * 运行所有性能测试
     */
    public void runAllBenchmarks() {
        System.out.println("========== List性能比较测试 ==========\n");
        
        // 随机访问测试
        benchmarkRandomAccess();
        
        // 添加操作测试
        benchmarkAddOperations();
        
        // 删除操作测试
        benchmarkRemoveOperations();
        
        // 遍历测试
        benchmarkIteration();
        
        // 内存占用测试
        benchmarkMemoryUsage();
        
        // 综合建议
        printRecommendations();
    }
    
    /**
     * 测试随机访问性能
     */
    public void benchmarkRandomAccess() {
        System.out.println("----- 随机访问性能测试 -----");
        
        int[] sizes = {SMALL_SIZE, MEDIUM_SIZE};
        
        for (int size : sizes) {
            // 准备数据
            List<Integer> arrayList = createArrayList(size);
            List<Integer> linkedList = createLinkedList(size);
            
            // 测试ArrayList随机访问
            long arrayListTime = measureRandomAccess(arrayList, size / 10);
            System.out.println(new BenchmarkResult("随机访问", "ArrayList", size, arrayListTime));
            
            // 测试LinkedList随机访问
            long linkedListTime = measureRandomAccess(linkedList, size / 10);
            System.out.println(new BenchmarkResult("随机访问", "LinkedList", size, linkedListTime));
            
            // 计算性能比
            double ratio = (double) linkedListTime / arrayListTime;
            System.out.printf("性能比: LinkedList/ArrayList = %.2fx\n\n", ratio);
        }
    }
    
    /**
     * 测量随机访问时间
     * 
     * @param list 要测试的列表
     * @param accessCount 访问次数
     * @return 耗时（纳秒）
     */
    private long measureRandomAccess(List<Integer> list, int accessCount) {
        int size = list.size();
        long start = System.nanoTime();
        
        for (int i = 0; i < accessCount; i++) {
            int index = random.nextInt(size);
            list.get(index);  // 随机访问
        }
        
        return System.nanoTime() - start;
    }
    
    /**
     * 测试添加操作性能
     */
    public void benchmarkAddOperations() {
        System.out.println("----- 添加操作性能测试 -----");
        
        int[] sizes = {SMALL_SIZE, MEDIUM_SIZE};
        
        for (int size : sizes) {
            // 尾部添加测试
            long arrayListTime = measureAddAtEnd(size);
            System.out.println(new BenchmarkResult("尾部添加", "ArrayList", size, arrayListTime));
            
            long linkedListTime = measureLinkedListAddAtEnd(size);
            System.out.println(new BenchmarkResult("尾部添加", "LinkedList", size, linkedListTime));
            
            // 头部添加测试
            arrayListTime = measureAddAtBeginning(new ArrayList<>(), size / 100);
            System.out.println(new BenchmarkResult("头部添加", "ArrayList", size / 100, arrayListTime));
            
            linkedListTime = measureAddAtBeginning(new LinkedList<>(), size / 100);
            System.out.println(new BenchmarkResult("头部添加", "LinkedList", size / 100, linkedListTime));
            
            System.out.println();
        }
    }
    
    /**
     * 测量ArrayList尾部添加时间
     * 
     * @param count 添加数量
     * @return 耗时（纳秒）
     */
    private long measureAddAtEnd(int count) {
        long start = System.nanoTime();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(i);
        }
        return System.nanoTime() - start;
    }
    
    /**
     * 测量LinkedList尾部添加时间
     * 
     * @param count 添加数量
     * @return 耗时（纳秒）
     */
    private long measureLinkedListAddAtEnd(int count) {
        long start = System.nanoTime();
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            list.add(i);
        }
        return System.nanoTime() - start;
    }
    
    /**
     * 测量头部添加时间
     * 
     * @param list 要测试的列表
     * @param count 添加数量
     * @return 耗时（纳秒）
     */
    private long measureAddAtBeginning(List<Integer> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.add(0, i);  // 在头部添加
        }
        return System.nanoTime() - start;
    }
    
    /**
     * 测试删除操作性能
     */
    public void benchmarkRemoveOperations() {
        System.out.println("----- 删除操作性能测试 -----");
        
        int size = SMALL_SIZE;
        
        // 尾部删除
        List<Integer> arrayList = createArrayList(size);
        List<Integer> linkedList = createLinkedList(size);
        
        long arrayListTime = measureRemoveFromEnd(arrayList, size / 10);
        System.out.println(new BenchmarkResult("尾部删除", "ArrayList", size / 10, arrayListTime));
        
        long linkedListTime = measureRemoveFromEnd(linkedList, size / 10);
        System.out.println(new BenchmarkResult("尾部删除", "LinkedList", size / 10, linkedListTime));
        
        // 头部删除
        arrayList = createArrayList(size);
        linkedList = createLinkedList(size);
        
        arrayListTime = measureRemoveFromBeginning(arrayList, size / 100);
        System.out.println(new BenchmarkResult("头部删除", "ArrayList", size / 100, arrayListTime));
        
        linkedListTime = measureRemoveFromBeginning(linkedList, size / 100);
        System.out.println(new BenchmarkResult("头部删除", "LinkedList", size / 100, linkedListTime));
        
        System.out.println();
    }
    
    /**
     * 测量尾部删除时间
     * 
     * @param list 要测试的列表
     * @param count 删除数量
     * @return 耗时（纳秒）
     */
    private long measureRemoveFromEnd(List<Integer> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.remove(list.size() - 1);
        }
        return System.nanoTime() - start;
    }
    
    /**
     * 测量头部删除时间
     * 
     * @param list 要测试的列表
     * @param count 删除数量
     * @return 耗时（纳秒）
     */
    private long measureRemoveFromBeginning(List<Integer> list, int count) {
        long start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            list.remove(0);
        }
        return System.nanoTime() - start;
    }
    
    /**
     * 测试遍历性能
     */
    public void benchmarkIteration() {
        System.out.println("----- 遍历性能测试 -----");
        
        int size = MEDIUM_SIZE;
        List<Integer> arrayList = createArrayList(size);
        List<Integer> linkedList = createLinkedList(size);
        
        // for循环遍历（索引访问）
        long arrayListTime = measureIndexedIteration(arrayList);
        System.out.println(new BenchmarkResult("索引遍历", "ArrayList", size, arrayListTime));
        
        long linkedListTime = measureIndexedIteration(linkedList);
        System.out.println(new BenchmarkResult("索引遍历", "LinkedList", size, linkedListTime));
        
        // 增强for循环遍历（迭代器访问）
        arrayListTime = measureIteratorIteration(arrayList);
        System.out.println(new BenchmarkResult("迭代器遍历", "ArrayList", size, arrayListTime));
        
        linkedListTime = measureIteratorIteration(linkedList);
        System.out.println(new BenchmarkResult("迭代器遍历", "LinkedList", size, linkedListTime));
        
        System.out.println();
    }
    
    /**
     * 测量索引遍历时间
     * 
     * @param list 要测试的列表
     * @return 耗时（纳秒）
     */
    private long measureIndexedIteration(List<Integer> list) {
        long sum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return System.nanoTime() - start;
    }
    
    /**
     * 测量迭代器遍历时间
     * 
     * @param list 要测试的列表
     * @return 耗时（纳秒）
     */
    private long measureIteratorIteration(List<Integer> list) {
        long sum = 0;
        long start = System.nanoTime();
        for (Integer value : list) {
            sum += value;
        }
        return System.nanoTime() - start;
    }
    
    /**
     * 测试内存占用
     */
    public void benchmarkMemoryUsage() {
        System.out.println("----- 内存占用测试 -----");
        
        int size = LARGE_SIZE;
        
        // 强制垃圾回收
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // 创建ArrayList
        List<Integer> arrayList = createArrayList(size);
        long arrayListMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memoryBefore;
        
        // 释放ArrayList
        arrayList = null;
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        memoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // 创建LinkedList
        List<Integer> linkedList = createLinkedList(size);
        long linkedListMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - memoryBefore;
        
        System.out.printf("ArrayList内存占用:  ~%,d bytes (~%.2f MB)\n", 
            arrayListMemory, arrayListMemory / (1024.0 * 1024));
        System.out.printf("LinkedList内存占用: ~%,d bytes (~%.2f MB)\n", 
            linkedListMemory, linkedListMemory / (1024.0 * 1024));
        System.out.printf("内存比: LinkedList/ArrayList = %.2fx\n\n", 
            (double) linkedListMemory / arrayListMemory);
    }
    
    /**
     * 创建指定大小的ArrayList
     * 
     * @param size 列表大小
     * @return 创建的ArrayList
     */
    private List<Integer> createArrayList(int size) {
        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }
    
    /**
     * 创建指定大小的LinkedList
     * 
     * @param size 列表大小
     * @return 创建的LinkedList
     */
    private List<Integer> createLinkedList(int size) {
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        return list;
    }
    
    /**
     * 打印选择建议
     */
    private void printRecommendations() {
        System.out.println("========== 选择建议 ==========\n");
        
        System.out.println("选择ArrayList的场景：");
        System.out.println("  1. 频繁随机访问元素（get操作）");
        System.out.println("  2. 主要在尾部添加/删除元素");
        System.out.println("  3. 需要高效的内存使用");
        System.out.println("  4. 列表大小相对稳定（避免频繁扩容）");
        System.out.println();
        
        System.out.println("选择LinkedList的场景：");
        System.out.println("  1. 频繁在头部/中间插入删除元素");
        System.out.println("  2. 实现队列（Queue）或双端队列（Deque）");
        System.out.println("  3. 不需要频繁随机访问");
        System.out.println("  4. 列表大小变化频繁");
        System.out.println();
        
        System.out.println("一般建议：");
        System.out.println("  - 默认选择ArrayList（缓存友好，内存占用小）");
        System.out.println("  - 只有在明确需要频繁插入删除时才选择LinkedList");
        System.out.println("  - 使用迭代器遍历LinkedList，避免使用索引遍历");
        System.out.println("  - 预估ArrayList大小，减少扩容开销");
    }
    
    /**
     * 演示RandomAccess接口的作用
     */
    public void demonstrateRandomAccess() {
        System.out.println("\n========== RandomAccess接口演示 ==========\n");
        
        System.out.println("RandomAccess是一个标记接口，用于标识支持快速随机访问的List实现。");
        System.out.println();
        
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();
        
        System.out.println("ArrayList实现RandomAccess: " + 
            (arrayList instanceof java.util.RandomAccess));
        System.out.println("LinkedList实现RandomAccess: " + 
            (linkedList instanceof java.util.RandomAccess));
        System.out.println();
        
        System.out.println("应用示例：Collections.binarySearch()根据RandomAccess选择算法");
        System.out.println("  - 实现RandomAccess：使用索引二分查找（O(log n)访问）");
        System.out.println("  - 未实现RandomAccess：使用迭代器二分查找（O(n)遍历）");
    }
}
