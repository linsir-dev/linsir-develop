package com.linsir.abc.core.grammar.operator;

/**
 * 逻辑运算符示例
 *
 * 本类演示 Java 逻辑运算符的使用，包括短路运算和非短路运算
 * 对应 JDK: 布尔逻辑运算
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class LogicalOperators {

    /**
     * 演示基本逻辑运算
     * 与、或、非、异或
     */
    public void demonstrateBasicLogical() {
        System.out.println("=== 基本逻辑运算 ===");

        boolean a = true;
        boolean b = false;

        System.out.println("a = " + a + ", b = " + b);
        System.out.println();

        // 逻辑与 &&：两边都为true，结果才为true
        System.out.println("a && b = " + (a && b) + " (true AND false = false)");

        // 逻辑或 ||：只要一边为true，结果就为true
        System.out.println("a || b = " + (a || b) + " (true OR false = true)");

        // 逻辑非 !：取反
        System.out.println("!a = " + (!a) + " (NOT true = false)");
        System.out.println("!b = " + (!b) + " (NOT false = true)");

        // 逻辑异或 ^：两边不同结果为true
        System.out.println("a ^ b = " + (a ^ b) + " (true XOR false = true)");
        System.out.println("a ^ a = " + (a ^ a) + " (true XOR true = false)");
    }

    /**
     * 演示短路运算
     * 短路运算符不会计算不必要的操作数
     */
    public void demonstrateShortCircuit() {
        System.out.println("\n=== 短路运算 ===");

        // 短路逻辑与 &&：左边为false时，右边不会执行
        System.out.println("短路逻辑与 (&&):");
        boolean result1 = false && willNotExecute();
        System.out.println("  false && willNotExecute() = " + result1);
        System.out.println("  右边方法没有被执行（短路）");

        // 短路逻辑或 ||：左边为true时，右边不会执行
        System.out.println("\n短路逻辑或 (||):");
        boolean result2 = true || willNotExecute();
        System.out.println("  true || willNotExecute() = " + result2);
        System.out.println("  右边方法没有被执行（短路）");

        // 对比：非短路运算符 & 和 |
        System.out.println("\n非短路运算符 (& 和 |):");
        System.out.println("  即使左边已经能确定结果，右边仍会执行");
        boolean result3 = false & willExecute();
        System.out.println("  false & willExecute() = " + result3);

        boolean result4 = true | willExecute();
        System.out.println("  true | willExecute() = " + result4);
    }

    /**
     * 演示短路运算的实际应用
     * 避免空指针异常等安全问题
     */
    public void demonstratePracticalUsage() {
        System.out.println("\n=== 短路运算的实际应用 ===");

        String str = null;

        // 安全调用：先检查非空，再调用方法
        System.out.println("安全调用示例:");
        if (str != null && str.length() > 0) {
            System.out.println("  字符串长度: " + str.length());
        } else {
            System.out.println("  字符串为空或null，安全跳过");
        }

        // 如果写成这样会抛出 NullPointerException
        // if (str.length() > 0 && str != null)  // 错误！

        // 另一个例子：数组边界检查
        int[] arr = {1, 2, 3};
        int index = 5;

        System.out.println("\n数组边界检查:");
        if (index >= 0 && index < arr.length && arr[index] > 0) {
            System.out.println("  值: " + arr[index]);
        } else {
            System.out.println("  索引越界或条件不满足，安全跳过");
        }

        // 默认值设置
        System.out.println("\n默认值设置:");
        String name = null;
        String displayName = (name != null && !name.isEmpty()) ? name : "未知";
        System.out.println("  显示名称: " + displayName);
    }

    /**
     * 演示复杂逻辑表达式
     */
    public void demonstrateComplexExpressions() {
        System.out.println("\n=== 复杂逻辑表达式 ===");

        int age = 25;
        boolean hasId = true;
        boolean isVip = false;

        System.out.println("条件: age=" + age + ", hasId=" + hasId + ", isVip=" + isVip);
        System.out.println();

        // 入场条件：有身份证，且（年龄>=18 或是VIP）
        boolean canEnter = hasId && (age >= 18 || isVip);
        System.out.println("入场条件 (hasId && (age>=18 || isVip)): " + canEnter);

        // 优惠条件：是VIP 或 (年龄<18 或 年龄>60)
        boolean hasDiscount = isVip || (age < 18 || age > 60);
        System.out.println("优惠条件 (isVip || age<18 || age>60): " + hasDiscount);

        // 使用德摩根定律简化
        // !(A && B) 等价于 !A || !B
        // !(A || B) 等价于 !A && !B
        boolean condition1 = !(age >= 18 && hasId);
        boolean condition2 = age < 18 || !hasId;
        System.out.println("\n德摩根定律验证:");
        System.out.println("  !(age>=18 && hasId) = " + condition1);
        System.out.println("  age<18 || !hasId = " + condition2);
        System.out.println("  两者相等: " + (condition1 == condition2));
    }

    /**
     * 演示布尔代数
     */
    public void demonstrateBooleanAlgebra() {
        System.out.println("\n=== 布尔代数定律 ===");

        boolean a = true;
        boolean b = false;

        // 交换律
        System.out.println("交换律:");
        System.out.println("  a && b = b && a: " + (a && b) + " = " + (b && a));
        System.out.println("  a || b = b || a: " + (a || b) + " = " + (b || a));

        // 结合律
        boolean c = true;
        System.out.println("\n结合律:");
        System.out.println("  (a && b) && c = a && (b && c): " 
            + ((a && b) && c) + " = " + (a && (b && c)));
        System.out.println("  (a || b) || c = a || (b || c): " 
            + ((a || b) || c) + " = " + (a || (b || c)));

        // 分配律
        System.out.println("\n分配律:");
        System.out.println("  a && (b || c) = (a && b) || (a && c): " 
            + (a && (b || c)) + " = " + ((a && b) || (a && c)));
        System.out.println("  a || (b && c) = (a || b) && (a || c): " 
            + (a || (b && c)) + " = " + ((a || b) && (a || c)));

        // 恒等律
        System.out.println("\n恒等律:");
        System.out.println("  a && true = a: " + (a && true) + " = " + a);
        System.out.println("  a || false = a: " + (a || false) + " = " + a);

        // 零律
        System.out.println("\n零律:");
        System.out.println("  a && false = false: " + (a && false));
        System.out.println("  a || true = true: " + (a || true));

        // 双重否定
        System.out.println("\n双重否定:");
        System.out.println("  !!a = a: " + (!!a) + " = " + a);
    }

    /**
     * 用于演示短路的方法，不会被执行
     *
     * @return true
     */
    private boolean willNotExecute() {
        System.out.println("  [这个方法不应该被执行]");
        return true;
    }

    /**
     * 用于演示非短路的方法，会被执行
     *
     * @return true
     */
    private boolean willExecute() {
        System.out.println("  [这个方法被执行了]");
        return true;
    }

    /**
     * 主方法，运行所有演示
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 逻辑运算符演示                        ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        LogicalOperators demo = new LogicalOperators();

        demo.demonstrateBasicLogical();
        demo.demonstrateShortCircuit();
        demo.demonstratePracticalUsage();
        demo.demonstrateComplexExpressions();
        demo.demonstrateBooleanAlgebra();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
