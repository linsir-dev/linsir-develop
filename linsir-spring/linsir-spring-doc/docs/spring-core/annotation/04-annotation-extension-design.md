# 注解处理模块扩展设计

## 1. 扩展设计概述

本文档描述注解处理模块的扩展设计，包括如何基于现有架构进行功能扩展、自定义注解处理器开发、以及与 Spring IoC 容器的集成方案。

## 2. 扩展架构

### 2.1 扩展点设计

```
┌─────────────────────────────────────────────────────────────┐
│                    注解处理扩展架构                           │
├─────────────────────────────────────────────────────────────┤
│  应用层                                                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ 自定义注解    │  │ 组合注解      │  │ 注解驱动组件  │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
├─────────────────────────────────────────────────────────────┤
│  扩展层                                                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ 注解处理器    │  │ 属性转换器    │  │ 条件评估器    │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
├─────────────────────────────────────────────────────────────┤
│  核心层（现有）                                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ AnnotationUtils│ │MergedAnnotations│ │AnnotationAttributes│ │
│  └──────────────┘  └──────────────┘  └──────────────┘       │
└─────────────────────────────────────────────────────────────┘
```

## 3. 自定义注解处理器

### 3.1 处理器接口设计

```java
/**
 * 注解处理器接口
 * 
 * 用于处理特定类型的注解，实现自定义的注解处理逻辑。
 *
 * @param <A> 处理的注解类型
 * @author linsir
 * @since 1.0.0
 */
public interface AnnotationProcessor<A extends Annotation> {
    
    /**
     * 获取处理的注解类型
     *
     * @return 注解类型
     */
    Class<A> getAnnotationType();
    
    /**
     * 处理注解
     *
     * @param element 注解元素
     * @param annotation 注解实例
     * @param context 处理上下文
     * @return 处理结果
     */
    AnnotationProcessResult process(AnnotatedElement element, A annotation, 
                                    AnnotationProcessContext context);
    
    /**
     * 判断是否支持处理该元素
     *
     * @param element 注解元素
     * @return true 如果支持
     */
    default boolean supports(AnnotatedElement element) {
        return true;
    }
}

/**
 * 注解处理结果
 */
public class AnnotationProcessResult {
    private final boolean success;
    private final Object result;
    private final String message;
    
    // 构造方法、getter...
}

/**
 * 注解处理上下文
 */
public class AnnotationProcessContext {
    private final Map<String, Object> attributes = new HashMap<>();
    
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }
}
```

### 3.2 处理器注册与管理

```java
/**
 * 注解处理器注册表
 * 
 * 管理所有注解处理器，提供处理器的注册和查找功能。
 *
 * @author linsir
 * @since 1.0.0
 */
public class AnnotationProcessorRegistry {
    
    private final Map<Class<? extends Annotation>, AnnotationProcessor<?>> processors = 
        new ConcurrentHashMap<>();
    
    /**
     * 注册处理器
     *
     * @param processor 处理器实例
     */
    public <A extends Annotation> void register(AnnotationProcessor<A> processor) {
        processors.put(processor.getAnnotationType(), processor);
    }
    
    /**
     * 获取处理器
     *
     * @param annotationType 注解类型
     * @return 处理器实例
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> AnnotationProcessor<A> getProcessor(Class<A> annotationType) {
        return (AnnotationProcessor<A>) processors.get(annotationType);
    }
    
    /**
     * 处理注解
     *
     * @param element 注解元素
     * @param annotation 注解实例
     * @return 处理结果
     */
    public <A extends Annotation> AnnotationProcessResult process(AnnotatedElement element, 
                                                                   A annotation) {
        AnnotationProcessor<A> processor = getProcessor((Class<A>) annotation.annotationType());
        if (processor != null && processor.supports(element)) {
            return processor.process(element, annotation, new AnnotationProcessContext());
        }
        return AnnotationProcessResult.skipped();
    }
}
```

### 3.3 示例：事务注解处理器

```java
/**
 * 事务注解处理器
 * 
 * 处理 @Transactional 注解，创建事务代理。
 *
 * @author linsir
 * @since 1.0.0
 */
public class TransactionalAnnotationProcessor implements AnnotationProcessor<Transactional> {
    
    private final TransactionManager transactionManager;
    
    public TransactionalAnnotationProcessor(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    @Override
    public Class<Transactional> getAnnotationType() {
        return Transactional.class;
    }
    
    @Override
    public AnnotationProcessResult process(AnnotatedElement element, Transactional annotation,
                                           AnnotationProcessContext context) {
        if (element instanceof Method) {
            Method method = (Method) element;
            
            // 创建事务属性
            TransactionAttribute attribute = createTransactionAttribute(annotation);
            
            // 注册事务拦截器
            registerTransactionInterceptor(method, attribute);
            
            return AnnotationProcessResult.success("事务配置已应用");
        }
        return AnnotationProcessResult.skipped();
    }
    
    private TransactionAttribute createTransactionAttribute(Transactional annotation) {
        return new TransactionAttribute(
            annotation.propagation(),
            annotation.isolation(),
            annotation.timeout(),
            annotation.readOnly()
        );
    }
    
    private void registerTransactionInterceptor(Method method, TransactionAttribute attribute) {
        // 注册事务拦截器逻辑
    }
}
```

