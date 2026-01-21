package com.linsir.abc.pdai.base.keywords;

/**
 * 演示 finalize 方法的用法
 * 1. Object 类的方法，在垃圾回收器 (GC) 准备释放对象内存之前调用。
 * 2. 已在 JDK 9 中被标记为 Deprecated (过时)，不推荐使用。
 * 3. 缺点：执行时间不确定、性能差、可能导致对象复活。
 */
public class FinalizeDemo {

    // 静态变量用于演示对象复活
    public static FinalizeDemo SAVE_HOOK = null;

    private String name;

    public FinalizeDemo(String name) {
        this.name = name;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("执行 finalize 方法: " + name + " 被回收了");
        // 演示对象复活：在 finalize 中将自己赋值给静态变量，导致无法被回收
        FinalizeDemo.SAVE_HOOK = this;
    }

    public void isAlive() {
        System.out.println("是的, " + name + " 还活着");
    }

    public static void main(String[] args) throws InterruptedException {
        SAVE_HOOK = new FinalizeDemo("DemoObject");

        // 第一次 GC
        System.out.println("--- 第一次 GC ---");
        SAVE_HOOK = null; // 断开引用
        System.gc();      // 提示 JVM 进行 GC
        
        // 暂停 500ms 等待 finalize 执行（注意：finalize 优先级低，不保证一定执行，但在简单 Demo 中通常有效）
        Thread.sleep(500);

        if (SAVE_HOOK != null) {
            SAVE_HOOK.isAlive(); // 应该输出还活着，因为在 finalize 中复活了
        } else {
            System.out.println("DemoObject 已经死了");
        }

        // 第二次 GC
        System.out.println("\n--- 第二次 GC ---");
        SAVE_HOOK = null; // 再次断开引用
        System.gc();
        
        Thread.sleep(500);

        if (SAVE_HOOK != null) {
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("DemoObject 已经死了"); 
            // 应该输出已经死了，因为 finalize 只会被 JVM 调用一次
        }
    }
}
