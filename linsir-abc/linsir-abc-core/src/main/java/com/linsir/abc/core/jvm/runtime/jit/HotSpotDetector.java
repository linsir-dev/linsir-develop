package com.linsir.abc.core.jvm.runtime.jit;

/**
 * 热点代码探测演示类
 * 
 * <p>演示JVM中JIT编译器如何探测热点代码，包括方法调用计数器和回边计数器的工作原理。
 * 热点代码是指被频繁执行的方法或循环体，JIT编译器会将这些代码编译为本地机器码以提高执行效率。</p>
 * 
 * <p>相关JVM参数：</p>
 * <ul>
 *   <li>-XX:CompileThreshold=10000 - 方法调用计数器阈值（Server模式）</li>
 *   <li>-XX:OnStackReplacePercentage=140 - OSR编译触发百分比</li>
 *   <li>-XX:+PrintCompilation - 打印编译信息</li>
 * </ul>
 * 
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see JITAnalysisDemo
 */
public class HotSpotDetector {
    
    /**
     * 用于累加的静态变量，防止编译器过度优化
     */
    private static int value = 0;
    
    /**
     * 热点方法 - 会被JIT编译器识别并编译
     * 
     * <p>该方法包含大量计算操作，当被频繁调用时，
     * 方法调用计数器会递增，达到阈值后触发C1/C2编译。</p>
     * 
     * <p>分层编译层级：</p>
     * <ul>
     *   <li>第1-3层：C1编译器编译，带性能监控</li>
     *   <li>第4层：C2编译器编译，激进优化</li>
     * </ul>
     */
    public static void hotMethod() {
        for (int i = 0; i < 100000; i++) {
            value += i;
        }
    }
    
    /**
     * 普通方法 - 调用次数较少，可能不会被JIT编译
     * 
     * <p>该方法仅执行简单的输出操作，
     * 由于调用频率低，方法调用计数器不会达到编译阈值。</p>
     */
    public static void normalMethod() {
        System.out.println("Normal method executed");
    }
    
    /**
     * 触发OSR（栈上替换）编译的方法
     * 
     * <p>OSR编译针对循环体热点代码，即使方法整体调用次数不多，
     * 只要循环体执行次数足够多，也会触发编译。</p>
     * 
     * <p>回边计数器统计循环体执行次数，达到阈值后触发OSR编译。</p>
     * 
     * @param iterations 循环迭代次数
     */
    public static void triggerOSR(int iterations) {
        long sum = 0;
        // 这个循环体会被OSR编译
        for (int i = 0; i < iterations; i++) {
            sum += i * i;
        }
        System.out.println("OSR result: " + sum);
    }
    
    /**
     * 计算密集型方法 - 适合JIT编译优化
     * 
     * <p>包含多层循环和数学运算，编译后性能提升明显。</p>
     * 
     * @param n 计算参数
     * @return 计算结果
     */
    public static long computeIntensive(int n) {
        long result = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result += i * j;
            }
        }
        return result;
    }
    
    /**
     * 主方法 - 演示热点代码探测过程
     * 
     * <p>执行流程：</p>
     * <ol>
     *   <li>调用1000次hotMethod - 可能触发C1编译（第1-3层）</li>
     *   <li>继续调用10000次hotMethod - 可能触发C2编译（第4层）</li>
     *   <li>调用normalMethod - 不会被编译</li>
     *   <li>执行大循环 - 触发OSR编译</li>
     * </ol>
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== 热点代码探测演示 ===");
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println();
        
        // 阶段1：调用1000次 - 可能触发C1编译
        System.out.println("阶段1: 调用hotMethod 1000次（可能触发C1编译）");
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            hotMethod();
        }
        long end1 = System.currentTimeMillis();
        System.out.println("耗时: " + (end1 - start1) + " ms");
        System.out.println();
        
        // 阶段2：继续调用10000次 - 可能触发C2编译
        System.out.println("阶段2: 调用hotMethod 10000次（可能触发C2编译）");
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            hotMethod();
        }
        long end2 = System.currentTimeMillis();
        System.out.println("耗时: " + (end2 - start2) + " ms");
        System.out.println("注意：编译后的代码执行更快");
        System.out.println();
        
        // 阶段3：调用普通方法
        System.out.println("阶段3: 调用normalMethod");
        normalMethod();
        System.out.println("普通方法调用次数少，不会被JIT编译");
        System.out.println();
        
        // 阶段4：触发OSR编译
        System.out.println("阶段4: 触发OSR编译");
        long start3 = System.currentTimeMillis();
        triggerOSR(10000000);
        long end3 = System.currentTimeMillis();
        System.out.println("OSR执行耗时: " + (end3 - start3) + " ms");
        System.out.println();
        
        // 阶段5：计算密集型任务
        System.out.println("阶段5: 计算密集型任务");
        long start4 = System.currentTimeMillis();
        long result = computeIntensive(1000);
        long end4 = System.currentTimeMillis();
        System.out.println("计算结果: " + result);
        System.out.println("计算耗时: " + (end4 - start4) + " ms");
        System.out.println();
        
        System.out.println("=== 演示完成 ===");
        System.out.println("提示：使用 -XX:+PrintCompilation 参数查看编译日志");
    }
}
