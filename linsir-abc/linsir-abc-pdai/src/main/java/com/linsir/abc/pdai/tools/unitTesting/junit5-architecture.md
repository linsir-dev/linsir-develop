# JUnit 5整体架构

## JUnit 5简介

JUnit 5是Java平台上最流行的单元测试框架JUnit的最新版本，于2017年正式发布。与JUnit 4相比，JUnit 5进行了全面的架构重构，采用模块化设计，提供了更强大、更灵活的测试能力。

JUnit 5由三个不同的子项目组成：

1. **JUnit Platform**：用于在JVM上启动测试框架的基础平台
2. **JUnit Jupiter**：用于在JUnit 5中编写测试的新编程模型和扩展模型
3. **JUnit Vintage**：用于在JUnit 5平台上运行基于JUnit 3和JUnit 4的测试

## JUnit 5架构图

```
┌─────────────────────────────────────────────────────────────┐
│                         JUnit 5                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐│
│  │  JUnit Platform  │  │  JUnit Jupiter   │  │JUnit Vintage ││
│  │                  │  │                  │  │              ││
│  │  - Test Engine   │  │  - Jupiter API   │  │  - JUnit 3   ││
│  │  - Launcher API  │  │  - Assertions    │  │  - JUnit 4   ││
│  │  - Console       │  │  - Assumptions   │  │  - Runner    ││
│  │    Launcher      │  │  - Annotations   │  │              ││
│  │  - JUnit Suite   │  │  - Test Instance │  │              ││
│  │    Engine        │  │  - Test Lifecycle│  │              ││
│  │                  │  │  - Parameterized │  │              ││
│  │                  │  │    Tests         │  │              ││
│  └──────────────────┘  └──────────────────┘  └──────────────┘│
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## JUnit Platform

JUnit Platform是JUnit 5的基础平台，提供了在JVM上启动测试框架的核心功能。它定义了TestEngine API，使得第三方测试库或测试框架可以集成到JUnit平台上。

### 主要组件

#### 1. Test Engine API

Test Engine API是JUnit Platform的核心接口，定义了测试引擎需要实现的基本功能：

```java
public interface TestEngine {
    String getId();
    TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId);
    void execute(ExecutionRequest request);
}
```

#### 2. Launcher API

Launcher API提供了编程方式来发现和执行测试：

```java
LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
    .selectors(selectPackage("com.example.tests"))
    .filters(includeClassNamePatterns(".*Test"))
    .build();

Launcher launcher = LauncherFactory.create();
launcher.execute(request);
```

#### 3. Console Launcher

Console Launcher是JUnit Platform提供的命令行工具，用于从命令行执行测试：

```bash
java -jar junit-platform-console-standalone.jar \
  --scan-class-path \
  --include-class-name ".*Test"
```

#### 4. JUnit Suite Engine

JUnit Suite Engine允许将多个测试类组合成一个测试套件：

```java
@Suite
@SelectPackages("com.example.tests")
public class AllTests {
}
```

## JUnit Jupiter

JUnit Jupiter是JUnit 5的核心测试框架，提供了编写测试的新编程模型和扩展模型。

### 主要组件

#### 1. Jupiter API

Jupiter API是编写JUnit 5测试的主要API，位于`org.junit.jupiter.api`包中。

#### 2. 注解（Annotations）

JUnit 5提供了丰富的注解来控制测试的执行：

| 注解 | 描述 |
|------|------|
| `@Test` | 标识测试方法 |
| `@ParameterizedTest` | 标识参数化测试 |
| `@RepeatedTest` | 标识重复测试 |
| `@TestFactory` | 标识动态测试工厂方法 |
| `@TestTemplate` | 标识测试模板方法 |
| `@TestClassOrder` | 指定测试类的执行顺序 |
| `@TestMethodOrder` | 指定测试方法的执行顺序 |
| `@TestInstance` | 配置测试实例的生命周期 |
| `@DisplayName` | 指定测试的显示名称 |
| `@DisplayNameGeneration` | 配置显示名称的生成策略 |
| `@BeforeEach` | 在每个测试方法之前执行 |
| `@AfterEach` | 在每个测试方法之后执行 |
| `@BeforeAll` | 在所有测试方法之前执行（静态方法） |
| `@AfterAll` | 在所有测试方法之后执行（静态方法） |
| `@Nested` | 标识嵌套测试类 |
| `@Tag` | 为测试或测试类添加标签 |
| `@Disabled` | 禁用测试或测试类 |
| `@Timeout` | 为测试设置超时时间 |
| `@ExtendWith` | 注册自定义扩展 |

#### 3. 断言（Assertions）

JUnit 5提供了丰富的断言方法：

```java
import static org.junit.jupiter.api.Assertions.*;

class AssertionsDemo {
    
    @Test
    void standardAssertions() {
        assertEquals(2, 2);
        assertEquals(4, 4, "The optional assertion message is now the last parameter.");
        assertTrue('a' < 'b', () -> "Assertion messages can be lazily evaluated.");
    }
    
    @Test
    void groupedAssertions() {
        assertAll("person",
            () -> assertEquals("John", person.getFirstName()),
            () -> assertEquals("Doe", person.getLastName())
        );
    }
    
    @Test
    void exceptionTesting() {
        Exception exception = assertThrows(ArithmeticException.class, () -> {
            int i = 1 / 0;
        });
        assertEquals("/ by zero", exception.getMessage());
    }
    
