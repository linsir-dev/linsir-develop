package com.linsir.abc.core.jvm.tuning.deployment;

/**
 * 垃圾收集器类型枚举
 * 定义了JVM支持的各种垃圾收集器及其适用场景
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public enum GarbageCollectorType {

    /**
     * G1垃圾收集器
     * 特点：平衡吞吐量和延迟
     * 适用场景：堆内存小于32GB
     * JVM参数：-XX:+UseG1GC
     */
    G1("G1", "-XX:+UseG1GC", "平衡吞吐量和延迟", "堆内存<32GB"),

    /**
     * ZGC垃圾收集器
     * 特点：超低延迟，停顿时间<10ms
     * 适用场景：超大堆、低延迟需求
     * JVM参数：-XX:+UseZGC
     */
    ZGC("ZGC", "-XX:+UseZGC", "超低延迟", "超大堆、低延迟需求"),

    /**
     * Shenandoah垃圾收集器
     * 特点：低延迟，与ZGC类似
     * 适用场景：需要低延迟的应用
     * JVM参数：-XX:+UseShenandoahGC
     */
    SHENANDOAH("Shenandoah", "-XX:+UseShenandoahGC", "低延迟", "低延迟需求"),

    /**
     * Parallel垃圾收集器
     * 特点：高吞吐量
     * 适用场景：后台批处理任务
     * JVM参数：-XX:+UseParallelGC
     */
    PARALLEL("Parallel", "-XX:+UseParallelGC", "高吞吐量", "后台批处理任务"),

    /**
     * CMS垃圾收集器（已废弃）
     * 特点：并发低停顿
     * 适用场景：JDK 8及以下版本
     * JVM参数：-XX:+UseConcMarkSweepGC
     */
    CMS("CMS", "-XX:+UseConcMarkSweepGC", "并发低停顿", "JDK 8及以下");

    /**
     * 收集器名称
     */
    private final String name;

    /**
     * JVM启动参数
     */
    private final String jvmOption;

    /**
     * 特点描述
     */
    private final String feature;

    /**
     * 适用场景
     */
    private final String applicableScenario;

    GarbageCollectorType(String name, String jvmOption, String feature, String applicableScenario) {
        this.name = name;
        this.jvmOption = jvmOption;
        this.feature = feature;
        this.applicableScenario = applicableScenario;
    }

    public String getName() {
        return name;
    }

    public String getJvmOption() {
        return jvmOption;
    }

    public String getFeature() {
        return feature;
    }

    public String getApplicableScenario() {
        return applicableScenario;
    }
}
