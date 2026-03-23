# 注解处理模块代码说明

## 1. 模块概述

注解处理模块是 Spring Core 的核心组件之一，提供了对 Java 注解的完整支持，包括注解的获取、查找、属性提取、元注解处理和组合注解等功能。

## 2. 包结构

```
spring_core/annotation/
├── utils/                      # 工具类
│   ├── AnnotationUtils.java    # 核心注解工具
│   └── AnnotatedElementUtils.java  # 注解元素工具
├── core/                       # 核心接口和实现
│   ├── MergedAnnotations.java  # 合并注解集合接口
│   ├── MergedAnnotation.java   # 单个合并注解接口
│   ├── MergedAnnotationsImpl.java  # 实现类
│   └── MergedAnnotationImpl.java   # 单个合并注解实现
├── attribute/                  # 属性处理
│   └── AnnotationAttributes.java   # 注解属性映射
├── meta/                       # 元注解定义
│   ├── Component.java          # 组件注解
│   ├── Service.java            # 服务注解
│   ├── Repository.java         # 仓库注解
│   ├── Autowired.java          # 自动装配注解
│   ├── Qualifier.java          # 限定符注解
│   ├── Value.java              # 值注入注解
│   ├── Transactional.java      # 事务注解
│   └── Scope.java              # 作用域注解
└── support/                    # 组合注解支持
    ├── ServiceFacade.java      # 服务门面组合注解
    ├── Cacheable.java          # 缓存注解
    ├── Scheduled.java          # 定时任务注解
    └── Async.java              # 异步方法注解
```

## 3. 核心类详解

### 3.1 AnnotationUtils

`AnnotationUtils` 是注解处理的基础工具类，提供了注解的获取、查找和属性提取等功能。

#### 核心方法

| 方法 | 说明 |
|------|------|
| `getAnnotation(element, annotationType)` | 获取元素上直接声明的注解 |
| `findAnnotation(element, annotationType)` | 递归查找注解（包括父类） |
| `getAnnotationAttributes(annotation)` | 获取注解的所有属性值 |
| `getAnnotationAttribute(annotation, attributeName)` | 获取指定属性值 |
| `isAnnotatedWith(annotationType, metaAnnotationType)` | 判断注解是否包含元注解 |

#### 使用示例

```java
// 获取直接声明的注解
Service service = AnnotationUtils.getAnnotation(MyClass.class, Service.class);

// 递归查找注解（包括父类）
Component component = AnnotationUtils.findAnnotation(MyClass.class, Component.class);

// 获取注解属性
AnnotationAttributes attrs = AnnotationUtils.getAnnotationAttributes(service);
String value = attrs.getString("value");
```

### 3.2 AnnotatedElementUtils

`AnnotatedElementUtils` 提供了更高级的注解处理功能，支持元注解的递归查找和属性合并。

#### 核心方法

| 方法 | 说明 |
|------|------|
| `hasAnnotation(element, annotationType)` | 判断是否包含注解（包括元注解） |
| `hasDirectAnnotation(element, annotationType)` | 判断是否直接声明注解 |
| `getMergedAnnotation(element, annotationType)` | 获取合并后的注解 |
| `getMergedAnnotationAttributes(element, annotationType)` | 获取合并后的注解属性 |
| `findFirstAnnotation(element, annotationType)` | 查找第一个匹配的注解（包括父类） |
| `hasMetaAnnotation(element, metaAnnotationType)` | 判断是否包含元注解 |
| `getMetaAnnotations(annotationType)` | 获取注解的所有元注解 |

#### 使用示例

```java
// 判断是否包含注解（包括元注解）
boolean hasComponent = AnnotatedElementUtils.hasAnnotation(MyService.class, Component.class);

// 获取合并后的注解属性
AnnotationAttributes attrs = AnnotatedElementUtils.getMergedAnnotationAttributes(
    MyService.class, Service.class);

// 查找元注解
List<Annotation> metaAnnotations = AnnotatedElementUtils.getMetaAnnotations(Service.class);
```

### 3.3 MergedAnnotations

`MergedAnnotations` 表示一个元素上的所有注解的集合，支持多种搜索策略。

#### 搜索策略

| 策略 | 说明 |
|------|------|
| `DIRECT` | 仅直接声明的注解 |
| `INHERITED_ANNOTATIONS` | 包含元注解 |
| `SUPERCLASS` | 包含父类的注解 |
| `TYPE_HIERARCHY` | 包含类型层次结构 |
| `TYPE_HIERARCHY_AND_ENCLOSING_CLASSES` | 包含外部类 |

#### 使用示例

```java
// 获取元素上的所有注解
MergedAnnotations annotations = MergedAnnotations.from(MyClass.class);

// 判断是否包含指定注解
boolean hasService = annotations.isPresent(Service.class);

// 获取指定注解
Optional<MergedAnnotation<Service>> optional = annotations.get(Service.class);
if (optional.isPresent()) {
    MergedAnnotation<Service> merged = optional.get();
    String value = merged.getString("value");
}

// 使用特定搜索策略
MergedAnnotations annotations = MergedAnnotations.from(
    MyClass.class, 
    MergedAnnotations.SearchStrategy.TYPE_HIERARCHY
);
```

### 3.4 MergedAnnotation

`MergedAnnotation` 表示一个合并后的注解，包含了注解实例、距离信息和属性值。

#### 核心方法

