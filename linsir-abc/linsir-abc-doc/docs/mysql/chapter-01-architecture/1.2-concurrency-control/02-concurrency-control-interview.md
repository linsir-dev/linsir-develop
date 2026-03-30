# 1.2 并发控制 - 面试题总结

## 一、基础概念题

### 1. MySQL 有哪些锁？从不同的维度分别说明

**参考答案：**

**按锁定范围：**
- **表级锁**：锁定整张表，适用于 MyISAM 引擎，开销小但并发度低
- **行级锁**：锁定单行数据，InnoDB 引擎支持，并发度高但开销大
- **页级锁**：锁定数据页（如 BDB 引擎），介于表锁和行锁之间

**按锁模式：**
- **共享锁（S 锁）**：允许多事务读取同一资源，但禁止修改（如 `SELECT ... LOCK IN SHARE MODE`）
- **排他锁（X 锁）**：独占资源，禁止其他事务读写（如 `SELECT ... FOR UPDATE`）

**按锁机制：**
- **意向锁（IS/IX 锁）**：表级锁，用于快速判断表中是否有行级锁冲突
- **间隙锁（Gap Lock）**：锁定索引范围间隙，防止幻读
- **临键锁（Next-Key Lock）**：间隙锁 + 记录锁，锁定左开右闭区间
- **记录锁（Record Lock）**：精确锁定单行记录的索引项

---

### 2. 什么是脏读、不可重复读、幻读？请分别举例说明

**参考答案：**

| 问题 | 定义 | 示例 |
|------|------|------|
| **脏读** | 读取到其他事务未提交的数据 | 事务 A 修改但未提交，事务 B 读取到修改后的值，随后 A 回滚，B 读到的就是脏数据 |
| **不可重复读** | 同一事务内多次读取同一数据，结果不一致 | 事务 A 读取后，事务 B 修改并提交，事务 A 再次读取结果不同 |
| **幻读** | 同一事务内多次查询，结果集数量不同 | 事务 A 查询后，事务 B 插入新数据，事务 A 再次查询多出新行 |

**关键区别：**
- 脏读 vs 不可重复读：脏读读到的是未提交数据，不可重复读读到的是已提交数据
- 不可重复读 vs 幻读：不可重复读是同一行数据的值变了，幻读是结果集的行数变了

---

### 3. MySQL 的四种事务隔离级别分别是什么？能解决哪些问题？

**参考答案：**

| 隔离级别 | 脏读 | 不可重复读 | 幻读 | 说明 |
|----------|------|------------|------|------|
| **READ UNCOMMITTED** | ❌ | ❌ | ❌ | 最低级别，几乎不使用 |
| **READ COMMITTED** | ✅ | ❌ | ❌ | 只能读到已提交的数据，Oracle 默认 |
| **REPEATABLE READ** | ✅ | ✅ | ❌ | MySQL 默认，通过 MVCC + 间隙锁基本解决幻读 |
| **SERIALIZABLE** | ✅ | ✅ | ✅ | 最高级别，强制串行执行，性能最差 |

**实现机制：**
- **脏读解决**：MVCC（不读未提交的版本）
- **不可重复读解决**：RR + MVCC（Read View 只生成一次）
- **幻读解决**：RR + 间隙锁（Next-Key Lock）

---

## 二、锁机制深入题

### 4. 行锁和表锁有什么区别？什么情况下会退化为表锁？

**参考答案：**

| 对比项 | 行级锁 | 表级锁 |
|--------|--------|--------|
| 锁定粒度 | 单行数据 | 整张表 |
| 并发性能 | 高 | 低 |
| 开销 | 大 | 小 |
| 死锁风险 | 有 | 无 |
| 适用场景 | 高并发写操作（OLTP） | 批量操作或低并发 |
| 支持引擎 | InnoDB | MyISAM/InnoDB |

**行锁退化为表锁的情况：**
1. **WHERE 条件没有使用索引**：全表扫描时，InnoDB 会给所有行加行锁，等效于锁全表
2. **使用 `LOCK TABLES` 显式加表锁**
3. **执行 `ALTER`、`TRUNCATE` 等 DDL 操作**

**避免方法：**
- 确保查询条件使用索引
- 使用 InnoDB 引擎
- 避免全表扫描

---

### 5. 什么是间隙锁（Gap Lock）？什么时候会触发？

**参考答案：**

**定义：**
间隙锁锁定的是索引记录之间的"空隙"，而不是记录本身。目的是防止其他事务在间隙中插入新数据，从而防止幻读。

**触发条件：**
1. **隔离级别为 REPEATABLE READ**（可重复读）
2. **使用范围查询**（如 `WHERE id > 5 AND id < 10`）
3. **使用非唯一索引进行等值查询**

