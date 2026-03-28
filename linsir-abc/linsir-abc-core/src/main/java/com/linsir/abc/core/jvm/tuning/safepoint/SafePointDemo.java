package com.linsir.abc.core.jvm.tuning.safepoint;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * 安全点演示类
 * 演示安全点导致的长时间停顿问题及解决方案
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class SafePointDemo {

    private static final Logger LOGGER = Logger.getLogger(SafePointDemo.class.getName());

    /**
     * 问题示例：使用int计数的大循环
     * JVM无法在计数循环中插入安全点检查，导致长时间停顿
     */
    public void problematicIntLoop() {
        LOGGER.info("Starting problematic int loop...");
        long startTime = System.currentTimeMillis();

        // 问题：使用int类型，JVM无法在此循环中插入安全点
        for (int i = 0; i < 1_000_000_000; i++) {
            // 模拟一些计算
            int result = i * i;
            if (result < 0) {
                break;
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info("Problematic int loop completed in " + duration + "ms");
    }

    /**
     * 解决方案1：使用long类型
     * JVM会在long类型的循环中插入安全点检查
     */
    public void fixedLongLoop() {
        LOGGER.info("Starting fixed long loop...");
        long startTime = System.currentTimeMillis();

        // 解决方案：使用long类型，JVM会在每次迭代检查安全点
        for (long i = 0; i < 1_000_000_000L; i++) {
            // 模拟一些计算
            long result = i * i;
            if (result < 0) {
                break;
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info("Fixed long loop completed in " + duration + "ms");
    }

    /**
     * 解决方案2：在循环中插入安全点检查
     * 定期调用Thread.yield()或执行可能触发安全点的操作
     */
    public void fixedWithSafePointPoll() {
        LOGGER.info("Starting loop with safe point poll...");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1_000_000_000; i++) {
            // 模拟一些计算
            int result = i * i;

            // 解决方案：每隔一定次数插入安全点检查
            if (i % 1000 == 0) {
                Thread.yield(); // 主动让出CPU，检查安全点
            }

            if (result < 0) {
                break;
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info("Loop with safe point poll completed in " + duration + "ms");
    }

    /**
     * 解决方案3：使用可中断的计算方式
     * 将大循环拆分为多个小循环
     */
    public void fixedWithChunkedLoop() {
        LOGGER.info("Starting chunked loop...");
        long startTime = System.currentTimeMillis();

        int chunkSize = 1_000_000;
        int totalIterations = 1_000_000_000;

        for (int chunk = 0; chunk < totalIterations / chunkSize; chunk++) {
            // 处理一个chunk
            for (int i = chunk * chunkSize; i < (chunk + 1) * chunkSize; i++) {
                int result = i * i;
                if (result < 0) {
                    break;
                }
            }

            // 每个chunk结束后检查安全点
            Thread.yield();
        }

        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info("Chunked loop completed in " + duration + "ms");
    }

    /**
     * 解决方案4：使用并行流
     * 利用ForkJoinPool自动处理安全点
     */
    public void fixedWithParallelStream() {
        LOGGER.info("Starting parallel stream processing...");
        long startTime = System.currentTimeMillis();

        // 使用并行流处理大量数据
        long count = java.util.stream.LongStream.range(0, 1_000_000_000L)
                .parallel()
                .map(i -> i * i)
                .filter(result -> result > 0)
                .count();

        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info("Parallel stream completed in " + duration + "ms, count=" + count);
    }

    /**
     * 模拟长时间运行的计算任务
     * 展示如何在实际业务代码中处理安全点问题
     */
    public void processLargeDataset(int[] data) {
        LOGGER.info("Processing large dataset with " + data.length + " elements...");
        long startTime = System.currentTimeMillis();

        // 使用分块处理
        int chunkSize = 10000;
        long totalSum = 0;

        for (int i = 0; i < data.length; i += chunkSize) {
            int end = Math.min(i + chunkSize, data.length);
            long chunkSum = 0;

            for (int j = i; j < end; j++) {
                chunkSum += data[j];
            }

            totalSum += chunkSum;

            // 每个chunk结束后让出CPU
            if (i % (chunkSize * 10) == 0) {
                Thread.yield();
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        LOGGER.info("Dataset processing completed in " + duration + "ms, totalSum=" + totalSum);
    }

    /**
     * 生成JVM参数建议
     *
     * @return JVM参数建议
     */
    public static String getJvmOptionsRecommendation() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 安全点优化JVM参数建议 ===\n\n");

        sb.append("1. 打印安全点统计信息（用于排查）:\n");
        sb.append("   -XX:+PrintSafepointStatistics\n");
        sb.append("   -XX:PrintSafepointStatisticsCount=1\n\n");

        sb.append("2. 打印应用停顿时间:\n");
        sb.append("   -XX:+PrintGCApplicationStoppedTime\n\n");

        sb.append("3. 强制在计数循环中插入安全点（谨慎使用，有性能开销）:\n");
        sb.append("   -XX:+UnlockDiagnosticVMOptions\n");
        sb.append("   -XX:+UseCountedLoopSafepoints\n\n");

        sb.append("4. 启用GC日志:\n");
        sb.append("   -Xlog:gc*:file=/logs/gc.log:time,uptime,level,tags:filecount=5,filesize=100m\n\n");

        sb.append("5. 安全点日志:\n");
        sb.append("   -Xlog:safepoint*:file=/logs/safepoint.log:time,uptime,level,tags\n\n");

        sb.append("代码优化建议:\n");
        sb.append("1. 将int循环变量改为long类型\n");
        sb.append("2. 在长时间运行的循环中定期调用Thread.yield()\n");
        sb.append("3. 将大循环拆分为多个小循环\n");
        sb.append("4. 使用并行流处理大量数据\n");
        sb.append("5. 避免在循环中执行同步IO操作\n");

        return sb.toString();
    }

    /**
     * 安全点日志解析示例
     *
     * @param logLine 日志行
     * @return 解析结果
     */
    public static String parseSafepointLog(String logLine) {
        // 示例日志格式：
        // vmop                    [threads: total initially_running wait_to_block]    [time: spin block sync cleanup vmop] page_trap_count
        // 0.302: no vm operation  [      27          0              0    ]      [0     0     0     0     0    ]  0

        if (logLine == null || logLine.trim().isEmpty()) {
            return "Empty log line";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== 安全点日志解析 ===\n");

        if (logLine.contains("spin") || logLine.contains("block")) {
            sb.append("检测到安全点停顿\n");

            if (logLine.contains("spin") && !logLine.contains("spin 0")) {
                sb.append("- spin时间非零：线程进入安全点较慢\n");
            }

            if (logLine.contains("block") && !logLine.contains("block 0")) {
                sb.append("- block时间非零：线程被阻塞等待安全点\n");
            }
        }

        sb.append("原始日志: ").append(logLine);

        return sb.toString();
    }
}
