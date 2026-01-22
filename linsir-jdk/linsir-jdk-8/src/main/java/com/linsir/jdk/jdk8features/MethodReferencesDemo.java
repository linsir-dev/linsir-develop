package com.linsir.jdk.jdk8features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 方法引用示例
 */
public class MethodReferencesDemo {

    public void demonstrate() {
        System.out.println("\n=== 方法引用示例 ===");
        
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        // 1. 构造引用: ClassName::new
        System.out.println("1. 构造引用:");
        Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> newList = listSupplier.get();
        System.out.println("创建新列表: " + newList);
        
        // 2. 对象::实例方法
        System.out.println("\n2. 对象::实例方法:");
        Consumer<String> printer = System.out::println;
        printer.accept("对象::实例方法示例");
        
        // 3. 类名::静态方法
        System.out.println("\n3. 类名::静态方法:");
        Function<Integer, String> stringFunction = String::valueOf;
        String result = stringFunction.apply(123);
        System.out.println("类名::静态方法示例: " + result);
        
        // 4. 类名::实例方法
        System.out.println("\n4. 类名::实例方法:");
        List<String> sortedNames = names.stream()
                .sorted(String::compareTo)
                .collect(Collectors.toList());
        System.out.println("排序前: " + names);
        System.out.println("排序后: " + sortedNames);
    }
}
