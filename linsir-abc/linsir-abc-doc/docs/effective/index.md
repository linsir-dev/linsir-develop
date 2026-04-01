# Effective

> Java 最佳实践与设计模式

***

## 目录

### [设计模式](./designpattern/)

设计模式是解决软件设计中常见问题的可复用解决方案。本项目包含 23 种经典设计模式的实现和文档。

#### 创建型模式 (5种)
- [单例模式](./designpattern/singleton/01-singleton-overview) - 确保一个类只有一个实例
- [工厂模式](./designpattern/factory/01-factory-overview) - 定义创建对象的接口
- [抽象工厂模式](./designpattern/abstract-factory/01-abstract-factory-overview) - 创建相关或依赖对象的家族
- [建造者模式](./designpattern/builder/01-builder-overview) - 分步骤构建复杂对象
- [原型模式](./designpattern/prototype/01-prototype-overview) - 通过复制现有对象创建新对象

#### 结构型模式 (7种)
- [代理模式](./designpattern/proxy/01-proxy-overview) - 为其他对象提供代理以控制访问
- [适配器模式](./designpattern/adapter/01-adapter-overview) - 将类的接口转换成客户希望的接口
- [桥接模式](./designpattern/bridge/01-bridge-overview) - 分离抽象部分与实现部分
- [装饰器模式](./designpattern/decorator/01-decorator-overview) - 动态地给对象添加额外职责
- [外观模式](./designpattern/facade/01-facade-overview) - 为子系统提供统一的接口
- [享元模式](./designpattern/flyweight/01-flyweight-overview) - 运用共享技术支持大量细粒度对象
- [组合模式](./designpattern/composite/01-composite-overview) - 将对象组合成树形结构

#### 行为型模式 (11种)
- [观察者模式](./designpattern/observer/01-observer-overview) - 定义对象间的一对多依赖关系
- [策略模式](./designpattern/strategy/01-strategy-overview) - 定义一系列算法并使其可相互替换
- [模板方法模式](./designpattern/template-method/01-template-method-overview) - 定义算法骨架，延迟到子类实现
- [状态模式](./designpattern/state/01-state-overview) - 允许对象在状态改变时改变行为
- [命令模式](./designpattern/command/01-command-overview) - 将请求封装为对象
- [迭代器模式](./designpattern/iterator/01-iterator-overview) - 顺序访问聚合对象的元素
- [中介者模式](./designpattern/mediator/01-mediator-overview) - 封装对象间的交互
- [备忘录模式](./designpattern/memento/01-memento-overview) - 捕获并保存对象的内部状态
- [访问者模式](./designpattern/visitor/01-visitor-overview) - 作用于对象结构中各元素的操作
- [责任链模式](./designpattern/chain-of-responsibility/01-chain-of-responsibility-overview) - 使多个对象有机会处理请求
- [解释器模式](./designpattern/interpreter/01-interpreter-overview) - 定义语言的文法表示

***

## 设计原则

### SOLID 原则

1. **单一职责原则 (SRP)** - 一个类应该只有一个引起它变化的原因
2. **开闭原则 (OCP)** - 对扩展开放，对修改关闭
3. **里氏替换原则 (LSP)** - 子类必须能够替换其父类
4. **接口隔离原则 (ISP)** - 客户端不应该依赖它不需要的接口
5. **依赖倒置原则 (DIP)** - 高层模块不应该依赖低层模块，两者都应该依赖抽象

### 其他重要原则

- **DRY** - Don't Repeat Yourself（不要重复自己）
- **KISS** - Keep It Simple, Stupid（保持简单）
- **YAGNI** - You Aren't Gonna Need It（你不会需要它）
