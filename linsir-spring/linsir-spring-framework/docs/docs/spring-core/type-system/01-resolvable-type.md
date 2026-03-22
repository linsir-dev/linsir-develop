# ResolvableType 深度分析

## 概述

`ResolvableType` 是 Spring Framework 类型系统的核心类，它解决了 Java 泛型擦除带来的运行时类型信息获取难题。

## 问题背景

### Java 泛型擦除机制

Java 泛型在编译时会被擦除，运行时无法直接获取泛型参数：

```java
// 定义带泛型的类
public class UserService implements BaseService<User> {}

// 运行时尝试获取泛型参数
Type genericInterface = UserService.class.getGenericInterfaces()[0];
System.out.println(genericInterface); 
// 输出: BaseService<User> - 但无法直接获取 User.class

// 传统方式获取泛型参数非常复杂
ParameterizedType paramType = (ParameterizedType) genericInterface;
Type[] actualTypes = paramType.getActualTypeArguments();
// 还需要处理 TypeVariable、WildcardType 等各种情况
```

### ResolvableType 的解决方案

```java
// 使用 ResolvableType 简化泛型解析
ResolvableType type = ResolvableType.forClass(UserService.class);
ResolvableType baseService = type.as(BaseService.class);

Class entityClass = baseService.getGeneric(0).resolve();  // User.class
Class idClass = baseService.getGeneric(1).resolve();      // Long.class
```

## 核心架构

### 类结构

```
ResolvableType
├── 工厂方法
│   ├── forClass(Class)              # 从 Class 创建
│   ├── forField(Field)              # 从字段创建
│   ├── forMethodParameter(...)      # 从方法参数创建
│   ├── forMethodReturnType(...)     # 从方法返回类型创建
│   └── forType(Type, Class)         # 从 Type 创建
├── 解析方法
│   ├── resolve()                    # 解析为 Class
│   ├── getGeneric(int)              # 获取泛型参数
│   ├── as(Class)                    # 视为指定类型
│   ├── isAssignableFrom()           # 类型兼容性检查
│   └── isArray()                    # 数组检查
└── 内部结构
    ├── Type type                    # 原始类型
    ├── ResolvableType componentType # 数组组件
    ├── ResolvableType[] generics    # 泛型参数数组
    ├── Class resolved               # 解析后的 Class
    └── VariableResolver             # 变量解析器
```

### 核心字段

```java
public class ResolvableType implements Serializable {
    
    // 原始类型（Java 反射的 Type 接口）
    private final Type type;
    
    // 数组组件类型（如果是数组类型）
    private final ResolvableType componentType;
    
    // 泛型参数数组
    private final ResolvableType[] generics;
    
    // 解析后的具体 Class
    private final Class resolved;
    
    // 变量解析器（用于解析泛型变量如 T）
    private final VariableResolver variableResolver;
    
    // 空类型单例
    public static final ResolvableType NONE = new ResolvableType(null, null, null, null);
}
```

## 工厂方法详解

### 1. forClass - 从 Class 创建

```java
/**
 * 从 Class 创建 ResolvableType
 * 最简单的方式，无泛型信息
 */
public static ResolvableType forClass(Class clazz) {
    return new ResolvableType(clazz);
}

// 使用示例
ResolvableType type = ResolvableType.forClass(String.class);
Class resolved = type.resolve();  // String.class
```

### 2. forField - 从字段创建

```java
/**
 * 从字段创建，解析字段的泛型类型
 * 例如：private List<String> names; 会解析出 String
 */
public static ResolvableType forField(Field field) {
    Assert.notNull(field, "Field must not be null");
    return forType(field.getGenericType(), 
        new DefaultVariableResolver(field.getDeclaringClass()));
}

// 使用示例
public class Config {
    private List<String> names;
    private Map<String, Integer> settings;
}

Field namesField = Config.class.getDeclaredField("names");
ResolvableType namesType = ResolvableType.forField(namesField);
Class elementType = namesType.getGeneric(0).resolve();  // String.class

Field settingsField = Config.class.getDeclaredField("settings");
ResolvableType settingsType = ResolvableType.forField(settingsField);
Class keyType = settingsType.getGeneric(0).resolve();   // String.class
Class valueType = settingsType.getGeneric(1).resolve(); // Integer.class
```

