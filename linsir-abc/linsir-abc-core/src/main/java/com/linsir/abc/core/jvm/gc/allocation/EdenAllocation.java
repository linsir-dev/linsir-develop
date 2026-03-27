package com.linsir.abc.core.jvm.gc.allocation;

/**
 * Eden区内存分配示例
 *
 * 演示对象在Eden区的分配过程以及Minor GC的触发。
 *
 * VM参数: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
 *
 * 参数说明:
 * - -Xms20M -Xmx20M: 堆内存初始和最大值20MB
 * - -Xmn10M: 新生代大小10MB
 * - -XX:SurvivorRatio=8: Eden区与Survivor区比例为8:1
 *   即Eden区8MB，每个Survivor区1MB
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class EdenAllocation {

    /**
     * 1MB的大小常量
     */
    private static final int _1MB = 1024 * 1024;

    /**
     * 测试对象分配
     *
     * 分配4个对象，观察Eden区使用和Minor GC情况
     */
    public static void testAllocation() {
        System.out.println("=== Eden区内存分配测试 ===\n");
        System.out.println("VM参数: -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8");
        System.out.println("预期: Eden=8MB, Survivor(from)=1MB, Survivor(to)=1MB, 老年代=10MB\n");

        byte[] allocation1, allocation2, allocation3, allocation4;

        System.out.println("分配 allocation1 = new byte[2 * _1MB]");
        allocation1 = new byte[2 * _1MB];
        printAllocationInfo(1, 2);

        System.out.println("\n分配 allocation2 = new byte[2 * _1MB]");
        allocation2 = new byte[2 * _1MB];
        printAllocationInfo(2, 4);

        System.out.println("\n分配 allocation3 = new byte[2 * _1MB]");
        allocation3 = new byte[2 * _1MB];
        printAllocationInfo(3, 6);

        System.out.println("\n分配 allocation4 = new byte[4 * _1MB] (触发Minor GC)");
        // 此时Eden区已有6MB，再分配4MB会超过8MB，触发Minor GC
        allocation4 = new byte[4 * _1MB];
        printAllocationInfo(4, 10);

        System.out.println("\n=== 分配完成 ===");
        System.out.println("allocation1-3 应该被晋升到老年代或保留在Survivor区");
        System.out.println("allocation4 应该分配在Eden区或老年代");
    }

    /**
     * 打印分配信息
     *
     * @param step 步骤编号
     * @param totalAllocatedMB 已分配总内存（MB）
     */
    private static void printAllocationInfo(int step, int totalAllocatedMB) {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / _1MB;
        long freeMemory = runtime.freeMemory() / _1MB;
        long usedMemory = totalMemory - freeMemory;

        System.out.println("  步骤" + step + ": 已分配 " + totalAllocatedMB + "MB");
        System.out.println("  堆内存: 总计=" + totalMemory + "MB, 已用=" + usedMemory + "MB, 空闲=" + freeMemory + "MB");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        testAllocation();

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
