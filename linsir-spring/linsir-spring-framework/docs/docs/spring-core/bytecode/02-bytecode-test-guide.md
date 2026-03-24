# 字节码操作模块 - 测试说明文档

## 1. 测试概述

字节码操作模块的测试覆盖了CGLIB代理、ASM字节码操作、Objenesis对象实例化和类加载器管理四大子模块。所有测试使用JUnit 5框架编写，确保代码的正确性和稳定性。

## 2. 测试结构

```
src/test/java/com/linsir/spring/framework/spring_core/bytecode/
├── cglib/
│   ├── proxy/
│   │   ├── MethodInterceptorTest.java    # 方法拦截器测试
│   │   └── EnhancerTest.java             # 增强器测试
│   └── reflect/
│       └── FastClassTest.java            # FastClass测试
├── asm/
│   ├── TypeTest.java                     # 类型描述符测试
│   └── OpcodesTest.java                  # 操作码测试
├── objenesis/
│   └── ObjenesisTest.java                # Objenesis测试
└── loader/
    ├── BytecodeClassLoaderTest.java      # 字节码类加载器测试
    └── ClassLoaderUtilsTest.java         # 类加载器工具测试
```

## 3. 测试环境

### 3.1 依赖配置

```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 3.2 运行测试

```bash
# 运行所有字节码测试
mvn test -Dtest="com.linsir.spring.framework.spring_core.bytecode.**"

