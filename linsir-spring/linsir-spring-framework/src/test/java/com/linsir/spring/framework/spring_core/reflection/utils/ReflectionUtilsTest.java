package com.linsir.spring.framework.spring_core.reflection.utils;

import com.linsir.spring.framework.spring_core.reflection.model.*;
import com.linsir.spring.framework.spring_core.reflection.service.BaseService;
import com.linsir.spring.framework.spring_core.reflection.service.OrderService;
import com.linsir.spring.framework.spring_core.reflection.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReflectionUtils 测试类
 * 测试反射工具的各种功能
 */
@DisplayName("ReflectionUtils 反射工具测试")
class ReflectionUtilsTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    // ==================== 字段操作测试 ====================

    @Test
    @DisplayName("测试查找字段 - 基本查找")
    void testFindField_Basic() {
        // 查找 UserService 中的 userRepository 字段
        Field field = ReflectionUtils.findField(UserService.class, "userRepository");
        assertNotNull(field, "应该找到 userRepository 字段");
        assertEquals("userRepository", field.getName(), "字段名应该匹配");
    }

    @Test
    @DisplayName("测试查找字段 - 指定类型")
    void testFindField_WithType() {
        // 查找指定类型的字段
        Field field = ReflectionUtils.findField(UserService.class, "secretKey", String.class);
        assertNotNull(field, "应该找到 secretKey 字段");
        assertEquals(String.class, field.getType(), "字段类型应该匹配");
    }

    @Test
    @DisplayName("测试查找字段 - 继承链查找")
    void testFindField_Inheritance() {
        // OrderService 继承 BaseService，查找父类字段
        Field field = ReflectionUtils.findField(OrderService.class, "serviceName");
        assertNotNull(field, "应该在父类中找到 serviceName 字段");
        assertEquals("serviceName", field.getName(), "字段名应该匹配");
    }

    @Test
    @DisplayName("测试查找字段 - 不存在的字段")
    void testFindField_NotFound() {
        Field field = ReflectionUtils.findField(UserService.class, "nonExistentField");
        assertNull(field, "不存在的字段应该返回 null");
    }

    @Test
    @DisplayName("测试获取字段值 - 私有字段")
    void testGetField_Private() {
        // 设置私有字段值
        userService.setSecretKey("test-secret-key");

        // 通过反射获取
        Field field = ReflectionUtils.findField(UserService.class, "secretKey");
        assertNotNull(field);

        Object value = ReflectionUtils.getField(field, userService);
        assertEquals("test-secret-key", value, "应该获取到正确的字段值");
    }

    @Test
    @DisplayName("测试获取字段值 - 静态字段")
    void testGetField_Static() {
        Field field = ReflectionUtils.findField(UserService.class, "SERVICE_NAME");
        assertNotNull(field);

        Object value = ReflectionUtils.getField(field, null);  // 静态字段传 null
        assertEquals("UserService", value, "应该获取到静态字段值");
    }

    @Test
    @DisplayName("测试设置字段值 - 私有字段")
    void testSetField_Private() {
        Field field = ReflectionUtils.findField(UserService.class, "secretKey");
        assertNotNull(field);

        // 设置新值
        ReflectionUtils.setField(field, userService, "new-secret-key");

        // 验证
        assertEquals("new-secret-key", userService.getSecretKey(), "字段值应该被设置");
    }

    @Test
    @DisplayName("测试遍历字段 - 所有字段")
    void testDoWithFields_All() {
        List<String> fieldNames = new ArrayList<>();

        ReflectionUtils.doWithFields(UserService.class, field -> {
            fieldNames.add(field.getName());
        });

        // 验证包含 UserService 和父类的字段
        assertTrue(fieldNames.contains("userRepository"), "应该包含 userRepository 字段");
        assertTrue(fieldNames.contains("secretKey"), "应该包含 secretKey 字段");
    }

    @Test
    @DisplayName("测试遍历字段 - 带过滤器")
    void testDoWithFields_WithFilter() {
        List<String> fieldNames = new ArrayList<>();

        // 只处理标记 @Autowired 的字段
        ReflectionUtils.doWithFields(UserService.class,
            field -> fieldNames.add(field.getName()),
            field -> field.isAnnotationPresent(Autowired.class)
        );

        assertEquals(1, fieldNames.size(), "应该只有一个 @Autowired 字段");
        assertEquals("userRepository", fieldNames.get(0), "应该是 userRepository 字段");
    }

    @Test
    @DisplayName("测试获取所有声明字段")
    void testGetAllDeclaredFields() {
        Field[] fields = ReflectionUtils.getAllDeclaredFields(UserService.class);
        assertTrue(fields.length > 0, "应该返回字段数组");

        // 验证包含 UserService 的字段
        boolean hasUserRepository = false;
        boolean hasSecretKey = false;
        for (Field field : fields) {
            if ("userRepository".equals(field.getName())) {
                hasUserRepository = true;
            }
            if ("secretKey".equals(field.getName())) {
                hasSecretKey = true;
            }
        }
        assertTrue(hasUserRepository, "应该包含 userRepository 字段");
        assertTrue(hasSecretKey, "应该包含 secretKey 字段");
    }

    // ==================== 方法操作测试 ====================

    @Test
    @DisplayName("测试查找方法 - 基本查找")
    void testFindMethod_Basic() {
        Method method = ReflectionUtils.findMethod(UserService.class, "findById", Long.class);
        assertNotNull(method, "应该找到 findById 方法");
        assertEquals("findById", method.getName(), "方法名应该匹配");
    }

    @Test
    @DisplayName("测试查找方法 - 继承链查找")
    void testFindMethod_Inheritance() {
        // OrderService 继承 BaseService，查找父类方法
        Method method = ReflectionUtils.findMethod(OrderService.class, "getServiceName");
        assertNotNull(method, "应该在父类中找到 getServiceName 方法");
    }

    @Test
    @DisplayName("测试查找方法 - 重载方法")
    void testFindMethod_Overloaded() {
        // 查找带参数的重载方法
        Method method = ReflectionUtils.findMethod(UserService.class, "findByUsername", String.class);
        assertNotNull(method, "应该找到 findByUsername(String) 方法");

        // 查找另一个重载方法
        Method method2 = ReflectionUtils.findMethod(UserService.class, "findByUsername", String.class, String.class);
        assertNotNull(method2, "应该找到 findByUsername(String, String) 方法");

        // 验证不是同一个方法
        assertNotEquals(method, method2, "两个重载方法应该不同");
    }

    @Test
    @DisplayName("测试调用方法 - 公共方法")
    void testInvokeMethod_Public() {
        Method method = ReflectionUtils.findMethod(UserService.class, "getServiceInfo");
        assertNotNull(method);

        Object result = ReflectionUtils.invokeMethod(method, userService);  // 实例方法
        assertEquals("UserService - User Management Service", result, "应该返回正确的服务信息");
    }

    @Test
    @DisplayName("测试调用方法 - 私有方法")
    void testInvokeMethod_Private() {
        Method method = ReflectionUtils.findMethod(UserService.class, "generateToken", Long.class);
        assertNotNull(method, "应该找到私有方法");

        Object result = ReflectionUtils.invokeMethod(method, userService, 123L);
        assertNotNull(result, "应该返回 token");
        assertTrue(result.toString().startsWith("token-123-"), "token 格式应该正确");
    }

    @Test
    @DisplayName("测试调用方法 - 受保护方法")
    void testInvokeMethod_Protected() {
        Method method = ReflectionUtils.findMethod(UserService.class, "logOperation", String.class);
        assertNotNull(method, "应该找到受保护方法");

        // 调用受保护方法
        assertDoesNotThrow(() -> {
            ReflectionUtils.invokeMethod(method, userService, "test operation");
        }, "应该成功调用受保护方法");
    }

    @Test
    @DisplayName("测试遍历方法 - 所有方法")
    void testDoWithMethods_All() {
        AtomicInteger count = new AtomicInteger(0);

        ReflectionUtils.doWithMethods(UserService.class, method -> {
            count.incrementAndGet();
        });

        assertTrue(count.get() > 0, "应该遍历到方法");
    }

    @Test
    @DisplayName("测试遍历方法 - 带过滤器")
    void testDoWithMethods_WithFilter() {
        List<String> methodNames = new ArrayList<>();

        // 只处理标记 @Transactional 的方法
        ReflectionUtils.doWithMethods(UserService.class,
            method -> methodNames.add(method.getName()),
            method -> method.isAnnotationPresent(Transactional.class)
        );

        // UserService 中有多个 @Transactional 方法
        assertTrue(methodNames.size() >= 3, "应该找到多个 @Transactional 方法");
        assertTrue(methodNames.contains("findById"), "应该包含 findById 方法");
        assertTrue(methodNames.contains("save"), "应该包含 save 方法");
    }

    @Test
    @DisplayName("测试获取所有声明方法")
    void testGetAllDeclaredMethods() {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(UserService.class);
        assertTrue(methods.length > 0, "应该返回方法数组");
    }

    @Test
    @DisplayName("测试获取唯一声明方法")
    void testGetUniqueDeclaredMethods() {
        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(UserService.class);
        assertTrue(methods.length > 0, "应该返回唯一方法数组");
    }

    // ==================== 访问控制测试 ====================

    @Test
    @DisplayName("测试设置可访问")
    void testMakeAccessible() {
        Field field = ReflectionUtils.findField(UserService.class, "secretKey");
        assertNotNull(field);
        assertFalse(field.isAccessible(), "私有字段默认不可访问");

        ReflectionUtils.makeAccessible(field);
        assertTrue(field.isAccessible(), "设置后应该可访问");
    }

    @Test
    @DisplayName("测试判断 public static final")
    void testIsPublicStaticFinal() {
        Field field = ReflectionUtils.findField(UserService.class, "SERVICE_NAME");
        assertNotNull(field);
        assertTrue(ReflectionUtils.isPublicStaticFinal(field), "SERVICE_NAME 应该是 public static final");

        Field privateField = ReflectionUtils.findField(UserService.class, "secretKey");
        assertNotNull(privateField);
        assertFalse(ReflectionUtils.isPublicStaticFinal(privateField), "secretKey 不是 public static final");
    }

    // ==================== 异常处理测试 ====================

    @Test
    @DisplayName("测试反射异常")
    void testReflectionException() {
        Field field = ReflectionUtils.findField(UserService.class, "userRepository");
        assertNotNull(field);

        // 尝试获取 null 对象的字段值
        ReflectionUtils.ReflectionException exception = assertThrows(
            ReflectionUtils.ReflectionException.class,
            () -> ReflectionUtils.getField(field, null),
            "应该抛出 ReflectionException"
        );

        assertNotNull(exception.getCause(), "异常应该包含原始原因");
    }

    // ==================== 类操作测试 ====================

    @Test
    @DisplayName("测试判断 CGLIB 代理类")
    void testIsCglibProxyClass() {
        assertFalse(ReflectionUtils.isCglibProxyClass(UserService.class), "普通类不是 CGLIB 代理");
    }

    @Test
    @DisplayName("测试获取用户定义的类")
    void testGetUserClass() {
        Class<?> userClass = ReflectionUtils.getUserClass(UserService.class);
        assertEquals(UserService.class, userClass, "普通类返回自身");
    }
}
