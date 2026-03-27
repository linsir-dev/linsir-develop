# 第5章 调优案例分析与实战

## 5.1 概述

JVM调优是Java应用性能优化的重要环节。本章通过真实案例分析和实战演练，帮助读者掌握JVM调优的方法和技巧。调优的核心目标是：在有限资源约束下，让应用达到预期的性能指标。

调优前需要明确：
- **性能目标**：吞吐量、延迟、内存占用，三者往往不可兼得
- **调优范围**：是单个应用还是整个系统
- **资源限制**：硬件配置、JDK版本、运行环境

调优的一般步骤：
1. 监控现状，收集性能数据
2. 分析瓶颈，确定优化方向
3. 制定方案，调整JVM参数或代码
4. 验证效果，对比调优前后指标
5. 持续监控，确保长期稳定

---

## 5.2 案例分析

### 5.2.1 大内存硬件上的程序部署策略

**场景描述**

某系统部署在64GB内存的高性能服务器上，使用单机单Java进程部署。随着业务增长，发现Full GC时间过长（每次10-30秒），导致应用周期性卡顿。

**问题分析**

1. 大堆内存（如32GB）导致Full GC耗时过长
2. 使用Parallel Old收集器，STW时间与堆大小成正比
3. 单机单进程无法充分利用多核CPU

**解决方案**

方案一：使用G1或低延迟收集器
```bash
# 使用G1收集器，控制最大停顿时间
java -Xms24g -Xmx24g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar application.jar
```

方案二：使用ZGC（JDK 11+）
```bash
# ZGC适合超大堆内存，停顿时间<10ms
java -Xms32g -Xmx32g \
     -XX:+UseZGC \
     -jar application.jar
```

方案三：单机多实例部署（推荐）
```bash
# 将64GB内存分配给4个Java进程，每个16GB
# 实例1
java -Xms12g -Xmx12g -XX:+UseG1GC -Dserver.port=8081 -jar app.jar

# 实例2
java -Xms12g -Xmx12g -XX:+UseG1GC -Dserver.port=8082 -jar app.jar

# 实例3
java -Xms12g -Xmx12g -XX:+UseG1GC -Dserver.port=8083 -jar app.jar

# 实例4
java -Xms12g -Xmx12g -XX:+UseG1GC -Dserver.port=8084 -jar app.jar
```

**方案对比**

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| G1 | 平衡吞吐量和延迟 | 大堆时停顿仍较长 | 堆内存<32GB |
| ZGC | 超低延迟 | 吞吐量略低 | 超大堆、低延迟需求 |
| 多实例 | 充分利用CPU，单实例故障影响小 | 部署复杂 | 高并发、高可用需求 |

**调优效果**

采用方案三后：
- Full GC时间从30秒降至200ms以内
- 系统吞吐量提升40%
- 单实例故障不影响整体服务

---

### 5.2.2 集群间同步导致的内存溢出

**场景描述**

某分布式缓存系统，使用JGroups进行集群节点间数据同步。运行一段时间后频繁出现OOM，堆转储显示大量`org.jgroups.Message`对象。

**问题分析**

1. 网络抖动导致消息重传
2. 接收队列无限增长，消费速度跟不上生产速度
3. 大对象（缓存数据）频繁在节点间传输

**解决方案**

1. **限制队列大小**
```java
// 配置JGroups接收队列上限
props.setProperty("recv_buf_size", "10000");
props.setProperty("max_bundle_size", "64K");
```

2. **启用流量控制**
```java
// 添加FC（Flow Control）协议
props.setProperty("protocol_stack", "...FC(max_credits=2M,min_threshold=0.4):...");
```

3. **JVM参数调优**
```bash
java -Xms8g -Xmx8g \
     -XX:+UseG1GC \
     -XX:G1HeapRegionSize=16m \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/logs/heapdump.hprof \
     -jar cache-server.jar
```

4. **代码优化**
```java
// 添加消息处理超时机制
public void receive(Message msg) {
    if (messageQueue.size() > MAX_QUEUE_SIZE) {
        log.warn("Message queue full, dropping message");
        return;
    }
    // 异步处理，避免阻塞接收线程
    messageProcessor.submit(() -> processMessage(msg));
}
```

**调优效果**
- OOM问题彻底解决
- 集群同步延迟从秒级降至毫秒级
- 网络抖动时系统保持稳定

---

