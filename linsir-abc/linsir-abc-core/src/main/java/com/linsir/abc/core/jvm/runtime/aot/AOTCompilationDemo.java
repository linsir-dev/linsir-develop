package com.linsir.abc.core.jvm.runtime.aot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 提前编译（AOT）演示类
 *
 * <p>演示JDK 9+引入的Jaotc提前编译工具的使用方法和效果。
 * AOT编译将字节码在运行前编译为本地机器码，可以显著提升启动速度。</p>
 *
 * <p>AOT编译的优势：</p>
 * <ul>
 *   <li>启动速度快 - 无需等待JIT编译预热</li>
 *   <li>内存占用低 - 不需要JIT编译器和编译缓存</li>
 *   <li>性能可预测 - 没有编译阶段的性能波动</li>
 * </ul>
 *
 * <p>AOT编译的劣势：</p>
 * <ul>
 *   <li>失去跨平台性 - 编译结果与平台绑定</li>
 *   <li>无法动态优化 - 不能根据运行时数据优化</li>
 *   <li>动态特性受限 - 反射、动态代理需要额外配置</li>
 * </ul>
 *
 * <p>Jaotc基本用法：</p>
 * <pre>
 * # 编译单个类
 * jaotc --output libHello.so Hello.class
 *
 * # 编译整个模块
 * jaotc --output libjava.base.so --module java.base
 *
 * # 使用AOT库
 * java -XX:AOTLibrary=./libHello.so Hello
 * </pre>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-29
 * @see AOTPerformanceTest
 */
public class AOTCompilationDemo {

