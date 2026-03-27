package com.linsir.abc.core.jvm.gc.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * 引用类型对比示例
 * 
 * 演示四种引用类型（强引用、软引用、弱引用、虚引用）的区别和特性
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class ReferenceTypeComparison {

    /**
     * 1MB的大小常量
     */
    private static final int ONE_MB = 1024 * 1024;

    /**
     * 对比四种引用类型的基本特性
     */
    public void compareReferenceTypes() {
        System.out.println("=== 四种引用类型对比 ===\n");

        // 创建引用队列（用于虚引用）
        ReferenceQueue<Object> queue = new ReferenceQueue<>();

        // 1. 强引用
        Object strongRef = new Object();
        System.out.println("1. 强引用 (Strong Reference)");
        System.out.println("   对象: " + strongRef);
        System.out.println("   特性: 永远不会被回收");
        System.out.println("   get()方法: 可以获取对象");

        // 2. 软引用
        SoftReference<Object> softRef = new SoftReference<>(new Object());
        System.out.println("\n2. 软引用 (Soft Reference)");
        System.out.println("   对象: " + softRef.get());
        System.out.println("   特性: 内存不足时回收");
        System.out.println("   get()方法: 可以获取对象");

        // 3. 弱引用
        WeakReference<Object> weakRef = new WeakReference<>(new Object());
        System.out.println("\n3. 弱引用 (Weak Reference)");
        System.out.println("   对象: " + weakRef.get());
        System.out.println("   特性: 下次GC时回收");
        System.out.println("   get()方法: 可以获取对象");

        // 4. 虚引用
        PhantomReference<Object> phantomRef = new PhantomReference<>(new Object(), queue);
        System.out.println("\n4. 虚引用 (Phantom Reference)");
        System.out.println("   对象: " + phantomRef.get());
        System.out.println("   特性: 随时可能回收，用于跟踪回收状态");
        System.out.println("   get()方法: 永远返回null");

        System.out.println("\n" + "=".repeat(50));
    }

    /**
     * 对比四种引用类型在GC后的行为
     */
    public void compareGcBehavior() {
        System.out.println("\n=== GC后行为对比 ===\n");

        ReferenceQueue<Object> queue = new ReferenceQueue<>();

        // 创建各种引用
        Object strongObj = new Object();
        SoftReference<Object> softRef = new SoftReference<>(new Object());
        WeakReference<Object> weakRef = new WeakReference<>(new Object());
        PhantomReference<Object> phantomRef = new PhantomReference<>(new Object(), queue);

        System.out.println("GC前:");
        System.out.println("  强引用: " + (strongObj != null ? "存活" : "null"));
        System.out.println("  软引用: " + (softRef.get() != null ? "存活" : "null"));
        System.out.println("  弱引用: " + (weakRef.get() != null ? "存活" : "null"));
        System.out.println("  虚引用: " + (phantomRef.get() != null ? "存活" : "null"));

        // 调用GC
        System.out.println("\n调用System.gc()...");
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nGC后（内存充足）:");
        System.out.println("  强引用: " + (strongObj != null ? "存活" : "null"));
        System.out.println("  软引用: " + (softRef.get() != null ? "存活" : "null"));
        System.out.println("  弱引用: " + (weakRef.get() != null ? "存活" : "null"));
        System.out.println("  虚引用: " + (phantomRef.get() != null ? "存活" : "null"));

        System.out.println("\n引用队列状态: " + (queue.poll() != null ? "有通知" : "无通知"));

        System.out.println("\n" + "=".repeat(50));
    }

    /**
     * 对比引用类型的使用场景
     */
    public void compareUseCases() {
        System.out.println("\n=== 使用场景对比 ===\n");

        System.out.println("┌──────────┬────────────────────┬─────────────────────────────┐");
        System.out.println("│ 引用类型  │ 回收时机            │ 典型使用场景                 │");
        System.out.println("├──────────┼────────────────────┼─────────────────────────────┤");
        System.out.println("│ 强引用    │ 永不回收            │ 普通对象引用                 │");
        System.out.println("├──────────┼────────────────────┼─────────────────────────────┤");
        System.out.println("│ 软引用    │ 内存不足时回收       │ 缓存实现（图片缓存、网页缓存）│");
        System.out.println("├──────────┼────────────────────┼─────────────────────────────┤");
        System.out.println("│ 弱引用    │ 下次GC时回收         │ WeakHashMap、规范化映射      │");
        System.out.println("├──────────┼────────────────────┼─────────────────────────────┤");
        System.out.println("│ 虚引用    │ 随时可能回收         │ 跟踪对象回收、资源清理       │");
        System.out.println("└──────────┴────────────────────┴─────────────────────────────┘");

        System.out.println("\n" + "=".repeat(50));
    }

    /**
     * 演示内存压力下不同引用类型的回收顺序
     * 
     * VM参数: -Xms20m -Xmx20m
     */
    public void demonstrateMemoryPressure() {
        System.out.println("\n=== 内存压力下回收顺序演示 ===\n");
        System.out.println("注意: 此演示需要在VM参数 -Xms20m -Xmx20m 下运行\n");

        // 创建引用
        SoftReference<byte[]> softRef = new SoftReference<>(new byte[4 * ONE_MB]);
        WeakReference<byte[]> weakRef = new WeakReference<>(new byte[2 * ONE_MB]);

        System.out.println("初始状态:");
        System.out.println("  软引用对象: " + (softRef.get() != null ? "存活 (4MB)" : "null"));
        System.out.println("  弱引用对象: " + (weakRef.get() != null ? "存活 (2MB)" : "null"));

        // 分配内存产生压力
        System.out.println("\n分配内存产生压力...");
        try {
            for (int i = 0; i < 10; i++) {
                byte[] allocation = new byte[ONE_MB];
                System.out.println("  分配 " + (i + 1) + "MB");

                // 检查引用状态
                boolean softAlive = softRef.get() != null;
                boolean weakAlive = weakRef.get() != null;

                if (!softAlive || !weakAlive) {
                    System.out.println("\n引用状态变化:");
                    System.out.println("  软引用: " + (softAlive ? "存活" : "已被回收"));
                    System.out.println("  弱引用: " + (weakAlive ? "存活" : "已被回收"));
                    break;
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("\n发生OutOfMemoryError");
            System.out.println("  软引用: " + (softRef.get() != null ? "存活" : "已被回收"));
            System.out.println("  弱引用: " + (weakRef.get() != null ? "存活" : "已被回收"));
        }
    }

    /**
     * 主方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        ReferenceTypeComparison comparison = new ReferenceTypeComparison();

        // 对比引用类型基本特性
        comparison.compareReferenceTypes();

        // 对比GC后行为
        comparison.compareGcBehavior();

        // 对比使用场景
        comparison.compareUseCases();

        // 演示内存压力下的回收顺序
        comparison.demonstrateMemoryPressure();
    }
}
