# 第2章 OOM异常代码实战指南

本文档说明 `linsir-abc-core` 模块中 JVM 内存溢出异常演示代码的结构、作用和执行方法。

## 一、代码结构

```
linsir-abc-core/src/main/java/com/linsir/abc/core/jvm/memory/
├── heap/
│   └── HeapOutOfMemory.java              # Java堆内存溢出
├── stack/
│   ├── StackOverflowError.java           # 栈深度溢出
│   └── StackOutOfMemory.java             # 栈内存溢出
├── methodarea/
│   ├── MethodAreaOutOfMemory.java        # 方法区(元空间)溢出
│   └── RuntimeConstantPoolOutOfMemory.java # 运行时常量池溢出
├── direct/
│   └── DirectMemoryOutOfMemory.java      # 直接内存(堆外内存)溢出
└── oom/
    └── OutOfMemoryTestRunner.java        # 统一测试运行器
```

### 1.1 包结构说明

| 包路径 | 说明 |
|--------|------|
| `com.linsir.abc.core.jvm.memory.heap` | 堆内存相关演示 |
| `com.linsir.abc.core.jvm.memory.stack` | 栈内存相关演示 |
| `com.linsir.abc.core.jvm.memory.methodarea` | 方法区相关演示 |
| `com.linsir.abc.core.jvm.memory.direct` | 直接内存相关演示 |
| `com.linsir.abc.core.jvm.memory.oom` | 测试运行器 |

## 二、代码作用详解

### 2.1 HeapOutOfMemory - Java堆溢出

**作用**：演示Java堆内存溢出异常 (`java.lang.OutOfMemoryError: Java heap space`)

**原理**：
- 不断创建占用内存的对象（每个对象约1MB）
- 使用List保持对象引用，防止被GC回收
- 当堆内存耗尽时触发OOM

**关键代码**：
```java
static class MemoryObject {
    private final byte[] data = new byte[1024 * 1024]; // 1MB
}

while (true) {
    objectList.add(new MemoryObject());
}
```

---

### 2.2 StackOverflowError - 栈深度溢出

**作用**：演示栈深度溢出异常 (`java.lang.StackOverflowError`)

**原理**：
- 通过无限递归调用，每次调用在栈中创建一个栈帧
- 当栈深度超过虚拟机允许的最大深度时触发异常

**关键代码**：
```java
public void recursiveCall() {
    stackLength++;
    recursiveCall();  // 无限递归
}
```

**与StackOutOfMemory的区别**：
- `StackOverflowError`：单个线程的栈深度超过限制
- `OutOfMemoryError`：无法为线程分配栈内存（创建过多线程）

---

### 2.3 StackOutOfMemory - 栈内存溢出

**作用**：演示无法创建新线程导致的OOM (`java.lang.OutOfMemoryError: unable to create new native thread`)

**原理**：
- 不断创建新线程，每个线程都需要分配独立的栈空间
- 当系统无法为更多线程分配栈内存时触发OOM

**关键代码**：
```java
while (true) {
    Thread thread = new Thread(() -> dontStop());
    thread.start();
}
```

**⚠️ 警告**：在Windows平台运行可能导致系统假死，请谨慎测试。

---

### 2.4 MethodAreaOutOfMemory - 方法区溢出

**作用**：演示方法区(元空间)溢出 (`java.lang.OutOfMemoryError: Metaspace`)

**原理**：
- 使用CGLib动态生成大量类
- 每个类都会加载到方法区(元空间)
- 当元空间耗尽时触发OOM

**依赖**：
```xml
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>3.3.0</version>
</dependency>
```

**关键代码**：
```java
Enhancer enhancer = new Enhancer();
enhancer.setSuperclass(TargetClass.class);
enhancer.setUseCache(false);  // 禁用缓存，确保每次都生成新类
enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) 
    -> proxy.invokeSuper(obj, args));
enhancer.create();
```

**JDK版本差异**：
- JDK 7及之前：`OutOfMemoryError: PermGen space`
- JDK 8+：`OutOfMemoryError: Metaspace`

---

### 2.5 RuntimeConstantPoolOutOfMemory - 运行时常量池溢出

