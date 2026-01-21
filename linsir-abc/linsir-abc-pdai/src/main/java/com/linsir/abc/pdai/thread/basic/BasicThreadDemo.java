package com.linsir.abc.pdai.thread.basic;

/**
 * @author linsir
 * @version 1.0
 * @description: 演示基础线程机制
 * @date 2026/1/21 22:38
 */
public class BasicThreadDemo {
    // 1. 线程的生命周期和状态
    static class ThreadStateDemo {
        public static void test() {
            System.out.println("1. 线程的生命周期和状态:");
            Thread thread = new Thread(() -> {
                System.out.println("线程状态: " + Thread.currentThread().getState());
                try {
                    Thread.sleep(1000); // 进入TIMED_WAITING状态
                    synchronized (ThreadStateDemo.class) {
                        ThreadStateDemo.class.wait(500); // 进入WAITING状态
                    }
                } catch (InterruptedException e) {
                    System.out.println("线程被中断");
                }
            }, "StateThread");
            
            System.out.println("新建线程后: " + thread.getState()); // NEW状态
            thread.start();
            
            try {
                Thread.sleep(100); // 等待线程启动
                System.out.println("线程启动后: " + thread.getState()); // RUNNABLE状态
                Thread.sleep(1100); // 等待线程进入睡眠
                System.out.println("线程睡眠后: " + thread.getState()); // TIMED_WAITING状态
                
                synchronized (ThreadStateDemo.class) {
                    ThreadStateDemo.class.notify(); // 唤醒等待的线程
                }
                
                thread.join(); // 等待线程结束
                System.out.println("线程结束后: " + thread.getState()); // TERMINATED状态
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 2. 线程的优先级
    static class ThreadPriorityDemo {
        public static void test() {
            System.out.println("2. 线程的优先级:");
            System.out.println("线程优先级范围: " + Thread.MIN_PRIORITY + "-" + Thread.MAX_PRIORITY);
            System.out.println("默认线程优先级: " + Thread.NORM_PRIORITY);
            
            Runnable task = () -> {
                for (int i = 0; i < 5; i++) {
                    System.out.println(Thread.currentThread().getName() + " 优先级: " + 
                            Thread.currentThread().getPriority() + " 执行中, i = " + i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            
            Thread lowPriorityThread = new Thread(task, "低优先级线程");
            Thread normalPriorityThread = new Thread(task, "普通优先级线程");
            Thread highPriorityThread = new Thread(task, "高优先级线程");
            
            lowPriorityThread.setPriority(Thread.MIN_PRIORITY); // 1
            normalPriorityThread.setPriority(Thread.NORM_PRIORITY); // 5
            highPriorityThread.setPriority(Thread.MAX_PRIORITY); // 10
            
            lowPriorityThread.start();
            normalPriorityThread.start();
            highPriorityThread.start();
            
            try {
                lowPriorityThread.join();
                normalPriorityThread.join();
                highPriorityThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 3. 线程的中断
    static class ThreadInterruptDemo {
        public static void test() {
            System.out.println("3. 线程的中断:");
            
            // 示例1: 通过中断标志结束线程
            Thread interruptThread1 = new Thread(() -> {
                int count = 0;
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("线程1执行中, count = " + count++);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        System.out.println("线程1被中断, 退出循环");
                        Thread.currentThread().interrupt(); // 重新设置中断标志
                        break;
                    }
                }
            }, "InterruptThread1");
            
            interruptThread1.start();
            
            try {
                Thread.sleep(1000);
                System.out.println("主线程: 中断线程1");
                interruptThread1.interrupt();
                interruptThread1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // 示例2: 响应中断
            Thread interruptThread2 = new Thread(() -> {
                System.out.println("线程2开始执行");
                try {
                    Thread.sleep(2000); // 睡眠2秒
                } catch (InterruptedException e) {
                    System.out.println("线程2在睡眠时被中断");
                }
                System.out.println("线程2结束执行");
            }, "InterruptThread2");
            
            interruptThread2.start();
            
            try {
                Thread.sleep(500);
                System.out.println("主线程: 中断线程2");
                interruptThread2.interrupt();
                interruptThread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 4. 线程的等待和通知
    static class ThreadWaitNotifyDemo {
        private static final Object lock = new Object();
        private static boolean ready = false;
        
        public static void test() {
            System.out.println("4. 线程的等待和通知:");
            
            // 等待线程
            Thread waiterThread = new Thread(() -> {
                synchronized (lock) {
                    while (!ready) {
                        try {
                            System.out.println("等待线程: 等待条件满足");
                            lock.wait(); // 等待通知
                            System.out.println("等待线程: 收到通知");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("等待线程: 条件满足，继续执行");
                }
            }, "WaiterThread");
            
            // 通知线程
            Thread notifierThread = new Thread(() -> {
                try {
                    Thread.sleep(1000); // 模拟准备工作
                    synchronized (lock) {
                        System.out.println("通知线程: 准备工作完成，设置条件为true");
                        ready = true;
                        lock.notify(); // 通知等待的线程
                        // lock.notifyAll(); // 通知所有等待的线程
                        System.out.println("通知线程: 发送通知完成");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "NotifierThread");
            
            waiterThread.start();
            notifierThread.start();
            
            try {
                waiterThread.join();
                notifierThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 5. 线程的睡眠和加入
    static class ThreadSleepJoinDemo {
        public static void test() {
            System.out.println("5. 线程的睡眠和加入:");
            
            Thread joinThread1 = new Thread(() -> {
                System.out.println("线程1开始执行");
                for (int i = 0; i < 3; i++) {
                    System.out.println("线程1执行中, i = " + i);
                    try {
                        Thread.sleep(300); // 睡眠300ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("线程1执行完毕");
            }, "JoinThread1");
            
            Thread joinThread2 = new Thread(() -> {
                System.out.println("线程2开始执行");
                try {
                    System.out.println("线程2: 等待线程1执行完毕");
                    joinThread1.join(); // 等待线程1结束
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 3; i++) {
                    System.out.println("线程2执行中, i = " + i);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("线程2执行完毕");
            }, "JoinThread2");
            
            joinThread1.start();
            Thread.sleep(100); // 确保线程1先启动
            joinThread2.start();
            
            try {
                joinThread2.join(); // 等待线程2结束
                System.out.println("主线程: 所有子线程执行完毕");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 6. 守护线程
    static class DaemonThreadDemo {
        public static void test() {
            System.out.println("6. 守护线程:");
            
            // 创建守护线程
            Thread daemonThread = new Thread(() -> {
                while (true) {
                    System.out.println("守护线程执行中...");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "DaemonThread");
            
            daemonThread.setDaemon(true); // 设置为守护线程
            daemonThread.start();
            
            // 创建用户线程
            Thread userThread = new Thread(() -> {
                for (int i = 0; i < 3; i++) {
                    System.out.println("用户线程执行中, i = " + i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("用户线程执行完毕");
            }, "UserThread");
            
            userThread.start();
            
            try {
                userThread.join(); // 等待用户线程结束
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            System.out.println("主线程执行完毕");
            // 当所有用户线程结束，守护线程会自动终止
        }
    }
    
    public static void main(String[] args) {
        ThreadStateDemo.test(); // 测试线程状态
        ThreadPriorityDemo.test(); // 测试线程优先级
        ThreadInterruptDemo.test(); // 测试线程中断
        ThreadWaitNotifyDemo.test(); // 测试线程等待和通知
        ThreadSleepJoinDemo.test(); // 测试线程睡眠和加入
        DaemonThreadDemo.test(); // 测试守护线程
    }
}