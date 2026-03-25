package com.linsir.abc.core.base.lang.wrapper;

import java.lang.reflect.Field;

/**
 * Integer缓存深度分析
 * 
 * 本类深入分析Integer缓存的实现机制：
 * 1. 通过反射查看Integer内部的缓存实现
 * 2. 分析缓存的初始化和使用
 * 3. 测试不同JVM参数对缓存的影响
 * 4. 缓存的性能影响分析
 * 
 * Integer缓存实现要点：
 * - 使用静态内部类IntegerCache实现延迟加载
 * - 默认缓存范围：-128 ~ 127
 * - 可通过-XX:AutoBoxCacheMax参数调整上限
 * - 使用valueOf方法时优先返回缓存实例
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class IntegerCacheAnalysis {
    
    // 缓存范围常量
    private static final int DEFAULT_LOW = -128;
    private static final int DEFAULT_HIGH = 127;
    
    /**
     * 分析Integer缓存的内部实现
     */
    public void analyzeCacheImplementation() {
        System.out.println("========== Integer缓存实现分析 ==========");
        
        try {
            // 获取IntegerCache类
            Class<?> cacheClass = Class.forName("java.lang.Integer$IntegerCache");
            
            // 获取cache字段
            Field cacheField = cacheClass.getDeclaredField("cache");
            cacheField.setAccessible(true);
            Integer[] cache = (Integer[]) cacheField.get(null);
            
            // 获取low字段
            Field lowField = cacheClass.getDeclaredField("low");
            lowField.setAccessible(true);
            int low = lowField.getInt(null);
            
            // 获取high字段
            Field highField = cacheClass.getDeclaredField("high");
            highField.setAccessible(true);
            int high = highField.getInt(null);
            
            System.out.println("缓存数组长度: " + cache.length);
            System.out.println("缓存下限 (low): " + low);
            System.out.println("缓存上限 (high): " + high);
            System.out.println("缓存范围: " + low + " ~ " + high);
            
            // 验证缓存内容
            System.out.println("\n缓存内容验证:");
            System.out.println("cache[0] = " + cache[0] + " (应为 " + low + ")");
            System.out.println("cache[" + (cache.length - 1) + "] = " + 
                cache[cache.length - 1] + " (应为 " + high + ")");
            
            // 验证缓存对象的身份
            System.out.println("\n缓存对象身份验证:");
            Integer cachedValue = cache[128];  // 对应数值0
            Integer zero = Integer.valueOf(0);
            System.out.println("cache[128] == Integer.valueOf(0): " + (cachedValue == zero));
            
        } catch (Exception e) {
            System.err.println("分析失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试缓存边界值
     */
    public void testCacheBoundaries() {
        System.out.println("\n========== 缓存边界值测试 ==========");
        
        // 测试缓存下限边界
        System.out.println("\n--- 下限边界测试 ---");
        testValue(DEFAULT_LOW - 1);  // -129，超出缓存
        testValue(DEFAULT_LOW);       // -128，缓存边界
        testValue(DEFAULT_LOW + 1);   // -127，缓存内
        
        // 测试缓存上限边界
        System.out.println("\n--- 上限边界测试 ---");
        testValue(DEFAULT_HIGH - 1);  // 126，缓存内
        testValue(DEFAULT_HIGH);       // 127，缓存边界
        testValue(DEFAULT_HIGH + 1);   // 128，超出缓存
    }
    
    /**
     * 测试单个值是否在缓存中
     * 
     * @param value 要测试的值
     */
    private void testValue(int value) {
        Integer a = Integer.valueOf(value);
        Integer b = Integer.valueOf(value);
        boolean sameObject = (a == b);
        
        System.out.println("值: " + value + 
            ", 使用缓存: " + sameObject +
            (value >= DEFAULT_LOW && value <= DEFAULT_HIGH ? " (在缓存范围内)" : " (超出缓存范围)"));
    }
    
    /**
     * 比较valueOf和new Integer的性能
     */
    public void comparePerformance() {
        System.out.println("\n========== 性能比较测试 ==========");
        
        final int ITERATIONS = 10_000_000;
        
        // 测试Integer.valueOf（使用缓存）
        long start1 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            Integer value = Integer.valueOf(i % 256 - 128);  // 使用缓存范围内的值
        }
        long duration1 = System.nanoTime() - start1;
        
        // 测试new Integer（创建新对象）
        long start2 = System.nanoTime();
        for (int i = 0; i < ITERATIONS; i++) {
            Integer value = new Integer(i % 256 - 128);  // 创建新对象
        }
        long duration2 = System.nanoTime() - start2;
        
        System.out.println("迭代次数: " + ITERATIONS);
        System.out.println("Integer.valueOf()耗时: " + (duration1 / 1_000_000) + " ms");
        System.out.println("new Integer()耗时: " + (duration2 / 1_000_000) + " ms");
        System.out.println("性能提升: " + String.format("%.2f", (double)duration2 / duration1) + "x");
        
        // 内存占用比较
        System.out.println("\n内存占用分析:");
        System.out.println("Integer.valueOf(): 复用缓存对象，内存占用低");
        System.out.println("new Integer(): 每次创建新对象，内存占用高");
    }
    
    /**
     * 分析自动装箱的行为
     */
    public void analyzeAutoBoxing() {
        System.out.println("\n========== 自动装箱行为分析 ==========");
        
        // 编译器会将 Integer i = 100; 转换为 Integer i = Integer.valueOf(100);
        
        System.out.println("\n--- 字面量赋值 ---");
        Integer a = 100;  // 使用缓存
        Integer b = 100;
        System.out.println("Integer a = 100;");
        System.out.println("Integer b = 100;");
        System.out.println("a == b: " + (a == b) + " (使用缓存)");
        
        System.out.println("\n--- 表达式结果 ---");
        Integer c = 50 + 50;  // 编译时常量，直接计算为100
        System.out.println("Integer c = 50 + 50;");
        System.out.println("a == c: " + (a == c) + " (编译时常量优化)");
        
        System.out.println("\n--- 变量参与运算 ---");
        int x = 50;
        int y = 50;
        Integer d = x + y;  // 运行时计算，然后装箱
        System.out.println("int x = 50, y = 50;");
        System.out.println("Integer d = x + y;");
        System.out.println("a == d: " + (a == d) + " (运行时计算后装箱)");
        
        System.out.println("\n--- 方法返回值 ---");
        Integer e = getInteger100();  // 方法返回值装箱
        System.out.println("Integer e = getInteger100();");
        System.out.println("a == e: " + (a == e));
    }
    
    /**
     * 返回100的方法
     * 
     * @return Integer 100
     */
    private Integer getInteger100() {
        return 100;  // 自动装箱
    }
    
    /**
     * 演示缓存的线程安全性
     */
    public void demonstrateThreadSafety() {
        System.out.println("\n========== 线程安全性测试 ==========");
        
        System.out.println("Integer缓存是线程安全的:");
        System.out.println("1. 缓存数组在静态初始化块中创建");
        System.out.println("2. 数组创建后不可变（引用不可变，内容也不变）");
        System.out.println("3. 多个线程可以安全地共享缓存的Integer对象");
        
        // 多线程测试
        final int THREAD_COUNT = 10;
        final int ITERATIONS_PER_THREAD = 10000;
        Thread[] threads = new Thread[THREAD_COUNT];
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < ITERATIONS_PER_THREAD; j++) {
                    // 所有线程都获取缓存中的同一个对象
                    Integer value = Integer.valueOf(100);
                    // 验证对象一致性
                    if (value != Integer.valueOf(100)) {
                        System.err.println("线程安全问题！");
                    }
                }
            });
        }
        
        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("多线程测试完成，未发现问题");
    }
    
    /**
     * 分析JVM参数对缓存的影响
     */
    public void analyzeJvmParameter() {
        System.out.println("\n========== JVM参数影响分析 ==========");
        
        System.out.println("可通过以下JVM参数调整Integer缓存:");
        System.out.println("-XX:AutoBoxCacheMax=<size>");
        System.out.println();
        System.out.println("示例:");
        System.out.println("java -XX:AutoBoxCacheMax=500 MyClass");
        System.out.println("将缓存范围调整为 -128 ~ 500");
        System.out.println();
        
        // 显示当前缓存上限
        try {
            Class<?> cacheClass = Class.forName("java.lang.Integer$IntegerCache");
            Field highField = cacheClass.getDeclaredField("high");
            highField.setAccessible(true);
            int high = highField.getInt(null);
            
            System.out.println("当前缓存上限: " + high);
            
            if (high == DEFAULT_HIGH) {
                System.out.println("(使用默认值127，未设置JVM参数)");
            } else {
                System.out.println("(通过-XX:AutoBoxCacheMax设置)");
            }
            
        } catch (Exception e) {
            System.err.println("获取缓存上限失败: " + e.getMessage());
        }
    }
    
    /**
     * 提供最佳实践建议
     */
    public void provideBestPractices() {
        System.out.println("\n========== 最佳实践建议 ==========");
        
        System.out.println("1. 优先使用自动装箱/拆箱:");
        System.out.println("   Integer i = 100;  // 推荐");
        System.out.println("   Integer i = new Integer(100);  // 不推荐，已过时");
        System.out.println();
        
        System.out.println("2. 使用equals进行值比较:");
        System.out.println("   if (a.equals(b))  // 推荐");
        System.out.println("   if (a == b)  // 不推荐，可能出错");
        System.out.println();
        
        System.out.println("3. 注意缓存范围:");
        System.out.println("   - 小整数比较时使用==可能成功（缓存内）");
        System.out.println("   - 大整数比较时使用==会失败（超出缓存）");
        System.out.println();
        
        System.out.println("4. 避免不必要的装箱拆箱:");
        System.out.println("   // 不推荐");
        System.out.println("   Integer sum = 0;");
        System.out.println("   for (Integer i : list) sum += i;");
        System.out.println();
        System.out.println("   // 推荐");
        System.out.println("   int sum = 0;");
        System.out.println("   for (int i : list) sum += i;");
        System.out.println();
        
        System.out.println("5. 根据场景调整缓存大小:");
        System.out.println("   - 如果应用频繁使用较大的整数，可考虑增大缓存");
        System.out.println("   - 使用-XX:AutoBoxCacheMax参数");
        System.out.println("   - 注意：增大缓存会增加内存占用");
    }
    
    /**
     * 运行所有分析
     */
    public void runAllAnalysis() {
        analyzeCacheImplementation();
        testCacheBoundaries();
        comparePerformance();
        analyzeAutoBoxing();
        demonstrateThreadSafety();
        analyzeJvmParameter();
        provideBestPractices();
    }
}
