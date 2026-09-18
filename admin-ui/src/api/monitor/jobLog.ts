import type { PageResult } from '@/types/base/api/common'
import type {
  JobLogQueryParams,
  SysJobLog,
} from '@/types/base/api/monitor/jobLog'
import { requestClient } from '@/utils/request'

export function listJobLog(query: JobLogQueryParams) {
  return requestClient.get<PageResult<SysJobLog>>('/monitor/jobLog/list', query)
}

export function getJobLog(id: number) {
  return requestClient.get<SysJobLog>(`/monitor/jobLog/${id}`)
}

export function delJobLog(ids: number | number[]) {
  return requestClient.delete<void>(
    `/monitor/jobLog/${Array.isArray(ids) ? ids.join(',') : ids}`,
  )
}

export function cleanJobLog() {
  return requestClient.delete<void>('/monitor/jobLog/clean')
}

export function exportJobLog(query: JobLogQueryParams) {
  return requestClient.download('/monitor/jobLog/export', {
    method: 'POST',
    params: query,
  })
}
