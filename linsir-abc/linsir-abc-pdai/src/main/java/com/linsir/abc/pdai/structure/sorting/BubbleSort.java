package com.linsir.abc.pdai.structure.sorting;

/**
 * 冒泡排序示例代码
 * 
 * 说明：
 * 1. 冒泡排序是一种简单的排序算法，它重复地走访过要排序的数列，一次比较两个元素
 * 2. 如果它们的顺序错误就把它们交换过来，直到没有再需要交换的元素为止
 * 3. 冒泡排序的时间复杂度：O(n²) 平均和最坏情况，O(n) 最好情况
 * 4. 冒泡排序的空间复杂度：O(1)
 * 5. 冒泡排序是稳定的排序算法
 */
public class BubbleSort {

    /**
     * 冒泡排序实现
     */
    public void sort(int[] arr) {
        int n = arr.length;
        boolean swapped;
        
        // 外层循环控制排序轮数
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            
            // 内层循环比较相邻元素
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // 交换元素
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            
            // 如果本轮没有发生交换，说明数组已经有序，提前退出
            if (!swapped) {
                break;
            }
        }
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
     * 测试冒泡排序
     */
    public void test() {
        System.out.println("=== 冒泡排序示例 ===");
        
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
