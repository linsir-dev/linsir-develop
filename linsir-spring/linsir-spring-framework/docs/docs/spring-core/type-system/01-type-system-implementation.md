# Spring 类型系统实现说明文档

## 一、项目概述

本项目基于 Spring Framework 的 `org.springframework.core.type` 包，实现了一套完整的类型系统示例代码，包括泛型解析、类元数据扫描、类型过滤和组件扫描等核心功能。

### 1.1 项目结构

```
linsir-spring-framework/src/main/java/com/linsir/spring/framework/spring_core/type_system/
├── resolvable/                          # 泛型解析模块
│   ├── processor/
│   │   └── TypeResolver.java            # 泛型解析处理器
│   ├── container/
│   │   ├── DataHolder.java              # 泛型数据容器
│   │   └── GenericContainer.java        # 通用泛型容器
│   ├── service/
│   │   ├── BaseService.java             # 服务基类（带泛型）
│   │   ├── UserService.java             # 用户服务实现
│   │   └── OrderService.java            # 订单服务实现
│   └── entity/
│       ├── User.java                    # 用户实体
│       └── Order.java                   # 订单实体
├── metadata/                            # 元数据模块
│   ├── scanner/
│   │   ├── ClassMetadataScanner.java    # 类元数据扫描器
│   │   └── ClassMetadataInfo.java       # 类元数据信息
│   └── model/
│       ├── Component.java               # 组件注解
│       ├── Service.java                 # 服务注解
│       └── Repository.java              # 仓库注解
├── filter/                              # 类型过滤器模块
│   ├── TypeFilter.java                  # 过滤器接口
│   ├── AnnotationTypeFilter.java        # 注解类型过滤器
│   ├── AssignableTypeFilter.java        # 可分配类型过滤器
│   ├── RegexPatternTypeFilter.java      # 正则模式过滤器
│   └── CompositeTypeFilter.java         # 组合过滤器
└── component/                           # 组件扫描模块
    ├── ComponentScanner.java            # 组件扫描器
    ├── ComponentInfo.java               # 组件信息
    ├── ComponentType.java               # 组件类型枚举
    └── ScanResult.java                  # 扫描结果
```

### 1.2 测试结构

```
linsir-spring-framework/src/test/java/com/linsir/spring/framework/spring_core/type_system/
├── resolvable/
│   └── TypeResolverTest.java            # 泛型解析测试
├── metadata/
│   └── ClassMetadataScannerTest.java    # 元数据扫描测试
├── filter/
│   └── TypeFilterTest.java              # 类型过滤器测试
├── component/
│   └── ComponentScannerTest.java        # 组件扫描测试
└── TypeSystemIntegrationTest.java       # 集成测试
```

## 二、核心模块详解

### 2.1 ResolvableType 泛型解析模块

#### 2.1.1 核心类说明

**TypeResolver.java** - 泛型解析处理器

提供以下核心功能：

| 方法 | 功能说明 | 使用场景 |
|------|----------|----------|
| `resolveClassGenerics(Class, Class)` | 解析类的泛型参数 | 获取继承链中的泛型类型 |
| `resolveFieldGeneric(Field)` | 解析字段的泛型类型 | 获取字段声明的泛型 |
| `resolveMethodReturnGeneric(Method)` | 解析方法返回类型的泛型 | 获取方法返回的泛型 |
| `resolveMethodParameterGeneric(Method, int)` | 解析方法参数的泛型 | 获取方法参数的泛型 |
| `isAssignable(Class, Class)` | 检查类型可分配性 | 类型兼容性检查 |
| `getArrayComponentType(Class)` | 获取数组组件类型 | 数组类型处理 |

**示例代码：**

```java
// 解析 UserService 继承 BaseService<User, Long> 的泛型参数
Class<?>[] generics = TypeResolver.resolveClassGenerics(UserService.class, BaseService.class);
// 结果: [User.class, Long.class]

// 解析字段泛型
Field field = DataHolder.class.getDeclaredField("stringList");
ResolvableType fieldType = ResolvableType.forField(field);
Class<?> genericType = fieldType.getGeneric(0).resolve();
// 结果: String.class
```

