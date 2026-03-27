# JVM 垃圾收集器与内存分配策略 - 面试题总结

## 一、基础概念篇

### 1. 什么是垃圾回收（GC）？为什么需要GC？

**答案：**
垃圾回收（Garbage Collection，GC）是Java虚拟机自动管理内存的机制，用于自动回收程序中不再使用的对象所占用的内存空间。

**为什么需要GC：**
- **避免内存泄漏**：自动回收无用对象，防止内存泄漏
- **减少程序员负担**：无需手动管理内存（如C/C++的malloc/free）
- **提高开发效率**：程序员可以专注于业务逻辑
- **保证程序稳定性**：自动内存管理减少因内存问题导致的程序崩溃

---

### 2. Java中判断对象是否存活的方法有哪些？

**答案：**

**（1）引用计数算法**
- 原理：给对象添加引用计数器，引用+1，失效-1，为0时回收
- **缺点**：无法解决循环引用问题
- **Java未采用此算法**

**（2）可达性分析算法（Java采用）**
- 原理：从GC Roots开始向下搜索，不可达的对象可回收
- GC Roots包括：
  - 虚拟机栈中引用的对象
  - 方法区中类静态属性引用的对象
  - 方法区中常量引用的对象
  - 本地方法栈中JNI引用的对象
  - 虚拟机内部引用（基本数据类型Class对象、异常对象等）

---

### 3. Java中有哪几种引用类型？它们的区别是什么？

**答案：**

| 引用类型 | 回收时机 | 使用场景 | get()方法 |
|---------|---------|---------|----------|
| **强引用** | 永不回收 | 普通对象引用 | 返回对象 |
| **软引用** | 内存不足时回收 | 缓存实现 | 返回对象 |
| **弱引用** | 下次GC时回收 | WeakHashMap | 返回对象 |
| **虚引用** | 随时可能回收 | 跟踪对象回收状态 | 永远返回null |

**代码示例：**
```java
// 强引用
Object strongRef = new Object();

// 软引用
SoftReference<Object> softRef = new SoftReference<>(new Object());

// 弱引用
WeakReference<Object> weakRef = new WeakReference<>(new Object());

// 虚引用
PhantomReference<Object> phantomRef = new PhantomReference<>(
    new Object(), new ReferenceQueue<>());
```

---

### 4. 对象的finalization机制是什么？

**答案：**
对象的`finalize()`方法是对象逃脱死亡命运的最后一次机会。

**执行过程：**
1. 对象被标记为可回收后，判断是否覆盖`finalize()`方法
2. 如果覆盖且未执行过，放入F-Queue队列
3. Finalizer线程执行`finalize()`方法
4. 如果对象在`finalize()`中重新建立引用链，则移除回收集合

**注意：**
- `finalize()`只能执行一次
- 执行时间不确定，不建议使用
- 可用`try-finally`替代

---

## 二、垃圾收集算法篇

### 5. 常见的垃圾收集算法有哪些？各自的优缺点？

**答案：**

| 算法 | 优点 | 缺点 | 适用场景 |
|-----|------|------|---------|
| **标记-清除** | 实现简单，不需要移动对象 | 效率低，产生内存碎片 | 老年代（CMS） |
| **复制** | 效率高，无内存碎片 | 内存利用率低（50%） | 新生代 |
| **标记-整理** | 无内存碎片 | 需要移动对象，停顿时间长 | 老年代 |
| **分代收集** | 针对不同代采用不同算法 | 实现复杂 | 现代JVM标准方案 |

**复制算法的优化（Appel式回收）：**
- Eden : Survivor0 : Survivor1 = 8 : 1 : 1
- 只浪费10%内存，利用率90%

---

### 6. 为什么新生代使用复制算法，老年代使用标记-清除/标记-整理算法？

**答案：**

**新生代特点：**
- 对象存活率低（约98%对象朝生夕死）
- 复制算法只需复制少量存活对象，效率高
- 新生代空间相对较小，复制成本低

**老年代特点：**
- 对象存活率高
- 复制算法需要复制大量对象，效率低
- 标记-清除/标记-整理更适合存活率高的场景

---

## 三、垃圾收集器篇

### 7. 常见的垃圾收集器有哪些？它们的特点和适用场景？

**答案：**

#### 新生代收集器

