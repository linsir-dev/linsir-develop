package com.linsir.core.thread;


import java.util.concurrent.TimeUnit;

public class Interrupted {
    public static void main(String[] args) throws InterruptedException {

        // sleepThread不停的尝试睡眠
        Thread sleepThread = new Thread(new SleepRunner(),"SleepThread");
        sleepThread.setDaemon(true);

        // busyThread不停的运行
        Thread busyThread= new Thread(new BusyRunner(),"BusyRunner");
        busyThread.setDaemon(true);

        sleepThread.start();
        busyThread.start();

        // 休眠5秒，让sleepThread和busyThread充分运行
        TimeUnit.SECONDS.sleep(5);

        sleepThread.interrupt();
        busyThread.interrupt();

        System.out.print("SleepThread interrupted is "+sleepThread.isInterrupted()+"\n");
        System.out.print("BusyThread interrupted is "+busyThread.isInterrupted()+"\n");
    }

    static class SleepRunner implements Runnable{
        @Override
        public void run() {
                while (true)
                {
                    SleepUtils.second(10);
                }
        }
    }

    static class BusyRunner implements Runnable{

        @Override
        public void run() {
            while (true)
            {

            }
        }
    }
}
