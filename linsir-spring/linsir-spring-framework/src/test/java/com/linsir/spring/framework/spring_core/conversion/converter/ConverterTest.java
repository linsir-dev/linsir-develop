package com.linsir.spring.framework.spring_core.conversion.converter;

import com.linsir.spring.framework.spring_core.conversion.support.StringToNumberConverterFactory;
import com.linsir.spring.framework.spring_core.conversion.support.StringToUserConverter;
import com.linsir.spring.framework.spring_core.conversion.support.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 转换器测试类
 * 测试 Converter 接口的各种实现
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("Converter 测试")
public class ConverterTest {

    @Test
    @DisplayName("测试字符串转用户对象转换器")
    void testStringToUserConverter() {
        StringToUserConverter converter = new StringToUserConverter();

        // 测试完整格式
        User user1 = converter.convert("zhangsan,25,zhangsan@example.com");
        assertNotNull(user1);
        assertEquals("zhangsan", user1.getName());
        assertEquals(Integer.valueOf(25), user1.getAge());
        assertEquals("zhangsan@example.com", user1.getEmail());

        // 测试简化格式
        User user2 = converter.convert("lisi,30");
        assertNotNull(user2);
        assertEquals("lisi", user2.getName());
        assertEquals(Integer.valueOf(30), user2.getAge());
        assertNull(user2.getEmail());
    }

    @Test
    @DisplayName("测试字符串转用户对象转换器 - 空值处理")
    void testStringToUserConverterNull() {
        StringToUserConverter converter = new StringToUserConverter();

        assertNull(converter.convert(null));
        assertNull(converter.convert(""));
    }

    @Test
    @DisplayName("测试字符串转用户对象转换器 - 无效格式")
    void testStringToUserConverterInvalidFormat() {
        StringToUserConverter converter = new StringToUserConverter();

        assertThrows(IllegalArgumentException.class, () -> {
            converter.convert("zhangsan"); // 缺少年龄
        });
    }

    @Test
    @DisplayName("测试字符串转数字转换器工厂 - Integer")
    void testStringToNumberConverterFactoryInteger() {
        StringToNumberConverterFactory factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.getConverter(Integer.class);

        assertEquals(Integer.valueOf(42), converter.convert("42"));
        assertEquals(Integer.valueOf(-100), converter.convert("-100"));
        assertEquals(Integer.valueOf(0), converter.convert("0"));
    }

    @Test
    @DisplayName("测试字符串转数字转换器工厂 - Long")
    void testStringToNumberConverterFactoryLong() {
        StringToNumberConverterFactory factory = new StringToNumberConverterFactory();
        Converter<String, Long> converter = factory.getConverter(Long.class);

        assertEquals(Long.valueOf(9999999999L), converter.convert("9999999999"));
        assertEquals(Long.valueOf(-1L), converter.convert("-1"));
    }

    @Test
    @DisplayName("测试字符串转数字转换器工厂 - Double")
    void testStringToNumberConverterFactoryDouble() {
        StringToNumberConverterFactory factory = new StringToNumberConverterFactory();
        Converter<String, Double> converter = factory.getConverter(Double.class);

        assertEquals(Double.valueOf(3.14159), converter.convert("3.14159"));
        assertEquals(Double.valueOf(-0.001), converter.convert("-0.001"));
    }

    @Test
    @DisplayName("测试字符串转数字转换器工厂 - Float")
    void testStringToNumberConverterFactoryFloat() {
        StringToNumberConverterFactory factory = new StringToNumberConverterFactory();
        Converter<String, Float> converter = factory.getConverter(Float.class);

        assertEquals(Float.valueOf(2.5f), converter.convert("2.5"));
    }

    @Test
    @DisplayName("测试字符串转数字转换器工厂 - BigDecimal")
    void testStringToNumberConverterFactoryBigDecimal() {
        StringToNumberConverterFactory factory = new StringToNumberConverterFactory();
        Converter<String, BigDecimal> converter = factory.getConverter(BigDecimal.class);

        assertEquals(new BigDecimal("12345678901234567890.123456789"),
                converter.convert("12345678901234567890.123456789"));
    }

    @Test
    @DisplayName("测试字符串转数字转换器工厂 - BigInteger")
    void testStringToNumberConverterFactoryBigInteger() {
        StringToNumberConverterFactory factory = new StringToNumberConverterFactory();
        Converter<String, BigInteger> converter = factory.getConverter(BigInteger.class);

        assertEquals(new BigInteger("12345678901234567890"),
                converter.convert("12345678901234567890"));
    }

    @Test
    @DisplayName("测试字符串转数字转换器工厂 - 空值处理")
    void testStringToNumberConverterFactoryNull() {
        StringToNumberConverterFactory factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.getConverter(Integer.class);

        assertNull(converter.convert(null));
        assertNull(converter.convert(""));
    }

    @Test
    @DisplayName("测试字符串转数字转换器工厂 - 无效数字")
    void testStringToNumberConverterFactoryInvalid() {
        StringToNumberConverterFactory factory = new StringToNumberConverterFactory();
        Converter<String, Integer> converter = factory.getConverter(Integer.class);

        assertThrows(NumberFormatException.class, () -> {
            converter.convert("not-a-number");
        });
    }

    @Test
    @DisplayName("测试 Lambda 转换器")
    void testLambdaConverter() {
        Converter<String, String> trimConverter = String::trim;
        assertEquals("hello", trimConverter.convert("  hello  "));

        Converter<String, Integer> lengthConverter = s -> s != null ? s.length() : 0;
        assertEquals(Integer.valueOf(5), lengthConverter.convert("hello"));
        assertEquals(Integer.valueOf(0), lengthConverter.convert(null));
    }

    @Test
    @DisplayName("测试自定义转换器 - 字符串反转")
    void testCustomReverseConverter() {
        Converter<String, String> reverseConverter = s -> {
            if (s == null) return null;
            return new StringBuilder(s).reverse().toString();
        };

        assertEquals("olleh", reverseConverter.convert("hello"));
        assertEquals("54321", reverseConverter.convert("12345"));
        assertNull(reverseConverter.convert(null));
    }
}
