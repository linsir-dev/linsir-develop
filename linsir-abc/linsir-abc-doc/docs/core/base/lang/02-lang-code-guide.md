# java.lang 包代码说明文档

## 一、代码结构

### 1.1 包结构概览

```
com.linsir.abc.core.base.lang/
├── object/                    # Object核心机制
│   ├── ObjectMethodOverride.java      # 重写equals、hashCode、toString、clone
│   ├── HashCodeGenerator.java         # 哈希码生成策略
│   └── DeepCloneable.java             # 深拷贝实现
├── string/                    # String不可变性
│   ├── StringImmutability.java        # String不可变性演示
│   └── StringConcatenationBenchmark.java  # 字符串拼接性能测试
├── system/                    # System系统操作
│   ├── SystemPropertyManager.java     # 系统属性管理
│   └── ArrayCopyPerformance.java      # 数组拷贝性能测试
├── thread/                    # Thread线程管理
│   ├── ThreadLifecycleManager.java    # 线程生命周期管理
│   ├── ThreadLocalContext.java        # ThreadLocal上下文
│   └── ThreadSynchronization.java     # 线程同步机制
├── reflect/                   # 反射机制
│   ├── ReflectionInspector.java       # 反射检查器
│   └── DynamicProxyGenerator.java     # 动态代理生成器
└── wrapper/                   # 包装类与自动装箱
    ├── WrapperTypeCache.java          # 包装类缓存
    └── IntegerCacheAnalysis.java      # Integer缓存分析
```

### 1.2 各类详细结构

#### 1.2.1 ObjectMethodOverride.java

**JDK对应代码**: `java.lang.Object`

**类结构**:
```java
public class ObjectMethodOverride implements Cloneable {
    // 字段
    private final Long id;              // 唯一标识
    private String name;                // 名称
    private Integer age;                // 年龄
    private ObjectMethodOverride reference;  // 关联对象
    
    // 核心方法
    public boolean equals(Object obj)   // 基于id的相等性判断
    public int hashCode()               // 基于id的哈希码生成
    public String toString()            // 格式化字符串输出
    public ObjectMethodOverride clone() // 浅拷贝实现
    
    // 演示方法
    public static void demonstrate()
    public static void main(String[] args)
}
```

**设计要点**:
- `equals()` 仅基于 `id` 字段判断相等性
- `hashCode()` 与 `equals()` 保持一致，同样基于 `id`
- `clone()` 实现浅拷贝，关联对象引用相同

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `equals()` | `Object.equals()` | 对象相等性判断 |
| `hashCode()` | `Object.hashCode()` | 哈希码生成 |
| `toString()` | `Object.toString()` | 字符串表示 |
| `clone()` | `Object.clone()` | 对象克隆 |
| `Cloneable` | `java.lang.Cloneable` | 克隆标记接口 |
| `getClass()` | `Object.getClass()` | 获取类对象 |
| `finalize()` | `Object.finalize()` | 对象销毁前调用 |

#### 1.2.2 StringImmutability.java

**JDK对应代码**: `java.lang.String` / `java.lang.StringBuilder` / `java.lang.StringBuffer`

**类结构**:
```java
public class StringImmutability {
    // 演示方法
    public void demonstrateImmutability()        // 不可变性演示
    public void demonstrateStringPool()          // 常量池演示
    public void demonstrateConcatenationProblem() // 拼接性能问题
    public void demonstrateBuilderVsBuffer()     // Builder vs Buffer
    public void demonstrateSecurity()            // 安全性演示
    
    // 主方法
    public static void main(String[] args)
}
```

**核心概念**:
- String对象一旦创建不可修改
- 字符串常量池复用机制
- `+` 拼接 vs `StringBuilder` 性能差异

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `String` | `java.lang.String` | 不可变字符串类 |
| `StringBuilder` | `java.lang.StringBuilder` | 可变字符串（非线程安全） |
| `StringBuffer` | `java.lang.StringBuffer` | 可变字符串（线程安全） |
| `intern()` | `String.intern()` | 字符串入池 |
| `concat()` | `String.concat()` | 字符串连接 |
| `substring()` | `String.substring()` | 子字符串 |
| `String.valueOf()` | `String.valueOf()` | 类型转字符串 |
| `String.format()` | `String.format()` | 格式化字符串 |

#### 1.2.3 ThreadLifecycleManager.java

**JDK对应代码**: `java.lang.Thread` / `java.lang.Runnable`

