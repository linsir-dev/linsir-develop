package com.linsir.abc.pdai.Base.obj.oopdemo;

public class Dog extends Animal implements Walkable {
    private String breed;

    public Dog(String name, int age, String breed) {
        super(name, age);
        this.breed = breed;
    }

    public String getBreed() {
        return breed;
    }

    @Override
    public void speak() {
        System.out.println(getName() + " says: Woof!");
    }

    @Override
    public void walk() {
        System.out.println(getName() + " the " + breed + " is walking.");
    }

    @Override
    public String toString() {
        return super.toString() + " [Dog, breed=" + breed + "]";
    }
}