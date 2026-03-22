# TypeDescriptor 深度分析

## 概述

`TypeDescriptor` 是 Spring 类型系统的另一个核心类，它封装了 Java 类型和注解信息，为类型转换和属性访问提供统一的描述符。

## 与 ResolvableType 的关系

```
TypeDescriptor (类型描述符)
├── ResolvableType resolvableType  # 类型信息
├── Class type                     # 原始类型
├── Annotation[] annotations       # 注解信息
└── TypeDescriptor elementType     # 集合元素类型（如果是集合）
```

TypeDescriptor 在 ResolvableType 的基础上增加了注解支持，是更完整的类型描述。

## 核心功能

### 1. 类型描述

```java
public class TypeDescriptor {
    
    // 类型信息
    private final ResolvableType resolvableType;
    
    // 原始类型
    private final Class type;
    
    // 注解信息
    private final Annotation[] annotations;
    
    // 元素类型描述符（如果是数组或集合）
    private TypeDescriptor elementTypeDescriptor;
    
    // map 的 key/value 类型描述符
    private TypeDescriptor mapKeyTypeDescriptor;
    private TypeDescriptor mapValueTypeDescriptor;
}
```

### 2. 创建方式

```java
// 1. 从 Class 创建
TypeDescriptor descriptor = TypeDescriptor.valueOf(String.class);

// 2. 从字段创建
Field field = User.class.getDeclaredField("name");
TypeDescriptor fieldDescriptor = new TypeDescriptor(field);

// 3. 从方法参数创建
Method method = UserService.class.getMethod("save", User.class);
TypeDescriptor paramDescriptor = new TypeDescriptor(
    new MethodParameter(method, 0)
);

// 4. 从 Property 创建
Property property = new Property(
    User.class, 
    "name", 
    User.class.getMethod("getName"),
    User.class.getMethod("setName", String.class)
);
TypeDescriptor propertyDescriptor = new TypeDescriptor(property);
```

## 核心方法详解

### 类型检查方法

```java
/**
 * 检查是否为数组类型
 */
public boolean isArray() {
    return getType().isArray();
}

/**
 * 检查是否为集合类型
 */
public boolean isCollection() {
    return Collection.class.isAssignableFrom(getType());
}

/**
 * 检查是否为 Map 类型
 */
public boolean isMap() {
    return Map.class.isAssignableFrom(getType());
}

/**
 * 检查是否为枚举类型
 */
public boolean isEnum() {
    return getType().isEnum();
}

/**
 * 检查是否为原始类型
 */
public boolean isPrimitive() {
    return getType().isPrimitive();
}

/**
 * 检查是否包装类型
 */
public boolean isWrapperType() {
    return ClassUtils.isPrimitiveWrapper(getType());
}

/**
 * 检查是否可为 null
 */
public boolean isAssignableTo(TypeDescriptor target) {
    return this.resolvableType.isAssignableFrom(target.resolvableType);
}
```

### 元素类型获取

```java
/**
 * 获取数组/集合的元素类型描述符
 */
public TypeDescriptor getElementTypeDescriptor() {
    if (this.elementTypeDescriptor == null) {
        if (isArray()) {
            // 数组元素类型
            Class elementType = getType().getComponentType();
            this.elementTypeDescriptor = TypeDescriptor.valueOf(elementType);
        } else if (isCollection()) {
            // 集合泛型参数
            ResolvableType elementType = this.resolvableType.asCollection().getGeneric(0);
            this.elementTypeDescriptor = new TypeDescriptor(elementType, null, getAnnotations());
        }
    }
    return this.elementTypeDescriptor;
}

// 使用示例
public class User {
    private List<String> tags;
    private String[] names;
}

Field tagsField = User.class.getDeclaredField("tags");
TypeDescriptor tagsDescriptor = new TypeDescriptor(tagsField);
TypeDescriptor elementDescriptor = tagsDescriptor.getElementTypeDescriptor();
Class elementType = elementDescriptor.getType();  // String.class

Field namesField = User.class.getDeclaredField("names");
TypeDescriptor namesDescriptor = new TypeDescriptor(namesField);
TypeDescriptor arrayElementDescriptor = namesDescriptor.getElementTypeDescriptor();
Class arrayElementType = arrayElementDescriptor.getType();  // String.class
```

