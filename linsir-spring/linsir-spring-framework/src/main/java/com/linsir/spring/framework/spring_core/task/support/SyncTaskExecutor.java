package com.linsir.spring.framework.spring_core.task.support;

import com.linsir.spring.framework.spring_core.task.core.TaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 同步任务执行器
 *
 * 在当前线程同步执行任务。适用于不需要并发执行的场景，
 * 或者作为调试和测试用途。
 *
 * @author linsir
 * @since 1.0.0
 */
public class SyncTaskExecutor implements TaskExecutor {

    /**
     * 单例实例
     */
    public static final SyncTaskExecutor INSTANCE = new SyncTaskExecutor();

    /**
     * 私有构造函数，强制使用单例
     */
    private SyncTaskExecutor() {
    }

    /**
     * 获取同步任务执行器实例
     *
     * @return SyncTaskExecutor 实例
     */
    public static SyncTaskExecutor getInstance() {
        return INSTANCE;
    }

    /**
     * 在当前线程同步执行任务
     *
     * @param task 要执行的任务
     * @throws IllegalArgumentException 如果 task 为 null
     */
    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        task.run();
    }

    /**
     * 提交一个无返回值的任务（同步执行）
     *
     * @param task 要执行的任务
     * @return 已完成的 Future
     */
    public Future<?> submit(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        FutureTask<Object> futureTask = new FutureTask<>(task, null);
        futureTask.run();
        return futureTask;
    }

    /**
     * 提交一个有返回值的任务（同步执行）
     *
     * @param task 要执行的任务
     * @param <T> 返回值的类型
     * @return 包含结果的 Future
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        FutureTask<T> futureTask = new FutureTask<>(task);
        futureTask.run();
        return futureTask;
    }

    /**
     * 提交一个带有结果的无返回值任务（同步执行）
     *
     * @param task 要执行的任务
     * @param result 任务完成后返回的结果
     * @param <T> 结果类型
     * @return 包含结果的 Future
     */
    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        FutureTask<T> futureTask = new FutureTask<>(task, result);
        futureTask.run();
        return futureTask;
    }
}
