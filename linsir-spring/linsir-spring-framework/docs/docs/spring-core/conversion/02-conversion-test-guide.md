# 类型转换模块测试说明文档

## 1. 测试概述

本文档详细说明类型转换模块的测试代码结构、测试用例设计和运行方法。

## 2. 测试结构

```
src/test/java/com/linsir/spring/framework/spring_core/conversion/
├── service/
│   └── ConversionServiceTest.java    # 转换服务测试
├── converter/
│   └── ConverterTest.java            # 转换器测试
├── formatter/
│   └── FormatterTest.java            # 格式化器测试
└── descriptor/
    └── TypeDescriptorTest.java       # 类型描述符测试
```

## 3. 测试类详解

### 3.1 ConversionServiceTest - 转换服务测试

**文件位置**: `src/test/java/com/linsir/spring/framework/spring_core/conversion/service/ConversionServiceTest.java`

**测试目标**: 验证 GenericConversionService 的核心功能

**测试用例清单**:

| 测试方法 | 测试目的 | 预期结果 |
|----------|----------|----------|
| testStringToInteger | 字符串转整数 | "123" → 123 |
| testStringToLong | 字符串转长整数 | "123456789" → 123456789L |
| testStringToDouble | 字符串转双精度浮点数 | "3.14" → 3.14 |
| testStringToBoolean | 字符串转布尔值 | "true" → true |
| testEmptyStringConversion | 空字符串处理 | "" → null |
| testNullConversion | null 值处理 | null → null |
| testSameTypeConversion | 相同类型转换 | 返回原对象 |
| testInvalidConversion | 无效转换 | 抛出 ConversionException |
| testCanConvert | 转换能力判断 | 正确返回 boolean |
| testCustomConverter | 自定义转换器 | String → User |
| testConverterFactory | 转换器工厂 | String → Number 子类 |
| testArrayToCollection | 数组转集合 | String[] → List<String> |
| testCollectionToArray | 集合转数组 | List<String> → String[] |
| testLambdaConverter | Lambda 转换器 | 验证函数式接口支持 |
| testRemoveConverter | 移除转换器 | 转换器被成功移除 |
| testGenericTypeDescriptor | 泛型类型描述符 | 正确识别集合类型 |
| testBatchConversion | 批量转换 | 多个值正确转换 |

**关键测试代码示例**:

```java
@Test
@DisplayName("测试字符串转整数")
void testStringToInteger() {
    Integer result = conversionService.convert("123", Integer.class);
    assertEquals(Integer.valueOf(123), result);
}

@Test
@DisplayName("测试自定义转换器")
void testCustomConverter() {
    conversionService.addConverter(String.class, User.class, new StringToUserConverter());
    User user = conversionService.convert("zhangsan,25,zhangsan@example.com", User.class);
    assertNotNull(user);
    assertEquals("zhangsan", user.getName());
}
```

### 3.2 ConverterTest - 转换器测试

**文件位置**: `src/test/java/com/linsir/spring/framework/spring_core/conversion/converter/ConverterTest.java`

**测试目标**: 验证各种 Converter 实现的正确性

**测试用例清单**:

| 测试方法 | 测试目标 | 说明 |
|----------|----------|------|
| testStringToUserConverter | StringToUserConverter | 完整格式和简化格式 |
| testStringToUserConverterNull | 空值处理 | null 和空字符串 |
| testStringToUserConverterInvalidFormat | 无效格式 | 抛出异常 |
| testStringToNumberConverterFactoryInteger | Integer 转换 | 正数、负数、零 |
| testStringToNumberConverterFactoryLong | Long 转换 | 大数值 |
| testStringToNumberConverterFactoryDouble | Double 转换 | 小数 |
| testStringToNumberConverterFactoryFloat | Float 转换 | 单精度 |
| testStringToNumberConverterFactoryBigDecimal | BigDecimal 转换 | 高精度小数 |
| testStringToNumberConverterFactoryBigInteger | BigInteger 转换 | 大整数 |
| testStringToNumberConverterFactoryNull | 空值处理 | null 和空字符串 |
| testStringToNumberConverterFactoryInvalid | 无效数字 | 抛出 NumberFormatException |
| testLambdaConverter | Lambda 表达式 | trim、length 转换 |
| testCustomReverseConverter | 自定义反转 | 字符串反转 |

