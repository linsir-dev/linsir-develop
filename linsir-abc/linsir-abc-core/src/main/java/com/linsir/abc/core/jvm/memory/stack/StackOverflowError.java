package com.linsir.abc.core.jvm.memory.stack;

/**
 * 虚拟机栈栈溢出(StackOverflowError)
 * 
 * <p>虚拟机栈是线程私有的，每个方法执行时都会创建一个栈帧。如果线程请求的栈深度
 * 超过虚拟机允许的最大深度，将抛出StackOverflowError。</p>
 * 
 * <p><strong>典型场景:</strong> 无限递归调用</p>
 * 
 * <p><strong>VM参数:</strong> -Xss128k (设置较小的栈空间以便快速触发)</p>
 * 
 * <p><strong>预期异常:</strong> java.lang.StackOverflowError</p>
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class StackOverflowError {

    /**
     * 栈深度计数器
     * 用于记录递归调用的深度
     */
    private int stackLength = 1;

    /**
     * 递归方法，用于触发栈溢出
     * 
     * <p>该方法无限递归调用自身，每次调用都会在栈中创建一个新的栈帧，
     * 当栈空间耗尽时抛出StackOverflowError。</p>
     */
    public void recursiveCall() {
        stackLength++;
        recursiveCall();
    }

    /**
     * 程序入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        StackOverflowError demo = new StackOverflowError();
        
        System.out.println("开始递归调用，准备触发栈溢出...");
        System.out.println("VM参数: -Xss128k");
        
        try {
            demo.recursiveCall();
        } catch (java.lang.StackOverflowError e) {
            System.err.println("捕获到StackOverflowError");
            System.err.println("栈深度: " + demo.stackLength);
            throw e;
        }
    }
}
