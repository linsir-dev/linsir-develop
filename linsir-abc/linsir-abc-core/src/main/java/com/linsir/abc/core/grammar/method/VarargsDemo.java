package com.linsir.abc.core.grammar.method;

/**
 * 可变参数示例
 *
 * 本类演示 Java 可变参数的使用
 * 对应 JDK: 可变参数 varargs
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class VarargsDemo {

    /**
     * 使用可变参数计算总和
     *
     * @param numbers 可变数量的整数
     * @return 总和
     */
    public int sum(int... numbers) {
        int total = 0;
        for (int num : numbers) {
            total += num;
        }
        return total;
    }

    /**
     * 使用可变参数计算平均值
     *
     * @param numbers 可变数量的整数
     * @return 平均值
     */
    public double average(int... numbers) {
        if (numbers.length == 0) {
            return 0.0;
        }
        return (double) sum(numbers) / numbers.length;
    }

    /**
     * 查找最大值
     *
     * @param numbers 可变数量的整数
     * @return 最大值
     */
    public int max(int... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("至少需要一个参数");
        }
        int max = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }
        return max;
    }

    /**
     * 格式化输出（可变参数 + 普通参数）
     *
     * @param format 格式字符串
     * @param args 可变参数
     */
    public void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    /**
     * 可变参数与数组的关系
     *
     * @param arr 数组参数
     */
    public void printArray(int[] arr) {
        System.out.print("数组参数: [");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    /**
     * 可变参数版本
     *
     * @param numbers 可变参数
     */
    public void printVarargs(int... numbers) {
        System.out.print("可变参数: [");
        for (int i = 0; i < numbers.length; i++) {
            System.out.print(numbers[i]);
            if (i < numbers.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    /**
     * 演示基本用法
     */
    public void demonstrateBasicUsage() {
        System.out.println("=== 可变参数基本用法 ===");

        // 不传参数
        System.out.println("sum() = " + sum());

        // 传一个参数
        System.out.println("sum(5) = " + sum(5));

        // 传多个参数
        System.out.println("sum(1, 2, 3, 4, 5) = " + sum(1, 2, 3, 4, 5));

        // 传数组
        int[] arr = {10, 20, 30};
        System.out.println("sum(arr) = " + sum(arr));
    }

    /**
     * 演示实际应用
     */
    public void demonstratePracticalUsage() {
        System.out.println("\n=== 实际应用 ===");

        // 计算平均值
        System.out.println("average(10, 20, 30) = " + average(10, 20, 30));
        System.out.println("average(1, 2, 3, 4, 5) = " + average(1, 2, 3, 4, 5));

        // 查找最大值
        System.out.println("\nmax(3, 1, 4, 1, 5, 9, 2, 6) = " + max(3, 1, 4, 1, 5, 9, 2, 6));

        // 格式化输出
        System.out.println("\n格式化输出:");
        printf("姓名: %s, 年龄: %d, 成绩: %.2f%n", "张三", 20, 85.5);
        printf("当前时间: %s%n", java.time.LocalDateTime.now());
    }

    /**
     * 演示可变参数与数组的关系
     */
    public void demonstrateArrayRelationship() {
        System.out.println("\n=== 可变参数与数组的关系 ===");

        // 可变参数本质上就是数组
        int[] arr = {1, 2, 3};

        System.out.println("传递数组给可变参数方法:");
        System.out.println("  sum(arr) = " + sum(arr));

        System.out.println("\n两种声明方式的对比:");
        printArray(arr);
        printVarargs(arr);
        printVarargs(1, 2, 3);
    }

    /**
     * 演示注意事项
     */
    public void demonstrateNotes() {
        System.out.println("\n=== 注意事项 ===");

        System.out.println("1. 可变参数必须是最后一个参数");
        System.out.println("   void method(String str, int... nums) // 正确");
        System.out.println("   void method(int... nums, String str) // 错误!");

        System.out.println("\n2. 一个方法只能有一个可变参数");
        System.out.println("   void method(int... nums, String... strs) // 错误!");

        System.out.println("\n3. 重载时的优先级");
        // 如果有精确匹配的方法，会优先调用
        testOverloading(1, 2);  // 调用两个参数的版本
        testOverloading(1, 2, 3);  // 调用可变参数版本
    }

    /**
     * 两个参数的方法
     */
    public void testOverloading(int a, int b) {
        System.out.println("  调用: testOverloading(int, int)");
    }

    /**
     * 可变参数的方法
     */
    public void testOverloading(int... nums) {
        System.out.println("  调用: testOverloading(int...)");
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 可变参数演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        VarargsDemo demo = new VarargsDemo();
        demo.demonstrateBasicUsage();
        demo.demonstratePracticalUsage();
        demo.demonstrateArrayRelationship();
        demo.demonstrateNotes();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
