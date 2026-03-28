package com.linsir.abc.core.jvm.tuning.offheap;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * 堆外内存监控器
 * 定期监控堆外内存使用情况，防止堆外内存溢出
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class OffHeapMemoryMonitor {

    private static final Logger LOGGER = Logger.getLogger(OffHeapMemoryMonitor.class.getName());

    /**
     * 默认监控间隔（秒）
     */
    private static final long DEFAULT_MONITOR_INTERVAL_SECONDS = 60;

    /**
     * 内存使用警告阈值（百分比）
     */
    private static final double WARNING_THRESHOLD = 0.8;

    /**
     * 内存使用危险阈值（百分比）
     */
    private static final double DANGER_THRESHOLD = 0.9;

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
     * 最大堆外内存限制
     */
    private final long maxDirectMemory;

    public OffHeapMemoryMonitor() {
        this(DEFAULT_MONITOR_INTERVAL_SECONDS);
    }

    public OffHeapMemoryMonitor(long monitorIntervalSeconds) {
        this.monitorIntervalSeconds = monitorIntervalSeconds;
        this.scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "offheap-monitor");
            t.setDaemon(true);
            return t;
        });
        this.running = new AtomicBoolean(false);
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.maxDirectMemory = getMaxDirectMemoryFromJvm();
    }

    /**
     * 从JVM参数获取最大堆外内存
     *
     * @return 最大堆外内存（字节）
     */
    private long getMaxDirectMemoryFromJvm() {
        try {
            Class<?> vmClass = Class.forName("sun.misc.VM");
            java.lang.reflect.Method maxDirectMemoryMethod = vmClass.getDeclaredMethod("maxDirectMemory");
            return (long) maxDirectMemoryMethod.invoke(null);
        } catch (Exception e) {
            LOGGER.warning("Failed to get max direct memory from VM: " + e.getMessage());
            return Runtime.getRuntime().maxMemory();
        }
    }

    /**
     * 启动监控
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            LOGGER.info("Starting OffHeapMemoryMonitor with interval " + monitorIntervalSeconds + " seconds");
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
            LOGGER.info("Stopping OffHeapMemoryMonitor");
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
            MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();
            long used = nonHeapUsage.getUsed();
            long committed = nonHeapUsage.getCommitted();
            long max = nonHeapUsage.getMax();

            // 计算使用率
            double usageRatio = max > 0 ? (double) used / max : (double) used / committed;

            // 记录日志
            LOGGER.info(String.format(
                    "Off-heap memory: used=%dMB, committed=%dMB, max=%dMB, usage=%.2f%%",
                    used / 1024 / 1024,
                    committed / 1024 / 1024,
                    max / 1024 / 1024,
                    usageRatio * 100
            ));

            // 检查阈值
            if (usageRatio >= DANGER_THRESHOLD) {
                LOGGER.severe("CRITICAL: Off-heap memory usage exceeds " + (DANGER_THRESHOLD * 100) +
                        "%! Current usage: " + String.format("%.2f%%", usageRatio * 100));
                onDangerThresholdExceeded(used, max);
            } else if (usageRatio >= WARNING_THRESHOLD) {
                LOGGER.warning("WARNING: Off-heap memory usage exceeds " + (WARNING_THRESHOLD * 100) +
                        "%. Current usage: " + String.format("%.2f%%", usageRatio * 100));
                onWarningThresholdExceeded(used, max);
            }

        } catch (Exception e) {
            LOGGER.severe("Error monitoring off-heap memory: " + e.getMessage());
        }
    }

    /**
     * 警告阈值超过时的处理
     *
     * @param used 已使用内存
     * @param max  最大内存
     */
    protected void onWarningThresholdExceeded(long used, long max) {
        // 子类可重写此方法实现自定义处理
        LOGGER.warning("Off-heap memory warning threshold exceeded. Consider reducing buffer usage.");
    }

    /**
     * 危险阈值超过时的处理
     *
     * @param used 已使用内存
     * @param max  最大内存
     */
    protected void onDangerThresholdExceeded(long used, long max) {
        // 子类可重写此方法实现自定义处理
        LOGGER.severe("Off-heap memory danger threshold exceeded! Immediate action required.");
        // 可以触发GC或释放缓存
        System.gc();
    }

    /**
     * 获取当前堆外内存使用信息
     *
     * @return 内存使用信息
     */
    public MemoryInfo getMemoryInfo() {
        MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();
        return new MemoryInfo(
                nonHeapUsage.getUsed(),
                nonHeapUsage.getCommitted(),
                nonHeapUsage.getMax() > 0 ? nonHeapUsage.getMax() : maxDirectMemory
        );
    }

    /**
     * 内存信息类
     */
    public static class MemoryInfo {

        /**
         * 已使用内存（字节）
         */
        private final long used;

        /**
         * 已提交内存（字节）
         */
        private final long committed;

        /**
         * 最大内存（字节）
         */
        private final long max;

        public MemoryInfo(long used, long committed, long max) {
            this.used = used;
            this.committed = committed;
            this.max = max;
        }

        public long getUsed() {
            return used;
        }

        public long getCommitted() {
            return committed;
        }

        public long getMax() {
            return max;
        }

        /**
         * 获取使用率
         *
         * @return 使用率（0-1）
         */
        public double getUsageRatio() {
            return max > 0 ? (double) used / max : (double) used / committed;
        }

        @Override
        public String toString() {
            return String.format("MemoryInfo[used=%dMB, committed=%dMB, max=%dMB, usage=%.2f%%]",
                    used / 1024 / 1024,
                    committed / 1024 / 1024,
                    max / 1024 / 1024,
                    getUsageRatio() * 100);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public long getMonitorIntervalSeconds() {
        return monitorIntervalSeconds;
    }
}
