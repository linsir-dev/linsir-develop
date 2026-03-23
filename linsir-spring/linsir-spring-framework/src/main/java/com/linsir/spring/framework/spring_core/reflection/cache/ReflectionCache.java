package com.linsir.spring.framework.spring_core.reflection.cache;

import com.linsir.spring.framework.spring_core.reflection.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 反射结果缓存
 * 用于优化反射操作的性能
 *
 * 缓存内容：
 * 1. 类的所有字段
 * 2. 类的所有方法
 * 3. 单个字段查找结果
 * 4. 单个方法查找结果
 */
public class ReflectionCache {

    /**
     * 字段缓存
     * Key: 类对象
     * Value: 字段数组
     */
    private static final ConcurrentMap<Class<?>, Field[]> declaredFieldsCache = new ConcurrentHashMap<>();

    /**
     * 方法缓存
     * Key: 类对象
     * Value: 方法数组
     */
    private static final ConcurrentMap<Class<?>, Method[]> declaredMethodsCache = new ConcurrentHashMap<>();

    /**
     * 字段查找缓存
     * Key: 类名 + 字段名
     * Value: 字段对象
     */
    private static final ConcurrentMap<String, Field> fieldLookupCache = new ConcurrentHashMap<>();

    /**
     * 方法查找缓存
     * Key: 类名 + 方法签名
     * Value: 方法对象
     */
    private static final ConcurrentMap<String, Method> methodLookupCache = new ConcurrentHashMap<>();

    /**
     * 获取类的所有声明字段（包含父类）
     *
     * @param clazz 类对象
     * @return 字段数组
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        if (clazz == null) {
            return new Field[0];
        }
        return declaredFieldsCache.computeIfAbsent(clazz, 
            k -> ReflectionUtils.getAllDeclaredFields(k));
    }

    /**
     * 获取类的所有声明方法（包含父类）
     *
     * @param clazz 类对象
     * @return 方法数组
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        if (clazz == null) {
            return new Method[0];
        }
        return declaredMethodsCache.computeIfAbsent(clazz,
            k -> ReflectionUtils.getAllDeclaredMethods(k));
    }

    /**
     * 查找字段（带缓存）
     *
     * @param clazz     类对象
     * @param fieldName 字段名
     * @return 字段对象
     */
    public static Field findField(Class<?> clazz, String fieldName) {
        String key = buildFieldKey(clazz, fieldName);
        return fieldLookupCache.computeIfAbsent(key, k -> 
            ReflectionUtils.findField(clazz, fieldName));
    }

    /**
     * 查找字段（带缓存，可指定类型）
     *
     * @param clazz     类对象
     * @param fieldName 字段名
     * @param type      字段类型
     * @return 字段对象
     */
    public static Field findField(Class<?> clazz, String fieldName, Class<?> type) {
        String key = buildFieldKey(clazz, fieldName) + "#" + (type != null ? type.getName() : "null");
        return fieldLookupCache.computeIfAbsent(key, k ->
            ReflectionUtils.findField(clazz, fieldName, type));
    }

    /**
     * 查找方法（带缓存）
     *
     * @param clazz      类对象
     * @param methodName 方法名
     * @param paramTypes 参数类型
     * @return 方法对象
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        String key = buildMethodKey(clazz, methodName, paramTypes);
        return methodLookupCache.computeIfAbsent(key, k ->
            ReflectionUtils.findMethod(clazz, methodName, paramTypes));
    }

    /**
     * 构建字段缓存 Key
     */
    private static String buildFieldKey(Class<?> clazz, String fieldName) {
        return clazz.getName() + "." + fieldName;
    }

    /**
     * 构建方法缓存 Key
     */
    private static String buildMethodKey(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        StringBuilder key = new StringBuilder(clazz.getName());
        key.append(".").append(methodName);
        key.append("(");
        if (paramTypes != null && paramTypes.length > 0) {
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) {
                    key.append(",");
                }
                key.append(paramTypes[i] != null ? paramTypes[i].getName() : "null");
            }
        }
        key.append(")");
        return key.toString();
    }

    /**
     * 清空所有缓存
     */
    public static void clearCache() {
        declaredFieldsCache.clear();
        declaredMethodsCache.clear();
        fieldLookupCache.clear();
        methodLookupCache.clear();
    }

    /**
     * 清空指定类的缓存
     *
     * @param clazz 类对象
     */
    public static void clearCache(Class<?> clazz) {
        if (clazz == null) {
            return;
        }
        declaredFieldsCache.remove(clazz);
        declaredMethodsCache.remove(clazz);

        // 清理字段查找缓存
        String classPrefix = clazz.getName() + ".";
        fieldLookupCache.keySet().removeIf(key -> key.startsWith(classPrefix));

        // 清理方法查找缓存
        methodLookupCache.keySet().removeIf(key -> key.startsWith(classPrefix));
    }

    /**
     * 获取缓存统计信息
     *
     * @return 统计信息字符串
     */
    public static String getCacheStats() {
        return String.format(
            "ReflectionCache Stats:\n" +
            "  - Declared Fields: %d\n" +
            "  - Declared Methods: %d\n" +
            "  - Field Lookups: %d\n" +
            "  - Method Lookups: %d",
            declaredFieldsCache.size(),
            declaredMethodsCache.size(),
            fieldLookupCache.size(),
            methodLookupCache.size()
        );
    }

    /**
     * 获取缓存的字段数量
     *
     * @return 字段数量
     */
    public static int getCachedFieldCount() {
        return declaredFieldsCache.size();
    }

    /**
     * 获取缓存的方法数量
     *
     * @return 方法数量
     */
    public static int getCachedMethodCount() {
        return declaredMethodsCache.size();
    }
}
