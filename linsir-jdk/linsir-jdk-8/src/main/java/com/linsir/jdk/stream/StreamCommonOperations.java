package com.linsir.jdk.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Stream API常用功能示例
 */
public class StreamCommonOperations {

    /**
     * 创建Stream的方法
     */
    public void createStreams() {
        System.out.println("=== 创建Stream的方法 ===");
        
        // 从集合创建
        List<String> list = Arrays.asList("a", "b", "c");
        Stream<String> streamFromList = list.stream();
        System.out.println("从集合创建: " + streamFromList.collect(Collectors.toList()));
        
        // 从数组创建
        String[] array = {"x", "y", "z"};
        Stream<String> streamFromArray = Arrays.stream(array);
        System.out.println("从数组创建: " + streamFromArray.collect(Collectors.toList()));
        
        // 使用of方法创建
        Stream<String> streamFromOf = Stream.of("1", "2", "3");
        System.out.println("使用of方法创建: " + streamFromOf.collect(Collectors.toList()));
        
        // 创建空Stream
        Stream<String> emptyStream = Stream.empty();
        System.out.println("创建空Stream: " + emptyStream.collect(Collectors.toList()));
        
        // 创建无限Stream - generate
        Stream<Double> randomStream = Stream.generate(Math::random).limit(3);
        System.out.println("使用generate创建无限Stream: " + randomStream.collect(Collectors.toList()));
        
        // 创建无限Stream - iterate
        Stream<Integer> iterateStream = Stream.iterate(0, n -> n + 2).limit(5);
        System.out.println("使用iterate创建无限Stream: " + iterateStream.collect(Collectors.toList()));
    }

    /**
     * 中间操作示例
     */
    public void intermediateOperations() {
        System.out.println("\n=== 中间操作示例 ===");
        
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Eve", "Bob");
        System.out.println("原始数据: " + names);
        
        // filter - 过滤
        List<String> filtered = names.stream()
                .filter(name -> name.length() > 3)
                .collect(Collectors.toList());
        System.out.println("filter(长度>3): " + filtered);
        
        // map - 映射
        List<String> mapped = names.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println("map(转大写): " + mapped);
        
        // flatMap - 扁平化映射
        List<String> words = Arrays.asList("hello world", "java stream", "lambda expression");
        List<String> flatMapped = words.stream()
                .flatMap(word -> Arrays.stream(word.split(" ")))
                .collect(Collectors.toList());
        System.out.println("flatMap(拆分单词): " + flatMapped);
        
        // sorted - 排序
        List<String> sorted = names.stream()
                .sorted()
                .collect(Collectors.toList());
        System.out.println("sorted(自然排序): " + sorted);
        
        // sorted - 自定义排序
        List<String> sortedByLength = names.stream()
                .sorted((a, b) -> b.length() - a.length())
                .collect(Collectors.toList());
        System.out.println("sorted(按长度倒序): " + sortedByLength);
        
        // distinct - 去重
        List<String> distinct = names.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("distinct(去重): " + distinct);
        
        // limit - 限制
        List<String> limited = names.stream()
                .limit(3)
                .collect(Collectors.toList());
        System.out.println("limit(前3个): " + limited);
        
        // skip - 跳过
        List<String> skipped = names.stream()
                .skip(2)
                .collect(Collectors.toList());
        System.out.println("skip(跳过前2个): " + skipped);
        
        // peek - 调试
        List<String> peeked = names.stream()
                .peek(name -> System.out.println("处理元素: " + name))
                .collect(Collectors.toList());
    }