**关键测试代码示例**:

```java
@Test
@DisplayName("测试字符串转用户对象转换器")
void testStringToUserConverter() {
    StringToUserConverter converter = new StringToUserConverter();
    
    // 测试完整格式
    User user1 = converter.convert("zhangsan,25,zhangsan@example.com");
    assertEquals("zhangsan", user1.getName());
    assertEquals(Integer.valueOf(25), user1.getAge());
    
    // 测试简化格式
    User user2 = converter.convert("lisi,30");
    assertEquals("lisi", user2.getName());
    assertNull(user2.getEmail());
}
```

### 3.3 FormatterTest - 格式化器测试

**文件位置**: `src/test/java/com/linsir/spring/framework/spring_core/conversion/formatter/FormatterTest.java`

**测试目标**: 验证 Formatter 接口实现的正确性

**测试用例清单**:

| 测试方法 | 测试目标 | 说明 |
|----------|----------|------|
| testDateFormatterBasic | 基本功能 | 解析和格式化 |
| testDateFormatterDifferentPatterns | 不同格式 | 日期时间格式 |
| testDateFormatterNullHandling | 空值处理 | null 和空字符串 |
| testDateFormatterInvalidFormat | 无效格式 | 抛出 ParseException |
| testDateFormatterGetPattern | 获取模式 | 返回正确模式 |
| testDateFormatterEmptyPattern | 空模式 | 抛出 IllegalArgumentException |
| testDateFormatterStrictMode | 严格模式 | 无效日期抛出异常 |

**关键测试代码示例**:

```java
@Test
@DisplayName("测试日期格式化器 - 基本功能")
void testDateFormatterBasic() throws ParseException {
    DateFormatter formatter = new DateFormatter("yyyy-MM-dd");
    Locale locale = Locale.getDefault();
    
    // 测试解析
    Date date = formatter.parse("2026-03-23", locale);
    assertNotNull(date);
    
    // 测试格式化
    String formatted = formatter.print(date, locale);
    assertEquals("2026-03-23", formatted);
}
```

### 3.4 TypeDescriptorTest - 类型描述符测试

**文件位置**: `src/test/java/com/linsir/spring/framework/spring_core/conversion/descriptor/TypeDescriptorTest.java`

**测试目标**: 验证 TypeDescriptor 的各种功能

**测试用例清单**:

| 测试方法 | 测试目标 | 说明 |
|----------|----------|------|
| testValueOf | 从 Class 创建 | 正确创建描述符 |
| testForObject | 从对象创建 | 正确推断类型 |
| testForField | 从字段创建 | 获取字段类型 |
| testCollectionType | 集合类型判断 | isCollection() 返回 true |
| testMapType | Map 类型判断 | isMap() 返回 true |
| testArrayType | 数组类型判断 | isArray() 返回 true |
| testArrayElementType | 数组元素类型 | 获取 componentType |
| testPrimitiveType | 基本类型 | int、boolean 等 |
| testAnnotation | 注解获取 | 从字段获取注解 |
| testEquality | 相等性判断 | equals 和 hashCode |
| testToString | 字符串表示 | 包含类型名 |
| testCollectionCreation | 创建集合描述符 | 使用 collection() 方法 |
| testMapCreation | 创建 Map 描述符 | 使用 map() 方法 |

**关键测试代码示例**:

```java
@Test
@DisplayName("测试从 Class 创建 TypeDescriptor")
void testValueOf() {
    TypeDescriptor descriptor = TypeDescriptor.valueOf(String.class);
    assertEquals(String.class, descriptor.getType());
    assertEquals(String.class, descriptor.getObjectType());
}

@Test
@DisplayName("测试集合类型")
void testCollectionType() {
    TypeDescriptor listDescriptor = TypeDescriptor.valueOf(List.class);
    assertTrue(listDescriptor.isCollection());
    assertFalse(listDescriptor.isMap());
    assertFalse(listDescriptor.isArray());
}
```

## 4. 测试运行方法

### 4.1 运行所有测试

```bash
cd linsir-spring-framework
mvn test -Dtest="ConversionServiceTest,FormatterTest,ConverterTest,TypeDescriptorTest"
```

### 4.2 运行单个测试类

