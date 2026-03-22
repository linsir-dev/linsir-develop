package com.linsir.spring.framework.spring_core.type_system.descriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Demo: TypeDescriptor.valueOf() - Creating type descriptors from various sources
 * Simulates Spring's TypeDescriptor API
 */
public class ValueOfDemo {

    // Fields with annotations for demonstration
    @NotNull
    private String name;

    @Size(min = 1, max = 100)
    private List<String> tags;

    @Range(min = 0, max = 150)
    private int age;

    private Map<String, User> userMap;

    public static void main(String[] args) throws Exception {
        System.out.println("===== TypeDescriptor.valueOf() Demo =====\n");

        // Demo 1: From Class
        demoFromClass();

        // Demo 2: From Field
        demoFromField();

        // Demo 3: From Method
        demoFromMethod();

        // Demo 4: From Object
        demoFromObject();

        System.out.println("\n===== Demo Complete =====");
    }

    /**
     * Demo 1: Create type descriptor from Class
     */
    private static void demoFromClass() {
        System.out.println("1. TypeDescriptor from Class:");

        Class<?> clazz = String.class;
        System.out.println("   Source: String.class");
        System.out.println("   Type: " + clazz.getName());
        System.out.println("   Is primitive: " + clazz.isPrimitive());
        System.out.println("   Is array: " + clazz.isArray());

        System.out.println();
    }

    /**
     * Demo 2: Create type descriptor from Field
     */
    private static void demoFromField() throws Exception {
        System.out.println("2. TypeDescriptor from Field:");

        Field nameField = ValueOfDemo.class.getDeclaredField("name");
        System.out.println("   Field: @NotNull String name");
        System.out.println("   Field type: " + nameField.getType().getName());

        // Get annotations
        NotNull notNull = nameField.getAnnotation(NotNull.class);
        if (notNull != null) {
            System.out.println("   Annotations: @NotNull(message=\"" + notNull.message() + "\")");
        }

        Field tagsField = ValueOfDemo.class.getDeclaredField("tags");
        System.out.println("\n   Field: @Size(min=1, max=100) List<String> tags");
        System.out.println("   Field type: " + tagsField.getType().getName());
        System.out.println("   Generic type: " + tagsField.getGenericType());

        Size size = tagsField.getAnnotation(Size.class);
        if (size != null) {
            System.out.println("   Annotations: @Size(min=" + size.min() + ", max=" + size.max() + ")");
        }

        Field ageField = ValueOfDemo.class.getDeclaredField("age");
        System.out.println("\n   Field: @Range(min=0, max=150) int age");
        Range range = ageField.getAnnotation(Range.class);
        if (range != null) {
            System.out.println("   Annotations: @Range(min=" + range.min() + ", max=" + range.max() + ")");
        }

        System.out.println();
    }

    /**
     * Demo 3: Create type descriptor from Method
     */
    private static void demoFromMethod() throws Exception {
        System.out.println("3. TypeDescriptor from Method:");

        Method getter = UserService.class.getMethod("getServiceName");
        System.out.println("   Method: String getServiceName()");
        System.out.println("   Return type: " + getter.getReturnType().getName());
        System.out.println("   Parameter count: " + getter.getParameterCount());

        Method setter = UserService.class.getMethod("setServiceName", String.class);
        System.out.println("\n   Method: void setServiceName(String)");
        System.out.println("   Parameter types: " + java.util.Arrays.toString(setter.getParameterTypes()));

        System.out.println();
    }

    /**
     * Demo 4: Create type descriptor from Object
     */
    private static void demoFromObject() {
        System.out.println("4. TypeDescriptor from Object:");

        Object str = "Hello";
        System.out.println("   Object: \"Hello\"");
        System.out.println("   Runtime type: " + str.getClass().getName());

        Object list = new java.util.ArrayList<String>();
        System.out.println("\n   Object: new ArrayList<String>()");
        System.out.println("   Runtime type: " + list.getClass().getName());

        Object map = new java.util.HashMap<String, Integer>();
        System.out.println("\n   Object: new HashMap<String, Integer>()");
        System.out.println("   Runtime type: " + map.getClass().getName());

        System.out.println();
    }
}
