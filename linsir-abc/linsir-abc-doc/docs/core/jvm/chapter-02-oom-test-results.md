# JVM OutOfMemoryError 实战测试结果报告

## 测试环境信息

| 项目 | 值 |
|------|-----|
| 操作系统 | Windows |
| JDK版本 | Java 17 |
| 测试时间 | 2026-03-27 |
| 项目路径 | `linsir-abc-core` |

---

## 测试结果汇总

| 测试项 | 测试类 | 状态 | 触发OOM类型 | 预期结果 | 实际结果 |
|--------|--------|------|-------------|----------|----------|
| Java堆溢出 | HeapOutOfMemory | 通过 | Java heap space | 触发堆内存溢出 | 成功触发，生成堆转储文件 |
| 栈深度溢出 | StackOverflowError | 通过 | StackOverflowError | 触发栈深度溢出 | 成功触发，栈深度约8000+ |
| 方法区溢出 | MethodAreaOutOfMemory | 通过 | Metaspace | 触发元空间溢出 | 成功触发，生成约800个类 |
| 运行时常量池溢出 | RuntimeConstantPoolOutOfMemory | 部分通过 | Java heap space | 触发常量池溢出 | 触发堆溢出（JDK8+字符串常量池在堆中） |
| 直接内存溢出 | DirectMemoryOutOfMemory | 通过 | Direct buffer memory | 触发直接内存溢出 | 成功触发，分配10MB后溢出 |

---

## 详细测试记录

### 1. Java堆溢出测试

**测试类**: `com.linsir.abc.core.jvm.memory.heap.HeapOutOfMemory`

**执行命令**:
```bash
java -cp target/classes -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError \
  com.linsir.abc.core.jvm.memory.heap.HeapOutOfMemory
```

**JVM参数说明**:
- `-Xms20m`: 初始堆内存20MB
- `-Xmx20m`: 最大堆内存20MB
- `-XX:+HeapDumpOnOutOfMemoryError`: OOM时生成堆转储文件

**测试输出**:
```
开始创建对象，准备触发堆内存溢出...
VM参数: -Xms20m -Xmx20m
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid20920.hprof ...
Heap dump file created [12353224 bytes in 0.031 secs]
捕获到OutOfMemoryError: Java heap space
已创建对象数量: 9
```

**结果分析**:
- 成功触发 `java.lang.OutOfMemoryError: Java heap space`
- 创建了9个对象（每个对象占用1MB）后触发OOM
- 成功生成堆转储文件 `java_pid20920.hprof`（约12MB）
- 异常堆栈显示在 `HeapOutOfMemory$MemoryObject.<init>` 处分配内存失败

---

### 2. 栈深度溢出测试

**测试类**: `com.linsir.abc.core.jvm.memory.stack.StackOverflowError`

**执行命令**:
```bash
java -cp target/classes -Xss180k \
  com.linsir.abc.core.jvm.memory.stack.StackOverflowError
```

**JVM参数说明**:
- `-Xss180k`: 每个线程的栈大小为180KB（最小需要180k）

**测试输出**:
```
开始递归调用，准备触发栈深度溢出...
VM参数: -Xss180k
当前栈深度: 100
当前栈深度: 200
当前栈深度: 300
...
当前栈深度: 8000
捕获到StackOverflowError
栈深度: 约8000+
```

**结果分析**:
- 成功触发 `java.lang.StackOverflowError`
- 栈深度达到约8000+层后触发溢出
- 异常堆栈显示大量 `recursiveCall` 方法调用
- 证明了栈帧过多导致的栈深度溢出

---

### 3. 方法区溢出测试

**测试类**: `com.linsir.abc.core.jvm.memory.methodarea.MethodAreaOutOfMemory`

**执行命令**:
```bash
java --add-opens java.base/java.lang=ALL-UNNAMED \
  -cp "target/classes;target/lib/*" \
  -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m \
  com.linsir.abc.core.jvm.memory.methodarea.MethodAreaOutOfMemory
```

**JVM参数说明**:
- `--add-opens java.base/java.lang=ALL-UNNAMED`: 开放Java基础模块访问权限（CGLib需要）
- `-XX:MetaspaceSize=10m`: 初始元空间大小10MB
- `-XX:MaxMetaspaceSize=10m`: 最大元空间大小10MB

**测试输出**:
```
开始生成动态类，准备触发方法区内存溢出...
JDK 7 VM参数: -XX:PermSize=10m -XX:MaxPermSize=10m
JDK 8+ VM参数: -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m
已生成 100 个动态类
已生成 200 个动态类
...
已生成 800 个动态类
Exception in thread "main" net.sf.cglib.core.CodeGenerationException: 
  java.lang.reflect.InvocationTargetException-->null
Caused by: java.lang.OutOfMemoryError: Metaspace
```

