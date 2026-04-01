# 适配器模式 (Adapter Pattern)

> 将一个类的接口转换成客户希望的另外一个接口

***

## 一、模式概述

### 1.1 定义

适配器模式（Adapter Pattern）是一种结构型设计模式，它允许接口不兼容的对象能够相互合作。

### 1.2 适用场景

- 需要使用现有的类，但其接口不符合需要
- 需要创建一个可以复用的类，该类可以与其他不相关的类或不可预见的类协同工作
- 需要使用几个现有的子类，但是不可能对每一个都进行子类化以匹配它们的接口

### 1.3 优缺点

| 优点 | 缺点 |
|------|------|
| 提高了类的复用性 | 过多使用会导致系统混乱 |
| 增加了类的透明度 | 增加了系统的复杂度 |
| 灵活性好 | Java最多只能适配一个适配者类 |

***

## 二、实现方式

### 2.1 类适配器（使用继承）

```java
// 目标接口
public interface BigPort {
    void useBigPort();
}

// 被适配者
public class SmallPort {
    public void useSmallPort() {
        System.out.println("使用小端口");
    }
}

// 适配器
public class SmallToBig extends SmallPort implements BigPort {
    @Override
    public void useBigPort() {
        useSmallPort();
    }
}
```

### 2.2 对象适配器（使用组合）

```java
public class SmallToBig implements BigPort {
    private SmallPort smallPort;
    
    public SmallToBig(SmallPort smallPort) {
        this.smallPort = smallPort;
    }
    
    @Override
    public void useBigPort() {
        smallPort.useSmallPort();
    }
}
```

***

## 三、相关文档

- [代码指南](./02-adapter-code-guide.md)
