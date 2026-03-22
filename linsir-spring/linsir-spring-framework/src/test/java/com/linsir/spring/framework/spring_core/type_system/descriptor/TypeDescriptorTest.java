package com.linsir.spring.framework.spring_core.type_system.descriptor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * TypeDescriptor test class
 * Tests type descriptor functionality and annotation handling
 */
public class TypeDescriptorTest {

    // Test fields with annotations
    @NotNull
    private String name;

    @Size(min = 1, max = 100)
    private List<String> tags;

    @Range(min = 0, max = 150)
    private int age;

    private Map<String, User> userMap;

    @Test
    public void testNotNullAnnotation() throws Exception {
        Field field = TypeDescriptorTest.class.getDeclaredField("name");
        
        NotNull notNull = field.getAnnotation(NotNull.class);
        assertNotNull(notNull, "NotNull annotation should be present");
        assertEquals("Value must not be null", notNull.message());
        
        assertEquals(String.class, field.getType());
    }

    @Test
    public void testSizeAnnotation() throws Exception {
        Field field = TypeDescriptorTest.class.getDeclaredField("tags");
        
        Size size = field.getAnnotation(Size.class);
        assertNotNull(size, "Size annotation should be present");
        assertEquals(1, size.min());
        assertEquals(100, size.max());
        assertEquals("Size must be between {min} and {max}", size.message());
    }

    @Test
    public void testRangeAnnotation() throws Exception {
        Field field = TypeDescriptorTest.class.getDeclaredField("age");
        
        Range range = field.getAnnotation(Range.class);
        assertNotNull(range, "Range annotation should be present");
        assertEquals(0, range.min());
        assertEquals(150, range.max());
    }

    @Test
    public void testGenericFieldType() throws Exception {
        Field field = TypeDescriptorTest.class.getDeclaredField("tags");
        
        assertEquals(List.class, field.getType());
        
        java.lang.reflect.Type genericType = field.getGenericType();
        assertTrue(genericType instanceof java.lang.reflect.ParameterizedType);
        
        java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericType;
        java.lang.reflect.Type[] args = paramType.getActualTypeArguments();
        assertEquals(1, args.length);
        assertEquals(String.class, args[0]);
    }

    @Test
    public void testMapFieldType() throws Exception {
        Field field = TypeDescriptorTest.class.getDeclaredField("userMap");
        
        assertEquals(Map.class, field.getType());
        
        java.lang.reflect.Type genericType = field.getGenericType();
        assertTrue(genericType instanceof java.lang.reflect.ParameterizedType);
        
        java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericType;
        java.lang.reflect.Type[] args = paramType.getActualTypeArguments();
        assertEquals(2, args.length);
        assertEquals(String.class, args[0]);
        assertEquals(User.class, args[1]);
    }

    @Test
    public void testUserClassProperties() {
        User user = new User("John", 30, "john@example.com");
        
        assertEquals("John", user.getName());
        assertEquals(30, user.getAge());
        assertEquals("john@example.com", user.getEmail());
        
        user.setName("Jane");
        user.setAge(25);
        user.setEmail("jane@example.com");
        
        assertEquals("Jane", user.getName());
        assertEquals(25, user.getAge());
        assertEquals("jane@example.com", user.getEmail());
    }

    @Test
    public void testUserServiceProperties() throws Exception {
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
    }

    @Test
    public void testUserServiceUserOperations() {
        UserService service = new UserService();
        
        User user1 = new User("Alice", 25, "alice@example.com");
        User user2 = new User("Bob", 30, "bob@example.com");
        
        service.save(user1);
        service.save(user2);
        
        assertEquals(2, service.findAll().size());
        
        User found = service.findById(0L);
        assertNotNull(found);
        assertEquals("Alice", found.getName());
    }

    @Test
    public void testAnnotationRetention() {
        // Verify annotations are retained at runtime
        assertTrue(NotNull.class.isAnnotationPresent(java.lang.annotation.Retention.class));
        assertTrue(Size.class.isAnnotationPresent(java.lang.annotation.Retention.class));
        assertTrue(Range.class.isAnnotationPresent(java.lang.annotation.Retention.class));
        
        java.lang.annotation.Retention notNullRetention = NotNull.class.getAnnotation(java.lang.annotation.Retention.class);
        assertEquals(java.lang.annotation.RetentionPolicy.RUNTIME, notNullRetention.value());
    }

    @Test
    public void testAnnotationTargets() {
        // Verify annotation targets
        assertTrue(NotNull.class.isAnnotationPresent(java.lang.annotation.Target.class));
        assertTrue(Size.class.isAnnotationPresent(java.lang.annotation.Target.class));
        assertTrue(Range.class.isAnnotationPresent(java.lang.annotation.Target.class));
        
        java.lang.annotation.Target notNullTarget = NotNull.class.getAnnotation(java.lang.annotation.Target.class);
        java.lang.annotation.ElementType[] targets = notNullTarget.value();
        assertTrue(java.util.Arrays.asList(targets).contains(java.lang.annotation.ElementType.FIELD));
        assertTrue(java.util.Arrays.asList(targets).contains(java.lang.annotation.ElementType.METHOD));
        assertTrue(java.util.Arrays.asList(targets).contains(java.lang.annotation.ElementType.PARAMETER));
    }
}
