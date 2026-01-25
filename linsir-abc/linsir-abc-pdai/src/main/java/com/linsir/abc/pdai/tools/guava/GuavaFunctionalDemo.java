package com.linsir.abc.pdai.tools.guava;

import com.google.common.base.*;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Guava 函数式编程示例
 * 演示 Guava 提供的函数式编程工具
 */
public class GuavaFunctionalDemo {

    /**
     * 演示 Function 接口
     */
    public static void demonstrateFunction() {
        System.out.println("=== Function 接口示例 ===");
        
        // 创建一个将字符串转换为整数的函数
        Function<String, Integer> stringToInt = new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return Integer.parseInt(input);
            }
        };
        
        // 使用函数
        Integer result = stringToInt.apply("123");
        System.out.println("将字符串 '123' 转换为整数: " + result);
        
        // 创建一个将整数加倍的函数
        Function<Integer, Integer> doubleFunction = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input * 2;
            }
        };
        
        // 函数组合
        Function<String, Integer> stringToDoubleInt = Functions.compose(doubleFunction, stringToInt);
        Integer composedResult = stringToDoubleInt.apply("123");
        System.out.println("将字符串 '123' 转换为整数并加倍: " + composedResult);
        
        // 恒等函数
        Function<String, String> identityFunction = Functions.identity();
        System.out.println("恒等函数应用于 'test': " + identityFunction.apply("test"));
    }

    /**
     * 演示 Predicate 接口
     */
    public static void demonstratePredicate() {
        System.out.println("\n=== Predicate 接口示例 ===");
        
        // 创建一个判断字符串是否为空的谓词
        Predicate<String> isEmpty = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return Strings.isNullOrEmpty(input);
            }
        };
        
        // 使用谓词
        System.out.println("判断空字符串: " + isEmpty.apply(""));
        System.out.println("判断非空字符串: " + isEmpty.apply("test"));
        
        // 创建一个判断字符串长度是否大于 3 的谓词
        Predicate<String> lengthGreaterThan3 = new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input != null && input.length() > 3;
            }
        };
        
        // 谓词组合 - 与
        Predicate<String> nonEmptyAndLongerThan3 = Predicates.and(isEmpty, lengthGreaterThan3);
        System.out.println("判断空字符串且长度大于 3: " + nonEmptyAndLongerThan3.apply("test"));
        
        // 谓词组合 - 或
        Predicate<String> emptyOrLongerThan3 = Predicates.or(isEmpty, lengthGreaterThan3);
        System.out.println("判断空字符串或长度大于 3: " + emptyOrLongerThan3.apply("test"));
        System.out.println("判断空字符串或长度大于 3: " + emptyOrLongerThan3.apply(""));
        
        // 谓词组合 - 非
        Predicate<String> notEmpty = Predicates.not(isEmpty);
        System.out.println("判断非空字符串: " + notEmpty.apply("test"));
        
        // 集合过滤
        List<String> strings = Lists.newArrayList("", "a", "ab", "abc", "abcd", "abcde");
        System.out.println("原始字符串列表: " + strings);
        List<String> filteredStrings = Lists.newArrayList(Collections2.filter(strings, lengthGreaterThan3));
        System.out.println("过滤后长度大于 3 的字符串: " + filteredStrings);
    }

    /**
     * 演示 Supplier 接口
     */
    public static void demonstrateSupplier() {
        System.out.println("\n=== Supplier 接口示例 ===");
        
        // 创建一个提供当前时间的供应商
        Supplier<Long> currentTimeSupplier = new Supplier<Long>() {
            @Override
            public Long get() {
                return System.currentTimeMillis();
            }
        };
        
        // 使用供应商
        System.out.println("当前时间戳: " + currentTimeSupplier.get());
        
        // 延迟 1 秒后再次获取
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("1 秒后时间戳: " + currentTimeSupplier.get());
        
        // 常量供应商
        Supplier<String> constantSupplier = Suppliers.ofInstance("Hello, Guava!");
        System.out.println("常量供应商: " + constantSupplier.get());
        System.out.println("常量供应商（再次调用）: " + constantSupplier.get());
        
        // 记忆化供应商（缓存结果）
        Supplier<Long> memoizedSupplier = Suppliers.memoizeWithExpiration(currentTimeSupplier, 500, java.util.concurrent.TimeUnit.MILLISECONDS);
        System.out.println("记忆化供应商（首次）: " + memoizedSupplier.get());
        System.out.println("记忆化供应商（立即再次调用）: " + memoizedSupplier.get());
        
        // 延迟 600 毫秒后再次获取（超过过期时间）
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("600 毫秒后记忆化供应商: " + memoizedSupplier.get());
    }

    /**
     * 演示 Consumer 接口
     */
    public static void demonstrateConsumer() {
        System.out.println("\n=== Consumer 接口示例 ===");
        
        // 创建一个打印字符串的消费者
        Consumer<String> printConsumer = new Consumer<String>() {
            @Override
            public void accept(String input) {
                System.out.println("消费: " + input);
            }
        };
        
        // 使用消费者
        printConsumer.accept("Hello");
        printConsumer.accept("Guava");
        
        // 批量消费
        List<String> items = Lists.newArrayList("Item 1", "Item 2", "Item 3");
        System.out.println("批量消费:");
        for (String item : items) {
            printConsumer.accept(item);
        }
        
        // 消费者链（模拟）
        Consumer<String> upperCaseConsumer = new Consumer<String>() {
            @Override
            public void accept(String input) {
                System.out.println("转换为大写: " + input.toUpperCase());
            }
        };
        
        System.out.println("消费者链:");
        for (String item : items) {
            printConsumer.accept(item);
            upperCaseConsumer.accept(item);
        }
    }

    /**
     * 演示 Functions 工具类
     */
    public static void demonstrateFunctionsUtils() {
        System.out.println("\n=== Functions 工具类示例 ===");
        
        // forMap 函数
        java.util.Map<String, Integer> map = com.google.common.collect.Maps.newHashMap();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        
        Function<String, Integer> mapFunction = Functions.forMap(map, -1); // 未找到时返回 -1
        System.out.println("Functions.forMap('one'): " + mapFunction.apply("one"));
        System.out.println("Functions.forMap('four'): " + mapFunction.apply("four"));
        
        // constant 函数
        Function<Object, String> constantFunction = Functions.constant("Constant Value");
        System.out.println("Functions.constant 应用于任意对象: " + constantFunction.apply(new Object()));
        
        // toStringFunction
        Function<Object, String> toStringFunction = Functions.toStringFunction();
        System.out.println("Functions.toStringFunction 应用于 123: " + toStringFunction.apply(123));
    }

    /**
     * 演示 Predicates 工具类
     */
    public static void demonstratePredicatesUtils() {
        System.out.println("\n=== Predicates 工具类示例 ===");
        
        // alwaysTrue 和 alwaysFalse
        System.out.println("Predicates.alwaysTrue(): " + Predicates.alwaysTrue().apply("anything"));
        System.out.println("Predicates.alwaysFalse(): " + Predicates.alwaysFalse().apply("anything"));
        
        // isNull 和 notNull
        System.out.println("Predicates.isNull() 应用于 null: " + Predicates.isNull().apply(null));
        System.out.println("Predicates.notNull() 应用于 null: " + Predicates.notNull().apply(null));
        System.out.println("Predicates.notNull() 应用于 'test': " + Predicates.notNull().apply("test"));
        
        // instanceOf
        System.out.println("Predicates.instanceOf(String.class) 应用于 'test': " + Predicates.instanceOf(String.class).apply("test"));
        System.out.println("Predicates.instanceOf(Integer.class) 应用于 'test': " + Predicates.instanceOf(Integer.class).apply("test"));
        
        // equalTo
        System.out.println("Predicates.equalTo('test') 应用于 'test': " + Predicates.equalTo("test").apply("test"));
        System.out.println("Predicates.equalTo('test') 应用于 'other': " + Predicates.equalTo("test").apply("other"));
    }

    /**
     * 演示 Suppliers 工具类
     */
    public static void demonstrateSuppliersUtils() {
        System.out.println("\n=== Suppliers 工具类示例 ===");
        
        // ofInstance
        Supplier<String> instanceSupplier = Suppliers.ofInstance("Instance");
        System.out.println("Suppliers.ofInstance: " + instanceSupplier.get());
        
        // memoize
        Supplier<Long> timeSupplier = new Supplier<Long>() {
            @Override
            public Long get() {
                System.out.println("计算时间戳...");
                return System.currentTimeMillis();
            }
        };
        
        Supplier<Long> memoized = Suppliers.memoize(timeSupplier);
        System.out.println("Suppliers.memoize 首次调用: " + memoized.get());
        System.out.println("Suppliers.memoize 再次调用: " + memoized.get());
        
        // 从 Callable 创建 Supplier
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Callable result";
            }
        };
        
        Supplier<String> supplierFromCallable = Suppliers.supplier(callable);
        System.out.println("Suppliers.supplier 从 Callable 创建: " + supplierFromCallable.get());
    }

    /**
     * 演示函数式编程综合示例
     */
    public static void demonstrateFunctionalProgramming() {
        System.out.println("\n=== 函数式编程综合示例 ===");
        
        // 数据
        List<String> inputList = Lists.newArrayList("1", "2", "3", "4", "5", "", "6");
        System.out.println("原始输入列表: " + inputList);
        
        // 1. 过滤掉空字符串
        Predicate<String> notEmpty = Predicates.not(Predicates.equalTo(""));
        List<String> filteredList = Lists.newArrayList(Collections2.filter(inputList, notEmpty));
        System.out.println("过滤后列表: " + filteredList);
        
        // 2. 将字符串转换为整数
        Function<String, Integer> stringToInt = new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return Integer.parseInt(input);
            }
        };
        
        // 3. 将整数加倍
        Function<Integer, Integer> doubleFunction = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input * 2;
            }
        };
        
        // 4. 组合函数
        Function<String, Integer> stringToDoubleInt = Functions.compose(doubleFunction, stringToInt);
        
        // 5. 应用函数到列表
        List<Integer> resultList = new ArrayList<>();
        for (String s : filteredList) {
            resultList.add(stringToDoubleInt.apply(s));
        }
        System.out.println("最终结果列表: " + resultList);
        
        // 6. 使用 Suppliers 生成结果
        Supplier<List<Integer>> resultSupplier = new Supplier<List<Integer>>() {
            @Override
            public List<Integer> get() {
                List<Integer> list = new ArrayList<>();
                for (String s : filteredList) {
                    list.add(stringToDoubleInt.apply(s));
                }
                return list;
            }
        };
        
        System.out.println("从 Supplier 获取结果: " + resultSupplier.get());
    }

    /**
     * 演示 Optional 类
     */
    public static void demonstrateOptional() {
        System.out.println("\n=== Optional 类示例 ===");
        
        // 创建 Optional
        Optional<String> presentOptional = Optional.of("Hello");
        Optional<String> absentOptional = Optional.absent();
        Optional<String> nullableOptional = Optional.fromNullable(null);
        Optional<String> nullablePresentOptional = Optional.fromNullable("World");
        
        // 检查是否存在
        System.out.println("presentOptional.isPresent(): " + presentOptional.isPresent());
        System.out.println("absentOptional.isPresent(): " + absentOptional.isPresent());
        System.out.println("nullableOptional.isPresent(): " + nullableOptional.isPresent());
        System.out.println("nullablePresentOptional.isPresent(): " + nullablePresentOptional.isPresent());
        
        // 获取值
        System.out.println("presentOptional.get(): " + presentOptional.get());
        
        // 获取值或默认值
        System.out.println("absentOptional.or('Default'): " + absentOptional.or("Default"));
        System.out.println("absentOptional.orNull(): " + absentOptional.orNull());
        
        // 转换 Optional
        Optional<Integer> transformedOptional = presentOptional.transform(new Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return input.length();
            }
        });
        System.out.println("转换后的 Optional: " + transformedOptional.get());
    }
}
