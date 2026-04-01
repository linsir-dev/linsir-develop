# 迭代器模式 - 代码指南

> 本文档详细说明迭代器模式的代码实现和使用方法

***

## 一、项目结构

```
iterator/
├── Iterator.java        # 迭代器接口
├── Aggregate.java       # 聚合接口
├── ConcreteIterator.java # 具体迭代器
└── ConcreteAggregate.java # 具体聚合
```

***

## 二、代码详解

### 2.1 Iterator - 迭代器接口

```java
package com.linsir.designpattern.iterator;

public interface Iterator {
    boolean hasNext();
    Object next();
}
```

---

### 2.2 Aggregate - 聚合接口

```java
package com.linsir.designpattern.iterator;

public interface Aggregate {
    Iterator createIterator();
}
```

---

### 2.3 ConcreteIterator - 具体迭代器

```java
package com.linsir.designpattern.iterator;

import java.util.List;

public class ConcreteIterator implements Iterator {
    private List<Object> list;
    private int index = 0;

    public ConcreteIterator(List<Object> list) {
        this.list = list;
    }

    @Override
    public boolean hasNext() {
        return index < list.size();
    }

    @Override
    public Object next() {
        return list.get(index++);
    }
}
```

---

### 2.4 ConcreteAggregate - 具体聚合

```java
package com.linsir.designpattern.iterator;

import java.util.ArrayList;
import java.util.List;

public class ConcreteAggregate implements Aggregate {
    private List<Object> list = new ArrayList<>();

    public void add(Object obj) {
        list.add(obj);
    }

    public void remove(Object obj) {
        list.remove(obj);
    }

    @Override
    public Iterator createIterator() {
        return new ConcreteIterator(list);
    }
}
```

***

## 三、使用示例

```java
public class IteratorTest {
    public static void main(String[] args) {
        ConcreteAggregate aggregate = new ConcreteAggregate();
        aggregate.add("元素1");
        aggregate.add("元素2");
        aggregate.add("元素3");

        Iterator iterator = aggregate.createIterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
```

***

## 四、Java内置迭代器

Java提供了内置的迭代器支持：

```java
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JavaIteratorTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");

        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
```

***

## 五、相关文档

- [设计模式概述](./01-iterator-overview.md)
