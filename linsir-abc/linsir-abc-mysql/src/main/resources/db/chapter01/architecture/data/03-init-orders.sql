-- ========================================================
-- 订单数据初始化脚本
-- 文件名: 03-init-orders.sql
-- 位置: db/chapter01/architecture/data/
-- 说明: 初始化订单和订单明细数据
-- 依赖: 需要先执行 01-init-users.sql 和 02-init-products.sql
-- ========================================================

USE `linsir-abc-mysql`;

-- 清空现有数据（可选，开发环境使用）
-- SET FOREIGN_KEY_CHECKS = 0;
-- TRUNCATE TABLE order_items;
-- TRUNCATE TABLE orders;
-- SET FOREIGN_KEY_CHECKS = 1;

-- ========================================================
-- 1. 初始化订单数据
-- ========================================================
INSERT INTO orders (order_no, user_id, total_amount, discount_amount, pay_amount, status, pay_type, pay_time, remark) VALUES
('ORD202403300001', 2, 7999.00, 0.00, 7999.00, 3, 1, '2024-03-30 10:30:00', 'iPhone 15 Pro订单'),
('ORD202403300002', 2, 1899.00, 100.00, 1799.00, 3, 2, '2024-03-30 11:15:00', 'AirPods Pro 2订单'),
('ORD202403300003', 3, 14999.00, 500.00, 14499.00, 2, 1, '2024-03-30 14:20:00', 'MacBook Pro订单'),
('ORD202403300004', 3, 4799.00, 0.00, 4799.00, 1, NULL, NULL, 'iPad Air订单'),
('ORD202403300005', 4, 2999.00, 0.00, 2999.00, 3, 2, '2024-03-30 16:45:00', 'Apple Watch订单');

-- ========================================================
-- 2. 初始化订单明细数据
-- ========================================================
INSERT INTO order_items (order_id, product_id, product_name, product_price, quantity, subtotal) VALUES
(1, 1, 'iPhone 15 Pro', 7999.00, 1, 7999.00),
(2, 3, 'AirPods Pro 2', 1899.00, 1, 1899.00),
(3, 2, 'MacBook Pro 14', 14999.00, 1, 14999.00),
(4, 4, 'iPad Air 5', 4799.00, 1, 4799.00),
(5, 5, 'Apple Watch S9', 2999.00, 1, 2999.00);

-- 验证数据插入
SELECT o.id, o.order_no, o.user_id, o.total_amount, o.status, oi.product_name, oi.quantity
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
ORDER BY o.id;
