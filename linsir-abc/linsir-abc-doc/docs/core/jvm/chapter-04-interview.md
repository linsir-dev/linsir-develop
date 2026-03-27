# 第4章 虚拟机性能监控与故障处理工具 - 面试题汇总

本文档汇总了JVM性能监控与故障处理工具相关的常见面试题及答案，按类别组织，便于系统学习和复习。

---

## 一、基础命令行工具

### 1.1 jps相关

#### Q1：jps命令的作用是什么？

**答案：**
jps（JVM Process Status Tool）用于列出正在运行的Java虚拟机进程，显示虚拟机执行主类名称以及本地虚拟机唯一ID（LVMID）。对于本地虚拟机进程，LVMID与操作系统的进程ID（PID）一致。

常用选项：
- `-q`：只输出LVMID
- `-m`：输出传递给main()函数的参数
- `-l`：输出主类全名或JAR路径
- `-v`：输出JVM启动参数

---

#### Q2：jps的实现原理是什么？

**答案：**
jps通过Java的Attach API与目标JVM进程通信获取进程信息。在Linux/Unix系统上，它还会查找`/tmp/hsperfdata_<username>`目录下的性能数据文件。

---

### 1.2 jstat相关

#### Q3：jstat命令可以监控哪些信息？

**答案：**
jstat（JVM Statistics Monitoring Tool）可以监控：
- 类加载情况（-class）
- GC情况（-gc, -gcutil, -gccause）
- 内存容量（-gccapacity）
- JIT编译（-compiler）

常用选项：
- `-gc`：显示Eden、Survivor、老年代、元空间等的容量和已用空间
- `-gcutil`：显示各区域使用百分比
- `-gccause`：显示GC原因

---

#### Q4：jstat输出中的S0C、S1C、S0U、S1U分别代表什么？

**答案：**
- `S0C/S1C`：Survivor 0/1区容量（KB）
- `S0U/S1U`：Survivor 0/1区已使用（KB）
- `EC/EU`：Eden区容量/已使用（KB）
- `OC/OU`：老年代容量/已使用（KB）
- `MC/MU`：元空间容量/已使用（KB）
- `YGC/YGCT`：Young GC次数/耗时
- `FGC/FGCT`：Full GC次数/耗时

---

#### Q5：如何使用jstat持续监控GC情况？

**答案：**
```bash
# 每250毫秒查询一次，共查询20次
jstat -gc <pid> 250 20

# 监控GC百分比，每5秒一次
jstat -gcutil <pid> 5000
```

---

### 1.3 jinfo相关

#### Q6：jinfo命令的作用是什么？

**答案：**
jinfo（Configuration Info for Java）用于：
- 实时查看JVM参数（-flags, -flag name）
- 动态修改部分JVM参数（-flag [+/-]name, -flag name=value）
- 查看系统属性（-sysprops）

注意：只有被标记为`manageable`的参数才能动态修改。

---

#### Q7：如何查看一个Java进程使用的垃圾收集器？

**答案：**
```bash
# 使用jinfo查看
jinfo -flag UseG1GC <pid>
jinfo -flag UseParallelGC <pid>
jinfo -flag UseConcMarkSweepGC <pid>

# 或者查看所有参数
jinfo -flags <pid>
```

---

### 1.4 jmap相关

#### Q8：jmap命令的主要用途是什么？

**答案：**
jmap（Memory Map for Java）用于：
- 生成堆转储快照（-dump）
- 查看堆详细信息（-heap）
- 查看堆中对象统计（-histo）
- 查看类加载器统计（-clstats）

---

#### Q9：如何生成堆转储文件？有哪些方式？

**答案：**
方式一：使用jmap
```bash
# 生成堆转储
jmap -dump:format=b,file=/tmp/heap.hprof <pid>

# 只dump存活对象
jmap -dump:live,format=b,file=/tmp/heap.hprof <pid>
```

方式二：JVM参数
```bash
# 在OOM时自动生成
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/tmp/heap.hprof
```

方式三：使用jcmd
```bash
jcmd <pid> GC.heap_dump /tmp/heap.hprof
```

---

#### Q10：jmap -histo输出中，[C、[B、[I分别代表什么？

**答案：**
- `[C`：char数组（通常来自String内部）
- `[B`：byte数组
- `[I`：int数组
- `[S`：short数组
- `[J`：long数组
- `[Z`：boolean数组
- `[F`：float数组
- `[D`：double数组
- `[L<类名>;`：对象数组

