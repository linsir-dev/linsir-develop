package com.linsir.abc.core.grammar.controlflow;

/**
 * 循环语句示例
 *
 * 本类演示 Java 循环语句的使用
 * 对应 JDK: 循环控制
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class Loops {

    /**
     * 演示 for 循环
     */
    public void demonstrateForLoop() {
        System.out.println("=== for 循环 ===");

        // 基本 for 循环
        System.out.println("基本 for 循环:");
        for (int i = 0; i < 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 多个初始化表达式
        System.out.println("\n多个初始化:");
        for (int i = 0, j = 10; i < j; i++, j--) {
            System.out.println("  i=" + i + ", j=" + j);
        }

        // 省略部分表达式
        System.out.println("\n省略初始化（外部定义）:");
        int k = 0;
        for (; k < 3; k++) {
            System.out.print(k + " ");
        }
        System.out.println("\n循环后 k=" + k);

        // 无限循环
        System.out.println("\n有限循环（避免无限）:");
        int count = 0;
        for (;;) {
            if (count >= 3) break;
            System.out.print(count + " ");
            count++;
        }
        System.out.println();
    }

    /**
     * 演示增强 for 循环
     */
    public void demonstrateEnhancedForLoop() {
        System.out.println("\n=== 增强 for 循环 ===");

        // 遍历数组
        int[] numbers = {1, 2, 3, 4, 5};
        System.out.println("遍历数组:");
        for (int num : numbers) {
            System.out.print(num + " ");
        }
        System.out.println();

        // 遍历字符串
        String[] fruits = {"Apple", "Banana", "Cherry"};
        System.out.println("\n遍历字符串数组:");
        for (String fruit : fruits) {
            System.out.println("  " + fruit);
        }

        // 与迭代器等价
        System.out.println("\n增强 for 是迭代器的语法糖:");
        java.util.List<String> list = java.util.Arrays.asList("A", "B", "C");
        for (String item : list) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    /**
     * 演示 while 循环
     */
    public void demonstrateWhileLoop() {
        System.out.println("\n=== while 循环 ===");

        // 基本 while 循环
        System.out.println("基本 while:");
        int i = 0;
        while (i < 5) {
            System.out.print(i + " ");
            i++;
        }
        System.out.println();

        // 条件可能一开始就不满足
        System.out.println("\n条件不满足的情况:");
        int j = 10;
        while (j < 5) {
            System.out.println("不会执行");
            j++;
        }
        System.out.println("  循环未执行（条件一开始为false）");
    }

    /**
     * 演示 do-while 循环
     */
    public void demonstrateDoWhileLoop() {
        System.out.println("\n=== do-while 循环 ===");

        // 基本 do-while
        System.out.println("基本 do-while:");
        int i = 0;
        do {
            System.out.print(i + " ");
            i++;
        } while (i < 5);
        System.out.println();

        // 至少执行一次
        System.out.println("\n至少执行一次:");
        int j = 10;
        do {
            System.out.println("  执行了一次，即使条件不满足");
            j++;
        } while (j < 5);
    }

    /**
     * 演示循环控制语句
     */
    public void demonstrateLoopControl() {
        System.out.println("\n=== 循环控制语句 ===");

        // break 语句
        System.out.println("break 语句:");
        for (int i = 0; i < 10; i++) {
            if (i == 5) break;  // 完全退出循环
            System.out.print(i + " ");
        }
        System.out.println("(在 5 处跳出)");

        // continue 语句
        System.out.println("\ncontinue 语句:");
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) continue;  // 跳过偶数
            System.out.print(i + " ");
        }
        System.out.println("(跳过偶数)");

        // 带标签的 break
        System.out.println("\n带标签的 break:");
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    break outer;  // 跳出外层循环
                }
                System.out.println("  i=" + i + ", j=" + j);
            }
        }
    }

    /**
     * 演示嵌套循环
     */
    public void demonstrateNestedLoops() {
        System.out.println("\n=== 嵌套循环 ===");

        // 打印乘法表
        System.out.println("乘法表:");
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.printf("%d*%d=%-2d ", j, i, i * j);
            }
            System.out.println();
        }

        // 打印三角形
        System.out.println("\n三角形:");
        int size = 5;
        for (int i = 1; i <= size; i++) {
            // 打印空格
            for (int j = 1; j <= size - i; j++) {
                System.out.print(" ");
            }
            // 打印星号
            for (int k = 1; k <= 2 * i - 1; k++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 循环语句演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        Loops demo = new Loops();
        demo.demonstrateForLoop();
        demo.demonstrateEnhancedForLoop();
        demo.demonstrateWhileLoop();
        demo.demonstrateDoWhileLoop();
        demo.demonstrateLoopControl();
        demo.demonstrateNestedLoops();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
