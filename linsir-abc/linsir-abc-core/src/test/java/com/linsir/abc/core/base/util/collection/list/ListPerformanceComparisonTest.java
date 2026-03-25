package com.linsir.abc.core.base.util.collection.list;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ListPerformanceComparison测试类
 */
public class ListPerformanceComparisonTest {

    /**
     * 测试运行所有性能测试
     */
    @Test
    public void testRunAllBenchmarks() {
        ListPerformanceComparison comparison = new ListPerformanceComparison();
        // 运行所有性能测试，不抛出异常即成功
        comparison.runAllBenchmarks();
    }

    /**
     * 测试随机访问性能
     */
    @Test
    public void testBenchmarkRandomAccess() {
        ListPerformanceComparison comparison = new ListPerformanceComparison();
        // 测试随机访问性能，不抛出异常即成功
        comparison.benchmarkRandomAccess();
    }

    /**
     * 测试添加操作性能
     */
    @Test
    public void testBenchmarkAddOperations() {
        ListPerformanceComparison comparison = new ListPerformanceComparison();
        // 测试添加操作性能，不抛出异常即成功
        comparison.benchmarkAddOperations();
    }

    /**
     * 测试删除操作性能
     */
    @Test
    public void testBenchmarkRemoveOperations() {
        ListPerformanceComparison comparison = new ListPerformanceComparison();
        // 测试删除操作性能，不抛出异常即成功
        comparison.benchmarkRemoveOperations();
    }

    /**
     * 测试遍历性能
     */
    @Test
    public void testBenchmarkIteration() {
        ListPerformanceComparison comparison = new ListPerformanceComparison();
        // 测试遍历性能，不抛出异常即成功
        comparison.benchmarkIteration();
    }

    /**
     * 测试内存占用
     */
    @Test
    public void testBenchmarkMemoryUsage() {
        ListPerformanceComparison comparison = new ListPerformanceComparison();
        // 测试内存占用，不抛出异常即成功
        comparison.benchmarkMemoryUsage();
    }

    /**
     * 测试RandomAccess接口演示
     */
    @Test
    public void testDemonstrateRandomAccess() {
        ListPerformanceComparison comparison = new ListPerformanceComparison();
        // 演示RandomAccess接口，不抛出异常即成功
        comparison.demonstrateRandomAccess();
    }

    /**
     * 验证ArrayList实现RandomAccess接口
     */
    @Test
    public void testArrayListImplementsRandomAccess() {
        java.util.ArrayList<String> arrayList = new java.util.ArrayList<>();
        assertTrue(arrayList instanceof java.util.RandomAccess);
    }

    /**
     * 验证LinkedList不实现RandomAccess接口
     */
    @Test
    public void testLinkedListNotImplementsRandomAccess() {
        java.util.LinkedList<String> linkedList = new java.util.LinkedList<>();
        assertFalse(linkedList instanceof java.util.RandomAccess);
    }

    /**
     * 测试结果类
     */
    @Test
    public void testBenchmarkResult() {
        ListPerformanceComparison.BenchmarkResult result = 
            new ListPerformanceComparison.BenchmarkResult(
                "测试操作", "ArrayList", 10000, 1000000);

        assertNotNull(result.toString());
        assertTrue(result.toString().contains("测试操作"));
        assertTrue(result.toString().contains("ArrayList"));
    }
}
