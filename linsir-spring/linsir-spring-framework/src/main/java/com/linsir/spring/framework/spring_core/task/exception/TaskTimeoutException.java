package com.linsir.spring.framework.spring_core.task.exception;

/**
 * 任务执行超时异常
 *
 * 当任务执行时间超过设定的超时时间时抛出此异常。
 * 用于控制任务的执行时间，防止长时间运行的任务占用资源。
 *
 * @author linsir
 * @since 1.0.0
 */
public class TaskTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 创建任务超时异常
     */
    public TaskTimeoutException() {
        super();
    }

    /**
     * 创建任务超时异常
     *
     * @param message 异常消息
     */
    public TaskTimeoutException(String message) {
        super(message);
    }

    /**
     * 创建任务超时异常
     *
     * @param message 异常消息
     * @param cause 原始异常
     */
    public TaskTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 创建任务超时异常
     *
     * @param cause 原始异常
     */
    public TaskTimeoutException(Throwable cause) {
        super(cause);
    }
}
