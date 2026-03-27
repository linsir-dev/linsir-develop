# 第3章 垃圾收集器与内存分配策略 - 代码说明文档

本文档详细说明 `linsir-abc-core` 模块中 JVM GC 相关示例代码的结构、作用及预期执行结果。

## 一、代码结构

```
com.linsir.abc.core.jvm.gc/
├── GCTestRunner.java                 # 统一测试入口
├── reference/                        # 引用类型示例
│   ├── SoftReferenceExample.java     # 软引用示例
│   ├── WeakReferenceExample.java     # 弱引用示例
│   ├── PhantomReferenceExample.java  # 虚引用示例
│   └── ReferenceTypeComparison.java  # 引用类型对比
├── finalize/                         # Finalize机制示例
│   ├── FinalizeEscapeGC.java         # Finalize逃逸示例
│   └── ResourceCleanupExample.java   # 资源清理替代方案
├── allocation/                       # 内存分配策略示例
│   ├── EdenAllocation.java           # Eden区分配
│   ├── PretenureSizeThreshold.java   # 大对象直接进入老年代
│   ├── TenuringThreshold.java        # 长期存活对象晋升
│   └── HandlePromotionFailure.java   # 空间分配担保
└── utils/                            # 工具类
    └── GCLogAnalyzer.java            # GC日志分析器
```

## 二、代码作用与预期结果

### 2.1 引用类型示例 (reference/)

#### 2.1.1 SoftReferenceExample - 软引用示例

**代码作用：**
演示软引用（Soft Reference）的特性，展示软引用对象在内存充足时保留、内存不足时被回收的行为，以及软引用在缓存场景中的应用。

**核心方法：**
- `demonstrateBasicUsage()` - 演示软引用基本用法
- `demonstrateGcUnderMemoryPressure()` - 演示内存压力下软引用回收
- `demonstrateCacheScenario()` - 演示软引用作为缓存的应用

**预期执行结果：**
```
=== 软引用基本用法演示 ===
强引用对象: java.lang.Object@xxxx
软引用获取对象: java.lang.Object@xxxx
断开强引用后，软引用获取对象: java.lang.Object@xxxx
软引用对象在内存不足时会被自动回收

=== 内存压力下软引用回收演示 ===
最大堆内存: 20MB
添加第 1 个软引用对象
  当前存活对象数: 1/1
...
添加第 10 个软引用对象
  当前存活对象数: 1/10  ← 开始回收
...
添加第 20 个软引用对象
  当前存活对象数: 2/20

最终存活对象统计:
  对象 1-18: 已被回收
  对象 19-20: 存活 (1MB)
总计: 2/20 个对象存活
```

**VM参数建议：** `-Xms20m -Xmx20m`

---

#### 2.1.2 WeakReferenceExample - 弱引用示例

**代码作用：**
演示弱引用（Weak Reference）的特性，展示弱引用对象在下次GC时被回收的行为，以及WeakHashMap和规范化映射的应用场景。

**核心方法：**
- `demonstrateBasicUsage()` - 演示弱引用基本用法
- `demonstrateDifferenceWithStrongRef()` - 对比弱引用与强引用
- `demonstrateWeakHashMap()` - 演示WeakHashMap自动清理
- `demonstrateCanonicalMapping()` - 演示规范化映射

**预期执行结果：**
```
=== 弱引用基本用法演示 ===
GC前获取对象: java.lang.Object@xxxx
调用System.gc()...
GC后获取对象: null
对象已被回收

=== 弱引用与强引用区别演示 ===
GC前:
  强引用对象: 存活
  弱引用(有强引用): 存活
  弱引用(无强引用): 存活

GC后:
  强引用对象: 存活
  弱引用(有强引用): 存活
  弱引用(无强引用): null

断开强引用后GC:
  弱引用(原强引用): null

=== WeakHashMap演示 ===
GC前:
  HashMap大小: 1
  WeakHashMap大小: 1

断开强引用并GC后:
  HashMap大小: 1
  WeakHashMap大小: 0  ← 自动清理
```

