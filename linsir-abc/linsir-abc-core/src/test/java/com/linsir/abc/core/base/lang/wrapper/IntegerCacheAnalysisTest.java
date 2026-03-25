package com.linsir.abc.core.base.lang.wrapper;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * IntegerCacheAnalysis测试类
 */
public class IntegerCacheAnalysisTest {

    /**
     * 测试缓存实现分析
     */
    @Test
    public void testAnalyzeCacheImplementation() {
        IntegerCacheAnalysis analysis = new IntegerCacheAnalysis();
        // 此方法主要打印信息，不抛出异常即成功
        analysis.analyzeCacheImplementation();
    }

    /**
     * 测试缓存边界
     */
    @Test
    public void testCacheBoundaries() {
        IntegerCacheAnalysis analysis = new IntegerCacheAnalysis();
        // 此方法主要打印信息，不抛出异常即成功
        analysis.testCacheBoundaries();

        // 验证缓存边界
        Integer low = Integer.valueOf(-128);
        Integer low2 = Integer.valueOf(-128);
        assertSame(low, low2);

        Integer high = Integer.valueOf(127);
        Integer high2 = Integer.valueOf(127);
        assertSame(high, high2);

        Integer outside = Integer.valueOf(128);
        Integer outside2 = Integer.valueOf(128);
        assertNotSame(outside, outside2);
    }

    /**
     * 测试性能比较
     */
    @Test
    public void testComparePerformance() {
        IntegerCacheAnalysis analysis = new IntegerCacheAnalysis();
        // 此方法主要打印性能数据，不抛出异常即成功
        analysis.comparePerformance();
    }

    /**
     * 测试自动装箱行为
     */
    @Test
    public void testAnalyzeAutoBoxing() {
        IntegerCacheAnalysis analysis = new IntegerCacheAnalysis();
        // 此方法主要打印信息，不抛出异常即成功
        analysis.analyzeAutoBoxing();

        // 验证编译时常量优化
        Integer a = 100;
        Integer b = 50 + 50;
        assertSame(a, b);
    }

    /**
     * 测试线程安全性
     */
    @Test
    public void testDemonstrateThreadSafety() {
        IntegerCacheAnalysis analysis = new IntegerCacheAnalysis();
        // 此方法主要演示线程安全性，不抛出异常即成功
        analysis.demonstrateThreadSafety();
    }

    /**
     * 测试JVM参数分析
     */
    @Test
    public void testAnalyzeJvmParameter() {
        IntegerCacheAnalysis analysis = new IntegerCacheAnalysis();
        // 此方法主要打印信息，不抛出异常即成功
        analysis.analyzeJvmParameter();
    }

    /**
     * 测试最佳实践
     */
    @Test
    public void testProvideBestPractices() {
        IntegerCacheAnalysis analysis = new IntegerCacheAnalysis();
        // 此方法主要打印建议，不抛出异常即成功
        analysis.provideBestPractices();
    }

    /**
     * 测试所有分析
     */
    @Test
    public void testRunAllAnalysis() {
        IntegerCacheAnalysis analysis = new IntegerCacheAnalysis();
        // 运行所有分析，不抛出异常即成功
        analysis.runAllAnalysis();
    }
}