#### 2.1.2 实体类设计

**BaseService.java** - 带泛型的服务基类
```java
public abstract class BaseService<T, ID> {
    protected Class<T> entityClass;
    protected Class<ID> idClass;
    
    // 通过 ResolvableType 解析泛型参数
    public BaseService() {
        ResolvableType type = ResolvableType.forClass(getClass()).as(BaseService.class);
        this.entityClass = (Class<T>) type.getGeneric(0).resolve();
        this.idClass = (Class<ID>) type.getGeneric(1).resolve();
    }
}
```

**UserService.java** - 具体服务实现
```java
public class UserService extends BaseService<User, Long> {
    // 继承 BaseService 的泛型解析能力
    // entityClass = User.class, idClass = Long.class
}
```

### 2.2 ClassMetadata 元数据扫描模块

#### 2.2.1 核心类说明

**ClassMetadataScanner.java** - 类元数据扫描器

使用 Spring 的 `MetadataReader` 机制，在不加载类的情况下获取类的结构信息。

| 方法 | 功能说明 |
|------|----------|
| `scanPackage(String)` | 扫描指定包下的所有类 |
| `getClassMetadata(String)` | 获取单个类的元数据 |
| `scanByAnnotation(String, Class)` | 按注解筛选类 |

**ClassMetadataInfo.java** - 元数据信息载体

包含以下属性：
- `className` - 完整类名
- `superClassName` - 父类名
- `interfaceNames` - 实现的接口名数组
- `isAbstract` - 是否为抽象类
- `isInterface` - 是否为接口
- `isAnnotation` - 是否为注解
- `isFinal` - 是否为 final 类
- `annotationTypes` - 类上的注解类型集合

#### 2.2.2 注解定义

**Component.java** - 基础组件注解
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}
```

**Service.java** - 服务层注解（组合 @Component）
```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component  // 元注解，表示 Service 也是一种 Component
public @interface Service {
    String value() default "";
    String description() default "";
}
```

### 2.3 TypeFilter 类型过滤器模块

#### 2.3.1 过滤器接口

**TypeFilter.java**
```java
public interface TypeFilter {
    boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory);
}
```

#### 2.3.2 过滤器实现

| 过滤器类 | 功能 | 使用示例 |
|----------|------|----------|
| `AnnotationTypeFilter` | 按注解类型匹配 | 筛选标注 @Service 的类 |
| `AssignableTypeFilter` | 按类型继承关系匹配 | 筛选实现某接口的类 |
| `RegexPatternTypeFilter` | 按类名正则匹配 | 筛选以 "Service" 结尾的类 |
| `CompositeTypeFilter` | 组合多个过滤器 | AND/OR 逻辑组合 |

**AnnotationTypeFilter 示例：**
```java
// 创建过滤器：筛选标注 @Service 的类
AnnotationTypeFilter filter = new AnnotationTypeFilter(Service.class);

// 使用
MetadataReader reader = metadataReaderFactory.getMetadataReader(className);
boolean matches = filter.match(reader, metadataReaderFactory);
```

**CompositeTypeFilter 示例：**
```java
// 创建 AND 组合过滤器
CompositeTypeFilter andFilter = new CompositeTypeFilter(
    CompositeTypeFilter.LogicalOperator.AND
);
andFilter.addFilter(new AnnotationTypeFilter(Component.class));
andFilter.addFilter(new RegexPatternTypeFilter(".*Service$"));

