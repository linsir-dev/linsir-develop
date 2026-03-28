package com.linsir.abc.core.jvm.jmm.volatileexample;

/**
 * volatile状态标志位示例
 * 
 * 演示volatile作为状态标志位的正确使用场景
 * volatile保证状态变更对所有线程立即可见
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class VolatileFlag {
    
    /**
     * volatile修饰的状态标志位
     * 保证running变量的可见性，任何线程修改后其他线程立即可见
     */
    private volatile boolean running = true;
    
    /**
     * 停止标志位
     * 调用后running变为false，工作线程会立即看到变更并停止
     */
    public void stop() {
        running = false;
    }
    
    /**
     * 执行工作任务
     * 循环检查running标志位，当running为false时退出循环
     */
    public void doWork() {
        while (running) {
            // 执行任务
            System.out.println(Thread.currentThread().getName() + " is working...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println(Thread.currentThread().getName() + " stopped.");
    }
    
    /**
     * 获取当前运行状态
     * @return 是否正在运行
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * 演示volatile状态标志位的使用
     */
    public static void main(String[] args) throws InterruptedException {
        VolatileFlag flag = new VolatileFlag();
        
        // 启动工作线程
        Thread worker = new Thread(flag::doWork, "Worker-Thread");
        worker.start();
        
        // 主线程等待500ms后停止工作线程
        Thread.sleep(500);
        System.out.println("Main thread calling stop...");
        flag.stop();
        
        // 等待工作线程结束
        worker.join();
        System.out.println("Main thread finished.");
    }
}
