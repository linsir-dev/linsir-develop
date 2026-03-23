package com.linsir.spring.framework.spring_core.asserts;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 断言工具类
 * 
 * 提供一系列静态方法用于参数校验和状态检查，帮助消除样板代码，
 * 统一异常类型，提高代码可读性。
 * 
 * 设计特点：
 * 1. 所有方法都是静态的，无需实例化
 * 2. 类被声明为 final，防止被继承
 * 3. 私有构造器强制使用静态方法
 * 4. 条件不满足时立即抛出标准异常
 * 
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public final class Assert {

    /**
     * 私有构造器，防止实例化
     */
    private Assert() {
        throw new AssertionError("Assert 类不能实例化");
    }

    // ==================== 对象断言 ====================

    /**
     * 断言对象不为 null
     * 
     * @param object 要检查的对象
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果对象为 null
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言对象不为 null（延迟消息计算版本）
     * 
     * @param object 要检查的对象
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果对象为 null
     */
    public static void notNull(Object object, Supplier<String> messageSupplier) {
        if (object == null) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 断言对象为 null
     * 
     * @param object 要检查的对象
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果对象不为 null
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言对象为 null（延迟消息计算版本）
     * 
     * @param object 要检查的对象
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果对象不为 null
     */
    public static void isNull(Object object, Supplier<String> messageSupplier) {
        if (object != null) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    // ==================== 字符串断言 ====================

    /**
     * 断言字符串不为 null 且至少包含一个非空白字符
     * 
     * @param text 要检查的字符串
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果字符串为 null、空或仅包含空白字符
     */
    public static void hasText(String text, String message) {
        if (!hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言字符串不为 null 且至少包含一个非空白字符（延迟消息计算版本）
     * 
     * @param text 要检查的字符串
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果字符串为 null、空或仅包含空白字符
     */
    public static void hasText(String text, Supplier<String> messageSupplier) {
        if (!hasText(text)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 检查字符串是否包含非空白字符
     * 
     * @param text 要检查的字符串
     * @return true 如果字符串不为 null 且包含非空白字符
     */
    private static boolean hasText(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 断言字符串不为 null 且长度大于 0
     * 
     * @param text 要检查的字符串
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果字符串为 null 或空
     */
    public static void hasLength(String text, String message) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言字符串不为 null 且长度大于 0（延迟消息计算版本）
     * 
     * @param text 要检查的字符串
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果字符串为 null 或空
     */
    public static void hasLength(String text, Supplier<String> messageSupplier) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 断言字符串不包含指定的子串
     * 
     * @param textToSearch 要搜索的字符串
     * @param substring 不应出现的子串
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果字符串包含指定子串
     */
    public static void doesNotContain(String textToSearch, String substring, String message) {
        if (hasText(textToSearch) && hasText(substring) && textToSearch.contains(substring)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言字符串不包含指定的子串（延迟消息计算版本）
     * 
     * @param textToSearch 要搜索的字符串
     * @param substring 不应出现的子串
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果字符串包含指定子串
     */
    public static void doesNotContain(String textToSearch, String substring, Supplier<String> messageSupplier) {
        if (hasText(textToSearch) && hasText(substring) && textToSearch.contains(substring)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    // ==================== 布尔断言 ====================

    /**
     * 断言表达式为 true
     * 
     * @param expression 布尔表达式
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果表达式为 false
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式为 true（延迟消息计算版本）
     * 
     * @param expression 布尔表达式
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果表达式为 false
     */
    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 断言表达式为 false
     * 
     * @param expression 布尔表达式
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果表达式为 true
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式为 false（延迟消息计算版本）
     * 
     * @param expression 布尔表达式
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果表达式为 true
     */
    public static void isFalse(boolean expression, Supplier<String> messageSupplier) {
        if (expression) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    // ==================== 数组断言 ====================

    /**
     * 断言数组不为 null 且至少包含一个元素
     * 
     * @param array 要检查的数组
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果数组为 null 或空
     */
    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言数组不为 null 且至少包含一个元素（延迟消息计算版本）
     * 
     * @param array 要检查的数组
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果数组为 null 或空
     */
    public static void notEmpty(Object[] array, Supplier<String> messageSupplier) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 断言数组不包含 null 元素
     * 
     * @param array 要检查的数组
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果数组包含 null 元素
     */
    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    /**
     * 断言数组不包含 null 元素（延迟消息计算版本）
     * 
     * @param array 要检查的数组
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果数组包含 null 元素
     */
    public static void noNullElements(Object[] array, Supplier<String> messageSupplier) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalArgumentException(nullSafeGet(messageSupplier));
                }
            }
        }
    }

    // ==================== 集合断言 ====================

    /**
     * 断言集合不为 null 且至少包含一个元素
     * 
     * @param collection 要检查的集合
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果集合为 null 或空
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言集合不为 null 且至少包含一个元素（延迟消息计算版本）
     * 
     * @param collection 要检查的集合
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果集合为 null 或空
     */
    public static void notEmpty(Collection<?> collection, Supplier<String> messageSupplier) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    // ==================== Map 断言 ====================

    /**
     * 断言 Map 不为 null 且至少包含一个键值对
     * 
     * @param map 要检查的 Map
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果 Map 为 null 或空
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言 Map 不为 null 且至少包含一个键值对（延迟消息计算版本）
     * 
     * @param map 要检查的 Map
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果 Map 为 null 或空
     */
    public static void notEmpty(Map<?, ?> map, Supplier<String> messageSupplier) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    // ==================== 类型断言 ====================

    /**
     * 断言对象是指定类型的实例
     * 
     * @param type 期望的类型
     * @param obj 要检查的对象
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果对象不是指定类型的实例
     */
    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言对象是指定类型的实例（延迟消息计算版本）
     * 
     * @param type 期望的类型
     * @param obj 要检查的对象
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果对象不是指定类型的实例
     */
    public static void isInstanceOf(Class<?> type, Object obj, Supplier<String> messageSupplier) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    /**
     * 断言一个类型可以赋值给另一个类型
     * 
     * @param superType 超类型
     * @param subType 子类型
     * @param message 断言失败时的错误消息
     * @throws IllegalArgumentException 如果子类型不能赋值给超类型
     */
    public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
        notNull(superType, "Super type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言一个类型可以赋值给另一个类型（延迟消息计算版本）
     * 
     * @param superType 超类型
     * @param subType 子类型
     * @param messageSupplier 错误消息提供者
     * @throws IllegalArgumentException 如果子类型不能赋值给超类型
     */
    public static void isAssignable(Class<?> superType, Class<?> subType, Supplier<String> messageSupplier) {
        notNull(superType, "Super type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    // ==================== 状态断言 ====================

    /**
     * 断言对象状态满足条件
     * 
     * 与 isTrue 不同，state 抛出 IllegalStateException，
     * 用于表示对象状态错误而非参数错误
     * 
     * @param expression 布尔表达式
     * @param message 断言失败时的错误消息
     * @throws IllegalStateException 如果表达式为 false
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * 断言对象状态满足条件（延迟消息计算版本）
     * 
     * @param expression 布尔表达式
     * @param messageSupplier 错误消息提供者
     * @throws IllegalStateException 如果表达式为 false
     */
    public static void state(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalStateException(nullSafeGet(messageSupplier));
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 安全地获取 Supplier 提供的消息
     * 
     * @param messageSupplier 消息提供者
     * @return 消息字符串，如果 Supplier 为 null 则返回 null
     */
    private static String nullSafeGet(Supplier<String> messageSupplier) {
        return messageSupplier != null ? messageSupplier.get() : null;
    }
}