// 只有同时满足两个条件的类才会被匹配
```

### 2.4 ComponentScanner 组件扫描模块

#### 2.4.1 核心类说明

**ComponentScanner.java** - 组件扫描器

整合了元数据扫描和类型过滤功能，实现完整的组件扫描流程。

| 方法 | 功能说明 |
|------|----------|
| `scan(String)` | 扫描指定包下的所有组件 |
| `addIncludeFilter(TypeFilter)` | 添加包含过滤器 |
| `addExcludeFilter(TypeFilter)` | 添加排除过滤器 |

**扫描流程：**
```
1. 将包路径转换为资源路径 (classpath*:com/example/**/*.class)
2. 使用 PathMatchingResourcePatternResolver 获取所有类资源
3. 对每个资源创建 MetadataReader
4. 应用排除过滤器，排除匹配的类
5. 应用包含过滤器，筛选目标类
6. 创建 ComponentInfo 对象，包含类信息和组件类型
7. 返回 ScanResult 结果集
```

**ComponentType.java** - 组件类型枚举
```java
public enum ComponentType {
    COMPONENT,      // 普通组件
    SERVICE,        // 服务层组件
    REPOSITORY,     // 数据访问层组件
    CONTROLLER,     // 控制层组件
    CONFIGURATION,  // 配置类
    UNKNOWN         // 未知类型
}
```

## 三、测试说明

### 3.1 测试统计

| 测试类 | 测试数量 | 说明 |
|--------|----------|------|
| TypeResolverTest | 15 | 泛型解析功能测试 |
| ClassMetadataScannerTest | 9 | 元数据扫描测试 |
| TypeFilterTest | 8 | 类型过滤器测试 |
| ComponentScannerTest | 9 | 组件扫描测试 |
| TypeSystemIntegrationTest | 7 | 集成测试 |
| **总计** | **48** | 全部通过 |

### 3.2 测试分类

#### 3.2.1 单元测试

**TypeResolverTest 主要测试点：**
- 类泛型解析（`testResolveClassGenerics`）
- 字段泛型解析（`testResolveFieldGeneric`）
- 嵌套泛型解析（`testResolveNestedFieldGeneric`）
- Map泛型解析（`testResolveMapFieldGeneric`）
- 复杂Map泛型解析（`testResolveComplexMapFieldGeneric`）
- 方法返回类型泛型解析（`testResolveMethodReturnGeneric`）
- 方法参数泛型解析（`testResolveMethodParameterGeneric`）
- 类型可分配性检查（`testIsAssignable`）
- 数组组件类型获取（`testGetArrayComponentType`）
- 传统反射方式对比（`testResolveGenericsTraditionally`）

**ClassMetadataScannerTest 主要测试点：**
- 包扫描功能（`testScanPackage`）
- 按注解扫描（`testScanByAnnotation`）
- 类元数据属性（`testClassMetadataProperties`）
- 注解元数据（`testAnnotationMetadata`）

**TypeFilterTest 主要测试点：**
- 注解类型过滤器（`testAnnotationTypeFilter`）
- 可分配类型过滤器（`testAssignableTypeFilter`）
- 正则模式过滤器（`testRegexPatternTypeFilter`）
- 组合过滤器（`testCompositeTypeFilterAnd`、`testCompositeTypeFilterOr`）

#### 3.2.2 集成测试

**TypeSystemIntegrationTest 主要测试场景：**
- 扫描到泛型解析的完整流程（`testScanToGenericResolution`）
- 元数据与组件类型判断集成（`testMetadataWithComponentType`）
- 过滤器与扫描器集成（`testFilterWithScanner`）
- 复杂泛型字段解析（`testComplexGenericFieldResolution`）
- 类型系统核心功能（`testTypeSystemCoreFeatures`）

### 3.3 测试执行结果

```
[INFO] Tests run: 48, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

所有测试用例全部通过，验证了类型系统各模块的正确性和集成能力。

## 四、使用示例

### 4.1 泛型解析示例

```java
// 创建服务实例
UserService userService = new UserService();

// 获取解析后的泛型类型
System.out.println("实体类型: " + userService.getEntityClass());  // User.class
System.out.println("ID类型: " + userService.getIdClass());      // Long.class
```

### 4.2 元数据扫描示例

