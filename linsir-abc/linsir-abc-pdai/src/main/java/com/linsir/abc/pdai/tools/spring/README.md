# Spring 常用工具类分析

## 目录介绍

本目录用于分析和演示 Spring 框架中常用的工具类。Spring 框架提供了丰富的工具类，这些工具类简化了 Java 开发中的各种常见操作，提高了开发效率。

## 代码结构

本目录包含以下代码文件：

```
tools/spring/
├── README.md                # 本说明文档
├── SpringDemoMain.java      # 主类，用于演示所有示例代码
├── SpringStringUtilsDemo.java      # StringUtils 示例代码
├── SpringCollectionUtilsDemo.java  # CollectionUtils 示例代码
├── SpringObjectUtilsDemo.java      # ObjectUtils 示例代码
├── SpringReflectionUtilsDemo.java  # ReflectionUtils 示例代码
├── SpringAssertDemo.java           # Assert 示例代码
├── SpringClassUtilsDemo.java       # ClassUtils 示例代码
├── SpringFileCopyUtilsDemo.java    # FileCopyUtils 示例代码
└── SpringResourceLoaderDemo.java   # ResourceLoader 示例代码
```

## 如何运行示例代码

1. 确保 Maven 依赖项已正确配置（已在 pom.xml 中添加了 spring-core 和 spring-context 依赖）
2. 编译项目：`mvn compile`
3. 运行主类：`java -cp "target\classes;target\dependency\*" com.linsir.abc.pdai.tools.spring.SpringDemoMain`

运行后，将看到所有工具类的示例代码执行结果，包括详细的输出说明。

## Spring 核心工具类

### 1. 字符串工具类 (`org.springframework.util.StringUtils`)

`StringUtils` 是 Spring 框架中最常用的工具类之一，提供了丰富的字符串操作方法。

#### 示例代码

示例代码文件：`SpringStringUtilsDemo.java`

该文件演示了 `StringUtils` 的常用方法，包括：
- 字符串判空方法 (`hasLength`, `hasText`, `isEmpty`)
- 字符串修剪方法 (`trimWhitespace`, `trimAllWhitespace`)
- 字符串分隔和连接方法 (`commaDelimitedListToStringArray`, `collectionToDelimitedString`)
- 字符串替换方法 (`replace`)
- 字符串前缀和后缀检查方法 (`startsWithIgnoreCase`, `endsWithIgnoreCase`)
- 字符串大小写转换方法 (`capitalize`, `uncapitalize`)
- 其他常用方法

#### 主要方法

| 方法名 | 描述 | 示例 |
|-------|------|------|
| `hasLength(String str)` | 检查字符串是否有长度 | `StringUtils.hasLength("hello")` → `true` |
| `hasText(String str)` | 检查字符串是否有文本内容 | `StringUtils.hasText("  ")` → `false` |
| `isEmpty(Object str)` | 检查字符串是否为空 | `StringUtils.isEmpty(null)` → `true` |
| `trimWhitespace(String str)` | 去除字符串首尾空白 | `StringUtils.trimWhitespace("  hello  ")` → `"hello"` |
| `trimAllWhitespace(String str)` | 去除字符串所有空白 | `StringUtils.trimAllWhitespace("h e l l o")` → `"hello"` |
| `commaDelimitedListToStringArray(String str)` | 逗号分隔的字符串转数组 | `StringUtils.commaDelimitedListToStringArray("a,b,c")` → `["a", "b", "c"]` |
| `collectionToDelimitedString(Collection<?> coll, String delim)` | 集合转分隔字符串 | `StringUtils.collectionToDelimitedString(Arrays.asList("a", "b"), ",")` → `"a,b"` |
| `replace(String inString, String oldPattern, String newPattern)` | 替换字符串 | `StringUtils.replace("hello world", "world", "Spring")` → `"hello Spring"` |
| `startsWithIgnoreCase(String str, String prefix)` | 忽略大小写检查前缀 | `StringUtils.startsWithIgnoreCase("Hello", "he")` → `true` |
| `endsWithIgnoreCase(String str, String suffix)` | 忽略大小写检查后缀 | `StringUtils.endsWithIgnoreCase("Hello", "LO")` → `true` |
| `capitalize(String str)` | 首字母大写 | `StringUtils.capitalize("hello")` → `"Hello"` |
| `uncapitalize(String str)` | 首字母小写 | `StringUtils.uncapitalize("Hello")` → `"hello"` |

