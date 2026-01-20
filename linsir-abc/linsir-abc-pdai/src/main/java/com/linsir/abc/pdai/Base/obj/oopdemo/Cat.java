package com.linsir.abc.pdai.Base.obj.oopdemo;

public class Cat extends Animal implements Walkable {
    private boolean indoor;

    public Cat(String name, int age, boolean indoor) {
        super(name, age);
        this.indoor = indoor;
    }

    public boolean isIndoor() {
        return indoor;
    }

    @Override
    public void speak() {
        System.out.println(getName() + " says: Meow!");
    }

    @Override
    public void walk() {
        System.out.println(getName() + " trots gracefully.");
    }

    @Override
    public String toString() {
        return super.toString() + " [Cat, indoor=" + indoor + "]";
    }
}