```java
// 创建扫描器
ClassMetadataScanner scanner = new ClassMetadataScanner();

// 扫描包
List<ClassMetadataInfo> metadataList = scanner.scanPackage(
    "com.linsir.spring.framework.spring_core.type_system.metadata.model"
);

// 遍历结果
for (ClassMetadataInfo info : metadataList) {
    System.out.println("类名: " + info.getClassName());
    System.out.println("是注解: " + info.isAnnotation());
    System.out.println("注解类型: " + info.getAnnotationTypes());
}
```

### 4.3 组件扫描示例

```java
// 创建组件扫描器
ComponentScanner scanner = new ComponentScanner();

// 添加包含过滤器：只扫描标注 @Service 的类
scanner.addIncludeFilter(new AnnotationTypeFilter(Service.class));

// 执行扫描
ScanResult result = scanner.scan(
    "com.linsir.spring.framework.spring_core.type_system"
);

// 处理结果
for (ComponentInfo component : result.getComponents()) {
    System.out.println("组件: " + component.getClassName());
    System.out.println("类型: " + component.getComponentType());
}
```

## 五、技术要点总结

### 5.1 ResolvableType 使用要点

1. **创建方式**：通过 `forClass()`、`forField()`、`forMethodParameter()` 等工厂方法创建
2. **泛型解析**：使用 `getGeneric(int)` 获取指定位置的泛型，`resolve()` 解析为 Class
3. **嵌套泛型**：支持多层嵌套，如 `List<List<String>>` 可通过多次 `getGeneric()` 解析
4. **类型转换**：使用 `as(Class)` 将类型视为指定类的子类型，用于解析继承链中的泛型

### 5.2 元数据扫描要点

1. **不加载类**：使用 `MetadataReader` 直接读取类文件字节码，避免类加载开销
2. **缓存机制**：使用 `CachingMetadataReaderFactory` 缓存元数据读取器
3. **资源匹配**：使用 `PathMatchingResourcePatternResolver` 支持 Ant 风格路径匹配

### 5.3 类型过滤器要点

1. **组合逻辑**：使用 `CompositeTypeFilter` 实现 AND/OR 逻辑组合
2. **元注解支持**：`AnnotationTypeFilter` 支持考虑元注解（considerMetaAnnotations）
3. **继承关系**：`AssignableTypeFilter` 支持匹配指定类型的所有子类/实现类

### 5.4 组件扫描要点

1. **过滤器链**：包含过滤器和排除过滤器按顺序应用
2. **组件类型识别**：根据类上的注解自动识别组件类型（Service、Repository 等）
3. **扫描结果**：`ScanResult` 包含扫描到的组件、类名列表和统计信息

## 六、扩展建议

### 6.1 功能扩展（深入设计与封装建议）

#### 6.1.1 支持更多注解的封装设计

**问题分析**：
当前 `ComponentScanner.createComponentInfo()` 方法使用硬编码的 if-else 链判断组件类型，新增注解需要修改源码，违反开闭原则。

**封装扩展方案 - 组件类型解析器链**：