#### 运行效果

运行 `SpringStringUtilsDemo.demonstrateStringUtils()` 方法将输出：
- 各种字符串判空方法的结果
- 字符串修剪操作的结果
- 字符串分隔和连接操作的结果
- 字符串替换操作的结果
- 字符串前缀和后缀检查的结果
- 字符串大小写转换的结果
- 其他常用方法的结果

### 2. 集合工具类 (`org.springframework.util.CollectionUtils`)

`CollectionUtils` 提供了对集合操作的工具方法。

#### 示例代码

示例代码文件：`SpringCollectionUtilsDemo.java`

该文件演示了 `CollectionUtils` 的常用方法，包括：
- 集合判空方法 (`isEmpty`)
- 集合包含方法 (`containsAny`, `findFirstMatch`)
- 数组合并到集合 (`mergeArrayIntoCollection`)
- 属性合并到 Map (`mergePropertiesIntoMap`)
- 创建指定初始容量的集合 (`newHashMap`, `newLinkedHashMap`)
- 集合差集、交集和并集操作

#### 主要方法

| 方法名 | 描述 | 示例 |
|-------|------|------|
| `isEmpty(Collection<?> collection)` | 检查集合是否为空 | `CollectionUtils.isEmpty(null)` → `true` |
| `containsAny(Collection<?> source, Collection<?> candidates)` | 检查集合是否包含任一候选元素 | `CollectionUtils.containsAny(Arrays.asList(1, 2), Arrays.asList(2, 3))` → `true` |
| `findFirstMatch(Collection<?> source, Collection<?> candidates)` | 查找第一个匹配的元素 | `CollectionUtils.findFirstMatch(Arrays.asList(1, 2), Arrays.asList(2, 3))` → `2` |
| `mergeArrayIntoCollection(Object array, Collection<?> collection)` | 数组合并到集合 | `CollectionUtils.mergeArrayIntoCollection(new Object[]{1, 2}, list)` |
| `mergePropertiesIntoMap(Properties props, Map<String, Object> map)` | 属性合并到 Map | `CollectionUtils.mergePropertiesIntoMap(properties, map)` |
| `newHashMap(int initialCapacity)` | 创建指定初始容量的 HashMap | `Map<String, Object> map = CollectionUtils.newHashMap(16)` |
| `newLinkedHashMap(int initialCapacity)` | 创建指定初始容量的 LinkedHashMap | `Map<String, Object> map = CollectionUtils.newLinkedHashMap(16)` |

#### 运行效果

运行 `SpringCollectionUtilsDemo.demonstrateCollectionUtils()` 方法将输出：
- 各种集合判空方法的结果
- 集合包含操作的结果
- 数组合并到集合的结果
- 属性合并到 Map 的结果
- 创建指定初始容量集合的结果
- 集合差集、交集和并集操作的结果

### 3. 对象工具类 (`org.springframework.util.ObjectUtils`)

`ObjectUtils` 提供了对对象操作的工具方法。

#### 示例代码

示例代码文件：`SpringObjectUtilsDemo.java`

该文件演示了 `ObjectUtils` 的常用方法，包括：
- 对象判空方法 (`isEmpty`)
- 对象比较方法 (`nullSafeEquals`)
- 对象哈希码方法 (`nullSafeHashCode`)
- 对象身份字符串方法 (`identityToString`)
- 对象显示字符串方法 (`getDisplayString`)
- 向数组添加对象方法 (`addObjectToArray`)
- 转换为对象数组方法
- 数组长度方法
- 数组索引检查方法

#### 主要方法

