# JVM 深度解析

## 概述

Java 虚拟机（Java Virtual Machine，JVM）是 Java 平台的核心组件，它提供了 Java 程序的运行环境，实现了"一次编写，到处运行"（Write Once, Run Anywhere）的跨平台特性。

## 学习路径

本模块按照从基础到深入的顺序，系统地介绍 JVM 的核心知识，共分为五个部分：

```mermaid
flowchart TB
    subgraph Part1["第一部分：走近Java"]
        A1[第1章 走近Java]
    end
    
    subgraph Part2["第二部分：自动内存管理机制"]
        B1[第2章 Java内存区域与内存溢出异常]
        B2[第3章 垃圾收集器与内存分配策略]
        B3[第4章 虚拟机性能监控与故障处理工具]
        B4[第5章 调优案例分析与实战]
    end
    
    subgraph Part3["第三部分：虚拟机执行子系统"]
        C1[第6章 类文件结构]
        C2[第7章 虚拟机类加载机制]
        C3[第8章 虚拟机字节码执行引擎]
        C4[第9章 类加载及执行子系统的案例与实战]
    end
    
    subgraph Part4["第四部分：程序编译与代码优化"]
        D1[第10章 早期编译期优化]
        D2[第11章 晚期运行期优化]
    end
    
    subgraph Part5["第五部分：高效并发"]
        E1[第12章 Java内存模型与线程]
        E2[第13章 线程安全与锁优化]
    end
    
    Part1 --> Part2
    Part2 --> Part3
    Part3 --> Part4
    Part4 --> Part5
```

## 知识体系结构

```mermaid
mindmap
  root((JVM<br/>知识体系))
    第一部分
      走近Java
        Java技术体系
        Java发展史
        编译JDK实战
    第二部分
      自动内存管理
        运行时数据区域
        垃圾收集算法
        垃圾收集器
        内存分配策略
        性能监控工具
        调优案例
    第三部分
      虚拟机执行子系统
        类文件结构
        类加载机制
        字节码执行引擎
        实战案例
    第四部分
      编译与优化
        编译期优化
        运行期优化
        JIT编译器
    第五部分
      高效并发
        Java内存模型
        线程实现
        线程安全
        锁优化
```

## 内容导航

### 第一部分：走近Java

| 章节 | 内容概述 |
|------|----------|
| [第1章 走近Java](./chapter-01-intro) | Java技术体系、发展史、未来展望、编译JDK实战 |

### 第二部分：自动内存管理机制

| 章节 | 内容概述 |
|------|----------|
| [第2章 Java内存区域与内存溢出异常](./chapter-02-memory) | 运行时数据区域详解、OutOfMemoryError异常实战 |
| [第3章 垃圾收集器与内存分配策略](./chapter-03-gc) | 垃圾回收算法、垃圾收集器对比、内存分配策略 |
| [第4章 虚拟机性能监控与故障处理工具](./chapter-04-tools) | JDK命令行工具(jps/jstat/jmap等)、可视化工具(JConsole/VisualVM) |
| [第5章 调优案例分析与实战](./chapter-05-tuning) | 调优案例分析、Eclipse运行速度调优实战 |

### 第三部分：虚拟机执行子系统

| 章节 | 内容概述 |
|------|----------|
| [第6章 类文件结构](./chapter-06-classfile) | Class文件结构、魔数、常量池、字段表、方法表、属性表 |
| [第7章 虚拟机类加载机制](./chapter-07-classloading) | 类加载时机、类加载过程、类加载器、双亲委派模型 |
| [第8章 虚拟机字节码执行引擎](./chapter-08-execution) | 运行时栈帧结构、方法调用、解释执行引擎 |
| [第9章 类加载及执行子系统的案例与实战](./chapter-09-cases) | Tomcat/OSGi类加载器架构、动态代理、远程执行功能实战 |

### 第四部分：程序编译与代码优化

| 章节 | 内容概述 |
|------|----------|
| [第10章 早期（编译期）优化](./chapter-10-compile-time) | Javac编译器、Java语法糖、注解处理器实战 |
| [第11章 晚期（运行期）优化](./chapter-11-runtime) | 即时编译器、编译优化技术、逃逸分析 |

### 第五部分：高效并发

| 章节 | 内容概述 |
|------|----------|
| [第12章 Java内存模型与线程](./chapter-12-jmm) | Java内存模型、volatile、线程实现与调度 |
| [第13章 线程安全与锁优化](./chapter-13-thread-safety) | 线程安全实现、锁优化技术(自旋锁/轻量级锁/偏向锁) |

