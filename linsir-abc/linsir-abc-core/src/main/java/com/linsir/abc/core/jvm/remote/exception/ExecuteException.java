package com.linsir.abc.core.jvm.remote.exception;

/**
 * 执行异常
 *
 * 功能：在远程代码执行过程中发生错误时抛出
 *
 * 使用场景：
 * 1. 代码执行超时
 * 2. 代码抛出运行时异常
 * 3. 安全管理器阻止执行
 * 4. 其他执行相关错误
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ExecuteException extends Exception {

    /**
     * 构造执行异常
     *
     * @param message 异常信息
     */
    public ExecuteException(String message) {
        super(message);
    }

    /**
     * 构造执行异常
     *
     * @param message 异常信息
     * @param cause 原始异常
     */
    public ExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
