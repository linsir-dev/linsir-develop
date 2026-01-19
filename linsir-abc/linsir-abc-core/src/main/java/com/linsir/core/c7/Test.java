package com.linsir.core.c7;

public class Test {
    public static void main(String[] args) {
        BuyTicketThread t = new BuyTicketThread();

        Thread t1 = new Thread(t,"窗口1");
        t1.start();

        Thread t2 = new Thread(t,"窗口2");
        t2.start();

        Thread t3 = new Thread(t,"窗口3");
        t3.start();
    }
}