---

#### 2.1.3 PhantomReferenceExample - 虚引用示例

**代码作用：**
演示虚引用（Phantom Reference）的特性，展示虚引用无法获取对象、用于跟踪对象回收状态的功能，以及在资源清理和直接内存管理中的应用。

**核心方法：**
- `demonstrateBasicUsage()` - 演示虚引用基本用法
- `demonstrateResourceCleanup()` - 演示资源清理场景
- `demonstrateDirectMemoryCleanup()` - 演示直接内存清理

**预期执行结果：**
```
=== 虚引用基本用法演示 ===
原始对象: java.lang.Object@xxxx
虚引用get()结果: null  ← 永远返回null
引用队列poll(): null

断开强引用
调用System.gc()...
等待对象被回收...

从引用队列中获取到: java.lang.ref.PhantomReference@xxxx
对象已被回收  ← 收到回收通知

=== 虚引用资源清理演示 ===
分配资源: resource_0
...
分配资源: resource_4
已分配资源数量: 5

释放部分引用后资源数量: 5

调用System.gc()...
等待Cleaner执行...

清理后资源数量: 2  ← 自动清理已回收对象的资源
```

---

#### 2.1.4 ReferenceTypeComparison - 引用类型对比

**代码作用：**
对比四种引用类型（强引用、软引用、弱引用、虚引用）的区别，展示它们在GC前后的行为差异和使用场景。

**核心方法：**
- `compareReferenceTypes()` - 对比四种引用类型基本特性
- `compareGcBehavior()` - 对比GC后行为
- `compareUseCases()` - 对比使用场景
- `demonstrateMemoryPressure()` - 演示内存压力下回收顺序

**预期执行结果：**
```
=== 四种引用类型对比 ===
1. 强引用: get()可获取，永不回收
2. 软引用: get()可获取，内存不足回收
3. 弱引用: get()可获取，下次GC回收
4. 虚引用: get()返回null，用于跟踪回收

=== GC后行为对比 ===
GC前: 全部存活
GC后（内存充足）:
  强引用: 存活
  软引用: 存活
  弱引用: null
  虚引用: null
引用队列状态: 有通知

=== 使用场景对比 ===
┌──────────┬────────────────────┬─────────────────────────────┐
│ 引用类型  │ 回收时机            │ 典型使用场景                 │
├──────────┼────────────────────┼─────────────────────────────┤
│ 强引用    │ 永不回收            │ 普通对象引用                 │
│ 软引用    │ 内存不足时回收       │ 缓存实现                     │
│ 弱引用    │ 下次GC时回收         │ WeakHashMap、规范化映射      │
│ 虚引用    │ 随时可能回收         │ 跟踪对象回收、资源清理       │
└──────────┴────────────────────┴─────────────────────────────┘
```

---

### 2.2 Finalize机制示例 (finalize/)

#### 2.2.1 FinalizeEscapeGC - Finalize逃逸示例

**代码作用：**
演示对象如何在finalize()方法中自我拯救，逃脱垃圾回收，同时展示finalize()方法只能被执行一次的特性。

**核心方法：**
- `demonstrateEscape()` - 演示对象自我拯救
- `demonstrateUnreliability()` - 演示finalize()的不可靠性
- `demonstrateAlternative()` - 演示替代方案

**预期执行结果：**
```
=== Finalize方法自我拯救演示 ===
创建对象: Object-1

--- 第一次拯救 ---
断开强引用: SAVE_HOOK = null
调用System.gc()...
等待Finalizer线程执行（500ms）...
[Object-1] finalize method executed!
[Object-1] Self-rescue: SAVE_HOOK = this
结果: 对象成功拯救自己！
[Object-1] yes, I am still alive :)

--- 第二次尝试（相同代码）---
再次断开强引用: SAVE_HOOK = null
调用System.gc()...
等待Finalizer线程执行（500ms）...
结果: 对象已死亡 :( 

结论：finalize()方法只会被执行一次！
```

