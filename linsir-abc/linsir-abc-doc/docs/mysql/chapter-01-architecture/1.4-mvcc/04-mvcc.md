# 1.4 多版本并发控制（MVCC）

> 本文档基于《高性能MySQL》（第3版）第1章1.4节内容整理总结

多版本并发控制（Multi-Version Concurrency Control，MVCC）是MySQL InnoDB存储引擎实现高并发、高性能事务处理的核心机制。它通过保存数据在某个时间点的快照来实现并发控制，使得读操作不会阻塞写操作，写操作也不会阻塞读操作，从而大大提高了数据库的并发性能。

***

## 什么是MVCC

MVCC的本质是**"多版本数据隔离"**——InnoDB为表中的每一行数据维护多个历史版本，每个版本都关联一个事务ID。当事务读取数据时，不会直接读取最新版本，而是根据自身事务的隔离级别，读取符合条件的"历史版本"，从而避免与写操作产生锁竞争。

### 核心特点

```mermaid
mindmap
  root((MVCC核心特点))
    无锁读
      普通读操作无需加锁
      不阻塞写操作
    多版本
      每行数据存在多个历史版本
      存储于Undo Log中
    隔离性
      通过Read View控制可见性
      满足不同隔离级别需求
```

| 特性 | 说明 |
|------|------|
| **无锁读** | 普通读操作（快照读）无需加锁，不阻塞写操作 |
| **多版本** | 每行数据存在多个历史版本，存储于Undo Log中 |
| **隔离性** | 通过Read View控制不同事务对数据版本的可见性 |

### 为什么需要MVCC

在没有MVCC的情况下，MySQL只能通过"加锁"来解决读写冲突：

```mermaid
flowchart TD
    A[传统锁机制的问题] --> B[共享锁 S锁]
    A --> C[排他锁 X锁]
    
    B --> B1[读操作加S锁]
    B --> B2[写操作需等待S锁释放]
    
    C --> C1[写操作加X锁]
    C --> C2[读操作需等待X锁释放]
    
    B2 --> D[高并发下性能差]
    C2 --> D
```

**MVCC的解决方案：**
- 读操作（快照读）读取历史版本，无需加锁
- 写操作修改数据时，生成新的版本
- 不影响旧版本的读取，实现"读写并行"

***

## MVCC的核心组件

MVCC的实现依赖InnoDB的三个核心组件，三者协同工作，完成"版本生成→版本串联→版本筛选"的全流程。

### 1. 隐藏字段（版本的基础）

InnoDB会为表中的每一行数据自动添加三个隐藏字段：

```mermaid
flowchart LR
    A[行数据结构] --> B[业务字段]
    A --> C[隐藏字段]
    
    C --> D[DB_TRX_ID<br/>6字节]
    C --> E[DB_ROLL_PTR<br/>7字节]
    C --> F[DB_ROW_ID<br/>6字节]
    
    D --> D1[最后修改事务ID]
    E --> E1[回滚指针]
    F --> F1[隐含自增行ID]
```

| 隐藏字段 | 长度 | 含义 | 核心作用 |
|----------|------|------|----------|
| **DB_TRX_ID** | 6字节 | 最近修改该行数据的事务ID | 标识该版本由哪个事务生成 |
| **DB_ROLL_PTR** | 7字节 | 回滚指针，指向Undo Log中该数据的上一个版本 | 串联多个历史版本，形成版本链 |
| **DB_ROW_ID** | 6字节 | 隐含自增行ID（表无主键时自动生成） | 用于唯一标识行 |

### 2. Undo Log（版本的存储载体）

Undo Log是InnoDB用于存储数据历史版本的日志文件，当事务对数据进行修改时，InnoDB会先将修改前的旧版本数据写入Undo Log。

**Undo Log的两种类型：**

```mermaid
flowchart TD
    A[Undo Log类型] --> B[Update Undo Log]
    A --> C[Insert Undo Log]
    
    B --> B1[用于UPDATE/DELETE]
    B --> B2[支持MVCC和事务回滚]
    B --> B3[事务提交后不立即删除]
    
    C --> C1[用于INSERT操作]
    C --> C2[仅用于事务回滚]
    C --> C3[事务提交后可删除]
```

**版本链的形成：**

```mermaid
flowchart LR
    A[当前行数据<br/>DB_TRX_ID=103<br/>name=Charlie] -->|DB_ROLL_PTR| B[Undo Log版本2<br/>DB_TRX_ID=102<br/>name=Bob]
    B -->|DB_ROLL_PTR| C[Undo Log版本1<br/>DB_TRX_ID=101<br/>name=Alice]
    C -->|DB_ROLL_PTR| D[NULL<br/>初始版本]
    
    style A fill:#9f9,stroke:#333
    style B fill:#bfb,stroke:#333
    style C fill:#bbf,stroke:#333
```

### 3. Read View（版本的筛选规则）

Read View是事务执行快照读时生成的一个"一致性视图"，它定义了当前事务能看到哪些版本的数据。

