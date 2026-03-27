# 第1章 走近Java - 面试题总结

## 一、Java技术体系相关面试题

### 1.1 JDK、JRE、JVM 的区别和联系

**Q1：请解释 JDK、JRE、JVM 三者之间的关系？**

**答案：**

```
JDK (Java Development Kit)
├── JRE (Java Runtime Environment)
│   ├── JVM (Java Virtual Machine)
│   └── Java SE API 类库
└── 开发工具 (javac、javadoc、jdb 等)
```

- **JVM（Java虚拟机）**：是运行 Java 字节码的虚拟机，负责将字节码转换为机器码执行
- **JRE（Java运行时环境）**：包含 JVM 和运行 Java 程序所需的核心类库，是运行 Java 程序的最小环境
- **JDK（Java开发工具包）**：包含 JRE 和开发工具（编译器、调试器等），是开发 Java 程序的最小环境

**关系**：JDK ⊃ JRE ⊃ JVM

---

**Q2：为什么 Java 被称为"平台无关"的语言？**

**答案：**

Java 的平台无关性得益于"一次编写，到处运行"（Write Once, Run Anywhere）的设计理念：

1. **编译阶段**：Java 源代码（.java）被编译为平台无关的字节码（.class）
2. **运行阶段**：不同平台（Windows、Linux、macOS）安装对应的 JVM，JVM 负责将字节码解释或编译为本地机器码执行
3. **统一标准**：字节码格式是统一的，由 JVM 屏蔽底层操作系统差异

```
Java源代码(.java)
      ↓ javac编译
字节码(.class) ← 平台无关
      ↓ 不同平台JVM执行
┌─────────┬─────────┬─────────┐
│Windows  │  Linux  │  macOS  │
│   JVM   │   JVM   │   JVM   │
└─────────┴─────────┴─────────┘
```

---

**Q3：只安装了 JRE 可以编译 Java 程序吗？**

**答案：**

不可以。编译 Java 程序需要 `javac` 编译器，而 `javac` 只包含在 JDK 中，JRE 仅提供运行环境。

---

## 二、Java平台分类面试题

### 2.1 Java 四大平台

**Q4：Java 技术体系包含哪几个平台？分别适用于什么场景？**

**答案：**

| 平台 | 全称 | 适用场景 | 现状 |
|------|------|----------|------|
| **Java Card** | - | 智能卡、SIM卡等微型设备 | 特定领域使用 |
| **Java ME** | Micro Edition | 移动终端、嵌入式设备 | 已被 Android 取代 |
| **Java SE** | Standard Edition | 桌面应用、基础开发 | 最常用 |
| **Java EE** | Enterprise Edition<br/>(现 Jakarta EE) | 企业级应用、Web应用 | 主流企业开发 |

**层级关系**：Java SE 是基础，Java EE 构建于 Java SE 之上。

---

**Q5：Java EE 和 Jakarta EE 有什么区别？**

**答案：**

- **Java EE**（Java Platform, Enterprise Edition）：Oracle 主导的企业级平台
- **2017年**：Oracle 将 Java EE 捐赠给 Eclipse 基金会
- **2019年**：更名为 **Jakarta EE**，避免商标问题
- **本质**：两者是同一技术体系，Jakarta EE 是 Java EE 的开源延续

---

## 三、Java版本历史面试题

### 3.1 重要版本特性

**Q6：JDK 8 有哪些重要新特性？**

**答案：**

JDK 8（2014年发布）是 Java 历史上最重要的版本之一：

1. **Lambda 表达式**：简化函数式编程，`(参数) -> { 方法体 }`
2. **Stream API**：支持函数式风格的集合操作
3. **方法引用**：`ClassName::methodName`
4. **默认方法**：接口可以有默认实现
5. **新日期时间 API**：`java.time` 包，替代旧的 Date/Calendar
6. **Optional 类**：避免空指针异常
7. **Nashorn JavaScript 引擎**：在 JVM 上运行 JavaScript
8. **重复注解**：同一位置可多次使用同一注解

---

**Q7：JDK 9 引入的模块化系统（Jigsaw）有什么作用？**

**答案：**

JDK 9 引入的 **JPMS**（Java Platform Module System）解决了 Java 平台的 Jar Hell 问题：

**主要优势**：
- **更小的运行时**：只包含必要的模块，减少内存占用
- **更好的封装性**：强封装内部 API，提高安全性
- **明确的依赖关系**：通过 `module-info.java` 声明依赖
- **改进的性能**：更快的启动时间

**模块声明示例**：
```java
module com.example.app {
    requires java.base;
    requires java.net.http;
    exports com.example.api;
}
```

---

