package com.linsir.abc.core.grammar.variable;

import java.util.Date;

/**
 * 常量示例
 *
 * 本类演示 Java 中常量的定义和使用，包括编译期常量、运行期常量和枚举
 * 对应 JDK: final 关键字的使用
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class Constants {

    /**
     * 编译期常量
     * 基本类型或 String，编译时确定值，存储在常量池中
     */
    public static final int MAX_SIZE = 100;
    public static final double PI = 3.141592653589793;
    public static final String APP_NAME = "LinsirABC";
    public static final String VERSION = "1.0.0";

    /**
     * 运行期常量
     * 对象类型，运行时确定值，存储在堆中
     */
    public static final Date CREATE_TIME = new Date();

    /**
     * 实例常量
     * 每个实例可以有不同的值，但创建后不可修改
     */
    private final int instanceConstant;

    /**
     * 构造方法
     * 初始化实例常量
     *
     * @param value 实例常量的值
     */
    public Constants(int value) {
        this.instanceConstant = value;
    }

    /**
     * 演示常量的使用
     */
    public void demonstrateConstants() {
        System.out.println("=== 常量使用 ===");

        // 访问类常量
        System.out.println("MAX_SIZE: " + MAX_SIZE);
        System.out.println("PI: " + PI);
        System.out.println("APP_NAME: " + APP_NAME);
        System.out.println("VERSION: " + VERSION);

        // 运行期常量
        System.out.println("CREATE_TIME: " + CREATE_TIME);

        // 实例常量
        System.out.println("instanceConstant: " + instanceConstant);

        // 局部常量
        final int localConst = 50;
        System.out.println("localConst: " + localConst);

        // 引用类型的常量
        // 引用本身不可变，但对象内容可变
        final StringBuilder sb = new StringBuilder("Hello");
        System.out.println("\n引用类型常量 StringBuilder:");
        System.out.println("  初始值: " + sb);

        sb.append(" World");  // 可以修改对象内容
        System.out.println("  修改后: " + sb);

        // sb = new StringBuilder();  // 错误：不能改变引用指向

        // 数组常量
        final int[] arr = {1, 2, 3};
        arr[0] = 100;  // 可以修改数组元素
        // arr = new int[5];  // 错误：不能改变引用
        System.out.println("  数组常量修改元素后: [" + arr[0] + ", " + arr[1] + ", " + arr[2] + "]");
    }

    /**
     * 演示枚举的使用
     */
    public void demonstrateEnum() {
        System.out.println("\n=== 枚举常量 ===");

        // 使用枚举
        Status status = Status.ACTIVE;
        System.out.println("当前状态: " + status);
        System.out.println("状态描述: " + status.getDescription());
        System.out.println("状态编码: " + status.getCode());

        // 遍历所有枚举值
        System.out.println("\n所有状态:");
        for (Status s : Status.values()) {
            System.out.println("  " + s + " (code=" + s.getCode() + "): " + s.getDescription());
        }

        // 根据字符串获取枚举
        Status parsed = Status.valueOf("ACTIVE");
        System.out.println("\n从字符串解析: Status.valueOf(\"ACTIVE\") = " + parsed);

        // 使用 switch 处理枚举
        System.out.println("\n使用 switch 处理枚举:");
        handleStatus(Status.INACTIVE);
        handleStatus(Status.DELETED);
    }

    /**
     * 处理状态
     *
     * @param status 状态枚举
     */
    private void handleStatus(Status status) {
        switch (status) {
            case ACTIVE:
                System.out.println("  处理激活状态");
                break;
            case INACTIVE:
                System.out.println("  处理未激活状态");
                break;
            case DELETED:
                System.out.println("  处理已删除状态");
                break;
            default:
                System.out.println("  未知状态");
        }
    }

    /**
     * 获取实例常量的值
     *
     * @return 实例常量值
     */
    public int getInstanceConstant() {
        return instanceConstant;
    }

    /**
     * 主方法，运行所有演示
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 常量演示                              ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        // 创建实例
        Constants demo = new Constants(42);

        demo.demonstrateConstants();
        demo.demonstrateEnum();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
        System.out.println("\n总结:");
        System.out.println("• 编译期常量：基本类型/String，编译时确定，性能最优");
        System.out.println("• 运行期常量：对象类型，运行时确定");
        System.out.println("• 引用常量：引用不可变，对象内容可变");
        System.out.println("• 枚举：类型安全的常量集合，可包含方法和字段");
    }

    /**
     * 状态枚举
     * 演示带构造方法和字段的枚举
     */
    public enum Status {
        /** 激活状态 */
        ACTIVE(1, "激活"),
        /** 未激活状态 */
        INACTIVE(2, "未激活"),
        /** 已删除状态 */
        DELETED(3, "已删除");

        /** 状态编码 */
        private final int code;
        /** 状态描述 */
        private final String description;

        /**
         * 枚举构造方法
         *
         * @param code        状态编码
         * @param description 状态描述
         */
        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 获取状态编码
         *
         * @return 状态编码
         */
        public int getCode() {
            return code;
        }

        /**
         * 获取状态描述
         *
         * @return 状态描述
         */
        public String getDescription() {
            return description;
        }
    }
}
