-- 已有 RuoYi 生成器表升级：先备份这两张表，再选择目标数据库执行。
-- 保留所有主键值；gen_table_column.table_id 仍是关联 gen_table.id 的外键。
-- MySQL DDL 会隐式提交；本脚本没有 DROP TABLE，可重复执行。
SET @generator_ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'gen_table' AND column_name = 'table_id'), 'ALTER TABLE gen_table CHANGE COLUMN table_id id BIGINT NOT NULL AUTO_INCREMENT', 'SELECT 1');
PREPARE generator_stmt FROM @generator_ddl;
EXECUTE generator_stmt;
DEALLOCATE PREPARE generator_stmt;

SET @generator_ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'gen_table_column' AND column_name = 'column_id'), 'ALTER TABLE gen_table_column CHANGE COLUMN column_id id BIGINT NOT NULL AUTO_INCREMENT', 'SELECT 1');
PREPARE generator_stmt FROM @generator_ddl;
EXECUTE generator_stmt;
DEALLOCATE PREPARE generator_stmt;

SET @generator_ddl = IF(EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'gen_table' AND column_name = 'tpl_web_type'), 'ALTER TABLE gen_table DROP COLUMN tpl_web_type', 'SELECT 1');
PREPARE generator_stmt FROM @generator_ddl;
EXECUTE generator_stmt;
DEALLOCATE PREPARE generator_stmt;

SET @generator_ddl = IF(NOT EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'gen_table' AND column_name = 'form_col_num'), 'ALTER TABLE gen_table ADD COLUMN form_col_num INT DEFAULT 1', 'SELECT 1');
PREPARE generator_stmt FROM @generator_ddl;
EXECUTE generator_stmt;
DEALLOCATE PREPARE generator_stmt;
-- 核对迁移结果；不改变业务表的主键。
SELECT id, table_name FROM gen_table ORDER BY id;
SELECT id, table_id, column_name FROM gen_table_column ORDER BY id;