**Read View包含4个核心属性：**

| 属性 | 说明 |
|------|------|
| **trx_ids** | 生成Read View时，当前系统中所有活跃（未提交）的事务ID列表 |
| **min_trx_id** | trx_ids中的最小事务ID（当前最老的活跃事务） |
| **max_trx_id** | 下一个将要分配的事务ID（当前最大事务ID+1） |
| **creator_trx_id** | 生成该Read View的当前事务ID |

**可见性判断算法：**

```mermaid
flowchart TD
    A[获取记录的trx_id] --> B{trx_id ==<br/>creator_trx_id?}
    B -->|是| C[可见<br/>自己的修改]
    B -->|否| D{trx_id <<br/>min_trx_id?}
    D -->|是| E[可见<br/>已提交的历史事务]
    D -->|否| F{trx_id >=<br/>max_trx_id?}
    F -->|是| G[不可见<br/>未来事务]
    F -->|否| H{trx_id在<br/>活跃列表中?}
    H -->|是| I[不可见<br/>未提交事务]
    H -->|否| J[可见<br/>已提交事务]
    
    style C fill:#c8e6c9,stroke:#333
    style E fill:#c8e6c9,stroke:#333
    style J fill:#c8e6c9,stroke:#333
    style G fill:#ffcdd2,stroke:#333
    style I fill:#ffcdd2,stroke:#333
```

***

## MVCC的工作原理

### 快照读（Snapshot Read）

快照读是MVCC实现无锁读的核心机制，普通的SELECT语句就是快照读。

**快照读流程：**

```mermaid
sequenceDiagram
    participant T as 事务
    participant RV as Read View
    participant Row as 数据行
    participant UL as Undo Log
    
    T->>RV: 1. 创建/获取Read View
    T->>Row: 2. 获取记录最新版本
    Row->>T: 返回版本信息<br/>trx_id + 数据
    
    alt 版本可见
        T->>T: 3a. 返回该版本数据
    else 版本不可见
        T->>UL: 3b. 通过DB_ROLL_PTR<br/>找到上一版本
        UL->>T: 返回历史版本
        T->>T: 重复可见性判断
    end
```

### 当前读（Current Read）

当前读读取的是最新版本的数据，需要加锁。以下操作属于当前读：
- SELECT ... FOR UPDATE
- SELECT ... LOCK IN SHARE MODE
- INSERT、UPDATE、DELETE

```mermaid
flowchart LR
    A[当前读] --> B[读取最新版本]
    A --> C[需要加锁]
    
    B --> D[SELECT ... FOR UPDATE<br/>排他锁]
    B --> E[SELECT ... LOCK IN SHARE MODE<br/>共享锁]
    
    C --> F[INSERT/UPDATE/DELETE<br/>自动加排他锁]
```

### MVCC与事务隔离级别

不同隔离级别下，Read View的生成时机不同：

```mermaid
flowchart TD
    A[事务隔离级别] --> B[READ COMMITTED]
    A --> C[REPEATABLE READ]
    
    B --> B1[每次SELECT生成新Read View]
    B --> B2[可看到其他事务新提交的变更]
    
    C --> C1[事务开始时生成Read View]
    C --> C2[整个事务使用同一个Read View]
    C --> C3[保证可重复读]
```

| 隔离级别 | Read View生成时机 | 效果 |
|----------|-------------------|------|
| **READ COMMITTED** | 每次SELECT时生成 | 可看到其他事务新提交的变更 |
| **REPEATABLE READ** | 事务开始时生成 | 整个事务看到的数据保持一致 |

### MVCC解决并发问题

```mermaid
flowchart TD
    A[并发问题] --> B[脏读]
    A --> C[不可重复读]
    A --> D[幻读]
    
    B --> B1[MVCC解决<br/>不读取未提交版本]
    C --> C1[RR级别解决<br/>同一Read View]
    D --> D1[快照读部分解决<br/>当前读需Gap Lock]
```

| 并发问题 | MVCC是否解决 | 说明 |
|----------|--------------|------|
| **脏读** | ✅ 解决 | 不读取未提交事务的版本 |
| **不可重复读** | ✅ RR级别解决 | 同一事务使用同一个Read View |
| **幻读** | ⚠️ 部分解决 | 快照读解决，当前读需要Gap Lock配合 |

***

## MVCC的实际应用

### 场景1：读写并发

```mermaid
sequenceDiagram
    participant T1 as 事务A<br/>写操作
    participant DB as 数据库
    participant T2 as 事务B<br/>读操作
    
    T2->>DB: 1. 开始事务<br/>创建Read View
    T1->>DB: 2. UPDATE数据<br/>生成新版本
    T1->>DB: 3. 未提交
    T2->>DB: 4. SELECT读取<br/>通过Read View判断
    DB->>T2: 5. 返回旧版本数据<br/>事务A的修改不可见
    T1->>DB: 6. COMMIT提交
    T2->>DB: 7. 再次SELECT<br/>RR级别：仍返回旧版本
```

