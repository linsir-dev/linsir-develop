# 断言工具模块测试说明文档

## 1. 测试概述

本文档详细说明断言工具模块的测试代码结构、测试用例设计和运行方法。

## 2. 测试结构

```
src/test/java/com/linsir/spring/framework/spring_core/asserts/
└── AssertTest.java    # 断言工具测试类
```

## 3. 测试类详解

### 3.1 AssertTest - 断言工具测试

**文件位置**: `src/test/java/com/linsir/spring/framework/spring_core/asserts/AssertTest.java`

**测试目标**: 验证 Assert 类的所有断言方法

**测试用例清单**:

| 测试方法 | 测试目标 | 说明 |
|----------|----------|------|
| testNotNullWithNonNullObject | notNull - 非 null 对象 | 不抛出异常 |
| testNotNullWithNullObject | notNull - null 对象 | 抛出 IllegalArgumentException |
| testNotNullWithSupplier | notNull - Supplier 版本 | 延迟消息计算 |
| testIsNullWithNullObject | isNull - null 对象 | 不抛出异常 |
| testIsNullWithNonNullObject | isNull - 非 null 对象 | 抛出 IllegalArgumentException |
| testHasTextWithValidString | hasText - 有效字符串 | 不抛出异常 |
| testHasTextWithNull | hasText - null | 抛出异常 |
| testHasTextWithEmptyString | hasText - 空字符串 | 抛出异常 |
| testHasTextWithWhitespaceOnly | hasText - 仅空白字符 | 抛出异常 |
| testHasTextWithSupplier | hasText - Supplier 版本 | 延迟消息计算 |
| testHasLengthWithValidString | hasLength - 有效字符串 | 不抛出异常 |
| testHasLengthWithNull | hasLength - null | 抛出异常 |
| testHasLengthWithEmptyString | hasLength - 空字符串 | 抛出异常 |
| testDoesNotContainWithValidString | doesNotContain - 有效字符串 | 不抛出异常 |
| testDoesNotContainWithContainingString | doesNotContain - 包含子串 | 抛出异常 |
| testDoesNotContainWithNullString | doesNotContain - null 字符串 | 不抛出异常 |
| testDoesNotContainWithNullSubstring | doesNotContain - null 子串 | 不抛出异常 |
| testIsTrueWithTrueExpression | isTrue - true 表达式 | 不抛出异常 |
| testIsTrueWithFalseExpression | isTrue - false 表达式 | 抛出异常 |
| testIsTrueWithSupplier | isTrue - Supplier 版本 | 延迟消息计算 |
| testIsFalseWithFalseExpression | isFalse - false 表达式 | 不抛出异常 |
| testIsFalseWithTrueExpression | isFalse - true 表达式 | 抛出异常 |
| testNotEmptyArrayWithValidArray | notEmpty (数组) - 有效数组 | 不抛出异常 |
| testNotEmptyArrayWithNull | notEmpty (数组) - null | 抛出异常 |
| testNotEmptyArrayWithEmptyArray | notEmpty (数组) - 空数组 | 抛出异常 |
| testNoNullElementsWithValidArray | noNullElements - 有效数组 | 不抛出异常 |
| testNoNullElementsWithNullArray | noNullElements - null 数组 | 不抛出异常 |
| testNoNullElementsWithNullElement | noNullElements - 包含 null | 抛出异常 |
| testNoNullElementsWithSupplier | noNullElements - Supplier 版本 | 延迟消息计算 |
| testNotEmptyCollectionWithValidCollection | notEmpty (集合) - 有效集合 | 不抛出异常 |
| testNotEmptyCollectionWithNull | notEmpty (集合) - null | 抛出异常 |
| testNotEmptyCollectionWithEmptyCollection | notEmpty (集合) - 空集合 | 抛出异常 |
| testNotEmptyCollectionWithSupplier | notEmpty (集合) - Supplier 版本 | 延迟消息计算 |
| testNotEmptyMapWithValidMap | notEmpty (Map) - 有效 Map | 不抛出异常 |
| testNotEmptyMapWithNull | notEmpty (Map) - null | 抛出异常 |
| testNotEmptyMapWithEmptyMap | notEmpty (Map) - 空 Map | 抛出异常 |
| testIsInstanceOfWithValidInstance | isInstanceOf - 有效实例 | 不抛出异常 |
| testIsInstanceOfWithNullType | isInstanceOf - null 类型 | 抛出异常 |
| testIsInstanceOfWithInvalidInstance | isInstanceOf - 无效实例 | 抛出异常 |
| testIsInstanceOfWithSupplier | isInstanceOf - Supplier 版本 | 延迟消息计算 |
| testIsAssignableWithValidTypes | isAssignable - 有效类型 | 不抛出异常 |
| testIsAssignableWithNullSuperType | isAssignable - null 超类型 | 抛出异常 |
| testIsAssignableWithNullSubType | isAssignable - null 子类型 | 抛出异常 |
| testIsAssignableWithInvalidTypes | isAssignable - 无效类型 | 抛出异常 |
| testStateWithValidState | state - 有效状态 | 不抛出异常 |
| testStateWithInvalidState | state - 无效状态 | 抛出 IllegalStateException |
| testStateWithSupplier | state - Supplier 版本 | 延迟消息计算 |
| testComplexBusinessScenario | 复杂业务场景 - 用户注册 | 综合测试 |
| testOrderStateScenario | 复杂业务场景 - 订单状态 | 综合测试 |
| testEdgeCasesWithWhitespace | 边界条件 - 空白字符 | 边界测试 |
| testEdgeCasesWithEmptyCollections | 边界条件 - 空集合 | 边界测试 |

