package com.linsir.abc.core.grammar.method;

/**
 * 方法基础示例
 *
 * 本类演示 Java 方法的基本概念和定义
 * 对应 JDK: 方法声明和调用
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class MethodBasics {

    /**
     * 实例变量
     */
    private int value;

    /**
     * 无参构造方法
     */
    public MethodBasics() {
        this.value = 0;
    }

    /**
     * 带参构造方法
     *
     * @param value 初始值
     */
    public MethodBasics(int value) {
        this.value = value;
    }

    /**
     * 无返回值、无参数的方法
     * 打印问候语
     */
    public void sayHello() {
        System.out.println("Hello, World!");
    }

    /**
     * 有返回值、有参数的方法
     * 计算两个整数的和
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return 两数之和
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * 重载方法 - 参数类型不同
     * 计算两个浮点数的和
     *
     * @param a 第一个浮点数
     * @param b 第二个浮点数
     * @return 两数之和
     */
    public double add(double a, double b) {
        return a + b;
    }

    /**
     * 重载方法 - 参数个数不同
     * 计算三个整数的和
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @param c 第三个整数
     * @return 三数之和
     */
    public int add(int a, int b, int c) {
        return a + b + c;
    }

    /**
     * 私有方法
     * 辅助计算方法
     *
     * @param n 输入数字
     * @return 平方值
     */
    private int square(int n) {
        return n * n;
    }

    /**
     * 使用私有方法
     *
     * @param n 输入数字
     * @return 平方值
     */
    public int getSquare(int n) {
        return square(n);
    }

    /**
     * 静态方法
     * 不需要创建对象即可调用
     *
     * @param radius 半径
     * @return 圆面积
     */
    public static double calculateCircleArea(double radius) {
        return Math.PI * radius * radius;
    }

    /**
     * 获取当前值
     *
     * @return 当前值
     */
    public int getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 新值
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * 演示方法调用
     */
    public void demonstrateMethodCalls() {
        System.out.println("=== 方法调用演示 ===");

        // 调用无参方法
        System.out.println("调用 sayHello():");
        sayHello();

        // 调用有参方法
        System.out.println("\n调用 add(5, 3): " + add(5, 3));

        // 调用重载方法
        System.out.println("调用 add(2.5, 3.5): " + add(2.5, 3.5));
        System.out.println("调用 add(1, 2, 3): " + add(1, 2, 3));

        // 调用静态方法
        System.out.println("\n调用静态方法 calculateCircleArea(5): " + calculateCircleArea(5));

        // 通过类名调用静态方法
        System.out.println("通过类名调用: MethodBasics.calculateCircleArea(3): " 
            + MethodBasics.calculateCircleArea(3));

        // 调用私有方法的包装方法
        System.out.println("\n调用 getSquare(4): " + getSquare(4));
    }

    /**
     * 演示递归方法
     * 计算阶乘
     *
     * @param n 非负整数
     * @return n的阶乘
     */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n必须是非负整数");
        }
        if (n == 0 || n == 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    /**
     * 演示递归
     */
    public void demonstrateRecursion() {
        System.out.println("\n=== 递归演示 ===");

        System.out.println("阶乘计算:");
        for (int i = 0; i <= 10; i++) {
            System.out.println("  " + i + "! = " + factorial(i));
        }

        // 斐波那契数列
        System.out.println("\n斐波那契数列:");
        for (int i = 0; i < 10; i++) {
            System.out.print(fibonacci(i) + " ");
        }
        System.out.println();
    }

    /**
     * 斐波那契数列
     *
     * @param n 索引
     * @return 第n个斐波那契数
     */
    private long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 方法基础演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        // 创建对象
        MethodBasics demo = new MethodBasics(100);

        demo.demonstrateMethodCalls();
        demo.demonstrateRecursion();

        System.out.println("\n当前值: " + demo.getValue());
        demo.setValue(200);
        System.out.println("设置后值: " + demo.getValue());

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
