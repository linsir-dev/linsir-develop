package com.linsir.abc.pdai.tools.guava;

import com.google.common.cache.*;
import com.google.common.util.concurrent.UncheckedExecutionException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Guava 缓存示例
 * 演示 Guava 提供的强大内存缓存功能
 */
public class GuavaCacheDemo {

    /**
     * 演示基本缓存操作
     */
    public static void demonstrateBasicCache() {
        System.out.println("=== 基本缓存操作示例 ===");
        
        // 创建基本缓存
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(100) // 最大缓存大小
                .build();
        
        // 添加缓存项
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        
        // 获取缓存项
        String value1 = cache.getIfPresent("key1");
        System.out.println("获取缓存项 key1: " + value1);
        
        // 获取不存在的缓存项
        String value4 = cache.getIfPresent("key4");
        System.out.println("获取不存在的缓存项 key4: " + value4);
        
        // 移除缓存项
        cache.invalidate("key1");
        System.out.println("移除 key1 后，获取 key1: " + cache.getIfPresent("key1"));
        
        // 清空缓存
        cache.invalidateAll();
        System.out.println("清空缓存后，获取 key2: " + cache.getIfPresent("key2"));
    }

    /**
     * 演示缓存过期策略
     */
    public static void demonstrateCacheExpiration() throws InterruptedException {
        System.out.println("\n=== 缓存过期策略示例 ===");
        
        // 创建具有过期策略的缓存
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(2, TimeUnit.SECONDS) // 写入后过期
                .expireAfterAccess(1, TimeUnit.SECONDS) // 访问后过期
                .build();
        
        // 添加缓存项
        cache.put("key1", "value1");
        System.out.println("添加缓存项 key1: " + cache.getIfPresent("key1"));
        
        // 等待 0.5 秒后访问
        Thread.sleep(500);
        System.out.println("0.5 秒后访问 key1: " + cache.getIfPresent("key1"));
        
        // 等待 1.5 秒后访问（超过访问过期时间）
        Thread.sleep(1500);
        System.out.println("1.5 秒后访问 key1: " + cache.getIfPresent("key1"));
        
        // 重新添加缓存项
        cache.put("key1", "value1");
        System.out.println("重新添加缓存项 key1: " + cache.getIfPresent("key1"));
        
        // 等待 2.5 秒后访问（超过写入过期时间）
        Thread.sleep(2500);
        System.out.println("2.5 秒后访问 key1: " + cache.getIfPresent("key1"));
    }

