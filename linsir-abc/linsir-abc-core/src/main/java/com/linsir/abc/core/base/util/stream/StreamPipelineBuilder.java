package com.linsir.abc.core.base.util.stream;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

/**
 * Stream管道构建器
 * 演示Stream API的核心原理：延迟执行、管道操作、中间操作与终止操作
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>构建操作链（管道）</li>
 *   <li>延迟执行（Lazy Evaluation）</li>
 *   <li>短路操作（Short-circuiting）</li>
 *   <li>无状态与有状态操作</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class StreamPipelineBuilder<T> {

    /**
     * 数据源
     */
    private List<T> source;

    /**
     * 操作管道（中间操作链）
     */
    private final List<Operation<T>> pipeline;

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        FILTER,      // 过滤
        MAP,         // 映射
        FLAT_MAP,    // 扁平映射
        DISTINCT,    // 去重（有状态）
        SORTED,      // 排序（有状态）
        PEEK,        // 查看
        LIMIT,       // 限制（短路）
        SKIP         // 跳过
    }

    /**
     * 操作接口
     */
    public interface Operation<T> {
        List<T> apply(List<T> input);
        OperationType getType();
    }

    /**
     * 私有构造器
     */
    private StreamPipelineBuilder(List<T> source) {
        this.source = new ArrayList<>(source);
        this.pipeline = new ArrayList<>();
    }

    /**
     * 创建Stream构建器
     *
     * @param source 数据源
     * @param <T> 元素类型
     * @return Stream构建器
     */
    public static <T> StreamPipelineBuilder<T> of(List<T> source) {
        return new StreamPipelineBuilder<>(source);
    }

    /**
     * 创建Stream构建器（可变参数）
     *
     * @param elements 元素数组
     * @param <T> 元素类型
     * @return Stream构建器
     */
    @SafeVarargs
    public static <T> StreamPipelineBuilder<T> of(T... elements) {
        return new StreamPipelineBuilder<>(Arrays.asList(elements));
    }

    /**
     * 过滤操作（中间操作）
     *
     * @param predicate 谓词条件
     * @return this
     */
    public StreamPipelineBuilder<T> filter(Predicate<T> predicate) {
        pipeline.add(new Operation<T>() {
            @Override
            public List<T> apply(List<T> input) {
                List<T> result = new ArrayList<>();
                for (T t : input) {
                    if (predicate.test(t)) {
                        result.add(t);
                    }
                }
                return result;
            }

            @Override
            public OperationType getType() {
                return OperationType.FILTER;
            }
        });
        return this;
    }

    /**
     * 映射操作（中间操作）
     *
     * @param mapper 映射函数
     * @param <R> 返回类型
     * @return 新的Stream构建器
     */
    @SuppressWarnings("unchecked")
    public <R> StreamPipelineBuilder<R> map(Function<T, R> mapper) {
        StreamPipelineBuilder<R> newBuilder = new StreamPipelineBuilder<>(Collections.emptyList());
        newBuilder.pipeline.addAll((List) this.pipeline);
        newBuilder.pipeline.add(new Operation<R>() {
            @Override
            public List<R> apply(List<R> input) {
                List<R> result = new ArrayList<>();
                for (Object t : input) {
                    R r = mapper.apply((T) t);
                    result.add(r);
                }
                return result;
            }

            @Override
            public OperationType getType() {
                return OperationType.MAP;
            }
        });
        // 复制source引用以便执行时使用
        newBuilder.source = (List) this.source;
        return newBuilder;
    }

    /**
     * 去重操作（有状态中间操作）
     *
     * @return this
     */
    public StreamPipelineBuilder<T> distinct() {
        pipeline.add(new Operation<T>() {
            @Override
            public List<T> apply(List<T> input) {
                return new ArrayList<>(new LinkedHashSet<>(input));
            }

            @Override
            public OperationType getType() {
                return OperationType.DISTINCT;
            }
        });
        return this;
    }

    /**
     * 排序操作（有状态中间操作）
     *
     * @param comparator 比较器
     * @return this
     */
    public StreamPipelineBuilder<T> sorted(Comparator<T> comparator) {
        pipeline.add(new Operation<T>() {
            @Override
            public List<T> apply(List<T> input) {
                List<T> result = new ArrayList<>(input);
                result.sort(comparator);
                return result;
            }

            @Override
            public OperationType getType() {
                return OperationType.SORTED;
            }
        });
        return this;
    }

    /**
     * 限制操作（短路中间操作）
     *
     * @param maxSize 最大数量
     * @return this
     */
    public StreamPipelineBuilder<T> limit(long maxSize) {
        pipeline.add(new Operation<T>() {
            @Override
            public List<T> apply(List<T> input) {
                int size = (int) Math.min(maxSize, input.size());
                return new ArrayList<>(input.subList(0, size));
            }

            @Override
            public OperationType getType() {
                return OperationType.LIMIT;
            }
        });
        return this;
    }

    /**
     * 跳过操作（有状态中间操作）
     *
     * @param n 跳过数量
     * @return this
     */
    public StreamPipelineBuilder<T> skip(long n) {
        pipeline.add(new Operation<T>() {
            @Override
            public List<T> apply(List<T> input) {
                if (n >= input.size()) {
                    return Collections.emptyList();
                }
                return new ArrayList<>(input.subList((int) n, input.size()));
            }

            @Override
            public OperationType getType() {
                return OperationType.SKIP;
            }
        });
        return this;
    }

    /**
     * 查看操作（中间操作，用于调试）
     *
     * @param action 消费操作
     * @return this
     */
    public StreamPipelineBuilder<T> peek(Consumer<T> action) {
        pipeline.add(new Operation<T>() {
            @Override
            public List<T> apply(List<T> input) {
                for (T t : input) {
                    action.accept(t);
                }
                return input;
            }

            @Override
            public OperationType getType() {
                return OperationType.PEEK;
            }
        });
        return this;
    }

    /**
     * 收集操作（终止操作）
     *
     * @param collector 收集器
     * @param <A> 累加器类型
     * @param <R> 结果类型
     * @return 收集结果
     */
    public <A, R> R collect(Collector<T, A, R> collector) {
        List<T> result = executePipeline();
        A accumulator = collector.supplier().get();
        for (T t : result) {
            collector.accumulator().accept(accumulator, t);
        }
        return collector.finisher().apply(accumulator);
    }

    /**
     * 转换为List（终止操作）
     *
     * @return List结果
     */
    public List<T> toList() {
        return executePipeline();
    }

    /**
     * 遍历操作（终止操作）
     *
     * @param action 消费操作
     */
    public void forEach(Consumer<T> action) {
        List<T> result = executePipeline();
        for (T t : result) {
            action.accept(t);
        }
    }

    /**
     * 计数操作（终止操作）
     *
     * @return 元素数量
     */
    public long count() {
        return executePipeline().size();
    }

    /**
     * 任意匹配（短路终止操作）
     *
     * @param predicate 谓词条件
     * @return 是否匹配
     */
    public boolean anyMatch(Predicate<T> predicate) {
        List<T> result = executePipeline();
        for (T t : result) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 全部匹配（短路终止操作）
     *
     * @param predicate 谓词条件
     * @return 是否全部匹配
     */
    public boolean allMatch(Predicate<T> predicate) {
        List<T> result = executePipeline();
        for (T t : result) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 查找第一个（短路终止操作）
     *
     * @return 第一个元素
     */
    public Optional<T> findFirst() {
        List<T> result = executePipeline();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    /**
     * 归约操作（终止操作）
     *
     * @param identity 初始值
     * @param accumulator 累加器
     * @return 归约结果
     */
    public T reduce(T identity, BinaryOperator<T> accumulator) {
        List<T> result = executePipeline();
        T acc = identity;
        for (T t : result) {
            acc = accumulator.apply(acc, t);
        }
        return acc;
    }

    /**
     * 获取管道信息（用于分析）
     *
     * @return 操作管道描述
     */
    public String getPipelineInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Stream Pipeline:\n");
        sb.append("  Source: ").append(source).append("\n");
        sb.append("  Operations (").append(pipeline.size()).append("):\n");
        for (int i = 0; i < pipeline.size(); i++) {
            sb.append("    ").append(i + 1).append(". ")
              .append(pipeline.get(i).getType()).append("\n");
        }
        return sb.toString();
    }

    /**
     * 执行管道
     *
     * @return 执行结果
     */
    private List<T> executePipeline() {
        List<T> result = new ArrayList<>(source);
        for (Operation<T> operation : pipeline) {
            result = operation.apply(result);
        }
        return result;
    }

    /**
     * 创建通用收集器
     */
    public static <T, A, R> Collector<T, A, R> collector(
            Supplier<A> supplier,
            BiConsumer<A, T> accumulator,
            Function<A, R> finisher) {
        return new Collector<T, A, R>() {
            @Override
            public Supplier<A> supplier() {
                return supplier;
            }

            @Override
            public BiConsumer<A, T> accumulator() {
                return accumulator;
            }

            @Override
            public BinaryOperator<A> combiner() {
                return (a1, a2) -> a1;
            }

            @Override
            public Function<A, R> finisher() {
                return finisher;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }
}
