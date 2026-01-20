package com.linsir.abc.pdai.Base.obj;

/**
 * Fish：会游泳的动物
 */
public class Fish extends Animal implements Swimmable {
    public Fish(long id, String name) {
        super(id, name);
    }

    @Override
    public void sound() {
        System.out.println(getName() + ": ... (silent)");
    }

    @Override
    public void swim() {
        System.out.println(getName() + " swims in water.");
    }

    @Override
    public void move() {
        swim(); // 鱼的移动是游泳
    }
}