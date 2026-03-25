package com.linsir.abc.core.base.lang.wrapper;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * WrapperTypeCache测试类
 */
public class WrapperTypeCacheTest {

    /**
     * 测试Integer缓存机制
     */
    @Test
    public void testIntegerCache() {
        // 在缓存范围内（-128 ~ 127）
        Integer a = 100;
        Integer b = 100;
        assertSame(a, b);

        // 超出缓存范围
        Integer c = 200;
        Integer d = 200;
        assertNotSame(c, d);

        // 使用equals比较
        assertEquals(c, d);
    }

    /**
     * 测试其他包装类缓存
     */
    @Test
    public void testOtherCaches() {
        // Byte缓存
        Byte byte1 = 100;
        Byte byte2 = 100;
        assertSame(byte1, byte2);

        // Short缓存
        Short short1 = 100;
        Short short2 = 100;
        assertSame(short1, short2);

        // Long缓存
        Long long1 = 100L;
        Long long2 = 100L;
        assertSame(long1, long2);

        // Character缓存
        Character char1 = 'a';
        Character char2 = 'a';
        assertSame(char1, char2);

        // Boolean缓存
        Boolean bool1 = true;
        Boolean bool2 = true;
        assertSame(bool1, bool2);
    }

    /**
     * 测试自动装箱拆箱
     */
    @Test
    public void testAutoBoxing() {
        // 自动装箱
        Integer boxed = 100;
        assertEquals(Integer.valueOf(100), boxed);

        // 自动拆箱
        int unboxed = boxed;
        assertEquals(100, unboxed);

        // 表达式中的自动装箱拆箱
        Integer result = boxed + 50;
        assertEquals(Integer.valueOf(150), result);
    }

    /**
     * 测试比较陷阱
     */
    @Test
    public void testComparisonTraps() {
        // == vs equals（缓存范围内）
        Integer a = 100;
        Integer b = 100;
        assertTrue(a == b);

        // == vs equals（缓存范围外）
        Integer c = 200;
        Integer d = 200;
        assertFalse(c == d);
        assertTrue(c.equals(d));
    }

    /**
     * 测试包装类方法
     */
    @Test
    public void testWrapperMethods() {
        // 解析方法
        int parsed = Integer.parseInt("100");
        assertEquals(100, parsed);

        // 类型转换
        Integer num = 100;
        assertEquals(100L, num.longValue());
        assertEquals(100.0f, num.floatValue(), 0.001);
        assertEquals(100.0, num.doubleValue(), 0.001);

        // 比较方法
        assertEquals(-1, Integer.compare(100, 200));
        assertEquals(0, Integer.compare(100, 100));
        assertEquals(1, Integer.compare(200, 100));
    }

    /**
     * 测试自定义缓存
     */
    @Test
    public void testCustomCache() {
        Integer a = WrapperTypeCache.CustomIntegerCache.valueOf(100);
        Integer b = WrapperTypeCache.CustomIntegerCache.valueOf(100);
        assertSame(a, b);

        // 超出缓存范围
        Integer c = WrapperTypeCache.CustomIntegerCache.valueOf(200);
        Integer d = WrapperTypeCache.CustomIntegerCache.valueOf(200);
        assertNotSame(c, d);
    }
}
