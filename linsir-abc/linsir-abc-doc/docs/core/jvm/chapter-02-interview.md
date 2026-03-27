# 第2章 面试题总结 - Java内存区域与内存溢出异常

## 一、JVM内存区域基础

### 1.1 JVM内存区域划分

**Q1：JVM内存区域有哪些？哪些是线程私有的，哪些是线程共享的？**

**答案：**

JVM将内存划分为5大区域：

```
┌─────────────────────────────────────────────────────────┐
│                    JVM 运行时数据区                       │
├─────────────────────────────────────────────────────────┤
│  线程共享区域                                             │
│  ├── 堆（Heap）：存放对象实例                              │
│  └── 方法区（Method Area）：类信息、常量、静态变量           │
├─────────────────────────────────────────────────────────┤
│  线程私有区域                                             │
│  ├── 程序计数器（PC Register）：记录执行位置               │
│  ├── 虚拟机栈（VM Stack）：Java方法执行的内存模型           │
│  └── 本地方法栈（Native Method Stack）：Native方法执行      │
└─────────────────────────────────────────────────────────┘
```

**线程私有**：程序计数器、虚拟机栈、本地方法栈（随线程生灭）
**线程共享**：堆、方法区（随JVM启动创建）

---

### 1.2 程序计数器

**Q2：程序计数器有什么作用？为什么它是唯一不会发生OOM的区域？**

**答案：**

**作用**：
- 记录当前线程执行的字节码指令地址（行号指示器）
- 线程切换后恢复到正确的执行位置

**不会发生OOM的原因**：
- 程序计数器占用的内存空间非常小（几乎可忽略）
- 只存储一个地址值，所需内存大小是固定的
- 没有动态扩展的需求

**特殊情况**：执行Native方法时，计数器值为空（Undefined）

---

### 1.3 虚拟机栈

**Q3：虚拟机栈的栈帧包含哪些部分？局部变量表如何存储数据？**

**答案：**

**栈帧结构**：

| 组成部分 | 说明 |
|---------|------|
| **局部变量表** | 存储方法参数和局部变量，以Slot为单位 |
| **操作数栈** | 用于计算的操作数存储，LIFO结构 |
| **动态连接** | 指向运行时常量池的方法引用 |
| **方法返回地址** | 方法执行完毕后的返回位置 |
| **附加信息** | 调试信息等 |

**局部变量表存储规则**：
- 以**变量槽（Slot）**为最小单位，每个Slot 32位
- `long`和`double`占2个Slot（64位）
- 其他基本类型和引用类型占1个Slot
- 局部变量表大小在编译期确定

---

**Q4：什么情况下会发生栈溢出（StackOverflowError）？**

**答案：**

**触发条件**：
- 线程请求的栈深度超过虚拟机允许的最大深度
- 典型场景：**无限递归**

**示例代码**：
```java
public class StackOverflowDemo {
    private int stackLength = 1;
    
    public void stackLeak() {
        stackLength++;
        stackLeak();  // 无限递归
    }
    
    public static void main(String[] args) {
        StackOverflowDemo demo = new StackOverflowDemo();
        try {
            demo.stackLeak();
        } catch (StackOverflowError e) {
            System.out.println("栈深度: " + demo.stackLength);
            throw e;
        }
    }
}
```

**解决方案**：
- 调整栈大小：`-Xss256k` 或 `-Xss1m`
- 优化程序，避免过深的递归调用

---

### 1.4 堆内存

**Q5：Java堆为什么要分为新生代和老年代？**

**答案：**

**分代的原因**：
- **对象生命周期不同**：大多数对象朝生夕死，少数对象长期存活
- **提高GC效率**：针对不同代采用不同的回收算法

**堆内存划分**：

```
Java堆
├── 新生代（Young Generation，占1/3）
│   ├── Eden区（8/10）：新对象首先分配
│   ├── Survivor0（1/10）：存活对象复制区
│   └── Survivor1（1/10）：存活对象复制区
│
└── 老年代（Old Generation，占2/3）
    └── 长期存活的对象
```

**对象晋升过程**：
```
对象创建 → Eden区 → Minor GC存活 → Survivor区
                                      ↓
                    经历默认15次GC仍存活
                                      ↓
                                老年代
```

