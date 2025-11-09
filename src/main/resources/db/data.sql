/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost:3306
 Source Schema         : filetransfer

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 06/11/2025 10:00:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 初始化管理员账户
-- 用户名: admin
-- 密码: admin123
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb0bta/OauRxaOKSr.QhqyD2R5FKvMQjmHoLkm5Sy', 'admin', '系统管理部', '系统管理员组', '系统管理员', '010-12345678', 'ADMIN001', '2023-01-01 00:00:00', 0, 0, NULL, 'active', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;