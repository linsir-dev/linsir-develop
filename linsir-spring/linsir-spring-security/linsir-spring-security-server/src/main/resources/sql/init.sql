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

-- 2. 初始化权限（菜单）- 根据 ManagerController 导航菜单结构
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `resource_type`, `url`, `method`, `parent_id`, `icon`, `sort_order`) VALUES
-- 系统管理
('system:manage', '系统管理', 'menu', NULL, NULL, 0, 'icon-save', 1),
('dashboard:view', '系统首页', 'menu', '/manager/home', NULL, 1, 'icon-home', 1),
('system:info', '系统信息', 'menu', '/manager/system-info', NULL, 1, 'icon-info', 2),

-- 用户管理
('user:manage', '用户管理', 'menu', NULL, NULL, 0, 'icon-users', 2),
('user:list', '用户列表', 'menu', '/manager/user/list', NULL, 4, 'icon-edit', 1),
('user:add', '添加用户', 'menu', '/manager/user/add', NULL, 4, 'icon-add', 2),
('user:role', '角色分配', 'menu', '/manager/user/role', NULL, 4, 'icon-filter', 3),

-- 角色权限
('role:manage', '角色权限', 'menu', NULL, NULL, 0, 'icon-lock', 3),
('role:list', '角色列表', 'menu', '/manager/role/list', NULL, 8, 'icon-edit', 1),
('permission:list', '权限列表', 'menu', '/manager/permission/list', NULL, 8, 'icon-edit', 2),

-- 系统设置
('settings:manage', '系统设置', 'menu', NULL, NULL, 0, 'icon-settings', 4),
('settings:basic', '基本设置', 'menu', '/manager/settings/basic', NULL, 11, 'icon-save', 1),
('settings:security', '安全设置', 'menu', '/manager/settings/security', NULL, 11, 'icon-lock', 2),
('settings:log', '日志管理', 'menu', '/manager/settings/log', NULL, 11, 'icon-search', 3);

-- 3. 初始化权限（接口）
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `resource_type`, `url`, `method`, `parent_id`, `sort_order`) VALUES
-- 用户接口权限（parent_id 指向用户列表菜单 5）
('api:user:list', '用户列表接口', 'api', '/api/user/list', 'GET', 5, 1),
('api:user:create', '创建用户接口', 'api', '/api/user/create', 'POST', 5, 2),
('api:user:update', '更新用户接口', 'api', '/api/user/update', 'PUT', 5, 3),
('api:user:delete', '删除用户接口', 'api', '/api/user/delete', 'DELETE', 5, 4),

-- 角色接口权限（parent_id 指向角色列表菜单 9）
('api:role:list', '角色列表接口', 'api', '/api/role/list', 'GET', 9, 1),
('api:role:create', '创建角色接口', 'api', '/api/role/create', 'POST', 9, 2),
('api:role:update', '更新角色接口', 'api', '/api/role/update', 'PUT', 9, 3),
('api:role:delete', '删除角色接口', 'api', '/api/role/delete', 'DELETE', 9, 4),
('api:role:permissions', '获取角色权限接口', 'api', '/api/role/*/permissions', 'GET', 9, 5),
('api:role:assign:permissions', '分配角色权限接口', 'api', '/api/role/*/permissions', 'POST', 9, 6),

-- 权限接口权限（parent_id 指向权限列表菜单 10）
('api:permission:list', '权限列表接口', 'api', '/api/permission/list', 'GET', 10, 1),
('api:permission:create', '创建权限接口', 'api', '/api/permission/create', 'POST', 10, 2),
('api:permission:update', '更新权限接口', 'api', '/api/permission/update', 'PUT', 10, 3),
('api:permission:delete', '删除权限接口', 'api', '/api/permission/delete', 'DELETE', 10, 4),
('api:permission:menu', '菜单列表接口', 'api', '/api/permission/menu/list', 'GET', 10, 5);

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
-- ROLE_ADMIN 拥有所有权限（1-28）
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20),
(1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28),

-- ROLE_USER 拥有部分权限（系统首页、用户列表、角色列表、权限列表）
(2, 2),  -- 系统首页
(2, 5),  -- 用户列表
(2, 9),  -- 角色列表
(2, 10), -- 权限列表

-- ROLE_TEST 拥有测试权限（系统首页、用户管理相关）
(3, 2),  -- 系统首页
(3, 5), (3, 6), (3, 7); -- 用户列表、添加用户、角色分配

-- ========================================================
-- 添加外键约束（可选，根据实际需求决定是否启用）
-- ========================================================

-- 用户角色关联表外键
-- ALTER TABLE `sys_user_role` ADD CONSTRAINT `fk_user_role_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `sys_user_role` ADD CONSTRAINT `fk_user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE;

-- 角色权限关联表外键
-- ALTER TABLE `sys_role_permission` ADD CONSTRAINT `fk_role_permission_role_id` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `sys_role_permission` ADD CONSTRAINT `fk_role_permission_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE;
