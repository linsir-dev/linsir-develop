package com.linsir.abc.core.base.time.temporal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;

/**
 * TemporalAdjusterImplementation测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class TemporalAdjusterImplementationTest {

    @Test
    public void testNextWorkDayFromMonday() {
        LocalDate monday = LocalDate.of(2026, 3, 23); // Monday
        TemporalAdjuster adjuster = TemporalAdjusterImplementation.nextWorkDay();

        LocalDate result = monday.with(adjuster);

        assertEquals(DayOfWeek.TUESDAY, result.getDayOfWeek());
        assertEquals(24, result.getDayOfMonth());
    }

    @Test
    public void testNextWorkDayFromFriday() {
        LocalDate friday = LocalDate.of(2026, 3, 27); // Friday
        TemporalAdjuster adjuster = TemporalAdjusterImplementation.nextWorkDay();

        LocalDate result = friday.with(adjuster);

        assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());
        assertEquals(30, result.getDayOfMonth()); // Skip weekend
    }

    @Test
    public void testNextWorkDayFromSaturday() {
        LocalDate saturday = LocalDate.of(2026, 3, 28); // Saturday
        TemporalAdjuster adjuster = TemporalAdjusterImplementation.nextWorkDay();

        LocalDate result = saturday.with(adjuster);

        assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());
        assertEquals(30, result.getDayOfMonth());
    }

    @Test
    public void testFirstDayOfMonth() {
        LocalDate date = LocalDate.of(2026, 3, 15);
        TemporalAdjuster adjuster = TemporalAdjusterImplementation.firstDayOfMonth();

        LocalDate result = date.with(adjuster);

        assertEquals(2026, result.getYear());
        assertEquals(3, result.getMonthValue());
        assertEquals(1, result.getDayOfMonth());
    }

    @Test
    public void testLastDayOfMonth() {
        LocalDate date = LocalDate.of(2026, 3, 15);
        TemporalAdjuster adjuster = TemporalAdjusterImplementation.lastDayOfMonth();

        LocalDate result = date.with(adjuster);

        assertEquals(2026, result.getYear());
        assertEquals(3, result.getMonthValue());
        assertEquals(31, result.getDayOfMonth()); // March has 31 days
    }

    @Test
    public void testNextMonday() {
        LocalDate wednesday = LocalDate.of(2026, 3, 25); // Wednesday
        TemporalAdjuster adjuster = TemporalAdjusterImplementation.nextMonday();

        LocalDate result = wednesday.with(adjuster);

        assertEquals(DayOfWeek.MONDAY, result.getDayOfWeek());
        assertTrue(result.isAfter(wednesday));
    }

    @Test
    public void testFirstFridayOfMonth() {
        LocalDate date = LocalDate.of(2026, 3, 15);
        TemporalAdjuster adjuster = TemporalAdjusterImplementation.firstFridayOfMonth();

        LocalDate result = date.with(adjuster);

        assertEquals(DayOfWeek.FRIDAY, result.getDayOfWeek());
        assertEquals(2026, result.getYear());
        assertEquals(3, result.getMonthValue());
        assertEquals(6, result.getDayOfMonth()); // First Friday of March 2026
    }

    @Test
    public void testAdjustersNotNull() {
        assertNotNull(TemporalAdjusterImplementation.nextWorkDay());
        assertNotNull(TemporalAdjusterImplementation.firstDayOfMonth());
        assertNotNull(TemporalAdjusterImplementation.lastDayOfMonth());
        assertNotNull(TemporalAdjusterImplementation.nextMonday());
        assertNotNull(TemporalAdjusterImplementation.firstFridayOfMonth());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> TemporalAdjusterImplementation.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> TemporalAdjusterImplementation.main(new String[]{}));
    }
}
