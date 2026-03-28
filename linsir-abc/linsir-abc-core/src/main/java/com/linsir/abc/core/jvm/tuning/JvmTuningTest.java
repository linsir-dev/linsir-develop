package com.linsir.abc.core.jvm.tuning;

import com.linsir.abc.core.jvm.tuning.cluster.BoundedMessageQueue;
import com.linsir.abc.core.jvm.tuning.cluster.ClusterMessage;
import com.linsir.abc.core.jvm.tuning.cluster.ClusterSyncManager;
import com.linsir.abc.core.jvm.tuning.cluster.MessageType;
import com.linsir.abc.core.jvm.tuning.crash.JvmCrashAnalyzer;
import com.linsir.abc.core.jvm.tuning.datastructure.MemoryEfficientList;
import com.linsir.abc.core.jvm.tuning.datastructure.MemoryEfficientMap;
import com.linsir.abc.core.jvm.tuning.deployment.GarbageCollectorType;
import com.linsir.abc.core.jvm.tuning.deployment.JvmDeploymentConfig;
import com.linsir.abc.core.jvm.tuning.deployment.MultiInstanceDeployment;
import com.linsir.abc.core.jvm.tuning.external.CommandExecutor;
import com.linsir.abc.core.jvm.tuning.external.SystemInfoProvider;
import com.linsir.abc.core.jvm.tuning.offheap.DirectBufferLeakDemo;
import com.linsir.abc.core.jvm.tuning.offheap.DirectBufferManager;
import com.linsir.abc.core.jvm.tuning.offheap.OffHeapMemoryMonitor;
import com.linsir.abc.core.jvm.tuning.safepoint.SafePointDemo;
import com.linsir.abc.core.jvm.tuning.virtualmemory.VirtualMemoryMonitor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * JVM调优测试类
 * 用于验证各调优案例的实现
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class JvmTuningTest {

    private static final Logger LOGGER = Logger.getLogger(JvmTuningTest.class.getName());

    public static void main(String[] args) {
        LOGGER.info("=== JVM调优案例测试开始 ===\n");

        testDeploymentStrategy();
        testClusterSync();
        testOffHeapMemory();
        testExternalCommand();
        testJvmCrashAnalysis();
        testMemoryEfficientDataStructures();
        testVirtualMemory();
        testSafePoint();

        LOGGER.info("\n=== JVM调优案例测试完成 ===");
    }

    /**
     * 测试5.2.1 大内存硬件部署策略
     */
    private static void testDeploymentStrategy() {
        LOGGER.info("\n--- 测试5.2.1 大内存硬件部署策略 ---");

        // 测试多实例部署配置
        MultiInstanceDeployment deployment = new MultiInstanceDeployment(64, 4, GarbageCollectorType.G1);

        LOGGER.info("服务器总内存: " + deployment.getTotalMemoryGb() + "GB");
        LOGGER.info("实例数量: " + deployment.getInstanceCount());
        LOGGER.info("GC类型: " + deployment.getGcType().getName());

        // 生成启动命令
        List<String> commands = deployment.generateAllStartCommands("application.jar");
        LOGGER.info("生成的启动命令:");
        for (String cmd : commands) {
            LOGGER.info("  " + cmd);
        }

        // 打印方案对比
        LOGGER.info("\n" + MultiInstanceDeployment.getDeploymentComparisonReport());

        // 测试GC时间估算
        long gcTime = MultiInstanceDeployment.estimateFullGcTime(16, GarbageCollectorType.G1);
        LOGGER.info("预估Full GC时间（16GB堆，G1）: " + gcTime + "ms");
    }

    /**
     * 测试5.2.2 集群间同步导致的内存溢出
     */
    private static void testClusterSync() {
        LOGGER.info("\n--- 测试5.2.2 集群间同步导致的内存溢出 ---");

        // 创建集群同步管理器
        ClusterSyncManager syncManager = new ClusterSyncManager("node-1", 1000);
        syncManager.start();

        // 测试消息队列
        BoundedMessageQueue queue = syncManager.getMessageQueue();
        LOGGER.info("队列初始状态: " + queue.getStatistics());

        // 发送消息
        for (int i = 0; i < 100; i++) {
            ClusterMessage message = new ClusterMessage(
                    "msg-" + i,
                    MessageType.CACHE_SYNC,
                    "node-1",
                    null,
                    new byte[1024], // 1KB payload
                    30000
            );
            syncManager.receive(message);
        }

        LOGGER.info("发送100条消息后: " + queue.getStatistics());

        // 测试队列满的情况
        for (int i = 0; i < 2000; i++) {
            ClusterMessage message = new ClusterMessage(
                    "msg-overflow-" + i,
                    MessageType.CACHE_SYNC,
                    "node-1",
                    null,
                    new byte[1024],
                    30000
            );
            syncManager.receive(message);
        }

        LOGGER.info("尝试发送2000条消息后: " + queue.getStatistics());
        LOGGER.info("丢弃消息数: " + queue.getDroppedMessageCount());

        syncManager.stop();
        LOGGER.info("集群同步管理器统计: " + syncManager.getStatistics());
    }

    /**
     * 测试5.2.3 堆外内存导致的溢出错误
     */
    private static void testOffHeapMemory() {
        LOGGER.info("\n--- 测试5.2.3 堆外内存导致的溢出错误 ---");

        // 测试DirectBufferManager
        DirectBufferManager manager = new DirectBufferManager(100 * 1024 * 1024, true); // 100MB限制

        LOGGER.info("初始状态: " + manager.getStatistics());

        // 分配缓冲区
        List<ByteBuffer> buffers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ByteBuffer buffer = manager.allocate("buffer-" + i, 1024 * 1024); // 1MB
            buffers.add(buffer);
        }

        LOGGER.info("分配10个1MB缓冲区后: " + manager.getStatistics());

        // 释放部分缓冲区
        for (int i = 0; i < 5; i++) {
            manager.release("buffer-" + i);
        }

        LOGGER.info("释放5个缓冲区后: " + manager.getStatistics());

        // 打印JVM参数建议
        LOGGER.info("\n" + DirectBufferLeakDemo.getJvmOptionsRecommendation());

        manager.shutdown();
    }

    /**
     * 测试5.2.4 外部命令导致系统缓慢
     */
    private static void testExternalCommand() {
        LOGGER.info("\n--- 测试5.2.4 外部命令导致系统缓慢 ---");

        // 测试SystemInfoProvider
        SystemInfoProvider infoProvider = new SystemInfoProvider();

        // 使用Java API获取系统信息
        String sysInfo = infoProvider.getSystemInfoWithJavaApi();
        LOGGER.info("系统信息（Java API）:\n" + sysInfo);

        // 使用缓存获取
        SystemInfoProvider.SystemInfo cachedInfo = infoProvider.getSystemInfoWithCache();
        LOGGER.info("缓存的系统信息: " + cachedInfo);

        // 测试CommandExecutor
        CommandExecutor executor = new CommandExecutor(2, 5, TimeUnit.SECONDS);

        try {
            // 执行简单命令
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String result = executor.execute("java -version");
                LOGGER.info("命令执行结果:\n" + result.substring(0, Math.min(result.length(), 200)));
            } else {
                String result = executor.execute("uname -a");
                LOGGER.info("命令执行结果: " + result);
            }
        } catch (Exception e) {
            LOGGER.warning("命令执行失败: " + e.getMessage());
        }

        executor.shutdown();
        infoProvider.shutdown();
    }

    /**
     * 测试5.2.5 服务器虚拟机进程崩溃
     */
    private static void testJvmCrashAnalysis() {
        LOGGER.info("\n--- 测试5.2.5 服务器虚拟机进程崩溃 ---");

        // 创建崩溃分析器
        JvmCrashAnalyzer analyzer = new JvmCrashAnalyzer("./logs");

        // 分析
        JvmCrashAnalyzer.CrashAnalysisReport report = analyzer.analyze();

        LOGGER.info("崩溃分析报告:\n" + report);

        // 打印JVM参数建议
        LOGGER.info("\n" + JvmCrashAnalyzer.getRecommendedJvmOptions());
    }

    /**
     * 测试5.2.6 不恰当数据结构导致内存占用过大
     */
    private static void testMemoryEfficientDataStructures() {
        LOGGER.info("\n--- 测试5.2.6 不恰当数据结构导致内存占用过大 ---");

        // 测试内存高效的List
        int elementCount = 1000000;
        LOGGER.info(MemoryEfficientList.getMemoryComparisonReport(elementCount));

        // 测试IntArrayList
        int[] intData = new int[10000];
        for (int i = 0; i < intData.length; i++) {
            intData[i] = i;
        }

        MemoryEfficientList.IntArrayList intList = MemoryEfficientList.efficientIntList(intData);
        LOGGER.info("IntArrayList大小: " + intList.size());
        LOGGER.info("IntArrayList前5个元素: " + intList.get(0) + ", " + intList.get(1) + ", " + intList.get(2));

        // 测试内存高效的Map
        LOGGER.info("\n" + MemoryEfficientMap.getMemoryComparisonReport(elementCount));

        // 测试IntKeyMap
        MemoryEfficientMap.IntKeyMap<String> intKeyMap = new MemoryEfficientMap.IntKeyMap<>();
        for (int i = 0; i < 1000; i++) {
            intKeyMap.put(i, "value-" + i);
        }
        LOGGER.info("IntKeyMap大小: " + intKeyMap.size());
        LOGGER.info("IntKeyMap获取key=500: " + intKeyMap.get(500));
    }

    /**
     * 测试5.2.7 Windows虚拟内存导致的长时间停顿
     */
    private static void testVirtualMemory() {
        LOGGER.info("\n--- 测试5.2.7 Windows虚拟内存导致的长时间停顿 ---");

        // 获取系统内存信息
        VirtualMemoryMonitor monitor = new VirtualMemoryMonitor(10);
        VirtualMemoryMonitor.SystemMemoryInfo memoryInfo = monitor.getSystemMemoryInfo();

        LOGGER.info("系统内存信息: " + memoryInfo);

        // 打印JVM参数建议
        LOGGER.info("\n" + VirtualMemoryMonitor.getJvmOptionsRecommendation());
    }

    /**
     * 测试5.2.8 安全点导致长时间停顿
     */
    private static void testSafePoint() {
        LOGGER.info("\n--- 测试5.2.8 安全点导致长时间停顿 ---");

        SafePointDemo demo = new SafePointDemo();

        // 测试修复后的long循环
        demo.fixedLongLoop();

        // 测试带安全点检查的循环
        demo.fixedWithSafePointPoll();

        // 测试分块循环
        demo.fixedWithChunkedLoop();

        // 测试数据集处理
        int[] data = new int[1000000];
        Arrays.fill(data, 1);
        demo.processLargeDataset(data);

        // 打印JVM参数建议
        LOGGER.info("\n" + SafePointDemo.getJvmOptionsRecommendation());

        // 测试日志解析
        String sampleLog = "0.303: CGC_Operation    [      27          1              1    ]      [0     0     2     0     5    ]  0";
        LOGGER.info("\n" + SafePointDemo.parseSafepointLog(sampleLog));
    }
}
