# 环境抽象模块测试说明文档

## 1. 测试概述

本文档详细说明环境抽象模块的测试代码结构、测试用例设计和运行方法。

### 1.1 测试目标

- 验证 PropertySource 各种实现的正确性
- 验证 MutablePropertySources 的优先级管理
- 验证 PropertyResolver 的占位符解析
- 验证 Environment 的 Profile 管理
- 验证 Profiles 条件表达式的解析

### 1.2 测试统计

| 测试类 | 测试方法 | 说明 |
|--------|----------|------|
| PropertySourceTest | 9 | 属性源实现测试 |
| MutablePropertySourcesTest | 14 | 属性源集合测试 |
| PropertyResolverTest | 21 | 属性解析器测试 |
| EnvironmentTest | 14 | 环境接口测试 |
| ProfilesTest | 12 | Profile 条件测试 |
| **总计** | **70** | |

## 2. 测试结构

```
src/test/java/com/linsir/spring/framework/spring_core/env/
│
├── source/
│   └── PropertySourceTest.java          # 属性源测试
│
├── support/
│   └── MutablePropertySourcesTest.java  # 属性源集合测试
│
├── resolver/
│   └── PropertyResolverTest.java        # 属性解析器测试
│
├── core/
│   └── EnvironmentTest.java             # 环境测试
│
└── profile/
    └── ProfilesTest.java                # Profile 测试
```

## 3. PropertySourceTest 详解

### 3.1 测试类结构

```java
@Test
void testMapPropertySource() {
    // 测试 MapPropertySource 的基本功能
}

@Test
void testMapPropertySourceModify() {
    // 测试 MapPropertySource 的修改操作
}

@Test
void testPropertiesPropertySource() {
    // 测试 PropertiesPropertySource
}

@Test
void testSystemEnvironmentPropertySource() {
    // 测试 SystemEnvironmentPropertySource
}

@Test
void testCommandLinePropertySource() {
    // 测试 CommandLinePropertySource
}
```

### 3.2 核心测试用例

| 测试方法 | 测试内容 | 预期结果 |
|----------|----------|----------|
| testMapPropertySource | MapPropertySource 基本功能 | 正确获取属性值 |
| testMapPropertySourceModify | setProperty/removeProperty | 属性修改成功 |
| testPropertiesPropertySource | PropertiesPropertySource | 正确读取 Properties |
| testSystemEnvironmentPropertySource | 环境变量命名转换 | spring.profiles.active ↔ SPRING_PROFILES_ACTIVE |
| testCommandLinePropertySource | 命令行参数解析 | --key=value 格式正确解析 |
| testCommandLinePropertySourceWithSpace | 空格分隔格式 | --key value 格式正确解析 |
| testPropertySourceEquality | 属性源相等性 | 同名属性源相等 |
| testPropertySourceToString | toString 方法 | 包含名称和类型信息 |
| testPropertySourceNullValidation | 空值校验 | 抛出 IllegalArgumentException |

### 3.3 测试示例

```java
@Test
void testSystemEnvironmentPropertySource() {
    // 准备测试数据
    Map<String, Object> env = new HashMap<>();
    env.put("SPRING_PROFILES_ACTIVE", "dev");

    // 创建 SystemEnvironmentPropertySource
    SystemEnvironmentPropertySource source = 
        new SystemEnvironmentPropertySource(env);

    // 验证转换后的匹配
    assertTrue(source.containsProperty("spring.profiles.active"));
    assertEquals("dev", source.getProperty("spring.profiles.active"));
}
```

## 4. MutablePropertySourcesTest 详解

### 4.1 核心测试用例

| 测试方法 | 测试内容 | 预期结果 |
|----------|----------|----------|
| testAddFirst | 添加到开头 | 优先级最高 |
| testAddLast | 添加到末尾 | 优先级最低 |
| testAddBefore | 在指定源之前添加 | 位置正确 |
| testAddAfter | 在指定源之后添加 | 位置正确 |
| testGetByName | 按名称获取 | 返回正确属性源 |
| testContains | 判断是否包含 | 返回正确布尔值 |
| testRemove | 移除属性源 | 移除成功 |
| testReplace | 替换属性源 | 替换成功 |
| testDuplicateNameHandling | 重复名称处理 | 新源替换旧源 |
| testIsEmpty | 判断是否为空 | 返回正确布尔值 |
| testGetPropertySourceNames | 获取名称列表 | 返回所有名称 |
| testIterator | 迭代器 | 正确遍历所有源 |
| testCopyConstructor | 拷贝构造 | 正确复制所有源 |
| testNullValidation | 空值校验 | 抛出异常 |

### 4.2 优先级测试示例

