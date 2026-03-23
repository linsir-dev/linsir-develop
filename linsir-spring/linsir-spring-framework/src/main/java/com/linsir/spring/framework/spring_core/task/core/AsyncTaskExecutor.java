package com.linsir.spring.framework.spring_core.task.core;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 异步任务执行器接口
 *
 * 扩展了 TaskExecutor，支持返回 Future 的异步执行模式。
 * 允许提交有返回值和无返回值的任务，并获取执行结果。
 *
 * @author linsir
 * @since 1.0.0
 */
public interface AsyncTaskExecutor extends TaskExecutor {

    /**
     * 提交一个无返回值的任务
     *
     * @param task 要执行的任务
     * @return Future 对象，可用于检查任务状态
     * @throws TaskRejectedException 如果任务被拒绝
     */
    Future<?> submit(Runnable task);

    /**
     * 提交一个有返回值的任务
     *
     * @param task 要执行的任务
     * @param <T> 返回值的类型
     * @return Future 对象，可用于获取执行结果
     * @throws TaskRejectedException 如果任务被拒绝
     */
    <T> Future<T> submit(Callable<T> task);

    /**
     * 提交一个带有结果的无返回值任务
     *
     * @param task 要执行的任务
     * @param result 任务完成后返回的结果
     * @param <T> 结果类型
     * @return Future 对象
     * @throws TaskRejectedException 如果任务被拒绝
     */
    <T> Future<T> submit(Runnable task, T result);

    /**
     * 设置任务执行的超时时间（毫秒）
     *
     * @param timeoutMillis 超时时间，0 或负数表示无超时
     */
    void setTaskTimeout(long timeoutMillis);

    /**
     * 获取当前的任务超时时间
     *
     * @return 超时时间（毫秒）
     */
    long getTaskTimeout();
}
