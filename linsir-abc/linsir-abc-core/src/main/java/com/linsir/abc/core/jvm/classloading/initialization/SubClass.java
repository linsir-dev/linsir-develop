package com.linsir.abc.core.jvm.classloading.initialization;

/**
 * 子类，用于演示被动引用示例
 * 当通过子类引用父类的静态字段时，子类不会被初始化
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class SubClass extends SuperClass {

    /**
     * 静态代码块，类初始化时执行
     */
    static {
        System.out.println("SubClass init!");
    }

    /**
     * 子类特有的静态字段
     */
    public static int subValue = 456;

    /**
     * 子类特有的静态常量
     */
    public static final String SUB_CONSTANT = "Hello SubClass";

    /**
     * 子类特有的实例字段
     */
    private String subName;

    /**
     * 构造方法
     */
    public SubClass() {
        super();
        this.subName = "SubClass";
        System.out.println("SubClass constructor executed!");
    }

    /**
     * 获取子类名称
     *
     * @return 子类名称
     */
    public String getSubName() {
        return subName;
    }
}
