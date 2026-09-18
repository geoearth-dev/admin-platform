import type { PageParam } from '@/types/base/api/common'

export interface OperationLogQueryParams extends PageParam {
  title?: string
  userName?: string
  businessType?: number
  businessTypes?: number[]
  /** 0 异常，1 正常。 */
  status?: 0 | 1
  httpMethod?: string
  beginDate?: string
  endDate?: string
}

export interface SysOperationLog {
  id: number
  title?: string
  businessType?: number
  businessTypes?: number[]
  methodName?: string
  httpMethod?: string
  operatorType?: number
  userName?: string
  deptName?: string
  requestUri?: string
  ipAddress?: string
  operationLocation?: string
  requestParams?: string
  responseBody?: string
  /** 0 异常，1 正常。 */
  status: 0 | 1
  errorMessage?: string
  operationTime?: string
  durationMs?: number
}
