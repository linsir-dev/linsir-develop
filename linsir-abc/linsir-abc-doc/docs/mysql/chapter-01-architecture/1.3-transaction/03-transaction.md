# 1.3 事务

> 事务是数据库管理系统执行过程中的一个逻辑单位，由一个或多个SQL语句组成。事务处理可以确保除非事务性单元内的所有操作都成功完成，否则不会永久更新面向数据的资源。

***

## 一、事务基础

### 1.1 什么是事务

事务（Transaction）是数据库操作的一个执行单元，它包含了一组数据库操作命令，这些命令要么全部执行成功，要么全部不执行。事务是数据库并发控制和恢复技术的基础。

### 1.2 ACID特性

事务必须满足四个基本特性，即ACID特性：

| 特性 | 英文 | 说明 | 实现机制 |
|------|------|------|----------|
| **原子性** | Atomicity | 事务是一个不可分割的最小工作单位，要么全部完成，要么全部失败回滚 | Undo Log（回滚日志） |
| **一致性** | Consistency | 事务执行前后，数据库从一个一致性状态转换到另一个一致性状态 | 约束校验、触发器、存储过程 |
| **隔离性** | Isolation | 并发执行的事务之间互不干扰，一个事务的执行不应该影响其他事务 | MVCC、锁机制 |
| **持久性** | Durability | 一旦事务提交，其对数据库的修改就是永久性的，即使系统故障也不会丢失 | Redo Log（重做日志） |

#### 1.2.1 原子性（Atomicity）

原子性保证事务中的所有操作要么全部成功，要么全部失败。如果事务中的任何一个操作失败，整个事务都会被回滚到事务开始前的状态。

**实现原理：**
- 通过 **Undo Log（回滚日志）** 实现
- 当事务执行修改操作时，InnoDB会先将原始数据写入Undo Log
- 如果事务需要回滚，通过Undo Log恢复原始数据
- Undo Log采用链表结构，形成版本链，支持多版本并发控制

#### 1.2.2 一致性（Consistency）

一致性确保事务执行前后，数据库的完整性约束不会被破坏。数据库必须从一个有效状态转换到另一个有效状态。

**保证机制：**
- 数据库约束：主键约束、外键约束、唯一约束、CHECK约束
- 触发器（Trigger）：自动执行的完整性检查
- 存储过程：业务逻辑封装
- 原子性、隔离性、持久性共同保障一致性

#### 1.2.3 隔离性（Isolation）

隔离性确保并发执行的事务之间互不干扰。每个事务都感觉自己是系统中唯一运行的事务。

**实现机制：**
- **锁机制**：控制并发修改，包括共享锁（S锁）和排他锁（X锁）
- **MVCC（多版本并发控制）**：控制并发读取，通过保存数据的历史版本实现

#### 1.2.4 持久性（Durability）

持久性保证一旦事务提交，其对数据库的修改就是永久性的，即使系统发生故障也不会丢失。

**实现原理：**
- 通过 **Redo Log（重做日志）** 实现
- 采用 **WAL（Write-Ahead Logging，预写日志）** 策略
- 事务提交前，先将修改记录写入Redo Log
- 系统崩溃后，通过Redo Log恢复已提交的数据

### 1.3 事务的状态

```
┌─────────────┐
│   活动状态   │  ← 事务正在执行中
│  (Active)   │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────────┐
│ 部分提交状态  │  ← 最后一条语句执行完毕，等待持久化
│(Partially Committed)│
└──────┬──────────────────────────────┘
       │
       ├──────────┐
       │          │
       ▼          ▼
┌──────────┐  ┌──────────┐
│ 提交状态  │  │ 失败状态  │
│(Committed)│  │ (Failed) │
└──────────┘  └────┬─────┘
                   │
                   ▼
            ┌──────────┐
            │ 中止状态  │  ← 事务回滚，数据库恢复到事务前状态
            │(Aborted) │
            └──────────┘
```

### 1.4 事务的基本操作

#### 1.4.1 开启事务