### 场景2：可重复读保证

```mermaid
sequenceDiagram
    participant T1 as 事务A
    participant DB as 数据库
    participant T2 as 事务B
    
    T1->>DB: 1. BEGIN<br/>Read View: max_trx_id=100
    T1->>DB: 2. SELECT<br/>读取版本链
    DB->>T1: 返回数据=100
    
    T2->>DB: 3. BEGIN<br/>trx_id=101
    T2->>DB: 4. UPDATE<br/>修改数据为200
    T2->>DB: 5. COMMIT
    
    T1->>DB: 6. 再次SELECT<br/>使用相同Read View
    DB->>T1: 返回数据=100<br/>保证可重复读
```

### 场景3：版本链遍历

```mermaid
flowchart LR
    A[事务查询] --> B[获取最新版本<br/>trx_id=105]
    B --> C{105在<br/>活跃列表?}
    C -->|是| D[不可见<br/>找上一版本]
    D --> E[Undo Log<br/>trx_id=102]
    E --> F{102 <br/>min_trx_id?}
    F -->|是| G[可见<br/>返回该版本]
    F -->|否| H[继续遍历...]
```

***

## MVCC的优势与局限性

### 优势

```mermaid
flowchart TD
    A[MVCC优势] --> B[高并发性能]
    A --> C[读写不阻塞]
    A --> D[简化事务设计]
    
    B --> B1[读操作无需加锁<br/>减少锁竞争]
    C --> C1[读不阻塞写<br/>写不阻塞读]
    D --> D1[减少显式锁使用<br/>降低死锁概率]
```

| 优势 | 说明 |
|------|------|
| **高并发性能** | 读操作无需加锁，减少锁竞争 |
| **读写不阻塞** | 读不阻塞写，写不阻塞读 |
| **简化事务设计** | 减少显式锁使用，降低死锁概率 |

### 局限性

```mermaid
flowchart TD
    A[MVCC局限性] --> B[存储开销]
    A --> C[长事务问题]
    A --> D[不解决写写冲突]
    
    B --> B1[Undo Log持续增长<br/>占用磁盘空间]
    C --> C1[阻止旧版本清理<br/>导致Undo Log膨胀]
    D --> D1[写操作仍需加锁<br/>存在锁竞争]
```

| 局限性 | 说明 | 解决方案 |
|--------|------|----------|
| **存储开销** | Undo Log持续增长，占用磁盘空间 | 合理设置Purge线程参数 |
| **长事务问题** | 长事务阻止旧版本清理 | 避免超长事务，及时提交 |
| **不解决写写冲突** | 写操作仍需加锁 | 使用行锁或乐观锁 |

### 最佳实践

```mermaid
flowchart TD
    A[MVCC最佳实践] --> B[避免长事务]
    A --> C[合理选择隔离级别]
    A --> D[监控Undo Log]
    A --> E[优化查询]
    
    B --> B1[及时提交或回滚<br/>减少版本保留时间]
    C --> C1[RC vs RR<br/>根据业务需求选择]
    D --> D1[监控undo_log_consumed<br/>防止表空间膨胀]
    E --> E1[使用索引<br/>减少版本链遍历]
```

1. **避免长事务**：及时提交或回滚事务，减少版本保留时间
2. **合理选择隔离级别**：根据业务需求选择READ COMMITTED或REPEATABLE READ
3. **监控Undo Log**：关注`undo_log_consumed`指标，防止表空间膨胀
4. **优化查询**：使用索引减少版本链遍历深度

***

## 总结

### MVCC核心概念图

```mermaid
mindmap
  root((MVCC))
    核心组件
      隐藏字段
        DB_TRX_ID
        DB_ROLL_PTR
        DB_ROW_ID
      Undo Log
        Update Undo Log
        Insert Undo Log
      Read View
        trx_ids
        min_trx_id
        max_trx_id
        creator_trx_id
    读取类型
      快照读
        无锁读
        读取历史版本
      当前读
        需要加锁
        读取最新版本
    隔离级别
      READ COMMITTED
        每次SELECT新Read View
      REPEATABLE READ
        事务开始时Read View
    解决的问题
      脏读
      不可重复读
      幻读部分解决
```

### 关键要点

1. **MVCC本质**：通过保存数据历史版本+控制版本可见性，实现高效并发控制
2. **核心组件**：隐藏字段、Undo Log、Read View三者协同工作
3. **读写分离**：快照读无锁，当前读加锁
4. **隔离级别差异**：RC每次SELECT生成新Read View，RR事务开始时生成
5. **注意事项**：避免长事务，监控Undo Log，合理选择隔离级别

### 参考资源

- 《高性能MySQL》（第3版）第1章 1.4节
- MySQL 官方文档：[InnoDB Multi-Versioning](https://dev.mysql.com/doc/refman/8.0/en/innodb-multi-versioning.html)
- InnoDB 事务模型：[Consistent Nonlocking Reads](https://dev.mysql.com/doc/refman/8.0/en/innodb-consistent-read.html)
