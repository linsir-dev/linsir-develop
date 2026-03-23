# Spring 反射工具功能扩展 - 深入设计与封装建议

## 一、概述

本文档基于 `00-reflection-overview.md` 中介绍的核心反射工具，提供深入的设计与封装建议，帮助开发者构建更强大、更灵活的反射工具链。

## 二、核心扩展方向

### 2.1 扩展方向矩阵

| 扩展方向 | 当前能力 | 扩展目标 | 设计模式 | 复杂度 |
|---------|---------|---------|---------|--------|
| 字段操作增强 | 基础CRUD | 批量操作、链式调用 | Builder模式 | 中 |
| 方法调用优化 | 直接调用 | 异步调用、批量调用 | 代理模式 | 中 |
| 反射结果缓存 | 无缓存 | 多级缓存、过期策略 | 装饰器模式 | 高 |
| 类型安全封装 | 原始类型 | 泛型安全、类型推断 | 模板方法 | 中 |
| 注解处理器链 | 单一处理 | 责任链、条件处理 | 责任链模式 | 高 |
| 属性访问器 | 字段直接访问 | 属性路径、嵌套访问 | 访问者模式 | 高 |

## 三、详细设计方案

### 3.1 字段操作增强 - FieldAccessor 设计

#### 3.1.1 问题分析

当前 `ReflectionUtils` 的字段操作存在以下局限：

1. **无法处理嵌套属性**：如 `user.address.city` 需要多次反射
2. **缺乏批量操作**：无法一次性设置多个字段
3. **类型转换缺失**：字段类型与值类型不匹配时需要手动转换
4. **链式调用不支持**：无法流畅地连续操作多个字段

#### 3.1.2 设计方案 - 属性访问器模式