```java
/**
 * 组件类型解析器接口
 * 策略模式：每个解析器负责一种组件类型的识别
 */
public interface ComponentTypeResolver {
    /**
     * 判断是否支持该元数据
     */
    boolean supports(MetadataReader metadataReader);
    
    /**
     * 解析组件类型
     */
    ComponentType resolve(MetadataReader metadataReader);
    
    /**
     * 解析器优先级，数字越小优先级越高
     */
    int getOrder();
}

/**
 * 注解组件类型解析器
 * 基于注解识别组件类型
 */
public class AnnotationComponentTypeResolver implements ComponentTypeResolver {
    private final String annotationName;
    private final ComponentType componentType;
    private final int order;
    
    public AnnotationComponentTypeResolver(String annotationName, ComponentType componentType, int order) {
        this.annotationName = annotationName;
        this.componentType = componentType;
        this.order = order;
    }
    
    @Override
    public boolean supports(MetadataReader metadataReader) {
        return metadataReader.getAnnotationMetadata().hasAnnotation(annotationName);
    }
    
    @Override
    public ComponentType resolve(MetadataReader metadataReader) {
        return componentType;
    }
    
    @Override
    public int getOrder() {
        return order;
    }
}

/**
 * 组件类型解析器注册表
 * 管理所有解析器，支持动态注册
 */
public class ComponentTypeResolverRegistry {
    private final List<ComponentTypeResolver> resolvers = new ArrayList<>();
    
    public ComponentTypeResolverRegistry() {
        // 注册默认解析器
        registerDefaultResolvers();
    }
    
    private void registerDefaultResolvers() {
        // 使用 Spring 标准注解
        register(new AnnotationComponentTypeResolver(
            "org.springframework.stereotype.Controller", 
            ComponentType.CONTROLLER, 100));
        register(new AnnotationComponentTypeResolver(
            "org.springframework.stereotype.Service", 
            ComponentType.SERVICE, 200));
        register(new AnnotationComponentTypeResolver(
            "org.springframework.stereotype.Repository", 
            ComponentType.REPOSITORY, 300));
        register(new AnnotationComponentTypeResolver(
            "org.springframework.stereotype.Component", 
            ComponentType.COMPONENT, 400));
        register(new AnnotationComponentTypeResolver(
            "org.springframework.context.annotation.Configuration", 
            ComponentType.CONFIGURATION, 500));
    }
    
    public void register(ComponentTypeResolver resolver) {
        resolvers.add(resolver);
        // 按优先级排序
        resolvers.sort(Comparator.comparingInt(ComponentTypeResolver::getOrder));
    }
    
    public ComponentType resolve(MetadataReader metadataReader) {
        for (ComponentTypeResolver resolver : resolvers) {
            if (resolver.supports(metadataReader)) {
                return resolver.resolve(metadataReader);
            }
        }
        return ComponentType.UNKNOWN;
    }
}

/**
 * 扩展后的 ComponentScanner
 */
public class ComponentScanner {
    private final ComponentTypeResolverRegistry typeResolverRegistry;
    
    public ComponentScanner() {
        this.typeResolverRegistry = new ComponentTypeResolverRegistry();
    }
    
    public void registerComponentTypeResolver(ComponentTypeResolver resolver) {
        this.typeResolverRegistry.register(resolver);
    }
    
    private ComponentInfo createComponentInfo(MetadataReader metadataReader) {
        ComponentInfo info = new ComponentInfo();
        info.setClassName(metadataReader.getClassMetadata().getClassName());
        
        // 使用解析器链判断组件类型
        ComponentType componentType = typeResolverRegistry.resolve(metadataReader);
        info.setComponentType(componentType);
        
        return info;
    }
}
```

**使用示例**：

```java
// 1. 注册自定义注解解析器
ComponentScanner scanner = new ComponentScanner();
scanner.registerComponentTypeResolver(
    new AnnotationComponentTypeResolver(
        "com.mycompany.annotation.Facade", 
        ComponentType.FACADE, 150)
);

// 2. 自定义解析器（非注解方式）
scanner.registerComponentTypeResolver(new ComponentTypeResolver() {
    @Override
    public boolean supports(MetadataReader metadataReader) {
        // 按类名后缀判断
        String className = metadataReader.getClassMetadata().getClassName();
        return className.endsWith("FacadeImpl");
    }
    
    @Override
    public ComponentType resolve(MetadataReader metadataReader) {
        return ComponentType.FACADE;
    }
    
    @Override
    public int getOrder() {
        return 600;
    }
});
```

**优势**：
- 符合开闭原则：新增组件类型无需修改扫描器代码
- 支持优先级：Controller > Service > Repository 的层级关系
- 灵活扩展：支持注解、类名、接口实现等多种识别方式

---

#### 6.1.2 方法级别扫描的封装设计

**问题分析**：
当前扫描器只扫描类级别，无法识别 `@Bean`、`@EventListener` 等方法级注解，限制了框架能力。

