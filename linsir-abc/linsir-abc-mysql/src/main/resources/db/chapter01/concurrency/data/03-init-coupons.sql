-- ============================================================
-- 优惠券数据初始化脚本
-- 用于演示秒杀、并发领取场景
-- ============================================================

-- 初始化优惠券数据
INSERT INTO coupons (coupon_code, coupon_name, total_quantity, remaining_quantity, 
                     discount_amount, min_order_amount, valid_start_time, valid_end_time, 
                     status, version) VALUES
('COUPON001', '满100减10', 1000, 1000, 10.00, 100.00, 
 DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 1, 0),
('COUPON002', '满200减30', 500, 500, 30.00, 200.00, 
 DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 1, 0),
('COUPON003', '8折优惠券', 100, 100, NULL, 50.00, 
 DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 1, 0),
('COUPON004', '限量秒杀券', 10, 10, 50.00, 200.00, 
 DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1, 0),
('COUPON005', '已过期优惠券', 100, 100, 20.00, 100.00, 
 DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 2, 0),
('COUPON006', '未开始优惠券', 100, 100, 15.00, 100.00, 
 DATE_ADD(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 0, 0);

-- 初始化用户优惠券数据（用于测试使用场景）
INSERT INTO user_coupons (user_id, coupon_id, status, grab_time) VALUES
(1, 1, 0, NOW()),  -- 用户1，优惠券1，未使用
(1, 2, 0, NOW()),  -- 用户1，优惠券2，未使用
(2, 1, 0, NOW()),  -- 用户2，优惠券1，未使用
(2, 2, 1, NOW()),  -- 用户2，优惠券2，已使用
(3, 1, 0, NOW());  -- 用户3，优惠券1，未使用
