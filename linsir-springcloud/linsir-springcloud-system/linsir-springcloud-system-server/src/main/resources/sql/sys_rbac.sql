-- ========================================================-- RBAC 权限模型数据库表结构（精简版）-- 包含：用户、角色、权限、用户角色关联、角色权限关联-- 权限类型：模块、菜单、按钮、操作-- 作者: linsir-- 版本: 1.0.0-- ========================================================-- --------------------------------------------------------
-- 1. 用户表 (sys_user)-- 存储系统用户信息-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户账号',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(64) DEFAULT '' COMMENT '用户昵称',
    `avatar` VARCHAR(256) DEFAULT '' COMMENT '头像地址',
    `email` VARCHAR(128) DEFAULT '' COMMENT '邮箱',
    `mobile` VARCHAR(20) DEFAULT '' COMMENT '手机号码',
    `sex` TINYINT DEFAULT 0 COMMENT '用户性别（0未知 1男 2女）',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
    `user_type` TINYINT NOT NULL DEFAULT 0 COMMENT '用户类型（0系统用户 1普通用户）',
    `account_non_expired` TINYINT NOT NULL DEFAULT 1 COMMENT '帐户是否未过期（0已过期 1未过期）',
    `account_non_locked` TINYINT NOT NULL DEFAULT 1 COMMENT '帐户是否未锁定（0已锁定 1未锁定）',
    `credentials_non_expired` TINYINT NOT NULL DEFAULT 1 COMMENT '凭证是否未过期（0已过期 1未过期）',
    `login_ip` VARCHAR(128) DEFAULT '' COMMENT '最后登录IP',
    `login_date` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志（0-未删除，1-已删除）',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_mobile` (`mobile`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';-- --------------------------------------------------------
-- 2. 角色表 (sys_role)-- 存储系统角色信息-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(64) NOT NULL COMMENT '角色权限字符串',
    `role_sort` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '角色状态（0正常 1停用）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志（0-未删除，1-已删除）',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色信息表';-- --------------------------------------------------------
-- 3. 权限表 (sys_permission)-- 存储权限信息，类型包含：模块、菜单、按钮、操作-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `permission_name` VARCHAR(64) NOT NULL COMMENT '权限名称',
    `permission_code` VARCHAR(128) NOT NULL COMMENT '权限标识（如：system:user:create）',
    `permission_type` TINYINT NOT NULL DEFAULT 1 COMMENT '权限类型（1模块 2菜单 3按钮 4操作）',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父权限ID',
    `icon` VARCHAR(128) DEFAULT '' COMMENT '图标（菜单类型时使用）',
    `path` VARCHAR(200) DEFAULT '' COMMENT '路由路径（菜单类型时使用）',
    `component` VARCHAR(256) DEFAULT NULL COMMENT '组件路径（菜单类型时使用）',
    `permission_sort` INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态（0正常 1停用）',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志（0-未删除，1-已删除）',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_permission_type` (`permission_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';-- --------------------------------------------------------
-- 4. 用户角色关联表 (sys_user_role)-- 用户和角色的多对多关系-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志（0-未删除，1-已删除）',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';-- --------------------------------------------------------
-- 5. 角色权限关联表 (sys_role_permission)-- 角色和权限的多对多关系-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新人ID',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标志（0-未删除，1-已删除）',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和权限关联表';-- --------------------------------------------------------
-- 6. 操作日志表 (sys_oper_log)-- 记录用户操作日志-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_oper_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `title` VARCHAR(64) DEFAULT '' COMMENT '模块标题',
    `business_type` TINYINT DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `method` VARCHAR(256) DEFAULT '' COMMENT '方法名称',
    `request_method` VARCHAR(20) DEFAULT '' COMMENT '请求方式',
    `operator_type` TINYINT DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    `oper_name` VARCHAR(64) DEFAULT '' COMMENT '操作人员',
    `oper_url` VARCHAR(500) DEFAULT '' COMMENT '请求URL',
    `oper_ip` VARCHAR(128) DEFAULT '' COMMENT '主机地址',
    `oper_location` VARCHAR(128) DEFAULT '' COMMENT '操作地点',
    `oper_param` TEXT COMMENT '请求参数',
    `json_result` TEXT COMMENT '返回参数',
    `status` TINYINT DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    `error_msg` VARCHAR(2000) DEFAULT '' COMMENT '错误消息',
    `oper_time` DATETIME DEFAULT NULL COMMENT '操作时间',
    `cost_time` BIGINT DEFAULT 0 COMMENT '消耗时间（毫秒）',
    PRIMARY KEY (`id`),
    KEY `idx_oper_name` (`oper_name`),
    KEY `idx_business_type` (`business_type`),
    KEY `idx_oper_time` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志记录';-- --------------------------------------------------------
