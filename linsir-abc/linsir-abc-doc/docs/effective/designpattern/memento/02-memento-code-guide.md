# 备忘录模式 - 代码指南

> 本文档详细说明备忘录模式的代码实现和使用方法

***

## 一、项目结构

```
memento/
├── Memento.java         # 备忘录
├── Originator.java      # 原发器
├── Caretaker.java       # 负责人
└── Client.java          # 客户端
```

***

## 二、代码详解

### 2.1 Memento - 备忘录

```java
package com.linsir.designpattern.memento;

public class Memento {
    private String state;

    public Memento(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
```

---

### 2.2 Originator - 原发器

```java
package com.linsir.designpattern.memento;

public class Originator {
    private String state;

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public Memento createMemento() {
        return new Memento(state);
    }

    public void restoreMemento(Memento memento) {
        this.state = memento.getState();
    }
}
```

---

### 2.3 Caretaker - 负责人

```java
package com.linsir.designpattern.memento;

import java.util.ArrayList;
import java.util.List;

public class Caretaker {
    private List<Memento> mementoList = new ArrayList<>();

    public void add(Memento memento) {
        mementoList.add(memento);
    }

    public Memento get(int index) {
        return mementoList.get(index);
    }
}
```

***

## 三、使用示例

```java
public class MementoTest {
    public static void main(String[] args) {
        Originator originator = new Originator();
        Caretaker caretaker = new Caretaker();

        // 设置状态并保存
        originator.setState("状态1");
        caretaker.add(originator.createMemento());

        originator.setState("状态2");
        caretaker.add(originator.createMemento());

        originator.setState("状态3");
        System.out.println("当前状态: " + originator.getState());

        // 恢复到状态1
        originator.restoreMemento(caretaker.get(0));
        System.out.println("恢复后的状态: " + originator.getState());
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-memento-overview.md)
