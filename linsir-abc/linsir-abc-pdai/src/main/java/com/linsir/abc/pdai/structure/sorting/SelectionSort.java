package com.linsir.abc.pdai.structure.sorting;

/**
 * 选择排序示例代码
 * 
 * 说明：
 * 1. 选择排序是一种简单的排序算法，它的工作原理是每一次从待排序的数据元素中选出最小（或最大）的一个元素
 * 2. 存放在序列的起始位置，直到全部待排序的数据元素排完
 * 3. 选择排序的时间复杂度：O(n²) 平均、最坏和最好情况
 * 4. 选择排序的空间复杂度：O(1)
 * 5. 选择排序是不稳定的排序算法
 */
public class SelectionSort {

    /**
     * 选择排序实现
     */
    public void sort(int[] arr) {
        int n = arr.length;
        
        // 外层循环控制排序轮数
        for (int i = 0; i < n - 1; i++) {
            // 假设当前位置是最小值的位置
            int minIndex = i;
            
            // 内层循环寻找最小值
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            
            // 如果找到的最小值不是当前位置，交换元素
            if (minIndex != i) {
                int temp = arr[i];
                arr[i] = arr[minIndex];
                arr[minIndex] = temp;
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
     * 测试选择排序
     */
    public void test() {
        System.out.println("=== 选择排序示例 ===");
        
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
