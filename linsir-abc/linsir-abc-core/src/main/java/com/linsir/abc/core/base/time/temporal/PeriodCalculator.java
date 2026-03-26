package com.linsir.abc.core.base.time.temporal;

import java.time.*;
import java.time.temporal.*;

/**
 * 日期间隔计算器
 * 演示Period的使用，表示日期间隔
 *
 * 设计要点：
 * 1. Period表示以年、月、日为单位的时间间隔
 * 2. 适合表示日期间隔（如年龄、工龄）
 * 3. 与Duration不同，Period包含日期信息
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class PeriodCalculator {

    /**
     * 计算两个日期的间隔
     */
    public Period between(LocalDate start, LocalDate end) {
        return Period.between(start, end);
    }

    /**
     * 创建Period
     */
    public Period ofYears(int years) {
        return Period.ofYears(years);
    }

    public Period ofMonths(int months) {
        return Period.ofMonths(months);
    }

    public Period ofDays(int days) {
        return Period.ofDays(days);
    }

    public Period of(int years, int months, int days) {
        return Period.of(years, months, days);
    }

    /**
     * Period加减
     */
    public Period plus(Period p1, Period p2) {
        return p1.plus(p2);
    }

    public Period minus(Period p1, Period p2) {
        return p1.minus(p2);
    }

    /**
     * 添加到日期
     */
    public LocalDate addTo(LocalDate date, Period period) {
        return date.plus(period);
    }

    public LocalDate subtractFrom(LocalDate date, Period period) {
        return date.minus(period);
    }

    /**
     * 计算年龄
     */
    public Period calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now());
    }

    /**
     * 演示Period计算
     */
    public static void demonstrate() {
        System.out.println("=== Period日期间隔计算演示 ===\n");

        PeriodCalculator calculator = new PeriodCalculator();

        // 创建Period
        System.out.println("创建Period:");
        Period twoYears = calculator.ofYears(2);
        Period sixMonths = calculator.ofMonths(6);
        Period fifteenDays = calculator.ofDays(15);
        System.out.println("  2年: " + twoYears);
        System.out.println("  6个月: " + sixMonths);
        System.out.println("  15天: " + fifteenDays);

        // 组合Period
        Period combined = calculator.of(2, 6, 15);
        System.out.println("  2年6个月15天: " + combined);

        // 计算日期间隔
        System.out.println("\n计算日期间隔:");
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2026, 3, 26);
        Period period = calculator.between(start, end);
        System.out.println("  从 " + start + " 到 " + end);
        System.out.println("  间隔: " + period.getYears() + "年 " + period.getMonths() + "月 " + period.getDays() + "天");

        // 计算年龄
        System.out.println("\n计算年龄:");
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        Period age = calculator.calculateAge(birthDate);
        System.out.println("  出生日期: " + birthDate);
        System.out.println("  年龄: " + age.getYears() + "岁 " + age.getMonths() + "个月 " + age.getDays() + "天");

        // 日期加减
        System.out.println("\n日期加减:");
        LocalDate today = LocalDate.now();
        LocalDate future = calculator.addTo(today, calculator.of(1, 0, 0));
        LocalDate past = calculator.subtractFrom(today, calculator.of(0, 6, 0));
        System.out.println("  今天: " + today);
        System.out.println("  1年后: " + future);
        System.out.println("  6个月前: " + past);
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
