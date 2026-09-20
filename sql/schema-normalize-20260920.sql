-- 2026-09-20 数据库规范化；适用于迁移前的 admin_platform，执行一次。
-- 执行前停用旧版本应用并完整备份。MySQL DDL 会隐式提交，失败时不可仅靠 ROLLBACK 恢复。
-- 二值标识统一为 TINYINT UNSIGNED NOT NULL，0=否/未删除，1=是/已删除。
-- 原有 NULL 状态按 0（停用）补齐；其他空标识按字段默认值补齐。
-- 分类枚举（性别、公告类型、任务策略等）不属于二值标识，保持原值。
SET NAMES utf8mb4;
SET SESSION lock_wait_timeout = 15;
SET SESSION sql_mode = CONCAT_WS(',', @@sql_mode, 'STRICT_ALL_TABLES');

UPDATE `gen_table_column` SET `is_pk`=0 WHERE `is_pk` IS NULL;
UPDATE `gen_table_column` SET `is_increment`=0 WHERE `is_increment` IS NULL;
UPDATE `gen_table_column` SET `is_required`=0 WHERE `is_required` IS NULL;
UPDATE `gen_table_column` SET `is_insert`=0 WHERE `is_insert` IS NULL;
UPDATE `gen_table_column` SET `is_edit`=0 WHERE `is_edit` IS NULL;
UPDATE `gen_table_column` SET `is_list`=0 WHERE `is_list` IS NULL;
UPDATE `gen_table_column` SET `is_query`=0 WHERE `is_query` IS NULL;
UPDATE `sys_config` SET `config_type`=0 WHERE `config_type` IS NULL;
UPDATE `sys_content` SET `del_flag`=0 WHERE `del_flag` IS NULL;
UPDATE `sys_content` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_dept` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_dept` SET `del_flag`=0 WHERE `del_flag` IS NULL;
UPDATE `sys_dict_data` SET `is_default`=0 WHERE `is_default` IS NULL;
UPDATE `sys_dict_data` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_dict_type` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_job` SET `concurrent`=0 WHERE `concurrent` IS NULL;
UPDATE `sys_job` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_job_log` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_login_log` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_menu` SET `keep_alive`=0 WHERE `keep_alive` IS NULL;
UPDATE `sys_menu` SET `hide_in_menu`=0 WHERE `hide_in_menu` IS NULL;
UPDATE `sys_menu` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_menu` SET `affix_tab`=0 WHERE `affix_tab` IS NULL;
UPDATE `sys_menu` SET `hide_children_in_menu`=0 WHERE `hide_children_in_menu` IS NULL;
UPDATE `sys_menu` SET `hide_in_breadcrumb`=0 WHERE `hide_in_breadcrumb` IS NULL;
UPDATE `sys_menu` SET `hide_in_tab`=0 WHERE `hide_in_tab` IS NULL;
UPDATE `sys_menu` SET `open_in_new_window`=0 WHERE `open_in_new_window` IS NULL;
UPDATE `sys_menu` SET `no_basic_layout`=0 WHERE `no_basic_layout` IS NULL;
UPDATE `sys_message` SET `has_read`=0 WHERE `has_read` IS NULL;
UPDATE `sys_message` SET `has_retraction`=0 WHERE `has_retraction` IS NULL;
UPDATE `sys_message` SET `valid_flag`=1 WHERE `valid_flag` IS NULL;
UPDATE `sys_message` SET `del_flag`=0 WHERE `del_flag` IS NULL;
UPDATE `sys_message_template` SET `valid_flag`=1 WHERE `valid_flag` IS NULL;
UPDATE `sys_message_template` SET `del_flag`=0 WHERE `del_flag` IS NULL;
UPDATE `sys_notice` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_operation_log` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_post` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_role` SET `menu_check_linked`=1 WHERE `menu_check_linked` IS NULL;
UPDATE `sys_role` SET `dept_check_linked`=1 WHERE `dept_check_linked` IS NULL;
UPDATE `sys_role` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_role` SET `del_flag`=0 WHERE `del_flag` IS NULL;
UPDATE `sys_user` SET `status`=0 WHERE `status` IS NULL;
UPDATE `sys_user` SET `del_flag`=0 WHERE `del_flag` IS NULL;
UPDATE `sys_user` SET del_flag=1 WHERE del_flag=2;
UPDATE `sys_role` SET del_flag=1 WHERE del_flag=2;
UPDATE `sys_dept` SET del_flag=1 WHERE del_flag=2;
UPDATE `sys_content` SET del_flag=1 WHERE del_flag=2;
UPDATE `sys_message` SET del_flag=1 WHERE del_flag=2;
UPDATE `sys_message_template` SET del_flag=1 WHERE del_flag=2;
UPDATE sys_config SET config_type=CASE config_type WHEN 'Y' THEN '1' WHEN 'N' THEN '0' ELSE config_type END;
UPDATE sys_dict_data SET is_default=CASE is_default WHEN 'Y' THEN '1' WHEN 'N' THEN '0' ELSE is_default END;
UPDATE sys_dict_data SET dict_value=CASE dict_value WHEN 'Y' THEN '1' WHEN 'N' THEN '0' ELSE dict_value END WHERE dict_type='sys_yes_no';
UPDATE sys_dict_data SET dict_value=CASE dict_label WHEN '成功' THEN '1' WHEN '失败' THEN '0' ELSE dict_value END WHERE dict_type='sys_common_status';
UPDATE sys_dict_data SET dict_value=CASE dict_label WHEN '正常' THEN '1' WHEN '关闭' THEN '0' ELSE dict_value END WHERE dict_type='sys_notice_status';