**示例：**
```sql
-- 假设数据：id = 1, 5, 10
-- 事务 A
START TRANSACTION;
SELECT * FROM users WHERE id > 5 AND id < 10 FOR UPDATE;
-- 间隙锁锁定 (5, 10) 之间的间隙

-- 事务 B
INSERT INTO users (id, name) VALUES (7, '张三');
-- 阻塞！等待间隙锁释放
```

**注意：** READ COMMITTED 隔离级别下没有间隙锁。

---

### 6. 什么是临键锁（Next-Key Lock）？它是如何解决幻读的？

**参考答案：**

**定义：**
临键锁 = 记录锁 + 间隙锁，锁定范围是**左开右闭区间** `(上一个记录, 当前记录]`

**作用：**
- 锁定记录本身（防止修改）
- 锁定记录前面的间隙（防止插入）

**解决幻读的原理：**
```
数据：id = 10, 20, 30, 40
Next-Key Lock 范围：
(-∞, 10], (10, 20], (20, 30], (30, 40], (40, +∞)

示例：
SELECT * FROM users WHERE id = 20 FOR UPDATE;
-- 锁定范围：(10, 20]
-- 其他事务无法在 id=20 处插入或修改
```

**加锁规则（RR 级别）：**
- 唯一索引等值查询且记录存在 → 记录锁
- 唯一索引等值查询但记录不存在 → 间隙锁
- 非唯一索引等值查询 → 临键锁
- 范围查询 → 临键锁

---

### 7. 意向锁（Intention Lock）的作用是什么？

**参考答案：**

**定义：**
意向锁是表级锁，用于表示事务稍后要对表中的某些行加共享锁或排他锁。

**类型：**
- **IS 锁（意向共享锁）**：表示事务要对某些行加 S 锁
- **IX 锁（意向排他锁）**：表示事务要对某些行加 X 锁

**作用：**
1. **快速判断冲突**：事务要加表锁时，只需检查意向锁，不需要遍历所有行
2. **协调表锁和行锁**：允许表锁和行锁共存

**兼容性矩阵：**
|  | IS | IX | S | X |
|--|----|----|---|---|
| **IS** | ✅ | ✅ | ✅ | ❌ |
| **IX** | ✅ | ✅ | ❌ | ❌ |
| **S** | ✅ | ❌ | ✅ | ❌ |
| **X** | ❌ | ❌ | ❌ | ❌ |

---

## 三、MVCC 相关题

### 8. 什么是 MVCC？它的实现原理是什么？

**参考答案：**

**定义：**
MVCC（Multi-Version Concurrency Control，多版本并发控制）是通过保存数据在不同时间点的多个版本来实现并发控制的技术。

**核心思想：**
- 读操作读取历史版本（快照读），不需要加锁
- 写操作创建新版本，不影响读取旧版本
- 实现读写不阻塞，提高并发性能

**实现原理（三大组件）：**

**1. 隐藏字段**
每行数据包含两个隐藏列：
- `DB_TRX_ID`：最后修改这行的事务 ID
- `DB_ROLL_PTR`：回滚指针，指向 undo log 中的历史版本

**2. Undo Log**
保存数据的历史版本，形成版本链：
```
当前版本 ← 历史版本1 ← 历史版本2 ← ...
```

**3. Read View（一致性视图）**
决定事务能看到哪个版本的数据：
- `creator_trx_id`：创建该视图的事务 ID
- `m_ids`：活跃事务 ID 列表
- `min_trx_id`：最小活跃事务 ID
- `max_trx_id`：下一个分配的事务 ID

**可见性判断规则：**
1. 如果 `DB_TRX_ID` = `creator_trx_id`：可见（自己修改的）
2. 如果 `DB_TRX_ID` < `min_trx_id`：可见（已提交）
3. 如果 `DB_TRX_ID` >= `max_trx_id`：不可见（将来事务）
4. 如果 `min_trx_id` <= `DB_TRX_ID` < `max_trx_id`：
   - 在 `m_ids` 中：不可见（未提交）
   - 不在 `m_ids` 中：可见（已提交）

---

### 9. 快照读和当前读有什么区别？

**参考答案：**

| 特性 | 快照读（Snapshot Read） | 当前读（Current Read） |
|------|------------------------|------------------------|
| **读取内容** | 历史版本（快照） | 最新版本 |
| **是否加锁** | 不加锁 | 加锁 |
| **实现机制** | MVCC | 锁机制 |
| **SQL 示例** | 普通 `SELECT` | `SELECT ... FOR UPDATE` / `UPDATE` / `DELETE` |
| **阻塞情况** | 不阻塞其他事务 | 可能阻塞其他事务 |

**示例：**
```sql
-- 快照读（RR 级别）
START TRANSACTION;
SELECT * FROM users WHERE id = 1;  -- 读取快照，不加锁

-- 当前读
SELECT * FROM users WHERE id = 1 FOR UPDATE;  -- 读取最新数据，加 X 锁
UPDATE users SET name = '张三' WHERE id = 1;   -- 当前读 + 加 X 锁
```

