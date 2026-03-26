package com.linsir.abc.core.grammar.exception;

/**
 * 异常处理示例
 *
 * 本类演示 Java 异常处理机制
 * 对应 JDK: java.lang.Exception
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExceptionHandling {

    /**
     * 演示 try-catch
     */
    public void demonstrateTryCatch() {
        System.out.println("=== try-catch 基本用法 ===");

        // 捕获算术异常
        try {
            int result = 10 / 0;
            System.out.println("结果: " + result);
        } catch (ArithmeticException e) {
            System.out.println("捕获到算术异常: " + e.getMessage());
        }

        // 捕获数组越界异常
        try {
            int[] arr = {1, 2, 3};
            System.out.println(arr[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("捕获到数组越界异常: " + e.getMessage());
        }

        System.out.println("程序继续执行...");
    }

    /**
     * 演示多重 catch
     */
    public void demonstrateMultipleCatch() {
        System.out.println("\n=== 多重 catch ===");

        String[] inputs = {"123", "abc", null};

        for (String input : inputs) {
            try {
                int num = Integer.parseInt(input);
                int result = 100 / num;
                System.out.println("输入: " + input + ", 结果: " + result);
            } catch (NumberFormatException e) {
                System.out.println("数字格式错误: " + input);
            } catch (ArithmeticException e) {
                System.out.println("算术错误: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("其他异常: " + e.getClass().getSimpleName());
            }
        }
    }

    /**
     * 演示 try-catch-finally
     */
    public void demonstrateFinally() {
        System.out.println("\n=== finally 块 ===");

        System.out.println("情况1: 正常执行");
        executeWithFinally(true);

        System.out.println("\n情况2: 发生异常");
        executeWithFinally(false);
    }

    private void executeWithFinally(boolean success) {
        try {
            System.out.println("  try 块开始");
            if (!success) {
                throw new RuntimeException("模拟异常");
            }
            System.out.println("  try 块正常结束");
        } catch (RuntimeException e) {
            System.out.println("  catch 块: " + e.getMessage());
        } finally {
            System.out.println("  finally 块: 总是执行");
        }
    }

    /**
     * 演示 try-with-resources
     */
    public void demonstrateTryWithResources() {
        System.out.println("\n=== try-with-resources ===");

        // Java 7+ 自动关闭资源
        try (Resource resource = new Resource("资源1")) {
            resource.doSomething();
        } catch (Exception e) {
            System.out.println("异常: " + e.getMessage());
        }

        System.out.println("\n多个资源:");
        try (Resource r1 = new Resource("资源A");
             Resource r2 = new Resource("资源B")) {
            r1.doSomething();
            r2.doSomething();
        }
    }

    /**
     * 演示抛出异常
     */
    public void demonstrateThrowing() throws Exception {
        System.out.println("\n=== 抛出异常 ===");

        // 检查参数
        validateAge(25);   // 正常
        validateAge(-5);   // 抛出异常
    }

    private void validateAge(int age) throws IllegalArgumentException {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("年龄必须在 0-150 之间: " + age);
        }
        System.out.println("年龄有效: " + age);
    }

    /**
     * 演示异常链
     */
    public void demonstrateExceptionChaining() {
        System.out.println("\n=== 异常链 ===");

        try {
            method1();
        } catch (Exception e) {
            System.out.println("捕获异常: " + e.getMessage());
            System.out.println("原因: " + e.getCause());
        }
    }

    private void method1() throws Exception {
        try {
            method2();
        } catch (IllegalArgumentException e) {
            throw new Exception("method1 出错", e);  // 保留原始异常
        }
    }

    private void method2() {
        throw new IllegalArgumentException("method2 参数错误");
    }

    /**
     * 资源类
     */
    static class Resource implements AutoCloseable {
        private String name;

        public Resource(String name) {
            this.name = name;
            System.out.println("  打开资源: " + name);
        }

        public void doSomething() {
            System.out.println("  使用资源: " + name);
        }

        @Override
        public void close() {
            System.out.println("  关闭资源: " + name);
        }
    }

    /**
     * 主方法
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 异常处理演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        ExceptionHandling demo = new ExceptionHandling();
        demo.demonstrateTryCatch();
        demo.demonstrateMultipleCatch();
        demo.demonstrateFinally();
        demo.demonstrateTryWithResources();

        try {
            demo.demonstrateThrowing();
        } catch (Exception e) {
            System.out.println("主方法捕获: " + e.getMessage());
        }

        demo.demonstrateExceptionChaining();

        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