---

**Q6：新生代中Eden和Survivor区的比例是多少？为什么这样设计？**

**答案：**

**默认比例**：Eden : Survivor0 : Survivor1 = 8 : 1 : 1

**这样设计的原因**：
- **Eden区大**：大多数新对象在Eden区分配，很快就会被回收
- **Survivor区小**：仅用于存放Minor GC后的存活对象
- **两个Survivor**：采用复制算法，一个空闲，一个使用，交替进行

**相关参数**：
```bash
-XX:SurvivorRatio=8  # Eden/Survivor比例，默认8
```

---

**Q7：堆内存的常见配置参数有哪些？**

**答案：**

| 参数 | 说明 | 示例 |
|------|------|------|
| `-Xms` | 堆初始大小 | `-Xms512m` |
| `-Xmx` | 堆最大大小 | `-Xmx2g` |
| `-Xmn` | 新生代大小 | `-Xmn256m` |
| `-XX:NewRatio` | 老年代/新生代比例 | `-XX:NewRatio=2`（默认） |
| `-XX:SurvivorRatio` | Eden/Survivor比例 | `-XX:SurvivorRatio=8`（默认） |

**建议**：`-Xms` 和 `-Xmx` 设置为相同值，避免运行时动态扩展带来的性能开销。

---

### 1.5 方法区

**Q8：方法区和永久代（PermGen）、元空间（Metaspace）是什么关系？**

**答案：**

**概念关系**：
- **方法区**：JVM规范定义的内存区域，存储类信息、常量、静态变量等
- **永久代（PermGen）**：JDK 7及之前的实现，在JVM堆内存中
- **元空间（Metaspace）**：JDK 8及之后的实现，在本地内存中

**对比**：

| 特性 | 永久代（PermGen） | 元空间（Metaspace） |
|------|-------------------|---------------------|
| **位置** | JVM堆内存 | 本地内存（Native Memory） |
| **大小限制** | 固定，`-XX:MaxPermSize` | 受限于系统内存 |
| **OOM风险** | 高，容易出现`PermGen space` | 低，但可能耗尽系统内存 |
| **垃圾回收** | 频率低，效率差 | 更高效的GC |
| **参数** | `-XX:PermSize`, `-XX:MaxPermSize` | `-XX:MetaspaceSize`, `-XX:MaxMetaspaceSize` |

**为什么移除永久代？**
1. 避免OOM：永久代大小固定，类加载过多时容易溢出
2. 与JRockit统一：Oracle收购BEA后统一虚拟机实现
3. 简化GC：减少Full GC的复杂度

---

**Q9：方法区存储哪些内容？**

**答案：**

| 数据类型 | 说明 |
|---------|------|
| **类型信息** | 类/接口/枚举/注解的完整名称、修饰符、父类、接口列表 |
| **字段信息** | 字段名称、类型、修饰符 |
| **方法信息** | 方法名称、返回类型、参数类型、修饰符、字节码 |
| **静态变量** | 类变量（static修饰） |
| **常量池** | 编译期生成的字面量和符号引用 |
| **JIT编译代码** | 即时编译器编译后的本地机器码 |

---

### 1.6 直接内存

**Q10：什么是直接内存（Direct Memory）？它有什么特点？**

**答案：**

**定义**：直接内存不是JVM运行时数据区的一部分，是堆外内存，通过NIO的`ByteBuffer.allocateDirect()`分配。

**特点**：

| 特性 | 说明 |
|------|------|
| **位置** | 堆外内存，不受JVM堆大小限制 |
| **分配** | 通过Unsafe类或`ByteBuffer.allocateDirect()` |
| **回收** | 不受GC直接管理，需要显式释放或等待Cleaner |
| **性能** | 避免了Java堆和Native堆之间的数据复制 |

**使用场景**：
- NIO操作（文件通道、网络通道）
- 大数据处理
- 缓存系统

**OOM类型**：`java.lang.OutOfMemoryError: Direct buffer memory`

**参数**：`-XX:MaxDirectMemorySize=512m`

---

## 二、内存溢出异常（OOM）

### 2.1 OOM类型与排查