```sql
-- 方式1：显式开启事务
START TRANSACTION;
-- 或
BEGIN;

-- 方式2：设置保存点
START TRANSACTION;
SAVEPOINT sp1;  -- 创建保存点
```

#### 1.4.2 提交事务

```sql
-- 提交事务，使所有修改永久生效
COMMIT;
```

#### 1.4.3 回滚事务

```sql
-- 回滚整个事务
ROLLBACK;

-- 回滚到指定保存点
ROLLBACK TO SAVEPOINT sp1;

-- 释放保存点
RELEASE SAVEPOINT sp1;
```

#### 1.4.4 自动提交模式

```sql
-- 查看自动提交状态
SELECT @@autocommit;

-- 关闭自动提交（会话级）
SET autocommit = 0;

-- 开启自动提交（默认）
SET autocommit = 1;
```

**注意：** MySQL默认开启自动提交（autocommit=1），每条SQL语句都会自动成为一个事务。

***

## 二、事务隔离级别

### 2.1 并发事务问题

在多事务并发执行时，可能出现以下问题：

#### 2.1.1 脏读（Dirty Read）

一个事务读取了另一个事务尚未提交的数据，而这些数据可能随后被回滚。

```
时间线：
T1: BEGIN;
T1: UPDATE account SET balance = 1000 WHERE id = 1;  -- 未提交
T2: BEGIN;
T2: SELECT balance FROM account WHERE id = 1;  -- 读取到1000（脏数据）
T1: ROLLBACK;  -- T1回滚，balance恢复原始值
T2: -- 此时T2持有的1000是脏数据
```

#### 2.1.2 不可重复读（Non-Repeatable Read）

在同一个事务内，多次读取同一数据，结果不一致（被其他已提交的事务修改）。

```
时间线：
T1: BEGIN;
T1: SELECT balance FROM account WHERE id = 1;  -- 读取到500
T2: BEGIN;
T2: UPDATE account SET balance = 1000 WHERE id = 1;
T2: COMMIT;
T1: SELECT balance FROM account WHERE id = 1;  -- 读取到1000（不一致！）
T1: COMMIT;
```

#### 2.1.3 幻读（Phantom Read）

在同一个事务内，多次执行同一范围查询，返回的结果集行数不一致（其他事务插入或删除了符合范围的数据）。

```
时间线：
T1: BEGIN;
T1: SELECT * FROM account WHERE balance > 500;  -- 返回2条记录
T2: BEGIN;
T2: INSERT INTO account (id, balance) VALUES (3, 800);
T2: COMMIT;
T1: SELECT * FROM account WHERE balance > 500;  -- 返回3条记录（幻读！）
T1: COMMIT;
```

### 2.2 四种隔离级别

SQL标准定义了四种事务隔离级别：

| 隔离级别 | 脏读 | 不可重复读 | 幻读 | 并发性能 | 说明 |
|----------|------|------------|------|----------|------|
| **读未提交** (Read Uncommitted) | ✅ 允许 | ✅ 允许 | ✅ 允许 | 最高 | 最低隔离级别，几乎不使用 |
| **读已提交** (Read Committed) | ❌ 禁止 | ✅ 允许 | ✅ 允许 | 较高 | Oracle默认级别 |
| **可重复读** (Repeatable Read) | ❌ 禁止 | ❌ 禁止 | ⚠️ 部分允许 | 中等 | **MySQL默认级别** |
| **串行化** (Serializable) | ❌ 禁止 | ❌ 禁止 | ❌ 禁止 | 最低 | 最高隔离级别，性能最差 |

#### 2.2.1 读未提交（Read Uncommitted）

- 最低的隔离级别
- 一个事务可以读取另一个未提交事务的修改
- 可能出现脏读、不可重复读、幻读
- 实际应用中几乎不使用

#### 2.2.2 读已提交（Read Committed）

- 一个事务只能读取另一个已提交事务的修改
- 解决了脏读问题
- 可能出现不可重复读和幻读
- **Oracle和PostgreSQL的默认隔离级别**