```java
@Test
void testAddFirst() {
    // 创建两个属性源
    MapPropertySource source1 = new MapPropertySource("source1", map1);
    MapPropertySource source2 = new MapPropertySource("source2", map2);

    // 先添加 source1
    propertySources.addLast(source1);
    
    // 在开头添加 source2（更高优先级）
    propertySources.addFirst(source2);

    // 验证顺序
    assertSame(source2, propertySources.get(0)); // 高优先级
    assertSame(source1, propertySources.get(1)); // 低优先级
}
```

## 5. PropertyResolverTest 详解

### 5.1 核心测试用例

| 测试方法 | 测试内容 | 预期结果 |
|----------|----------|----------|
| testContainsProperty | 判断属性存在 | 返回正确布尔值 |
| testGetProperty | 获取属性值 | 返回正确值或 null |
| testGetPropertyWithDefault | 带默认值 | 不存在时返回默认值 |
| testGetPropertyWithType | 类型转换 | 正确转换为指定类型 |
| testGetPropertyWithTypeAndDefault | 类型转换+默认值 | 正确转换或返回默认值 |
| testGetRequiredProperty | 必需属性 | 不存在时抛出异常 |
| testResolvePlaceholders | 占位符解析 | 正确替换占位符 |
| testResolvePlaceholdersWithDefault | 带默认值的占位符 | 使用默认值 |
| testResolvePlaceholdersNested | 嵌套占位符 | 递归解析 |
| testResolvePlaceholdersRecursive | 递归占位符 | 多级解析 |
| testResolveRequiredPlaceholders | 必需占位符 | 无法解析时抛出异常 |
| testPropertySourcePriority | 属性源优先级 | 高优先级覆盖低优先级 |
| testNullInput | 空输入 | 正确处理 null |
| testEmptyPropertySources | 空属性源 | 返回 null 或 false |
| testTypeConversion | 类型转换 | int/long/boolean/double |
| testTypeConversionFailure | 类型转换失败 | 抛出异常 |
| testCircularPlaceholderReference | 循环引用 | 抛出异常 |

### 5.2 占位符解析测试

```java
@Test
void testResolvePlaceholders() {
    // 配置属性
    map.put("app.name", "TestApp");
    map.put("app.port", "8080");

    // 简单占位符
    String result = resolver.resolvePlaceholders("${app.name}");
    assertEquals("TestApp", result);

    // 带默认值的占位符
    result = resolver.resolvePlaceholders("${app.nonexistent:default}");
    assertEquals("default", result);

    // 嵌套占位符
    map.put("app.fullname", "${app.name}-v${app.version}");
    map.put("app.version", "1.0");
    result = resolver.resolvePlaceholders("${app.fullname}");
    assertEquals("TestApp-v1.0", result);
}
```

### 5.3 类型转换测试

```java
@Test
void testTypeConversion() {
    map.put("app.port", "8080");
    map.put("app.debug", "true");

    // Integer 转换
    Integer port = resolver.getProperty("app.port", Integer.class);
    assertEquals(8080, port);

    // Boolean 转换
    Boolean debug = resolver.getProperty("app.debug", Boolean.class);
    assertTrue(debug);

    // Long 转换
    Long portLong = resolver.getProperty("app.port", Long.class);
    assertEquals(8080L, portLong);
}
```

## 6. EnvironmentTest 详解

### 6.1 核心测试用例

| 测试方法 | 测试内容 | 预期结果 |
|----------|----------|----------|
| testGetActiveProfiles | 获取激活的 Profile | 返回设置的 Profile |
| testGetDefaultProfiles | 获取默认 Profile | 返回默认或设置的 Profile |
| testAcceptsProfiles | 判断 Profile 激活 | 正确判断各种表达式 |
| testAcceptsProfilesMultiple | 多个 Profile | 或关系判断 |
| testAcceptsProfilesWithNoActiveProfiles | 无激活 Profile | 使用默认 Profile |
| testAddActiveProfile | 添加激活 Profile | 添加成功 |
| testPropertySources | 属性源集合 | 不为空 |
| testAddPropertySource | 添加属性源 | 添加成功 |
| testMerge | 合并环境 | 属性源和 Profile 都合并 |
| testPropertyResolution | 属性解析 | 正确解析属性 |
| testResolvePlaceholders | 占位符解析 | 正确解析 |
| testActiveProfilesFromProperty | 从属性读取 Profile | 正确读取 spring.profiles.active |
| testNullProfile | null Profile | 返回 false |
| testToString | toString | 包含环境信息 |

### 6.2 Profile 管理测试

```java
@Test
void testAcceptsProfiles() {
    // 设置激活的 Profile
    environment.setActiveProfiles("dev");

    // 验证激活的 Profile
    assertTrue(environment.acceptsProfiles("dev"));
    assertFalse(environment.acceptsProfiles("prod"));

    // 验证否定表达式
    assertTrue(environment.acceptsProfiles("!prod"));
    assertFalse(environment.acceptsProfiles("!dev"));
}

@Test
void testAcceptsProfilesMultiple() {
    environment.setActiveProfiles("dev", "test");

    // 或关系
    assertTrue(environment.acceptsProfiles("dev", "prod"));
    assertFalse(environment.acceptsProfiles("prod", "staging"));
}
```

