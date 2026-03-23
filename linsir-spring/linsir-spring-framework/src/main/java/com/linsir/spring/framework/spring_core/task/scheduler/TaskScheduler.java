package com.linsir.spring.framework.spring_core.task.scheduler;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * 任务调度器接口
 *
 * 定义任务调度的标准接口，支持延迟执行、固定频率执行、固定延迟执行等多种调度模式。
 * 是 Spring 定时任务调度的基础接口。
 *
 * @author linsir
 * @since 1.0.0
 */
public interface TaskScheduler {

    /**
     * 在指定时间执行任务
     *
     * @param task 要执行的任务
     * @param startTime 开始执行的时间
     * @return ScheduledFuture 对象，可用于取消任务
     */
    ScheduledFuture<?> schedule(Runnable task, Date startTime);

    /**
     * 延迟指定时间后执行任务
     *
     * @param task 要执行的任务
     * @param delayMillis 延迟时间（毫秒）
     * @return ScheduledFuture 对象
     */
    ScheduledFuture<?> scheduleWithDelay(Runnable task, long delayMillis);

    /**
     * 按固定频率执行任务
     *
     * @param task 要执行的任务
     * @param periodMillis 执行周期（毫秒）
     * @return ScheduledFuture 对象
     */
    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long periodMillis);

    /**
     * 按固定频率执行任务，从指定时间开始
     *
     * @param task 要执行的任务
     * @param startTime 开始时间
     * @param periodMillis 执行周期（毫秒）
     * @return ScheduledFuture 对象
     */
    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long periodMillis);

    /**
     * 按固定延迟执行任务（上次执行完成后等待指定时间再执行）
     *
     * @param task 要执行的任务
     * @param delayMillis 延迟时间（毫秒）
     * @return ScheduledFuture 对象
     */
    ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delayMillis);

    /**
     * 按固定延迟执行任务，从指定时间开始
     *
     * @param task 要执行的任务
     * @param startTime 开始时间
     * @param delayMillis 延迟时间（毫秒）
     * @return ScheduledFuture 对象
     */
    ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delayMillis);

    /**
     * 立即执行一次任务
     *
     * @param task 要执行的任务
     * @return ScheduledFuture 对象
     */
    ScheduledFuture<?> executeImmediately(Runnable task);

    /**
     * 关闭调度器
     */
    void shutdown();

    /**
     * 检查调度器是否已关闭
     *
     * @return true 如果调度器已关闭
     */
    boolean isShutdown();
}