-- 7. 登录日志表 (sys_login_log)-- 记录用户登录日志-- --------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_login_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `username` VARCHAR(64) DEFAULT '' COMMENT '用户账号',
    `ip_address` VARCHAR(128) DEFAULT '' COMMENT '登录IP地址',
    `login_location` VARCHAR(128) DEFAULT '' COMMENT '登录地点',
    `browser` VARCHAR(64) DEFAULT '' COMMENT '浏览器类型',
    `os` VARCHAR(64) DEFAULT '' COMMENT '操作系统',
    `login_status` TINYINT DEFAULT 0 COMMENT '登录状态（0成功 1失败）',
    `msg` VARCHAR(256) DEFAULT '' COMMENT '提示消息',
    `login_time` DATETIME DEFAULT NULL COMMENT '访问时间',
    PRIMARY KEY (`id`),
    KEY `idx_username` (`username`),
    KEY `idx_login_time` (`login_time`),
    KEY `idx_login_status` (`login_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统访问记录';-- --------------------------------------------------------
-- 初始化数据-- --------------------------------------------------------

-- 初始化角色数据
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_sort`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, '超级管理员', 'super_admin', 1, 0, '超级管理员', 1, NOW(), 1, NOW(), 0),
(2, '普通角色', 'common', 2, 0, '普通角色', 1, NOW(), 1, NOW(), 0);

-- 初始化权限数据（模块、菜单、按钮、操作）-- 系统管理模块
INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `icon`, `path`, `component`, `permission_sort`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, '系统管理', 'system', 1, 0, 'system', '', NULL, 1, 0, '系统管理模块', 1, NOW(), 1, NOW(), 0);

-- 用户管理菜单
INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `icon`, `path`, `component`, `permission_sort`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(2, '用户管理', 'system:user', 2, 1, 'user', '/system/user', 'system/user/index', 1, 0, '用户管理菜单', 1, NOW(), 1, NOW(), 0);

-- 用户管理按钮/操作权限
INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `icon`, `path`, `component`, `permission_sort`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(3, '用户查询', 'system:user:query', 4, 2, '', '', NULL, 1, 0, '用户查询操作', 1, NOW(), 1, NOW(), 0),
(4, '用户新增', 'system:user:create', 4, 2, '', '', NULL, 2, 0, '用户新增操作', 1, NOW(), 1, NOW(), 0),
(5, '用户修改', 'system:user:update', 4, 2, '', '', NULL, 3, 0, '用户修改操作', 1, NOW(), 1, NOW(), 0),
(6, '用户删除', 'system:user:delete', 4, 2, '', '', NULL, 4, 0, '用户删除操作', 1, NOW(), 1, NOW(), 0),
(7, '用户导出', 'system:user:export', 4, 2, '', '', NULL, 5, 0, '用户导出操作', 1, NOW(), 1, NOW(), 0);

-- 角色管理菜单
INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `icon`, `path`, `component`, `permission_sort`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(8, '角色管理', 'system:role', 2, 1, 'peoples', '/system/role', 'system/role/index', 2, 0, '角色管理菜单', 1, NOW(), 1, NOW(), 0);