---

### 1.5 jstack相关

#### Q11：jstack命令的作用是什么？

**答案：**
jstack（Stack Trace for Java）用于生成虚拟机当前时刻的线程快照（threaddump），帮助定位线程长时间停顿的原因，如死锁、死循环、请求外部资源导致的挂起等。

---

#### Q12：如何使用jstack分析CPU占用高的问题？

**答案：**
步骤：
1. 使用`top`找到CPU占用高的Java进程PID
2. 使用`top -Hp <pid>`找到占用CPU高的线程TID
3. 将TID转换为16进制：`printf "%x\n" <tid>`
4. 使用`jstack <pid>`导出线程栈
5. 在jstack输出中查找16进制的线程ID
6. 定位到具体代码位置

---

#### Q13：jstack输出中的线程状态有哪些？分别代表什么？

**答案：**
- `NEW`：线程刚被创建，还未启动
- `RUNNABLE`：线程正在JVM中执行
- `BLOCKED`：线程被阻塞，等待监视器锁
- `WAITING`：线程无限期等待另一个线程执行特定操作
- `TIMED_WAITING`：线程在指定时间内等待
- `TERMINATED`：线程已退出

---

#### Q14：如何使用jstack检测死锁？

**答案：**
```bash
# 使用-l选项显示锁信息
jstack -l <pid>
```

如果存在死锁，jstack会在输出末尾显示：
```
Found one Java-level deadlock:
=============================
"Thread-1":
  waiting to lock monitor 0x... (object 0x..., a java.lang.Object),
  which is held by "Thread-2"
"Thread-2":
  waiting to lock monitor 0x... (object 0x..., a java.lang.Object),
  which is held by "Thread-1"

Found 1 deadlock.
```

---

## 二、可视化监控工具

### 2.1 JConsole

#### Q15：JConsole的主要功能有哪些？

**答案：**
JConsole（Java Monitoring and Management Console）基于JMX实现，主要功能：
- **概览**：显示堆内存、线程、类加载、CPU使用率的实时图表
- **内存**：显示堆和非堆内存使用情况，可手动触发GC
- **线程**：显示线程数量、详细信息、死锁检测
- **类**：显示已加载/卸载类数量
- **VM摘要**：显示JVM版本、启动参数、系统属性
- **MBeans**：查看和操作MBean

---

#### Q16：如何配置JConsole远程连接？

**答案：**
启动Java应用时添加JMX参数：
```bash
java -Dcom.sun.management.jmxremote \
     -Dcom.sun.management.jmxremote.port=9010 \
     -Dcom.sun.management.jmxremote.ssl=false \
     -Dcom.sun.management.jmxremote.authenticate=false \
     -jar application.jar
```

然后在JConsole中输入远程主机IP和端口9010连接。

---

### 2.2 VisualVM

#### Q17：VisualVM相比JConsole有哪些优势？

**答案：**
VisualVM是功能更强大的可视化监控工具：
- 整合了多个JDK命令行工具的功能
- 支持插件扩展（如Visual GC）
- 支持CPU和内存抽样/分析
- 支持堆转储和线程转储的生成和分析
- 可以分析应用程序快照
- 支持远程监控

---

#### Q18：如何使用VisualVM分析内存泄漏？

**答案：**
步骤：
1. 启动VisualVM并连接目标进程
2. 在"Monitor"标签页点击"Heap Dump"
3. 切换到"Classes"视图，按实例数量排序
4. 查找异常增长的类
5. 右键点击类，选择"View in Instances View"
6. 分析对象引用链，定位泄漏源

---

### 2.3 JMC/JFR

#### Q19：Java Mission Control（JMC）和Java Flight Recorder（JFR）是什么关系？

**答案：**
- **JMC**：监控工具，提供可视化界面
- **JFR**：数据收集框架，以极低开销记录JVM运行时信息

JMC使用JFR收集的数据进行分析和展示。JFR在JDK 11之前是商业特性，JDK 11之后开源。

---

#### Q20：JFR相比其他监控工具有什么优势？

**答案：**
JFR的优势：
- **低开销**：对应用性能影响极小（通常<1%），适合生产环境
- **数据全面**：记录线程、锁、GC、方法采样、异常、I/O等
- **时间精确**：基于事件的时间戳精确到纳秒
- **持续监控**：可以长时间持续记录

