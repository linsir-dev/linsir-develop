package com.linsir.designpattern.templateMethod;

public class ConcreteClass extends AbstractClass{
    @Override
    void step1() {
        System.out.println("ConcreteClass: Step 1");
    }

    @Override
    void step2() {
        System.out.println("ConcreteClass: Step 2");
    }

    @Override
    void step3() {
        System.out.println("ConcreteClass: Step 3");
    }
}
