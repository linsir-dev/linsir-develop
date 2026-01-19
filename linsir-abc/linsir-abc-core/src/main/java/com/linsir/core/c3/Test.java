package com.linsir.core.c3;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入你要购买的饮品： 1.可乐 2.咖啡 3.奶茶");
        int choice = sc.nextInt();
        Drink drink = DrinkStore.getDrink(choice);
        System.out.println(drink.showDrink());

    }
}