**Q8：JDK 11 和 JDK 17 为什么是 LTS 版本？有什么重要特性？**

**答案：**

**LTS（Long Term Support，长期支持）版本**提供多年更新支持，适合生产环境。

**JDK 11 LTS（2018年）重要特性**：
- ZGC（低延迟垃圾收集器）
- HTTP Client API（标准版）
- 移除 Java EE 和 CORBA 模块
- 引入 `var` 局部变量类型推断
- 单文件源代码可直接运行

**JDK 17 LTS（2021年）重要特性**：
- 密封类（Sealed Classes）正式版
- 模式匹配 for instanceof
- 新的 macOS 渲染管道
- 移除实验性的 AOT 和 JIT 编译器
- 更强的封装 JDK 内部

---

**Q9：JDK 21 的虚拟线程（Virtual Threads）是什么？**

**答案：**

**虚拟线程**是 Project Loom 的核心特性，在 JDK 21 中正式发布：

**核心特点**：
- **轻量级**：由 JVM 管理，而非操作系统
- **高并发**：可创建数百万个虚拟线程
- **低开销**：上下文切换成本低
- **兼容性好**：兼容现有 Thread API

**与平台线程对比**：

| 特性 | 平台线程 | 虚拟线程 |
|------|----------|----------|
| 实现 | 操作系统线程 | JVM 管理 |
| 内存占用 | ~1MB 栈空间 | ~几百字节 |
| 创建数量 | 数千级别 | 数百万级别 |
| 适用场景 | CPU 密集型 | I/O 密集型 |

**使用示例**：
```java
// 创建虚拟线程
Thread.startVirtualThread(() -> {
    System.out.println("Running in virtual thread");
});

// 或使用 ExecutorService
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(() -> doWork());
}
```

---

**Q10：列举从 JDK 8 到 JDK 21 的主要版本及特性**

**答案：**

| 版本 | 年份 | 主要特性 |
|------|------|----------|
| JDK 8 | 2014 | Lambda、Stream API、新日期API |
| JDK 9 | 2017 | 模块化系统（Jigsaw）、JShell |
| JDK 10 | 2018 | 局部变量类型推断（var） |
| JDK 11 | 2018 | ZGC、HTTP Client、LTS |
| JDK 12-13 | 2019 | Switch 表达式、文本块 |
| JDK 14-15 | 2020 | Records、密封类预览 |
| JDK 16-17 | 2021 | 模式匹配、LTS |
| JDK 18-19 | 2022 | UTF-8 默认字符集、虚拟线程预览 |
| JDK 20-21 | 2023 | 虚拟线程正式版、分代 ZGC、LTS |

---

## 四、JVM架构与原理面试题

### 4.1 JVM 基本概念

**Q11：什么是 JVM？它的主要作用是什么？**

**答案：**

**JVM（Java Virtual Machine，Java虚拟机）**是运行 Java 字节码的抽象计算机。

**主要作用**：
1. **字节码执行**：加载、验证、执行 .class 文件
2. **内存管理**：自动内存分配和垃圾回收
3. **线程管理**：提供多线程支持
4. **平台抽象**：屏蔽底层操作系统差异
5. **安全控制**：字节码验证、安全管理器

**JVM 核心组件**：
```
┌─────────────────────────────────────────┐
│              类加载子系统                │
├─────────────────────────────────────────┤
│  运行时数据区  │  执行引擎  │  本地方法接口 │
│  (内存区域)   │ (解释器/编译器)│   (JNI)    │
├─────────────────────────────────────────┤
│           本地方法库 (Native)            │
└─────────────────────────────────────────┘
```

---

**Q12：HotSpot VM 是什么？为什么叫"HotSpot"？**

**答案：**

**HotSpot VM** 是 Sun/Oracle JDK 默认的 JVM 实现，也是目前使用最广泛的 JVM。

**名称由来**：
- **热点探测（Hot Spot Detection）**：JVM 会检测程序中执行频率高的"热点代码"
- **JIT 编译**：将热点代码编译为本地机器码，提高执行效率

**HotSpot 特点**：
- 解释器与 JIT 编译器混合执行
- 自适应优化器
- 先进的垃圾收集器（G1、ZGC、Shenandoah）

---

**Q13：解释器与 JIT 编译器有什么区别？**

**答案：**

| 特性 | 解释器 | JIT 编译器 |
|------|--------|-----------|
| **执行方式** | 逐行解释字节码 | 将热点代码编译为机器码 |
| **启动速度** | 快，立即执行 | 慢，需要编译时间 |
| **执行效率** | 慢 | 快（编译后） |
| **适用场景** | 冷代码、启动阶段 | 热点代码、长期运行 |