| 收集器 | 特点 | 适用场景 |
|-------|------|---------|
| **Serial** | 单线程，简单高效 | Client模式，小内存 |
| **ParNew** | Serial的多线程版本 | Server模式，配合CMS |
| **Parallel Scavenge** | 吞吐量优先，自适应调节 | 后台运算，批处理 |

#### 老年代收集器

| 收集器 | 特点 | 适用场景 |
|-------|------|---------|
| **Serial Old** | Serial的老年代版本 | Client模式 |
| **Parallel Old** | 多线程，标记-整理 | 吞吐量优先 |
| **CMS** | 并发收集，低停顿 | 互联网应用，响应敏感 |

#### 整堆收集器

| 收集器 | 特点 | 适用场景 |
|-------|------|---------|
| **G1** | 分区收集，可预测停顿 | 大堆内存（6GB+） |

---

### 8. CMS收集器的工作原理是什么？有什么优缺点？

**答案：**

**工作原理（4个阶段）：**
```
初始标记 ──→ 并发标记 ──→ 重新标记 ──→ 并发清除
(STW)       (并发)        (STW)       (并发)
```

1. **初始标记**：标记GC Roots直接关联的对象（STW，很快）
2. **并发标记**：GC Roots Tracing（并发，时间较长）
3. **重新标记**：修正并发期间的标记变动（STW，比初始标记长）
4. **并发清除**：清除可回收对象（并发）

**优点：**
- 并发收集，低停顿
- 适合对响应时间敏感的应用

**缺点：**
1. **CPU敏感**：并发阶段占用CPU资源
2. **浮动垃圾**：并发清理时产生的新垃圾需下次回收
3. **内存碎片**：使用标记-清除算法，产生碎片
4. **Concurrent Mode Failure**：预留内存不足时触发Full GC

**关键参数：**
```bash
-XX:+UseConcMarkSweepGC
-XX:CMSInitiatingOccupancyFraction=68
-XX:+UseCMSCompactAtFullCollection
```

---

### 9. G1收集器与CMS收集器相比有什么优势？

**答案：**

| 特性 | CMS | G1 |
|-----|-----|-----|
| **算法** | 标记-清除 | 标记-整理 + 复制 |
| **内存碎片** | 有 | 无 |
| **停顿时间** | 不可预测 | 可预测（MaxGCPauseMillis） |
| **内存布局** | 传统分代 | 分区（Region） |
| **适用堆大小** | 中小型堆 | 大堆（6GB+） |
| **Full GC** | 单线程Serial Old | 多线程并行 |

**G1的核心优势：**
1. **可预测的停顿时间模型**：用户可以指定最大停顿时间
2. **无内存碎片**：整体标记-整理，局部复制
3. **分区管理**：将堆划分为多个Region，优先回收价值最大的Region
4. **Remembered Set**：避免全堆扫描

**G1的内存布局：**
```
┌────┬────┬────┬────┬────┬────┬────┬────┐
│ E  │ E  │ S  │ O  │ O  │ H  │ O  │ E  │
├────┼────┼────┼────┼────┼────┼────┼────┤
│ O  │ E  │ O  │ O  │ S  │ O  │ E  │ O  │
└────┴────┴────┴────┴────┴────┴────┴────┘

E = Eden Region
S = Survivor Region  
O = Old Region
H = Humongous Region（大对象）
```

---

### 10. 如何选择垃圾收集器？

**答案：**

| 应用场景 | 推荐收集器 | 理由 |
|---------|-----------|------|
| 单CPU，小内存 | Serial + Serial Old | 简单高效，无线程开销 |
| 多CPU，追求吞吐量 | Parallel Scavenge + Parallel Old | 充分利用多核 |
| 多CPU，追求低停顿 | ParNew + CMS 或 G1 | 并发收集，用户体验好 |
| 大堆内存（6GB+） | G1 | 分区管理，可预测停顿 |
| JDK 9+默认 | G1 | 官方推荐 |

**JDK版本演进：**
- JDK 8：Parallel Scavenge + Parallel Old（默认）
- JDK 9+：G1（默认）
- JDK 11+：ZGC（实验性，超低延迟）

---

## 四、内存分配策略篇

### 11. 对象的内存分配策略有哪些？

**答案：**

**（1）对象优先在Eden分配**
- 大多数情况下，对象在新生代Eden区分配
- Eden区满时触发Minor GC

