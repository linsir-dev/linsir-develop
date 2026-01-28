# linsir-abc-effective 项目说明

## 项目概述

linsir-abc-effective 是 linsir-abc 项目的一个重要子模块，主要专注于设计模式的实现和分布式锁的应用。该模块提供了 23 种经典设计模式的完整 Java 实现，以及基于 ZooKeeper 的分布式锁实现，旨在帮助开发者更好地理解和应用这些核心编程概念。

## 项目结构

### 目录结构

```
linsir-abc-effective/
├── src/
│   ├── main/java/com/linsir/
│   │   ├── designpattern/      # 设计模式实现
│   │   │   ├── abstractFactory/      # 抽象工厂模式
│   │   │   ├── adapter/             # 适配器模式
│   │   │   ├── bridge/              # 桥接模式
│   │   │   ├── builder/             # 建造者模式
│   │   │   ├── chainOfResponsibility/ # 责任链模式
│   │   │   ├── command/             # 命令模式
│   │   │   ├── composite/           # 组合模式
│   │   │   ├── decorator/           # 装饰器模式
│   │   │   ├── facade/              # 外观模式
│   │   │   ├── factory/             # 工厂模式
│   │   │   ├── flyweight/           # 享元模式
│   │   │   ├── interpreter/         # 解释器模式
│   │   │   ├── iterator/            # 迭代器模式
│   │   │   ├── mediator/            # 中介者模式
│   │   │   ├── memento/             # 备忘录模式
│   │   │   ├── observer/            # 观察者模式
│   │   │   ├── protype/             # 原型模式
│   │   │   ├── proxy/               # 代理模式
│   │   │   ├── singleton/           # 单例模式
│   │   │   ├── state/               # 状态模式
│   │   │   ├── strategy/            # 策略模式
│   │   │   └── templateMethod/      # 模板方法模式
│   │   └── lock/                   # 分布式锁实现
│   │       ├── ZkLock.java           # ZooKeeper 锁基础实现
│   │       └── ZookeeperImproverLock.java # ZooKeeper 锁改进实现
│   └── test/                       # 测试代码
├── target/                         # 构建输出目录
└── pom.xml                         # Maven 配置文件
```

### 核心模块说明

1. **designpattern 包**：包含 23 种经典设计模式的完整实现
   - **创建型模式**：单例模式、工厂模式、抽象工厂模式、建造者模式、原型模式
   - **结构型模式**：适配器模式、桥接模式、组合模式、装饰器模式、外观模式、享元模式、代理模式
   - **行为型模式**：责任链模式、命令模式、解释器模式、迭代器模式、中介者模式、备忘录模式、观察者模式、状态模式、策略模式、模板方法模式、访问者模式

2. **lock 包**：包含基于 ZooKeeper 的分布式锁实现
   - `ZkLock`：基础的 ZooKeeper 分布式锁实现
   - `ZookeeperImproverLock`：改进的 ZooKeeper 分布式锁实现，提供更可靠的锁机制

## 技术栈

| 技术/依赖           | 版本    | 用途                     |
|--------------------|---------|--------------------------|
| Java               | 8+      | 基础开发语言             |
| Maven              | 3.x     | 项目构建和依赖管理       |
| ZooKeeper Client   | 0.9     | 分布式锁的 ZooKeeper 客户端 |

## 核心功能

### 1. 设计模式实现

提供了 23 种经典设计模式的完整 Java 实现，每种模式都有清晰的代码结构和示例：

- **单例模式**：5 种不同实现方式（饿汉式、懒汉式、双重检查锁、静态内部类、枚举）
- **工厂模式**：简单工厂、工厂方法、抽象工厂
- **观察者模式**：完整的观察者模式实现
- **策略模式**：数学运算策略示例
- **装饰器模式**：动态添加功能的实现
- **代理模式**：静态代理、动态代理示例
- **其他设计模式**：完整的 23 种设计模式实现

### 2. 分布式锁实现

基于 ZooKeeper 实现了分布式锁机制，解决分布式环境下的并发问题：

- **基础分布式锁**：`ZkLock` 类实现了基本的分布式锁功能
- **改进分布式锁**：`ZookeeperImproverLock` 类提供了更可靠的分布式锁实现，包括锁的获取、释放、重入等特性

## 设计模式实现详情

### 创建型模式

| 设计模式 | 实现类 | 说明 |
|---------|--------|------|
| 单例模式 | `singleton/SingletonDemo1-5` | 5 种不同实现方式 |
| 工厂模式 | `factory/` | 简单工厂、工厂方法模式 |
| 抽象工厂 | `abstractFactory/` | 抽象工厂模式实现 |
| 建造者模式 | `builder/` | 建造者模式实现 |
| 原型模式 | `protype/` | 原型模式实现，支持深拷贝 |

