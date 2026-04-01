# 享元模式 - 代码指南

> 本文档详细说明享元模式的代码实现和使用方法

***

## 一、项目结构

```
flyweight/
├── Flyweight.java           # 享元接口
├── ConcreteFlyweight.java   # 具体享元
├── FlyweightFactory.java    # 享元工厂
└── Client.java              # 客户端
```

***

## 二、代码详解

### 2.1 Flyweight - 享元接口

```java
package com.linsir.designpattern.flyweight;

public interface Flyweight {
    void operation(String extrinsicState);
}
```

---

### 2.2 ConcreteFlyweight - 具体享元

```java
package com.linsir.designpattern.flyweight;

public class ConcreteFlyweight implements Flyweight {
    private String intrinsicState;  // 内部状态

    public ConcreteFlyweight(String intrinsicState) {
        this.intrinsicState = intrinsicState;
    }

    @Override
    public void operation(String extrinsicState) {
        System.out.println("内部状态: " + intrinsicState + ", 外部状态: " + extrinsicState);
    }
}
```

---

### 2.3 FlyweightFactory - 享元工厂

```java
package com.linsir.designpattern.flyweight;

import java.util.HashMap;
import java.util.Map;

public class FlyweightFactory {
    private Map<String, Flyweight> flyweights = new HashMap<>();

    public Flyweight getFlyweight(String key) {
        if (!flyweights.containsKey(key)) {
            flyweights.put(key, new ConcreteFlyweight(key));
        }
        return flyweights.get(key);
    }
}
```

***

## 三、使用示例

```java
public class FlyweightTest {
    public static void main(String[] args) {
        FlyweightFactory factory = new FlyweightFactory();

        // 获取享元对象
        Flyweight flyweight1 = factory.getFlyweight("A");
        Flyweight flyweight2 = factory.getFlyweight("A");
        Flyweight flyweight3 = factory.getFlyweight("B");

        // 使用享元对象
        flyweight1.operation("外部状态1");
        flyweight2.operation("外部状态2");
        flyweight3.operation("外部状态3");

        // 验证是否是同一个对象
        System.out.println(flyweight1 == flyweight2);  // true
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-flyweight-overview.md)
