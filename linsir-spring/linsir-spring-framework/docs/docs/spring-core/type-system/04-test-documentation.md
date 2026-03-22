# 类型系统测试代码说明文档

本文档详细说明 `linsir-spring-framework` 项目中类型系统（type_system）模块的源代码和测试代码结构、功能和使用方法。

## 目录结构

### 源代码目录

```
src/main/java/com/linsir/spring/framework/spring_core/type_system/
├── TypeSystemTestRunner.java                        # 类型系统测试运行器
├── conversion/                                      # 类型转换模块
│   ├── ConvertiblePair.java                         # 可转换类型对
│   ├── Converter.java                               # 转换器接口
│   ├── GenericConverter.java                        # 通用转换器接口
│   ├── ConversionService.java                       # 转换服务接口
│   └── ConverterLookupDemo.java                     # 转换器查找演示
├── descriptor/                                      # 类型描述符模块
│   ├── NotNull.java                                 # 非空注解
│   ├── Size.java                                    # 大小限制注解
│   ├── Range.java                                   # 范围限制注解
│   ├── User.java                                    # 用户实体类
│   ├── UserService.java                             # 用户服务类
│   ├── PropertyDescriptorDemo.java                  # 属性描述符演示
│   └── ValueOfDemo.java                             # ValueOf 方法演示
└── resolvable/                                      # 类型解析模块
    ├── User.java                                    # 用户实体类
    ├── BaseService.java                             # 基础服务抽象类
    ├── UserService.java                             # 用户服务实现类
    ├── UserController.java                          # 用户控制器
    ├── Config.java                                  # 配置类
    ├── ForClassDemo.java                            # 类类型解析演示
    ├── ForFieldDemo.java                            # 字段类型解析演示
    ├── ForMethodParameterDemo.java                  # 方法参数类型解析演示
    ├── ForMethodReturnTypeDemo.java                 # 方法返回类型解析演示
    ├── GenericResolutionDemo.java                   # 泛型解析演示
    ├── TraditionalApproachDemo.java                 # 传统方法演示
    └── TypeChecksDemo.java                          # 类型检查演示
```

### 测试代码目录

```
src/test/java/com/linsir/spring/framework/spring_core/type_system/
├── TypeSystemStandaloneTest.java                    # 类型系统综合独立测试
├── conversion/
│   └── ConversionServiceTest.java                   # 转换服务测试
├── descriptor/
│   ├── TypeDescriptorTest.java                      # 类型描述符 JUnit 测试
│   └── TypeDescriptorStandaloneTest.java            # 类型描述符独立测试
└── resolvable/
    ├── ResolvableTypeTest.java                      # 可解析类型 JUnit 测试
    └── ResolvableTypeStandaloneTest.java            # 可解析类型独立测试
```

## 源代码说明

### 1. Conversion 模块 (conversion/)

#### 核心接口

| 文件 | 说明 | 关键代码 |
|-----|------|---------|
| ConvertiblePair.java | 可转换类型对，封装源类型和目标类型 | sourceType, targetType, equals(), hashCode() |
| Converter.java | 单一类型转换器函数式接口 | FunctionalInterface, T convert(S source) |
| GenericConverter.java | 通用转换器接口，支持多对多转换 | getConvertibleTypes(), convert(...) |
| ConversionService.java | 转换服务接口，提供统一转换入口 | canConvert(), convert() |

#### 演示类

| 文件 | 说明 |
|-----|------|
| ConverterLookupDemo.java | 演示转换器的注册和查找机制 |

### 2. Descriptor 模块 (descriptor/)

#### 注解定义

| 文件 | 说明 | 属性 |
|-----|------|------|
| NotNull.java | 非空约束注解 | message() |
| Size.java | 集合大小约束注解 | min(), max(), message() |
| Range.java | 数值范围约束注解 | min(), max(), message() |

注解元数据：
- Target: FIELD, METHOD, PARAMETER
- Retention: RUNTIME

#### 实体类

| 文件 | 说明 | 属性 |
|-----|------|------|
| User.java | 用户实体类 | name, age, email |
| UserService.java | 用户服务类 | serviceName, maxUsers, users |

