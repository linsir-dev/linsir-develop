package com.linsir.abc.pdai.thread;

public class Func1 {

    public void func()
    {
        synchronized (this)
        {
            for (int i = 0; i < 10; i++) {
                System.out.print(i + " ");
            }
        }
    }
}
