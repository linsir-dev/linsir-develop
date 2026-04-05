-- 插入10条测试用户数据
-- 密码都是：123456（BCrypt加密后的值）

INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `phone`, `status`, `deleted`, `create_time`, `update_time`) VALUES
('user001', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户001', 'user001@test.com', '13800138001', 1, 0, NOW(), NOW()),
('user002', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户002', 'user002@test.com', '13800138002', 1, 0, NOW(), NOW()),
('user003', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户003', 'user003@test.com', '13800138003', 1, 0, NOW(), NOW()),
('user004', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户004', 'user004@test.com', '13800138004', 1, 0, NOW(), NOW()),
('user005', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户005', 'user005@test.com', '13800138005', 1, 0, NOW(), NOW()),
('user006', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户006', 'user006@test.com', '13800138006', 0, 0, NOW(), NOW()),
('user007', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户007', 'user007@test.com', '13800138007', 1, 0, NOW(), NOW()),
('user008', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户008', 'user008@test.com', '13800138008', 1, 0, NOW(), NOW()),
('user009', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户009', 'user009@test.com', '13800138009', 0, 0, NOW(), NOW()),
('user010', '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '用户010', 'user010@test.com', '13800138010', 1, 0, NOW(), NOW());