**HotSpot 的执行策略**：
1. 程序启动时，使用**解释器**快速执行
2. 运行过程中，检测**热点代码**
3. 将热点代码交给 **JIT 编译器**编译为机器码
4. 后续直接执行编译后的机器码

**热点代码检测标准**：
- 方法调用计数器（Invocation Counter）
- 回边计数器（Back Edge Counter，循环体）

---

### 4.2 64位 JVM

**Q14：32位和64位 JVM 有什么区别？**

**答案：**

| 特性 | 32位 JVM | 64位 JVM |
|------|----------|----------|
| **最大堆内存** | ~4GB | 理论可达 16EB（实际 TB 级） |
| **指针大小** | 4 字节 | 8 字节（未压缩） |
| **内存占用** | 较少 | 较多（指针膨胀） |
| **性能** | 指针操作快 | 寄存器优势，但指针操作慢 |

**压缩指针（Compressed Oops）**：
- 64位 JVM 默认开启 `-XX:+UseCompressedOops`
- 将 64 位指针压缩为 32 位，减少内存占用
- 堆内存小于 32GB 时有效

---

**Q15：64位 JVM 中 int 类型还是 4 字节吗？**

**答案：**

是的。Java 的基本数据类型长度是固定的，与 JVM 位宽无关：

| 类型 | 长度 |
|------|------|
| byte | 1 字节 |
| short | 2 字节 |
| int | 4 字节 |
| long | 8 字节 |
| float | 4 字节 |
| double | 8 字节 |
| char | 2 字节 |
| boolean | 1 字节 |

**注意**：引用类型（对象指针）的长度会变化（32位 JVM 4字节，64位 JVM 8字节）。

---

## 五、OpenJDK 与源码相关面试题

### 5.1 OpenJDK 基础

**Q16：OpenJDK 和 Oracle JDK 有什么区别？**

**答案：**

| 特性 | OpenJDK | Oracle JDK |
|------|---------|------------|
| **许可证** | GPL v2 with Classpath Exception | Oracle Technology Network License |
| **源码** | 开源 | 基于 OpenJDK 构建 |
| **商业支持** | 社区支持 | Oracle 商业支持 |
| **功能** | 核心功能完整 | 可能包含额外商业特性 |
| **更新频率** | 每6个月发布 | 每6个月 + LTS |

**关系**：Oracle JDK 是基于 OpenJDK 源码构建的，核心功能基本一致。

---

**Q17：如何获取 OpenJDK 源码？**

**答案：**

```bash
# 使用 Git 克隆（推荐）
git clone https://github.com/openjdk/jdk.git

# 切换到特定版本
git checkout jdk-17+35

# 或使用 Mercurial（官方仓库）
hg clone https://hg.openjdk.org/jdk/jdk
```

---

### 5.2 HotSpot 源码结构

**Q18：OpenJDK 源码中 HotSpot 目录的主要结构是什么？**

**答案：**

```
hotspot/
├── cpu/                    # CPU 架构相关代码
│   ├── x86/               # x86/x86_64
│   ├── arm/               # ARM
│   └── aarch64/           # ARM64
│
├── os/                     # 操作系统相关代码
│   ├── linux/
│   ├── windows/
│   └── bsd/
│
├── os_cpu/                 # CPU+OS 组合代码
│   └── linux_x86/
│
└── share/vm/               # 平台无关核心代码
    ├── classfile/          # 类文件解析
    ├── compiler/           # JIT 编译器
    ├── gc/                 # 垃圾收集器
    ├── interpreter/        # 解释器
    ├── memory/             # 内存管理
    ├── oops/               # 对象表示
    ├── runtime/            # 运行时系统
    └── prims/              # JNI 实现
```

---

**Q19：HotSpot 中 oops 目录的作用是什么？**

**答案：**

**oops（Ordinary Object Pointers，普通对象指针）**目录定义了 JVM 中对象的内存表示：

| 文件 | 作用 |
|------|------|
| `oop.hpp` | Oop 基类定义 |
| `instanceOop.hpp` | 实例对象（类的实例） |
| `arrayOop.hpp` | 数组对象 |
| `markWord.hpp` | 对象头中的标记字 |
| `klass.hpp` | Klass（类元数据） |

**OOP-Klass 模型**：
- **Oop**：描述对象实例数据（堆中）
- **Klass**：描述类元数据（元空间中）
- 两者通过指针关联，实现对象与类的分离

---

**Q20：HotSpot 源码中 gc 目录包含哪些垃圾收集器？**

**答案：**