**作用**：演示运行时常量池溢出

**原理**：
- 使用`String.intern()`方法将字符串添加到常量池
- 使用List保持引用，防止被GC回收

**JDK版本差异**：
- JDK 6：字符串常量池在永久代，触发 `PermGen space`
- JDK 7+：字符串常量池移到Java堆，触发 `Java heap space`

**关键代码**：
```java
while (true) {
    String str = String.valueOf(i++).intern();
    stringList.add(str);
}
```

---

### 2.6 DirectMemoryOutOfMemory - 直接内存溢出

**作用**：演示直接内存(堆外内存)溢出 (`java.lang.OutOfMemoryError: Direct buffer memory`)

**原理**：
- 使用NIO的`ByteBuffer.allocateDirect()`分配直接内存
- 或使用Unsafe类直接分配堆外内存
- 当直接内存超过`-XX:MaxDirectMemorySize`限制时触发OOM

**两种分配方式**：

**方式1：ByteBuffer（推荐）**
```java
ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024); // 1MB
```

**方式2：Unsafe（需要特殊权限）**
```java
Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
unsafeField.setAccessible(true);
Unsafe unsafe = (Unsafe) unsafeField.get(null);
unsafe.allocateMemory(1024 * 1024);
```

---

### 2.7 OutOfMemoryTestRunner - 测试运行器

**作用**：提供统一的入口来运行各种OOM异常演示程序

**支持的测试类型**：

| 参数 | 说明 |
|------|------|
| `heap` | Java堆溢出测试 |
| `stack` | 虚拟机栈溢出(StackOverflowError)测试 |
| `stackoom` | 虚拟机栈内存溢出(OutOfMemoryError)测试 |
| `methodarea` | 方法区溢出测试 |
| `constantpool` | 运行时常量池溢出测试 |
| `direct` | 直接内存溢出测试 |
| `all` | 运行所有测试 |

**使用方式**：
```bash
# 命令行模式
java OutOfMemoryTestRunner heap

# 交互模式（不带参数）
java OutOfMemoryTestRunner
```

## 三、代码执行预期结果

### 3.1 编译项目

```bash
cd linsir-abc/linsir-abc-core
mvn clean compile
mvn dependency:copy-dependencies -DoutputDirectory=target/lib
```

### 3.2 执行测试

#### 1. Java堆溢出测试

**执行命令**：
```bash
java -cp target/classes -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError \
  com.linsir.abc.core.jvm.memory.heap.HeapOutOfMemory
```

**预期输出**：
```
开始创建对象，准备触发堆内存溢出...
VM参数: -Xms20m -Xmx20m
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pidxxxx.hprof ...
Heap dump file created [xxxx bytes in x.xxx secs]
捕获到OutOfMemoryError: Java heap space
已创建对象数量: x
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
```

---

#### 2. 栈深度溢出测试

**执行命令**：
```bash
java -cp target/classes -Xss180k \
  com.linsir.abc.core.jvm.memory.stack.StackOverflowError
```

**预期输出**：
```
开始递归调用，准备触发栈溢出...
VM参数: -Xss180k
捕获到StackOverflowError
栈深度: xxxx
Exception in thread "main" java.lang.StackOverflowError
    at com.linsir.abc.core.jvm.memory.stack.StackOverflowError.recursiveCall(...)
    ...
```

---

#### 3. 直接内存溢出测试

**执行命令**：
```bash
java -cp target/classes -Xmx20m -XX:MaxDirectMemorySize=10m \
  com.linsir.abc.core.jvm.memory.direct.DirectMemoryOutOfMemory
```

**预期输出**：
```
开始分配直接内存，准备触发OOM...
VM参数: -Xmx20m -XX:MaxDirectMemorySize=10m
分配方式: ByteBuffer
使用ByteBuffer.allocateDirect()分配直接内存...
已分配 10 MB 直接内存
捕获到OutOfMemoryError: Cannot reserve 1048576 bytes of direct buffer memory
已分配内存: 10 MB
Exception in thread "main" java.lang.OutOfMemoryError: Direct buffer memory
```

---

#### 4. 方法区溢出测试