**封装扩展方案 - 分层扫描架构**：

```java
/**
 * 扫描目标枚举
 */
public enum ScanTarget {
    CLASS,      // 仅扫描类
    METHOD,     // 仅扫描方法
    ALL         // 扫描类和方法
}

/**
 * 方法元数据信息
 */
public class MethodMetadataInfo {
    private String methodName;
    private String declaringClassName;
    private Set<String> annotationTypes;
    private String returnTypeName;
    private String[] parameterTypeNames;
    // ... getter/setter
}

/**
 * 方法过滤器接口
 */
public interface MethodFilter {
    boolean matches(MethodMetadata methodMetadata);
}

/**
 * 注解方法过滤器
 */
public class AnnotationMethodFilter implements MethodFilter {
    private final String annotationName;
    
    public AnnotationMethodFilter(String annotationName) {
        this.annotationName = annotationName;
    }
    
    @Override
    public boolean matches(MethodMetadata methodMetadata) {
        return methodMetadata.isAnnotated(annotationName);
    }
}

/**
 * 方法扫描器
 */
public class MethodScanner {
    private final List<MethodFilter> filters = new ArrayList<>();
    
    public void addFilter(MethodFilter filter) {
        filters.add(filter);
    }
    
    public List<MethodMetadataInfo> scanMethods(MetadataReader metadataReader) {
        List<MethodMetadataInfo> methods = new ArrayList<>();
        
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        Set<MethodMetadata> methodMetadataSet = annotationMetadata.getAnnotatedMethods("*");
        
        for (MethodMetadata methodMetadata : methodMetadataSet) {
            if (matchesFilters(methodMetadata)) {
                MethodMetadataInfo info = new MethodMetadataInfo();
                info.setMethodName(methodMetadata.getMethodName());
                info.setDeclaringClassName(metadataReader.getClassMetadata().getClassName());
                info.setAnnotationTypes(methodMetadata.getAnnotationTypes());
                info.setReturnTypeName(methodMetadata.getReturnTypeName());
                methods.add(info);
            }
        }
        
        return methods;
    }
    
    private boolean matchesFilters(MethodMetadata methodMetadata) {
        if (filters.isEmpty()) return true;
        return filters.stream().anyMatch(f -> f.matches(methodMetadata));
    }
}

/**
 * 增强的扫描结果
 */
public class EnhancedScanResult extends ScanResult {
    private List<MethodMetadataInfo> scannedMethods = new ArrayList<>();
    
    public void addScannedMethod(MethodMetadataInfo method) {
        scannedMethods.add(method);
    }
    
    public List<MethodMetadataInfo> getScannedMethods() {
        return scannedMethods;
    }
}

/**
 * 增强的组件扫描器
 */
public class EnhancedComponentScanner extends ComponentScanner {
    private final MethodScanner methodScanner;
    private ScanTarget scanTarget = ScanTarget.CLASS;
    
    public EnhancedComponentScanner() {
        super();
        this.methodScanner = new MethodScanner();
    }
    
    public void setScanTarget(ScanTarget scanTarget) {
        this.scanTarget = scanTarget;
    }
    
    public void addMethodFilter(MethodFilter filter) {
        this.methodScanner.addFilter(filter);
    }
    
    @Override
    public EnhancedScanResult scan(String basePackage) throws IOException {
        EnhancedScanResult result = new EnhancedScanResult();
        
        // 扫描类
        if (scanTarget == ScanTarget.CLASS || scanTarget == ScanTarget.ALL) {
            ScanResult classResult = super.scan(basePackage);
            result.setComponents(classResult.getComponents());
        }
        
        // 扫描方法
        if (scanTarget == ScanTarget.METHOD || scanTarget == ScanTarget.ALL) {
            scanMethods(basePackage, result);
        }
        
        return result;
    }
    
    private void scanMethods(String basePackage, EnhancedScanResult result) throws IOException {
        String packageSearchPath = "classpath*:" + basePackage.replace('.', '/') + "/**/*.class";
        Resource[] resources = resourceResolver.getResources(packageSearchPath);
        
        for (Resource resource : resources) {
            if (!resource.isReadable()) continue;
            
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
            List<MethodMetadataInfo> methods = methodScanner.scanMethods(metadataReader);
            
            for (MethodMetadataInfo method : methods) {
                result.addScannedMethod(method);
            }
        }
    }
}
```

