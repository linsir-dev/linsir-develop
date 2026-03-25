package com.linsir.abc.core.base.lang.string;

/**
 * 字符串拼接性能测试类
 * 
 * 本类用于测试不同字符串拼接方式的性能差异：
 * 1. 使用 + 运算符
 * 2. 使用 StringBuilder
 * 3. 使用 StringBuffer
 * 4. 使用 String.concat()
 * 5. 使用 String.join()
 * 
 * 测试结果预期：
 * - 少量拼接：+ 运算符和 StringBuilder 性能接近
 * - 大量拼接：StringBuilder 性能最优
 * - 多线程环境：StringBuffer 线程安全但性能稍差
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class StringConcatenationBenchmark {
    
    /**
     * 测试次数
     */
    private static final int ITERATIONS = 100000;
    
    /**
     * 执行性能测试
     * 
     * @return 测试结果报告
     */
    public String runBenchmark() {
        StringBuilder report = new StringBuilder();
        report.append("=== 字符串拼接性能测试 ===\n");
        report.append("测试次数: ").append(ITERATIONS).append("\n\n");
        
        // 测试1：使用 + 运算符
        long plusTime = benchmarkPlusOperator();
        report.append("1. 使用 + 运算符: ").append(plusTime).append(" ms\n");
        
        // 测试2：使用 StringBuilder
        long builderTime = benchmarkStringBuilder();
        report.append("2. 使用 StringBuilder: ").append(builderTime).append(" ms\n");
        
        // 测试3：使用 StringBuffer
        long bufferTime = benchmarkStringBuffer();
        report.append("3. 使用 StringBuffer: ").append(bufferTime).append(" ms\n");
        
        // 测试4：使用 String.concat()
        long concatTime = benchmarkStringConcat();
        report.append("4. 使用 String.concat(): ").append(concatTime).append(" ms\n");
        
        // 计算性能对比
        report.append("\n=== 性能对比 ===\n");
        report.append("StringBuilder 比 + 运算符快 ").append(String.format("%.2f", (double)plusTime / builderTime)).append(" 倍\n");
        report.append("StringBuffer 比 + 运算符快 ").append(String.format("%.2f", (double)plusTime / bufferTime)).append(" 倍\n");
        
        return report.toString();
    }
    
    /**
     * 测试使用 + 运算符的性能
     * 
     * @return 执行时间（毫秒）
     */
    public long benchmarkPlusOperator() {
        long startTime = System.currentTimeMillis();
        
        String result = "";
        for (int i = 0; i < ITERATIONS; i++) {
            result = result + i; // 每次循环都创建新的String对象
        }
        
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
    
    /**
     * 测试使用 StringBuilder 的性能
     * 
     * @return 执行时间（毫秒）
     */
    public long benchmarkStringBuilder() {
        long startTime = System.currentTimeMillis();
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ITERATIONS; i++) {
            builder.append(i); // 在同一个StringBuilder对象上追加
        }
        String result = builder.toString();
        
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
    
    /**
     * 测试使用 StringBuffer 的性能
     * 
     * @return 执行时间（毫秒）
     */
    public long benchmarkStringBuffer() {
        long startTime = System.currentTimeMillis();
        
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < ITERATIONS; i++) {
            buffer.append(i); // 线程安全的追加操作
        }
        String result = buffer.toString();
        
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
    
    /**
     * 测试使用 String.concat() 的性能
     * 
     * @return 执行时间（毫秒）
     */
    public long benchmarkStringConcat() {
        long startTime = System.currentTimeMillis();
        
        String result = "";
        for (int i = 0; i < ITERATIONS; i++) {
            result = result.concat(String.valueOf(i)); // 每次创建新字符串
        }
        
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
    
    /**
     * 测试预分配容量的 StringBuilder 性能
     * 通过预分配容量避免扩容操作，进一步提升性能
     * 
     * @return 执行时间（毫秒）
     */
    public long benchmarkStringBuilderWithCapacity() {
        long startTime = System.currentTimeMillis();
        
        // 预分配容量：每个数字平均6字符（包括负号）
        StringBuilder builder = new StringBuilder(ITERATIONS * 6);
        for (int i = 0; i < ITERATIONS; i++) {
            builder.append(i);
        }
        String result = builder.toString();
        
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
    
    /**
     * 测试多线程环境下 StringBuilder vs StringBuffer
     * 验证线程安全性差异
     * 
     * @throws InterruptedException 线程中断异常
     */
    public void benchmarkThreadSafety() throws InterruptedException {
        final int THREAD_COUNT = 10;
        final int ITERATIONS_PER_THREAD = 10000;
        
        // 测试 StringBuilder（非线程安全）
        StringBuilder unsafeBuilder = new StringBuilder();
        Thread[] unsafeThreads = new Thread[THREAD_COUNT];
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            unsafeThreads[i] = new Thread(() -> {
                for (int j = 0; j < ITERATIONS_PER_THREAD; j++) {
                    unsafeBuilder.append("a");
                }
            });
            unsafeThreads[i].start();
        }
        
        for (Thread thread : unsafeThreads) {
            thread.join();
        }
        
        System.out.println("StringBuilder 最终结果长度: " + unsafeBuilder.length());
        System.out.println("期望长度: " + (THREAD_COUNT * ITERATIONS_PER_THREAD));
        System.out.println("是否线程安全: " + (unsafeBuilder.length() == THREAD_COUNT * ITERATIONS_PER_THREAD));
        
        // 测试 StringBuffer（线程安全）
        StringBuffer safeBuffer = new StringBuffer();
        Thread[] safeThreads = new Thread[THREAD_COUNT];
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            safeThreads[i] = new Thread(() -> {
                for (int j = 0; j < ITERATIONS_PER_THREAD; j++) {
                    safeBuffer.append("a");
                }
            });
            safeThreads[i].start();
        }
        
        for (Thread thread : safeThreads) {
            thread.join();
        }
        
        System.out.println("StringBuffer 最终结果长度: " + safeBuffer.length());
        System.out.println("期望长度: " + (THREAD_COUNT * ITERATIONS_PER_THREAD));
        System.out.println("是否线程安全: " + (safeBuffer.length() == THREAD_COUNT * ITERATIONS_PER_THREAD));
    }
}
