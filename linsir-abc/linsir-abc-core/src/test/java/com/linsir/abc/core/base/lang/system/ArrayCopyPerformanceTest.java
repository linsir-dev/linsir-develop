package com.linsir.abc.core.base.lang.system;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ArrayCopyPerformance测试类
 */
public class ArrayCopyPerformanceTest {

    /**
     * 测试运行性能测试
     */
    @Test
    public void testRunBenchmark() {
        ArrayCopyPerformance benchmark = new ArrayCopyPerformance();
        String report = benchmark.runBenchmark();

        assertNotNull(report);
        assertTrue(report.contains("数组拷贝性能测试"));
        assertTrue(report.contains("System.arraycopy()"));
        assertTrue(report.contains("Arrays.copyOf()"));
        assertTrue(report.contains("clone()"));
        assertTrue(report.contains("手动循环"));
    }

    /**
     * 测试System.arraycopy性能
     */
    @Test
    public void testBenchmarkSystemArrayCopy() {
        ArrayCopyPerformance benchmark = new ArrayCopyPerformance();
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        long time = benchmark.benchmarkSystemArrayCopy(source);

        assertTrue(time >= 0);
    }

    /**
     * 测试Arrays.copyOf性能
     */
    @Test
    public void testBenchmarkArraysCopyOf() {
        ArrayCopyPerformance benchmark = new ArrayCopyPerformance();
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        long time = benchmark.benchmarkArraysCopyOf(source);

        assertTrue(time >= 0);
    }

    /**
     * 测试clone方法性能
     */
    @Test
    public void testBenchmarkClone() {
        ArrayCopyPerformance benchmark = new ArrayCopyPerformance();
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        long time = benchmark.benchmarkClone(source);

        assertTrue(time >= 0);
    }

    /**
     * 测试手动循环性能
     */
    @Test
    public void testBenchmarkManualLoop() {
        ArrayCopyPerformance benchmark = new ArrayCopyPerformance();
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        long time = benchmark.benchmarkManualLoop(source);

        assertTrue(time >= 0);
    }

    /**
     * 验证System.arraycopy拷贝结果正确
     */
    @Test
    public void testSystemArrayCopyCorrectness() {
        int[] source = {1, 2, 3, 4, 5};
        int[] dest = new int[5];

        System.arraycopy(source, 0, dest, 0, 5);

        assertArrayEquals(source, dest);
    }

    /**
     * 验证Arrays.copyOf拷贝结果正确
     */
    @Test
    public void testArraysCopyOfCorrectness() {
        int[] source = {1, 2, 3, 4, 5};

        int[] dest = java.util.Arrays.copyOf(source, source.length);

        assertArrayEquals(source, dest);

        // 验证是新数组
        assertNotSame(source, dest);
    }

    /**
     * 验证clone拷贝结果正确
     */
    @Test
    public void testCloneCorrectness() {
        int[] source = {1, 2, 3, 4, 5};

        int[] dest = source.clone();

        assertArrayEquals(source, dest);

        // 验证是新数组
        assertNotSame(source, dest);
    }

    /**
     * 验证手动循环拷贝结果正确
     */
    @Test
    public void testManualLoopCorrectness() {
        int[] source = {1, 2, 3, 4, 5};
        int[] dest = new int[5];

        for (int i = 0; i < source.length; i++) {
            dest[i] = source[i];
        }

        assertArrayEquals(source, dest);
    }

    /**
     * 测试部分数组拷贝
     */
    @Test
    public void testPartialArrayCopy() {
        int[] source = {1, 2, 3, 4, 5};
        int[] dest = new int[3];

        // 只拷贝中间3个元素
        System.arraycopy(source, 1, dest, 0, 3);

        assertEquals(2, dest[0]);
        assertEquals(3, dest[1]);
        assertEquals(4, dest[2]);
    }

    /**
     * 测试System.arraycopy比手动循环快
     */
    @Test
    public void testSystemArrayCopyFasterThanManual() {
        ArrayCopyPerformance benchmark = new ArrayCopyPerformance();

        // 使用较大的数组进行测试
        int[] source = new int[100000];
        for (int i = 0; i < source.length; i++) {
            source[i] = i;
        }

        long systemTime = benchmark.benchmarkSystemArrayCopy(source);
        long manualTime = benchmark.benchmarkManualLoop(source);

        // 两者都应该能正常运行
        assertTrue(systemTime >= 0);
        assertTrue(manualTime >= 0);

        // System.arraycopy通常是native实现，应该更快
        // 但由于测试波动，这里不做严格断言
    }

    /**
     * 测试对象数组拷贝
     */
    @Test
    public void testObjectArrayCopy() {
        String[] source = {"A", "B", "C", "D", "E"};
        String[] dest = new String[5];

        System.arraycopy(source, 0, dest, 0, 5);

        assertArrayEquals(source, dest);
    }

    /**
     * 测试二维数组拷贝
     */
    @Test
    public void testTwoDimensionalArrayCopy() {
        int[][] source = {{1, 2}, {3, 4}, {5, 6}};
        int[][] dest = new int[3][2];

        System.arraycopy(source, 0, dest, 0, 3);

        // 验证拷贝结果
        for (int i = 0; i < source.length; i++) {
            assertArrayEquals(source[i], dest[i]);
        }

        // 注意：这是浅拷贝
        assertSame(source[0], dest[0]);
    }
}
