package com.linsir.spring.framework.spring_core.type_system.descriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * TypeDescriptor standalone test
 * Can run outside sandbox with: java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.descriptor.TypeDescriptorStandaloneTest
 */
public class TypeDescriptorStandaloneTest {

    private static int testCount = 0;
    private static int passCount = 0;
    private static int failCount = 0;

    // Test fields with annotations
    @NotNull
    private String name;

    @Size(min = 1, max = 100)
    private List<String> tags;

    @Range(min = 0, max = 150)
    private int age;

    private Map<String, User> userMap;

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("    TypeDescriptor Standalone Test");
        System.out.println("========================================\n");

        TypeDescriptorStandaloneTest test = new TypeDescriptorStandaloneTest();

        // Annotation tests
        test.testNotNullAnnotation();
        test.testSizeAnnotation();
        test.testRangeAnnotation();

        // Type tests
        test.testGenericFieldType();
        test.testMapFieldType();

        // User class tests
        test.testUserClassProperties();

        // UserService tests
        test.testUserServiceProperties();
        test.testUserServiceUserOperations();

        // Annotation metadata tests
        test.testAnnotationRetention();
        test.testAnnotationTargets();

        // Print report
        printTestReport();
    }

    private void testNotNullAnnotation() throws Exception {
        startTest("NotNull Annotation");
        try {
            Field field = TypeDescriptorStandaloneTest.class.getDeclaredField("name");
            
            NotNull notNull = field.getAnnotation(NotNull.class);
            assertNotNull(notNull, "NotNull annotation should be present");
            assertEquals("Value must not be null", notNull.message());
            assertEquals(String.class, field.getType());
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testSizeAnnotation() throws Exception {
        startTest("Size Annotation");
        try {
            Field field = TypeDescriptorStandaloneTest.class.getDeclaredField("tags");
            
            Size size = field.getAnnotation(Size.class);
            assertNotNull(size, "Size annotation should be present");
            assertEquals(1, size.min());
            assertEquals(100, size.max());
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testRangeAnnotation() throws Exception {
        startTest("Range Annotation");
        try {
            Field field = TypeDescriptorStandaloneTest.class.getDeclaredField("age");
            
            Range range = field.getAnnotation(Range.class);
            assertNotNull(range, "Range annotation should be present");
            assertEquals(0, range.min());
            assertEquals(150, range.max());
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testGenericFieldType() throws Exception {
        startTest("Generic Field Type");
        try {
            Field field = TypeDescriptorStandaloneTest.class.getDeclaredField("tags");
            
            assertEquals(List.class, field.getType());
            
            java.lang.reflect.Type genericType = field.getGenericType();
            assertTrue(genericType instanceof java.lang.reflect.ParameterizedType);
            
            java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericType;
            java.lang.reflect.Type[] args = paramType.getActualTypeArguments();
            assertEquals(1, args.length);
            assertEquals(String.class, args[0]);
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testMapFieldType() throws Exception {
        startTest("Map Field Type");
        try {
            Field field = TypeDescriptorStandaloneTest.class.getDeclaredField("userMap");
            
            assertEquals(Map.class, field.getType());
            
            java.lang.reflect.Type genericType = field.getGenericType();
            assertTrue(genericType instanceof java.lang.reflect.ParameterizedType);
            
            java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericType;
            java.lang.reflect.Type[] args = paramType.getActualTypeArguments();
            assertEquals(2, args.length);
            assertEquals(String.class, args[0]);
            assertEquals(User.class, args[1]);
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testUserClassProperties() {
        startTest("User Class Properties");
        try {
            User user = new User("John", 30, "john@example.com");
            
            assertEquals("John", user.getName());
            assertEquals(Integer.valueOf(30), user.getAge());
            assertEquals("john@example.com", user.getEmail());
            
            user.setName("Jane");
            user.setAge(25);
            user.setEmail("jane@example.com");
            
            assertEquals("Jane", user.getName());
            assertEquals(Integer.valueOf(25), user.getAge());
            assertEquals("jane@example.com", user.getEmail());
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testUserServiceProperties() throws Exception {
        startTest("UserService Properties");
        try {
            UserService service = new UserService("TestService", 100);
            
            assertEquals("TestService", service.getServiceName());
            assertEquals(100, service.getMaxUsers());
            
            // Test property access via reflection
            Method getter = UserService.class.getMethod("getServiceName");
            assertEquals(String.class, getter.getReturnType());
            
            Method setter = UserService.class.getMethod("setServiceName", String.class);
            assertEquals(void.class, setter.getReturnType());
            
            // Test property modification
            service.setServiceName("UpdatedService");
            assertEquals("UpdatedService", service.getServiceName());
            
            service.setMaxUsers(200);
            assertEquals(200, service.getMaxUsers());
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testUserServiceUserOperations() {
        startTest("UserService User Operations");
        try {
            UserService service = new UserService();
            
            User user1 = new User("Alice", 25, "alice@example.com");
            User user2 = new User("Bob", 30, "bob@example.com");
            
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

    private void testAnnotationRetention() {
        startTest("Annotation Retention");
        try {
            // Verify annotations are retained at runtime
            assertTrue(NotNull.class.isAnnotationPresent(java.lang.annotation.Retention.class));
            assertTrue(Size.class.isAnnotationPresent(java.lang.annotation.Retention.class));
            assertTrue(Range.class.isAnnotationPresent(java.lang.annotation.Retention.class));
            
            java.lang.annotation.Retention notNullRetention = NotNull.class.getAnnotation(java.lang.annotation.Retention.class);
            assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME, notNullRetention.value());
            
            pass();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void testAnnotationTargets() {
        startTest("Annotation Targets");
        try {
            // Verify annotation targets
            assertTrue(NotNull.class.isAnnotationPresent(java.lang.annotation.Target.class));
            assertTrue(Size.class.isAnnotationPresent(java.lang.annotation.Target.class));
            assertTrue(Range.class.isAnnotationPresent(java.lang.annotation.Target.class));
            
            java.lang.annotation.Target notNullTarget = NotNull.class.getAnnotation(java.lang.annotation.Target.class);
            java.lang.annotation.ElementType[] targets = notNullTarget.value();
            assertTrue(java.util.Arrays.asList(targets).contains(java.lang.annotation.ElementType.FIELD));
            assertTrue(java.util.Arrays.asList(targets).contains(java.lang.annotation.ElementType.METHOD));
            assertTrue(java.util.Arrays.asList(targets).contains(java.lang.annotation.ElementType.PARAMETER));
            
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
