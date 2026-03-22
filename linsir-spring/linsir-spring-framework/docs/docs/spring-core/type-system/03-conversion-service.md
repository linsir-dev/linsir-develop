# ConversionService 深度分析

## 概述

`ConversionService` 是 Spring Framework 的类型转换系统，它提供了一套统一、可扩展的类型转换机制，用于在不同类型之间进行转换。

## 核心接口

### ConversionService

```java
/**
 * 类型转换服务接口
 * 提供类型转换的核心能力
 */
public interface ConversionService {
    
    /**
     * 判断是否可以从 sourceType 转换到 targetType
     */
    boolean canConvert(Class sourceType, Class targetType);
    
    /**
     * 判断是否可以从 sourceType 转换到 targetType（使用 TypeDescriptor）
     */
    boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);
    
    /**
     * 执行类型转换
     */
    <T> T convert(Object source, Class<T> targetType);
    
    /**
     * 执行类型转换（使用 TypeDescriptor）
     */
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
}
```

### Converter

```java
/**
 * 简单类型转换器接口
 * 用于一对一的类型转换
 */
@FunctionalInterface
public interface Converter<S, T> {
    
    /**
     * 将源类型转换为目标类型
     */
    T convert(S source);
}

// 示例：String 到 Integer 的转换器
public class StringToIntegerConverter implements Converter<String, Integer> {
    
    @Override
    public Integer convert(String source) {
        return Integer.valueOf(source);
    }
}

// 示例：String 到 Date 的转换器
public class StringToDateConverter implements Converter<String, Date> {
    
    private final DateFormat dateFormat;
    
    public StringToDateConverter(String pattern) {
        this.dateFormat = new SimpleDateFormat(pattern);
    }
    
    @Override
    public Date convert(String source) {
        try {
            return dateFormat.parse(source);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + source, e);
        }
    }
}
```

### ConverterFactory

```java
/**
 * 转换器工厂接口
 * 用于创建针对特定目标类型的转换器
 */
public interface ConverterFactory<S, R> {
    
    /**
     * 获取转换器
     * @param targetType 目标类型
     * @return 转换器
     */
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}

// 示例：String 到 Number 的转换器工厂
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {
    
    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumberConverter<>(targetType);
    }
    
    private static final class StringToNumberConverter<T extends Number> 
            implements Converter<String, T> {
        
        private final Class<T> targetType;
        
        public StringToNumberConverter(Class<T> targetType) {
            this.targetType = targetType;
        }
        
        @Override
        public T convert(String source) {
            if (targetType.equals(Integer.class)) {
                return (T) Integer.valueOf(source);
            } else if (targetType.equals(Long.class)) {
                return (T) Long.valueOf(source);
            } else if (targetType.equals(Double.class)) {
                return (T) Double.valueOf(source);
            }
            // ... 其他数字类型
            throw new IllegalArgumentException("Unsupported number type: " + targetType);
        }
    }
}
```

### GenericConverter

```java
/**
 * 通用转换器接口
 * 用于复杂的类型转换场景，支持泛型类型
 */
public interface GenericConverter {
    
    /**
     * 获取支持的转换类型对
     */
    Set<ConvertiblePair> getConvertibleTypes();
    
    /**
     * 执行转换
     * @param source 源对象
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 转换后的对象
     */
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
    
    /**
     * 可转换类型对
     */
    final class ConvertiblePair {
        private final Class sourceType;
        private final Class targetType;
        
        public ConvertiblePair(Class sourceType, Class targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }
        
        public Class getSourceType() { return sourceType; }
        public Class getTargetType() { return targetType; }
    }
}

// 示例：数组到集合的转换器
public class ArrayToCollectionConverter implements GenericConverter {
    
    private final ConversionService conversionService;
    
    public ArrayToCollectionConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }
    
    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(
            new ConvertiblePair(Object[].class, Collection.class)
        );
    }
    
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }
        
        Object[] array = (Object[]) source;
        TypeDescriptor elementType = targetType.getElementTypeDescriptor();
        
        Collection<Object> target = CollectionFactory.createCollection(
            targetType.getType(), 
            elementType != null ? elementType.getType() : null, 
            array.length
        );
        
        for (Object element : array) {
            Object targetElement = conversionService.convert(
                element, 
                sourceType.getElementTypeDescriptor(), 
                elementType
            );
            target.add(targetElement);
        }
        
        return target;
    }
}
```

## 核心实现

### GenericConversionService

