package com.linsir.spring.framework.spring_core.task.exception;

/**
 * 任务被拒绝异常
 *
 * 当任务执行器无法接受新任务时抛出此异常。
 * 可能的原因包括：线程池已满、执行器已关闭、资源不足等。
 *
 * @author linsir
 * @since 1.0.0
 */
public class TaskRejectedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 创建任务被拒绝异常
     */
    public TaskRejectedException() {
        super();
    }

    /**
     * 创建任务被拒绝异常
     *
     * @param message 异常消息
     */
    public TaskRejectedException(String message) {
        super(message);
    }

    /**
     * 创建任务被拒绝异常
     *
     * @param message 异常消息
     * @param cause 原始异常
     */
    public TaskRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 创建任务被拒绝异常
     *
     * @param cause 原始异常
     */
    public TaskRejectedException(Throwable cause) {
        super(cause);
    }
}