**InnoDB实现：**
- 每次SELECT都会生成新的ReadView（读视图）
- 可以看到其他事务已提交的最新修改
- 锁定读（SELECT FOR UPDATE）只锁定索引记录，不锁定间隙

#### 2.2.3 可重复读（Repeatable Read）

- 同一个事务中，多次读取同一数据结果一致
- 解决了脏读和不可重复读问题
- **MySQL InnoDB的默认隔离级别**
- InnoDB通过MVCC + 间隙锁（Gap Lock）解决了大部分幻读问题

**InnoDB实现：**
- 事务启动后的第一次SELECT生成ReadView，后续查询复用该ReadView
- 保证事务内多次读取结果一致
- 使用Next-Key Lock（行锁+间隙锁）防止幻读

#### 2.2.4 串行化（Serializable）

- 最高的隔离级别
- 所有事务串行执行，完全避免并发问题
- 读取时加共享锁，写入时加排他锁
- 性能最差，仅用于强一致性场景

### 2.3 隔离级别设置

```sql
-- 查看当前隔离级别（MySQL 8.0+）
SELECT @@transaction_isolation;
-- 或
SHOW VARIABLES LIKE 'transaction_isolation';

-- 设置会话级隔离级别
SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- 设置全局隔离级别（需重启会话生效）
SET GLOBAL TRANSACTION ISOLATION LEVEL REPEATABLE READ;
```

### 2.4 隔离级别选择建议

| 应用场景 | 推荐隔离级别 | 说明 |
|----------|--------------|------|
| 互联网应用（电商、社交） | READ COMMITTED | 平衡并发性能和一致性 |
| 金融、支付系统 | REPEATABLE READ / SERIALIZABLE | 保证强一致性 |
| 报表查询系统 | REPEATABLE READ | 保证统计结果一致性 |
| 日志系统 | READ COMMITTED | 追求高并发写入 |

***

## 三、死锁

### 3.1 什么是死锁

死锁是指两个或多个事务在执行过程中，因争夺资源而造成的一种互相等待的现象，若无外力作用，它们都将无法继续执行。

```
死锁示例：

事务T1                          事务T2
─────────────────────────────────────────────────
BEGIN;                          BEGIN;
UPDATE A SET ...;               UPDATE B SET ...;
                                
-- 等待B的锁                      -- 等待A的锁
UPDATE B SET ...;  ←───────→    UPDATE A SET ...;
(等待T2释放B)                   (等待T1释放A)

结果：T1和T2互相等待，形成死锁
```

### 3.2 死锁产生条件

死锁产生需要同时满足以下四个条件：

1. **互斥条件**：资源不能被共享，只能被一个事务占用
2. **请求与保持条件**：事务已经持有了至少一个资源，但又提出了新的资源请求
3. **不剥夺条件**：事务已获得的资源在未使用完之前不能被强制剥夺
4. **循环等待条件**：多个事务之间形成一种头尾相接的循环等待资源关系

### 3.3 InnoDB死锁检测与处理

#### 3.3.1 死锁检测机制

InnoDB内置了死锁检测机制：
- 使用 **等待图（Wait-for Graph）** 算法检测死锁
- 当检测到死锁时，自动选择一个事务进行回滚
- 通常选择 **代价最小的事务**（Undo Log最少的事务）作为牺牲者

#### 3.3.2 死锁处理参数

```sql
-- 查看死锁检测是否开启（默认ON）
SHOW VARIABLES LIKE 'innodb_deadlock_detect';

-- 关闭死锁检测（不推荐，可能导致大量锁等待）
SET GLOBAL innodb_deadlock_detect = OFF;

-- 查看锁等待超时时间（默认50秒）
SHOW VARIABLES LIKE 'innodb_lock_wait_timeout';

-- 设置锁等待超时时间
SET GLOBAL innodb_lock_wait_timeout = 30;
```

#### 3.3.3 死锁日志

