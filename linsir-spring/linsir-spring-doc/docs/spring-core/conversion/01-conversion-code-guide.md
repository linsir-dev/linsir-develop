# 类型转换模块代码说明文档

## 1. 模块概述

本文档详细说明类型转换模块的示例代码实现，包括核心接口、实现类和使用示例。

## 2. 核心接口设计

### 2.1 ConversionService - 类型转换服务接口

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/service/ConversionService.java`

**设计意图**: 提供统一的类型转换入口，屏蔽底层转换细节。

**核心方法**:
```java
// 判断是否可以转换
boolean canConvert(Class<?> sourceType, Class<?> targetType);

// 执行类型转换
<T> T convert(Object source, Class<T> targetType);
```

**使用场景**: 作为类型转换的 Facade 接口，客户端只需依赖此接口即可完成所有转换操作。

### 2.2 Converter - 单向转换器接口

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/converter/Converter.java`

**设计意图**: 定义最简单的类型转换契约，支持 Lambda 表达式实现。

**代码示例**:
```java
@FunctionalInterface
public interface Converter<S, T> {
    T convert(S source);
}
```

**实现特点**:
- 使用 `@FunctionalInterface` 注解，支持 Lambda 表达式
- 泛型参数明确源类型和目标类型
- 返回值可能为 null

### 2.3 ConverterFactory - 转换器工厂接口

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/factory/ConverterFactory.java`

**设计意图**: 当需要将一种类型转换为多种相关类型时使用，避免创建大量相似的转换器。

**典型应用场景**: String 转各种 Number 子类（Integer、Long、Double 等）

```java
public interface ConverterFactory<S, R> {
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
```

### 2.4 GenericConverter - 通用转换器接口

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/generic/GenericConverter.java`

**设计意图**: 支持复杂的类型转换场景，特别是需要访问泛型信息的情况。

**与 Converter 的区别**:
| 特性 | Converter | GenericConverter |
|------|-----------|------------------|
| 类型对支持 | 单一源类型到目标类型 | 多对多 |
| 泛型信息 | 不支持 | 支持 |
| 复杂度 | 简单 | 复杂 |
| 适用场景 | 简单类型转换 | 集合、Map等复杂类型 |

**核心结构**:
```java
public interface GenericConverter {
    Set<ConvertiblePair> getConvertibleTypes();
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
}
```

### 2.5 Formatter - 格式化接口

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/formatter/Formatter.java`

**设计意图**: 专门处理字符串与对象的转换，支持国际化。

**接口继承关系**:
```
Formatter<T> extends Printer<T>, Parser<T>
```

**与 Converter 的区别**:
- Formatter 专门处理字符串与对象的转换
- Formatter 支持 Locale 国际化
- Formatter 用于展示层面的格式化

## 3. 核心实现类

### 3.1 GenericConversionService - 转换服务默认实现

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/support/GenericConversionService.java`

**架构设计**:
```
┌─────────────────────────────────────┐
│     GenericConversionService        │
├─────────────────────────────────────┤
│  - converters: Set<GenericConverter>│
│  - converterCache: Map              │
├─────────────────────────────────────┤
│  + addConverter()                   │
│  + addConverterFactory()            │
│  + convert()                        │
│  + canConvert()                     │
└─────────────────────────────────────┘
```

**内置转换器**:
- StringToIntegerConverter: 字符串转整数
- StringToLongConverter: 字符串转长整数
- StringToDoubleConverter: 字符串转双精度浮点数
- StringToBooleanConverter: 字符串转布尔值
- NumberToNumberConverter: 数字互转
- ArrayToCollectionConverter: 数组转集合
- CollectionToArrayConverter: 集合转数组

**缓存机制**: 使用 `ConcurrentHashMap` 缓存转换器查找结果，提升性能。

### 3.2 TypeDescriptor - 类型描述符

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/descriptor/TypeDescriptor.java`

**设计意图**: 提供对 Java 类型的完整描述，包括泛型参数信息。

**核心功能**:
- 支持从 Class、Field、Method 参数创建
- 判断集合、Map、数组类型
- 获取元素类型、Map键值类型
- 支持注解获取

**使用示例**:
```java
// 从 Class 创建
TypeDescriptor desc = TypeDescriptor.valueOf(String.class);

// 从字段创建
TypeDescriptor fieldDesc = TypeDescriptor.forField(field);

// 从对象创建
TypeDescriptor objDesc = TypeDescriptor.forObject(object);
```

### 3.3 ConversionException - 转换异常

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/exception/ConversionException.java`

**设计特点**:
- 记录源类型、目标类型和源值
- 提供详细的错误信息
- 支持链式异常

## 4. 示例实现类

### 4.1 StringToUserConverter - 自定义转换器示例

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/support/StringToUserConverter.java`

**功能说明**: 将字符串格式 "name,age,email" 转换为用户对象。

**实现代码**:
```java
public class StringToUserConverter implements Converter<String, User> {
    @Override
    public User convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        String[] parts = source.split(",");
        User user = new User();
        user.setName(parts[0].trim());
        user.setAge(Integer.parseInt(parts[1].trim()));
        