**注意事项：**
- finalize()方法在JDK 9+中已被标记为废弃
- 不建议在生产代码中使用finalize()

---

#### 2.2.2 ResourceCleanupExample - 资源清理替代方案

**代码作用：**
演示finalize()的替代方案：try-with-resources和Cleaner（Java 9+），展示这些方案如何更可靠地管理资源。

**核心类：**
- `AutoCloseableResource` - 使用try-with-resources的资源类
- `CleanerResource` - 使用Cleaner的资源类

**核心方法：**
- `demonstrateTryWithResources()` - 演示try-with-resources用法
- `demonstrateCleaner()` - 演示Cleaner用法
- `compareApproaches()` - 对比各种方案

**预期执行结果：**
```
=== try-with-resources 演示 ===
1. 基本用法:
[Resource-1] 资源创建
[Resource-1] 使用资源
[Resource-1] 资源关闭（try-with-resources）← 自动关闭

2. 多个资源:
[Resource-A] 资源创建
[Resource-B] 资源创建
[Resource-A] 使用资源
[Resource-B] 使用资源
[Resource-B] 资源关闭（按相反顺序关闭）
[Resource-A] 资源关闭

3. 异常处理:
[Resource-Exception] 资源创建
[Resource-Exception] 使用资源
[Resource-Exception] 资源关闭（异常时仍关闭）
捕获异常: 模拟异常

=== Cleaner 演示 ===
3. GC自动清理:
[CleanerResource-2] 资源创建（使用Cleaner）
[CleanerResource-2] 使用资源
断开引用...
调用System.gc()...
等待Cleaner执行...
[CleanerResource-2] 资源清理（Cleaner）← GC后自动清理
```

---

### 2.3 内存分配策略示例 (allocation/)

#### 2.3.1 EdenAllocation - Eden区分配

**代码作用：**
演示对象在Eden区的分配过程，以及当Eden区空间不足时触发Minor GC的行为。

**VM参数：** `-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8`

**内存布局：**
- 堆总大小：20MB
- 新生代：10MB（Eden 8MB + Survivor from 1MB + Survivor to 1MB）
- 老年代：10MB

**预期执行结果：**
```
=== Eden区内存分配测试 ===
VM参数: -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
预期: Eden=8MB, Survivor(from)=1MB, Survivor(to)=1MB, 老年代=10MB

分配 allocation1 = new byte[2 * _1MB]  ← Eden区: 2MB
分配 allocation2 = new byte[2 * _1MB]  ← Eden区: 4MB
分配 allocation3 = new byte[2 * _1MB]  ← Eden区: 6MB
分配 allocation4 = new byte[4 * _1MB]  ← Eden区: 10MB > 8MB，触发Minor GC

[GC (Allocation Failure)  6487K->152K(9216K), 0.0049383 secs]
allocation1-3 被晋升到老年代或Survivor区
allocation4 分配在Eden区或老年代
```

---

#### 2.3.2 PretenureSizeThreshold - 大对象直接进入老年代

**代码作用：**
演示大对象（大于-XX:PretenureSizeThreshold设置值）直接在老年代分配的行为。

**VM参数：** `-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:PretenureSizeThreshold=3145728`

**注意：** 此参数只对Serial和ParNew收集器有效，G1收集器使用Humongous区域处理大对象。

**预期执行结果：**
```
=== 大对象直接进入老年代测试 ===
VM参数: -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8
        -XX:PretenureSizeThreshold=3145728 (3MB)

分配 allocation = new byte[4 * _1MB] (4MB > 3MB阈值)
预期: 对象直接在老年代分配，不触发Minor GC

分配完成:
  对象大小: 4MB
  堆内存: 总计=20MB, 已用=7MB, 空闲=13MB

观察GC日志:
  如果没有出现[GC]或[Full GC]，说明对象直接在老年代分配
  如果老年代使用量增加了4MB，说明分配成功
```

