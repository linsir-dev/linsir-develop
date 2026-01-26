# JUnit 5与JUnit 4的差别在哪里？

## 概述

JUnit 5是JUnit框架的下一代版本，与JUnit 4相比进行了全面的架构重构和功能增强。JUnit 5采用模块化设计，提供了更强大、更灵活的测试能力。本文将详细对比JUnit 5和JUnit 4的主要差异。

## 架构差异

### JUnit 4架构

JUnit 4是一个单一的、自包含的测试框架，所有功能都包含在一个JAR包中：

```
JUnit 4
├── Core
├── Runner
├── Assertions
├── Rules
└── ...
```

### JUnit 5架构

JUnit 5采用模块化设计，由三个独立的子项目组成：

```
JUnit 5
├── JUnit Platform (基础平台)
├── JUnit Jupiter (新编程模型)
└── JUnit Vintage (向后兼容)
```

**主要区别**：
- JUnit 4是单一JAR包，JUnit 5是多个模块的组合
- JUnit 5支持第三方测试引擎集成
- JUnit 5提供了更好的扩展性

## 包名差异

### JUnit 4

JUnit 4的所有类都在`org.junit`包下：

```java
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
```

### JUnit 5

JUnit 5的类在`org.junit.jupiter.api`包下：

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
```

**主要区别**：
- JUnit 5使用新的包名，避免与JUnit 4冲突
- JUnit 5的包名更加清晰，表明是Jupiter API

## 注解差异

### 测试方法注解

| JUnit 4 | JUnit 5 | 说明 |
|---------|---------|------|
| `@Test` | `@Test` | 标识测试方法 |
| `@Before` | `@BeforeEach` | 在每个测试方法之前执行 |
| `@After` | `@AfterEach` | 在每个测试方法之后执行 |
| `@BeforeClass` | `@BeforeAll` | 在所有测试方法之前执行 |
| `@AfterClass` | `@AfterAll` | 在所有测试方法之后执行 |
| `@Ignore` | `@Disabled` | 禁用测试 |
| `@RunWith` | `@ExtendWith` | 注册扩展 |

### JUnit 5新增注解

JUnit 5引入了许多新的注解：

| 注解 | 说明 |
|------|------|
| `@DisplayName` | 指定测试的显示名称 |
| `@Nested` | 标识嵌套测试类 |
| `@Tag` | 为测试添加标签 |
| `@ParameterizedTest` | 标识参数化测试 |
| `@RepeatedTest` | 标识重复测试 |
| `@TestFactory` | 标识动态测试工厂 |
| `@TestInstance` | 配置测试实例生命周期 |
| `@Timeout` | 为测试设置超时时间 |

### 示例对比

**JUnit 4示例**：

```java
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Ignore;

public class CalculatorTestJUnit4 {
    
    @BeforeClass
    public static void setUpBeforeClass() {
        System.out.println("Before all tests");
    }
    
    @Before
    public void setUp() {
        System.out.println("Before each test");
    }
    
    @Test
    public void testAdd() {
        Calculator calculator = new Calculator();
        assertEquals(5, calculator.add(2, 3));
    }
    
    @Ignore("Not ready yet")
    @Test
    public void testSubtract() {
        Calculator calculator = new Calculator();
        assertEquals(1, calculator.subtract(3, 2));
    }
    
    @After
    public void tearDown() {
        System.out.println("After each test");
    }
    
    @AfterClass
    public static void tearDownAfterClass() {
        System.out.println("After all tests");
    }
}
```

**JUnit 5示例**：

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Calculator Tests")
class CalculatorTestJUnit5 {
    
    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("Before all tests");
    }
    
    @BeforeEach
    void setUp() {
        System.out.println("Before each test");
    }
    
    @Test
    @DisplayName("Test addition")
    void testAdd() {
        Calculator calculator = new Calculator();
        assertEquals(5, calculator.add(2, 3));
    }
    
    @Disabled("Not ready yet")
    @Test
    @DisplayName("Test subtraction")
    void testSubtract() {
        Calculator calculator = new Calculator();
        assertEquals(1, calculator.subtract(3, 2));
    }
    
    @AfterEach
    void tearDown() {
        System.out.println("After each test");
    }
    
    @AfterAll
    static void tearDownAfterAll() {
        System.out.println("After all tests");
    }
}
```

## 断言差异

### JUnit 4断言

