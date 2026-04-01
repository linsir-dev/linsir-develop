# 原型模式 - 代码指南

> 本文档详细说明原型模式的代码实现和使用方法

***

## 一、项目结构

```
prototype/
├── Prototype.java    # 原型类
└── Client.java       # 客户端
```

***

## 二、代码详解

### 2.1 Prototype - 原型类

```java
package com.linsir.designpattern.prototype;

public class Prototype implements Cloneable {
    private String name;
    
    public Prototype(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public Prototype clone() throws CloneNotSupportedException {
        return (Prototype) super.clone();
    }
}
```

***

## 三、使用示例

```java
public class PrototypeTest {
    public static void main(String[] args) throws CloneNotSupportedException {
        // 创建原型对象
        Prototype prototype = new Prototype("原型");
        
        // 克隆对象
        Prototype clone = prototype.clone();
        
        System.out.println("原型: " + prototype.getName());
        System.out.println("克隆: " + clone.getName());
    }
}
```

***

## 四、深拷贝与浅拷贝

### 4.1 浅拷贝

- 只复制对象本身和基本数据类型字段
- 引用类型字段只复制引用，不复制对象

### 4.2 深拷贝

- 复制对象本身和所有引用对象
- 实现方式：
  1. 重写clone()方法，对引用对象也进行克隆
  2. 使用序列化实现

***

## 五、相关文档

- [设计模式概述](./01-prototype-overview.md)
