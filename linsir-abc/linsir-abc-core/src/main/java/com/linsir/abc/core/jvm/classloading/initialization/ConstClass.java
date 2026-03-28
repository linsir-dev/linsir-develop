package com.linsir.abc.core.jvm.classloading.initialization;

/**
 * 常量类，用于演示编译期常量不会触发类初始化
 * 常量在编译阶段已存入调用类的常量池，不会触发定义常量的类的初始化
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class ConstClass {

    /**
     * 静态代码块，类初始化时执行
     */
    static {
        System.out.println("ConstClass init!");
    }

    /**
     * 编译期常量
     * 在编译阶段已存入调用类的常量池，不会触发ConstClass的初始化
     */
    public static final String HELLO_WORLD = "hello world";

    /**
     * 编译期常量（基本类型）
     */
    public static final int MAX_VALUE = 100;

    /**
     * 运行期常量
     * 在运行期才能确定值，会触发ConstClass的初始化
     */
    public static final String RUNTIME_CONSTANT = System.currentTimeMillis() + "";

    /**
     * 静态字段（非final）
     * 会触发ConstClass的初始化
     */
    public static String staticField = "static field";
}