## 4. 属性转换器扩展

### 4.1 转换器接口

```java
/**
 * 注解属性转换器
 * 
 * 用于将注解属性值转换为特定类型。
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 * @author linsir
 * @since 1.0.0
 */
public interface AnnotationAttributeConverter<S, T> {
    
    /**
     * 转换属性值
     *
     * @param source 源值
     * @param targetType 目标类型
     * @return 转换后的值
     */
    T convert(S source, Class<T> targetType);
    
    /**
     * 判断是否支持该转换
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return true 如果支持
     */
    boolean supports(Class<?> sourceType, Class<?> targetType);
}

/**
 * 字符串到枚举的转换器
 */
public class StringToEnumConverter implements AnnotationAttributeConverter<String, Enum<?>> {
    
    @Override
    @SuppressWarnings("unchecked")
    public Enum<?> convert(String source, Class<Enum<?>> targetType) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        return Enum.valueOf((Class<Enum>) targetType, source);
    }
    
    @Override
    public boolean supports(Class<?> sourceType, Class<?> targetType) {
        return String.class.equals(sourceType) && targetType.isEnum();
    }
}

/**
 * SpEL 表达式转换器
 */
public class SpelExpressionConverter implements AnnotationAttributeConverter<String, Object> {
    
    private final ExpressionParser parser = new SpelExpressionParser();
    
    @Override
    public Object convert(String source, Class<Object> targetType) {
        if (source == null || !source.startsWith("#{")) {
            return source;
        }
        
        Expression expression = parser.parseExpression(source);
        return expression.getValue(targetType);
    }
    
    @Override
    public boolean supports(Class<?> sourceType, Class<?> targetType) {
        return String.class.equals(sourceType);
    }
}
```

### 4.2 转换器注册表

```java
/**
 * 属性转换器注册表
 *
 * @author linsir
 * @since 1.0.0
 */
public class AnnotationAttributeConverterRegistry {
    
    private final List<AnnotationAttributeConverter<?, ?>> converters = new ArrayList<>();
    
    /**
     * 注册转换器
     *
     * @param converter 转换器
     */
    public void register(AnnotationAttributeConverter<?, ?> converter) {
        converters.add(converter);
    }
    
    /**
     * 转换属性值
     *
     * @param source 源值
     * @param targetType 目标类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    public <S, T> T convert(S source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        
        Class<S> sourceType = (Class<S>) source.getClass();
        
        for (AnnotationAttributeConverter<?, ?> converter : converters) {
            if (converter.supports(sourceType, targetType)) {
                return ((AnnotationAttributeConverter<S, T>) converter).convert(source, targetType);
            }
        }
        
        // 默认转换
        return defaultConvert(source, targetType);
    }
    
    @SuppressWarnings("unchecked")
    private <S, T> T defaultConvert(S source, Class<T> targetType) {
        if (targetType.isInstance(source)) {
            return (T) source;
        }
        return null;
    }
}
```

## 5. 条件评估器扩展

### 5.1 条件注解设计

```java
/**
 * 条件注解
 * 
 * 用于根据条件决定是否启用某个组件或配置。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {
    
    /**
     * 条件类
     * 必须实现 Condition 接口
     *
     * @return 条件类数组
     */
    Class<? extends Condition>[] value();
}

/**
 * 条件接口
 */
public interface Condition {
    
    /**
     * 评估条件
     *
     * @param context 条件上下文
     * @return true 如果条件匹配
     */
    boolean matches(ConditionContext context);
}

/**
 * 条件上下文
 */
public class ConditionContext {
    
    private final Environment environment;
    private final ClassLoader classLoader;
    private final Map<String, Object> metadata = new HashMap<>();
    
    public ConditionContext(Environment environment, ClassLoader classLoader) {
        this.environment = environment;
        this.classLoader = classLoader;
    }
    
    public Environment getEnvironment() {
        return environment;
    }
    
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getMetadata(String key) {
        return (T) metadata.get(key);
    }
}
```

### 5.2 示例条件实现

