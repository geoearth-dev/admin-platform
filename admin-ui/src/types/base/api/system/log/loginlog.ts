import type { PageParam } from '@/types/base/api/common'

/** 登录日志查询，日期范围由后台按整天处理。 */
export interface LoginLogQueryParams extends PageParam {
  userName?: string
  ipAddress?: string
  status?: '0' | '1'
  beginDate?: string
  endDate?: string
}

export interface SysLoginLog {
  id: number
  userName: string
  /** 0 失败，1 成功。 */
  status: '0' | '1'
  ipAddress?: string
  loginLocation?: string
  browser?: string
  operatingSystem?: string
  message?: string
  loginTime?: string
}
