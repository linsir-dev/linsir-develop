package com.linsir.abc.pdai.Base.generic;

/**
 * 泛型方法示例
 */
public class GenericMethod {

    /**
     * 泛型方法
     * @param <T> 类型参数
     * @param array 数组
     * @return 数组中的最大值
     */
    public static <T extends Comparable<T>> T getMax(T[] array) {
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
     * 泛型方法示例2
     * @param <T> 类型参数
     * @param t 泛型参数
     */
    public static <T> void printValue(T t) {
        System.out.println("值: " + t);
        System.out.println("类型: " + t.getClass().getName());
    }
}
