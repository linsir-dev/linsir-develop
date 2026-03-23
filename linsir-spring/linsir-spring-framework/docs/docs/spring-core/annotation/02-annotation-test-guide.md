# 注解处理模块测试说明

## 1. 测试概述

注解处理模块的测试采用 JUnit 5 框架，共包含 5 个测试类，114 个测试用例，覆盖了注解处理的各个方面。

## 2. 测试结构

```
test/java/com/linsir/spring/framework/spring_core/annotation/
├── utils/
│   ├── AnnotationUtilsTest.java          # 22个测试
│   └── AnnotatedElementUtilsTest.java    # 23个测试
├── attribute/
│   └── AnnotationAttributesTest.java     # 27个测试
├── core/
│   └── MergedAnnotationsTest.java        # 24个测试
└── support/
    └── AnnotationSupportTest.java        # 18个测试
```

## 3. 测试类详解

### 3.1 AnnotationUtilsTest

测试 `AnnotationUtils` 工具类的核心功能。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 基础获取 | `testGetAnnotation` | 测试获取直接声明的注解 |
| | `testGetAnnotationNotFound` | 测试获取不存在的注解 |
| | `testGetAnnotationWithNull` | 测试 null 参数处理 |
| 递归查找 | `testFindAnnotation` | 测试递归查找注解 |
| | `testFindAnnotationFromParent` | 测试从父类查找注解 |
| | `testFindAnnotationNotFound` | 测试查找不存在的注解 |
| 属性提取 | `testGetAnnotationAttributes` | 测试获取注解所有属性 |
| | `testGetAnnotationAttribute` | 测试获取指定属性值 |
| | `testGetAnnotationAttributeWithType` | 测试带类型的属性获取 |
| | `testGetDefaultValue` | 测试获取属性默认值 |
| 元注解 | `testIsAnnotatedWith` | 测试判断元注解 |
| | `testIsAnnotatedWithNull` | 测试 null 参数 |
| 工具方法 | `testHasAnnotation` | 测试判断注解存在性 |
| | `testGetAnnotations` | 测试获取所有注解 |
| | `testGetRepeatableAnnotations` | 测试获取重复注解 |
| | `testEquals` | 测试注解相等性 |
| | `testHashCode` | 测试注解哈希码 |
| | `testToString` | 测试注解字符串表示 |

#### 示例测试代码

```java
@Test
void testGetAnnotation() {
    TestAnnotation annotation = AnnotationUtils.getAnnotation(
        AnnotatedClass.class, TestAnnotation.class);
    
    assertNotNull(annotation);
    assertEquals("test", annotation.value());
}

@Test
void testFindAnnotationFromParent() {
    // 子类没有直接声明 @ParentAnnotation，但父类有
    ParentAnnotation annotation = AnnotationUtils.findAnnotation(
        ChildClass.class, ParentAnnotation.class);
    
    assertNotNull(annotation);
    assertEquals("parent", annotation.value());
}
```

### 3.2 AnnotatedElementUtilsTest

测试 `AnnotatedElementUtils` 的高级注解处理功能。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 存在性检查 | `testHasAnnotation` | 测试包含注解（包括元注解） |
| | `testHasDirectAnnotation` | 测试直接声明注解 |
| | `testHasMetaAnnotation` | 测试包含元注解 |
| 合并注解 | `testGetMergedAnnotation` | 测试获取合并注解 |
| | `testGetMergedAnnotationAttributes` | 测试获取合并属性 |
| | `testGetMergedRepeatableAnnotations` | 测试获取重复注解 |
| 查找功能 | `testFindFirstAnnotation` | 测试查找第一个注解 |
| | `testFindAllAnnotations` | 测试查找所有注解 |
| 元注解操作 | `testGetAnnotationsWithMetaAnnotation` | 测试获取带元注解的注解 |
| | `testGetMetaAnnotations` | 测试获取元注解列表 |
| 属性合并 | `testMergeAnnotationAttributes` | 测试合并属性 |
| 工具方法 | `testEquals` | 测试注解相等性 |
| | `testHashCode` | 测试注解哈希码 |
| | `testToString` | 测试注解字符串表示 |

#### 示例测试代码

```java
@Test
void testHasAnnotation() {
    // @OrderService 包含 @BusinessService 和 @Component 元注解
    assertTrue(AnnotatedElementUtils.hasAnnotation(AnnotatedClass.class, OrderService.class));
    assertTrue(AnnotatedElementUtils.hasAnnotation(AnnotatedClass.class, Component.class));
    assertTrue(AnnotatedElementUtils.hasAnnotation(AnnotatedClass.class, BusinessService.class));
}

@Test
void testGetMergedRepeatableAnnotations() {
    // @Scheduled 是可重复注解
    List<Scheduled> annotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(
        ScheduledClass.class, Scheduled.class);
    
    assertEquals(2, annotations.size());
}
```

