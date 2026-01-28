# linsir-abc-core 项目说明

## 项目概述

linsir-abc-core 是 linsir-abc 项目的核心子模块，主要包含基础的 Java 示例代码和工具类，涵盖了面向对象编程、设计模式、多线程等核心 Java 知识点。该模块作为整个项目的基础组件，为其他子模块提供通用的功能支持。

## 项目结构

### 目录结构

```
linsir-abc-core/
├── src/
│   ├── main/java/com/linsir/core/
│   │   ├── base/           # 基础类和接口
│   │   ├── c1/             # 循环相关示例
│   │   ├── c2/             # 披萨店设计模式示例
│   │   ├── c3/             # 饮料店设计模式示例
│   │   ├── c4/             # 测试示例
│   │   ├── c5/             # 测试示例
│   │   ├── c6/             # 测试示例
│   │   ├── c7/             # 多线程示例
│   │   ├── c8/             # 网络编程示例
│   │   ├── chapter3/       # 第三章示例
│   │   └── thread/         # 线程相关示例
│   └── test/               # 测试代码
├── target/                 # 构建输出目录
└── pom.xml                 # Maven 配置文件
```

### 核心模块说明

1. **base 包**：包含基础的类和接口示例，展示了面向对象编程的基本概念
   - `AClass`：基础类示例
   - `InterfaceA`、`InterfaceB`、`InterfaceC`：接口示例
   - `InterfaceCImpl`：接口实现示例

2. **c2 包**：披萨店设计模式示例，展示了简单工厂模式的应用
   - `Pizza`：披萨基类
   - `BaconPizza`、`FruitsPizza`：具体披萨实现
   - `PizzzaStore`：披萨店类，负责创建披萨

3. **c3 包**：饮料店设计模式示例，展示了工厂方法模式的应用
   - `Drink`：饮料基类
   - `Coffee`、`Cola`、`Milkytea`：具体饮料实现
   - `DrinkStore`：饮料店类，负责创建饮料

4. **thread 包**：多线程编程示例，展示了线程的各种状态和操作
   - `Daemon`：守护线程示例
   - `Interrupted`：线程中断示例
   - `MultiThread`：多线程示例
   - `ThreadState`：线程状态示例
   - `Shutdown`：线程关闭示例

5. **c7 包**：多线程并发示例
   - `BuyTicketThread`：购票线程示例

6. **c8 包**：网络编程示例
   - `ServerThread`：服务器线程示例
   - `TestClient`：客户端测试示例
   - `TestServer`：服务器测试示例

## 技术栈

| 技术/依赖           | 版本         | 用途                     |
|--------------------|--------------|--------------------------|
| Java               | 8            | 基础开发语言             |
| Maven              | 3.x          | 项目构建和依赖管理       |
| JUnit              | 4.13.2       | 单元测试框架             |
| JUnit Jupiter      | RELEASE      | JUnit 5 测试框架         |
| SLF4J              | 1.7.25       | 日志门面                 |
| Logback            | 1.2.3        | 日志实现                 |

## 主要功能

1. **面向对象编程示例**：展示了类、接口、继承、实现等基本概念
2. **设计模式示例**：包含简单工厂模式、工厂方法模式等设计模式的应用
3. **多线程编程示例**：展示了线程的创建、状态管理、中断等操作
4. **网络编程示例**：展示了基本的客户端-服务器通信
5. **基础语法示例**：包含循环、条件判断等基础语法的使用

## 构建与运行

### 构建项目

```bash
# 在项目根目录执行
mvn clean install
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=TestClassName
```

### 运行示例

```bash
# 编译并运行特定类
mvn compile exec:java -Dexec.mainClass="com.linsir.core.c2.Test"
```

## 示例代码说明

### 1. 披萨店示例（c2 包）

展示了简单工厂模式的应用，通过 `PizzzaStore` 类创建不同类型的披萨：

```java
// 创建披萨店
PizzzaStore store = new PizzzaStore();

// 订购培根披萨
Pizza baconPizza = store.orderPizza("bacon");
System.out.println("订购了：" + baconPizza.getName());

// 订购水果披萨
Pizza fruitsPizza = store.orderPizza("fruits");
System.out.println("订购了：" + fruitsPizza.getName());
```

### 2. 饮料店示例（c3 包）

展示了工厂方法模式的应用，通过 `DrinkStore` 类创建不同类型的饮料：

```java
// 创建饮料店
DrinkStore store = new DrinkStore();

// 点咖啡
Drink coffee = store.orderDrink("coffee");
System.out.println("点了：" + coffee.getName());

// 点可乐
Drink cola = store.orderDrink("cola");
System.out.println("点了：" + cola.getName());

// 点奶茶
Drink milkytea = store.orderDrink("milkytea");
System.out.println("点了：" + milkytea.getName());
```

### 3. 多线程示例（thread 包）

展示了多线程的创建和管理：

```java
// 创建并启动多个线程
for (int i = 0; i < 5; i++) {
    Thread thread = new Thread(new MultiThread.Runner(), "Thread-" + i);
    thread.start();
}

// 等待所有线程完成
Thread.sleep(2000);
System.out.println("Main thread exiting.");
```

## 项目特点

1. **模块化设计**：清晰的包结构，便于理解和维护
2. **示例丰富**：涵盖了 Java 核心知识点的多个方面
3. **设计模式应用**：通过实际示例展示了设计模式的使用
4. **多线程实践**：包含了多线程编程的常见场景和解决方案
5. **易于扩展**：模块化的结构使得添加新功能非常方便

## 学习价值

- **Java 基础学习**：适合 Java 初学者学习面向对象编程和基础语法
- **设计模式理解**：通过实际示例理解设计模式的应用场景和实现方式
- **多线程编程**：学习多线程的基本概念和实践技巧
- **项目结构参考**：了解 Maven 项目的标准结构和配置

## 未来规划

1. **增加更多设计模式示例**：如单例模式、观察者模式、策略模式等
2. **完善多线程并发工具**：添加线程池、并发集合等示例
3. **增加网络编程深度**：添加 HTTP 服务器、WebSocket 等示例
4. **添加数据库操作示例**：展示 JDBC、JPA 等数据库操作
5. **集成 Spring 框架**：展示 Spring 的基本使用

## 贡献指南

欢迎对项目进行贡献，包括：

1. **添加新的示例代码**：补充未覆盖的 Java 知识点
2. **改进现有代码**：优化代码结构和性能
3. **修复 bug**：解决代码中存在的问题
4. **完善文档**：补充和改进项目文档

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 联系方式

如有问题或建议，欢迎联系项目维护者：

- 邮箱：example@example.com
- GitHub：https://github.com/example/linsir-abc

---

**注**：本项目仅用于学习和教学目的，展示了 Java 核心知识点的基本应用。