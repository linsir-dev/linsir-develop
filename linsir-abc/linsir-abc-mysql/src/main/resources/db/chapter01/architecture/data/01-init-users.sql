-- ========================================================
-- 用户数据初始化脚本
-- 文件名: 01-init-users.sql
-- 位置: db/chapter01/architecture/data/
-- 说明: 初始化用户数据，用于认证授权演示
-- 依赖: 需要先执行 init 目录下的建表脚本
-- ========================================================

USE `linsir-abc-mysql`;

-- 清空现有数据（可选，开发环境使用）
-- TRUNCATE TABLE users;

-- 初始化用户数据
-- 密码使用BCrypt加密，明文密码为: password
INSERT INTO users (username, password, email, phone, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'admin@example.com', '13800138000', 'ADMIN', 1),
('zhangsan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'zhangsan@example.com', '13800138001', 'USER', 1),
('lisi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'lisi@example.com', '13800138002', 'USER', 1),
('wangwu', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'wangwu@example.com', '13800138003', 'USER', 1),
('guest', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'guest@example.com', '13800138004', 'GUEST', 1);

-- 验证数据插入
SELECT id, username, email, phone, role, status, created_at FROM users;