**重要区别：**
- 快照读可能读到历史数据
- 当前读一定读到最新数据，但会加锁

---

### 10. REPEATABLE READ 隔离级别下，MVCC 如何避免不可重复读？

**参考答案：**

**原理：**
在 RR 级别下，事务启动时生成 Read View，**整个事务期间只使用这一个 Read View**。

**过程：**
```sql
-- 事务 B
START TRANSACTION;
SELECT * FROM users WHERE id = 1;  -- 生成 Read View，假设看到 balance = 1000

-- 事务 A
START TRANSACTION;
UPDATE users SET balance = 900 WHERE id = 1;
COMMIT;  -- 提交修改

-- 事务 B 再次查询
SELECT * FROM users WHERE id = 1;  
-- 仍然看到 balance = 1000（基于同一个 Read View）

COMMIT;
```

**关键点：**
- 事务 B 两次查询使用同一个 Read View
- 事务 A 的修改对事务 B 不可见（因为事务 A 的 trx_id 在 Read View 的 m_ids 中或大于 max_trx_id）
- 实现了"可重复读"

**对比 READ COMMITTED：**
- RC 级别：每次查询生成新的 Read View，能看到其他事务已提交的修改
- RR 级别：事务开始时生成 Read View，整个事务使用同一视图

---

## 四、综合应用题

### 11. 为什么 MySQL 既有锁机制，还要引入 MVCC？

**参考答案：**

**原因：**
MVCC 用"版本链 + Read View"把读写解耦了，在保证必要隔离的同时大幅提升并发读取性能。

**对比：**

| 场景 | 锁机制 | MVCC |
|------|--------|------|
| 高并发读 | 需要加锁，性能差 | 无锁，性能远优于锁 |
| 高并发写 | 直接高效 | 需要维护版本链，有开销 |
| 读写混合 | 读写相互阻塞 | 读写不阻塞，最优 |

**结论：**
- 锁机制：解决写-写冲突，保证强一致性
- MVCC：解决读-写冲突，实现读写不阻塞
- 两者结合：MVCC + 锁 = 高并发 + 一致性

---

### 12. 线上出现死锁，如何排查和解决？

**参考答案：**

**排查方法：**

**1. 查看死锁日志**
```sql
SHOW ENGINE INNODB STATUS;
-- 查看 LATEST DETECTED DEADLOCK 部分
```

**2. 查询锁等待信息**
```sql
-- 查看当前锁等待
SELECT * FROM information_schema.innodb_lock_waits;

-- 查看当前锁
SELECT * FROM information_schema.innodb_locks;
```

**3. 开启死锁监控**
```sql
-- 开启死锁检测日志
SET GLOBAL innodb_print_all_deadlocks = ON;
```

**死锁产生的四个条件：**
1. **互斥**：资源被独占
2. **持有并等待**：事务持有资源并等待其他资源
3. **不可剥夺**：资源无法强制释放
4. **循环等待**：事务间形成环形等待链

**解决方案：**

**1. 预防死锁**
- **统一加锁顺序**：所有事务按相同顺序获取锁
- **减少事务大小**：缩短锁持有时间
- **使用索引**：避免全表扫描导致锁范围扩大

**2. 死锁处理**
- **设置超时**：`innodb_lock_wait_timeout`（默认 50 秒）
- **自动检测**：InnoDB 自动检测并回滚代价最小的事务
- **重试机制**：应用层捕获死锁异常后重试

**示例（统一加锁顺序）：**
```java
// 按主键升序排序，统一加锁顺序
List<Long> ids = Arrays.asList(3L, 1L, 2L);
Collections.sort(ids);  // [1, 2, 3]

for (Long id : ids) {
    // 按顺序加锁，避免死锁
    updateAccount(id, amount);
}
```

---

### 13. 乐观锁和悲观锁的区别？MySQL 中如何实现？

**参考答案：**

| 特性 | 悲观锁 | 乐观锁 |
|------|--------|--------|
| **思想** | 假设冲突频繁，提前加锁 | 假设冲突少，不加锁，提交时检查 |
| **实现** | `SELECT ... FOR UPDATE` | 版本号 / CAS |
| **性能** | 低并发性能好 | 高并发性能好 |
| **一致性** | 强一致性 | 最终一致性 |
| **适用场景** | 写多读少 | 读多写少 |

**MySQL 实现：**

**悲观锁：**
```sql
START TRANSACTION;
SELECT * FROM users WHERE id = 1 FOR UPDATE;  -- 加 X 锁
-- 执行业务逻辑
UPDATE users SET balance = balance - 100 WHERE id = 1;
COMMIT;
```

