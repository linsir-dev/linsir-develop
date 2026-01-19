package com.linsir.core.c2;

public class FruitsPizza extends Pizza{
    private String burdening;

    public String getBurdening() {
        return burdening;
    }

    public void setBurdening(String burdening) {
        this.burdening = burdening;
    }

    public FruitsPizza() {
    }

    public FruitsPizza(String name, int size, double price, String burdening) {
        super(name, size, price);
        this.burdening = burdening;
    }

    @Override
    public String showPizza() {
        return super.showPizza() + "\n配料是： " + burdening;
    }
}