**类结构**:
```java
public class ThreadLifecycleManager {
    // 静态字段
    private static final AtomicInteger threadCounter  // 线程计数器
    
    // 实例字段
    private volatile boolean isRunning   // 运行标志
    private volatile boolean isPaused    // 暂停标志
    
    // 线程管理方法
    public Thread createAndStartThread(Runnable, String)      // 创建启动线程
    public Thread createDaemonThread(Runnable, String)        // 创建守护线程
    public void waitForCompletion(Thread)                     // 等待线程完成
    public boolean waitForCompletion(Thread, long)            // 带超时等待
    public void interruptThread(Thread)                       // 中断线程
    public Thread.State getThreadState(Thread)                // 获取线程状态
    
    // 控制方法
    public void pauseExecution()          // 暂停执行
    public void resumeExecution()         // 恢复执行
    public void stopExecution()           // 停止执行
    public boolean isRunning()            // 是否运行中
    public boolean isPaused()             // 是否暂停
}
```

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `Thread` | `java.lang.Thread` | 线程类 |
| `Runnable` | `java.lang.Runnable` | 线程执行接口 |
| `start()` | `Thread.start()` | 启动线程 |
| `join()` | `Thread.join()` | 等待线程完成 |
| `interrupt()` | `Thread.interrupt()` | 中断线程 |
| `isDaemon()` | `Thread.isDaemon()` | 守护线程 |
| `Thread.State` | `java.lang.Thread.State` | 线程状态枚举 |
| `Thread.sleep()` | `Thread.sleep()` | 线程休眠 |
| `Thread.yield()` | `Thread.yield()` | 让出CPU |
| `Thread.currentThread()` | `Thread.currentThread()` | 获取当前线程 |
```

---

## 二、代码使用场景

### 2.1 ObjectMethodOverride - 对象基础方法重写

**适用场景**:
- 需要作为HashMap/HashSet的key的自定义对象
- 需要对象比较和哈希存储的业务实体
- 需要对象克隆功能的场景

**使用示例**:
```java
// 创建对象
ObjectMethodOverride obj1 = new ObjectMethodOverride(1L, "张三", 25);
ObjectMethodOverride obj2 = new ObjectMethodOverride(1L, "李四", 30);

// 相等性判断（基于id）
boolean isEqual = obj1.equals(obj2);  // true，id相同即相等

// 哈希码（基于id）
int hashCode = obj1.hashCode();

// 克隆对象
ObjectMethodOverride clone = obj1.clone();
```

### 2.2 StringImmutability - 字符串处理

**适用场景**:
- 理解String不可变性对程序的影响
- 选择正确的字符串拼接方式
- 优化字符串操作性能

**使用示例**:
```java
StringImmutability demo = new StringImmutability();

// 理解不可变性
demo.demonstrateImmutability();

// 理解常量池
demo.demonstrateStringPool();

// 性能对比
demo.demonstrateConcatenationProblem();
```

**最佳实践**:
| 场景 | 推荐方式 | 原因 |
|------|----------|------|
| 单线程字符串拼接 | StringBuilder | 性能最好 |
| 多线程字符串拼接 | StringBuffer | 线程安全 |
| 字符串常量 | 字面量 | 常量池复用 |
| 频繁修改的字符串 | StringBuilder | 避免创建大量临时对象 |

### 2.3 ThreadLifecycleManager - 线程管理

**适用场景**:
- 需要统一管理线程生命周期的应用
- 需要线程池之外的精细线程控制
- 需要线程状态监控的场景

**使用示例**:
```java
ThreadLifecycleManager manager = new ThreadLifecycleManager();

// 创建并启动线程
Thread thread = manager.createAndStartThread(() -> {
    // 任务逻辑
    System.out.println("线程执行中...");
}, "Worker-1");

// 等待线程完成
manager.waitForCompletion(thread);

// 获取线程状态
Thread.State state = manager.getThreadState(thread);
```

### 2.4 ReflectionInspector - 反射检查

**适用场景**:
- 框架开发（如Spring的依赖注入）
- 序列化/反序列化
- 动态代理实现
- 单元测试（访问私有成员）

### 2.5 ThreadLocalContext - 线程上下文

**适用场景**:
- 用户会话信息存储
- 数据库连接管理
- 请求上下文传递
- 线程安全的对象存储

---

## 三、测试代码结构

### 3.1 测试包结构

```
src/test/java/com/linsir/abc/core/base/lang/
├── object/
│   ├── ObjectMethodOverrideTest.java   # Object方法重写测试
│   ├── HashCodeGeneratorTest.java      # 哈希码生成测试
│   └── DeepCloneableTest.java          # 深拷贝测试
├── string/
│   ├── StringImmutabilityTest.java     # String不可变性测试
│   └── StringConcatenationBenchmarkTest.java # 拼接性能测试
├── system/
│   ├── SystemPropertyManagerTest.java  # 系统属性测试
│   └── ArrayCopyPerformanceTest.java   # 数组拷贝测试
├── thread/
│   ├── ThreadLifecycleManagerTest.java # 线程生命周期测试
│   ├── ThreadLocalContextTest.java     # ThreadLocal测试
│   └── ThreadSynchronizationTest.java  # 线程同步测试
├── reflect/
│   ├── ReflectionInspectorTest.java    # 反射检查测试
│   └── DynamicProxyGeneratorTest.java  # 动态代理测试
└── wrapper/
    ├── WrapperTypeCacheTest.java       # 包装类缓存测试
    └── IntegerCacheAnalysisTest.java   # Integer缓存测试
