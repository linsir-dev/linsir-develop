package com.linsir.designpattern.singleton;

public class SingletonDemo4 {
//    饿汉  变种
    private  static  SingletonDemo4 instance =null;
    static {
        instance = new SingletonDemo4();
    }

    private  SingletonDemo4(){}

    public  static  SingletonDemo4 getInstance()
    {
        System.out.print("\n 饿汉  变种\n");
        return  instance;
    }
}