| 方法名 | 描述 | 示例 |
|-------|------|------|
| `isEmpty(Object obj)` | 检查对象是否为空 | `ObjectUtils.isEmpty(null)` → `true` |
| `nullSafeEquals(Object o1, Object o2)` | 安全地比较两个对象是否相等 | `ObjectUtils.nullSafeEquals("a", "a")` → `true` |
| `nullSafeHashCode(Object obj)` | 安全地获取对象的哈希码 | `ObjectUtils.nullSafeHashCode("hello")` → 哈希码值 |
| `identityToString(Object obj)` | 获取对象的身份字符串 | `ObjectUtils.identityToString(obj)` → `"java.lang.Object@123456"` |
| `getDisplayString(Object obj)` | 获取对象的显示字符串 | `ObjectUtils.getDisplayString(obj)` → 对象的字符串表示 |
| `addObjectToArray(Object[] array, Object obj)` | 向数组添加对象 | `ObjectUtils.addObjectToArray(new Object[]{1, 2}, 3)` → `[1, 2, 3]` |

#### 运行效果

运行 `SpringObjectUtilsDemo.demonstrateObjectUtils()` 方法将输出：
- 各种对象判空方法的结果
- 对象比较方法的结果
- 对象哈希码方法的结果
- 对象身份字符串方法的结果
- 对象显示字符串方法的结果
- 向数组添加对象方法的结果
- 转换为对象数组方法的结果
- 数组长度方法的结果
- 数组索引检查方法的结果

### 4. 反射工具类 (`org.springframework.util.ReflectionUtils`)

`ReflectionUtils` 提供了对 Java 反射操作的工具方法，简化了反射代码的编写。

#### 示例代码

示例代码文件：`SpringReflectionUtilsDemo.java`

该文件演示了 `ReflectionUtils` 的常用方法，包括：
- 获取声明的字段
- 获取声明的方法
- 调用方法
- 设置字段值
- 遍历字段
- 遍历方法
- 查找特定方法
- 处理异常

#### 主要方法

| 方法名 | 描述 | 示例 |
|-------|------|------|
| `getDeclaredField(Class<?> clazz, String name)` | 获取声明的字段 | `ReflectionUtils.getDeclaredField(User.class, "name")` |
| `getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes)` | 获取声明的方法 | `ReflectionUtils.getDeclaredMethod(User.class, "getName")` |
| `invokeMethod(Method method, Object target, Object... args)` | 调用方法 | `ReflectionUtils.invokeMethod(getNameMethod, user)` |
| `setField(Field field, Object target, Object value)` | 设置字段值 | `ReflectionUtils.setField(nameField, user, "New Name")` |
| `getField(Field field, Object target)` | 获取字段值 | `ReflectionUtils.getField(nameField, user)` |
| `doWithFields(Class<?> clazz, FieldCallback fc)` | 遍历字段 | `ReflectionUtils.doWithFields(User.class, field -> {...})` |
| `doWithMethods(Class<?> clazz, MethodCallback mc)` | 遍历方法 | `ReflectionUtils.doWithMethods(User.class, method -> {...})` |
| `makeAccessible(AccessibleObject ao)` | 设置可访问 | `ReflectionUtils.makeAccessible(field)` |

#### 运行效果

运行 `SpringReflectionUtilsDemo.demonstrateReflectionUtils()` 方法将输出：
- 原始用户对象信息
- 获取到的字段信息
- 获取到的方法信息
- 调用 getter 方法的结果
- 调用 setter 方法后的用户对象信息
- 当前字段值
- 设置字段值后的用户对象信息
- 遍历字段的结果
- 遍历方法的结果
- 找到的特定方法信息
- 预期的异常信息

### 5. 类型转换工具类 (`org.springframework.core.convert.support.DefaultConversionService`)

Spring 提供了强大的类型转换系统，`DefaultConversionService` 是其核心实现。

#### 主要功能

- 基本类型之间的转换
- 字符串与其他类型之间的转换
- 集合、数组与其他类型之间的转换
- 自定义类型转换

#### 使用示例