```java
/**
 * 属性访问器接口
 * 提供对对象属性的统一访问方式
 */
public interface PropertyAccessor {
    
    /**
     * 获取属性值
     * @param target 目标对象
     * @param propertyPath 属性路径，支持嵌套如 "address.city"
     * @return 属性值
     */
    Object getProperty(Object target, String propertyPath);
    
    /**
     * 设置属性值
     * @param target 目标对象
     * @param propertyPath 属性路径
     * @param value 属性值
     */
    void setProperty(Object target, String propertyPath, Object value);
    
    /**
     * 获取属性类型
     */
    Class<?> getPropertyType(Object target, String propertyPath);
    
    /**
     * 判断属性是否可读
     */
    boolean isReadable(Object target, String propertyPath);
    
    /**
     * 判断属性是否可写
     */
    boolean isWritable(Object target, String propertyPath);
}

/**
 * 反射属性访问器实现
 * 基于 ReflectionUtils 实现属性访问
 */
public class ReflectionPropertyAccessor implements PropertyAccessor {
    
    private final PropertyPathParser pathParser;
    private final TypeConverter typeConverter;
    
    public ReflectionPropertyAccessor() {
        this.pathParser = new PropertyPathParser();
        this.typeConverter = new DefaultTypeConverter();
    }
    
    @Override
    public Object getProperty(Object target, String propertyPath) {
        if (target == null || propertyPath == null) {
            return null;
        }
        
        String[] pathSegments = pathParser.parse(propertyPath);
        Object current = target;
        
        for (String segment : pathSegments) {
            if (current == null) {
                return null;
            }
            current = getNestedProperty(current, segment);
        }
        
        return current;
    }
    
    private Object getNestedProperty(Object target, String propertyName) {
        Field field = ReflectionUtils.findField(target.getClass(), propertyName);
        if (field == null) {
            throw new PropertyAccessException("找不到属性: " + propertyName);
        }
        return ReflectionUtils.getField(field, target);
    }
    
    @Override
    public void setProperty(Object target, String propertyPath, Object value) {
        if (target == null || propertyPath == null) {
            return;
        }
        
        String[] pathSegments = pathParser.parse(propertyPath);
        
        // 遍历到倒数第二个属性
        Object current = target;
        for (int i = 0; i < pathSegments.length - 1; i++) {
            if (current == null) {
                throw new PropertyAccessException("路径中的属性为null: " + pathSegments[i]);
            }
            current = getNestedProperty(current, pathSegments[i]);
        }
        
        // 设置最后一个属性
        String finalProperty = pathSegments[pathSegments.length - 1];
        Field field = ReflectionUtils.findField(current.getClass(), finalProperty);
        if (field == null) {
            throw new PropertyAccessException("找不到属性: " + finalProperty);
        }
        
        // 类型转换
        Object convertedValue = typeConverter.convert(value, field.getType());
        ReflectionUtils.setField(field, current, convertedValue);
    }
    
    @Override
    public Class<?> getPropertyType(Object target, String propertyPath) {
        // 实现获取属性类型逻辑
        return null;
    }
    
    @Override
    public boolean isReadable(Object target, String propertyPath) {
        try {
            getProperty(target, propertyPath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean isWritable(Object target, String propertyPath) {
        // 实现可写性检查逻辑
        return true;
    }
}

/**
 * 属性路径解析器
 */
public class PropertyPathParser {
    
    private static final String NESTED_PROPERTY_SEPARATOR = ".";
    
    public String[] parse(String propertyPath) {
        if (propertyPath == null || propertyPath.isEmpty()) {
            return new String[0];
        }
        return propertyPath.split("\\" + NESTED_PROPERTY_SEPARATOR);
    }
}

/**
 * 类型转换器接口
 */
public interface TypeConverter {
    Object convert(Object source, Class<?> targetType);
}

/**
 * 默认类型转换器
 */
public class DefaultTypeConverter implements TypeConverter {
    
    private final Map<ConverterKey, Converter<?, ?>> converters = new HashMap<>();
    
    public DefaultTypeConverter() {
        registerDefaultConverters();
    }
    
    private void registerDefaultConverters() {
        // String -> Integer
        registerConverter(String.class, Integer.class, Integer::parseInt);
        registerConverter(String.class, int.class, Integer::parseInt);
        
        // String -> Long
        registerConverter(String.class, Long.class, Long::parseLong);
        registerConverter(String.class, long.class, Long::parseLong);
        
        // String -> Boolean
        registerConverter(String.class, Boolean.class, Boolean::parseBoolean);
        registerConverter(String.class, boolean.class, Boolean::parseBoolean);
        
        // Integer -> String
        registerConverter(Integer.class, String.class, String::valueOf);
        
        // 更多转换器...
    }
    
    private <S, T> void registerConverter(Class<S> sourceType, Class<T> targetType, 
                                          Function<S, T> converter) {
        converters.put(new ConverterKey(sourceType, targetType), converter);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Object convert(Object source, Class<?> targetType) {
        if (source == null) {
            return null;
        }
        
        if (targetType.isInstance(source)) {
            return source;
        }
        
        ConverterKey key = new ConverterKey(source.getClass(), targetType);
        Converter<Object, Object> converter = (Converter<Object, Object>) converters.get(key);
        
        if (converter != null) {
            return converter.convert(source);
        }
        
        throw new ConversionException(
            String.format("无法从 %s 转换为 %s", source.getClass(), targetType));
    }
    
    private static class ConverterKey {
        private final Class<?> sourceType;
        private final Class<?> targetType;
        
        public ConverterKey(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConverterKey that = (ConverterKey) o;
            return Objects.equals(sourceType, that.sourceType) &&
                   Objects.equals(targetType, that.targetType);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(sourceType, targetType);
        }
    }
}
```

**使用示例**：

```java
// 1. 创建属性访问器
PropertyAccessor accessor = new ReflectionPropertyAccessor();

// 2. 简单属性访问
User user = new User();
accessor.setProperty(user, "username", "john");
String username = (String) accessor.getProperty(user, "username");

// 3. 嵌套属性访问
accessor.setProperty(user, "address.city", "Beijing");
String city = (String) accessor.getProperty(user, "address.city");

// 4. 自动类型转换
accessor.setProperty(user, "age", "25");  // String -> int
int age = (int) accessor.getProperty(user, "age");
```

#### 3.1.3 批量操作设计

