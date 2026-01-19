package com.linsir.core.c3;

public class Drink {
    private String name;
    private double price;
    private String size;
    private String remarks;
    private int num;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Drink() {
    }

    public Drink(String name, double price, String size, String remarks,  int num) {
        this.name = name;
        this.price = price;
        this.size = size;
        this.remarks = remarks;
        this.num = num;
    }

    public Drink(String name, double price) {
        this.name = name;
        this.price = price;
    }
    public String showDrink(){
        return remarks + " ," + size +", 购买的数量是： " +num + " , 价钱:"+ num +"*"+ price + "="+ num*price+ "元";
    }
}
