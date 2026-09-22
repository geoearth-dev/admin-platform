/** 在线用户查询参数，返回全部匹配会话，由前端分页。 */
export interface OnlineQueryParams {
  /** 登录 IP */
  ipAddress?: string
  /** 用户账号 */
  userName?: string
}

/** 在线用户信息 */
export interface OnlineSession {
  /** 会话编号 */
  sessionId: string
  userId: number
  nickName?: string
  currentSession: boolean
  forceLogoutAllowed: boolean
  /** 部门名称 */
  deptName?: string
  /** 用户名称 */
  userName: string
  /** 登录IP */
  ip?: string
  /** 登录地址 */
  loginLocation?: string
  /** 浏览器类型 */
  browser?: string
  /** 操作系统 */
  os?: string
  /** 登录时间 */
  loginTime: number
}