#### 演示类

| 文件 | 说明 |
|-----|------|
| PropertyDescriptorDemo.java | JavaBean 属性描述符演示，展示 getter/setter 反射访问 |
| ValueOfDemo.java | ValueOf 方法演示，展示字符串到基本类型的转换 |

### 3. Resolvable 模块 (resolvable/)

#### 实体类

| 文件 | 说明 | 属性 |
|-----|------|------|
| User.java | 用户实体类（简化版） | name, age |
| Config.java | 配置类 | ConfigHolder, DatabaseConfig, CacheConfig |

#### 服务类

| 文件 | 说明 | 泛型参数 |
|-----|------|---------|
| BaseService.java | 基础服务抽象类 | T, ID |
| UserService.java | 用户服务实现类 | BaseService User Long |
| UserController.java | 用户控制器 | List User |

#### 演示类

| 文件 | 说明 |
|-----|------|
| ForClassDemo.java | 通过类获取泛型类型信息 |
| ForFieldDemo.java | 通过字段获取泛型类型信息 |
| ForMethodParameterDemo.java | 通过方法参数获取泛型类型信息 |
| ForMethodReturnTypeDemo.java | 通过方法返回值获取泛型类型信息 |
| GenericResolutionDemo.java | 泛型解析综合演示 |
| TraditionalApproachDemo.java | 传统类型解析方法对比 |
| TypeChecksDemo.java | 类型检查操作演示 |

## 测试分类

### 1. ConversionService 测试

**文件位置**: conversion/ConversionServiceTest.java

#### 功能说明
测试 Spring 类型转换系统的核心接口：
- ConvertiblePair: 源类型与目标类型的配对
- Converter: 单一类型转换器接口
- ConverterFactory: 转换器工厂
- GenericConverter: 通用类型转换器
- ConversionService: 转换服务接口

#### 测试用例

| 测试方法 | 说明 |
|---------|------|
| testConvertiblePairCreation() | 测试 ConvertiblePair 创建 |
| testConvertiblePairEquality() | 测试 ConvertiblePair 相等性 |
| testConverterInterface() | 测试 Converter 函数式接口 |
| testStringToLongConversion() | 测试 String 转 Long |
| testStringToDoubleConversion() | 测试 String 转 Double |
| testStringToBooleanConversion() | 测试 String 转 Boolean |

#### 运行方式

**JUnit 方式**:
```
mvn test -Dtest=ConversionServiceTest
```

**独立运行方式**:
```
# 编译
javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/com/linsir/spring/framework/spring_core/type_system/TypeSystemStandaloneTest.java

# 运行
java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.TypeSystemStandaloneTest
```

### 2. TypeDescriptor 测试

**文件位置**: 
- descriptor/TypeDescriptorTest.java (JUnit 版本)
- descriptor/TypeDescriptorStandaloneTest.java (独立运行版本)

#### 功能说明
测试类型描述符系统，包括：
- 注解元数据读取（NotNull, Size, Range）
- 泛型类型解析（List String, Map String User）
- JavaBean 属性访问
- 注解的 Retention 和 Target 元数据

#### 测试用例

| 测试方法 | 说明 |
|---------|------|
| testNotNullAnnotation() | 测试 NotNull 注解 |
| testSizeAnnotation() | 测试 Size 注解（集合大小限制） |
| testRangeAnnotation() | 测试 Range 注解（数值范围限制） |
| testGenericFieldType() | 测试泛型字段类型解析 |
| testMapFieldType() | 测试 Map 泛型参数解析 |
| testUserClassProperties() | 测试 User 类属性访问 |
| testUserServiceProperties() | 测试 UserService 属性 |
| testUserServiceUserOperations() | 测试 UserService CRUD 操作 |
| testAnnotationRetention() | 测试注解保留策略 |
| testAnnotationTargets() | 测试注解目标元素 |

#### 注解说明

```java
@NotNull(message = "Value must not be null")
private String name;

@Size(min = 1, max = 100)
private List tags;

@Range(min = 0, max = 150)
private int age;
```