```java
// 创建转换服务
ConversionService conversionService = new DefaultConversionService();

// 字符串转整数
Integer number = conversionService.convert("123", Integer.class);

// 整数转字符串
String str = conversionService.convert(123, String.class);

// 字符串转日期
Date date = conversionService.convert("2024-01-01", Date.class);

// 集合转字符串
List<String> list = Arrays.asList("a", "b", "c");
String listStr = conversionService.convert(list, String.class);
```

### 8. 资源加载工具类 (`org.springframework.core.io.ResourceLoader`)

`ResourceLoader` 提供了加载资源的能力，是 Spring 资源访问抽象的核心接口。

#### 示例代码

示例代码文件：`SpringResourceLoaderDemo.java`

该文件演示了 `ResourceLoader` 的常用方法，包括：
- 创建资源加载器
- 加载类路径资源
- 加载文件系统资源
- 加载 URL 资源
- 加载相对路径资源
- 资源类型判断

#### 主要实现

- `DefaultResourceLoader` - 默认实现
- `ClassLoaderResourceLoader` - 基于类加载器的实现
- `FileSystemResourceLoader` - 基于文件系统的实现

#### 资源类型

- `ClassPathResource` - 类路径资源
- `FileSystemResource` - 文件系统资源
- `UrlResource` - URL 资源
- `ServletContextResource` - Web 应用资源

#### 运行效果

运行 `SpringResourceLoaderDemo.demonstrateResourceLoader()` 方法将输出：
- 创建的资源加载器信息
- 加载类路径资源的结果
- 加载文件系统资源的结果
- 加载 URL 资源的结果（前 500 个字符）
- 加载相对路径资源的结果
- 各种资源类型的判断结果

### 5. 断言工具类 (`org.springframework.util.Assert`)

`Assert` 提供了断言方法，用于验证参数和状态的有效性。

#### 示例代码

示例代码文件：`SpringAssertDemo.java`

该文件演示了 `Assert` 的常用方法，包括：
- `notNull` 方法：断言对象不为 null
- `isNull` 方法：断言对象为 null
- `isTrue` 方法：断言表达式为 true
- `hasLength` 方法：断言字符串有长度
- `hasText` 方法：断言字符串有文本内容
- `notEmpty` 方法：断言集合不为空
- `notEmpty` 方法：断言 Map 不为空
- `isInstanceOf` 方法：断言对象是指定类型
- 实际应用示例

#### 主要方法

| 方法名 | 描述 | 示例 |
|-------|------|------|
| `notNull(Object object, String message)` | 断言对象不为 null | `Assert.notNull(user, "User must not be null")` |
| `isNull(Object object, String message)` | 断言对象为 null | `Assert.isNull(user, "User must be null")` |
| `isTrue(boolean expression, String message)` | 断言表达式为 true | `Assert.isTrue(age > 0, "Age must be positive")` |
| `hasLength(String text, String message)` | 断言字符串有长度 | `Assert.hasLength(username, "Username must not be empty")` |
| `hasText(String text, String message)` | 断言字符串有文本内容 | `Assert.hasText(email, "Email must not be blank")` |
| `notEmpty(Collection<?> collection, String message)` | 断言集合不为空 | `Assert.notEmpty(userList, "User list must not be empty")` |
| `notEmpty(Map<?, ?> map, String message)` | 断言 Map 不为空 | `Assert.notEmpty(userMap, "User map must not be empty")` |
| `isInstanceOf(Class<?> type, Object obj, String message)` | 断言对象是指定类型 | `Assert.isInstanceOf(User.class, obj, "Object must be a User")` |

#### 运行效果

运行 `SpringAssertDemo.demonstrateAssert()` 方法将输出：
- 各种断言方法的测试结果
- 预期异常的捕获和处理
- 实际应用示例的运行结果

### 6. 类工具类 (`org.springframework.util.ClassUtils`)

`ClassUtils` 提供了对 Java 类操作的工具方法。

#### 示例代码

示例代码文件：`SpringClassUtilsDemo.java`

