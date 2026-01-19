package com.linsir.core.c1;

public class For {
    public static void main(String[] args) {
        int sum = 0;
        String str[] = {"a","b","c"};
        for(int i = 1;i < 101;i++){
            sum += i;
        }
        System.out.println("1+2+3+...+ 100 = " + sum);
        for(int i = 1; i < str.length + 1; i++){
            System.out.println("序号： " + i + " 名称： "+str[i -1]);

        }
    }
}
