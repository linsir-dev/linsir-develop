package com.linsir.abc.pdai.Base.obj;

/**
 * Dog：继承 Animal，提供具体实现与特有方法
 */
public class Dog extends Animal {
    public Dog(long id, String name) {
        super(id, name);
    }

    @Override
    public void sound() {
        System.out.println(getName() + ": Woof!");
    }

    public void fetch() {
        System.out.println(getName() + " fetches the ball.");
    }
}