package com.linsir.abc.core.jvm.classloading.initialization;

/**
 * 父类，用于演示被动引用示例
 * 当通过子类引用父类的静态字段时，只有父类会被初始化
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class SuperClass {

    /**
     * 静态代码块，类初始化时执行
     */
    static {
        System.out.println("SuperClass init!");
    }

    /**
     * 静态字段
     */
    public static int value = 123;

    /**
     * 静态常量（编译期常量）
     */
    public static final String CONSTANT = "Hello SuperClass";

    /**
     * 实例字段
     */
    private String name;

    /**
     * 构造方法
     */
    public SuperClass() {
        this.name = "SuperClass";
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }
}