```java
/**
 * 批量属性设置器
 */
public class BatchPropertySetter {
    
    private final PropertyAccessor propertyAccessor;
    private final Map<String, Object> propertyValues = new LinkedHashMap<>();
    
    public BatchPropertySetter(PropertyAccessor propertyAccessor) {
        this.propertyAccessor = propertyAccessor;
    }
    
    /**
     * 添加属性设置
     */
    public BatchPropertySetter add(String propertyPath, Object value) {
        propertyValues.put(propertyPath, value);
        return this;
    }
    
    /**
     * 应用到目标对象
     */
    public void applyTo(Object target) {
        for (Map.Entry<String, Object> entry : propertyValues.entrySet()) {
            propertyAccessor.setProperty(target, entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * 应用到多个目标对象
     */
    public void applyTo(List<?> targets) {
        for (Object target : targets) {
            applyTo(target);
        }
    }
    
    /**
     * 创建新对象并设置属性
     */
    public <T> T createAndApply(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            applyTo(instance);
            return instance;
        } catch (Exception e) {
            throw new PropertyAccessException("创建实例失败", e);
        }
    }
}

/**
 * 属性映射器 - 对象间属性复制
 */
public class PropertyMapper {
    
    private final PropertyAccessor propertyAccessor;
    private final Map<String, String> propertyMappings = new LinkedHashMap<>();
    
    public PropertyMapper(PropertyAccessor propertyAccessor) {
        this.propertyAccessor = propertyAccessor;
    }
    
    /**
     * 添加属性映射
     * @param sourceProperty 源属性路径
     * @param targetProperty 目标属性路径
     */
    public PropertyMapper map(String sourceProperty, String targetProperty) {
        propertyMappings.put(sourceProperty, targetProperty);
        return this;
    }
    
    /**
     * 添加同名属性映射
     */
    public PropertyMapper map(String property) {
        return map(property, property);
    }
    
    /**
     * 执行映射
     */
    public void map(Object source, Object target) {
        for (Map.Entry<String, String> entry : propertyMappings.entrySet()) {
            Object value = propertyAccessor.getProperty(source, entry.getKey());
            propertyAccessor.setProperty(target, entry.getValue(), value);
        }
    }
    
    /**
     * 创建并映射到新对象
     */
    public <T> T mapToNew(Object source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            map(source, target);
            return target;
        } catch (Exception e) {
            throw new PropertyAccessException("创建目标对象失败", e);
        }
    }
}
```

**使用示例**：

```java
// 批量设置属性
User user = new BatchPropertySetter(accessor)
    .add("username", "john")
    .add("email", "john@example.com")
    .add("address.city", "Beijing")
    .add("age", 25)
    .createAndApply(User.class);

// 对象间属性复制
UserDTO dto = new PropertyMapper(accessor)
    .map("username")
    .map("email")
    .map("address.city", "city")
    .mapToNew(user, UserDTO.class);
```

---

### 3.2 方法调用优化 - MethodInvoker 增强

#### 3.2.1 问题分析

当前 `ReflectionUtils.invokeMethod()` 存在以下问题：

1. **同步阻塞**：大量方法调用时性能受限
2. **缺乏批量调用**：无法一次性调用多个方法
3. **结果处理单一**：不支持回调、Future等异步结果处理
4. **参数匹配不灵活**：需要精确匹配参数类型

#### 3.2.2 设计方案 - 方法调用器链

