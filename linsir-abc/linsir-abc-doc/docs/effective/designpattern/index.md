# 设计模式

> 23种经典设计模式的Java实现

***

## 什么是设计模式

设计模式（Design Pattern）是一套被反复使用、多数人知晓的、经过分类编目的、代码设计经验的总结。使用设计模式是为了可重用代码、让代码更容易被他人理解、保证代码可靠性。

## 设计模式的分类

### 创建型模式 (Creational Patterns)

创建型模式关注对象的创建机制，试图创建对象的方式适合当前的情况。

| 模式 | 描述 |
|------|------|
| [单例模式](./singleton/01-singleton-overview) | 确保一个类只有一个实例，并提供一个全局访问点 |
| [工厂模式](./factory/01-factory-overview) | 定义一个创建对象的接口，让子类决定实例化哪个类 |
| [抽象工厂模式](./abstract-factory/01-abstract-factory-overview) | 提供一个创建一系列相关或相互依赖对象的接口 |
| [建造者模式](./builder/01-builder-overview) | 将一个复杂对象的构建与它的表示分离 |
| [原型模式](./prototype/01-prototype-overview) | 用原型实例指定创建对象的种类，通过复制创建新对象 |

### 结构型模式 (Structural Patterns)

结构型模式关注如何组合类和对象以获得更大的结构。

| 模式 | 描述 |
|------|------|
| [代理模式](./proxy/01-proxy-overview) | 为其他对象提供一种代理以控制对这个对象的访问 |
| [适配器模式](./adapter/01-adapter-overview) | 将一个类的接口转换成客户希望的另外一个接口 |
| [桥接模式](./bridge/01-bridge-overview) | 将抽象部分与实现部分分离，使它们都可以独立变化 |
| [装饰器模式](./decorator/01-decorator-overview) | 动态地给一个对象添加一些额外的职责 |
| [外观模式](./facade/01-facade-overview) | 为子系统中的一组接口提供一个一致的界面 |
| [享元模式](./flyweight/01-flyweight-overview) | 运用共享技术有效地支持大量细粒度的对象 |
| [组合模式](./composite/01-composite-overview) | 将对象组合成树形结构以表示"部分-整体"的层次结构 |

### 行为型模式 (Behavioral Patterns)

行为型模式关注对象之间的通信和职责分配。

| 模式 | 描述 |
|------|------|
| [观察者模式](./observer/01-observer-overview) | 定义对象间的一对多依赖关系，当一个对象状态改变时，所有依赖者收到通知 |
| [策略模式](./strategy/01-strategy-overview) | 定义一系列算法，把它们一个个封装起来，并且使它们可相互替换 |
| [模板方法模式](./template-method/01-template-method-overview) | 定义一个操作中的算法的骨架，将一些步骤延迟到子类中 |
| [状态模式](./state/01-state-overview) | 允许对象在其内部状态改变时改变它的行为 |
| [命令模式](./command/01-command-overview) | 将一个请求封装为一个对象，从而使你可用不同的请求对客户进行参数化 |
| [迭代器模式](./iterator/01-iterator-overview) | 提供一种方法顺序访问一个聚合对象中的各个元素 |
| [中介者模式](./mediator/01-mediator-overview) | 用一个中介对象来封装一系列的对象交互 |
| [备忘录模式](./memento/01-memento-overview) | 在不破坏封装性的前提下，捕获一个对象的内部状态 |
| [访问者模式](./visitor/01-visitor-overview) | 表示一个作用于某对象结构中的各元素的操作 |
| [责任链模式](./chain-of-responsibility/01-chain-of-responsibility-overview) | 使多个对象都有机会处理请求，从而避免请求的发送者和接收者之间的耦合关系 |
| [解释器模式](./interpreter/01-interpreter-overview) | 给定一个语言，定义它的文法的一种表示，并定义一个解释器 |

***

## 如何选择设计模式

1. **考虑设计模式是怎样解决设计问题的** - 了解模式如何帮助你解决特定问题
2. **浏览模式的意图部分** - 找到与你面临的问题相关的模式
3. **研究模式如何相互关联** - 了解模式之间的关系
4. **研究目的相似的模式** - 比较相似模式的优缺点
5. **检查重新设计的原因** - 了解什么导致设计需要改变
