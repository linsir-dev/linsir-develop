package com.linsir.abc.core.jvm.tuning.crash;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.logging.Logger;

/**
 * JVM崩溃分析器
 * 用于分析JVM进程崩溃的原因，包括OOM Killer、JNI调用崩溃等
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class JvmCrashAnalyzer {

    private static final Logger LOGGER = Logger.getLogger(JvmCrashAnalyzer.class.getName());

    /**
     * JVM崩溃日志文件名前缀
     */
    private static final String HS_ERR_PID_PREFIX = "hs_err_pid";

    /**
     * 崩溃日志文件扩展名
     */
    private static final String HS_ERR_PID_SUFFIX = ".log";

    /**
     * 日志目录
     */
    private final String logDirectory;

    public JvmCrashAnalyzer() {
        this("./logs");
    }

    public JvmCrashAnalyzer(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    /**
     * 分析可能的崩溃原因
     *
     * @return 分析报告
     */
    public CrashAnalysisReport analyze() {
        CrashAnalysisReport report = new CrashAnalysisReport();

        // 检查崩溃日志
        File crashLog = findLatestCrashLog();
        if (crashLog != null) {
            report.setCrashLogFound(true);
            report.setCrashLogPath(crashLog.getAbsolutePath());
            analyzeCrashLog(crashLog, report);
        }

        // 检查系统资源限制
        checkSystemLimits(report);

        // 检查JVM参数
        checkJvmArguments(report);

        return report;
    }

    /**
     * 查找最新的崩溃日志
     *
     * @return 崩溃日志文件，如果没有则返回null
     */
    private File findLatestCrashLog() {
        File logDir = new File(logDirectory);
        if (!logDir.exists() || !logDir.isDirectory()) {
            return null;
        }

        File[] files = logDir.listFiles((dir, name) ->
                name.startsWith(HS_ERR_PID_PREFIX) && name.endsWith(HS_ERR_PID_SUFFIX));

        if (files == null || files.length == 0) {
            return null;
        }

        // 找到最新的文件
        File latest = files[0];
        for (File file : files) {
            if (file.lastModified() > latest.lastModified()) {
                latest = file;
            }
        }

        return latest;
    }

    /**
     * 分析崩溃日志
     *
     * @param crashLog 崩溃日志文件
     * @param report   分析报告
     */
    private void analyzeCrashLog(File crashLog, CrashAnalysisReport report) {
        try {
            java.nio.file.Path path = crashLog.toPath();
            List<String> lines = java.nio.file.Files.readAllLines(path);

            for (String line : lines) {
                // 分析SIGSEGV（段错误）
                if (line.contains("SIGSEGV")) {
                    report.addPossibleCause("JNI调用导致访问非法内存");
                    report.setCrashType(CrashType.JNI_CRASH);
                }

                // 分析SIGABRT
                if (line.contains("SIGABRT")) {
                    report.addPossibleCause("JVM异常终止，可能是OOM Killer或资源耗尽");
                    report.setCrashType(CrashType.ABORT);
                }

                // 分析问题帧
                if (line.contains("Problematic frame:")) {
                    report.setProblematicFrame(line.trim());
                    if (line.contains("C [")) {
                        report.addPossibleCause("本地代码（C/C++）崩溃");
                    }
                }

                // 分析当前线程
                if (line.contains("Current thread")) {
                    report.setCurrentThreadInfo(line.trim());
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to analyze crash log: " + e.getMessage());
        }
    }

    /**
     * 检查系统限制
     *
     * @param report 分析报告
     */
    private void checkSystemLimits(CrashAnalysisReport report) {
        // 获取当前进程ID
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeMXBean.getName();
        String pid = jvmName.split("@")[0];

        report.setProcessId(pid);

        // 检查文件句柄限制
        try {
            File fdDir = new File("/proc/" + pid + "/fd");
            if (fdDir.exists() && fdDir.isDirectory()) {
                File[] fds = fdDir.listFiles();
                int openFds = fds != null ? fds.length : 0;
                report.setOpenFileDescriptors(openFds);

                if (openFds > 10000) {
                    report.addPossibleCause("文件句柄数过多（" + openFds + "），可能接近系统限制");
                }
            }
        } catch (Exception e) {
            LOGGER.fine("Cannot check file descriptors: " + e.getMessage());
        }

        // 检查线程数
        try {
            int threadCount = ManagementFactory.getThreadMXBean().getThreadCount();
            report.setThreadCount(threadCount);

            if (threadCount > 5000) {
                report.addPossibleCause("线程数过多（" + threadCount + "），可能超过系统限制");
            }
        } catch (Exception e) {
            LOGGER.fine("Cannot check thread count: " + e.getMessage());
        }
    }

    /**
     * 检查JVM参数
     *
     * @param report 分析报告
     */
    private void checkJvmArguments(CrashAnalysisReport report) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> inputArguments = runtimeMXBean.getInputArguments();

        report.setJvmArguments(inputArguments);

        // 检查是否有错误日志配置
        boolean hasErrorFile = inputArguments.stream()
                .anyMatch(arg -> arg.contains("-XX:ErrorFile"));
        if (!hasErrorFile) {
            report.addRecommendation("建议添加 -XX:ErrorFile 参数以捕获崩溃日志");
        }

        // 检查是否有堆转储配置
        boolean hasHeapDump = inputArguments.stream()
                .anyMatch(arg -> arg.contains("-XX:+HeapDumpOnOutOfMemoryError"));
        if (!hasHeapDump) {
            report.addRecommendation("建议添加 -XX:+HeapDumpOnOutOfMemoryError 参数以在OOM时生成堆转储");
        }
    }

    /**
     * 生成JVM参数建议
     *
     * @return JVM参数建议
     */
    public static String getRecommendedJvmOptions() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== JVM崩溃预防参数建议 ===\n\n");

        sb.append("1. 错误日志配置:\n");
        sb.append("   -XX:ErrorFile=/logs/hs_err_pid%p.log\n\n");

        sb.append("2. OOM时生成堆转储:\n");
        sb.append("   -XX:+HeapDumpOnOutOfMemoryError\n");
        sb.append("   -XX:HeapDumpPath=/logs/heapdump.hprof\n\n");

        sb.append("3. GC日志:\n");
        sb.append("   -XX:+PrintGCDetails\n");
        sb.append("   -XX:+PrintGCTimeStamps\n");
        sb.append("   -Xloggc:/logs/gc.log\n\n");

        sb.append("4. 增加资源限制:\n");
        sb.append("   -Xss512k  # 减少线程栈大小\n");
        sb.append("   -XX:MaxDirectMemorySize=1g  # 限制堆外内存\n\n");

        return sb.toString();
    }

    /**
     * 崩溃类型枚举
     */
    public enum CrashType {
        UNKNOWN,
        JNI_CRASH,
        OOM_KILLER,
        ABORT,
        SEGMENTATION_FAULT
    }

    /**
     * 崩溃分析报告
     */
    public static class CrashAnalysisReport {
        private boolean crashLogFound;
        private String crashLogPath;
        private CrashType crashType;
        private String problematicFrame;
        private String currentThreadInfo;
        private String processId;
        private int openFileDescriptors;
        private int threadCount;
        private List<String> jvmArguments;
        private final java.util.List<String> possibleCauses = new java.util.ArrayList<>();
        private final java.util.List<String> recommendations = new java.util.ArrayList<>();

        public boolean isCrashLogFound() {
            return crashLogFound;
        }

        public void setCrashLogFound(boolean crashLogFound) {
            this.crashLogFound = crashLogFound;
        }

        public String getCrashLogPath() {
            return crashLogPath;
        }

        public void setCrashLogPath(String crashLogPath) {
            this.crashLogPath = crashLogPath;
        }

        public CrashType getCrashType() {
            return crashType;
        }

        public void setCrashType(CrashType crashType) {
            this.crashType = crashType;
        }

        public String getProblematicFrame() {
            return problematicFrame;
        }

        public void setProblematicFrame(String problematicFrame) {
            this.problematicFrame = problematicFrame;
        }

        public String getCurrentThreadInfo() {
            return currentThreadInfo;
        }

        public void setCurrentThreadInfo(String currentThreadInfo) {
            this.currentThreadInfo = currentThreadInfo;
        }

        public String getProcessId() {
            return processId;
        }

        public void setProcessId(String processId) {
            this.processId = processId;
        }

        public int getOpenFileDescriptors() {
            return openFileDescriptors;
        }

        public void setOpenFileDescriptors(int openFileDescriptors) {
            this.openFileDescriptors = openFileDescriptors;
        }

        public int getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(int threadCount) {
            this.threadCount = threadCount;
        }

        public List<String> getJvmArguments() {
            return jvmArguments;
        }

        public void setJvmArguments(List<String> jvmArguments) {
            this.jvmArguments = jvmArguments;
        }

        public void addPossibleCause(String cause) {
            possibleCauses.add(cause);
        }

        public java.util.List<String> getPossibleCauses() {
            return new java.util.ArrayList<>(possibleCauses);
        }

        public void addRecommendation(String recommendation) {
            recommendations.add(recommendation);
        }

        public java.util.List<String> getRecommendations() {
            return new java.util.ArrayList<>(recommendations);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== JVM崩溃分析报告 ===\n\n");
            sb.append("崩溃日志找到: ").append(crashLogFound).append("\n");
            if (crashLogFound) {
                sb.append("日志路径: ").append(crashLogPath).append("\n");
            }
            sb.append("崩溃类型: ").append(crashType).append("\n");
            sb.append("进程ID: ").append(processId).append("\n");
            sb.append("打开的文件句柄: ").append(openFileDescriptors).append("\n");
            sb.append("线程数: ").append(threadCount).append("\n\n");

            if (!possibleCauses.isEmpty()) {
                sb.append("可能原因:\n");
                for (String cause : possibleCauses) {
                    sb.append("  - ").append(cause).append("\n");
                }
                sb.append("\n");
            }

            if (!recommendations.isEmpty()) {
                sb.append("建议:\n");
                for (String rec : recommendations) {
                    sb.append("  - ").append(rec).append("\n");
                }
            }

            return sb.toString();
        }
    }
}
