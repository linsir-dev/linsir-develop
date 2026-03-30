-- 测试数据库数据初始化脚本
-- 用于H2内存数据库测试

-- 插入测试用户数据
INSERT INTO users (username, password, email, phone, status, role, login_count, created_at, updated_at) VALUES
('zhangsan', 'a8f5f167f44f4964e6c998dee827110c9a0c5e1e7a5b6e5f9d7c7e8f9a0b1c2d', 'zhangsan@test.com', '13800138001', 1, 'USER', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('lisi', 'a8f5f167f44f4964e6c998dee827110c9a0c5e1e7a5b6e5f9d7c7e8f9a0b1c2d', 'lisi@test.com', '13800138002', 1, 'USER', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin', 'a8f5f167f44f4964e6c998dee827110c9a0c5e1e7a5b6e5f9d7c7e8f9a0b1c2d', 'admin@test.com', '13800138003', 1, 'ADMIN', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('disabled_user', 'a8f5f167f44f4964e6c998dee827110c9a0c5e1e7a5b6e5f9d7c7e8f9a0b1c2d', 'disabled@test.com', '13800138004', 0, 'USER', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 插入测试产品数据
INSERT INTO products (name, description, price, stock, status, created_at, updated_at) VALUES
('iPhone 15', 'Apple iPhone 15 128GB', 5999.00, 100, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('MacBook Pro', 'Apple MacBook Pro 14英寸', 14999.00, 50, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('AirPods Pro', 'Apple AirPods Pro 2代', 1999.00, 200, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('iPad Air', 'Apple iPad Air 5代', 4799.00, 80, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
