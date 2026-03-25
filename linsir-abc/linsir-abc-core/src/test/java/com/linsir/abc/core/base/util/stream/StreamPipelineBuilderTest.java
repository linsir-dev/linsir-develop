package com.linsir.abc.core.base.util.stream;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * StreamPipelineBuilder 测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class StreamPipelineBuilderTest {

    @Test
    public void testFilter() {
        StreamPipelineBuilder<Integer> builder = StreamPipelineBuilder.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> result = builder.filter(n -> n % 2 == 0).toList();

        assertEquals(Arrays.asList(2, 4, 6, 8, 10), result);
    }

    @Test
    public void testMap() {
        StreamPipelineBuilder<Integer> builder = StreamPipelineBuilder.of(1, 2, 3, 4, 5);
        List<Integer> result = builder.map(n -> n * n).toList();

        assertEquals(Arrays.asList(1, 4, 9, 16, 25), result);
    }

    @Test
    public void testDistinct() {
        StreamPipelineBuilder<Integer> builder = StreamPipelineBuilder.of(1, 2, 2, 3, 3, 3, 4);
        List<Integer> result = builder.distinct().toList();

        assertEquals(Arrays.asList(1, 2, 3, 4), result);
    }

    @Test
    public void testSorted() {
        StreamPipelineBuilder<Integer> builder = StreamPipelineBuilder.of(5, 2, 8, 1, 9);
        List<Integer> result = builder.sorted(Integer::compareTo).toList();

        assertEquals(Arrays.asList(1, 2, 5, 8, 9), result);
    }

    @Test
    public void testLimit() {
        StreamPipelineBuilder<Integer> builder = StreamPipelineBuilder.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> result = builder.limit(5).toList();

        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result);
    }

    @Test
    public void testSkip() {
        StreamPipelineBuilder<Integer> builder = StreamPipelineBuilder.of(1, 2, 3, 4, 5);
        List<Integer> result = builder.skip(2).toList();

        assertEquals(Arrays.asList(3, 4, 5), result);
    }

    @Test
    public void testChainedOperations() {
        List<Integer> result = StreamPipelineBuilder.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .limit(3)
                .toList();

        assertEquals(Arrays.asList(4, 16, 36), result);
    }

    @Test
    public void testCount() {
        long count = StreamPipelineBuilder.of(1, 2, 3, 4, 5)
                .filter(n -> n > 2)
                .count();

        assertEquals(3, count);
    }

    @Test
    public void testAnyMatch() {
        boolean hasEven = StreamPipelineBuilder.of(1, 3, 5, 7, 8)
                .anyMatch(n -> n % 2 == 0);

        assertTrue(hasEven);

        boolean hasNegative = StreamPipelineBuilder.of(1, 3, 5, 7)
                .anyMatch(n -> n < 0);

        assertFalse(hasNegative);
    }

    @Test
    public void testAllMatch() {
        boolean allPositive = StreamPipelineBuilder.of(1, 2, 3, 4, 5)
                .allMatch(n -> n > 0);

        assertTrue(allPositive);

        boolean allEven = StreamPipelineBuilder.of(1, 2, 3, 4)
                .allMatch(n -> n % 2 == 0);

        assertFalse(allEven);
    }

    @Test
    public void testFindFirst() {
        Optional<Integer> first = StreamPipelineBuilder.of(5, 2, 8, 1)
                .filter(n -> n > 3)
                .findFirst();

        assertTrue(first.isPresent());
        assertEquals(Integer.valueOf(5), first.get());
    }

    @Test
    public void testFindFirstEmpty() {
        Optional<Integer> first = StreamPipelineBuilder.<Integer>of()
                .findFirst();

        assertFalse(first.isPresent());
    }

    @Test
    public void testReduce() {
        int sum = StreamPipelineBuilder.of(1, 2, 3, 4, 5)
                .reduce(0, Integer::sum);

        assertEquals(15, sum);
    }

    @Test
    public void testCollectToList() {
        List<String> result = StreamPipelineBuilder.of("a", "b", "c")
                .toList();

        assertEquals(Arrays.asList("a", "b", "c"), result);
    }

    @Test
    public void testForEach() {
        List<Integer> collected = new ArrayList<>();
        StreamPipelineBuilder.of(1, 2, 3)
                .forEach(collected::add);

        assertEquals(Arrays.asList(1, 2, 3), collected);
    }

    @Test
    public void testPeek() {
        List<Integer> peeked = new ArrayList<>();
        List<Integer> result = StreamPipelineBuilder.of(1, 2, 3)
                .peek(peeked::add)
                .toList();

        assertEquals(Arrays.asList(1, 2, 3), peeked);
        assertEquals(Arrays.asList(1, 2, 3), result);
    }

    @Test
    public void testPipelineInfo() {
        StreamPipelineBuilder<Integer> builder = StreamPipelineBuilder.of(1, 2, 3, 4, 5)
                .filter(n -> n > 2)
                .map(n -> n * n)
                .sorted(Integer::compareTo)
                .limit(2);

        String info = builder.getPipelineInfo();
        assertTrue(info.contains("FILTER"));
        assertTrue(info.contains("MAP"));
        assertTrue(info.contains("SORTED"));
        assertTrue(info.contains("LIMIT"));
    }

    @Test
    public void testEmptyStream() {
        List<Integer> result = StreamPipelineBuilder.<Integer>of()
                .filter(n -> n > 0)
                .toList();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testComplexPipeline() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve");

        // 先测试filter
        List<String> filtered = StreamPipelineBuilder.of(names)
                .filter(name -> name.length() > 3)
                .toList();
        assertEquals(Arrays.asList("Alice", "Charlie", "David"), filtered);

        // 再测试filter + map
        List<String> result = StreamPipelineBuilder.of(names)
                .filter(name -> name.length() > 3)
                .map(String::toUpperCase)
                .toList();

        assertEquals(Arrays.asList("ALICE", "CHARLIE", "DAVID"), result);
    }
}