```java
/**
 * 方法调用器接口
 */
public interface MethodInvoker {
    
    /**
     * 同步调用
     */
    Object invoke(Object target, String methodName, Object... args);
    
    /**
     * 异步调用
     */
    Future<Object> invokeAsync(Object target, String methodName, Object... args);
    
    /**
     * 批量调用
     */
    List<Object> invokeBatch(List<InvocationRequest> requests);
    
    /**
     * 带回调的调用
     */
    void invokeWithCallback(Object target, String methodName, 
                           InvocationCallback callback, Object... args);
}

/**
 * 调用请求
 */
public class InvocationRequest {
    private Object target;
    private String methodName;
    private Object[] args;
    private Class<?>[] argTypes;  // 可选，用于精确匹配
    
    // Builder模式构建
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private InvocationRequest request = new InvocationRequest();
        
        public Builder target(Object target) {
            request.target = target;
            return this;
        }
        
        public Builder method(String methodName) {
            request.methodName = methodName;
            return this;
        }
        
        public Builder args(Object... args) {
            request.args = args;
            return this;
        }
        
        public Builder argTypes(Class<?>... argTypes) {
            request.argTypes = argTypes;
            return this;
        }
        
        public InvocationRequest build() {
            return request;
        }
    }
    
    // getters...
}

/**
 * 调用回调接口
 */
public interface InvocationCallback {
    void onSuccess(Object result);
    void onFailure(Throwable exception);
}

/**
 * 增强方法调用器
 */
public class EnhancedMethodInvoker implements MethodInvoker {
    
    private final ExecutorService executor;
    private final MethodResolver methodResolver;
    private final ParameterResolver parameterResolver;
    
    public EnhancedMethodInvoker() {
        this.executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
        this.methodResolver = new SmartMethodResolver();
        this.parameterResolver = new DefaultParameterResolver();
    }
    
    @Override
    public Object invoke(Object target, String methodName, Object... args) {
        Method method = methodResolver.resolve(target.getClass(), methodName, args);
        Object[] resolvedArgs = parameterResolver.resolve(method, args);
        return ReflectionUtils.invokeMethod(method, target, resolvedArgs);
    }
    
    @Override
    public Future<Object> invokeAsync(Object target, String methodName, Object... args) {
        return executor.submit(() -> invoke(target, methodName, args));
    }
    
    @Override
    public List<Object> invokeBatch(List<InvocationRequest> requests) {
        return requests.parallelStream()
            .map(req -> invoke(req.getTarget(), req.getMethodName(), req.getArgs()))
            .collect(Collectors.toList());
    }
    
    @Override
    public void invokeWithCallback(Object target, String methodName, 
                                   InvocationCallback callback, Object... args) {
        executor.submit(() -> {
            try {
                Object result = invoke(target, methodName, args);
                callback.onSuccess(result);
            } catch (Exception e) {
                callback.onFailure(e);
            }
        });
    }
}

/**
 * 智能方法解析器
 * 支持模糊匹配和参数类型推断
 */
public class SmartMethodResolver implements MethodResolver {
    
    @Override
    public Method resolve(Class<?> targetClass, String methodName, Object[] args) {
        // 1. 精确匹配
        Class<?>[] argTypes = Arrays.stream(args)
            .map(a -> a != null ? a.getClass() : null)
            .toArray(Class<?>[]::new);
        
        Method method = ReflectionUtils.findMethod(targetClass, methodName, argTypes);
        if (method != null) {
            return method;
        }
        
        // 2. 模糊匹配 - 查找所有同名方法
        List<Method> candidates = new ArrayList<>();
        ReflectionUtils.doWithMethods(targetClass, candidates::add, 
            m -> m.getName().equals(methodName));
        
        if (candidates.isEmpty()) {
            throw new MethodNotFoundException("找不到方法: " + methodName);
        }
        
        // 3. 选择最匹配的方法
        return selectBestMatch(candidates, args);
    }
    
    private Method selectBestMatch(List<Method> candidates, Object[] args) {
        // 实现匹配算法：参数数量、类型兼容性等
        return candidates.get(0);  // 简化实现
    }
}
```

**使用示例**：

```java
// 创建调用器
EnhancedMethodInvoker invoker = new EnhancedMethodInvoker();

// 1. 同步调用
Object result = invoker.invoke(userService, "findById", 1L);

// 2. 异步调用
Future<Object> future = invoker.invokeAsync(userService, "findById", 1L);
Object asyncResult = future.get();

// 3. 批量调用
List<InvocationRequest> requests = Arrays.asList(
    InvocationRequest.builder()
        .target(userService)
        .method("findById")
        .args(1L)
        .build(),
    InvocationRequest.builder()
        .target(userService)
        .method("findById")
        .args(2L)
        .build()
);
List<Object> results = invoker.invokeBatch(requests);

// 4. 带回调的调用
invoker.invokeWithCallback(userService, "save", new InvocationCallback() {
    @Override
    public void onSuccess(Object result) {
        System.out.println("保存成功: " + result);
    }
    
    @Override
    public void onFailure(Throwable exception) {
        System.err.println("保存失败: " + exception.getMessage());
    }
}, user);
```

---

### 3.3 反射结果缓存 - 多级缓存架构

#### 3.3.1 问题分析

当前 `ReflectionCache` 是简单的内存缓存，存在以下问题：