**使用示例**：

```java
// 1. 创建增强扫描器
EnhancedComponentScanner scanner = new EnhancedComponentScanner();

// 2. 设置扫描目标为类和方法
scanner.setScanTarget(ScanTarget.ALL);

// 3. 添加方法过滤器：只扫描标注 @Bean 的方法
scanner.addMethodFilter(new AnnotationMethodFilter(
    "org.springframework.context.annotation.Bean"
));

// 4. 执行扫描
EnhancedScanResult result = scanner.scan("com.example.config");

// 5. 处理结果
for (MethodMetadataInfo method : result.getScannedMethods()) {
    System.out.println("Bean方法: " + method.getDeclaringClassName() + "." + method.getMethodName());
}
```

**应用场景**：
- 配置类扫描：识别 `@Configuration` 类中的 `@Bean` 方法
- 事件监听扫描：识别 `@EventListener` 方法
- AOP 切入点扫描：识别自定义注解标记的方法

---

#### 6.1.3 条件过滤的封装设计

**问题分析**：
Spring 的 `@Conditional` 注解允许根据运行时条件决定是否加载 Bean，当前扫描器缺乏这种能力。

**封装扩展方案 - 条件评估框架**：

```java
/**
 * 条件上下文
 * 提供条件判断所需的环境信息
 */
public class ConditionContext {
    private final Properties properties;
    private final Map<String, Object> environment;
    private final ClassLoader classLoader;
    
    public ConditionContext() {
        this.properties = System.getProperties();
        this.environment = new HashMap<>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }
    
    public void setEnvironmentVariable(String key, Object value) {
        environment.put(key, value);
    }
    
    public Object getEnvironmentVariable(String key) {
        return environment.get(key);
    }
}

/**
 * 条件接口
 * 实现类定义具体的条件判断逻辑
 */
public interface Condition {
    /**
     * 判断是否满足条件
     * @param metadataReader 类元数据读取器
     * @param context 条件上下文
     * @return true 表示满足条件
     */
    boolean matches(MetadataReader metadataReader, ConditionContext context);
}

/**
 * 基于属性的条件
 */
public class PropertyCondition implements Condition {
    private final String propertyName;
    private final String expectedValue;
    
    public PropertyCondition(String propertyName, String expectedValue) {
        this.propertyName = propertyName;
        this.expectedValue = expectedValue;
    }
    
    @Override
    public boolean matches(MetadataReader metadataReader, ConditionContext context) {
        String actualValue = context.getProperty(propertyName);
        return expectedValue.equals(actualValue);
    }
}

/**
 * 基于类存在的条件
 */
public class ClassPresentCondition implements Condition {
    private final String className;
    
    public ClassPresentCondition(String className) {
        this.className = className;
    }
    
    @Override
    public boolean matches(MetadataReader metadataReader, ConditionContext context) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

/**
 * 组合条件
 */
public class CompositeCondition implements Condition {
    public enum LogicalOperator {
        AND, OR, NOT
    }
    
    private final List<Condition> conditions;
    private final LogicalOperator operator;
    
    public CompositeCondition(LogicalOperator operator) {
        this.conditions = new ArrayList<>();
        this.operator = operator;
    }
    
    public void addCondition(Condition condition) {
        conditions.add(condition);
    }
    
    @Override
    public boolean matches(MetadataReader metadataReader, ConditionContext context) {
        switch (operator) {
            case AND:
                return conditions.stream().allMatch(c -> c.matches(metadataReader, context));
            case OR:
                return conditions.stream().anyMatch(c -> c.matches(metadataReader, context));
            case NOT:
                return conditions.isEmpty() || !conditions.get(0).matches(metadataReader, context);
            default:
                return false;
        }
    }
}

/**
 * 条件过滤器适配器
 * 将 Condition 适配为 TypeFilter
 */
public class ConditionTypeFilter implements TypeFilter {
    private final Condition condition;
    private final ConditionContext context;
    
    public ConditionTypeFilter(Condition condition, ConditionContext context) {
        this.condition = condition;
        this.context = context;
    }
    
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
        return condition.matches(metadataReader, context);
    }
}

/**
 * 条件扫描器
 */
public class ConditionalComponentScanner extends ComponentScanner {
    private final ConditionContext context;
    
    public ConditionalComponentScanner() {
        this.context = new ConditionContext();
    }
    
    public ConditionContext getContext() {
        return context;
    }
    
    /**
     * 添加条件过滤器
     */
    public void addCondition(Condition condition) {
        addIncludeFilter(new ConditionTypeFilter(condition, context));
    }
    
    /**
     * 基于注解的条件扫描
     * 扫描标注了 @Conditional 注解的类
     */
    public ScanResult scanConditional(String basePackage) throws IOException {
        // 先扫描所有标注 @Conditional 的类
        List<ClassMetadataInfo> conditionalClasses = new ArrayList<>();
        
        // 实际实现中，这里会解析 @Conditional 注解的值
        // 并创建对应的 Condition 对象进行判断
        
        return scan(basePackage);
    }
}
```

