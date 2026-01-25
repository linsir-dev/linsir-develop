package com.linsir.abc.pdai.tools.guava;

/**
 * Guava 示例代码演示主类
 * 用于运行和验证所有 Guava 库的示例代码
 */
public class GuavaDemoMain {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("Guava 库示例代码验证");
        System.out.println("==========================================");
        
        try {
            // 运行 Guava 集合工具类示例
            System.out.println("\n1. 运行 Guava 集合工具类示例:");
            System.out.println("------------------------------------------");
            GuavaCollectionsDemo.demonstrateImmutableCollections();
            GuavaCollectionsDemo.demonstrateNewCollectionTypes();
            GuavaCollectionsDemo.demonstrateCollectionUtilities();
            GuavaCollectionsDemo.demonstrateCollectionTransformations();
            
            // 运行 Guava 字符串工具类示例
            System.out.println("\n2. 运行 Guava 字符串工具类示例:");
            System.out.println("------------------------------------------");
            GuavaStringsDemo.demonstrateCharMatcher();
            GuavaStringsDemo.demonstrateSplitter();
            GuavaStringsDemo.demonstrateJoiner();
            GuavaStringsDemo.demonstrateStrings();
            GuavaStringsDemo.demonstrateCaseFormat();
            GuavaStringsDemo.demonstrateStringProcessing();
            
            // 运行 Guava 缓存示例
            System.out.println("\n3. 运行 Guava 缓存示例:");
            System.out.println("------------------------------------------");
            GuavaCacheDemo.demonstrateBasicCache();
            GuavaCacheDemo.demonstrateCacheExpiration();
            GuavaCacheDemo.demonstrateCacheLoader();
            GuavaCacheDemo.demonstrateCacheRemovalListener();
            GuavaCacheDemo.demonstrateCacheStats();
            GuavaCacheDemo.demonstrateCacheRefresh();
            GuavaCacheDemo.demonstrateCacheSizeLimit();
            GuavaCacheDemo.demonstrateCacheUsage();
            
            // 运行 Guava 函数式编程示例
            System.out.println("\n4. 运行 Guava 函数式编程示例:");
            System.out.println("------------------------------------------");
            GuavaFunctionalDemo.demonstrateFunction();
            GuavaFunctionalDemo.demonstratePredicate();
            GuavaFunctionalDemo.demonstrateSupplier();
            GuavaFunctionalDemo.demonstrateConsumer();
            GuavaFunctionalDemo.demonstrateFunctionsUtils();
            GuavaFunctionalDemo.demonstratePredicatesUtils();
            GuavaFunctionalDemo.demonstrateSuppliersUtils();
            GuavaFunctionalDemo.demonstrateFunctionalProgramming();
            GuavaFunctionalDemo.demonstrateOptional();
            
            // 运行 Guava 并发工具示例
            System.out.println("\n5. 运行 Guava 并发工具示例:");
            System.out.println("------------------------------------------");
            GuavaConcurrencyDemo.demonstrateListenableFuture();
            GuavaConcurrencyDemo.demonstrateRateLimiter();
            GuavaConcurrencyDemo.demonstrateStriped();
            GuavaConcurrencyDemo.demonstrateMonitor();
            GuavaConcurrencyDemo.demonstrateMoreExecutors();
            GuavaConcurrencyDemo.demonstrateFutures();
            
            System.out.println("\n==========================================");
            System.out.println("Guava 库示例代码验证完成！");
            System.out.println("==========================================");
            
        } catch (Exception e) {
            System.err.println("运行示例时发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
