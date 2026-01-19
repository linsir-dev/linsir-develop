package com.linsir.core.c3;

public class Cola extends Drink{
    public Cola() {
    }

    public Cola(String name, double price, String size, String remarks, int num) {
        super(name, price, size, remarks,  num);
    }

    @Override
    public String showDrink() {
        return "订单信息：您购买了可乐：" + super.showDrink();
    }
}
