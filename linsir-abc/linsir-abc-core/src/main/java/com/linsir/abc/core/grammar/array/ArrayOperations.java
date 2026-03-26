package com.linsir.abc.core.grammar.array;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 数组操作示例
 *
 * 本类演示 Java 数组的常用操作
 * 对应 JDK: java.util.Arrays
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ArrayOperations {

    /**
     * 演示数组排序
     */
    public void demonstrateSorting() {
        System.out.println("=== 数组排序 ===");

        // 基本类型排序
        int[] numbers = {5, 2, 8, 1, 9, 3};
        System.out.println("原始数组: " + Arrays.toString(numbers));

        Arrays.sort(numbers);
        System.out.println("升序排序: " + Arrays.toString(numbers));

        // 部分排序
        int[] arr2 = {5, 2, 8, 1, 9, 3};
        Arrays.sort(arr2, 1, 4);  // 排序索引1到3
        System.out.println("部分排序 [1,4): " + Arrays.toString(arr2));

        // 对象数组排序
        String[] fruits = {"banana", "apple", "cherry", "date"};
        System.out.println("\n字符串数组: " + Arrays.toString(fruits));
        Arrays.sort(fruits);
        System.out.println("自然排序: " + Arrays.toString(fruits));

        // 自定义排序（降序）
        Integer[] nums = {5, 2, 8, 1, 9};
        Arrays.sort(nums, Comparator.reverseOrder());
        System.out.println("\n降序排序: " + Arrays.toString(nums));

        // 并行排序（大数据量时更快）
        int[] large = {9, 3, 7, 1, 5, 2, 8, 4, 6};
        Arrays.parallelSort(large);
        System.out.println("\n并行排序: " + Arrays.toString(large));
    }

    /**
     * 演示数组查找
     */
    public void demonstrateSearching() {
        System.out.println("\n=== 数组查找 ===");

        int[] arr = {1, 3, 5, 7, 9, 11, 13};
        System.out.println("有序数组: " + Arrays.toString(arr));

        // 二分查找（数组必须已排序）
        int index = Arrays.binarySearch(arr, 7);
        System.out.println("查找 7: 索引=" + index);

        index = Arrays.binarySearch(arr, 6);
        System.out.println("查找 6（不存在）: 索引=" + index + " (插入点=" + (-index-1) + ")");

        // 在范围内查找
        index = Arrays.binarySearch(arr, 1, 4, 5);
        System.out.println("在[1,4)范围内查找 5: 索引=" + index);
    }

    /**
     * 演示数组填充和复制
     */
    public void demonstrateFillAndCopy() {
        System.out.println("\n=== 数组填充和复制 ===");

        // 填充
        int[] arr = new int[5];
        Arrays.fill(arr, 100);
        System.out.println("填充 100: " + Arrays.toString(arr));

        Arrays.fill(arr, 1, 4, 50);
        System.out.println("部分填充 [1,4) 为 50: " + Arrays.toString(arr));

        // 复制
        int[] original = {1, 2, 3, 4, 5};
        int[] copy1 = Arrays.copyOf(original, original.length);
        System.out.println("\n原始数组: " + Arrays.toString(original));
        System.out.println("完整复制: " + Arrays.toString(copy1));

        int[] copy2 = Arrays.copyOf(original, 3);
        System.out.println("复制前3个: " + Arrays.toString(copy2));

        int[] copy3 = Arrays.copyOf(original, 7);
        System.out.println("扩展到7个: " + Arrays.toString(copy3));

        // 范围复制
        int[] copy4 = Arrays.copyOfRange(original, 1, 4);
        System.out.println("复制范围[1,4): " + Arrays.toString(copy4));
    }

    /**
     * 演示数组比较
     */
    public void demonstrateComparison() {
        System.out.println("\n=== 数组比较 ===");

        int[] arr1 = {1, 2, 3};
        int[] arr2 = {1, 2, 3};
        int[] arr3 = {1, 2, 4};

        System.out.println("arr1: " + Arrays.toString(arr1));
        System.out.println("arr2: " + Arrays.toString(arr2));
        System.out.println("arr3: " + Arrays.toString(arr3));

        // 引用比较
        System.out.println("\n引用比较:");
        System.out.println("arr1 == arr2 ? " + (arr1 == arr2));

        // 内容比较
        System.out.println("\n内容比较:");
        System.out.println("Arrays.equals(arr1, arr2) ? " + Arrays.equals(arr1, arr2));
        System.out.println("Arrays.equals(arr1, arr3) ? " + Arrays.equals(arr1, arr3));

        // 多维数组比较
        int[][] m1 = {{1, 2}, {3, 4}};
        int[][] m2 = {{1, 2}, {3, 4}};
        System.out.println("\n多维数组比较:");
        System.out.println("Arrays.equals(m1, m2) ? " + Arrays.equals(m1, m2));
        System.out.println("Arrays.deepEquals(m1, m2) ? " + Arrays.deepEquals(m1, m2));
    }

    /**
     * 演示数组转换
     */
    public void demonstrateConversion() {
        System.out.println("\n=== 数组转换 ===");

        // 数组转字符串
        int[] arr = {1, 2, 3, 4, 5};
        System.out.println("数组转字符串: " + Arrays.toString(arr));

        // 多维数组转字符串
        int[][] matrix = {{1, 2}, {3, 4}};
        System.out.println("多维数组: " + Arrays.deepToString(matrix));

        // 数组转列表（注意：返回的是固定大小的列表）
        String[] strArr = {"a", "b", "c"};
        java.util.List<String> list = Arrays.asList(strArr);
        System.out.println("\n数组转List: " + list);

        // 基本类型数组流操作
        int[] numbers = {1, 2, 3, 4, 5};
        int sum = Arrays.stream(numbers).sum();
        double avg = Arrays.stream(numbers).average().orElse(0);
        System.out.println("\n流操作 - 和: " + sum + ", 平均值: " + avg);
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 数组操作演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        ArrayOperations demo = new ArrayOperations();
        demo.demonstrateSorting();
        demo.demonstrateSearching();
        demo.demonstrateFillAndCopy();
        demo.demonstrateComparison();
        demo.demonstrateConversion();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
