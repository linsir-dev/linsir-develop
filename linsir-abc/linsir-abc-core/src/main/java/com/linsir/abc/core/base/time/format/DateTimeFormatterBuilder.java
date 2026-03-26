package com.linsir.abc.core.base.time.format;

import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * 日期时间格式化器构建器
 * 演示DateTimeFormatter的使用
 *
 * 设计要点：
 * 1. DateTimeFormatter是线程安全的
 * 2. 支持自定义格式化模式
 * 3. 支持本地化
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class DateTimeFormatterBuilder {

    /**
     * 预定义格式化器
     */
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter BASIC_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter CHINESE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分ss秒");

    /**
     * 格式化日期时间
     */
    public String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime.format(formatter);
    }

    /**
     * 解析日期时间
     */
    public LocalDateTime parse(String text, DateTimeFormatter formatter) {
        return LocalDateTime.parse(text, formatter);
    }

    /**
     * 创建自定义格式化器
     */
    public DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern);
    }

    /**
     * 创建本地化格式化器
     */
    public DateTimeFormatter createLocalizedFormatter(FormatStyle dateStyle, FormatStyle timeStyle, Locale locale) {
        return DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle).withLocale(locale);
    }

    /**
     * 演示格式化
     */
    public static void demonstrate() {
        System.out.println("=== DateTimeFormatter格式化演示 ===\n");

        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        LocalDateTime now = LocalDateTime.now();

        // 使用预定义格式化器
        System.out.println("预定义格式化器:");
        System.out.println("  ISO: " + builder.format(now, ISO_FORMATTER));
        System.out.println("  BASIC: " + builder.format(now, BASIC_FORMATTER));
        System.out.println("  CHINESE: " + builder.format(now, CHINESE_FORMATTER));

        // 自定义格式化器
        System.out.println("\n自定义格式化器:");
        DateTimeFormatter custom = builder.createFormatter("yyyy/MM/dd HH:mm");
        System.out.println("  Custom: " + builder.format(now, custom));

        // 本地化格式化
        System.out.println("\n本地化格式化:");
        DateTimeFormatter usFormatter = builder.createLocalizedFormatter(
                FormatStyle.MEDIUM, FormatStyle.SHORT, Locale.US);
        System.out.println("  US: " + builder.format(now, usFormatter));

        // 解析
        System.out.println("\n解析:");
        String text = "2026-03-26 14:30:00";
        LocalDateTime parsed = builder.parse(text, BASIC_FORMATTER);
        System.out.println("  解析: " + text + " -> " + parsed);
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
