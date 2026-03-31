-- ============================================================
-- 第一章 1.2 并发控制 - 测试数据库数据初始化脚本
-- 用于H2内存数据库测试
-- ============================================================

-- ============================================================
-- 1. 账户数据
-- ============================================================
INSERT INTO accounts (account_no, account_name, balance, frozen_amount, version, status) VALUES
('ACC001', '测试账户A', 10000.0000, 0.0000, 0, 1),
('ACC002', '测试账户B', 10000.0000, 0.0000, 0, 1),
('ACC003', '测试账户C', 5000.0000, 0.0000, 0, 1),
('ACC004', '测试账户D', 5000.0000, 0.0000, 0, 1),
('ACC005', '冻结账户', 10000.0000, 0.0000, 0, 0);

-- ============================================================
-- 2. 交易流水数据
-- ============================================================
INSERT INTO transaction_logs (transaction_no, account_id, transaction_type, amount, balance_before, balance_after, related_account_id, remark) VALUES
('TXN202403010001', 1, 1, 1000.0000, 0.0000, 1000.0000, NULL, '初始充值'),
('TXN202403010002', 1, 1, 9000.0000, 1000.0000, 10000.0000, NULL, '充值'),
('TXN202403010003', 2, 1, 10000.0000, 0.0000, 10000.0000, NULL, '初始充值'),
('TXN202403010004', 1, 4, -1000.0000, 10000.0000, 9000.0000, 2, '转账给账户B'),
('TXN202403010005', 2, 3, 1000.0000, 10000.0000, 11000.0000, 1, '接收账户A转账'),
('TXN202403010006', 3, 1, 5000.0000, 0.0000, 5000.0000, NULL, '初始充值');

-- ============================================================
-- 3. 库存数据
-- ============================================================
INSERT INTO inventory (product_id, warehouse_id, available_stock, locked_stock, version) VALUES
(1001, 1, 1000, 0, 0),
(1002, 1, 500, 0, 0),
(1003, 1, 100, 0, 0),
(1004, 1, 50, 0, 0),
(1001, 2, 500, 0, 0);

-- ============================================================
-- 4. 优惠券数据
-- ============================================================
INSERT INTO coupons (coupon_code, coupon_name, total_quantity, remaining_quantity, discount_amount, min_order_amount, valid_start_time, valid_end_time, status, version) VALUES
('COUPON001', '满100减10', 1000, 1000, 10.00, 100.00, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', 30, CURRENT_TIMESTAMP), 1, 0),
('COUPON002', '满200减30', 500, 500, 30.00, 200.00, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', 30, CURRENT_TIMESTAMP), 1, 0),
('COUPON003', '8折优惠券', 100, 100, NULL, 50.00, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', 7, CURRENT_TIMESTAMP), 1, 0),
('COUPON004', '限量秒杀券', 10, 10, 50.00, 200.00, DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', 1, CURRENT_TIMESTAMP), 1, 0),
('COUPON005', '已过期优惠券', 100, 100, 20.00, 100.00, DATEADD('DAY', -30, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP), 2, 0),
('COUPON006', '未开始优惠券', 100, 100, 15.00, 100.00, DATEADD('DAY', 1, CURRENT_TIMESTAMP), DATEADD('DAY', 30, CURRENT_TIMESTAMP), 0, 0);

-- ============================================================
-- 5. 用户优惠券数据
-- ============================================================
INSERT INTO user_coupons (user_id, coupon_id, status, grab_time) VALUES
(1, 1, 0, CURRENT_TIMESTAMP),
(1, 2, 0, CURRENT_TIMESTAMP),
(2, 1, 0, CURRENT_TIMESTAMP),
(2, 2, 1, CURRENT_TIMESTAMP),
(3, 1, 0, CURRENT_TIMESTAMP);
