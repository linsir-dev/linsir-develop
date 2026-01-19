package com.linsir.designpattern.decorator;

public class NormalBread implements IBread {
    public void prepair() {
        System.out.println("准备面粉,水以及发酵粉...");
    }

    public void kneadFlour() {
        System.out.println("和面...");
    }

    public void steamed() {
        System.out.println("蒸馒头...香喷喷的馒头出炉了");
    }

    public void process() {
        prepair();
        kneadFlour();
        steamed();
    }
}
