package com.linsir.abc.core.base.time.temporal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Period;

/**
 * PeriodCalculator测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class PeriodCalculatorTest {

    @Test
    public void testConstructor() {
        PeriodCalculator calculator = new PeriodCalculator();
        assertNotNull(calculator);
    }

    @Test
    public void testBetween() {
        PeriodCalculator calculator = new PeriodCalculator();
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2026, 3, 26);

        Period result = calculator.between(start, end);

        assertEquals(6, result.getYears());
        assertEquals(2, result.getMonths());
        assertEquals(25, result.getDays());
    }

    @Test
    public void testOfYears() {
        PeriodCalculator calculator = new PeriodCalculator();

        Period result = calculator.ofYears(5);

        assertEquals(5, result.getYears());
        assertEquals(0, result.getMonths());
        assertEquals(0, result.getDays());
    }

    @Test
    public void testOfMonths() {
        PeriodCalculator calculator = new PeriodCalculator();

        Period result = calculator.ofMonths(6);

        assertEquals(0, result.getYears());
        assertEquals(6, result.getMonths());
        assertEquals(0, result.getDays());
    }

    @Test
    public void testOfDays() {
        PeriodCalculator calculator = new PeriodCalculator();

        Period result = calculator.ofDays(15);

        assertEquals(0, result.getYears());
        assertEquals(0, result.getMonths());
        assertEquals(15, result.getDays());
    }

    @Test
    public void testOf() {
        PeriodCalculator calculator = new PeriodCalculator();

        Period result = calculator.of(2, 6, 15);

        assertEquals(2, result.getYears());
        assertEquals(6, result.getMonths());
        assertEquals(15, result.getDays());
    }

    @Test
    public void testPlus() {
        PeriodCalculator calculator = new PeriodCalculator();
        Period p1 = calculator.of(1, 2, 3);
        Period p2 = calculator.of(2, 3, 4);

        Period result = calculator.plus(p1, p2);

        assertEquals(3, result.getYears());
        assertEquals(5, result.getMonths());
        assertEquals(7, result.getDays());
    }

    @Test
    public void testMinus() {
        PeriodCalculator calculator = new PeriodCalculator();
        Period p1 = calculator.of(5, 6, 10);
        Period p2 = calculator.of(2, 3, 5);

        Period result = calculator.minus(p1, p2);

        assertEquals(3, result.getYears());
        assertEquals(3, result.getMonths());
        assertEquals(5, result.getDays());
    }

    @Test
    public void testAddTo() {
        PeriodCalculator calculator = new PeriodCalculator();
        LocalDate date = LocalDate.of(2026, 3, 26);
        Period period = calculator.of(1, 2, 3);

        LocalDate result = calculator.addTo(date, period);

        assertEquals(2027, result.getYear());
        assertEquals(5, result.getMonthValue());
        assertEquals(29, result.getDayOfMonth());
    }

    @Test
    public void testSubtractFrom() {
        PeriodCalculator calculator = new PeriodCalculator();
        LocalDate date = LocalDate.of(2026, 3, 26);
        Period period = calculator.of(1, 2, 3);

        LocalDate result = calculator.subtractFrom(date, period);

        assertEquals(2025, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(23, result.getDayOfMonth());
    }

    @Test
    public void testCalculateAge() {
        PeriodCalculator calculator = new PeriodCalculator();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        Period age = calculator.calculateAge(birthDate);

        assertTrue(age.getYears() >= 35); // Should be at least 35 years
        assertTrue(age.isNegative() == false);
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> PeriodCalculator.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> PeriodCalculator.main(new String[]{}));
    }
}
