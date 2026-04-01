# 1.3 事务

> 本文档基于《高性能MySQL》（第3版）第1章1.3节内容整理总结

事务是数据库管理系统执行过程中的一个逻辑单位，由一个有限的数据库操作序列构成。事务将多个操作捆绑在一起，确保这些操作要么全部成功执行，要么全部不执行，从而维护数据库的完整性。

***

## 1.3.1 隔离级别

SQL标准定义了四种事务隔离级别，每种级别都规定了事务中所做的修改在何时对其他事务可见，以及在事务内能够看到其他事务所做修改的程度。

### 四种隔离级别对比

| 隔离级别 | 脏读 | 不可重复读 | 幻读 | 加锁读 | 性能 |
|----------|------|------------|------|--------|------|
| READ UNCOMMITTED | ✅ 可能 | ✅ 可能 | ✅ 可能 | ❌ 否 | 最高 |
| READ COMMITTED | ❌ 否 | ✅ 可能 | ✅ 可能 | ❌ 否 | 较高 |
| REPEATABLE READ | ❌ 否 | ❌ 否 | ⚠️ 部分避免 | ❌ 否 | 中等 |
| SERIALIZABLE | ❌ 否 | ❌ 否 | ❌ 否 | ✅ 是 | 最低 |

> **注意：** MySQL InnoDB 的默认隔离级别是 **REPEATABLE READ**，通过 Next-Key Lock 机制在很大程度上避免了幻读问题。

### 1. READ UNCOMMITTED（读未提交）

**定义：** 最低的隔离级别，事务可以读取到其他事务尚未提交的数据。

**特点：**
- 事务对数据的修改即使未提交，也会立即被其他事务看到
- 会导致脏读、不可重复读和幻读所有问题
- 性能最高，但数据一致性最差

**脏读示例：**

```mermaid
sequenceDiagram
    participant A as 事务A
    participant DB as 数据库
    participant B as 事务B
    
    A->>DB: UPDATE balance=1500<br/>(未提交)
    B->>DB: SELECT balance<br/>读到1500
    A->>DB: ROLLBACK<br/>balance=1000
    Note over B: 事务B读到了脏数据1500<br/>实际应为1000
```

**适用场景：** 对数据一致性要求极低、主要用于统计分析且允许数据暂时不一致的场景。

### 2. READ COMMITTED（读已提交）

**定义：** 事务只能读取到其他事务已经提交的数据，避免了脏读。

**特点：**
- 每次 SELECT 都会创建一个新的数据快照
- 避免了脏读，但仍可能出现不可重复读和幻读
- Oracle、PostgreSQL 等数据库的默认隔离级别

**不可重复读示例：**

```mermaid
sequenceDiagram
    participant A as 事务A
    participant DB as 数据库
    participant B as 事务B
    
    A->>DB: SELECT balance=1000<br/>第一次读取
    B->>DB: UPDATE balance=1500
    B->>DB: COMMIT
    A->>DB: SELECT balance=1500<br/>第二次读取
    Note over A: 同一事务内两次读取<br/>结果不一致
```

**适用场景：** 大多数业务系统，适用于对数据实时性要求较高、能够接受同一事务内多次查询结果可能变化的场景。

### 3. REPEATABLE READ（可重复读）

**定义：** MySQL InnoDB 的默认隔离级别，保证在同一个事务中多次读取同一数据的结果一致。

**特点：**
- 事务开始时创建一致性视图（Read View），后续所有读取都基于该视图
- 避免了脏读和不可重复读
- 通过 Next-Key Lock 机制在很大程度上避免了幻读

**实现机制：**

```mermaid
flowchart TD
    Start([事务开始]) --> CreateView[创建Read View<br/>记录当前活跃事务ID列表]
    CreateView --> Select1[SELECT操作]
    Select1 --> CheckVersion{数据版本<br/>是否可见?}
    CheckVersion -->|可见| ReturnData[返回数据]
    CheckVersion -->|不可见| FindPrev[查找上一个版本<br/>通过Undo Log]
    FindPrev --> CheckVersion
    ReturnData --> OtherTx[其他事务<br/>修改并提交]
    OtherTx --> Select2[再次SELECT<br/>使用相同Read View]
    Select2 --> ReturnSame[返回相同数据<br/>实现可重复读]
```

