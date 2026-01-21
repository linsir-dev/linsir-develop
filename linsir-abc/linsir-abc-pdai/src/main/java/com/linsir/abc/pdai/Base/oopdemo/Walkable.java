package com.linsir.abc.pdai.Base.oopdemo;

public interface Walkable {
    void walk();

    default void stroll() {
        System.out.println("Taking a leisurely stroll.");
    }
}