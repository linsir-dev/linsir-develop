package com.linsir.abc.core.jvm.gc.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

/**
 * 虚引用示例
 * 
 * 虚引用也称为幽灵引用或者幻影引用，它是最弱的一种引用关系。
 * 一个对象是否有虚引用的存在，完全不会对其生存时间构成影响，
 * 也无法通过虚引用来取得一个对象实例。
 * 为一个对象设置虚引用关联的唯一目的就是能在这个对象被收集器回收时收到一个系统通知。
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class PhantomReferenceExample {

    /**
     * 演示虚引用的基本用法
     */
    public void demonstrateBasicUsage() {
        System.out.println("=== 虚引用基本用法演示 ===");

        // 创建引用队列
        ReferenceQueue<Object> queue = new ReferenceQueue<>();

        // 创建对象
        Object obj = new Object();
        System.out.println("原始对象: " + obj);

        // 创建虚引用
        PhantomReference<Object> phantomRef = new PhantomReference<>(obj, queue);

        // 虚引用的get()方法永远返回null
        System.out.println("虚引用get()结果: " + phantomRef.get());
        System.out.println("引用队列poll(): " + queue.poll());

        // 断开强引用
        obj = null;
        System.out.println("\n断开强引用");

        // 建议GC
        System.out.println("调用System.gc()...");
        System.gc();

        // 等待对象被回收
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 检查引用队列
        Reference<?> ref = queue.poll();
        if (ref != null) {
            System.out.println("从引用队列中获取到: " + ref);
            System.out.println("对象已被回收");
        } else {
            System.out.println("引用队列为空，对象可能还未被回收");
        }
        System.out.println();
    }

    /**
     * 演示虚引用用于资源清理
     */
    public void demonstrateResourceCleanup() {
        System.out.println("=== 虚引用资源清理演示 ===");

        // 创建资源管理器
        ResourceManager manager = new ResourceManager();

        // 分配资源
        for (int i = 0; i < 5; i++) {
            manager.allocateResource("resource_" + i);
        }

        System.out.println("已分配资源数量: " + manager.getResourceCount());

        // 释放部分资源引用
        manager.releaseResource("resource_0");
        manager.releaseResource("resource_1");
        manager.releaseResource("resource_2");

        System.out.println("释放部分引用后资源数量: " + manager.getResourceCount());

        // 调用GC
        System.out.println("\n调用System.gc()...");
        System.gc();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 处理引用队列，执行清理
        manager.processReferenceQueue();

        System.out.println("清理后资源数量: " + manager.getResourceCount());
        System.out.println();
    }

    /**
     * 演示虚引用与直接内存回收
     */
    public void demonstrateDirectMemoryCleanup() {
        System.out.println("=== 虚引用与直接内存清理演示 ===");

        // 模拟直接内存分配器
        DirectMemoryAllocator allocator = new DirectMemoryAllocator();

        // 分配直接内存
        DirectMemoryBlock block1 = allocator.allocate(1024);
        DirectMemoryBlock block2 = allocator.allocate(2048);
        DirectMemoryBlock block3 = allocator.allocate(4096);

        System.out.println("已分配直接内存块数量: " + allocator.getBlockCount());
        System.out.println("总分配内存: " + allocator.getTotalAllocated() + " bytes");

        // 释放部分引用
        block1 = null;
        block2 = null;

        System.out.println("\n释放block1和block2引用");
        System.out.println("调用System.gc()...");
        System.gc();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 处理引用队列
        allocator.processReferenceQueue();

        System.out.println("清理后内存块数量: " + allocator.getBlockCount());
        System.out.println("剩余总分配内存: " + allocator.getTotalAllocated() + " bytes");
    }

    /**
     * 资源管理器 - 使用虚引用跟踪资源回收
     */
    private static class ResourceManager {
        private ReferenceQueue<Object> refQueue = new ReferenceQueue<>();
        private Map<PhantomReference<Object>, ResourceInfo> resources = new HashMap<>();

        /**
         * 资源信息
         */
        private static class ResourceInfo {
            String name;
            long allocatedTime;

            ResourceInfo(String name) {
                this.name = name;
                this.allocatedTime = System.currentTimeMillis();
            }
        }

        /**
         * 分配资源
         * 
         * @param name 资源名称
         * @return 资源对象
         */
        public Object allocateResource(String name) {
            Object resource = new Object();
            PhantomReference<Object> phantomRef = new PhantomReference<>(resource, refQueue);
            resources.put(phantomRef, new ResourceInfo(name));
            System.out.println("分配资源: " + name);
            return resource;
        }

        /**
         * 释放资源引用（实际资源由GC回收）
         * 
         * @param name 资源名称
         */
        public void releaseResource(String name) {
            System.out.println("释放资源引用: " + name);
            // 实际资源会在GC后被回收，这里只是断开引用
        }

        /**
         * 处理引用队列，执行清理操作
         */
        public void processReferenceQueue() {
            System.out.println("处理引用队列...");
            Reference<?> ref;
            while ((ref = refQueue.poll()) != null) {
                ResourceInfo info = resources.remove(ref);
                if (info != null) {
                    System.out.println("  清理资源: " + info.name + 
                        " (存活时间: " + (System.currentTimeMillis() - info.allocatedTime) + "ms)");
                }
                ref.clear();
            }
        }

        /**
         * 获取资源数量
         * 
         * @return 资源数量
         */
        public int getResourceCount() {
            return resources.size();
        }
    }

    /**
     * 直接内存块
     */
    private static class DirectMemoryBlock {
        private long address;
        private int size;

        DirectMemoryBlock(long address, int size) {
            this.address = address;
            this.size = size;
        }

        int getSize() {
            return size;
        }
    }

    /**
     * 直接内存分配器 - 模拟使用虚引用管理直接内存
     */
    private static class DirectMemoryAllocator {
        private ReferenceQueue<Object> refQueue = new ReferenceQueue<>();
        private Map<PhantomReference<Object>, DirectMemoryBlock> blocks = new HashMap<>();
        private long nextAddress = 0x1000;

        /**
         * 分配直接内存
         * 
         * @param size 内存大小
         * @return 内存块引用对象
         */
        public DirectMemoryBlock allocate(int size) {
            // 创建一个对象作为引用持有者
            Object holder = new Object();
            DirectMemoryBlock block = new DirectMemoryBlock(nextAddress, size);
            nextAddress += size;

            // 创建虚引用
            PhantomReference<Object> phantomRef = new PhantomReference<>(holder, refQueue);
            blocks.put(phantomRef, block);

            System.out.println("分配直接内存: " + size + " bytes at 0x" + 
                Long.toHexString(block.address));

            return block;
        }

        /**
         * 处理引用队列，释放直接内存
         */
        public void processReferenceQueue() {
            System.out.println("处理引用队列，释放直接内存...");
            Reference<?> ref;
            while ((ref = refQueue.poll()) != null) {
                DirectMemoryBlock block = blocks.remove(ref);
                if (block != null) {
                    System.out.println("  释放直接内存: " + block.getSize() + " bytes");
                }
                ref.clear();
            }
        }

        /**
         * 获取内存块数量
         * 
         * @return 内存块数量
         */
        public int getBlockCount() {
            return blocks.size();
        }

        /**
         * 获取总分配内存
         * 
         * @return 总分配内存大小
         */
        public long getTotalAllocated() {
            long total = 0;
            for (DirectMemoryBlock block : blocks.values()) {
                total += block.getSize();
            }
            return total;
        }
    }

    /**
     * 主方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        PhantomReferenceExample example = new PhantomReferenceExample();

        // 演示基本用法
        example.demonstrateBasicUsage();

        // 演示资源清理
        example.demonstrateResourceCleanup();

        // 演示直接内存清理
        example.demonstrateDirectMemoryCleanup();
    }
}
