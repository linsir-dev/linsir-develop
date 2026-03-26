package com.linsir.abc.core.base.time.temporal;

import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DurationCalculator 测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class DurationCalculatorTest {

    private DurationCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DurationCalculator();
    }

    @Test
    @DisplayName("测试创建Duration")
    void testCreateDuration() {
        Duration ofHours = calculator.ofHours(2);
        assertEquals(2, ofHours.toHours());

        Duration ofMinutes = calculator.ofMinutes(30);
        assertEquals(30, ofMinutes.toMinutes());

        Duration ofSeconds = calculator.ofSeconds(90);
        assertEquals(90, ofSeconds.getSeconds());

        Duration ofMillis = calculator.ofMillis(500);
        assertEquals(500, ofMillis.toMillis());
    }

    @Test
    @DisplayName("测试计算两个时间之间的Duration")
    void testBetween() {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(14, 30);

        Duration duration = calculator.between(start, end);

        assertEquals(4, duration.toHours());
        assertEquals(270, duration.toMinutes());
    }

    @Test
    @DisplayName("测试计算两个Instant之间的Duration")
    void testBetweenInstant() {
        Instant start = Instant.parse("2024-03-15T10:00:00Z");
        Instant end = Instant.parse("2024-03-15T10:30:00Z");

        Duration duration = calculator.betweenInstant(start, end);

        assertEquals(30, duration.toMinutes());
    }

    @Test
    @DisplayName("测试Duration加法")
    void testPlus() {
        Duration d1 = Duration.ofMinutes(30);
        Duration d2 = Duration.ofMinutes(15);

        Duration result = calculator.plus(d1, d2);

        assertEquals(45, result.toMinutes());
    }

    @Test
    @DisplayName("测试Duration减法")
    void testMinus() {
        Duration d1 = Duration.ofHours(2);
        Duration d2 = Duration.ofMinutes(30);

        Duration result = calculator.minus(d1, d2);

        assertEquals(90, result.toMinutes());
    }

    @Test
    @DisplayName("测试转换为不同单位")
    void testToUnits() {
        Duration duration = Duration.ofHours(2).plusMinutes(30).plusSeconds(45);

        assertEquals(2, calculator.toHours(duration));
        assertEquals(150, calculator.toMinutes(duration));
        assertEquals(9045, calculator.toSeconds(duration));
        assertEquals(9045000, calculator.toMillis(duration));
    }

    @Test
    @DisplayName("测试测量任务执行时间")
    void testMeasureExecutionTime() {
        String result = DurationCalculator.measureExecutionTime(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Task completed";
        });

        assertEquals("Task completed", result);
    }

    @Test
    @DisplayName("测试Duration比较")
    void testCompare() {
        Duration d1 = Duration.ofMinutes(30);
        Duration d2 = Duration.ofHours(1);
        Duration d3 = Duration.ofMinutes(30);

        assertTrue(d1.compareTo(d2) < 0);
        assertTrue(d2.compareTo(d1) > 0);
        assertEquals(0, d1.compareTo(d3));
    }

    @Test
    @DisplayName("测试Duration为零或负")
    void testZeroAndNegative() {
        Duration zero = Duration.ZERO;
        assertTrue(zero.isZero());

        Duration negative = Duration.ofMinutes(-30);
        assertTrue(negative.isNegative());
    }
}
