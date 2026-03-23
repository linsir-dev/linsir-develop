package com.linsir.spring.framework.spring_core.task.scheduler;

import com.linsir.spring.framework.spring_core.task.exception.TaskRejectedException;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于并发包的任务调度器实现
 *
 * 使用 ScheduledThreadPoolExecutor 实现任务调度功能。
 * 支持延迟执行、固定频率执行、固定延迟执行等多种调度模式。
 *
 * @author linsir
 * @since 1.0.0
 */
public class ConcurrentTaskScheduler implements TaskScheduler {

    private ScheduledExecutorService scheduledExecutor;
    private String threadNamePrefix = "TaskScheduler-";
    private int poolSize = 1;
    private boolean removeOnCancelPolicy = true;

    /**
     * 默认构造函数
     */
    public ConcurrentTaskScheduler() {
    }

    /**
     * 使用指定的线程池大小创建调度器
     *
     * @param poolSize 线程池大小
     */
    public ConcurrentTaskScheduler(int poolSize) {
        this.poolSize = poolSize;
    }

    /**
     * 使用已有的 ScheduledExecutorService 创建调度器
     *
     * @param scheduledExecutor 调度执行器
     */
    public ConcurrentTaskScheduler(ScheduledExecutorService scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
    }

    /**
     * 初始化调度器
     */
    public void initialize() {
        if (scheduledExecutor == null) {
            scheduledExecutor = Executors.newScheduledThreadPool(
                    poolSize,
                    new CustomThreadFactory(threadNamePrefix)
            );
            if (scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
                ((ScheduledThreadPoolExecutor) scheduledExecutor).setRemoveOnCancelPolicy(removeOnCancelPolicy);
            }
        }
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time must not be null");
        }
        ensureInitialized();

        long delay = startTime.getTime() - System.currentTimeMillis();
        delay = Math.max(delay, 0);

        try {
            return scheduledExecutor.schedule(task, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by scheduler", e);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithDelay(Runnable task, long delayMillis) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (delayMillis < 0) {
            throw new IllegalArgumentException("Delay must not be negative");
        }
        ensureInitialized();

        try {
            return scheduledExecutor.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by scheduler", e);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long periodMillis) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (periodMillis <= 0) {
            throw new IllegalArgumentException("Period must be positive");
        }
        ensureInitialized();

        try {
            return scheduledExecutor.scheduleAtFixedRate(task, 0, periodMillis, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by scheduler", e);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long periodMillis) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time must not be null");
        }
        if (periodMillis <= 0) {
            throw new IllegalArgumentException("Period must be positive");
        }
        ensureInitialized();

        long delay = startTime.getTime() - System.currentTimeMillis();
        delay = Math.max(delay, 0);

        try {
            return scheduledExecutor.scheduleAtFixedRate(task, delay, periodMillis, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by scheduler", e);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delayMillis) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (delayMillis <= 0) {
            throw new IllegalArgumentException("Delay must be positive");
        }
        ensureInitialized();

        try {
            return scheduledExecutor.scheduleWithFixedDelay(task, 0, delayMillis, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by scheduler", e);
        }
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delayMillis) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time must not be null");
        }
        if (delayMillis <= 0) {
            throw new IllegalArgumentException("Delay must be positive");
        }
        ensureInitialized();

        long delay = startTime.getTime() - System.currentTimeMillis();
        delay = Math.max(delay, 0);

        try {
            return scheduledExecutor.scheduleWithFixedDelay(task, delay, delayMillis, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException e) {
            throw new TaskRejectedException("Task was rejected by scheduler", e);
        }
    }

    @Override
    public ScheduledFuture<?> executeImmediately(Runnable task) {
        return scheduleWithDelay(task, 0);
    }

    @Override
    public void shutdown() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdown();
        }
    }

    @Override
    public boolean isShutdown() {
        return scheduledExecutor == null || scheduledExecutor.isShutdown();
    }

    /**
     * 确保调度器已初始化
     */
    private void ensureInitialized() {
        if (scheduledExecutor == null) {
            initialize();
        }
    }

    // Getters and Setters

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public void setRemoveOnCancelPolicy(boolean removeOnCancelPolicy) {
        this.removeOnCancelPolicy = removeOnCancelPolicy;
        if (scheduledExecutor instanceof ScheduledThreadPoolExecutor) {
            ((ScheduledThreadPoolExecutor) scheduledExecutor).setRemoveOnCancelPolicy(removeOnCancelPolicy);
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
}