### 5.2.3 堆外内存导致的溢出错误

**场景描述**

某NIO网络服务器，使用Netty框架。运行一段时间后进程被系统OOM Killer终止，但JVM堆内存使用正常。

**问题分析**

1. 使用堆外内存（Direct Buffer）存储网络数据
2. 未正确释放ByteBuffer，导致堆外内存泄漏
3. 系统物理内存耗尽，触发OOM Killer

**排查过程**

```bash
# 查看进程内存使用（RSS包含堆外内存）
pmap -x <pid> | tail -1

# 查看堆外内存使用（JDK 8+）
jcmd <pid> VM.native_memory summary

# 查看详细堆外内存分配
jcmd <pid> VM.native_memory detail | grep -A 5 "Internal"
```

**解决方案**

1. **限制堆外内存大小**
```bash
java -Xms4g -Xmx4g \
     -XX:MaxDirectMemorySize=2g \
     -XX:+UseG1GC \
     -jar netty-server.jar
```

2. **代码修复**
```java
// 错误示例：未释放ByteBuffer
public void handleRead(SocketChannel channel) {
    ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    channel.read(buffer);
    // 缺少 buffer.clear() 或显式释放
}

// 正确示例：使用try-finally确保释放
public void handleRead(SocketChannel channel) {
    ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    try {
        channel.read(buffer);
        processBuffer(buffer);
    } finally {
        ((DirectBuffer) buffer).cleaner().clean();
    }
}

// 更好的方案：使用Netty的ByteBuf
public void handleRead(ChannelHandlerContext ctx, ByteBuf msg) {
    try {
        processMessage(msg);
    } finally {
        msg.release(); // 引用计数减1，自动释放
    }
}
```

3. **监控堆外内存**
```java
// 定期打印堆外内存使用
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(() -> {
    long used = ManagementFactory.getMemoryMXBean()
        .getNonHeapMemoryUsage().getUsed();
    long committed = ManagementFactory.getMemoryMXBean()
        .getNonHeapMemoryUsage().getCommitted();
    log.info("Non-heap memory: used={}MB, committed={}MB", 
        used/1024/1024, committed/1024/1024);
}, 0, 60, TimeUnit.SECONDS);
```

**调优效果**
- 堆外内存使用稳定在限制范围内
- 系统不再被OOM Killer终止
- 内存泄漏问题彻底解决

---

### 5.2.4 外部命令导致系统缓慢

**场景描述**

某Web应用，每次处理请求时需要调用外部shell命令获取系统信息。压测时发现吞吐量极低，响应时间不稳定。

**问题分析**

1. 使用`Runtime.exec()`频繁创建进程
2. 进程创建开销大（fork操作）
3. 未正确读取进程输出流，导致缓冲区满阻塞

**问题代码**
```java
// 问题代码
public String getSystemInfo() throws IOException {
    Process process = Runtime.getRuntime().exec("uname -a");
    // 未读取输出流，可能导致阻塞
    return "OK";
}
```

**解决方案**

1. **使用Java API替代外部命令**
```java
// 使用Java原生API获取系统信息
public String getSystemInfo() {
    StringBuilder info = new StringBuilder();
    info.append("OS: ").append(System.getProperty("os.name")).append("\n");
    info.append("Arch: ").append(System.getProperty("os.arch")).append("\n");
    info.append("CPUs: ").append(Runtime.getRuntime().availableProcessors());
    return info.toString();
}
```

2. **如需执行命令，使用进程池**
```java
@Component
public class CommandExecutor {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    
    public String execute(String command, long timeout, TimeUnit unit) 
            throws Exception {
        Future<String> future = executor.submit(() -> {
            Process process = Runtime.getRuntime().exec(command);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        });
        return future.get(timeout, unit);
    }
}
```

3. **使用ProcessBuilder优化**
```java
public String executeCommand(List<String> commands) throws IOException {
    ProcessBuilder pb = new ProcessBuilder(commands);
    pb.redirectErrorStream(true); // 合并错误流
    Process process = pb.start();
    
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream()))) {
        String output = reader.lines().collect(Collectors.joining("\n"));
        process.waitFor(5, TimeUnit.SECONDS);
        return output;
    }
}
```

**调优效果**
- 吞吐量提升10倍以上
- 响应时间从秒级降至毫秒级
- 系统资源使用更稳定

---

### 5.2.5 服务器虚拟机进程崩溃

**场景描述**

