# 中介者模式 - 代码指南

> 本文档详细说明中介者模式的代码实现和使用方法

***

## 一、项目结构

```
mediator/
├── Mediator.java        # 中介者接口
├── ConcreteMediator.java # 具体中介者
├── Colleague.java       # 同事类
├── ColleagueA.java      # 具体同事A
└── ColleagueB.java      # 具体同事B
```

***

## 二、代码详解

### 2.1 Mediator - 中介者接口

```java
package com.linsir.designpattern.mediator;

public interface Mediator {
    void send(String message, Colleague colleague);
}
```

---

### 2.2 Colleague - 同事类

```java
package com.linsir.designpattern.mediator;

public abstract class Colleague {
    protected Mediator mediator;

    public Colleague(Mediator mediator) {
        this.mediator = mediator;
    }

    public abstract void send(String message);
    public abstract void receive(String message);
}
```

---

### 2.3 ColleagueA - 具体同事A

```java
package com.linsir.designpattern.mediator;

public class ColleagueA extends Colleague {
    public ColleagueA(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void send(String message) {
        mediator.send(message, this);
    }

    @Override
    public void receive(String message) {
        System.out.println("同事A收到消息: " + message);
    }
}
```

---

### 2.4 ColleagueB - 具体同事B

```java
package com.linsir.designpattern.mediator;

public class ColleagueB extends Colleague {
    public ColleagueB(Mediator mediator) {
        super(mediator);
    }

    @Override
    public void send(String message) {
        mediator.send(message, this);
    }

    @Override
    public void receive(String message) {
        System.out.println("同事B收到消息: " + message);
    }
}
```

---

### 2.5 ConcreteMediator - 具体中介者

```java
package com.linsir.designpattern.mediator;

public class ConcreteMediator implements Mediator {
    private ColleagueA colleagueA;
    private ColleagueB colleagueB;

    public void setColleagueA(ColleagueA colleagueA) {
        this.colleagueA = colleagueA;
    }

    public void setColleagueB(ColleagueB colleagueB) {
        this.colleagueB = colleagueB;
    }

    @Override
    public void send(String message, Colleague colleague) {
        if (colleague == colleagueA) {
            colleagueB.receive(message);
        } else {
            colleagueA.receive(message);
        }
    }
}
```

***

## 三、使用示例

```java
public class MediatorTest {
    public static void main(String[] args) {
        ConcreteMediator mediator = new ConcreteMediator();

        ColleagueA colleagueA = new ColleagueA(mediator);
        ColleagueB colleagueB = new ColleagueB(mediator);

        mediator.setColleagueA(colleagueA);
        mediator.setColleagueB(colleagueB);

        colleagueA.send("来自A的消息");
        colleagueB.send("来自B的消息");
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-mediator-overview.md)