**使用示例**：

```java
// 1. 创建条件扫描器
ConditionalComponentScanner scanner = new ConditionalComponentScanner();

// 2. 设置环境属性
scanner.getContext().setEnvironmentVariable("app.mode", "production");
System.setProperty("feature.enabled", "true");

// 3. 添加条件：只在 production 模式下加载
scanner.addCondition(new PropertyCondition("app.mode", "production"));

// 4. 添加组合条件：production 模式且特定类存在
CompositeCondition composite = new CompositeCondition(CompositeCondition.LogicalOperator.AND);
composite.addCondition(new PropertyCondition("app.mode", "production"));
composite.addCondition(new ClassPresentCondition("com.example.OptionalDependency"));
scanner.addCondition(composite);

// 5. 执行扫描
ScanResult result = scanner.scan("com.example.service");
```

**高级应用场景**：

```java
/**
 * 基于 Profile 的条件
 */
public class ProfileCondition implements Condition {
    private final Set<String> activeProfiles;
    
    public ProfileCondition(String... profiles) {
        this.activeProfiles = new HashSet<>(Arrays.asList(profiles));
    }
    
    @Override
    public boolean matches(MetadataReader metadataReader, ConditionContext context) {
        String currentProfile = context.getProperty("spring.profiles.active");
        return activeProfiles.contains(currentProfile);
    }
}

/**
 * 基于表达式的条件（SpEL 简化版）
 */
public class ExpressionCondition implements Condition {
    private final String expression;
    
    public ExpressionCondition(String expression) {
        this.expression = expression;
    }
    
    @Override
    public boolean matches(MetadataReader metadataReader, ConditionContext context) {
        // 解析表达式如: "${app.enabled} == true && ${app.version} >= 2"
        // 实际实现可以使用 Spring 的 SpEL 或自定义表达式引擎
        return evaluateExpression(expression, context);
    }
    
    private boolean evaluateExpression(String expr, ConditionContext context) {
        // 简化实现：解析属性占位符并比较
        // 实际项目中建议使用 Spring Expression Language
        return true;
    }
}
```

---

### 6.2 性能优化

1. **并行扫描**：对大型项目，可以使用并行流优化扫描性能
2. **增量扫描**：记录扫描时间戳，只扫描修改过的类
3. **索引缓存**：建立类名到元数据的索引，加速查找

---

**文档版本**: 1.0.0  
**创建日期**: 2024-01-01  
**作者**: linsir
