# 访问者模式 - 代码指南

> 本文档详细说明访问者模式的代码实现和使用方法

***

## 一、项目结构

```
visitor/
├── Visitor.java         # 访问者接口
├── ConcreteVisitor.java # 具体访问者
├── Element.java         # 元素接口
├── ConcreteElementA.java # 具体元素A
├── ConcreteElementB.java # 具体元素B
└── ObjectStructure.java # 对象结构
```

***

## 二、代码详解

### 2.1 Visitor - 访问者接口

```java
package com.linsir.designpattern.visitor;

public interface Visitor {
    void visit(ConcreteElementA element);
    void visit(ConcreteElementB element);
}
```

---

### 2.2 Element - 元素接口

```java
package com.linsir.designpattern.visitor;

public interface Element {
    void accept(Visitor visitor);
}
```

---

### 2.3 ConcreteElementA - 具体元素A

```java
package com.linsir.designpattern.visitor;

public class ConcreteElementA implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void operationA() {
        System.out.println("元素A的操作");
    }
}
```

---

### 2.4 ConcreteElementB - 具体元素B

```java
package com.linsir.designpattern.visitor;

public class ConcreteElementB implements Element {
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void operationB() {
        System.out.println("元素B的操作");
    }
}
```

---

### 2.5 ConcreteVisitor - 具体访问者

```java
package com.linsir.designpattern.visitor;

public class ConcreteVisitor implements Visitor {
    @Override
    public void visit(ConcreteElementA element) {
        System.out.println("访问者访问元素A");
        element.operationA();
    }

    @Override
    public void visit(ConcreteElementB element) {
        System.out.println("访问者访问元素B");
        element.operationB();
    }
}
```

---

### 2.6 ObjectStructure - 对象结构

```java
package com.linsir.designpattern.visitor;

import java.util.ArrayList;
import java.util.List;

public class ObjectStructure {
    private List<Element> elements = new ArrayList<>();

    public void add(Element element) {
        elements.add(element);
    }

    public void remove(Element element) {
        elements.remove(element);
    }

    public void accept(Visitor visitor) {
        for (Element element : elements) {
            element.accept(visitor);
        }
    }
}
```

***

## 三、使用示例

```java
public class VisitorTest {
    public static void main(String[] args) {
        ObjectStructure structure = new ObjectStructure();
        structure.add(new ConcreteElementA());
        structure.add(new ConcreteElementB());

        Visitor visitor = new ConcreteVisitor();
        structure.accept(visitor);
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-visitor-overview.md)
