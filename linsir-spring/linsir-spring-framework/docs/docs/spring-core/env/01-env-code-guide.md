# 环境抽象模块代码说明文档

## 1. 代码结构

```
src/main/java/com/linsir/spring/framework/spring_core/env/
│
├── core/                           # 核心接口
│   ├── Environment.java            # 环境接口
│   └── ConfigurableEnvironment.java # 可配置环境接口
│
├── resolver/                       # 属性解析
│   └── PropertyResolver.java       # 属性解析器接口
│
├── source/                         # 属性源
│   ├── PropertySource.java         # 属性源抽象基类
│   ├── MapPropertySource.java      # Map属性源
│   ├── PropertiesPropertySource.java # Properties属性源
│   ├── SystemEnvironmentPropertySource.java # 系统环境变量
│   └── CommandLinePropertySource.java # 命令行参数
│
├── support/                        # 支持类
│   ├── MutablePropertySources.java # 可变属性源集合
│   ├── PropertySourcesPropertyResolver.java # 属性解析器实现
│   ├── AbstractEnvironment.java    # 环境抽象基类
│   └── StandardEnvironment.java    # 标准环境实现
│
└── profile/                        # Profile支持
    ├── Profiles.java               # Profile条件接口
    └── ProfileCondition.java       # Profile条件工具
```

## 2. 核心接口详解

### 2.1 PropertySource - 属性源抽象

**文件**: `env/source/PropertySource.java`

**职责**: 定义属性源的统一接口，所有具体属性源都继承此类。

**核心方法**:

```java
// 获取属性源名称
public String getName()

// 获取实际的属性源对象
public T getSource()

// 判断是否包含指定属性
public abstract boolean containsProperty(String name)

// 获取属性值
public abstract Object getProperty(String name)

// 获取属性值并转换为指定类型
public <E> E getProperty(String name, Class<E> targetType)
```

**设计要点**:
- 使用泛型 `<T>` 允许不同类型的属性源
- 强制子类实现 `containsProperty` 和 `getProperty`
- 提供类型安全的属性获取方法

### 2.2 PropertyResolver - 属性解析器

**文件**: `env/resolver/PropertyResolver.java`

**职责**: 提供属性解析的核心能力，包括占位符解析。

**核心方法**:

```java
// 判断是否包含属性
boolean containsProperty(String key)

// 获取属性值
String getProperty(String key)
String getProperty(String key, String defaultValue)
<T> T getProperty(String key, Class<T> targetType)
<T> T getProperty(String key, Class<T> targetType, T defaultValue)

// 获取必需属性
String getRequiredProperty(String key)
<T> T getRequiredProperty(String key, Class<T> targetType)

// 解析占位符
String resolvePlaceholders(String text)
String resolveRequiredPlaceholders(String text)
```

**设计要点**:
- 支持默认值机制
- 支持类型转换
- 支持占位符解析 `${...}`

### 2.3 Environment - 环境接口

**文件**: `env/core/Environment.java`

**职责**: 整合属性解析和 Profile 管理。

**核心方法**:

```java
// Profile 管理
String[] getActiveProfiles()
String[] getDefaultProfiles()
boolean acceptsProfiles(String profile)
boolean acceptsProfiles(String... profiles)
```

**设计要点**:
- 继承 PropertyResolver，具备属性解析能力
- 提供 Profile 相关的查询方法
- 支持 Profile 表达式（否定、或、与）

### 2.4 ConfigurableEnvironment - 可配置环境

**文件**: `env/core/ConfigurableEnvironment.java`

**职责**: 扩展 Environment，提供配置能力。

**核心方法**:

```java
// Profile 配置
void setActiveProfiles(String... profiles)
void addActiveProfile(String profile)
void setDefaultProfiles(String... profiles)

// 属性源管理
MutablePropertySources getPropertySources()
void addPropertySource(PropertySource<?> propertySource)

// 环境合并
void merge(ConfigurableEnvironment environment)
```

## 3. 具体实现详解

### 3.1 MapPropertySource

**文件**: `env/source/MapPropertySource.java`

**功能**: 基于 Map 的属性源实现。

**使用场景**:
- 从配置文件加载属性
- 内存中的配置管理
- 动态配置修改

**示例**:

