package com.linsir.spring.framework.spring_core.task.support;

import com.linsir.spring.framework.spring_core.task.core.AsyncListenableTaskExecutor;
import com.linsir.spring.framework.spring_core.task.core.ListenableFuture;
import com.linsir.spring.framework.spring_core.task.exception.TaskRejectedException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池任务执行器
 *
 * 基于 Java 线程池实现的异步任务执行器。支持核心线程数、最大线程数、
 * 队列容量等配置，是生产环境推荐使用的执行器。
 *
 * @author linsir
 * @since 1.0.0
 */
public class ThreadPoolTaskExecutor implements AsyncListenableTaskExecutor {

    private ThreadPoolExecutor threadPoolExecutor;
    private String threadNamePrefix = "ThreadPoolTaskExecutor-";
    private int corePoolSize = 5;
    private int maxPoolSize = 10;
    private int queueCapacity = 100;
    private long keepAliveSeconds = 60;
    private boolean allowCoreThreadTimeOut = false;
    private long taskTimeout = 0;

    /**
     * 默认构造函数
     */
    public ThreadPoolTaskExecutor() {
    }

    /**
     * 初始化线程池
     */
    public void initialize() {
        if (threadPoolExecutor != null) {
            shutdown();
        }

        BlockingQueue<Runnable> queue = createQueue(queueCapacity);

        this.threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveSeconds,
                TimeUnit.SECONDS,
                queue,
                new CustomThreadFactory(threadNamePrefix),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        this.threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
    }

    /**
     * 创建任务队列
     *
     * @param capacity 队列容量
     * @return 阻塞队列
     */
    protected BlockingQueue<Runnable> createQueue(int capacity) {
        if (capacity > 0) {
            return new LinkedBlockingQueue<>(capacity);
        } else {
            return new SynchronousQueue<>();
        }
    }

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (threadPoolExecutor == null) {
            initialize();
        }
        try {
            threadPoolExecutor.execute(task);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by thread pool", e);
        }
    }

    @Override
    public Future<?> submit(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (threadPoolExecutor == null) {
            initialize();
        }
        try {
            return threadPoolExecutor.submit(task);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by thread pool", e);
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (threadPoolExecutor == null) {
            initialize();
        }
        try {
            return threadPoolExecutor.submit(task);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by thread pool", e);
        }
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (threadPoolExecutor == null) {
            initialize();
        }
        try {
            return threadPoolExecutor.submit(task, result);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by thread pool", e);
        }
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (threadPoolExecutor == null) {
            initialize();
        }
        ListenableFutureTask<Object> futureTask = new ListenableFutureTask<>(task, null);
        try {
            threadPoolExecutor.execute(futureTask);
            return futureTask;
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by thread pool", e);
        }
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (threadPoolExecutor == null) {
            initialize();
        }
        ListenableFutureTask<T> futureTask = new ListenableFutureTask<>(task);
        try {
            threadPoolExecutor.execute(futureTask);
            return futureTask;
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by thread pool", e);
        }
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
     * 关闭线程池
     */
    public void shutdown() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
        }
    }

    /**
     * 立即关闭线程池
     *
     * @return 未执行的任务列表
     */
    public java.util.List<Runnable> shutdownNow() {
        if (threadPoolExecutor != null) {
            return threadPoolExecutor.shutdownNow();
        }
        return java.util.Collections.emptyList();
    }

    /**
     * 检查线程池是否已关闭
     *
     * @return true 如果线程池已关闭
     */
    public boolean isShutdown() {
        return threadPoolExecutor == null || threadPoolExecutor.isShutdown();
    }

    /**
     * 检查线程池是否已终止
     *
     * @return true 如果线程池已终止
     */
    public boolean isTerminated() {
        return threadPoolExecutor == null || threadPoolExecutor.isTerminated();
    }

    // Getters and Setters

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        if (threadPoolExecutor != null) {
            threadPoolExecutor.setCorePoolSize(corePoolSize);
        }
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        if (threadPoolExecutor != null) {
            threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
        }
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public void setKeepAliveSeconds(long keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
        if (threadPoolExecutor != null) {
            threadPoolExecutor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
        }
    }

    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        if (threadPoolExecutor != null) {
            threadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
        }
    }

    /**
     * 自定义线程工厂
     */
    private static class CustomThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public CustomThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, prefix + threadNumber.getAndIncrement());
            thread.setDaemon(false);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
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
                if (isDone()) {
                    try {
                        get();
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
