package com.linsir.abc.core.base.time.temporal;

import java.time.*;
import java.time.temporal.*;

/**
 * 持续时间计算器
 * 演示Duration的使用，表示时间间隔
 *
 * 设计要点：
 * 1. Duration表示以秒和纳秒为单位的时间间隔
 * 2. 适合表示短时间间隔（如任务执行时间）
 * 3. 与Period不同，Duration包含时间信息
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class DurationCalculator {

    /**
     * 计算两个时间点的间隔
     */
    public Duration between(LocalTime start, LocalTime end) {
        return Duration.between(start, end);
    }

    public Duration betweenInstant(Instant start, Instant end) {
        return Duration.between(start, end);
    }

    /**
     * 创建Duration
     */
    public Duration ofHours(long hours) {
        return Duration.ofHours(hours);
    }

    public Duration ofMinutes(long minutes) {
        return Duration.ofMinutes(minutes);
    }

    public Duration ofSeconds(long seconds) {
        return Duration.ofSeconds(seconds);
    }

    public Duration ofMillis(long millis) {
        return Duration.ofMillis(millis);
    }

    /**
     * Duration加减
     */
    public Duration plus(Duration d1, Duration d2) {
        return d1.plus(d2);
    }

    public Duration minus(Duration d1, Duration d2) {
        return d1.minus(d2);
    }

    /**
     * 转换为其他单位
     */
    public long toHours(Duration duration) {
        return duration.toHours();
    }

    public long toMinutes(Duration duration) {
        return duration.toMinutes();
    }

    public long toSeconds(Duration duration) {
        return duration.getSeconds();
    }

    public long toMillis(Duration duration) {
        return duration.toMillis();
    }

    /**
     * 测量任务执行时间
     */
    public static <T> T measureExecutionTime(Task<T> task) {
        Instant start = Instant.now();
        T result = task.execute();
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("任务执行时间: " + duration.toMillis() + " ms");
        return result;
    }

    @FunctionalInterface
    public interface Task<T> {
        T execute();
    }

    /**
     * 演示Duration计算
     */
    public static void demonstrate() {
        System.out.println("=== Duration持续时间计算演示 ===\n");

        DurationCalculator calculator = new DurationCalculator();

        // 创建Duration
        System.out.println("创建Duration:");
        Duration twoHours = calculator.ofHours(2);
        Duration thirtyMinutes = calculator.ofMinutes(30);
        System.out.println("  2小时: " + twoHours);
        System.out.println("  30分钟: " + thirtyMinutes);

        // 加减
        System.out.println("\n加减操作:");
        Duration sum = calculator.plus(twoHours, thirtyMinutes);
        System.out.println("  2小时 + 30分钟 = " + sum);
        System.out.println("  转换为分钟: " + calculator.toMinutes(sum));

        // 计算时间差
        System.out.println("\n计算时间差:");
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 30);
        Duration workDuration = calculator.between(start, end);
        System.out.println("  工作时间: " + start + " 到 " + end);
        System.out.println("  时长: " + workDuration);
        System.out.println("  小时数: " + calculator.toHours(workDuration));

        // 测量任务执行时间
        System.out.println("\n测量任务执行时间:");
        measureExecutionTime(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "任务完成";
        });
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