ALTER TABLE `gen_table_column`
  MODIFY COLUMN `is_pk` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否主键（1是）',
  ADD CONSTRAINT `chk_gen_table_column_is_pk` CHECK (`is_pk` IN (0,1)),
  MODIFY COLUMN `is_increment` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否自增（1是）',
  ADD CONSTRAINT `chk_gen_table_column_is_increment` CHECK (`is_increment` IN (0,1)),
  MODIFY COLUMN `is_required` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否必填（1是）',
  ADD CONSTRAINT `chk_gen_table_column_is_required` CHECK (`is_required` IN (0,1)),
  MODIFY COLUMN `is_insert` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否为插入字段（1是）',
  ADD CONSTRAINT `chk_gen_table_column_is_insert` CHECK (`is_insert` IN (0,1)),
  MODIFY COLUMN `is_edit` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否编辑字段（1是）',
  ADD CONSTRAINT `chk_gen_table_column_is_edit` CHECK (`is_edit` IN (0,1)),
  MODIFY COLUMN `is_list` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否列表字段（1是）',
  ADD CONSTRAINT `chk_gen_table_column_is_list` CHECK (`is_list` IN (0,1)),
  MODIFY COLUMN `is_query` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否查询字段（1是）',
  ADD CONSTRAINT `chk_gen_table_column_is_query` CHECK (`is_query` IN (0,1));

ALTER TABLE `sys_config`
  MODIFY COLUMN `config_type` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '系统内置（0否 1是）',
  ADD CONSTRAINT `chk_sys_config_config_type` CHECK (`config_type` IN (0,1));

ALTER TABLE `sys_content`
  MODIFY COLUMN `del_flag` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  ADD CONSTRAINT `chk_sys_content_del_flag` CHECK (`del_flag` IN (0,1)),
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0停用 1启用）',
  ADD CONSTRAINT `chk_sys_content_status` CHECK (`status` IN (0,1));

ALTER TABLE `sys_dept`
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0停用 1启用）',
  ADD CONSTRAINT `chk_sys_dept_status` CHECK (`status` IN (0,1)),
  MODIFY COLUMN `del_flag` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  ADD CONSTRAINT `chk_sys_dept_del_flag` CHECK (`del_flag` IN (0,1));

ALTER TABLE `sys_dict_data`
  MODIFY COLUMN `is_default` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否默认（0否 1是）',
  ADD CONSTRAINT `chk_sys_dict_data_is_default` CHECK (`is_default` IN (0,1)),
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0停用 1启用）',
  ADD CONSTRAINT `chk_sys_dict_data_status` CHECK (`status` IN (0,1));

