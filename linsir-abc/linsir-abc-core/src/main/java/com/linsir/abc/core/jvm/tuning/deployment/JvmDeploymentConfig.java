package com.linsir.abc.core.jvm.tuning.deployment;

/**
 * JVM部署配置类
 * 用于配置大内存硬件上的程序部署策略
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class JvmDeploymentConfig {

    /**
     * 服务器总内存（GB）
     */
    private int totalMemoryGb;

    /**
     * 实例数量
     */
    private int instanceCount;

    /**
     * 每个实例的堆内存大小（GB）
     */
    private int heapSizePerInstanceGb;

    /**
     * 垃圾收集器类型
     */
    private GarbageCollectorType gcType;

    /**
     * 最大GC停顿时间（毫秒）
     */
    private int maxGcPauseMillis;

    /**
     * 服务端口
     */
    private int serverPort;

    /**
     * 是否启用大页内存
     */
    private boolean useLargePages;

    public JvmDeploymentConfig() {
        this.totalMemoryGb = 64;
        this.instanceCount = 4;
        this.gcType = GarbageCollectorType.G1;
        this.maxGcPauseMillis = 200;
        this.useLargePages = false;
    }

    public JvmDeploymentConfig(int totalMemoryGb, int instanceCount, GarbageCollectorType gcType) {
        this.totalMemoryGb = totalMemoryGb;
        this.instanceCount = instanceCount;
        this.gcType = gcType;
        this.maxGcPauseMillis = 200;
        this.useLargePages = false;
        calculateHeapSize();
    }

    /**
     * 计算每个实例的堆内存大小
     * 预留20%内存给操作系统和其他开销
     */
    private void calculateHeapSize() {
        int availableMemory = (int) (totalMemoryGb * 0.8);
        this.heapSizePerInstanceGb = availableMemory / instanceCount;
    }

    /**
     * 生成JVM启动参数
     *
     * @return JVM参数字符串
     */
    public String generateJvmOptions() {
        StringBuilder sb = new StringBuilder();

        // 堆内存设置
        sb.append("-Xms").append(heapSizePerInstanceGb).append("g ");
        sb.append("-Xmx").append(heapSizePerInstanceGb).append("g ");

        // GC收集器设置
        sb.append(gcType.getJvmOption()).append(" ");

        // G1收集器特有参数
        if (gcType == GarbageCollectorType.G1) {
            sb.append("-XX:MaxGCPauseMillis=").append(maxGcPauseMillis).append(" ");
        }

        // 大页内存
        if (useLargePages) {
            sb.append("-XX:+UseLargePages ");
        }

        // GC日志参数
        sb.append("-XX:+PrintGCDetails ");
        sb.append("-XX:+PrintGCTimeStamps ");
        sb.append("-Xloggc:/logs/gc-%p.log ");

        return sb.toString().trim();
    }

    /**
     * 生成完整的启动命令
     *
     * @param jarPath JAR文件路径
     * @return 启动命令
     */
    public String generateStartCommand(String jarPath) {
        StringBuilder sb = new StringBuilder();
        sb.append("java ");
        sb.append(generateJvmOptions()).append(" ");
        sb.append("-Dserver.port=").append(serverPort).append(" ");
        sb.append("-jar ").append(jarPath);
        return sb.toString();
    }

    public int getTotalMemoryGb() {
        return totalMemoryGb;
    }

    public void setTotalMemoryGb(int totalMemoryGb) {
        this.totalMemoryGb = totalMemoryGb;
        calculateHeapSize();
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
        calculateHeapSize();
    }

    public int getHeapSizePerInstanceGb() {
        return heapSizePerInstanceGb;
    }

    public GarbageCollectorType getGcType() {
        return gcType;
    }

    public void setGcType(GarbageCollectorType gcType) {
        this.gcType = gcType;
    }

    public int getMaxGcPauseMillis() {
        return maxGcPauseMillis;
    }

    public void setMaxGcPauseMillis(int maxGcPauseMillis) {
        this.maxGcPauseMillis = maxGcPauseMillis;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isUseLargePages() {
        return useLargePages;
    }

    public void setUseLargePages(boolean useLargePages) {
        this.useLargePages = useLargePages;
    }
}