```
share/vm/gc/
├── serial/          # Serial 收集器（单线程）
├── parallel/        # Parallel 收集器（吞吐量优先）
├── cms/             # CMS 收集器（低延迟，已废弃）
├── g1/              # G1 收集器（平衡型）
├── zgc/             # ZGC 收集器（超低延迟）
├── shenandoah/      # Shenandoah 收集器（低延迟）
└── shared/          # GC 通用代码
```

---

## 六、Java 未来趋势面试题

### 6.1 技术发展方向

**Q21：Java 技术未来的发展方向有哪些？**

**答案：**

根据官方路线图和社区趋势，Java 的发展方向包括：

1. **模块化**：JPMS 持续完善，更细粒度的模块划分
2. **多语言支持**：JVM 作为多语言平台（Kotlin、Scala、Groovy 等）
3. **高性能并发**：虚拟线程（Project Loom）、结构化并发
4. **语法现代化**：模式匹配、Records、密封类等
5. **云原生优化**：更快的启动时间、更小的内存占用（GraalVM）
6. **GC 持续优化**：ZGC、Shenandoah 等低延迟收集器成熟

---

**Q22：什么是 Project Loom？它解决了什么问题？**

**答案：**

**Project Loom** 是 OpenJDK 的一个项目，旨在简化 Java 的高并发编程：

**核心特性**：
- **虚拟线程（Virtual Threads）**：轻量级线程，由 JVM 管理
- **结构化并发（Structured Concurrency）**：简化多线程任务管理
- **作用域值（Scoped Values）**：线程间数据共享

**解决问题**：
- 传统线程模型中，创建大量线程会导致内存耗尽
- 异步编程（CompletableFuture、Reactive）代码复杂难维护
- 虚拟线程让开发者可以用同步的方式编写高并发代码

---

**Q23：GraalVM 是什么？与传统 JVM 有什么区别？**

**答案：**

**GraalVM** 是 Oracle 开发的高性能多语言虚拟机：

**主要特性**：
1. **高性能 JIT 编译器**：用 Java 编写的 Graal 编译器
2. **Native Image**：将 Java 代码编译为本地可执行文件
3. **多语言支持**：Java、JavaScript、Python、Ruby、R 等
4. **低内存占用**：Native Image 启动快、内存占用少

**与传统 HotSpot 对比**：

| 特性 | HotSpot | GraalVM |
|------|---------|---------|
| JIT 编译器 | C1/C2 | Graal（Java编写） |
| AOT 编译 | 不支持 | Native Image |
| 启动时间 | 较慢 | 极快（AOT后） |
| 内存占用 | 较高 | 较低（AOT后） |
| 反射支持 | 完整 | AOT需要配置 |

---

## 七、综合面试题

### 7.1 场景分析题

**Q24：为什么 Android 没有使用 Java ME，而是开发了自己的虚拟机？**

**答案：**

**Java ME 的局限性**：
1. 功能受限，不适合现代智能手机
2. 更新缓慢，无法满足移动开发需求
3. 授权问题（Oracle 的 Java 许可）

**Android 的解决方案**：
1. 开发 **Dalvik VM**（后升级为 ART）
2. 使用 Java 语言，但运行自定义字节码（dex）
3. 针对移动设备优化（电池、内存）
4. 避免 Java 授权问题

---

**Q25：生产环境应该选择哪个 JDK 版本？**

**答案：**

**推荐策略**：

| 场景 | 推荐版本 | 理由 |
|------|----------|------|
| 新项目 | JDK 17 LTS 或 JDK 21 LTS | 长期支持，新特性丰富 |
| 稳定优先 | JDK 11 LTS | 成熟稳定，生态完善 |
| 遗留系统 | JDK 8 | 兼容性最好，但建议升级 |

**版本选择考虑因素**：
- LTS 版本提供多年支持
- 非 LTS 版本每6个月停止更新
- 考虑第三方库兼容性
- 团队技术储备

---

## 面试题分类索引

| 类别 | 题号范围 | 核心考点 |
|------|----------|----------|
| Java技术体系 | Q1-Q3 | JDK/JRE/JVM关系 |
| Java平台分类 | Q4-Q5 | SE/EE/ME/Card |
| Java版本历史 | Q6-Q10 | 各版本新特性 |
| JVM架构原理 | Q11-Q15 | HotSpot、解释器、64位JVM |
| OpenJDK源码 | Q16-Q20 | 源码结构、垃圾收集器 |
| 未来趋势 | Q21-Q23 | Project Loom、GraalVM |
| 综合分析 | Q24-Q25 | 场景选择 |

---

## 参考资料

1. 《深入理解Java虚拟机》（第3版）第1章
2. OpenJDK 官方文档：https://openjdk.org/
3. Java 版本发布说明：https://www.oracle.com/java/technologies/
