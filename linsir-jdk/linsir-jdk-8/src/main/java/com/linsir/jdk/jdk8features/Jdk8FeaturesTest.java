package com.linsir.jdk.jdk8features;

/**
 * JDK 8 特征综合测试类
 */
public class Jdk8FeaturesTest {
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("JDK 8 特征综合测试");
        System.out.println("=============================================");
        
        // 测试匿名类简写示例
        AnonymousClassDemo anonymousClassDemo = new AnonymousClassDemo();
        anonymousClassDemo.demonstrate();
        
        // 测试forEach Lambdas示例
        ForEachLambdasDemo forEachLambdasDemo = new ForEachLambdasDemo();
        forEachLambdasDemo.demonstrate();
        
        // 测试方法引用示例
        MethodReferencesDemo methodReferencesDemo = new MethodReferencesDemo();
        methodReferencesDemo.demonstrate();
        
        // 测试Filter & Predicate常规用法
        FilterPredicateDemo filterPredicateDemo = new FilterPredicateDemo();
        filterPredicateDemo.demonstrate();
        
        // 测试Map&Reduce常规用法
        MapReduceDemo mapReduceDemo = new MapReduceDemo();
        mapReduceDemo.demonstrate();
        
        // 测试Collectors常规用法
        CollectorsDemo collectorsDemo = new CollectorsDemo();
        collectorsDemo.demonstrate();
        
        // 测试flatMap常规用法
        FlatMapDemo flatMapDemo = new FlatMapDemo();
        flatMapDemo.demonstrate();
        
        // 测试distinct常规用法
        DistinctDemo distinctDemo = new DistinctDemo();
        distinctDemo.demonstrate();
        
        // 测试count常规用法
        CountDemo countDemo = new CountDemo();
        countDemo.demonstrate();
        
        // 测试Match示例
        MatchDemo matchDemo = new MatchDemo();
        matchDemo.demonstrate();
        
        // 测试min, max, summaryStatistics示例
        MinMaxSummaryDemo minMaxSummaryDemo = new MinMaxSummaryDemo();
        minMaxSummaryDemo.demonstrate();
        
        // 测试peek示例
        PeekDemo peekDemo = new PeekDemo();
        peekDemo.demonstrate();
        
        // 测试FunctionalInterface示例
        FunctionalInterfaceDemo functionalInterfaceDemo = new FunctionalInterfaceDemo();
        functionalInterfaceDemo.demonstrate();
        
        // 测试内置四大函数接口示例
        BuiltInFunctionalInterfacesDemo builtInFunctionalInterfacesDemo = new BuiltInFunctionalInterfacesDemo();
        builtInFunctionalInterfacesDemo.demonstrate();
        
        System.out.println("=============================================");
        System.out.println("JDK 8 特征综合测试完成");
        System.out.println("=============================================");
    }
}