**Q11：JVM中哪些区域可能发生OOM？分别如何排查？**

**答案：**

| OOM类型 | 触发区域 | 异常信息 | 排查方法 |
|---------|---------|---------|---------|
| **堆内存溢出** | 堆 | `Java heap space` | 堆转储分析、MAT工具 |
| **GC效率低** | 堆 | `GC overhead limit exceeded` | 优化代码、调整GC策略 |
| **永久代溢出** | 方法区（JDK<8） | `PermGen space` | 增大PermSize |
| **元空间溢出** | 方法区（JDK8+） | `Metaspace` | 增大MetaspaceSize |
| **栈溢出** | 栈 | `StackOverflowError` | 优化递归、增大-Xss |
| **无法创建线程** | 栈 | `Unable to create new native thread` | 减小栈大小、限制线程数 |
| **直接内存溢出** | 直接内存 | `Direct buffer memory` | 限制直接内存大小 |

---

**Q12：发生OOM后，JVM还能继续运行吗？**

**答案：**

**取决于OOM类型和代码处理**：

1. **未捕获的OOM**：线程终止，但JVM可能继续运行
2. **捕获的OOM**：如果代码捕获了OOM并继续执行，JVM可以继续运行
3. **关键线程OOM**：如GC线程发生OOM，可能导致JVM崩溃

**最佳实践**：
- 不要捕获OOM后继续执行业务逻辑
- OOM后应该记录日志、发送告警、优雅退出

---

**Q13：如何排查Java堆内存溢出？**

**答案：**

**排查步骤**：

1. **开启堆转储**：
   ```bash
   -XX:+HeapDumpOnOutOfMemoryError
   -XX:HeapDumpPath=/path/to/dump.hprof
   ```

2. **分析堆转储文件**（使用MAT工具）：
   - 查找大对象
   - 分析引用链
   - 确认内存泄漏点

3. **判断类型**：
   - **内存泄漏**：对象不再使用但仍被引用
   - **内存溢出**：对象确实需要这么多内存

4. **解决方案**：
   - 内存泄漏：修复代码，释放无用引用
   - 内存溢出：增大堆内存（`-Xmx`）

---

**Q14：元空间（Metaspace）溢出的常见原因有哪些？**

**答案：**

**常见原因**：

1. **动态类生成过多**：
   - CGLib动态代理
   - 大量JSP动态编译
   - Groovy等动态语言

2. **类加载器泄漏**：
   - OSGi等动态模块系统
   - 热部署未正确卸载类加载器

3. **反射使用不当**：
   - 频繁使用反射生成类

**排查方法**：
```bash
# 查看类加载情况
jcmd <pid> VM.classloader_stats

# 查看元空间使用
jstat -gc <pid>
```

**解决方案**：
- 增大元空间大小：`-XX:MaxMetaspaceSize=512m`
- 检查动态类生成代码
- 确保类加载器正确卸载

---

## 三、综合对比题

### 3.1 堆 vs 栈

**Q15：堆和栈的区别是什么？**

**答案：**

| 对比维度 | 堆（Heap） | 栈（Stack） |
|---------|-----------|------------|
| **存储内容** | 对象实例、数组 | 局部变量、方法参数、操作数 |
| **线程共享** | 线程共享 | 线程私有 |
| **生命周期** | 对象存活期间 | 方法调用期间 |
| **管理方式** | GC自动回收 | 自动分配/释放 |
| **空间大小** | 大（-Xmx设置） | 小（-Xss设置） |
| **溢出异常** | OutOfMemoryError | StackOverflowError |
| **存取速度** | 慢（需GC） | 快（LIFO） |
| **碎片问题** | 有（GC整理） | 无 |

---

### 3.2 内存分配策略

**Q16：对象在内存中是如何分配的？**

**答案：**

**对象分配流程**：

```
1. 对象创建
   ↓
2. 尝试在栈上分配（逃逸分析）
   ├── 未逃逸 → 栈分配（随方法结束销毁）
   └── 逃逸 → 继续下一步
   ↓
3. 判断对象大小
   ├── 大对象（超过阈值）→ 直接进入老年代
   └── 小对象 → 继续下一步
   ↓
4. 在Eden区分配
   ↓
5. Minor GC后存活
   ├── 存活次数 < 阈值 → 在Survivor区复制
   └── 存活次数 >= 阈值 → 晋升老年代
```

