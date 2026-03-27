package com.linsir.abc.core.jvm.memory.stack;

/**
 * 虚拟机栈内存溢出(OutOfMemoryError)
 * 
 * <p>当栈扩展时无法申请到足够的内存，或者创建线程时无法申请到足够的内存来创建栈，
 * 将抛出OutOfMemoryError。注意：这与StackOverflowError不同。</p>
 * 
 * <p><strong>典型场景:</strong> 无限创建线程，每个线程分配一定栈空间</p>
 * 
 * <p><strong>VM参数:</strong> -Xss2m (给每个线程分配较大的栈空间以便快速触发OOM)</p>
 * 
 * <p><strong>预期异常:</strong> java.lang.OutOfMemoryError: unable to create new native thread</p>
 * 
 * <p><strong>注意:</strong> 在Windows平台运行此代码可能导致系统假死，请谨慎运行</p>
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class StackOutOfMemory {

    /**
     * 线程计数器
     * 用于记录创建的线程数量
     */
    private int threadCount = 0;

    /**
     * 死循环方法，用于占用线程
     * 线程创建后会一直运行此方法，不会退出
     */
    private void dontStop() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 通过创建大量线程来触发栈内存溢出
     * 
     * <p>不断创建新线程，每个线程都会分配独立的栈空间。
     * 当系统无法为更多线程分配栈空间时，抛出OOM。</p>
     */
    public void stackLeakByThread() {
        while (true) {
            Thread thread = new Thread(() -> {
                threadCount++;
                System.out.println("创建第 " + threadCount + " 个线程");
                dontStop();
            });
            thread.start();
        }
    }

    /**
     * 程序入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        StackOutOfMemory demo = new StackOutOfMemory();
        
        System.out.println("开始创建线程，准备触发栈内存溢出...");
        System.out.println("VM参数: -Xss2m");
        System.out.println("警告: 此操作可能导致系统不稳定，请谨慎运行!");
        
        try {
            demo.stackLeakByThread();
        } catch (OutOfMemoryError e) {
            System.err.println("捕获到OutOfMemoryError: " + e.getMessage());
            System.err.println("已创建线程数量: " + demo.threadCount);
            throw e;
        }
    }
}