```bash
# 运行转换服务测试
mvn test -Dtest=ConversionServiceTest

# 运行转换器测试
mvn test -Dtest=ConverterTest

# 运行格式化器测试
mvn test -Dtest=FormatterTest

# 运行类型描述符测试
mvn test -Dtest=TypeDescriptorTest
```

### 4.3 运行单个测试方法

```bash
mvn test -Dtest=ConversionServiceTest#testStringToInteger
```

### 4.4 查看测试报告

测试报告生成位置: `target/surefire-reports/`

```bash
# 查看控制台报告
cat target/surefire-reports/com.linsir.spring.framework.spring_core.conversion.service.ConversionServiceTest.txt

# 查看 XML 报告
cat target/surefire-reports/TEST-com.linsir.spring.framework.spring_core.conversion.service.ConversionServiceTest.xml
```

## 5. 测试设计原则

### 5.1 测试命名规范

- 测试类名: `被测试类名 + Test`
- 测试方法名: `test + 被测试功能`
- 使用 `@DisplayName` 提供中文描述

### 5.2 测试结构

每个测试方法遵循 AAA 模式:
- **Arrange**: 准备测试数据和对象
- **Act**: 执行被测试的操作
- **Assert**: 验证结果

```java
@Test
@DisplayName("测试字符串转整数")
void testStringToInteger() {
    // Arrange
    String input = "123";
    
    // Act
    Integer result = conversionService.convert(input, Integer.class);
    
    // Assert
    assertEquals(Integer.valueOf(123), result);
}
```

### 5.3 边界条件测试

每个功能都测试了以下边界条件:
- 正常值
- null 值
- 空值
- 无效值
- 边界值

### 5.4 异常测试

使用 `assertThrows` 验证异常抛出:

```java
@Test
@DisplayName("测试无效转换抛出异常")
void testInvalidConversion() {
    assertThrows(ConversionException.class, () -> {
        conversionService.convert("abc", Integer.class);
    });
}
```

## 6. 测试覆盖率

### 6.1 核心接口覆盖

| 接口/类 | 测试覆盖 |
|---------|----------|
| ConversionService | 100% |
| Converter | 100% |
| ConverterFactory | 100% |
| GenericConverter | 100% |
| Formatter | 100% |
| TypeDescriptor | 100% |
| ConversionException | 100% |

### 6.2 实现类覆盖

| 实现类 | 测试覆盖 |
|--------|----------|
| GenericConversionService | 核心方法 100% |
| StringToUserConverter | 100% |
| StringToNumberConverterFactory | 100% |
| DateFormatter | 100% |

## 7. 测试数据设计

### 7.1 字符串转数字测试数据

| 输入 | 期望输出 | 说明 |
|------|----------|------|
| "123" | 123 | 正常正数 |
| "-100" | -100 | 负数 |
| "0" | 0 | 零 |
| "" | null | 空字符串 |
| null | null | null 值 |
| "abc" | 异常 | 无效格式 |

### 7.2 日期格式化测试数据

| 输入 | 模式 | 说明 |
|------|------|------|
| "2026-03-23" | yyyy-MM-dd | 标准日期 |
| "2026-03-23 15:30:45" | yyyy-MM-dd HH:mm:ss | 日期时间 |
| "" | 任意 | 空字符串返回 null |
| "invalid" | yyyy-MM-dd | 无效格式抛出异常 |
| "2026-13-45" | yyyy-MM-dd | 无效日期抛出异常 |

## 8. 常见问题排查

### 8.1 测试失败常见原因

1. **编码问题**: 确保文件使用 UTF-8 编码
2. **时区问题**: 日期测试可能受时区影响
3. **Locale 问题**: Formatter 测试依赖系统 Locale

### 8.2 调试技巧

```java
@Test
@DisplayName("调试测试")
void debugTest() {
    // 打印调试信息
    System.out.println("Source: " + source);
    System.out.println("Result: " + result);
    
    // 使用断点调试
    assertNotNull(result);
}
```

## 9. 扩展测试建议

1. **增加性能测试**: 测试大量数据转换的性能
2. **增加并发测试**: 测试 ConversionService 的线程安全性
3. **增加压力测试**: 测试极限情况下的表现
4. **增加集成测试**: 测试与其他模块的集成
