-- 岗位、公告逻辑删除；基于 schema-normalize-20260920.sql 执行后的库。
-- 先备份并停用旧版本应用；仅执行一次，MySQL DDL 会隐式提交。
SET NAMES utf8mb4;
SET SESSION lock_wait_timeout = 15;
ALTER TABLE sys_post
    ADD COLUMN del_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）' AFTER status,
    ADD CONSTRAINT chk_sys_post_del_flag CHECK (del_flag IN (0,1)),
    DROP INDEX uk_post_code,
    DROP INDEX uk_post_name,
    ADD UNIQUE KEY uk_post_code_active ((CASE WHEN del_flag=0 THEN post_code ELSE NULL END)),
    ADD UNIQUE KEY uk_post_name_active ((CASE WHEN del_flag=0 THEN post_name ELSE NULL END));
ALTER TABLE sys_notice
    ADD COLUMN del_flag TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '删除标志（0未删除 1已删除）' AFTER status,
    ADD CONSTRAINT chk_sys_notice_del_flag CHECK (del_flag IN (0,1));
-- 保留公告已读历史；现有岗位和公告全部保持未删除。
