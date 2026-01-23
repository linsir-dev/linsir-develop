package com.linsir.abc.pdai.structure.sorting;

/**
 * 快速排序示例代码
 * 
 * 说明：
 * 1. 快速排序是一种分治算法，它选择一个基准元素，将数组分为两部分
 * 2. 左部分的元素都小于基准，右部分的元素都大于基准，然后递归地排序这两部分
 * 3. 快速排序的时间复杂度：O(n log n) 平均情况，O(n²) 最坏情况，O(n log n) 最好情况
 * 4. 快速排序的空间复杂度：O(log n)
 * 5. 快速排序是不稳定的排序算法
 */
public class QuickSort {

    /**
     * 快速排序实现
     */
    public void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSort(arr, 0, arr.length - 1);
    }

    /**
     * 快速排序的递归实现
     */
    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // 分区操作，返回基准元素的位置
            int pivotIndex = partition(arr, low, high);
            
            // 递归排序左半部分
            quickSort(arr, low, pivotIndex - 1);
            // 递归排序右半部分
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    /**
     * 分区操作
     */
    private int partition(int[] arr, int low, int high) {
        // 选择最后一个元素作为基准
        int pivot = arr[high];
        // i指向小于基准的元素的边界
        int i = low - 1;
        
        // 遍历数组，将小于基准的元素移到左边
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                // 交换元素
                swap(arr, i, j);
            }
        }
        
        // 将基准元素放到正确的位置
        swap(arr, i + 1, high);
        return i + 1;
    }

    /**
     * 交换数组中的两个元素
     */
    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * 打印数组
     */
    public void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    /**
     * 测试快速排序
     */
    public void test() {
        System.out.println("=== 快速排序示例 ===");
        
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        System.out.println("排序前:");
        printArray(arr);
        
        sort(arr);
        
        System.out.println("排序后:");
        printArray(arr);
        
        // 测试已排序的数组
        int[] sortedArr = {1, 2, 3, 4, 5};
        System.out.println("\n已排序的数组:");
        printArray(sortedArr);
        
        sort(sortedArr);
        
        System.out.println("排序后:");
        printArray(sortedArr);
        
        // 测试逆序数组
        int[] reverseArr = {5, 4, 3, 2, 1};
        System.out.println("\n逆序数组:");
        printArray(reverseArr);
        
        sort(reverseArr);
        
        System.out.println("排序后:");
        printArray(reverseArr);
    }
}
