package com.linsir.abc.pdai.Base.obj;

/**
 * Bird：会飞的动物
 */
public class Bird extends Animal implements Flyable {
    public Bird(long id, String name) {
        super(id, name);
    }

    @Override
    public void sound() {
        System.out.println(getName() + ": Tweet!");
    }

    @Override
    public void fly() {
        System.out.println(getName() + " flies in the sky.");
    }

    // 覆写父类的具体方法
    @Override
    public void move() {
        System.out.println(getName() + " hops and flies.");
    }
}