        if (parts.length > 2) {
            user.setEmail(parts[2].trim());
        }
        
        return user;
    }
}
```

**设计要点**:
- 实现 `Converter<String, User>` 接口
- 处理 null 和空字符串
- 支持可选字段（email）
- 对输入进行 trim 处理

### 4.2 StringToNumberConverterFactory - 转换器工厂示例

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/support/StringToNumberConverterFactory.java`

**功能说明**: 创建 String 到各种 Number 子类的转换器。

**实现代码**:
```java
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {
    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumberConverter<>(targetType);
    }
}
```

**支持的类型**: Integer、Long、Double、Float、Short、Byte、BigDecimal、BigInteger

### 4.3 DateFormatter - 日期格式化器示例

**文件位置**: `src/main/java/com/linsir/spring/framework/spring_core/conversion/formatter/DateFormatter.java`

**功能说明**: 使用指定模式格式化日期。

**使用示例**:
```java
DateFormatter formatter = new DateFormatter("yyyy-MM-dd");
Date date = formatter.parse("2026-03-23", Locale.getDefault());
String str = formatter.print(date, Locale.getDefault()); // "2026-03-23"
```

**设计特点**:
- 使用 SimpleDateFormat 进行格式化
- 设置 lenient 为 false，严格日期校验
- 支持 null 值处理

## 5. 代码架构图

```
┌─────────────────────────────────────────────────────────────┐
│                     ConversionService                        │
│                     (统一转换入口)                           │
└───────────────────────┬─────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
        ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│  Converter   │ │ConverterFactory│ │GenericConverter│
│  (简单转换)   │ │  (工厂模式)   │ │ (复杂转换)   │
└──────────────┘ └──────────────┘ └──────────────┘
        │               │               │
        └───────────────┼───────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│              GenericConversionService                        │
│              (转换器注册与管理)                               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  converters: Set<GenericConverter>                    │  │
│  │  converterCache: Map<ConverterCacheKey, GenericConverter>│  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
        ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│TypeDescriptor│ │   Formatter  │ │ 内置转换器   │
│  (类型描述)   │ │  (格式化)    │ │ (具体实现)   │
└──────────────┘ └──────────────┘ └──────────────┘
```

## 6. 使用示例

### 6.1 基本类型转换

```java
GenericConversionService service = new GenericConversionService();
service.addDefaultConverters();

Integer num = service.convert("123", Integer.class);
Long longNum = service.convert("9999999999", Long.class);
Boolean flag = service.convert("true", Boolean.class);
```

### 6.2 自定义转换器

```java
// 注册自定义转换器
service.addConverter(String.class, User.class, new StringToUserConverter());

// 使用转换器
User user = service.convert("zhangsan,25,zhangsan@example.com", User.class);
```

### 6.3 数组与集合转换

```java
String[] array = new String[]{"a", "b", "c"};
List<String> list = (List<String>) service.convert(array,
    TypeDescriptor.valueOf(String[].class),
    TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class)));
```

### 6.4 日期格式化

```java
DateFormatter formatter = new DateFormatter("yyyy-MM-dd HH:mm:ss");
Date date = formatter.parse("2026-03-23 15:30:00", Locale.getDefault());
String str = formatter.print(date, Locale.getDefault());
```

## 7. 设计模式应用

| 模式 | 应用位置 | 说明 |
|------|----------|------|
| 策略模式 | Converter | 不同的转换算法封装为不同的策略 |
| 工厂模式 | ConverterFactory | 根据目标类型创建对应的转换器 |
| 适配器模式 | ConverterAdapter | 将 Converter 适配为 GenericConverter |
| 外观模式 | ConversionService | 提供统一的类型转换入口 |
| 缓存模式 | converterCache | 缓存转换器查找结果 |

## 8. 扩展建议

1. **增加更多内置转换器**: 如 StringToDate、StringToEnum 等
2. **支持条件转换**: 实现 ConditionalConverter 接口
3. **增加注解支持**: 通过注解配置转换规则
4. **支持 SpEL 表达式**: 在转换中使用表达式
5. **增加异步转换**: 支持大数据量的异步类型转换
