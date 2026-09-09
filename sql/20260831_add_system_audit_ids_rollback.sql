-- MySQL 8.0+
-- 回滚系统业务表审计用户 ID 字段。

ALTER TABLE sys_dept DROP COLUMN updater_id, DROP COLUMN creator_id;
ALTER TABLE sys_dict_data DROP COLUMN updater_id, DROP COLUMN creator_id;
ALTER TABLE sys_dict_type DROP COLUMN updater_id, DROP COLUMN creator_id;
ALTER TABLE sys_menu DROP COLUMN updater_id, DROP COLUMN creator_id;
ALTER TABLE sys_notice DROP COLUMN updater_id, DROP COLUMN creator_id;
ALTER TABLE sys_post DROP COLUMN updater_id, DROP COLUMN creator_id;
ALTER TABLE sys_role DROP COLUMN updater_id, DROP COLUMN creator_id;
ALTER TABLE sys_user DROP COLUMN updater_id, DROP COLUMN creator_id;