#### 运行方式

**JUnit 方式**:
```
mvn test -Dtest=TypeDescriptorTest
```

**独立运行方式**:
```
# 编译
javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/com/linsir/spring/framework/spring_core/type_system/descriptor/TypeDescriptorStandaloneTest.java

# 运行
java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.descriptor.TypeDescriptorStandaloneTest
```

### 3. ResolvableType 测试

**文件位置**:
- resolvable/ResolvableTypeTest.java (JUnit 版本)
- resolvable/ResolvableTypeStandaloneTest.java (独立运行版本)

#### 功能说明
测试泛型类型解析系统，包括：
- 类继承中的泛型参数解析
- 字段泛型类型解析
- 方法返回类型和参数类型解析
- 嵌套泛型解析（如 List List Integer）
- 类型可分配性检查

#### 测试用例

| 测试方法 | 说明 |
|---------|------|
| testUserClass() | 测试 User 实体类 |
| testBaseServiceGenericTypes() | 测试 BaseService T ID 泛型参数 |
| testUserServiceOperations() | 测试 UserService CRUD 操作 |
| testGenericFieldResolution() | 测试 List String 字段泛型 |
| testMapFieldResolution() | 测试 Map K V 字段泛型 |
| testNestedGenericResolution() | 测试嵌套泛型 List List Integer |
| testMethodReturnTypeResolution() | 测试方法返回类型泛型 |
| testMethodParameterResolution() | 测试方法参数类型泛型 |
| testConfigGenericTypes() | 测试 ConfigHolder 泛型类 |
| testTypeAssignability() | 测试类型可分配性 |
| testArrayType() | 测试数组类型 |

#### 泛型解析示例

```java
// 类继承泛型解析
public class UserService extends BaseService User Long { }

// 解析结果: T=User, ID=Long
Type genericSuperclass = UserService.class.getGenericSuperclass();
ParameterizedType paramType = (ParameterizedType) genericSuperclass;
Type[] actualArgs = paramType.getActualTypeArguments();
// actualArgs[0] = User.class
// actualArgs[1] = Long.class
```

#### 运行方式

**JUnit 方式**:
```
mvn test -Dtest=ResolvableTypeTest
```

**独立运行方式**:
```
# 编译
javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/com/linsir/spring/framework/spring_core/type_system/resolvable/ResolvableTypeStandaloneTest.java

# 运行
java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.resolvable.ResolvableTypeStandaloneTest
```

### 4. 综合类型系统测试

**文件位置**: TypeSystemStandaloneTest.java

#### 功能说明
综合测试类型系统的所有核心组件，包括：
- ConvertiblePair 的创建、相等性和哈希码
- 各种 Converter 实现（String to Integer/Long/Double/Boolean）
- GenericConverter 接口
- ConversionService 接口

#### 测试用例

| 测试方法 | 说明 |
|---------|------|
| testConvertiblePairCreation() | ConvertiblePair 创建测试 |
| testConvertiblePairEquality() | ConvertiblePair 相等性测试 |
| testConvertiblePairHashCode() | ConvertiblePair 哈希码测试 |
| testStringToIntegerConverter() | String 转 Integer 测试 |
| testStringToLongConverter() | String 转 Long 测试 |
| testStringToDoubleConverter() | String 转 Double 测试 |
| testStringToBooleanConverter() | String 转 Boolean 测试 |
| testGenericConverterInterface() | GenericConverter 接口测试 |
| testConversionServiceInterface() | ConversionService 接口测试 |

#### 运行方式

```
# 编译
javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/com/linsir/spring/framework/spring_core/type_system/TypeSystemStandaloneTest.java

# 运行
java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.TypeSystemStandaloneTest
```

## 测试工具类说明

### 独立测试框架

所有 StandaloneTest.java 文件都实现了简单的测试框架：

```java
private static int testCount = 0;   // 测试总数
private static int passCount = 0;   // 通过数
private static int failCount = 0;   // 失败数

// 辅助方法
private static void startTest(String testName)  // 开始测试
private static void pass()                      // 标记通过
private static void fail(String message)        // 标记失败
private static void assertEquals(...)           // 断言相等
private static void assertTrue(...)             // 断言为真
private static void assertNotNull(...)          // 断言非空
private static void printTestReport()           // 打印测试报告
```

