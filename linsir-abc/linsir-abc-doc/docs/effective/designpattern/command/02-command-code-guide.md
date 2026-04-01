# 命令模式 - 代码指南

> 本文档详细说明命令模式的代码实现和使用方法

***

## 一、项目结构

```
command/
├── Command.java         # 命令接口
├── ConcreteCommand.java # 具体命令
├── Receiver.java        # 接收者
├── Invoker.java         # 调用者
└── Client.java          # 客户端
```

***

## 二、代码详解

### 2.1 Command - 命令接口

```java
package com.linsir.designpattern.command;

public interface Command {
    void execute();
    void undo();
}
```

---

### 2.2 Receiver - 接收者

```java
package com.linsir.designpattern.command;

public class Receiver {
    public void action() {
        System.out.println("接收者执行操作");
    }

    public void undoAction() {
        System.out.println("接收者撤销操作");
    }
}
```

---

### 2.3 ConcreteCommand - 具体命令

```java
package com.linsir.designpattern.command;

public class ConcreteCommand implements Command {
    private Receiver receiver;

    public ConcreteCommand(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.action();
    }

    @Override
    public void undo() {
        receiver.undoAction();
    }
}
```

---

### 2.4 Invoker - 调用者

```java
package com.linsir.designpattern.command;

public class Invoker {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        command.execute();
    }

    public void undoCommand() {
        command.undo();
    }
}
```

***

## 三、使用示例

```java
public class CommandTest {
    public static void main(String[] args) {
        // 创建接收者
        Receiver receiver = new Receiver();

        // 创建命令
        Command command = new ConcreteCommand(receiver);

        // 创建调用者并设置命令
        Invoker invoker = new Invoker();
        invoker.setCommand(command);

        // 执行命令
        invoker.executeCommand();

        // 撤销命令
        invoker.undoCommand();
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-command-overview.md)
