package com.linsir.abc.core.jvm.gc.reference;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 弱引用示例
 * 
 * 弱引用也是用来描述非必需对象的，但是它的强度比软引用更弱一些，
 * 被弱引用关联的对象只能生存到下一次垃圾收集发生之前。
 * 当垃圾收集器工作时，无论当前内存是否足够，都会回收掉只被弱引用关联的对象。
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class WeakReferenceExample {

    /**
     * 演示弱引用的基本用法
     */
    public void demonstrateBasicUsage() {
        System.out.println("=== 弱引用基本用法演示 ===");

        // 创建弱引用
        WeakReference<Object> weakRef = new WeakReference<>(new Object());

        // 获取弱引用对象
        Object obj = weakRef.get();
        System.out.println("GC前获取对象: " + obj);

        // 建议GC（不保证立即执行）
        System.out.println("调用System.gc()...");
        System.gc();

        // 等待GC执行
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 再次获取，可能已被回收
        Object objAfterGc = weakRef.get();
        System.out.println("GC后获取对象: " + objAfterGc);
        System.out.println("对象" + (objAfterGc == null ? "已被回收" : "未被回收"));
        System.out.println();
    }

    /**
     * 演示弱引用与强引用的区别
     */
    public void demonstrateDifferenceWithStrongRef() {
        System.out.println("=== 弱引用与强引用区别演示 ===");

        // 强引用
        Object strongObj = new Object();
        WeakReference<Object> weakRefOfStrong = new WeakReference<>(strongObj);

        // 弱引用（无强引用指向）
        WeakReference<Object> weakRefOnly = new WeakReference<>(new Object());

        System.out.println("GC前:");
        System.out.println("  强引用对象: " + strongObj);
        System.out.println("  弱引用(有强引用): " + weakRefOfStrong.get());
        System.out.println("  弱引用(无强引用): " + weakRefOnly.get());

        // 调用GC
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nGC后:");
        System.out.println("  强引用对象: " + strongObj);
        System.out.println("  弱引用(有强引用): " + weakRefOfStrong.get());
        System.out.println("  弱引用(无强引用): " + weakRefOnly.get());

        // 断开强引用
        strongObj = null;
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n断开强引用后GC:");
        System.out.println("  弱引用(原强引用): " + weakRefOfStrong.get());
        System.out.println();
    }

    /**
     * 演示WeakHashMap的使用
     */
    public void demonstrateWeakHashMap() {
        System.out.println("=== WeakHashMap演示 ===");

        // 普通HashMap
        Map<Object, String> hashMap = new HashMap<>();
        // WeakHashMap
        Map<Object, String> weakHashMap = new WeakHashMap<>();

        // 创建键对象
        Object key1 = new Object();
        Object key2 = new Object();

        // 放入两个Map
        hashMap.put(key1, "HashMap Value");
        weakHashMap.put(key2, "WeakHashMap Value");

        System.out.println("GC前:");
        System.out.println("  HashMap大小: " + hashMap.size());
        System.out.println("  WeakHashMap大小: " + weakHashMap.size());

        // 断开强引用
        key1 = null;
        key2 = null;

        // 调用GC
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n断开强引用并GC后:");
        System.out.println("  HashMap大小: " + hashMap.size());
        System.out.println("  WeakHashMap大小: " + weakHashMap.size());
        System.out.println("  WeakHashMap条目: " + weakHashMap.entrySet());
        System.out.println();
    }

    /**
     * 演示弱引用作为规范化映射的应用场景
     */
    public void demonstrateCanonicalMapping() {
        System.out.println("=== 规范化映射应用场景演示 ===");

        // 使用WeakHashMap实现规范化映射
        CanonicalObjectPool pool = new CanonicalObjectPool();

        // 获取规范化对象
        String key1 = new String("shared_key");
        String key2 = new String("shared_key");

        Object canonical1 = pool.getCanonicalObject(key1);
        Object canonical2 = pool.getCanonicalObject(key2);

        System.out.println("key1 == key2: " + (key1 == key2));
        System.out.println("canonical1 == canonical2: " + (canonical1 == canonical2));
        System.out.println("规范化对象池大小: " + pool.size());

        // 断开所有外部引用
        key1 = null;
        key2 = null;
        canonical1 = null;
        canonical2 = null;

        // 调用GC
        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n断开所有引用并GC后:");
        System.out.println("规范化对象池大小: " + pool.size());
    }

    /**
     * 规范化对象池 - 使用WeakHashMap实现
     */
    private static class CanonicalObjectPool {
        private Map<String, Object> pool = new WeakHashMap<>();

        /**
         * 获取规范化对象
         * 
         * @param key 键
         * @return 规范化对象
         */
        public synchronized Object getCanonicalObject(String key) {
            Object obj = pool.get(key);
            if (obj == null) {
                obj = new Object();
                pool.put(key, obj);
                System.out.println("创建新对象，键: " + key);
            } else {
                System.out.println("复用已有对象，键: " + key);
            }
            return obj;
        }

        /**
         * 获取池中对象数量
         * 
         * @return 对象数量
         */
        public int size() {
            return pool.size();
        }
    }

    /**
     * 主方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        WeakReferenceExample example = new WeakReferenceExample();

        // 演示基本用法
        example.demonstrateBasicUsage();

        // 演示与强引用的区别
        example.demonstrateDifferenceWithStrongRef();

        // 演示WeakHashMap
        example.demonstrateWeakHashMap();

        // 演示规范化映射
        example.demonstrateCanonicalMapping();
    }
}
