package com.linsir.abc.core.jvm.jmm.happensbefore;

/**
 * happens-before规则示例
 * 
 * 演示Java内存模型中的8条happens-before规则：
 * 1. 程序次序规则
 * 2. 监视器锁规则
 * 3. volatile变量规则
 * 4. 线程启动规则
 * 5. 线程终止规则
 * 6. 线程中断规则
 * 7. 对象终结规则
 * 8. 传递性
 * 
 * happens-before是判断数据是否存在竞争、线程是否安全的主要依据
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class HappensBeforeRules {
    
    /**
     * 规则1：程序次序规则（Program Order Rule）
     * 在一个线程内，按照程序代码顺序，前面的操作happens-before于后面的操作
     */
    public static void programOrderRule() {
        System.out.println("=== Rule 1: Program Order Rule ===");
        
        int a = 1;      // 操作A
        int b = 2;      // 操作B
        int c = a + b;  // 操作C
        
        // A happens-before B（单线程内）
        // B happens-before C（单线程内）
        // 因此 A happens-before C（传递性）
        
        System.out.println("a = " + a + ", b = " + b + ", c = " + c);
        System.out.println("In single thread, operations follow program order.\n");
    }
    
    /**
     * 规则2：监视器锁规则（Monitor Lock Rule）
     * 对一个锁的unlock操作happens-before于后续对这个锁的lock操作
     */
    private static final Object monitor = new Object();
    private static int sharedValue = 0;
    
    public static void monitorLockRule() throws InterruptedException {
        System.out.println("=== Rule 2: Monitor Lock Rule ===");
        
        Thread writer = new Thread(() -> {
            synchronized (monitor) {
                sharedValue = 42;     // 操作A
                System.out.println("Writer: set value to 42");
            }                         // unlock happens-before后续lock
        });
        
        Thread reader = new Thread(() -> {
            synchronized (monitor) {
                // 操作B能看到A的结果（A happens-before B）
                System.out.println("Reader: read value = " + sharedValue);
            }
        });
        
        writer.start();
        writer.join();  // 确保writer先执行
        reader.start();
        reader.join();
        System.out.println();
    }
    
    /**
     * 规则3：volatile变量规则（Volatile Variable Rule）
     * 对一个volatile变量的写操作happens-before于后续对这个变量的读操作
     */
    private static volatile boolean flag = false;
    private static int volatileValue = 0;
    
    public static void volatileVariableRule() throws InterruptedException {
        System.out.println("=== Rule 3: Volatile Variable Rule ===");
        
        Thread writer = new Thread(() -> {
            volatileValue = 100;      // 操作A
            flag = true;              // 操作B（volatile写）
            System.out.println("Writer: set value to 100, flag to true");
        });
        
        Thread reader = new Thread(() -> {
            while (!flag) {
                // 等待flag变为true
            }
            // 操作C（volatile读）能看到B的结果
            // B happens-before C，因此A也happens-before C（传递性）
            System.out.println("Reader: flag is true, value = " + volatileValue);
        });
        
        reader.start();
        Thread.sleep(100);  // 确保reader先开始等待
        writer.start();
        
        writer.join();
        reader.join();
        System.out.println();
    }
    
    /**
     * 规则4：线程启动规则（Thread Start Rule）
     * Thread对象的start()方法happens-before于此线程的每一个动作
     */
    private static int startRuleValue = 0;
    
    public static void threadStartRule() throws InterruptedException {
        System.out.println("=== Rule 4: Thread Start Rule ===");
        
        startRuleValue = 50;  // 操作A
        
        Thread thread = new Thread(() -> {
            // 操作B能看到startRuleValue=50（A happens-before B）
            System.out.println("Child thread: value = " + startRuleValue);
        });
        
        // start() happens-before线程内所有操作
        thread.start();
        thread.join();
        System.out.println();
    }
    
    /**
     * 规则5：线程终止规则（Thread Termination Rule）
     * 线程中的所有操作都happens-before于对此线程的终止检测
     */
    private static int terminationRuleValue = 0;
    
    public static void threadTerminationRule() throws InterruptedException {
        System.out.println("=== Rule 5: Thread Termination Rule ===");
        
        Thread worker = new Thread(() -> {
            terminationRuleValue = 200;  // 操作A
            System.out.println("Worker thread: set value to 200");
        });
        
        worker.start();
        worker.join();  // 等待线程结束
        
        // A happens-before join返回，terminationRuleValue一定等于200
        System.out.println("Main thread after join: value = " + terminationRuleValue);
        System.out.println();
    }
    
    /**
     * 规则6：线程中断规则（Thread Interruption Rule）
     * 对线程interrupt()方法的调用happens-before于被中断线程检测到中断事件
     */
    private static volatile boolean interruptedDetected = false;
    
    public static void threadInterruptionRule() throws InterruptedException {
        System.out.println("=== Rule 6: Thread Interruption Rule ===");
        
        Thread worker = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                // 正常工作
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // 检测到中断
                    interruptedDetected = true;
                    System.out.println("Worker: Interrupted detected!");
                    break;
                }
            }
        });
        
        worker.start();
        Thread.sleep(100);
        worker.interrupt();  // 操作A
        // interrupt() happens-before被中断线程检测到中断事件
        worker.join();
        System.out.println("Main: Interrupted detected = " + interruptedDetected);
        System.out.println();
    }
    
    /**
     * 规则7：对象终结规则（Finalizer Rule）
     * 一个对象的初始化完成happens-before于它的finalize()方法的开始
     */
    public static class FinalizableObject {
        private final int value;
        
        public FinalizableObject(int value) {
            this.value = value;  // 初始化完成
            System.out.println("Object created with value = " + value);
        }
        
        @Override
        protected void finalize() throws Throwable {
            // 能看到value=42（初始化happens-before finalize）
            System.out.println("Finalize: value = " + value);
            super.finalize();
        }
    }
    
    public static void finalizerRule() {
        System.out.println("=== Rule 7: Finalizer Rule ===");
        
        FinalizableObject obj = new FinalizableObject(42);
        obj = null;  // 取消引用
        
        System.gc();  // 建议垃圾回收
        System.out.println("Object is eligible for garbage collection.");
        System.out.println();
    }
    
    /**
     * 规则8：传递性（Transitivity）
     * 如果A happens-before B，且B happens-before C，那么A happens-before C
     */
    private static volatile int transitivityFlag = 0;
    private static int transitivityValue = 0;
    
    public static void transitivityRule() throws InterruptedException {
        System.out.println("=== Rule 8: Transitivity ===");
        
        Thread writer = new Thread(() -> {
            transitivityValue = 300;  // 操作A
            transitivityFlag = 1;     // 操作B（volatile写）
            // 程序次序规则：A happens-before B
        });
        
        Thread reader = new Thread(() -> {
            if (transitivityFlag == 1) {  // 操作C（volatile读）
                // volatile变量规则：B happens-before C
                // 传递性：A happens-before C
                System.out.println("Reader: flag = 1, value = " + transitivityValue);
            }
        });
        
        reader.start();
        Thread.sleep(50);
        writer.start();
        
        writer.join();
        reader.join();
        System.out.println();
    }
    
    /**
     * 主方法：运行所有happens-before规则演示
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Happens-Before Rules Demo ===\n");
        
        programOrderRule();
        monitorLockRule();
        volatileVariableRule();
        threadStartRule();
        threadTerminationRule();
        threadInterruptionRule();
        finalizerRule();
        transitivityRule();
        
        System.out.println("=== All rules demonstrated ===");
    }
}
