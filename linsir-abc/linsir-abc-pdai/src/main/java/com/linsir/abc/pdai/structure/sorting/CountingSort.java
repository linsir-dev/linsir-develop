package com.linsir.abc.pdai.structure.sorting;

/**
 * 计数排序示例代码
 * 
 * 说明：
 * 1. 计数排序是一种非比较排序算法，它统计元素出现的次数，然后重建数组
 * 2. 计数排序适用于元素范围较小的场景
 * 3. 计数排序的时间复杂度：O(n + k)，其中k是元素的范围
 * 4. 计数排序的空间复杂度：O(k)
 * 5. 计数排序是稳定的排序算法
 */
public class CountingSort {

    /**
     * 计数排序实现
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
        
        // 计算计数数组的大小
        int range = max - min + 1;
        int[] count = new int[range];
        
        // 统计每个元素出现的次数
        for (int i = 0; i < arr.length; i++) {
            count[arr[i] - min]++;
        }
        
        // 计算累计次数
        for (int i = 1; i < range; i++) {
            count[i] += count[i - 1];
        }
        
        // 构建结果数组
        int[] output = new int[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) {
            output[count[arr[i] - min] - 1] = arr[i];
            count[arr[i] - min]--;
        }
        
        // 将结果复制回原数组
        for (int i = 0; i < arr.length; i++) {
            arr[i] = output[i];
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
     * 测试计数排序
     */
    public void test() {
        System.out.println("=== 计数排序示例 ===");
        
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
