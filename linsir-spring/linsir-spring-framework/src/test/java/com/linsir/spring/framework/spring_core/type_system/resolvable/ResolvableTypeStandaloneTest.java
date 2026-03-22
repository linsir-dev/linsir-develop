package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * ResolvableType standalone test
 * Can run outside sandbox with: java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.resolvable.ResolvableTypeStandaloneTest
 */
public class ResolvableTypeStandaloneTest {

    private static int testCount = 0;
    private static int passCount = 0;
    private static int failCount = 0;

    // Test fields with generic types
    private List<String> stringList;
    private Map<String, User> userMap;
    private List<List<Integer>> nestedList;

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("    ResolvableType Standalone Test");
        System.out.println("========================================\n");

        ResolvableTypeStandaloneTest test = new ResolvableTypeStandaloneTest();

        // User class tests
        test.testUserClass();

        // Generic type resolution tests
        test.testBaseServiceGenericTypes();
        test.testUserServiceOperations();
        test.testGenericFieldResolution();
        test.testMapFieldResolution();
        test.testNestedGenericResolution();
        test.testMethodReturnTypeResolution();
        test.testMethodParameterResolution();

        // Config tests
        test.testConfigGenericTypes();

        // Type checks
        test.testTypeAssignability();
        test.testArrayType();

        // Print report
        printTestReport();
    }

    private void testUserClass() {
        startTest("User Class");
        try {
            User user = new User("John", 30);
            
            assertEquals("John", user.getName());
            assertEquals(Integer.valueOf(30), user.getAge());
            
            user.setName("Jane");
            user.setAge(25);
            
            assertEquals("Jane", user.getName());
            assertEquals(Integer.valueOf(25), user.getAge());
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testBaseServiceGenericTypes() {
        startTest("BaseService Generic Types");
        try {
            Type genericSuperclass = UserService.class.getGenericSuperclass();
            assertTrue(genericSuperclass instanceof ParameterizedType);
            
            ParameterizedType paramType = (ParameterizedType) genericSuperclass;
            Type[] actualArgs = paramType.getActualTypeArguments();
            
            assertEquals(2, actualArgs.length);
            assertEquals(User.class, actualArgs[0]);
            assertEquals(Long.class, actualArgs[1]);
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testUserServiceOperations() {
        startTest("UserService Operations");
        try {
            UserService service = new UserService();
            
            User user1 = new User("Alice", 25);
            User user2 = new User("Bob", 30);
            
            service.save(user1);
            service.save(user2);
            
            assertEquals(2, service.findAll().size());
            
            User found = service.findById(0L);
            assertNotNull(found, "User should be found");
            assertEquals("Alice", found.getName());
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testGenericFieldResolution() throws Exception {
        startTest("Generic Field Resolution");
        try {
            Field field = ResolvableTypeStandaloneTest.class.getDeclaredField("stringList");
            
            assertEquals(List.class, field.getType());
            
            Type genericType = field.getGenericType();
            assertTrue(genericType instanceof ParameterizedType);
            
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] args = paramType.getActualTypeArguments();
            assertEquals(1, args.length);
            assertEquals(String.class, args[0]);
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testMapFieldResolution() throws Exception {
        startTest("Map Field Resolution");
        try {
            Field field = ResolvableTypeStandaloneTest.class.getDeclaredField("userMap");
            
            assertEquals(Map.class, field.getType());
            
            Type genericType = field.getGenericType();
            assertTrue(genericType instanceof ParameterizedType);
            
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] args = paramType.getActualTypeArguments();
            assertEquals(2, args.length);
            assertEquals(String.class, args[0]);
            assertEquals(User.class, args[1]);
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testNestedGenericResolution() throws Exception {
        startTest("Nested Generic Resolution");
        try {
            Field field = ResolvableTypeStandaloneTest.class.getDeclaredField("nestedList");
            
            Type genericType = field.getGenericType();
            assertTrue(genericType instanceof ParameterizedType);
            
            ParameterizedType outerType = (ParameterizedType) genericType;
            Type[] outerArgs = outerType.getActualTypeArguments();
            assertEquals(1, outerArgs.length);
            
            // Inner List<Integer>
            assertTrue(outerArgs[0] instanceof ParameterizedType);
            ParameterizedType innerType = (ParameterizedType) outerArgs[0];
            Type[] innerArgs = innerType.getActualTypeArguments();
            assertEquals(1, innerArgs.length);
            assertEquals(Integer.class, innerArgs[0]);
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testMethodReturnTypeResolution() throws Exception {
        startTest("Method Return Type Resolution");
        try {
            Method method = UserController.class.getMethod("getAllUsers");
            
            Type returnType = method.getGenericReturnType();
            assertTrue(returnType instanceof ParameterizedType);
            
            ParameterizedType paramType = (ParameterizedType) returnType;
            Type[] args = paramType.getActualTypeArguments();
            assertEquals(1, args.length);
            assertEquals(User.class, args[0]);
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testMethodParameterResolution() throws Exception {
        startTest("Method Parameter Resolution");
        try {
            Method method = UserController.class.getMethod("processUsers", List.class);
            
            Type[] paramTypes = method.getGenericParameterTypes();
            assertEquals(1, paramTypes.length);
            
            assertTrue(paramTypes[0] instanceof ParameterizedType);
            ParameterizedType paramType = (ParameterizedType) paramTypes[0];
            Type[] args = paramType.getActualTypeArguments();
            assertEquals(User.class, args[0]);
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testConfigGenericTypes() {
        startTest("Config Generic Types");
        try {
            Config.ConfigHolder<Config.DatabaseConfig> holder = new Config.ConfigHolder<>("db", new Config.DatabaseConfig("url", "user", "pass"));
            
            assertEquals("db", holder.getName());
            assertNotNull(holder.getConfig(), "Config should not be null");
            assertEquals("url", holder.getConfig().getUrl());
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testTypeAssignability() {
        startTest("Type Assignability");
        try {
            assertTrue(List.class.isAssignableFrom(java.util.ArrayList.class));
            assertTrue(Map.class.isAssignableFrom(java.util.HashMap.class));
            assertFalse(String.class.isAssignableFrom(Integer.class));
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testArrayType() {
        startTest("Array Type");
        try {
            Class<?> stringArrayClass = String[].class;
            assertTrue(stringArrayClass.isArray());
            assertEquals(String.class, stringArrayClass.getComponentType());
            
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

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but was true");
        }
    }

    private static void assertNotNull(Object obj, String message) {
        if (obj == null) {
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
