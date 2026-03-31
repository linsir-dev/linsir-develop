-- ============================================================
-- 事务隔离级别测试脚本
-- 演示不同隔离级别下的并发问题
-- ============================================================

-- ============================================================
-- 准备工作：查看当前隔离级别
-- ============================================================
SELECT @@transaction_isolation;
-- MySQL默认：REPEATABLE-READ

-- ============================================================
-- 测试1：脏读（Dirty Read）测试
-- 隔离级别：READ UNCOMMITTED 会出现，READ COMMITTED及以上不会
-- ============================================================

-- 设置隔离级别为读未提交
-- SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;

-- 会话1：
-- START TRANSACTION;
-- UPDATE accounts SET balance = 5000 WHERE id = 1;  -- 修改但不提交

-- 会话2（READ UNCOMMITTED）：
-- SELECT balance FROM accounts WHERE id = 1;
-- 结果：5000（读取到了未提交的数据 - 脏读）

-- 会话1：
-- ROLLBACK;  -- 回滚修改

-- 会话2再次查询：
-- SELECT balance FROM accounts WHERE id = 1;
-- 结果：10000（数据变回原值）

-- ============================================================
-- 测试2：不可重复读（Non-Repeatable Read）测试
-- 隔离级别：READ COMMITTED会出现，REPEATABLE READ及以上不会
-- ============================================================

-- 设置隔离级别为读已提交
-- SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- 会话1：
-- START TRANSACTION;
-- SELECT balance FROM accounts WHERE id = 1;
-- 结果：10000

-- 会话2：
-- START TRANSACTION;
-- UPDATE accounts SET balance = 8000 WHERE id = 1;
-- COMMIT;

-- 会话1（同一事务中再次查询）：
-- SELECT balance FROM accounts WHERE id = 1;
-- 结果：8000（同一事务中两次读取结果不同 - 不可重复读）

-- 会话1：
-- COMMIT;

-- ============================================================
-- 测试3：幻读（Phantom Read）测试
-- 隔离级别：REPEATABLE READ在MySQL中通过MVCC和间隙锁基本解决
-- ============================================================

-- 设置隔离级别为可重复读
-- SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;

-- 会话1：
-- START TRANSACTION;
-- SELECT * FROM accounts WHERE balance > 5000;
-- 假设返回3条记录

-- 会话2：
-- START TRANSACTION;
-- INSERT INTO accounts (account_no, account_name, balance) 
-- VALUES ('NEW001', '新账户', 8000);
-- COMMIT;

-- 会话1（同一事务中再次查询）：
-- SELECT * FROM accounts WHERE balance > 5000;
-- MySQL结果：仍然是3条（通过MVCC实现可重复读）
-- 其他数据库可能返回4条（幻读）

-- 会话1：
-- COMMIT;

-- ============================================================
-- 测试4：串行化（Serializable）测试
-- 最严格的隔离级别，完全避免所有并发问题
-- ============================================================

-- 设置隔离级别为串行化
-- SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- 会话1：
-- START TRANSACTION;
-- SELECT * FROM accounts WHERE balance > 5000;

-- 会话2：
-- INSERT INTO accounts (account_no, account_name, balance) 
-- VALUES ('NEW002', '新账户2', 8000);
-- 结果：被阻塞，直到会话1提交

-- 会话1：
-- COMMIT;

-- 会话2：
-- 现在可以执行插入

-- ============================================================
-- 测试5：丢失更新（Lost Update）测试
-- ============================================================

-- 初始：balance = 10000

-- 会话1：
-- START TRANSACTION;
-- SELECT balance FROM accounts WHERE id = 1;  -- 10000

-- 会话2：
-- START TRANSACTION;
-- SELECT balance FROM accounts WHERE id = 1;  -- 10000

-- 会话1：
-- UPDATE accounts SET balance = 10000 + 1000 WHERE id = 1;  -- 11000
-- COMMIT;

-- 会话2（基于旧值计算）：
-- UPDATE accounts SET balance = 10000 + 2000 WHERE id = 1;  -- 12000（覆盖了会话1的更新）
-- COMMIT;

-- 最终结果：12000（期望是13000，丢失了会话1的更新）

-- 解决方案1：使用乐观锁
-- UPDATE accounts SET balance = balance + 1000, version = version + 1 
-- WHERE id = 1 AND version = 0;

-- 解决方案2：使用悲观锁
-- SELECT * FROM accounts WHERE id = 1 FOR UPDATE;
-- UPDATE accounts SET balance = balance + 1000 WHERE id = 1;

-- ============================================================
-- 测试6：MVCC版本链查看（通过undo log）
-- ============================================================

-- 查看InnoDB事务状态
SELECT 
    trx_id,
    trx_state,
    trx_started,
    trx_tables_locked,
    trx_rows_locked
FROM information_schema.INNODB_TRX;

-- 查看锁等待
SELECT 
    waiting_trx_id,
    waiting_pid,
    blocking_trx_id,
    blocking_pid
FROM information_schema.INNODB_LOCK_WAITS;

-- 查看当前锁
SELECT 
    lock_id,
    lock_trx_id,
    lock_mode,
    lock_type,
    lock_table,
    lock_index
FROM information_schema.INNODB_LOCKS;
