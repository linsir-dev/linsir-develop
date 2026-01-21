package com.linsir.abc.pdai.Base.exceptiondemo;

/**
 * 异常演示类
 * 展示Error、Exception的使用场景
 */
public class ExceptionDemo {

    /**
     * 演示检查型异常
     */
    public void demonstrateCheckedException() throws CustomException {
        System.out.println("演示检查型异常...");
        throw new CustomException("这是一个自定义检查型异常");
    }

    /**
     * 演示非检查型异常
     */
    public void demonstrateUncheckedException() {
        System.out.println("演示非检查型异常...");
        throw new CustomRuntimeException("这是一个自定义运行时异常");
    }

    /**
     * 演示系统异常（非检查型）
     */
    public void demonstrateSystemException() {
        System.out.println("演示系统异常...");
        int[] arr = new int[5];
        System.out.println(arr[10]); // 数组越界异常
    }

    /**
     * 演示错误（Error）
     */
    public void demonstrateError() {
        System.out.println("演示错误（Error）...");
        // 尝试创建一个非常大的数组，可能导致OutOfMemoryError
        try {
            byte[] hugeArray = new byte[Integer.MAX_VALUE];
        } catch (Error e) {
            System.out.println("捕获到错误: " + e.getMessage());
        }
    }

    /**
     * 演示异常链
     */
    public void demonstrateExceptionChaining() throws CustomException {
        System.out.println("演示异常链...");
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            // 将原始异常包装为自定义异常
            throw new CustomException("计算错误", e);
        }
    }

    /**
     * 演示try-catch-finally
     */
    public void demonstrateTryCatchFinally() {
        System.out.println("\n演示try-catch-finally...");
        try {
            System.out.println("进入try块");
            int result = 10 / 0;
            System.out.println("try块正常执行完毕");
        } catch (ArithmeticException e) {
            System.out.println("捕获到异常: " + e.getMessage());
        } finally {
            System.out.println("执行finally块");
        }
        System.out.println("方法继续执行");
    }

    /**
     * 演示多重catch
     */
    public void demonstrateMultipleCatch() {
        System.out.println("\n演示多重catch...");
        try {
            Object obj = null;
            System.out.println(obj.toString()); // 空指针异常
        } catch (NullPointerException e) {
            System.out.println("捕获到空指针异常: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("捕获到通用异常: " + e.getMessage());
        }
    }
}
