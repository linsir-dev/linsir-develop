package com.linsir.abc.pdai.thread;

public class MoreThread {

    public static void main(String[] args) {

        Thread thread1 = new Thread(()->{
            while (true)
            {
                System.out.println("thread1  执行任务中");
            }
        });

        Thread thread2 = new Thread(()->{
            while (true)
            {
                System.out.println("thread2  执行任务中");
            }
        });

        thread1.start();
        thread2.start();
    }
}
