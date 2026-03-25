package com.linsir.abc.core.base.lang.system;

import java.util.Arrays;

/**
 * 数组拷贝性能测试类
 * 
 * 本类用于比较不同数组拷贝方式的性能：
 * 1. System.arraycopy() - native方法，性能最优
 * 2. Arrays.copyOf() - 内部调用System.arraycopy
 * 3. 手动循环拷贝 - 性能最差
 * 4. clone()方法 - 适用于一维数组
 * 
 * 性能排序（从快到慢）：
 * System.arraycopy ≈ Arrays.copyOf > clone() > 手动循环
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ArrayCopyPerformance {
    
    /**
     * 测试数组大小
     */
    private static final int ARRAY_SIZE = 10000000;
    
    /**
     * 测试次数
     */
    private static final int ITERATIONS = 10;
    
    /**
     * 执行性能测试
     * 
     * @return 测试结果报告
     */
    public String runBenchmark() {
        StringBuilder report = new StringBuilder();
        report.append("=== 数组拷贝性能测试 ===\n");
        report.append("数组大小: ").append(ARRAY_SIZE).append("\n");
        report.append("测试次数: ").append(ITERATIONS).append("\n\n");
        
        // 准备测试数据
        int[] sourceArray = new int[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            sourceArray[i] = i;
        }
        
        // 测试1：System.arraycopy
        long systemArrayCopyTime = benchmarkSystemArrayCopy(sourceArray);
        report.append("1. System.arraycopy(): ").append(systemArrayCopyTime).append(" ms\n");
        
        // 测试2：Arrays.copyOf
        long arraysCopyOfTime = benchmarkArraysCopyOf(sourceArray);
        report.append("2. Arrays.copyOf(): ").append(arraysCopyOfTime).append(" ms\n");
        
        // 测试3：clone方法
        long cloneTime = benchmarkClone(sourceArray);
        report.append("3. clone(): ").append(cloneTime).append(" ms\n");
        
        // 测试4：手动循环
        long manualLoopTime = benchmarkManualLoop(sourceArray);
        report.append("4. 手动循环: ").append(manualLoopTime).append(" ms\n");
        
        // 性能对比
        report.append("\n=== 性能对比 ===\n");
        report.append("System.arraycopy 比手动循环快 ")
              .append(String.format("%.2f", (double)manualLoopTime / systemArrayCopyTime))
              .append(" 倍\n");
        
        return report.toString();
    }
    
    /**
     * 测试System.arraycopy的性能
     * 
     * @param sourceArray 源数组
     * @return 平均执行时间（毫秒）
     */
    public long benchmarkSystemArrayCopy(int[] sourceArray) {
        long totalTime = 0;
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[] destArray = new int[sourceArray.length];
            
            long startTime = System.currentTimeMillis();
            System.arraycopy(sourceArray, 0, destArray, 0, sourceArray.length);
            long endTime = System.currentTimeMillis();
            
            totalTime += (endTime - startTime);
        }
        
        return totalTime / ITERATIONS;
    }
    
    /**
     * 测试Arrays.copyOf的性能
     * 
     * @param sourceArray 源数组
     * @return 平均执行时间（毫秒）
     */
    public long benchmarkArraysCopyOf(int[] sourceArray) {
        long totalTime = 0;
        
        for (int i = 0; i < ITERATIONS; i++) {
            long startTime = System.currentTimeMillis();
            int[] destArray = Arrays.copyOf(sourceArray, sourceArray.length);
            long endTime = System.currentTimeMillis();
            
            totalTime += (endTime - startTime);
        }
        
        return totalTime / ITERATIONS;
    }
    
    /**
     * 测试clone方法的性能
     * 
     * @param sourceArray 源数组
     * @return 平均执行时间（毫秒）
     */
    public long benchmarkClone(int[] sourceArray) {
        long totalTime = 0;
        
        for (int i = 0; i < ITERATIONS; i++) {
            long startTime = System.currentTimeMillis();
            int[] destArray = sourceArray.clone();
            long endTime = System.currentTimeMillis();
            
            totalTime += (endTime - startTime);
        }
        
        return totalTime / ITERATIONS;
    }
    
    /**
     * 测试手动循环的性能
     * 
     * @param sourceArray 源数组
     * @return 平均执行时间（毫秒）
     */
    public long benchmarkManualLoop(int[] sourceArray) {
        long totalTime = 0;
        
        for (int i = 0; i < ITERATIONS; i++) {
            int[] destArray = new int[sourceArray.length];
            
            long startTime = System.currentTimeMillis();
            for (int j = 0; j < sourceArray.length; j++) {
                destArray[j] = sourceArray[j];
            }
            long endTime = System.currentTimeMillis();
            
            totalTime += (endTime - startTime);
        }
        
        return totalTime / ITERATIONS;
    }
    
    /**
     * 演示System.arraycopy的高级用法
     * 支持重叠拷贝（源数组和目标数组可以是同一个）
     */
    public void demonstrateOverlapCopy() {
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        
        System.out.println("原始数组: " + Arrays.toString(array));
        
        // 将数组元素向后移动2位
        // 从位置0开始，拷贝到位置2，拷贝8个元素
        System.arraycopy(array, 0, array, 2, 8);
        
        System.out.println("重叠拷贝后: " + Arrays.toString(array));
        // 结果: [1, 2, 1, 2, 3, 4, 5, 6, 7, 8]
    }
    
    /**
     * 演示二维数组的拷贝
     * 注意：二维数组是数组的数组，需要逐层拷贝
     */
    public void demonstrate2DArrayCopy() {
        int[][] source2D = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        
        // 浅拷贝：只拷贝外层数组
        int[][] shallowCopy = source2D.clone();
        
        // 深拷贝：逐层拷贝
        int[][] deepCopy = new int[source2D.length][];
        for (int i = 0; i < source2D.length; i++) {
            deepCopy[i] = source2D[i].clone();
        }
        
        // 修改原数组
        source2D[0][0] = 100;
        
        System.out.println("原数组[0][0]: " + source2D[0][0]);
        System.out.println("浅拷贝[0][0]: " + shallowCopy[0][0]); // 也会变成100
        System.out.println("深拷贝[0][0]: " + deepCopy[0][0]);   // 仍然是1
    }
}