ALTER TABLE `sys_dict_type`
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0停用 1启用）',
  ADD CONSTRAINT `chk_sys_dict_type_status` CHECK (`status` IN (0,1));

ALTER TABLE `sys_job`
  MODIFY COLUMN `concurrent` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否并发执行（1允许 0禁止）',
  ADD CONSTRAINT `chk_sys_job_concurrent` CHECK (`concurrent` IN (0,1)),
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '任务状态（1正常 0暂停）',
  ADD CONSTRAINT `chk_sys_job_status` CHECK (`status` IN (0,1));

ALTER TABLE `sys_job_log`
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '执行状态（1成功 0失败）',
  ADD CONSTRAINT `chk_sys_job_log_status` CHECK (`status` IN (0,1));

ALTER TABLE `sys_login_log`
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '结果（0失败 1成功）',
  ADD CONSTRAINT `chk_sys_login_log_status` CHECK (`status` IN (0,1));

ALTER TABLE `sys_menu`
  MODIFY COLUMN `keep_alive` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否缓存（0不缓存 1缓存）',
  ADD CONSTRAINT `chk_sys_menu_keep_alive` CHECK (`keep_alive` IN (0,1)),
  MODIFY COLUMN `hide_in_menu` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否在菜单中隐藏（0显示 1隐藏）',
  ADD CONSTRAINT `chk_sys_menu_hide_in_menu` CHECK (`hide_in_menu` IN (0,1)),
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0停用 1启用）',
  ADD CONSTRAINT `chk_sys_menu_status` CHECK (`status` IN (0,1)),
  MODIFY COLUMN `affix_tab` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '固定标签',
  ADD CONSTRAINT `chk_sys_menu_affix_tab` CHECK (`affix_tab` IN (0,1)),
  MODIFY COLUMN `hide_children_in_menu` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '隐藏子菜单',
  ADD CONSTRAINT `chk_sys_menu_hide_children_in_menu` CHECK (`hide_children_in_menu` IN (0,1)),
  MODIFY COLUMN `hide_in_breadcrumb` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '隐藏面包屑',
  ADD CONSTRAINT `chk_sys_menu_hide_in_breadcrumb` CHECK (`hide_in_breadcrumb` IN (0,1)),
  MODIFY COLUMN `hide_in_tab` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '隐藏标签',
  ADD CONSTRAINT `chk_sys_menu_hide_in_tab` CHECK (`hide_in_tab` IN (0,1)),
  MODIFY COLUMN `open_in_new_window` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '新窗口打开',
  ADD CONSTRAINT `chk_sys_menu_open_in_new_window` CHECK (`open_in_new_window` IN (0,1)),
  MODIFY COLUMN `no_basic_layout` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '不使用基础布局',
  ADD CONSTRAINT `chk_sys_menu_no_basic_layout` CHECK (`no_basic_layout` IN (0,1));

ALTER TABLE `sys_message`
  MODIFY COLUMN `has_read` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已读',
  ADD CONSTRAINT `chk_sys_message_has_read` CHECK (`has_read` IN (0,1)),
  MODIFY COLUMN `has_retraction` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否撤回',
  ADD CONSTRAINT `chk_sys_message_has_retraction` CHECK (`has_retraction` IN (0,1)),
  MODIFY COLUMN `valid_flag` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
  ADD CONSTRAINT `chk_sys_message_valid_flag` CHECK (`valid_flag` IN (0,1)),
  MODIFY COLUMN `del_flag` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  ADD CONSTRAINT `chk_sys_message_del_flag` CHECK (`del_flag` IN (0,1));

ALTER TABLE `sys_message_template`
  MODIFY COLUMN `valid_flag` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否有效;0：无效，1：有效',
  ADD CONSTRAINT `chk_sys_message_template_valid_flag` CHECK (`valid_flag` IN (0,1)),
  MODIFY COLUMN `del_flag` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  ADD CONSTRAINT `chk_sys_message_template_del_flag` CHECK (`del_flag` IN (0,1));

ALTER TABLE `sys_notice`
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0停用 1启用）',
  ADD CONSTRAINT `chk_sys_notice_status` CHECK (`status` IN (0,1));

