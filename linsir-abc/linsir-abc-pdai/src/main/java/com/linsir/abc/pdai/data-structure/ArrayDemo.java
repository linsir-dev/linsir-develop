package com.linsir.abc.pdai.data-structure;

/**
 * 数组示例代码
 * 
 * 说明：
 * 1. 数组是一种线性数据结构，用于存储相同类型的元素
 * 2. 数组的特点：
 *    - 元素在内存中连续存储
 *    - 可以通过索引快速访问元素（时间复杂度O(1)）
 *    - 插入和删除元素效率较低（时间复杂度O(n)）
 *    - 长度固定，一旦创建不能修改
 */
public class ArrayDemo {

    /**
     * 创建和初始化数组
     */
    public void createAndInitArray() {
        System.out.println("=== 创建和初始化数组 ===");
        
        // 1. 声明并初始化数组
        int[] arr1 = new int[5]; // 创建长度为5的int数组，默认值为0
        
        // 2. 声明并初始化数组，同时赋值
        int[] arr2 = new int[]{1, 2, 3, 4, 5};
        
        // 3. 简化语法初始化数组
        int[] arr3 = {6, 7, 8, 9, 10};
        
        // 4. 创建二维数组
        int[][] arr4 = new int[2][3];
        int[][] arr5 = {{1, 2, 3}, {4, 5, 6}};
        
        // 打印数组元素
        System.out.println("arr1元素:");
        for (int i = 0; i < arr1.length; i++) {
            System.out.print(arr1[i] + " ");
        }
        System.out.println();
        
        System.out.println("arr2元素:");
        for (int i = 0; i < arr2.length; i++) {
            System.out.print(arr2[i] + " ");
        }
        System.out.println();
        
        System.out.println("arr3元素:");
        for (int i = 0; i < arr3.length; i++) {
            System.out.print(arr3[i] + " ");
        }
        System.out.println();
        
        System.out.println("arr5元素:");
        for (int i = 0; i < arr5.length; i++) {
            for (int j = 0; j < arr5[i].length; j++) {
                System.out.print(arr5[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * 数组的基本操作
     */
    public void arrayOperations() {
        System.out.println("\n=== 数组的基本操作 ===");
        
        int[] arr = {1, 2, 3, 4, 5};
        
        // 1. 访问元素
        System.out.println("访问索引2的元素: " + arr[2]);
        
        // 2. 修改元素
        arr[2] = 10;
        System.out.println("修改索引2的元素后: " + arr[2]);
        
        // 3. 遍历数组
        System.out.println("遍历数组:");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
        
        // 4. 增强for循环遍历
        System.out.println("增强for循环遍历:");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
        
        // 5. 数组长度
        System.out.println("数组长度: " + arr.length);
    }

    /**
     * 数组的常见算法
     */
    public void arrayAlgorithms() {
        System.out.println("\n=== 数组的常见算法 ===");
        
        int[] arr = {5, 2, 8, 1, 9, 3};
        
        // 1. 数组排序
        System.out.println("排序前:");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
        
        // 冒泡排序
        bubbleSort(arr);
        
        System.out.println("冒泡排序后:");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
        
        // 2. 数组查找
        int target = 8;
        int index = linearSearch(arr, target);
        System.out.println("线性查找元素 " + target + " 的索引: " + index);
        
        // 3. 数组最大值
        int max = findMax(arr);
        System.out.println("数组最大值: " + max);
        
        // 4. 数组最小值
        int min = findMin(arr);
        System.out.println("数组最小值: " + min);
        
        // 5. 数组求和
        int sum = calculateSum(arr);
        System.out.println("数组求和: " + sum);
    }

    /**
     * 冒泡排序
     */
    private void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // 交换元素
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    /**
     * 线性查找
     */
    private int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1; // 未找到
    }

    /**
     * 查找最大值
     */
    private int findMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    /**
     * 查找最小值
     */
    private int findMin(int[] arr) {
        int min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return min;
    }

    /**
     * 计算数组和
     */
    private int calculateSum(int[] arr) {
        int sum = 0;
        for (int num : arr) {
            sum += num;
        }
        return sum;
    }

    /**
     * 动态数组实现
     */
    public void dynamicArrayDemo() {
        System.out.println("\n=== 动态数组实现 ===");
        
        // 使用Java内置的ArrayList作为动态数组示例
        java.util.ArrayList<Integer> dynamicArray = new java.util.ArrayList<>();
        
        // 添加元素
        dynamicArray.add(1);
        dynamicArray.add(2);
        dynamicArray.add(3);
        System.out.println("添加元素后: " + dynamicArray);
        
        // 插入元素
        dynamicArray.add(1, 4);
        System.out.println("在索引1处插入元素4后: " + dynamicArray);
        
        // 删除元素
        dynamicArray.remove(2);
        System.out.println("删除索引2处的元素后: " + dynamicArray);
        
        // 访问元素
        System.out.println("访问索引1处的元素: " + dynamicArray.get(1));
        
        // 修改元素
        dynamicArray.set(0, 5);
        System.out.println("修改索引0处的元素为5后: " + dynamicArray);
        
        // 遍历元素
        System.out.println("遍历元素:");
        for (int num : dynamicArray) {
            System.out.print(num + " ");
        }
        System.out.println();
        
        // 数组长度
        System.out.println("数组长度: " + dynamicArray.size());
    }
}
