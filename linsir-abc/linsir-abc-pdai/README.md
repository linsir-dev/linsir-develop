# linsir-abc-pdai

## 项目概述

linsir-abc-pdai 是一个 Java 学习和示例项目，旨在通过实际代码演示 Java 核心概念、常用库的使用方法以及各种开发技术。项目包含了丰富的示例代码，涵盖了 Java 基础、集合框架、IO 操作、数据库连接、工具库使用等多个方面。

## 目录结构

项目采用标准 Maven 项目结构，主要代码位于 `src/main/java/com/linsir/abc/pdai` 目录下，按功能模块进行组织：

```
src/main/java/com/linsir/abc/pdai/
├── base/             # Java 基础相关示例
│   ├── annotationdemo/    # 注解使用示例
│   ├── equalsdemo/        # equals 和 hashCode 示例
│   ├── exceptiondemo/     # 异常处理示例
│   ├── generic/           # 泛型使用示例
│   ├── keywords/          # Java 关键字使用示例
│   ├── oopdemo/           # 面向对象编程示例
│   ├── reflectdemo/       # 反射使用示例
│   └── spidemo/           # SPI 机制示例
├── collection/       # 集合框架示例
│   ├── listdemo/          # List 集合使用示例
│   ├── mapdemo/           # Map 集合使用示例
│   ├── queuedemo/         # Queue 集合使用示例
│   └── setdemo/           # Set 集合使用示例
├── database/         # 数据库相关示例
│   ├── elasticsearch/     # Elasticsearch 相关文档
│   ├── mongodb/           # MongoDB 相关文档
│   ├── mysql/             # MySQL 相关示例
│   ├── principle/         # 数据库原理相关文档
│   └── redis/             # Redis 相关文档
├── io/               # IO 操作示例
│   ├── byteio/            # 字节流操作示例
│   ├── chario/            # 字符流操作示例
│   └── patterns/          # IO 设计模式示例
└── tools/            # 工具库使用示例
    ├── guava/             # Guava 工具库示例
    └── hutool/            # Hutool 工具库示例
```

## 依赖管理

项目使用 Maven 进行依赖管理，主要依赖包括：

- JDK 17+
- MySQL Connector/J 8.0.33
- Guava 32.1.3-jre
- Hutool 5.8.26

详细依赖信息可查看 `pom.xml` 文件。

## 示例代码说明

### 1. Java 基础示例 (`base/`)

- **注解示例**：演示自定义注解的创建和使用
- **equals 和 hashCode 示例**：演示正确实现 equals 和 hashCode 方法
- **异常处理示例**：演示自定义异常和异常处理机制
- **泛型示例**：演示泛型类、接口和方法的使用
- **关键字示例**：演示 final、finally、finalize 等关键字的使用
- **面向对象示例**：演示继承、多态、接口等面向对象特性
- **反射示例**：演示 Java 反射机制的使用
- **SPI 示例**：演示 Java SPI 机制的使用

### 2. 集合框架示例 (`collection/`)

- **List 示例**：演示 ArrayList、LinkedList 等 List 实现的使用
- **Map 示例**：演示 HashMap、TreeMap 等 Map 实现的使用
- **Queue 示例**：演示 Queue 接口及其实现的使用
- **Set 示例**：演示 HashSet、TreeSet 等 Set 实现的使用

### 3. 数据库示例 (`database/`)

- **MySQL 示例**：演示 JDBC 连接 MySQL 数据库
- **Elasticsearch 文档**：Elasticsearch 相关概念和使用说明
- **MongoDB 文档**：MongoDB 相关概念和使用说明
- **Redis 文档**：Redis 相关概念和使用说明

### 4. IO 操作示例 (`io/`)

- **字节流示例**：演示 ByteArrayInputStream、FileInputStream 等字节流的使用
- **字符流示例**：演示 FileReader、BufferedReader 等字符流的使用
- **IO 设计模式示例**：演示 BIO、AIO 等 IO 设计模式

### 5. 工具库示例 (`tools/`)

#### 5.1 Guava 示例 (`guava/`)

- **GuavaCollectionsDemo**：演示 Guava 增强集合的使用
- **GuavaStringsDemo**：演示 Guava 字符串工具的使用
- **GuavaCacheDemo**：演示 Guava 缓存的使用
- **GuavaFunctionalDemo**：演示 Guava 函数式编程工具的使用
- **GuavaConcurrencyDemo**：演示 Guava 并发工具的使用
- **GuavaDemoMain**：运行所有 Guava 示例的主类

#### 5.2 Hutool 示例 (`hutool/`)

- **HutoolCoreDemo**：演示 Hutool 核心工具类的使用
- **HutoolIODemo**：演示 Hutool IO 工具类的使用
- **HutoolHttpDemo**：演示 Hutool HTTP 工具类的使用
- **HutoolCryptoDemo**：演示 Hutool 加密工具类的使用
- **HutoolDemoMain**：运行所有 Hutool 示例的主类

## 如何运行示例

### 1. 运行 Guava 示例

```bash
# 使用 Maven 运行 Guava 示例
mvn exec:java -Dexec.mainClass=com.linsir.abc.pdai.tools.guava.GuavaDemoMain
```

### 2. 运行 Hutool 示例

```bash
# 使用 Maven 运行 Hutool 示例
mvn exec:java -Dexec.mainClass=com.linsir.abc.pdai.tools.hutool.HutoolDemoMain
```

### 3. 运行其他示例

对于其他示例，可以直接运行对应目录下的 `*Test.java` 文件，或使用 IDE 运行单个示例类。

## 项目特点

1. **全面的示例代码**：涵盖了 Java 开发中的各个方面
2. **详细的注释说明**：每个示例都有详细的注释说明
3. **模块化组织**：代码按功能模块进行组织，结构清晰
4. **工具库集成**：集成了 Guava 和 Hutool 等常用工具库
5. **易于学习**：示例代码简洁明了，易于理解和学习

## 贡献指南

欢迎贡献代码和文档，贡献方式如下：

1. Fork 本项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 Apache License 2.0 许可证，详见 LICENSE 文件。

## 联系信息

如有问题或建议，欢迎联系项目维护者。

---

**注意**：本项目仅供学习和参考使用，请勿用于生产环境。