该文件演示了 `ClassUtils` 的常用方法，包括：
- 获取默认类加载器
- 加载类
- 获取类的短名称
- 获取类的包名
- 检查类型是否可赋值
- 检查是否为内部类
- 获取类实现的所有接口
- 获取类的所有父类
- 获取类的 canonical 名称
- 检查类是否为数组类型
- 检查类是否为基本类型

#### 主要方法

| 方法名 | 描述 | 示例 |
|-------|------|------|
| `getDefaultClassLoader()` | 获取默认类加载器 | `ClassUtils.getDefaultClassLoader()` |
| `forName(String name, ClassLoader classLoader)` | 加载类 | `ClassUtils.forName("java.lang.String", ClassUtils.getDefaultClassLoader())` |
| `getShortName(Class<?> clazz)` | 获取类的短名称 | `ClassUtils.getShortName(User.class)` → `"User"` |
| `getPackageName(Class<?> clazz)` | 获取类的包名 | `ClassUtils.getPackageName(User.class)` → `"com.example"` |
| `isAssignable(Class<?> superType, Class<?> subType)` | 检查类型是否可赋值 | `ClassUtils.isAssignable(Number.class, Integer.class)` → `true` |
| `isInnerClass(Class<?> clazz)` | 检查是否为内部类 | `ClassUtils.isInnerClass(User.InnerClass.class)` → `true` |
| `getAllInterfacesForClass(Class<?> clazz)` | 获取类实现的所有接口 | `ClassUtils.getAllInterfacesForClass(User.class)` |
| `getAllSuperclasses(Class<?> clazz)` | 获取类的所有父类 | `ClassUtils.getAllSuperclasses(User.class)` |
| `getCanonicalName(Class<?> clazz)` | 获取类的 canonical 名称 | `ClassUtils.getCanonicalName(User.class)` → `"com.example.User"` |
| `isArray(Class<?> clazz)` | 检查类是否为数组类型 | `ClassUtils.isArray(String[].class)` → `true` |
| `isPrimitiveOrWrapper(Class<?> clazz)` | 检查类是否为基本类型或包装类型 | `ClassUtils.isPrimitiveOrWrapper(Integer.class)` → `true` |

#### 运行效果

运行 `SpringClassUtilsDemo.demonstrateClassUtils()` 方法将输出：
- 默认类加载器信息
- 加载的类信息
- 类的短名称
- 类的包名
- 类型可赋值检查结果
- 是否为内部类检查结果
- 类实现的所有接口
- 类的所有父类
- 类的 canonical 名称
- 是否为数组类型检查结果
- 是否为基本类型或包装类型检查结果

### 7. 文件工具类 (`org.springframework.util.FileCopyUtils`)

`FileCopyUtils` 提供了文件复制的工具方法。

#### 示例代码

示例代码文件：`SpringFileCopyUtilsDemo.java`

该文件演示了文件复制操作的常用方法，包括：
- 字节数组复制到文件
- 文件复制到文件
- 输入流复制到输出流
- 字符串复制到 Writer
- 文件复制到字节数组
- 输入流复制到字节数组
- Reader 复制到字符串

#### 主要方法

| 方法名 | 描述 | 示例 |
|-------|------|------|
| `copy(byte[] in, File out)` | 字节数组复制到文件 | `FileCopyUtils.copy(data, new File("output.txt"))` |
| `copy(File in, File out)` | 文件复制到文件 | `FileCopyUtils.copy(new File("input.txt"), new File("output.txt"))` |
| `copy(InputStream in, OutputStream out)` | 输入流复制到输出流 | `FileCopyUtils.copy(inputStream, outputStream)` |
| `copy(String in, Writer out)` | 字符串复制到 Writer | `FileCopyUtils.copy(content, writer)` |
| `copyToByteArray(File in)` | 文件复制到字节数组 | `byte[] data = FileCopyUtils.copyToByteArray(new File("input.txt"))` |
| `copyToByteArray(InputStream in)` | 输入流复制到字节数组 | `byte[] data = FileCopyUtils.copyToByteArray(inputStream)` |
| `copyToString(Reader in)` | Reader 复制到字符串 | `String content = FileCopyUtils.copyToString(reader)` |

#### 运行效果

