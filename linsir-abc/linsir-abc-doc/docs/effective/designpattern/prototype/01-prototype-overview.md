# 原型模式 (Prototype Pattern)

> 用原型实例指定创建对象的种类，并且通过拷贝这些原型创建新的对象

***

## 一、模式概述

### 1.1 定义

原型模式（Prototype Pattern）是一种创建型设计模式，它允许通过复制现有对象来创建新对象，而不是通过实例化类。

### 1.2 适用场景

- 当一个系统应该独立于它的产品创建、构成和表示时
- 当要实例化的类是在运行时刻指定时
- 为了避免创建一个与产品类层次平行的工厂类层次时
- 当一个类的实例只能有几个不同状态组合中的一种时

### 1.3 优缺点

| 优点 | 缺点 |
|------|------|
| 性能提高 | 配备克隆方法需要对类的功能进行通盘考虑 |
| 逃避构造函数的约束 | 必须实现Cloneable接口 |
| 简化对象创建 | 深拷贝与浅拷贝问题 |

***

## 二、实现方式

### 2.1 浅拷贝

```java
public class Prototype implements Cloneable {
    private String name;
    
    @Override
    public Prototype clone() throws CloneNotSupportedException {
        return (Prototype) super.clone();
    }
}
```

### 2.2 深拷贝

```java
public class Prototype implements Cloneable, Serializable {
    private String name;
    private List<String> list;
    
    // 使用序列化实现深拷贝
    public Prototype deepClone() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (Prototype) ois.readObject();
    }
}
```

***

## 三、相关文档

- [代码指南](./02-prototype-code-guide.md)