---

#### Q21：如何启动JFR记录？

**答案：**
方式一：启动时启用
```bash
java -XX:StartFlightRecording=duration=60s,filename=myrecording.jfr \
     -jar application.jar
```

方式二：使用jcmd动态启用
```bash
jcmd <pid> JFR.start duration=60s filename=myrecording.jfr
```

---

## 三、Arthas诊断工具

### 3.1 Arthas基础

#### Q22：Arthas是什么？相比JDK工具有什么优势？

**答案：**
Arthas是阿里巴巴开源的Java诊断工具，优势：
- **实时性**：无需重启应用即可诊断
- **无侵入**：不需要修改代码或添加依赖
- **功能丰富**：提供类加载、方法执行、线程、JVM等全方位诊断
- **易于使用**：命令行交互，支持Tab自动补全
- **生产友好**：对性能影响小，适合生产环境

---

#### Q23：Arthas的安装和启动方式？

**答案：**
```bash
# 快速安装
curl -O https://arthas.aliyun.com/arthas-boot.jar

# 启动
java -jar arthas-boot.jar

# 选择要诊断的进程编号
```

---

### 3.2 Arthas核心命令

#### Q24：Arthas的trace和watch命令有什么区别？

**答案：**
- **trace**：显示方法内部调用路径和每个节点的耗时，用于分析方法调用链性能
- **watch**：观察方法的入参、返回值、异常等数据，用于查看方法执行数据

使用场景：
- 需要分析方法耗时 → 使用trace
- 需要查看方法数据 → 使用watch

---

#### Q25：Arthas的tt命令有什么作用？

**答案：**
tt（Time Tunnel，时空隧道）用于：
- 记录方法每次调用的入参和返回信息
- 查看历史调用记录
- 重放历史调用（包括修改入参重放）

常用操作：
```bash
# 开始记录
tt -t com.example.Service getUser

# 查看记录
tt -l

# 重放第1000条记录
tt -i 1000 -p
```

---

#### Q26：如何使用Arthas查看方法的入参和返回值？

**答案：**
使用watch命令：
```bash
# 查看入参和返回值
watch com.example.Service getUser '{params,returnObj}' -x 2

# 查看异常
watch com.example.Service getUser '{params,throwExp}' -e

# 条件过滤（只查看id>100的情况）
watch com.example.Service getUser '{params[0],returnObj}' 'params[0].id>100' -x 2
```

表达式说明：
- `params`：入参数组
- `returnObj`：返回值
- `throwExp`：异常
- `#cost`：执行耗时
- `-x 2`：展开层级

---

#### Q27：Arthas如何实现热更新代码？

**答案：**
步骤：
1. 使用jad反编译类：`jad --source-only com.example.Service > /tmp/Service.java`
2. 修改代码
3. 使用mc编译：`mc /tmp/Service.java -d /tmp`
4. 使用retransform热更新：`retransform /tmp/com/example/Service.class`

注意：只能修改方法体，不能修改方法签名、类名、字段等。

---

#### Q28：Arthas的profiler命令可以做什么？

**答案：**
profiler命令使用async-profiler生成CPU/内存火焰图：
```bash
# 启动CPU分析
profiler start

# 查看状态
profiler status

# 停止并生成火焰图
profiler stop --file /tmp/flamegraph.svg
```

火焰图可以直观展示CPU时间消耗在哪些方法上，帮助定位性能瓶颈。

---

### 3.3 Arthas实战场景

#### Q29：线上接口响应慢，如何使用Arthas排查？

**答案：**
排查步骤：
1. 使用`dashboard`查看系统整体状态
2. 使用`thread -n 3`找到CPU占用高的线程
3. 使用`trace`跟踪接口方法，定位耗时长的内部调用
4. 使用`watch`观察慢请求的入参和返回值

```bash
# 跟踪接口方法，只查看耗时>1000ms的调用
trace com.example.controller.UserController getUser '#cost>1000'

# 观察慢请求详情
watch com.example.controller.UserController getUser '{params,returnObj,#cost}' '#cost>1000' -x 2
```

---

#### Q30：如何使用Arthas分析线上偶发异常？

**答案：**
使用tt命令记录方法调用：
```bash
# 记录方法调用
tt -t com.example.Service processOrder

# 当异常发生后，查看异常记录
tt -l

# 查看异常详情（假设第1000条记录是异常）
tt -i 1000

# 重放调用复现问题
tt -i 1000 -p
```

