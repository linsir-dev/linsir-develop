package com.linsir.designpattern.singleton;

public class SingletonDemo1 {
    //懒汉模式，线程不安全
    private static  SingletonDemo1 instance;
    public  static  SingletonDemo1 getInstance(){
        System.out.print("\n懒汉模式，线程不安全\n");
        if (instance == null)
        {
            instance = new SingletonDemo1();
        }
        return instance;
    }
}
