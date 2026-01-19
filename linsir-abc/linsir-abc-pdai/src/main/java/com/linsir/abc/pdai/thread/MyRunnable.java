package com.linsir.abc.pdai.thread;

public class MyRunnable implements Runnable{
    @Override
    public void run() {

        System.out.println("线程实现方法Runnable");
        try {

            Thread.sleep(30000);
            System.out.println("休息30000");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
