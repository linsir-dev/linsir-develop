package com.linsir.designpattern.facade;

// 子系统：投影仪
public class Projector {

    public void turnOn() {
        System.out.println("Projector is turned on");
    }

    public void turnOff() {
        System.out.println("Projector is turned off");
    }
}
