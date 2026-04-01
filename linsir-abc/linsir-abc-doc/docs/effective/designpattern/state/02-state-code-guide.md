# 状态模式 - 代码指南

> 本文档详细说明状态模式的代码实现和使用方法

***

## 一、项目结构

```
state/
├── State.java           # 状态接口
├── ConcreteStateA.java  # 具体状态A
├── ConcreteStateB.java  # 具体状态B
└── Context.java         # 上下文
```

***

## 二、代码详解

### 2.1 State - 状态接口

```java
package com.linsir.designpattern.state;

public interface State {
    void handle(Context context);
}
```

---

### 2.2 ConcreteStateA - 具体状态A

```java
package com.linsir.designpattern.state;

public class ConcreteStateA implements State {
    @Override
    public void handle(Context context) {
        System.out.println("当前是状态A");
        context.setState(new ConcreteStateB());
    }
}
```

---

### 2.3 ConcreteStateB - 具体状态B

```java
package com.linsir.designpattern.state;

public class ConcreteStateB implements State {
    @Override
    public void handle(Context context) {
        System.out.println("当前是状态B");
        context.setState(new ConcreteStateA());
    }
}
```

---

### 2.4 Context - 上下文

```java
package com.linsir.designpattern.state;

public class Context {
    private State state;

    public Context(State state) {
        this.state = state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void request() {
        state.handle(this);
    }
}
```

***

## 三、使用示例

```java
public class StateTest {
    public static void main(String[] args) {
        Context context = new Context(new ConcreteStateA());

        context.request();  // 状态A -> 状态B
        context.request();  // 状态B -> 状态A
        context.request();  // 状态A -> 状态B
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-state-overview.md)
