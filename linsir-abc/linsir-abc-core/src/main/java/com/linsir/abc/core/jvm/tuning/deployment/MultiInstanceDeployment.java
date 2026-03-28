package com.linsir.abc.core.jvm.tuning.deployment;

import java.util.ArrayList;
import java.util.List;

/**
 * 多实例部署管理器
 * 用于管理单机多Java进程部署策略
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class MultiInstanceDeployment {

    /**
     * 基础端口
     */
    private static final int BASE_PORT = 8080;

    /**
     * 服务器总内存（GB）
     */
    private final int totalMemoryGb;

    /**
     * 实例数量
     */
    private final int instanceCount;

    /**
     * 垃圾收集器类型
     */
    private final GarbageCollectorType gcType;

    /**
     * 部署配置列表
     */
    private final List<JvmDeploymentConfig> deploymentConfigs;

    public MultiInstanceDeployment(int totalMemoryGb, int instanceCount, GarbageCollectorType gcType) {
        this.totalMemoryGb = totalMemoryGb;
        this.instanceCount = instanceCount;
        this.gcType = gcType;
        this.deploymentConfigs = new ArrayList<>();
        initializeConfigs();
    }

    /**
     * 初始化部署配置
     */
    private void initializeConfigs() {
        int heapSizePerInstance = calculateHeapSizePerInstance();

        for (int i = 0; i < instanceCount; i++) {
            JvmDeploymentConfig config = new JvmDeploymentConfig();
            config.setTotalMemoryGb(totalMemoryGb);
            config.setInstanceCount(instanceCount);
            config.setGcType(gcType);
            config.setServerPort(BASE_PORT + i + 1);
            deploymentConfigs.add(config);
        }
    }

    /**
     * 计算每个实例的堆内存大小
     * 预留20%内存给操作系统和其他开销
     *
     * @return 每个实例的堆内存大小（GB）
     */
    private int calculateHeapSizePerInstance() {
        int availableMemory = (int) (totalMemoryGb * 0.8);
        return availableMemory / instanceCount;
    }

    /**
     * 生成所有实例的启动命令
     *
     * @param jarPath JAR文件路径
     * @return 启动命令列表
     */
    public List<String> generateAllStartCommands(String jarPath) {
        List<String> commands = new ArrayList<>();
        for (JvmDeploymentConfig config : deploymentConfigs) {
            commands.add(config.generateStartCommand(jarPath));
        }
        return commands;
    }

    /**
     * 获取部署方案对比报告
     *
     * @return 方案对比报告
     */
    public static String getDeploymentComparisonReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== JVM部署方案对比 ===\n\n");

        sb.append("方案一：G1收集器\n");
        sb.append("  优点：平衡吞吐量和延迟\n");
        sb.append("  缺点：大堆时停顿仍较长\n");
        sb.append("  适用场景：堆内存<32GB\n");
        sb.append("  JVM参数：-XX:+UseG1GC -XX:MaxGCPauseMillis=200\n\n");

        sb.append("方案二：ZGC收集器\n");
        sb.append("  优点：超低延迟\n");
        sb.append("  缺点：吞吐量略低\n");
        sb.append("  适用场景：超大堆、低延迟需求\n");
        sb.append("  JVM参数：-XX:+UseZGC\n\n");

        sb.append("方案三：单机多实例部署（推荐）\n");
        sb.append("  优点：充分利用CPU，单实例故障影响小\n");
        sb.append("  缺点：部署复杂\n");
        sb.append("  适用场景：高并发、高可用需求\n");
        sb.append("  示例：将64GB内存分配给4个Java进程，每个16GB\n");

        return sb.toString();
    }

    /**
     * 计算预期的Full GC时间
     *
     * @param heapSizeGb 堆内存大小（GB）
     * @param gcType     垃圾收集器类型
     * @return 预期的Full GC时间（毫秒）
     */
    public static long estimateFullGcTime(int heapSizeGb, GarbageCollectorType gcType) {
        switch (gcType) {
            case G1:
                // G1收集器：约10ms per GB
                return heapSizeGb * 10L;
            case ZGC:
                // ZGC收集器：小于10ms
                return 10L;
            case SHENANDOAH:
                // Shenandoah收集器：约5-20ms
                return 15L;
            case PARALLEL:
                // Parallel收集器：约500ms per GB
                return heapSizeGb * 500L;
            case CMS:
                // CMS收集器：约200ms per GB
                return heapSizeGb * 200L;
            default:
                return heapSizeGb * 100L;
        }
    }

    public List<JvmDeploymentConfig> getDeploymentConfigs() {
        return new ArrayList<>(deploymentConfigs);
    }

    public int getTotalMemoryGb() {
        return totalMemoryGb;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public GarbageCollectorType getGcType() {
        return gcType;
    }
}
