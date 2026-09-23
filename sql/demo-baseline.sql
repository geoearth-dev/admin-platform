-- Admin Platform demo baseline; exported 2026-09-22 from local admin_platform.
-- DESTRUCTIVE: recreates the listed tables in the database selected by the client.
-- Retains active users and their existing password hashes; do not publish this file.
-- No CREATE DATABASE / USE statements; source database was read only.
SET NAMES utf8mb4;
SET @OLD_SQL_MODE=@@SQL_MODE;
SET SQL_MODE='NO_AUTO_VALUE_ON_ZERO,NO_BACKSLASH_ESCAPES';
SET @OLD_TIME_ZONE=@@TIME_ZONE;
SET time_zone='+00:00';
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS;
SET FOREIGN_KEY_CHECKS=0;

-- Table: gen_table
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) NOT NULL COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `form_col_num` int DEFAULT '1' COMMENT '表单布局（单列 双列 三列）',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gen_table_name` (`table_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';

-- Table: gen_table_column
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint NOT NULL COMMENT '归属表编号',
  `column_name` varchar(200) NOT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否主键（1是）',
  `is_increment` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否自增（1是）',
  `is_required` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否必填（1是）',
  `is_insert` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否为插入字段（1是）',
  `is_edit` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否编辑字段（1是）',
  `is_list` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否列表字段（1是）',
  `is_query` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gen_table_column` (`table_id`,`column_name`),
  CONSTRAINT `chk_gen_table_column_is_edit` CHECK ((`is_edit` in (0,1))),
  CONSTRAINT `chk_gen_table_column_is_increment` CHECK ((`is_increment` in (0,1))),
  CONSTRAINT `chk_gen_table_column_is_insert` CHECK ((`is_insert` in (0,1))),
  CONSTRAINT `chk_gen_table_column_is_list` CHECK ((`is_list` in (0,1))),
  CONSTRAINT `chk_gen_table_column_is_pk` CHECK ((`is_pk` in (0,1))),
  CONSTRAINT `chk_gen_table_column_is_query` CHECK ((`is_query` in (0,1))),
  CONSTRAINT `chk_gen_table_column_is_required` CHECK ((`is_required` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';

-- Table: sys_config
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) NOT NULL COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '系统内置（0否 1是）',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者用户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者用户ID',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  CONSTRAINT `chk_sys_config_config_type` CHECK ((`config_type` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
INSERT INTO `sys_config` (`id`,`config_name`,`config_key`,`config_value`,`config_type`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` (`id`,`config_name`,`config_key`,`config_value`,`config_type`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (2,'用户管理-账号初始密码','sys.user.initPassword','123456',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'初始化密码 123456');
INSERT INTO `sys_config` (`id`,`config_name`,`config_key`,`config_value`,`config_type`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` (`id`,`config_name`,`config_key`,`config_value`,`config_type`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (4,'账号自助-验证码开关','sys.account.captchaEnabled','true',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` (`id`,`config_name`,`config_key`,`config_value`,`config_type`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` (`id`,`config_name`,`config_key`,`config_value`,`config_type`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (6,'用户登录-黑名单列表','sys.login.blackIPList','',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `sys_config` (`id`,`config_name`,`config_key`,`config_value`,`config_type`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` (`id`,`config_name`,`config_key`,`config_value`,`config_type`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` (`id`,`config_name`,`config_key`,`config_value`,`config_type`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (9,'用户管理-密码字符范围','sys.account.passwordCharRange','0',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数字和特殊字符（目前支持的特殊字符包括：~!@#$%^&*()-=_+）');

-- Table: sys_content
DROP TABLE IF EXISTS `sys_content`;
CREATE TABLE `sys_content` (
  `id` int NOT NULL COMMENT 'id',
  `sys_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '系统名称',
  `logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '系统logo',
  `login_logo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '登录页面logo',
  `carousel_image` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '轮播图',
  `contact_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '电子邮箱',
  `copyright` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '版权方',
  `record_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备案号',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `create_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `creator_id` int DEFAULT NULL COMMENT '创建人id',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '修改人',
  `updater_id` int DEFAULT NULL COMMENT '修改人id',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `config_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '配置信息',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `chk_sys_content_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_content_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;
INSERT INTO `sys_content` (`id`,`sys_name`,`logo`,`login_logo`,`carousel_image`,`contact_number`,`email`,`copyright`,`record_number`,`del_flag`,`status`,`create_by`,`creator_id`,`create_time`,`update_by`,`updater_id`,`update_time`,`remark`,`config_json`) VALUES (1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,NULL,NULL,'2026-08-31 14:57:32',NULL,NULL,NULL,NULL,'{"copyrightHttp":"https://qiantong.tech","systemMenuFullLogo":"","systemMenuLogo":""}');

-- Table: sys_dept
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) NOT NULL COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者用户ID',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dept_name_active` (`parent_id`,((case when (`del_flag` = 0) then `dept_name` else NULL end))),
  CONSTRAINT `chk_sys_dept_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_dept_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (100,0,'0','总公司',0,'','15888888888','',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL);
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (101,100,'0,100','重庆',1,'','15888888888','',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL);
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (102,100,'0,100','北京',2,'','15888888888','',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL);
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (103,101,'0,100,101','研发部门',1,'','15888888888','',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL);
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (104,101,'0,100,101','市场部门',2,'','15888888888','',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL);
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (105,101,'0,100,101','测试部门',3,'','15888888888','',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL);
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (106,101,'0,100,101','财务部门',4,'','15888888888','',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL);
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (107,101,'0,100,101','运维部门',5,'','15888888888','',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL);
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (108,102,'0,100,102','市场部门',1,'','15888888888','',1,0,'admin','2026-07-29 13:45:29','',NULL,NULL,NULL);
INSERT INTO `sys_dept` (`id`,`parent_id`,`ancestors`,`dept_name`,`order_num`,`leader`,`phone`,`email`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (109,102,'0,100,102','财务部门',2,'','15888888888','',1,0,'admin','2026-07-29 13:45:29','',NULL,NULL,NULL);

-- Table: sys_dict_data
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) NOT NULL COMMENT '字典键值',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `is_default` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否默认（0否 1是）',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者用户ID',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_dict_type_value` (`dict_type`,`dict_value`),
  CONSTRAINT `chk_sys_dict_data_is_default` CHECK ((`is_default` in (0,1))),
  CONSTRAINT `chk_sys_dict_data_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (1,1,'男','0','sys_user_sex','','','性别男',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (2,2,'女','1','sys_user_sex','','','性别女',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (3,3,'未知','2','sys_user_sex','','','性别未知',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (4,1,'显示','0','sys_show_hide','','primary','显示菜单',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (5,2,'隐藏','1','sys_show_hide','','danger','隐藏菜单',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (6,1,'正常','1','sys_normal_disable','','primary','正常状态',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (7,2,'停用','0','sys_normal_disable','','danger','停用状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (8,1,'正常','1','sys_job_status','','primary','正常状态',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (9,2,'暂停','0','sys_job_status','','danger','停用状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (10,1,'默认','DEFAULT','sys_job_group','','','默认分组',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (11,2,'系统','SYSTEM','sys_job_group','','','系统分组',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (12,1,'是','1','sys_yes_no','','primary','系统默认是',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (13,2,'否','0','sys_yes_no','','danger','系统默认否',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (14,1,'通知','1','sys_notice_type','','warning','通知',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (15,2,'公告','2','sys_notice_type','','success','公告',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (16,1,'正常','1','sys_notice_status','','primary','正常状态',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (17,2,'关闭','0','sys_notice_status','','danger','关闭状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (18,99,'其他','0','sys_oper_type','','info','其他操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (19,1,'新增','1','sys_oper_type','','info','新增操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (20,2,'修改','2','sys_oper_type','','info','修改操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (21,3,'删除','3','sys_oper_type','','danger','删除操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (22,4,'授权','4','sys_oper_type','','primary','授权操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (23,5,'导出','5','sys_oper_type','','warning','导出操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (24,6,'导入','6','sys_oper_type','','warning','导入操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (25,7,'强退','7','sys_oper_type','','danger','强退操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (26,8,'生成代码','8','sys_oper_type','','warning','生成操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (27,9,'清空数据','9','sys_oper_type','','danger','清空操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (28,1,'成功','1','sys_common_status','','primary','正常状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
INSERT INTO `sys_dict_data` (`id`,`dict_sort`,`dict_label`,`dict_value`,`dict_type`,`css_class`,`list_class`,`remark`,`is_default`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`creator_id`,`updater_id`) VALUES (29,2,'失败','0','sys_common_status','','danger','停用状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);

-- Table: sys_dict_type
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者用户ID',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `dict_type` (`dict_type`) USING BTREE,
  CONSTRAINT `chk_sys_dict_type_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (1,'用户性别','sys_user_sex',1,'admin','2026-07-29 13:45:30','',NULL,'用户性别列表',NULL,NULL);
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (2,'菜单状态','sys_show_hide',1,'admin','2026-07-29 13:45:30','',NULL,'菜单状态列表',NULL,NULL);
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (3,'系统开关','sys_normal_disable',1,'admin','2026-07-29 13:45:30','',NULL,'系统开关列表',NULL,NULL);
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (4,'任务状态','sys_job_status',1,'admin','2026-07-29 13:45:30','',NULL,'任务状态列表',NULL,NULL);
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (5,'任务分组','sys_job_group',1,'admin','2026-07-29 13:45:30','',NULL,'任务分组列表',NULL,NULL);
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (6,'系统是否','sys_yes_no',1,'admin','2026-07-29 13:45:30','',NULL,'系统是否列表',NULL,NULL);
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (7,'通知类型','sys_notice_type',1,'admin','2026-07-29 13:45:30','',NULL,'通知类型列表',NULL,NULL);
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (8,'通知状态','sys_notice_status',1,'admin','2026-07-29 13:45:30','',NULL,'通知状态列表',NULL,NULL);
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (9,'操作类型','sys_oper_type',1,'admin','2026-07-29 13:45:30','',NULL,'操作类型列表',NULL,NULL);
INSERT INTO `sys_dict_type` (`id`,`dict_name`,`dict_type`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (10,'系统状态','sys_common_status',1,'admin','2026-07-29 13:45:30','',NULL,'登录状态列表',NULL,NULL);

-- Table: sys_job
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否并发执行（1允许 0禁止）',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '任务状态（1正常 0暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_sys_job_concurrent` CHECK ((`concurrent` in (0,1))),
  CONSTRAINT `chk_sys_job_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';
INSERT INTO `sys_job` (`id`,`job_name`,`job_group`,`invoke_target`,`cron_expression`,`misfire_policy`,`concurrent`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) VALUES (1,'系统默认（无参）','DEFAULT','adminTask.taskNoParams','0/10 * * * * ?','3',0,0,'admin','2026-07-29 13:45:31','','2026-09-17 16:17:45','');
INSERT INTO `sys_job` (`id`,`job_name`,`job_group`,`invoke_target`,`cron_expression`,`misfire_policy`,`concurrent`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) VALUES (2,'系统默认（有参）','DEFAULT','adminTask.taskParams(''task'')','0/15 * * * * ?','3',0,0,'admin','2026-07-29 13:45:31','',NULL,'');
INSERT INTO `sys_job` (`id`,`job_name`,`job_group`,`invoke_target`,`cron_expression`,`misfire_policy`,`concurrent`,`status`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`) VALUES (3,'系统默认（多参）','DEFAULT','adminTask.taskMultipleParams(''task'', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3',0,0,'admin','2026-07-29 13:45:31','',NULL,'');

-- Table: sys_job_log
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '执行状态（1成功 0失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `chk_sys_job_log_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';

-- Table: sys_login_log
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户账号',
  `ip_address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '浏览器类型',
  `operating_system` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作系统',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '结果（0失败 1成功）',
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '提示消息',
  `login_time` timestamp NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_login_log_time` (`login_time`) USING BTREE,
  KEY `idx_login_log_status` (`status`) USING BTREE,
  CONSTRAINT `chk_sys_login_log_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';

-- Table: sys_menu
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` json DEFAULT NULL COMMENT '路由查询参数对象',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `link` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '外链地址',
  `iframe_src` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'iframe地址',
  `keep_alive` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否缓存（0不缓存 1缓存）',
  `menu_type` varchar(16) NOT NULL DEFAULT 'menu' COMMENT '菜单类型 catalog目录 menu菜单 embedded内嵌 link外链 button按钮',
  `hide_in_menu` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否在菜单中隐藏（0显示 1隐藏）',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `active_icon` varchar(100) DEFAULT NULL COMMENT '激活图标',
  `active_path` varchar(200) DEFAULT NULL COMMENT '激活菜单路径',
  `affix_tab` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '固定标签',
  `affix_tab_order` int NOT NULL DEFAULT '0' COMMENT '固定标签顺序',
  `badge` varchar(50) DEFAULT NULL COMMENT '徽标内容',
  `badge_type` varchar(10) DEFAULT NULL COMMENT '徽标类型 dot/normal',
  `badge_variants` varchar(20) DEFAULT NULL COMMENT '徽标样式',
  `hide_children_in_menu` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '隐藏子菜单',
  `hide_in_breadcrumb` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '隐藏面包屑',
  `hide_in_tab` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '隐藏标签',
  `redirect` varchar(200) DEFAULT NULL COMMENT '内部重定向路径',
  `open_in_new_window` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '新窗口打开',
  `no_basic_layout` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '不使用基础布局',
  `max_num_of_open_tab` int NOT NULL DEFAULT '-1' COMMENT '同一路由最多标签数 -1不限',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者用户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者用户ID',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_sys_menu_affix_tab` CHECK ((`affix_tab` in (0,1))),
  CONSTRAINT `chk_sys_menu_hide_children_in_menu` CHECK ((`hide_children_in_menu` in (0,1))),
  CONSTRAINT `chk_sys_menu_hide_in_breadcrumb` CHECK ((`hide_in_breadcrumb` in (0,1))),
  CONSTRAINT `chk_sys_menu_hide_in_menu` CHECK ((`hide_in_menu` in (0,1))),
  CONSTRAINT `chk_sys_menu_hide_in_tab` CHECK ((`hide_in_tab` in (0,1))),
  CONSTRAINT `chk_sys_menu_keep_alive` CHECK ((`keep_alive` in (0,1))),
  CONSTRAINT `chk_sys_menu_no_basic_layout` CHECK ((`no_basic_layout` in (0,1))),
  CONSTRAINT `chk_sys_menu_open_in_new_window` CHECK ((`open_in_new_window` in (0,1))),
  CONSTRAINT `chk_sys_menu_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1,'系统管理',0,1,'system',NULL,NULL,'','',NULL,0,'catalog',0,1,'','lucide:settings',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'系统管理目录');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (2,'系统监控',0,2,'monitor',NULL,NULL,'','',NULL,0,'catalog',0,1,'','lucide:activity',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'系统监控目录');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (3,'系统工具',0,3,'tool',NULL,NULL,'','',NULL,0,'catalog',0,1,'','lucide:wrench',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'系统工具目录');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (100,'用户管理',1,1,'user','sys/system/user/index',NULL,'','',NULL,0,'menu',0,1,'system:user:list','lucide:user-round',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'用户管理菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (101,'角色管理',1,2,'role','sys/system/role/index',NULL,'','',NULL,0,'menu',0,1,'system:role:list','lucide:users-round',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'角色管理菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (102,'菜单管理',1,3,'menu','sys/system/menu/index',NULL,'','',NULL,0,'menu',0,1,'system:menu:list','lucide:list-tree',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'菜单管理菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (103,'部门管理',1,4,'dept','sys/system/dept/index',NULL,'','',NULL,0,'menu',0,1,'system:dept:list','lucide:network',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'部门管理菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (104,'岗位管理',1,5,'post','sys/system/post/index',NULL,'','',NULL,0,'menu',0,1,'system:post:list','lucide:contact-round',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'岗位管理菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (105,'字典管理',1,6,'dict','sys/system/dict/index',NULL,'','',NULL,0,'menu',0,1,'system:dict:list','lucide:book-open',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'字典管理菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (106,'参数设置',1,7,'config','sys/system/config/index',NULL,'','',NULL,0,'menu',0,1,'system:config:list','lucide:settings-2',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'参数设置菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (107,'通知公告',1,8,'notice','sys/system/notice/index',NULL,'','',NULL,0,'menu',0,1,'system:notice:list','lucide:bell',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'通知公告菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (108,'日志管理',1,9,'log','',NULL,'','',NULL,0,'catalog',0,1,'','lucide:logs',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'日志管理菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (109,'在线用户',2,1,'online','sys/monitor/online/index',NULL,'','',NULL,0,'menu',0,1,'monitor:online:list','lucide:monitor-check',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'在线用户菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (110,'定时任务',2,2,'job','sys/monitor/job/index',NULL,'','',NULL,0,'menu',0,1,'monitor:job:list','lucide:clock',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'定时任务菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (111,'数据监控',2,3,'druid','',NULL,'','','/api/druid/index.html',1,'embedded',0,1,'monitor:druid:list','lucide:database',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'数据监控菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (112,'服务监控',2,4,'server','sys/monitor/server/index',NULL,'','',NULL,0,'menu',0,1,'monitor:server:list','lucide:server',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'服务监控菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (113,'缓存监控',2,5,'cache','sys/monitor/cache/index',NULL,'','',NULL,0,'menu',0,1,'monitor:cache:list','lucide:database-zap',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'缓存监控菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (114,'缓存列表',2,6,'cacheList','sys/monitor/cache/list',NULL,'','',NULL,0,'menu',0,1,'monitor:cache:list','lucide:list',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'缓存列表菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (115,'表单构建',3,1,'build','sys/tool/build/index',NULL,'','',NULL,0,'menu',0,1,'tool:build:list','lucide:panels-top-left',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'表单构建菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (116,'代码生成',3,2,'gen','sys/tool/gen/index',NULL,'','',NULL,0,'menu',0,1,'tool:gen:list','lucide:code-xml',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'代码生成菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (117,'系统接口',3,3,'doc','',NULL,'','','http://192.168.1.24:8080/doc',0,'embedded',0,1,'tool:swagger:list','lucide:cable',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'系统接口菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (500,'操作日志',108,1,'operlog','sys/system/log/operlog/index',NULL,'','',NULL,0,'menu',0,1,'monitor:operlog:list','lucide:file-clock',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'操作日志菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (501,'登录日志',108,2,'loginlog','sys/system/log/loginlog/index',NULL,'','',NULL,0,'menu',0,1,'monitor:logininfor:list','lucide:log-in',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'登录日志菜单');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1000,'用户查询',100,1,'','',NULL,'','',NULL,0,'button',0,1,'system:user:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1001,'用户新增',100,2,'','',NULL,'','',NULL,0,'button',0,1,'system:user:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1002,'用户修改',100,3,'','',NULL,'','',NULL,0,'button',0,1,'system:user:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1003,'用户删除',100,4,'','',NULL,'','',NULL,0,'button',0,1,'system:user:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1004,'用户导出',100,5,'','',NULL,'','',NULL,0,'button',0,1,'system:user:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1005,'用户导入',100,6,'','',NULL,'','',NULL,0,'button',0,1,'system:user:import','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1006,'重置密码',100,7,'','',NULL,'','',NULL,0,'button',0,1,'system:user:resetPwd','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1007,'角色查询',101,1,'','',NULL,'','',NULL,0,'button',0,1,'system:role:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1008,'角色新增',101,2,'','',NULL,'','',NULL,0,'button',0,1,'system:role:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1009,'角色修改',101,3,'','',NULL,'','',NULL,0,'button',0,1,'system:role:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1010,'角色删除',101,4,'','',NULL,'','',NULL,0,'button',0,1,'system:role:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1011,'角色导出',101,5,'','',NULL,'','',NULL,0,'button',0,1,'system:role:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1012,'菜单查询',102,1,'','',NULL,'','',NULL,0,'button',0,1,'system:menu:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1013,'菜单新增',102,2,'','',NULL,'','',NULL,0,'button',0,1,'system:menu:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1014,'菜单修改',102,3,'','',NULL,'','',NULL,0,'button',0,1,'system:menu:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1015,'菜单删除',102,4,'','',NULL,'','',NULL,0,'button',0,1,'system:menu:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1016,'部门查询',103,1,'','',NULL,'','',NULL,0,'button',0,1,'system:dept:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1017,'部门新增',103,2,'','',NULL,'','',NULL,0,'button',0,1,'system:dept:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1018,'部门修改',103,3,'','',NULL,'','',NULL,0,'button',0,1,'system:dept:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1019,'部门删除',103,4,'','',NULL,'','',NULL,0,'button',0,1,'system:dept:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1020,'岗位查询',104,1,'','',NULL,'','',NULL,0,'button',0,1,'system:post:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1021,'岗位新增',104,2,'','',NULL,'','',NULL,0,'button',0,1,'system:post:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1022,'岗位修改',104,3,'','',NULL,'','',NULL,0,'button',0,1,'system:post:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1023,'岗位删除',104,4,'','',NULL,'','1',NULL,0,'button',0,1,'system:post:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1024,'岗位导出',104,5,'','',NULL,'','1',NULL,0,'button',0,1,'system:post:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1025,'字典查询',105,1,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1026,'字典新增',105,2,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1027,'字典修改',105,3,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1028,'字典删除',105,4,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1029,'字典导出',105,5,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1030,'参数查询',106,1,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1031,'参数新增',106,2,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1032,'参数修改',106,3,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1033,'参数删除',106,4,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1034,'参数导出',106,5,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1035,'公告查询',107,1,'#','',NULL,'','1',NULL,0,'button',0,1,'system:notice:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1036,'公告新增',107,2,'#','',NULL,'','1',NULL,0,'button',0,1,'system:notice:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1037,'公告修改',107,3,'#','',NULL,'','1',NULL,0,'button',0,1,'system:notice:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1038,'公告删除',107,4,'#','',NULL,'','1',NULL,0,'button',0,1,'system:notice:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1039,'操作查询',500,1,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:operlog:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1040,'操作删除',500,2,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:operlog:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1041,'日志导出',500,3,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:operlog:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1042,'登录查询',501,1,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:logininfor:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1043,'登录删除',501,2,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:logininfor:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1044,'日志导出',501,3,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:logininfor:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1045,'账户解锁',501,4,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:logininfor:unlock','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1046,'在线查询',109,1,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:online:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1047,'批量强退',109,2,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:online:batchLogout','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1048,'单条强退',109,3,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:online:forceLogout','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1049,'任务查询',110,1,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1050,'任务新增',110,2,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1051,'任务修改',110,3,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1052,'任务删除',110,4,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1053,'状态修改',110,5,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:changeStatus','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1054,'任务导出',110,6,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1055,'生成查询',116,1,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1056,'生成修改',116,2,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1057,'生成删除',116,3,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1058,'导入代码',116,4,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:import','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1059,'预览代码',116,5,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:preview','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
INSERT INTO `sys_menu` (`id`,`menu_name`,`parent_id`,`order`,`path`,`component`,`query`,`route_name`,`link`,`iframe_src`,`keep_alive`,`menu_type`,`hide_in_menu`,`status`,`perms`,`icon`,`active_icon`,`active_path`,`affix_tab`,`affix_tab_order`,`badge`,`badge_type`,`badge_variants`,`hide_children_in_menu`,`hide_in_breadcrumb`,`hide_in_tab`,`redirect`,`open_in_new_window`,`no_basic_layout`,`max_num_of_open_tab`,`creator_id`,`create_by`,`create_time`,`updater_id`,`update_by`,`update_time`,`remark`) VALUES (1060,'生成代码',116,6,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:code','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');

-- Table: sys_message
DROP TABLE IF EXISTS `sys_message`;
CREATE TABLE `sys_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint DEFAULT NULL COMMENT '发送人',
  `receiver_id` bigint DEFAULT NULL COMMENT '接收人',
  `title` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息标题',
  `content` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `category` int NOT NULL COMMENT '消息类别',
  `message_level` int NOT NULL DEFAULT '0' COMMENT '消息级别',
  `module` int NOT NULL DEFAULT '0' COMMENT '消息模块',
  `business_type` int DEFAULT NULL COMMENT '业务类型',
  `business_id` bigint DEFAULT NULL COMMENT '业务记录ID',
  `business_url` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务链接',
  `read_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '阅读状态（0未读 1已读）',
  `read_time` timestamp(6) NULL DEFAULT NULL COMMENT '阅读时间',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_message_receiver_read` (`receiver_id`,`del_flag`,`read_status`),
  KEY `idx_message_receiver_time` (`receiver_id`,`del_flag`,`create_time`,`id`),
  CONSTRAINT `chk_sys_message_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_message_read_status` CHECK ((`read_status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='消息';

-- Table: sys_message_template
DROP TABLE IF EXISTS `sys_message_template`;
CREATE TABLE `sys_message_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板编码',
  `template_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `title_template` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息标题模板',
  `content_template` text COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容模板',
  `category` int NOT NULL COMMENT '消息类别',
  `message_level` int NOT NULL DEFAULT '0' COMMENT '消息级别',
  `status` char(1) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_message_template_code_active` (((case when (`del_flag` = 0) then `template_code` else NULL end))),
  CONSTRAINT `chk_sys_message_template_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_message_template_status` CHECK ((`status` in (_utf8mb4'0',_utf8mb4'1')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='消息模板';

-- Table: sys_notice
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `link` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '跳转链接',
  `avatar` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '通知头像地址',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者用户ID',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者用户ID',
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_sys_notice_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_notice_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';

INSERT INTO `sys_notice` (`id`,`notice_title`,`notice_type`,`notice_content`,`status`,`del_flag`,`link`,`avatar`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (1,'温馨提醒：2018-07-01 新版本发布啦','2',X'e696b0e78988e69cace58685e5aeb9',1,0,NULL,'https://avatar.vercel.sh/1','admin','2026-07-29T13:45:31','',NULL,'管理员',NULL,NULL);
INSERT INTO `sys_notice` (`id`,`notice_title`,`notice_type`,`notice_content`,`status`,`del_flag`,`link`,`avatar`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (2,'维护通知：2018-07-01 系统凌晨维护','1',X'e7bbb4e68aa4e58685e5aeb9',1,0,NULL,'https://avatar.vercel.sh/vercel.svg?text=VB','admin','2026-07-29T13:45:31','',NULL,'管理员',NULL,NULL);
INSERT INTO `sys_notice` (`id`,`notice_title`,`notice_type`,`notice_content`,`status`,`del_flag`,`link`,`avatar`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (3,'框架介绍','1',X'3c703e3c7370616e207374796c653d22636f6c6f723a20726762283233302c20302c2030293b223ee9a1b9e79baee4bb8be7bb8d3c2f7370616e3e3c2f703e3c703e3c7370616e207374796c653d22636f6c6f723a207267622835312c2035312c203531293b223ee5b297e4bd8de7aea1e79086e38081e5ae9ae697b6e4bbbbe58aa1e38081e69c8de58aa1e79b91e68ea7e38081e799bbe5bd95e697a5e5bf97e38081e6938de4bd9ce697a5e5bf97e38081e4bba3e7a081e7949fe68890e7ad89e58a9fe883bde38082e585b6e4b8adefbc8ce8bf98e694afe68c81e5a49ae695b0e68daee6ba90e38081e695b0e68daee69d83e99990e38081e59bbde99985e58c96e380815265646973e7bc93e5ad98e38081446f636b6572e983a8e7bdb2e38081e6bb91e58aa8e9aa8ce8af81e7a081e38081e7acace4b889e696b9e8aea4e8af81e799bbe5bd95e38081e58886e5b883e5bc8fe4ba8be58aa1e38081e38081e58886e5ba93e58886e8a1a8e5a484e79086e7ad89e68a80e69cafe789b9e782b9e380823c2f7370616e3e3c2f703e3c703e3c62723e3c2f703e3c703e3c6272207374796c653d22636f6c6f723a207267622834382c2034392c203531293b20666f6e742d66616d696c793a202671756f743b48656c766574696361204e6575652671756f743b2c2048656c7665746963612c20417269616c2c2073616e732d73657269663b20666f6e742d73697a653a20313270783b223e3c2f703e',1,0,NULL,'https://avatar.vercel.sh/1','admin','2026-07-29T13:45:31','admin','2026-07-30T17:46:30','管理员',NULL,NULL);
INSERT INTO `sys_notice` (`id`,`notice_title`,`notice_type`,`notice_content`,`status`,`del_flag`,`link`,`avatar`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (4,'跳转外部链接示例','1',X'30000000000000',1,0,'https://admin-docs.geoearth.dev','https://avatar.vercel.sh/satori','admin','2026-07-29T13:45:31','admin','2026-07-30T17:46:30','管理员',NULL,NULL);

-- Table: sys_notice_read
DROP TABLE IF EXISTS `sys_notice_read`;
CREATE TABLE `sys_notice_read` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '已读主键',
  `notice_id` int NOT NULL COMMENT '公告id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `read_time` datetime NOT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_notice` (`user_id`,`notice_id`) COMMENT '同一用户同一公告只记录一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告已读记录表';

-- Table: sys_operation_log
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '方法名称',
  `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '请求方式',
  `operator_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
  `request_uri` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求URL',
  `ip_address` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '主机地址',
  `operation_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作地点',
  `operation_time` timestamp NULL DEFAULT NULL COMMENT '操作时间',
  `request_params` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '请求参数',
  `response_body` json DEFAULT NULL COMMENT '返回参数',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '结果（0失败 1成功）',
  `error_message` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '错误消息',
  `duration_ms` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_sys_operation_log_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';

-- Table: sys_post
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者用户ID',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_code_active` (((case when (`del_flag` = 0) then `post_code` else NULL end))),
  UNIQUE KEY `uk_post_name_active` (((case when (`del_flag` = 0) then `post_name` else NULL end))),
  CONSTRAINT `chk_sys_post_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_post_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
INSERT INTO `sys_post` (`id`,`post_code`,`post_name`,`post_sort`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (1,'ceo','董事长',1,1,0,'admin','2026-07-29 13:45:29','',NULL,'',NULL,NULL);
INSERT INTO `sys_post` (`id`,`post_code`,`post_name`,`post_sort`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (2,'se','项目经理',2,1,0,'admin','2026-07-29 13:45:29','',NULL,'',NULL,NULL);
INSERT INTO `sys_post` (`id`,`post_code`,`post_name`,`post_sort`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (3,'hr','人力资源',3,1,0,'admin','2026-07-29 13:45:29','',NULL,'',NULL,NULL);
INSERT INTO `sys_post` (`id`,`post_code`,`post_name`,`post_sort`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (4,'user','普通员工',4,1,0,'admin','2026-07-29 13:45:29','',NULL,'',NULL,NULL);

-- Table: sys_role
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_linked` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_linked` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者用户ID',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key_active` (((case when (`del_flag` = 0) then `role_key` else NULL end))),
  UNIQUE KEY `uk_role_name_active` (((case when (`del_flag` = 0) then `role_name` else NULL end))),
  CONSTRAINT `chk_sys_role_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_role_dept_check_linked` CHECK ((`dept_check_linked` in (0,1))),
  CONSTRAINT `chk_sys_role_menu_check_linked` CHECK ((`menu_check_linked` in (0,1))),
  CONSTRAINT `chk_sys_role_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
INSERT INTO `sys_role` (`id`,`role_name`,`role_key`,`role_sort`,`data_scope`,`menu_check_linked`,`dept_check_linked`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (1,'超级管理员','admin',1,'1',1,1,1,0,'admin','2026-07-29 13:45:29','',NULL,'超级管理员',NULL,NULL);
INSERT INTO `sys_role` (`id`,`role_name`,`role_key`,`role_sort`,`data_scope`,`menu_check_linked`,`dept_check_linked`,`status`,`del_flag`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (2,'普通角色','common',2,'2',0,0,1,0,'admin','2026-07-29 13:45:29','系统管理员','2026-09-17 15:58:39','普通角色',NULL,1);

-- Table: sys_role_dept
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
INSERT INTO `sys_role_dept` (`role_id`,`dept_id`) VALUES (2,100);
INSERT INTO `sys_role_dept` (`role_id`,`dept_id`) VALUES (2,101);
INSERT INTO `sys_role_dept` (`role_id`,`dept_id`) VALUES (2,105);

-- Table: sys_role_menu
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,2);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,3);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,100);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,101);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,102);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,103);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,104);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,105);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,106);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,107);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,108);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,109);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,110);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,111);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,112);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,113);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,114);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,115);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,116);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,117);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,500);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,501);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1000);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1001);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1002);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1003);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1004);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1005);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1006);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1007);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1008);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1009);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1010);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1011);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1012);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1013);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1014);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1015);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1016);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1017);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1018);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1019);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1020);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1021);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1022);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1023);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1024);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1025);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1026);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1027);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1028);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1029);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1030);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1031);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1032);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1033);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1034);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1035);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1036);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1037);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1038);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1039);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1040);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1041);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1042);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1043);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1044);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1045);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1046);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1047);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1048);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1049);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1050);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1051);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1052);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1053);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1054);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1055);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1056);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1057);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1058);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1059);
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES (2,1060);

-- Table: sys_user
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `auth_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '认证平台id',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phone_number` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '状态（0停用 1启用）',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `last_login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `password_update_time` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator_id` bigint DEFAULT NULL COMMENT '创建者用户ID',
  `updater_id` bigint DEFAULT NULL COMMENT '更新者用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_name_active` (((case when (`del_flag` = 0) then `user_name` else NULL end))),
  UNIQUE KEY `uk_user_phone_active` (((case when (`del_flag` = 0) then nullif(`phone_number`,_utf8mb4'') else NULL end))),
  UNIQUE KEY `uk_user_email_active` (((case when (`del_flag` = 0) then nullif(`email`,_utf8mb4'') else NULL end))),
  CONSTRAINT `chk_sys_user_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_user_status` CHECK ((`status` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
INSERT INTO `sys_user` (`id`,`dept_id`,`auth_id`,`user_name`,`nick_name`,`user_type`,`email`,`phone_number`,`sex`,`avatar`,`password`,`status`,`del_flag`,`last_login_ip`,`last_login_time`,`password_update_time`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (1,103,'','admin','系统管理员','00','admin@163.com','15888888888','0','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',1,0,'',NULL,'2026-07-29 13:45:29','admin','2026-07-29 13:45:29','','2026-07-30 17:23:29','管理员',NULL,NULL);
INSERT INTO `sys_user` (`id`,`dept_id`,`auth_id`,`user_name`,`nick_name`,`user_type`,`email`,`phone_number`,`sex`,`avatar`,`password`,`status`,`del_flag`,`last_login_ip`,`last_login_time`,`password_update_time`,`create_by`,`create_time`,`update_by`,`update_time`,`remark`,`creator_id`,`updater_id`) VALUES (2,105,'','user','用户','00','user@qq.com','15666666666','1','','{bcrypt}$2a$10$UJPAhB.Pj6BuDrZCYF2UPOHGC9Uw8oeeJ62pmGbbYZofeK3Bevhjy',1,0,'',NULL,'2026-09-22 14:37:21','admin','2026-07-29 13:45:29','用户','2026-09-22 14:37:21','测试员',NULL,2);

-- Table: sys_user_auth_product
DROP TABLE IF EXISTS `sys_user_auth_product`;
CREATE TABLE `sys_user_auth_product` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `auth_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统一身份认证id',
  `auth_product_type` int NOT NULL DEFAULT '0' COMMENT '认证平台类型;0：冰凤框架；1：微信；2：支付宝',
  PRIMARY KEY (`user_id`,`auth_id`,`auth_product_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户与认证中心关系表';

-- Table: sys_user_post
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
INSERT INTO `sys_user_post` (`user_id`,`post_id`) VALUES (1,1);
INSERT INTO `sys_user_post` (`user_id`,`post_id`) VALUES (2,2);

-- Table: sys_user_role
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
INSERT INTO `sys_user_role` (`user_id`,`role_id`) VALUES (1,1);
INSERT INTO `sys_user_role` (`user_id`,`role_id`) VALUES (2,2);

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET time_zone=@OLD_TIME_ZONE;
SET SQL_MODE=@OLD_SQL_MODE;