### Map 类型处理

```java
/**
 * 获取 Map 的 Key 类型描述符
 */
public TypeDescriptor getMapKeyTypeDescriptor() {
    if (this.mapKeyTypeDescriptor == null) {
        if (isMap()) {
            ResolvableType keyType = this.resolvableType.asMap().getGeneric(0);
            this.mapKeyTypeDescriptor = new TypeDescriptor(keyType, null, getAnnotations());
        }
    }
    return this.mapKeyTypeDescriptor;
}

/**
 * 获取 Map 的 Value 类型描述符
 */
public TypeDescriptor getMapValueTypeDescriptor() {
    if (this.mapValueTypeDescriptor == null) {
        if (isMap()) {
            ResolvableType valueType = this.resolvableType.asMap().getGeneric(1);
            this.mapValueTypeDescriptor = new TypeDescriptor(valueType, null, getAnnotations());
        }
    }
    return this.mapValueTypeDescriptor;
}

// 使用示例
public class Config {
    private Map<String, Integer> settings;
}

Field settingsField = Config.class.getDeclaredField("settings");
TypeDescriptor settingsDescriptor = new TypeDescriptor(settingsField);

TypeDescriptor keyDescriptor = settingsDescriptor.getMapKeyTypeDescriptor();
Class keyType = keyDescriptor.getType();  // String.class

TypeDescriptor valueDescriptor = settingsDescriptor.getMapValueTypeDescriptor();
Class valueType = valueDescriptor.getType();  // Integer.class
```

### 注解支持

```java
/**
 * 获取所有注解
 */
public Annotation[] getAnnotations() {
    return this.annotations;
}

/**
 * 获取指定类型的注解
 */
public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
    for (Annotation annotation : getAnnotations()) {
        if (annotation.annotationType().equals(annotationType)) {
            return (T) annotation;
        }
    }
    return null;
}

/**
 * 检查是否有指定注解
 */
public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
    return getAnnotation(annotationType) != null;
}

// 使用示例
public class User {
    @NotNull
    @Size(min = 2, max = 50)
    private String name;
    
    @Min(0)
    @Max(150)
    private int age;
}

Field nameField = User.class.getDeclaredField("name");
TypeDescriptor nameDescriptor = new TypeDescriptor(nameField);

// 获取注解
NotNull notNull = nameDescriptor.getAnnotation(NotNull.class);
Size size = nameDescriptor.getAnnotation(Size.class);

// 检查注解
boolean hasNotNull = nameDescriptor.hasAnnotation(NotNull.class);  // true
boolean hasEmail = nameDescriptor.hasAnnotation(Email.class);      // false
```

## 在类型转换中的应用

### Converter 接口

```java
/**
 * 类型转换器接口
 * 使用 TypeDescriptor 获取源类型和目标类型的完整信息
 */
public interface Converter<S, T> {
    T convert(S source);
}

/**
 * 泛型转换器接口
 * 可以处理更复杂的类型转换场景
 */
public interface GenericConverter {
    
    Set<ConvertiblePair> getConvertibleTypes();
    
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
}

/**
 * 可转换类型对
 */
public static final class ConvertiblePair {
    private final Class sourceType;
    private final Class targetType;
    
    public ConvertiblePair(Class sourceType, Class targetType) {
        this.sourceType = sourceType;
        this.targetType = targetType;
    }
}
```

### 转换器实现示例

