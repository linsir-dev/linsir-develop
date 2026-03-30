-- ========================================================
-- 商品数据初始化脚本
-- 文件名: 02-init-products.sql
-- 位置: db/chapter01/architecture/data/
-- 说明: 初始化商品数据
-- 依赖: 需要先执行 init 目录下的建表脚本
-- ========================================================

USE `linsir-abc-mysql`;

-- 清空现有数据（可选，开发环境使用）
-- TRUNCATE TABLE products;

-- 初始化商品数据
INSERT INTO products (product_code, product_name, category_id, price, cost_price, stock, status) VALUES
('P001', 'iPhone 15 Pro', 1, 7999.00, 6500.00, 100, 1),
('P002', 'MacBook Pro 14', 2, 14999.00, 12000.00, 50, 1),
('P003', 'AirPods Pro 2', 3, 1899.00, 1200.00, 200, 1),
('P004', 'iPad Air 5', 4, 4799.00, 3800.00, 80, 1),
('P005', 'Apple Watch S9', 5, 2999.00, 2200.00, 150, 1),
('P006', '小米14 Pro', 1, 4999.00, 3800.00, 120, 1),
('P007', '华为Mate 60 Pro', 1, 6999.00, 5200.00, 80, 1),
('P008', '戴尔XPS 13', 2, 8999.00, 7000.00, 30, 1);

-- 验证数据插入
SELECT id, product_code, product_name, price, stock, status FROM products;
