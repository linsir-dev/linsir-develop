package com.linsir.abc.pdai.thread;


/*
* 线程不安全示
*
*
* 例如果多个线程对同一个共享数据进行访问而不采取同步操作的话，
* 那么操作的结果是不一致的。以下代码演示了 1000 个线程同时对
* cnt 执行自增操作，操作结束之后它的值有可能小于 1000。
------
*
* */
public class ThreadUnsafeExample {
    private int cnt = 0;

    public void add() {
        cnt++;
    }

    public int get() {
        return cnt;
    }
}
