package com.linsir.abc.pdai.Base.obj;

/**
 * Cat：final 类，不能被继承
 */
public final class Cat extends Animal {
    public Cat(long id, String name) {
        super(id, name);
    }

    @Override
    public void sound() {
        System.out.println(getName() + ": Meow!");
    }
}