```java
/**
 * 基于属性的条件
 */
public class OnPropertyCondition implements Condition {
    
    @Override
    public boolean matches(ConditionContext context) {
        // 实现属性条件判断
        return true;
    }
}

/**
 * 基于类的条件
 */
public class OnClassCondition implements Condition {
    
    private final String className;
    
    public OnClassCondition(String className) {
        this.className = className;
    }
    
    @Override
    public boolean matches(ConditionContext context) {
        try {
            context.getClassLoader().loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

/**
 * 基于 Profile 的条件
 */
public class OnProfileCondition implements Condition {
    
    private final String profile;
    
    public OnProfileCondition(String profile) {
        this.profile = profile;
    }
    
    @Override
    public boolean matches(ConditionContext context) {
        Environment env = context.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();
        return Arrays.asList(activeProfiles).contains(profile);
    }
}
```

## 6. 与 IoC 容器集成

### 6.1 注解驱动的 Bean 定义

```java
/**
 * 注解 Bean 定义扫描器
 * 
 * 扫描类路径下的注解，创建 Bean 定义。
 *
 * @author linsir
 * @since 1.0.0
 */
public class AnnotationBeanDefinitionScanner {
    
    private final BeanDefinitionRegistry registry;
    private final AnnotationProcessorRegistry processorRegistry;
    
    public AnnotationBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.processorRegistry = new AnnotationProcessorRegistry();
        registerDefaultProcessors();
    }
    
    private void registerDefaultProcessors() {
        processorRegistry.register(new ComponentAnnotationProcessor());
        processorRegistry.register(new ServiceAnnotationProcessor());
        processorRegistry.register(new RepositoryAnnotationProcessor());
    }
    
    /**
     * 扫描包
     *
     * @param basePackages 基础包路径
     */
    public void scan(String... basePackages) {
        for (String basePackage : basePackages) {
            scanPackage(basePackage);
        }
    }
    
    private void scanPackage(String basePackage) {
        // 扫描包下的所有类
        Set<Class<?>> candidates = findCandidateClasses(basePackage);
        
        for (Class<?> candidate : candidates) {
            processCandidate(candidate);
        }
    }
    
    private void processCandidate(Class<?> candidate) {
        // 检查类上的注解
        MergedAnnotations annotations = MergedAnnotations.from(candidate);
        
        // 查找 @Component 元注解
        if (annotations.isPresent(Component.class)) {
            MergedAnnotation<Component> component = annotations.getRequired(Component.class);
            String beanName = determineBeanName(candidate, component);
            
            // 创建 Bean 定义
            BeanDefinition beanDefinition = createBeanDefinition(candidate, annotations);
            registry.registerBeanDefinition(beanName, beanDefinition);
        }
    }
    
    private String determineBeanName(Class<?> clazz, MergedAnnotation<Component> component) {
        String value = component.getString("value");
        if (StringUtils.hasText(value)) {
            return value;
        }
        // 使用类名首字母小写
        return StringUtils.uncapitalize(clazz.getSimpleName());
    }
    
    private BeanDefinition createBeanDefinition(Class<?> clazz, MergedAnnotations annotations) {
        BeanDefinition definition = new BeanDefinition();
        definition.setBeanClass(clazz);
        
        // 处理作用域
        if (annotations.isPresent(Scope.class)) {
            MergedAnnotation<Scope> scope = annotations.getRequired(Scope.class);
            definition.setScope(scope.getString("value"));
        }
        
        // 处理懒加载
        if (annotations.isPresent(Lazy.class)) {
            definition.setLazyInit(true);
        }
        
        // 处理依赖注入
        processDependencies(clazz, definition);
        
        return definition;
    }
    
    private void processDependencies(Class<?> clazz, BeanDefinition definition) {
        // 处理字段注入
        for (Field field : clazz.getDeclaredFields()) {
            if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
                Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
                String dependencyName = qualifier != null ? qualifier.value() : field.getName();
                definition.addPropertyReference(field.getName(), dependencyName);
            }
            
            Value value = AnnotationUtils.getAnnotation(field, Value.class);
            if (value != null) {
                definition.addPropertyValue(field.getName(), value.value());
            }
        }
        
        // 处理方法注入
        for (Method method : clazz.getDeclaredMethods()) {
            if (AnnotationUtils.getAnnotation(method, Autowired.class) != null) {
                // 处理 setter 注入
            }
        }
    }
}
```

### 6.2 依赖注入处理

