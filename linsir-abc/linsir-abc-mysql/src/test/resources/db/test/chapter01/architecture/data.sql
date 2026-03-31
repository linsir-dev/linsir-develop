-- ============================================================
-- 第一章 1.1 MySQL逻辑架构 - 测试数据库数据初始化脚本
-- 用于H2内存数据库测试
-- ============================================================

-- ============================================================
-- 1. 用户数据
-- ============================================================
INSERT INTO users (username, password, email, phone, status, role, login_count, created_at, updated_at) VALUES
('zhangsan', 'a8f5f167f44f4964e6c998dee827110c9a0c5e1e7a5b6e5f9d7c7e8f9a0b1c2d', 'zhangsan@test.com', '13800138001', 1, 'USER', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('lisi', 'a8f5f167f44f4964e6c998dee827110c9a0c5e1e7a5b6e5f9d7c7e8f9a0b1c2d', 'lisi@test.com', '13800138002', 1, 'USER', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin', 'a8f5f167f44f4964e6c998dee827110c9a0c5e1e7a5b6e5f9d7c7e8f9a0b1c2d', 'admin@test.com', '13800138003', 1, 'ADMIN', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('disabled_user', 'a8f5f167f44f4964e6c998dee827110c9a0c5e1e7a5b6e5f9d7c7e8f9a0b1c2d', 'disabled@test.com', '13800138004', 0, 'USER', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- 2. 产品数据
-- ============================================================
INSERT INTO products (name, description, price, stock, status, created_at, updated_at) VALUES
('iPhone 15', 'Apple iPhone 15 128GB', 5999.00, 100, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MacBook Pro', 'Apple MacBook Pro 14英寸', 14999.00, 50, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AirPods Pro', 'Apple AirPods Pro 2代', 1999.00, 200, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('iPad Air', 'Apple iPad Air 5代', 4799.00, 80, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- 3. 订单数据
-- ============================================================
INSERT INTO orders (order_no, user_id, total_amount, status, created_at, updated_at) VALUES
('ORD202403010001', 1, 5999.00, 'PAID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ORD202403010002', 1, 14999.00, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ORD202403010003', 2, 1999.00, 'PAID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ORD202403010004', 3, 4799.00, 'SHIPPED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================================
-- 4. 订单项数据
-- ============================================================
INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) VALUES
(1, 1, 1, 5999.00, 5999.00),
(2, 2, 1, 14999.00, 14999.00),
(3, 3, 1, 1999.00, 1999.00),
(4, 4, 1, 4799.00, 4799.00);

-- ============================================================
-- 5. 连接会话数据
-- ============================================================
INSERT INTO connection_sessions (session_id, user_id, client_host, client_port, server_host, database_name, status, command_count) VALUES
('sess_001', 1, '192.168.1.100', 54321, 'localhost', 'test_db', 1, 10),
('sess_002', 2, '192.168.1.101', 54322, 'localhost', 'test_db', 1, 5),
('sess_003', 1, '192.168.1.100', 54323, 'localhost', 'test_db', 0, 20);
