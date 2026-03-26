# 流程图示例

本文档展示如何在 VitePress 中使用 Mermaid 流程图。

## 流程图示例

```mermaid
graph TD
    A[开始] --> B{判断}
    B -->|条件1| C[处理1]
    B -->|条件2| D[处理2]
    C --> E[结束]
    D --> E
```

## 时序图示例

```mermaid
sequenceDiagram
    participant 客户端
    participant 服务器
    participant 数据库

    客户端->>服务器: 发送请求
    服务器->>数据库: 查询数据
    数据库-->>服务器: 返回结果
    服务器-->>客户端: 返回响应
```

## 类图示例

```mermaid
classDiagram
    class ArrayList {
        +add(E e)
        +get(int index)
        +remove(int index)
    }
    class LinkedList {
        +addFirst(E e)
        +addLast(E e)
        +remove()
    }
    class List {
        <<interface>>
        +add(E e)
        +get(int index)
    }
    List <|-- ArrayList
    List <|-- LinkedList
```

## 甘特图示例

```mermaid
gantt
    title 项目进度
    dateFormat  YYYY-MM-DD
    section 设计阶段
    需求分析      :done, a1, 2024-01-01, 7d
    系统设计      :active, a2, after a1, 7d
    section 开发阶段
    编码实现      :a3, after a2, 14d
    单元测试      :a4, after a3, 7d
```

## 使用方法

在 Markdown 中使用以下语法：

````markdown
```mermaid
graph TD
    A[开始] --> B[结束]
```
````

支持的图表类型：
- `graph` - 流程图
- `sequenceDiagram` - 时序图
- `classDiagram` - 类图
- `gantt` - 甘特图
- `pie` - 饼图
- `flowchart` - 流程图（新版）
