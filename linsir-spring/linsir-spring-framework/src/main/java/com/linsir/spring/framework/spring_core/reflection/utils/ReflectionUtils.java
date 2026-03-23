package com.linsir.spring.framework.spring_core.reflection.utils;

import java.lang.reflect.*;
import java.util.*;

/**
 * Spring 风格的反射工具类
 * 提供字段、方法、类的全方位反射操作支持
 *
 * 设计特点：
 * 1. 统一异常处理：将 Checked Exception 转换为 RuntimeException
 * 2. 支持继承链：自动查找父类的字段和方法
 * 3. 回调接口：支持自定义字段和方法处理逻辑
 * 4. 访问控制：自动处理私有成员的访问权限
 */
public class ReflectionUtils {

    /**
     * 字段回调接口
     * 用于自定义字段处理逻辑
     */
    @FunctionalInterface
    public interface FieldCallback {
        /**
         * 处理字段
         *
         * @param field 字段对象
         * @throws IllegalAccessException 访问异常
         */
        void doWith(Field field) throws IllegalAccessException;
    }

    /**
     * 字段过滤器接口
     * 用于筛选需要处理的字段
     */
    @FunctionalInterface
    public interface FieldFilter {
        /**
         * 判断是否匹配
         *
         * @param field 字段对象
         * @return true 表示匹配，需要处理
         */
        boolean matches(Field field);
    }

    /**
     * 方法回调接口
     * 用于自定义方法处理逻辑
     */
    @FunctionalInterface
    public interface MethodCallback {
        /**
         * 处理方法
         *
         * @param method 方法对象
         * @throws IllegalAccessException 访问异常
         */
        void doWith(Method method) throws IllegalAccessException;
    }

    /**
     * 方法过滤器接口
     * 用于筛选需要处理的方法
     */
    @FunctionalInterface
    public interface MethodFilter {
        /**
         * 判断是否匹配
         *
         * @param method 方法对象
         * @return true 表示匹配，需要处理
         */
        boolean matches(Method method);
    }

    /**
     * 反射异常类
     * 统一包装反射操作中的异常
     */
    public static class ReflectionException extends RuntimeException {
        public ReflectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ==================== 字段操作 ====================

    /**
     * 查找字段（包含父类）
     *
     * @param clazz     目标类
     * @param fieldName 字段名
     * @return 字段对象，未找到返回 null
     */
    public static Field findField(Class<?> clazz, String fieldName) {
        return findField(clazz, fieldName, null);
    }

