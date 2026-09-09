-- MySQL 8.0+
-- 回滚系统参数表审计用户 ID 字段。

ALTER TABLE sys_config
    DROP COLUMN updater_id,
    DROP COLUMN creator_id;
