package com.linsir.abc.core.jvm.gc.allocation;

/**
 * 长期存活对象进入老年代示例
 *
 * 演示对象在Survivor区中经过多次Minor GC后晋升到老年代。
 *
 * VM参数: -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
 *         -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution
 *
 * 参数说明:
 * - -XX:MaxTenuringThreshold=1: 对象经过1次Minor GC后进入老年代（默认15）
 * - -XX:+PrintTenuringDistribution: 打印年龄分布信息
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class TenuringThreshold {

    /**
     * 1MB的大小常量
     */
    private static final int _1MB = 1024 * 1024;

    /**
     * 测试对象年龄晋升
     *
     * 通过设置较低的MaxTenuringThreshold，观察对象快速晋升到老年代
     */
    @SuppressWarnings("unused")
    public static void testTenuringThreshold() {
        System.out.println("=== 对象年龄晋升测试 ===\n");
        System.out.println("VM参数: -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8");
        System.out.println("        -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution");
        System.out.println("\n预期: 对象经过1次Minor GC后进入老年代\n");

        // 分配一个小对象，用于观察年龄增长
        System.out.println("1. 分配小对象 allocation1 = new byte[_1MB / 4]");
        byte[] allocation1 = new byte[_1MB / 4];
        System.out.println("   对象大小: 256KB");
        System.out.println("   预期位置: Eden区");

        // 分配大对象触发Minor GC
        System.out.println("\n2. 分配大对象触发Minor GC");
        System.out.println("   allocation2 = new byte[4 * _1MB]");
        byte[] allocation2 = new byte[4 * _1MB];
        System.out.println("   触发Minor GC后，allocation1应该进入Survivor区或老年代");

        // 再次分配，观察对象晋升
        System.out.println("\n3. 再次分配触发第二次Minor GC");
        System.out.println("   allocation3 = new byte[4 * _1MB]");
        byte[] allocation3 = new byte[4 * _1MB];
        System.out.println("   allocation3 = null");
        allocation3 = null;

        System.out.println("\n4. 第三次分配触发GC");
        System.out.println("   allocation3 = new byte[4 * _1MB]");
        allocation3 = new byte[4 * _1MB];

        System.out.println("\n观察GC日志中的年龄分布信息:");
        System.out.println("  - desired survivor size: 期望的Survivor空间大小");
        System.out.println("  - new threshold: 新的年龄阈值");
        System.out.println("  - age 1: 年龄为1的对象大小");
    }

    /**
     * 演示动态对象年龄判定
     *
     * 当Survivor空间中相同年龄对象大小总和超过Survivor空间一半时，
     * 年龄大于等于该年龄的对象可以直接进入老年代
     */
    public static void testDynamicAge() {
        System.out.println("\n=== 动态对象年龄判定测试 ===\n");
        System.out.println("动态年龄判定规则:");
        System.out.println("  Survivor空间中相同年龄对象大小总和 > Survivor空间一半");
        System.out.println("  则年龄 >= 该年龄的对象可以直接进入老年代\n");

        System.out.println("示例场景:");
        System.out.println("  Survivor空间: 1MB");
        System.out.println("  年龄1对象: 100KB");
        System.out.println("  年龄2对象: 100KB");
        System.out.println("  年龄3对象: 400KB (年龄3总和400KB > 512KB? 否)");
        System.out.println("  ...");
        System.out.println("  年龄5对象: 200KB (年龄5总和600KB > 512KB? 是!)");
        System.out.println("  结果: 年龄>=5的对象可以直接晋升老年代");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 测试对象年龄晋升
        testTenuringThreshold();

        // 演示动态年龄判定
        testDynamicAge();

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