### 3. forMethodParameter - 从方法参数创建

```java
/**
 * 从方法参数创建
 * 用于解析方法参数的泛型类型
 */
public static ResolvableType forMethodParameter(Method method, int parameterIndex) {
    Assert.notNull(method, "Method must not be null");
    MethodParameter methodParam = new MethodParameter(method, parameterIndex);
    return forMethodParameter(methodParam);
}

// 使用示例
public class UserController {
    public void saveUsers(List<User> users, Map<String, Object> metadata) {}
}

Method saveMethod = UserController.class.getMethod("saveUsers", List.class, Map.class);

// 第一个参数 List<User>
ResolvableType param1 = ResolvableType.forMethodParameter(saveMethod, 0);
Class param1Generic = param1.getGeneric(0).resolve();  // User.class

// 第二个参数 Map<String, Object>
ResolvableType param2 = ResolvableType.forMethodParameter(saveMethod, 1);
Class param2Key = param2.getGeneric(0).resolve();      // String.class
Class param2Value = param2.getGeneric(1).resolve();    // Object.class
```

### 4. forMethodReturnType - 从方法返回类型创建

```java
/**
 * 从方法返回类型创建
 * 考虑类继承关系中的泛型替换
 */
public static ResolvableType forMethodReturnType(Method method, Class implementingClass) {
    Assert.notNull(method, "Method must not be null");
    Type genericReturnType = method.getGenericReturnType();
    // 使用实现类的变量解析器，可以解析泛型变量
    return forType(genericReturnType, 
        new TypeVariableResolver(implementingClass));
}

// 使用示例
public interface Repository<T> {
    List<T> findAll();
    Optional<T> findById(Long id);
}

public class UserRepository implements Repository<User> {
    @Override
    public List<User> findAll() { return null; }
    
    @Override
    public Optional<User> findById(Long id) { return null; }
}

// 解析 findAll 的返回类型
Method findAllMethod = UserRepository.class.getMethod("findAll");
ResolvableType returnType = ResolvableType.forMethodReturnType(findAllMethod, UserRepository.class);
Class genericType = returnType.getGeneric(0).resolve();  // User.class

// 解析 findById 的返回类型
Method findByIdMethod = UserRepository.class.getMethod("findById", Long.class);
ResolvableType returnType2 = ResolvableType.forMethodReturnType(findByIdMethod, UserRepository.class);
Class optionalType = returnType2.getGeneric(0).resolve();  // User.class
```

## 解析方法详解

### 1. resolve - 解析为 Class

```java
/**
 * 解析为具体 Class
 * 如果无法解析返回 null
 */
public Class resolve() {
    if (this.resolved != null) {
        return this.resolved;
    }
    if (this.type == null) {
        return null;
    }
    return resolveClass();
}

/**
 * 解析为指定类型的 Class
 * 如果解析结果不是 expectedType 的子类，返回 null
 */
public Class resolve(Class expectedType) {
    Class resolved = resolve();
    if (resolved == null || expectedType.isAssignableFrom(resolved)) {
        return resolved;
    }
    return null;
}

// 使用示例
ResolvableType type = ResolvableType.forClass(UserService.class);
Class resolved = type.resolve();  // UserService.class

// 验证类型
Class serviceClass = type.resolve(BaseService.class);  // 如果 UserService 继承 BaseService，返回 UserService.class
```

### 2. getGeneric - 获取泛型参数