```sql
-- 查看最近一次死锁信息
SHOW ENGINE INNODB STATUS;

-- 开启死锁日志记录到错误日志
SET GLOBAL innodb_print_all_deadlocks = ON;
```

死锁日志示例：
```
------------------------
LATEST DETECTED DEADLOCK
------------------------
*** (1) TRANSACTION:
TRANSACTION 12345, ACTIVE 12 sec starting index read
mysql tables in use 1, locked 1
LOCK WAIT 3 lock struct(s), heap size 1136, 2 row lock(s)
MySQL thread id 100, OS thread handle 123456789, query id 1000 localhost 127.0.0.1 user updating
UPDATE account SET balance = balance - 100 WHERE id = 1
*** (1) WAITING FOR THIS LOCK TO BE GRANTED:
RECORD LOCKS space id 58 page no 3 n bits 72 index PRIMARY of table `test`.`account` trx id 12345 lock_mode X locks rec but not gap waiting

*** (2) TRANSACTION:
TRANSACTION 12346, ACTIVE 10 sec starting index read
mysql tables in use 1, locked 1
3 lock struct(s), heap size 1136, 2 row lock(s)
MySQL thread id 101, OS thread handle 123456790, query id 1001 localhost 127.0.0.1 user updating
UPDATE account SET balance = balance - 200 WHERE id = 2
*** (2) HOLDS THE LOCK(S):
RECORD LOCKS space id 58 page no 3 n bits 72 index PRIMARY of table `test`.`account` trx id 12346 lock_mode X locks rec but not gap

*** (2) WAITING FOR THIS LOCK TO BE GRANTED:
RECORD LOCKS space id 58 page no 3 n bits 72 index PRIMARY of table `test`.`account` trx id 12346 lock_mode X locks rec but not gap waiting

*** WE ROLL BACK TRANSACTION (2)
```

### 3.4 死锁预防策略

#### 3.4.1 加锁顺序一致

让所有事务按照相同的顺序获取锁：

```java
// 好的实践：按ID排序后加锁
public void transfer(Long fromId, Long toId, BigDecimal amount) {
    // 确保总是先锁ID小的，再锁ID大的
    Long firstId = Math.min(fromId, toId);
    Long secondId = Math.max(fromId, toId);
    
    Account firstAccount = accountMapper.selectForUpdate(firstId);
    Account secondAccount = accountMapper.selectForUpdate(secondId);
    
    // 执行转账...
}
```

#### 3.4.2 降低隔离级别

将隔离级别从REPEATABLE READ降低到READ COMMITTED，可以减少锁的持有时间。

#### 3.4.3 使用乐观锁

对于读多写少的场景，使用乐观锁（版本号机制）代替悲观锁：

```sql
-- 乐观锁实现
UPDATE account 
SET balance = balance - 100, version = version + 1 
WHERE id = 1 AND version = 5;

-- 检查更新行数，如果为0表示并发冲突，需要重试
```

#### 3.4.4 减少事务持有锁的时间

- 将非必要的操作移出事务
- 避免在事务中执行耗时的计算或IO操作
- 尽快提交事务

#### 3.4.5 使用索引减少锁范围

确保查询使用索引，避免全表扫描导致锁范围过大：

```sql
-- 不好的做法：全表扫描，锁定所有行
UPDATE account SET balance = balance - 100 WHERE name = '张三';

-- 好的做法：使用主键，只锁定一行
UPDATE account SET balance = balance - 100 WHERE id = 1;
```

***

## 四、事务日志

事务日志是保证ACID特性的核心机制，主要包括 **Redo Log（重做日志）** 和 **Undo Log（回滚日志）**。

### 4.1 Redo Log（重做日志）

#### 4.1.1 Redo Log作用

Redo Log用于保证事务的**持久性**，确保已提交的事务不会丢失。

**核心作用：**
- 记录数据页的物理修改
- 用于崩溃恢复（Crash Recovery）
- 实现WAL（Write-Ahead Logging）机制