### 3.3 AnnotationAttributesTest

测试 `AnnotationAttributes` 属性映射的功能。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 构造 | `testEmptyConstructor` | 测试空构造 |
| | `testFromMap` | 测试从 Map 创建 |
| 字符串属性 | `testGetString` | 测试获取字符串 |
| | `testGetStringWithDefault` | 测试带默认值的获取 |
| 布尔属性 | `testGetBoolean` | 测试获取布尔值 |
| | `testGetBooleanWithDefault` | 测试带默认值的获取 |
| 整数属性 | `testGetInt` | 测试获取整数 |
| | `testGetIntWithDefault` | 测试带默认值的获取 |
| 长整数属性 | `testGetLong` | 测试获取长整数 |
| | `testGetLongWithDefault` | 测试带默认值的获取 |
| 类属性 | `testGetClass` | 测试获取类 |
| | `testGetClassWithType` | 测试带类型的获取 |
| 数组属性 | `testGetArrayAttribute` | 测试获取数组 |
| 泛型获取 | `testGetAttribute` | 测试泛型属性获取 |
| | `testGetAttributeWithDefault` | 测试带默认值的泛型获取 |
| 工具方法 | `testHasAttribute` | 测试属性存在性 |
| | `testIsEmpty` | 测试空值判断 |
| | `testPutIfAbsentAttribute` | 测试条件添加 |
| | `testMerge` | 测试属性合并 |
| | `testToString` | 测试字符串表示 |

#### 示例测试代码

```java
@Test
void testGetAttributeWithDefault() {
    AnnotationAttributes attributes = new AnnotationAttributes();
    attributes.put("name", "test");
    
    // 存在的属性
    String value = attributes.getAttribute("name", String.class, "default");
    assertEquals("test", value);
    
    // 不存在的属性，返回默认值
    String defaultValue = attributes.getAttribute("nonexistent", String.class, "default");
    assertEquals("default", defaultValue);
}

@Test
void testMerge() {
    AnnotationAttributes primary = new AnnotationAttributes();
    primary.put("name", "primary");
    
    AnnotationAttributes secondary = new AnnotationAttributes();
    secondary.put("value", "secondary");
    
    primary.merge(secondary);
    
    assertEquals("primary", primary.getString("name"));
    assertEquals("secondary", primary.getString("value"));
}
```

### 3.4 MergedAnnotationsTest

测试 `MergedAnnotations` 合并注解体系的功能。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 基础操作 | `testFrom` | 测试创建实例 |
| | `testIsPresent` | 测试存在性判断 |
| | `testIsDirectlyPresent` | 测试直接声明判断 |
| 获取注解 | `testGet` | 测试获取注解 |
| | `testGetNotFound` | 测试获取不存在的注解 |
| | `testGetRequired` | 测试获取必需注解 |
| | `testGetWithDistance` | 测试按距离获取 |
| 集合操作 | `testGetAll` | 测试获取所有指定类型 |
| | `testSize` | 测试获取大小 |
| | `testIsEmpty` | 测试空判断 |
| | `testStream` | 测试流操作 |
| | `testIterator` | 测试迭代器 |
| 搜索策略 | `testServiceMetaAnnotation` | 测试元注解查找 |
| | `testSuperclassInheritance` | 测试父类继承 |
| 属性获取 | `testMergedAnnotationAttributes` | 测试属性获取 |
| | `testMergedAnnotationBooleanAttributes` | 测试布尔属性 |
| | `testMergedAnnotationIntAttributes` | 测试整数属性 |
| | `testMergedAnnotationLongAttributes` | 测试长整数属性 |
| | `testMergedAnnotationClassAttributes` | 测试类属性 |
| | `testMergedAnnotationArrayAttributes` | 测试数组属性 |

#### 示例测试代码

```java
@Test
void testGetWithDistance() {
    MergedAnnotations annotations = MergedAnnotations.from(
        AnnotatedClass.class,
        MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS
    );
    
    // 距离 0：直接声明
    Optional<MergedAnnotation<CustomAnnotation>> direct = annotations.get(CustomAnnotation.class, 0);
    assertEquals(0, direct.get().getDistance());
    
    // 距离 1：元注解
    Optional<MergedAnnotation<MetaAnnotation>> meta = annotations.get(MetaAnnotation.class, 1);
    assertEquals(1, meta.get().getDistance());
}

@Test
void testServiceMetaAnnotation() {
    // @Service 包含 @Component 元注解
    MergedAnnotations annotations = MergedAnnotations.from(ServiceClass.class);
    
    assertTrue(annotations.isPresent(Service.class));
    assertTrue(annotations.isPresent(Component.class));
}
```

