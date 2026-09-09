-- MySQL 8.0+ rollback for 20260829_normalize_system_schema.sql.

ALTER TABLE sys_operation_log
    RENAME COLUMN method_name TO method,
    RENAME COLUMN http_method TO request_method,
    RENAME COLUMN request_uri TO url,
    RENAME COLUMN ip_address TO ip,
    RENAME COLUMN operation_location TO location,
    RENAME COLUMN operation_time TO operate_time,
    RENAME COLUMN request_params TO param,
    RENAME COLUMN response_body TO result,
    RENAME COLUMN error_message TO error_msg,
    RENAME COLUMN duration_ms TO cost_time;

ALTER TABLE sys_login_log
    RENAME COLUMN ip_address TO ipaddr,
    RENAME COLUMN operating_system TO os,
    RENAME COLUMN message TO msg,
    RENAME INDEX idx_login_log_time TO idx_sys_logininfor_lt,
    RENAME INDEX idx_login_log_status TO idx_sys_logininfor_s;

ALTER TABLE sys_user
    RENAME COLUMN phone_number TO phonenumber,
    RENAME COLUMN last_login_ip TO login_ip,
    RENAME COLUMN last_login_time TO login_date,
    RENAME COLUMN password_update_time TO pwd_update_date;

ALTER TABLE sys_config RENAME COLUMN id TO config_id;
ALTER TABLE sys_dept RENAME COLUMN id TO dept_id;
ALTER TABLE sys_dict_data RENAME COLUMN id TO dict_code;
ALTER TABLE sys_dict_type RENAME COLUMN id TO dict_id;
ALTER TABLE sys_menu RENAME COLUMN id TO menu_id;
ALTER TABLE sys_notice RENAME COLUMN id TO notice_id;
ALTER TABLE sys_notice_read RENAME COLUMN id TO read_id;
ALTER TABLE sys_post RENAME COLUMN id TO post_id;
ALTER TABLE sys_role RENAME COLUMN id TO role_id;
ALTER TABLE sys_user RENAME COLUMN id TO user_id;
