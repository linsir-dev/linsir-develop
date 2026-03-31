-- ============================================================
-- 并发控制示例数据库脚本
-- 演示MySQL并发控制机制：悲观锁、乐观锁、事务隔离级别
-- ============================================================

-- 账户表
-- 用于演示转账、充值等并发场景
-- 支持乐观锁（version字段）和悲观锁（FOR UPDATE）
DROP TABLE IF EXISTS transaction_logs;
DROP TABLE IF EXISTS user_coupons;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账户ID',
    account_no VARCHAR(32) NOT NULL UNIQUE COMMENT '账户编号',
    account_name VARCHAR(64) NOT NULL COMMENT '账户名称',
    balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000 COMMENT '账户余额',
    frozen_amount DECIMAL(19, 4) NOT NULL DEFAULT 0.0000 COMMENT '冻结金额',
    version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-冻结，1-正常',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_account_no (account_no),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';

-- 库存表
-- 用于演示库存扣减、秒杀等并发场景
-- 支持乐观锁（version字段）和悲观锁（FOR UPDATE）
CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    warehouse_id INT NOT NULL DEFAULT 1 COMMENT '仓库ID',
    available_stock INT NOT NULL DEFAULT 0 COMMENT '可用库存',
    locked_stock INT NOT NULL DEFAULT 0 COMMENT '锁定库存',
    version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    last_check_time TIMESTAMP NULL COMMENT '最后盘点时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_product_warehouse (product_id, warehouse_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 优惠券表
-- 用于演示秒杀、并发领取场景
-- 支持乐观锁（version字段）和悲观锁（FOR UPDATE）
CREATE TABLE coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '优惠券ID',
    coupon_code VARCHAR(32) NOT NULL UNIQUE COMMENT '优惠券编码',
    coupon_name VARCHAR(128) NOT NULL COMMENT '优惠券名称',
    total_quantity INT NOT NULL COMMENT '总数量',
    remaining_quantity INT NOT NULL COMMENT '剩余数量',
    discount_amount DECIMAL(19, 4) NULL COMMENT '优惠金额（与折扣比例互斥）',
    discount_percent DECIMAL(3, 2) NULL COMMENT '折扣比例（与优惠金额互斥）',
    min_order_amount DECIMAL(19, 4) NULL COMMENT '最低订单金额',
    valid_start_time TIMESTAMP NOT NULL COMMENT '有效期开始',
    valid_end_time TIMESTAMP NOT NULL COMMENT '有效期结束',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-未开始，1-进行中，2-已结束',
    version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_valid_time (valid_start_time, valid_end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 用户优惠券表
-- 记录用户领取的优惠券信息
CREATE TABLE user_coupons (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未使用，1-已使用，2-已过期',
    use_time TIMESTAMP NULL COMMENT '使用时间',
    order_id BIGINT NULL COMMENT '使用订单ID',
    grab_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_coupon (user_id, coupon_id),
    INDEX idx_user_id (user_id),
    INDEX idx_coupon_id (coupon_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 交易流水表
-- 记录所有资金变动历史，支持审计和追溯
CREATE TABLE transaction_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流水ID',
    transaction_no VARCHAR(32) NOT NULL UNIQUE COMMENT '交易流水号',
    account_id BIGINT NOT NULL COMMENT '账户ID',
    transaction_type TINYINT NOT NULL COMMENT '交易类型：1-充值，2-提现，3-转账入，4-转账出，5-冻结，6-解冻',
    amount DECIMAL(19, 4) NOT NULL COMMENT '交易金额（正数增加，负数减少）',
    balance_before DECIMAL(19, 4) NOT NULL COMMENT '交易前余额',
    balance_after DECIMAL(19, 4) NOT NULL COMMENT '交易后余额',
    related_account_id BIGINT NULL COMMENT '对方账户ID',
    remark VARCHAR(256) NULL COMMENT '备注',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_account_id (account_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水表';
