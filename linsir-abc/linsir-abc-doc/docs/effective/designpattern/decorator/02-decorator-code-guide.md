# 装饰器模式 - 代码指南

> 本文档详细说明装饰器模式的代码实现和使用方法

***

## 一、项目结构

```
decorator/
├── IBread.java              # 抽象组件接口
├── NormalBread.java         # 具体组件
├── Decorator.java           # 抽象装饰类
└── CornDecorator.java       # 具体装饰类
```

***

## 二、代码详解

### 2.1 IBread - 抽象组件接口

```java
package com.linsir.designpattern.decorator;

public interface IBread {
    void paint();
}
```

---

### 2.2 NormalBread - 具体组件

```java
package com.linsir.designpattern.decorator;

public class NormalBread implements IBread {
    @Override
    public void paint() {
        System.out.println("普通面包");
    }
}
```

---

### 2.3 Decorator - 抽象装饰类

```java
package com.linsir.designpattern.decorator;

public abstract class Decorator implements IBread {
    protected IBread bread;

    public Decorator(IBread bread) {
        this.bread = bread;
    }

    @Override
    public void paint() {
        bread.paint();
    }
}
```

---

### 2.4 CornDecorator - 具体装饰类

```java
package com.linsir.designpattern.decorator;

public class CornDecorator extends Decorator {
    public CornDecorator(IBread bread) {
        super(bread);
    }

    @Override
    public void paint() {
        super.paint();
        System.out.println("添加玉米");
    }
}
```

***

## 三、使用示例

```java
public class DecoratorTest {
    public static void main(String[] args) {
        // 创建普通面包
        IBread normalBread = new NormalBread();
        normalBread.paint();
        
        System.out.println("---");
        
        // 创建玉米面包（装饰普通面包）
        IBread cornBread = new CornDecorator(normalBread);
        cornBread.paint();
    }
}
```

**输出**：
```
普通面包
---
普通面包
添加玉米
```

***

## 四、相关文档

- [设计模式概述](./01-decorator-overview.md)
