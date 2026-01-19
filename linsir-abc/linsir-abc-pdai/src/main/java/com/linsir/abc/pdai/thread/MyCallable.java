package com.linsir.abc.pdai.thread;

import java.util.concurrent.Callable;

public class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "MyCallable实现";
    }
}
