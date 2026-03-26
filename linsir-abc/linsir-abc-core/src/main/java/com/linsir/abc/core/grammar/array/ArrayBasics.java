package com.linsir.abc.core.grammar.array;

import java.util.Arrays;

/**
 * 数组基础示例
 *
 * 本类演示 Java 数组的基本概念和声明方式
 * 对应 JDK: java.util.Arrays
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ArrayBasics {

    /**
     * 演示数组的声明和初始化
     */
    public void demonstrateDeclaration() {
        System.out.println("=== 数组声明和初始化 ===");

        // 方式1：声明后分配空间
        int[] arr1;
        arr1 = new int[5];  // 默认初始化为0
        System.out.println("方式1 - 声明后分配空间:");
        printArray("arr1", arr1);

        // 方式2：声明同时分配空间
        int[] arr2 = new int[5];
        System.out.println("\n方式2 - 声明同时分配空间:");
        printArray("arr2", arr2);

        // 方式3：声明时直接初始化
        int[] arr3 = {1, 2, 3, 4, 5};
        System.out.println("\n方式3 - 直接初始化:");
        printArray("arr3", arr3);

        // 方式4：匿名数组
        int[] arr4 = new int[]{10, 20, 30};
        System.out.println("\n方式4 - 匿名数组:");
        printArray("arr4", arr4);

        // 不同数据类型的数组
        System.out.println("\n不同数据类型的数组:");
        double[] doubles = {1.1, 2.2, 3.3};
        String[] strings = {"Hello", "World"};
        boolean[] booleans = {true, false, true};

        System.out.println("  double[]: " + Arrays.toString(doubles));
        System.out.println("  String[]: " + Arrays.toString(strings));
        System.out.println("  boolean[]: " + Arrays.toString(booleans));
    }

    /**
     * 演示多维数组
     */
    public void demonstrateMultiDimensional() {
        System.out.println("\n=== 多维数组 ===");

        // 二维数组声明
        int[][] matrix = new int[3][4];
        System.out.println("二维数组 [3][4]:");
        printMatrix(matrix);

        // 不规则数组
        int[][] jagged = new int[3][];
        jagged[0] = new int[]{1};
        jagged[1] = new int[]{2, 3};
        jagged[2] = new int[]{4, 5, 6};

        System.out.println("\n不规则数组:");
        for (int i = 0; i < jagged.length; i++) {
            System.out.println("  第" + i + "行: " + Arrays.toString(jagged[i]));
        }

        // 直接初始化二维数组
        int[][] direct = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.println("\n直接初始化的二维数组:");
        printMatrix(direct);

        // 三维数组
        int[][][] cube = new int[2][2][2];
        System.out.println("\n三维数组维度信息:");
        System.out.println("  第一维长度: " + cube.length);
        System.out.println("  第二维长度: " + cube[0].length);
        System.out.println("  第三维长度: " + cube[0][0].length);
    }

    /**
     * 演示数组的基本操作
     */
    public void demonstrateBasicOperations() {
        System.out.println("\n=== 数组基本操作 ===");

        int[] arr = {5, 2, 8, 1, 9, 3};
        System.out.println("原始数组: " + Arrays.toString(arr));

        // 获取数组长度
        System.out.println("\n数组长度: " + arr.length);

        // 访问元素
        System.out.println("第一个元素: arr[0] = " + arr[0]);
        System.out.println("最后一个元素: arr[" + (arr.length-1) + "] = " + arr[arr.length-1]);

        // 修改元素
        arr[0] = 100;
        System.out.println("\n修改 arr[0] = 100 后: " + Arrays.toString(arr));

        // 遍历数组
        System.out.println("\n遍历数组:");
        System.out.print("  for循环: ");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }

        System.out.print("\n  增强for: ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();

        // 查找元素
        int target = 8;
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                index = i;
                break;
            }
        }
        System.out.println("\n查找 " + target + ": " + (index >= 0 ? "找到，索引=" + index : "未找到"));
    }

    /**
     * 演示数组的内存特性
     */
    public void demonstrateMemory() {
        System.out.println("\n=== 数组内存特性 ===");

        // 数组是引用类型
        int[] arr1 = {1, 2, 3};
        int[] arr2 = arr1;  // 引用赋值

        System.out.println("arr1: " + Arrays.toString(arr1));
        System.out.println("arr2: " + Arrays.toString(arr2));
        System.out.println("arr1 == arr2 ? " + (arr1 == arr2));

        // 修改 arr2 会影响 arr1
        arr2[0] = 100;
        System.out.println("\n修改 arr2[0] = 100 后:");
        System.out.println("arr1: " + Arrays.toString(arr1));
        System.out.println("arr2: " + Arrays.toString(arr2));

        // 创建副本
        int[] arr3 = arr1.clone();
        int[] arr4 = Arrays.copyOf(arr1, arr1.length);

        System.out.println("\n创建副本后:");
        System.out.println("arr1 == arr3 ? " + (arr1 == arr3));
        System.out.println("arr1 == arr4 ? " + (arr1 == arr4));
        System.out.println("Arrays.equals(arr1, arr3) ? " + Arrays.equals(arr1, arr3));
    }

    /**
     * 打印一维数组
     */
    private void printArray(String name, int[] arr) {
        System.out.println("  " + name + " = " + Arrays.toString(arr));
    }

    /**
     * 打印二维数组
     */
    private void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.println("  第" + i + "行: " + Arrays.toString(matrix[i]));
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 数组基础演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        ArrayBasics demo = new ArrayBasics();
        demo.demonstrateDeclaration();
        demo.demonstrateMultiDimensional();
        demo.demonstrateBasicOperations();
        demo.demonstrateMemory();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