### 测试报告格式

```
========================================
    ResolvableType Standalone Test
========================================

[1] User Class                               ... PASS
[2] BaseService Generic Types                ... PASS
...

========================================
           Test Report
========================================
Total:  11
Passed: 11
Failed: 0
========================================

All tests passed!
```

## 核心代码示例

### Conversion 模块示例

```java
// ConvertiblePair 使用
ConvertiblePair pair = new ConvertiblePair(String.class, Integer.class);
Class sourceType = pair.getSourceType();  // String.class
Class targetType = pair.getTargetType();  // Integer.class

// Converter 使用
Converter converter = Integer::valueOf;
Integer result = converter.convert("123");  // 123

// ConversionService 使用
ConversionService service = new DefaultConversionService();
if (service.canConvert(String.class, Integer.class)) {
    Integer value = service.convert("456", Integer.class);  // 456
}
```

### Descriptor 模块示例

```java
// 注解定义
@NotNull(message = "Name must not be null")
private String name;

@Size(min = 1, max = 100)
private List tags;

@Range(min = 0, max = 150)
private int age;

// 反射读取注解
Field field = clazz.getDeclaredField("name");
NotNull notNull = field.getAnnotation(NotNull.class);
String message = notNull.message();  // "Name must not be null"

// JavaBean 属性访问
PropertyDescriptor pd = new PropertyDescriptor("name", User.class);
Method getter = pd.getReadMethod();      // getName()
Method setter = pd.getWriteMethod();     // setName(String)
Object value = getter.invoke(user);      // 获取属性值
```

### Resolvable 模块示例

```java
// 类泛型参数解析
public class UserService extends BaseService { }

Type genericSuperclass = UserService.class.getGenericSuperclass();
ParameterizedType paramType = (ParameterizedType) genericSuperclass;
Type[] actualArgs = paramType.getActualTypeArguments();
// actualArgs[0] = User.class
// actualArgs[1] = Long.class

// 字段泛型解析
private List stringList;

Field field = clazz.getDeclaredField("stringList");
Type genericType = field.getGenericType();
ParameterizedType paramType = (ParameterizedType) genericType;
Type[] args = paramType.getActualTypeArguments();
// args[0] = String.class

// 方法返回类型解析
public List getAllUsers() { }

Method method = clazz.getMethod("getAllUsers");
Type returnType = method.getGenericReturnType();
// returnType = ParameterizedType(List)
```

## 批量运行所有测试

### 使用 Maven

```
# 运行所有类型系统测试
mvn test -Dtest="TypeSystem,ConversionService,TypeDescriptor,ResolvableType"

# 或运行整个模块测试
mvn test -pl linsir-spring-framework
```

### 使用独立运行方式

```
cd linsir-spring-framework

# 编译所有测试
javac -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/com/linsir/spring/framework/spring_core/type_system/TypeSystemStandaloneTest.java

# 运行所有独立测试
echo "TypeSystemStandaloneTest"
java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.TypeSystemStandaloneTest

echo "TypeDescriptorStandaloneTest"
java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.descriptor.TypeDescriptorStandaloneTest

echo "ResolvableTypeStandaloneTest"
java -cp target/classes;target/test-classes com.linsir.spring.framework.spring_core.type_system.resolvable.ResolvableTypeStandaloneTest
```

## 相关文档

- 01-resolvable-type.md - ResolvableType 类型解析
- 02-type-descriptor.md - TypeDescriptor 类型描述符
- 03-conversion-service.md - ConversionService 类型转换服务

## 注意事项

1. 编码问题: 所有 Java 文件使用 UTF-8 编码，编译时需要指定 -encoding UTF-8
2. 类路径: 独立运行时需包含 target/classes 和 target/test-classes
3. JDK 版本: 项目使用 JDK 17，确保环境变量配置正确
4. Windows 路径: Windows 下使用分号分隔类路径，Linux/Mac 使用冒号
