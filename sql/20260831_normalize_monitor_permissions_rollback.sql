-- 回退监控模块权限标识。

UPDATE sys_menu
SET perms = REPLACE(perms, 'monitor:login-log:', 'monitor:logininfor:')
WHERE perms LIKE 'monitor:login-log:%';

UPDATE sys_menu
SET perms = REPLACE(perms, 'monitor:operation-log:', 'monitor:operlog:')
WHERE perms LIKE 'monitor:operation-log:%';

UPDATE sys_menu
SET perms = REPLACE(perms, 'monitor:online-session:', 'monitor:online:')
WHERE perms LIKE 'monitor:online-session:%';

UPDATE sys_menu
SET perms = 'monitor:online:forceLogout'
WHERE perms = 'monitor:online:force-logout';