运行 `SpringFileCopyUtilsDemo.demonstrateFileCopyUtils()` 方法将输出：
- 准备测试文件的信息
- 字节数组复制到文件的结果
- 文件复制到文件的结果
- 输入流复制到输出流的结果
- 字符串复制到 Writer 的结果
- 文件复制到字节数组的结果
- 输入流复制到字节数组的结果
- Reader 复制到字符串的结果
- 清理临时文件的信息

### 10. 环境工具类 (`org.springframework.core.env.Environment`)

`Environment` 是 Spring 环境抽象的核心接口，提供了访问环境属性的能力。

#### 主要方法

| 方法名 | 描述 | 示例 |
|-------|------|------|
| `getProperty(String key)` | 获取属性值 | `environment.getProperty("app.name")` |
| `getProperty(String key, String defaultValue)` | 获取属性值，带默认值 | `environment.getProperty("app.port", "8080")` |
| `getProperty(String key, Class<T> targetType)` | 获取指定类型的属性值 | `environment.getProperty("app.port", Integer.class)` |
| `getRequiredProperty(String key)` | 获取必需的属性值 | `environment.getRequiredProperty("database.url")` |
| `containsProperty(String key)` | 检查属性是否存在 | `environment.containsProperty("app.name")` |
| `getActiveProfiles()` | 获取激活的配置文件 | `environment.getActiveProfiles()` |
| `acceptsProfiles(String... profiles)` | 检查是否接受指定的配置文件 | `environment.acceptsProfiles("dev", "test")` |

#### 使用示例

```java
// 获取属性值
String appName = environment.getProperty("app.name");
Integer appPort = environment.getProperty("app.port", Integer.class, 8080);

// 获取必需的属性值
String databaseUrl = environment.getRequiredProperty("database.url");

// 检查配置文件
if (environment.acceptsProfiles("prod")) {
    // 生产环境配置
}
```

## Spring Boot 工具类

### 1. 应用工具类 (`org.springframework.boot.SpringApplication`)

`SpringApplication` 是 Spring Boot 应用的入口类，提供了启动 Spring 应用的能力。

#### 主要方法

| 方法名 | 描述 | 示例 |
|-------|------|------|
| `run(Class<?> primarySource, String... args)` | 运行应用 | `SpringApplication.run(Application.class, args)` |
| `run(Class<?>[] primarySources, String[] args)` | 运行应用，多主类 | `SpringApplication.run(new Class[]{App1.class, App2.class}, args)` |
| `builder()` | 创建构建器 | `SpringApplicationBuilder builder = SpringApplication.builder()` |

#### 使用示例

```java
// 基本用法
public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
}

// 自定义配置
public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Application.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
    app.run(args);
}
```

### 2. 配置工具类 (`org.springframework.boot.context.properties.ConfigurationProperties`)

`ConfigurationProperties` 注解用于绑定配置属性到 Java 对象。

#### 使用示例

```java
// 配置类
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private int port;
    private Database database;
    
    // getter, setter
    
    public static class Database {
        private String url;
        private String username;
        private String password;
        
        // getter, setter
    }
}

// 启用配置属性
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig {
}

// 使用配置
@Service
public class MyService {
    private final AppProperties properties;
    
    public MyService(AppProperties properties) {
        this.properties = properties;
    }
    
    public void doSomething() {
        String dbUrl = properties.getDatabase().getUrl();
        // 使用配置
    }
}
```

### 3. 条件注解 (`org.springframework.boot.autoconfigure.condition`)

Spring Boot 提供了丰富的条件注解，用于根据条件启用配置。

#### 主要注解