**Next-Key Lock 机制：**

```mermaid
graph LR
    A[记录1] --> B[间隙锁]
    B --> C[记录2]
    C --> D[间隙锁]
    D --> E[记录3]
    
    style B fill:#f9f,stroke:#333
    style D fill:#f9f,stroke:#333
```

Next-Key Lock = 行锁 + 间隙锁，防止其他事务在锁定范围内插入新记录。

**适用场景：** 需要保证事务内多次读取相同数据结果一致的场景，如对账、报表生成等。

### 4. SERIALIZABLE（串行化）

**定义：** 最高的隔离级别，强制所有事务串行执行，完全避免并发问题。

**特点：**
- 所有的 SELECT 语句都会被隐式转换为 SELECT ... LOCK IN SHARE MODE
- 读取的数据会加共享锁，写操作会加排他锁
- 解决了脏读、不可重复读和幻读所有问题
- 并发性能最差，容易导致锁等待和死锁

**适用场景：** 对数据准确性要求极高、并发量不大、可以接受性能损失的场景，如银行核心系统的资金调拨。

***

## 1.3.2 死锁

### 什么是死锁

死锁是指两个或多个事务在同一资源上相互占用，并请求锁定对方占用的资源，从而导致恶性循环的现象。

### 死锁示例

```mermaid
sequenceDiagram
    participant A as 事务A
    participant R1 as 资源1
    participant R2 as 资源2
    participant B as 事务B
    
    A->>R1: 锁定资源1
    B->>R2: 锁定资源2
    A->>R2: 请求锁定资源2<br/>等待中...
    B->>R1: 请求锁定资源1<br/>等待中...
    Note over A,B: 死锁发生！<br/>互相等待对方释放资源
```

### 死锁产生的四个必要条件

```mermaid
flowchart TD
    A[死锁产生的必要条件] --> B[互斥条件]
    A --> C[请求与保持条件]
    A --> D[不剥夺条件]
    A --> E[循环等待条件]
    
    B --> B1[资源一次只能<br/>被一个事务占用]
    C --> C1[事务已占资源<br/>又申请新资源]
    D --> D1[已获得的资源<br/>不能被强制剥夺]
    E --> E1[事务之间形成<br/>循环等待链]
```

### 死锁检测与处理

**1. 死锁检测机制**

InnoDB 存储引擎通过 **等待图（Wait-for Graph）** 算法自动检测死锁：

```mermaid
flowchart LR
    A[事务A] -->|等待| B[事务B持有的锁]
    B -->|等待| C[事务C持有的锁]
    C -->|等待| A[事务A持有的锁]
    
    style A fill:#f99,stroke:#333
    style B fill:#f99,stroke:#333
    style C fill:#f99,stroke:#333
```

当检测到循环等待时，即判定发生死锁。

**2. 死锁处理策略**

- **超时回滚：** 设置 `innodb_lock_wait_timeout`（默认50秒），超时后自动回滚
- **主动检测：** InnoDB 自动检测死锁，选择**代价最小**的事务进行回滚
- **死锁日志：** 通过 `SHOW ENGINE INNODB STATUS` 查看死锁信息

**3. 死锁预防策略**

```mermaid
flowchart TD
    A[死锁预防策略] --> B[顺序加锁]
    A --> C[超时放弃]
    A --> D[一次性锁定]
    A --> E[乐观锁]
    
    B --> B1[所有事务按相同<br/>顺序访问资源]
    C --> C1[设置超时时间<br/>超时后重试]
    D --> D1[一次性获取<br/>所有需要的锁]
    E --> E1[使用版本号<br/>避免长时间持有锁]
```

### 死锁与事务隔离级别

