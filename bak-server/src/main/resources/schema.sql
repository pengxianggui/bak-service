/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44-log)
 Source Host           : localhost:3306
 Source Schema         : back-up

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44-log)
 File Encoding         : 65001

 Date: 27/11/2024 10:30:52
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for opr_log
-- ----------------------------
CREATE TABLE IF NOT EXISTS `opr_log`
(
    `id`            bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `operator`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作人',
    `category_code` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '操作的数据类目编码',
    `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作的数据类目名',
    `db_name`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据库名',
    `table_name`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表名',
    `type`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '类型(bak-备份;archive-归档;restore-还原)',
    `success`       bit(1)                                                        NOT NULL COMMENT '操作是否成功',
    `file_path`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成的文件路径',
    `expired_date`  date NULL DEFAULT NULL COMMENT '(文件)失效日期',
    `expired`       bit(1) DEFAULT b'0' COMMENT '是否失效',
    `cond`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'where条件',
    `msg`           text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '消息',
    `create_time`   datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX           `idx_create_time`(`create_time`) USING BTREE,
    INDEX           `idx_category_code`(`category_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 163 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for data_category
-- ----------------------------
CREATE TABLE IF NOT EXISTS `data_category`
(
    `id`              bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`            varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '品类编码',
    `name`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '品类名',
    `db_name`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '库名',
    `table_name`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表名',
    `time_field_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '时间字段名',
    `create_time`     datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据类目' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for task_config
-- ----------------------------
CREATE TABLE IF NOT EXISTS `task_config`
(
    `id`             bigint(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id`    bigint(11) NOT NULL COMMENT '数据类目id',
    `category_name`  varchar(255) NOT NULL COMMENT '数据类目名',
    `type`           varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'bak' COMMENT '任务类型(bak-备份;archive-归档)',
    `cron`           varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '执行频率。cron表达式',
    `path`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '存储路径。绝对路径',
    `cond`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '数据过滤条件(where)',
    `zip`            bit(1)                                                        NOT NULL DEFAULT b'1' COMMENT '产生的文件是否zip压缩',
    `enable`         bit(1)                                                        NOT NULL DEFAULT b'1' COMMENT '是否启用(默认启用)',
    `strategy`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'd' COMMENT '用于归档类型。阈值策略(d-启用天数阈值;r-启用条数阈值；默认d)',
    `strategy_value` int(9) NULL DEFAULT NULL COMMENT '用于归档类型。天数或条数, (如:365 或 200000)',
    `handle_type`    varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用于归档类型。处理类型, 超出阈值时如何处理(查看HandleType枚举类)',
    `keep_fate`      int(9) NULL DEFAULT NULL COMMENT '生成文件的保存天数',
    `create_time`    datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务配置' ROW_FORMAT = Dynamic;

SET
FOREIGN_KEY_CHECKS = 1;
