package com.linsir.abc.core.jvm.runtime;

import java.util.List;

import com.linsir.abc.core.jvm.runtime.aot.AOTCompilationDemo;
import com.linsir.abc.core.jvm.runtime.aot.AOTPerformanceTest;
import com.linsir.abc.core.jvm.runtime.graal.GraalCompilerDemo;
import com.linsir.abc.core.jvm.runtime.graal.GraalPerformanceTest;
import com.linsir.abc.core.jvm.runtime.jit.HotSpotDetector;
import com.linsir.abc.core.jvm.runtime.jit.JITAnalysisDemo;
import com.linsir.abc.core.jvm.runtime.jit.TieredCompilationDemo;
import com.linsir.abc.core.jvm.runtime.optimization.EscapeAnalysisDemo;
import com.linsir.abc.core.jvm.runtime.optimization.LoopOptimizationDemo;
import com.linsir.abc.core.jvm.runtime.optimization.MethodInliningDemo;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 第11章后端编译与优化测试类
 *
 * <p>测试JIT编译、AOT编译、编译器优化技术和Graal编译器相关功能。</p>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RuntimeCompilationTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        outputStream.reset();
    }

    // ==================== JIT编译测试 ====================

    @Test
    @Order(1)
    @DisplayName("测试热点代码探测")
    void testHotSpotDetection() {
        // 测试热点方法
        HotSpotDetector.hotMethod();
        HotSpotDetector.normalMethod();

        // 测试OSR触发
        HotSpotDetector.triggerOSR(1000);

        // 测试计算密集型方法
        long result = HotSpotDetector.computeIntensive(100);
        assertTrue(result > 0);
    }

    @Test
    @Order(2)
    @DisplayName("测试JIT分析方法")
    void testJITAnalysis() {
        // 测试calculate方法
        int result1 = JITAnalysisDemo.calculate(100);
        assertEquals(328350, result1); // sum of squares from 0 to 99

        // 测试斐波那契
        long fib1 = JITAnalysisDemo.fibonacci(10);
        assertEquals(55, fib1);

        long fib2 = JITAnalysisDemo.fibonacciIterative(10);
        assertEquals(55, fib2);

        // 测试矩阵乘法
        long matrixResult = JITAnalysisDemo.matrixMultiply(10);
        assertTrue(matrixResult >= 0);
    }

    @Test
    @Order(3)
    @DisplayName("测试分层编译模拟")
    void testTieredCompilation() {
        // 测试计算任务
        long result1 = TieredCompilationDemo.computeTask(100);
        assertTrue(result1 >= 0);

        // 测试字符串任务
        int result2 = TieredCompilationDemo.stringTask(100);
        assertTrue(result2 > 0);

        // 测试集合任务
        int result3 = TieredCompilationDemo.collectionTask(100);
        assertTrue(result3 >= 0);

        // 测试编译层级枚举
        assertEquals(0, TieredCompilationDemo.CompilationLevel.INTERPRETED.getLevel());
        assertEquals(4, TieredCompilationDemo.CompilationLevel.C2_OPTIMIZED.getLevel());
    }

    // ==================== AOT编译测试 ====================

    @Test
    @Order(4)
    @DisplayName("测试AOT编译方法")
    void testAOTCompilation() {
        // 测试计算方法
        long result1 = AOTCompilationDemo.calculate(100);
        assertEquals(328350, result1);

        // 测试斐波那契
        long fib = AOTCompilationDemo.fibonacci(10);
        assertEquals(55, fib);

        // 测试字符串处理
        String str = AOTCompilationDemo.processStrings(100);
        assertNotNull(str);
        assertTrue(str.length() > 0);

        // 测试集合操作
        List<Integer> list = AOTCompilationDemo.processCollection(100);
        assertNotNull(list);
        assertEquals(100, list.size());
    }

    @Test
    @Order(5)
    @DisplayName("测试AOT性能测试类")
    void testAOTPerformance() {
        // 测试矩阵乘法
        long result1 = AOTPerformanceTest.matrixMultiplication(10);
        assertTrue(result1 >= 0);

        // 测试素数计算
        int primes = AOTPerformanceTest.countPrimes(100);
        assertTrue(primes > 0);

        // 测试字符串处理
        String str = AOTPerformanceTest.stringProcessing(100);
        assertNotNull(str);

        // 测试集合操作
        long result2 = AOTPerformanceTest.collectionOperations(100);
        assertTrue(result2 >= 0);

        // 测试排序
        long result3 = AOTPerformanceTest.sortingTask(100);
        assertTrue(result3 >= 0);

        // 测试斐波那契
        long fib = AOTPerformanceTest.recursiveFibonacci(10);
        assertEquals(55, fib);
    }

    // ==================== 编译器优化技术测试 ====================

    @Test
    @Order(6)
    @DisplayName("测试方法内联")
    void testMethodInlining() {
        // 测试内联方法
        int result1 = MethodInliningDemo.calculateWithCalls(3, 4);
        assertEquals(49, result1); // (3+4)^2 = 49

        int result2 = MethodInliningDemo.calculateInlined(3, 4);
        assertEquals(49, result2);

        // 测试内联性能
        long result3 = MethodInliningDemo.testInliningPerformance(1000);
        assertTrue(result3 >= 0);

        // 测试内联深度
        int result4 = MethodInliningDemo.testInliningDepth(10);
        assertEquals(14, result4); // 10 + 1 + 1 + 1 + 1
    }

    @Test
    @Order(7)
    @DisplayName("测试逃逸分析")
    void testEscapeAnalysis() {
        // 测试无逃逸
        int result1 = EscapeAnalysisDemo.noEscape(3, 4);
        assertEquals(25, result1); // 3^2 + 4^2 = 25

        // 测试方法逃逸
        EscapeAnalysisDemo.Point point = EscapeAnalysisDemo.methodEscape(3, 4);
        assertNotNull(point);
        assertEquals(3, point.x);
        assertEquals(4, point.y);

        // 测试同步消除
        String str = EscapeAnalysisDemo.lockElimination(10);
        assertNotNull(str);
        assertTrue(str.contains("Item"));

        // 测试标量替换
        long result2 = EscapeAnalysisDemo.testScalarReplacement(1000);
        assertTrue(result2 >= 0);

        // 测试栈上分配
        long result3 = EscapeAnalysisDemo.testStackAllocation(1000);
        assertTrue(result3 >= 0);

        // 测试同步消除性能
        long result4 = EscapeAnalysisDemo.testLockElimination(1000);
        assertTrue(result4 >= 0);
    }

    @Test
    @Order(8)
    @DisplayName("测试循环优化")
    void testLoopOptimization() {
        int[] arr = {1, 2, 3, 4, 5};

        // 测试基础循环
        int result1 = LoopOptimizationDemo.basicLoop(arr);
        assertEquals(15, result1);

        // 测试展开循环
        int result2 = LoopOptimizationDemo.unrolledLoop(arr);
        assertEquals(15, result2);

        // 测试循环不变量
        int result3 = LoopOptimizationDemo.loopInvariantBefore(arr);
        assertTrue(result3 >= 0);

        int result4 = LoopOptimizationDemo.loopInvariantAfter(arr);
        assertTrue(result4 >= 0);

        // 测试边界检查消除
        int result5 = LoopOptimizationDemo.boundsCheckElimination(arr);
        assertEquals(15, result5);

        // 测试向量化
        int[] a = {1, 2, 3};
        int[] b = {4, 5, 6};
        int[] c = new int[3];
        LoopOptimizationDemo.vectorizedLoop(a, b, c);
        assertArrayEquals(new int[]{5, 7, 9}, c);
    }

    // ==================== Graal编译器测试 ====================

    @Test
    @Order(9)
    @DisplayName("测试Graal编译器检测")
    void testGraalCompiler() {
        // 测试编译器检测方法
        boolean isGraal = GraalCompilerDemo.isGraalCompiler();
        // 不断言具体值，因为取决于运行环境

        boolean isJVMCI = GraalCompilerDemo.isJVMCIEnabled();
        // 不断言具体值

        // 测试计算方法
        long result1 = GraalCompilerDemo.computeIntensive(100);
        assertTrue(result1 >= 0);

        // 测试斐波那契
        long fib1 = GraalCompilerDemo.fibonacci(10);
        assertEquals(55, fib1);

        long fib2 = GraalCompilerDemo.fibonacciIterative(10);
        assertEquals(55, fib2);

        // 测试部分逃逸分析
        int result2 = GraalCompilerDemo.partialEscapeAnalysis(true);
        assertEquals(30, result2);

        int result3 = GraalCompilerDemo.partialEscapeAnalysis(false);
        assertEquals(30, result3);
    }

    @Test
    @Order(10)
    @DisplayName("测试Graal性能测试类")
    void testGraalPerformance() {
        // 测试排序
        int[] arr = {5, 3, 1, 4, 2};
        GraalPerformanceTest.quickSort(arr, 0, arr.length - 1);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);

        // 测试素数筛法
        int primes = GraalPerformanceTest.sieveOfEratosthenes(100);
        assertTrue(primes > 0);

        // 测试矩阵乘法
        double[][] a = {{1, 2}, {3, 4}};
        double[][] b = {{5, 6}, {7, 8}};
        double[][] c = new double[2][2];
        GraalPerformanceTest.matrixMultiply(a, b, c, 2);
        assertEquals(19.0, c[0][0], 0.001);
        assertEquals(22.0, c[0][1], 0.001);

        // 测试PI计算
        double pi = GraalPerformanceTest.calculatePi(10000);
        assertTrue(pi > 3.0 && pi < 3.5);

        // 测试斐波那契
        long fib = GraalPerformanceTest.fibonacci(10);
        assertEquals(55, fib);

        // 测试字符串处理
        int len = GraalPerformanceTest.stringProcessing(100);
        assertTrue(len > 0);

        // 测试对象创建
        long sum = GraalPerformanceTest.objectCreationTest(100);
        assertTrue(sum >= 0);
    }

    // ==================== 综合测试 ====================

    @Test
    @Order(11)
    @DisplayName("测试所有编译优化技术")
    void testAllOptimizations() {
        // 执行多次以触发JIT编译
        for (int i = 0; i < 100; i++) {
            // JIT测试
            HotSpotDetector.hotMethod();
            JITAnalysisDemo.calculate(50);
            TieredCompilationDemo.computeTask(50);

            // AOT测试
            AOTCompilationDemo.calculate(50);

            // 优化技术测试
            MethodInliningDemo.calculateWithCalls(1, 2);
            EscapeAnalysisDemo.noEscape(1, 2);
            LoopOptimizationDemo.basicLoop(new int[]{1, 2, 3});

            // Graal测试
            GraalCompilerDemo.computeIntensive(50);
        }

        // 所有测试都应该正常完成
        assertTrue(true);
    }

    @Test
    @Order(12)
    @DisplayName("测试性能对比")
    void testPerformanceComparison() {
        int iterations = 10000;

        // 测试JIT性能
        long start1 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            JITAnalysisDemo.calculate(100);
        }
        long jitTime = System.nanoTime() - start1;

        // 测试AOT风格代码性能
        long start2 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            AOTCompilationDemo.calculate(100);
        }
        long aotTime = System.nanoTime() - start2;

        // 测试Graal风格代码性能
        long start3 = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            GraalCompilerDemo.computeIntensive(100);
        }
        long graalTime = System.nanoTime() - start3;

        // 输出性能对比（不断言，仅记录）
        System.setOut(originalOut);
        System.out.println("性能对比（" + iterations + "次迭代）:");
        System.out.println("  JIT风格: " + (jitTime / 1_000_000) + " ms");
        System.out.println("  AOT风格: " + (aotTime / 1_000_000) + " ms");
        System.out.println("  Graal风格: " + (graalTime / 1_000_000) + " ms");

        // 所有测试都应该在合理时间内完成
        assertTrue(jitTime > 0);
        assertTrue(aotTime > 0);
        assertTrue(graalTime > 0);
    }
}