```java
/**
 * ConversionService 的通用实现
 * 管理所有转换器并提供转换服务
 */
public class GenericConversionService implements ConfigurableConversionService {
    
    // 转换器注册表
    private final Converters converters = new Converters();
    
    // 转换器缓存
    private final Map<ConverterCacheKey, GenericConverter> converterCache = 
        new ConcurrentHashMap<>();
    
    // ========== 注册转换器 ==========
    
    @Override
    public void addConverter(Converter<?, ?> converter) {
        ResolvableType[] typeInfo = getRequiredTypeInfo(converter.getClass(), Converter.class);
        if (typeInfo == null) {
            throw new IllegalArgumentException("Unable to determine source type and target type");
        }
        addConverter(new ConverterAdapter(converter, typeInfo[0], typeInfo[1]));
    }
    
    @Override
    public void addConverterFactory(ConverterFactory<?, ?> factory) {
        ResolvableType[] typeInfo = getRequiredTypeInfo(factory.getClass(), ConverterFactory.class);
        if (typeInfo == null) {
            throw new IllegalArgumentException("Unable to determine source type and target type");
        }
        addConverter(new ConverterFactoryAdapter(factory, 
            typeInfo[0].resolve(Object.class), 
            typeInfo[1].resolve(Object.class)));
    }
    
    @Override
    public void addConverter(GenericConverter converter) {
        this.converters.add(converter);
        invalidateCache();
    }
    
    // ========== 执行转换 ==========
    
    @Override
    public <T> T convert(Object source, Class<T> targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        return (T) convert(source, TypeDescriptor.forObject(source), 
            TypeDescriptor.valueOf(targetType));
    }
    
    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Assert.notNull(targetType, "Target type to convert to cannot be null");
        
        if (source == null) {
            return null;
        }
        
        // 查找转换器
        GenericConverter converter = getConverter(sourceType, targetType);
        if (converter == null) {
            throw new ConverterNotFoundException(sourceType, targetType);
        }
        
        // 执行转换
        return converter.convert(source, sourceType, targetType);
    }
    
    // ========== 查找转换器 ==========
    
    protected GenericConverter getConverter(TypeDescriptor sourceType, TypeDescriptor targetType) {
        ConverterCacheKey key = new ConverterCacheKey(sourceType, targetType);
        
        // 先查缓存
        GenericConverter converter = this.converterCache.get(key);
        if (converter != null) {
            return converter;
        }
        
        // 查找转换器
        converter = findConverterForPair(sourceType, targetType);
        
        // 放入缓存
        if (converter != null) {
            this.converterCache.put(key, converter);
        }
        
        return converter;
    }
    
    private GenericConverter findConverterForPair(TypeDescriptor sourceType, TypeDescriptor targetType) {
        // 直接查找
        ConvertiblePair pair = new ConvertiblePair(sourceType.getType(), targetType.getType());
        GenericConverter converter = this.converters.get(pair);
        if (converter != null) {
            return converter;
        }
        
        // 尝试父类转换器
        return findConverterForSuperTypes(sourceType, targetType);
    }
    
    private GenericConverter findConverterForSuperTypes(TypeDescriptor sourceType, TypeDescriptor targetType) {
        // 遍历源类型的父类和接口
        for (Class superType : getSuperTypes(sourceType.getType())) {
            ConvertiblePair pair = new ConvertiblePair(superType, targetType.getType());
            GenericConverter converter = this.converters.get(pair);
            if (converter != null) {
                return converter;
            }
        }
        return null;
    }
}
```

### 内置转换器

Spring 提供了丰富的内置转换器：

```java
// 基本类型转换
StringToIntegerConverter      // String -> Integer
StringToLongConverter         // String -> Long
StringToDoubleConverter       // String -> Double
StringToBooleanConverter      // String -> Boolean
StringToCharacterConverter    // String -> Character
StringToEnumConverterFactory  // String -> Enum
NumberToNumberConverterFactory // Number -> Number

// 集合转换
ArrayToCollectionConverter    // Array -> Collection
CollectionToArrayConverter    // Collection -> Array
ArrayToArrayConverter         // Array -> Array
CollectionToCollectionConverter // Collection -> Collection
MapToMapConverter             // Map -> Map

// 对象转换
ObjectToStringConverter       // Object -> String
ObjectToObjectConverter       // Object -> Object
IdToEntityConverter           // Id -> Entity

// Spring 类型转换
StringToResourceConverter     // String -> Resource
StringToPropertiesConverter   // String -> Properties
PropertiesToStringConverter   // Properties -> String
```

## 配置与使用

### 编程式配置