1. **内存占用无限制**：可能导致OOM
2. **无过期策略**：缓存数据可能过时
3. **无持久化**：重启后缓存丢失
4. **单级缓存**：无法平衡性能和内存

#### 3.3.2 设计方案 - 多级缓存架构

```java
/**
 * 缓存级别枚举
 */
public enum CacheLevel {
    L1_MEMORY,      // 一级缓存：内存，速度最快
    L2_CAFFEINE,    // 二级缓存：Caffeine，带过期策略
    L3_REDIS        // 三级缓存：Redis，分布式共享
}

/**
 * 缓存配置
 */
public class CacheConfiguration {
    private int maxSize = 1000;
    private Duration expireAfterWrite = Duration.ofMinutes(10);
    private Duration expireAfterAccess = Duration.ofMinutes(5);
    private boolean recordStats = true;
    
    // Builder模式...
}

/**
 * 多级反射缓存
 */
public class MultiLevelReflectionCache {
    
    private final Cache<Class<?>, Field[]> l1FieldCache;
    private final Cache<Class<?>, Method[]> l1MethodCache;
    private final com.github.benmanes.caffeine.cache.Cache<String, Field> l2FieldCache;
    private final com.github.benmanes.caffeine.cache.Cache<String, Method> l2MethodCache;
    
    public MultiLevelReflectionCache(CacheConfiguration config) {
        // L1 缓存：ConcurrentHashMap
        this.l1FieldCache = new ConcurrentHashMap<>();
        this.l1MethodCache = new ConcurrentHashMap<>();
        
        // L2 缓存：Caffeine
        this.l2FieldCache = Caffeine.newBuilder()
            .maximumSize(config.getMaxSize())
            .expireAfterWrite(config.getExpireAfterWrite())
            .recordStats()
            .build();
        
        this.l2MethodCache = Caffeine.newBuilder()
            .maximumSize(config.getMaxSize())
            .expireAfterWrite(config.getExpireAfterWrite())
            .recordStats()
            .build();
    }
    
    /**
     * 获取字段（多级缓存）
     */
    public Field[] getDeclaredFields(Class<?> clazz) {
        // 1. 查L1缓存
        Field[] fields = l1FieldCache.get(clazz);
        if (fields != null) {
            return fields;
        }
        
        // 2. 查L2缓存
        String key = "fields:" + clazz.getName();
        fields = (Field[]) l2FieldCache.get(key, k -> {
            // 3. 从反射获取
            return ReflectionUtils.getAllDeclaredFields(clazz);
        });
        
        // 4. 回填L1缓存
        if (fields != null) {
            l1FieldCache.put(clazz, fields);
        }
        
        return fields;
    }
    
    /**
     * 查找方法（多级缓存）
     */
    public Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        String key = buildMethodKey(clazz, methodName, paramTypes);
        
        // 1. 查L2缓存（方法查找用L2更合适，因为key复杂）
        return l2MethodCache.get(key, k -> 
            ReflectionUtils.findMethod(clazz, methodName, paramTypes));
    }
    
    /**
     * 获取缓存统计
     */
    public CacheStats getStats() {
        return CacheStats.builder()
            .l1FieldCacheSize(l1FieldCache.size())
            .l1MethodCacheSize(l1MethodCache.size())
            .l2FieldCacheStats(l2FieldCache.stats())
            .l2MethodCacheStats(l2MethodCache.stats())
            .build();
    }
    
    private String buildMethodKey(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        StringBuilder key = new StringBuilder(clazz.getName());
        key.append(".").append(methodName);
        key.append("(");
        if (paramTypes != null) {
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) key.append(",");
                key.append(paramTypes[i] != null ? paramTypes[i].getName() : "null");
            }
        }
        key.append(")");
        return key.toString();
    }
}
```

---

### 3.4 类型安全封装 - 泛型反射工具

#### 3.4.1 问题分析

当前反射API返回的都是 `Object` 类型，需要强制类型转换：

```java
// 不安全的代码
Method method = ReflectionUtils.findMethod(service.getClass(), "findById", Long.class);
Object result = ReflectionUtils.invokeMethod(method, service, 1L);
User user = (User) result;  // 可能抛出 ClassCastException
```

#### 3.4.2 设计方案 - 泛型安全封装