```

### 3.2 测试类结构示例

#### ObjectMethodOverrideTest.java

```java
public class ObjectMethodOverrideTest {
    // equals契约测试
    @Test public void testEqualsReflexivity()      // 自反性
    @Test public void testEqualsSymmetry()         // 对称性
    @Test public void testEqualsTransitivity()     // 传递性
    @Test public void testEqualsConsistency()      // 一致性
    @Test public void testEqualsNonNull()          // 非空性
    @Test public void testEqualsDifferentId()      // 不同ID
    
    // hashCode契约测试
    @Test public void testHashCodeConsistency()    // 一致性
    @Test public void testHashCodeEquality()       // 相等对象相同hashCode
    
    // toString测试
    @Test public void testToStringNotNull()        // 非空
    @Test public void testToStringContainsId()     // 包含ID
    
    // clone测试
    @Test public void testCloneCreatesNewObject()  // 创建新对象
    @Test public void testCloneCopiesFields()      // 复制字段
    @Test public void testCloneShallowCopy()       // 浅拷贝验证
}
```

#### StringImmutabilityTest.java

```java
public class StringImmutabilityTest {
    @Test public void testImmutability()           // 不可变性
    @Test public void testStringPool()             // 常量池
    @Test public void testIntern()                 // intern方法
    @Test public void testStringBuilderPerformance() // 性能对比
    @Test public void testBuilderVsBuffer()        // Builder vs Buffer
    @Test public void testDemonstrateMethods()     // 演示方法
}
```

---

## 四、单元测试预期结果

### 4.1 ObjectMethodOverrideTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testEqualsReflexivity` | ✅ PASS | 对象等于自身 |
| `testEqualsSymmetry` | ✅ PASS | 相等性对称 |
| `testEqualsTransitivity` | ✅ PASS | 相等性传递 |
| `testEqualsConsistency` | ✅ PASS | 多次调用结果一致 |
| `testEqualsNonNull` | ✅ PASS | 不等于null |
| `testEqualsDifferentId` | ✅ PASS | 不同ID不相等 |
| `testHashCodeConsistency` | ✅ PASS | hashCode一致 |
| `testHashCodeEquality` | ✅ PASS | 相等对象hashCode相同 |
| `testCloneCreatesNewObject` | ✅ PASS | 克隆是新对象 |
| `testCloneShallowCopy` | ✅ PASS | 浅拷贝验证 |

### 4.2 StringImmutabilityTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testImmutability` | ✅ PASS | 修改后hashCode不同 |
| `testStringPool` | ✅ PASS | 字面量指向同一对象 |
| `testIntern` | ✅ PASS | intern后指向常量池 |
| `testStringBuilderPerformance` | ✅ PASS | Builder性能优于String拼接 |
| `testBuilderVsBuffer` | ✅ PASS | 结果正确性验证 |

### 4.3 ThreadLifecycleManagerTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testCreateAndStartThread` | ✅ PASS | 线程创建并启动 |
| `testCreateDaemonThread` | ✅ PASS | 守护线程创建 |
| `testWaitForCompletion` | ✅ PASS | 等待线程完成 |
| `testWaitForCompletionWithTimeout` | ✅ PASS | 带超时等待 |
| `testInterruptThread` | ✅ PASS | 线程中断 |
| `testGetThreadState` | ✅ PASS | 获取线程状态 |

### 4.4 所有测试汇总

| 测试类 | 测试数 | 预期通过率 |
|--------|--------|------------|
| ObjectMethodOverrideTest | 10 | 100% |
| HashCodeGeneratorTest | 5 | 100% |
| DeepCloneableTest | 6 | 100% |
| StringImmutabilityTest | 6 | 100% |
| StringConcatenationBenchmarkTest | 3 | 100% |
| SystemPropertyManagerTest | 5 | 100% |
| ArrayCopyPerformanceTest | 3 | 100% |
| ThreadLifecycleManagerTest | 6 | 100% |
| ThreadLocalContextTest | 5 | 100% |
| ThreadSynchronizationTest | 4 | 100% |
| ReflectionInspectorTest | 6 | 100% |
| DynamicProxyGeneratorTest | 4 | 100% |
| WrapperTypeCacheTest | 4 | 100% |
| IntegerCacheAnalysisTest | 4 | 100% |
| **总计** | **71** | **100%** |

---

## 五、运行测试

### 5.1 运行所有lang包测试

```bash
mvn test -Dtest="com.linsir.abc.core.base.lang.**"
```

### 5.2 运行单个测试类

```bash
mvn test -Dtest=ObjectMethodOverrideTest
```

### 5.3 预期输出

```
Tests run: 71, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.linsir.abc.core.base.lang.object.ObjectMethodOverrideTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] BUILD SUCCESS
```

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
