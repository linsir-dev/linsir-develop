package com.linsir.abc.core.base.time.temporal;

import java.time.*;
import java.time.temporal.*;

/**
 * 时间调整器实现
 * 演示TemporalAdjuster的使用
 *
 * 设计要点：
 * 1. TemporalAdjuster用于调整日期时间
 * 2. 内置了多种常用调整器
 * 3. 可以自定义调整逻辑
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class TemporalAdjusterImplementation {

    /**
     * 调整到下一个工作日
     */
    public static TemporalAdjuster nextWorkDay() {
        return temporal -> {
            DayOfWeek dayOfWeek = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int daysToAdd;
            switch (dayOfWeek) {
                case FRIDAY: daysToAdd = 3; break;
                case SATURDAY: daysToAdd = 2; break;
                default: daysToAdd = 1; break;
            }
            return temporal.plus(daysToAdd, ChronoUnit.DAYS);
        };
    }

    /**
     * 调整到本月第一天
     */
    public static TemporalAdjuster firstDayOfMonth() {
        return TemporalAdjusters.firstDayOfMonth();
    }

    /**
     * 调整到本月最后一天
     */
    public static TemporalAdjuster lastDayOfMonth() {
        return TemporalAdjusters.lastDayOfMonth();
    }

    /**
     * 调整到下一个周一
     */
    public static TemporalAdjuster nextMonday() {
        return TemporalAdjusters.next(DayOfWeek.MONDAY);
    }

    /**
     * 调整到本月第一个周五
     */
    public static TemporalAdjuster firstFridayOfMonth() {
        return TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.FRIDAY);
    }

    /**
     * 演示时间调整
     */
    public static void demonstrate() {
        System.out.println("=== TemporalAdjuster时间调整演示 ===\n");

        LocalDate today = LocalDate.now();
        System.out.println("今天: " + today + " (" + today.getDayOfWeek() + ")");

        // 内置调整器
        System.out.println("\n内置调整器:");
        System.out.println("  本月第一天: " + today.with(firstDayOfMonth()));
        System.out.println("  本月最后一天: " + today.with(lastDayOfMonth()));
        System.out.println("  下一个周一: " + today.with(nextMonday()));
        System.out.println("  本月第一个周五: " + today.with(firstFridayOfMonth()));

        // 自定义调整器
        System.out.println("\n自定义调整器 (下一个工作日):");
        LocalDate friday = today.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        System.out.println("  如果今天是周五: " + friday + " -> 下一个工作日: " + friday.with(nextWorkDay()));

        LocalDate saturday = today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        System.out.println("  如果今天是周六: " + saturday + " -> 下一个工作日: " + saturday.with(nextWorkDay()));
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
