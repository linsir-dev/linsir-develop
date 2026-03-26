# Java 语法基础

## 概述

本模块提供 Java 语法基础的完整学习资源，包括详细设计文档、代码示例和面试题汇总。

## 内容结构

### 📋 详细设计文档
[01-grammar-detailed-design.md](./01-grammar-detailed-design.md)

涵盖 10 大语法模块的详细设计：
- 数据类型（基本类型、引用类型、类型转换）
- 变量与常量（作用域、final、枚举）
- 运算符（算术、位运算、逻辑）
- 流程控制（条件、循环）
- 数组（基础、操作）
- 方法（基础、参数传递、可变参数）
- 面向对象（类、继承、多态、抽象类、接口）
- 异常处理（处理机制、自定义异常）
- 泛型（类、方法、通配符）
- 注解（内置、自定义）

### 💻 代码指南
[02-grammar-code-guide.md](./02-grammar-code-guide.md)

包含 27 个完整示例类：
- 代码结构说明
- 使用场景分析
- 测试代码结构
- 预期运行结果

### ❓ 面试题汇总
[03-grammar-interview-questions.md](./03-grammar-interview-questions.md)

精选 30 道高频面试题：
- 基本数据类型与包装类
- String 与字符串常量池
- 面向对象三大特性
- 异常处理机制
- 泛型与通配符
- 注解与反射

## 快速开始

### 查看代码示例

所有示例代码位于 `linsir-abc-core/src/main/java/com/linsir/abc/core/grammar/` 目录下：

```
grammar/
├── annotation/      # 注解
├── array/           # 数组
├── controlflow/     # 流程控制
├── datatype/        # 数据类型
├── exception/       # 异常处理
├── generic/         # 泛型
├── method/          # 方法
├── oop/             # 面向对象
├── operator/        # 运算符
└── variable/        # 变量与常量
```

### 运行示例

```bash
cd linsir-abc-core/src/main/java
javac -encoding UTF-8 com/linsir/abc/core/grammar/datatype/PrimitiveTypes.java
java com.linsir.abc.core.grammar.datatype.PrimitiveTypes
```

## 学习路径

1. **基础阶段**: 数据类型 → 变量 → 运算符 → 流程控制
2. **进阶阶段**: 数组 → 方法 → 面向对象
3. **高级阶段**: 异常处理 → 泛型 → 注解

## 对应 JDK 模块

所有示例代码均对应 JDK 标准库：
- `java.lang`: 基本类型、包装类、String、Object
- `java.util`: Arrays、集合框架
- `java.lang.reflect`: 反射机制
- `java.lang.annotation`: 注解支持

---

*最后更新: 2026-03-27*
