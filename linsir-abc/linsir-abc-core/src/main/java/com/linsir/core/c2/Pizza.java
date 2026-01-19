package com.linsir.core.c2;

public class Pizza {
    private String name; //名称
    private int size;    //大小
    private double price;//价格

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public String showPizza(){
        return "披萨的名字是： " + name + "\n披萨的大小是： " + size + "寸\n披萨的价格是： " + price + "元";
    }

    public Pizza() {
    }                   //空构造器

    public Pizza(String name, int size, double price) {
        this.name = name;
        this.size = size;
        this.price = price;
    }
}
