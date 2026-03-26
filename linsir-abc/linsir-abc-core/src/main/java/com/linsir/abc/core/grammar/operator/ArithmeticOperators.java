package com.linsir.abc.core.grammar.operator;

/**
 * 算术运算符示例
 *
 * 本类演示 Java 算术运算符的使用，包括基本运算、自增自减和复合赋值
 * 对应 JDK: 基本算术运算
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ArithmeticOperators {

    /**
     * 演示基本算术运算
     * 加法、减法、乘法、除法和取模
     */
    public void demonstrateBasicArithmetic() {
        System.out.println("=== 基本算术运算 ===");

        int a = 17, b = 5;

        System.out.println("a = " + a + ", b = " + b);
        System.out.println();

        // 加法
        int sum = a + b;
        System.out.println("a + b = " + sum);

        // 减法
        int diff = a - b;
        System.out.println("a - b = " + diff);

        // 乘法
        int product = a * b;
        System.out.println("a * b = " + product);

        // 整数除法（截断小数部分）
        int quotient = a / b;
        System.out.println("a / b (整数除法) = " + quotient);

        // 取模（求余数）
        int remainder = a % b;
        System.out.println("a % b (取模) = " + remainder);

        // 浮点数除法
        double floatQuotient = (double) a / b;
        System.out.println("(double)a / b = " + floatQuotient);

        // 负数取模
        System.out.println("\n负数取模:");
        System.out.println("17 % 5 = " + (17 % 5));
        System.out.println("-17 % 5 = " + (-17 % 5));
        System.out.println("17 % -5 = " + (17 % -5));
        System.out.println("-17 % -5 = " + (-17 % -5));
    }

    /**
     * 演示自增和自减运算符
     * 前缀形式和后缀形式的区别
     */
    public void demonstrateIncrementDecrement() {
        System.out.println("\n=== 自增自减运算符 ===");

        int x = 5;
        System.out.println("初始 x = " + x);
        System.out.println();

        // 前缀自增 ++x：先增加，后使用
        System.out.println("前缀自增 (++x):");
        x = 5;
        int y = ++x;  // x 先变成 6，然后赋值给 y
        System.out.println("  y = ++x: x = " + x + ", y = " + y);

        // 后缀自增 x++：先使用，后增加
        System.out.println("\n后缀自增 (x++):");
        x = 5;
        int z = x++;  // 先将 x(5) 赋值给 z，然后 x 变成 6
        System.out.println("  z = x++: x = " + x + ", z = " + z);

        // 自减同理
        System.out.println("\n自减运算:");
        x = 5;
        System.out.println("  --x = " + (--x) + ", x = " + x);
        x = 5;
        System.out.println("  x-- = " + (x--) + ", x = " + x);

        // 在复杂表达式中使用
        System.out.println("\n在表达式中使用:");
        x = 5;
        int result = x++ + ++x;  // 5 + 7 = 12
        System.out.println("  x = 5, x++ + ++x = " + result + " (x 最终 = " + x + ")");
    }

    /**
     * 演示复合赋值运算符
     * 简化算术运算和赋值
     */
    public void demonstrateCompoundAssignment() {
        System.out.println("\n=== 复合赋值运算符 ===");

        int a = 10;
        System.out.println("初始 a = " + a);
        System.out.println();

        // 加等于
        a += 5;  // 等价于 a = a + 5
        System.out.println("a += 5  -> a = " + a);

        // 减等于
        a -= 3;  // 等价于 a = a - 3
        System.out.println("a -= 3  -> a = " + a);

        // 乘等于
        a *= 2;  // 等价于 a = a * 2
        System.out.println("a *= 2  -> a = " + a);

        // 除等于
        a /= 4;  // 等价于 a = a / 4
        System.out.println("a /= 4  -> a = " + a);

        // 模等于
        a %= 3;  // 等价于 a = a % 3
        System.out.println("a %= 3  -> a = " + a);

        // 复合赋值会自动进行类型转换
        System.out.println("\n复合赋值的类型转换:");
        byte b = 10;
        // b = b + 5;  // 错误：int 不能赋值给 byte
        b += 5;  // 正确：复合赋值会自动转换
        System.out.println("byte b += 5 -> b = " + b);
    }

    /**
     * 演示整数溢出
     * 当运算结果超出类型范围时会发生溢出
     */
    public void demonstrateOverflow() {
        System.out.println("\n=== 整数溢出 ===");

        // int 最大值
        int max = Integer.MAX_VALUE;
        System.out.println("Integer.MAX_VALUE = " + max);

        // 溢出：最大值 + 1 变成最小值
        int overflow = max + 1;
        System.out.println("MAX_VALUE + 1 = " + overflow);
        System.out.println("Integer.MIN_VALUE = " + Integer.MIN_VALUE);

        // 下溢：最小值 - 1 变成最大值
        int min = Integer.MIN_VALUE;
        int underflow = min - 1;
        System.out.println("\nMIN_VALUE - 1 = " + underflow);

        // 使用 Math.addExact 检测溢出（Java 8+）
        System.out.println("\n使用 Math.addExact 检测溢出:");
        try {
            int result = Math.addExact(max, 1);
            System.out.println("结果: " + result);
        } catch (ArithmeticException e) {
            System.out.println("溢出检测: " + e.getMessage());
        }

        // 乘法溢出
        System.out.println("\n乘法溢出:");
        int large = 100_000;
        int product = large * large;  // 10_000_000_000 超出 int 范围
        System.out.println("100000 * 100000 = " + product + " (溢出)");

        // 正确使用 long
        long correct = (long) large * large;
        System.out.println("(long)100000 * 100000 = " + correct);
    }

    /**
     * 主方法，运行所有演示
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 算术运算符演示                        ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        ArithmeticOperators demo = new ArithmeticOperators();

        demo.demonstrateBasicArithmetic();
        demo.demonstrateIncrementDecrement();
        demo.demonstrateCompoundAssignment();
        demo.demonstrateOverflow();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
