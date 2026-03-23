package com.linsir.spring.framework.spring_core.task.core;

import java.util.concurrent.Future;

/**
 * 可监听的未来结果接口
 *
 * 扩展了 Future 接口，支持添加回调函数，在任务完成时自动执行。
 * 这是实现异步回调机制的基础接口。
 *
 * @param <T> 结果的类型
 * @author linsir
 * @since 1.0.0
 */
public interface ListenableFuture<T> extends Future<T> {

    /**
     * 添加成功回调
     *
     * @param callback 成功时的回调函数
     */
    void addSuccessCallback(SuccessCallback<T> callback);

    /**
     * 添加失败回调
     *
     * @param callback 失败时的回调函数
     */
    void addFailureCallback(FailureCallback callback);

    /**
     * 同时添加成功和失败回调
     *
     * @param successCallback 成功回调
     * @param failureCallback 失败回调
     */
    default void addCallbacks(SuccessCallback<T> successCallback, FailureCallback failureCallback) {
        addSuccessCallback(successCallback);
        addFailureCallback(failureCallback);
    }

    /**
     * 成功回调函数式接口
     *
     * @param <T> 结果类型
     */
    @FunctionalInterface
    interface SuccessCallback<T> {
        /**
         * 任务成功完成时调用
         *
         * @param result 任务执行结果
         */
        void onSuccess(T result);
    }

    /**
     * 失败回调函数式接口
     */
    @FunctionalInterface
    interface FailureCallback {
        /**
         * 任务执行失败时调用
         *
         * @param ex 异常信息
         */
        void onFailure(Throwable ex);
    }
}
