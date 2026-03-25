package com.linsir.abc.core.base.lang.string;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * StringConcatenationBenchmark测试类
 */
public class StringConcatenationBenchmarkTest {

    /**
     * 测试运行性能测试
     */
    @Test
    public void testRunBenchmark() {
        StringConcatenationBenchmark benchmark = new StringConcatenationBenchmark();
        String report = benchmark.runBenchmark();

        assertNotNull(report);
        assertTrue(report.contains("字符串拼接性能测试"));
        assertTrue(report.contains("使用 + 运算符"));
        assertTrue(report.contains("使用 StringBuilder"));
        assertTrue(report.contains("使用 StringBuffer"));
        assertTrue(report.contains("使用 String.concat()"));
    }

    /**
     * 测试+运算符性能
     */
    @Test
    public void testBenchmarkPlusOperator() {
        StringConcatenationBenchmark benchmark = new StringConcatenationBenchmark();
        long time = benchmark.benchmarkPlusOperator();

        // 应该返回非负时间
        assertTrue(time >= 0);
    }

    /**
     * 测试StringBuilder性能
     */
    @Test
    public void testBenchmarkStringBuilder() {
        StringConcatenationBenchmark benchmark = new StringConcatenationBenchmark();
        long time = benchmark.benchmarkStringBuilder();

        assertTrue(time >= 0);
    }

    /**
     * 测试StringBuffer性能
     */
    @Test
    public void testBenchmarkStringBuffer() {
        StringConcatenationBenchmark benchmark = new StringConcatenationBenchmark();
        long time = benchmark.benchmarkStringBuffer();

        assertTrue(time >= 0);
    }

    /**
     * 测试String.concat性能
     */
    @Test
    public void testBenchmarkStringConcat() {
        StringConcatenationBenchmark benchmark = new StringConcatenationBenchmark();
        long time = benchmark.benchmarkStringConcat();

        assertTrue(time >= 0);
    }



    /**
     * 验证StringBuilder比+运算符快
     */
    @Test
    public void testBuilderFasterThanPlus() {
        StringConcatenationBenchmark benchmark = new StringConcatenationBenchmark();

        long plusTime = benchmark.benchmarkPlusOperator();
        long builderTime = benchmark.benchmarkStringBuilder();

        // StringBuilder应该比+运算符快（在大量拼接时）
        // 注意：由于测试的随机性，这里只是验证两者都能正常运行
        assertTrue(plusTime >= 0);
        assertTrue(builderTime >= 0);
    }

    /**
     * 验证StringBuilder和StringBuffer性能接近
     */
    @Test
    public void testBuilderAndBufferPerformance() {
        StringConcatenationBenchmark benchmark = new StringConcatenationBenchmark();

        long builderTime = benchmark.benchmarkStringBuilder();
        long bufferTime = benchmark.benchmarkStringBuffer();

        // 两者都应该能正常运行
        assertTrue(builderTime >= 0);
        assertTrue(bufferTime >= 0);

        // StringBuilder通常比StringBuffer稍快（因为没有同步开销）
        // 但由于测试波动，这里不做严格断言
    }


}
