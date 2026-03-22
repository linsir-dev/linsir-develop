package com.linsir.spring.framework.spring_core.type_system.conversion;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ConversionService 核心接口测试
 */
public class ConversionServiceTest {

    @Test
    public void testConvertiblePairCreation() {
        ConvertiblePair pair = new ConvertiblePair(String.class, Integer.class);
        
        assertEquals(String.class, pair.getSourceType());
        assertEquals(Integer.class, pair.getTargetType());
    }

    @Test
    public void testConvertiblePairEquality() {
        ConvertiblePair pair1 = new ConvertiblePair(String.class, Integer.class);
        ConvertiblePair pair2 = new ConvertiblePair(String.class, Integer.class);
        ConvertiblePair pair3 = new ConvertiblePair(String.class, Long.class);
        
        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }

    @Test
    public void testConverterInterface() {
        // 测试 Converter 函数式接口
        Converter<String, Integer> converter = Integer::valueOf;
        
        Integer result = converter.convert("123");
        assertEquals(123, result);
    }

    @Test
    public void testStringToLongConversion() {
        Converter<String, Long> converter = Long::valueOf;
        
        Long result = converter.convert("9999999999");
        assertEquals(9999999999L, result);
    }

    @Test
    public void testStringToDoubleConversion() {
        Converter<String, Double> converter = Double::valueOf;
        
        Double result = converter.convert("3.14159");
        assertEquals(3.14159, result, 0.00001);
    }

    @Test
    public void testStringToBooleanConversion() {
        Converter<String, Boolean> converter = Boolean::valueOf;
        
        assertTrue(converter.convert("true"));
        assertFalse(converter.convert("false"));
        assertFalse(converter.convert(""));
    }
}
