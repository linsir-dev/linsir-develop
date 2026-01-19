package com.linsir.core.c3;

import java.util.Scanner;

public class DrinkStore {
    public static Drink getDrink(int choice){
        Scanner sc = new Scanner(System.in);
        Drink p = null;
        switch (choice){
            case 1:
            {
                System.out.println("请输入你需要的可乐的信息：");
                System.out.println("备注是可口可乐还是百事可乐，是否加冰：");
                String remarks =sc.next();
                System.out.println("选择大小：1.中杯 2.大杯 3.超大杯");
                int s = sc.nextInt();
                String size = "";
                if(s == 1){
                     size = "中杯";
                }
                if (s == 2) {
                     size = "大杯";
                }
                if (s == 3) {
                     size = "超大杯";
                }
                System.out.println("购买数量： ");
                int num = sc.nextInt();
                Cola c = new Cola("可乐",10.0,size,remarks,num);
                p = c;
            }
            break;
            case 2:
            {
                System.out.println("请输入你需要的咖啡的信息：");
                System.out.println("备注是加糖、加奶还是什么都不加，是否加冰：");
                String remarks =sc.next();
                System.out.println("选择大小：1.中杯 2.大杯 3.超大杯");
                int s = sc.nextInt();
                String size = "";
                if(s == 1){
                    size = "中杯";
                }
                if (s == 2) {
                    size = "大杯";
                }
                if (s == 3) {
                    size = "超大杯";
                };
                System.out.println("购买数量： ");
                int num = sc.nextInt();
                Coffee c = new Coffee("咖啡",10.0,size,remarks,num);
                p = c;
            }
            break;
            case 3:
            {
                System.out.println("请输入你需要的奶茶的信息：");
                System.out.println("备注是否加入椰果、红豆、布丁，是否加冰：");
                String remarks =sc.next();
                System.out.println("选择大小：1.中杯 2.大杯 3.超大杯");
                int s = sc.nextInt();
                String size = "";
                if(s == 1){
                    size = "中杯";
                }
                if (s == 2) {
                    size = "大杯";
                }
                if (s == 3) {
                    size = "超大杯";
                }
                System.out.println("购买数量： ");
                int num = sc.nextInt();
                Milkytea c = new Milkytea("奶茶",10.0,size,remarks,num);
                p = c;
            }
            break;
        }
        return p;
    }

}