```java
/**
 * 获取指定位置的泛型参数
 * 例如：Map<String, Integer> 的 getGeneric(0) 返回 String
 */
public ResolvableType getGeneric(int index) {
    if (this.generics == null || index < 0 || index >= this.generics.length) {
        return NONE;  // 返回空类型
    }
    return this.generics[index];
}

/**
 * 获取所有泛型参数
 */
public ResolvableType[] getGenerics() {
    return this.generics != null ? this.generics : new ResolvableType[0];
}

// 使用示例
ResolvableType mapType = ResolvableType.forClassWithGenerics(Map.class, String.class, Integer.class);
ResolvableType keyType = mapType.getGeneric(0);    // String
ResolvableType valueType = mapType.getGeneric(1);  // Integer
```

### 3. as - 类型转换

```java
/**
 * 将当前类型视为指定类的子类型，解析其泛型
 * 
 * 例如：
 * class UserService implements BaseService<User>
 * ResolvableType.forClass(UserService.class).as(BaseService.class)
 * 返回 BaseService<User> 的 ResolvableType
 */
public ResolvableType as(Class type) {
    if (this.resolved == null || type.isAssignableFrom(this.resolved)) {
        return this;
    }
    // 递归查找父类和接口
    for (ResolvableType interfaceType : getInterfaces()) {
        ResolvableType asType = interfaceType.as(type);
        if (asType != NONE) {
            return asType;
        }
    }
    return getSuperType().as(type);
}

// 使用示例
public interface BaseService<T, ID> {
    T findById(ID id);
}

public class UserService implements BaseService<User, Long> {}

ResolvableType type = ResolvableType.forClass(UserService.class);
ResolvableType baseService = type.as(BaseService.class);

Class entityClass = baseService.getGeneric(0).resolve();  // User.class
Class idClass = baseService.getGeneric(1).resolve();      // Long.class
```

### 4. 类型检查方法

```java
/**
 * 检查当前类型是否可以从其他类型赋值
 * 支持数组、泛型、通配符等复杂类型检查
 */
public boolean isAssignableFrom(ResolvableType other) {
    // 处理数组类型
    if (isArray()) {
        return other.isArray() && 
               getComponentType().isAssignableFrom(other.getComponentType());
    }
    // 处理泛型类型
    if (resolved == null || other.resolved == null) {
        return true;  // 无法确定时返回 true
    }
    return resolved.isAssignableFrom(other.resolved);
}

/**
 * 检查是否为数组类型
 */
public boolean isArray() {
    if (this == NONE) {
        return false;
    }
    return (this.type instanceof Class && ((Class) this.type).isArray()) ||
           this.type instanceof GenericArrayType ||
           this.componentType != null;
}

/**
 * 获取数组组件类型
 */
public ResolvableType getComponentType() {
    if (this == NONE) {
        return NONE;
    }
    if (this.componentType != null) {
        return this.componentType;
    }
    // 从 type 解析组件类型
    if (this.type instanceof Class) {
        Class componentType = ((Class) this.type).getComponentType();
        return forClass(componentType);
    }
    return NONE;
}

// 使用示例
ResolvableType arrayType = ResolvableType.forClass(String[].class);
boolean isArray = arrayType.isArray();  // true
ResolvableType componentType = arrayType.getComponentType();
Class componentClass = componentType.resolve();  // String.class
```

## 内部实现原理

### 类型解析流程

```java
private Class resolveClass() {
    // 1. 如果是 Class 类型，直接返回
    if (this.type instanceof Class) {
        return (Class) this.type;
    }
    
    // 2. 如果是 ParameterizedType（如 List<String>），返回原始类型
    if (this.type instanceof ParameterizedType) {
        Type rawType = ((ParameterizedType) this.type).getRawType();
        if (rawType instanceof Class) {
            return (Class) rawType;
        }
    }
    
    // 3. 处理 TypeVariable（泛型变量如 T）、WildcardType 等
    if (this.variableResolver != null) {
        ResolvableType resolved = this.variableResolver.resolveVariable(
            (TypeVariable) this.type);
        if (resolved != null) {
            return resolved.resolve();
        }
    }
    return null;
}
```

### VariableResolver 变量解析器

