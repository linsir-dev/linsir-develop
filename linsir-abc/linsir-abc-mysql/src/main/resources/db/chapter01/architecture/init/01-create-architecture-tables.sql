-- ========================================================
-- 架构演示相关表创建脚本
-- 文件名: 01-create-architecture-tables.sql
-- 位置: db/chapter01/architecture/init/
-- 说明: 创建订单、商品、订单明细、连接会话等表
-- 依赖: 需要先执行 db/common/init/ 下的脚本
-- ========================================================

USE `linsir-abc-mysql`;

-- ========================================================
-- 1. 商品表 (products)
-- ========================================================
CREATE TABLE IF NOT EXISTS products (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '商品编码',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    category_id INT UNSIGNED COMMENT '分类ID',
    price DECIMAL(18, 2) NOT NULL COMMENT '售价',
    cost_price DECIMAL(18, 2) COMMENT '成本价',
    stock INT UNSIGNED DEFAULT 0 COMMENT '库存数量',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    description TEXT COMMENT '商品描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 索引
    INDEX idx_product_code (product_code),
    INDEX idx_category (category_id),
    INDEX idx_status (status),
    INDEX idx_price (price),
    FULLTEXT INDEX idx_name (product_name)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ========================================================
-- 2. 订单表 (orders)
-- ========================================================
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(18, 2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(18, 2) DEFAULT 0.00 COMMENT '优惠金额',
    pay_amount DECIMAL(18, 2) NOT NULL COMMENT '实付金额',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消',
    pay_type TINYINT COMMENT '支付方式：1-支付宝，2-微信，3-银行卡',
    pay_time DATETIME COMMENT '支付时间',
    ship_time DATETIME COMMENT '发货时间',
    complete_time DATETIME COMMENT '完成时间',
    remark VARCHAR(500) COMMENT '订单备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 索引
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_user_status (user_id, status),

    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ========================================================
-- 3. 订单明细表 (order_items)
-- ========================================================
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_price DECIMAL(18, 2) NOT NULL COMMENT '商品单价',
    quantity INT UNSIGNED NOT NULL COMMENT '数量',
    subtotal DECIMAL(18, 2) NOT NULL COMMENT '小计金额',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 索引
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),

    -- 外键约束
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ========================================================
-- 4. 连接会话表 (connection_sessions)
-- ========================================================
CREATE TABLE IF NOT EXISTS connection_sessions (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    session_id VARCHAR(64) NOT NULL UNIQUE COMMENT '会话标识',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    client_host VARCHAR(100) COMMENT '客户端主机',
    client_port INT COMMENT '客户端端口',
    server_host VARCHAR(100) COMMENT '服务器主机',
    database_name VARCHAR(50) COMMENT '当前数据库',
    connection_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '连接时间',
    last_active_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后活跃时间',
    status TINYINT DEFAULT 1 COMMENT '状态：0-断开，1-活跃，2-空闲',
    command_count INT DEFAULT 0 COMMENT '命令执行次数',
    total_execute_time BIGINT DEFAULT 0 COMMENT '总执行时间(ms)',

    -- 索引
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_connection_time (connection_time),

    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='连接会话表';

-- ========================================================
-- 5. 操作日志表 (operation_logs)
-- ========================================================
CREATE TABLE IF NOT EXISTS operation_logs (
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT UNSIGNED COMMENT '操作用户ID',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_desc VARCHAR(500) COMMENT '操作描述',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_data TEXT COMMENT '响应数据',
    execute_time INT COMMENT '执行时间(ms)',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    status TINYINT DEFAULT 1 COMMENT '状态：0-失败，1-成功',
    error_msg TEXT COMMENT '错误信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at),
    INDEX idx_status (status),

    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 验证表创建
SHOW TABLES;