    /**
     * 计算密集型方法 - 适合AOT编译
     *
     * @param n 计算参数
     * @return 计算结果
     */
    public static long calculate(int n) {
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += i * i;
        }
        return sum;
    }

    /**
     * 斐波那契数列计算
     *
     * @param n 斐波那契索引
     * @return 斐波那契数
     */
    public static long fibonacci(int n) {
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
     * 字符串处理任务
     *
     * @param count 字符串数量
     * @return 拼接后的字符串
     */
    public static String processStrings(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("Item").append(i).append(" ");
        }
        return sb.toString();
    }

    /**
     * 集合操作任务
     *
     * @param size 集合大小
     * @return 处理结果
     */
    public static List<Integer> processCollection(int size) {
        List<Integer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(i * i);
        }
        return list;
    }

    /**
     * 生成Jaotc编译命令文件
     *
     * <p>命令文件用于控制哪些类需要编译，哪些需要排除。</p>
     *
     * @param filename 命令文件名
     * @throws IOException 当文件写入失败时
     */
    public static void generateCompileCommands(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);

        writer.write("# Jaotc编译命令文件\n");
        writer.write("# 格式: compileOnly <class-pattern> 或 exclude <class-pattern>\n\n");

        writer.write("# 编译指定类\n");
        writer.write("compileOnly com.linsir.abc.core.jvm.runtime.aot.AOTCompilationDemo\n");
        writer.write("compileOnly com.linsir.abc.core.jvm.runtime.aot.AOTPerformanceTest\n\n");

        writer.write("# 编译常用工具类\n");
        writer.write("compileOnly java.lang.String*\n");
        writer.write("compileOnly java.lang.Math*\n");
        writer.write("compileOnly java.util.ArrayList*\n");
        writer.write("compileOnly java.util.HashMap*\n\n");

        writer.write("# 排除不需要编译的方法\n");
        writer.write("exclude java.lang.Object.finalize\n");
        writer.write("exclude java.lang.Thread.run\n");

        writer.close();
        System.out.println("编译命令文件已生成: " + filename);
    }

    /**
     * 生成Jaotc编译脚本
     *
     * @param scriptName 脚本文件名
     * @param libName    输出库名
     * @throws IOException 当文件写入失败时
     */
    public static void generateCompileScript(String scriptName, String libName) throws IOException {
        FileWriter writer = new FileWriter(scriptName);

        // Windows批处理脚本
        if (scriptName.endsWith(".bat")) {
            writer.write("@echo off\n");
            writer.write("echo === Jaotc编译脚本 ===\n\n");

            writer.write("echo 编译类文件...\n");
            writer.write("jaotc --output " + libName + " \\\n");
            writer.write("  --class-name com.linsir.abc.core.jvm.runtime.aot.AOTCompilationDemo \\\n");
            writer.write("  --class-name com.linsir.abc.core.jvm.runtime.aot.AOTPerformanceTest \\\n");
            writer.write("  --search-path target/classes\n\n");

            writer.write("echo 编译完成: " + libName + "\n");
            writer.write("echo 使用方法: java -XX:AOTLibrary=./" + libName + " AOTCompilationDemo\n");
        }
        // Linux/Mac shell脚本
        else {
            writer.write("#!/bin/bash\n");
            writer.write("echo '=== Jaotc编译脚本 ==='\n\n");

            writer.write("echo '编译类文件...'\n");
            writer.write("jaotc --output " + libName + " \\\n");
            writer.write("  --class-name com.linsir.abc.core.jvm.runtime.aot.AOTCompilationDemo \\\n");
            writer.write("  --class-name com.linsir.abc.core.jvm.runtime.aot.AOTPerformanceTest \\\n");
            writer.write("  --search-path target/classes\n\n");

            writer.write("echo '编译完成: " + libName + "'\n");
            writer.write("echo '使用方法: java -XX:AOTLibrary=./" + libName + " AOTCompilationDemo'\n");
        }

        writer.close();
        System.out.println("编译脚本已生成: " + scriptName);
    }

    /**
     * 模拟AOT编译后的执行流程
     *
     * <p>展示AOT和JIT协作的执行过程。</p>
     */
    public static void simulateAOTExecution() {
        System.out.println("\n=== AOT执行流程模拟 ===\n");

        System.out.println("执行阶段:");
        System.out.println("1. 程序启动");
        System.out.println("   - 加载AOT编译的共享库 (.so/.dll)");
        System.out.println("   - 初始化JVM");
        System.out.println();

        System.out.println("2. 方法调用");
        System.out.println("   - 检查是否有AOT编译的代码");
        System.out.println("   - 如果有，直接执行本地代码");
        System.out.println("   - 如果没有，使用解释器或JIT编译");
        System.out.println();

        System.out.println("3. 运行时优化");
        System.out.println("   - JIT编译器继续收集性能数据");
        System.out.println("   - 热点代码可能被重新编译优化");
        System.out.println("   - 最终达到接近纯JIT的性能");
        System.out.println();

        System.out.println("AOT vs JIT对比:");
        System.out.println("┌─────────────┬──────────┬──────────┬──────────┐");
        System.out.println("│   特性      │   AOT    │   JIT    │ AOT+JIT  │");
        System.out.println("├─────────────┼──────────┼──────────┼──────────┤");
        System.out.println("│ 启动时间    │   极快   │   慢     │   快     │");
        System.out.println("│ 峰值性能    │   中等   │   最高   │   高     │");
        System.out.println("│ 内存占用    │   低     │   高     │   中等   │");
        System.out.println("│ 跨平台      │   否     │   是     │   部分   │");
        System.out.println("│ 动态优化    │   否     │   是     │   是     │");
        System.out.println("└─────────────┴──────────┴──────────┴──────────┘");
    }

    /**
     * 打印AOT编译使用指南
     */
    public static void printAOTGuide() {
        System.out.println("\n=== AOT编译使用指南 ===\n");

        System.out.println("1. 基本编译命令:");
        System.out.println("   jaotc --output libMyApp.so MyApp.class");
        System.out.println();

        System.out.println("2. 使用AOT库运行:");
        System.out.println("   java -XX:AOTLibrary=./libMyApp.so MyApp");
        System.out.println();

        System.out.println("3. 多个AOT库:");
        System.out.println("   java -XX:AOTLibrary=./lib1.so:./lib2.so MyApp");
        System.out.println();

        System.out.println("4. 查看AOT加载信息:");
        System.out.println("   java -XX:+PrintAOT -XX:AOTLibrary=./libMyApp.so MyApp");
        System.out.println();

        System.out.println("5. Jaotc常用参数:");
        System.out.println("   --output <file>       指定输出文件名");
        System.out.println("   --module <name>       编译整个模块");
        System.out.println("   --class-name <name>   指定要编译的类");
        System.out.println("   --search-path <path>  类搜索路径");
        System.out.println("   --compile-commands    编译命令文件");
        System.out.println("   --info                显示编译信息");
        System.out.println("   --verbose             详细输出");
        System.out.println();

        System.out.println("6. 适用场景:");
        System.out.println("   - Serverless函数");
        System.out.println("   - 微服务启动优化");
        System.out.println("   - 容器化部署");
        System.out.println("   - 内存受限环境");
        System.out.println();

        System.out.println("7. 限制条件:");
        System.out.println("   - 仅支持64位Linux和Windows");
        System.out.println("   - 反射需要配置");
        System.out.println("   - 动态代理需要配置");
        System.out.println("   - JNI需要配置");
        System.out.println();
    }

    /**
     * 主方法 - 演示AOT编译
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("=== 提前编译（AOT）演示 ===");
        System.out.println("JVM: " + System.getProperty("java.vm.name"));
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println();

        // 检查JDK版本
        int version = Runtime.version().feature();
        if (version < 9) {
            System.out.println("警告: AOT编译需要JDK 9或更高版本");
            System.out.println("当前版本: " + version);
            return;
        }
        System.out.println("JDK版本检查通过: " + version);
        System.out.println();

        // 模拟AOT执行流程
        simulateAOTExecution();

        // 打印使用指南
        printAOTGuide();

        // 生成编译脚本
        System.out.println("=== 生成编译脚本 ===\n");
        try {
            generateCompileCommands("jaotc-commands.txt");
            generateCompileScript("compile-aot.bat", "libAOTDemo.dll");
            generateCompileScript("compile-aot.sh", "libAOTDemo.so");
        } catch (IOException e) {
            System.err.println("生成脚本失败: " + e.getMessage());
        }
        System.out.println();

        // 执行性能测试
        System.out.println("=== AOT性能特征测试 ===\n");
        System.out.println("注意: 以下测试在普通JVM模式下运行");
        System.out.println("实际AOT性能需要使用jaotc编译后测试\n");

        // 测试1：计算任务
        System.out.println("测试1: 计算密集型任务");
        long start1 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            calculate(1000);
        }
        long time1 = (System.nanoTime() - start1) / 1_000_000;
        System.out.println("执行 100000 次 calculate(1000)");
        System.out.println("耗时: " + time1 + " ms");
        System.out.println();

        // 测试2：斐波那契
        System.out.println("测试2: 斐波那契计算");
        long start2 = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            fibonacci(50);
        }
        long time2 = (System.nanoTime() - start2) / 1_000_000;
        System.out.println("执行 100000 次 fibonacci(50)");
        System.out.println("耗时: " + time2 + " ms");
        System.out.println();

        // 测试3：字符串处理
        System.out.println("测试3: 字符串处理");
        long start3 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            processStrings(1000);
        }
        long time3 = (System.nanoTime() - start3) / 1_000_000;
        System.out.println("执行 10000 次 processStrings(1000)");
        System.out.println("耗时: " + time3 + " ms");
        System.out.println();

        // 测试4：集合操作
        System.out.println("测试4: 集合操作");
        long start4 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            processCollection(1000);
        }
        long time4 = (System.nanoTime() - start4) / 1_000_000;
        System.out.println("执行 10000 次 processCollection(1000)");
        System.out.println("耗时: " + time4 + " ms");
        System.out.println();

        System.out.println("=== 演示完成 ===");
        System.out.println();
        System.out.println("下一步:");
        System.out.println("1. 使用 jaotc 编译本类");
        System.out.println("2. 使用 -XX:AOTLibrary 参数运行");
        System.out.println("3. 对比启动时间和执行性能");
    }
}
