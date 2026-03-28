package com.linsir.abc.core.jvm.tuning.virtualmemory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * 虚拟内存监控器
 * 监控Windows系统虚拟内存使用情况，防止因页面交换导致的长时间停顿
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class VirtualMemoryMonitor {

    private static final Logger LOGGER = Logger.getLogger(VirtualMemoryMonitor.class.getName());

    /**
     * 默认监控间隔（秒）
     */
    private static final long DEFAULT_MONITOR_INTERVAL_SECONDS = 30;

    /**
     * 页面交换警告阈值（百分比）
     */
    private static final double PAGING_WARNING_THRESHOLD = 0.5;

    /**
     * 页面交换危险阈值（百分比）
     */
    private static final double PAGING_DANGER_THRESHOLD = 0.8;

    /**
     * 调度器
     */
    private final ScheduledExecutorService scheduler;

    /**
     * 是否运行中
     */
    private final AtomicBoolean running;

    /**
     * 监控间隔（秒）
     */
    private final long monitorIntervalSeconds;

    /**
     * 内存MXBean
     */
    private final MemoryMXBean memoryMXBean;

    /**
     * 操作系统MXBean
     */
    private final OperatingSystemMXBean osMXBean;

    public VirtualMemoryMonitor() {
        this(DEFAULT_MONITOR_INTERVAL_SECONDS);
    }

    public VirtualMemoryMonitor(long monitorIntervalSeconds) {
        this.monitorIntervalSeconds = monitorIntervalSeconds;
        this.scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "virtual-memory-monitor");
            t.setDaemon(true);
            return t;
        });
        this.running = new AtomicBoolean(false);
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.osMXBean = ManagementFactory.getOperatingSystemMXBean();
    }

    /**
     * 启动监控
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            LOGGER.info("Starting VirtualMemoryMonitor with interval " + monitorIntervalSeconds + " seconds");
            scheduler.scheduleAtFixedRate(
                    this::monitor,
                    0,
                    monitorIntervalSeconds,
                    TimeUnit.SECONDS
            );
        }
    }

    /**
     * 停止监控
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            LOGGER.info("Stopping VirtualMemoryMonitor");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 监控方法
     */
    private void monitor() {
        try {
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
            MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();

            long heapUsed = heapUsage.getUsed();
            long heapCommitted = heapUsage.getCommitted();
            long heapMax = heapUsage.getMax();

            long nonHeapUsed = nonHeapUsage.getUsed();
            long nonHeapCommitted = nonHeapUsage.getCommitted();

            // 计算堆内存使用率
            double heapUsageRatio = heapMax > 0 ? (double) heapUsed / heapMax : (double) heapUsed / heapCommitted;

            LOGGER.info(String.format(
                    "Memory - Heap: used=%dMB, committed=%dMB, max=%dMB, usage=%.2f%% | " +
                            "Non-Heap: used=%dMB, committed=%dMB",
                    heapUsed / 1024 / 1024,
                    heapCommitted / 1024 / 1024,
                    heapMax / 1024 / 1024,
                    heapUsageRatio * 100,
                    nonHeapUsed / 1024 / 1024,
                    nonHeapCommitted / 1024 / 1024
            ));

            // 检查内存使用是否过高
            if (heapUsageRatio >= PAGING_DANGER_THRESHOLD) {
                LOGGER.severe("CRITICAL: Heap memory usage exceeds " + (PAGING_DANGER_THRESHOLD * 100) +
                        "%! Risk of paging to disk.");
                onHighMemoryUsage();
            } else if (heapUsageRatio >= PAGING_WARNING_THRESHOLD) {
                LOGGER.warning("WARNING: Heap memory usage exceeds " + (PAGING_WARNING_THRESHOLD * 100) +
                        "%. Consider increasing heap size.");
            }

        } catch (Exception e) {
            LOGGER.severe("Error monitoring virtual memory: " + e.getMessage());
        }
    }

    /**
     * 高内存使用时的处理
     */
    protected void onHighMemoryUsage() {
        // 子类可重写此方法实现自定义处理
        LOGGER.warning("High memory usage detected. Suggestions:");
        LOGGER.warning("1. Increase physical memory");
        LOGGER.warning("2. Increase JVM heap size");
        LOGGER.warning("3. Use -XX:+UseLargePages to lock memory");
        LOGGER.warning("4. Check for memory leaks");
    }

    /**
     * 获取系统内存信息
     *
     * @return 内存信息
     */
    public SystemMemoryInfo getSystemMemoryInfo() {
        MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();

        return new SystemMemoryInfo(
                heapUsage.getUsed(),
                heapUsage.getCommitted(),
                heapUsage.getMax(),
                nonHeapUsage.getUsed(),
                nonHeapUsage.getCommitted(),
                osMXBean.getAvailableProcessors()
        );
    }

    /**
     * 生成JVM参数建议
     *
     * @return JVM参数建议
     */
    public static String getJvmOptionsRecommendation() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Windows虚拟内存优化JVM参数建议 ===\n\n");

        sb.append("1. 锁定堆内存，防止被交换到磁盘:\n");
        sb.append("   -XX:+UnlockExperimentalVMOptions\n");
        sb.append("   -XX:+UseLargePages\n\n");

        sb.append("2. 设置固定堆大小（避免动态调整）:\n");
        sb.append("   -Xms4g -Xmx4g\n\n");

        sb.append("3. 启用G1收集器并控制停顿时间:\n");
        sb.append("   -XX:+UseG1GC\n");
        sb.append("   -XX:MaxGCPauseMillis=100\n\n");

        sb.append("4. 启用字符串去重减少内存使用:\n");
        sb.append("   -XX:+UseStringDeduplication\n\n");

        sb.append("5. 启用GC日志监控停顿时间:\n");
        sb.append("   -XX:+PrintGCApplicationStoppedTime\n");
        sb.append("   -XX:+PrintSafepointStatistics\n\n");

        sb.append("Windows系统设置建议:\n");
        sb.append("1. 设置固定大小的页面文件（初始值和最大值相同）\n");
        sb.append("2. 增加物理内存\n");
        sb.append("3. 关闭不必要的后台程序\n");

        return sb.toString();
    }

    /**
     * 系统内存信息类
     */
    public static class SystemMemoryInfo {

        /**
         * 堆已使用内存（字节）
         */
        private final long heapUsed;

        /**
         * 堆已提交内存（字节）
         */
        private final long heapCommitted;

        /**
         * 堆最大内存（字节）
         */
        private final long heapMax;

        /**
         * 非堆已使用内存（字节）
         */
        private final long nonHeapUsed;

        /**
         * 非堆已提交内存（字节）
         */
        private final long nonHeapCommitted;

        /**
         * 可用处理器数
         */
        private final int availableProcessors;

        public SystemMemoryInfo(long heapUsed, long heapCommitted, long heapMax,
                                long nonHeapUsed, long nonHeapCommitted, int availableProcessors) {
            this.heapUsed = heapUsed;
            this.heapCommitted = heapCommitted;
            this.heapMax = heapMax;
            this.nonHeapUsed = nonHeapUsed;
            this.nonHeapCommitted = nonHeapCommitted;
            this.availableProcessors = availableProcessors;
        }

        public long getHeapUsed() {
            return heapUsed;
        }

        public long getHeapCommitted() {
            return heapCommitted;
        }

        public long getHeapMax() {
            return heapMax;
        }

        public long getNonHeapUsed() {
            return nonHeapUsed;
        }

        public long getNonHeapCommitted() {
            return nonHeapCommitted;
        }

        public int getAvailableProcessors() {
            return availableProcessors;
        }

        /**
         * 获取堆内存使用率
         *
         * @return 使用率（0-1）
         */
        public double getHeapUsageRatio() {
            return heapMax > 0 ? (double) heapUsed / heapMax : (double) heapUsed / heapCommitted;
        }

        @Override
        public String toString() {
            return String.format(
                    "SystemMemoryInfo[heapUsed=%dMB, heapCommitted=%dMB, heapMax=%dMB, heapUsage=%.2f%%, " +
                            "nonHeapUsed=%dMB, nonHeapCommitted=%dMB, processors=%d]",
                    heapUsed / 1024 / 1024,
                    heapCommitted / 1024 / 1024,
                    heapMax / 1024 / 1024,
                    getHeapUsageRatio() * 100,
                    nonHeapUsed / 1024 / 1024,
                    nonHeapCommitted / 1024 / 1024,
                    availableProcessors
            );
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public long getMonitorIntervalSeconds() {
        return monitorIntervalSeconds;
    }
}
