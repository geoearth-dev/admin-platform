-- 将监控模块权限标识统一为当前领域命名。

UPDATE sys_menu
SET perms = REPLACE(perms, 'monitor:logininfor:', 'monitor:login-log:')
WHERE perms LIKE 'monitor:logininfor:%';

UPDATE sys_menu
SET perms = REPLACE(perms, 'monitor:operlog:', 'monitor:operation-log:')
WHERE perms LIKE 'monitor:operlog:%';

UPDATE sys_menu
SET perms = REPLACE(perms, 'monitor:online:', 'monitor:online-session:')
WHERE perms LIKE 'monitor:online:%';

UPDATE sys_menu
SET perms = 'monitor:online-session:force-logout'
WHERE perms = 'monitor:online-session:forceLogout';