```java
/**
 * 依赖注入处理器
 *
 * @author linsir
 * @since 1.0.0
 */
public class AutowiredAnnotationProcessor implements AnnotationProcessor<Autowired> {
    
    private final BeanFactory beanFactory;
    
    public AutowiredAnnotationProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    @Override
    public Class<Autowired> getAnnotationType() {
        return Autowired.class;
    }
    
    @Override
    public AnnotationProcessResult process(AnnotatedElement element, Autowired annotation,
                                           AnnotationProcessContext context) {
        if (element instanceof Field) {
            processField((Field) element, annotation, context);
        } else if (element instanceof Method) {
            processMethod((Method) element, annotation, context);
        }
        return AnnotationProcessResult.success();
    }
    
    private void processField(Field field, Autowired annotation, AnnotationProcessContext context) {
        Object target = context.getAttribute("target");
        
        // 获取限定符
        Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
        String beanName = qualifier != null ? qualifier.value() : null;
        
        // 获取依赖
        Object dependency;
        if (beanName != null) {
            dependency = beanFactory.getBean(beanName, field.getType());
        } else {
            dependency = beanFactory.getBean(field.getType());
        }
        
        // 设置字段值
        try {
            field.setAccessible(true);
            field.set(target, dependency);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法注入字段: " + field.getName(), e);
        }
    }
    
    private void processMethod(Method method, Autowired annotation, AnnotationProcessContext context) {
        // 处理方法注入
    }
}
```

## 7. 性能优化扩展

### 7.1 注解缓存

```java
/**
 * 注解缓存管理器
 * 
 * 缓存注解解析结果，提高重复访问性能。
 *
 * @author linsir
 * @since 1.0.0
 */
public class AnnotationCacheManager {
    
    private final Map<AnnotatedElement, MergedAnnotations> mergedAnnotationsCache = 
        new ConcurrentHashMap<>();
    
    private final Map<Annotation, AnnotationAttributes> attributesCache = 
        new ConcurrentHashMap<>();
    
    /**
     * 获取缓存的 MergedAnnotations
     *
     * @param element 注解元素
     * @return MergedAnnotations
     */
    public MergedAnnotations getMergedAnnotations(AnnotatedElement element) {
        return mergedAnnotationsCache.computeIfAbsent(element, 
            e -> MergedAnnotations.from(e));
    }
    
    /**
     * 获取缓存的 AnnotationAttributes
     *
     * @param annotation 注解实例
     * @return AnnotationAttributes
     */
    public AnnotationAttributes getAnnotationAttributes(Annotation annotation) {
        return attributesCache.computeIfAbsent(annotation,
            a -> AnnotationUtils.getAnnotationAttributes(a));
    }
    
    /**
     * 清空缓存
     */
    public void clear() {
        mergedAnnotationsCache.clear();
        attributesCache.clear();
    }
}
```

### 7.2 异步注解处理

```java
/**
 * 异步注解处理器
 * 
 * 支持异步处理注解，提高处理效率。
 *
 * @author linsir
 * @since 1.0.0
 */
public class AsyncAnnotationProcessor {
    
    private final ExecutorService executorService;
    
    public AsyncAnnotationProcessor(int threadPoolSize) {
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }
    
    /**
     * 异步处理注解
     *
     * @param elements 注解元素列表
     * @param processor 处理器
     * @return CompletableFuture 列表
     */
    public <A extends Annotation> List<CompletableFuture<AnnotationProcessResult>> 
            processAsync(List<AnnotatedElement> elements, AnnotationProcessor<A> processor) {
        
        List<CompletableFuture<AnnotationProcessResult>> futures = new ArrayList<>();
        
        for (AnnotatedElement element : elements) {
            A annotation = AnnotationUtils.getAnnotation(element, processor.getAnnotationType());
            if (annotation != null) {
                CompletableFuture<AnnotationProcessResult> future = 
                    CompletableFuture.supplyAsync(() -> 
                        processor.process(element, annotation, new AnnotationProcessContext()),
                        executorService
                    );
                futures.add(future);
            }
        }
        
        return futures;
    }
}
```

## 8. 扩展使用示例

### 8.1 自定义组合注解

```java
/**
 * 缓存服务组合注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
@Cacheable("default")
@Transactional(readOnly = true)
public @interface CacheService {
    String value() default "";
    String cacheName() default "";
}

// 使用
@CacheService(value = "userCacheService", cacheName = "users")
public class UserCacheService {
    // 自动具有 @Service、@Cacheable、@Transactional 特性
}
```

### 8.2 自定义条件注解

```java
/**
 * 仅在开发环境启用
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnDevelopmentCondition.class)
public @interface ConditionalOnDevelopment {
}

public class OnDevelopmentCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context) {
        String[] profiles = context.getEnvironment().getActiveProfiles();
        return Arrays.asList(profiles).contains("dev");
    }
}

// 使用
@ConditionalOnDevelopment
@Component
public class DevelopmentOnlyComponent {
    // 仅在 dev profile 激活时创建
}
```

## 9. 总结

本文档提供了注解处理模块的完整扩展设计方案，包括：

1. **注解处理器**: 支持自定义注解处理逻辑
2. **属性转换器**: 支持灵活的属性类型转换
3. **条件评估器**: 支持条件化的组件启用
4. **IoC 集成**: 与 Spring IoC 容器的完整集成方案
5. **性能优化**: 缓存和异步处理支持

通过这些扩展点，可以构建强大的注解驱动框架，支持各种复杂的业务场景。
