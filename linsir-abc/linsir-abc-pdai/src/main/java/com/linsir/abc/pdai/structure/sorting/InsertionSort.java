package com.linsir.abc.pdai.structure.sorting;

/**
 * 插入排序示例代码
 * 
 * 说明：
 * 1. 插入排序是一种简单的排序算法，它的工作原理是通过构建有序序列
 * 2. 对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入
 * 3. 插入排序的时间复杂度：O(n²) 平均和最坏情况，O(n) 最好情况
 * 4. 插入排序的空间复杂度：O(1)
 * 5. 插入排序是稳定的排序算法
 */
public class InsertionSort {

    /**
     * 插入排序实现
     */
    public void sort(int[] arr) {
        int n = arr.length;
        
        // 外层循环控制未排序部分
        for (int i = 1; i < n; i++) {
            // 保存当前要插入的元素
            int key = arr[i];
            // j指向已排序部分的最后一个元素
            int j = i - 1;
            
            // 在内层循环中找到合适的插入位置
            while (j >= 0 && arr[j] > key) {
                // 将大于key的元素向右移动
                arr[j + 1] = arr[j];
                j--;
            }
            
            // 插入key到合适的位置
            arr[j + 1] = key;
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
     * 测试插入排序
     */
    public void test() {
        System.out.println("=== 插入排序示例 ===");
        
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
