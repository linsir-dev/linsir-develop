# 解释器模式 - 代码指南

> 本文档详细说明解释器模式的代码实现和使用方法

***

## 一、项目结构

```
interpreter/
├── Expression.java       # 抽象表达式
├── TerminalExpression.java # 终结符表达式
├── NonterminalExpression.java # 非终结符表达式
└── Context.java          # 上下文
```

***

## 二、代码详解

### 2.1 Expression - 抽象表达式

```java
package com.linsir.designpattern.interpreter;

public interface Expression {
    boolean interpret(String context);
}
```

---

### 2.2 TerminalExpression - 终结符表达式

```java
package com.linsir.designpattern.interpreter;

public class TerminalExpression implements Expression {
    private String data;

    public TerminalExpression(String data) {
        this.data = data;
    }

    @Override
    public boolean interpret(String context) {
        return context.contains(data);
    }
}
```

---

### 2.3 OrExpression - 或表达式

```java
package com.linsir.designpattern.interpreter;

public class OrExpression implements Expression {
    private Expression expr1;
    private Expression expr2;

    public OrExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public boolean interpret(String context) {
        return expr1.interpret(context) || expr2.interpret(context);
    }
}
```

---

### 2.4 AndExpression - 与表达式

```java
package com.linsir.designpattern.interpreter;

public class AndExpression implements Expression {
    private Expression expr1;
    private Expression expr2;

    public AndExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public boolean interpret(String context) {
        return expr1.interpret(context) && expr2.interpret(context);
    }
}
```

***

## 三、使用示例

```java
public class InterpreterTest {
    public static void main(String[] args) {
        Expression robert = new TerminalExpression("Robert");
        Expression john = new TerminalExpression("John");

        // Robert 或 John
        Expression orExpression = new OrExpression(robert, john);
        System.out.println("John is male? " + orExpression.interpret("John"));

        // Robert 和 John
        Expression andExpression = new AndExpression(robert, john);
        System.out.println("John and Robert? " + andExpression.interpret("John Robert"));
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-interpreter-overview.md)
