---
layout: home

hero:
  name: "Linsir JDK"
  text: "JDK 特性学习文档"
  tagline: 深入学习 JDK 8 和 JDK 11 的新特性
  actions:
    - theme: brand
      text: JDK 8 新特性
      link: /jdk8/
    - theme: alt
      text: JDK 11 新特性
      link: /jdk11/
    - theme: alt
      text: 面试题
      link: /interview/

features:
  - title: Lambda 表达式
    details: 学习函数式编程思想，简化代码编写，提高代码可读性
    link: /jdk8/lambda
  - title: Stream API
    details: 掌握流式数据处理，简化集合操作，支持并行计算
    link: /jdk8/stream
  - title: 函数式接口
    details: 理解函数式接口概念，熟练使用内置函数式接口
    link: /jdk8/functional-interface
  - title: 方法引用
    details: 简化 Lambda 表达式，提高代码简洁性
    link: /jdk8/method-reference
  - title: Optional
    details: 优雅处理空值，避免 NullPointerException
    link: /jdk8/optional
  - title: 新日期时间 API
    details: 替代旧的 Date 和 Calendar，设计更合理
    link: /jdk8/datetime
  - title: var 类型推断
    details: 简化变量声明，提高代码可读性
    link: /jdk11/var-type-inference
  - title: 接口私有方法
    details: 增强接口灵活性，支持代码复用
    link: /jdk11/interface-private-methods
---

## 快速开始

本项目整理了 JDK 8 和 JDK 11 的核心新特性，包含详细的代码示例和面试题。

### JDK 8 核心特性

- **Lambda 表达式**：函数式编程的基础
- **Stream API**：流式数据处理
- **函数式接口**：@FunctionalInterface
- **方法引用**：简化 Lambda 表达式
- **Optional**：空值处理
- **新的日期时间 API**：LocalDate、LocalTime、LocalDateTime
- **接口默认方法**：default 关键字

### JDK 11 核心特性

- **var 类型推断**：局部变量类型推断
- **接口私有方法**：private 接口方法
- **ProcessHandle**：进程管理 API
- **HTTP Client**：标准 HTTP 客户端
- **字符串新方法**：isBlank、lines、strip 等

### 目录结构

```
linsir-jdk/
├── linsir-jdk-8/          # JDK 8 示例代码
│   ├── lambda/            # Lambda 表达式
│   ├── stream/            # Stream API
│   └── jdk8features/      # 其他特性
├── linsir-jdk-11/         # JDK 11 示例代码
│   ├── var/               # 类型推断
│   ├── interfaceprivate/  # 接口私有方法
│   └── process/           # 进程管理
└── docs/                  # 文档站点
    ├── jdk8/              # JDK 8 文档
    ├── jdk11/             # JDK 11 文档
    └── interview/         # 面试题
```

## 开始学习

选择你感兴趣的主题，开始 JDK 学习之旅：

- [JDK 8 新特性](/jdk8/) - 从 Lambda 表达式开始
- [JDK 11 新特性](/jdk11/) - 探索最新特性
- [面试题](/interview/) - 准备技术面试
