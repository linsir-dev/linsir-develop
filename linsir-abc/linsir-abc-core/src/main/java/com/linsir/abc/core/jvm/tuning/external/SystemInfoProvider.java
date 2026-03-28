package com.linsir.abc.core.jvm.tuning.external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * 系统信息提供者
 * 演示如何正确获取系统信息，避免频繁调用外部命令导致的性能问题
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class SystemInfoProvider {

    private static final Logger LOGGER = Logger.getLogger(SystemInfoProvider.class.getName());

    /**
     * 进程执行器线程池
     */
    private final ExecutorService processExecutor;

    /**
     * 缓存的系统信息
     */
    private volatile SystemInfo cachedInfo;

    /**
     * 缓存过期时间（毫秒）
     */
    private final long cacheExpireMillis;

    /**
     * 上次更新时间
     */
    private volatile long lastUpdateTime;

    public SystemInfoProvider() {
        this(4, 60000); // 默认4个线程，缓存1分钟
    }

    public SystemInfoProvider(int threadPoolSize, long cacheExpireMillis) {
        this.processExecutor = Executors.newFixedThreadPool(threadPoolSize, r -> {
            Thread t = new Thread(r, "process-executor");
            t.setDaemon(true);
            return t;
        });
        this.cacheExpireMillis = cacheExpireMillis;
        this.cachedInfo = null;
        this.lastUpdateTime = 0;
    }

    /**
     * 错误示例：使用Runtime.exec()频繁创建进程
     * 每次调用都会创建新进程，开销大
     *
     * @return 系统信息
     */
    public String wrongWayGetSystemInfo() {
        try {
            // 问题：频繁创建进程，开销大
            Process process = Runtime.getRuntime().exec("systeminfo");
            // 未读取输出流，可能导致阻塞
            return "OK";
        } catch (Exception e) {
            LOGGER.warning("Failed to get system info: " + e.getMessage());
            return "ERROR";
        }
    }

    /**
     * 正确示例：使用Java原生API获取系统信息
     * 无需创建外部进程
     *
     * @return 系统信息字符串
     */
    public String getSystemInfoWithJavaApi() {
        StringBuilder info = new StringBuilder();
        info.append("=== System Information ===\n");
        info.append("OS Name: ").append(System.getProperty("os.name")).append("\n");
        info.append("OS Version: ").append(System.getProperty("os.version")).append("\n");
        info.append("OS Arch: ").append(System.getProperty("os.arch")).append("\n");
        info.append("Available Processors: ").append(Runtime.getRuntime().availableProcessors()).append("\n");
        info.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
        info.append("Java Vendor: ").append(System.getProperty("java.vendor")).append("\n");
        info.append("Max Memory: ").append(Runtime.getRuntime().maxMemory() / 1024 / 1024).append("MB\n");
        info.append("Total Memory: ").append(Runtime.getRuntime().totalMemory() / 1024 / 1024).append("MB\n");
        info.append("Free Memory: ").append(Runtime.getRuntime().freeMemory() / 1024 / 1024).append("MB\n");
        return info.toString();
    }

    /**
     * 正确示例：使用进程池执行命令
     *
     * @param command 命令
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 命令输出
     */
    public String executeCommand(String command, long timeout, TimeUnit unit) {
        Future<String> future = processExecutor.submit(() -> {
            ProcessBuilder pb = new ProcessBuilder(command.split("\\s+"));
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(timeout, unit);
            if (!finished) {
                process.destroyForcibly();
                throw new TimeoutException("Command execution timeout");
            }

            return output.toString();
        });

        try {
            return future.get(timeout, unit);
        } catch (TimeoutException e) {
            future.cancel(true);
            LOGGER.warning("Command execution timeout: " + command);
            return "TIMEOUT";
        } catch (Exception e) {
            LOGGER.warning("Command execution failed: " + e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * 获取系统信息（带缓存）
     *
     * @return 系统信息
     */
    public SystemInfo getSystemInfoWithCache() {
        long now = System.currentTimeMillis();
        if (cachedInfo == null || (now - lastUpdateTime) > cacheExpireMillis) {
            synchronized (this) {
                if (cachedInfo == null || (now - lastUpdateTime) > cacheExpireMillis) {
                    cachedInfo = collectSystemInfo();
                    lastUpdateTime = now;
                }
            }
        }
        return cachedInfo;
    }

    /**
     * 收集系统信息
     *
     * @return 系统信息
     */
    private SystemInfo collectSystemInfo() {
        SystemInfo info = new SystemInfo();
        info.setOsName(System.getProperty("os.name"));
        info.setOsVersion(System.getProperty("os.version"));
        info.setOsArch(System.getProperty("os.arch"));
        info.setAvailableProcessors(Runtime.getRuntime().availableProcessors());
        info.setMaxMemory(Runtime.getRuntime().maxMemory());
        info.setTotalMemory(Runtime.getRuntime().totalMemory());
        info.setFreeMemory(Runtime.getRuntime().freeMemory());
        info.setJavaVersion(System.getProperty("java.version"));
        info.setJavaVendor(System.getProperty("java.vendor"));
        return info;
    }

    /**
     * 关闭提供者
     */
    public void shutdown() {
        processExecutor.shutdown();
        try {
            if (!processExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                processExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            processExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 系统信息类
     */
    public static class SystemInfo {
        private String osName;
        private String osVersion;
        private String osArch;
        private int availableProcessors;
        private long maxMemory;
        private long totalMemory;
        private long freeMemory;
        private String javaVersion;
        private String javaVendor;

        public String getOsName() {
            return osName;
        }

        public void setOsName(String osName) {
            this.osName = osName;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public void setOsVersion(String osVersion) {
            this.osVersion = osVersion;
        }

        public String getOsArch() {
            return osArch;
        }

        public void setOsArch(String osArch) {
            this.osArch = osArch;
        }

        public int getAvailableProcessors() {
            return availableProcessors;
        }

        public void setAvailableProcessors(int availableProcessors) {
            this.availableProcessors = availableProcessors;
        }

        public long getMaxMemory() {
            return maxMemory;
        }

        public void setMaxMemory(long maxMemory) {
            this.maxMemory = maxMemory;
        }

        public long getTotalMemory() {
            return totalMemory;
        }

        public void setTotalMemory(long totalMemory) {
            this.totalMemory = totalMemory;
        }

        public long getFreeMemory() {
            return freeMemory;
        }

        public void setFreeMemory(long freeMemory) {
            this.freeMemory = freeMemory;
        }

        public String getJavaVersion() {
            return javaVersion;
        }

        public void setJavaVersion(String javaVersion) {
            this.javaVersion = javaVersion;
        }

        public String getJavaVendor() {
            return javaVendor;
        }

        public void setJavaVendor(String javaVendor) {
            this.javaVendor = javaVendor;
        }

        @Override
        public String toString() {
            return "SystemInfo{" +
                    "osName='" + osName + '\'' +
                    ", osVersion='" + osVersion + '\'' +
                    ", osArch='" + osArch + '\'' +
                    ", availableProcessors=" + availableProcessors +
                    ", maxMemory=" + maxMemory / 1024 / 1024 + "MB" +
                    ", totalMemory=" + totalMemory / 1024 / 1024 + "MB" +
                    ", freeMemory=" + freeMemory / 1024 / 1024 + "MB" +
                    ", javaVersion='" + javaVersion + '\'' +
                    ", javaVendor='" + javaVendor + '\'' +
                    '}';
        }
    }
}
