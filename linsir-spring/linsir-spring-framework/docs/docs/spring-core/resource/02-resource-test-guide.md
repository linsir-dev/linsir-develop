# Spring 资源抽象测试引导文档

## 概述

本文档指导如何运行和验证 Spring 资源抽象模块的测试代码，帮助开发者理解测试结构、运行方式和结果分析。

---

## 1. 测试环境准备

### 1.1 环境要求

- **JDK**: 17 或更高版本
- **Maven**: 3.8.0 或更高版本
- **操作系统**: Windows / Linux / macOS

### 1.2 验证环境

```bash
# 验证 JDK 版本
java -version

# 验证 Maven 版本
mvn -version
```

### 1.3 项目结构确认

确保以下目录结构完整：

```
linsir-spring-framework/
├── src/
│   ├── main/java/com/linsir/spring/framework/spring_core/resource/
│   │   ├── core/           # 资源实现类
│   │   ├── loader/         # 资源加载器
│   │   ├── pattern/        # 模式解析器
│   │   ├── service/        # 服务层
│   │   └── utils/          # 工具类
│   └── test/java/com/linsir/spring/framework/spring_core/resource/
│       ├── core/           # 资源测试类
│       ├── loader/         # 加载器测试
│       ├── pattern/        # 解析器测试
│       ├── service/        # 服务测试
│       └── resources/      # 测试资源文件
│           └── test-config/
│               ├── application.properties
│               └── test.txt
└── pom.xml
```

---

## 2. 测试运行方式

### 2.1 运行所有资源模块测试

```bash
# 进入项目目录
cd linsir-spring-framework

# 运行所有资源模块测试
mvn test -Dtest="com.linsir.spring.framework.spring_core.resource.**"
```

### 2.2 运行特定测试类

```bash
# 运行类路径资源测试
mvn test -Dtest=ClassPathResourceTest

# 运行文件系统资源测试
mvn test -Dtest=FileSystemResourceTest

# 运行 URL 资源测试
mvn test -Dtest=UrlResourceTest

# 运行字节数组资源测试
mvn test -Dtest=ByteArrayResourceTest

# 运行资源加载器测试
mvn test -Dtest=DefaultResourceLoaderTest

# 运行模式解析器测试
mvn test -Dtest=PathMatchingResourcePatternResolverTest

# 运行配置加载器测试
mvn test -Dtest=ConfigLoaderTest

# 运行模板服务测试
mvn test -Dtest=TemplateServiceTest

# 运行集成测试
mvn test -Dtest=ResourceIntegrationTest
```

### 2.3 运行特定测试方法

```bash
# 运行特定测试方法
mvn test -Dtest=ClassPathResourceTest#testExists

# 运行多个特定方法
mvn test -Dtest=ClassPathResourceTest#testExists+testGetInputStream
```

### 2.4 调试模式运行

```bash
# 启用调试模式
mvn test -Dtest="com.linsir.spring.framework.spring_core.resource.**" -Dmaven.surefire.debug

# 然后在 IDE 中连接调试器（端口 5005）
```

---

## 3. 测试参数配置

### 3.1 Maven 测试插件配置

在 `pom.xml` 中配置 Surefire 插件：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.2</version>
    <configuration>
        <!-- 包含资源模块测试 -->
        <includes>
            <include>**/resource/**/*Test.java</include>
        </includes>
        <!-- 测试报告编码 -->
        <encoding>UTF-8</encoding>
        <!-- 并行执行 -->
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
    </configuration>
</plugin>
```

### 3.2 JVM 参数配置

```bash
# 设置内存参数
mvn test -Dtest="com.linsir.spring.framework.spring_core.resource.**" \
  -DargLine="-Xmx512m -XX:MaxMetaspaceSize=256m"

# 设置系统属性
mvn test -Dtest="com.linsir.spring.framework.spring_core.resource.**" \
  -DargLine="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"
```

---

## 4. 测试资源准备

### 4.1 测试资源文件

测试资源文件位于 `src/test/resources/test-config/`：

**application.properties**:
```properties
# 应用配置
app.name=TestApplication
app.version=1.0.0
app.description=This is a test application

# 数据库配置
database.url=jdbc:mysql://localhost:3306/test
database.username=root
database.password=123456

# 服务器配置
server.port=8080
server.host=localhost
```

**test.txt**:
```
This is a test file for resource loading.
Line 2: Testing file reading functionality.
Line 3: Resource abstraction test.
```

### 4.2 临时文件处理

测试中使用 JUnit 5 的 `@TempDir` 注解创建临时目录：

```java
@TempDir
File tempDir;

@Test
void testWithTempFile() throws IOException {
    File tempFile = new File(tempDir, "test.txt");
    Files.write(tempFile.toPath(), "content".getBytes());
    
    FileSystemResource resource = new FileSystemResource(tempFile);
    assertTrue(resource.exists());
}
```

---

## 5. 测试结果分析

### 5.1 控制台输出解读

```
[INFO] Tests run: 129, Failures: 3, Errors: 0, Skipped: 0
```

- **Tests run**: 运行的测试总数
- **Failures**: 断言失败的测试数
- **Errors**: 发生异常的测试数
- **Skipped**: 跳过的测试数

### 5.2 测试报告位置

测试报告生成在以下位置：

```
target/
├── surefire-reports/                    # Surefire 测试报告
│   ├── com.linsir.spring.framework.spring_core.resource.core.ClassPathResourceTest.txt
│   ├── com.linsir.spring.framework.spring_core.resource.core.FileSystemResourceTest.txt
│   ├── ...
│   └── TEST-com.linsir.spring.framework.spring_core.resource.core.ClassPathResourceTest.xml
└── site/
    └── surefire-report.html             # HTML 格式测试报告