**乐观锁（版本号）：**
```sql
-- 查询时获取版本号
SELECT id, balance, version FROM users WHERE id = 1;
-- 假设 version = 10

-- 更新时检查版本号
UPDATE users 
SET balance = balance - 100, version = version + 1 
WHERE id = 1 AND version = 10;

-- 影响行数 = 1：更新成功
-- 影响行数 = 0：版本冲突，需要重试
```

---

### 14. 一条 UPDATE 语句的执行过程是怎样的？会加什么锁？

**参考答案：**

**执行过程：**
```
1. 解析 SQL
2. 优化器选择执行计划
3. 根据 WHERE 条件定位记录
   - 有索引：走索引定位
   - 无索引：全表扫描
4. 加锁
5. 修改数据
6. 写 Undo Log
7. 写 Redo Log
8. 返回结果
```

**加锁情况（RR 级别）：**

| 场景 | 加锁类型 |
|------|----------|
| 唯一索引等值查询且命中 | 记录锁（Record Lock） |
| 唯一索引等值查询未命中 | 间隙锁（Gap Lock） |
| 非唯一索引等值查询 | 临键锁（Next-Key Lock） |
| 范围查询 | 临键锁（Next-Key Lock） |
| 无索引 | 全表扫描，所有行加行锁 + 间隙锁（等效锁全表） |

**示例：**
```sql
-- 有索引：id 是主键
UPDATE users SET name = '张三' WHERE id = 1;
-- 加记录锁：锁定 id = 1 的行

-- 无索引：name 没有索引
UPDATE users SET age = 20 WHERE name = '张三';
-- 全表扫描，所有行加 X 锁 + Gap 锁（性能极差）
```

**优化建议：**
- UPDATE 的条件列必须建立索引
- 避免无索引的 UPDATE 语句

---

### 15. 如何用 MySQL 实现分布式锁？

**参考答案：**

**方案一：基于唯一索引**
```sql
-- 创建锁表
CREATE TABLE distributed_lock (
    lock_key VARCHAR(64) PRIMARY KEY,
    lock_value VARCHAR(64),
    expire_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 获取锁（利用唯一索引）
INSERT INTO distributed_lock (lock_key, lock_value, expire_time) 
VALUES ('order_lock', 'uuid_123', DATE_ADD(NOW(), INTERVAL 30 SECOND));
-- 成功：获取锁成功
-- 失败（唯一键冲突）：获取锁失败

-- 释放锁
DELETE FROM distributed_lock WHERE lock_key = 'order_lock' AND lock_value = 'uuid_123';
```

**方案二：基于乐观锁（版本号）**
```sql
-- 获取锁
SELECT * FROM distributed_lock WHERE lock_key = 'order_lock';

-- 更新锁状态（利用版本号）
UPDATE distributed_lock 
SET lock_value = 'uuid_123', version = version + 1 
WHERE lock_key = 'order_lock' AND version = old_version;

-- 影响行数 = 1：获取锁成功
-- 影响行数 = 0：锁已被其他事务获取
```

**注意事项：**
1. **设置超时机制**：防止死锁
2. **添加重试逻辑**：获取锁失败时重试
3. **锁续期**：长任务需要延长锁的过期时间
4. **推荐方案**：生产环境推荐使用 Redis 或 Zookeeper 实现分布式锁

---

## 五、面试技巧与口诀

### 锁种类口诀
```
"记录锁锁点，间隙锁锁缝，临键锁锁点缝"
```

### RR 防幻读口诀
```
"MVCC 看旧照，临键锁堵新插"
```

### 避免死锁口诀
```
"顺序加锁、批量合并、索引覆盖"
```

### 高频考点速记

| 问题 | 关键词 |
|------|--------|
| 脏读解决 | MVCC、RC 及以上 |
| 不可重复读解决 | RR + MVCC（Read View 只生成一次） |
| 幻读解决 | RR + 临键锁（Next-Key Lock） |
| 行锁退化 | 无索引、全表扫描 |
| 间隙锁触发条件 | RR 级别 + 范围查询/非唯一索引 |
| 死锁排查 | `SHOW ENGINE INNODB STATUS` |

---

## 参考资料

1. [MySQL 锁机制：行锁、表锁、间隙锁、临键锁 - CSDN](https://blog.csdn.net/qq_41187124/article/details/151924244)
2. [MySQL 锁机制 15 连问 - CSDN](https://blog.csdn.net/qq_25385555/article/details/149711797)
3. [MySQL 事务的并发问题：脏读、不可重复读、幻读到底怎么解决？ - 掘金](https://juejin.cn/post/7563108340537786422)
4. [MySQL 事务隔离级别面试讲解 - PHP中文网](https://m.php.cn/faq/2168918.html)
5. [SQL 事务隔离面试题解析 - PHP中文网](https://m.php.cn/faq/2194191.html)