#### 4.1.2 WAL机制

WAL（预写日志）是数据库系统中常用的技术，核心思想是：**先写日志，再写磁盘**。

```
事务执行流程：

1. 修改缓冲池中的数据页
2. 生成Redo Log记录，写入Redo Log Buffer
3. 事务提交时，将Redo Log Buffer刷新到磁盘（顺序写）
4. 后台线程异步将脏页刷新到数据文件（随机写）

崩溃恢复时：
- 如果数据文件未更新，但Redo Log已记录，则重做（Redo）这些修改
- 如果数据文件已更新，无需处理
```

#### 4.1.3 Redo Log结构

Redo Log采用循环写入的方式，由多个固定大小的日志文件组成：

```
┌─────────────────┐
│   ib_logfile0   │
│   (48MB)        │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   ib_logfile1   │
│   (48MB)        │
└────────┬────────┘
         │
         ▼
         (循环回到 ib_logfile0)
```

**关键参数：**

```sql
-- 查看Redo Log文件大小（默认48MB）
SHOW VARIABLES LIKE 'innodb_log_file_size';

-- 查看Redo Log文件数量（默认2个）
SHOW VARIABLES LIKE 'innodb_log_files_in_group';

-- 查看Redo Log Buffer大小（默认16MB）
SHOW VARIABLES LIKE 'innodb_log_buffer_size';
```

#### 4.1.4 Redo Log刷盘策略

```sql
-- 查看刷盘策略（默认1）
SHOW VARIABLES LIKE 'innodb_flush_log_at_trx_commit';
```

| 参数值 | 说明 | 数据安全性 | 性能 |
|--------|------|------------|------|
| **0** | 每秒将Redo Log Buffer写入并刷新到磁盘 | 低（可能丢失1秒数据） | 最高 |
| **1** | 每次事务提交时立即刷新到磁盘 | 高（不会丢失已提交数据） | 较低 |
| **2** | 每次事务提交时写入OS缓存，每秒刷新到磁盘 | 中（可能丢失1秒数据） | 较高 |

**生产环境建议：**
- 对数据安全性要求高的场景（金融、支付）：设置为 **1**
- 对性能要求高，可容忍少量数据丢失的场景：设置为 **2**

### 4.2 Undo Log（回滚日志）

#### 4.2.1 Undo Log作用

Undo Log用于保证事务的**原子性**，支持事务回滚和MVCC。

**核心作用：**
- 记录数据修改前的旧值
- 用于事务回滚（Rollback）
- 用于MVCC，提供历史版本数据

#### 4.2.2 Undo Log类型

| 类型 | 说明 | 用途 |
|------|------|------|
| **Insert Undo Log** | 记录INSERT操作 | 事务回滚时删除记录 |
| **Update Undo Log** | 记录UPDATE/DELETE操作 | 事务回滚时恢复数据，支持MVCC |

#### 4.2.3 Undo Log存储

MySQL 8.0中，Undo Log默认存储在独立的表空间中：

```sql
-- 查看Undo表空间配置
SHOW VARIABLES LIKE 'innodb_undo_tablespaces';

-- 查看Undo Log是否独立表空间（默认ON）
SHOW VARIABLES LIKE 'innodb_undo_log_encrypt';
```

#### 4.2.4 Undo Log生命周期

```
1. 事务开始
   │
   ▼
2. 执行DML操作
   ├── 生成Undo Log记录
   ├── 将Undo Log写入Undo Log Buffer
   └── 数据行中的DB_ROLL_PTR指向Undo Log
   │
   ▼
3. 事务提交
   ├── Undo Log不会立即删除
   ├── 标记为"可清理"
   └── 等待Purge线程清理
   │
   ▼
4. 事务回滚
   └── 使用Undo Log恢复数据
```

### 4.3 Binlog（二进制日志）

#### 4.3.1 Binlog作用

Binlog是MySQL Server层的日志，记录所有数据库表结构变更和数据修改的逻辑操作。

