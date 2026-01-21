package com.linsir.abc.pdai.base.oopdemo;

public interface Walkable {
    void walk();

    default void stroll() {
        System.out.println("Taking a leisurely stroll.");
    }
}