---

#### 2.3.3 TenuringThreshold - 长期存活对象晋升

**代码作用：**
演示对象在Survivor区中经过多次Minor GC后，根据年龄阈值晋升到老年代的过程。

**VM参数：** `-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:MaxTenuringThreshold=1 -XX:+PrintTenuringDistribution`

**预期执行结果：**
```
=== 对象年龄晋升测试 ===
1. 分配小对象 allocation1 = new byte[_1MB / 4]
   对象大小: 256KB
   预期位置: Eden区

2. 分配大对象触发Minor GC
   allocation2 = new byte[4 * _1MB]
   触发Minor GC后，allocation1应该进入Survivor区或老年代
   [GC (Allocation Failure) ...]

3. 再次分配触发第二次Minor GC
   allocation3 = new byte[4 * _1MB]
   allocation3 = null
   allocation3 = new byte[4 * _1MB]
   触发第二次Minor GC，allocation1年龄达到阈值，晋升老年代

观察GC日志中的年龄分布信息:
  - desired survivor size: 期望的Survivor空间大小
  - new threshold: 新的年龄阈值
  - age 1: 年龄为1的对象大小
```

---

#### 2.3.4 HandlePromotionFailure - 空间分配担保

**代码作用：**
演示Minor GC前的空间分配担保机制，展示虚拟机如何检查老年代空间是否足够容纳晋升对象。

**VM参数：** `-verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8`

**预期执行结果：**
```
=== 空间分配担保测试 ===
空间分配担保流程:
  1. 检查老年代空间 > 新生代总对象空间?
  2. 是 -> 安全进行Minor GC
  3. 否 -> 检查是否允许担保失败
  4. 允许 -> 检查老年代空间 > 历次晋升平均大小?
  5. 是 -> 尝试Minor GC（有风险）
  6. 否 -> 进行Full GC

1. 分配多个对象填满Eden区
   allocation1-3 各2MB，共6MB

2. 分配大对象触发Minor GC
   allocation4 = 4MB
   触发Minor GC，allocation1-3可能晋升到老年代
   [GC (Allocation Failure) ...]

3. 继续分配对象
   allocation5-6 各2MB

4. 再次分配大对象
   allocation7 = 4MB
   可能触发Full GC
   [Full GC (Ergonomics) ...]
```

---

### 2.4 工具类 (utils/)

#### 2.4.1 GCLogAnalyzer - GC日志分析器

**代码作用：**
解析和分析JVM GC日志，提取关键信息如GC类型、内存使用情况、GC耗时、回收效率等，并生成统计报告和性能建议。

**核心类：**
- `GCEvent` - GC事件记录，包含GC类型、内存变化、耗时等信息
- `GCStatistics` - GC统计信息，汇总多次GC的数据
- `GCLogAnalyzer` - 日志解析器

**核心方法：**
- `parseLogLine(String)` - 解析单行GC日志
- `analyzeLogFile(String)` - 分析GC日志文件
- `analyzeLogContent(String)` - 分析GC日志内容
- `generateReport(GCStatistics)` - 生成GC报告