**核心作用：**
- **数据恢复**：基于时间点恢复（Point-in-Time Recovery）
- **主从复制**：主库将Binlog发送给从库，实现数据同步
- **审计**：记录所有数据变更操作

#### 4.3.2 Binlog格式

```sql
-- 查看Binlog格式
SHOW VARIABLES LIKE 'binlog_format';
```

| 格式 | 说明 | 优点 | 缺点 |
|------|------|------|------|
| **STATEMENT** | 记录SQL语句 | 日志量小 | 某些语句可能导致主从不一致 |
| **ROW** | 记录每行数据的变更 | 精确、安全 | 日志量大 |
| **MIXED** | 混合模式，MySQL自动选择 | 平衡 | 复杂 |

**MySQL 8.0默认使用ROW格式。**

#### 4.3.3 Redo Log vs Binlog

| 特性 | Redo Log | Binlog |
|------|----------|--------|
| **层级** | 存储引擎层（InnoDB） | Server层 |
| **内容** | 物理日志（数据页修改） | 逻辑日志（SQL语句或行变更） |
| **用途** | 崩溃恢复 | 数据恢复、主从复制 |
| **写入方式** | 循环写入 | 追加写入 |
| **文件大小** | 固定大小 | 可配置，达到上限后滚动 |
| **事务关联** | 记录事务的物理修改 | 记录事务的逻辑操作 |

### 4.4 两阶段提交（Two-Phase Commit）

为了保证Redo Log和Binlog的一致性，MySQL使用**两阶段提交**机制。

```
事务提交过程：

        ┌─────────────────┐
        │   事务执行中     │
        │  (生成Redo Log)  │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │   准备阶段       │  ← Write Redo Log (Prepare)
        │  (Prepare Phase) │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │   写入Binlog     │
        │  (Write Binlog)  │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │   提交阶段       │  ← Write Redo Log (Commit)
        │  (Commit Phase)  │
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────┐
        │    事务提交      │
        │   (COMMIT)      │
        └─────────────────┘
```

**两阶段提交的作用：**
- 确保Redo Log和Binlog的一致性
- 崩溃恢复时，根据Binlog判断事务是否需要提交

***

## 五、MySQL中的事务

### 5.1 隐式事务与显式事务

#### 5.1.1 隐式事务

当`autocommit=1`时，每条SQL语句都是一个独立的事务：

```sql
-- 默认情况下，以下每条语句都是独立事务
INSERT INTO account (id, balance) VALUES (1, 1000);  -- 自动提交
UPDATE account SET balance = 900 WHERE id = 1;       -- 自动提交
```

#### 5.1.2 显式事务

通过BEGIN/START TRANSACTION显式控制事务边界：

```sql
-- 关闭自动提交
SET autocommit = 0;

-- 或显式开启事务
START TRANSACTION;
-- 或
BEGIN;

-- 执行多个操作
INSERT INTO account (id, balance) VALUES (1, 1000);
UPDATE account SET balance = 900 WHERE id = 1;

-- 提交或回滚
COMMIT;
-- 或
ROLLBACK;
```

### 5.2 事务中的DDL语句

在MySQL中，DDL语句（CREATE、ALTER、DROP等）会自动提交当前事务：

```sql
BEGIN;
INSERT INTO account (id, balance) VALUES (1, 1000);

-- DDL语句会自动提交前面的事务
CREATE TABLE test (id INT);  -- 自动提交！

-- 此时已经是一个新的事务
INSERT INTO account (id, balance) VALUES (2, 2000);
ROLLBACK;  -- 只回滚第二条INSERT
```

### 5.3 长事务问题

#### 5.3.1 长事务的危害

- **锁持有时间长**：阻塞其他事务的执行
- **Undo Log膨胀**：占用大量存储空间
- **影响Purge操作**：无法及时清理旧版本数据
- **主从延迟**：从库执行时间长，导致延迟

#### 5.3.2 监控长事务

