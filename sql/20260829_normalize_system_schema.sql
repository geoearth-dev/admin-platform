-- MySQL 8.0+
-- Normalize primary keys and the clearest legacy/abbreviated column names.
-- Back up admin_platform before execution: MySQL DDL commits implicitly.

ALTER TABLE sys_config RENAME COLUMN config_id TO id;
ALTER TABLE sys_dept RENAME COLUMN dept_id TO id;
ALTER TABLE sys_dict_data RENAME COLUMN dict_code TO id;
ALTER TABLE sys_dict_type RENAME COLUMN dict_id TO id;
ALTER TABLE sys_menu RENAME COLUMN menu_id TO id;
ALTER TABLE sys_notice RENAME COLUMN notice_id TO id;
ALTER TABLE sys_notice_read RENAME COLUMN read_id TO id;
ALTER TABLE sys_post RENAME COLUMN post_id TO id;
ALTER TABLE sys_role RENAME COLUMN role_id TO id;
ALTER TABLE sys_user RENAME COLUMN user_id TO id;

ALTER TABLE sys_user
    RENAME COLUMN phonenumber TO phone_number,
    RENAME COLUMN login_ip TO last_login_ip,
    RENAME COLUMN login_date TO last_login_time,
    RENAME COLUMN pwd_update_date TO password_update_time;

ALTER TABLE sys_login_log
    RENAME COLUMN ipaddr TO ip_address,
    RENAME COLUMN os TO operating_system,
    RENAME COLUMN msg TO message,
    RENAME INDEX idx_sys_logininfor_lt TO idx_login_log_time,
    RENAME INDEX idx_sys_logininfor_s TO idx_login_log_status;

ALTER TABLE sys_operation_log
    RENAME COLUMN method TO method_name,
    RENAME COLUMN request_method TO http_method,
    RENAME COLUMN url TO request_uri,
    RENAME COLUMN ip TO ip_address,
    RENAME COLUMN location TO operation_location,
    RENAME COLUMN operate_time TO operation_time,
    RENAME COLUMN param TO request_params,
    RENAME COLUMN result TO response_body,
    RENAME COLUMN error_msg TO error_message,
    RENAME COLUMN cost_time TO duration_ms;
