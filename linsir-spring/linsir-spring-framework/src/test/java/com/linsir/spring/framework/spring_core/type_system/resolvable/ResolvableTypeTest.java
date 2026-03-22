package com.linsir.spring.framework.spring_core.type_system.resolvable;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * ResolvableType test class
 * Tests generic type resolution functionality
 */
public class ResolvableTypeTest {

    // Test fields with generic types
    private List<String> stringList;
    private Map<String, User> userMap;
    private List<List<Integer>> nestedList;

    @Test
    public void testUserClass() {
        User user = new User("John", 30);
        
        assertEquals("John", user.getName());
        assertEquals(Integer.valueOf(30), user.getAge());
        
        user.setName("Jane");
        user.setAge(25);
        
        assertEquals("Jane", user.getName());
        assertEquals(Integer.valueOf(25), user.getAge());
    }

    @Test
    public void testBaseServiceGenericTypes() {
        // Test that UserService extends BaseService<User, Long>
        Type genericSuperclass = UserService.class.getGenericSuperclass();
        assertTrue(genericSuperclass instanceof ParameterizedType);
        
        ParameterizedType paramType = (ParameterizedType) genericSuperclass;
        Type[] actualArgs = paramType.getActualTypeArguments();
        
        assertEquals(2, actualArgs.length);
        assertEquals(User.class, actualArgs[0]);
        assertEquals(Long.class, actualArgs[1]);
    }

    @Test
    public void testUserServiceOperations() {
        UserService service = new UserService();
        
        User user1 = new User("Alice", 25);
        User user2 = new User("Bob", 30);
        
        service.save(user1);
        service.save(user2);
        
        assertEquals(2, service.findAll().size());
        
        User found = service.findById(0L);
        assertNotNull(found);
        assertEquals("Alice", found.getName());
    }

    @Test
    public void testGenericFieldResolution() throws Exception {
        Field field = ResolvableTypeTest.class.getDeclaredField("stringList");
        
        assertEquals(List.class, field.getType());
        
        Type genericType = field.getGenericType();
        assertTrue(genericType instanceof ParameterizedType);
        
        ParameterizedType paramType = (ParameterizedType) genericType;
        Type[] args = paramType.getActualTypeArguments();
        assertEquals(1, args.length);
        assertEquals(String.class, args[0]);
    }

    @Test
    public void testMapFieldResolution() throws Exception {
        Field field = ResolvableTypeTest.class.getDeclaredField("userMap");
        
        assertEquals(Map.class, field.getType());
        
        Type genericType = field.getGenericType();
        assertTrue(genericType instanceof ParameterizedType);
        
        ParameterizedType paramType = (ParameterizedType) genericType;
        Type[] args = paramType.getActualTypeArguments();
        assertEquals(2, args.length);
        assertEquals(String.class, args[0]);
        assertEquals(User.class, args[1]);
    }

    @Test
    public void testNestedGenericResolution() throws Exception {
        Field field = ResolvableTypeTest.class.getDeclaredField("nestedList");
        
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
    }

    @Test
    public void testMethodReturnTypeResolution() throws Exception {
        Method method = UserController.class.getMethod("getAllUsers");
        
        Type returnType = method.getGenericReturnType();
        assertTrue(returnType instanceof ParameterizedType);
        
        ParameterizedType paramType = (ParameterizedType) returnType;
        Type[] args = paramType.getActualTypeArguments();
        assertEquals(1, args.length);
        assertEquals(User.class, args[0]);
    }

    @Test
    public void testMethodParameterResolution() throws Exception {
        Method method = UserController.class.getMethod("processUsers", List.class);
        
        Type[] paramTypes = method.getGenericParameterTypes();
        assertEquals(1, paramTypes.length);
        
        assertTrue(paramTypes[0] instanceof ParameterizedType);
        ParameterizedType paramType = (ParameterizedType) paramTypes[0];
        Type[] args = paramType.getActualTypeArguments();
        assertEquals(User.class, args[0]);
    }

    @Test
    public void testConfigGenericTypes() {
        // Test ConfigHolder generic class
        Config.ConfigHolder<Config.DatabaseConfig> holder = new Config.ConfigHolder<>("db", new Config.DatabaseConfig("url", "user", "pass"));
        
        assertEquals("db", holder.getName());
        assertNotNull(holder.getConfig());
        assertEquals("url", holder.getConfig().getUrl());
    }

    @Test
    public void testTypeAssignability() {
        // Test type assignability
        assertTrue(List.class.isAssignableFrom(java.util.ArrayList.class));
        assertTrue(Map.class.isAssignableFrom(java.util.HashMap.class));
        assertFalse(String.class.isAssignableFrom(Integer.class));
    }

    @Test
    public void testArrayType() {
        Class<?> stringArrayClass = String[].class;
        assertTrue(stringArrayClass.isArray());
        assertEquals(String.class, stringArrayClass.getComponentType());
    }
}
