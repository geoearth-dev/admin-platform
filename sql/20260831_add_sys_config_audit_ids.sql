-- MySQL 8.0+
-- 为系统参数表补齐创建人与更新人的用户 ID，历史数据保持 NULL。

ALTER TABLE sys_config
    ADD COLUMN creator_id BIGINT NULL COMMENT '创建者用户ID' AFTER config_type,
    ADD COLUMN updater_id BIGINT NULL COMMENT '更新者用户ID' AFTER create_time;
