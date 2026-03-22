package com.linsir.spring.framework.spring_core.type_system;

import com.linsir.spring.framework.spring_core.type_system.conversion.*;

/**
 * 类型系统独立测试类
 * 不依赖 JUnit，可直接通过 java 命令运行
 * 
 * 运行方式:
 * 1. 编译: javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/com/linsir/spring/framework/spring_core/type_system/TypeSystemStandaloneTest.java
 * 2. 运行: java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.TypeSystemStandaloneTest
 */
public class TypeSystemStandaloneTest {

    private static int testCount = 0;
    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    类型系统核心接口独立测试");
        System.out.println("========================================\n");

        // 测试 ConvertiblePair
        testConvertiblePairCreation();
        testConvertiblePairEquality();
        testConvertiblePairHashCode();

        // 测试 Converter 接口
        testStringToIntegerConverter();
        testStringToLongConverter();
        testStringToDoubleConverter();
        testStringToBooleanConverter();

        // 测试 GenericConverter 接口
        testGenericConverterInterface();

        // 测试 ConversionService 接口
        testConversionServiceInterface();

        // 打印测试报告
        printTestReport();
    }

    private static void testConvertiblePairCreation() {
        startTest("ConvertiblePair Creation");
        try {
            ConvertiblePair pair = new ConvertiblePair(String.class, Integer.class);
            assertEquals(String.class, pair.getSourceType(), "SourceType should be String.class");
            assertEquals(Integer.class, pair.getTargetType(), "TargetType should be Integer.class");
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static void testConvertiblePairEquality() {
        startTest("ConvertiblePair Equality");
        try {
            ConvertiblePair pair1 = new ConvertiblePair(String.class, Integer.class);
            ConvertiblePair pair2 = new ConvertiblePair(String.class, Integer.class);
            ConvertiblePair pair3 = new ConvertiblePair(String.class, Long.class);

            assertTrue(pair1.equals(pair2), "Same ConvertiblePairs should be equal");
            assertFalse(pair1.equals(pair3), "Different ConvertiblePairs should not be equal");
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static void testConvertiblePairHashCode() {
        startTest("ConvertiblePair HashCode");
        try {
            ConvertiblePair pair1 = new ConvertiblePair(String.class, Integer.class);
            ConvertiblePair pair2 = new ConvertiblePair(String.class, Integer.class);

            assertEquals(pair1.hashCode(), pair2.hashCode(), "Same ConvertiblePairs should have same hashCode");
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static void testStringToIntegerConverter() {
        startTest("String -> Integer Converter");
        try {
            Converter<String, Integer> converter = Integer::valueOf;
            
            Integer result1 = converter.convert("123");
            assertEquals(Integer.valueOf(123), result1, "\"123\" should convert to 123");

            Integer result2 = converter.convert("0");
            assertEquals(Integer.valueOf(0), result2, "\"0\" should convert to 0");

            Integer result3 = converter.convert("-456");
            assertEquals(Integer.valueOf(-456), result3, "\"-456\" should convert to -456");

            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static void testStringToLongConverter() {
        startTest("String -> Long Converter");
        try {
            Converter<String, Long> converter = Long::valueOf;
            
            Long result = converter.convert("9999999999");
            assertEquals(Long.valueOf(9999999999L), result, "\"9999999999\" should convert to 9999999999L");
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static void testStringToDoubleConverter() {
        startTest("String -> Double Converter");
        try {
            Converter<String, Double> converter = Double::valueOf;
            
            Double result = converter.convert("3.14159");
            assertEquals(Double.valueOf(3.14159), result, 0.00001, "\"3.14159\" should convert to 3.14159");
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static void testStringToBooleanConverter() {
        startTest("String -> Boolean Converter");
        try {
            Converter<String, Boolean> converter = Boolean::valueOf;
            
            assertTrue(converter.convert("true"), "\"true\" should convert to true");
            assertFalse(converter.convert("false"), "\"false\" should convert to false");
            assertFalse(converter.convert(""), "Empty string should convert to false");
            assertFalse(converter.convert("random"), "Random string should convert to false");
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static void testGenericConverterInterface() {
        startTest("GenericConverter Interface");
        try {
            // Create a simple GenericConverter implementation
            GenericConverter arrayToListConverter = new GenericConverter() {
                @Override
                public java.util.Set<ConvertiblePair> getConvertibleTypes() {
                    java.util.Set<ConvertiblePair> pairs = new java.util.HashSet<>();
                    pairs.add(new ConvertiblePair(Object[].class, java.util.List.class));
                    return pairs;
                }

                @Override
                public Object convert(Object source, Class<?> sourceType, Class<?> targetType) {
                    if (source instanceof Object[]) {
                        return java.util.Arrays.asList((Object[]) source);
                    }
                    return null;
                }
            };

            java.util.Set<ConvertiblePair> types = arrayToListConverter.getConvertibleTypes();
            assertTrue(types != null && !types.isEmpty(), "getConvertibleTypes should not return empty set");

            // Test conversion
            String[] array = {"a", "b", "c"};
            @SuppressWarnings("unchecked")
            java.util.List<String> list = (java.util.List<String>) arrayToListConverter.convert(array, String[].class, java.util.List.class);
            assertTrue(list != null && list.size() == 3, "Array should convert to list with 3 elements");
            assertEquals("a", list.get(0), "First element should be \"a\"");

            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static void testConversionServiceInterface() {
        startTest("ConversionService Interface");
        try {
            // Create a simple ConversionService implementation
            ConversionService conversionService = new ConversionService() {
                private final java.util.Map<ConvertiblePair, Converter<?, ?>> converters = new java.util.HashMap<>();

                {
                    // Register basic converters
                    converters.put(new ConvertiblePair(String.class, Integer.class), (Converter<String, Integer>) Integer::valueOf);
                    converters.put(new ConvertiblePair(String.class, Long.class), (Converter<String, Long>) Long::valueOf);
                    converters.put(new ConvertiblePair(String.class, Double.class), (Converter<String, Double>) Double::valueOf);
                    converters.put(new ConvertiblePair(String.class, Boolean.class), (Converter<String, Boolean>) Boolean::valueOf);
                }

                @Override
                public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
                    return converters.containsKey(new ConvertiblePair(sourceType, targetType));
                }

                @SuppressWarnings("unchecked")
                @Override
                public <T> T convert(Object source, Class<T> targetType) {
                    if (source == null) return null;
                    Converter<Object, T> converter = (Converter<Object, T>) converters.get(new ConvertiblePair(source.getClass(), targetType));
                    if (converter == null) {
                        throw new IllegalArgumentException("No converter found from " + source.getClass() + " to " + targetType);
                    }
                    return converter.convert(source);
                }
            };

            // Test canConvert
            assertTrue(conversionService.canConvert(String.class, Integer.class), "Should support String -> Integer conversion");
            assertFalse(conversionService.canConvert(String.class, java.util.Date.class), "Should not support String -> Date conversion");

            // Test convert
            Integer intResult = conversionService.convert("42", Integer.class);
            assertEquals(Integer.valueOf(42), intResult, "\"42\" should convert to Integer 42");

            Double doubleResult = conversionService.convert("3.14", Double.class);
            assertEquals(Double.valueOf(3.14), doubleResult, 0.01, "\"3.14\" should convert to Double 3.14");

            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    // ==================== Helper Methods ====================

    private static void startTest(String testName) {
        testCount++;
        System.out.printf("[%d] %-40s ... ", testCount, testName);
    }

    private static void pass() {
        passCount++;
        System.out.println("PASS");
    }

    private static void fail(String message) {
        failCount++;
        System.out.println("FAIL: " + message);
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }

    private static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    private static void printTestReport() {
        System.out.println("\n========================================");
        System.out.println("           Test Report");
        System.out.println("========================================");
        System.out.printf("Total:  %d%n", testCount);
        System.out.printf("Passed: %d%n", passCount);
        System.out.printf("Failed: %d%n", failCount);
        System.out.println("========================================");

        if (failCount == 0) {
            System.out.println("\nAll tests passed!");
        } else {
            System.out.println("\nSome tests failed.");
            System.exit(1);
        }
    }
}
