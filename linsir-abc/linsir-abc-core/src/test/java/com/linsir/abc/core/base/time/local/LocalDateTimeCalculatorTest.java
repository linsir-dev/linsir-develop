package com.linsir.abc.core.base.time.local;

import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LocalDateTimeCalculator 测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class LocalDateTimeCalculatorTest {

    private LocalDateTimeCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new LocalDateTimeCalculator();
    }

    @Test
    @DisplayName("测试获取当前日期时间")
    void testNow() {
        LocalDateTime now = calculator.now();

        assertNotNull(now);
        assertTrue(now.isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(now.isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("测试日期时间加法")
    void testPlusOperations() {
        LocalDateTime base = LocalDateTime.of(2024, 1, 15, 10, 30);

        LocalDateTime plusDays = calculator.plusDays(base, 5);
        assertEquals(20, plusDays.getDayOfMonth());

        LocalDateTime plusYears = calculator.plusYears(base, 1);
        assertEquals(2025, plusYears.getYear());
    }

    @Test
    @DisplayName("测试日期时间减法")
    void testMinusOperations() {
        LocalDateTime base = LocalDateTime.of(2024, 3, 15, 10, 30);

        LocalDateTime minusMonths = calculator.minusMonths(base, 2);
        assertEquals(1, minusMonths.getMonthValue());
    }

    @Test
    @DisplayName("测试计算日期间隔")
    void testBetweenDates() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 3, 15);

        Period period = calculator.betweenDates(start, end);

        assertEquals(0, period.getYears());
        assertEquals(2, period.getMonths());
        assertEquals(14, period.getDays());
    }

    @Test
    @DisplayName("测试计算天数间隔")
    void testDaysBetween() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 15);

        long days = calculator.daysBetween(start, end);

        assertEquals(14, days);
    }

    @Test
    @DisplayName("测试计算小时间隔")
    void testHoursBetween() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 14, 30);

        long hours = calculator.hoursBetween(start, end);

        assertEquals(4, hours);
    }

    @Test
    @DisplayName("测试日期时间比较")
    void testCompareDateTimes() {
        LocalDateTime dt1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime dt2 = LocalDateTime.of(2024, 1, 2, 10, 0);

        assertTrue(calculator.isBefore(dt1, dt2));
        assertTrue(calculator.isAfter(dt2, dt1));
    }

    @Test
    @DisplayName("测试获取本月第一天")
    void testGetFirstDayOfMonth() {
        LocalDate date = LocalDate.of(2024, 3, 15);

        LocalDate firstDay = calculator.getFirstDayOfMonth(date);

        assertEquals(2024, firstDay.getYear());
        assertEquals(3, firstDay.getMonthValue());
        assertEquals(1, firstDay.getDayOfMonth());
    }

    @Test
    @DisplayName("测试获取本月最后一天")
    void testGetLastDayOfMonth() {
        LocalDate date = LocalDate.of(2024, 3, 15);

        LocalDate lastDay = calculator.getLastDayOfMonth(date);

        assertEquals(2024, lastDay.getYear());
        assertEquals(3, lastDay.getMonthValue());
        assertEquals(31, lastDay.getDayOfMonth());
    }

    @Test
    @DisplayName("测试获取本年第一天")
    void testGetFirstDayOfYear() {
        LocalDate date = LocalDate.of(2024, 3, 15);

        LocalDate firstDay = calculator.getFirstDayOfYear(date);

        assertEquals(2024, firstDay.getYear());
        assertEquals(1, firstDay.getMonthValue());
        assertEquals(1, firstDay.getDayOfMonth());
    }
}