JUnit 4的断言方法都是静态方法，位于`Assert`类中：

```java
import static org.junit.Assert.*;

assertEquals(5, calculator.add(2, 3));
assertTrue(result > 0);
assertNull(object);
assertNotNull(object);
assertArrayEquals(expected, actual);
```

### JUnit 5断言

JUnit 5的断言方法位于`Assertions`类中，提供了更多功能：

```java
import static org.junit.jupiter.api.Assertions.*;

assertEquals(5, calculator.add(2, 3));
assertTrue(result > 0);
assertNull(object);
assertNotNull(object);
assertArrayEquals(expected, actual);

// JUnit 5新增的断言
assertAll("person",
    () -> assertEquals("John", person.getFirstName()),
    () -> assertEquals("Doe", person.getLastName())
);

assertThrows(IllegalArgumentException.class, () -> {
    calculator.divide(10, 0);
});

assertTimeout(Duration.ofSeconds(1), () -> {
    // 执行不超过1秒的任务
});
```

### 主要区别

1. **断言类名**：JUnit 4使用`Assert`，JUnit 5使用`Assertions`
2. **分组断言**：JUnit 5支持`assertAll()`，可以同时验证多个断言
3. **异常断言**：JUnit 5的`assertThrows()`比JUnit 4的`@Test(expected=...)`更灵活
4. **超时断言**：JUnit 5支持`assertTimeout()`，可以验证执行时间
5. **Lambda支持**：JUnit 5的断言消息可以使用Lambda表达式延迟计算

## 假设差异

### JUnit 4假设

JUnit 4的假设方法位于`Assume`类中：

```java
import static org.junit.Assume.*;

assumeTrue("CI".equals(System.getenv("ENV")));
assumeNotNull(obj);
assumeNoException(exception);
```

### JUnit 5假设

JUnit 5的假设方法位于`Assumptions`类中，提供了更多功能：

```java
import static org.junit.jupiter.api.Assumptions.*;

assumeTrue("CI".equals(System.getenv("ENV")));
assumeNotNull(obj);

// JUnit 5新增
assumingThat("DEV".equals(System.getenv("ENV")),
    () -> {
        // 仅在DEV环境中执行
    });
```

### 主要区别

1. **类名**：JUnit 4使用`Assume`，JUnit 5使用`Assumptions`
2. **assumingThat**：JUnit 5新增`assumingThat()`，可以在满足条件时执行特定代码块

## 测试生命周期差异

### JUnit 4生命周期

JUnit 4的测试生命周期相对简单：

- 每个测试方法都会创建一个新的测试类实例
- `@Before`和`@After`方法在每个测试方法前后执行
- `@BeforeClass`和`@AfterClass`方法在所有测试方法前后执行一次（静态方法）

### JUnit 5生命周期

JUnit 5提供了更灵活的测试生命周期配置：

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LifecycleTest {
    
    @BeforeAll
    void beforeAll() {
        // 可以是非静态方法
    }
    
    @AfterAll
    void afterAll() {
        // 可以是非静态方法
    }
}
```

### 主要区别

1. **实例生命周期**：JUnit 5支持`PER_CLASS`模式，所有测试方法共享同一个实例
2. **非静态方法**：在`PER_CLASS`模式下，`@BeforeAll`和`@AfterAll`可以是非静态方法
3. **测试顺序**：JUnit 5支持通过`@TestMethodOrder`指定测试方法的执行顺序

## 参数化测试差异

### JUnit 4参数化测试

JUnit 4需要使用`@RunWith(Parameterized.class)`来运行参数化测试：

```java
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ParameterizedTestJUnit4 {
    
    private int input;
    private int expected;
    
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {1, 2},
            {2, 4},
            {3, 6}
        });
    }
    
    public ParameterizedTestJUnit4(int input, int expected) {
        this.input = input;
        this.expected = expected;
    }
    
    @Test
    public void testMultiply() {
        assertEquals(expected, input * 2);
    }
}
```

### JUnit 5参数化测试

JUnit 5提供了更简洁、更强大的参数化测试：

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class ParameterizedTestJUnit5 {
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void testWithValueSource(int argument) {
        assertTrue(argument > 0);
    }
    
    @ParameterizedTest
    @CsvSource({"1, 2", "2, 4", "3, 6"})
    void testWithCsvSource(int input, int expected) {
        assertEquals(expected, input * 2);
    }
    
    @ParameterizedTest
    @MethodSource("provideTestData")
    void testWithMethodSource(int input, int expected) {
        assertEquals(expected, input * 2);
    }
    
    static Stream<Arguments> provideTestData() {
        return Stream.of(
            Arguments.of(1, 2),
            Arguments.of(2, 4),
            Arguments.of(3, 6)
        );
    }
}
```

