package com.linsir.abc.pdai.structure.sorting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 桶排序示例代码
 * 
 * 说明：
 * 1. 桶排序是一种非比较排序算法，它将元素分到不同的桶中，对每个桶进行排序，然后合并
 * 2. 桶排序适用于均匀分布的数据
 * 3. 桶排序的时间复杂度：O(n + k) 平均情况，O(n²) 最坏情况，O(n) 最好情况
 * 4. 桶排序的空间复杂度：O(n + k)
 * 5. 桶排序是稳定的排序算法
 */
public class BucketSort {

    /**
     * 桶排序实现
     */
    public void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        // 找出数组中的最大值和最小值
        int max = arr[0];
        int min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        
        // 计算桶的数量
        int bucketCount = (max - min) / arr.length + 1;
        List<List<Integer>> buckets = new ArrayList<>(bucketCount);
        
        // 初始化桶
        for (int i = 0; i < bucketCount; i++) {
            buckets.add(new ArrayList<>());
        }
        
        // 将元素分配到桶中
        for (int i = 0; i < arr.length; i++) {
            int bucketIndex = (arr[i] - min) / arr.length;
            buckets.get(bucketIndex).add(arr[i]);
        }
        
        // 对每个桶进行排序
        int index = 0;
        for (List<Integer> bucket : buckets) {
            Collections.sort(bucket);
            // 将排序后的元素复制回原数组
            for (int num : bucket) {
                arr[index++] = num;
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
     * 测试桶排序
     */
    public void test() {
        System.out.println("=== 桶排序示例 ===");
        
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
