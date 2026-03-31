-- ============================================================
-- 锁机制测试脚本
-- 演示各种锁的使用场景和效果
-- ============================================================

-- ============================================================
-- 测试1：悲观锁（FOR UPDATE）测试
-- 场景：转账操作，防止并发修改同一账户
-- ============================================================

-- 会话1：开启事务并锁定账户
-- START TRANSACTION;
-- SELECT * FROM accounts WHERE id = 1 FOR UPDATE;
-- 此时账户1被锁定，其他会话无法修改

-- 会话2：尝试修改同一账户（会等待直到会话1提交或回滚）
-- UPDATE accounts SET balance = balance - 100 WHERE id = 1;

-- 会话1：提交事务释放锁
-- COMMIT;

-- ============================================================
-- 测试2：共享锁（LOCK IN SHARE MODE）测试
-- 场景：库存盘点，允许多个会话同时读取，但阻塞写入
-- ============================================================

-- 会话1：开启事务并加共享锁
-- START TRANSACTION;
-- SELECT * FROM inventory WHERE id = 1 LOCK IN SHARE MODE;
-- 可以读取，但不能修改

-- 会话2：也可以加共享锁读取
-- SELECT * FROM inventory WHERE id = 1 LOCK IN SHARE MODE;

-- 会话2：尝试修改会被阻塞
-- UPDATE inventory SET available_stock = 900 WHERE id = 1;

-- 会话1：提交后会话2才能执行更新
-- COMMIT;

-- ============================================================
-- 测试3：乐观锁测试
-- 场景：高并发下使用版本号控制并发
-- ============================================================

-- 初始状态：version = 0
-- SELECT id, balance, version FROM accounts WHERE id = 1;
-- 结果：id=1, balance=10000, version=0

-- 会话1和会话2同时读取同一记录
-- 会话1：UPDATE accounts SET balance = 9000, version = 1 
--        WHERE id = 1 AND version = 0;  -- 成功

-- 会话2：UPDATE accounts SET balance = 9000, version = 1 
--        WHERE id = 1 AND version = 0;  -- 失败，影响行数为0

-- ============================================================
-- 测试4：行锁与表锁对比
-- ============================================================

-- 行锁（InnoDB默认）：只锁定被操作的行
-- UPDATE accounts SET balance = 9000 WHERE id = 1;
-- 其他行可以正常操作

-- 表锁（显式锁定）：锁定整张表
-- LOCK TABLES accounts WRITE;
-- 其他会话无法读写该表
-- UNLOCK TABLES;

-- ============================================================
-- 测试5：间隙锁（Gap Lock）测试
-- 场景：防止幻读
-- ============================================================

-- 开启可重复读事务
-- START TRANSACTION;
-- SELECT * FROM accounts WHERE id BETWEEN 1 AND 5 FOR UPDATE;
-- 不仅锁定存在的记录，还锁定id在1-5之间的间隙

-- 其他会话无法插入id在1-5之间的新记录
-- INSERT INTO accounts (id, account_no, account_name) VALUES (3, 'NEW', '新账户'); -- 被阻塞

-- COMMIT;

-- ============================================================
-- 测试6：死锁检测
-- ============================================================

-- 会话1：
-- START TRANSACTION;
-- SELECT * FROM accounts WHERE id = 1 FOR UPDATE;
-- -- 等待一会儿
-- SELECT * FROM accounts WHERE id = 2 FOR UPDATE; -- 可能被阻塞或检测到死锁

-- 会话2（同时）：
-- START TRANSACTION;
-- SELECT * FROM accounts WHERE id = 2 FOR UPDATE;
-- -- 等待一会儿
-- SELECT * FROM accounts WHERE id = 1 FOR UPDATE; -- 可能被阻塞或检测到死锁

-- MySQL会自动检测死锁并回滚其中一个事务
