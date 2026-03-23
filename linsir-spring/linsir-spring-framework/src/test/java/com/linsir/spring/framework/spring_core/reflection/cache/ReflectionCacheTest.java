package com.linsir.spring.framework.spring_core.reflection.cache;

import com.linsir.spring.framework.spring_core.reflection.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReflectionCache 测试类
 * 测试反射缓存的功能
 */
@DisplayName("ReflectionCache 反射缓存测试")
class ReflectionCacheTest {

    @BeforeEach
    void setUp() {
        ReflectionCache.clearCache();
    }

    @Test
    @DisplayName("测试获取声明字段 - 缓存")
    void testGetDeclaredFields_Cache() {
        // 第一次获取
        Field[] fields1 = ReflectionCache.getDeclaredFields(UserService.class);
        assertTrue(fields1.length > 0, "应该返回字段数组");

        // 第二次获取（应该从缓存）
        Field[] fields2 = ReflectionCache.getDeclaredFields(UserService.class);
        assertSame(fields1, fields2, "应该返回缓存的同一数组");

        // 验证缓存统计
        assertEquals(1, ReflectionCache.getCachedFieldCount(), "应该缓存 1 个类的字段");
    }

    @Test
    @DisplayName("测试获取声明方法 - 缓存")
    void testGetDeclaredMethods_Cache() {
        // 第一次获取
        Method[] methods1 = ReflectionCache.getDeclaredMethods(UserService.class);
        assertTrue(methods1.length > 0, "应该返回方法数组");

        // 第二次获取（应该从缓存）
        Method[] methods2 = ReflectionCache.getDeclaredMethods(UserService.class);
        assertSame(methods1, methods2, "应该返回缓存的同一数组");

        // 验证缓存统计
        assertEquals(1, ReflectionCache.getCachedMethodCount(), "应该缓存 1 个类的方法");
    }

    @Test
    @DisplayName("测试查找字段 - 缓存")
    void testFindField_Cache() {
        // 第一次查找
        Field field1 = ReflectionCache.findField(UserService.class, "userRepository");
        assertNotNull(field1, "应该找到字段");

        // 第二次查找（应该从缓存）
        Field field2 = ReflectionCache.findField(UserService.class, "userRepository");
        assertSame(field1, field2, "应该返回缓存的同一字段");
    }

    @Test
    @DisplayName("测试查找字段 - 带类型缓存")
    void testFindField_WithTypeCache() {
        // 查找带类型的字段
        Field field1 = ReflectionCache.findField(UserService.class, "secretKey", String.class);
        assertNotNull(field1, "应该找到字段");

        // 第二次查找（应该从缓存）
        Field field2 = ReflectionCache.findField(UserService.class, "secretKey", String.class);
        assertSame(field1, field2, "应该返回缓存的同一字段");
    }

    @Test
    @DisplayName("测试查找方法 - 缓存")
    void testFindMethod_Cache() {
        // 第一次查找
        Method method1 = ReflectionCache.findMethod(UserService.class, "findById", Long.class);
        assertNotNull(method1, "应该找到方法");

        // 第二次查找（应该从缓存）
        Method method2 = ReflectionCache.findMethod(UserService.class, "findById", Long.class);
        assertSame(method1, method2, "应该返回缓存的同一方法");
    }

    @Test
    @DisplayName("测试清空所有缓存")
    void testClearCache() {
        // 填充缓存
        ReflectionCache.getDeclaredFields(UserService.class);
        ReflectionCache.getDeclaredMethods(UserService.class);
        ReflectionCache.findField(UserService.class, "userRepository");
        ReflectionCache.findMethod(UserService.class, "findById", Long.class);

        // 验证有缓存
        assertTrue(ReflectionCache.getCachedFieldCount() > 0, "应该有字段缓存");

        // 清空缓存
        ReflectionCache.clearCache();

        // 验证缓存已清空
        assertEquals(0, ReflectionCache.getCachedFieldCount(), "字段缓存应该被清空");
        assertEquals(0, ReflectionCache.getCachedMethodCount(), "方法缓存应该被清空");
    }

    @Test
    @DisplayName("测试清空指定类的缓存")
    void testClearCache_Class() {
        // 填充多个类的缓存
        ReflectionCache.getDeclaredFields(UserService.class);
        ReflectionCache.getDeclaredFields(String.class);

        // 验证有缓存
        assertEquals(2, ReflectionCache.getCachedFieldCount(), "应该有 2 个类的字段缓存");

        // 清空 UserService 的缓存
        ReflectionCache.clearCache(UserService.class);

        // 验证 UserService 的缓存被清空，但 String 的缓存还在
        assertEquals(1, ReflectionCache.getCachedFieldCount(), "应该只剩 1 个类的字段缓存");
    }

    @Test
    @DisplayName("测试获取缓存统计信息")
    void testGetCacheStats() {
        // 填充缓存
        ReflectionCache.getDeclaredFields(UserService.class);
        ReflectionCache.getDeclaredMethods(UserService.class);

        // 获取统计信息
        String stats = ReflectionCache.getCacheStats();

        // 验证统计信息包含关键内容
        assertNotNull(stats, "应该返回统计信息");
        assertTrue(stats.contains("Declared Fields"), "应该包含字段统计");
        assertTrue(stats.contains("Declared Methods"), "应该包含方法统计");
    }

    @Test
    @DisplayName("测试处理 null 类")
    void testHandleNullClass() {
        Field[] fields = ReflectionCache.getDeclaredFields(null);
        assertEquals(0, fields.length, "null 类应该返回空数组");

        Method[] methods = ReflectionCache.getDeclaredMethods(null);
        assertEquals(0, methods.length, "null 类应该返回空数组");
    }

    @Test
    @DisplayName("测试缓存并发安全性")
    void testCacheConcurrency() throws InterruptedException {
        // 多线程并发访问缓存
        int threadCount = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                // 并发获取缓存
                ReflectionCache.getDeclaredFields(UserService.class);
                ReflectionCache.findField(UserService.class, "userRepository");
                ReflectionCache.findMethod(UserService.class, "findById", Long.class);
            });
            threads[i].start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 验证缓存正常
        assertEquals(1, ReflectionCache.getCachedFieldCount(), "应该只有 1 个类的字段缓存");
    }
}
