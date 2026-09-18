-- Quartz 业务表：任务启用/允许并发/执行成功均为1，反之为0。
-- 新库可单独执行；已有若依表请执行 migrations/20260917-quartz-id-flags.sql。
CREATE TABLE IF NOT EXISTS sys_job (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    job_name VARCHAR(64) NOT NULL DEFAULT '' COMMENT '任务名称',
    job_group VARCHAR(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    invoke_target VARCHAR(500) NOT NULL COMMENT '调用目标字符串',
    cron_expression VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Cron执行表达式',
    misfire_policy VARCHAR(20) NOT NULL DEFAULT '3' COMMENT '计划策略（0默认 1立即执行 2执行一次 3放弃执行）',
    concurrent CHAR(1) NOT NULL DEFAULT '0' COMMENT '是否并发执行（1允许 0禁止）',
    status CHAR(1) NOT NULL DEFAULT '0' COMMENT '任务状态（1正常 0暂停）',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME(3) DEFAULT NULL COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME(3) DEFAULT NULL COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (id)
) COMMENT = '定时任务调度表';

CREATE TABLE IF NOT EXISTS sys_job_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    job_name VARCHAR(64) NOT NULL COMMENT '任务名称',
    job_group VARCHAR(64) NOT NULL COMMENT '任务组名',
    invoke_target VARCHAR(500) NOT NULL COMMENT '调用目标字符串',
    job_message VARCHAR(500) DEFAULT NULL COMMENT '日志信息',
    status CHAR(1) NOT NULL DEFAULT '1' COMMENT '执行状态（1成功 0失败）',
    exception_info VARCHAR(2000) DEFAULT '' COMMENT '异常信息',
    start_time DATETIME(3) DEFAULT NULL COMMENT '执行开始时间',
    end_time DATETIME(3) DEFAULT NULL COMMENT '执行结束时间',
    create_time DATETIME(3) DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
) COMMENT = '定时任务调度日志表';