| 注解 | 描述 | 示例 |
|------|------|------|
| `@ConditionalOnClass` | 当类存在时 | `@ConditionalOnClass(name = "com.mysql.jdbc.Driver")` |
| `@ConditionalOnMissingClass` | 当类不存在时 | `@ConditionalOnMissingClass("org.postgresql.Driver")` |
| `@ConditionalOnBean` | 当 Bean 存在时 | `@ConditionalOnBean(JdbcTemplate.class)` |
| `@ConditionalOnMissingBean` | 当 Bean 不存在时 | `@ConditionalOnMissingBean(DataSource.class)` |
| `@ConditionalOnProperty` | 当属性满足条件时 | `@ConditionalOnProperty(name = "app.feature.enabled", havingValue = "true")` |
| `@ConditionalOnExpression` | 当表达式为 true 时 | `@ConditionalOnExpression("${app.feature.enabled:false} and ${app.feature.version:1} >= 2")` |
| `@ConditionalOnResource` | 当资源存在时 | `@ConditionalOnResource(resources = "classpath:application-dev.properties")` |
| `@ConditionalOnWebApplication` | 当是 Web 应用时 | `@ConditionalOnWebApplication` |
| `@ConditionalOnNotWebApplication` | 当不是 Web 应用时 | `@ConditionalOnNotWebApplication` |

#### 使用示例

```java
// 当存在 JdbcTemplate Bean 时启用配置
@Configuration
@ConditionalOnBean(JdbcTemplate.class)
public class JdbcConfig {
    // 配置
}

// 当属性 app.feature.enabled 为 true 时启用配置
@Configuration
@ConditionalOnProperty(name = "app.feature.enabled", havingValue = "true")
public class FeatureConfig {
    // 配置
}
```

## 主类说明

### SpringDemoMain.java

示例代码文件：`SpringDemoMain.java`

该文件是一个包含 `main` 方法的主类，用于演示所有 Spring 工具类的示例代码。它按照以下顺序调用各个示例类的演示方法：

1. `SpringStringUtilsDemo.demonstrateStringUtils()` - 演示字符串工具类
2. `SpringCollectionUtilsDemo.demonstrateCollectionUtils()` - 演示集合工具类
3. `SpringObjectUtilsDemo.demonstrateObjectUtils()` - 演示对象工具类
4. `SpringReflectionUtilsDemo.demonstrateReflectionUtils()` - 演示反射工具类
5. `SpringAssertDemo.demonstrateAssert()` - 演示断言工具类
6. `SpringClassUtilsDemo.demonstrateClassUtils()` - 演示类工具类
7. `SpringFileCopyUtilsDemo.demonstrateFileCopyUtils()` - 演示文件工具类
8. `SpringResourceLoaderDemo.demonstrateResourceLoader()` - 演示资源加载工具类

运行该主类将输出所有工具类的演示结果，包括详细的操作过程和结果说明。

## 总结

Spring 框架提供了丰富的工具类，这些工具类简化了 Java 开发中的各种常见操作，提高了开发效率。本文档分析了 Spring 中最常用的工具类，包括：

1. **字符串工具类**：`StringUtils` - 提供丰富的字符串操作方法
2. **集合工具类**：`CollectionUtils` - 提供集合操作的工具方法
3. **对象工具类**：`ObjectUtils` - 提供对象操作的工具方法
4. **反射工具类**：`ReflectionUtils` - 简化反射代码的编写
5. **断言工具类**：`Assert` - 用于验证参数和状态的有效性
6. **类工具类**：`ClassUtils` - 提供对 Java 类操作的工具方法
7. **文件工具类**：`FileCopyUtils` - 提供文件复制的工具方法
8. **资源加载工具类**：`ResourceLoader` - 提供加载资源的能力

这些工具类不仅在 Spring 内部广泛使用，也可以在应用代码中直接使用，以简化代码编写，提高开发效率。

## 最佳实践

1. **合理使用工具类**：选择合适的工具类来简化代码，避免重复造轮子
2. **了解工具类的实现**：虽然工具类简化了代码，但了解其底层实现有助于更好地使用它们
3. **注意性能**：某些工具方法可能会有性能开销，如反射操作
4. **版本兼容性**：不同版本的 Spring 可能会有 API 变化，使用时要注意版本兼容性
5. **结合文档使用**：参考 Spring 官方文档和 Javadoc 来了解工具类的详细用法

通过合理使用 Spring 提供的工具类，可以大大简化 Java 开发工作，提高代码质量和开发效率。