**（2）大对象直接进入老年代**
- 大对象：需要大量连续内存空间的对象（如大数组）
- 避免在Eden和Survivor之间大量复制
- 参数：`-XX:PretenureSizeThreshold`

**（3）长期存活的对象进入老年代**
- 对象年龄计数器，默认15岁（CMS为6岁）
- 参数：`-XX:MaxTenuringThreshold`

**（4）动态对象年龄判定**
- Survivor中相同年龄对象大小总和 > Survivor/2
- 年龄>=该年龄的对象可直接晋升

**（5）空间分配担保**
- Minor GC前检查老年代空间
- 不足时进行Full GC

---

### 12. Minor GC、Major GC、Full GC的区别？

**答案：**

| GC类型 | 发生区域 | 触发条件 | 特点 |
|-------|---------|---------|------|
| **Minor GC** | 新生代 | Eden区满 | 频繁，速度快 |
| **Major GC** | 老年代 | 老年代满 | 较慢，比Minor GC慢10倍 |
| **Full GC** | 整堆（新生代+老年代+方法区） | 多种情况 | 最慢，应尽量避免 |

**触发Full GC的情况：**
1. 调用`System.gc()`（建议，非强制）
2. 老年代空间不足
3. 方法区空间不足
4. Minor GC后进入老年代的平均大小 > 老年代可用空间
5. CMS GC时出现Concurrent Mode Failure

---

### 13. 什么是空间分配担保？

**答案：**

**概念：**
在发生Minor GC之前，虚拟机会检查老年代最大可用连续空间是否大于新生代所有对象总空间。

**流程：**
```
老年代空间 > 新生代总对象空间?
    ├── 是 → 安全进行Minor GC
    └── 否 → HandlePromotionFailure允许?
                  ├── 否 → Full GC
                  └── 是 → 老年代空间 > 历次晋升平均大小?
                                    ├── 是 → 尝试Minor GC（有风险）
                                    └── 否 → Full GC
```

**冒险的含义：**
- 如果Minor GC后存活对象 > Survivor容量，需老年代担保
- 如果担保失败，重新发起Full GC

---

## 五、JVM参数与调优篇

### 14. 常用的JVM垃圾收集相关参数有哪些？

**答案：**

#### 收集器选择参数
```bash
-XX:+UseSerialGC              # Serial + Serial Old
-XX:+UseParNewGC              # ParNew + Serial Old
-XX:+UseParallelGC            # Parallel Scavenge + Serial Old
-XX:+UseParallelOldGC         # Parallel Scavenge + Parallel Old
-XX:+UseConcMarkSweepGC       # ParNew + CMS
-XX:+UseG1GC                  # G1
```

#### 堆内存参数
```bash
-Xms512m                      # 初始堆大小
-Xmx512m                      # 最大堆大小
-Xmn256m                      # 新生代大小
-XX:SurvivorRatio=8          # Eden:Survivor = 8:1
-XX:NewRatio=2               # 老年代:新生代 = 2:1
```

#### CMS参数
```bash
-XX:CMSInitiatingOccupancyFraction=68    # CMS触发百分比
-XX:+UseCMSCompactAtFullCollection       # Full GC时压缩
-XX:CMSFullGCsBeforeCompaction=0          # 多少次Full GC后压缩
```

#### G1参数
```bash
-XX:MaxGCPauseMillis=200                 # 最大GC停顿时间
-XX:G1HeapRegionSize=16m                 # Region大小
-XX:InitiatingHeapOccupancyPercent=45    # 触发并发GC的堆占用百分比
```

#### 日志参数
```bash
-XX:+PrintGCDetails           # 打印详细GC日志
-XX:+PrintGCTimeStamps        # 打印时间戳
-Xloggc:gc.log                # GC日志文件
-XX:+HeapDumpOnOutOfMemoryError   # OOM时生成堆转储
```

---

### 15. 什么是Stop The World？如何减少STW时间？

**答案：**

**Stop The World（STW）：**
垃圾收集时暂停所有应用线程的现象。在STW期间，应用不响应任何请求。

**减少STW的方法：**

1. **选择合适的收集器**
   - 低延迟场景：CMS、G1、ZGC
   - 大堆场景：G1、ZGC

