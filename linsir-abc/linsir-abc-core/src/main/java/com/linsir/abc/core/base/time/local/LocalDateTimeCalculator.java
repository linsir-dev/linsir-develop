package com.linsir.abc.core.base.time.local;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

/**
 * 本地日期时间计算器
 * 演示LocalDate、LocalTime、LocalDateTime的使用
 *
 * 设计要点：
 * 1. LocalDateTime不包含时区信息
 * 2. 提供丰富的日期时间计算方法
 * 3. 不可变对象，线程安全
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class LocalDateTimeCalculator {

    /**
     * 获取当前日期时间
     */
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 日期时间加减
     */
    public LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }

    public LocalDateTime minusMonths(LocalDateTime dateTime, long months) {
        return dateTime.minusMonths(months);
    }

    public LocalDateTime plusYears(LocalDateTime dateTime, long years) {
        return dateTime.plusYears(years);
    }

    /**
     * 计算两个日期时间的差值
     */
    public Period betweenDates(LocalDate start, LocalDate end) {
        return Period.between(start, end);
    }

    public long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    public long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * 比较日期时间
     */
    public boolean isBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isBefore(dateTime2);
    }

    public boolean isAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isAfter(dateTime2);
    }

    /**
     * 获取特定日期
     */
    public LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    public LocalDate getLastDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    public LocalDate getFirstDayOfYear(LocalDate date) {
        return date.withDayOfYear(1);
    }

    /**
     * 演示日期时间计算
     */
    public static void demonstrate() {
        System.out.println("=== LocalDateTime计算演示 ===\n");

        LocalDateTimeCalculator calculator = new LocalDateTimeCalculator();

        // 当前时间
        LocalDateTime now = calculator.now();
        System.out.println("当前时间: " + now);

        // 加减操作
        System.out.println("\n加减操作:");
        System.out.println("  加10天: " + calculator.plusDays(now, 10));
        System.out.println("  减3个月: " + calculator.minusMonths(now, 3));
        System.out.println("  加2年: " + calculator.plusYears(now, 2));

        // 计算差值
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 12, 31);
        Period period = calculator.betweenDates(startDate, endDate);
        System.out.println("\n日期差值 (2026-01-01 到 2026-12-31):");
        System.out.println("  Period: " + period.getYears() + "年 " + period.getMonths() + "月 " + period.getDays() + "天");
        System.out.println("  总天数: " + calculator.daysBetween(startDate, endDate));

        // 特定日期
        LocalDate today = LocalDate.now();
        System.out.println("\n特定日期:");
        System.out.println("  本月第一天: " + calculator.getFirstDayOfMonth(today));
        System.out.println("  本月最后一天: " + calculator.getLastDayOfMonth(today));
        System.out.println("  本年第一天: " + calculator.getFirstDayOfYear(today));
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
