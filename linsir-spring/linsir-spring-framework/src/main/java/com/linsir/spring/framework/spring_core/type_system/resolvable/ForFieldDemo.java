package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Demo: Using ResolvableType forField() to resolve field generic types
 */
public class ForFieldDemo {

    // Fields with various generic types for demonstration
    private List<String> stringList;
    private Map<String, User> userMap;
    private List<List<Integer>> nestedList;

    public static void main(String[] args) throws Exception {
        System.out.println("===== ResolvableType.forField() Demo =====\n");

        // Demo 1: Simple generic field
        demoSimpleGenericField();

        // Demo 2: Map field with generics
        demoMapField();

        // Demo 3: Nested generic field
        demoNestedGenericField();

        System.out.println("\n===== Demo Complete =====");
    }

    /**
     * Demo 1: Resolve simple generic List field
     */
    private static void demoSimpleGenericField() throws Exception {
        System.out.println("1. Simple Generic Field Resolution:");
        System.out.println("   Field: List<String> stringList");

        Field field = ForFieldDemo.class.getDeclaredField("stringList");
        Class<?> fieldType = field.getType();
        System.out.println("   Raw type: " + fieldType.getName());

        // Without ResolvableType - we only get List.class
        System.out.println("   Without generic info: " + fieldType.getSimpleName());

        // With ResolvableType - we can get String.class
        // Note: This is a simulation of what ResolvableType would do
        java.lang.reflect.Type genericType = field.getGenericType();
        System.out.println("   Generic type info: " + genericType);

        System.out.println();
    }

    /**
     * Demo 2: Resolve Map field with multiple type parameters
     */
    private static void demoMapField() throws Exception {
        System.out.println("2. Map Field Resolution:");
        System.out.println("   Field: Map<String, User> userMap");

        Field field = ForFieldDemo.class.getDeclaredField("userMap");
        Class<?> fieldType = field.getType();
        System.out.println("   Raw type: " + fieldType.getName());

        java.lang.reflect.Type genericType = field.getGenericType();
        System.out.println("   Generic type info: " + genericType);

        // Parse type parameters
        if (genericType instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericType;
            java.lang.reflect.Type[] actualArgs = paramType.getActualTypeArguments();
            System.out.println("   Key type: " + actualArgs[0]);
            System.out.println("   Value type: " + actualArgs[1]);
        }

        System.out.println();
    }

    /**
     * Demo 3: Resolve nested generic types
     */
    private static void demoNestedGenericField() throws Exception {
        System.out.println("3. Nested Generic Field Resolution:");
        System.out.println("   Field: List<List<Integer>> nestedList");

        Field field = ForFieldDemo.class.getDeclaredField("nestedList");
        Class<?> fieldType = field.getType();
        System.out.println("   Raw type: " + fieldType.getName());

        java.lang.reflect.Type genericType = field.getGenericType();
        System.out.println("   Generic type info: " + genericType);

        // Parse nested type parameters
        if (genericType instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericType;
            java.lang.reflect.Type[] args = paramType.getActualTypeArguments();
            System.out.println("   Outer generic: " + args[0]);

            if (args[0] instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType innerType = (java.lang.reflect.ParameterizedType) args[0];
                System.out.println("   Inner generic: " + innerType.getActualTypeArguments()[0]);
            }
        }

        System.out.println();
    }
}