### 结构型模式

| 设计模式 | 实现类 | 说明 |
|---------|--------|------|
| 适配器模式 | `adapter/` | 适配器模式实现 |
| 桥接模式 | `bridge/` | 桥接模式实现 |
| 组合模式 | `composite/` | 组合模式实现 |
| 装饰器模式 | `decorator/` | 装饰器模式实现 |
| 外观模式 | `facade/` | 外观模式实现 |
| 享元模式 | `flyweight/` | 享元模式实现 |
| 代理模式 | `proxy/` | 代理模式实现 |

### 行为型模式

| 设计模式 | 实现类 | 说明 |
|---------|--------|------|
| 责任链模式 | `chainOfResponsibility/` | 责任链模式实现 |
| 命令模式 | `command/` | 命令模式实现 |
| 解释器模式 | `interpreter/` | 解释器模式实现 |
| 迭代器模式 | `iterator/` | 迭代器模式实现 |
| 中介者模式 | `mediator/` | 中介者模式实现 |
| 备忘录模式 | `memento/` | 备忘录模式实现 |
| 观察者模式 | `observer/` | 观察者模式实现 |
| 状态模式 | `state/` | 状态模式实现 |
| 策略模式 | `strategy/` | 策略模式实现 |
| 模板方法模式 | `templateMethod/` | 模板方法模式实现 |
| 访问者模式 | `visitor/` | 访问者模式实现 |

## 分布式锁实现详情

### ZooKeeper 分布式锁

| 实现类 | 说明 | 核心功能 |
|--------|------|----------|
| `ZkLock` | 基础分布式锁 | 基本的锁获取和释放 |
| `ZookeeperImproverLock` | 改进分布式锁 | 更可靠的锁机制，支持自动重连、会话过期处理等 |

## 构建与运行

### 构建项目

```bash
# 在项目根目录执行
mvn clean install
```

### 运行示例

```bash
# 编译并运行特定设计模式示例
mvn compile exec:java -Dexec.mainClass="com.linsir.designpattern.singleton.SingletonDemo1"

# 运行分布式锁示例
mvn compile exec:java -Dexec.mainClass="com.linsir.lock.ZookeeperImproverLock"
```

### 运行分布式锁注意事项

运行分布式锁示例前，需要确保：
1. ZooKeeper 服务已经启动
2. 配置正确的 ZooKeeper 连接地址
3. 网络能够正常访问 ZooKeeper 服务

## 示例代码

### 1. 单例模式示例

```java
// 饿汉式单例模式
public class SingletonDemo1 {
    // 私有静态实例，立即初始化
    private static SingletonDemo1 instance = new SingletonDemo1();
    
    // 私有构造方法
    private SingletonDemo1() {
    }
    
    // 公共静态方法，返回实例
    public static SingletonDemo1 getInstance() {
        return instance;
    }
}

// 使用方式
SingletonDemo1 singleton = SingletonDemo1.getInstance();
```

### 2. 工厂模式示例

```java
// 工厂模式中的产品接口
public interface Shape {
    void draw();
}

// 具体产品实现
public class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing Circle");
    }
}

// 工厂类
public class ShapeFactory {
    public Shape getShape(String shapeType) {
        if (shapeType == null) {
            return null;
        }
        if (shapeType.equalsIgnoreCase("CIRCLE")) {
            return new Circle();
        } else if (shapeType.equalsIgnoreCase("RECTANGLE")) {
            return new Rectangle();
        } else if (shapeType.equalsIgnoreCase("SQUARE")) {
            return new Square();
        }
        return null;
    }
}

// 使用方式
ShapeFactory shapeFactory = new ShapeFactory();
Shape circle = shapeFactory.getShape("CIRCLE");
circle.draw();
```

### 3. 观察者模式示例

```java
// 主题接口
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

// 具体主题
public class ConcreteSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private int state;
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
        notifyObservers();
    }
    
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}

// 使用方式
ConcreteSubject subject = new ConcreteSubject();
Observer observer1 = new ConcreteObserver(subject);
Observer observer2 = new ConcreteObserver(subject);
subject.attach(observer1);
subject.attach(observer2);
subject.setState(10); // 会通知所有观察者
```

### 4. 分布式锁示例

