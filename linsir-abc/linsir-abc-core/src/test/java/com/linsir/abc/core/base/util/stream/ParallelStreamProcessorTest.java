package com.linsir.abc.core.base.util.stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * ParallelStreamProcessor 测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ParallelStreamProcessorTest {

    private ParallelStreamProcessor<Integer> processor;
    private List<Integer> largeDataset;

    @Before
    public void setUp() {
        // 创建大数据集
        largeDataset = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeDataset.add(i);
        }
        processor = ParallelStreamProcessor.of(largeDataset, 4);
    }

    @After
    public void tearDown() {
        if (processor != null) {
            processor.shutdown();
        }
    }

    @Test
    public void testParallelFilter() {
        List<Integer> result = processor.parallelFilter(n -> n % 2 == 0);

        assertEquals(5000, result.size());
        // 验证所有元素都是偶数
        for (Integer n : result) {
            assertTrue(n % 2 == 0);
        }
    }

    @Test
    public void testParallelMap() {
        List<Integer> result = processor.parallelMap(n -> n * n);

        assertEquals(10000, result.size());
        // 验证映射结果
        for (int i = 0; i < result.size(); i++) {
            assertEquals(Integer.valueOf(i * i), result.get(i));
        }
    }

    @Test
    public void testParallelReduce() {
        Integer sum = processor.parallelReduce(0, Integer::sum);

        // 0 + 1 + 2 + ... + 9999 = 9999 * 10000 / 2 = 49995000
        assertEquals(Integer.valueOf(49995000), sum);
    }

    @Test
    public void testParallelCollect() {
        List<Integer> result = processor.parallelCollect(
                Collectors.toList()
        );

        assertEquals(10000, result.size());
    }

    @Test
    public void testParallelGroupBy() {
        Map<String, List<Integer>> groups = processor.parallelGroupBy(n -> n % 2 == 0 ? "even" : "odd");

        assertEquals(2, groups.size());
        assertEquals(5000, groups.get("even").size());
        assertEquals(5000, groups.get("odd").size());
    }

    @Test
    public void testSmallDatasetSequential() {
        // 小数据集应该使用串行处理
        List<Integer> smallDataset = Arrays.asList(1, 2, 3, 4, 5);
        ParallelStreamProcessor<Integer> smallProcessor = ParallelStreamProcessor.of(smallDataset);

        List<Integer> result = smallProcessor.parallelFilter(n -> n > 2);

        assertEquals(Arrays.asList(3, 4, 5), result);
        smallProcessor.shutdown();
    }

    @Test
    public void testParallelism() {
        assertEquals(4, processor.getParallelism());
    }

    @Test
    public void testParallelFilterWithLargeDataset() {
        // 测试大数据集的并行过滤性能
        List<Integer> result = processor.parallelFilter(n -> n > 5000);

        assertEquals(4999, result.size());
        // 验证所有元素都大于5000
        for (Integer n : result) {
            assertTrue(n > 5000);
        }
    }

    @Test
    public void testParallelMapPerformance() {
        // 测试复杂映射操作
        List<Integer> result = processor.parallelMap(n -> {
            // 模拟复杂计算
            int sum = 0;
            for (int i = 0; i < 100; i++) {
                sum += n * i;
            }
            return sum;
        });

        assertEquals(10000, result.size());
    }

    @Test
    public void testEmptyDataset() {
        ParallelStreamProcessor<Integer> emptyProcessor = ParallelStreamProcessor.of(Collections.emptyList());

        List<Integer> result = emptyProcessor.parallelFilter(n -> true);

        assertTrue(result.isEmpty());
        emptyProcessor.shutdown();
    }

    @Test
    public void testSingleElement() {
        ParallelStreamProcessor<Integer> singleProcessor = ParallelStreamProcessor.of(Collections.singletonList(42));

        List<Integer> result = singleProcessor.parallelMap(n -> n * 2);

        assertEquals(Collections.singletonList(84), result);
        singleProcessor.shutdown();
    }

    @Test
    public void testParallelGroupByMultipleKeys() {
        // 按模3分组
        Map<Integer, List<Integer>> groups = processor.parallelGroupBy(n -> n % 3);

        assertEquals(3, groups.size());
        // 验证每个组的元素数量
        int count0 = groups.get(0).size();
        int count1 = groups.get(1).size();
        int count2 = groups.get(2).size();

        assertEquals(10000, count0 + count1 + count2);
    }

    @Test
    public void testChainedOperations() {
        // 先过滤再映射
        List<Integer> filtered = processor.parallelFilter(n -> n % 2 == 0);
        ParallelStreamProcessor<Integer> newProcessor = ParallelStreamProcessor.of(filtered);
        List<Integer> result = newProcessor.parallelMap(n -> n / 2);

        assertEquals(5000, result.size());
        // 验证结果：0, 1, 2, 3, ... 4999
        for (int i = 0; i < result.size(); i++) {
            assertEquals(Integer.valueOf(i), result.get(i));
        }
        newProcessor.shutdown();
    }
}