    /**
     * 终端操作示例
     */
    public void terminalOperations() {
        System.out.println("\n=== 终端操作示例 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        System.out.println("原始数据: " + numbers);
        
        // forEach - 遍历
        System.out.print("forEach(遍历): ");
        numbers.stream().forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // collect - 收集
        List<Integer> collected = numbers.stream()
                .collect(Collectors.toList());
        System.out.println("collect(收集到List): " + collected);
        
        // count - 计数
        long count = numbers.stream().count();
        System.out.println("count(元素数量): " + count);
        
        // sum - 求和
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        System.out.println("sum(求和): " + sum);
        
        // average - 平均值
        Optional<Double> average = numbers.stream().mapToInt(Integer::intValue).average();
        System.out.println("average(平均值): " + average.orElse(0.0));
        
        // max - 最大值
        Optional<Integer> max = numbers.stream().max(Integer::compare);
        System.out.println("max(最大值): " + max.orElse(0));
        
        // min - 最小值
        Optional<Integer> min = numbers.stream().min(Integer::compare);
        System.out.println("min(最小值): " + min.orElse(0));
        
        // reduce - 归约
        Optional<Integer> reduced = numbers.stream().reduce((a, b) -> a + b);
        System.out.println("reduce(归约求和): " + reduced.orElse(0));
        
        // reduce - 带初始值
        int reducedWithInit = numbers.stream().reduce(0, (a, b) -> a + b);
        System.out.println("reduce(带初始值求和): " + reducedWithInit);
        
        // anyMatch - 任意匹配
        boolean anyMatch = numbers.stream().anyMatch(n -> n > 8);
        System.out.println("anyMatch(存在大于8的元素): " + anyMatch);
        
        // allMatch - 全部匹配
        boolean allMatch = numbers.stream().allMatch(n -> n > 0);
        System.out.println("allMatch(所有元素大于0): " + allMatch);
        
        // noneMatch - 无匹配
        boolean noneMatch = numbers.stream().noneMatch(n -> n < 0);
        System.out.println("noneMatch(无元素小于0): " + noneMatch);
        
        // findFirst - 找到第一个
        Optional<Integer> first = numbers.stream().findFirst();
        System.out.println("findFirst(第一个元素): " + first.orElse(0));
        
        // findAny - 找到任意一个
        Optional<Integer> any = numbers.stream().findAny();
        System.out.println("findAny(任意一个元素): " + any.orElse(0));
    }

    /**
     * 数值Stream特化示例
     */
    public void numericStreams() {
        System.out.println("\n=== 数值Stream特化示例 ===");
        
        // IntStream.range - 不包含结束值
        IntStream range = IntStream.range(1, 5);
        System.out.println("IntStream.range(1, 5): " + range.boxed().collect(Collectors.toList()));
        
        // IntStream.rangeClosed - 包含结束值
        IntStream rangeClosed = IntStream.rangeClosed(1, 5);
        System.out.println("IntStream.rangeClosed(1, 5): " + rangeClosed.boxed().collect(Collectors.toList()));
        
        // 数值Stream的常用操作
        int sum = IntStream.rangeClosed(1, 10).sum();
        System.out.println("IntStream求和(1-10): " + sum);
        
        double average = IntStream.rangeClosed(1, 10).average().orElse(0.0);
        System.out.println("IntStream平均值(1-10): " + average);
        
        int max = IntStream.rangeClosed(1, 10).max().orElse(0);
        System.out.println("IntStream最大值(1-10): " + max);
        
        int min = IntStream.rangeClosed(1, 10).min().orElse(0);
        System.out.println("IntStream最小值(1-10): " + min);
    }

    /**
     * 并行Stream示例
     */
    public void parallelStream() {
        System.out.println("\n=== 并行Stream示例 ===");
        
        List<Integer> numbers = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
        
        // 串行Stream
        long startTime = System.currentTimeMillis();
        int sumSequential = numbers.stream()
                .filter(n -> n % 2 == 0)
                .mapToInt(Integer::intValue)
                .sum();
        long endTime = System.currentTimeMillis();
        System.out.println("串行Stream求和: " + sumSequential + ", 耗时: " + (endTime - startTime) + "ms");
        
        // 并行Stream
        startTime = System.currentTimeMillis();
        int sumParallel = numbers.parallelStream()
                .filter(n -> n % 2 == 0)
                .mapToInt(Integer::intValue)
                .sum();
        endTime = System.currentTimeMillis();
        System.out.println("并行Stream求和: " + sumParallel + ", 耗时: " + (endTime - startTime) + "ms");
    }

    /**
     * 综合示例
     */
    public void comprehensiveExample() {
        System.out.println("\n=== 综合示例 ===");
        
        List<String> words = Arrays.asList(
                "apple", "banana", "cherry", "date", "elderberry",
                "fig", "grape", "honeydew", "kiwi", "lemon"
        );
        
        List<String> result = words.stream()
                .filter(word -> word.length() > 4)      // 过滤长度大于4的单词
                .map(String::toUpperCase)               // 转换为大写
                .sorted((a, b) -> b.compareTo(a))       // 倒序排序
                .limit(5)                               // 取前5个
                .collect(Collectors.toList());           // 收集到List
        
        System.out.println("原始数据: " + words);
        System.out.println("处理结果: " + result);
    }
}
