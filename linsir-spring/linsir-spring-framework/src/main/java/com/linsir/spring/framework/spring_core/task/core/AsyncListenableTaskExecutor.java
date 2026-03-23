package com.linsir.spring.framework.spring_core.task.core;

import java.util.concurrent.Callable;

/**
 * 支持回调的异步任务执行器接口
 *
 * 扩展了 AsyncTaskExecutor，支持返回 ListenableFuture 的异步执行。
 * 允许添加成功和失败的回调函数。
 *
 * @author linsir
 * @since 1.0.0
 */
public interface AsyncListenableTaskExecutor extends AsyncTaskExecutor {

    /**
     * 提交一个无返回值的任务，返回可监听的未来对象
     *
     * @param task 要执行的任务
     * @return ListenableFuture 对象
     */
    ListenableFuture<?> submitListenable(Runnable task);

    /**
     * 提交一个有返回值的任务，返回可监听的未来对象
     *
     * @param task 要执行的任务
     * @param <T> 返回值的类型
     * @return ListenableFuture 对象
     */
    <T> ListenableFuture<T> submitListenable(Callable<T> task);
}
