package com.linsir.abc.core.base.time.format;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * DateTimeFormatterBuilder测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class DateTimeFormatterBuilderTest {

    @Test
    public void testConstructor() {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        assertNotNull(builder);
    }

    @Test
    public void testFormatWithIsoFormatter() {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 26, 14, 30, 0);

        String result = builder.format(dateTime, DateTimeFormatterBuilder.ISO_FORMATTER);

        assertNotNull(result);
        assertTrue(result.contains("2026"));
        assertTrue(result.contains("03"));
        assertTrue(result.contains("26"));
    }

    @Test
    public void testFormatWithBasicFormatter() {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 26, 14, 30, 0);

        String result = builder.format(dateTime, DateTimeFormatterBuilder.BASIC_FORMATTER);

        assertEquals("2026-03-26 14:30:00", result);
    }

    @Test
    public void testFormatWithChineseFormatter() {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 26, 14, 30, 0);

        String result = builder.format(dateTime, DateTimeFormatterBuilder.CHINESE_FORMATTER);

        assertTrue(result.contains("2026年"));
        assertTrue(result.contains("03月"));
        assertTrue(result.contains("26日"));
    }

    @Test
    public void testParse() {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        String text = "2026-03-26 14:30:00";

        LocalDateTime result = builder.parse(text, DateTimeFormatterBuilder.BASIC_FORMATTER);

        assertEquals(2026, result.getYear());
        assertEquals(3, result.getMonthValue());
        assertEquals(26, result.getDayOfMonth());
        assertEquals(14, result.getHour());
        assertEquals(30, result.getMinute());
        assertEquals(0, result.getSecond());
    }

    @Test
    public void testCreateFormatter() {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();

        DateTimeFormatter formatter = builder.createFormatter("yyyy/MM/dd HH:mm");
        assertNotNull(formatter);

        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 26, 14, 30, 0);
        String result = builder.format(dateTime, formatter);

        assertEquals("2026/03/26 14:30", result);
    }

    @Test
    public void testCreateLocalizedFormatter() {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();

        DateTimeFormatter formatter = builder.createLocalizedFormatter(
                FormatStyle.MEDIUM, FormatStyle.SHORT, Locale.US);
        assertNotNull(formatter);

        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 26, 14, 30, 0);
        String result = builder.format(dateTime, formatter);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testPredefinedFormatters() {
        assertNotNull(DateTimeFormatterBuilder.ISO_FORMATTER);
        assertNotNull(DateTimeFormatterBuilder.BASIC_FORMATTER);
        assertNotNull(DateTimeFormatterBuilder.CHINESE_FORMATTER);
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> DateTimeFormatterBuilder.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> DateTimeFormatterBuilder.main(new String[]{}));
    }
}
