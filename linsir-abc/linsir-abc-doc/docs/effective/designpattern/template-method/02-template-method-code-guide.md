# 模板方法模式 - 代码指南

> 本文档详细说明模板方法模式的代码实现和使用方法

***

## 一、项目结构

```
templateMethod/
├── AbstractClass.java    # 抽象类
├── ConcreteClassA.java   # 具体类A
└── ConcreteClassB.java   # 具体类B
```

***

## 二、代码详解

### 2.1 AbstractClass - 抽象类

```java
package com.linsir.designpattern.templateMethod;

public abstract class AbstractClass {
    // 模板方法
    public final void templateMethod() {
        primitiveOperation1();
        primitiveOperation2();
        concreteOperation();
        hook();
    }

    // 抽象操作1
    protected abstract void primitiveOperation1();

    // 抽象操作2
    protected abstract void primitiveOperation2();

    // 具体操作
    private void concreteOperation() {
        System.out.println("具体操作");
    }

    // 钩子方法
    protected void hook() {
        // 默认实现为空
    }
}
```

---

### 2.2 ConcreteClassA - 具体类A

```java
package com.linsir.designpattern.templateMethod;

public class ConcreteClassA extends AbstractClass {
    @Override
    protected void primitiveOperation1() {
        System.out.println("具体类A的操作1");
    }

    @Override
    protected void primitiveOperation2() {
        System.out.println("具体类A的操作2");
    }
}
```

---

### 2.3 ConcreteClassB - 具体类B

```java
package com.linsir.designpattern.templateMethod;

public class ConcreteClassB extends AbstractClass {
    @Override
    protected void primitiveOperation1() {
        System.out.println("具体类B的操作1");
    }

    @Override
    protected void primitiveOperation2() {
        System.out.println("具体类B的操作2");
    }

    @Override
    protected void hook() {
        System.out.println("具体类B的钩子方法");
    }
}
```

***

## 三、使用示例

```java
public class TemplateMethodTest {
    public static void main(String[] args) {
        AbstractClass classA = new ConcreteClassA();
        classA.templateMethod();

        System.out.println("---");

        AbstractClass classB = new ConcreteClassB();
        classB.templateMethod();
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-template-method-overview.md)