某系统运行一段时间后，JVM进程突然消失，没有留下任何错误日志。系统监控显示内存和CPU使用正常。

**问题分析**

可能原因：
1. 被系统OOM Killer终止（内存不足）
2. 被运维脚本误杀
3. JNI调用导致JVM崩溃（hs_err_pid.log）
4. 系统资源限制（ulimit）

**排查过程**

```bash
# 查看系统日志
sudo grep -i "killed process" /var/log/messages
sudo dmesg | grep -i "out of memory"

# 查看JVM崩溃日志
ls -la hs_err_pid*.log

# 检查系统资源限制
ulimit -a
cat /proc/<pid>/limits

# 检查是否被信号终止
tail -f /var/log/audit/audit.log | grep kill
```

**常见原因及解决方案**

1. **OOM Killer**
```bash
# 调整OOM分数，降低被kill优先级
echo -15 > /proc/<pid>/oom_score_adj

# 或增加系统内存/交换空间
```

2. **JNI调用崩溃**
```bash
# 检查hs_err_pid.log中的Crashed字段
# 通常包含：
# - SIGSEGV (0xb) at pc=0x... 访问非法内存
# - Problematic frame: C [libxxx.so] 问题库

# 解决方案：
# 1. 升级JNI库到最新版本
# 2. 检查JNI代码中的内存操作
# 3. 添加JVM参数生成更详细的崩溃日志
-XX:ErrorFile=/logs/hs_err_pid%p.log
-XX:HeapDumpPath=/logs/
```

3. **线程数超限**
```bash
# 查看当前线程数
ps -eLf | grep <pid> | wc -l

# 增加系统线程限制
echo "* soft nproc 65535" >> /etc/security/limits.conf
echo "* hard nproc 65535" >> /etc/security/limits.conf
```

4. **文件句柄耗尽**
```bash
# 查看打开的文件数
ls /proc/<pid>/fd | wc -l

# 增加文件句柄限制
ulimit -n 65535
```

**预防措施**

```bash
# JVM启动参数增加错误处理
java -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/logs/heapdump.hprof \
     -XX:ErrorFile=/logs/hs_err_pid%p.log \
     -XX:+PrintGCDetails \
     -Xloggc:/logs/gc.log \
     -jar application.jar

# 使用systemd管理，自动重启
[Service]
Restart=always
RestartSec=5
```

---

### 5.2.6 不恰当数据结构导致内存占用过大

**场景描述**

某数据分析系统，需要加载大量数据到内存。使用`ArrayList`存储数据，发现内存占用远超预期，频繁触发Full GC。

**问题分析**

1. `ArrayList`扩容机制导致内存浪费（1.5倍扩容）
2. 存储了大量重复字符串（没有intern）
3. 使用`Integer`、`Long`等包装类，对象头开销大

**问题代码**
```java
// 问题代码
List<String> data = new ArrayList<>(); // 默认容量10
for (String line : lines) {
    data.add(line); // 频繁扩容，内存碎片
}
```

**解决方案**

1. **预估容量，避免扩容**
```java
// 预先指定容量
List<String> data = new ArrayList<>(lines.size());
```

2. **使用更紧凑的数据结构**
```java
// 使用Trove4j等原始类型集合
TIntArrayList intList = new TIntArrayList(); // 比ArrayList<Integer>省内存

// 使用fastutil
Int2ObjectMap<String> map = new Int2ObjectOpenHashMap<>();
```

3. **字符串去重**
```java
// JDK 8u20+ 开启字符串去重
-XX:+UseStringDeduplication

// 或手动intern（需谨慎）
String interned = line.intern();
```

4. **使用压缩指针（64位JVM）**
```bash
-XX:+UseCompressedOops      # 压缩普通对象指针
-XX:+UseCompressedClassPointers  # 压缩类指针
```

5. **数据分页加载**
```java
public void processLargeData(File file) throws IOException {
    try (Stream<String> lines = Files.lines(file.toPath())) {
        lines.parallel()
             .map(this::transform)
             .filter(Objects::nonNull)
             .forEach(this::saveToDatabase); // 流式处理，不全部加载到内存
    }
}
```

**调优效果**
- 内存占用减少60%
- Full GC频率从每小时10次降至每天1次
- 数据处理速度提升30%

---

### 5.2.7 由Windows虚拟内存导致的长时间停顿

**场景描述**

