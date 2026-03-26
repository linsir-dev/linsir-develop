package com.linsir.abc.core.base.time.local;

import java.time.*;

/**
 * 时间戳转换器
 * 演示Instant的使用，表示时间轴上的一个点
 *
 * 设计要点：
 * 1. Instant表示从1970-01-01T00:00:00Z开始的秒数和纳秒数
 * 2. 与时区无关
 * 3. 适合记录事件时间戳
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class InstantConverter {

    /**
     * 获取当前时间戳
     */
    public Instant now() {
        return Instant.now();
    }

    /**
     * 从毫秒创建Instant
     */
    public Instant fromEpochMilli(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli);
    }

    /**
     * 从秒创建Instant
     */
    public Instant fromEpochSecond(long epochSecond) {
        return Instant.ofEpochSecond(epochSecond);
    }

    /**
     * 转换为毫秒
     */
    public long toEpochMilli(Instant instant) {
        return instant.toEpochMilli();
    }

    /**
     * 转换为秒
     */
    public long toEpochSecond(Instant instant) {
        return instant.getEpochSecond();
    }

    /**
     * Instant转LocalDateTime（指定时区）
     */
    public LocalDateTime toLocalDateTime(Instant instant, ZoneId zone) {
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * LocalDateTime转Instant
     */
    public Instant fromLocalDateTime(LocalDateTime dateTime, ZoneId zone) {
        return dateTime.atZone(zone).toInstant();
    }

    /**
     * 演示Instant转换
     */
    public static void demonstrate() {
        System.out.println("=== Instant时间戳转换演示 ===\n");

        InstantConverter converter = new InstantConverter();

        // 当前时间戳
        Instant now = converter.now();
        System.out.println("当前Instant: " + now);
        System.out.println("  纪元秒: " + converter.toEpochSecond(now));
        System.out.println("  纪元毫秒: " + converter.toEpochMilli(now));

        // 从毫秒创建
        long currentMillis = System.currentTimeMillis();
        Instant fromMillis = converter.fromEpochMilli(currentMillis);
        System.out.println("\n从毫秒创建: " + fromMillis);

        // 时区转换
        System.out.println("\n时区转换:");
        LocalDateTime localBeijing = converter.toLocalDateTime(now, ZoneId.of("Asia/Shanghai"));
        LocalDateTime localNewYork = converter.toLocalDateTime(now, ZoneId.of("America/New_York"));
        System.out.println("  北京时间: " + localBeijing);
        System.out.println("  纽约时间: " + localNewYork);

        // 比较
        Instant later = now.plusSeconds(3600);
        System.out.println("\n1小时后: " + later);
        System.out.println("  是否在现在之后: " + later.isAfter(now));
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
