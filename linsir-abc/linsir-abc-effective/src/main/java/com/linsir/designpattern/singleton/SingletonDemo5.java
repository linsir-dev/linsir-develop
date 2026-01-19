package com.linsir.designpattern.singleton;

public class SingletonDemo5 {
//静态内部类
    private static class SingletonHolder{
    private static final SingletonDemo5 instance = new SingletonDemo5();
}
    private SingletonDemo5(){}

    public static final SingletonDemo5 getInsatance(){

        System.out.print("\n 静态内部类\n");
        return SingletonHolder.instance;
    }
}