ALTER TABLE `sys_operation_log`
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '结果（0失败 1成功）',
  ADD CONSTRAINT `chk_sys_operation_log_status` CHECK (`status` IN (0,1));

ALTER TABLE `sys_post`
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0停用 1启用）',
  ADD CONSTRAINT `chk_sys_post_status` CHECK (`status` IN (0,1));

ALTER TABLE `sys_role`
  MODIFY COLUMN `menu_check_linked` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  ADD CONSTRAINT `chk_sys_role_menu_check_linked` CHECK (`menu_check_linked` IN (0,1)),
  MODIFY COLUMN `dept_check_linked` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  ADD CONSTRAINT `chk_sys_role_dept_check_linked` CHECK (`dept_check_linked` IN (0,1)),
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0停用 1启用）',
  ADD CONSTRAINT `chk_sys_role_status` CHECK (`status` IN (0,1)),
  MODIFY COLUMN `del_flag` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  ADD CONSTRAINT `chk_sys_role_del_flag` CHECK (`del_flag` IN (0,1));

ALTER TABLE `sys_user`
  MODIFY COLUMN `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态（0停用 1启用）',
  ADD CONSTRAINT `chk_sys_user_status` CHECK (`status` IN (0,1)),
  MODIFY COLUMN `del_flag` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）',
  ADD CONSTRAINT `chk_sys_user_del_flag` CHECK (`del_flag` IN (0,1));

-- 普通业务唯一键。
ALTER TABLE sys_config MODIFY config_key VARCHAR(100) NOT NULL COMMENT '参数键名', ADD UNIQUE KEY uk_config_key (config_key);
ALTER TABLE sys_post ADD UNIQUE KEY uk_post_code (post_code), ADD UNIQUE KEY uk_post_name (post_name);
ALTER TABLE sys_dict_type MODIFY dict_type VARCHAR(100) NOT NULL COMMENT '字典类型';
ALTER TABLE sys_dict_data MODIFY dict_type VARCHAR(100) NOT NULL COMMENT '字典类型', MODIFY dict_value VARCHAR(100) NOT NULL COMMENT '字典键值', ADD UNIQUE KEY uk_dict_type_value (dict_type,dict_value);
ALTER TABLE gen_table MODIFY table_name VARCHAR(200) NOT NULL COMMENT '表名称', ADD UNIQUE KEY uk_gen_table_name (table_name);
ALTER TABLE gen_table_column MODIFY table_id BIGINT NOT NULL COMMENT '归属表编号', MODIFY column_name VARCHAR(200) NOT NULL COMMENT '列名称', ADD UNIQUE KEY uk_gen_table_column (table_id,column_name);

-- 仅未删除记录参与唯一约束；允许多次软删除和重新创建，空手机号/邮箱不参与。
ALTER TABLE sys_user ADD UNIQUE KEY uk_user_name_active ((CASE WHEN del_flag=0 THEN user_name ELSE NULL END)), ADD UNIQUE KEY uk_user_phone_active ((CASE WHEN del_flag=0 THEN NULLIF(phone_number,'') ELSE NULL END)), ADD UNIQUE KEY uk_user_email_active ((CASE WHEN del_flag=0 THEN NULLIF(email,'') ELSE NULL END));
ALTER TABLE sys_role ADD UNIQUE KEY uk_role_key_active ((CASE WHEN del_flag=0 THEN role_key ELSE NULL END)), ADD UNIQUE KEY uk_role_name_active ((CASE WHEN del_flag=0 THEN role_name ELSE NULL END));
ALTER TABLE sys_dept MODIFY parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门id', MODIFY dept_name VARCHAR(30) NOT NULL COMMENT '部门名称', ADD UNIQUE KEY uk_dept_name_active (parent_id,(CASE WHEN del_flag=0 THEN dept_name ELSE NULL END));

-- 主键已提供同等唯一性，移除重复索引。
ALTER TABLE sys_message DROP INDEX ID_362038349924500;
ALTER TABLE sys_message_template DROP INDEX ID_362038929647900;

-- 历史一次性迁移记录，当前应用无引用；备份中保留原记录。
DROP TABLE IF EXISTS sys_status_migration;