**相关参数**：
```bash
-XX:PretenureSizeThreshold=1048576  # 大对象阈值（字节）
-XX:MaxTenuringThreshold=15          # 晋升阈值（默认15）
```

---

## 四、实战场景题

### 4.1 场景分析

**Q17：线上服务出现`Java heap space` OOM，如何快速定位问题？**

**答案：**

**快速定位步骤**：

1. **确认OOM类型**：
   ```bash
   grep "OutOfMemoryError" /var/log/app/*.log
   ```

2. **获取堆转储文件**：
   - 如果已配置`-XX:+HeapDumpOnOutOfMemoryError`，直接获取.hprof文件
   - 否则使用：
     ```bash
     jmap -dump:format=b,file=heap.hprof <pid>
     ```

3. **使用MAT分析**：
   - 打开.hprof文件
   - 查看Dominator Tree，找出占用内存最大的对象
   - 查看Path to GC Roots，分析引用链

4. **常见场景**：
   - **缓存未设置上限**：Guava Cache、Caffeine未配置最大容量
   - **集合无限增长**：List/Map持续添加元素
   - **大对象**：一次性加载大量数据到内存
   - **内存泄漏**：静态集合持有对象引用

5. **临时解决方案**：
   - 重启服务
   - 增大堆内存（`-Xmx`）
   - 限流降级

---

**Q18：如何模拟各种OOM场景用于测试？**

**答案：**

**1. 堆内存溢出**：
```java
// VM Args: -Xms20m -Xmx20m
List<byte[]> list = new ArrayList<>();
while (true) {
    list.add(new byte[1024 * 1024]); // 1MB
}
```

**2. 栈溢出**：
```java
// VM Args: -Xss128k
public void stackLeak() {
    stackLeak();  // 无限递归
}
```

**3. 元空间溢出**：
```java
// VM Args: -XX:MaxMetaspaceSize=32m
while (true) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(Target.class);
    enhancer.setUseCache(false);
    enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) 
        -> proxy.invokeSuper(obj, args));
    enhancer.create();
}
```

**4. 直接内存溢出**：
```java
// VM Args: -XX:MaxDirectMemorySize=10m
ByteBuffer.allocateDirect(1024 * 1024 * 100); // 100MB
```

---

## 五、JVM参数速查表

### 5.1 内存区域参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `-Xms` | 堆初始大小 | 物理内存的1/64 |
| `-Xmx` | 堆最大大小 | 物理内存的1/4 |
| `-Xmn` | 新生代大小 | 堆的1/3 |
| `-Xss` | 线程栈大小 | 1MB（Linux） |
| `-XX:NewRatio` | 老年代/新生代比例 | 2 |
| `-XX:SurvivorRatio` | Eden/Survivor比例 | 8 |
| `-XX:PermSize` | 永久代初始大小（JDK<8） | - |
| `-XX:MaxPermSize` | 永久代最大大小（JDK<8） | - |
| `-XX:MetaspaceSize` | 元空间初始大小（JDK8+） | 21MB |
| `-XX:MaxMetaspaceSize` | 元空间最大大小（JDK8+） | 无限制 |
| `-XX:MaxDirectMemorySize` | 直接内存最大大小 | 与-Xmx相同 |

### 5.2 OME排查参数

| 参数 | 说明 |
|------|------|
| `-XX:+HeapDumpOnOutOfMemoryError` | OOM时生成堆转储 |
| `-XX:HeapDumpPath=/path` | 堆转储文件路径 |
| `-XX:+PrintGCDetails` | 打印GC详细信息 |
| `-XX:+PrintGCDateStamps` | 打印GC时间戳 |
| `-Xloggc:/path/gc.log` | GC日志文件路径 |

---

## 参考资料

1. 《深入理解Java虚拟机》（第3版）第2章
2. JVM规范：https://docs.oracle.com/javase/specs/jvms/se17/html/
3. Java SE HotSpot Virtual Machine Garbage Collection Tuning Guide