```java
/**
 * 变量解析器接口
 * 用于解析泛型变量（如 T、K、V）为具体类型
 */
interface VariableResolver {
    ResolvableType resolveVariable(TypeVariable variable);
}

/**
 * 默认实现：基于 Class 的变量解析
 */
private static class DefaultVariableResolver implements VariableResolver {
    
    private final Class source;
    
    DefaultVariableResolver(Class source) {
        this.source = source;
    }
    
    @Override
    public ResolvableType resolveVariable(TypeVariable variable) {
        // 在父类和接口中查找泛型变量的实际类型
        return ResolvableType.forType(
            GenericTypeResolver.resolveTypeVariable(source, variable),
            this
        );
    }
}
```

## 缓存机制

### 缓存设计

```java
public class ResolvableType implements Serializable {
    
    // 使用 ConcurrentReferenceHashMap 实现缓存
    // 键: Type + ClassLoader, 值: ResolvableType
    // 使用软引用，在内存不足时允许 GC 回收
    private static final ConcurrentReferenceHashMap<ResolvableType, ResolvableType> 
        cache = new ConcurrentReferenceHashMap<>(256);
    
    /**
     * 带缓存的工厂方法
     * 相同的 Type 会返回相同的 ResolvableType 实例
     */
    public static ResolvableType forType(Type type, VariableResolver variableResolver) {
        if (type == null) {
            return NONE;
        }
        
        // 构建缓存键（使用 type 和 variableResolver）
        ResolvableType key = new ResolvableType(type, variableResolver);
        
        // 尝试从缓存获取
        ResolvableType cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        
        // 创建新的 ResolvableType
        ResolvableType result = new ResolvableType(type, variableResolver, null, null);
        
        // 放入缓存（如果已存在则使用已存在的）
        ResolvableType existing = cache.putIfAbsent(key, result);
        return existing != null ? existing : result;
    }
    
    /**
     * 清空缓存
     * 在类加载器被回收时调用，防止内存泄漏
     */
    public static void clearCache() {
        cache.clear();
    }
}

/**
 * 并发引用 HashMap - Spring 自定义实现
 * 特点：
 * 1. 线程安全（使用分段锁）
 * 2. 支持软引用/弱引用作为键或值
 * 3. 自动清理被 GC 回收的条目
 */
public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> {
    
    private final ReferenceType referenceType;  // SOFT 或 WEAK
    
    public enum ReferenceType {
        SOFT,  // 软引用 - 内存不足时回收
        WEAK   // 弱引用 - GC 时回收
    }
}
```

## 实际应用场景

### 1. Spring 依赖注入

```java
@Component
public class UserService {
    
    // Spring 使用 ResolvableType 解析字段泛型
    @Autowired
    private List<UserRepository> repositories;  // 解析出 UserRepository
    
    @Autowired
    private Map<String, UserRepository> repositoryMap;  // 解析出 String, UserRepository
}
```

### 2. Spring Data 泛型解析

```java
public class RepositoryFactory {
    
    public Class getEntityClass(Class repositoryClass) {
        ResolvableType type = ResolvableType.forClass(repositoryClass);
        ResolvableType repositoryInterface = type.as(Repository.class);
        return repositoryInterface.getGeneric(0).resolve();
    }
}

// 使用
Class entityClass = getEntityClass(UserRepository.class);  // User.class
```

### 3. 事件监听泛型解析

```java
@Component
public class UserEventListener implements ApplicationListener<UserCreatedEvent> {
    
    @Override
    public void onApplicationEvent(UserCreatedEvent event) {
        // Spring 使用 ResolvableType 确定监听的事件类型
    }
}
```

## 总结

ResolvableType 是 Spring Framework 类型系统的基石，它通过以下方式解决了 Java 泛型擦除问题：

1. **统一抽象**：封装了 Java 反射中复杂的 Type 体系
2. **便捷 API**：提供简洁的方法获取泛型参数
3. **性能优化**：使用缓存避免重复解析
4. **内存安全**：使用软引用防止内存泄漏
5. **广泛应用**：在依赖注入、事件监听、数据访问等场景都有应用
