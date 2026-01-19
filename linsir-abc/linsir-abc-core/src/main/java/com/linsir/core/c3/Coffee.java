package com.linsir.core.c3;

public class Coffee extends Drink{
    public Coffee() {
    }

    public Coffee(String name, double price, String size, String remarks, int num) {
        super(name, price, size, remarks, num);
    }

    @Override
    public String showDrink() {
        return "订单信息：您购买了咖啡： " + super.showDrink();
    }
}
