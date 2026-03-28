package com.linsir.abc.core.jvm.runtime.jit;

/**
 * JIT编译分析演示类
 * 
 * <p>演示如何分析JIT编译过程和结果，包括编译阈值、分层编译效果等。
 * 通过对比预热前后的性能差异，展示JIT编译带来的性能提升。</p>
 * 
 * <p>建议的JVM参数：</p>
 * <pre>
 * -XX:+PrintCompilation     # 打印编译信息
 * -XX:+PrintInlining        # 打印内联决策（需UnlockDiagnosticVMOptions）
 * -XX:+LogCompilation       # 记录详细编译日志
 * -XX:+UnlockDiagnosticVMOptions  # 解锁诊断参数
 * </pre>
 * 
 * <p>PrintCompilation输出格式说明：</p>
 * <pre>
 * timestamp compilation_id attributes tiered_level method_name
 * 
 * 示例：123 45 % 3 com.example.MyClass::myMethod @ 12 (70 bytes)
 * - 123: 虚拟机启动后的毫秒数
 * - 45: 编译任务ID
 * - %: 编译属性（%=OSR编译, s=同步方法, !=有异常处理器）
 * - 3: 分层编译层级（0-4）
 * - @ 12: 字节码位置
 * - (70 bytes): 字节码大小
 * </pre>
 * 
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see HotSpotDetector
 */
public class JITAnalysisDemo {
    
    /**
     * 用于累加结果的静态变量
     * 使用volatile防止编译器过度优化
     */
    private static volatile int sum = 0;
    
    /**
     * 热点计算方法 - 会被JIT编译优化
     * 
     * <p>该方法计算1到n的平方和，包含循环和数学运算。
     * 当调用次数达到编译阈值时，JIT编译器会将其编译为本地代码。</p>
     * 
     * <p>优化技术可能包括：</p>
     * <ul>
     *   <li>循环展开（Loop Unrolling）</li>
     *   <li>范围检查消除（Range Check Elimination）</li>
     *   <li>向量化（Vectorization，C2编译器）</li>
     * </ul>
     * 
     * @param n 计算上限
     * @return 平方和结果
     */
    public static int calculate(int n) {
        int result = 0;
        for (int i = 0; i < n; i++) {
            result += i * i;
        }
        return result;
    }
    
    /**
     * 斐波那契数列计算方法 - 递归算法
     * 
     * <p>用于演示JIT对递归方法的优化。
     * 注意：尾递归优化在Java中默认不开启。</p>
     * 
     * @param n 斐波那契数列索引
     * @return 斐波那契数
     */
    public static long fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
    
