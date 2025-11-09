/*
 Navicat Premium Dump SQL

 Source Server         : 本地MySQL
 Source Server Type    : MySQL
 Source Server Version : 80043 (8.0.43)
 Source Host           : localhost:3306
 Source Schema         : filetransfer

 Target Server Type    : MySQL
 Target Server Version : 80043 (8.0.43)
 File Encoding         : 65001

 Date: 09/11/2025 23:42:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for download_logs
-- ----------------------------
DROP TABLE IF EXISTS `download_logs`;
CREATE TABLE `download_logs`  (
  `download_id` int NOT NULL AUTO_INCREMENT COMMENT '下载记录唯一标识（主键，自增）',
  `file_id` int NOT NULL COMMENT '被下载的文件ID（外键，关联files(file_id)）',
  `user_id` int NOT NULL COMMENT '下载用户ID（外键，关联users(user_id)）',
  `download_time` datetime NOT NULL COMMENT '下载时间（截取当前系统时间）',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '下载ip地址',
  PRIMARY KEY (`download_id`) USING BTREE,
  INDEX `file_id`(`file_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `download_logs_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `files` (`file_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `download_logs_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '下载记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of download_logs
-- ----------------------------

-- ----------------------------
-- Table structure for files
-- ----------------------------
DROP TABLE IF EXISTS `files`;
CREATE TABLE `files`  (
  `file_id` int NOT NULL AUTO_INCREMENT COMMENT '文件唯一标志（主键，自增）',
  `user_id` int NOT NULL COMMENT '上传用户ID（外键，关联users(user_id)）',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名（可重复）',
  `stored_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储文件名（服务器上）唯一',
  `file_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件类型（扩展名）',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节->B、KB、MB、GB）',
  `extract_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提取码（唯一）',
  `delete_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '删除码（唯一）',
  `upload_time` datetime NOT NULL COMMENT '上传时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间（上传后48小时）',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `status` enum('active','deleted') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '文件状态：有效或已删除',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件存储路径',
  PRIMARY KEY (`file_id`) USING BTREE,
  UNIQUE INDEX `extract_code`(`extract_code` ASC) USING BTREE,
  UNIQUE INDEX `delete_code`(`delete_code` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `files_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of files
-- ----------------------------
INSERT INTO `files` VALUES (1, 1, 'favicon.ico', 'b67701b9-d31f-4ea5-9472-377eb66b71b6.ico', '.ico', 270398, 'JTKLE', 'M9D37', '2025-11-09 23:09:58', '2025-11-11 23:09:58', 2, 'active', 'D:\\project\\oss\\filetransfer\\b67701b9-d31f-4ea5-9472-377eb66b71b6.ico');

-- ----------------------------
-- Table structure for registration_audit
-- ----------------------------
DROP TABLE IF EXISTS `registration_audit`;
CREATE TABLE `registration_audit`  (
  `registration_id` int NOT NULL AUTO_INCREMENT COMMENT '注册申请唯一标识（主键，自增）',
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
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '注册待审核表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of registration_audit
-- ----------------------------
INSERT INTO `registration_audit` VALUES (1, 'user001', '$2a$10$N7OsC5joNV78bzuK2cHYeeGJOgKTyxuQUPpH0YhC/jCfkG1Svkjhu', '测试', '测试', '用户001', '1355555555', 'OA123', '2025-11-06 22:33:34', 'approved');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户唯一标志（主键，自增）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（唯一）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `role` enum('admin','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'user' COMMENT '用户角色（管理员/普通用户）',
  `parent_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主单位（本级主要单位）',
  `child_unit` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属单位（下级所属单位）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `telephone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '单位固定电话',
  `office_oa` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OA办公账号',
  `registration_time` datetime NOT NULL COMMENT '注册时间（截取当前系统时间）',
  `upload_count` int NULL DEFAULT 0 COMMENT '上传累计次数',
  `download_count` int NULL DEFAULT 0 COMMENT '下载累计次数',
  `last_login` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` enum('active','inactive') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '账户状态：激活或禁用',
  `approved_by` int NULL DEFAULT NULL COMMENT '审核通过的管理员用户ID（自引用外键，外键关联users（user_id)）',
  `approved_time` datetime NULL DEFAULT NULL COMMENT '审核通过时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '$2a$10$N7OsC5joNV78bzuK2cHYeeGJOgKTyxuQUPpH0YhC/jCfkG1Svkjhu', 'admin', '系统管理部', '系统管理员组', '系统管理员', '010-12345678', 'ADMIN001', '2023-01-01 00:00:00', 0, 0, '2025-11-09 23:40:30', 'active', NULL, NULL);
INSERT INTO `users` VALUES (2, 'user001', '$2a$10$8yhD80Fky5P/Kshpoth2h.bkqOLTSH0b3I7Q8aPXDAMQuPKo5/nAK', 'user', '测试', '测试', '用户001', '1355555555', 'OA123', '2025-11-06 22:53:19', 0, 0, NULL, 'active', 1, '2025-11-06 22:53:19');

SET FOREIGN_KEY_CHECKS = 1;
