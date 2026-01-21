package com.linsir.abc.pdai.base.exceptiondemo;

/**
 * 异常测试类
 * 演示Java异常体系中的Error和Exception
 */
public class ExceptionTest {
    public static void main(String[] args) {
        System.out.println("=== Java异常体系演示 ===\n");
        
        ExceptionDemo demo = new ExceptionDemo();
        
        // 1. 演示try-catch-finally
        demo.demonstrateTryCatchFinally();
        
        // 2. 演示多重catch
        demo.demonstrateMultipleCatch();
        
        // 3. 演示非检查型异常
        System.out.println("\n3. 演示非检查型异常:");
        try {
            demo.demonstrateUncheckedException();
        } catch (CustomRuntimeException e) {
            System.out.println("捕获到非检查型异常: " + e.getMessage());
        }
        
        // 4. 演示系统异常
        System.out.println("\n4. 演示系统异常:");
        try {
            demo.demonstrateSystemException();
        } catch (Exception e) {
            System.out.println("捕获到系统异常: " + e.getMessage());
        }
        
        // 5. 演示检查型异常
        System.out.println("\n5. 演示检查型异常:");
        try {
            demo.demonstrateCheckedException();
        } catch (CustomException e) {
            System.out.println("捕获到检查型异常: " + e.getMessage());
        }
        
        // 6. 演示异常链
        System.out.println("\n6. 演示异常链:");
        try {
            demo.demonstrateExceptionChaining();
        } catch (CustomException e) {
            System.out.println("捕获到异常: " + e.getMessage());
            System.out.println("原始异常: " + e.getCause().getMessage());
        }
        
        // 7. 演示错误（Error）
        System.out.println("\n7. 演示错误（Error）:");
        demo.demonstrateError();
        
        System.out.println("\n=== 异常演示完成 ===");
        System.out.println("\n总结:");
        System.out.println("- Exception: 程序可以处理的异常，分为检查型和非检查型");
        System.out.println("- Error: 严重问题，程序一般无法处理，如OutOfMemoryError");
        System.out.println("- 检查型异常: 需要显式捕获或声明抛出");
        System.out.println("- 非检查型异常: 继承自RuntimeException，无需显式处理");
    }
}
