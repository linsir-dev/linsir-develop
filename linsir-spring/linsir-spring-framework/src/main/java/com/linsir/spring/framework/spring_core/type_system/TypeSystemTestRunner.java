package com.linsir.spring.framework.spring_core.type_system;

import com.linsir.spring.framework.spring_core.type_system.conversion.*;

/**
 * 类型系统测试运行器
 * 用于验证核心接口功能
 */
public class TypeSystemTestRunner {

    public static void main(String[] args) {
        System.out.println("=== 类型系统核心接口测试 ===\n");
        
        // 测试 ConvertiblePair
        testConvertiblePair();
        
        // 测试 Converter 接口
        testConverter();
        
        System.out.println("\n=== 所有测试通过 ===");
    }
    
    private static void testConvertiblePair() {
        System.out.println("1. 测试 ConvertiblePair:");
        
        ConvertiblePair pair = new ConvertiblePair(String.class, Integer.class);
        System.out.println("   创建 ConvertiblePair: " + pair.getSourceType().getSimpleName() + " -> " + pair.getTargetType().getSimpleName());
        
        // 测试相等性
        ConvertiblePair pair2 = new ConvertiblePair(String.class, Integer.class);
        assert pair.equals(pair2) : "相等的 ConvertiblePair 应该相等";
        System.out.println("   ✓ 相等性测试通过");
        
        // 测试不等性
        ConvertiblePair pair3 = new ConvertiblePair(String.class, Long.class);
        assert !pair.equals(pair3) : "不同的 ConvertiblePair 不应该相等";
        System.out.println("   ✓ 不等性测试通过");
        
        System.out.println();
    }
    
    private static void testConverter() {
        System.out.println("2. 测试 Converter 接口:");
        
        // String -> Integer
        Converter<String, Integer> intConverter = Integer::valueOf;
        Integer intResult = intConverter.convert("123");
        assert intResult == 123 : "String '123' 应该转换为 Integer 123";
        System.out.println("   ✓ String -> Integer: \"123\" -> " + intResult);
        
        // String -> Long
        Converter<String, Long> longConverter = Long::valueOf;
        Long longResult = longConverter.convert("9999999999");
        assert longResult == 9999999999L : "String '9999999999' 应该转换为 Long 9999999999";
        System.out.println("   ✓ String -> Long: \"9999999999\" -> " + longResult);
        
        // String -> Double
        Converter<String, Double> doubleConverter = Double::valueOf;
        Double doubleResult = doubleConverter.convert("3.14159");
        assert Math.abs(doubleResult - 3.14159) < 0.00001 : "String '3.14159' 应该转换为 Double 3.14159";
        System.out.println("   ✓ String -> Double: \"3.14159\" -> " + doubleResult);
        
        // String -> Boolean
        Converter<String, Boolean> boolConverter = Boolean::valueOf;
        Boolean boolResult = boolConverter.convert("true");
        assert boolResult : "String 'true' 应该转换为 Boolean true";
        System.out.println("   ✓ String -> Boolean: \"true\" -> " + boolResult);
        
        System.out.println();
    }
}
