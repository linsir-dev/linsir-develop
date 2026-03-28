package com.linsir.abc.core.jvm.jmm.volatileexample;

/**
 * volatile读写锁的读操作示例
 * 
 * 演示volatile在读多写少场景下的应用：
 * - 读操作：使用volatile，无需加锁，性能高
 * - 写操作：使用synchronized，保证原子性
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class ReadWriteCounter {
    
    /**
     * volatile修饰的计数器值
     * 保证读操作的可见性，无需加锁
     */
    private volatile int value = 0;
    
    /**
     * 读操作：无需加锁，直接返回volatile变量的值
     * 利用volatile的可见性保证，始终读取最新值
     * 
     * @return 当前计数值
     */
    public int getValue() {
        return value;
    }
    
    /**
     * 写操作：使用synchronized保证原子性
     * volatile不能保证复合操作的原子性
     */
    public synchronized void increment() {
        value++;
    }
    
    /**
     * 写操作：使用synchronized保证原子性
     * @param delta 增加的值
     */
    public synchronized void add(int delta) {
        value += delta;
    }
    
    /**
     * 写操作：使用synchronized保证原子性
     * @param newValue 新值
     */
    public synchronized void setValue(int newValue) {
        value = newValue;
    }
    
    /**
     * 演示读多写少场景下的性能优势
     */
    public static void main(String[] args) throws InterruptedException {
        ReadWriteCounter counter = new ReadWriteCounter();
        
        // 写线程
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Writer");
        
        // 多个读线程
        Thread[] readers = new Thread[10];
        for (int i = 0; i < readers.length; i++) {
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    int value = counter.getValue();
                    System.out.println(Thread.currentThread().getName() + 
                        " read value: " + value);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }, "Reader-" + i);
        }
        
        // 启动所有线程
        writer.start();
        for (Thread reader : readers) {
            reader.start();
        }
        
        // 等待完成
        writer.join();
        for (Thread reader : readers) {
            reader.join();
        }
        
        System.out.println("Final value: " + counter.getValue());
    }
}
