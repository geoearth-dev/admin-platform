/** 超级管理员拥有的全部权限。 */
export const ALL_PERMISSION = '*:*:*'

export const ONLINE_SESSION_PERMISSION = {
  list: 'monitor:online-session:list',
  forceLogout: 'monitor:online-session:force-logout',
} as const

export const LOGIN_LOG_PERMISSION = {
  list: 'monitor:login-log:list',
  remove: 'monitor:login-log:remove',
  export: 'monitor:login-log:export',
  unlock: 'monitor:login-log:unlock',
} as const

export const OPERATION_LOG_PERMISSION = {
  list: 'monitor:operation-log:list',
  query: 'monitor:operation-log:query',
  remove: 'monitor:operation-log:remove',
  export: 'monitor:operation-log:export',
} as const

export const NOTICE_PERMISSION = {
  list: 'system:notice:list',
  query: 'system:notice:query',
  add: 'system:notice:add',
  edit: 'system:notice:edit',
  remove: 'system:notice:remove',
} as const

/** 用户管理权限，与后端接口的权限标识保持一致。 */
export const USER_PERMISSION = {
  list: 'system:user:list',
  query: 'system:user:query',
  add: 'system:user:add',
  edit: 'system:user:edit',
  remove: 'system:user:remove',
  import: 'system:user:import',
  export: 'system:user:export',
  resetPassword: 'system:user:reset-password',
} as const

/** 角色管理权限。 */
export const ROLE_PERMISSION = {
  list: 'system:role:list',
  query: 'system:role:query',
  add: 'system:role:add',
  edit: 'system:role:edit',
  remove: 'system:role:remove',
  export: 'system:role:export',
} as const

/** 菜单管理权限。 */
export const MENU_PERMISSION = {
  list: 'system:menu:list',
  query: 'system:menu:query',
  add: 'system:menu:add',
  edit: 'system:menu:edit',
  remove: 'system:menu:remove',
} as const

/** 部门管理权限。 */
export const DEPT_PERMISSION = {
  list: 'system:dept:list',
  query: 'system:dept:query',
  add: 'system:dept:add',
  edit: 'system:dept:edit',
  remove: 'system:dept:remove',
} as const

/** 字典类型和字典数据共用的权限。 */
export const DICT_PERMISSION = {
  list: 'system:dict:list',
  query: 'system:dict:query',
  add: 'system:dict:add',
  edit: 'system:dict:edit',
  remove: 'system:dict:remove',
  export: 'system:dict:export',
} as const
