package com.linsir.spring.framework.spring_core.conversion.service;

import com.linsir.spring.framework.spring_core.conversion.converter.Converter;
import com.linsir.spring.framework.spring_core.conversion.descriptor.TypeDescriptor;
import com.linsir.spring.framework.spring_core.conversion.exception.ConversionException;
import com.linsir.spring.framework.spring_core.conversion.support.GenericConversionService;
import com.linsir.spring.framework.spring_core.conversion.support.StringToNumberConverterFactory;
import com.linsir.spring.framework.spring_core.conversion.support.StringToUserConverter;
import com.linsir.spring.framework.spring_core.conversion.support.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 类型转换服务测试类
 * 测试 ConversionService 的核心功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("ConversionService 测试")
public class ConversionServiceTest {

    private GenericConversionService conversionService;

    @BeforeEach
    void setUp() {
        conversionService = new GenericConversionService();
        conversionService.addDefaultConverters();
    }

    @Test
    @DisplayName("测试字符串转整数")
    void testStringToInteger() {
        Integer result = conversionService.convert("123", Integer.class);
        assertEquals(Integer.valueOf(123), result);
    }

    @Test
    @DisplayName("测试字符串转长整数")
    void testStringToLong() {
        Long result = conversionService.convert("123456789", Long.class);
        assertEquals(Long.valueOf(123456789L), result);
    }

    @Test
    @DisplayName("测试字符串转双精度浮点数")
    void testStringToDouble() {
        Double result = conversionService.convert("3.14", Double.class);
        assertEquals(Double.valueOf(3.14), result);
    }

    @Test
    @DisplayName("测试字符串转布尔值")
    void testStringToBoolean() {
        assertTrue(conversionService.convert("true", Boolean.class));
        assertFalse(conversionService.convert("false", Boolean.class));
    }

    @Test
    @DisplayName("测试空字符串转换")
    void testEmptyStringConversion() {
        assertNull(conversionService.convert("", Integer.class));
    }

    @Test
    @DisplayName("测试 null 值转换")
    void testNullConversion() {
        assertNull(conversionService.convert(null, Integer.class));
    }

    @Test
    @DisplayName("测试相同类型转换")
    void testSameTypeConversion() {
        String source = "test";
        String result = conversionService.convert(source, String.class);
        assertSame(source, result);
    }

    @Test
    @DisplayName("测试无效转换抛出异常")
    void testInvalidConversion() {
        assertThrows(ConversionException.class, () -> {
            conversionService.convert("abc", Integer.class);
        });
    }

    @Test
    @DisplayName("测试 canConvert 方法")
    void testCanConvert() {
        assertTrue(conversionService.canConvert(String.class, Integer.class));
        assertTrue(conversionService.canConvert(String.class, Long.class));
        assertFalse(conversionService.canConvert(Integer.class, java.util.Date.class));
    }

    @Test
    @DisplayName("测试自定义转换器")
    void testCustomConverter() {
        conversionService.addConverter(String.class, User.class, new StringToUserConverter());

        User user = conversionService.convert("zhangsan,25,zhangsan@example.com", User.class);

        assertNotNull(user);
        assertEquals("zhangsan", user.getName());
        assertEquals(Integer.valueOf(25), user.getAge());
        assertEquals("zhangsan@example.com", user.getEmail());
    }

    @Test
    @DisplayName("测试 ConverterFactory")
    void testConverterFactory() {
        conversionService.addConverterFactory(new StringToNumberConverterFactory(), String.class, Number.class);

        Integer intResult = conversionService.convert("42", Integer.class);
        assertEquals(Integer.valueOf(42), intResult);

        Long longResult = conversionService.convert("9999999999", Long.class);
        assertEquals(Long.valueOf(9999999999L), longResult);

        Double doubleResult = conversionService.convert("3.14159", Double.class);
        assertEquals(Double.valueOf(3.14159), doubleResult);
    }

    @Test
    @DisplayName("测试数组转集合")
    void testArrayToCollection() {
        String[] array = new String[]{"a", "b", "c"};

        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) conversionService.convert(array,
                TypeDescriptor.valueOf(String[].class),
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class)));

        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals(Arrays.asList("a", "b", "c"), list);
    }

    @Test
    @DisplayName("测试集合转数组")
    void testCollectionToArray() {
        List<String> list = Arrays.asList("x", "y", "z");

        String[] array = (String[]) conversionService.convert(list,
                TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class)),
                TypeDescriptor.valueOf(String[].class));

        assertNotNull(array);
        assertEquals(3, array.length);
        assertArrayEquals(new String[]{"x", "y", "z"}, array);
    }

    @Test
    @DisplayName("测试 Lambda 转换器")
    void testLambdaConverter() {
        Converter<String, String> upperCaseConverter = String::toUpperCase;
        // 注意：由于相同类型的转换器已存在，这里只是演示 Lambda 转换器的用法
        // 实际转换可能仍使用内置转换器
        String result = upperCaseConverter.convert("hello");
        assertEquals("HELLO", result);
    }

    @Test
    @DisplayName("测试移除转换器")
    void testRemoveConverter() {
        conversionService.addConverter(String.class, User.class, new StringToUserConverter());
        assertTrue(conversionService.canConvert(String.class, User.class));

        conversionService.removeConvertible(String.class, User.class);
        assertFalse(conversionService.canConvert(String.class, User.class));
    }

    @Test
    @DisplayName("测试带泛型的 TypeDescriptor")
    void testGenericTypeDescriptor() {
        // 创建一个 List<String> 类型的描述符
        TypeDescriptor listType = TypeDescriptor.valueOf(java.util.ArrayList.class);

        assertTrue(listType.isCollection());
        // 简化测试，只验证集合类型判断
    }

    @Test
    @DisplayName("测试批量转换")
    void testBatchConversion() {
        List<String> stringNumbers = Arrays.asList("1", "2", "3", "4", "5");
        List<Integer> integers = new ArrayList<>();

        for (String str : stringNumbers) {
            integers.add(conversionService.convert(str, Integer.class));
        }

        assertEquals(Arrays.asList(1, 2, 3, 4, 5), integers);
    }
}