```java
/**
 * 编程式创建 ConversionService
 */
public class ConversionServiceExample {
    
    public void configure() {
        // 创建 ConversionService
        DefaultConversionService conversionService = new DefaultConversionService();
        
        // 添加内置转换器
        DefaultConversionService.addDefaultConverters(conversionService);
        
        // 注册自定义转换器
        conversionService.addConverter(new StringToDateConverter("yyyy-MM-dd"));
        conversionService.addConverter(new UserDtoToUserConverter());
        conversionService.addConverterFactory(new StringToNumberConverterFactory());
        
        // 使用 ConversionService
        Integer number = conversionService.convert("123", Integer.class);
        Date date = conversionService.convert("2024-01-01", Date.class);
        List<String> list = conversionService.convert(new String[]{"a", "b", "c"}, List.class);
    }
}
```

### Spring 配置

```java
/**
 * Spring 配置类
 */
@Configuration
public class ConversionConfig {
    
    @Bean
    public ConversionService conversionService() {
        DefaultConversionService service = new DefaultConversionService();
        
        // 添加默认转换器
        DefaultConversionService.addDefaultConverters(service);
        
        // 添加自定义转换器
        service.addConverter(new StringToDateConverter("yyyy-MM-dd"));
        service.addConverter(new UserDtoToUserConverter());
        
        return service;
    }
}
```

### Formatter 集成

```java
/**
 * Formatter 是 ConversionService 的扩展
 * 支持本地化格式化和解析
 */
public interface Formatter<T> extends Printer<T>, Parser<T> {
}

public interface Printer<T> {
    String print(T object, Locale locale);
}

public interface Parser<T> {
    T parse(String text, Locale locale) throws ParseException;
}

// 示例：日期格式化器
public class DateFormatter implements Formatter<Date> {
    
    private String pattern;
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    @Override
    public String print(Date date, Locale locale) {
        return new SimpleDateFormat(pattern, locale).format(date);
    }
    
    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        return new SimpleDateFormat(pattern, locale).parse(text);
    }
}

// 配置 Formatter
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateFormatter dateFormatter = new DateFormatter();
        dateFormatter.setPattern("yyyy-MM-dd");
        registry.addFormatter(dateFormatter);
    }
}
```

## 实际应用场景

### 1. Spring MVC 数据绑定

```java
@Controller
public class UserController {
    
    @PostMapping("/users")
    public String createUser(@RequestBody UserDto userDto) {
        // Spring 自动使用 ConversionService 将 UserDto 转换为 User
        // 需要配置相应的 Converter
        User user = conversionService.convert(userDto, User.class);
        userService.save(user);
        return "redirect:/users";
    }
}

// DTO 转换器
public class UserDtoToUserConverter implements Converter<UserDto, User> {
    
    @Override
    public User convert(UserDto source) {
        User user = new User();
        user.setName(source.getName());
        user.setEmail(source.getEmail());
        // ... 其他字段映射
        return user;
    }
}
```

### 2. 配置文件属性绑定

```java
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    // 自动转换配置值
    private Date startDate;        // "2024-01-01" -> Date
    private Duration timeout;      // "30s" -> Duration
    private DataSize maxFileSize;  // "10MB" -> DataSize
    private List<String> hosts;    // 逗号分隔字符串 -> List
    
    // getters and setters
}
```

### 3. SpEL 表达式

```java
@Service
public class ExpressionService {
    
    @Autowired
    private ConversionService conversionService;
    
    public void evaluate() {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setTypeConverter(new StandardTypeConverter(conversionService));
        
        // SpEL 会自动使用 ConversionService 进行类型转换
        Expression expression = parser.parseExpression("'123' + 456");
        Integer result = expression.getValue(context, Integer.class);  // 579
    }
}
```

## 总结

ConversionService 是 Spring Framework 的类型转换基础设施：

1. **统一接口**：提供一致的类型转换 API
2. **可扩展**：支持自定义 Converter、ConverterFactory、GenericConverter
3. **缓存优化**：转换器查找结果被缓存以提高性能
4. **泛型支持**：通过 GenericConverter 支持复杂泛型类型转换
5. **广泛应用**：在数据绑定、SpEL、Spring MVC 等场景都有应用

类型转换系统的层次结构：

```
ConversionService (接口)
├── GenericConversionService (实现)
│   ├── Converters (转换器注册表)
│   ├── ConverterCache (转换器缓存)
│   └── 转换逻辑
├── Converter (简单转换器)
├── ConverterFactory (转换器工厂)
└── GenericConverter (通用转换器)
```
