package com.linsir.abc.core.jvm.memory.oom;

import com.linsir.abc.core.jvm.memory.direct.DirectMemoryOutOfMemory;
import com.linsir.abc.core.jvm.memory.heap.HeapOutOfMemory;
import com.linsir.abc.core.jvm.memory.methodarea.MethodAreaOutOfMemory;
import com.linsir.abc.core.jvm.memory.methodarea.RuntimeConstantPoolOutOfMemory;
import com.linsir.abc.core.jvm.memory.stack.StackOutOfMemory;
import com.linsir.abc.core.jvm.memory.stack.StackOverflowError;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * OutOfMemoryError异常测试运行器
 *
 * <p>提供统一的入口来运行各种OOM异常演示程序。</p>
 *
 * <p><strong>支持的测试类型:</strong></p>
 * <ul>
 *   <li>heap - Java堆溢出</li>
 *   <li>stack - 虚拟机栈溢出(StackOverflowError)</li>
 *   <li>stackoom - 虚拟机栈内存溢出(OutOfMemoryError)</li>
 *   <li>methodarea - 方法区溢出</li>
 *   <li>constantpool - 运行时常量池溢出</li>
 *   <li>direct - 直接内存溢出</li>
 *   <li>all - 运行所有测试</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class OutOfMemoryTestRunner {

    /**
     * 测试类型与描述的映射
     */
    private static final Map<String, String> TEST_DESCRIPTIONS = new HashMap<>();

    static {
        TEST_DESCRIPTIONS.put("heap", "Java堆溢出 (Heap OOM)");
        TEST_DESCRIPTIONS.put("stack", "虚拟机栈溢出 (StackOverflowError)");
        TEST_DESCRIPTIONS.put("stackoom", "虚拟机栈内存溢出 (Stack OOM)");
        TEST_DESCRIPTIONS.put("methodarea", "方法区溢出 (Method Area OOM)");
        TEST_DESCRIPTIONS.put("constantpool", "运行时常量池溢出 (Constant Pool OOM)");
        TEST_DESCRIPTIONS.put("direct", "直接内存溢出 (Direct Memory OOM)");
    }

    /**
     * 程序入口
     *
     * @param args 命令行参数，args[0]为测试类型
     */
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("    JVM OutOfMemoryError 异常测试运行器");
        System.out.println("==============================================");
        System.out.println();

        if (args.length == 0) {
            printUsage();
            interactiveMode();
            return;
        }

        String testType = args[0].toLowerCase();
        runTest(testType);
    }

    /**
     * 打印使用说明
     */
    private static void printUsage() {
        System.out.println("用法: java OutOfMemoryTestRunner <测试类型>");
        System.out.println();
        System.out.println("支持的测试类型:");
        TEST_DESCRIPTIONS.forEach((key, value) -> 
            System.out.println("  " + key + " - " + value));
        System.out.println("  all - 运行所有测试");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  java OutOfMemoryTestRunner heap");
        System.out.println("  java OutOfMemoryTestRunner stack");
        System.out.println("  java OutOfMemoryTestRunner all");
        System.out.println();
    }

    /**
     * 交互模式
     */
    private static void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("请输入测试类型 (或输入 'exit' 退出): ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            if ("exit".equals(input) || "quit".equals(input)) {
                System.out.println("退出测试运行器。");
                break;
            }
            
            if ("help".equals(input) || "?".equals(input)) {
                printUsage();
                continue;
            }
            
            runTest(input);
            System.out.println();
        }
        
        scanner.close();
    }

    /**
     * 运行指定类型的测试
     *
     * @param testType 测试类型
     */
    private static void runTest(String testType) {
        System.out.println("启动测试: " + TEST_DESCRIPTIONS.getOrDefault(testType, testType));
        System.out.println("----------------------------------------------");
        
        try {
            switch (testType) {
                case "heap":
                    runHeapOOM();
                    break;
                case "stack":
                    runStackOverflow();
                    break;
                case "stackoom":
                    runStackOOM();
                    break;
                case "methodarea":
                    runMethodAreaOOM();
                    break;
                case "constantpool":
                    runConstantPoolOOM();
                    break;
                case "direct":
                    runDirectMemoryOOM();
                    break;
                case "all":
                    runAllTests();
                    break;
                default:
                    System.err.println("未知的测试类型: " + testType);
                    System.out.println("使用 'help' 查看支持的测试类型。");
            }
        } catch (Throwable e) {
            System.err.println("测试执行异常: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * 运行Java堆溢出测试
     */
    private static void runHeapOOM() {
        System.out.println("【Java堆溢出测试】");
        System.out.println("VM参数建议: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError");
        System.out.println();
        HeapOutOfMemory.main(new String[]{});
    }

    /**
     * 运行栈溢出测试
     */
    private static void runStackOverflow() {
        System.out.println("【虚拟机栈溢出测试】");
        System.out.println("VM参数建议: -Xss180k");
        System.out.println();
        StackOverflowError.main(new String[]{});
    }

    /**
     * 运行栈内存溢出测试
     */
    private static void runStackOOM() {
        System.out.println("【虚拟机栈内存溢出测试】");
        System.out.println("VM参数建议: -Xss2m");
        System.out.println("警告: 此测试可能导致系统不稳定!");
        System.out.println();
        StackOutOfMemory.main(new String[]{});
    }

    /**
     * 运行方法区溢出测试
     */
    private static void runMethodAreaOOM() {
        System.out.println("【方法区溢出测试】");
        System.out.println("JDK 7 VM参数建议: -XX:PermSize=10m -XX:MaxPermSize=10m");
        System.out.println("JDK 8+ VM参数建议: -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m");
        System.out.println("依赖: 需要cglib库");
        System.out.println();
        MethodAreaOutOfMemory.main(new String[]{});
    }

    /**
     * 运行运行时常量池溢出测试
     */
    private static void runConstantPoolOOM() {
        System.out.println("【运行时常量池溢出测试】");
        System.out.println("JDK 6 VM参数建议: -XX:PermSize=10m -XX:MaxPermSize=10m");
        System.out.println("JDK 7+ VM参数建议: -Xms20m -Xmx20m");
        System.out.println();
        RuntimeConstantPoolOutOfMemory.main(new String[]{});
    }

    /**
     * 运行直接内存溢出测试
     */
    private static void runDirectMemoryOOM() {
        System.out.println("【直接内存溢出测试】");
        System.out.println("VM参数建议: -Xmx20m -XX:MaxDirectMemorySize=10m");
        System.out.println();
        DirectMemoryOutOfMemory.main(new String[]{});
    }

    /**
     * 运行所有测试
     */
    private static void runAllTests() {
        System.out.println("【运行所有OOM测试】");
        System.out.println("注意: 每个测试都需要不同的VM参数，建议单独运行。");
        System.out.println();
        
        TEST_DESCRIPTIONS.forEach((key, value) -> {
            System.out.println("\n>>> 准备运行: " + value);
            System.out.println("按Enter键继续...");
            try {
                System.in.read();
            } catch (Exception e) {
                // ignore
            }
            runTest(key);
        });
    }
}