| 隔离级别 | 死锁可能性 | 原因 |
|----------|------------|------|
| READ UNCOMMITTED | 极低 | 几乎不加锁 |
| READ COMMITTED | 较低 | 锁持有时间短 |
| REPEATABLE READ | 中等 | 间隙锁增加锁范围 |
| SERIALIZABLE | 高 | 大量共享锁和排他锁冲突 |

***

## 1.3.3 事务日志

事务日志是 InnoDB 存储引擎实现事务持久性和崩溃恢复的核心机制，采用 **预写式日志（Write-Ahead Logging, WAL）** 策略。

### WAL 核心思想

```mermaid
flowchart LR
    A[数据修改] --> B[先写日志<br/>Redo Log]
    B --> C[事务提交]
    C --> D[异步刷盘<br/>数据页]
    
    style B fill:#9f9,stroke:#333
```

**先写日志，再写磁盘：** 当数据修改时，先将修改记录到日志，事务即可提交成功，数据页可以稍后异步刷盘。

### Redo Log（重做日志）

**定义：** InnoDB 引擎特有的物理日志，记录"在某个数据页上做了什么修改"。

**核心作用：**

```mermaid
flowchart TD
    A[Redo Log作用] --> B[实现WAL机制]
    A --> C[崩溃恢复]
    A --> D[提高性能]
    
    B --> B1[先写日志<br/>再刷数据页]
    C --> C1[系统崩溃后<br/>恢复已提交事务]
    D --> D1[顺序写代替<br/>随机写]
```

**Redo Log 结构：**

```mermaid
graph LR
    A[Redo Log Buffer<br/>内存] --> B[OS Buffer]
    B --> C[Redo Log File<br/>磁盘]
    
    C --> D[ib_logfile0]
    C --> E[ib_logfile1]
    
    style A fill:#bbf,stroke:#333
    style C fill:#bfb,stroke:#333
```

**刷盘策略（`innodb_flush_log_at_trx_commit`）：**

| 值 | 策略 | 安全性 | 性能 |
|----|------|--------|------|
| 0 | 每秒刷盘 | 低 | 最高 |
| 1 | 每次事务提交刷盘 | 最高 | 较低 |
| 2 | 每次提交写入OS Buffer | 较高 | 较高 |

### Undo Log（回滚日志）

**定义：** 逻辑日志，记录"修改前的数据状态"，用于事务回滚和MVCC。

**核心作用：**

```mermaid
flowchart TD
    A[Undo Log作用] --> B[事务回滚]
    A --> C[MVCC实现]
    A --> D[崩溃恢复]
    
    B --> B1[撤销未提交事务<br/>的修改]
    C --> C1[提供历史版本<br/>实现一致性读]
    D --> D1[清理未提交事务]
```

**Undo Log 与 MVCC：**

```mermaid
flowchart LR
    A[数据行] --> B[当前版本]
    B --> C[Undo Log]
    C --> D[历史版本1]
    D --> E[历史版本2]
    E --> F[历史版本3]
    
    style B fill:#fbf,stroke:#333
    style C fill:#bff,stroke:#333
```

每个数据行的隐藏列中包含指向 Undo Log 的指针，形成版本链。

### Binlog（二进制日志）

**定义：** MySQL Server 层的逻辑日志，记录所有修改数据的 SQL 语句。

**与 Redo Log 的区别：**

| 特性 | Redo Log | Binlog |
|------|----------|--------|
| 层级 | 存储引擎层 | Server 层 |
| 类型 | 物理日志 | 逻辑日志 |
| 内容 | 数据页修改 | SQL语句 |
| 用途 | 崩溃恢复 | 主从复制、数据恢复 |
| 写入方式 | 循环写 | 追加写 |

**两阶段提交：**

```mermaid
sequenceDiagram
    participant A as 事务执行
    participant R as Redo Log<br/>Prepare
    participant B as Binlog<br/>写入
    participant C as Redo Log<br/>Commit
    
    A->>R: 写入Redo Log<br/>状态: Prepare
    R->>B: 写入Binlog
    B->>C: 写入Redo Log<br/>状态: Commit
    C->>A: 事务提交完成
```