    @Test
    void timeoutNotExceeded() {
        assertTimeout(Duration.ofSeconds(1), () -> {
            // 执行不超过1秒的任务
        });
    }
}
```

#### 4. 假设（Assumptions）

假设用于在满足特定条件时才执行测试：

```java
import static org.junit.jupiter.api.Assumptions.*;

class AssumptionsDemo {
    
    @Test
    void testOnlyOnCiServer() {
        assumeTrue("CI".equals(System.getenv("ENV")));
    }
    
    @Test
    void testInDevelopmentEnvironment() {
        assumingThat("DEV".equals(System.getenv("ENV")),
            () -> {
                // 仅在DEV环境中执行
            });
    }
}
```

#### 5. 测试生命周期（Test Lifecycle）

JUnit 5提供了灵活的测试生命周期管理：

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LifecycleDemo {
    
    @BeforeAll
    static void beforeAll() {
        System.out.println("Before all tests");
    }
    
    @BeforeEach
    void beforeEach() {
        System.out.println("Before each test");
    }
    
    @Test
    void test1() {
        System.out.println("Test 1");
    }
    
    @Test
    void test2() {
        System.out.println("Test 2");
    }
    
    @AfterEach
    void afterEach() {
        System.out.println("After each test");
    }
    
    @AfterAll
    static void afterAll() {
        System.out.println("After all tests");
    }
}
```

#### 6. 参数化测试（Parameterized Tests）

JUnit 5提供了强大的参数化测试功能：

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

class ParameterizedTestDemo {
    
    @ParameterizedTest
    @ValueSource(strings = {"hello", "world"})
    void testWithValueSource(String word) {
        assertNotNull(word);
    }
    
    @ParameterizedTest
    @CsvSource({"apple, 10", "banana, 20", "orange, 30"})
    void testWithCsvSource(String fruit, int quantity) {
        assertNotNull(fruit);
        assertTrue(quantity > 0);
    }
    
    @ParameterizedTest
    @MethodSource("provideStringsForTest")
    void testWithMethodSource(String argument) {
        assertNotNull(argument);
    }
    
    static Stream<String> provideStringsForTest() {
        return Stream.of("foo", "bar");
    }
}
```

#### 7. 动态测试（Dynamic Tests）

动态测试允许在运行时生成测试用例：

```java
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

class DynamicTestsDemo {
    
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromStream() {
        return Stream.of("apple", "banana", "orange")
            .map(fruit -> DynamicTest.dynamicTest("Test with " + fruit, () -> {
                assertNotNull(fruit);
                assertTrue(fruit.length() > 0);
            }));
    }
}
```

#### 8. 扩展模型（Extension Model）

JUnit 5的扩展模型提供了强大的扩展能力，可以自定义测试行为：

```java
import org.junit.jupiter.api.extension.*;

class CustomExtension implements BeforeAllCallback, AfterAllCallback, 
    BeforeEachCallback, AfterEachCallback, ParameterResolver {
    
    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        System.out.println("Before all tests");
    }
    
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        System.out.println("After all tests");
    }
    
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        System.out.println("Before each test");
    }
    
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        System.out.println("After each test");
    }
    
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, 
        ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == CustomService.class;
    }
    
    @Override
    public Object resolveParameter(ParameterContext parameterContext, 
        ExtensionContext extensionContext) throws ParameterResolutionException {
        return new CustomService();
    }
}

@ExtendWith(CustomExtension.class)
class TestWithCustomExtension {
    
    @Test
    void testWithExtension(CustomService service) {
        assertNotNull(service);
    }
}
```

## JUnit Vintage

JUnit Vintage提供了在JUnit 5平台上运行基于JUnit 3和JUnit 4的测试的能力。这使得现有的JUnit 3和JUnit 4测试可以在JUnit 5环境中继续运行，无需修改代码。

### 使用Vintage

要使用JUnit Vintage，需要在项目中添加依赖：

```xml
<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>
```

然后，JUnit 3和JUnit 4的测试就可以在JUnit 5平台上运行了。

## JUnit 5的依赖配置

### Maven配置

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-engine</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <version>5.9.2</version>
    <scope>test</scope>
</dependency>
```

### Gradle配置

```groovy
test {
    useJUnitPlatform()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.2'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.2'
    testImplementation 'org.junit.jupiter:junit-jupiter-params:5.9.2'
}
```

## JUnit 5的优势

1. **模块化设计**：JUnit 5采用模块化设计，使得各个组件可以独立发展和演进
2. **扩展性强**：新的扩展模型提供了强大的扩展能力
3. **支持Java 8+**：充分利用Java 8的新特性，如Lambda表达式、Stream API等
4. **更好的参数化测试**：提供了更强大、更灵活的参数化测试功能
5. **动态测试**：支持在运行时动态生成测试用例
6. **更好的集成**：与构建工具（Maven、Gradle）和IDE（IntelliJ IDEA、Eclipse）的集成更加紧密
7. **向后兼容**：通过JUnit Vintage支持JUnit 3和JUnit 4的测试

## 总结

JUnit 5是一个现代化、模块化的单元测试框架，由JUnit Platform、JUnit Jupiter和JUnit Vintage三个子项目组成。JUnit Platform提供了基础平台和Launcher API，JUnit Jupiter提供了新的编程模型和扩展模型，JUnit Vintage提供了向后兼容性。

JUnit 5的架构设计使其具有强大的扩展能力和灵活性，能够满足各种测试需求。通过丰富的注解、断言、参数化测试、动态测试等功能，JUnit 5为开发人员提供了强大的测试工具，帮助编写高质量、可维护的测试代码。
