package com.linsir.designpattern.observer;

// 具体观察者类
public class ConcreteObserver implements Observer {

    private String name;

    public ConcreteObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(int state) {
        System.out.println(name + " 收到更新，新状态为: " + state);
    }

}