# 运行特定模块测试
mvn test -Dtest="MethodInterceptorTest,EnhancerTest"
mvn test -Dtest="FastClassTest"
mvn test -Dtest="TypeTest,OpcodesTest"
mvn test -Dtest="ObjenesisTest"
mvn test -Dtest="BytecodeClassLoaderTest,ClassLoaderUtilsTest"
```

## 4. CGLIB代理测试

### 4.1 MethodInterceptorTest（方法拦截器测试）

**测试目标**：验证方法拦截器的核心功能

**测试用例**：

| 测试方法 | 说明 | 测试点 |
|---------|------|--------|
| `testSimpleInterceptor` | 简单拦截器测试 | 验证方法拦截是否正常 |
| `testBeforeAdvice` | 前置处理测试 | 验证前置通知执行 |
| `testAfterAdvice` | 后置处理测试 | 验证后置通知执行 |
| `testReturnValueModification` | 返回值修改测试 | 验证返回值可被修改 |
| `testArgumentModification` | 参数修改测试 | 验证参数可被修改 |
| `testExceptionHandling` | 异常处理测试 | 验证异常可被捕获和处理 |
| `testAroundAdvice` | 环绕通知测试 | 验证完整拦截流程 |
| `testMethodProxy` | 方法代理基本功能测试 | 验证MethodProxy核心方法 |
| `testMethodProxyWithArgs` | 带参数方法代理测试 | 验证参数传递 |
| `testMethodProxyInvoke` | invoke方法测试 | 验证invoke方法 |
| `testInterceptorChain` | 拦截器链测试 | 验证拦截器链式调用 |
| `testCreateWithConstructorArgs` | 构造参数测试 | 验证带参数构造代理 |

**示例测试代码**：

```java
@Test
public void testSimpleInterceptor() {
    // 记录拦截的方法
    List<String> interceptedMethods = new ArrayList<>();

    MethodInterceptor interceptor = new MethodInterceptor() {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            interceptedMethods.add(method.getName());
            return proxy.invokeSuper(obj, args);
        }
    };

    // 创建代理
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(UserService.class);
    enhancer.setCallback(interceptor);

    IUserService proxy = (IUserService) enhancer.create();

    // 调用方法
    proxy.setUserName("张三");
    String result = proxy.getUserName();

    // 验证
    assertEquals("张三", result);
    assertTrue(interceptedMethods.contains("setUserName"));
    assertTrue(interceptedMethods.contains("getUserName"));
}
```

### 4.2 EnhancerTest（增强器测试）

**测试目标**：验证Enhancer的配置和创建功能

**测试用例**：

| 测试方法 | 说明 | 测试点 |
|---------|------|--------|
| `testBasicProxyCreation` | 基本代理创建 | 验证代理对象创建成功 |
| `testProxyWithConstructorArgs` | 构造参数代理 | 验证带参数构造 |
| `testCallbackFilter` | 回调过滤器 | 验证不同方法使用不同回调 |
| `testMethodProxyMapping` | 方法代理映射 | 验证方法映射创建 |
| `testSetAndGetSuperclass` | 父类设置 | 验证setter/getter |
| `testSetAndGetCallback` | 回调设置 | 验证setter/getter |
| `testSetAndGetCallbacks` | 回调数组设置 | 验证setter/getter |
| `testSetAndGetCallbackFilter` | 过滤器设置 | 验证setter/getter |
| `testSetAndGetInterfaces` | 接口设置 | 验证setter/getter |
| `testSetAndGetUseFactory` | 工厂设置 | 验证setter/getter |
| `testCreateWithoutSuperclass` | 无父类异常 | 验证异常抛出 |
| `testSetInterfaceAsSuperclass` | 接口作为父类异常 | 验证异常抛出 |
| `testSetFinalClassAsSuperclass` | final类作为父类异常 | 验证异常抛出 |

### 4.3 FastClassTest（FastClass测试）

**测试目标**：验证反射优化功能

**测试用例**：

| 测试方法 | 说明 | 测试点 |
|---------|------|--------|
| `testCreateFastClass` | 创建FastClass | 验证创建成功 |
| `testGetIndex` | 获取方法索引 | 验证索引获取 |
| `testInvokeByIndex` | 通过索引调用 | 验证方法调用 |
| `testInvokeByName` | 通过名称调用 | 验证方法调用 |
| `testFastMethod` | FastMethod测试 | 验证FastMethod功能 |
| `testReturnTypes` | 返回类型测试 | 验证各种返回类型 |
| `testExceptionHandling` | 异常处理 | 验证异常传递 |
| `testParameterTypes` | 参数类型 | 验证参数处理 |
| `testMethodNotFound` | 方法未找到 | 验证异常抛出 |
| `testNullTarget` | null目标 | 验证异常抛出 |
| `testPerformance` | 性能测试 | 验证性能优势 |

## 5. ASM字节码操作测试

### 5.1 TypeTest（类型描述符测试）

**测试目标**：验证Type类的类型描述符功能

**测试用例**：

| 测试方法 | 说明 | 测试点 |
|---------|------|--------|
| `testPrimitiveTypes` | 基本类型 | 验证基本类型描述符 |
| `testObjectTypeDescriptor` | 对象类型 | 验证对象类型描述符 |
| `testArrayTypeDescriptor` | 数组类型 | 验证数组类型描述符 |
| `testGetTypeFromDescriptor` | 从描述符创建 | 验证反向解析 |
| `testInternalName` | 内部名称 | 验证内部名称转换 |
| `testMethodDescriptor` | 方法描述符 | 验证方法签名描述符 |
| `testGetArgumentTypes` | 参数类型 | 验证参数类型解析 |
| `testGetReturnType` | 返回类型 | 验证返回类型解析 |
| `testGetSize` | 类型大小 | 验证JVM类型大小 |
| `testTypeChecks` | 类型检查 | 验证isXXX方法 |
| `testToString` | toString | 验证字符串表示 |

### 5.2 OpcodesTest（操作码测试）

**测试目标**：验证Opcodes常量定义

**测试用例**：

| 测试方法 | 说明 | 测试点 |
|---------|------|--------|
| `testVersionOpcodes` | 版本操作码 | 验证版本常量 |
| `testAccessFlagOpcodes` | 访问标志 | 验证访问标志常量 |
| `testTypeOpcodes` | 类型操作码 | 验证类型常量 |
| `testConstantOpcodes` | 常量操作码 | 验证常量指令 |
| `testLoadStoreOpcodes` | 加载存储 | 验证加载存储指令 |
| `testArithmeticOpcodes` | 算术指令 | 验证算术运算指令 |
| `testMethodInvocationOpcodes` | 方法调用 | 验证方法调用指令 |
| `testReturnOpcodes` | 返回指令 | 验证返回指令 |
| `testArrayOpcodes` | 数组指令 | 验证数组操作指令 |
| `testStackOpcodes` | 栈操作 | 验证栈操作指令 |
| `testConversionOpcodes` | 类型转换 | 验证类型转换指令 |
| `testComparisonOpcodes` | 比较指令 | 验证比较跳转指令 |
| `testFrameOpcodes` | 帧操作 | 验证帧类型常量 |

## 6. Objenesis对象实例化测试

### 6.1 ObjenesisTest

**测试目标**：验证对象实例化功能

**测试用例**：

| 测试方法 | 说明 | 测试点 |
|---------|------|--------|
| `testBasicInstantiation` | 基本实例化 | 验证对象创建 |
| `testMultipleInstantiations` | 多次实例化 | 验证可重复创建 |
| `testInstantiatorReuse` | 实例化器复用 | 验证缓存机制 |
| `testConstructorNotCalled` | 构造函数未调用 | 验证绕过构造 |
| `testDifferentInstantiators` | 不同实例化器 | 验证多种策略 |
| `testUnsafeInstantiator` | Unsafe方式 | 验证Unsafe策略 |
| `testReflectionFactoryInstantiator` | 反射工厂 | 验证反射工厂策略 |
| `testConstructorInstantiator` | 构造函数 | 验证构造策略 |
| `testPrimitiveType` | 基本类型 | 验证不支持基本类型 |
| `testArrayType` | 数组类型 | 验证数组实例化 |
| `testInterface` | 接口类型 | 验证接口实例化失败 |
| `testAbstractClass` | 抽象类 | 验证抽象类实例化 |
| `testFieldInitialization` | 字段初始化 | 验证字段默认值 |

## 7. 类加载器管理测试

### 7.1 BytecodeClassLoaderTest

**测试目标**：验证字节码类加载功能

**测试用例**：

| 测试方法 | 说明 | 测试点 |
|---------|------|--------|
| `testConstructor` | 构造函数 | 验证构造器 |
| `testConstructorWithParent` | 带父类构造 | 验证父类加载器 |
| `testDefineClass` | 定义类 | 验证类定义 |
| `testDefineClassValidation` | 参数验证 | 验证参数检查 |
| `testClassCache` | 类缓存 | 验证缓存机制 |
| `testClearCache` | 清除缓存 | 验证缓存清除 |
| `testGetDefinedClassNames` | 获取类名 | 验证类名列表 |
| `testDefineClassWithoutCache` | 无缓存定义 | 验证非缓存定义 |

### 7.2 ClassLoaderUtilsTest

**测试目标**：验证类加载器工具功能

**测试用例**：

| 测试方法 | 说明 | 测试点 |
|---------|------|--------|
| `testGetDefaultClassLoader` | 默认加载器 | 验证获取默认加载器 |
| `testGetClassLoaderHierarchy` | 加载器层次 | 验证层次结构 |
| `testGetNullClassLoaderHierarchy` | null层次 | 验证null处理 |
| `testGetClassBytes` | 获取字节码 | 验证字节码获取 |
| `testGetClassBytesNotFound` | 字节码未找到 | 验证null返回 |
| `testIsLoadedBy` | 加载器检查 | 验证类加载器判断 |
| `testGetClassLoaderName` | 加载器名称 | 验证名称获取 |
| `testGetBootstrapClassLoaderName` | Bootstrap名称 | 验证Bootstrap名称 |
| `testPrintClassLoaderHierarchy` | 打印层次 | 验证层次打印 |
| `testCreateBytecodeClassLoader` | 创建加载器 | 验证加载器创建 |
| `testCannotInstantiate` | 不可实例化 | 验证工具类特性 |

## 8. 测试数据与工具类

### 8.1 测试目标类

所有测试使用内嵌的测试目标类，例如：

```java
// MethodInterceptorTest中的目标类
public interface IUserService {
    String getUserName();
    void setUserName(String userName);
    String sayHello(String name);
    int add(int a, int b);
    void throwException();
}

