package com.linsir.abc.core.jvm.classloading.initialization;

import java.util.logging.Logger;

/**
 * 被动引用示例演示类
 * 演示三种不会触发类初始化的被动引用场景
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class PassiveReferenceDemo {

    private static final Logger LOGGER = Logger.getLogger(PassiveReferenceDemo.class.getName());

    /**
     * 示例1：通过子类引用父类的静态字段
     * 只有父类会被初始化，子类不会被初始化
     */
    public void demonstrateSubClassReference() {
        LOGGER.info("=== 示例1：通过子类引用父类的静态字段 ===");
        LOGGER.info("预期：只输出 'SuperClass init!'，不输出 'SubClass init!'");
        System.out.println("\n--- 开始测试 ---");

        // 通过子类引用父类的静态字段
        int value = SubClass.value;
        System.out.println("SubClass.value = " + value);

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：对于静态字段，只有直接定义这个字段的类才会被初始化");
    }

    /**
     * 示例2：通过数组定义来引用类
     * 数组类型由虚拟机自动生成，不会触发元素类型的初始化
     */
    public void demonstrateArrayReference() {
        LOGGER.info("=== 示例2：通过数组定义来引用类 ===");
        LOGGER.info("预期：不输出 'SuperClass init!'");
        System.out.println("\n--- 开始测试 ---");

        // 定义数组不会触发SuperClass的初始化
        SuperClass[] array = new SuperClass[10];
        System.out.println("数组类型: " + array.getClass().getName());
        System.out.println("数组长度: " + array.length);

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：数组类型由虚拟机自动生成，继承自java.lang.Object，不会触发元素类型的初始化");
    }

    /**
     * 示例3：常量在编译阶段存入调用类的常量池
     * 编译期常量不会触发定义常量的类的初始化
     */
    public void demonstrateConstantReference() {
        LOGGER.info("=== 示例3：常量在编译阶段存入调用类的常量池 ===");
        LOGGER.info("预期：不输出 'ConstClass init!'");
        System.out.println("\n--- 开始测试 ---");

        // 编译期常量不会触发ConstClass的初始化
        String hello = ConstClass.HELLO_WORLD;
        System.out.println("ConstClass.HELLO_WORLD = " + hello);

        int max = ConstClass.MAX_VALUE;
        System.out.println("ConstClass.MAX_VALUE = " + max);

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：编译期常量已存入当前类的常量池，本质上没有引用ConstClass");
    }

    /**
     * 示例4：运行期常量会触发类初始化
     * 运行期才能确定值的常量会触发类的初始化
     */
    public void demonstrateRuntimeConstant() {
        LOGGER.info("=== 示例4：运行期常量会触发类初始化 ===");
        LOGGER.info("预期：输出 'ConstClass init!'");
        System.out.println("\n--- 开始测试 ---");

        // 运行期常量会触发ConstClass的初始化
        String runtime = ConstClass.RUNTIME_CONSTANT;
        System.out.println("ConstClass.RUNTIME_CONSTANT = " + runtime);

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：运行期常量会触发类的初始化");
    }

    /**
     * 示例5：引用子类的静态字段会触发子类初始化
     */
    public void demonstrateSubClassStaticField() {
        LOGGER.info("=== 示例5：引用子类的静态字段会触发子类初始化 ===");
        LOGGER.info("预期：先输出 'SuperClass init!'，再输出 'SubClass init!'");
        System.out.println("\n--- 开始测试 ---");

        // 引用子类的静态字段，会先初始化父类，再初始化子类
        int subValue = SubClass.subValue;
        System.out.println("SubClass.subValue = " + subValue);

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：当初始化子类时，如果发现其父类还没有初始化，则会先触发父类的初始化");
    }

    /**
     * 示例6：创建子类实例会触发父子类初始化
     */
    public void demonstrateInstanceCreation() {
        LOGGER.info("=== 示例6：创建子类实例会触发父子类初始化 ===");
        LOGGER.info("预期：先输出 'SuperClass init!'，再输出 'SubClass init!'，最后输出构造方法信息");
        System.out.println("\n--- 开始测试 ---");

        // 创建子类实例，会先初始化父类，再初始化子类
        SubClass sub = new SubClass();
        System.out.println("创建实例: " + sub.getClass().getName());

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：使用new关键字实例化对象时，会触发类的初始化");
    }

    /**
     * 运行所有被动引用示例
     */
    public void runAllDemos() {
        LOGGER.info("\n========== 被动引用示例演示 ==========\n");

        demonstrateSubClassReference();
        demonstrateArrayReference();
        demonstrateConstantReference();
        demonstrateRuntimeConstant();
        demonstrateSubClassStaticField();
        demonstrateInstanceCreation();

        LOGGER.info("\n========== 演示结束 ==========\n");
    }

    /**
     * 主方法，用于单独运行演示
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        PassiveReferenceDemo demo = new PassiveReferenceDemo();
        demo.runAllDemos();
    }
}
