-- ============================================================
-- 第一章 1.1 MySQL逻辑架构 - 测试数据库表结构初始化脚本
-- 用于H2内存数据库测试
-- ============================================================

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS connection_sessions;
DROP TABLE IF EXISTS users;

-- ============================================================
-- 1. 用户相关表
-- ============================================================

-- 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    status TINYINT DEFAULT 1,
    role VARCHAR(20) DEFAULT 'USER',
    last_login_time TIMESTAMP NULL,
    last_login_ip VARCHAR(50),
    login_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建连接会话表
CREATE TABLE connection_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    client_host VARCHAR(50),
    client_port INT,
    server_host VARCHAR(50),
    database_name VARCHAR(50),
    connection_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_active_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TINYINT DEFAULT 1,
    command_count INT DEFAULT 0,
    total_execute_time BIGINT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ============================================================
-- 2. 产品订单相关表
-- ============================================================

-- 创建产品表
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建订单表
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 创建订单项表
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
