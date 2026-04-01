-- ========================================================
-- MySQL事务演示数据库脚本
-- 基于《高性能MySQL》第3版 1.3 事务章节
-- ========================================================

-- 删除已存在的表（按依赖关系倒序）
DROP TABLE IF EXISTS point_transaction_logs;
DROP TABLE IF EXISTS point_exchange_records;
DROP TABLE IF EXISTS product_inventory;
DROP TABLE IF EXISTS exchange_products;
DROP TABLE IF EXISTS point_accounts;
DROP TABLE IF EXISTS bank_transaction_logs;
DROP TABLE IF EXISTS transfer_records;
DROP TABLE IF EXISTS bank_accounts;

-- ========================================================
-- 1. 银行账户表
-- 用于演示事务的ACID特性
-- ========================================================
CREATE TABLE bank_accounts (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    account_no VARCHAR(32) NOT NULL COMMENT '账户编号',
    account_name VARCHAR(64) NOT NULL COMMENT '账户名称',
    balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
    frozen_amount DECIMAL(18, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-冻结，1-正常',
    version INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_account_no (account_no),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='银行账户表';

-- ========================================================
-- 2. 转账记录表
-- 记录转账交易，用于事务审计和追踪
-- ========================================================
CREATE TABLE transfer_records (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    transfer_no VARCHAR(32) NOT NULL COMMENT '转账单号',
    from_account_id BIGINT UNSIGNED NOT NULL COMMENT '转出账户ID',
    to_account_id BIGINT UNSIGNED NOT NULL COMMENT '转入账户ID',
    amount DECIMAL(18, 2) NOT NULL COMMENT '转账金额',
    fee DECIMAL(18, 2) NOT NULL DEFAULT 0.00 COMMENT '手续费',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-处理中，1-成功，2-失败',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    completed_at DATETIME DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_transfer_no (transfer_no),
    KEY idx_from_account (from_account_id),
    KEY idx_to_account (to_account_id),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='转账记录表';

-- ========================================================
-- 3. 银行交易流水表
-- 记录账户的所有交易明细，用于事务日志和持久性演示
-- ========================================================
CREATE TABLE bank_transaction_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    transaction_no VARCHAR(32) NOT NULL COMMENT '交易流水号',
    account_id BIGINT UNSIGNED NOT NULL COMMENT '账户ID',
    transaction_type TINYINT NOT NULL COMMENT '交易类型：1-转入，2-转出',
    amount DECIMAL(18, 2) NOT NULL COMMENT '交易金额（正数增加，负数减少）',
    balance_before DECIMAL(18, 2) NOT NULL COMMENT '交易前余额',
    balance_after DECIMAL(18, 2) NOT NULL COMMENT '交易后余额',
    related_account_id BIGINT UNSIGNED DEFAULT NULL COMMENT '对方账户ID',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_transaction_no (transaction_no),
    KEY idx_account_id (account_id),
    KEY idx_transaction_type (transaction_type),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='银行交易流水表';

-- ========================================================
-- 4. 积分账户表
-- 用于演示事务的原子性和持久性
-- ========================================================
CREATE TABLE point_accounts (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    available_points BIGINT NOT NULL DEFAULT 0 COMMENT '可用积分',
    frozen_points BIGINT NOT NULL DEFAULT 0 COMMENT '冻结积分',
    total_earned BIGINT NOT NULL DEFAULT 0 COMMENT '累计获得积分',
    total_consumed BIGINT NOT NULL DEFAULT 0 COMMENT '累计消费积分',
    version INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_available_points (available_points)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分账户表';

-- ========================================================
-- 5. 积分交易流水表
-- 记录积分变动明细
-- ========================================================
CREATE TABLE point_transaction_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    transaction_no VARCHAR(32) NOT NULL COMMENT '交易流水号',
    point_account_id BIGINT UNSIGNED NOT NULL COMMENT '积分账户ID',
    transaction_type TINYINT NOT NULL COMMENT '交易类型：1-获得，2-消费',
    points BIGINT NOT NULL COMMENT '积分数量（正数增加，负数减少）',
    balance_before BIGINT NOT NULL COMMENT '交易前积分',
    balance_after BIGINT NOT NULL COMMENT '交易后积分',
    source_type VARCHAR(32) DEFAULT NULL COMMENT '来源类型：EXCHANGE-兑换',
    source_id BIGINT UNSIGNED DEFAULT NULL COMMENT '来源ID',
    remark VARCHAR(256) DEFAULT NULL COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_transaction_no (transaction_no),
    KEY idx_point_account_id (point_account_id),
    KEY idx_transaction_type (transaction_type),
    KEY idx_source (source_type, source_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分交易流水表';

-- ========================================================
-- 6. 兑换商品表
-- 积分商城商品信息
-- ========================================================
CREATE TABLE exchange_products (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    product_code VARCHAR(32) NOT NULL COMMENT '商品编码',
    product_name VARCHAR(128) NOT NULL COMMENT '商品名称',
    description VARCHAR(512) DEFAULT NULL COMMENT '商品描述',
    required_points BIGINT NOT NULL COMMENT '所需积分',
    price DECIMAL(10, 2) DEFAULT NULL COMMENT '商品参考价格',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (product_code),
    KEY idx_status (status),
    KEY idx_required_points (required_points)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='兑换商品表';

-- ========================================================
-- 7. 商品库存表
-- 用于演示并发控制和库存扣减
-- ========================================================
CREATE TABLE product_inventory (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    available_stock INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '可用库存',
    locked_stock INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '锁定库存',
    version INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_id (product_id),
    KEY idx_available_stock (available_stock)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品库存表';

-- ========================================================
-- 8. 积分兑换记录表
-- 记录积分兑换历史
-- ========================================================
CREATE TABLE point_exchange_records (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '主键ID',
    exchange_no VARCHAR(32) NOT NULL COMMENT '兑换单号',
    point_account_id BIGINT UNSIGNED NOT NULL COMMENT '积分账户ID',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    quantity INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '兑换数量',
    total_points BIGINT NOT NULL COMMENT '消耗总积分',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-处理中，1-成功，2-失败，3-已取消',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    completed_at DATETIME DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_exchange_no (exchange_no),
    KEY idx_point_account_id (point_account_id),
    KEY idx_product_id (product_id),
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分兑换记录表';
