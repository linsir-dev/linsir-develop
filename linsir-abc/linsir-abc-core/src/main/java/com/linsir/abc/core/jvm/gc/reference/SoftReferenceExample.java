package com.linsir.abc.core.jvm.gc.reference;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 软引用示例
 * 
 * 软引用是用来描述一些还有用但并非必需的对象。
 * 对于软引用关联着的对象，在系统将要发生内存溢出异常之前，
 * 将会把这些对象列进回收范围之中进行第二次回收。
 * 如果这次回收还没有足够的内存，才会抛出内存溢出异常。
 * 
 * VM参数: -Xms20m -Xmx20m -XX:+PrintGCDetails
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class SoftReferenceExample {

    /**
     * 1MB的大小常量
     */
    private static final int ONE_MB = 1024 * 1024;

    /**
     * 软引用缓存列表
     */
    private List<SoftReference<byte[]>> cache = new ArrayList<>();

    /**
     * 演示软引用的基本用法
     */
    public void demonstrateBasicUsage() {
        System.out.println("=== 软引用基本用法演示 ===");

        // 创建强引用
        Object strongRef = new Object();
        System.out.println("强引用对象: " + strongRef);

        // 创建软引用
        SoftReference<Object> softRef = new SoftReference<>(strongRef);
        System.out.println("软引用获取对象: " + softRef.get());

        // 断开强引用
        strongRef = null;
        System.out.println("断开强引用后，软引用获取对象: " + softRef.get());

        // 在内存不足时，软引用对象会被回收
        System.out.println("软引用对象在内存不足时会被自动回收");
        System.out.println();
    }

    /**
     * 演示软引用在内存不足时被回收
     */
    public void demonstrateGcUnderMemoryPressure() {
        System.out.println("=== 内存压力下软引用回收演示 ===");
        System.out.println("最大堆内存: " + Runtime.getRuntime().maxMemory() / ONE_MB + "MB");

        // 创建软引用缓存
        for (int i = 0; i < 20; i++) {
            byte[] data = new byte[ONE_MB]; // 1MB的数据
            SoftReference<byte[]> softRef = new SoftReference<>(data);
            cache.add(softRef);
            System.out.println("添加第 " + (i + 1) + " 个软引用对象");

            // 检查已缓存的对象是否被回收
            int aliveCount = 0;
            for (SoftReference<byte[]> ref : cache) {
                if (ref.get() != null) {
                    aliveCount++;
                }
            }
            System.out.println("  当前存活对象数: " + aliveCount + "/" + cache.size());
        }

        System.out.println("\n最终存活对象统计:");
        int finalAliveCount = 0;
        for (int i = 0; i < cache.size(); i++) {
            byte[] data = cache.get(i).get();
            if (data != null) {
                finalAliveCount++;
                System.out.println("  对象 " + (i + 1) + ": 存活 (" + data.length / ONE_MB + "MB)");
            } else {
                System.out.println("  对象 " + (i + 1) + ": 已被回收");
            }
        }
        System.out.println("总计: " + finalAliveCount + "/" + cache.size() + " 个对象存活");
    }

    /**
     * 演示软引用作为缓存的应用场景
     */
    public void demonstrateCacheScenario() {
        System.out.println("\n=== 软引用缓存应用场景演示 ===");

        // 模拟图片缓存
        ImageCache imageCache = new ImageCache();

        // 加载图片
        imageCache.loadImage("image1.jpg", new byte[2 * ONE_MB]);
        imageCache.loadImage("image2.jpg", new byte[2 * ONE_MB]);
        imageCache.loadImage("image3.jpg", new byte[2 * ONE_MB]);

        System.out.println("已缓存图片数量: " + imageCache.getCachedCount());

        // 获取图片
        byte[] image1 = imageCache.getImage("image1.jpg");
        System.out.println("获取 image1.jpg: " + (image1 != null ? "命中" : "未命中（已被回收）"));

        // 模拟内存压力
        System.out.println("\n模拟内存压力...");
        List<byte[]> memoryPressure = new ArrayList<>();
        try {
            for (int i = 0; i < 15; i++) {
                memoryPressure.add(new byte[ONE_MB]);
            }
        } catch (OutOfMemoryError e) {
            System.out.println("内存不足异常");
        }

        // 再次检查缓存
        System.out.println("\n内存压力后缓存状态:");
        System.out.println("缓存图片数量: " + imageCache.getCachedCount());

        byte[] image1After = imageCache.getImage("image1.jpg");
        System.out.println("获取 image1.jpg: " + (image1After != null ? "命中" : "未命中（已被回收）"));
    }

    /**
     * 图片缓存类 - 使用软引用实现
     */
    private static class ImageCache {
        private java.util.Map<String, SoftReference<byte[]>> cache = new java.util.HashMap<>();

        /**
         * 加载图片到缓存
         * 
         * @param name 图片名称
         * @param data 图片数据
         */
        public void loadImage(String name, byte[] data) {
            cache.put(name, new SoftReference<>(data));
            System.out.println("加载图片: " + name + " (" + data.length / ONE_MB + "MB)");
        }

        /**
         * 从缓存获取图片
         * 
         * @param name 图片名称
         * @return 图片数据，如果已被回收则返回null
         */
        public byte[] getImage(String name) {
            SoftReference<byte[]> ref = cache.get(name);
            if (ref != null) {
                return ref.get();
            }
            return null;
        }

        /**
         * 获取缓存中存活的对象数量
         * 
         * @return 存活对象数量
         */
        public int getCachedCount() {
            int count = 0;
            for (SoftReference<byte[]> ref : cache.values()) {
                if (ref.get() != null) {
                    count++;
                }
            }
            return count;
        }
    }

    /**
     * 主方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SoftReferenceExample example = new SoftReferenceExample();

        // 演示基本用法
        example.demonstrateBasicUsage();

        // 演示内存压力下回收
        example.demonstrateGcUnderMemoryPressure();

        // 演示缓存场景
        example.demonstrateCacheScenario();
    }
}
