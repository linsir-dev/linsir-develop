package com.linsir.abc.core.base.util.stream;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * 并行流处理器
 * 演示并行Stream的实现原理：任务拆分、Fork/Join框架、线程安全
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>任务拆分（Spliterator）</li>
 *   <li>Fork/Join并行执行</li>
 *   <li>线程安全合并结果</li>
 *   <li>性能权衡（数据量、操作复杂度）</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ParallelStreamProcessor<T> {

    /**
     * 线程池（ForkJoinPool）
     */
    private final ForkJoinPool forkJoinPool;

    /**
     * 数据源
     */
    private final List<T> source;

    /**
     * 并行阈值
     */
    private static final int PARALLELISM_THRESHOLD = 1000;

    /**
     * 私有构造器
     */
    private ParallelStreamProcessor(List<T> source, int parallelism) {
        this.source = new ArrayList<>(source);
        this.forkJoinPool = new ForkJoinPool(parallelism);
    }

    /**
     * 创建并行流处理器
     *
     * @param source 数据源
     * @param <T> 元素类型
     * @return 并行流处理器
     */
    public static <T> ParallelStreamProcessor<T> of(List<T> source) {
        return new ParallelStreamProcessor<>(source, Runtime.getRuntime().availableProcessors());
    }

    /**
     * 创建并行流处理器（指定并行度）
     *
     * @param source 数据源
     * @param parallelism 并行度
     * @param <T> 元素类型
     * @return 并行流处理器
     */
    public static <T> ParallelStreamProcessor<T> of(List<T> source, int parallelism) {
        return new ParallelStreamProcessor<>(source, parallelism);
    }

    /**
     * 并行过滤
     *
     * @param predicate 谓词条件
     * @return 过滤后的列表
     */
    public List<T> parallelFilter(Predicate<T> predicate) {
        if (source.size() < PARALLELISM_THRESHOLD) {
            // 数据量小，串行处理
            List<T> result = new ArrayList<>();
            for (T t : source) {
                if (predicate.test(t)) {
                    result.add(t);
                }
            }
            return result;
        }

        // 并行处理
        return forkJoinPool.invoke(new FilterTask<>(source, 0, source.size(), predicate));
    }

    /**
     * 并行映射
     *
     * @param mapper 映射函数
     * @param <R> 返回类型
     * @return 映射后的列表
     */
    public <R> List<R> parallelMap(Function<T, R> mapper) {
        if (source.size() < PARALLELISM_THRESHOLD) {
            // 数据量小，串行处理
            List<R> result = new ArrayList<>(source.size());
            for (T t : source) {
                result.add(mapper.apply(t));
            }
            return result;
        }

        // 并行处理
        return forkJoinPool.invoke(new MapTask<>(source, 0, source.size(), mapper));
    }

    /**
     * 并行归约
     *
     * @param identity 初始值
     * @param accumulator 累加器
     * @return 归约结果
     */
    public T parallelReduce(T identity, BinaryOperator<T> accumulator) {
        if (source.size() < PARALLELISM_THRESHOLD) {
            // 数据量小，串行处理
            T result = identity;
            for (T t : source) {
                result = accumulator.apply(result, t);
            }
            return result;
        }

        // 并行处理
        return forkJoinPool.invoke(new ReduceTask<>(source, 0, source.size(), identity, accumulator));
    }

    /**
     * 并行收集
     *
     * @param collector 收集器
     * @param <A> 累加器类型
     * @param <R> 结果类型
     * @return 收集结果
     */
    public <A, R> R parallelCollect(Collector<T, A, R> collector) {
        if (source.size() < PARALLELISM_THRESHOLD) {
            // 数据量小，串行处理
            A accumulator = collector.supplier().get();
            for (T t : source) {
                collector.accumulator().accept(accumulator, t);
            }
            return collector.finisher().apply(accumulator);
        }

        // 并行处理
        return forkJoinPool.invoke(new CollectTask<>(source, 0, source.size(), collector));
    }

    /**
     * 并行排序
     *
     * @param comparator 比较器
     * @return 排序后的列表
     */
    public List<T> parallelSort(Comparator<T> comparator) {
        List<T> result = new ArrayList<>(source);
        result.parallelStream().sorted(comparator);
        return result;
    }

    /**
     * 并行分组
     *
     * @param classifier 分类函数
     * @param <K> 键类型
     * @return 分组结果
     */
    public <K> Map<K, List<T>> parallelGroupBy(Function<T, K> classifier) {
        if (source.size() < PARALLELISM_THRESHOLD) {
            // 数据量小，串行处理
            Map<K, List<T>> result = new HashMap<>();
            for (T t : source) {
                K key = classifier.apply(t);
                result.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
            }
            return result;
        }

        // 并行处理
        return forkJoinPool.invoke(new GroupByTask<>(source, 0, source.size(), classifier));
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        forkJoinPool.shutdown();
    }

    /**
     * 获取并行度
     *
     * @return 并行度
     */
    public int getParallelism() {
        return forkJoinPool.getParallelism();
    }

    /**
     * 过滤任务（ForkJoinTask）
     */
    private static class FilterTask<T> extends RecursiveTask<List<T>> {
        private final List<T> source;
        private final int start;
        private final int end;
        private final Predicate<T> predicate;
        private static final int THRESHOLD = 100;

        FilterTask(List<T> source, int start, int end, Predicate<T> predicate) {
            this.source = source;
            this.start = start;
            this.end = end;
            this.predicate = predicate;
        }

        @Override
        protected List<T> compute() {
            if (end - start <= THRESHOLD) {
                // 直接计算
                List<T> result = new ArrayList<>();
                for (int i = start; i < end; i++) {
                    T t = source.get(i);
                    if (predicate.test(t)) {
                        result.add(t);
                    }
                }
                return result;
            }

            // 拆分任务
            int mid = (start + end) / 2;
            FilterTask<T> left = new FilterTask<>(source, start, mid, predicate);
            FilterTask<T> right = new FilterTask<>(source, mid, end, predicate);

            left.fork();
            List<T> rightResult = right.compute();
            List<T> leftResult = left.join();

            // 合并结果
            leftResult.addAll(rightResult);
            return leftResult;
        }
    }

    /**
     * 映射任务（ForkJoinTask）
     */
    private static class MapTask<T, R> extends RecursiveTask<List<R>> {
        private final List<T> source;
        private final int start;
        private final int end;
        private final Function<T, R> mapper;
        private static final int THRESHOLD = 100;

        MapTask(List<T> source, int start, int end, Function<T, R> mapper) {
            this.source = source;
            this.start = start;
            this.end = end;
            this.mapper = mapper;
        }

        @Override
        protected List<R> compute() {
            if (end - start <= THRESHOLD) {
                // 直接计算
                List<R> result = new ArrayList<>(end - start);
                for (int i = start; i < end; i++) {
                    result.add(mapper.apply(source.get(i)));
                }
                return result;
            }

            // 拆分任务
            int mid = (start + end) / 2;
            MapTask<T, R> left = new MapTask<>(source, start, mid, mapper);
            MapTask<T, R> right = new MapTask<>(source, mid, end, mapper);

            left.fork();
            List<R> rightResult = right.compute();
            List<R> leftResult = left.join();

            // 合并结果
            leftResult.addAll(rightResult);
            return leftResult;
        }
    }

    /**
     * 归约任务（ForkJoinTask）
     */
    private static class ReduceTask<T> extends RecursiveTask<T> {
        private final List<T> source;
        private final int start;
        private final int end;
        private final T identity;
        private final BinaryOperator<T> accumulator;
        private static final int THRESHOLD = 100;

        ReduceTask(List<T> source, int start, int end, T identity, BinaryOperator<T> accumulator) {
            this.source = source;
            this.start = start;
            this.end = end;
            this.identity = identity;
            this.accumulator = accumulator;
        }

        @Override
        protected T compute() {
            if (end - start <= THRESHOLD) {
                // 直接计算
                T result = identity;
                for (int i = start; i < end; i++) {
                    result = accumulator.apply(result, source.get(i));
                }
                return result;
            }

            // 拆分任务
            int mid = (start + end) / 2;
            ReduceTask<T> left = new ReduceTask<>(source, start, mid, identity, accumulator);
            ReduceTask<T> right = new ReduceTask<>(source, mid, end, identity, accumulator);

            left.fork();
            T rightResult = right.compute();
            T leftResult = left.join();

            // 合并结果
            return accumulator.apply(leftResult, rightResult);
        }
    }

    /**
     * 收集任务（ForkJoinTask）
     */
    private static class CollectTask<T, A, R> extends RecursiveTask<R> {
        private final List<T> source;
        private final int start;
        private final int end;
        private final Collector<T, A, R> collector;
        private static final int THRESHOLD = 100;

        CollectTask(List<T> source, int start, int end, Collector<T, A, R> collector) {
            this.source = source;
            this.start = start;
            this.end = end;
            this.collector = collector;
        }

        @Override
        protected R compute() {
            if (end - start <= THRESHOLD) {
                // 直接计算
                A accumulator = collector.supplier().get();
                for (int i = start; i < end; i++) {
                    collector.accumulator().accept(accumulator, source.get(i));
                }
                return collector.finisher().apply(accumulator);
            }

            // 拆分任务
            int mid = (start + end) / 2;
            CollectTask<T, A, R> left = new CollectTask<>(source, start, mid, collector);
            CollectTask<T, A, R> right = new CollectTask<>(source, mid, end, collector);

            left.fork();
            R rightResult = right.compute();
            R leftResult = left.join();

            // 合并结果
            if (leftResult instanceof List && rightResult instanceof List) {
                List<T> merged = new ArrayList<>((List<T>) leftResult);
                merged.addAll((List<T>) rightResult);
                return (R) merged;
            }
            return rightResult;
        }
    }

    /**
     * 分组任务（ForkJoinTask）
     */
    private static class GroupByTask<T, K> extends RecursiveTask<Map<K, List<T>>> {
        private final List<T> source;
        private final int start;
        private final int end;
        private final Function<T, K> classifier;
        private static final int THRESHOLD = 100;

        GroupByTask(List<T> source, int start, int end, Function<T, K> classifier) {
            this.source = source;
            this.start = start;
            this.end = end;
            this.classifier = classifier;
        }

        @Override
        protected Map<K, List<T>> compute() {
            if (end - start <= THRESHOLD) {
                // 直接计算
                Map<K, List<T>> result = new HashMap<>();
                for (int i = start; i < end; i++) {
                    T t = source.get(i);
                    K key = classifier.apply(t);
                    result.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
                }
                return result;
            }

            // 拆分任务
            int mid = (start + end) / 2;
            GroupByTask<T, K> left = new GroupByTask<>(source, start, mid, classifier);
            GroupByTask<T, K> right = new GroupByTask<>(source, mid, end, classifier);

            left.fork();
            Map<K, List<T>> rightResult = right.compute();
            Map<K, List<T>> leftResult = left.join();

            // 合并结果
            for (Map.Entry<K, List<T>> entry : rightResult.entrySet()) {
                leftResult.merge(entry.getKey(), entry.getValue(), (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                });
            }
            return leftResult;
        }
    }

    /**
     * 性能测试
     */
    public static void main(String[] args) {
        // 创建大数据集
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            numbers.add(i);
        }

        // 串行处理
        long start = System.currentTimeMillis();
        List<Integer> serialResult = new ArrayList<>();
        for (Integer n : numbers) {
            if (n % 2 == 0) {
                serialResult.add(n * n);
            }
        }
        long serialTime = System.currentTimeMillis() - start;

        // 并行处理
        ParallelStreamProcessor<Integer> processor = ParallelStreamProcessor.of(numbers);
        start = System.currentTimeMillis();
        List<Integer> parallelResult = processor.parallelFilter(n -> n % 2 == 0);
        parallelResult = processor.parallelMap(n -> n * n);
        long parallelTime = System.currentTimeMillis() - start;

        System.out.println("串行处理时间: " + serialTime + "ms");
        System.out.println("并行处理时间: " + parallelTime + "ms");
        System.out.println("加速比: " + (double) serialTime / parallelTime);

        processor.shutdown();
    }
}
