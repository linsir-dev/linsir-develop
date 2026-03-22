package com.linsir.spring.framework.spring_core.type_system.conversion;

import java.util.*;

/**
 * Demo: Converter Lookup - How ConversionService finds appropriate converters
 * Demonstrates the converter registration and lookup mechanism
 */
public class ConverterLookupDemo {

    public static void main(String[] args) {
        System.out.println("===== Converter Lookup Demo =====\n");

        // Create a simple conversion service
        SimpleConversionService conversionService = new SimpleConversionService();

        // Demo 1: Register converters
        demoRegisterConverters(conversionService);

        // Demo 2: Lookup and convert
        demoLookupAndConvert(conversionService);

        // Demo 3: Check conversion capability
        demoCanConvert(conversionService);

        // Demo 4: Generic converter lookup
        demoGenericConverterLookup(conversionService);

        System.out.println("\n===== Demo Complete =====");
    }

    /**
     * Demo 1: Register various converters
     */
    private static void demoRegisterConverters(SimpleConversionService service) {
        System.out.println("1. Registering Converters:");

        // String to Integer
        service.registerConverter(String.class, Integer.class,
                (Converter<String, Integer>) Integer::valueOf);
        System.out.println("   Registered: String -> Integer");

        // String to Long
        service.registerConverter(String.class, Long.class,
                (Converter<String, Long>) Long::valueOf);
        System.out.println("   Registered: String -> Long");

        // String to Double
        service.registerConverter(String.class, Double.class,
                (Converter<String, Double>) Double::valueOf);
        System.out.println("   Registered: String -> Double");

        // String to Boolean
        service.registerConverter(String.class, Boolean.class,
                (Converter<String, Boolean>) Boolean::valueOf);
        System.out.println("   Registered: String -> Boolean");

        // Integer to String
        service.registerConverter(Integer.class, String.class,
                (Converter<Integer, String>) String::valueOf);
        System.out.println("   Registered: Integer -> String");

        System.out.println();
    }

    /**
     * Demo 2: Lookup converter and perform conversion
     */
    private static void demoLookupAndConvert(SimpleConversionService service) {
        System.out.println("2. Converter Lookup and Conversion:");

        // String to Integer
        Integer intResult = service.convert("123", Integer.class);
        System.out.println("   Convert \"123\" -> Integer: " + intResult);

        // String to Long
        Long longResult = service.convert("9999999999", Long.class);
        System.out.println("   Convert \"9999999999\" -> Long: " + longResult);

        // String to Double
        Double doubleResult = service.convert("3.14159", Double.class);
        System.out.println("   Convert \"3.14159\" -> Double: " + doubleResult);

        // String to Boolean
        Boolean boolResult = service.convert("true", Boolean.class);
        System.out.println("   Convert \"true\" -> Boolean: " + boolResult);

        // Integer to String
        String strResult = service.convert(456, String.class);
        System.out.println("   Convert 456 -> String: \"" + strResult + "\"");

        System.out.println();
    }

    /**
     * Demo 3: Check if conversion is possible
     */
    private static void demoCanConvert(SimpleConversionService service) {
        System.out.println("3. Conversion Capability Check:");

        System.out.println("   Can convert String -> Integer: " +
                service.canConvert(String.class, Integer.class));
        System.out.println("   Can convert String -> Long: " +
                service.canConvert(String.class, Long.class));
        System.out.println("   Can convert String -> Date: " +
                service.canConvert(String.class, java.util.Date.class));
        System.out.println("   Can convert Integer -> String: " +
                service.canConvert(Integer.class, String.class));
        System.out.println("   Can convert Double -> Integer: " +
                service.canConvert(Double.class, Integer.class));

        System.out.println();
    }

    /**
     * Demo 4: Generic converter lookup
     */
    private static void demoGenericConverterLookup(SimpleConversionService service) {
        System.out.println("4. Generic Converter Registration:");

        // Register a generic array to list converter
        GenericConverter arrayToListConverter = new GenericConverter() {
            @Override
            public Set<ConvertiblePair> getConvertibleTypes() {
                Set<ConvertiblePair> pairs = new HashSet<>();
                pairs.add(new ConvertiblePair(Object[].class, List.class));
                return pairs;
            }

            @Override
            public Object convert(Object source, Class<?> sourceType, Class<?> targetType) {
                if (source instanceof Object[]) {
                    return Arrays.asList((Object[]) source);
                }
                return null;
            }
        };

        service.registerGenericConverter(arrayToListConverter);
        System.out.println("   Registered GenericConverter: Object[] -> List");

        // Test conversion
        String[] array = {"a", "b", "c"};
        @SuppressWarnings("unchecked")
        List<String> list = (List<String>) service.convert(array, List.class);
        System.out.println("   Convert String[] -> List: " + list);

        System.out.println();
    }

    /**
     * Simple ConversionService implementation for demo
     */
    static class SimpleConversionService {
        private final Map<ConvertiblePair, Converter<?, ?>> converters = new HashMap<>();
        private final List<GenericConverter> genericConverters = new ArrayList<>();

        public <S, T> void registerConverter(Class<S> sourceType, Class<T> targetType,
                                              Converter<S, T> converter) {
            converters.put(new ConvertiblePair(sourceType, targetType), converter);
        }

        public void registerGenericConverter(GenericConverter converter) {
            genericConverters.add(converter);
        }

        public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
            return converters.containsKey(new ConvertiblePair(sourceType, targetType));
        }

        @SuppressWarnings("unchecked")
        public <T> T convert(Object source, Class<T> targetType) {
            if (source == null) {
                return null;
            }

            Class<?> sourceType = source.getClass();

            // Try specific converter first
            Converter<Object, T> converter =
                    (Converter<Object, T>) converters.get(new ConvertiblePair(sourceType, targetType));

            if (converter != null) {
                return converter.convert(source);
            }

            // Try generic converters
            for (GenericConverter genericConverter : genericConverters) {
                for (ConvertiblePair pair : genericConverter.getConvertibleTypes()) {
                    if (pair.getSourceType().isAssignableFrom(sourceType) &&
                            targetType.isAssignableFrom(pair.getTargetType())) {
                        @SuppressWarnings("unchecked")
                        T result = (T) genericConverter.convert(source, sourceType, targetType);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }

            throw new IllegalArgumentException("No converter found from " + sourceType + " to " + targetType);
        }
    }
}
