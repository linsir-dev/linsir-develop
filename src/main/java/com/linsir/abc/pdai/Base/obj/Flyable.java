package com.linsir.abc.pdai.Base.obj;

/**
 * 表示可飞行能力
 */
public interface Flyable {
    void fly();

    default void land() {
        System.out.println("Landing...");
    }
}