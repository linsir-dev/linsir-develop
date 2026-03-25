package com.linsir.abc.core.base.util.stream;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * 自定义收集器
 * 演示如何创建和使用自定义Collector，实现复杂的数据收集逻辑
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>Supplier: 创建累加器</li>
 *   <li>Accumulator: 累加元素</li>
 *   <li>Combiner: 合并累加器（并行）</li>
 *   <li>Finisher: 转换最终结果</li>
 *   <li>Characteristics: 收集器特性</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class CustomCollector {

    /**
     * 私有构造器
     */
    private CustomCollector() {
        // 工具类
    }

    /**
     * 创建分组计数收集器
     *
     * @param classifier 分类函数
     * @param <T> 元素类型
     * @param <K> 键类型
     * @return 分组计数收集器
     */
    public static <T, K> Collector<T, ?, Map<K, Long>> groupingCounting(Function<T, K> classifier) {
        return new Collector<T, Map<K, Long>, Map<K, Long>>() {
            @Override
            public Supplier<Map<K, Long>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<K, Long>, T> accumulator() {
                return (map, t) -> {
                    K key = classifier.apply(t);
                    map.merge(key, 1L, Long::sum);
                };
            }

            @Override
            public BinaryOperator<Map<K, Long>> combiner() {
                return (map1, map2) -> {
                    map2.forEach((key, value) -> map1.merge(key, value, Long::sum));
                    return map1;
                };
            }

            @Override
            public Function<Map<K, Long>, Map<K, Long>> finisher() {
                return map -> map;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }

    /**
     * 创建分区统计收集器
     *
     * @param predicate 分区条件
     * @param <T> 元素类型
     * @return 分区统计收集器
     */
    public static <T> Collector<T, ?, PartitionStatistics<T>> partitioningStatistics(
            Predicate<T> predicate) {
        return new Collector<T, PartitionAccumulator<T>, PartitionStatistics<T>>() {
            @Override
            public Supplier<PartitionAccumulator<T>> supplier() {
                return PartitionAccumulator::new;
            }

            @Override
            public BiConsumer<PartitionAccumulator<T>, T> accumulator() {
                return (acc, t) -> {
                    if (predicate.test(t)) {
                        acc.trueList.add(t);
                    } else {
                        acc.falseList.add(t);
                    }
                };
            }

            @Override
            public BinaryOperator<PartitionAccumulator<T>> combiner() {
                return (acc1, acc2) -> {
                    acc1.trueList.addAll(acc2.trueList);
                    acc1.falseList.addAll(acc2.falseList);
                    return acc1;
                };
            }

            @Override
            public Function<PartitionAccumulator<T>, PartitionStatistics<T>> finisher() {
                return acc -> new PartitionStatistics<>(
                        acc.trueList,
                        acc.falseList,
                        acc.trueList.size(),
                        acc.falseList.size()
                );
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }

    /**
     * 分区累加器
     */
    public static class PartitionAccumulator<T> {
        List<T> trueList = new ArrayList<>();
        List<T> falseList = new ArrayList<>();
    }

    /**
     * 分区统计结果
     */
    public static class PartitionStatistics<T> {
        private final List<T> truePartition;
        private final List<T> falsePartition;
        private final long trueCount;
        private final long falseCount;

        public PartitionStatistics(List<T> truePartition, List<T> falsePartition,
                                   long trueCount, long falseCount) {
            this.truePartition = truePartition;
            this.falsePartition = falsePartition;
            this.trueCount = trueCount;
            this.falseCount = falseCount;
        }

        public List<T> getTruePartition() {
            return truePartition;
        }

        public List<T> getFalsePartition() {
            return falsePartition;
        }

        public long getTrueCount() {
            return trueCount;
        }

        public long getFalseCount() {
            return falseCount;
        }

        @Override
        public String toString() {
            return "PartitionStatistics{" +
                    "trueCount=" + trueCount +
                    ", falseCount=" + falseCount +
                    '}';
        }
    }

    /**
     * 创建TopN收集器
     *
     * @param n 数量
     * @param comparator 比较器
     * @param <T> 元素类型
     * @return TopN收集器
     */
    public static <T> Collector<T, ?, List<T>> topN(int n, Comparator<T> comparator) {
        return new Collector<T, PriorityQueue<T>, List<T>>() {
            @Override
            public Supplier<PriorityQueue<T>> supplier() {
                return () -> new PriorityQueue<>(comparator);
            }

            @Override
            public BiConsumer<PriorityQueue<T>, T> accumulator() {
                return (queue, t) -> {
                    if (queue.size() < n) {
                        queue.offer(t);
                    } else if (comparator.compare(t, queue.peek()) > 0) {
                        queue.poll();
                        queue.offer(t);
                    }
                };
            }

            @Override
            public BinaryOperator<PriorityQueue<T>> combiner() {
                return (q1, q2) -> {
                    for (T t : q2) {
                        accumulator().accept(q1, t);
                    }
                    return q1;
                };
            }

            @Override
            public Function<PriorityQueue<T>, List<T>> finisher() {
                return queue -> {
                    List<T> result = new ArrayList<>(queue);
                    result.sort(comparator.reversed());
                    return result;
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }

    /**
     * 创建字符串连接收集器（带分隔符、前缀、后缀）
     *
     * @param delimiter 分隔符
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 字符串连接收集器
     */
    public static Collector<String, ?, String> joining(String delimiter, String prefix, String suffix) {
        return new Collector<String, StringJoiner, String>() {
            @Override
            public Supplier<StringJoiner> supplier() {
                return () -> new StringJoiner(delimiter, prefix, suffix);
            }

            @Override
            public BiConsumer<StringJoiner, String> accumulator() {
                return StringJoiner::add;
            }

            @Override
            public BinaryOperator<StringJoiner> combiner() {
                return StringJoiner::merge;
            }

            @Override
            public Function<StringJoiner, String> finisher() {
                return StringJoiner::toString;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }

    /**
     * 创建汇总统计收集器
     *
     * @param mapper 数值映射函数
     * @param <T> 元素类型
     * @return 汇总统计收集器
     */
    public static <T> Collector<T, ?, SummaryStatistics> summarizingInt(ToIntFunction<T> mapper) {
        return new Collector<T, SummaryAccumulator, SummaryStatistics>() {
            @Override
            public Supplier<SummaryAccumulator> supplier() {
                return SummaryAccumulator::new;
            }

            @Override
            public BiConsumer<SummaryAccumulator, T> accumulator() {
                return (acc, t) -> acc.add(mapper.applyAsInt(t));
            }

            @Override
            public BinaryOperator<SummaryAccumulator> combiner() {
                return SummaryAccumulator::combine;
            }

            @Override
            public Function<SummaryAccumulator, SummaryStatistics> finisher() {
                return SummaryAccumulator::toStatistics;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }

    /**
     * 汇总累加器
     */
    public static class SummaryAccumulator {
        private long count = 0;
        private long sum = 0;
        private int min = Integer.MAX_VALUE;
        private int max = Integer.MIN_VALUE;

        public void add(int value) {
            count++;
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        public SummaryAccumulator combine(SummaryAccumulator other) {
            this.count += other.count;
            this.sum += other.sum;
            this.min = Math.min(this.min, other.min);
            this.max = Math.max(this.max, other.max);
            return this;
        }

        public SummaryStatistics toStatistics() {
            return new SummaryStatistics(count, sum, min, max,
                    count > 0 ? (double) sum / count : 0);
        }
    }

    /**
     * 汇总统计结果
     */
    public static class SummaryStatistics {
        private final long count;
        private final long sum;
        private final int min;
        private final int max;
        private final double average;

        public SummaryStatistics(long count, long sum, int min, int max, double average) {
            this.count = count;
            this.sum = sum;
            this.min = min;
            this.max = max;
            this.average = average;
        }

        public long getCount() {
            return count;
        }

        public long getSum() {
            return sum;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public double getAverage() {
            return average;
        }

        @Override
        public String toString() {
            return "SummaryStatistics{" +
                    "count=" + count +
                    ", sum=" + sum +
                    ", min=" + min +
                    ", max=" + max +
                    ", average=" + average +
                    '}';
        }
    }

    /**
     * 创建去重收集器（保持顺序）
     *
     * @param <T> 元素类型
     * @return 去重收集器
     */
    public static <T> Collector<T, ?, List<T>> distinctOrdered() {
        return new Collector<T, Set<T>, List<T>>() {
            @Override
            public Supplier<Set<T>> supplier() {
                return LinkedHashSet::new;
            }

            @Override
            public BiConsumer<Set<T>, T> accumulator() {
                return Set::add;
            }

            @Override
            public BinaryOperator<Set<T>> combiner() {
                return (set1, set2) -> {
                    set1.addAll(set2);
                    return set1;
                };
            }

            @Override
            public Function<Set<T>, List<T>> finisher() {
                return ArrayList::new;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.UNORDERED);
            }
        };
    }

    /**
     * 创建批量收集器（每N个元素一批）
     *
     * @param batchSize 批次大小
     * @param <T> 元素类型
     * @return 批量收集器
     */
    public static <T> Collector<T, ?, List<List<T>>> batching(int batchSize) {
        return new Collector<T, List<List<T>>, List<List<T>>>() {
            @Override
            public Supplier<List<List<T>>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<List<T>>, T> accumulator() {
                return (batches, t) -> {
                    if (batches.isEmpty() || batches.get(batches.size() - 1).size() >= batchSize) {
                        batches.add(new ArrayList<>());
                    }
                    batches.get(batches.size() - 1).add(t);
                };
            }

            @Override
            public BinaryOperator<List<List<T>>> combiner() {
                return (batches1, batches2) -> {
                    batches1.addAll(batches2);
                    return batches1;
                };
            }

            @Override
            public Function<List<List<T>>, List<List<T>>> finisher() {
                return batches -> batches;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }
}
