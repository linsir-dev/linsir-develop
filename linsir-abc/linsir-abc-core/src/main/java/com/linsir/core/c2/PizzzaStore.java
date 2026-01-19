package com.linsir.core.c2;

import java.util.Scanner;

public class PizzzaStore {
    public static Pizza getPizza(int choice) {
        Scanner sc = new Scanner(System.in);
        Pizza p = null;
        switch (choice) {
            case 1: {
                System.out.println("请输入培根的克数： ");
                double weight = sc.nextDouble();
                System.out.println("请输入披萨的大小： ");
                int size = sc.nextInt();
                System.out.println("请输入披萨的价格： ");
                double price = sc.nextDouble();

                BaconPizza bp = new BaconPizza("培根披萨", size, price, weight);
                p = bp;
            }
            break;
            case 2: {
                System.out.println("请输入加入的水果,水果有榴莲、草莓、芒果 ： ");
                String burdening = sc.next();
                System.out.println("请输入披萨的大小： ");
                int size = sc.nextInt();
                System.out.println("请输入披萨的价格： ");
                double price = sc.nextDouble();

                FruitsPizza fp = new FruitsPizza("水果披萨", size, price, burdening);
                p = fp;
            }
            break;
        }
        return p ;
    }

}
