package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Demo: Using ResolvableType.forMethodParameter() to resolve method parameter types
 */
public class ForMethodParameterDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("===== ResolvableType.forMethodParameter() Demo =====\n");

        // Demo 1: Simple parameter type
        demoSimpleParameter();

        // Demo 2: Generic parameter type
        demoGenericParameter();

        // Demo 3: Multiple parameters
        demoMultipleParameters();

        System.out.println("\n===== Demo Complete =====");
    }

    // Methods for demonstration
    public void simpleMethod(String name, int age) {
    }

    public void genericMethod(List<User> users, String keyword) {
    }

    public void complexMethod(List<List<String>> nested, User user, Long id) {
    }

    /**
     * Demo 1: Simple parameter types
     */
    private static void demoSimpleParameter() throws Exception {
        System.out.println("1. Simple Parameter Types:");
        System.out.println("   Method: simpleMethod(String name, int age)");

        Method method = ForMethodParameterDemo.class.getMethod("simpleMethod", String.class, int.class);
        Parameter[] params = method.getParameters();

        System.out.println("   Parameters:");
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            System.out.println("     [" + i + "] " + param.getName() + ": " + param.getType().getSimpleName());
        }

        System.out.println();
    }

    /**
     * Demo 2: Generic parameter types
     */
    private static void demoGenericParameter() throws Exception {
        System.out.println("2. Generic Parameter Types:");
        System.out.println("   Method: genericMethod(List<User> users, String keyword)");

        Method method = ForMethodParameterDemo.class.getMethod("genericMethod", List.class, String.class);
        Parameter[] params = method.getParameters();

        System.out.println("   Parameters:");
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            Type paramType = param.getParameterizedType();

            System.out.println("     [" + i + "] " + param.getName() + ":");
            System.out.println("         Raw type: " + param.getType().getSimpleName());
            System.out.println("         Generic type: " + paramType);

            if (paramType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) paramType;
                Type[] args = pt.getActualTypeArguments();
                System.out.println("         Generic args: " + java.util.Arrays.toString(args));
            }
        }

        System.out.println();
    }

    /**
     * Demo 3: Multiple complex parameters
     */
    private static void demoMultipleParameters() throws Exception {
        System.out.println("3. Multiple Complex Parameters:");
        System.out.println("   Method: complexMethod(List<List<String>> nested, User user, Long id)");

        Method method = ForMethodParameterDemo.class.getMethod("complexMethod", List.class, User.class, Long.class);
        Parameter[] params = method.getParameters();

        System.out.println("   Parameters:");
        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];
            System.out.println("     [" + i + "] " + param.getName() + ": " + param.getParameterizedType());
        }

        System.out.println();
    }
}
