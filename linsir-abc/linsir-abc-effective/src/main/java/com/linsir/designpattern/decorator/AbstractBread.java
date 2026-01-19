package com.linsir.designpattern.decorator;

public abstract class AbstractBread implements IBread {

    private final IBread bread;

    protected AbstractBread(IBread bread) {
        this.bread = bread;
    }

    public void prepair() {
        this.bread.prepair();
    }

    public void kneadFlour() {
        this.bread.kneadFlour();
    }

    public void steamed() {
        this.bread.steamed();
    }

    public void process() {
        prepair();
        kneadFlour();
        steamed();
    }
}
