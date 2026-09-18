import type { PageResult } from '@/types/base/api/common'
import type {
  OperationLogQueryParams,
  SysOperationLog,
} from '@/types/base/api/system/log/operlog'
import { requestClient } from '@/utils/request'

export function listOperationLog(query: OperationLogQueryParams = {}) {
  return requestClient.get<PageResult<SysOperationLog>>(
    '/monitor/operation-log/list',
    query,
  )
}

export function getOperationLog(id: number) {
  return requestClient.get<SysOperationLog>(`/monitor/operation-log/${id}`)
}

export function delOperationLog(id: number | number[]) {
  const ids = Array.isArray(id) ? id.join(',') : id
  return requestClient.delete<void>(`/monitor/operation-log/${ids}`)
}

export function cleanOperationLog() {
  return requestClient.delete<void>('/monitor/operation-log/clean')
}

export function exportOperationLog(query: OperationLogQueryParams = {}) {
  return requestClient.download('/monitor/operation-log/export', {
    method: 'POST',
    params: query,
  })
}