```sql
-- 查看正在执行的事务
SELECT 
    trx_id,
    trx_state,
    trx_started,
    TIMESTAMPDIFF(SECOND, trx_started, NOW()) AS trx_seconds,
    trx_mysql_thread_id,
    trx_tables_locked,
    trx_rows_locked
FROM information_schema.INNODB_TRX
ORDER BY trx_started;

-- 查看长事务（超过60秒）
SELECT 
    trx_id,
    trx_mysql_thread_id,
    trx_state,
    trx_started,
    TIMESTAMPDIFF(SECOND, trx_started, NOW()) AS trx_seconds
FROM information_schema.INNODB_TRX
WHERE TIMESTAMPDIFF(SECOND, trx_started, NOW()) > 60;
```

#### 5.3.3 避免长事务

- 将大事务拆分为多个小事务
- 避免在事务中执行耗时操作
- 设置事务超时时间
- 使用批量操作代替单条操作

### 5.4 事务最佳实践

#### 5.4.1 事务设计原则

1. **保持事务简短**：尽快提交，减少锁持有时间
2. **避免嵌套事务**：MySQL不支持真正的嵌套事务
3. **在事务开始处获取所有需要的锁**：避免死锁
4. **按固定顺序访问资源**：避免循环等待
5. **设置合理的隔离级别**：不要盲目使用最高隔离级别

#### 5.4.2 事务异常处理

```java
@Service
public class AccountService {
    
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        try {
            // 1. 参数校验
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("转账金额必须大于0");
            }
            
            // 2. 获取账户（按ID排序，避免死锁）
            Long firstId = Math.min(fromId, toId);
            Long secondId = Math.max(fromId, toId);
            
            Account firstAccount = accountMapper.selectForUpdate(firstId);
            Account secondAccount = accountMapper.selectForUpdate(secondId);
            
            // 3. 确定转出和转入账户
            Account fromAccount = fromId.equals(firstId) ? firstAccount : secondAccount;
            Account toAccount = toId.equals(firstId) ? firstAccount : secondAccount;
            
            // 4. 检查余额
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("余额不足");
            }
            
            // 5. 执行转账
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));
            
            accountMapper.update(fromAccount);
            accountMapper.update(toAccount);
            
            // 6. 记录日志
            transactionLogMapper.insert(new TransactionLog(fromId, toId, amount));
            
        } catch (Exception e) {
            // 记录日志，异常会触发回滚
            log.error("转账失败", e);
            throw e;
        }
    }
}
```

***

## 六、总结

### 6.1 核心概念回顾

| 概念 | 要点 |
|------|------|
| **ACID** | 原子性（Undo Log）、一致性（约束）、隔离性（MVCC+锁）、持久性（Redo Log） |
| **隔离级别** | 读未提交、读已提交、可重复读（默认）、串行化 |
| **并发问题** | 脏读、不可重复读、幻读 |
| **死锁** | 互斥、请求保持、不剥夺、循环等待；检测与预防策略 |
| **事务日志** | Redo Log（持久性）、Undo Log（原子性+MVCC）、Binlog（复制+恢复） |

### 6.2 实际应用建议

1. **默认使用REPEATABLE READ隔离级别**，MySQL InnoDB已优化得很好
2. **预防死锁**：按固定顺序加锁、使用乐观锁、降低隔离级别
3. **避免长事务**：拆分大事务、尽快提交
4. **合理配置日志**：
   - `innodb_flush_log_at_trx_commit = 1`（高安全）或 `2`（高性能）
   - `sync_binlog = 1`（确保Binlog持久化）
5. **监控事务**：定期检查长事务和死锁日志

### 6.3 与其他章节的关系

- **1.2 并发控制**：事务隔离通过锁和MVCC实现
- **1.4 多版本并发控制（MVCC）**：基于Undo Log实现
- **1.5 存储引擎**：InnoDB支持事务，MyISAM不支持

***

**参考资料：**
- MySQL 8.0 Reference Manual - Transaction Isolation Levels
- 《高性能MySQL》（第4版）
- InnoDB Transaction and Locking Model
