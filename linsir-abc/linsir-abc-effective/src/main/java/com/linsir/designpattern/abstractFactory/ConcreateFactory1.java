package com.linsir.designpattern.abstractFactory;

public class ConcreateFactory1 implements AbstractFactory {
    public ProductA factoryA() {
        return new ProductA1();
    }

    public ProductB factoryB() {
        return new ProductB1();
    }
}
