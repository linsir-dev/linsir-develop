package com.linsir.abc.core.base.time.format;

import java.time.*;
import java.time.format.*;

/**
 * ISO日期时间解析器
 * 演示ISO-8601标准的日期时间解析
 *
 * 设计要点：
 * 1. ISO-8601是国际标准的日期时间表示法
 * 2. 格式如：2026-03-26T14:30:00+08:00
 * 3. 支持带时区的日期时间
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class IsoDateTimeParser {

    /**
     * 解析ISO日期时间
     */
    public LocalDateTime parseLocalDateTime(String isoString) {
        return LocalDateTime.parse(isoString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * 解析带时区的日期时间
     */
    public ZonedDateTime parseZonedDateTime(String isoString) {
        return ZonedDateTime.parse(isoString, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    /**
     * 解析日期
     */
    public LocalDate parseDate(String isoString) {
        return LocalDate.parse(isoString, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 格式化为ISO字符串
     */
    public String format(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String formatWithZone(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    /**
     * 演示ISO解析
     */
    public static void demonstrate() {
        System.out.println("=== ISO日期时间解析演示 ===\n");

        IsoDateTimeParser parser = new IsoDateTimeParser();

        // 解析LocalDateTime
        String localIso = "2026-03-26T14:30:00";
        LocalDateTime local = parser.parseLocalDateTime(localIso);
        System.out.println("解析LocalDateTime: " + localIso + " -> " + local);

        // 解析带时区的日期时间
        String zonedIso = "2026-03-26T14:30:00+08:00[Asia/Shanghai]";
        try {
            ZonedDateTime zoned = parser.parseZonedDateTime(zonedIso);
            System.out.println("解析ZonedDateTime: " + zonedIso + " -> " + zoned);
        } catch (DateTimeParseException e) {
            System.out.println("解析ZonedDateTime: " + zonedIso);
            System.out.println("  注意：简化版不支持完整格式");
        }

        // 解析日期
        String dateIso = "2026-03-26";
        LocalDate date = parser.parseDate(dateIso);
        System.out.println("解析LocalDate: " + dateIso + " -> " + date);

        // 格式化
        System.out.println("\n格式化:");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("  LocalDateTime: " + parser.format(now));

        ZonedDateTime nowZoned = ZonedDateTime.now();
        System.out.println("  ZonedDateTime: " + parser.formatWithZone(nowZoned));
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
