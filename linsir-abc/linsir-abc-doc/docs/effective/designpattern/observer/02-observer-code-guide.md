# 观察者模式 - 代码指南

> 本文档详细说明观察者模式的代码实现和使用方法

***

## 一、项目结构

```
observer/
├── Subject.java              # 主题接口
├── Observer.java             # 观察者接口
├── ConcreteSubject.java      # 具体主题
└── ConcreteObserver.java     # 具体观察者
```

***

## 二、代码详解

### 2.1 Subject - 主题接口

```java
package com.linsir.designpattern.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    protected List<Observer> observers = new ArrayList<>();

    // 注册观察者
    public void attach(Observer observer) {
        observers.add(observer);
    }

    // 移除观察者
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    // 通知所有观察者
    public abstract void notifyObservers();
}
```

---

### 2.2 Observer - 观察者接口

```java
package com.linsir.designpattern.observer;

public interface Observer {
    void update(String message);
}
```

---

### 2.3 ConcreteSubject - 具体主题

```java
package com.linsir.designpattern.observer;

public class ConcreteSubject extends Subject {
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(state);
        }
    }
}
```

---

### 2.4 ConcreteObserver - 具体观察者

```java
package com.linsir.designpattern.observer;

public class ConcreteObserver implements Observer {
    private String name;
    private String state;

    public ConcreteObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        this.state = message;
        System.out.println(name + " 收到消息: " + message);
    }
}
```

***

## 三、使用示例

```java
public class ObserverTest {
    public static void main(String[] args) {
        // 创建主题
        ConcreteSubject subject = new ConcreteSubject();

        // 创建观察者
        Observer observer1 = new ConcreteObserver("观察者1");
        Observer observer2 = new ConcreteObserver("观察者2");
        Observer observer3 = new ConcreteObserver("观察者3");

        // 注册观察者
        subject.attach(observer1);
        subject.attach(observer2);
        subject.attach(observer3);

        // 改变主题状态，自动通知所有观察者
        subject.setState("主题状态发生变化");
    }
}
```

**输出**：
```
观察者1 收到消息: 主题状态发生变化
观察者2 收到消息: 主题状态发生变化
观察者3 收到消息: 主题状态发生变化
```

***

## 四、Java内置观察者模式

Java提供了内置的观察者模式支持：

```java
import java.util.Observable;
import java.util.Observer;

// 被观察者
public class NewsObservable extends Observable {
    public void publishNews(String news) {
        setChanged();  // 标记状态已改变
        notifyObservers(news);  // 通知观察者
    }
}

// 观察者
public class NewsObserver implements Observer {
    private String name;

    public NewsObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println(name + " 收到新闻: " + arg);
    }
}
```

***

## 五、相关文档

- [设计模式概述](./01-observer-overview.md)
