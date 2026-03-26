package com.linsir.abc.core.base.time.local;

import org.junit.jupiter.api.*;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InstantConverter 测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class InstantConverterTest {

    private InstantConverter converter;

    @BeforeEach
    void setUp() {
        converter = new InstantConverter();
    }

    @Test
    @DisplayName("测试获取当前Instant")
    void testNow() {
        Instant now = converter.now();

        assertNotNull(now);
        assertTrue(now.isBefore(Instant.now().plusMillis(100)));
        assertTrue(now.isAfter(Instant.now().minusMillis(100)));
    }

    @Test
    @DisplayName("测试Instant与毫秒时间戳转换")
    void testInstantToEpochMilli() {
        long currentMillis = System.currentTimeMillis();
        Instant instant = converter.fromEpochMilli(currentMillis);
        long convertedMillis = converter.toEpochMilli(instant);

        assertEquals(currentMillis, convertedMillis);
    }

    @Test
    @DisplayName("测试Instant与秒时间戳转换")
    void testInstantToEpochSecond() {
        long currentSeconds = System.currentTimeMillis() / 1000;
        Instant instant = converter.fromEpochSecond(currentSeconds);
        long convertedSeconds = converter.toEpochSecond(instant);

        assertEquals(currentSeconds, convertedSeconds);
    }

    @Test
    @DisplayName("测试Instant与LocalDateTime转换")
    void testInstantToLocalDateTime() {
        Instant instant = Instant.parse("2024-03-15T10:30:00Z");
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        LocalDateTime localDateTime = converter.toLocalDateTime(instant, zoneId);

        assertEquals(2024, localDateTime.getYear());
        assertEquals(3, localDateTime.getMonthValue());
        assertEquals(15, localDateTime.getDayOfMonth());
    }

    @Test
    @DisplayName("测试LocalDateTime与Instant转换")
    void testLocalDateTimeToInstant() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 3, 15, 18, 30);
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");

        Instant instant = converter.fromLocalDateTime(localDateTime, zoneId);
        LocalDateTime converted = converter.toLocalDateTime(instant, zoneId);

        assertEquals(localDateTime, converted);
    }

    @Test
    @DisplayName("测试不同时区的转换")
    void testDifferentTimeZones() {
        Instant instant = Instant.now();
        ZoneId beijingZone = ZoneId.of("Asia/Shanghai");
        ZoneId newYorkZone = ZoneId.of("America/New_York");

        LocalDateTime beijingTime = converter.toLocalDateTime(instant, beijingZone);
        LocalDateTime newYorkTime = converter.toLocalDateTime(instant, newYorkZone);

        // 北京和纽约时间应该不同
        assertNotEquals(beijingTime, newYorkTime);

        // 但转换回Instant应该相同
        Instant beijingInstant = converter.fromLocalDateTime(beijingTime, beijingZone);
        Instant newYorkInstant = converter.fromLocalDateTime(newYorkTime, newYorkZone);
        assertEquals(beijingInstant, newYorkInstant);
    }

    @Test
    @DisplayName("测试Instant比较")
    void testCompare() {
        Instant instant1 = Instant.parse("2024-03-15T10:00:00Z");
        Instant instant2 = Instant.parse("2024-03-15T11:00:00Z");

        assertTrue(instant1.isBefore(instant2));
        assertTrue(instant2.isAfter(instant1));
    }
}
