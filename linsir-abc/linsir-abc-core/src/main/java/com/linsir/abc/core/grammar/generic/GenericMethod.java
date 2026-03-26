package com.linsir.abc.core.grammar.generic;

import java.util.List;

/**
 * 泛型方法示例
 *
 * 本类演示 Java 泛型方法的定义和使用
 * 对应 JDK: 泛型方法
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class GenericMethod {

    /**
     * 泛型方法：打印数组
     *
     * @param <T> 泛型类型
     * @param array 要打印的数组
     */
    public <T> void printArray(T[] array) {
        System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i < array.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    /**
     * 泛型方法：交换数组元素
     *
     * @param <T> 泛型类型
     * @param array 数组
     * @param i 索引1
     * @param j 索引2
     */
    public <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * 泛型方法：获取最大值（使用 Comparable）
     *
     * @param <T> 泛型类型，必须实现 Comparable
     * @param a 第一个值
     * @param b 第二个值
     * @return 最大值
     */
    public <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }

    /**
     * 泛型方法：打印列表
     *
     * @param <T> 泛型类型
     * @param list 列表
     */
    public <T> void printList(List<T> list) {
        System.out.println("列表内容:");
        for (T item : list) {
            System.out.println("  " + item);
        }
    }

    /**
     * 演示基本泛型方法
     */
    public void demonstrateGenericMethod() {
        System.out.println("=== 泛型方法基本用法 ===");

        // 打印 Integer 数组
        Integer[] intArray = {1, 2, 3, 4, 5};
        System.out.print("Integer 数组: ");
        printArray(intArray);

        // 打印 String 数组
        String[] strArray = {"Hello", "World", "Java"};
        System.out.print("String 数组: ");
        printArray(strArray);

        // 打印 Double 数组
        Double[] doubleArray = {1.1, 2.2, 3.3};
        System.out.print("Double 数组: ");
        printArray(doubleArray);
    }

    /**
     * 演示泛型方法交换元素
     */
    public void demonstrateSwap() {
        System.out.println("\n=== 泛型方法交换元素 ===");

        String[] fruits = {"Apple", "Banana", "Cherry"};
        System.out.println("交换前: ");
        printArray(fruits);

        swap(fruits, 0, 2);

        System.out.println("交换后 (0 和 2): ");
        printArray(fruits);
    }

    /**
     * 演示带边界的泛型方法
     */
    public void demonstrateBoundedMethod() {
        System.out.println("\n=== 带边界的泛型方法 ===");

        // 比较整数
        Integer maxInt = max(10, 20);
        System.out.println("max(10, 20) = " + maxInt);

        // 比较字符串
        String maxStr = max("Apple", "Banana");
        System.out.println("max(\"Apple\", \"Banana\") = " + maxStr);

        // 比较双精度数
        Double maxDouble = max(3.14, 2.71);
        System.out.println("max(3.14, 2.71) = " + maxDouble);
    }

    /**
     * 演示静态泛型方法
     */
    public void demonstrateStaticGenericMethod() {
        System.out.println("\n=== 静态泛型方法 ===");

        // 使用静态泛型方法
        Integer[] intArr = {3, 1, 4, 1, 5};
        Integer maxInt = GenericMethod.findMax(intArr);
        System.out.println("数组最大值: " + maxInt);

        String[] strArr = {"apple", "banana", "cherry"};
        String maxStr = GenericMethod.findMax(strArr);
        System.out.println("字符串数组最大值: " + maxStr);
    }

    /**
     * 静态泛型方法：查找数组最大值
     *
     * @param <T> 泛型类型
     * @param array 数组
     * @return 最大值
     */
    public static <T extends Comparable<T>> T findMax(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        T max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i].compareTo(max) > 0) {
                max = array[i];
            }
        }
        return max;
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 泛型方法演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        GenericMethod demo = new GenericMethod();
        demo.demonstrateGenericMethod();
        demo.demonstrateSwap();
        demo.demonstrateBoundedMethod();
        demo.demonstrateStaticGenericMethod();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
