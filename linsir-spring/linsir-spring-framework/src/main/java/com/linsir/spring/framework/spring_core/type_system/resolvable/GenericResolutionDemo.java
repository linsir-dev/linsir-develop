package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Demo: Generic type resolution using Java Reflection
 * Simulates what Spring's ResolvableType provides
 */
public class GenericResolutionDemo {

    // Test fields
    private List<String> stringList;
    private Map<String, User> userMap;
    private List<User> userList;

    public static void main(String[] args) throws Exception {
        System.out.println("===== Generic Type Resolution Demo =====\n");

        // Demo 1: Resolve List<String>
        resolveFieldType("stringList");

        // Demo 2: Resolve Map<String, User>
        resolveFieldType("userMap");

        // Demo 3: Resolve List<User>
        resolveFieldType("userList");

        // Demo 4: Resolve UserService generic superclass
        resolveUserServiceGenerics();

        System.out.println("\n===== Demo Complete =====");
    }

    /**
     * Resolve generic type of a field
     */
    private static void resolveFieldType(String fieldName) throws Exception {
        System.out.println("Resolving field: " + fieldName);

        Field field = GenericResolutionDemo.class.getDeclaredField(fieldName);
        Type genericType = field.getGenericType();

        System.out.println("  Field type: " + field.getType().getSimpleName());
        System.out.println("  Generic type: " + genericType);

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type[] args = pt.getActualTypeArguments();
            System.out.println("  Type arguments:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("    [" + i + "] " + args[i]);
            }
        }

        System.out.println();
    }

    /**
     * Resolve UserService generic superclass
     */
    private static void resolveUserServiceGenerics() {
        System.out.println("Resolving UserService generic superclass:");

        Type genericSuperclass = UserService.class.getGenericSuperclass();
        System.out.println("  Generic superclass: " + genericSuperclass);

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            Type[] args = pt.getActualTypeArguments();

            System.out.println("  Resolved types:");
            System.out.println("    T (Entity type): " + args[0]);
            System.out.println("    ID (Id type): " + args[1]);
        }

        System.out.println();
    }
}
