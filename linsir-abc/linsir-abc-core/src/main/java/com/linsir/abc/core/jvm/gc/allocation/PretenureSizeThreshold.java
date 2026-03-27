package com.linsir.abc.core.jvm.gc.allocation;

/**
 * 大对象直接进入老年代示例
 *
 * 演示大对象（大于-XX:PretenureSizeThreshold设置值）直接在老年代分配。
 *
 * VM参数: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
 *         -XX:PretenureSizeThreshold=3145728
 *
 * 参数说明:
 * - -XX:PretenureSizeThreshold=3145728: 大于3MB的对象直接在老年代分配
 *
 * 注意: -XX:PretenureSizeThreshold参数只对Serial和ParNew两款收集器有效
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class PretenureSizeThreshold {

    /**
     * 1MB的大小常量
     */
    private static final int _1MB = 1024 * 1024;

    /**
     * 测试大对象直接进入老年代
     *
     * 分配一个4MB的大对象，观察其在老年代的分配
     */
    public static void testPretenureSizeThreshold() {
        System.out.println("=== 大对象直接进入老年代测试 ===\n");
        System.out.println("VM参数: -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8");
        System.out.println("        -XX:PretenureSizeThreshold=3145728 (3MB)");
        System.out.println("\n注意: 此参数只对Serial和ParNew收集器有效\n");

        byte[] allocation;

        System.out.println("分配 allocation = new byte[4 * _1MB] (4MB > 3MB阈值)");
        System.out.println("预期: 对象直接在老年代分配，不触发Minor GC\n");

        // 直接分配在老年代
        allocation = new byte[4 * _1MB];

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / _1MB;
        long freeMemory = runtime.freeMemory() / _1MB;
        long usedMemory = totalMemory - freeMemory;

        System.out.println("分配完成:");
        System.out.println("  对象大小: 4MB");
        System.out.println("  堆内存: 总计=" + totalMemory + "MB, 已用=" + usedMemory + "MB, 空闲=" + freeMemory + "MB");
        System.out.println("\n观察GC日志:");
        System.out.println("  如果没有出现[GC]或[Full GC]，说明对象直接在老年代分配");
        System.out.println("  如果老年代使用量增加了4MB，说明分配成功");
    }

    /**
     * 对比测试：小对象和大对象的分配
     *
     * 分别分配小对象和大对象，观察分配位置差异
     */
    public static void testComparison() {
        System.out.println("\n=== 小对象 vs 大对象分配对比 ===\n");

        // 小对象（2MB < 3MB阈值）
        System.out.println("1. 分配小对象: new byte[2 * _1MB] (2MB < 3MB阈值)");
        System.out.println("   预期: 在Eden区分配");
        byte[] smallObject = new byte[2 * _1MB];

        // 大对象（4MB > 3MB阈值）
        System.out.println("\n2. 分配大对象: new byte[4 * _1MB] (4MB > 3MB阈值)");
        System.out.println("   预期: 直接在老年代分配");
        byte[] largeObject = new byte[4 * _1MB];

        System.out.println("\n分配完成，观察GC日志确认对象分配位置");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 测试大对象直接进入老年代
        testPretenureSizeThreshold();

        // 对比测试
        testComparison();

        // 建议GC查看最终内存状态
        System.out.println("\n调用System.gc()查看最终状态...");
        System.gc();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
