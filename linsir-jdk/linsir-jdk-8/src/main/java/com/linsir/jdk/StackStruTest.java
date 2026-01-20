package com.linsir.jdk;

public class StackStruTest {

     public static void main(String[] args) {
        int i = 10;
        int j = 20;
        int k = i+j;
        System.out.println(k);

         System.out.printf("a{}", k);

         try {
             Thread.sleep(3000);
         } catch (InterruptedException e) {
             throw new RuntimeException(e);
         }
     }
}
