package com.linsir.abc.pdai.structure.sorting;

/**
 * 归并排序示例代码
 * 
 * 说明：
 * 1. 归并排序是一种分治算法，它将数组分成两半，分别对两半进行排序，然后将排序好的两半合并
 * 2. 归并排序的时间复杂度：O(n log n) 平均、最坏和最好情况
 * 3. 归并排序的空间复杂度：O(n)
 * 4. 归并排序是稳定的排序算法
 */
public class MergeSort {

    /**
     * 归并排序实现
     */
    public void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int[] temp = new int[arr.length];
        mergeSort(arr, 0, arr.length - 1, temp);
    }

    /**
     * 归并排序的递归实现
     */
    private void mergeSort(int[] arr, int left, int right, int[] temp) {
        if (left < right) {
            // 计算中间位置
            int mid = left + (right - left) / 2;
            
            // 递归排序左半部分
            mergeSort(arr, left, mid, temp);
            // 递归排序右半部分
            mergeSort(arr, mid + 1, right, temp);
            
            // 合并两个排序好的部分
            merge(arr, left, mid, right, temp);
        }
    }

    /**
     * 合并两个排序好的子数组
     */
    private void merge(int[] arr, int left, int mid, int right, int[] temp) {
        // 复制元素到临时数组
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }
        
        int i = left;      // 左子数组的起始索引
        int j = mid + 1;   // 右子数组的起始索引
        int k = left;      // 合并后数组的起始索引
        
        // 合并两个子数组
        while (i <= mid && j <= right) {
            if (temp[i] <= temp[j]) {
                arr[k] = temp[i];
                i++;
            } else {
                arr[k] = temp[j];
                j++;
            }
            k++;
        }
        
        // 复制左子数组中剩余的元素
        while (i <= mid) {
            arr[k] = temp[i];
            i++;
            k++;
        }
        
        // 复制右子数组中剩余的元素
        while (j <= right) {
            arr[k] = temp[j];
            j++;
            k++;
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
     * 测试归并排序
     */
    public void test() {
        System.out.println("=== 归并排序示例 ===");
        
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
