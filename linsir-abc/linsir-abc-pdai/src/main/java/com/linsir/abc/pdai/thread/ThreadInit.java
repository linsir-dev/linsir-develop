package com.linsir.abc.pdai.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ThreadInit {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        MyRunnable instance = new MyRunnable();

        MyCallable mc = new MyCallable();
        FutureTask<String> ft = new FutureTask<>(mc);


        Thread thread = new Thread(instance);

        Thread thread1 = new Thread(ft);


        thread.start();
        thread1.start();

        System.out.println(ft.get());

        Thread thread3 = new Thread(()->{
            System.out.println("守护进程...");
        });
        thread.setDaemon(true);

        thread3.start();

    }
}