-- 角色管理按钮/操作权限
INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `icon`, `path`, `component`, `permission_sort`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(9, '角色查询', 'system:role:query', 4, 8, '', '', NULL, 1, 0, '角色查询操作', 1, NOW(), 1, NOW(), 0),
(10, '角色新增', 'system:role:create', 4, 8, '', '', NULL, 2, 0, '角色新增操作', 1, NOW(), 1, NOW(), 0),
(11, '角色修改', 'system:role:update', 4, 8, '', '', NULL, 3, 0, '角色修改操作', 1, NOW(), 1, NOW(), 0),
(12, '角色删除', 'system:role:delete', 4, 8, '', '', NULL, 4, 0, '角色删除操作', 1, NOW(), 1, NOW(), 0);

-- 权限管理菜单
INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `icon`, `path`, `component`, `permission_sort`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(13, '权限管理', 'system:permission', 2, 1, 'tree', '/system/permission', 'system/permission/index', 3, 0, '权限管理菜单', 1, NOW(), 1, NOW(), 0);

-- 权限管理按钮/操作权限
INSERT INTO `sys_permission` (`id`, `permission_name`, `permission_code`, `permission_type`, `parent_id`, `icon`, `path`, `component`, `permission_sort`, `status`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(14, '权限查询', 'system:permission:query', 4, 13, '', '', NULL, 1, 0, '权限查询操作', 1, NOW(), 1, NOW(), 0),
(15, '权限新增', 'system:permission:create', 4, 13, '', '', NULL, 2, 0, '权限新增操作', 1, NOW(), 1, NOW(), 0),
(16, '权限修改', 'system:permission:update', 4, 13, '', '', NULL, 3, 0, '权限修改操作', 1, NOW(), 1, NOW(), 0),
(17, '权限删除', 'system:permission:delete', 4, 13, '', '', NULL, 4, 0, '权限删除操作', 1, NOW(), 1, NOW(), 0);

-- 初始化用户数据（密码: admin123，BCrypt加密后）
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `mobile`, `sex`, `status`, `user_type`, `account_non_expired`, `account_non_locked`, `credentials_non_expired`, `login_ip`, `login_date`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, 'admin', '$2a$10$7JB720yubVS5vRVJ8nJDJuaB4lyE1uYhJzGKYbVXnRkKjKzJ9V0q', '管理员', '', 'admin@linsir.com', '13800138000', 0, 0, 0, 1, 1, 1, '127.0.0.1', NOW(), '管理员', 1, NOW(), 1, NOW(), 0);

-- 初始化用户角色关联
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, 1, 1, 1, NOW(), 1, NOW(), 0);

-- 初始化角色权限关联（超级管理员拥有所有权限）
INSERT INTO `sys_role_permission` (`id`, `role_id`, `permission_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, 1, 1, 1, NOW(), 1, NOW(), 0),
(2, 1, 2, 1, NOW(), 1, NOW(), 0),
(3, 1, 3, 1, NOW(), 1, NOW(), 0),
(4, 1, 4, 1, NOW(), 1, NOW(), 0),
(5, 1, 5, 1, NOW(), 1, NOW(), 0),
(6, 1, 6, 1, NOW(), 1, NOW(), 0),
(7, 1, 7, 1, NOW(), 1, NOW(), 0),
(8, 1, 8, 1, NOW(), 1, NOW(), 0),
(9, 1, 9, 1, NOW(), 1, NOW(), 0),
(10, 1, 10, 1, NOW(), 1, NOW(), 0),
(11, 1, 11, 1, NOW(), 1, NOW(), 0),
(12, 1, 12, 1, NOW(), 1, NOW(), 0),
(13, 1, 13, 1, NOW(), 1, NOW(), 0),
(14, 1, 14, 1, NOW(), 1, NOW(), 0),
(15, 1, 15, 1, NOW(), 1, NOW(), 0),
(16, 1, 16, 1, NOW(), 1, NOW(), 0),
(17, 1, 17, 1, NOW(), 1, NOW(), 0);
