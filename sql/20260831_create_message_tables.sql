-- MySQL 8.0+
-- 创建站内消息与消息模板表。

CREATE TABLE sys_message_template
(
    id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    template_code    VARCHAR(64)  NOT NULL COMMENT '模板编码',
    template_name    VARCHAR(100) NOT NULL COMMENT '模板名称',
    title_template   VARCHAR(200) NOT NULL COMMENT '标题模板',
    content_template TEXT         NOT NULL COMMENT '内容模板',
    category         TINYINT      NOT NULL COMMENT '消息类别',
    message_level    TINYINT      NOT NULL COMMENT '消息级别',
    status           CHAR(1)      NOT NULL DEFAULT '0' COMMENT '状态（0启用 1停用）',
    del_flag         TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
    creator_id       BIGINT       NULL COMMENT '创建者用户ID',
    create_by        VARCHAR(64)  NULL COMMENT '创建者',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater_id       BIGINT       NULL COMMENT '更新者用户ID',
    update_by        VARCHAR(64)  NULL COMMENT '更新者',
    update_time      DATETIME     NULL COMMENT '更新时间',
    remark           VARCHAR(500) NULL COMMENT '备注',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_message_template_code (template_code)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '站内消息模板';

CREATE TABLE sys_message
(
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    sender_id      BIGINT       NULL COMMENT '发送人用户ID',
    receiver_id    BIGINT       NOT NULL COMMENT '接收人用户ID',
    title          VARCHAR(200) NOT NULL COMMENT '消息标题',
    content        TEXT         NOT NULL COMMENT '消息内容',
    category       TINYINT      NOT NULL COMMENT '消息类别',
    message_level  TINYINT      NOT NULL COMMENT '消息级别',
    module         TINYINT      NULL COMMENT '所属模块',
    business_type  TINYINT      NULL COMMENT '关联业务类型',
    business_id    BIGINT       NULL COMMENT '关联业务ID',
    business_url   VARCHAR(500) NULL COMMENT '关联业务链接',
    read_status    TINYINT      NOT NULL DEFAULT 0 COMMENT '阅读状态（0未读 1已读）',
    read_time      DATETIME     NULL COMMENT '阅读时间',
    del_flag       TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
    creator_id     BIGINT       NULL COMMENT '创建者用户ID',
    create_by      VARCHAR(64)  NULL COMMENT '创建者',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater_id     BIGINT       NULL COMMENT '更新者用户ID',
    update_by      VARCHAR(64)  NULL COMMENT '更新者',
    update_time    DATETIME     NULL COMMENT '更新时间',
    remark         VARCHAR(500) NULL COMMENT '备注',
    PRIMARY KEY (id),
    KEY idx_sys_message_receiver_read (receiver_id, read_status, create_time),
    KEY idx_sys_message_receiver_module (receiver_id, module, create_time),
    KEY idx_sys_message_business (business_type, business_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '站内消息';