### 3.5 AnnotationSupportTest

测试组合注解和元注解的高级功能。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 组合注解 | `testServiceFacadeHasComponent` | 测试组合注解包含元注解 |
| | `testServiceFacadeAttributes` | 测试组合注解属性 |
| | `testCacheableAnnotation` | 测试缓存注解 |
| 方法注解 | `testScheduledAnnotationOnMethod` | 测试方法上的定时注解 |
| | `testScheduledFixedRate` | 测试固定频率配置 |
| | `testAsyncAnnotation` | 测试异步注解 |
| 依赖注入 | `testAutowiredAnnotation` | 测试自动装配注解 |
| | `testQualifierAnnotation` | 测试限定符注解 |
| | `testValueAnnotation` | 测试值注入注解 |
| | `testValueSpELExpression` | 测试 SpEL 表达式 |
| 元数据 | `testTransactionalDefaults` | 测试事务注解默认值 |
| | `testScopeDefaults` | 测试作用域默认值 |
| | `testAnnotationRetention` | 测试保留策略 |
| | `testAnnotationTarget` | 测试目标范围 |
| | `testDocumentedAnnotation` | 测试文档化注解 |
| 元注解链 | `testMetaAnnotationChain` | 测试元注解链 |
| 工具类 | `testAnnotationUtilsGetAnnotation` | 测试工具类获取注解 |
| | `testAnnotationUtilsFindMetaAnnotation` | 测试工具类查找元注解 |

#### 示例测试代码

```java
@Test
void testServiceFacadeHasComponent() {
    // @ServiceFacade 包含 @Component、@Scope、@Transactional
    assertTrue(AnnotatedElementUtils.hasAnnotation(UserServiceFacade.class, Component.class));
    assertTrue(AnnotatedElementUtils.hasAnnotation(UserServiceFacade.class, Scope.class));
    assertTrue(AnnotatedElementUtils.hasAnnotation(UserServiceFacade.class, Transactional.class));
}

@Test
void testMetaAnnotationChain() {
    // @Service -> @Component
    assertTrue(AnnotationUtils.isAnnotatedWith(Service.class, Component.class));
    
    // @Repository -> @Component
    assertTrue(AnnotationUtils.isAnnotatedWith(Repository.class, Component.class));
}
```

## 4. 测试辅助类

### 4.1 测试注解定义

```java
// 用于测试的自定义注解
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestAnnotation {
    String value() default "";
    int count() default 0;
    boolean active() default true;
    String[] values() default {};
}

// 带元注解的注解
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MetaAnnotation {
    String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@MetaAnnotation("meta")
public @interface CustomAnnotation {
    String name() default "";
}
```

### 4.2 测试类定义

```java
// 带注解的测试类
@TestAnnotation(value = "test", count = 5, active = true)
static class AnnotatedClass {
}

// 父类
@ParentAnnotation("parent")
static class ParentClass {
}

// 子类（继承父类注解）
static class ChildClass extends ParentClass {
}

// 使用组合注解的类
@ServiceFacade(value = "userService", readOnly = true)
static class UserServiceFacade {
}
```

## 5. 运行测试

### 5.1 运行所有注解测试

```bash
mvn test -Dtest="AnnotationUtilsTest,AnnotatedElementUtilsTest,AnnotationAttributesTest,MergedAnnotationsTest,AnnotationSupportTest" -pl linsir-spring/linsir-spring-framework
```

### 5.2 运行单个测试类

```bash
mvn test -Dtest=AnnotationUtilsTest -pl linsir-spring/linsir-spring-framework
```

### 5.3 运行单个测试方法

```bash
mvn test -Dtest=AnnotationUtilsTest#testGetAnnotation -pl linsir-spring/linsir-spring-framework
```

## 6. 测试覆盖率

| 测试类 | 测试数 | 覆盖功能 |
|--------|--------|----------|
| AnnotationUtilsTest | 22 | 注解获取、查找、属性提取 |
| AnnotatedElementUtilsTest | 23 | 高级注解处理、元注解 |
| AnnotationAttributesTest | 27 | 属性映射、类型转换 |
| MergedAnnotationsTest | 24 | 合并注解、搜索策略 |
| AnnotationSupportTest | 18 | 组合注解、实际应用 |
| **总计** | **114** | **完整覆盖** |

## 7. 测试设计原则

### 7.1 边界条件测试

- null 参数处理
- 空集合处理
- 类型不匹配处理
- 默认值处理

### 7.2 异常测试

- 查找不存在的注解
- 获取必需的缺失注解
- 类型转换失败

### 7.3 组合测试

- 元注解链查找
- 属性合并
- 多层级继承
