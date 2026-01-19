package com.linsir.core.c3;

public class Milkytea extends Drink{
    public Milkytea() {
    }

    public Milkytea(String name, double price, String size, String remarks, int num) {
        super(name, price, size, remarks, num);
    }

    @Override
    public String showDrink() {
        return "订单信息：您购买了奶茶： " + super.showDrink();
    }
}
