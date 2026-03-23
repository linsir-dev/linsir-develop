package com.linsir.spring.framework.spring_core.conversion.formatter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 格式化器测试类
 * 测试 Formatter 接口的实现
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("Formatter 测试")
public class FormatterTest {

    @Test
    @DisplayName("测试日期格式化器 - 基本功能")
    void testDateFormatterBasic() throws ParseException {
        DateFormatter formatter = new DateFormatter("yyyy-MM-dd");
        Locale locale = Locale.getDefault();

        // 测试解析
        Date date = formatter.parse("2026-03-23", locale);
        assertNotNull(date);

        // 测试格式化
        String formatted = formatter.print(date, locale);
        assertEquals("2026-03-23", formatted);
    }

    @Test
    @DisplayName("测试日期格式化器 - 不同格式模式")
    void testDateFormatterDifferentPatterns() throws ParseException {
        // 测试日期时间格式
        DateFormatter formatter = new DateFormatter("yyyy-MM-dd HH:mm:ss");
        Locale locale = Locale.getDefault();

        Date date = formatter.parse("2026-03-23 15:30:45", locale);
        String formatted = formatter.print(date, locale);
        assertEquals("2026-03-23 15:30:45", formatted);
    }

    @Test
    @DisplayName("测试日期格式化器 - 空值处理")
    void testDateFormatterNullHandling() throws ParseException {
        DateFormatter formatter = new DateFormatter("yyyy-MM-dd");
        Locale locale = Locale.getDefault();

        // 空字符串解析应返回 null
        Date parsed = formatter.parse("", locale);
        assertNull(parsed);

        // null 对象格式化应返回空字符串
        String formatted = formatter.print(null, locale);
        assertEquals("", formatted);
    }

    @Test
    @DisplayName("测试日期格式化器 - 无效格式抛出异常")
    void testDateFormatterInvalidFormat() {
        DateFormatter formatter = new DateFormatter("yyyy-MM-dd");
        Locale locale = Locale.getDefault();

        assertThrows(ParseException.class, () -> {
            formatter.parse("invalid-date", locale);
        });
    }

    @Test
    @DisplayName("测试日期格式化器 - 获取模式")
    void testDateFormatterGetPattern() {
        String pattern = "yyyy/MM/dd";
        DateFormatter formatter = new DateFormatter(pattern);
        assertEquals(pattern, formatter.getPattern());
    }

    @Test
    @DisplayName("测试日期格式化器 - 空模式抛出异常")
    void testDateFormatterEmptyPattern() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DateFormatter("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new DateFormatter(null);
        });
    }

    @Test
    @DisplayName("测试日期格式化器 - 严格模式")
    void testDateFormatterStrictMode() {
        DateFormatter formatter = new DateFormatter("yyyy-MM-dd");
        Locale locale = Locale.getDefault();

        // 严格模式下，无效日期应该抛出异常
        assertThrows(ParseException.class, () -> {
            formatter.parse("2026-13-45", locale); // 无效月份和日期
        });
    }
}
