package com.linsir.designpattern.singleton;

public class SingletonDemo2 {

// 懒汉模式 线程安全
    private  static  SingletonDemo2 instance;

    public  static synchronized SingletonDemo2 getInstance()
    {
        System.out.print("\n懒汉模式 线程安全\n");
        if (instance == null)
        {
            instance= new SingletonDemo2();
        }
        return  instance;
    }
}
