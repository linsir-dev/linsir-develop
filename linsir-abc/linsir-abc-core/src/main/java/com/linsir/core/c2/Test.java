package com.linsir.core.c2;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        System.out.println("请选择披萨种类： 1.培根披萨   2.水果披萨");
        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();
        Pizza pizza = PizzzaStore.getPizza(choice);
        System.out.println(pizza.showPizza());

    }
}
