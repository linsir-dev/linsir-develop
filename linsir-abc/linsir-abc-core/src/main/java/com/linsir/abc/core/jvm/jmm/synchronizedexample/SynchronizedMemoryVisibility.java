package com.linsir.abc.core.jvm.jmm.synchronizedexample;

/**
 * synchronized内存可见性示例
 * 
 * 演示synchronized如何保证内存可见性：
 * - 进入synchronized块：清空工作内存，从主内存重新读取变量值
 * - 退出synchronized块：将工作内存中的变量值刷新到主内存
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class SynchronizedMemoryVisibility {
    
    /**
     * 共享变量（非volatile）
     * 通过synchronized保证可见性
     */
    private int sharedValue = 0;
    
    /**
     * 写操作：在synchronized块内修改共享变量
     * 退出synchronized块时会将值刷新到主内存
     * 
     * @param value 要写入的值
     */
    public synchronized void write(int value) {
        sharedValue = value;
        System.out.println(Thread.currentThread().getName() + " wrote: " + value);
    }
    
    /**
     * 读操作：在synchronized块内读取共享变量
     * 进入synchronized块时会从主内存重新读取
     * 
     * @return 当前值
     */
    public synchronized int read() {
        int value = sharedValue;
        System.out.println(Thread.currentThread().getName() + " read: " + value);
        return value;
    }
    
    /**
     * 获取共享变量（无同步，可能读取到过期值）
     * 仅用于演示对比
     * 
     * @return 当前值（可能过期）
     */
    public int readUnsynchronized() {
        return sharedValue;
    }
    
    /**
     * 演示synchronized的内存可见性保证
     */
    public static void main(String[] args) throws InterruptedException {
        SynchronizedMemoryVisibility demo = new SynchronizedMemoryVisibility();
        
        // 写线程
        Thread writer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                demo.write(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Writer");
        
        // 读线程（使用同步读）
        Thread reader = new Thread(() -> {
            int lastValue = 0;
            while (lastValue < 5) {
                int currentValue = demo.read();
                if (currentValue != lastValue) {
                    lastValue = currentValue;
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Reader");
        
        System.out.println("=== Synchronized Memory Visibility Test ===");
        writer.start();
        reader.start();
        
        writer.join();
        reader.join();
        
        System.out.println("\nFinal value: " + demo.read());
    }
}
