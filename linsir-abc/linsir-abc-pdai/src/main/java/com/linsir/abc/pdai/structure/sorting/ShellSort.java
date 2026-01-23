package com.linsir.abc.pdai.structure.sorting;

/**
 * 希尔排序示例代码
 * 
 * 说明：
 * 1. 希尔排序是插入排序的改进版本，它通过设定一个增量序列，将数组分成若干子序列
 * 2. 对每个子序列进行插入排序，然后逐步减小增量，直到增量为1时进行最后一次插入排序
 * 3. 希尔排序的时间复杂度：O(n^1.3) 平均情况，O(n²) 最坏情况，O(n) 最好情况
 * 4. 希尔排序的空间复杂度：O(1)
 * 5. 希尔排序是不稳定的排序算法
 */
public class ShellSort {

    /**
     * 希尔排序实现
     */
    public void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int n = arr.length;
        
        // 设定增量序列，这里使用希尔建议的序列：n/2, n/4, ..., 1
        for (int gap = n / 2; gap > 0; gap /= 2) {
            // 对每个子序列进行插入排序
            for (int i = gap; i < n; i++) {
                // 保存当前要插入的元素
                int key = arr[i];
                // j指向已排序部分的最后一个元素
                int j = i - gap;
                
                // 在内层循环中找到合适的插入位置
                while (j >= 0 && arr[j] > key) {
                    // 将大于key的元素向右移动
                    arr[j + gap] = arr[j];
                    j -= gap;
                }
                
                // 插入key到合适的位置
                arr[j + gap] = key;
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
     * 测试希尔排序
     */
    public void test() {
        System.out.println("=== 希尔排序示例 ===");
        
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