    /**
     * 迭代版斐波那契 - 更适合JIT优化
     * 
     * <p>相比递归版本，迭代版本更容易被JIT优化，
     * 因为没有方法调用开销和栈帧创建。</p>
     * 
     * @param n 斐波那契数列索引
     * @return 斐波那契数
     */
    public static long fibonacciIterative(int n) {
        if (n <= 1) {
            return n;
        }
        long a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            long temp = a + b;
            a = b;
            b = temp;
        }
        return b;
    }
    
    /**
     * 矩阵乘法方法 - 计算密集型操作
     * 
     * <p>演示JIT对复杂算法的优化效果。
     * 编译器可能进行循环优化、向量化等高级优化。</p>
     * 
     * @param size 矩阵大小
     * @return 矩阵元素和
     */
    public static long matrixMultiply(int size) {
        long result = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result += i * j * k;
                }
            }
        }
        return result;
    }
    
    /**
     * 预热方法 - 触发JIT编译
     * 
     * <p>通过多次调用目标方法，使其达到编译阈值，
     * 从而在正式测试前完成JIT编译。</p>
     * 
     * @param iterations 预热迭代次数
     */
    public static void warmUp(int iterations) {
        System.out.println("开始预热...");
        for (int i = 0; i < iterations; i++) {
            calculate(100);
            fibonacciIterative(30);
        }
        System.out.println("预热完成");
    }
    
    /**
     * 性能测试方法 - 测量calculate方法性能
     * 
     * @param iterations 测试迭代次数
     * @param n 计算参数
     * @return 总耗时（毫秒）
     */
    public static long benchmarkCalculate(int iterations, int n) {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            sum += calculate(n);
        }
        long end = System.nanoTime();
        return (end - start) / 1_000_000; // 转换为毫秒
    }
    
    /**
     * 性能测试方法 - 测量斐波那契方法性能
     * 
     * @param iterations 测试迭代次数
     * @param n 斐波那契索引
     * @return 总耗时（毫秒）
     */
    public static long benchmarkFibonacci(int iterations, int n) {
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            sum += fibonacciIterative(n);
        }
        long end = System.nanoTime();
        return (end - start) / 1_000_000; // 转换为毫秒
    }
    
    /**
     * 主方法 - 演示JIT编译分析
     * 
     * <p>执行流程：</p>
     * <ol>
     *   <li>执行预热，触发JIT编译</li>
     *   <li>进行性能测试（使用编译后的代码）</li>
     *   <li>输出性能对比结果</li>
     * </ol>
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== JIT编译分析演示 ===");
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println();
        
        // 预热阶段 - 触发JIT编译
        System.out.println("--- 预热阶段 ---");
        warmUp(100000);
        System.out.println();
        
        // 测试1：calculate方法性能
        System.out.println("--- 测试1: calculate方法性能 ---");
        System.out.println("执行 1,000,000 次 calculate(1000)");
        long time1 = benchmarkCalculate(1000000, 1000);
        System.out.println("总耗时: " + time1 + " ms");
        System.out.println("平均每次: " + (time1 * 1000.0 / 1000000) + " μs");
        System.out.println("结果校验: " + sum);
        System.out.println();
        
        // 测试2：斐波那契方法性能
        System.out.println("--- 测试2: 斐波那契方法性能 ---");
        System.out.println("执行 1,000,000 次 fibonacciIterative(30)");
        long time2 = benchmarkFibonacci(1000000, 30);
        System.out.println("总耗时: " + time2 + " ms");
        System.out.println("平均每次: " + (time2 * 1000.0 / 1000000) + " μs");
        System.out.println();
        
        // 测试3：矩阵乘法性能
        System.out.println("--- 测试3: 矩阵乘法性能 ---");
        System.out.println("执行 100 次 matrixMultiply(100)");
        long start3 = System.nanoTime();
        long matrixResult = 0;
        for (int i = 0; i < 100; i++) {
            matrixResult += matrixMultiply(100);
        }
        long end3 = System.nanoTime();
        long time3 = (end3 - start3) / 1_000_000;
        System.out.println("总耗时: " + time3 + " ms");
        System.out.println("平均每次: " + (time3 * 10.0) + " ms");
        System.out.println("结果: " + matrixResult);
        System.out.println();
        
        // 测试4：递归vs迭代性能对比
        System.out.println("--- 测试4: 递归 vs 迭代性能对比 ---");
        System.out.println("计算 fibonacci(40)");
        
        long startRec = System.nanoTime();
        long recResult = fibonacci(40);
        long endRec = System.nanoTime();
        long recTime = (endRec - startRec) / 1_000_000;
        
        long startIter = System.nanoTime();
        long iterResult = fibonacciIterative(40);
        long endIter = System.nanoTime();
        long iterTime = (endIter - startIter) / 1_000_000;
        
        System.out.println("递归版本: 结果=" + recResult + ", 耗时=" + recTime + " ms");
        System.out.println("迭代版本: 结果=" + iterResult + ", 耗时=" + iterTime + " ms");
        System.out.println("性能提升: " + (recTime / (double)iterTime) + "x");
        System.out.println();
        
        System.out.println("=== 演示完成 ===");
        System.out.println();
        System.out.println("JIT编译优化建议：");
        System.out.println("1. 避免在热点循环中创建对象");
        System.out.println("2. 使用方法内联友好的小方法");
        System.out.println("3. 优先使用迭代而非递归");
        System.out.println("4. 确保热点代码能被编译器优化");
        System.out.println();
        System.out.println("查看编译日志：");
        System.out.println("java -XX:+PrintCompilation JITAnalysisDemo");
    }
}
