package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Demo: Type checking and assignment compatibility
 */
public class TypeChecksDemo {

    private List<String> stringList;
    private Map<String, Integer> stringIntMap;
    private Collection<User> userCollection;

    public static void main(String[] args) throws Exception {
        System.out.println("===== Type Checks Demo =====\n");

        // Demo 1: Check if type is assignable
        demoAssignableCheck();

        // Demo 2: Check if type is array
        demoArrayCheck();

        // Demo 3: Check if type is collection
        demoCollectionCheck();

        // Demo 4: Check if type is map
        demoMapCheck();

        System.out.println("\n===== Demo Complete =====");
    }

    /**
     * Demo: Check type assignability
     */
    private static void demoAssignableCheck() {
        System.out.println("1. Type Assignability Check:");

        Class<?> listClass = List.class;
        Class<?> arrayListClass = java.util.ArrayList.class;

        System.out.println("   List.isAssignableFrom(ArrayList): " + listClass.isAssignableFrom(arrayListClass));
        System.out.println("   ArrayList.isAssignableFrom(List): " + arrayListClass.isAssignableFrom(listClass));

        System.out.println();
    }

    /**
     * Demo: Check if type is array
     */
    private static void demoArrayCheck() {
        System.out.println("2. Array Type Check:");

        Class<?> stringArrayClass = String[].class;
        Class<?> intArrayClass = int[].class;
        Class<?> listClass = List.class;

        System.out.println("   String[].isArray(): " + stringArrayClass.isArray());
        System.out.println("   int[].isArray(): " + intArrayClass.isArray());
        System.out.println("   List.isArray(): " + listClass.isArray());

        System.out.println();
    }

    /**
     * Demo: Check if type is collection
     */
    private static void demoCollectionCheck() throws Exception {
        System.out.println("3. Collection Type Check:");

        Field field = TypeChecksDemo.class.getDeclaredField("stringList");
        Class<?> fieldType = field.getType();

        System.out.println("   Field: List<String> stringList");
        System.out.println("   Is Collection: " + Collection.class.isAssignableFrom(fieldType));

        Field collectionField = TypeChecksDemo.class.getDeclaredField("userCollection");
        System.out.println("   Field: Collection<User> userCollection");
        System.out.println("   Is Collection: " + Collection.class.isAssignableFrom(collectionField.getType()));

        System.out.println();
    }

    /**
     * Demo: Check if type is map
     */
    private static void demoMapCheck() throws Exception {
        System.out.println("4. Map Type Check:");

        Field field = TypeChecksDemo.class.getDeclaredField("stringIntMap");
        Class<?> fieldType = field.getType();

        System.out.println("   Field: Map<String, Integer> stringIntMap");
        System.out.println("   Is Map: " + Map.class.isAssignableFrom(fieldType));

        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type[] args = pt.getActualTypeArguments();
            System.out.println("   Key type: " + args[0]);
            System.out.println("   Value type: " + args[1]);
        }

        System.out.println();
    }
}
