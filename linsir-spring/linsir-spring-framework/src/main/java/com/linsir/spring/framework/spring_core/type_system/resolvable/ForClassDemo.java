package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Demo: Using ResolvableType.forClass() to resolve class generic types
 */
public class ForClassDemo {

    public static void main(String[] args) {
        System.out.println("===== ResolvableType.forClass() Demo =====\n");

        // Demo 1: Direct class resolution
        demoDirectClass();

        // Demo 2: Generic superclass resolution
        demoGenericSuperclass();

        // Demo 3: Generic interfaces resolution
        demoGenericInterfaces();

        System.out.println("\n===== Demo Complete =====");
    }

    /**
     * Demo 1: Direct class type resolution
     */
    private static void demoDirectClass() {
        System.out.println("1. Direct Class Resolution:");

        Class<?> clazz = UserService.class;
        System.out.println("   Class: " + clazz.getName());
        System.out.println("   Simple name: " + clazz.getSimpleName());
        System.out.println("   Superclass: " + clazz.getSuperclass().getName());

        System.out.println();
    }

    /**
     * Demo 2: Generic superclass resolution
     */
    private static void demoGenericSuperclass() {
        System.out.println("2. Generic Superclass Resolution:");
        System.out.println("   Class: UserService extends BaseService<User, Long>");

        Class<?> clazz = UserService.class;
        Type genericSuperclass = clazz.getGenericSuperclass();

        System.out.println("   Generic superclass: " + genericSuperclass);

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericSuperclass;
            Type[] actualArgs = paramType.getActualTypeArguments();

            System.out.println("   Resolved type parameters:");
            for (int i = 0; i < actualArgs.length; i++) {
                System.out.println("     T" + (i + 1) + " = " + actualArgs[i]);
            }

            // Demonstrate what ResolvableType provides
            System.out.println("   T (entity type) = " + actualArgs[0]);
            System.out.println("   ID (id type) = " + actualArgs[1]);
        }

        System.out.println();
    }

    /**
     * Demo 3: Generic interfaces resolution
     */
    private static void demoGenericInterfaces() {
        System.out.println("3. Generic Interfaces Resolution:");

        // Create an anonymous class with generic interface
        Comparable<String> comparable = new Comparable<String>() {
            @Override
            public int compareTo(String o) {
                return 0;
            }
        };

        Class<?> clazz = comparable.getClass();
        Type[] genericInterfaces = clazz.getGenericInterfaces();

        System.out.println("   Anonymous class implementing Comparable<String>");
        for (Type iface : genericInterfaces) {
            System.out.println("   Interface: " + iface);

            if (iface instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) iface;
                Type[] args = paramType.getActualTypeArguments();
                System.out.println("   Generic argument: " + args[0]);
            }
        }

        System.out.println();
    }
}
