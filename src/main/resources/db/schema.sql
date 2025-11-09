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
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户唯一标志（主键，自增）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（唯一）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `role` enum('admin','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色（管理员/普通用户）',
  `parent_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主单位（本级主要单位）',
  `child_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属单位（下级所属单位）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `telephone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单位固定电话',
  `office_oa` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OA办公账号',
  `registration_time` datetime NOT NULL COMMENT '注册时间（截取当前系统时间）',
  `upload_count` int(11) NULL DEFAULT 0 COMMENT '上传累计次数',
  `download_count` int(11) NULL DEFAULT 0 COMMENT '下载累计次数',
  `last_login` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '账户状态：激活或禁用',
  `approved_by` int(11) NULL DEFAULT NULL COMMENT '审核通过的管理员用户ID（自引用外键，外键关联users（user_id)）',
  `approved_time` datetime NULL DEFAULT NULL COMMENT '审核通过时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for registration_audit
-- ----------------------------
DROP TABLE IF EXISTS `registration_audit`;
CREATE TABLE `registration_audit`  (
  `registration_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '注册申请唯一标识（主键，自增）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（唯一）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `parent_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主单位（本级主要单位）',
  `child_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属单位（下级所属单位）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `telephone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单位固定电话',
  `office_oa` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OA办公账号',
  `registration_time` datetime NOT NULL COMMENT '申请注册时间',
  `status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending' COMMENT '审核状态：待处理、已同意、已拒绝',
  PRIMARY KEY (`registration_id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '注册待审核表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for files
-- ----------------------------
DROP TABLE IF EXISTS `files`;
CREATE TABLE `files`  (
  `file_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '文件唯一标志（主键，自增）',
  `user_id` int(11) NOT NULL COMMENT '上传用户ID（外键，关联users(user_id)）',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名（可重复）',
  `stored_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储文件名（服务器上）唯一',
  `file_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件类型（扩展名）',
  `file_size` bigint(20) NOT NULL COMMENT '文件大小（字节->B、KB、MB、GB）',
  `extract_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提取码（唯一）',
  `delete_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '删除码（唯一）',
  `upload_time` datetime NOT NULL COMMENT '上传时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间（上传后48小时）',
  `download_count` int(11) NULL DEFAULT 0 COMMENT '下载次数',
  `status` enum('active','deleted') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '文件状态：有效或已删除',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件存储路径',
  PRIMARY KEY (`file_id`) USING BTREE,
  UNIQUE INDEX `extract_code`(`extract_code`) USING BTREE,
  UNIQUE INDEX `delete_code`(`delete_code`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `files_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for download_logs
-- ----------------------------
DROP TABLE IF EXISTS `download_logs`;
CREATE TABLE `download_logs`  (
  `download_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '下载记录唯一标识（主键，自增）',
  `file_id` int(11) NOT NULL COMMENT '被下载的文件ID（外键，关联files(file_id)）',
  `user_id` int(11) NOT NULL COMMENT '下载用户ID（外键，关联users(user_id)）',
  `download_time` datetime NOT NULL COMMENT '下载时间（截取当前系统时间）',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下载ip地址',
  PRIMARY KEY (`download_id`) USING BTREE,
  INDEX `file_id`(`file_id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  CONSTRAINT `download_logs_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `files` (`file_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `download_logs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '下载记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;