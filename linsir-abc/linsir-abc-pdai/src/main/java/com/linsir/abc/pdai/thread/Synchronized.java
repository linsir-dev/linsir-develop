package com.linsir.abc.pdai.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Synchronized {

    public static void main(String[] args) {
        Func1 func1 = new Func1();

        ExecutorService executorService = Executors.newCachedThreadPool();


        executorService.execute(() -> func1.func());
        executorService.execute(() -> func1.func());
    }
}
