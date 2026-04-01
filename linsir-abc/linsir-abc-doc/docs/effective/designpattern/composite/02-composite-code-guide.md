# 组合模式 - 代码指南

> 本文档详细说明组合模式的代码实现和使用方法

***

## 一、项目结构

```
composite/
├── Component.java       # 组件接口
├── Leaf.java            # 叶子节点
├── Composite.java       # 组合节点
└── Client.java          # 客户端
```

***

## 二、代码详解

### 2.1 Component - 组件接口

```java
package com.linsir.designpattern.composite;

public interface Component {
    void operation();
    void add(Component component);
    void remove(Component component);
    Component getChild(int index);
}
```

---

### 2.2 Leaf - 叶子节点

```java
package com.linsir.designpattern.composite;

public class Leaf implements Component {
    private String name;

    public Leaf(String name) {
        this.name = name;
    }

    @Override
    public void operation() {
        System.out.println("叶子节点 " + name + " 的操作");
    }

    @Override
    public void add(Component component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Component component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component getChild(int index) {
        throw new UnsupportedOperationException();
    }
}
```

---

### 2.3 Composite - 组合节点

```java
package com.linsir.designpattern.composite;

import java.util.ArrayList;
import java.util.List;

public class Composite implements Component {
    private String name;
    private List<Component> children = new ArrayList<>();

    public Composite(String name) {
        this.name = name;
    }

    @Override
    public void operation() {
        System.out.println("组合节点 " + name + " 的操作");
        for (Component child : children) {
            child.operation();
        }
    }

    @Override
    public void add(Component component) {
        children.add(component);
    }

    @Override
    public void remove(Component component) {
        children.remove(component);
    }

    @Override
    public Component getChild(int index) {
        return children.get(index);
    }
}
```

***

## 三、使用示例

```java
public class CompositeTest {
    public static void main(String[] args) {
        // 创建根节点
        Component root = new Composite("根节点");

        // 创建分支节点
        Component branch1 = new Composite("分支1");
        Component branch2 = new Composite("分支2");

        // 创建叶子节点
        Component leaf1 = new Leaf("叶子1");
        Component leaf2 = new Leaf("叶子2");
        Component leaf3 = new Leaf("叶子3");

        // 组装树形结构
        root.add(branch1);
        root.add(branch2);
        branch1.add(leaf1);
        branch1.add(leaf2);
        branch2.add(leaf3);

        // 统一操作
        root.operation();
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-composite-overview.md)