---

## 四、综合故障排查

### 4.1 CPU问题

#### Q31：线上应用CPU使用率100%，如何排查？

**答案：**
排查步骤：

**方式一：使用命令行工具**
1. `top`找到CPU占用高的Java进程PID
2. `top -Hp <pid>`找到占用CPU高的线程TID
3. `printf "%x\n" <tid>`将TID转16进制
4. `jstack <pid>`导出线程栈
5. 查找16进制线程ID，定位代码

**方式二：使用Arthas**
1. `dashboard`查看整体情况
2. `thread -n 3`直接查看CPU占用最高的线程
3. `thread <tid>`查看线程栈

常见原因：
- 死循环
- 频繁GC
- 复杂计算
- 正则表达式回溯

---

### 4.2 内存问题

#### Q32：线上应用出现OutOfMemoryError，如何排查？

**答案：**
排查步骤：

1. **查看GC情况**
   ```bash
   jstat -gcutil <pid> 1000
   ```
   观察老年代使用率是否持续上升，Full GC是否频繁。

2. **生成堆转储**
   ```bash
   jmap -dump:live,format=b,file=/tmp/heap.hprof <pid>
   ```

3. **分析堆转储**
   - 使用VisualVM或MAT打开.hprof文件
   - 查找大对象或对象数量异常多的类
   - 分析对象引用链，定位泄漏源

4. **使用Arthas快速分析**
   ```bash
   # 查看内存占用大的类
   vmtool --action getInstances --className com.example.Service --limit 10
   
   # 生成堆转储
   heapdump /tmp/dump.hprof
   ```

常见原因：
- 内存泄漏（未释放引用）
- 缓存未设置上限
- 大对象分配
- 元空间溢出（类加载过多）

---

#### Q33：如何排查内存泄漏？

**答案：**
排查方法：

1. **确认内存泄漏**
   - 使用jstat观察老年代使用率是否持续上升
   - Full GC后内存没有明显下降

2. **生成并分析堆转储**
   - 在不同时间点生成多个堆转储
   - 对比分析哪些对象持续增长

3. **分析引用链**
   - 使用MAT的Dominator Tree视图
   - 查找保留堆内存最大的对象
   - 分析GC Roots路径

4. **代码审查**
   - 检查静态集合是否持有对象引用
   - 检查监听器、回调是否未移除
   - 检查ThreadLocal使用是否正确
   - 检查数据库连接、流是否未关闭

---

### 4.3 线程问题

#### Q34：应用无响应（假死），如何排查？

**答案：**
排查步骤：

1. **导出线程栈**
   ```bash
   jstack -l <pid> > threaddump.txt
   ```

2. **分析线程状态**
   - 查找BLOCKED状态的线程（锁竞争）
   - 查找WAITING/TIMED_WAITING状态的线程（等待条件）
   - 检查是否有死锁

3. **使用Arthas**
   ```bash
   # 查看所有线程
   thread
   
   # 查看BLOCKED线程
   thread --state BLOCKED
   ```

常见原因：
- 死锁
- 线程池耗尽
- 数据库连接池耗尽
- 等待外部资源（如HTTP请求超时）
- 死循环导致线程占满CPU

---

#### Q35：如何检测和解决死锁？

**答案：**
检测方法：
1. `jstack -l <pid>`查看是否有死锁信息
2. Arthas：`thread -b`直接检测死锁
3. VisualVM/JConsole图形化检测

解决方法：
1. **避免嵌套锁**：尽量减少持有一个锁时去获取另一个锁
2. **统一锁顺序**：所有线程按相同顺序获取锁
3. **使用超时**：使用`tryLock()`设置获取锁的超时时间
4. **使用并发工具类**：使用`ConcurrentHashMap`代替`HashMap+锁`，使用`CopyOnWriteArrayList`等
5. **死锁检测和恢复**：使用jstack定期检测，发现死锁后重启或告警

---

### 4.4 GC问题

#### Q36：如何判断GC是否正常？

**答案：**
判断指标：

1. **GC频率**
   - Young GC：几秒到几十秒一次正常
   - Full GC：应该很少发生（几小时或几天一次）

2. **GC耗时**
   - Young GC：< 100ms
   - Full GC：< 1s（G1/CMS），< 100ms（ZGC/Shenandoah）

