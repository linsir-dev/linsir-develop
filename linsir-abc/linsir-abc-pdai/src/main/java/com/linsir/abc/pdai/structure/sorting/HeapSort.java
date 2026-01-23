package com.linsir.abc.pdai.structure.sorting;

/**
 * 堆排序示例代码
 * 
 * 说明：
 * 1. 堆排序是一种基于比较的排序算法，它利用堆这种数据结构来进行排序
 * 2. 堆是一种特殊的完全二叉树，分为最大堆和最小堆
 * 3. 堆排序的时间复杂度：O(n log n) 平均、最坏和最好情况
 * 4. 堆排序的空间复杂度：O(1)
 * 5. 堆排序是不稳定的排序算法
 */
public class HeapSort {

    /**
     * 堆排序实现
     */
    public void sort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        int n = arr.length;
        
        // 构建最大堆
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }
        
        // 逐个提取元素
        for (int i = n - 1; i > 0; i--) {
            // 将当前根节点（最大值）移到数组末尾
            swap(arr, 0, i);
            
            // 对剩余的堆进行调整
            heapify(arr, i, 0);
        }
    }

    /**
     * 堆化操作
     */
    private void heapify(int[] arr, int n, int i) {
        // 初始化最大值为当前节点
        int largest = i;
        // 左子节点
        int left = 2 * i + 1;
        // 右子节点
        int right = 2 * i + 2;
        
        // 如果左子节点大于当前最大值
        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }
        
        // 如果右子节点大于当前最大值
        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }
        
        // 如果最大值不是当前节点，交换并继续堆化
        if (largest != i) {
            swap(arr, i, largest);
            heapify(arr, n, largest);
        }
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
     * 测试堆排序
     */
    public void test() {
        System.out.println("=== 堆排序示例 ===");
        
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
