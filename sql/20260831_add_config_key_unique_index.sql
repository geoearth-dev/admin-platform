-- 执行前请先确认 sys_config.config_key 不存在重复值。
ALTER TABLE sys_config
    ADD CONSTRAINT uk_sys_config_config_key UNIQUE (config_key);
