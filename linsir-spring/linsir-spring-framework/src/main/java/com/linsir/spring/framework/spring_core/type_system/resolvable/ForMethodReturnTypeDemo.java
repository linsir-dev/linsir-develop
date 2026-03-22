package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Demo: Using ResolvableType.forMethodReturnType() to resolve method return types
 */
public class ForMethodReturnTypeDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("===== ResolvableType.forMethodReturnType() Demo =====\n");

        // Demo 1: Simple return type
        demoSimpleReturnType();

        // Demo 2: Generic return type
        demoGenericReturnType();

        // Demo 3: Complex generic return type
        demoComplexReturnType();

        System.out.println("\n===== Demo Complete =====");
    }

    // Methods for demonstration
    public String simpleMethod() {
        return "";
    }

    public List<User> listMethod() {
        return null;
    }

    public Map<String, List<User>> complexMethod() {
        return null;
    }

    /**
     * Demo 1: Simple return type
     */
    private static void demoSimpleReturnType() throws Exception {
        System.out.println("1. Simple Return Type:");
        System.out.println("   Method: String simpleMethod()");

        Method method = ForMethodReturnTypeDemo.class.getMethod("simpleMethod");
        Class<?> returnType = method.getReturnType();

        System.out.println("   Return type: " + returnType.getName());
        System.out.println("   Is generic: " + (method.getGenericReturnType() != returnType));

        System.out.println();
    }

    /**
     * Demo 2: Generic return type
     */
    private static void demoGenericReturnType() throws Exception {
        System.out.println("2. Generic Return Type:");
        System.out.println("   Method: List<User> listMethod()");

        Method method = ForMethodReturnTypeDemo.class.getMethod("listMethod");
        Class<?> returnType = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();

        System.out.println("   Raw return type: " + returnType.getName());
        System.out.println("   Generic return type: " + genericReturnType);

        if (genericReturnType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericReturnType;
            Type[] args = pt.getActualTypeArguments();
            System.out.println("   Generic arguments: " + java.util.Arrays.toString(args));
        }

        System.out.println();
    }

    /**
     * Demo 3: Complex generic return type
     */
    private static void demoComplexReturnType() throws Exception {
        System.out.println("3. Complex Generic Return Type:");
        System.out.println("   Method: Map<String, List<User>> complexMethod()");

        Method method = ForMethodReturnTypeDemo.class.getMethod("complexMethod");
        Class<?> returnType = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();

        System.out.println("   Raw return type: " + returnType.getName());
        System.out.println("   Generic return type: " + genericReturnType);

        if (genericReturnType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericReturnType;
            Type[] args = pt.getActualTypeArguments();
            System.out.println("   Type arguments:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("     [" + i + "] " + args[i]);
            }
        }

        System.out.println();
    }
}
