package com.linsir.abc.pdai.structure.sorting;

/**
 * 排序算法测试类
 * 
 * 说明：
 * 1. 测试所有排序算法的正确性
 * 2. 包括冒泡排序、选择排序、插入排序、归并排序、快速排序、堆排序、希尔排序、计数排序、桶排序、基数排序
 */
public class SortingTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("排序算法测试");
        System.out.println("========================================");
        
        // 测试冒泡排序
        BubbleSort bubbleSort = new BubbleSort();
        bubbleSort.test();
        System.out.println();
        
        // 测试选择排序
        SelectionSort selectionSort = new SelectionSort();
        selectionSort.test();
        System.out.println();
        
        // 测试插入排序
        InsertionSort insertionSort = new InsertionSort();
        insertionSort.test();
        System.out.println();
        
        // 测试归并排序
        MergeSort mergeSort = new MergeSort();
        mergeSort.test();
        System.out.println();
        
        // 测试快速排序
        QuickSort quickSort = new QuickSort();
        quickSort.test();
        System.out.println();
        
        // 测试堆排序
        HeapSort heapSort = new HeapSort();
        heapSort.test();
        System.out.println();
        
        // 测试希尔排序
        ShellSort shellSort = new ShellSort();
        shellSort.test();
        System.out.println();
        
        // 测试计数排序
        CountingSort countingSort = new CountingSort();
        countingSort.test();
        System.out.println();
        
        // 测试桶排序
        BucketSort bucketSort = new BucketSort();
        bucketSort.test();
        System.out.println();
        
        // 测试基数排序
        RadixSort radixSort = new RadixSort();
        radixSort.test();
        
        System.out.println("========================================");
        System.out.println("所有排序算法测试完成");
        System.out.println("========================================");
    }
}