### 主要区别

1. **注解**：JUnit 5使用`@ParameterizedTest`，不需要`@RunWith`
2. **数据源**：JUnit 5提供了多种数据源注解（`@ValueSource`、`@CsvSource`、`@MethodSource`等）
3. **灵活性**：JUnit 5的参数化测试更加灵活和强大

## 动态测试

### JUnit 4

JUnit 4不支持动态测试，所有测试用例必须在编译时确定。

### JUnit 5

JUnit 5支持动态测试，可以在运行时生成测试用例：

```java
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import java.util.stream.Stream;

class DynamicTestExample {
    
    @TestFactory
    Stream<DynamicTest> dynamicTests() {
        return Stream.of("apple", "banana", "orange")
            .map(fruit -> DynamicTest.dynamicTest("Test with " + fruit, () -> {
                assertNotNull(fruit);
                assertTrue(fruit.length() > 0);
            }));
    }
}
```

### 主要区别

- JUnit 5支持动态测试，JUnit 4不支持
- 动态测试允许在运行时根据条件生成测试用例

## 扩展模型差异

### JUnit 4扩展

JUnit 4使用`@RunWith`和`Rule`机制来扩展功能：

```java
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class RuleTestJUnit4 {
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    @Test
    public void testUsingTempFolder() throws IOException {
        File createdFile = folder.newFile("test.txt");
        assertTrue(createdFile.exists());
    }
}
```

### JUnit 5扩展

JUnit 5提供了更强大的扩展模型：

```java
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CustomExtension.class)
class ExtensionTestJUnit5 {
    
    @Test
    void testWithExtension() {
        // 测试代码
    }
}

// 自定义扩展
class CustomExtension implements BeforeAllCallback, AfterAllCallback {
    
    @Override
    public void beforeAll(ExtensionContext context) {
        System.out.println("Before all tests");
    }
    
    @Override
    public void afterAll(ExtensionContext context) {
        System.out.println("After all tests");
    }
}
```

### 主要区别

1. **扩展点**：JUnit 5提供了更多的扩展点（BeforeAllCallback、BeforeEachCallback等）
2. **组合性**：JUnit 5支持多个扩展的组合使用
3. **参数解析**：JUnit 5支持通过扩展解析测试方法的参数

## Java版本支持

### JUnit 4

- 支持Java 5及以上版本
- 不支持Java 8的新特性（Lambda表达式、Stream API等）

### JUnit 5

- 支持Java 8及以上版本
- 充分利用Java 8的新特性
- 更好的Lambda表达式支持

## 迁移指南

### 从JUnit 4迁移到JUnit 5

1. **更新依赖**：将JUnit 4依赖替换为JUnit 5依赖
2. **更新导入**：将`org.junit`替换为`org.junit.jupiter.api`
3. **更新注解**：
   - `@Before` → `@BeforeEach`
   - `@After` → `@AfterEach`
   - `@BeforeClass` → `@BeforeAll`
   - `@AfterClass` → `@AfterAll`
   - `@Ignore` → `@Disabled`
4. **更新断言**：将`Assert`替换为`Assertions`
5. **更新假设**：将`Assume`替换为`Assumptions`
6. **更新扩展**：将`@RunWith`和`Rule`替换为`@ExtendWith`

### 向后兼容

如果需要同时使用JUnit 4和JUnit 5，可以添加JUnit Vintage依赖：

```xml
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>
```

## 总结

JUnit 5相比JUnit 4进行了全面的改进和增强：

1. **架构**：模块化设计，更好的扩展性
2. **注解**：更丰富的注解，更清晰的命名
3. **断言**：更强大的断言功能，支持分组断言
4. **参数化测试**：更简洁、更灵活的参数化测试
5. **动态测试**：支持运行时生成测试用例
6. **扩展模型**：更强大的扩展能力
7. **Java 8支持**：充分利用Java 8的新特性

虽然JUnit 5提供了许多新功能，但JUnit 4仍然被广泛使用。对于新项目，建议使用JUnit 5；对于现有项目，可以根据实际情况逐步迁移。
