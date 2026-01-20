package com.linsir.abc.pdai.Base.obj;

/**
 * 抽象动物类：定义抽象行为并提供部分实现
 */
public abstract class Animal extends BaseEntity {
    public Animal(long id, String name) {
        super(id, name);
    }

    // 抽象方法：由子类实现
    public abstract void sound();

    // 具体方法：子类可以选择覆写
    public void move() {
        System.out.println(getName() + " moves.");
    }

    // 方法重载（overload）
    public void eat() {
        eat("food");
    }

    public void eat(String what) {
        System.out.println(getName() + " eats " + what + ".");
    }

    // final 方法不能被覆写
    public final void info() {
        System.out.println("INFO: " + toString());
    }

    // static 成员（类级别）
    public static void species() {
        System.out.println("Animals: multicellular organisms (示例 static 方法)");
    }
}