某Windows服务器运行的Java应用，每隔一段时间出现数秒停顿，GC日志显示STW时间与GC实际耗时严重不符。

**问题分析**

1. Windows系统虚拟内存管理策略
2. 系统内存不足时，将内存页交换到磁盘
3. GC时需要访问被交换出去的内存页，导致大量磁盘IO

**排查过程**

```powershell
# 查看系统内存使用
Get-Process | Sort-Object WorkingSet -Descending | Select-Object -First 10

# 查看页面文件使用
Get-Counter "\Paging File(_Total)\% Usage"

# JVM启动时添加参数查看安全点停顿
-XX:+PrintGCApplicationStoppedTime
-XX:+PrintSafepointStatistics
```

**解决方案**

1. **增加物理内存**
   - 最直接有效的方案

2. **调整Windows虚拟内存设置**
```powershell
# 设置固定大小的页面文件（避免动态调整开销）
# 控制面板 -> 系统 -> 高级系统设置 -> 性能设置 -> 高级 -> 虚拟内存
# 设置为"自定义大小"，初始值和最大值相同（如8192MB）
```

3. **JVM参数调优**
```bash
# 锁定堆内存，防止被交换（需要管理员权限）
java -Xms4g -Xmx4g -XX:+UseG1GC \
     -XX:+UnlockExperimentalVMOptions \
     -XX:+UseLargePages \
     -jar application.jar
```

4. **使用G1的字符串去重**
```bash
-XX:+UseStringDeduplication  # 减少堆内存使用
```

**调优效果**
- 停顿时间从数秒降至200ms以内
- 系统响应更加稳定

---

### 5.2.8 由安全点导致长时间停顿

**场景描述**

某低延迟交易系统，偶尔出现100ms以上的停顿，GC日志显示GC时间很短，但`Total time for which application threads were stopped`很长。

**问题分析**

1. 线程长时间不进入安全点（Safe Point）
2. 常见原因：
   - 大循环（int计数而非long）
   - 长时间执行的JNI代码
   - 大量线程竞争锁

**排查过程**

```bash
# 添加JVM参数查看安全点信息
-XX:+PrintSafepointStatistics
-XX:PrintSafepointStatisticsCount=1
-XX:+LogVMOutput
-XX:LogFile=/logs/vm.log
```

日志分析：
```
vmop                    [threads: total initially_running wait_to_block]    [time: spin block sync cleanup vmop] page_trap_count
0.302: no vm operation  [      27          0              0    ]      [0     0     0     0     0    ]  0
0.303: CGC_Operation    [      27          1              1    ]      [0     0     2     0     5    ]  0
# 如果spin或block时间很长，说明线程进入安全点慢
```

**问题代码**
```java
// 问题：使用int计数的大循环，JVM无法插入安全点检查
public void process() {
    for (int i = 0; i < 1000000000; i++) {  // int类型
        // 处理逻辑
    }
}
```

**解决方案**

1. **修改循环变量为long类型**
```java
// 解决方案：使用long类型，JVM会在每次迭代检查安全点
public void process() {
    for (long i = 0; i < 1000000000L; i++) {
        // 处理逻辑
    }
}
```

2. **在循环中插入安全点检查**
```java
public void process() {
    for (int i = 0; i < 1000000000; i++) {
        if (i % 1000 == 0) {
            Thread.yield(); // 主动让出CPU，检查安全点
        }
        // 处理逻辑
    }
}
```

3. **使用-XX:+UseCountedLoopSafepoints（谨慎使用）**
```bash
# 强制在计数循环中插入安全点，但有性能开销
-XX:+UseCountedLoopSafepoints
```

4. **异步日志**
```java
// 避免同步IO阻塞
AsyncLogger.log(message); // 使用异步日志框架
```

**调优效果**
- 安全点停顿从100ms+降至10ms以内
- 满足低延迟交易系统的SLA要求

---

## 5.3 实战：Eclipse运行速度调优

### 5.3.1 调优前的程序运行状态

**环境信息**
- Eclipse版本：2023-06
- JDK版本：OpenJDK 17
- 系统配置：16GB内存，8核CPU
- 项目规模：中型Java项目（约500个类）

**初始JVM参数**
```bash
# Eclipse默认配置
eclipse.ini:
-Xms256m
-Xmx1024m
-XX:+UseG1GC
```

