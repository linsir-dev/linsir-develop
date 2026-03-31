-- ============================================================
-- 第一章 1.2 并发控制 - 测试数据库表结构初始化脚本
-- 用于H2内存数据库测试
-- ============================================================

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS transaction_logs;
DROP TABLE IF EXISTS user_coupons;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS accounts;

-- ============================================================
-- 1. 账户相关表
-- ============================================================

-- 账户表
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_no VARCHAR(32) NOT NULL UNIQUE,
    account_name VARCHAR(64) NOT NULL,
    balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    frozen_amount DECIMAL(19, 4) NOT NULL DEFAULT 0.0000,
    version INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 交易流水表
CREATE TABLE transaction_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_no VARCHAR(32) NOT NULL UNIQUE,
    account_id BIGINT NOT NULL,
    transaction_type TINYINT NOT NULL COMMENT '交易类型：1-充值，2-提现，3-转账入，4-转账出，5-冻结，6-解冻',
    amount DECIMAL(19, 4) NOT NULL,
    balance_before DECIMAL(19, 4) NOT NULL,
    balance_after DECIMAL(19, 4) NOT NULL,
    related_account_id BIGINT NULL,
    remark VARCHAR(256) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 2. 库存相关表
-- ============================================================

-- 库存表
CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id INT NOT NULL DEFAULT 1,
    available_stock INT NOT NULL DEFAULT 0,
    locked_stock INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    last_check_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_product_warehouse UNIQUE (product_id, warehouse_id)
);

-- ============================================================
-- 3. 优惠券相关表
-- ============================================================

-- 优惠券表
CREATE TABLE coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_code VARCHAR(32) NOT NULL UNIQUE,
    coupon_name VARCHAR(128) NOT NULL,
    total_quantity INT NOT NULL,
    remaining_quantity INT NOT NULL,
    discount_amount DECIMAL(19, 4) NULL,
    discount_percent DECIMAL(3, 2) NULL,
    min_order_amount DECIMAL(19, 4) NULL,
    valid_start_time TIMESTAMP NOT NULL,
    valid_end_time TIMESTAMP NOT NULL,
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-未开始，1-进行中，2-已结束',
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户优惠券表
CREATE TABLE user_coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未使用，1-已使用，2-已过期',
    use_time TIMESTAMP NULL,
    order_id BIGINT NULL,
    grab_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_coupon UNIQUE (user_id, coupon_id)
);