```java
/**
 * String 到 Collection 的转换器
 */
public class StringToCollectionConverter implements GenericConverter {
    
    private final ConversionService conversionService;
    
    public StringToCollectionConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(
            new ConvertiblePair(String.class, Collection.class)
        );
    }
    
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        
        String string = (String) source;
        String[] elements = StringUtils.commaDelimitedListToStringArray(string);
        
        // 获取集合元素类型
        TypeDescriptor elementType = targetType.getElementTypeDescriptor();
        
        // 创建目标集合
        Collection<Object> target = CollectionFactory.createCollection(
            targetType.getType(), elementType != null ? elementType.getType() : null, elements.length
        );
        
        // 转换每个元素
        for (String element : elements) {
            Object targetElement = conversionService.convert(
                element.trim(), 
                TypeDescriptor.valueOf(String.class), 
                elementType
            );
            target.add(targetElement);
        }
        
        return target;
    }
}

/**
 * Map 到 Map 的转换器
 */
public class MapToMapConverter implements GenericConverter {
    
    private final ConversionService conversionService;
    
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Map sourceMap = (Map) source;
        Map targetMap = CollectionFactory.createMap(
            targetType.getType(), sourceMap.size()
        );
        
        // 获取 key/value 类型描述符
        TypeDescriptor targetKeyType = targetType.getMapKeyTypeDescriptor();
        TypeDescriptor targetValueType = targetType.getMapValueTypeDescriptor();
        
        for (Map.Entry entry : (Set<Map.Entry>) sourceMap.entrySet()) {
            Object targetKey = conversionService.convert(
                entry.getKey(),
                sourceType.getMapKeyTypeDescriptor(),
                targetKeyType
            );
            Object targetValue = conversionService.convert(
                entry.getValue(),
                sourceType.getMapValueTypeDescriptor(),
                targetValueType
            );
            targetMap.put(targetKey, targetValue);
        }
        
        return targetMap;
    }
}
```

## 在属性访问中的应用

### Property 类

```java
/**
 * 属性描述
 * 封装字段或 getter/setter 方法对
 */
public class Property {
    
    private final Class objectType;      // 对象类型
    private final String name;           // 属性名
    private final Method readMethod;     // getter 方法
    private final Method writeMethod;    // setter 方法
    
    public Property(Class objectType, String name, 
                    Method readMethod, Method writeMethod) {
        this.objectType = objectType;
        this.name = name;
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }
    
    public Class getType() {
        return readMethod != null ? readMethod.getReturnType() 
                                  : writeMethod.getParameterTypes()[0];
    }
    
    public Type getGenericType() {
        return readMethod != null ? readMethod.getGenericReturnType()
                                  : writeMethod.getGenericParameterTypes()[0];
    }
}
```

### BeanWrapper 使用

```java
/**
 * BeanWrapper 使用 TypeDescriptor 访问属性
 */
public class BeanWrapperExample {
    
    public void demonstrate() {
        User user = new User();
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(user);
        
        // 设置属性值
        wrapper.setPropertyValue("name", "John");
        wrapper.setPropertyValue("age", 25);
        
        // 获取属性值
        String name = (String) wrapper.getPropertyValue("name");
        
        // 获取属性描述符
        PropertyDescriptor nameDescriptor = wrapper.getPropertyDescriptor("name");
        TypeDescriptor typeDescriptor = wrapper.getPropertyTypeDescriptor("name");
        
        // 检查属性类型
        boolean isString = typeDescriptor.isAssignableTo(TypeDescriptor.valueOf(String.class));
    }
}
```

## 在数据绑定中的应用

### WebDataBinder

```java
/**
 * WebDataBinder 使用 TypeDescriptor 进行数据绑定
 */
public class WebDataBinderExample {
    
    public void bindData(HttpServletRequest request, User user) {
        WebDataBinder binder = new WebDataBinder(user);
        
        // 注册自定义转换器
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
            new SimpleDateFormat("yyyy-MM-dd"), true
        ));
        
        // 绑定请求参数到对象
        MutablePropertyValues pvs = new MutablePropertyValues(request.getParameterMap());
        binder.bind(pvs);
        
        // 在绑定过程中，TypeDescriptor 用于：
        // 1. 确定目标类型
        // 2. 查找合适的转换器
        // 3. 处理集合和 Map 的元素类型
    }
}
```

## 总结

TypeDescriptor 是 Spring 类型系统的完整描述符：

1. **类型信息**：基于 ResolvableType 提供泛型支持
2. **注解支持**：可以获取字段/方法上的注解
3. **集合支持**：自动解析集合元素类型
4. **Map 支持**：自动解析 Map 的 key/value 类型
5. **广泛应用**：在类型转换、属性访问、数据绑定等场景都有应用

与 ResolvableType 相比，TypeDescriptor 更适合需要注解信息的场景，是 Spring 类型转换和数据绑定的基础设施。
