package com.linsir.abc.core.jvm.gc.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GC日志分析工具
 * 
 * 用于解析和分析JVM GC日志，提取关键信息如：
 * - GC类型（Minor GC、Full GC）
 * - 内存使用情况（新生代、老年代、永久代/元空间）
 * - GC耗时
 * - 回收效率
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class GCLogAnalyzer {

    /**
     * GC事件记录
     */
    public static class GCEvent {
        
        /**
         * GC类型
         */
        public enum GCType {
            /**
             * 新生代GC
             */
            MINOR_GC,
            /**
             * 老年代GC
             */
            FULL_GC,
            /**
             * G1收集器的Young GC
             */
            G1_YOUNG,
            /**
             * G1收集器的Mixed GC
             */
            G1_MIXED,
            /**
             * G1收集器的Full GC
             */
            G1_FULL,
            /**
             * 未知类型
             */
            UNKNOWN
        }

        private GCType type;
        private long timestamp;
        private long beforeHeap;
        private long afterHeap;
        private long totalHeap;
        private long beforeYoung;
        private long afterYoung;
        private long totalYoung;
        private long beforeOld;
        private long afterOld;
        private long totalOld;
        private double duration;
        private String rawLog;

        // Getters
        public GCType getType() { return type; }
        public void setType(GCType type) { this.type = type; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public long getBeforeHeap() { return beforeHeap; }
        public void setBeforeHeap(long beforeHeap) { this.beforeHeap = beforeHeap; }
        public long getAfterHeap() { return afterHeap; }
        public void setAfterHeap(long afterHeap) { this.afterHeap = afterHeap; }
        public long getTotalHeap() { return totalHeap; }
        public void setTotalHeap(long totalHeap) { this.totalHeap = totalHeap; }
        public long getBeforeYoung() { return beforeYoung; }
        public void setBeforeYoung(long beforeYoung) { this.beforeYoung = beforeYoung; }
        public long getAfterYoung() { return afterYoung; }
        public void setAfterYoung(long afterYoung) { this.afterYoung = afterYoung; }
        public long getTotalYoung() { return totalYoung; }
        public void setTotalYoung(long totalYoung) { this.totalYoung = totalYoung; }
        public long getBeforeOld() { return beforeOld; }
        public void setBeforeOld(long beforeOld) { this.beforeOld = beforeOld; }
        public long getAfterOld() { return afterOld; }
        public void setAfterOld(long afterOld) { this.afterOld = afterOld; }
        public long getTotalOld() { return totalOld; }
        public void setTotalOld(long totalOld) { this.totalOld = totalOld; }
        public double getDuration() { return duration; }
        public void setDuration(double duration) { this.duration = duration; }
        public String getRawLog() { return rawLog; }
        public void setRawLog(String rawLog) { this.rawLog = rawLog; }

        /**
         * 获取回收的内存大小
         * 
         * @return 回收的内存大小（KB）
         */
        public long getReclaimedMemory() {
            return beforeHeap - afterHeap;
        }

        /**
         * 获取回收效率
         * 
         * @return 回收效率（0-1之间）
         */
        public double getReclamationRatio() {
            if (beforeHeap == 0) return 0;
            return (double) getReclaimedMemory() / beforeHeap;
        }

        @Override
        public String toString() {
            return String.format(
                "GCEvent{type=%s, duration=%.3fs, heap: %dK->%dK(%dK), reclaimed=%dK, ratio=%.2f%%}",
                type, duration, beforeHeap, afterHeap, totalHeap,
                getReclaimedMemory(), getReclamationRatio() * 100
            );
        }
    }

    /**
     * GC统计信息
     */
    public static class GCStatistics {
        private int totalGCCount = 0;
        private int minorGCCount = 0;
        private int fullGCCount = 0;
        private double totalGCTime = 0;
        private double maxGCTime = 0;
        private double minGCTime = Double.MAX_VALUE;
        private long totalReclaimedMemory = 0;
        private List<GCEvent> events = new ArrayList<>();

        public void addEvent(GCEvent event) {
            events.add(event);
            totalGCCount++;
            totalGCTime += event.getDuration();
            maxGCTime = Math.max(maxGCTime, event.getDuration());
            minGCTime = Math.min(minGCTime, event.getDuration());
            totalReclaimedMemory += event.getReclaimedMemory();

            switch (event.getType()) {
                case MINOR_GC:
                case G1_YOUNG:
                    minorGCCount++;
                    break;
                case FULL_GC:
                case G1_FULL:
                    fullGCCount++;
                    break;
                default:
                    break;
            }
        }

        // Getters
        public int getTotalGCCount() { return totalGCCount; }
        public int getMinorGCCount() { return minorGCCount; }
        public int getFullGCCount() { return fullGCCount; }
        public double getTotalGCTime() { return totalGCTime; }
        public double getMaxGCTime() { return maxGCTime; }
        public double getMinGCTime() { 
            return minGCTime == Double.MAX_VALUE ? 0 : minGCTime; 
        }
        public double getAverageGCTime() { 
            return totalGCCount == 0 ? 0 : totalGCTime / totalGCCount; 
        }
        public long getTotalReclaimedMemory() { return totalReclaimedMemory; }
        public List<GCEvent> getEvents() { return events; }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== GC统计信息 ===\n");
            sb.append("总GC次数: ").append(totalGCCount).append("\n");
            sb.append("  - Minor GC: ").append(minorGCCount).append("\n");
            sb.append("  - Full GC: ").append(fullGCCount).append("\n");
            sb.append("总GC时间: ").append(String.format("%.3f", totalGCTime)).append("s\n");
            sb.append("平均GC时间: ").append(String.format("%.3f", getAverageGCTime())).append("s\n");
            sb.append("最大GC时间: ").append(String.format("%.3f", maxGCTime)).append("s\n");
            sb.append("最小GC时间: ").append(String.format("%.3f", getMinGCTime())).append("s\n");
            sb.append("总回收内存: ").append(totalReclaimedMemory / 1024).append("MB\n");
            return sb.toString();
        }
    }

    // GC日志解析正则表达式
    private static final Pattern MINOR_GC_PATTERN = Pattern.compile(
        "\\[GC.*?(\\d+)K->(\\d+)K\\((\\d+)K\\).*?(\\d+\\.\\d+)"
    );
    private static final Pattern FULL_GC_PATTERN = Pattern.compile(
        "\\[Full GC.*?(\\d+)K->(\\d+)K\\((\\d+)K\\).*?(\\d+\\.\\d+)"
    );

    /**
     * 解析单行GC日志
     * 
     * @param logLine GC日志行
     * @return GC事件对象，如果无法解析则返回null
     */
    public GCEvent parseLogLine(String logLine) {
        if (logLine == null || logLine.trim().isEmpty()) {
            return null;
        }

        GCEvent event = new GCEvent();
        event.setRawLog(logLine);

        // 判断GC类型
        if (logLine.contains("Full GC")) {
            event.setType(GCEvent.GCType.FULL_GC);
        } else if (logLine.contains("GC")) {
            event.setType(GCEvent.GCType.MINOR_GC);
        } else {
            return null;
        }

        // 解析堆内存变化
        Matcher matcher = MINOR_GC_PATTERN.matcher(logLine);
        if (!matcher.find()) {
            matcher = FULL_GC_PATTERN.matcher(logLine);
            if (!matcher.find()) {
                return null;
            }
        }

        try {
            event.setBeforeHeap(Long.parseLong(matcher.group(1)));
            event.setAfterHeap(Long.parseLong(matcher.group(2)));
            event.setTotalHeap(Long.parseLong(matcher.group(3)));
            event.setDuration(Double.parseDouble(matcher.group(4)));
        } catch (NumberFormatException e) {
            return null;
        }

        return event;
    }

    /**
     * 分析GC日志文件
     * 
     * @param filePath 日志文件路径
     * @return GC统计信息
     * @throws IOException 当读取文件失败时抛出
     */
    public GCStatistics analyzeLogFile(String filePath) throws IOException {
        GCStatistics statistics = new GCStatistics();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                GCEvent event = parseLogLine(line);
                if (event != null) {
                    statistics.addEvent(event);
                }
            }
        }

        return statistics;
    }

    /**
     * 分析GC日志内容
     * 
     * @param logContent 日志内容
     * @return GC统计信息
     */
    public GCStatistics analyzeLogContent(String logContent) {
        GCStatistics statistics = new GCStatistics();

        String[] lines = logContent.split("\\r?\\n");
        for (String line : lines) {
            GCEvent event = parseLogLine(line);
            if (event != null) {
                statistics.addEvent(event);
            }
        }

        return statistics;
    }

    /**
     * 生成GC报告
     * 
     * @param statistics GC统计信息
     * @return 格式化的报告字符串
     */
    public String generateReport(GCStatistics statistics) {
        StringBuilder report = new StringBuilder();
        
        report.append(statistics.toString());
        
        report.append("\n=== GC事件详情 ===\n");
        int count = 0;
        for (GCEvent event : statistics.getEvents()) {
            report.append("[").append(++count).append("] ").append(event).append("\n");
            if (count >= 10) {
                report.append("... 共 ").append(statistics.getEvents().size()).append(" 个事件\n");
                break;
            }
        }

        // 性能建议
        report.append("\n=== 性能建议 ===\n");
        if (statistics.getFullGCCount() > statistics.getMinorGCCount() / 10) {
            report.append("⚠ Full GC次数过多，建议检查：\n");
            report.append("  - 老年代内存配置是否充足\n");
            report.append("  - 是否存在内存泄漏\n");
            report.append("  - 大对象是否直接进入老年代\n");
        }
        
        if (statistics.getAverageGCTime() > 0.5) {
            report.append("⚠ 平均GC时间过长，建议：\n");
            report.append("  - 考虑使用并发收集器（CMS/G1/ZGC）\n");
            report.append("  - 增加堆内存大小\n");
            report.append("  - 优化对象创建和生命周期\n");
        }

        if (statistics.getTotalGCCount() == 0) {
            report.append("ℹ 未检测到GC事件\n");
        } else if (statistics.getFullGCCount() == 0 && statistics.getAverageGCTime() < 0.1) {
            report.append("✓ GC性能良好\n");
        }

        return report.toString();
    }

    /**
     * 主方法 - 演示用法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        GCLogAnalyzer analyzer = new GCLogAnalyzer();

        // 示例GC日志
        String sampleLog = 
            "[GC (Allocation Failure)  6487K->152K(9216K), 0.0049383 secs]\n" +
            "[GC (Allocation Failure)  6296K->152K(9216K), 0.0034567 secs]\n" +
            "[Full GC (Ergonomics)  8192K->6144K(19456K), 0.1234567 secs]\n" +
            "[GC (Allocation Failure)  6144K->152K(9216K), 0.0023456 secs]\n";

        System.out.println("示例GC日志分析:\n");
        System.out.println(sampleLog);

        GCStatistics statistics = analyzer.analyzeLogContent(sampleLog);
        String report = analyzer.generateReport(statistics);
        System.out.println(report);
    }
}
