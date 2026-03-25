package com.linsir.abc.core.base.util.stream;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * CustomCollector 测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomCollectorTest {

    @Test
    public void testGroupingCounting() {
        List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "banana", "apple");

        Map<String, Long> counts = words.stream()
                .collect(CustomCollector.groupingCounting(word -> word));

        assertEquals(Long.valueOf(3), counts.get("apple"));
        assertEquals(Long.valueOf(2), counts.get("banana"));
        assertEquals(Long.valueOf(1), counts.get("cherry"));
    }

    @Test
    public void testPartitioningStatistics() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        CustomCollector.PartitionStatistics<Integer> stats = numbers.stream()
                .collect(CustomCollector.partitioningStatistics(n -> n % 2 == 0));

        assertEquals(5, stats.getTrueCount());  // 偶数
        assertEquals(5, stats.getFalseCount()); // 奇数
        assertEquals(Arrays.asList(2, 4, 6, 8, 10), stats.getTruePartition());
        assertEquals(Arrays.asList(1, 3, 5, 7, 9), stats.getFalsePartition());
    }

    @Test
    public void testTopN() {
        List<Integer> numbers = Arrays.asList(5, 2, 8, 1, 9, 3, 7, 4, 6, 10);

        // 获取最大的3个
        List<Integer> top3 = numbers.stream()
                .collect(CustomCollector.topN(3, Integer::compareTo));

        // 验证结果包含最大的3个数（顺序可能不同）
        assertEquals(3, top3.size());
        assertTrue(top3.contains(10));
        assertTrue(top3.contains(9));
        assertTrue(top3.contains(8));
    }

    @Test
    public void testTopNWithStrings() {
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");

        // 获取最长的3个
        List<String> longest3 = words.stream()
                .collect(CustomCollector.topN(3, Comparator.comparingInt(String::length)));

        // 验证结果包含最长的3个单词（顺序可能不同）
        assertEquals(3, longest3.size());
        assertTrue(longest3.contains("elderberry")); // 长度10
        assertTrue(longest3.contains("cherry"));     // 长度6
        assertTrue(longest3.contains("banana"));     // 长度6
    }

    @Test
    public void testJoining() {
        List<String> words = Arrays.asList("Hello", "World", "Java");

        String result = words.stream()
                .collect(CustomCollector.joining(", ", "[", "]"));

        assertEquals("[Hello, World, Java]", result);
    }

    @Test
    public void testJoiningEmpty() {
        List<String> words = Collections.emptyList();

        String result = words.stream()
                .collect(CustomCollector.joining(", ", "[", "]"));

        assertEquals("[]", result);
    }

    @Test
    public void testSummarizingInt() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        CustomCollector.SummaryStatistics stats = numbers.stream()
                .collect(CustomCollector.summarizingInt(n -> n));

        assertEquals(5, stats.getCount());
        assertEquals(15, stats.getSum());
        assertEquals(1, stats.getMin());
        assertEquals(5, stats.getMax());
        assertEquals(3.0, stats.getAverage(), 0.001);
    }

    @Test
    public void testSummarizingIntSingleElement() {
        List<Integer> numbers = Collections.singletonList(42);

        CustomCollector.SummaryStatistics stats = numbers.stream()
                .collect(CustomCollector.summarizingInt(n -> n));

        assertEquals(1, stats.getCount());
        assertEquals(42, stats.getSum());
        assertEquals(42, stats.getMin());
        assertEquals(42, stats.getMax());
        assertEquals(42.0, stats.getAverage(), 0.001);
    }

    @Test
    public void testDistinctOrdered() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);

        List<Integer> distinct = numbers.stream()
                .collect(CustomCollector.distinctOrdered());

        assertEquals(Arrays.asList(3, 1, 4, 5, 9, 2, 6), distinct);
    }

    @Test
    public void testDistinctOrderedWithStrings() {
        List<String> words = Arrays.asList("apple", "banana", "apple", "cherry", "banana");

        List<String> distinct = words.stream()
                .collect(CustomCollector.distinctOrdered());

        assertEquals(Arrays.asList("apple", "banana", "cherry"), distinct);
    }

    @Test
    public void testBatching() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<List<Integer>> batches = numbers.stream()
                .collect(CustomCollector.batching(3));

        assertEquals(4, batches.size());
        assertEquals(Arrays.asList(1, 2, 3), batches.get(0));
        assertEquals(Arrays.asList(4, 5, 6), batches.get(1));
        assertEquals(Arrays.asList(7, 8, 9), batches.get(2));
        assertEquals(Arrays.asList(10), batches.get(3));
    }

    @Test
    public void testBatchingExact() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        List<List<Integer>> batches = numbers.stream()
                .collect(CustomCollector.batching(2));

        assertEquals(3, batches.size());
        assertEquals(Arrays.asList(1, 2), batches.get(0));
        assertEquals(Arrays.asList(3, 4), batches.get(1));
        assertEquals(Arrays.asList(5, 6), batches.get(2));
    }

    @Test
    public void testBatchingEmpty() {
        List<Integer> numbers = Collections.emptyList();

        List<List<Integer>> batches = numbers.stream()
                .collect(CustomCollector.batching(3));

        assertTrue(batches.isEmpty());
    }

    @Test
    public void testGroupingCountingEmpty() {
        List<String> words = Collections.emptyList();

        Map<String, Long> counts = words.stream()
                .collect(CustomCollector.groupingCounting(word -> word));

        assertTrue(counts.isEmpty());
    }

    @Test
    public void testComplexPipeline() {
        List<String> words = Arrays.asList(
                "apple", "banana", "apricot", "blueberry", "avocado", "blackberry"
        );

        // 按首字母分组计数
        Map<Character, Long> counts = words.stream()
                .collect(CustomCollector.groupingCounting(word -> word.charAt(0)));

        assertEquals(Long.valueOf(3), counts.get('a'));
        assertEquals(Long.valueOf(3), counts.get('b'));

        // 获取长度最长的3个单词
        List<String> longest3 = words.stream()
                .collect(CustomCollector.topN(3, Comparator.comparingInt(String::length)));

        // 验证结果包含最长的3个单词（顺序可能不同）
        // blackberry(10), blueberry(9), apricot(7)或avocado(7)
        assertEquals(3, longest3.size());
        assertTrue(longest3.contains("blackberry")); // 长度10
        assertTrue(longest3.contains("blueberry"));  // 长度9
        // 第3个可能是apricot或avocado（都是长度7）
        assertTrue(longest3.contains("apricot") || longest3.contains("avocado"));
    }

    @Test
    public void testPartitioningStatisticsEmpty() {
        List<Integer> numbers = Collections.emptyList();

        CustomCollector.PartitionStatistics<Integer> stats = numbers.stream()
                .collect(CustomCollector.partitioningStatistics(n -> n > 0));

        assertEquals(0, stats.getTrueCount());
        assertEquals(0, stats.getFalseCount());
        assertTrue(stats.getTruePartition().isEmpty());
        assertTrue(stats.getFalsePartition().isEmpty());
    }
}
