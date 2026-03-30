-- ========================================================
-- 数据库创建脚本
-- 文件名: 01-create-database.sql
-- 位置: db/common/init/
-- 说明: 创建项目数据库，设置字符集和时区
-- 执行顺序: 第一个执行
-- ========================================================

-- 删除已存在的数据库（可选，谨慎使用）
-- DROP DATABASE IF EXISTS `linsir-abc-mysql`;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `linsir-abc-mysql`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `linsir-abc-mysql`;

-- 设置时区为东八区（北京时间）
SET GLOBAL time_zone = '+8:00';
SET time_zone = '+8:00';

-- 设置SQL模式
SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- 验证数据库创建
SELECT DATABASE() AS current_database,
       @@character_set_database AS charset,
       @@collation_database AS collation;