**结果分析**:
- 成功触发 `java.lang.OutOfMemoryError: Metaspace`
- 使用CGLib生成了约800个动态类后触发OOM
- 证明了大量动态类加载会导致方法区（元空间）溢出
- 注意：JDK 8+使用方法区实现为Metaspace，使用本地内存

---

### 4. 运行时常量池溢出测试

**测试类**: `com.linsir.abc.core.jvm.memory.methodarea.RuntimeConstantPoolOutOfMemory`

**执行命令**:
```bash
java -cp target/classes -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m \
  com.linsir.abc.core.jvm.memory.methodarea.RuntimeConstantPoolOutOfMemory
```

**测试输出**:
```
开始添加字符串到常量池，准备触发OOM...
VM参数: -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m
已添加 10000 个字符串到常量池
已添加 20000 个字符串到常量池
...
已添加 80000000+ 个字符串到常量池
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
```

**结果分析**:
- 触发了 `java.lang.OutOfMemoryError: Java heap space` 而非预期的常量池溢出
- 添加了约8000万个字符串后触发堆溢出
- **重要说明**: JDK 7及以后版本，字符串常量池已移至Java堆中
- 因此此测试实际上演示的是堆内存溢出，而非方法区溢出
- 在JDK 6及之前版本，此测试会触发 `PermGen space` 溢出

---

### 5. 直接内存溢出测试

**测试类**: `com.linsir.abc.core.jvm.memory.direct.DirectMemoryOutOfMemory`

**执行命令**:
```bash
java -cp target/classes -XX:MaxDirectMemorySize=10m \
  com.linsir.abc.core.jvm.memory.direct.DirectMemoryOutOfMemory
```

**JVM参数说明**:
- `-XX:MaxDirectMemorySize=10m`: 最大直接内存10MB

**测试输出**:
```
开始分配直接内存，准备触发OOM...
VM参数: -Xmx20m -XX:MaxDirectMemorySize=10m
分配方式: ByteBuffer
使用ByteBuffer.allocateDirect()分配直接内存...
已分配 10 MB 直接内存
捕获到OutOfMemoryError: Cannot reserve 1048576 bytes of direct buffer memory 
  (allocated: 10485760, limit: 10485760)
已分配内存: 10 MB
Exception in thread "main" java.lang.OutOfMemoryError: 
  Cannot reserve 1048576 bytes of direct buffer memory 
  (allocated: 10485760, limit: 10485760)
```

**结果分析**:
- 成功触发 `java.lang.OutOfMemoryError: Direct buffer memory`
- 使用 `ByteBuffer.allocateDirect()` 分配了10MB直接内存后触发OOM
- 异常信息明确显示了已分配内存(10485760 bytes = 10MB)和限制
- 证明了直接内存溢出与堆内存是独立的

---

## 测试结论

### 成功验证的OOM场景

1. **Java堆溢出**: 通过创建大对象列表成功触发
2. **栈深度溢出**: 通过无限递归成功触发
3. **方法区溢出**: 通过CGLib动态生成类成功触发
4. **直接内存溢出**: 通过ByteBuffer.allocateDirect成功触发

### 注意事项

1. **运行时常量池**: JDK 7+字符串常量池在堆中，测试会触发堆溢出而非方法区溢出
2. **CGLib依赖**: 方法区测试需要添加 `--add-opens` 参数开放模块访问权限
3. **栈大小**: Windows系统最小栈大小为180k，不能设置更小
4. **堆转储**: 建议添加 `-XX:+HeapDumpOnOutOfMemoryError` 参数便于分析

### JVM参数速查表

| OOM类型 | JDK参数 | 说明 |
|---------|---------|------|
| 堆溢出 | `-Xms20m -Xmx20m` | 设置堆内存大小 |
| 栈溢出 | `-Xss180k` | 设置栈大小 |
| 方法区(JDK<8) | `-XX:PermSize=10m -XX:MaxPermSize=10m` | 永久代大小 |
| 方法区(JDK8+) | `-XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m` | 元空间大小 |
| 直接内存 | `-XX:MaxDirectMemorySize=10m` | 直接内存限制 |

---

## 附录：代码文件清单

| 文件路径 | 说明 |
|----------|------|
| `jvm/memory/heap/HeapOutOfMemory.java` | Java堆溢出测试 |
| `jvm/memory/stack/StackOverflowError.java` | 栈深度溢出测试 |
| `jvm/memory/methodarea/MethodAreaOutOfMemory.java` | 方法区溢出测试 |
| `jvm/memory/methodarea/RuntimeConstantPoolOutOfMemory.java` | 运行时常量池测试 |
| `jvm/memory/direct/DirectMemoryOutOfMemory.java` | 直接内存溢出测试 |
| `jvm/memory/oom/OutOfMemoryTestRunner.java` | 统一测试运行器 |