```java
/**
 * 泛型方法引用
 */
public class GenericMethodReference<T, R> {
    
    private final Class<T> targetClass;
    private final String methodName;
    private final Class<?>[] paramTypes;
    private final Class<R> returnType;
    
    private GenericMethodReference(Class<T> targetClass, String methodName, 
                                   Class<?>[] paramTypes, Class<R> returnType) {
        this.targetClass = targetClass;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }
    
    /**
     * 创建方法引用
     */
    public static <T, R> GenericMethodReference<T, R> of(
            Class<T> targetClass, String methodName, 
            Class<R> returnType, Class<?>... paramTypes) {
        return new GenericMethodReference<>(targetClass, methodName, paramTypes, returnType);
    }
    
    /**
     * 调用方法（类型安全）
     */
    @SuppressWarnings("unchecked")
    public R invoke(T target, Object... args) {
        Method method = ReflectionUtils.findMethod(targetClass, methodName, paramTypes);
        Object result = ReflectionUtils.invokeMethod(method, target, args);
        return (R) result;
    }
    
    /**
     * 获取返回类型
     */
    public Class<R> getReturnType() {
        return returnType;
    }
}

/**
 * 泛型字段引用
 */
public class GenericFieldReference<T, F> {
    
    private final Class<T> targetClass;
    private final String fieldName;
    private final Class<F> fieldType;
    
    public GenericFieldReference(Class<T> targetClass, String fieldName, Class<F> fieldType) {
        this.targetClass = targetClass;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }
    
    /**
     * 获取字段值（类型安全）
     */
    @SuppressWarnings("unchecked")
    public F get(T target) {
        Field field = ReflectionUtils.findField(targetClass, fieldName, fieldType);
        return (F) ReflectionUtils.getField(field, target);
    }
    
    /**
     * 设置字段值（类型安全）
     */
    public void set(T target, F value) {
        Field field = ReflectionUtils.findField(targetClass, fieldName, fieldType);
        ReflectionUtils.setField(field, target, value);
    }
}

/**
 * 类型安全的反射工具类
 */
public class TypeSafeReflectionUtils {
    
    /**
     * 创建方法引用
     */
    public static <T, R> GenericMethodReference<T, R> method(
            Class<T> targetClass, String methodName, 
            Class<R> returnType, Class<?>... paramTypes) {
        return GenericMethodReference.of(targetClass, methodName, returnType, paramTypes);
    }
    
    /**
     * 创建字段引用
     */
    public static <T, F> GenericFieldReference<T, F> field(
            Class<T> targetClass, String fieldName, Class<F> fieldType) {
        return new GenericFieldReference<>(targetClass, fieldName, fieldType);
    }
}
```

**使用示例**：

```java
// 1. 创建类型安全的方法引用
GenericMethodReference<UserService, User> findByIdMethod = 
    TypeSafeReflectionUtils.method(
        UserService.class, 
        "findById", 
        User.class, 
        Long.class);

// 2. 调用（无需强制类型转换）
User user = findByIdMethod.invoke(userService, 1L);

// 3. 创建类型安全的字段引用
GenericFieldReference<User, String> usernameField = 
    TypeSafeReflectionUtils.field(User.class, "username", String.class);

// 4. 读写字段（类型安全）
String username = usernameField.get(user);
usernameField.set(user, "newName");
```

---

### 3.5 注解处理器链 - 责任链模式

#### 3.5.1 问题分析

当前注解处理是硬编码的，缺乏灵活性：

```java
// 硬编码处理
if (field.isAnnotationPresent(Autowired.class)) {
    // 处理 Autowired
} else if (field.isAnnotationPresent(Value.class)) {
    // 处理 Value
}
// 新增注解需要修改源码
```

#### 3.5.2 设计方案 - 责任链模式