    /**
     * 演示缓存加载器
     */
    public static void demonstrateCacheLoader() {
        System.out.println("\n=== 缓存加载器示例 ===");
        
        // 创建带有加载器的缓存
        LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        // 模拟从数据源加载数据
                        System.out.println("缓存未命中，从数据源加载: " + key);
                        return "加载的值 for " + key;
                    }
                });
        
        try {
            // 获取缓存项（首次加载）
            String value1 = loadingCache.get("key1");
            System.out.println("首次获取 key1: " + value1);
            
            // 再次获取缓存项（缓存命中）
            String value1Again = loadingCache.get("key1");
            System.out.println("再次获取 key1: " + value1Again);
            
            // 获取另一个缓存项（首次加载）
            String value2 = loadingCache.get("key2");
            System.out.println("首次获取 key2: " + value2);
            
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示缓存移除监听器
     */
    public static void demonstrateCacheRemovalListener() {
        System.out.println("\n=== 缓存移除监听器示例 ===");
        
        // 创建带有移除监听器的缓存
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(3) // 最大缓存大小为 3
                .removalListener(new RemovalListener<String, String>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, String> notification) {
                        System.out.println("缓存项被移除: " + notification.getKey() + " = " + notification.getValue() + ", 原因: " + notification.getCause());
                    }
                })
                .build();
        
        // 添加 4 个缓存项，触发缓存淘汰
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        System.out.println("添加 key1, key2, key3 后缓存大小: " + cache.size());
        
        // 添加第 4 个缓存项，触发 LRU 淘汰
        cache.put("key4", "value4");
        System.out.println("添加 key4 后缓存大小: " + cache.size());
        System.out.println("缓存中是否存在 key1: " + (cache.getIfPresent("key1") != null));
        
        // 手动移除缓存项
        cache.invalidate("key2");
        System.out.println("手动移除 key2 后，缓存中是否存在 key2: " + (cache.getIfPresent("key2") != null));
    }

    /**
     * 演示缓存统计
     */
    public static void demonstrateCacheStats() {
        System.out.println("\n=== 缓存统计示例 ===");
        
        // 创建启用统计的缓存
        LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .recordStats() // 启用统计
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        return "加载的值 for " + key;
                    }
                });
        
        try {
            // 模拟缓存操作
            loadingCache.get("key1"); // 缓存未命中
            loadingCache.get("key1"); // 缓存命中
            loadingCache.get("key1"); // 缓存命中
            loadingCache.get("key2"); // 缓存未命中
            loadingCache.get("key2"); // 缓存命中
            
            // 获取缓存统计信息
            CacheStats stats = loadingCache.stats();
            System.out.println("缓存统计信息:");
            System.out.println("缓存请求次数: " + stats.requestCount());
            System.out.println("缓存命中次数: " + stats.hitCount());
            System.out.println("缓存未命中次数: " + stats.missCount());
            System.out.println("缓存命中率: " + stats.hitRate());
            System.out.println("缓存加载次数: " + stats.loadCount());
            System.out.println("缓存加载成功次数: " + stats.loadSuccessCount());
            System.out.println("缓存加载失败次数: " + stats.loadExceptionCount());
            System.out.println("缓存总加载时间 (ms): " + stats.totalLoadTime() / 1000000.0);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示缓存刷新
     */
    public static void demonstrateCacheRefresh() throws InterruptedException {
        System.out.println("\n=== 缓存刷新示例 ===");
        
        // 创建支持刷新的缓存
        LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .refreshAfterWrite(2, TimeUnit.SECONDS) // 写入后刷新
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        System.out.println("加载/刷新缓存: " + key);
                        return "值 for " + key + " (时间: " + System.currentTimeMillis() / 1000 + ")";
                    }
                });
        
        try {
            // 首次加载
            System.out.println("首次加载 key1: " + loadingCache.get("key1"));
            
            // 1 秒后获取（缓存命中，不刷新）
            Thread.sleep(1000);
            System.out.println("1 秒后获取 key1: " + loadingCache.get("key1"));
            
            // 3 秒后获取（触发刷新）
            Thread.sleep(3000);
            System.out.println("3 秒后获取 key1: " + loadingCache.get("key1"));
            
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示缓存大小限制
     */
    public static void demonstrateCacheSizeLimit() {
        System.out.println("\n=== 缓存大小限制示例 ===");
        
        // 创建限制大小的缓存
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(3) // 最大缓存大小为 3
                .build();
        
        // 添加缓存项
        for (int i = 1; i <= 5; i++) {
            cache.put("key" + i, "value" + i);
            System.out.println("添加 key" + i + " 后缓存大小: " + cache.size());
            
            // 检查哪些缓存项仍然存在
            System.out.print("缓存中存在的键: ");
            for (int j = 1; j <= i; j++) {
                if (cache.getIfPresent("key" + j) != null) {
                    System.out.print("key" + j + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * 演示缓存的综合使用
     */
    public static void demonstrateCacheUsage() {
        System.out.println("\n=== 缓存综合使用示例 ===");
        
        // 创建一个功能完整的缓存
        LoadingCache<String, User> userCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .recordStats()
                .removalListener(new RemovalListener<String, User>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, User> notification) {
                        System.out.println("用户缓存被移除: " + notification.getKey() + ", 原因: " + notification.getCause());
                    }
                })
                .build(new CacheLoader<String, User>() {
                    @Override
                    public User load(String userId) throws Exception {
                        // 模拟从数据库加载用户
                        System.out.println("从数据库加载用户: " + userId);
                        return new User(userId, "User " + userId, "user" + userId + "@example.com");
                    }
                });
        
        try {
            // 获取用户（首次加载）
            User user1 = userCache.get("1");
            System.out.println("获取用户 1: " + user1);
            
            // 再次获取用户（缓存命中）
            User user1Again = userCache.get("1");
            System.out.println("再次获取用户 1: " + user1Again);
            
            // 获取另一个用户
            User user2 = userCache.get("2");
            System.out.println("获取用户 2: " + user2);
            
            // 打印缓存统计
            CacheStats stats = userCache.stats();
            System.out.println("缓存命中率: " + stats.hitRate());
            System.out.println("缓存未命中率: " + stats.missRate());
            
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户类，用于缓存示例
     */
    private static class User {
        private String id;
        private String name;
        private String email;
        
        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        @Override
        public String toString() {
            return "User{id='" + id + "', name='" + name + "', email='" + email + "'}";
        }
    }
}
