package com.linsir.designpattern.abstractFactory;


public class ConcreateFactory2 implements AbstractFactory {
    public ProductA factoryA() {
        return new ProductA2();
    }

    public ProductB factoryB() {
        return new ProductB2();
    }
}
