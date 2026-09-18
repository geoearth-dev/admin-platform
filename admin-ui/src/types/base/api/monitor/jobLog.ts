import type { BaseEntity } from '../common'
import type { JobFlag, JobQueryParams } from './job'

export interface JobLogQueryParams extends JobQueryParams {
  params?: {
    beginTime?: string
    endTime?: string
  }
}

export interface SysJobLog extends BaseEntity {
  id: number
  jobName: string
  jobGroup: string
  invokeTarget: string
  jobMessage: string | null
  exceptionInfo: string | null
  /** 1成功，0失败 */
  status: JobFlag
  startTime: string | null
  endTime: string | null
}
