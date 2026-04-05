-- ========================================================
-- 数据库初始化脚本
-- 数据库名称: linsir-spring-security-server
-- 描述: 用户、角色、权限管理系统
-- ========================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `linsir-spring-security-server`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `linsir-spring-security-server`;

-- ========================================================
-- 1. 用户表 (sys_user)
-- ========================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ========================================================
-- 2. 角色表 (sys_role)
-- ========================================================
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码（如：ROLE_ADMIN）',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ========================================================
-- 3. 权限表 (sys_permission)
-- ========================================================
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码（如：user:create）',
    `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `resource_type` VARCHAR(20) NOT NULL COMMENT '资源类型：menu-菜单，button-按钮，api-接口',
    `url` VARCHAR(255) DEFAULT NULL COMMENT '资源URL',
    `method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法：GET/POST/PUT/DELETE',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID，0表示顶级',
    `icon` VARCHAR(50) DEFAULT NULL COMMENT '图标',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_resource_type` (`resource_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ========================================================
-- 4. 用户角色关联表 (sys_user_role)
-- ========================================================
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ========================================================
-- 5. 角色权限关联表 (sys_role_permission)
-- ========================================================
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ========================================================
-- 初始化数据
-- ========================================================

-- 1. 初始化角色
INSERT INTO `sys_role` (`role_code`, `role_name`, `description`, `sort_order`) VALUES
('ROLE_ADMIN', '超级管理员', '拥有所有权限', 1),
('ROLE_USER', '普通用户', '拥有基本权限', 2),
('ROLE_TEST', '测试用户', '拥有测试权限', 3);

-- 2. 初始化权限（菜单）
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `resource_type`, `url`, `method`, `parent_id`, `icon`, `sort_order`) VALUES
-- 系统管理
('system:manage', '系统管理', 'menu', '/system', NULL, 0, 'icon-setting', 1),
('user:manage', '用户管理', 'menu', '/system/user', NULL, 1, 'icon-user', 1),
('role:manage', '角色管理', 'menu', '/system/role', NULL, 1, 'icon-role', 2),
('permission:manage', '权限管理', 'menu', '/system/permission', NULL, 1, 'icon-lock', 3),

-- 业务管理
('business:manage', '业务管理', 'menu', '/business', NULL, 0, 'icon-application', 2),
('order:manage', '订单管理', 'menu', '/business/order', NULL, 5, 'icon-order', 1),
('product:manage', '商品管理', 'menu', '/business/product', NULL, 5, 'icon-product', 2);

-- 3. 初始化权限（接口）
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `resource_type`, `url`, `method`, `parent_id`, `sort_order`) VALUES
-- 用户接口权限
('user:list', '用户列表', 'api', '/api/user/list', 'GET', 2, 1),
('user:create', '创建用户', 'api', '/api/user/create', 'POST', 2, 2),
('user:update', '更新用户', 'api', '/api/user/update', 'PUT', 2, 3),
('user:delete', '删除用户', 'api', '/api/user/delete', 'DELETE', 2, 4),

-- 角色接口权限
('role:list', '角色列表', 'api', '/api/role/list', 'GET', 3, 1),
('role:create', '创建角色', 'api', '/api/role/create', 'POST', 3, 2),
('role:update', '更新角色', 'api', '/api/role/update', 'PUT', 3, 3),
('role:delete', '删除角色', 'api', '/api/role/delete', 'DELETE', 3, 4),

-- 权限接口权限
('permission:list', '权限列表', 'api', '/api/permission/list', 'GET', 4, 1),
('permission:create', '创建权限', 'api', '/api/permission/create', 'POST', 4, 2),
('permission:update', '更新权限', 'api', '/api/permission/update', 'PUT', 4, 3),
('permission:delete', '删除权限', 'api', '/api/permission/delete', 'DELETE', 4, 4);

-- 4. 初始化管理员用户（密码：admin123）
-- 密码使用 BCrypt 加密：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `phone`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '超级管理员', 'admin@linsir.com', '13800138000', 1),
('user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '普通用户', 'user@linsir.com', '13800138001', 1),
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', '测试用户', 'test@linsir.com', '13800138002', 1);

-- 5. 初始化用户角色关联
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),  -- admin -> ROLE_ADMIN
(2, 2),  -- user -> ROLE_USER
(3, 3);  -- test -> ROLE_TEST

-- 6. 初始化角色权限关联（ROLE_ADMIN 拥有所有权限）
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`) VALUES
-- ROLE_ADMIN 拥有所有权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7),
(1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14),
(1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20),

-- ROLE_USER 拥有部分权限
(2, 5), (2, 6), (2, 7),  -- 业务管理菜单
(2, 8), (2, 9),          -- 用户查看权限

-- ROLE_TEST 拥有测试权限
(3, 5), (3, 6), (3, 7),  -- 业务管理菜单
(3, 8), (3, 9), (3, 10), (3, 11); -- 用户管理权限

-- ========================================================
-- 添加外键约束（可选，根据实际需求决定是否启用）
-- ========================================================

-- 用户角色关联表外键
-- ALTER TABLE `sys_user_role` ADD CONSTRAINT `fk_user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `sys_user_role` ADD CONSTRAINT `fk_user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE;

-- 角色权限关联表外键
-- ALTER TABLE `sys_role_permission` ADD CONSTRAINT `fk_role_permission_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `sys_role_permission` ADD CONSTRAINT `fk_role_permission_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE;
