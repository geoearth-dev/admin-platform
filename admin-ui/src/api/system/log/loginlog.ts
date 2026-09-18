import type { PageResult } from '@/types/base/api/common'
import type {
  LoginLogQueryParams,
  SysLoginLog,
} from '@/types/base/api/system/log/loginlog'
import { requestClient } from '@/utils/request'

export function listLoginLog(query: LoginLogQueryParams = {}) {
  return requestClient.get<PageResult<SysLoginLog>>(
    '/monitor/login-log/list',
    query,
  )
}

export function delLoginLog(id: number | number[]) {
  const ids = Array.isArray(id) ? id.join(',') : id
  return requestClient.delete<void>(`/monitor/login-log/${ids}`)
}

export function cleanLoginLog() {
  return requestClient.delete<void>('/monitor/login-log/clean')
}

export function unlockLoginLog(userName: string) {
  return requestClient.get<void>(
    `/monitor/login-log/unlock/${encodeURIComponent(userName)}`,
  )
}

export function exportLoginLog(query: LoginLogQueryParams = {}) {
  return requestClient.download('/monitor/login-log/export', {
    method: 'POST',
    params: query,
  })
}
