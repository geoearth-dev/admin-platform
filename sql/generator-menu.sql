-- 生成器入口及权限，仅适用于当前 admin-platform 的 sys_menu 结构。
SET @toolParentId = COALESCE((SELECT id FROM sys_menu WHERE path = 'tool' AND menu_type = 'catalog' ORDER BY id LIMIT 1), 0);
INSERT INTO sys_menu (menu_name, parent_id, `order`, path, component, route_name, menu_type, hide_in_menu, keep_alive, status, perms, create_time)
SELECT '代码生成', @toolParentId, 2, IF(@toolParentId = 0, '/tool/gen', 'gen'), 'sys/tool/gen/index', 'Gen', 'menu', 0, 1, 1, 'tool:gen:list', NOW()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'tool:gen:list');
UPDATE sys_menu SET component = 'sys/tool/gen/index', route_name = 'Gen', menu_type = 'menu', status = 1 WHERE perms = 'tool:gen:list';
SET @genMenuId = (SELECT id FROM sys_menu WHERE perms = 'tool:gen:list' ORDER BY id LIMIT 1);
INSERT INTO sys_menu (menu_name,parent_id,`order`,path,menu_type,hide_in_menu,status,perms,create_time) SELECT '代码生成查询',@genMenuId,1,'','button',1,1,'tool:gen:query',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'tool:gen:query');
INSERT INTO sys_menu (menu_name,parent_id,`order`,path,menu_type,hide_in_menu,status,perms,create_time) SELECT '代码生成导入',@genMenuId,2,'','button',1,1,'tool:gen:import',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'tool:gen:import');
INSERT INTO sys_menu (menu_name,parent_id,`order`,path,menu_type,hide_in_menu,status,perms,create_time) SELECT '代码生成编辑',@genMenuId,3,'','button',1,1,'tool:gen:edit',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'tool:gen:edit');
INSERT INTO sys_menu (menu_name,parent_id,`order`,path,menu_type,hide_in_menu,status,perms,create_time) SELECT '代码生成删除',@genMenuId,4,'','button',1,1,'tool:gen:remove',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'tool:gen:remove');
INSERT INTO sys_menu (menu_name,parent_id,`order`,path,menu_type,hide_in_menu,status,perms,create_time) SELECT '代码生成预览',@genMenuId,5,'','button',1,1,'tool:gen:preview',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'tool:gen:preview');
INSERT INTO sys_menu (menu_name,parent_id,`order`,path,menu_type,hide_in_menu,status,perms,create_time) SELECT '代码生成生成',@genMenuId,6,'','button',1,1,'tool:gen:code',NOW() WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'tool:gen:code');