```java
/**
 * 注解处理器接口
 */
public interface AnnotationHandler<A extends Annotation> {
    
    /**
     * 获取处理的注解类型
     */
    Class<A> getAnnotationType();
    
    /**
     * 处理注解
     * @param annotation 注解实例
     * @param target 目标对象
     * @param element 注解所在的元素（Field/Method/Class）
     * @param context 处理上下文
     */
    void handle(A annotation, Object target, AnnotatedElement element, 
                AnnotationHandlerContext context);
    
    /**
     * 处理器优先级，数字越小优先级越高
     */
    default int getOrder() {
        return 100;
    }
}

/**
 * 注解处理器上下文
 */
public class AnnotationHandlerContext {
    private final Map<String, Object> attributes = new HashMap<>();
    private final ApplicationContext applicationContext;
    
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
    
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}

/**
 * Autowired 注解处理器
 */
public class AutowiredAnnotationHandler implements AnnotationHandler<Autowired> {
    
    @Override
    public Class<Autowired> getAnnotationType() {
        return Autowired.class;
    }
    
    @Override
    public void handle(Autowired annotation, Object target, AnnotatedElement element,
                       AnnotationHandlerContext context) {
        if (!(element instanceof Field)) {
            return;
        }
        
        Field field = (Field) element;
        Class<?> fieldType = field.getType();
        
        // 从上下文获取 Bean
        Object bean = context.getApplicationContext().getBean(fieldType);
        if (bean == null && annotation.required()) {
            throw new DependencyNotFoundException("找不到依赖: " + fieldType);
        }
        
        // 注入
        ReflectionUtils.setField(field, target, bean);
    }
}

/**
 * Value 注解处理器
 */
public class ValueAnnotationHandler implements AnnotationHandler<Value> {
    
    @Override
    public Class<Value> getAnnotationType() {
        return Value.class;
    }
    
    @Override
    public void handle(Value annotation, Object target, AnnotatedElement element,
                       AnnotationHandlerContext context) {
        if (!(element instanceof Field)) {
            return;
        }
        
        Field field = (Field) element;
        String expression = annotation.value();
        
        // 解析表达式 ${property.name}
        String propertyValue = resolveExpression(expression, context);
        
        // 类型转换并注入
        Object convertedValue = convertType(propertyValue, field.getType());
        ReflectionUtils.setField(field, target, convertedValue);
    }
    
    private String resolveExpression(String expression, AnnotationHandlerContext context) {
        // 实现表达式解析
        if (expression.startsWith("${") && expression.endsWith("}")) {
            String key = expression.substring(2, expression.length() - 1);
            return context.getApplicationContext().getEnvironment().getProperty(key);
        }
        return expression;
    }
    
    private Object convertType(String value, Class<?> targetType) {
        // 实现类型转换
        return value;
    }
}

/**
 * 注解处理器链
 */
public class AnnotationHandlerChain {
    
    private final List<AnnotationHandler<?>> handlers = new ArrayList<>();
    
    /**
     * 注册处理器
     */
    public void registerHandler(AnnotationHandler<?> handler) {
        handlers.add(handler);
        // 按优先级排序
        handlers.sort(Comparator.comparingInt(AnnotationHandler::getOrder));
    }
    
    /**
     * 处理对象的所有注解
     */
    public void process(Object target, AnnotationHandlerContext context) {
        Class<?> clazz = target.getClass();
        
        // 处理字段上的注解
        ReflectionUtils.doWithFields(clazz, field -> {
            for (Annotation annotation : field.getAnnotations()) {
                handleAnnotation(annotation, target, field, context);
            }
        });
        
        // 处理方法上的注解
        ReflectionUtils.doWithMethods(clazz, method -> {
            for (Annotation annotation : method.getAnnotations()) {
                handleAnnotation(annotation, target, method, context);
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private <A extends Annotation> void handleAnnotation(
            A annotation, Object target, AnnotatedElement element,
            AnnotationHandlerContext context) {
        
        for (AnnotationHandler<?> handler : handlers) {
            if (handler.getAnnotationType().isInstance(annotation)) {
                ((AnnotationHandler<A>) handler).handle(annotation, target, element, context);
                return;
            }
        }
    }
}
```

**使用示例**：

```java
// 1. 创建处理器链
AnnotationHandlerChain chain = new AnnotationHandlerChain();

// 2. 注册处理器
chain.registerHandler(new AutowiredAnnotationHandler());
chain.registerHandler(new ValueAnnotationHandler());
chain.registerHandler(new PostConstructAnnotationHandler());

// 3. 处理对象
AnnotationHandlerContext context = new AnnotationHandlerContext();
context.setApplicationContext(applicationContext);
chain.process(userService, context);
```