## 核心知识点速览

### 1. JVM 整体架构

```mermaid
flowchart TB
    subgraph Source["源代码层"]
        Java[Java源代码]
    end
    
    subgraph Compile["编译层"]
        Javac[javac编译器]
        Bytecode[字节码文件 .class]
    end
    
    subgraph JVM["JVM运行时"]
        CL[类加载器子系统]
        RT[运行时数据区]
        EE[执行引擎]
        NA[本地方法接口]
    end
    
    subgraph OS["操作系统层"]
        Native[本地操作系统]
    end
    
    Java --> Javac
    Javac --> Bytecode
    Bytecode --> CL
    CL --> RT
    RT --> EE
    EE --> NA
    NA --> Native
```

### 2. 运行时数据区

```mermaid
flowchart TB
    subgraph Runtime["JVM运行时数据区"]
        direction TB
        
        subgraph Private["线程私有区域"]
            PC[程序计数器]
            Stack[虚拟机栈]
            NativeStack[本地方法栈]
        end
        
        subgraph Shared["线程共享区域"]
            Heap[Java堆]
            MethodArea[方法区/元空间]
        end
        
        subgraph Direct["直接内存"]
            DirectMem[Native堆外内存]
        end
    end
    
    subgraph HeapDetail["堆内存细分"]
        Young[年轻代]
        Old[老年代]
        Eden[Eden区]
        S0[Survivor0]
        S1[Survivor1]
    end
    
    Heap --> Young
    Heap --> Old
    Young --> Eden
    Young --> S0
    Young --> S1
```

### 3. 类加载生命周期

```mermaid
stateDiagram-v2
    [*] --> 加载
    加载 --> 验证
    验证 --> 准备
    准备 --> 解析
    解析 --> 初始化
    初始化 --> 使用
    使用 --> 卸载
    卸载 --> [*]
    
    加载 --> 初始化: 立即初始化情况
    验证 --> 初始化: 立即初始化情况
    准备 --> 初始化: 立即初始化情况
```

### 4. 垃圾收集器演进

```mermaid
timeline
    title JVM垃圾收集器发展历程
    section JDK 1.3
        Serial : 单线程收集器
        Serial Old : 老年代版本
    section JDK 1.4
        ParNew : Serial多线程版本
        Parallel Scavenge : 吞吐量优先
        CMS : 并发低停顿收集器
    section JDK 6
        Parallel Old : Parallel Scavenge老年代版本
    section JDK 7
        G1 : 区域化分代收集器
    section JDK 11
        ZGC : 低延迟可扩展收集器
        Epsilon : 无操作收集器
    section JDK 15
        Shenandoah : 低延迟收集器
    section JDK 21
        Generational ZGC : 分代ZGC
```

### 5. Java内存模型

```mermaid
flowchart LR
    subgraph JMM["Java内存模型 JMM"]
        direction TB
        
        subgraph Main["主内存 Main Memory"]
            Variables[共享变量]
        end
        
        subgraph Work1["工作内存1"]
            Copy1[变量副本]
        end
        
        subgraph Work2["工作内存2"]
            Copy2[变量副本]
        end
        
        subgraph WorkN["工作内存N"]
            CopyN[变量副本]
        end
    end
    
    subgraph Operations["内存交互操作"]
        Lock[lock锁定]
        Unlock[unlock解锁]
        Read[read读取]
        Load[load载入]
        Use[use使用]
        Assign[assign赋值]
        Store[store存储]
        Write[write写入]
    end
    
    Main --> Work1
    Main --> Work2
    Main --> WorkN
```

## 快速开始

### 查看 JVM 版本

```bash
java -version
```

### 查看 JVM 参数

```bash
java -XX:+PrintFlagsFinal -version
```

### 常用监控命令

```bash
# 查看 JVM 进程
jps -l

# 查看堆内存使用情况
jmap -heap <pid>

# 查看 GC 情况
jstat -gc <pid> 1000

# 生成堆转储文件
jmap -dump:format=b,file=heap.hprof <pid>
```

## 推荐学习资源

- [官方 JVM 规范](https://docs.oracle.com/javase/specs/jvms/se17/html/)
- [深入理解 Java 虚拟机（周志明）](https://book.douban.com/subject/34907497/)
- [Java Performance: The Definitive Guide](https://www.oreilly.com/library/view/java-performance/9781449363512/)
