package com.linsir.core.c7;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BuyTicketThread implements Runnable{
    int ticketnum = 10;
    @Override
    public void run() {
        Lock lock = new ReentrantLock();
        for (int i = 0; i <=100 ; i++) {
            lock.lock();
            try
            {
                if (ticketnum > 0){
                    System.out.println("我在"+Thread.currentThread().getName()+"买到了第" + ticketnum-- + "张车票");}
            }catch (Exception e){
                    e.printStackTrace();
            } finally{
            lock.unlock();
        }

        }
    }
}
