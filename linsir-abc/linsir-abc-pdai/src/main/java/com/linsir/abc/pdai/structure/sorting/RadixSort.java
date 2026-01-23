package com.linsir.abc.pdai.structure.sorting;

import java.util.ArrayList;
import java.util.List;

/**
 * 基数排序示例代码
 * 
 * 说明：
 * 1. 基数排序是一种非比较排序算法，它按位排序，从低位到高位
 * 2. 基数排序适用于整数或字符串排序
 * 3. 基数排序的时间复杂度：O(n * k)，其中k是位数
 * 4. 基数排序的空间复杂度：O(n + k)
 * 5. 基数排序是稳定的排序算法
 */
public class RadixSort {

    /**
     * 基数排序实现
     */
    public void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        // 找出数组中的最大值
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        
        // 计算最大值的位数
        int digitCount = 0;
        while (max > 0) {
            digitCount++;
            max /= 10;
        }
        
        // 对每一位进行计数排序
        for (int i = 0; i < digitCount; i++) {
            countingSortByDigit(arr, i);
        }
    }

    /**
     * 按指定位数进行计数排序
     */
    private void countingSortByDigit(int[] arr, int digit) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10]; // 0-9共10个数字
        
        // 统计当前位上每个数字出现的次数
        for (int i = 0; i < n; i++) {
            int digitValue = getDigit(arr[i], digit);
            count[digitValue]++;
        }
        
        // 计算累计次数
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }
        
        // 构建结果数组
        for (int i = n - 1; i >= 0; i--) {
            int digitValue = getDigit(arr[i], digit);
            output[count[digitValue] - 1] = arr[i];
            count[digitValue]--;
        }
        
        // 将结果复制回原数组
        for (int i = 0; i < n; i++) {
            arr[i] = output[i];
        }
    }

    /**
     * 获取数字的指定位数的值
     */
    private int getDigit(int num, int digit) {
        return (num / (int) Math.pow(10, digit)) % 10;
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
     * 测试基数排序
     */
    public void test() {
        System.out.println("=== 基数排序示例 ===");
        
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
