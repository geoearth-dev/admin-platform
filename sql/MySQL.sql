-- 当前本地数据库完整快照（结构和现有数据），2026-09-20 同步。
-- 已包含字段规范化及岗位、公告软删除；本机已迁移，无需重复执行增量脚本。
-- 本文件包含 DROP TABLE，仅用于初始化/恢复，勿作为已有库的增量升级脚本执行。
-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: admin_platform
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
INSERT INTO `gen_table` VALUES (1,'sys_config','参数配置表',NULL,NULL,'SysConfig','crud','dev.geo.admin.system','system','config','参数配置','geo',1,'0','/',NULL,'admin','2026-09-20 11:30:37','',NULL,NULL);
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
INSERT INTO `gen_table_column` VALUES (1,1,'id','参数主键','int','Integer','id',1,1,0,1,0,0,0,'EQ','input','',1,'admin','2026-09-20 11:30:37','',NULL),(2,1,'config_name','参数名称','varchar(100)','String','configName',0,0,0,1,1,1,1,'LIKE','input','',2,'admin','2026-09-20 11:30:37','',NULL),(3,1,'config_key','参数键名','varchar(100)','String','configKey',0,0,0,1,1,1,1,'EQ','input','',3,'admin','2026-09-20 11:30:37','',NULL),(4,1,'config_value','参数键值','varchar(500)','String','configValue',0,0,0,1,1,1,1,'EQ','textarea','',4,'admin','2026-09-20 11:30:37','',NULL),(5,1,'config_type','系统内置（Y是 N否）','char(1)','String','configType',0,0,0,1,1,1,1,'EQ','select','',5,'admin','2026-09-20 11:30:37','',NULL),(6,1,'creator_id','创建者用户ID','bigint','Long','creatorId',0,0,0,0,0,1,0,'EQ','input','',6,'admin','2026-09-20 11:30:37','',NULL),(7,1,'create_by','创建者','varchar(64)','String','createBy',0,0,0,0,0,0,0,'EQ','input','',7,'admin','2026-09-20 11:30:37','',NULL),(8,1,'create_time','创建时间','datetime','Instant','createTime',0,0,0,0,0,0,0,'EQ','datetime','',8,'admin','2026-09-20 11:30:37','',NULL),(9,1,'updater_id','更新者用户ID','bigint','Long','updaterId',0,0,0,0,0,1,0,'EQ','input','',9,'admin','2026-09-20 11:30:37','',NULL),(10,1,'update_by','更新者','varchar(64)','String','updateBy',0,0,0,0,0,0,0,'EQ','input','',10,'admin','2026-09-20 11:30:37','',NULL),(11,1,'update_time','更新时间','datetime','Instant','updateTime',0,0,0,0,0,0,0,'EQ','datetime','',11,'admin','2026-09-20 11:30:37','',NULL),(12,1,'remark','备注','varchar(500)','String','remark',0,0,0,1,1,1,0,'EQ','textarea','',12,'admin','2026-09-20 11:30:37','',NULL);
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','true',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框'),(9,'用户管理-密码字符范围','sys.account.passwordCharRange','0',1,NULL,'admin','2026-07-29 13:45:30',NULL,'',NULL,'默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数字和特殊字符（目前支持的特殊字符包括：~!@#$%^&*()-=_+）');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_content`
--

DROP TABLE IF EXISTS `sys_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_content`
--

LOCK TABLES `sys_content` WRITE;
/*!40000 ALTER TABLE `sys_content` DISABLE KEYS */;
INSERT INTO `sys_content` VALUES (1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,NULL,NULL,'2026-08-31 14:57:32',NULL,NULL,NULL,NULL,'{\"copyrightHttp\":\"https://qiantong.tech\",\"systemMenuFullLogo\":\"\",\"systemMenuLogo\":\"\"}');
/*!40000 ALTER TABLE `sys_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','信易源',0,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL),(101,100,'0,100','重庆',1,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL),(102,100,'0,100','北京',2,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL),(103,101,'0,100,101','研发部门',1,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL),(104,101,'0,100,101','市场部门',2,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL),(105,101,'0,100,101','测试部门',3,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL),(106,101,'0,100,101','财务部门',4,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL),(107,101,'0,100,101','运维部门',5,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:28','',NULL,NULL,NULL),(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:29','',NULL,NULL,NULL),(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com',1,0,'admin','2026-07-29 13:45:29','',NULL,NULL,NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','性别男',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(2,2,'女','1','sys_user_sex','','','性别女',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(3,3,'未知','2','sys_user_sex','','','性别未知',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(4,1,'显示','0','sys_show_hide','','primary','显示菜单',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(5,2,'隐藏','1','sys_show_hide','','danger','隐藏菜单',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(6,1,'正常','1','sys_normal_disable','','primary','正常状态',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(7,2,'停用','0','sys_normal_disable','','danger','停用状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(8,1,'正常','1','sys_job_status','','primary','正常状态',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(9,2,'暂停','0','sys_job_status','','danger','停用状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(10,1,'默认','DEFAULT','sys_job_group','','','默认分组',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(11,2,'系统','SYSTEM','sys_job_group','','','系统分组',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(12,1,'是','1','sys_yes_no','','primary','系统默认是',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(13,2,'否','0','sys_yes_no','','danger','系统默认否',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(14,1,'通知','1','sys_notice_type','','warning','通知',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(15,2,'公告','2','sys_notice_type','','success','公告',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(16,1,'正常','1','sys_notice_status','','primary','正常状态',1,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(17,2,'关闭','0','sys_notice_status','','danger','关闭状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(18,99,'其他','0','sys_oper_type','','info','其他操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(19,1,'新增','1','sys_oper_type','','info','新增操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(20,2,'修改','2','sys_oper_type','','info','修改操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(21,3,'删除','3','sys_oper_type','','danger','删除操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(22,4,'授权','4','sys_oper_type','','primary','授权操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(23,5,'导出','5','sys_oper_type','','warning','导出操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(24,6,'导入','6','sys_oper_type','','warning','导入操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(25,7,'强退','7','sys_oper_type','','danger','强退操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(26,8,'生成代码','8','sys_oper_type','','warning','生成操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(27,9,'清空数据','9','sys_oper_type','','danger','清空操作',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(28,1,'成功','1','sys_common_status','','primary','正常状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL),(29,2,'失败','0','sys_common_status','','danger','停用状态',0,1,'admin','2026-07-29 13:45:30','',NULL,NULL,NULL);
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex',1,'admin','2026-07-29 13:45:30','',NULL,'用户性别列表',NULL,NULL),(2,'菜单状态','sys_show_hide',1,'admin','2026-07-29 13:45:30','',NULL,'菜单状态列表',NULL,NULL),(3,'系统开关','sys_normal_disable',1,'admin','2026-07-29 13:45:30','',NULL,'系统开关列表',NULL,NULL),(4,'任务状态','sys_job_status',1,'admin','2026-07-29 13:45:30','',NULL,'任务状态列表',NULL,NULL),(5,'任务分组','sys_job_group',1,'admin','2026-07-29 13:45:30','',NULL,'任务分组列表',NULL,NULL),(6,'系统是否','sys_yes_no',1,'admin','2026-07-29 13:45:30','',NULL,'系统是否列表',NULL,NULL),(7,'通知类型','sys_notice_type',1,'admin','2026-07-29 13:45:30','',NULL,'通知类型列表',NULL,NULL),(8,'通知状态','sys_notice_status',1,'admin','2026-07-29 13:45:30','',NULL,'通知状态列表',NULL,NULL),(9,'操作类型','sys_oper_type',1,'admin','2026-07-29 13:45:30','',NULL,'操作类型列表',NULL,NULL),(10,'系统状态','sys_common_status',1,'admin','2026-07-29 13:45:30','',NULL,'登录状态列表',NULL,NULL);
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','adminTask.taskNoParams','0/10 * * * * ?','3',0,0,'admin','2026-07-29 13:45:31','','2026-09-17 16:17:45',''),(2,'系统默认（有参）','DEFAULT','adminTask.taskParams(\'task\')','0/15 * * * * ?','3',0,0,'admin','2026-07-29 13:45:31','',NULL,''),(3,'系统默认（多参）','DEFAULT','adminTask.taskMultipleParams(\'task\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3',0,0,'admin','2026-07-29 13:45:31','',NULL,'');
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
INSERT INTO `sys_job_log` VALUES (3,'系统默认（无参）','DEFAULT','adminTask.taskNoParams','系统默认（无参） 总共耗时：2毫秒',1,NULL,'2026-09-17 16:16:50','2026-09-17 16:16:50','2026-09-17 16:16:50'),(4,'系统默认（无参）','DEFAULT','adminTask.taskNoParams','系统默认（无参） 总共耗时：0毫秒',1,NULL,'2026-09-17 16:17:00','2026-09-17 16:17:00','2026-09-17 16:17:00'),(5,'系统默认（无参）','DEFAULT','adminTask.taskNoParams','系统默认（无参） 总共耗时：0毫秒',1,NULL,'2026-09-17 16:17:10','2026-09-17 16:17:10','2026-09-17 16:17:10'),(6,'系统默认（无参）','DEFAULT','adminTask.taskNoParams','系统默认（无参） 总共耗时：0毫秒',1,NULL,'2026-09-17 16:17:20','2026-09-17 16:17:20','2026-09-17 16:17:20'),(7,'系统默认（无参）','DEFAULT','adminTask.taskNoParams','系统默认（无参） 总共耗时：0毫秒',1,NULL,'2026-09-17 16:17:30','2026-09-17 16:17:30','2026-09-17 16:17:30'),(8,'系统默认（无参）','DEFAULT','adminTask.taskNoParams','系统默认（无参） 总共耗时：0毫秒',1,NULL,'2026-09-17 16:17:40','2026-09-17 16:17:40','2026-09-17 16:17:40'),(9,'系统默认（无参）','DEFAULT','adminTask.taskNoParams','系统默认（无参） 总共耗时：0毫秒',1,NULL,'2026-09-17 16:17:49','2026-09-17 16:17:49','2026-09-17 16:17:49');
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_login_log`
--

DROP TABLE IF EXISTS `sys_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_login_log`
--

LOCK TABLES `sys_login_log` WRITE;
/*!40000 ALTER TABLE `sys_login_log` DISABLE KEYS */;
INSERT INTO `sys_login_log` VALUES (59,'admin','127.0.0.1','内网IP','Chrome 153','Windows10',1,'登录成功','2026-09-15 09:58:51'),(60,'admin','127.0.0.1','内网IP','Chrome 153','Windows10',1,'登录成功','2026-09-16 01:23:03'),(61,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10',1,'登录成功','2026-09-16 13:31:14'),(62,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10',1,'登录成功','2026-09-16 13:33:36'),(63,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10',0,'验证码错误','2026-09-16 14:25:01'),(64,'admin','127.0.0.1','内网IP','Edge 146','Windows >=10',1,'登录成功','2026-09-16 14:25:02'),(65,'admin','127.0.0.1','内网IP','Chrome 153','Windows10',1,'登录成功','2026-09-18 03:06:20'),(66,'admin','127.0.0.1','内网IP','Chrome 153','Windows10',1,'登录成功','2026-09-20 06:49:37');
/*!40000 ALTER TABLE `sys_login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2002 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,1,'system',NULL,NULL,'','',NULL,0,'catalog',0,1,'','lucide:settings',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'系统管理目录'),(2,'系统监控',0,2,'monitor',NULL,NULL,'','',NULL,0,'catalog',0,1,'','lucide:activity',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'系统监控目录'),(3,'系统工具',0,3,'tool',NULL,NULL,'','',NULL,0,'catalog',0,1,'','lucide:wrench',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'系统工具目录'),(100,'用户管理',1,1,'user','sys/system/user/index',NULL,'','',NULL,0,'menu',0,1,'system:user:list','lucide:user-round',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','sys/system/role/index',NULL,'','',NULL,0,'menu',0,1,'system:role:list','lucide:users-round',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','sys/system/menu/index',NULL,'','',NULL,0,'menu',0,1,'system:menu:list','lucide:list-tree',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','sys/system/dept/index',NULL,'','',NULL,0,'menu',0,1,'system:dept:list','lucide:network',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','sys/system/post/index',NULL,'','',NULL,0,'menu',0,1,'system:post:list','lucide:contact-round',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'岗位管理菜单'),(105,'字典管理',1,6,'dict','sys/system/dict/index',NULL,'','',NULL,0,'menu',0,1,'system:dict:list','lucide:book-open',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','sys/system/config/index',NULL,'','',NULL,0,'menu',0,1,'system:config:list','lucide:settings-2',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','sys/system/notice/index',NULL,'','',NULL,0,'menu',0,1,'system:notice:list','lucide:bell',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'通知公告菜单'),(108,'日志管理',1,9,'log','',NULL,'','',NULL,0,'catalog',0,1,'','lucide:logs',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','sys/monitor/online/index',NULL,'','',NULL,0,'menu',0,1,'monitor:online:list','lucide:monitor-check',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','sys/monitor/job/index',NULL,'','',NULL,0,'menu',0,1,'monitor:job:list','lucide:clock',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','',NULL,'','','/api/druid/index.html',1,'embedded',0,1,'monitor:druid:list','lucide:database',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','sys/monitor/server/index',NULL,'','',NULL,0,'menu',0,1,'monitor:server:list','lucide:server',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','sys/monitor/cache/index',NULL,'','',NULL,0,'menu',0,1,'monitor:cache:list','lucide:database-zap',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','sys/monitor/cache/list',NULL,'','',NULL,0,'menu',0,1,'monitor:cache:list','lucide:list',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'缓存列表菜单'),(115,'表单构建',3,1,'build','sys/tool/build/index',NULL,'','',NULL,0,'menu',0,1,'tool:build:list','lucide:panels-top-left',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'表单构建菜单'),(116,'代码生成',3,2,'gen','sys/tool/gen/index',NULL,'','',NULL,0,'menu',0,1,'tool:gen:list','lucide:code-xml',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'代码生成菜单'),(117,'系统接口',3,3,'doc','',NULL,'','','http://192.168.1.24:8080/doc',0,'embedded',0,1,'tool:swagger:list','lucide:cable',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','sys/system/log/operlog/index',NULL,'','',NULL,0,'menu',0,1,'monitor:operlog:list','lucide:file-clock',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'操作日志菜单'),(501,'登录日志',108,2,'loginlog','sys/system/log/loginlog/index',NULL,'','',NULL,0,'menu',0,1,'monitor:logininfor:list','lucide:log-in',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','',NULL,'','',NULL,0,'button',0,1,'system:user:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1001,'用户新增',100,2,'','',NULL,'','',NULL,0,'button',0,1,'system:user:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1002,'用户修改',100,3,'','',NULL,'','',NULL,0,'button',0,1,'system:user:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1003,'用户删除',100,4,'','',NULL,'','',NULL,0,'button',0,1,'system:user:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1004,'用户导出',100,5,'','',NULL,'','',NULL,0,'button',0,1,'system:user:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1005,'用户导入',100,6,'','',NULL,'','',NULL,0,'button',0,1,'system:user:import','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1006,'重置密码',100,7,'','',NULL,'','',NULL,0,'button',0,1,'system:user:resetPwd','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1007,'角色查询',101,1,'','',NULL,'','',NULL,0,'button',0,1,'system:role:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1008,'角色新增',101,2,'','',NULL,'','',NULL,0,'button',0,1,'system:role:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1009,'角色修改',101,3,'','',NULL,'','',NULL,0,'button',0,1,'system:role:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1010,'角色删除',101,4,'','',NULL,'','',NULL,0,'button',0,1,'system:role:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1011,'角色导出',101,5,'','',NULL,'','',NULL,0,'button',0,1,'system:role:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1012,'菜单查询',102,1,'','',NULL,'','',NULL,0,'button',0,1,'system:menu:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1013,'菜单新增',102,2,'','',NULL,'','',NULL,0,'button',0,1,'system:menu:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1014,'菜单修改',102,3,'','',NULL,'','',NULL,0,'button',0,1,'system:menu:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1015,'菜单删除',102,4,'','',NULL,'','',NULL,0,'button',0,1,'system:menu:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1016,'部门查询',103,1,'','',NULL,'','',NULL,0,'button',0,1,'system:dept:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1017,'部门新增',103,2,'','',NULL,'','',NULL,0,'button',0,1,'system:dept:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1018,'部门修改',103,3,'','',NULL,'','',NULL,0,'button',0,1,'system:dept:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1019,'部门删除',103,4,'','',NULL,'','',NULL,0,'button',0,1,'system:dept:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1020,'岗位查询',104,1,'','',NULL,'','',NULL,0,'button',0,1,'system:post:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1021,'岗位新增',104,2,'','',NULL,'','',NULL,0,'button',0,1,'system:post:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1022,'岗位修改',104,3,'','',NULL,'','',NULL,0,'button',0,1,'system:post:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1023,'岗位删除',104,4,'','',NULL,'','1',NULL,0,'button',0,1,'system:post:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1024,'岗位导出',104,5,'','',NULL,'','1',NULL,0,'button',0,1,'system:post:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1025,'字典查询',105,1,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1026,'字典新增',105,2,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1027,'字典修改',105,3,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1028,'字典删除',105,4,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1029,'字典导出',105,5,'#','',NULL,'','1',NULL,0,'button',0,1,'system:dict:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1030,'参数查询',106,1,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1031,'参数新增',106,2,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1032,'参数修改',106,3,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1033,'参数删除',106,4,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1034,'参数导出',106,5,'#','',NULL,'','1',NULL,0,'button',0,1,'system:config:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1035,'公告查询',107,1,'#','',NULL,'','1',NULL,0,'button',0,1,'system:notice:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1036,'公告新增',107,2,'#','',NULL,'','1',NULL,0,'button',0,1,'system:notice:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1037,'公告修改',107,3,'#','',NULL,'','1',NULL,0,'button',0,1,'system:notice:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1038,'公告删除',107,4,'#','',NULL,'','1',NULL,0,'button',0,1,'system:notice:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1039,'操作查询',500,1,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:operlog:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1040,'操作删除',500,2,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:operlog:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1041,'日志导出',500,3,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:operlog:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1042,'登录查询',501,1,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:logininfor:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1043,'登录删除',501,2,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:logininfor:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1044,'日志导出',501,3,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:logininfor:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1045,'账户解锁',501,4,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:logininfor:unlock','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1046,'在线查询',109,1,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:online:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1047,'批量强退',109,2,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:online:batchLogout','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1048,'单条强退',109,3,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:online:forceLogout','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1049,'任务查询',110,1,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1050,'任务新增',110,2,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:add','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1051,'任务修改',110,3,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1052,'任务删除',110,4,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1053,'状态修改',110,5,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:changeStatus','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1054,'任务导出',110,6,'#','',NULL,'','1',NULL,0,'button',0,1,'monitor:job:export','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1055,'生成查询',116,1,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:query','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1056,'生成修改',116,2,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:edit','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1057,'生成删除',116,3,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:remove','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1058,'导入代码',116,4,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:import','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1059,'预览代码',116,5,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:preview','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,''),(1060,'生成代码',116,6,'#','',NULL,'','1',NULL,0,'button',0,1,'tool:gen:code','#',NULL,NULL,0,0,NULL,NULL,NULL,0,0,0,NULL,0,0,-1,NULL,'admin','2026-07-29 13:45:29',NULL,'',NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_message`
--

DROP TABLE IF EXISTS `sys_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_message` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `sender_id` bigint DEFAULT NULL COMMENT '发送人',
  `receiver_id` bigint DEFAULT NULL COMMENT '接收人',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息标题',
  `content` varchar(3072) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息模板内容',
  `category` int NOT NULL COMMENT '消息类别',
  `msg_level` int NOT NULL DEFAULT '0' COMMENT '消息等级',
  `module` int NOT NULL DEFAULT '0' COMMENT '消息模块',
  `entity_type` int DEFAULT NULL COMMENT '实体类型',
  `entity_id` bigint DEFAULT NULL COMMENT '实体id',
  `entity_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '消息链接',
  `has_read` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否已读',
  `has_retraction` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否撤回',
  `valid_flag` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `chk_sys_message_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_message_has_read` CHECK ((`has_read` in (0,1))),
  CONSTRAINT `chk_sys_message_has_retraction` CHECK ((`has_retraction` in (0,1))),
  CONSTRAINT `chk_sys_message_valid_flag` CHECK ((`valid_flag` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='消息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_message`
--

LOCK TABLES `sys_message` WRITE;
/*!40000 ALTER TABLE `sys_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_message_template`
--

DROP TABLE IF EXISTS `sys_message_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_message_template` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息标题',
  `content` varchar(3072) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息模板内容',
  `category` int NOT NULL COMMENT '消息类别',
  `msg_level` int NOT NULL DEFAULT '0' COMMENT '消息等级',
  `valid_flag` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '是否有效;0：无效，1：有效',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '删除标志（0未删除 1已删除）',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建人',
  `creator_id` bigint DEFAULT NULL COMMENT '创建人id',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新人',
  `updater_id` bigint DEFAULT NULL COMMENT '更新人id',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `chk_sys_message_template_del_flag` CHECK ((`del_flag` in (0,1))),
  CONSTRAINT `chk_sys_message_template_valid_flag` CHECK ((`valid_flag` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='消息模板';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_message_template`
--

LOCK TABLES `sys_message_template` WRITE;
/*!40000 ALTER TABLE `sys_message_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_message_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 新版本发布啦','2',0xE696B0E78988E69CACE58685E5AEB9,1,0,NULL,'https://avatar.vercel.sh/1','admin','2026-07-29 13:45:31','',NULL,'管理员',NULL,NULL),(2,'维护通知：2018-07-01 系统凌晨维护','1',0xE7BBB4E68AA4E58685E5AEB9,1,0,NULL,'https://avatar.vercel.sh/vercel.svg?text=VB','admin','2026-07-29 13:45:31','',NULL,'管理员',NULL,NULL),(3,'框架介绍','1',0x3C703E3C7370616E207374796C653D22636F6C6F723A20726762283233302C20302C2030293B223EE9A1B9E79BAEE4BB8BE7BB8D3C2F7370616E3E3C2F703E3C703E3C7370616E207374796C653D22636F6C6F723A207267622835312C2035312C203531293B223EE5B297E4BD8DE7AEA1E79086E38081E5AE9AE697B6E4BBBBE58AA1E38081E69C8DE58AA1E79B91E68EA7E38081E799BBE5BD95E697A5E5BF97E38081E6938DE4BD9CE697A5E5BF97E38081E4BBA3E7A081E7949FE68890E7AD89E58A9FE883BDE38082E585B6E4B8ADEFBC8CE8BF98E694AFE68C81E5A49AE695B0E68DAEE6BA90E38081E695B0E68DAEE69D83E99990E38081E59BBDE99985E58C96E380815265646973E7BC93E5AD98E38081446F636B6572E983A8E7BDB2E38081E6BB91E58AA8E9AA8CE8AF81E7A081E38081E7ACACE4B889E696B9E8AEA4E8AF81E799BBE5BD95E38081E58886E5B883E5BC8FE4BA8BE58AA1E38081E38081E58886E5BA93E58886E8A1A8E5A484E79086E7AD89E68A80E69CAFE789B9E782B9E380823C2F7370616E3E3C2F703E3C703E3C62723E3C2F703E3C703E3C6272207374796C653D22636F6C6F723A207267622834382C2034392C203531293B20666F6E742D66616D696C793A202671756F743B48656C766574696361204E6575652671756F743B2C2048656C7665746963612C20417269616C2C2073616E732D73657269663B20666F6E742D73697A653A20313270783B223E3C2F703E,1,0,NULL,'https://avatar.vercel.sh/1','admin','2026-07-29 13:45:31','admin','2026-07-30 17:46:30','管理员',NULL,NULL),(4,'跳转外部链接示例','1',0x30000000000000,1,0,'https://doc.vben.pro','https://avatar.vercel.sh/satori','admin','2026-07-29 13:45:31','admin','2026-07-30 17:46:30','管理员',NULL,NULL);
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice_read`
--

DROP TABLE IF EXISTS `sys_notice_read`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice_read` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '已读主键',
  `notice_id` int NOT NULL COMMENT '公告id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `read_time` datetime NOT NULL COMMENT '阅读时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_notice` (`user_id`,`notice_id`) COMMENT '同一用户同一公告只记录一次'
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='公告已读记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice_read`
--

LOCK TABLES `sys_notice_read` WRITE;
/*!40000 ALTER TABLE `sys_notice_read` DISABLE KEYS */;
INSERT INTO `sys_notice_read` VALUES (5,4,1,'2026-09-16 11:52:49'),(6,3,1,'2026-09-16 11:59:41');
/*!40000 ALTER TABLE `sys_notice_read` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_operation_log`
--

DROP TABLE IF EXISTS `sys_operation_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_operation_log`
--

LOCK TABLES `sys_operation_log` WRITE;
/*!40000 ALTER TABLE `sys_operation_log` DISABLE KEYS */;
INSERT INTO `sys_operation_log` VALUES (8,'参数管理',9,'dev.geo.admin.system.controller.system.SysConfigController.refreshCache()','DELETE','1','','研发部门','/system/config/refreshCache','127.0.0.1','内网IP','2026-09-15 08:57:54','{}','{\"code\": 200}',1,'',12),(9,'在线会话',7,'dev.geo.admin.system.controller.monitor.SysUserOnlineController.terminateSession()','DELETE','1','','研发部门','/monitor/online-session/813837c2-4d01-4088-8b18-c57ce3249435','127.0.0.1','内网IP','2026-09-16 06:43:20','{}','{\"code\": 200}',1,'',114),(10,'在线会话',7,'dev.geo.admin.system.controller.monitor.SysUserOnlineController.terminateSession()','DELETE','1','','研发部门','/monitor/online-session/2144a140-9d5b-4dd2-a58f-9510c7dc6c77','127.0.0.1','内网IP','2026-09-16 06:43:40','{}','{\"code\": 200}',1,'',1),(11,'在线会话',7,'dev.geo.admin.system.controller.monitor.SysUserOnlineController.terminateSession()','DELETE','1','','研发部门','/monitor/online-session/d192a9e4-c52e-4d4b-9cb4-1d3d2812c112','127.0.0.1','内网IP','2026-09-16 06:43:42','{}','{\"code\": 200}',1,'',0),(12,'在线会话',7,'dev.geo.admin.system.controller.monitor.SysUserOnlineController.terminateSession()','DELETE','1','','研发部门','/monitor/online-session/68adf992-3f3f-4ff3-8871-4690615c18c0','127.0.0.1','内网IP','2026-09-16 06:43:44','{}','{\"code\": 200}',1,'',1),(13,'在线会话',7,'dev.geo.admin.system.controller.monitor.SysUserOnlineController.terminateSession()','DELETE','1','','研发部门','/monitor/online-session/096bc01e-61dc-4a19-bf90-4e4acc603185','127.0.0.1','内网IP','2026-09-16 13:33:16','{}','{\"code\": 200}',1,'',142),(14,'定时任务',1,'dev.geo.admin.quartz.controller.SysJobController.add()','POST','1','','','/monitor/job','127.0.0.1','内网IP','2026-09-17 02:16:11','{\"concurrent\":\"0\",\"createBy\":\"quartz-smoke\",\"cronExpression\":\"0 0 0 1 1 ? 2099\",\"id\":101,\"invokeTarget\":\"ryTask.ryParams(\'quartz smoke\')\",\"jobGroup\":\"QUARTZ_SMOKE\",\"jobName\":\"quartz-smoke-1adec975-5d32-4528-9fb5-47047cde6e10\",\"misfirePolicy\":\"3\",\"nextValidTime\":\"2099-01-01 00:00:00\",\"params\":{},\"status\":\"0\"}','{\"code\": 200}',1,'',235),(15,'定时任务',2,'dev.geo.admin.quartz.controller.SysJobController.edit()','PUT','1','','','/monitor/job','127.0.0.1','内网IP','2026-09-17 02:16:12','{\"concurrent\":\"0\",\"createBy\":\"quartz-smoke\",\"createTime\":\"2026-09-17T02:16:11Z\",\"cronExpression\":\"0 0 0 1 1 ? 2099\",\"id\":101,\"invokeTarget\":\"ryTask.ryParams(\'quartz smoke\')\",\"jobGroup\":\"QUARTZ_SMOKE_EDIT\",\"jobName\":\"quartz-smoke-1adec975-5d32-4528-9fb5-47047cde6e10\",\"misfirePolicy\":\"3\",\"nextValidTime\":\"2099-01-01 00:00:00\",\"params\":{},\"status\":\"0\",\"updateBy\":\"quartz-smoke\"}','{\"code\": 200}',1,'',32),(16,'定时任务',2,'dev.geo.admin.quartz.controller.SysJobController.changeStatus()','PUT','1','','','/monitor/job/changeStatus','127.0.0.1','内网IP','2026-09-17 02:16:12','{\"id\":101,\"params\":{},\"status\":\"1\",\"updateBy\":\"quartz-smoke\"}','{\"code\": 200}',1,'',15),(17,'定时任务',2,'dev.geo.admin.quartz.controller.SysJobController.changeStatus()','PUT','1','','','/monitor/job/changeStatus','127.0.0.1','内网IP','2026-09-17 02:16:12','{\"id\":101,\"params\":{},\"status\":\"0\",\"updateBy\":\"quartz-smoke\"}','{\"code\": 200}',1,'',9),(18,'定时任务',2,'dev.geo.admin.quartz.controller.SysJobController.run()','PUT','1','','','/monitor/job/run','127.0.0.1','内网IP','2026-09-17 02:16:12','{\"id\":101,\"params\":{}}','{\"code\": 200}',1,'',4),(19,'定时任务',3,'dev.geo.admin.quartz.controller.SysJobController.remove()','DELETE','1','','','/monitor/job/101','127.0.0.1','内网IP','2026-09-17 02:16:12','{}','{\"code\": 200}',1,'',11),(20,'角色管理',2,'dev.geo.admin.system.controller.system.SysRoleController.changeStatus()','PUT','1','','研发部门','/system/role/status','127.0.0.1','内网IP','2026-09-17 07:58:36','{\"id\":2,\"status\":\"0\"}','{\"code\": 200}',1,'',34),(21,'角色管理',2,'dev.geo.admin.system.controller.system.SysRoleController.changeStatus()','PUT','1','','研发部门','/system/role/status','127.0.0.1','内网IP','2026-09-17 07:58:39','{\"id\":2,\"status\":\"1\"}','{\"code\": 200}',1,'',36),(22,'定时任务',2,'dev.geo.admin.quartz.controller.SysJobController.run()','PUT','1','','研发部门','/monitor/job/run','127.0.0.1','内网IP','2026-09-17 08:10:15','{\"id\":1,\"jobGroup\":\"DEFAULT\",\"params\":{}}','{\"code\": 200}',1,'',134),(23,'定时任务',2,'dev.geo.admin.quartz.controller.SysJobController.run()','PUT','1','','研发部门','/monitor/job/run','127.0.0.1','内网IP','2026-09-17 08:11:50','{\"id\":1,\"jobGroup\":\"DEFAULT\",\"params\":{}}','{\"code\": 200}',1,'',2),(24,'定时任务',2,'dev.geo.admin.quartz.controller.SysJobController.changeStatus()','PUT','1','','研发部门','/monitor/job/changeStatus','127.0.0.1','内网IP','2026-09-17 08:17:45','{\"id\":1,\"params\":{},\"status\":\"0\"}','{\"code\": 200}',1,'',200),(25,'定时任务',2,'dev.geo.admin.quartz.controller.SysJobController.run()','PUT','1','','研发部门','/monitor/job/run','127.0.0.1','内网IP','2026-09-17 08:17:49','{\"id\":1,\"jobGroup\":\"DEFAULT\",\"params\":{}}','{\"code\": 200}',1,'',5),(26,'操作日志',5,'dev.geo.admin.system.controller.monitor.SysOperationLogController.export()','POST','1','','研发部门','/monitor/operation-log/export','127.0.0.1','内网IP','2026-09-20 03:18:03','{\"pageSize\":\"10\",\"pageNum\":\"1\"}',NULL,1,'',679),(27,'用户管理',5,'dev.geo.admin.system.controller.system.SysUserController.export()','POST','1','','研发部门','/system/user/export','127.0.0.1','内网IP','2026-09-20 03:20:59','{\"pageNum\":1,\"pageSize\":-1,\"params\":{\"dataScope\":\"\"}}',NULL,1,'',47),(28,'代码生成',6,'dev.geo.admin.generator.controller.GenController.importTableSave()','POST','1','','研发部门','/tool/gen/importTable','127.0.0.1','内网IP','2026-09-20 03:30:37','{\"tables\":[\"sys_config\"]}','{\"code\": 200}',1,'',54);
/*!40000 ALTER TABLE `sys_operation_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,1,0,'admin','2026-07-29 13:45:29','',NULL,'',NULL,NULL),(2,'se','项目经理',2,1,0,'admin','2026-07-29 13:45:29','',NULL,'',NULL,NULL),(3,'hr','人力资源',3,1,0,'admin','2026-07-29 13:45:29','',NULL,'',NULL,NULL),(4,'user','普通员工',4,1,0,'admin','2026-07-29 13:45:29','',NULL,'',NULL,NULL);
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,1,0,'admin','2026-07-29 13:45:29','',NULL,'超级管理员',NULL,NULL),(2,'普通角色','common',2,'2',0,0,1,0,'admin','2026-07-29 13:45:29','系统管理员','2026-09-17 15:58:39','普通角色',NULL,1);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (2,1),(2,2),(2,3),(2,4),(2,100),(2,101),(2,102),(2,103),(2,104),(2,105),(2,106),(2,107),(2,108),(2,109),(2,110),(2,111),(2,112),(2,113),(2,114),(2,115),(2,116),(2,117),(2,500),(2,501),(2,1000),(2,1001),(2,1002),(2,1003),(2,1004),(2,1005),(2,1006),(2,1007),(2,1008),(2,1009),(2,1010),(2,1011),(2,1012),(2,1013),(2,1014),(2,1015),(2,1016),(2,1017),(2,1018),(2,1019),(2,1020),(2,1021),(2,1022),(2,1023),(2,1024),(2,1025),(2,1026),(2,1027),(2,1028),(2,1029),(2,1030),(2,1031),(2,1032),(2,1033),(2,1034),(2,1035),(2,1036),(2,1037),(2,1038),(2,1039),(2,1040),(2,1041),(2,1042),(2,1043),(2,1044),(2,1045),(2,1046),(2,1047),(2,1048),(2,1049),(2,1050),(2,1051),(2,1052),(2,1053),(2,1054),(2,1055),(2,1056),(2,1057),(2,1058),(2,1059),(2,1060);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,103,NULL,'admin','系统管理员','00','admin@163.com','15888888888','0','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',1,0,'127.0.0.1','2026-09-20 14:49:37','2026-07-29 13:45:29','admin','2026-07-29 13:45:29','','2026-07-30 17:23:29','管理员',NULL,NULL),(2,105,NULL,'user','用户','00','user@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',1,0,'127.0.0.1','2026-07-29 13:45:29','2026-07-29 13:45:29','admin','2026-07-29 13:45:29','系统管理员','2026-09-10 23:35:23','测试员',NULL,1);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_auth_product`
--

DROP TABLE IF EXISTS `sys_user_auth_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_auth_product` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `auth_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '统一身份认证id',
  `auth_product_type` int NOT NULL DEFAULT '0' COMMENT '认证平台类型;0：冰凤框架；1：微信；2：支付宝',
  PRIMARY KEY (`user_id`,`auth_id`,`auth_product_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='用户与认证中心关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_auth_product`
--

LOCK TABLES `sys_user_auth_product` WRITE;
/*!40000 ALTER TABLE `sys_user_auth_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_user_auth_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'admin_platform'
--

--
-- Dumping routines for database 'admin_platform'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-20 17:24:29
