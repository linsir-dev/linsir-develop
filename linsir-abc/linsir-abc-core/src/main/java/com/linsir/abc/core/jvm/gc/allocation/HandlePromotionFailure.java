package com.linsir.abc.core.jvm.gc.allocation;

/**
 * 空间分配担保示例
 *
 * 演示Minor GC前的空间分配担保机制。
 *
 * VM参数: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
 *         -XX:-HandlePromotionFailure (JDK 6 Update 24之后已废弃)
 *
 * 空间分配担保流程:
 * 1. 检查老年代最大可用连续空间是否大于新生代所有对象总空间
 * 2. 如果成立，Minor GC可以安全进行
 * 3. 如果不成立，检查是否允许担保失败
 * 4. 如果允许，检查老年代最大可用连续空间是否大于历次晋升到老年代对象的平均大小
 * 5. 如果大于，尝试进行Minor GC（有风险）
 * 6. 如果小于或不允许担保失败，则进行Full GC
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class HandlePromotionFailure {

    /**
     * 1MB的大小常量
     */
    private static final int _1MB = 1024 * 1024;

    /**
     * 测试空间分配担保
     *
     * 通过大量对象分配和GC，观察空间分配担保机制的工作
     */
    @SuppressWarnings("unused")
    public static void testHandlePromotion() {
        System.out.println("=== 空间分配担保测试 ===\n");
        System.out.println("VM参数: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8");
        System.out.println("\n空间分配担保流程:");
        System.out.println("  1. 检查老年代空间 > 新生代总对象空间?");
        System.out.println("  2. 是 -> 安全进行Minor GC");
        System.out.println("  3. 否 -> 检查是否允许担保失败");
        System.out.println("  4. 允许 -> 检查老年代空间 > 历次晋升平均大小?");
        System.out.println("  5. 是 -> 尝试Minor GC（有风险）");
        System.out.println("  6. 否 -> 进行Full GC\n");

        // 分配对象填满Eden区
        System.out.println("1. 分配多个对象填满Eden区");
        byte[] allocation1 = new byte[2 * _1MB];
        byte[] allocation2 = new byte[2 * _1MB];
        byte[] allocation3 = new byte[2 * _1MB];

        System.out.println("   allocation1-3 各2MB，共6MB");

        // 分配大对象触发GC
        System.out.println("\n2. 分配大对象触发Minor GC");
        byte[] allocation4 = new byte[4 * _1MB];
        System.out.println("   allocation4 = 4MB");
        System.out.println("   触发Minor GC，allocation1-3可能晋升到老年代");

        // 继续分配
        System.out.println("\n3. 继续分配对象");
        byte[] allocation5 = new byte[2 * _1MB];
        byte[] allocation6 = new byte[2 * _1MB];

        System.out.println("\n4. 再次分配大对象");
        byte[] allocation7 = new byte[4 * _1MB];
        System.out.println("   可能触发Full GC");

        System.out.println("\n观察GC日志:");
        System.out.println("  - [GC]: Minor GC");
        System.out.println("  - [Full GC]: Full GC");
        System.out.println("  - 老年代使用量变化");
    }

    /**
     * 演示担保失败场景
     *
     * 当Minor GC后存活对象突增，超过平均值时，会发生担保失败
     */
    public static void testPromotionFailure() {
        System.out.println("\n=== 担保失败场景演示 ===\n");

        System.out.println("担保失败（Handle Promotion Failure）场景:");
        System.out.println("  假设历次Minor GC晋升到老年代的平均大小为2MB");
        System.out.println("  老年代剩余空间为3MB");
        System.out.println("  检查: 3MB > 2MB? 是，允许进行Minor GC\n");

        System.out.println("  但本次Minor GC后存活对象突增为5MB");
        System.out.println("  Survivor空间无法容纳（假设为1MB）");
        System.out.println("  需要老年代担保5MB，但老年代只有3MB\n");

        System.out.println("  结果: 担保失败！");
        System.out.println("  处理: 重新发起Full GC\n");

        System.out.println("优化建议:");
        System.out.println("  - 尽管担保失败会绕圈子，但大部分情况下仍建议打开HandlePromotionFailure");
        System.out.println("  - 避免Full GC过于频繁");
        System.out.println("  - JDK 6 Update 24之后此参数已废弃，默认开启担保机制");
    }

    /**
     * 打印内存信息
     */
    private static void printMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / _1MB;
        long freeMemory = runtime.freeMemory() / _1MB;
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory() / _1MB;

        System.out.println("当前内存状态:");
        System.out.println("  最大堆内存: " + maxMemory + "MB");
        System.out.println("  当前堆内存: " + totalMemory + "MB");
        System.out.println("  已使用内存: " + usedMemory + "MB");
        System.out.println("  空闲内存: " + freeMemory + "MB");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("初始内存状态:");
        printMemoryInfo();

        // 测试空间分配担保
        testHandlePromotion();

        System.out.println("\n当前内存状态:");
        printMemoryInfo();

        // 演示担保失败场景
        testPromotionFailure();

        // 建议GC查看最终内存状态
        System.out.println("\n调用System.gc()查看最终状态...");
        System.gc();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n最终内存状态:");
        printMemoryInfo();
    }
}
