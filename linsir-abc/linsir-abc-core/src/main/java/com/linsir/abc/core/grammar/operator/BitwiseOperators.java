package com.linsir.abc.core.grammar.operator;

/**
 * 位运算符示例
 *
 * 本类演示 Java 位运算符的使用，包括基本位运算、移位运算和实际应用
 * 对应 JDK: 位运算操作
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class BitwiseOperators {

    /**
     * 演示基本位运算
     * 按位与、或、异或、取反
     */
    public void demonstrateBasicBitwise() {
        System.out.println("=== 基本位运算 ===");

        int a = 5;   // 二进制: 0101
        int b = 3;   // 二进制: 0011

        System.out.println("a = " + a + " (二进制: " + toBinary(a) + ")");
        System.out.println("b = " + b + " (二进制: " + toBinary(b) + ")");
        System.out.println();

        // 按位与 &：两位都为1，结果为1
        int and = a & b;  // 0001 = 1
        System.out.println("a & b = " + and + " (二进制: " + toBinary(and) + ")");
        System.out.println("  解释: 0101 & 0011 = 0001");

        // 按位或 |：有一位为1，结果为1
        int or = a | b;   // 0111 = 7
        System.out.println("\na | b = " + or + " (二进制: " + toBinary(or) + ")");
        System.out.println("  解释: 0101 | 0011 = 0111");

        // 按位异或 ^：两位不同，结果为1
        int xor = a ^ b;  // 0110 = 6
        System.out.println("\na ^ b = " + xor + " (二进制: " + toBinary(xor) + ")");
        System.out.println("  解释: 0101 ^ 0011 = 0110");

        // 按位取反 ~：0变1，1变0
        int not = ~a;     // 1111...1010 = -6
        System.out.println("\n~a = " + not + " (二进制: " + toBinary(not) + ")");
        System.out.println("  解释: ~0101 = 1111...1010 (补码表示的负数)");
    }

    /**
     * 演示移位运算
     * 左移、算术右移、逻辑右移
     */
    public void demonstrateShift() {
        System.out.println("\n=== 移位运算 ===");

        int a = 8;   // 二进制: 1000

        System.out.println("a = " + a + " (二进制: " + toBinary(a) + ")");
        System.out.println();

        // 左移 <<: 高位丢弃，低位补0，相当于乘以2的n次方
        int leftShift = a << 2;  // 100000 = 32
        System.out.println("a << 2 = " + leftShift + " (二进制: " + toBinary(leftShift) + ")");
        System.out.println("  相当于 a * 4 = " + (a * 4));

        // 算术右移 >>: 低位丢弃，高位补符号位
        int rightShift = a >> 2;  // 0010 = 2
        System.out.println("\na >> 2 = " + rightShift + " (二进制: " + toBinary(rightShift) + ")");
        System.out.println("  相当于 a / 4 = " + (a / 4));

        // 负数移位
        System.out.println("\n负数移位:");
        int negative = -8;
        System.out.println("-8 = " + negative + " (二进制: " + toBinary(negative) + ")");

        // 算术右移保持符号
        int negArithShift = negative >> 2;
        System.out.println("-8 >> 2 = " + negArithShift + " (算术右移，保持符号)");

        // 逻辑右移 >>> 高位补0
        int negLogicShift = negative >>> 2;
        System.out.println("-8 >>> 2 = " + negLogicShift + " (逻辑右移，高位补0)");
    }

    /**
     * 演示位运算在权限控制中的应用
     * 使用位标志表示权限
     */
    public void demonstratePermissionControl() {
        System.out.println("\n=== 位运算应用：权限控制 ===");

        // 定义权限（2的幂次，确保只有一位是1）
        final int READ = 1 << 0;    // 0001 = 1
        final int WRITE = 1 << 1;   // 0010 = 2
        final int EXECUTE = 1 << 2; // 0100 = 4
        final int DELETE = 1 << 3;  // 1000 = 8

        System.out.println("权限定义:");
        System.out.println("  READ = " + READ + " (二进制: " + toBinary4(READ) + ")");
        System.out.println("  WRITE = " + WRITE + " (二进制: " + toBinary4(WRITE) + ")");
        System.out.println("  EXECUTE = " + EXECUTE + " (二进制: " + toBinary4(EXECUTE) + ")");
        System.out.println("  DELETE = " + DELETE + " (二进制: " + toBinary4(DELETE) + ")");

        // 组合权限（使用按位或）
        int userPermission = READ | WRITE;
        int adminPermission = READ | WRITE | EXECUTE | DELETE;

        System.out.println("\n用户权限: " + userPermission + " (二进制: " + toBinary4(userPermission) + ")");
        System.out.println("管理员权限: " + adminPermission + " (二进制: " + toBinary4(adminPermission) + ")");

        // 检查权限（使用按位与）
        System.out.println("\n权限检查:");
        System.out.println("  用户是否有 READ 权限? " + hasPermission(userPermission, READ));
        System.out.println("  用户是否有 EXECUTE 权限? " + hasPermission(userPermission, EXECUTE));
        System.out.println("  管理员是否有所有权限? " + hasPermission(adminPermission, READ | WRITE | EXECUTE | DELETE));

        // 添加权限
        userPermission = addPermission(userPermission, EXECUTE);
        System.out.println("\n添加 EXECUTE 后，用户权限: " + userPermission);
        System.out.println("  现在有 EXECUTE 权限? " + hasPermission(userPermission, EXECUTE));

        // 移除权限
        userPermission = removePermission(userPermission, WRITE);
        System.out.println("\n移除 WRITE 后，用户权限: " + userPermission);
        System.out.println("  还有 WRITE 权限? " + hasPermission(userPermission, WRITE));
    }

    /**
     * 演示位运算在高效乘除法中的应用
     */
    public void demonstrateFastMath() {
        System.out.println("\n=== 位运算应用：高效乘除法 ===");

        int num = 16;
        System.out.println("num = " + num);
        System.out.println();

        // 乘以2的幂次（左移）
        System.out.println("乘法（左移）:");
        System.out.println("  " + num + " << 1 = " + (num << 1) + " (相当于 *2)");
        System.out.println("  " + num + " << 2 = " + (num << 2) + " (相当于 *4)");
        System.out.println("  " + num + " << 3 = " + (num << 3) + " (相当于 *8)");

        // 除以2的幂次（右移）
        System.out.println("\n除法（右移）:");
        System.out.println("  " + num + " >> 1 = " + (num >> 1) + " (相当于 /2)");
        System.out.println("  " + num + " >> 2 = " + (num >> 2) + " (相当于 /4)");
        System.out.println("  " + num + " >> 3 = " + (num >> 3) + " (相当于 /8)");

        // 取模2的幂次（与）
        System.out.println("\n取模（与运算）:");
        System.out.println("  " + num + " & 7 = " + (num & 7) + " (相当于 %8)");
        System.out.println("  " + num + " & 15 = " + (num & 15) + " (相当于 %16)");

        // 判断奇偶
        System.out.println("\n判断奇偶:");
        System.out.println("  " + num + " & 1 = " + (num & 1) + " (0为偶数，1为奇数)");
        System.out.println("  " + (num + 1) + " & 1 = " + ((num + 1) & 1));
    }

    /**
     * 将整数转换为32位二进制字符串
     *
     * @param num 要转换的整数
     * @return 32位二进制字符串
     */
    private String toBinary(int num) {
        return String.format("%32s", Integer.toBinaryString(num)).replace(' ', '0');
    }

    /**
     * 将整数转换为4位二进制字符串（用于权限演示）
     *
     * @param num 要转换的整数
     * @return 4位二进制字符串
     */
    private String toBinary4(int num) {
        return String.format("%4s", Integer.toBinaryString(num)).replace(' ', '0');
    }

    /**
     * 检查是否具有指定权限
     *
     * @param permissions 当前权限组合
     * @param permission  要检查的权限
     * @return 如果有该权限返回true
     */
    private boolean hasPermission(int permissions, int permission) {
        return (permissions & permission) == permission;
    }

    /**
     * 添加权限
     *
     * @param permissions 当前权限组合
     * @param permission  要添加的权限
     * @return 新的权限组合
     */
    private int addPermission(int permissions, int permission) {
        return permissions | permission;
    }

    /**
     * 移除权限
     *
     * @param permissions 当前权限组合
     * @param permission  要移除的权限
     * @return 新的权限组合
     */
    private int removePermission(int permissions, int permission) {
        return permissions & ~permission;
    }

    /**
     * 主方法，运行所有演示
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 位运算符演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        BitwiseOperators demo = new BitwiseOperators();

        demo.demonstrateBasicBitwise();
        demo.demonstrateShift();
        demo.demonstratePermissionControl();
        demo.demonstrateFastMath();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