3. **内存趋势**
   - 老年代使用率应该稳定，不应持续增长

监控命令：
```bash
# 查看GC统计
jstat -gcutil <pid> 1000

# 查看GC原因
jstat -gccause <pid> 1000
```

---

#### Q37：Full GC频繁，如何排查和解决？

**答案：**
排查步骤：

1. **查看GC日志**
   - 分析Full GC触发原因（Ergonomics、Allocation Failure等）
   - 查看GC前后内存变化

2. **检查内存泄漏**
   - 使用jmap生成堆转储分析

3. **检查大对象**
   - 是否有大对象直接进入老年代

解决方法：
1. **调整堆大小**：`-Xms`和`-Xmx`设置为相同值，避免动态扩展
2. **调整代大小**：增大新生代，减少对象过早晋升
3. **选择合适的GC**：
   - 低延迟需求：G1、ZGC、Shenandoah
   - 高吞吐需求：Parallel GC
4. **优化代码**：
   - 减少大对象分配
   - 避免在循环中创建对象
   - 使用对象池

---

## 五、工具对比与选择

#### Q38：JDK自带工具和Arthas如何选择？

**答案：**

| 场景 | 推荐工具 | 原因 |
|------|---------|------|
| 快速查看进程 | jps | 简单直接 |
| 持续监控GC | jstat | 轻量，适合脚本 |
| 生成堆转储 | jmap/Arthas | 功能相同 |
| 线程分析 | Arthas/jstack | Arthas更友好 |
| 方法追踪 | Arthas | JDK工具无法实现 |
| 热更新 | Arthas | 独有功能 |
| 生产环境诊断 | Arthas | 功能丰富、低侵入 |
| 可视化分析 | VisualVM/JMC | 图形界面直观 |

---

#### Q39：生产环境诊断应该选择什么工具？

**答案：**
生产环境推荐：

1. **首选Arthas**：
   - 功能全面（监控、诊断、热修复）
   - 对应用性能影响小
   - 无需重启应用
   - 命令行操作，适合服务器环境

2. **持续监控使用JMC+JFR**：
   - 低开销（<1%性能影响）
   - 可长时间记录
   - 适合事后分析

3. **应急情况使用命令行工具**：
   - jstat：快速查看GC
   - jstack：导出线程栈
   - jmap：生成堆转储

注意事项：
- 生产环境使用工具前，了解其对性能的影响
- 避免在高峰期进行耗时操作（如生成大堆转储）
- 做好权限控制，避免敏感信息泄露

---

#### Q40：不同JDK版本的工具差异有哪些？

**答案：**

| 特性 | JDK 8 | JDK 11+ |
|------|-------|---------|
| VisualVM | 自带 | 需单独下载 |
| JMC/JFR | 商业特性 | 开源免费 |
| jhat | 可用 | 已移除 |
| 统一日志 | 无 | `-Xlog` |
| ZGC支持 | 无 | 有 |

建议：
- 生产环境尽量使用JDK 11或17，获得更好的工具支持
- JFR在JDK 11+开源，可以充分利用
- ZGC和Shenandoah等低延迟GC需要JDK 11+

---

## 六、面试技巧与注意事项

### 6.1 回答技巧

1. **结合实际经验**：如果有线上排查经验，可以结合实际案例回答
2. **分步骤说明**：排查问题时要条理清晰，分步骤说明
3. **工具对比**：了解不同工具的优缺点，能根据场景选择合适工具
4. **关注新版本**：了解JDK新版本的工具变化（如JFR开源、ZGC支持等）

### 6.2 常见误区

1. **只知道工具名称，不会使用**：要能说出具体命令和参数
2. **忽视生产环境影响**：要说明工具对应用性能的影响
3. **只会一种工具**：要了解多种工具，能根据场景选择
4. **忽视安全**：生产环境诊断要考虑权限和敏感信息保护

### 6.3 加分项

1. 有实际线上问题排查经验
2. 了解Arthas等第三方工具
3. 了解JFR等高级功能
4. 能编写脚本自动化监控
5. 了解JVM底层原理（如SA、Attach API等）

---

## 参考文档

- [chapter-04-tools.md](./chapter-04-tools.md) - 第4章详细内容
- [Arthas官方文档](https://arthas.aliyun.com/doc/)
- [JDK官方文档](https://docs.oracle.com/en/java/javase/17/tools/)
