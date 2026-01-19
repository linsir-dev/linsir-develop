package com.linsir.designpattern.decorator;

public class Decorator implements Source {


    private Source source;

    public  void decorator1(){
        System.out.print("decorate");
    }

    public void method() {
        decorator1();
        source.method();
    }



}
