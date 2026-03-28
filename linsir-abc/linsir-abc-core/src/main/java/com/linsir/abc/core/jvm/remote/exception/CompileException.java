package com.linsir.abc.core.jvm.remote.exception;

/**
 * 编译异常
 *
 * 功能：在动态编译过程中发生错误时抛出
 *
 * 使用场景：
 * 1. Java源代码语法错误
 * 2. 编译器无法找到依赖类
 * 3. 其他编译相关错误
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class CompileException extends Exception {

    /**
     * 构造编译异常
     *
     * @param message 异常信息
     */
    public CompileException(String message) {
        super(message);
    }

    /**
     * 构造编译异常
     *
     * @param message 异常信息
     * @param cause 原始异常
     */
    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