2. **调整堆内存大小**
   - 避免过大堆导致GC时间过长
   - 合理设置新生代/老年代比例

3. **优化代码**
   - 减少大对象创建
   - 及时释放无用引用
   - 使用对象池

4. **JVM参数调优**
   - 设置合理的GC触发阈值
   - 开启并发标记

5. **使用新一代收集器**
   - ZGC（JDK 11+）：目标<10ms停顿
   - Shenandoah（JDK 12+）：低延迟

---

### 16. 什么是内存泄漏？如何排查内存泄漏？

**答案：**

**内存泄漏：**
程序中已分配的内存由于某种原因未释放或无法释放，导致内存持续增长，最终可能引发OOM。

**常见原因：**
1. 静态集合类持有对象引用
2. 未关闭的资源（连接、流等）
3. 监听器未移除
4. 单例模式持有外部对象
5. ThreadLocal未remove

**排查工具：**
1. **jmap**：生成堆转储文件
   ```bash
   jmap -dump:format=b,file=heap.hprof <pid>
   ```

2. **jvisualvm**：可视化分析堆内存

3. **MAT（Memory Analyzer Tool）**：分析堆转储文件
   - 查找Dominator Tree
   - 查找GC Roots

4. **Arthas**：实时监控和诊断
   ```bash
   dashboard    # 查看内存概况
   heapdump     # 生成堆转储
   ```

**排查步骤：**
1. 确认内存泄漏（内存持续增长，GC无法回收）
2. 生成堆转储文件
3. 分析大对象和引用链
4. 定位泄漏代码
5. 修复验证

---

## 六、进阶篇

### 17. 什么是TLAB（Thread Local Allocation Buffer）？

**答案：**

**TLAB：**
线程本地分配缓冲区，是线程私有的分配区域。

**作用：**
- 避免多线程竞争堆内存分配
- 提高内存分配效率
- 减少同步开销

**原理：**
1. 每个线程在Eden区分配一块私有的TLAB
2. 对象优先在TLAB中分配
3. TLAB用完后再在Eden区分配（需同步）

**相关参数：**
```bash
-XX:+UseTLAB                 # 启用TLAB（默认开启）
-XX:TLABSize=512k           # 设置TLAB大小
-XX:+ResizeTLAB             # 自动调整TLAB大小
```

---

### 18. 什么是卡表（Card Table）和记忆集（Remembered Set）？

**答案：**

**问题背景：**
Minor GC时，如果老年代对象引用新生代对象，需要扫描整个老年代来确定新生代对象的存活。

**卡表（Card Table）：**
- 老年代划分为512字节的卡页（Card Page）
- 卡表是一个字节数组，每个元素对应一个卡页
- 当卡页中的对象引用发生变化时，标记为"脏卡"
- Minor GC时只需扫描脏卡对应的老年代区域

**记忆集（Remembered Set）：**
- G1收集器中的概念
- 每个Region维护一个Remembered Set
- 记录其他Region引用本Region的对象
- 避免全堆扫描

**关系：**
- 卡表是记忆集的一种具体实现
- CMS使用卡表，G1使用Remembered Set

---

### 19. 什么是安全点（Safepoint）和安全区域（Safe Region）？

**答案：**

**安全点（Safepoint）：**
程序执行时并非在所有地方都能停顿下来开始GC，只有在特定的位置才能停顿，这些位置称为安全点。

**安全点的选择：**
- 方法调用
- 循环跳转
- 异常跳转
- 长时间执行的代码段

**安全区域（Safe Region）：**
指在一段代码片段中，引用关系不会发生变化。在这个区域中的任意地方开始GC都是安全的。

**使用场景：**
- 线程处于Sleep状态或Blocked状态
- 线程无法响应JVM的中断请求
- 此时进入安全区域，JVM可以安全地执行GC

---

### 20. 什么是三色标记算法？CMS和G1如何使用它？

**答案：**

**三色标记：**
并发标记时使用的标记算法，将对象分为三种颜色：

| 颜色 | 含义 | 状态 |
|-----|------|------|
| **白色** | 未访问 | 可回收 |
| **灰色** | 已访问，但引用未处理 | 待处理 |
| **黑色** | 已访问，引用已处理 | 存活 |

**标记过程：**
1. 初始：GC Roots为灰色，其他为白色
2. 处理灰色对象，标记为黑色，其子对象标记为灰色
3. 重复步骤2，直到没有灰色对象
4. 剩余白色对象可回收

