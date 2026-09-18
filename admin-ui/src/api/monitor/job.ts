import type { PageResult } from '@/types/base/api/common'
import type {
  JobFlag,
  JobQueryParams,
  JobSaveParams,
  SysJob,
} from '@/types/base/api/monitor/job'
import { requestClient } from '@/utils/request'

export function listJob(query: JobQueryParams) {
  return requestClient.get<PageResult<SysJob>>('/monitor/job/list', query)
}

export function getJob(id: number) {
  return requestClient.get<SysJob>(`/monitor/job/${id}`)
}

export function addJob(data: JobSaveParams) {
  return requestClient.post<void>('/monitor/job', data)
}

export function updateJob(data: JobSaveParams) {
  return requestClient.put<void>('/monitor/job', data)
}

export function delJob(ids: number | number[]) {
  return requestClient.delete<void>(
    `/monitor/job/${Array.isArray(ids) ? ids.join(',') : ids}`,
  )
}

export function changeJobStatus(id: number, status: JobFlag) {
  return requestClient.put<void>('/monitor/job/changeStatus', { id, status })
}

export function runJob(id: number, jobGroup: string) {
  return requestClient.put<void>('/monitor/job/run', { id, jobGroup })
}

export function exportJob(query: JobQueryParams) {
  return requestClient.download('/monitor/job/export', {
    method: 'POST',
    params: query,
  })
}