**预期执行结果：**
```
示例GC日志分析:

[GC (Allocation Failure)  6487K->152K(9216K), 0.0049383 secs]
[GC (Allocation Failure)  6296K->152K(9216K), 0.0034567 secs]
[Full GC (Ergonomics)  8192K->6144K(19456K), 0.1234567 secs]
[GC (Allocation Failure)  6144K->152K(9216K), 0.0023456 secs]

=== GC统计信息 ===
总GC次数: 4
  - Minor GC: 3
  - Full GC: 1
总GC时间: 0.134s
平均GC时间: 0.034s
最大GC时间: 0.123s
最小GC时间: 0.002s
总回收内存: 20MB

=== GC事件详情 ===
[1] GCEvent{type=MINOR_GC, duration=0.005s, heap: 6487K->152K(9216K), reclaimed=6335K, ratio=97.66%}
[2] GCEvent{type=MINOR_GC, duration=0.003s, heap: 6296K->152K(9216K), reclaimed=6144K, ratio=97.59%}
[3] GCEvent{type=FULL_GC, duration=0.123s, heap: 8192K->6144K(19456K), reclaimed=2048K, ratio=25.00%}
[4] GCEvent{type=MINOR_GC, duration=0.002s, heap: 6144K->152K(9216K), reclaimed=5992K, ratio=97.53%}

=== 性能建议 ===
⚠ Full GC次数过多，建议检查：
  - 老年代内存配置是否充足
  - 是否存在内存泄漏
  - 大对象是否直接进入老年代
```

---

### 2.5 统一测试入口

#### 2.5.1 GCTestRunner - GC测试运行器

**代码作用：**
提供统一的交互式测试入口，方便运行和验证所有GC相关示例代码。

**使用方法：**
```bash
java -cp target/classes com.linsir.abc.core.jvm.gc.GCTestRunner
```

**菜单选项：**
- 1.1 - 软引用示例
- 1.2 - 弱引用示例
- 1.3 - 虚引用示例
- 1.4 - 引用类型对比
- 2.1 - Finalize逃逸示例
- 2.2 - 资源清理替代方案
- 3.1 - Eden区分配
- 3.2 - 大对象直接进入老年代
- 3.3 - 长期存活对象晋升
- 3.4 - 空间分配担保
- 4.1 - GC日志分析器

---

## 三、运行说明

### 3.1 编译代码
```bash
cd linsir-abc-core
mvn clean compile
```

### 3.2 运行单个示例
```bash
# 引用类型示例
java -cp target/classes com.linsir.abc.core.jvm.gc.reference.SoftReferenceExample
java -cp target/classes com.linsir.abc.core.jvm.gc.reference.WeakReferenceExample
java -cp target/classes com.linsir.abc.core.jvm.gc.reference.PhantomReferenceExample
java -cp target/classes com.linsir.abc.core.jvm.gc.reference.ReferenceTypeComparison

# Finalize机制示例
java -cp target/classes com.linsir.abc.core.jvm.gc.finalize.FinalizeEscapeGC
java -cp target/classes com.linsir.abc.core.jvm.gc.finalize.ResourceCleanupExample

# 内存分配策略示例（需要指定VM参数）
java -verbose:gc -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails -XX:SurvivorRatio=8 \
     -cp target/classes \
     com.linsir.abc.core.jvm.gc.allocation.EdenAllocation

# GC日志分析器
java -cp target/classes com.linsir.abc.core.jvm.gc.utils.GCLogAnalyzer

# 统一测试入口
java -cp target/classes com.linsir.abc.core.jvm.gc.GCTestRunner
```

### 3.3 注意事项

1. **JDK版本**：代码使用JDK 17编写和测试，部分特性（如Cleaner）需要Java 9+
2. **收集器差异**：
   - JDK 17默认使用G1收集器
   - `-XX:PretenureSizeThreshold`参数对G1不生效，G1使用Humongous区域处理大对象
   - 观察到的GC日志格式可能与文档中的示例略有不同
3. **内存设置**：内存分配策略示例建议在较小的堆内存（20MB）下运行，以便观察GC行为
4. **finalize()废弃**：FinalizeEscapeGC示例中的finalize()方法在JDK 9+中已被标记为废弃，仅用于学习目的

---

## 四、相关文档

- [chapter-03-gc.md](./chapter-03-gc.md) - 垃圾收集器与内存分配策略理论文档
- [chapter-03-gc-interview.md](./chapter-03-gc-interview.md) - GC相关面试题汇总
