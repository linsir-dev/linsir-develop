package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Demo: Traditional approach vs ResolvableType approach
 * Shows the complexity of traditional reflection and how ResolvableType simplifies it
 */
public class TraditionalApproachDemo {

    private List<String> stringList;
    private Map<String, User> userMap;

    public List<User> getUsers() {
        return null;
    }

    public void processUsers(List<User> users) {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("===== Traditional vs ResolvableType Approach =====\n");

        // Demo 1: Traditional approach for field
        demoTraditionalFieldApproach();

        // Demo 2: Traditional approach for method return type
        demoTraditionalMethodReturnApproach();

        // Demo 3: Traditional approach for method parameter
        demoTraditionalMethodParameterApproach();

        // Demo 4: Show the complexity
        demoComplexityComparison();

        System.out.println("\n===== Demo Complete =====");
    }

    /**
     * Demo: Traditional approach to get field generic type
     */
    private static void demoTraditionalFieldApproach() throws Exception {
        System.out.println("1. Traditional Approach - Field Generic Type:");
        System.out.println("   Field: List<String> stringList");

        Field field = TraditionalApproachDemo.class.getDeclaredField("stringList");

        // Step 1: Get generic type
        Type genericType = field.getGenericType();
        System.out.println("   Step 1 - Generic type: " + genericType);

        // Step 2: Check if parameterized
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type[] args = pt.getActualTypeArguments();
            System.out.println("   Step 2 - Type arguments: " + java.util.Arrays.toString(args));

            // Step 3: Resolve to Class
            for (Type arg : args) {
                if (arg instanceof Class) {
                    System.out.println("   Step 3 - Resolved class: " + ((Class<?>) arg).getName());
                }
            }
        }

        System.out.println();
    }

    /**
     * Demo: Traditional approach for method return type
     */
    private static void demoTraditionalMethodReturnApproach() throws Exception {
        System.out.println("2. Traditional Approach - Method Return Type:");
        System.out.println("   Method: List<User> getUsers()");

        Method method = TraditionalApproachDemo.class.getMethod("getUsers");
        Type returnType = method.getGenericReturnType();

        System.out.println("   Generic return type: " + returnType);

        if (returnType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) returnType;
            Type[] args = pt.getActualTypeArguments();
            System.out.println("   Element type: " + args[0]);
        }

        System.out.println();
    }

    /**
     * Demo: Traditional approach for method parameter
     */
    private static void demoTraditionalMethodParameterApproach() throws Exception {
        System.out.println("3. Traditional Approach - Method Parameter:");
        System.out.println("   Method: void processUsers(List<User> users)");

        Method method = TraditionalApproachDemo.class.getMethod("processUsers", List.class);
        Type[] paramTypes = method.getGenericParameterTypes();

        System.out.println("   Parameter types:");
        for (int i = 0; i < paramTypes.length; i++) {
            System.out.println("     [" + i + "] " + paramTypes[i]);

            if (paramTypes[i] instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) paramTypes[i];
                System.out.println("         Generic args: " + java.util.Arrays.toString(pt.getActualTypeArguments()));
            }
        }

        System.out.println();
    }

    /**
     * Demo: Show complexity comparison
     */
    private static void demoComplexityComparison() {
        System.out.println("4. Complexity Comparison:");
        System.out.println();
        System.out.println("   Traditional Approach:");
        System.out.println("   - Multiple type checks (instanceof)");
        System.out.println("   - Manual casting to ParameterizedType");
        System.out.println("   - Manual extraction of type arguments");
        System.out.println("   - No built-in nesting support");
        System.out.println("   - Verbose and error-prone");
        System.out.println();
        System.out.println("   ResolvableType Approach (Spring):");
        System.out.println("   - Single API for all type operations");
        System.out.println("   - Fluent API for nested types");
        System.out.println("   - Built-in type resolution");
        System.out.println("   - Handles complex generic hierarchies");
        System.out.println("   - Type assignment checking");
        System.out.println();
    }
}