```

### 5.3 生成 HTML 测试报告

```bash
# 生成完整测试报告
mvn surefire-report:report

# 查看报告
# target/site/surefire-report.html
```

---

## 6. 常见问题排查

### 6.1 编译错误

**问题**: 编译时出现 "非法字符" 错误

**原因**: 源代码中包含全角字符或特殊 Unicode 字符

**解决**:
```bash
# 检查文件编码
file -i src/main/java/.../*.java

# 确保使用 UTF-8 编码
# 在 IDE 中设置 File Encoding 为 UTF-8
```

### 6.2 资源文件找不到

**问题**: `ClassPathResource` 测试失败，提示资源不存在

**原因**: 
1. 资源文件未正确放置在 `src/test/resources` 目录
2. Maven 未正确复制资源文件

**解决**:
```bash
# 清理并重新编译
mvn clean test

# 检查资源文件是否存在
ls -la src/test/resources/test-config/

# 检查 target 目录中的资源
ls -la target/test-classes/test-config/
```

### 6.3 测试超时

**问题**: 某些测试执行时间过长

**解决**:
```java
@Test
@Timeout(value = 5, unit = TimeUnit.SECONDS)  // 设置 5 秒超时
void testMethod() {
    // 测试代码
}
```

### 6.4 平台相关失败

**问题**: Windows 和 Linux 下路径分隔符不同导致测试失败

**解决**:
```java
// 使用 File.separator 而不是硬编码 "/"
String path = "config" + File.separator + "app.properties";

// 或者使用 Paths 类
Path path = Paths.get("config", "app.properties");
```

---

## 7. 测试扩展指南

### 7.1 添加新测试类

1. 在 `src/test/java/.../resource/` 下创建新的测试类
2. 遵循命名规范：`XxxTest.java`
3. 使用 JUnit 5 注解：

```java
package com.linsir.spring.framework.spring_core.resource;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("自定义资源测试")
public class CustomResourceTest {

    @BeforeAll
    static void setUpAll() {
        // 类级别初始化
    }

    @BeforeEach
    void setUp() {
        // 方法级别初始化
    }

    @Test
    @DisplayName("测试自定义功能")
    void testCustomFeature() {
        // 测试代码
        assertTrue(condition, "描述信息");
    }

    @AfterEach
    void tearDown() {
        // 清理工作
    }

    @AfterAll
    static void tearDownAll() {
        // 类级别清理
    }
}
```

### 7.2 添加测试资源

1. 在 `src/test/resources/` 下创建资源文件
2. 使用有意义的目录结构组织资源
3. 在测试代码中引用：

```java
Resource resource = new ClassPathResource("custom/test-data.json");
```

### 7.3 参数化测试

```java
@ParameterizedTest
@ValueSource(strings = {"test1.txt", "test2.txt", "test3.txt"})
@DisplayName("测试多个文件")
void testMultipleFiles(String filename) {
    Resource resource = new ClassPathResource("test-config/" + filename);
    assertTrue(resource.exists());
}
```

---

## 8. 持续集成配置

### 8.1 GitHub Actions 示例

```yaml
name: Resource Module Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Run Resource Tests
      run: |
        cd linsir-spring-framework
        mvn test -Dtest="com.linsir.spring.framework.spring_core.resource.**"
    
    - name: Upload Test Results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: linsir-spring-framework/target/surefire-reports/
```

### 8.2 Jenkins Pipeline 示例

```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven-3.8'
        jdk 'JDK-17'
    }
    
    stages {
        stage('Test Resource Module') {
            steps {
                dir('linsir-spring-framework') {
                    sh 'mvn test -Dtest="com.linsir.spring.framework.spring_core.resource.**"'
                }
            }
            post {
                always {
                    junit 'linsir-spring-framework/target/surefire-reports/*.xml'
                }
            }
        }
    }
}
```

---

## 9. 测试检查清单

在提交代码前，请确认以下检查项：

### 9.1 代码检查

- [ ] 所有新代码都有对应的单元测试
- [ ] 测试方法有清晰的 DisplayName
- [ ] 测试断言有描述信息
- [ ] 没有使用 `Thread.sleep()` 进行等待
- [ ] 资源正确关闭（使用 try-with-resources）

### 9.2 运行检查

- [ ] 本地运行所有测试通过
- [ ] 没有编译警告
- [ ] 代码覆盖率不低于 80%
- [ ] 没有资源泄漏

### 9.3 文档检查

- [ ] 新增类有 JavaDoc 注释
- [ ] 复杂逻辑有注释说明
- [ ] 测试类有类级别注释

---

## 10. 参考资源

### 10.1 相关文档

- [Spring Framework 官方文档 - Resources](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#resources)
- [JUnit 5 用户指南](https://junit.org/junit5/docs/current/user-guide/)
- [Maven Surefire 插件文档](https://maven.apache.org/surefire/maven-surefire-plugin/)

### 10.2 相关代码

- [资源抽象概述文档](./00-resource-overview.md)
- [资源抽象代码指南](./01-resource-code-guide.md)
- [资源抽象测试报告](./03-resource-test-report.md)

---

## 11. 总结

本测试引导文档提供了：

1. **环境准备** - 完整的测试环境要求和验证方法
2. **运行方式** - 多种测试运行方式和参数配置
3. **结果分析** - 测试报告解读和常见问题排查
4. **扩展指南** - 如何添加新测试和配置 CI/CD
5. **检查清单** - 代码提交前的检查项

按照本文档指引，可以顺利运行和验证 Spring 资源抽象模块的所有测试。