```java
// 使用分布式锁
ZookeeperImproverLock lock = new ZookeeperImproverLock("localhost:2181", "lock");

try {
    // 获取锁
    if (lock.lock()) {
        System.out.println("获取锁成功，执行业务逻辑...");
        // 执行业务逻辑
        Thread.sleep(5000);
    }
} catch (Exception e) {
    e.printStackTrace();
} finally {
    // 释放锁
    lock.unlock();
    System.out.println("释放锁成功");
}
```

## 项目特点

1. **完整的设计模式实现**：包含 23 种经典设计模式的完整 Java 实现
2. **代码结构清晰**：每种设计模式都有独立的包和清晰的类结构
3. **示例丰富**：每种设计模式都有详细的示例代码和使用方式
4. **分布式锁实现**：基于 ZooKeeper 实现了可靠的分布式锁机制
5. **易于理解**：代码注释详细，便于学习和理解
6. **易于扩展**：模块化的结构使得添加新的设计模式或功能非常方便

## 学习价值

- **设计模式学习**：通过完整的实现示例，深入理解 23 种经典设计模式
- **分布式锁实践**：学习基于 ZooKeeper 的分布式锁实现原理和应用
- **代码结构参考**：了解如何组织一个包含多种设计模式的项目
- **编程思想培养**：通过设计模式的学习，培养良好的编程思想和设计能力
- **分布式系统理解**：通过分布式锁的实现，加深对分布式系统的理解

## 设计模式应用场景

### 创建型模式应用场景

| 设计模式 | 应用场景 |
|---------|----------|
| 单例模式 | 配置管理、日志记录、线程池等需要唯一实例的场景 |
| 工厂模式 | 对象创建逻辑复杂，需要统一管理的场景 |
| 抽象工厂 | 产品族创建，需要多个相关产品的场景 |
| 建造者模式 | 复杂对象的创建，需要多个步骤的场景 |
| 原型模式 | 对象创建成本高，需要克隆的场景 |

### 结构型模式应用场景

| 设计模式 | 应用场景 |
|---------|----------|
| 适配器模式 | 接口不兼容，需要适配的场景 |
| 桥接模式 | 多维度变化，需要解耦的场景 |
| 组合模式 | 树形结构，需要统一处理的场景 |
| 装饰器模式 | 动态添加功能，不修改原有代码的场景 |
| 外观模式 | 复杂系统，需要简化接口的场景 |
| 享元模式 | 大量相似对象，需要共享的场景 |
| 代理模式 | 访问控制、远程调用、延迟加载的场景 |

### 行为型模式应用场景

| 设计模式 | 应用场景 |
|---------|----------|
| 责任链模式 | 请求处理链，需要多个处理器的场景 |
| 命令模式 | 操作封装，需要撤销/重做的场景 |
| 解释器模式 | 语言解析，需要解释语法的场景 |
| 迭代器模式 | 集合遍历，需要统一遍历接口的场景 |
| 中介者模式 | 多个对象交互，需要集中协调的场景 |
| 备忘录模式 | 状态保存，需要恢复状态的场景 |
| 观察者模式 | 事件通知，需要一对多通信的场景 |
| 状态模式 | 状态转换，需要根据状态改变行为的场景 |
| 策略模式 | 算法选择，需要动态切换算法的场景 |
| 模板方法模式 | 算法骨架，需要子类实现具体步骤的场景 |
| 访问者模式 | 元素操作，需要分离算法和元素的场景 |

## 未来规划

1. **增加更多设计模式示例**：为每种设计模式添加更多实际应用示例
2. **完善分布式锁实现**：添加更多类型的分布式锁实现（如基于 Redis 的分布式锁）
3. **增加设计模式的测试用例**：为每种设计模式添加单元测试
4. **添加设计模式的性能对比**：分析不同设计模式的性能特点
5. **集成更多分布式技术**：添加分布式协调、分布式配置等功能
6. **提供设计模式的最佳实践指南**：总结每种设计模式的最佳应用场景和实践

## 贡献指南

欢迎对项目进行贡献，包括：

1. **添加新的设计模式实现**：补充未覆盖的设计模式或变体
2. **改进现有实现**：优化代码结构和性能
3. **添加测试用例**：为设计模式和分布式锁添加单元测试
4. **完善文档**：补充和改进项目文档
5. **添加新功能**：如其他类型的分布式锁实现

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 联系方式

如有问题或建议，欢迎联系项目维护者：

- 邮箱：example@example.com
- GitHub：https://github.com/example/linsir-abc

---

**注**：本项目主要用于学习和教学目的，展示了设计模式和分布式锁的基本实现原理。在实际生产环境中使用时，需要根据具体场景进行适当的调整和优化。