两阶段提交保证了 Redo Log 和 Binlog 的一致性，避免主从数据不一致。

***

## 1.3.4 MySQL 中的事务

### 自动提交（AUTOCOMMIT）

MySQL 默认采用自动提交模式，每条 SQL 语句都会被当作一个独立的事务自动提交。

```sql
-- 查看自动提交状态
SHOW VARIABLES LIKE 'autocommit';

-- 关闭自动提交
SET autocommit = 0;

-- 开启事务
START TRANSACTION;
-- 或
BEGIN;

-- 提交事务
COMMIT;

-- 回滚事务
ROLLBACK;
```

### 事务控制语句

```mermaid
flowchart TD
    A[事务控制] --> B[START TRANSACTION]
    A --> C[COMMIT]
    A --> D[ROLLBACK]
    A --> E[SAVEPOINT]
    A --> F[ROLLBACK TO SAVEPOINT]
    
    B --> B1[开启新事务]
    C --> C1[提交所有修改]
    D --> D1[撤销所有修改]
    E --> E1[设置保存点]
    F --> F1[回滚到保存点]
```

### 隐式提交

某些语句会导致当前事务被隐式提交：

- DDL 语句（CREATE、ALTER、DROP 等）
- 隐式使用或修改 mysql 数据库中的表
- 事务控制或锁定语句（BEGIN、START TRANSACTION 等）
- 加载数据的语句（LOAD DATA）
- 复制相关的语句

### 事务与存储引擎

```mermaid
flowchart TD
    A[MySQL存储引擎] --> B[InnoDB]
    A --> C[MyISAM]
    A --> D[Memory]
    
    B --> B1[支持事务<br/>支持行级锁<br/>支持外键]
    C --> C1[不支持事务<br/>表级锁]
    D --> D1[不支持事务<br/>表级锁<br/>数据存内存]
    
    style B fill:#9f9,stroke:#333
    style C fill:#f99,stroke:#333
    style D fill:#f99,stroke:#333
```

**重要提示：** 只有 InnoDB 存储引擎支持完整的事务功能，MyISAM 不支持事务。

### 长事务的风险

```mermaid
flowchart TD
    A[长事务风险] --> B[锁持有时间长]
    A --> C[Undo Log膨胀]
    A --> D[阻塞其他事务]
    A --> E[影响复制延迟]
    
    B --> B1[降低并发性能]
    C --> C1[占用更多存储空间]
    D --> D1[其他事务等待]
    E --> E1[主从数据延迟增大]
```

**最佳实践：**
- 尽量缩短事务执行时间
- 避免在事务中进行用户交互
- 将大事务拆分为多个小事务
- 及时提交或回滚事务

***

## 总结

### 事务核心概念图

```mermaid
mindmap
  root((事务))
    ACID
      原子性 Atomicity
      一致性 Consistency
      隔离性 Isolation
      持久性 Durability
    隔离级别
      READ UNCOMMITTED
      READ COMMITTED
      REPEATABLE READ
      SERIALIZABLE
    并发问题
      脏读
      不可重复读
      幻读
    事务日志
      Redo Log
      Undo Log
      Binlog
    死锁
      检测机制
      预防策略
```

### 关键要点

1. **隔离级别选择：** 根据业务需求选择合适的隔离级别，在一致性和性能之间取得平衡
2. **死锁处理：** 应用程序应设计为重试机制，而不是试图完全避免死锁
3. **日志机制：** 理解 Redo Log、Undo Log 和 Binlog 的作用和区别
4. **存储引擎：** 需要事务支持时，必须使用 InnoDB 存储引擎
5. **事务设计：** 保持事务短小精悍，避免长事务带来的各种问题

### 参考资源

- 《高性能MySQL》（第3版）第1章 1.3节
- MySQL 官方文档：[Transaction Isolation Levels](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-isolation-levels.html)
- InnoDB 事务模型：[InnoDB Transaction Model](https://dev.mysql.com/doc/refman/8.0/en/innodb-transaction-model.html)