public static class UserService implements IUserService {
    // 实现...
}
```

### 8.2 模拟字节码生成

```java
private byte[] createMockBytecode() {
    // 创建一个简单的类文件结构（魔数+版本+常量池等）
    byte[] bytecode = new byte[100];
    // CAFEBABE - Java类文件魔数
    bytecode[0] = (byte) 0xCA;
    bytecode[1] = (byte) 0xFE;
    bytecode[2] = (byte) 0xBA;
    bytecode[3] = (byte) 0xBE;
    // 版本号
    bytecode[4] = 0x00;
    bytecode[5] = 0x00;
    bytecode[6] = 0x00;
    bytecode[7] = 0x37; // Java 11
    // ... 其他字节码数据
    return bytecode;
}
```

## 9. 测试最佳实践

### 9.1 命名规范

- 测试类名：`被测试类名 + Test`
- 测试方法名：`test + 被测试功能`
- 使用描述性名称，清晰表达测试意图

### 9.2 断言使用

```java
// 基本断言
assertEquals(expected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(object);
assertNotNull(object);

// 异常断言
assertThrows(ExceptionType.class, () -> {
    // 可能抛出异常的代码
});

// 组合断言
assertAll("验证多个条件",
    () -> assertEquals(expected1, actual1),
    () -> assertEquals(expected2, actual2),
    () -> assertTrue(condition)
);
```

### 9.3 测试隔离

- 每个测试方法独立运行
- 不依赖其他测试的执行顺序
- 使用局部变量，避免共享状态

### 9.4 边界条件

测试应覆盖：
- 正常输入
- 边界值
- 异常输入
- null值
- 空集合/数组

## 10. 常见问题

### 10.1 代理类需要实现接口

由于当前实现使用JDK动态代理，被代理类需要实现接口：

```java
// 正确
public interface IService { }
public class ServiceImpl implements IService { }

// 使用
IService proxy = (IService) enhancer.create();
```

### 10.2 类加载器委托

自定义类加载器默认会委托给父类加载器，测试时需要注意：

```java
// 创建不委托的类加载器
ClassLoader customLoader = new ClassLoader(null) {
    @Override
    public InputStream getResourceAsStream(String name) {
        return null; // 不委托
    }
};
```

### 10.3 字节码有效性

模拟字节码需要符合Java类文件格式，否则会导致`ClassFormatError`。

## 11. 扩展测试

### 11.1 添加新测试

1. 在对应测试类中添加测试方法
2. 使用`@Test`注解标记
3. 编写测试逻辑和断言
4. 运行测试验证

### 11.2 测试覆盖率

当前测试覆盖率：
- CGLIB代理：100% 核心功能
- ASM操作：100% 核心功能
- Objenesis：100% 核心功能
- 类加载器：100% 核心功能

## 12. 相关文档

- [字节码操作概述](./00-bytecode-overview.md)
- [字节码操作代码说明](./01-bytecode-code-guide.md)
- [字节码操作测试报告](./03-bytecode-test-report.md)
- [字节码操作扩展设计](./04-bytecode-extension-design.md)