| 方法 | 说明 |
|------|------|
| `getType()` | 获取注解类型 |
| `getDistance()` | 获取距离（0表示直接声明） |
| `getSource()` | 获取来源 |
| `getString(attributeName)` | 获取字符串属性 |
| `getBoolean(attributeName)` | 获取布尔属性 |
| `getInt(attributeName)` | 获取整数属性 |
| `getAttributes()` | 获取所有属性 |

#### 使用示例

```java
MergedAnnotation<Service> merged = annotations.getRequired(Service.class);

// 获取注解类型
Class<Service> type = merged.getType();

// 获取属性
String value = merged.getString("value");
boolean lazy = merged.getBoolean("lazy", false);

// 获取所有属性
AnnotationAttributes attrs = merged.getAttributes();
```

### 3.5 AnnotationAttributes

`AnnotationAttributes` 是注解属性的映射容器，继承自 `LinkedHashMap`，提供了类型安全的属性访问方法。

#### 核心方法

| 方法 | 说明 |
|------|------|
| `getString(attributeName)` | 获取字符串属性 |
| `getBoolean(attributeName)` | 获取布尔属性 |
| `getInt(attributeName)` | 获取整数属性 |
| `getLong(attributeName)` | 获取长整数属性 |
| `getClass(attributeName)` | 获取类属性 |
| `getArrayAttribute(attributeName)` | 获取数组属性 |
| `getAttribute(attributeName, type)` | 获取指定类型属性 |
| `merge(other)` | 合并另一个属性映射 |

#### 使用示例

```java
AnnotationAttributes attrs = new AnnotationAttributes();
attrs.put("name", "test");
attrs.put("count", 42);
attrs.put("active", true);

// 获取属性
String name = attrs.getString("name");
int count = attrs.getInt("count");
boolean active = attrs.getBoolean("active", false);

// 合并属性
AnnotationAttributes other = new AnnotationAttributes();
other.put("value", "other");
attrs.merge(other);
```

## 4. 元注解设计

### 4.1 核心元注解

#### @Component

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}
```

#### @Service

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    String value() default "";
}
```

`@Service` 注解使用了 `@Component` 作为元注解，因此标注 `@Service` 的类也相当于标注了 `@Component`。

### 4.2 元注解查找

```java
// 检查 @Service 是否包含 @Component 元注解
boolean isMetaAnnotated = AnnotationUtils.isAnnotatedWith(Service.class, Component.class);
// 返回 true
```

## 5. 组合注解设计

### 5.1 ServiceFacade 组合注解

`@ServiceFacade` 是一个典型的组合注解，同时包含 `@Component`、`@Scope` 和 `@Transactional`：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Scope("singleton")
@Transactional
public @interface ServiceFacade {
    String value() default "";
    Transactional.Propagation propagation() default Transactional.Propagation.REQUIRED;
    boolean readOnly() default false;
}
```

### 5.2 使用组合注解

```java
@ServiceFacade(value = "userService", propagation = REQUIRES_NEW, readOnly = true)
public class UserService {
    // 该类同时具有 @Component、@Scope("singleton") 和 @Transactional 的特性
}
```

### 5.3 查找组合注解的元注解

```java
// 检查 UserService 类是否包含 @Component（通过 @ServiceFacade 的元注解）
boolean hasComponent = AnnotatedElementUtils.hasAnnotation(UserService.class, Component.class);
// 返回 true

// 检查是否包含 @Transactional
boolean hasTransactional = AnnotatedElementUtils.hasAnnotation(UserService.class, Transactional.class);
// 返回 true
```

## 6. 属性别名与覆盖

### 6.1 属性别名

组合注解可以定义与元注解属性对应的别名：

```java
@ServiceFacade(value = "myService")  // value 对应 @Component 的 value
public class MyService {}
```

### 6.2 属性覆盖

组合注解的属性可以覆盖元注解的默认属性：

```java
@Scope("prototype")  // 覆盖 @ServiceFacade 中的 @Scope("singleton")
@ServiceFacade
public class MyService {}
```

## 7. 最佳实践

### 7.1 注解查找策略选择

- **仅需直接声明的注解**：使用 `AnnotationUtils.getAnnotation()`
- **需要包含元注解**：使用 `AnnotatedElementUtils.hasAnnotation()`
- **需要完整注解层次**：使用 `MergedAnnotations`

### 7.2 性能考虑

- `AnnotationUtils` 使用缓存机制缓存注解方法，提高重复访问性能
- 对于频繁访问的注解，建议缓存 `AnnotationAttributes` 结果
- 避免在循环中重复创建 `MergedAnnotations` 实例

### 7.3 类型安全

- 使用 `AnnotationAttributes` 的类型安全方法（如 `getString()`、`getInt()`）
- 使用泛型方法 `getAttribute(attributeName, type)` 进行类型转换
- 使用默认值方法避免空值检查

## 8. 扩展点

### 8.1 自定义注解处理器

可以通过实现 `MergedAnnotation` 接口创建自定义的注解合并策略：

```java
public class CustomMergedAnnotation<A extends Annotation> implements MergedAnnotation<A> {
    // 实现自定义的合并逻辑
}
```

### 8.2 自定义属性提取器

可以扩展 `AnnotationAttributes` 添加自定义的属性转换逻辑：

```java
public class CustomAnnotationAttributes extends AnnotationAttributes {
    public CustomType getCustomType(String attributeName) {
        // 自定义转换逻辑
    }
}
```
