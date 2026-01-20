package com.linsir.abc.pdai.oopdemo;

public abstract class Animal {
    private String name;
    private int age;

    public Animal(String name) {
        this(name, 0);
    }

    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Encapsulation: private fields with getters/setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Polymorphism: abstract method to be implemented by subclasses
    public abstract void speak();

    // Concrete method shared by all animals
    public void eat(String food) {
        System.out.println(name + " is eating " + food + ".");
    }

    @Override
    public String toString() {
        return String.format("%s (age %d)", name, age);
    }
}