package com.linsir.abc.core.base.util.concurrent;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 定时任务执行器实现
 * 演示ScheduledThreadPoolExecutor的核心原理：延迟队列、任务调度、周期任务
 *
 * <p>核心组件：</p>
 * <ul>
 *   <li>延迟队列：存储定时任务，按执行时间排序</li>
 *   <li>任务包装：将Runnable/Callable包装为ScheduledFutureTask</li>
 *   <li>线程池：使用线程池执行任务</li>
 * </ul>
 *
 * <p>任务类型：</p>
 * <ul>
 *   <li>延迟任务：延迟一定时间后执行一次</li>
 *   <li>固定频率任务：按固定频率周期性执行</li>
 *   <li>固定延迟任务：上次执行完成后延迟固定时间再执行</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ScheduledExecutorImplementation {

    /**
     * 序列号生成器（用于排序）
     */
    private static final AtomicLong sequencer = new AtomicLong(0);

    /**
     * 线程池
     */
    private final ThreadPoolExecutorImplementation executor;

    /**
     * 延迟队列
     */
    private final DelayQueue<ScheduledFutureTask<?>> delayQueue = new DelayQueue<>();

    /**
     * 是否继续执行
     */
    private volatile boolean continueExistingPeriodicTasksAfterShutdown;

    /**
     * 是否执行延迟任务
     */
    private volatile boolean executeExistingDelayedTasksAfterShutdown = true;

    /**
     * 默认构造器
     */
    public ScheduledExecutorImplementation(int corePoolSize) {
        this.executor = new ThreadPoolExecutorImplementation(
                corePoolSize, Integer.MAX_VALUE, 10L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
        startWorker();
    }

    /**
     * 启动工作线程
     */
    private void startWorker() {
        Thread worker = new Thread(this::runWorker);
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * 工作线程运行逻辑
     */
    private void runWorker() {
        while (!executor.isShutdown()) {
            try {
                ScheduledFutureTask<?> task = delayQueue.take();
                if (!task.isCancelled()) {
                    executor.execute(task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * 提交延迟任务
     */
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        if (command == null || unit == null) {
            throw new NullPointerException();
        }
        if (delay < 0) {
            delay = 0;
        }
        long triggerTime = triggerTime(unit.toNanos(delay));
        ScheduledFutureTask<Void> task = new ScheduledFutureTask<>(
                command, null, triggerTime, sequencer.getAndIncrement());
        delayQueue.offer(task);
        return task;
    }

    /**
     * 提交延迟任务（Callable版本）
     */
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        if (callable == null || unit == null) {
            throw new NullPointerException();
        }
        if (delay < 0) {
            delay = 0;
        }
        long triggerTime = triggerTime(unit.toNanos(delay));
        ScheduledFutureTask<V> task = new ScheduledFutureTask<>(
                callable, triggerTime, sequencer.getAndIncrement());
        delayQueue.offer(task);
        return task;
    }

    /**
     * 提交固定频率周期任务
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                   long initialDelay,
                                                   long period,
                                                   TimeUnit unit) {
        if (command == null || unit == null) {
            throw new NullPointerException();
        }
        if (period <= 0) {
            throw new IllegalArgumentException("period must be positive");
        }
        long triggerTime = triggerTime(unit.toNanos(initialDelay));
        ScheduledFutureTask<Void> task = new ScheduledFutureTask<>(
                command, null, triggerTime, unit.toNanos(period), sequencer.getAndIncrement());
        delayQueue.offer(task);
        return task;
    }

    /**
     * 提交固定延迟周期任务
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                      long initialDelay,
                                                      long delay,
                                                      TimeUnit unit) {
        if (command == null || unit == null) {
            throw new NullPointerException();
        }
        if (delay <= 0) {
            throw new IllegalArgumentException("delay must be positive");
        }
        long triggerTime = triggerTime(unit.toNanos(initialDelay));
        ScheduledFutureTask<Void> task = new ScheduledFutureTask<>(
                command, null, triggerTime, -unit.toNanos(delay), sequencer.getAndIncrement());
        delayQueue.offer(task);
        return task;
    }

    /**
     * 计算触发时间
     */
    private long triggerTime(long delay) {
        return now() + delay;
    }

    /**
     * 获取当前时间（纳秒）
     */
    private long now() {
        return System.nanoTime();
    }

    /**
     * 关闭执行器
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * 立即关闭执行器
     */
    public List<Runnable> shutdownNow() {
        return executor.shutdownNow();
    }

    /**
     * 判断是否已关闭
     */
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    /**
     * 判断是否已终止
     */
    public boolean isTerminated() {
        return executor.isTerminated();
    }

    /**
     * 等待终止
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executor.awaitTermination(timeout, unit);
    }

    /**
     * 定时任务Future实现
     */
    private class ScheduledFutureTask<V> implements RunnableScheduledFuture<V> {

        private final Runnable runnable;
        private final Callable<V> callable;
        private V result;
        private Throwable exception;

        private long time;           // 执行时间（纳秒）
        private final long sequenceNumber;  // 序列号（用于排序）
        private final long period;   // 周期（正数表示固定频率，负数表示固定延迟）

        private volatile boolean cancelled = false;
        private volatile boolean done = false;
        private volatile int state = 0;  // 0:NEW, 1:RUNNING, 2:DONE

        ScheduledFutureTask(Runnable r, V result, long ns, long sequenceNumber) {
            this.runnable = r;
            this.callable = null;
            this.time = ns;
            this.period = 0;
            this.sequenceNumber = sequenceNumber;
        }

        ScheduledFutureTask(Callable<V> c, long ns, long sequenceNumber) {
            this.runnable = null;
            this.callable = c;
            this.time = ns;
            this.period = 0;
            this.sequenceNumber = sequenceNumber;
        }

        ScheduledFutureTask(Runnable r, V result, long ns, long period, long sequenceNumber) {
            this.runnable = r;
            this.callable = null;
            this.time = ns;
            this.period = period;
            this.sequenceNumber = sequenceNumber;
        }

        @Override
        public boolean isPeriodic() {
            return period != 0;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(time - now(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            if (other == this) {
                return 0;
            }
            if (other instanceof ScheduledFutureTask) {
                ScheduledFutureTask<?> x = (ScheduledFutureTask<?>) other;
                long diff = time - x.time;
                if (diff < 0) {
                    return -1;
                } else if (diff > 0) {
                    return 1;
                } else if (sequenceNumber < x.sequenceNumber) {
                    return -1;
                } else {
                    return 1;
                }
            }
            long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
            return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
        }

        @Override
        public void run() {
            if (state != 0 || cancelled) {
                return;
            }
            state = 1;

            try {
                if (isPeriodic()) {
                    runPeriodic();
                } else {
                    runOnce();
                }
            } finally {
                state = 2;
                done = true;
            }
        }

        private void runOnce() {
            try {
                if (runnable != null) {
                    runnable.run();
                } else if (callable != null) {
                    result = callable.call();
                }
            } catch (Throwable t) {
                exception = t;
            }
        }

        private void runPeriodic() {
            boolean ok = false;
            try {
                runnable.run();
                ok = true;
            } catch (Throwable t) {
                exception = t;
            }

            if (ok) {
                // 重新调度
                long p = period;
                if (p > 0) {
                    // 固定频率
                    time += p;
                } else {
                    // 固定延迟
                    time = now() - p;
                }
                delayQueue.offer(this);
            }
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            if (done) {
                return false;
            }
            cancelled = true;
            delayQueue.remove(this);
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public boolean isDone() {
            return done;
        }

        @Override
        public V get() throws InterruptedException, ExecutionException {
            while (!done) {
                Thread.sleep(1);
            }
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return result;
        }

        @Override
        public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
            while (!done && System.currentTimeMillis() < deadline) {
                Thread.sleep(1);
            }
            if (!done) {
                throw new TimeoutException();
            }
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return result;
        }
    }

    /**
     * RunnableScheduledFuture接口
     */
    private interface RunnableScheduledFuture<V> extends RunnableFuture<V>, ScheduledFuture<V> {
        boolean isPeriodic();
    }
}