**问题表现**
1. 启动时间：45秒
2. 代码编译响应慢，经常出现"Building workspace"卡顿
3. 打开大文件时界面冻结
4. 频繁触发Full GC

**性能数据收集**

```bash
# 添加GC日志参数
-XX:+PrintGCDetails
-XX:+PrintGCTimeStamps
-Xloggc:/tmp/eclipse_gc.log

# 使用VisualVM监控
```

GC日志分析：
```
[GC pause (G1 Evacuation Pause) (young) 1024M->512M(1024M), 0.5234567 secs]
[Full GC (Allocation Failure)  1024M->768M(1024M), 1.2345678 secs]
```

**瓶颈分析**
1. 堆内存太小（最大1GB），无法满足IDE需求
2. 元空间不足，频繁类加载/卸载
3. JIT编译耗时，方法解释执行

---

### 5.3.2 升级JDK版本的性能变化及兼容问题

**升级方案**

从OpenJDK 17升级至OpenJDK 21，利用新特性：
- G1 GC优化
- ZGC支持（实验性）
- 更好的JIT编译器

**升级步骤**

1. **下载并配置JDK 21**
```bash
# 解压JDK 21
tar -xzf openjdk-21_linux-x64_bin.tar.gz

# 修改eclipse.ini
-vm
/opt/jdk-21/bin/java
```

2. **处理兼容性问题**

问题一：模块系统警告
```
WARNING: Using incubator modules: jdk.incubator.vector
```
解决：移除或更新使用孵化器模块的插件

问题二：反射访问限制
```
java.lang.reflect.InaccessibleObjectException
```
解决：添加JVM参数
```bash
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
```

3. **性能对比**

| 指标 | JDK 17 | JDK 21 | 提升 |
|------|--------|--------|------|
| 启动时间 | 45s | 38s | 15.6% |
| 编译速度 | 基准 | +12% | 12% |
| 内存占用 | 1024M | 1024M | 持平 |

---

### 5.3.3 编译时间和类加载时间的优化

**优化目标**
- 减少启动时的类加载时间
- 加速JIT编译
- 提升代码编译响应速度

**优化方案**

1. **使用AppCDS（Application Class-Data Sharing）**
```bash
# 1. 生成类列表
java -Xshare:off -XX:+UseAppCDS -XX:DumpLoadedClassList=eclipse.classlist \
     -jar eclipse/plugins/org.eclipse.equinox.launcher_*.jar

# 2. 生成共享归档文件
java -Xshare:dump -XX:+UseAppCDS -XX:SharedClassListFile=eclipse.classlist \
     -XX:SharedArchiveFile=eclipse.jsa \
     -jar eclipse/plugins/org.eclipse.equinox.launcher_*.jar

# 3. 使用共享归档
eclipse.ini添加：
-XX:+UseAppCDS
-XX:SharedArchiveFile=eclipse.jsa
```

2. **启用JIT编译优化**
```bash
# 使用更多编译线程
-XX:CICompilerCount=8

# 提高编译阈值（适合长时间运行的IDE）
-XX:CompileThreshold=5000

# 启用分层编译（默认开启）
-XX:+TieredCompilation
```

3. **优化类加载**
```bash
# 增加元空间大小
-XX:MetaspaceSize=256m
-XX:MaxMetaspaceSize=512m

# 启用类数据共享
-Xshare:auto
```

**优化效果**
- 启动时间从38s降至28s（AppCDS贡献约10s）
- 类加载时间减少40%
- JIT编译的热点方法响应更快

---

### 5.3.4 调整内存设置控制垃圾收集频率

**优化目标**
- 减少GC停顿时间
- 降低GC频率
- 提升IDE响应速度

**优化方案**

1. **增大堆内存**
```bash
eclipse.ini:
-Xms2g
-Xmx4g
```

2. **优化G1参数**
```bash
# 控制最大停顿时间
-XX:MaxGCPauseMillis=100

# 增大G1区域大小（减少区域数量，降低管理开销）
-XX:G1HeapRegionSize=16m

# 调整并发GC线程数
-XX:ConcGCThreads=4

# 调整新生代大小
-XX:G1NewSizePercent=20
-XX:G1MaxNewSizePercent=30
```

3. **启用字符串去重**
```bash
-XX:+UseStringDeduplication
```

**GC日志对比**

优化前：
```
[GC pause (G1 Evacuation Pause) (young) 1024M->512M(1024M), 0.5234567 secs]
平均GC频率：每30秒一次
平均GC耗时：500ms
```

