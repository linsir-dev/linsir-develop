package com.linsir.spring.framework.spring_core.task.support;

import com.linsir.spring.framework.spring_core.task.core.AsyncListenableTaskExecutor;
import com.linsir.spring.framework.spring_core.task.core.ListenableFuture;
import com.linsir.spring.framework.spring_core.task.exception.TaskRejectedException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单异步任务执行器
 *
 * 为每个提交的任务创建一个新线程来执行。适用于任务数量较少、
 * 执行时间较短的场景。注意：大量任务可能导致资源耗尽。
 *
 * @author linsir
 * @since 1.0.0
 */
public class SimpleAsyncTaskExecutor implements AsyncListenableTaskExecutor {

    private String threadNamePrefix = "SimpleAsyncTaskExecutor-";
    private final AtomicInteger threadCount = new AtomicInteger(0);
    private volatile boolean active = true;
    private long taskTimeout = 0;

    /**
     * 默认构造函数
     */
    public SimpleAsyncTaskExecutor() {
    }

    /**
     * 使用指定的线程名前缀创建执行器
     *
     * @param threadNamePrefix 线程名前缀
     */
    public SimpleAsyncTaskExecutor(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    /**
     * 设置线程名前缀
     *
     * @param threadNamePrefix 线程名前缀
     */
    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    /**
     * 获取线程名前缀
     *
     * @return 线程名前缀
     */
    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    @Override
    public void setTaskTimeout(long timeoutMillis) {
        this.taskTimeout = timeoutMillis;
    }

    @Override
    public long getTaskTimeout() {
        return taskTimeout;
    }

    /**
     * 关闭执行器，不再接受新任务
     */
    public void shutdown() {
        this.active = false;
    }

    /**
     * 检查执行器是否处于活动状态
     *
     * @return true 如果执行器可以接受任务
     */
    public boolean isActive() {
        return active;
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (!active) {
            throw new TaskRejectedException("TaskExecutor is not active");
        }
        Thread thread = createThread(task);
        thread.start();
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (!active) {
            throw new TaskRejectedException("TaskExecutor is not active");
        }
        FutureTask<Object> futureTask = new FutureTask<>(task, null);
        Thread thread = createThread(futureTask);
        thread.start();
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (!active) {
            throw new TaskRejectedException("TaskExecutor is not active");
        }
        FutureTask<T> futureTask = new FutureTask<>(task);
        Thread thread = createThread(futureTask);
        thread.start();
        return futureTask;
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (!active) {
            throw new TaskRejectedException("TaskExecutor is not active");
        }
        FutureTask<T> futureTask = new FutureTask<>(task, result);
        Thread thread = createThread(futureTask);
        thread.start();
        return futureTask;
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (!active) {
            throw new TaskRejectedException("TaskExecutor is not active");
        }
        ListenableFutureTask<Object> futureTask = new ListenableFutureTask<>(task, null);
        Thread thread = createThread(futureTask);
        thread.start();
        return futureTask;
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (!active) {
            throw new TaskRejectedException("TaskExecutor is not active");
        }
        ListenableFutureTask<T> futureTask = new ListenableFutureTask<>(task);
        Thread thread = createThread(futureTask);
        thread.start();
        return futureTask;
    }

    /**
     * 创建新线程
     *
     * @param runnable 线程执行的任务
     * @return 新创建的线程
     */
    protected Thread createThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(threadNamePrefix + threadCount.incrementAndGet());
        thread.setDaemon(true);
        return thread;
    }

    /**
     * 内部类：可监听的未来任务
     */
    private static class ListenableFutureTask<T> extends FutureTask<T> implements ListenableFuture<T> {

        private final java.util.List<SuccessCallback<T>> successCallbacks = new java.util.ArrayList<>();
        private final java.util.List<FailureCallback> failureCallbacks = new java.util.ArrayList<>();

        public ListenableFutureTask(Runnable runnable, T result) {
            super(runnable, result);
        }

        public ListenableFutureTask(Callable<T> callable) {
            super(callable);
        }

        @Override
        public void addSuccessCallback(SuccessCallback<T> callback) {
            synchronized (this) {
                // 如果任务已完成，立即执行回调
                if (isDone()) {
                    try {
                        T result = get();
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        // 忽略异常
                    }
                } else {
                    successCallbacks.add(callback);
                }
            }
        }

        @Override
        public void addFailureCallback(FailureCallback callback) {
            synchronized (this) {
                // 如果任务已完成，检查是否有异常并立即执行回调
                if (isDone()) {
                    try {
                        get(); // 尝试获取结果，如果有异常会抛出
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                } else {
                    failureCallbacks.add(callback);
                }
            }
        }

        @Override
        protected void done() {
            super.done();
            synchronized (this) {
                try {
                    T result = get();
                    for (SuccessCallback<T> callback : successCallbacks) {
                        try {
                            callback.onSuccess(result);
                        } catch (Exception e) {
                            // 忽略回调中的异常
                        }
                    }
                } catch (Exception e) {
                    for (FailureCallback callback : failureCallbacks) {
                        try {
                            callback.onFailure(e);
                        } catch (Exception ex) {
                            // 忽略回调中的异常
                        }
                    }
                }
            }
        }
    }
}
