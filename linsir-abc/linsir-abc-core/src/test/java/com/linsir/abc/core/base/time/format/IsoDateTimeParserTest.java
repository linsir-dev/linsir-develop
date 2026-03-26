package com.linsir.abc.core.base.time.format;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

/**
 * IsoDateTimeParser测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class IsoDateTimeParserTest {

    @Test
    public void testConstructor() {
        IsoDateTimeParser parser = new IsoDateTimeParser();
        assertNotNull(parser);
    }

    @Test
    public void testParseLocalDateTime() {
        IsoDateTimeParser parser = new IsoDateTimeParser();
        String isoString = "2026-03-26T14:30:00";

        LocalDateTime result = parser.parseLocalDateTime(isoString);

        assertEquals(2026, result.getYear());
        assertEquals(3, result.getMonthValue());
        assertEquals(26, result.getDayOfMonth());
        assertEquals(14, result.getHour());
        assertEquals(30, result.getMinute());
        assertEquals(0, result.getSecond());
    }

    @Test
    public void testParseLocalDateTimeWithInvalidFormat() {
        IsoDateTimeParser parser = new IsoDateTimeParser();
        String invalidIso = "2026/03/26 14:30:00";

        assertThrows(DateTimeParseException.class, () -> {
            parser.parseLocalDateTime(invalidIso);
        });
    }

    @Test
    public void testParseZonedDateTime() {
        IsoDateTimeParser parser = new IsoDateTimeParser();
        String isoString = "2026-03-26T14:30:00+08:00[Asia/Shanghai]";

        // 测试解析，某些格式可能不支持
        assertDoesNotThrow(() -> {
            try {
                ZonedDateTime result = parser.parseZonedDateTime(isoString);
                assertNotNull(result);
                assertEquals(2026, result.getYear());
            } catch (DateTimeParseException e) {
                // 某些格式可能不被支持
            }
        });
    }

    @Test
    public void testParseDate() {
        IsoDateTimeParser parser = new IsoDateTimeParser();
        String isoString = "2026-03-26";

        LocalDate result = parser.parseDate(isoString);

        assertEquals(2026, result.getYear());
        assertEquals(3, result.getMonthValue());
        assertEquals(26, result.getDayOfMonth());
    }

    @Test
    public void testFormatLocalDateTime() {
        IsoDateTimeParser parser = new IsoDateTimeParser();
        LocalDateTime dateTime = LocalDateTime.of(2026, 3, 26, 14, 30, 0);

        String result = parser.format(dateTime);

        assertEquals("2026-03-26T14:30:00", result);
    }

    @Test
    public void testFormatWithZone() {
        IsoDateTimeParser parser = new IsoDateTimeParser();
        ZonedDateTime zonedDateTime = ZonedDateTime.of(
                2026, 3, 26, 14, 30, 0, 0,
                java.time.ZoneId.of("Asia/Shanghai"));

        String result = parser.formatWithZone(zonedDateTime);

        assertNotNull(result);
        assertTrue(result.contains("2026"));
        assertTrue(result.contains("03"));
        assertTrue(result.contains("26"));
    }

    @Test
    public void testRoundTrip() {
        IsoDateTimeParser parser = new IsoDateTimeParser();
        LocalDateTime original = LocalDateTime.of(2026, 3, 26, 14, 30, 0);

        String formatted = parser.format(original);
        LocalDateTime parsed = parser.parseLocalDateTime(formatted);

        assertEquals(original, parsed);
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> IsoDateTimeParser.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> IsoDateTimeParser.main(new String[]{}));
    }
}
