package com.linsir.abc.pdai.Base.keyword;

/**
 * 演示 finally 代码块的用法
 * 1. 无论是否发生异常，finally 代码块总会执行（除非 System.exit()）。
 * 2. 在 try 或 catch 中有 return 时，finally 依然会执行。
 */
public class FinallyDemo {

    public static void main(String[] args) {
        System.out.println("--- 演示 finally 的基本用法 ---");
        basicUsage();

        System.out.println("\n--- 演示 return 与 finally 的执行顺序 ---");
        int result = testReturn();
        System.out.println("testReturn() 返回结果: " + result);
        
        System.out.println("\n--- 演示 System.exit() ---");
        // 注意：这会终止 JVM，放在最后执行
        testExit(); 
    }

    public static void basicUsage() {
        try {
            System.out.println("1. 进入 try 块");
            int a = 10 / 0; // 产生异常
        } catch (ArithmeticException e) {
            System.out.println("2. 捕获异常: " + e.getMessage());
        } finally {
            System.out.println("3. 执行 finally 块 (无论是否有异常都会执行)");
        }
    }

    public static int testReturn() {
        try {
            System.out.println("1. 进入 try 块 (准备 return 1)");
            return 1;
        } finally {
            System.out.println("2. 执行 finally 块 (在 return 之前执行)");
            // 警告：不建议在 finally 中使用 return，这会覆盖 try/catch 中的返回值
            // return 2; 
        }
    }

    public static void testExit() {
        try {
            System.out.println("1. 进入 try 块 (准备 System.exit(0))");
            System.exit(0);
        } finally {
            System.out.println("2. 这行代码不会被执行，因为 JVM 已经退出了");
        }
    }
}