```java
Map<String, Object> map = new HashMap<>();
map.put("app.name", "MyApp");
map.put("app.port", 8080);

MapPropertySource source = new MapPropertySource("appConfig", map);
String name = source.getProperty("app.name", String.class); // "MyApp"
```

### 3.2 SystemEnvironmentPropertySource

**文件**: `env/source/SystemEnvironmentPropertySource.java`

**功能**: 系统环境变量属性源，支持命名风格转换。

**命名转换**:
- `spring.profiles.active` → `SPRING_PROFILES_ACTIVE`
- `db.url` → `DB_URL`

**示例**:

```java
// 环境变量: SPRING_PROFILES_ACTIVE=dev
SystemEnvironmentPropertySource source = 
    new SystemEnvironmentPropertySource(System.getenv());

// 两种写法都可以获取到值
source.getProperty("spring.profiles.active"); // "dev"
source.getProperty("SPRING_PROFILES_ACTIVE"); // "dev"
```

### 3.3 CommandLinePropertySource

**文件**: `env/source/CommandLinePropertySource.java`

**功能**: 命令行参数属性源。

**支持的格式**:
- `--key=value`
- `--key value`
- `--flag` (布尔标志)

**示例**:

```java
String[] args = {"--server.port=8080", "--debug"};
CommandLinePropertySource source = new CommandLinePropertySource(args);

source.getProperty("server.port"); // "8080"
source.getProperty("debug");       // "true"
```

### 3.4 MutablePropertySources

**文件**: `env/support/MutablePropertySources.java`

**功能**: 管理多个属性源，支持优先级排序。

**核心操作**:

```java
MutablePropertySources sources = new MutablePropertySources();

// 添加到开头（高优先级）
sources.addFirst(highPrioritySource);

// 添加到末尾（低优先级）
sources.addLast(lowPrioritySource);

// 在指定源之前/之后添加
sources.addBefore("existingSource", newSource);
sources.addAfter("existingSource", newSource);

// 替换和移除
sources.replace("sourceName", newSource);
sources.remove("sourceName");
```

**优先级规则**:
- 先添加的属性源优先级高
- 遍历时按添加顺序查找
- 找到第一个匹配即返回

### 3.5 PropertySourcesPropertyResolver

**文件**: `env/support/PropertySourcesPropertyResolver.java`

**功能**: 基于 PropertySources 的属性解析器实现。

**占位符解析**:

```java
// 简单占位符
resolver.resolvePlaceholders("${app.name}");

// 带默认值
resolver.resolvePlaceholders("${app.name:DefaultApp}");

// 嵌套占位符
resolver.resolvePlaceholders("${app.fullname:${app.name}-v${app.version}}");
```

**类型转换**:

```java
// 自动类型转换
Integer port = resolver.getProperty("app.port", Integer.class);
Boolean debug = resolver.getProperty("app.debug", Boolean.class);
```

### 3.6 AbstractEnvironment

**文件**: `env/support/AbstractEnvironment.java`

**功能**: 环境抽象基类，提供通用实现。

**默认属性源**:
- 系统环境变量 (`systemEnvironment`)
- 系统属性 (`systemProperties`)

**Profile 处理**:

```java
// 设置激活的 Profile
environment.setActiveProfiles("dev", "test");

// 添加激活的 Profile
environment.addActiveProfile("local");

// 判断 Profile 是否激活
environment.acceptsProfiles("dev");      // true
environment.acceptsProfiles("!prod");    // true（否定）
environment.acceptsProfiles("dev|prod"); // true（或）
```

### 3.7 StandardEnvironment

**文件**: `env/support/StandardEnvironment.java`

**功能**: 标准环境实现，适用于非 Web 应用。

**使用示例**:

```java
StandardEnvironment env = new StandardEnvironment();

// 添加命令行参数
env.addCommandLineArgs(args);

// 添加自定义属性源
Map<String, Object> map = new HashMap<>();
map.put("custom.key", "value");
env.addPropertySource(new MapPropertySource("custom", map));

// 获取属性
String value = env.getProperty("custom.key");
```

### 3.8 Profiles

**文件**: `env/profile/Profiles.java`

**功能**: Profile 条件接口，支持复杂表达式。

**表达式语法**:

```java
// 简单匹配
Profiles.parse("dev")

// 否定
Profiles.parse("!prod")

// 或（任一匹配）
Profiles.parse("dev | test")

// 与（全部匹配）
Profiles.parse("dev & local")

// 复杂表达式
Profiles.parse("(dev & test) | prod")
```

**使用方式**:

```java
Profiles profiles = Profiles.parse("dev | test");
boolean matches = profiles.matches(environment);
```

### 3.9 ProfileCondition

**文件**: `env/profile/ProfileCondition.java`

**功能**: Profile 条件工具类，提供便捷方法。

```java
// 判断特定 Profile
ProfileCondition.isActive(environment, "dev");
ProfileCondition.isDev(environment);
ProfileCondition.isTest(environment);
ProfileCondition.isProd(environment);
ProfileCondition.isStaging(environment);

// 判断是否有激活的 Profile
ProfileCondition.isNoProfileActive(environment);
```

## 4. 使用示例

### 4.1 基础使用

```java
// 创建环境
StandardEnvironment env = new StandardEnvironment();

// 添加属性源
Map<String, Object> appConfig = new HashMap<>();
appConfig.put("app.name", "MyApplication");
appConfig.put("app.version", "1.0.0");
appConfig.put("server.port", "8080");

env.addPropertySource(new MapPropertySource("appConfig", appConfig));

// 获取属性
String appName = env.getProperty("app.name");
Integer port = env.getProperty("server.port", Integer.class);
```

### 4.2 Profile 管理

```java
// 设置 Profile
env.setActiveProfiles("dev");

// 条件配置
if (env.acceptsProfiles("dev")) {
    // 开发环境配置
} else if (env.acceptsProfiles("prod")) {
    // 生产环境配置
}

// 使用 Profiles 表达式
Profiles profiles = Profiles.parse("dev | test");
if (profiles.matches(env)) {
    // 开发或测试环境
}
```

### 4.3 占位符解析

```java
// 配置属性
Map<String, Object> config = new HashMap<>();
config.put("db.host", "localhost");
config.put("db.port", "3306");
config.put("db.url", "jdbc:mysql://${db.host}:${db.port}/mydb");

env.addPropertySource(new MapPropertySource("dbConfig", config));

// 解析占位符
String url = env.resolvePlaceholders("${db.url}");
// 结果: jdbc:mysql://localhost:3306/mydb
```

### 4.4 属性源优先级

```java
MutablePropertySources sources = new MutablePropertySources();

// 命令行参数（最高优先级）
Map<String, Object> cmdArgs = new HashMap<>();
cmdArgs.put("server.port", "9090");
sources.addFirst(new MapPropertySource("commandLineArgs", cmdArgs));

// 配置文件
Map<String, Object> fileConfig = new HashMap<>();
fileConfig.put("server.port", "8080");
fileConfig.put("app.name", "MyApp");
sources.addLast(new MapPropertySource("fileConfig", fileConfig));

// 创建解析器
PropertyResolver resolver = new PropertySourcesPropertyResolver(sources);

// 高优先级覆盖低优先级
resolver.getProperty("server.port"); // "9090"
resolver.getProperty("app.name");    // "MyApp"
```

## 5. 设计亮点

### 5.1 职责分离

- **PropertySource**: 负责属性存储
- **PropertyResolver**: 负责属性解析
- **Environment**: 负责环境管理
- **Profiles**: 负责条件判断

### 5.2 扩展性

- 通过继承 `PropertySource` 可添加新的属性源类型
- 通过实现 `PropertyResolver` 可自定义解析逻辑
- 通过继承 `AbstractEnvironment` 可创建特定环境

### 5.3 优先级机制

- 明确的属性源优先级规则
- 支持动态调整优先级
- 符合 Spring 的配置优先级约定

### 5.4 类型安全

- 泛型支持
- 自动类型转换
- 编译时类型检查

## 6. 注意事项

1. **属性源名称唯一**: 同名属性源会被替换
2. **线程安全**: MutablePropertySources 使用 CopyOnWriteArrayList
3. **空值处理**: 注意处理 null 值和空字符串
4. **类型转换**: 确保属性值可以转换为目标类型
5. **循环引用**: 占位符解析会检测循环引用并抛出异常