    /**
     * 查找字段（包含父类，可指定类型）
     *
     * @param clazz     目标类
     * @param fieldName 字段名
     * @param type      字段类型（可选）
     * @return 字段对象，未找到返回 null
     */
    public static Field findField(Class<?> clazz, String fieldName, Class<?> type) {
        Class<?> searchType = clazz;
        while (searchType != null && searchType != Object.class) {
            try {
                Field field = searchType.getDeclaredField(fieldName);
                if (type == null || type.equals(field.getType())) {
                    return field;
                }
            } catch (NoSuchFieldException e) {
                // 继续查找父类
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 获取字段值
     *
     * @param field  字段对象
     * @param target 目标对象（静态字段传 null）
     * @return 字段值
     */
    public static Object getField(Field field, Object target) {
        try {
            makeAccessible(field);
            return field.get(target);
        } catch (NullPointerException e) {
            throw new ReflectionException("无法获取字段值（目标对象为null或非静态字段）: " + field.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("无法获取字段值: " + field.getName(), e);
        }
    }

    /**
     * 设置字段值
     *
     * @param field  字段对象
     * @param target 目标对象（静态字段传 null）
     * @param value  字段值
     */
    public static void setField(Field field, Object target, Object value) {
        try {
            makeAccessible(field);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("无法设置字段值: " + field.getName(), e);
        }
    }

    /**
     * 遍历所有字段（包含父类）
     *
     * @param clazz    目标类
     * @param callback 字段回调
     */
    public static void doWithFields(Class<?> clazz, FieldCallback callback) {
        doWithFields(clazz, callback, null);
    }

    /**
     * 遍历所有字段（包含父类，带过滤器）
     *
     * @param clazz    目标类
     * @param callback 字段回调
     * @param filter   字段过滤器（可选）
     */
    public static void doWithFields(Class<?> clazz, FieldCallback callback, FieldFilter filter) {
        Class<?> targetClass = clazz;
        do {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                if (filter == null || filter.matches(field)) {
                    try {
                        callback.doWith(field);
                    } catch (IllegalAccessException e) {
                        throw new ReflectionException("字段处理失败: " + field.getName(), e);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
    }

    /**
     * 获取所有声明的字段（包含父类，去重）
     *
     * @param clazz 目标类
     * @return 字段数组
     */
    public static Field[] getAllDeclaredFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        doWithFields(clazz, fields::add);
        return fields.toArray(new Field[0]);
    }

    // ==================== 方法操作 ====================

    /**
     * 查找方法（包含父类）
     *
     * @param clazz      目标类
     * @param methodName 方法名
     * @param paramTypes 参数类型
     * @return 方法对象，未找到返回 null
     */
    public static Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        Class<?> searchType = clazz;
        while (searchType != null && searchType != Object.class) {
            try {
                return searchType.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
                // 继续查找父类
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    /**
     * 调用方法
     *
     * @param method 方法对象
     * @param target 目标对象（静态方法传 null）
     * @param args   方法参数
     * @return 方法返回值
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            makeAccessible(method);
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            throw new ReflectionException("无法调用方法: " + method.getName(), e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException("方法执行异常: " + method.getName(), e.getTargetException());
        }
    }

    /**
     * 遍历所有方法（包含父类）
     *
     * @param clazz    目标类
     * @param callback 方法回调
     */
    public static void doWithMethods(Class<?> clazz, MethodCallback callback) {
        doWithMethods(clazz, callback, null);
    }

    /**
     * 遍历所有方法（包含父类，带过滤器）
     *
     * @param clazz    目标类
     * @param callback 方法回调
     * @param filter   方法过滤器（可选）
     */
    public static void doWithMethods(Class<?> clazz, MethodCallback callback, MethodFilter filter) {
        Class<?> targetClass = clazz;
        do {
            Method[] methods = targetClass.getDeclaredMethods();
            for (Method method : methods) {
                if (filter == null || filter.matches(method)) {
                    try {
                        callback.doWith(method);
                    } catch (IllegalAccessException e) {
                        throw new ReflectionException("方法处理失败: " + method.getName(), e);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
    }

    /**
     * 获取所有声明的方法（包含父类，去重）
     *
     * @param clazz 目标类
     * @return 方法数组
     */
    public static Method[] getAllDeclaredMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        doWithMethods(clazz, methods::add);
        return methods.toArray(new Method[0]);
    }

    /**
     * 获取唯一声明的方法（桥接方法去重）
     *
     * @param clazz 目标类
     * @return 方法数组
     */
    public static Method[] getUniqueDeclaredMethods(Class<?> clazz) {
        Set<Method> uniqueMethods = new LinkedHashSet<>();
        doWithMethods(clazz, uniqueMethods::add);
        return uniqueMethods.toArray(new Method[0]);
    }

    // ==================== 访问控制 ====================

    /**
     * 设置可访问（处理私有成员）
     *
     * @param accessible 可访问对象
     */
    public static void makeAccessible(AccessibleObject accessible) {
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }
    }

    /**
     * 判断是否为 public static final
     *
     * @param field 字段对象
     * @return true 表示是常量字段
     */
    public static boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) &&
               Modifier.isStatic(modifiers) &&
               Modifier.isFinal(modifiers);
    }

    // ==================== 类操作 ====================

    /**
     * 判断是否为 CGLIB 代理类
     *
     * @param clazz 类对象
     * @return true 表示是 CGLIB 代理类
     */
    public static boolean isCglibProxyClass(Class<?> clazz) {
        return clazz != null && clazz.getName().contains("$$");
    }

    /**
     * 获取用户定义的类（处理代理类）
     *
     * @param clazz 类对象
     * @return 用户定义的类
     */
    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz.getName().contains("$$")) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }
        return clazz;
    }
}