---

## 四、综合应用示例

### 4.1 完整的对象工厂

```java
/**
 * 对象工厂 - 综合运用所有扩展功能
 */
public class EnhancedObjectFactory {
    
    private final PropertyAccessor propertyAccessor;
    private final MethodInvoker methodInvoker;
    private final MultiLevelReflectionCache cache;
    private final AnnotationHandlerChain annotationHandlerChain;
    
    public EnhancedObjectFactory() {
        this.propertyAccessor = new ReflectionPropertyAccessor();
        this.methodInvoker = new EnhancedMethodInvoker();
        this.cache = new MultiLevelReflectionCache(new CacheConfiguration());
        this.annotationHandlerChain = new AnnotationHandlerChain();
        initializeHandlers();
    }
    
    private void initializeHandlers() {
        annotationHandlerChain.registerHandler(new AutowiredAnnotationHandler());
        annotationHandlerChain.registerHandler(new ValueAnnotationHandler());
    }
    
    /**
     * 创建并初始化对象
     */
    public <T> T createObject(Class<T> clazz, Map<String, Object> properties) {
        try {
            // 1. 创建实例
            T instance = clazz.getDeclaredConstructor().newInstance();
            
            // 2. 设置属性
            BatchPropertySetter setter = new BatchPropertySetter(propertyAccessor);
            properties.forEach(setter::add);
            setter.applyTo(instance);
            
            // 3. 处理注解
            AnnotationHandlerContext context = new AnnotationHandlerContext();
            annotationHandlerChain.process(instance, context);
            
            // 4. 调用初始化方法
            methodInvoker.invoke(instance, "init");
            
            return instance;
        } catch (Exception e) {
            throw new ObjectCreationException("创建对象失败", e);
        }
    }
    
    /**
     * 复制对象
     */
    public <S, T> T copyObject(S source, Class<T> targetClass, String... propertyNames) {
        PropertyMapper mapper = new PropertyMapper(propertyAccessor);
        for (String property : propertyNames) {
            mapper.map(property);
        }
        return mapper.mapToNew(source, targetClass);
    }
}
```

**使用示例**：

```java
EnhancedObjectFactory factory = new EnhancedObjectFactory();

// 创建并初始化对象
User user = factory.createObject(User.class, Map.of(
    "username", "john",
    "email", "john@example.com",
    "address.city", "Beijing"
));

// 复制对象
UserDTO dto = factory.copyObject(user, UserDTO.class, 
    "username", "email", "address.city");
```

---

## 五、性能优化建议

### 5.1 缓存策略矩阵

| 数据类型 | 缓存级别 | 过期策略 | 适用场景 |
|---------|---------|---------|---------|
| Class 元数据 | L1 | 永不过期 | 类信息不变化 |
| Field 数组 | L1+L2 | 写入后10分钟 | 字段信息相对稳定 |
| Method 查找 | L2 | 写入后5分钟 | 方法查找频繁 |
| 注解扫描 | L2+L3 | 写入后1小时 | 注解不常变化 |

### 5.2 并发优化

```java
// 使用 ConcurrentHashMap 替代 synchronized
private final ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<>();

// 使用 computeIfAbsent 保证原子性
public Method getMethod(String key, Function<String, Method> loader) {
    return methodCache.computeIfAbsent(key, loader);
}

// 使用 StampedLock 优化读写锁
private final StampedLock lock = new StampedLock();

public void updateCache(String key, Method method) {
    long stamp = lock.writeLock();
    try {
        methodCache.put(key, method);
    } finally {
        lock.unlockWrite(stamp);
    }
}
```

---

## 六、总结

本文档提供了基于 Spring 反射工具的深入设计与封装建议，包括：

1. **属性访问器模式**：支持嵌套属性、批量操作、类型转换
2. **方法调用器链**：支持同步/异步/批量调用、智能参数匹配
3. **多级缓存架构**：平衡性能和内存占用
4. **类型安全封装**：利用泛型消除强制类型转换
5. **注解处理器链**：责任链模式实现灵活的注解处理

这些设计方案遵循开闭原则、单一职责原则，提供了良好的扩展性和可维护性。

---

**文档版本**: 1.0.0  
**更新日期**: 2026-03-23  
**作者**: linsir
