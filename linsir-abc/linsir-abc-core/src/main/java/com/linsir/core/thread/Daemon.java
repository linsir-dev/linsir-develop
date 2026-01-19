package com.linsir.core.thread;


public class Daemon {
    /*Daemon线程是一种支持型线程，因为它主要被用作程序中后台调度以及支持性工作。这
意味着，当一个Java虚拟机中不存在非Daemon线程的时候，Java虚拟机将会退出。可以通过调
用Thread.setDaemon(true)将线程设置为Daemon线程。*/
    /*Daemon线程被用作完成支持性工作，但是在Java虚拟机退出时Daemon线程中的finally块
并不一定会执行*/
    public static void main (String[] args)
    {
         Thread thread = new Thread(new DaemonRunner(),"DaemonRunner");
         thread.setDaemon(true);
         thread.start();
    }

    static class DaemonRunner implements Runnable{

        @Override
        public void run() {
            try {
                SleepUtils.second(10);
            }
            finally {
                System.out.print("DaemonThread finally run.");
            }
        }
    }
}
