-- MySQL 8.0+
-- 为系统业务表补齐创建人与更新人的用户 ID；历史数据保持 NULL。
-- sys_config 已由 20260831_add_sys_config_audit_ids.sql 单独处理。

ALTER TABLE sys_dept
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建者用户ID',
    ADD COLUMN updater_id BIGINT NULL COMMENT '更新者用户ID';

ALTER TABLE sys_dict_data
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建者用户ID',
    ADD COLUMN updater_id BIGINT NULL COMMENT '更新者用户ID';

ALTER TABLE sys_dict_type
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建者用户ID',
    ADD COLUMN updater_id BIGINT NULL COMMENT '更新者用户ID';

ALTER TABLE sys_menu
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建者用户ID',
    ADD COLUMN updater_id BIGINT NULL COMMENT '更新者用户ID';

ALTER TABLE sys_notice
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建者用户ID',
    ADD COLUMN updater_id BIGINT NULL COMMENT '更新者用户ID';

ALTER TABLE sys_post
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建者用户ID',
    ADD COLUMN updater_id BIGINT NULL COMMENT '更新者用户ID';

ALTER TABLE sys_role
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建者用户ID',
    ADD COLUMN updater_id BIGINT NULL COMMENT '更新者用户ID';

ALTER TABLE sys_user
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建者用户ID',
    ADD COLUMN updater_id BIGINT NULL COMMENT '更新者用户ID';