### 6.3 环境合并测试

```java
@Test
void testMerge() {
    // 创建另一个环境
    StandardEnvironment other = new StandardEnvironment();
    other.setActiveProfiles("staging");
    
    Map<String, Object> map = new HashMap<>();
    map.put("other.key", "othervalue");
    other.addPropertySource(new MapPropertySource("otherSource", map));

    // 合并环境
    environment.merge(other);

    // 验证属性源被合并
    assertEquals("othervalue", environment.getProperty("other.key"));

    // 验证 Profile 被合并
    assertTrue(environment.acceptsProfiles("staging"));
}
```

## 7. ProfilesTest 详解

### 7.1 核心测试用例

| 测试方法 | 测试内容 | 预期结果 |
|----------|----------|----------|
| testParseSimpleProfile | 简单 Profile | 正确匹配 |
| testParseNegatedProfile | 否定表达式 | !prod 正确判断 |
| testParseOrExpression | 或表达式 | dev \| prod 正确判断 |
| testParseAndExpression | 与表达式 | dev & test 正确判断 |
| testParseComplexExpression | 复杂表达式 | (dev & test) \| prod |
| testParseNullOrEmpty | null/空字符串 | 返回 false |
| testOf | Profiles.of() | 任一匹配 |
| testAllOf | Profiles.allOf() | 全部匹配 |
| testProfileConditionIsActive | isActive | 正确判断 |
| testProfileConditionIsAnyActive | isAnyActive | 任一激活 |
| testProfileConditionIsNoProfileActive | isNoProfileActive | 无激活 Profile |
| testProfileConditionEnvironmentChecks | 环境判断 | dev/test/prod/staging |

### 7.2 Profile 表达式测试

```java
@Test
void testParseOrExpression() {
    Profiles profiles = Profiles.parse("dev | prod");
    
    environment.setActiveProfiles("dev");
    assertTrue(profiles.matches(environment)); // dev 激活
    
    environment.setActiveProfiles("prod");
    assertTrue(profiles.matches(environment)); // prod 激活
    
    environment.setActiveProfiles("staging");
    assertFalse(profiles.matches(environment)); // 都不激活
}

@Test
void testParseAndExpression() {
    environment.setActiveProfiles("dev", "test", "local");
    
    Profiles profiles = Profiles.parse("dev & test");
    assertTrue(profiles.matches(environment)); // 都激活
    
    profiles = Profiles.parse("dev & prod");
    assertFalse(profiles.matches(environment)); // prod 未激活
}
```

### 7.3 环境判断测试

```java
@Test
void testProfileConditionEnvironmentChecks() {
    // 开发环境
    environment.setActiveProfiles("dev");
    assertTrue(ProfileCondition.isDev(environment));
    assertFalse(ProfileCondition.isProd(environment));

    // 生产环境
    environment.setActiveProfiles("prod");
    assertFalse(ProfileCondition.isDev(environment));
    assertTrue(ProfileCondition.isProd(environment));

    // 别名支持
    environment.setActiveProfiles("development");
    assertTrue(ProfileCondition.isDev(environment));
    
    environment.setActiveProfiles("production");
    assertTrue(ProfileCondition.isProd(environment));
}
```

## 8. 运行测试

### 8.1 运行所有环境测试

```bash
mvn test -Dtest="PropertySourceTest,MutablePropertySourcesTest,PropertyResolverTest,EnvironmentTest,ProfilesTest"
```

### 8.2 运行单个测试类

```bash
mvn test -Dtest=PropertySourceTest
mvn test -Dtest=EnvironmentTest
```

### 8.3 运行单个测试方法

```bash
mvn test -Dtest=PropertyResolverTest#testResolvePlaceholders
```

### 8.4 生成测试报告

```bash
mvn test -Dtest="*env*" -Dsurefire.useFile=false
```

## 9. 测试覆盖分析

### 9.1 覆盖范围

| 组件 | 测试覆盖 |
|------|----------|
| PropertySource 继承体系 | ✅ 全部覆盖 |
| MutablePropertySources 操作 | ✅ 全部覆盖 |
| PropertyResolver 功能 | ✅ 全部覆盖 |
| Environment Profile 管理 | ✅ 全部覆盖 |
| Profiles 表达式解析 | ✅ 全部覆盖 |

### 9.2 边界条件

- ✅ 空值处理
- ✅ 空字符串处理
- ✅ 类型转换失败
- ✅ 循环引用检测
- ✅ 属性源优先级
- ✅ Profile 表达式边界

## 10. 扩展测试建议

1. **并发测试**: 测试 MutablePropertySources 的线程安全性
2. **性能测试**: 测试大量属性源时的性能表现
3. **集成测试**: 测试与 ConversionService 的集成
4. **边界测试**: 更多边界条件测试
5. **异常测试**: 更多异常情况测试
