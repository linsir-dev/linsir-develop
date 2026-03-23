package com.linsir.spring.framework.spring_core.task.core;

/**
 * 任务执行器接口
 *
 * 定义执行任务的基本契约。实现类可以决定是同步执行还是异步执行。
 * 这是 Spring 任务执行模块的最基础接口。
 *
 * @author linsir
 * @since 1.0.0
 */
@FunctionalInterface
public interface TaskExecutor {

    /**
     * 执行给定的任务
     *
     * @param task 要执行的任务，不能为 null
     * @throws TaskRejectedException 如果任务被拒绝执行
     * @throws IllegalArgumentException 如果 task 为 null
     */
    void execute(Runnable task);
}