优化后：
```
[GC pause (G1 Evacuation Pause) (young) 2048M->1024M(4096M), 0.0892345 secs]
平均GC频率：每2分钟一次
平均GC耗时：90ms
```

**优化效果**
- GC频率降低75%
- GC停顿时间减少82%
- "Building workspace"卡顿消失

---

### 5.3.5 选择收集器降低延迟

**评估不同收集器**

| 收集器 | 平均停顿 | 最大停顿 | 吞吐量 | 适用场景 |
|--------|----------|----------|--------|----------|
| G1 | 90ms | 150ms | 高 | 平衡型 |
| ZGC | 5ms | 10ms | 中高 | 超低延迟 |
| Shenandoah | 8ms | 15ms | 中高 | 超低延迟 |

**ZGC配置（JDK 21）**
```bash
eclipse.ini:
-XX:+UseZGC
-XX:+ZGenerational  # JDK 21+ 分代ZGC
-Xms2g
-Xmx4g
```

**ZGC效果**
- 启动时间：28s → 30s（略增加）
- GC停顿：平均5ms，最大10ms
- 内存占用：略高于G1（约10%）

**最终配置**

考虑到Eclipse对吞吐量和内存占用的需求，最终选择G1收集器：

```bash
eclipse.ini完整配置：
-vm
/opt/jdk-21/bin/java

-vmargs
-Xms2g
-Xmx4g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100
-XX:G1HeapRegionSize=16m
-XX:MetaspaceSize=256m
-XX:MaxMetaspaceSize=512m
-XX:+UseStringDeduplication
-XX:+UseAppCDS
-XX:SharedArchiveFile=eclipse.jsa
-XX:CICompilerCount=8

--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
```

**最终效果**

| 指标 | 优化前 | 优化后 | 提升 |
|------|--------|--------|------|
| 启动时间 | 45s | 28s | 37.8% |
| GC停顿 | 500ms | 90ms | 82% |
| GC频率 | 30秒/次 | 2分钟/次 | 75% |
| 编译响应 | 卡顿 | 流畅 | 显著提升 |

---

## 5.4 本章小结

本章通过8个真实案例和Eclipse调优实战，展示了JVM调优的完整过程。

### 关键调优经验

1. **大内存部署**：超大堆内存应考虑使用ZGC/Shenandoah，或采用多实例部署
2. **网络应用**：注意堆外内存使用，设置`MaxDirectMemorySize`限制
3. **外部命令**：避免频繁创建进程，使用Java API或进程池替代
4. **进程崩溃**：系统检查OOM Killer、JNI崩溃、资源限制
5. **数据结构**：预估容量、使用紧凑结构、流式处理大数据
6. **安全点**：避免大循环使用int计数，注意线程进入安全点时间

### 调优工具推荐

| 工具 | 用途 |
|------|------|
| GC日志 + GCViewer | GC分析 |
| VisualVM/JMC | 综合监控 |
| jstat | 实时GC监控 |
| jstack | 线程分析 |
| Async-profiler/Arthas | CPU/内存分析 |

### 调优原则

1. **不要过早优化**：先确定瓶颈，再针对性调优
2. **一次只改一个参数**：便于对比效果
3. **监控先行**：调优前后都要有数据支撑
4. **生产环境谨慎**：先在测试环境验证
5. **持续迭代**：调优是一个持续过程

### 常用调优参数速查

```bash
# 内存设置
-Xms4g -Xmx4g                    # 堆内存
-XX:MetaspaceSize=256m           # 元空间初始
-XX:MaxMetaspaceSize=512m        # 元空间最大
-XX:MaxDirectMemorySize=2g       # 堆外内存限制

# GC设置
-XX:+UseG1GC                     # 使用G1
-XX:MaxGCPauseMillis=200         # 最大停顿时间
-XX:G1HeapRegionSize=16m         # G1区域大小
-XX:+UseStringDeduplication      # 字符串去重

# 调试诊断
-XX:+HeapDumpOnOutOfMemoryError  # OOM时生成堆转储
-XX:HeapDumpPath=/logs/          # 堆转储路径
-Xlog:gc*:file=/logs/gc.log      # GC日志（JDK 9+）
```

调优的本质是在吞吐量、延迟、内存占用之间找到适合应用场景的平衡点。没有最好的配置，只有最适合的配置。