**并发标记的问题：**
- **漏标**：对象被错误地标记为可回收
- **错标**：对象被错误地标记为存活

**CMS的解决方案：**
- **增量更新（Incremental Update）**：记录黑色对象新增的引用，重新扫描

**G1的解决方案：**
- **原始快照（Snapshot At The Beginning, SATB）**：记录灰色对象删除的引用，重新扫描

---

## 七、实战场景篇

### 21. 生产环境出现OOM，如何排查？

**答案：**

**排查步骤：**

**1. 确认OOM类型**
```
java.lang.OutOfMemoryError: Java heap space          # 堆内存溢出
java.lang.OutOfMemoryError: Metaspace                # 元空间溢出
java.lang.OutOfMemoryError: Unable to create new native thread  # 线程溢出
java.lang.OutOfMemoryError: Direct buffer memory     # 直接内存溢出
```

**2. 开启OOM时自动转储**
```bash
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/path/to/dumps/
```

**3. 分析堆转储文件**
- 使用MAT分析大对象
- 查找Dominator Tree
- 分析GC Roots路径

**4. 常见解决方案**
- 堆溢出：增加堆内存，检查内存泄漏
- 元空间溢出：增加MetaspaceSize，检查类加载泄漏
- 线程溢出：减少线程数，使用线程池
- 直接内存溢出：检查NIO使用，限制直接内存大小

---

### 22. 线上服务GC时间过长，如何优化？

**答案：**

**诊断步骤：**

**1. 分析GC日志**
```bash
-XX:+PrintGCDetails
-XX:+PrintGCTimeStamps
-Xloggc:gc.log
```

**2. 确定问题类型**
- Minor GC频繁：新生代太小
- Full GC频繁：老年代太小或内存泄漏
- GC时间长：堆太大或收集器选择不当

**3. 优化方案**

**调整堆大小：**
```bash
-Xms4g -Xmx4g           # 设置合理的堆大小
-Xmn2g                  # 调整新生代大小
-XX:SurvivorRatio=8    # 调整Eden/Survivor比例
```

**更换收集器：**
```bash
# 低延迟场景
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100

# 大堆场景（JDK 11+）
-XX:+UnlockExperimentalVMOptions
-XX:+UseZGC
```

**代码优化：**
- 减少大对象创建
- 使用对象池
- 及时释放资源
- 避免内存泄漏

---

### 23. 如何监控JVM的GC情况？

**答案：**

**命令行工具：**

**jstat：**
```bash
jstat -gc <pid> 1000    # 每秒输出GC统计
jstat -gcutil <pid>     # 输出GC利用率
```

**jmap：**
```bash
jmap -heap <pid>        # 查看堆信息
jmap -histo <pid>       # 查看对象统计
```

**可视化工具：**

**jvisualvm：**
- JDK自带，可视化监控
- 查看GC活动、堆内存变化

**Arthas：**
```bash
dashboard               # 综合监控面板
vmoption                # 查看JVM参数
```

**Prometheus + Grafana：**
- 使用JMX Exporter暴露指标
- Grafana展示GC图表

**关键监控指标：**
- GC次数（Minor GC/Full GC）
- GC耗时
- 堆内存使用率
- 老年代使用率
- 元空间使用率

---

## 八、总结

### 核心知识点回顾

1. **对象存活判定**：可达性分析算法、GC Roots、四种引用类型
2. **垃圾收集算法**：标记-清除、复制、标记-整理、分代收集
3. **垃圾收集器**：Serial、ParNew、Parallel Scavenge、CMS、G1
4. **内存分配策略**：Eden优先、大对象直接进老年代、动态年龄判定
5. **JVM调优**：参数配置、GC日志分析、问题排查

### 面试技巧

1. **理解原理**：不仅要记住概念，还要理解背后的设计思想
2. **对比记忆**：通过对比不同收集器、算法的优缺点加深理解
3. **结合实际**：结合生产环境问题，展示实战经验
4. **关注演进**：了解JDK版本演进（G1成为JDK 9+默认收集器）
5. **新技术**：了解ZGC、Shenandoah等新一代低延迟收集器

---

*本文档基于《深入理解Java虚拟机》第3章内容整理，结合常见面试题总结而成。*
