package com.linsir.designpattern.singleton;

public class SingletonDemo3 {

//    饿汉
    private  static  SingletonDemo3  instance = new SingletonDemo3();

    public static SingletonDemo3 getInstance() {
        System.out.print("\n饿汉\n");
        return instance;
    }
}
