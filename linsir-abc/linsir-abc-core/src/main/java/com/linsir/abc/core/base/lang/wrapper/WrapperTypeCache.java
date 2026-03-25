package com.linsir.abc.core.base.lang.wrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 包装类缓存机制演示
 * 
 * 本类演示Java包装类的缓存机制：
 * 1. Integer缓存（-128 ~ 127）
 * 2. Byte、Short、Long缓存（-128 ~ 127）
 * 3. Character缓存（0 ~ 127）
 * 4. Boolean缓存（true/false）
 * 5. 自动装箱拆箱机制
 * 
 * 缓存机制原理：
 * - 使用静态内部类实现延迟加载
 * - 使用数组存储缓存的实例
 * - valueOf方法优先返回缓存实例
 * 
 * 注意事项：
 * - 超出缓存范围会创建新对象
 * - 使用==比较包装类时需要注意缓存范围
 * - 建议使用equals进行值比较
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class WrapperTypeCache {
    
    /**
     * 演示Integer缓存机制
     */
    public void demonstrateIntegerCache() {
        System.out.println("========== Integer缓存演示 ==========");
        
        // 在缓存范围内（-128 ~ 127）
        Integer a = 100;  // 自动装箱，调用Integer.valueOf(100)
        Integer b = 100;
        System.out.println("Integer 100 == Integer 100: " + (a == b)); // true，同一对象
        
        // 超出缓存范围
        Integer c = 200;
        Integer d = 200;
        System.out.println("Integer 200 == Integer 200: " + (c == d)); // false，不同对象
        
        // 使用new创建（已过时，不推荐使用）
        Integer e = new Integer(100);
        Integer f = new Integer(100);
        System.out.println("new Integer(100) == new Integer(100): " + (e == f)); // false
        
        // 正确的值比较方式
        System.out.println("使用equals比较: " + c.equals(d)); // true
        
        // 显示缓存范围
        System.out.println("Integer缓存范围: -128 ~ 127");
        System.out.println("缓存上限可通过-XX:AutoBoxCacheMax=<size>调整");
    }
    
    /**
     * 演示其他包装类的缓存
     */
    public void demonstrateOtherCaches() {
        System.out.println("\n========== 其他包装类缓存演示 ==========");
        
        // Byte缓存：-128 ~ 127（全部缓存）
        Byte byte1 = 100;
        Byte byte2 = 100;
        System.out.println("Byte 100 == Byte 100: " + (byte1 == byte2)); // true
        
        // Short缓存：-128 ~ 127
        Short short1 = 100;
        Short short2 = 100;
        System.out.println("Short 100 == Short 100: " + (short1 == short2)); // true
        
        Short short3 = 200;
        Short short4 = 200;
        System.out.println("Short 200 == Short 200: " + (short3 == short4)); // false
        
        // Long缓存：-128 ~ 127
        Long long1 = 100L;
        Long long2 = 100L;
        System.out.println("Long 100 == Long 100: " + (long1 == long2)); // true
        
        Long long3 = 200L;
        Long long4 = 200L;
        System.out.println("Long 200 == Long 200: " + (long3 == long4)); // false
        
        // Character缓存：0 ~ 127
        Character char1 = 'a';  // ASCII 97
        Character char2 = 'a';
        System.out.println("Character 'a' == Character 'a': " + (char1 == char2)); // true
        
        Character char3 = '\u9999';  // 超出缓存范围
        Character char4 = '\u9999';
        System.out.println("Character '\u9999' == Character '\u9999': " + (char3 == char4)); // false
        
        // Boolean缓存：true和false
        Boolean bool1 = true;
        Boolean bool2 = true;
        System.out.println("Boolean true == Boolean true: " + (bool1 == bool2)); // true
        
        Boolean bool3 = false;
        Boolean bool4 = false;
        System.out.println("Boolean false == Boolean false: " + (bool3 == bool4)); // true
        
        // Float和Double没有缓存
        Float float1 = 1.0f;
        Float float2 = 1.0f;
        System.out.println("Float 1.0 == Float 1.0: " + (float1 == float2)); // false
        
        Double double1 = 1.0;
        Double double2 = 1.0;
        System.out.println("Double 1.0 == Double 1.0: " + (double1 == double2)); // false
    }
    
    /**
     * 演示自动装箱和拆箱
     */
    public void demonstrateAutoBoxing() {
        System.out.println("\n========== 自动装箱拆箱演示 ==========");
        
        // 自动装箱：基本类型 -> 包装类
        Integer boxed = 100;  // 等价于 Integer.valueOf(100)
        System.out.println("自动装箱: int 100 -> Integer " + boxed);
        
        // 自动拆箱：包装类 -> 基本类型
        int unboxed = boxed;  // 等价于 boxed.intValue()
        System.out.println("自动拆箱: Integer " + boxed + " -> int " + unboxed);
        
        // 在表达式中的自动装箱拆箱
        Integer a = 100;
        Integer b = 200;
        Integer c = a + b;  // 先拆箱相加，再装箱
        System.out.println("a + b = " + c);
        
        // 比较时的自动拆箱
        Integer x = 100;
        int y = 100;
        System.out.println("Integer 100 == int 100: " + (x == y)); // true，x自动拆箱
        
        // 混合运算
        Integer m = 100;
        Long n = 200L;
        // Long result = m + n;  // 编译错误，类型不匹配
        long result = m + n;  // 都可以自动拆箱为long
        System.out.println("m + n = " + result);
    }
    
    /**
     * 演示包装类的比较陷阱
     */
    public void demonstrateComparisonTraps() {
        System.out.println("\n========== 包装类比较陷阱 ==========");
        
        // 陷阱1：== vs equals
        Integer a = 100;
        Integer b = 100;
        System.out.println("a == b: " + (a == b));  // true（在缓存范围内）
        
        Integer c = 200;
        Integer d = 200;
        System.out.println("c == d: " + (c == d));  // false（超出缓存范围）
        System.out.println("c.equals(d): " + c.equals(d));  // true
        
        // 陷阱2：不同类型比较
        Integer e = 100;
        Long f = 100L;
        // System.out.println(e == f);  // 编译错误，类型不兼容
        System.out.println("e.equals(f): " + e.equals(f));  // false，类型不同直接返回false
        
        // 陷阱3：null值拆箱
        Integer nullInteger = null;
        try {
            int value = nullInteger;  // 自动拆箱，抛出NullPointerException
            System.out.println("值: " + value);
        } catch (NullPointerException ex) {
            System.out.println("null自动拆箱抛出NullPointerException");
        }
        
        // 陷阱4：三元运算符中的类型提升
        // 注意：三元运算符会进行类型提升，Integer和Double会统一提升为Double
        Number n = true ? Integer.valueOf(1) : Double.valueOf(2.0);
        System.out.println("三元运算符结果类型: " + n.getClass().getName()); // Double
        System.out.println("三元运算符结果值: " + n);  // 1.0
    }
    
    /**
     * 演示包装类的常用方法
     */
    public void demonstrateWrapperMethods() {
        System.out.println("\n========== 包装类常用方法 ==========");
        
        // 构造方法（已过时，建议使用valueOf）
        Integer i1 = Integer.valueOf(100);
        Integer i2 = Integer.valueOf("100");
        System.out.println("Integer.valueOf(100): " + i1);
        System.out.println("Integer.valueOf(\"100\"): " + i2);
        
        // 解析方法
        int parsed = Integer.parseInt("100");
        System.out.println("Integer.parseInt(\"100\"): " + parsed);
        
        // 转换为其他类型
        Integer num = 100;
        byte b = num.byteValue();
        short s = num.shortValue();
        long l = num.longValue();
        float f = num.floatValue();
        double d = num.doubleValue();
        System.out.println("类型转换: byte=" + b + ", short=" + s + ", long=" + l + 
            ", float=" + f + ", double=" + d);
        
        // 进制转换
        String binary = Integer.toBinaryString(100);
        String hex = Integer.toHexString(100);
        String octal = Integer.toOctalString(100);
        System.out.println("100的二进制: " + binary);
        System.out.println("100的十六进制: " + hex);
        System.out.println("100的八进制: " + octal);
        
        // 比较方法
        Integer x = 100;
        Integer y = 200;
        System.out.println("compare(100, 200): " + Integer.compare(x, y));  // -1
        System.out.println("compareTo(200): " + y.compareTo(x));  // 1
        
        // 最值常量
        System.out.println("Integer.MAX_VALUE: " + Integer.MAX_VALUE);
        System.out.println("Integer.MIN_VALUE: " + Integer.MIN_VALUE);
        System.out.println("Integer.SIZE: " + Integer.SIZE + " bits");
        System.out.println("Integer.BYTES: " + Integer.BYTES + " bytes");
    }
    
    /**
     * 自定义简单的Integer缓存实现
     */
    public static class CustomIntegerCache {
        // 缓存范围
        private static final int LOW = -128;
        private static final int HIGH = 127;
        
        // 缓存数组
        private static final Integer[] cache;
        
        static {
            // 初始化缓存
            cache = new Integer[HIGH - LOW + 1];
            int j = LOW;
            for (int i = 0; i < cache.length; i++) {
                cache[i] = new Integer(j++);
            }
        }
        
        /**
         * 获取缓存的Integer实例
         * 
         * @param i 整数值
         * @return Integer实例
         */
        public static Integer valueOf(int i) {
            if (i >= LOW && i <= HIGH) {
                return cache[i - LOW];
            }
            return new Integer(i);
        }
        
        /**
         * 获取缓存范围
         * 
         * @return 缓存范围描述
         */
        public static String getCacheRange() {
            return LOW + " ~ " + HIGH;
        }
    }
    
    /**
     * 演示自定义缓存
     */
    public void demonstrateCustomCache() {
        System.out.println("\n========== 自定义Integer缓存演示 ==========");
        
        System.out.println("自定义缓存范围: " + CustomIntegerCache.getCacheRange());
        
        Integer a = CustomIntegerCache.valueOf(100);
        Integer b = CustomIntegerCache.valueOf(100);
        System.out.println("自定义缓存 100 == 100: " + (a == b)); // true
        
        Integer c = CustomIntegerCache.valueOf(200);
        Integer d = CustomIntegerCache.valueOf(200);
        System.out.println("自定义缓存 200 == 200: " + (c == d)); // false（超出缓存范围）
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        demonstrateIntegerCache();
        demonstrateOtherCaches();
        demonstrateAutoBoxing();
        demonstrateComparisonTraps();
        demonstrateWrapperMethods();
        demonstrateCustomCache();
    }
}
