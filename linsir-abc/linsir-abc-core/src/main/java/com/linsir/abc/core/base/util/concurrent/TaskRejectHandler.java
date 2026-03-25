package com.linsir.abc.core.base.util.concurrent;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务拒绝处理器
 * 演示线程池任务拒绝的各种策略及其实现原理
 *
 * <p>拒绝策略触发条件：</p>
 * <ul>
 *   <li>线程池已关闭</li>
 *   <li>线程数达到最大线程数且任务队列已满</li>
 * </ul>
 *
 * <p>常见拒绝策略：</p>
 * <ul>
 *   <li>AbortPolicy：直接抛出异常（默认策略）</li>
 *   <li>CallerRunsPolicy：由调用线程执行任务</li>
 *   <li>DiscardPolicy：静默丢弃任务</li>
 *   <li>DiscardOldestPolicy：丢弃最旧任务，尝试提交新任务</li>
 *   <li>自定义策略：根据业务需求实现</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class TaskRejectHandler {

    /**
     * 拒绝策略接口
     */
    @FunctionalInterface
    public interface RejectedExecutionHandler {
        /**
         * 处理被拒绝的任务
         *
         * @param r 被拒绝的任务
         * @param executor 执行器
         */
        void rejectedExecution(Runnable r, ThreadPoolExecutor executor);
    }

    /**
     * 中止策略（默认）
     * 直接抛出RejectedExecutionException异常
     */
    public static class AbortPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            throw new RejectedExecutionException("Task " + r.toString() +
                    " rejected from " + executor.toString());
        }
    }

    /**
     * 调用者运行策略
     * 由提交任务的线程（调用者）自己执行该任务
     *
     * <p>优点：</p>
     * <ul>
     *   <li>不会丢失任务</li>
     *   <li>提供了一种简单的反馈控制机制，减缓新任务的提交速度</li>
     * </ul>
     */
    public static class CallerRunsPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                // 由调用线程执行任务
                r.run();
            }
        }
    }

    /**
     * 丢弃策略
     * 静默丢弃被拒绝的任务，不抛出异常
     *
     * <p>适用场景：</p>
 * <ul>
 *   <li>任务可以容忍丢失</li>
 *   <li>不需要知道任务是否被执行</li>
 * </ul>
 */
    public static class DiscardPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 什么都不做，直接丢弃任务
        }
    }

    /**
     * 丢弃最旧策略
     * 丢弃队列中最旧的任务，然后尝试重新提交当前任务
     *
     * <p>适用场景：</p>
     * <ul>
     *   <li>新任务比旧任务更重要</li>
     *   <li>希望优先处理最新任务</li>
     * </ul>
     */
    public static class DiscardOldestPolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (!executor.isShutdown()) {
                // 丢弃队列中最旧的任务
                executor.getQueue().poll();
                // 重新尝试提交当前任务
                executor.execute(r);
            }
        }
    }

    /**
     * 重试策略
     * 等待一段时间后重试提交
     */
    public static class RetryPolicy implements RejectedExecutionHandler {

        private final long retryDelayMs;
        private final int maxRetries;

        public RetryPolicy(long retryDelayMs, int maxRetries) {
            this.retryDelayMs = retryDelayMs;
            this.maxRetries = maxRetries;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor.isShutdown()) {
                throw new RejectedExecutionException("Executor is shutdown");
            }

            int retries = 0;
            while (retries < maxRetries) {
                try {
                    Thread.sleep(retryDelayMs);
                    if (!executor.isShutdown()) {
                        executor.execute(r);
                        return;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RejectedExecutionException("Retry interrupted", e);
                } catch (RejectedExecutionException e) {
                    retries++;
                }
            }
            throw new RejectedExecutionException("Task rejected after " + maxRetries + " retries");
        }
    }

    /**
     * 队列等待策略
     * 阻塞等待直到队列有空位
     */
    public static class BlockPolicy implements RejectedExecutionHandler {

        private final long maxWaitMs;

        public BlockPolicy(long maxWaitMs) {
            this.maxWaitMs = maxWaitMs;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor.isShutdown()) {
                throw new RejectedExecutionException("Executor is shutdown");
            }

            try {
                // 尝试将任务放入队列，阻塞等待
                boolean offered = executor.getQueue().offer(r, maxWaitMs, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (!offered) {
                    throw new RejectedExecutionException("Task rejected after waiting " + maxWaitMs + "ms");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RejectedExecutionException("Wait interrupted", e);
            }
        }
    }

    /**
     * 日志记录策略
     * 记录被拒绝的任务信息，然后使用其他策略处理
     */
    public static class LoggingPolicy implements RejectedExecutionHandler {

        private final RejectedExecutionHandler delegate;

        public LoggingPolicy(RejectedExecutionHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 记录日志
            System.err.println("[REJECTED] Task rejected: " + r +
                    ", Active: " + executor.getActiveCount() +
                    ", Pool: " + executor.getPoolSize() +
                    ", Queue: " + executor.getQueue().size());

            // 委托给其他策略处理
            delegate.rejectedExecution(r, executor);
        }
    }

    /**
     * 自定义拒绝异常
     */
    public static class TaskRejectedException extends RejectedExecutionException {
        private final Runnable task;
        private final long rejectTime;

        public TaskRejectedException(String message, Runnable task) {
            super(message);
            this.task = task;
            this.rejectTime = System.currentTimeMillis();
        }

        public Runnable getTask() {
            return task;
        }

        public long getRejectTime() {
            return rejectTime;
        }
    }

    /**
     * 拒绝统计信息
     */
    public static class RejectionStatistics {
        private long totalRejections = 0;
        private long lastRejectionTime = 0;
        private final java.util.concurrent.atomic.AtomicLong rejectionCount =
                new java.util.concurrent.atomic.AtomicLong(0);

        public void recordRejection() {
            totalRejections++;
            lastRejectionTime = System.currentTimeMillis();
            rejectionCount.incrementAndGet();
        }

        public long getTotalRejections() {
            return totalRejections;
        }

        public long getLastRejectionTime() {
            return lastRejectionTime;
        }

        public long getRejectionCount() {
            return rejectionCount.get();
        }

        public void reset() {
            totalRejections = 0;
            lastRejectionTime = 0;
            rejectionCount.set(0);
        }
    }
}
