package com.linsir.spring.framework.spring_core.asserts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Assert 断言工具测试类
 * 
 * 测试 Assert 类的所有断言方法，包括：
 * - 对象断言（notNull, isNull）
 * - 字符串断言（hasText, hasLength, doesNotContain）
 * - 布尔断言（isTrue, isFalse）
 * - 数组断言（notEmpty, noNullElements）
 * - 集合断言（notEmpty）
 * - Map断言（notEmpty）
 * - 类型断言（isInstanceOf, isAssignable）
 * - 状态断言（state）
 * 
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
@DisplayName("Assert 断言工具测试")
class AssertTest {

    // ==================== 对象断言测试 ====================

    @Test
    @DisplayName("测试 notNull - 对象不为 null 时不抛出异常")
    void testNotNullWithNonNullObject() {
        // 应该不抛出异常
        assertDoesNotThrow(() -> Assert.notNull("test", "Object must not be null"));
        assertDoesNotThrow(() -> Assert.notNull(123, "Object must not be null"));
        assertDoesNotThrow(() -> Assert.notNull(new Object(), "Object must not be null"));
    }

    @Test
    @DisplayName("测试 notNull - 对象为 null 时抛出 IllegalArgumentException")
    void testNotNullWithNullObject() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notNull(null, "Object must not be null")
        );
        assertEquals("Object must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("测试 notNull - 使用 Supplier 延迟计算消息")
    void testNotNullWithSupplier() {
        // 成功时，Supplier 不应被调用
        assertDoesNotThrow(() -> Assert.notNull("test", () -> "This should not be called"));

        // 失败时，Supplier 应该被调用
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notNull(null, () -> "Custom message from supplier")
        );
        assertEquals("Custom message from supplier", exception.getMessage());
    }

    @Test
    @DisplayName("测试 isNull - 对象为 null 时不抛出异常")
    void testIsNullWithNullObject() {
        assertDoesNotThrow(() -> Assert.isNull(null, "Object must be null"));
    }

    @Test
    @DisplayName("测试 isNull - 对象不为 null 时抛出 IllegalArgumentException")
    void testIsNullWithNonNullObject() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isNull("test", "Object must be null")
        );
        assertEquals("Object must be null", exception.getMessage());
    }

    // ==================== 字符串断言测试 ====================

    @Test
    @DisplayName("测试 hasText - 字符串包含非空白字符时不抛出异常")
    void testHasTextWithValidString() {
        assertDoesNotThrow(() -> Assert.hasText("hello", "Text must not be empty"));
        assertDoesNotThrow(() -> Assert.hasText("  hello  ", "Text must not be empty"));
        assertDoesNotThrow(() -> Assert.hasText("a", "Text must not be empty"));
    }

    @Test
    @DisplayName("测试 hasText - 字符串为 null 时抛出异常")
    void testHasTextWithNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.hasText(null, "Text must not be empty")
        );
        assertEquals("Text must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("测试 hasText - 字符串为空时抛出异常")
    void testHasTextWithEmptyString() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.hasText("", "Text must not be empty")
        );
        assertEquals("Text must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("测试 hasText - 字符串仅包含空白字符时抛出异常")
    void testHasTextWithWhitespaceOnly() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.hasText("   ", "Text must not be empty")
        );
        assertEquals("Text must not be empty", exception.getMessage());

        exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.hasText("\t\n\r", "Text must not be empty")
        );
        assertEquals("Text must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("测试 hasText - 使用 Supplier 延迟计算消息")
    void testHasTextWithSupplier() {
        assertDoesNotThrow(() -> Assert.hasText("hello", () -> "Should not be called"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.hasText(null, () -> "Supplier message")
        );
        assertEquals("Supplier message", exception.getMessage());
    }

    @Test
    @DisplayName("测试 hasLength - 字符串有长度时不抛出异常")
    void testHasLengthWithValidString() {
        assertDoesNotThrow(() -> Assert.hasLength("hello", "Text must have length"));
        assertDoesNotThrow(() -> Assert.hasLength(" ", "Text must have length"));
        assertDoesNotThrow(() -> Assert.hasLength("a", "Text must have length"));
    }

    @Test
    @DisplayName("测试 hasLength - 字符串为 null 时抛出异常")
    void testHasLengthWithNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.hasLength(null, "Text must have length")
        );
        assertEquals("Text must have length", exception.getMessage());
    }

    @Test
    @DisplayName("测试 hasLength - 字符串为空时抛出异常")
    void testHasLengthWithEmptyString() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.hasLength("", "Text must have length")
        );
        assertEquals("Text must have length", exception.getMessage());
    }

    @Test
    @DisplayName("测试 doesNotContain - 字符串不包含子串时不抛出异常")
    void testDoesNotContainWithValidString() {
        assertDoesNotThrow(() -> Assert.doesNotContain("hello", "world", "Text must not contain substring"));
        assertDoesNotThrow(() -> Assert.doesNotContain("hello", "xyz", "Text must not contain substring"));
    }

    @Test
    @DisplayName("测试 doesNotContain - 字符串包含子串时抛出异常")
    void testDoesNotContainWithContainingString() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.doesNotContain("hello world", "world", "Text must not contain 'world'")
        );
        assertEquals("Text must not contain 'world'", exception.getMessage());
    }

    @Test
    @DisplayName("测试 doesNotContain - 字符串为 null 时不抛出异常")
    void testDoesNotContainWithNullString() {
        assertDoesNotThrow(() -> Assert.doesNotContain(null, "test", "Message"));
    }

    @Test
    @DisplayName("测试 doesNotContain - 子串为 null 时不抛出异常")
    void testDoesNotContainWithNullSubstring() {
        assertDoesNotThrow(() -> Assert.doesNotContain("hello", null, "Message"));
    }

    // ==================== 布尔断言测试 ====================

    @Test
    @DisplayName("测试 isTrue - 表达式为 true 时不抛出异常")
    void testIsTrueWithTrueExpression() {
        assertDoesNotThrow(() -> Assert.isTrue(true, "Expression must be true"));
        assertDoesNotThrow(() -> Assert.isTrue(1 == 1, "Expression must be true"));
        assertDoesNotThrow(() -> Assert.isTrue("hello".length() > 0, "Expression must be true"));
    }

    @Test
    @DisplayName("测试 isTrue - 表达式为 false 时抛出异常")
    void testIsTrueWithFalseExpression() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isTrue(false, "Expression must be true")
        );
        assertEquals("Expression must be true", exception.getMessage());

        exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isTrue(1 == 2, "Expression must be true")
        );
        assertEquals("Expression must be true", exception.getMessage());
    }

    @Test
    @DisplayName("测试 isTrue - 使用 Supplier 延迟计算消息")
    void testIsTrueWithSupplier() {
        assertDoesNotThrow(() -> Assert.isTrue(true, () -> "Should not be called"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isTrue(false, () -> "Supplier message for false")
        );
        assertEquals("Supplier message for false", exception.getMessage());
    }

    @Test
    @DisplayName("测试 isFalse - 表达式为 false 时不抛出异常")
    void testIsFalseWithFalseExpression() {
        assertDoesNotThrow(() -> Assert.isFalse(false, "Expression must be false"));
        assertDoesNotThrow(() -> Assert.isFalse(1 == 2, "Expression must be false"));
    }

    @Test
    @DisplayName("测试 isFalse - 表达式为 true 时抛出异常")
    void testIsFalseWithTrueExpression() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isFalse(true, "Expression must be false")
        );
        assertEquals("Expression must be false", exception.getMessage());
    }

    // ==================== 数组断言测试 ====================

    @Test
    @DisplayName("测试 notEmpty (数组) - 数组不为空时不抛出异常")
    void testNotEmptyArrayWithValidArray() {
        assertDoesNotThrow(() -> Assert.notEmpty(new Object[]{"a"}, "Array must not be empty"));
        assertDoesNotThrow(() -> Assert.notEmpty(new String[]{"hello"}, "Array must not be empty"));
        assertDoesNotThrow(() -> Assert.notEmpty(new Integer[]{1, 2, 3}, "Array must not be empty"));
    }

    @Test
    @DisplayName("测试 notEmpty (数组) - 数组为 null 时抛出异常")
    void testNotEmptyArrayWithNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty((Object[]) null, "Array must not be empty")
        );
        assertEquals("Array must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("测试 notEmpty (数组) - 数组为空时抛出异常")
    void testNotEmptyArrayWithEmptyArray() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty(new Object[]{}, "Array must not be empty")
        );
        assertEquals("Array must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("测试 noNullElements - 数组不包含 null 元素时不抛出异常")
    void testNoNullElementsWithValidArray() {
        assertDoesNotThrow(() -> Assert.noNullElements(new Object[]{"a", "b"}, "Array must not contain null"));
        assertDoesNotThrow(() -> Assert.noNullElements(new Integer[]{1, 2, 3}, "Array must not contain null"));
    }

    @Test
    @DisplayName("测试 noNullElements - 数组为 null 时不抛出异常")
    void testNoNullElementsWithNullArray() {
        assertDoesNotThrow(() -> Assert.noNullElements((Object[]) null, "Array must not contain null"));
    }

    @Test
    @DisplayName("测试 noNullElements - 数组包含 null 元素时抛出异常")
    void testNoNullElementsWithNullElement() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.noNullElements(new Object[]{"a", null, "b"}, "Array must not contain null")
        );
        assertEquals("Array must not contain null", exception.getMessage());
    }

    @Test
    @DisplayName("测试 noNullElements - 使用 Supplier 延迟计算消息")
    void testNoNullElementsWithSupplier() {
        assertDoesNotThrow(() -> Assert.noNullElements(new Object[]{"a"}, () -> "Should not be called"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.noNullElements(new Object[]{null}, () -> "Supplier message for null element")
        );
        assertEquals("Supplier message for null element", exception.getMessage());
    }

    // ==================== 集合断言测试 ====================

    @Test
    @DisplayName("测试 notEmpty (Collection) - 集合不为空时不抛出异常")
    void testNotEmptyCollectionWithValidCollection() {
        assertDoesNotThrow(() -> Assert.notEmpty(Arrays.asList("a"), "Collection must not be empty"));
        assertDoesNotThrow(() -> Assert.notEmpty(Collections.singletonList("item"), "Collection must not be empty"));
        
        Set<String> set = new HashSet<>();
        set.add("element");
        assertDoesNotThrow(() -> Assert.notEmpty(set, "Collection must not be empty"));
    }

    @Test
    @DisplayName("测试 notEmpty (Collection) - 集合为 null 时抛出异常")
    void testNotEmptyCollectionWithNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty((Collection<?>) null, "Collection must not be empty")
        );
        assertEquals("Collection must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("测试 notEmpty (Collection) - 集合为空时抛出异常")
    void testNotEmptyCollectionWithEmptyCollection() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty(Collections.emptyList(), "Collection must not be empty")
        );
        assertEquals("Collection must not be empty", exception.getMessage());

        exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty(new ArrayList<>(), "Collection must not be empty")
        );
        assertEquals("Collection must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("测试 notEmpty (Collection) - 使用 Supplier 延迟计算消息")
    void testNotEmptyCollectionWithSupplier() {
        assertDoesNotThrow(() -> Assert.notEmpty(Arrays.asList("a"), () -> "Should not be called"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty((Collection<?>) null, () -> "Supplier message for empty collection")
        );
        assertEquals("Supplier message for empty collection", exception.getMessage());
    }

    // ==================== Map 断言测试 ====================

    @Test
    @DisplayName("测试 notEmpty (Map) - Map 不为空时不抛出异常")
    void testNotEmptyMapWithValidMap() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        assertDoesNotThrow(() -> Assert.notEmpty(map, "Map must not be empty"));
    }

    @Test
    @DisplayName("测试 notEmpty (Map) - Map 为 null 时抛出异常")
    void testNotEmptyMapWithNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty((Map<?, ?>) null, "Map must not be empty")
        );
        assertEquals("Map must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("测试 notEmpty (Map) - Map 为空时抛出异常")
    void testNotEmptyMapWithEmptyMap() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty(Collections.emptyMap(), "Map must not be empty")
        );
        assertEquals("Map must not be empty", exception.getMessage());

        exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty(new HashMap<>(), "Map must not be empty")
        );
        assertEquals("Map must not be empty", exception.getMessage());
    }

    // ==================== 类型断言测试 ====================

    @Test
    @DisplayName("测试 isInstanceOf - 对象是指定类型的实例时不抛出异常")
    void testIsInstanceOfWithValidInstance() {
        assertDoesNotThrow(() -> Assert.isInstanceOf(String.class, "hello", "Must be String"));
        assertDoesNotThrow(() -> Assert.isInstanceOf(Number.class, Integer.valueOf(123), "Must be Number"));
        assertDoesNotThrow(() -> Assert.isInstanceOf(Object.class, new Object(), "Must be Object"));
    }

    @Test
    @DisplayName("测试 isInstanceOf - 类型参数为 null 时抛出异常")
    void testIsInstanceOfWithNullType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isInstanceOf(null, "hello", "Type must not be null")
        );
        assertEquals("Type to check against must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("测试 isInstanceOf - 对象不是指定类型的实例时抛出异常")
    void testIsInstanceOfWithInvalidInstance() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isInstanceOf(Integer.class, "hello", "Must be Integer")
        );
        assertEquals("Must be Integer", exception.getMessage());

        exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isInstanceOf(String.class, 123, "Must be String")
        );
        assertEquals("Must be String", exception.getMessage());
    }

    @Test
    @DisplayName("测试 isInstanceOf - 使用 Supplier 延迟计算消息")
    void testIsInstanceOfWithSupplier() {
        assertDoesNotThrow(() -> Assert.isInstanceOf(String.class, "hello", () -> "Should not be called"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isInstanceOf(Integer.class, "hello", () -> "Supplier message for type mismatch")
        );
        assertEquals("Supplier message for type mismatch", exception.getMessage());
    }

    @Test
    @DisplayName("测试 isAssignable - 类型可以赋值时不抛出异常")
    void testIsAssignableWithValidTypes() {
        assertDoesNotThrow(() -> Assert.isAssignable(Object.class, String.class, "Must be assignable"));
        assertDoesNotThrow(() -> Assert.isAssignable(Number.class, Integer.class, "Must be assignable"));
        assertDoesNotThrow(() -> Assert.isAssignable(CharSequence.class, String.class, "Must be assignable"));
    }

    @Test
    @DisplayName("测试 isAssignable - 超类型为 null 时抛出异常")
    void testIsAssignableWithNullSuperType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isAssignable(null, String.class, "Super type must not be null")
        );
        assertEquals("Super type to check against must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("测试 isAssignable - 子类型为 null 时抛出异常")
    void testIsAssignableWithNullSubType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isAssignable(Object.class, null, "Sub type must not be null")
        );
        assertEquals("Sub type must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("测试 isAssignable - 类型不能赋值时抛出异常")
    void testIsAssignableWithInvalidTypes() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isAssignable(String.class, Integer.class, "String is not assignable from Integer")
        );
        assertEquals("String is not assignable from Integer", exception.getMessage());

        exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.isAssignable(Number.class, String.class, "Number is not assignable from String")
        );
        assertEquals("Number is not assignable from String", exception.getMessage());
    }

    // ==================== 状态断言测试 ====================

    @Test
    @DisplayName("测试 state - 状态为 true 时不抛出异常")
    void testStateWithValidState() {
        assertDoesNotThrow(() -> Assert.state(true, "State must be valid"));
    }

    @Test
    @DisplayName("测试 state - 状态为 false 时抛出 IllegalStateException")
    void testStateWithInvalidState() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> Assert.state(false, "State must be valid")
        );
        assertEquals("State must be valid", exception.getMessage());
    }

    @Test
    @DisplayName("测试 state - 使用 Supplier 延迟计算消息")
    void testStateWithSupplier() {
        assertDoesNotThrow(() -> Assert.state(true, () -> "Should not be called"));

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> Assert.state(false, () -> "Supplier message for invalid state")
        );
        assertEquals("Supplier message for invalid state", exception.getMessage());
    }

    // ==================== 综合场景测试 ====================

    @Test
    @DisplayName("测试复杂业务场景 - 用户注册参数校验")
    void testComplexBusinessScenario() {
        // 模拟用户注册参数校验
        String username = "zhangsan";
        String email = "zhangsan@example.com";
        Integer age = 25;
        String[] roles = new String[]{"USER", "ADMIN"};

        // 所有断言应该通过
        assertDoesNotThrow(() -> {
            Assert.hasText(username, "Username must not be empty");
            Assert.hasText(email, "Email must not be empty");
            Assert.notNull(age, "Age must not be null");
            Assert.isTrue(age >= 0 && age <= 150, "Age must be between 0 and 150");
            Assert.notEmpty(roles, "Roles must not be empty");
            Assert.noNullElements(roles, "Roles must not contain null");
        });
    }

    @Test
    @DisplayName("测试复杂业务场景 - 订单提交状态校验")
    void testOrderStateScenario() {
        // 模拟订单状态校验
        String orderStatus = "CREATED";
        List<String> items = Arrays.asList("item1", "item2");

        // 状态校验应该通过
        assertDoesNotThrow(() -> {
            Assert.state("CREATED".equals(orderStatus), "Order must be in CREATED status");
            Assert.notEmpty(items, "Order must have items");
        });

        // 状态校验应该失败
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> Assert.state("SUBMITTED".equals(orderStatus), "Order must be in SUBMITTED status")
        );
        assertEquals("Order must be in SUBMITTED status", exception.getMessage());
    }

    @Test
    @DisplayName("测试边界条件 - 空字符串和空白字符")
    void testEdgeCasesWithWhitespace() {
        // hasLength 应该接受空白字符
        assertDoesNotThrow(() -> Assert.hasLength("   ", "Should pass for whitespace"));

        // hasText 应该拒绝空白字符
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.hasText("   ", "Should fail for whitespace")
        );
        assertEquals("Should fail for whitespace", exception.getMessage());
    }

    @Test
    @DisplayName("测试边界条件 - 空集合和空数组")
    void testEdgeCasesWithEmptyCollections() {
        // 空数组
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty(new Object[]{}, "Empty array should fail")
        );
        assertEquals("Empty array should fail", exception.getMessage());

        // 空集合
        exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty(Collections.emptyList(), "Empty list should fail")
        );
        assertEquals("Empty list should fail", exception.getMessage());

        // 空 Map
        exception = assertThrows(
            IllegalArgumentException.class,
            () -> Assert.notEmpty(Collections.emptyMap(), "Empty map should fail")
        );
        assertEquals("Empty map should fail", exception.getMessage());
    }
}