**关键测试代码示例**:

```java
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
```

## 4. 测试运行方法

### 4.1 运行所有测试

```bash
cd linsir-spring-framework
mvn test -Dtest=AssertTest
```

### 4.2 运行单个测试方法

```bash
mvn test -Dtest=AssertTest#testNotNullWithNullObject
```

### 4.3 查看测试报告

测试报告生成位置: `target/surefire-reports/`

```bash
# 查看控制台报告
cat target/surefire-reports/com.linsir.spring.framework.spring_core.asserts.AssertTest.txt

# 查看 XML 报告
cat target/surefire-reports/TEST-com.linsir.spring.framework.spring_core.asserts.AssertTest.xml
```

## 5. 测试设计原则

### 5.1 测试命名规范

- 测试类名: `被测试类名 + Test`
- 测试方法名: `test + 被测试功能 + 条件`
- 使用 `@DisplayName` 提供中文描述

### 5.2 测试结构

每个测试方法遵循 AAA 模式:
- **Arrange**: 准备测试数据和对象
- **Act**: 执行被测试的操作
- **Assert**: 验证结果

```java
@Test
@DisplayName("测试 notNull - 对象为 null 时抛出异常")
void testNotNullWithNullObject() {
    // Arrange
    String message = "Object must not be null";
    
    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> Assert.notNull(null, message)
    );
    assertEquals(message, exception.getMessage());
}
```

### 5.3 边界条件测试

每个断言方法都测试了以下边界条件:
- 正常值
- null 值
- 空值（空字符串、空数组、空集合）
- 边界值（空白字符、零长度）

### 5.4 异常测试

使用 `assertThrows` 验证异常抛出:

```java
@Test
@DisplayName("测试 notNull - 对象为 null 时抛出异常")
void testNotNullWithNullObject() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> Assert.notNull(null, "Object must not be null")
    );
    assertEquals("Object must not be null", exception.getMessage());
}
```

## 6. 测试覆盖率

### 6.1 方法覆盖

| 断言方法 | 测试覆盖 |
|----------|----------|
| notNull | ✅ 基础版本 + Supplier 版本 |
| isNull | ✅ 基础版本 + Supplier 版本 |
| hasText | ✅ 基础版本 + Supplier 版本 |
| hasLength | ✅ 基础版本 + Supplier 版本 |
| doesNotContain | ✅ 基础版本 + Supplier 版本 |
| isTrue | ✅ 基础版本 + Supplier 版本 |
| isFalse | ✅ 基础版本 + Supplier 版本 |
| notEmpty (数组) | ✅ 基础版本 + Supplier 版本 |
| notEmpty (集合) | ✅ 基础版本 + Supplier 版本 |
| notEmpty (Map) | ✅ 基础版本 + Supplier 版本 |
| noNullElements | ✅ 基础版本 + Supplier 版本 |
| isInstanceOf | ✅ 基础版本 + Supplier 版本 |
| isAssignable | ✅ 基础版本 + Supplier 版本 |
| state | ✅ 基础版本 + Supplier 版本 |

### 6.2 场景覆盖

| 场景 | 测试覆盖 |
|------|----------|
| 正常值 | ✅ 所有方法 |
| null 值 | ✅ 所有相关方法 |
| 空值 | ✅ 字符串、数组、集合、Map |
| 边界值 | ✅ 空白字符、空集合 |
| 异常消息 | ✅ 验证消息内容 |
| Supplier 延迟计算 | ✅ 所有 Supplier 版本 |
| 复杂业务场景 | ✅ 用户注册、订单状态 |

## 7. 测试数据设计

### 7.1 字符串测试数据

| 输入 | hasText | hasLength | 说明 |
|------|---------|-----------|------|
| "hello" | ✅ | ✅ | 正常字符串 |
| "  hello  " | ✅ | ✅ | 包含空白 |
| " " | ❌ | ✅ | 仅空白字符 |
| "" | ❌ | ❌ | 空字符串 |
| null | ❌ | ❌ | null 值 |

### 7.2 集合测试数据

| 输入 | notEmpty | noNullElements | 说明 |
|------|----------|----------------|------|
| ["a", "b"] | ✅ | ✅ | 正常数组 |
| ["a", null] | ✅ | ❌ | 包含 null |
| [] | ❌ | ✅ | 空数组 |
| null | ❌ | ✅ | null 数组 |

### 7.3 类型测试数据

| 超类型 | 子类型 | isAssignable | 说明 |
|--------|--------|--------------|------|
| Object | String | ✅ | 继承关系 |
| Number | Integer | ✅ | 继承关系 |
| String | Integer | ❌ | 无继承关系 |
| Object | null | ❌ | null 子类型 |
| null | String | ❌ | null 超类型 |

## 8. 常见问题排查

### 8.1 测试失败常见原因

1. **异常类型不匹配**: 确保使用正确的异常类型（IllegalArgumentException vs IllegalStateException）
2. **消息内容不匹配**: 验证异常消息与预期完全一致
3. **边界条件遗漏**: 确保测试 null、空值等边界条件

### 8.2 调试技巧

```java
@Test
@DisplayName("调试测试")
void debugTest() {
    try {
        Assert.notNull(null, "Test message");
    } catch (IllegalArgumentException e) {
        System.out.println("Exception message: " + e.getMessage());
        throw e;
    }
}
```

## 9. 扩展测试建议

1. **增加性能测试**: 测试 Supplier 版本的性能优势
2. **增加并发测试**: 测试 Assert 类的线程安全性
3. **增加压力测试**: 测试大量断言的性能表现
4. **增加集成测试**: 测试与其他模块的集成使用