**执行命令**：
```bash
java --add-opens java.base/java.lang=ALL-UNNAMED \
  -cp "target/classes;target/lib/*" \
  -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m \
  com.linsir.abc.core.jvm.memory.methodarea.MethodAreaOutOfMemory
```

**⚠️ 注意**：JDK 9+ 需要添加 `--add-opens` 参数以允许CGLib访问内部API。

**预期输出**：
```
开始生成动态类，准备触发方法区内存溢出...
JDK 7 VM参数: -XX:PermSize=10m -XX:MaxPermSize=10m
JDK 8+ VM参数: -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m
已生成 100 个动态类
已生成 200 个动态类
...
已生成 800 个动态类
Exception in thread "main" java.lang.OutOfMemoryError: Metaspace
```

---

#### 5. 运行时常量池溢出测试

**执行命令**：
```bash
java -cp target/classes -Xms20m -Xmx20m \
  com.linsir.abc.core.jvm.memory.methodarea.RuntimeConstantPoolOutOfMemory
```

**预期输出**：
```
开始添加字符串到常量池，准备触发OOM...
JDK 6 VM参数: -XX:PermSize=10m -XX:MaxPermSize=10m
JDK 7+ VM参数: -Xms20m -Xmx20m
已添加 10000 个字符串到常量池
已添加 20000 个字符串到常量池
...
已添加 360000 个字符串到常量池
捕获到OutOfMemoryError: Java heap space
已添加字符串数量: 360145
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
```

---

#### 6. 使用测试运行器

**执行命令**：
```bash
java -cp target/classes com.linsir.abc.core.jvm.memory.oom.OutOfMemoryTestRunner
```

**交互模式示例**：
```
==============================================
    JVM OutOfMemoryError 异常测试运行器
==============================================

用法: java OutOfMemoryTestRunner <测试类型>

支持的测试类型:
  heap - Java堆溢出 (Heap OOM)
  stack - 虚拟机栈溢出 (StackOverflowError)
  stackoom - 虚拟机栈内存溢出 (Stack OOM)
  methodarea - 方法区溢出 (Method Area OOM)
  constantpool - 运行时常量池溢出 (Constant Pool OOM)
  direct - 直接内存溢出 (Direct Memory OOM)
  all - 运行所有测试

请输入测试类型 (或输入 'exit' 退出): heap
启动测试: Java堆溢出 (Heap OOM)
----------------------------------------------
【Java堆溢出测试】
VM参数建议: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
...
```

## 四、JVM参数速查表

| 参数 | 说明 | 示例 |
|------|------|------|
| `-Xms` | 堆初始大小 | `-Xms20m` |
| `-Xmx` | 堆最大大小 | `-Xmx20m` |
| `-Xss` | 线程栈大小 | `-Xss180k` |
| `-XX:MetaspaceSize` | 元空间初始大小 | `-XX:MetaspaceSize=10m` |
| `-XX:MaxMetaspaceSize` | 元空间最大大小 | `-XX:MaxMetaspaceSize=10m` |
| `-XX:MaxDirectMemorySize` | 直接内存最大大小 | `-XX:MaxDirectMemorySize=10m` |
| `-XX:+HeapDumpOnOutOfMemoryError` | OOM时生成堆转储 | - |
| `--add-opens` | 开放模块访问权限(JDK 9+) | `--add-opens java.base/java.lang=ALL-UNNAMED` |

## 五、注意事项

1. **堆转储文件**：使用 `-XX:+HeapDumpOnOutOfMemoryError` 参数可以在OOM时自动生成 `.hprof` 文件，用于后续分析。

2. **JDK版本差异**：
   - 永久代(PermGen) vs 元空间(Metaspace)：JDK 8将方法区从永久代移到元空间
   - 字符串常量池位置：JDK 7从永久代移到Java堆

3. **CGLib依赖**：方法区溢出测试需要CGLib库，已添加到pom.xml。

4. **模块系统(JDK 9+)**：使用CGLib需要添加 `--add-opens` 参数开放内部API访问权限。

5. **系统稳定性**：`StackOutOfMemory` 测试可能导致系统假死，建议在隔离环境测试。
