import type { BaseEntity, PageParam } from '../common'

/** 1 表示启用、允许或成功，0 表示相反含义。 */
export type JobFlag = '0' | '1'
export type MisfirePolicy = '0' | '1' | '2' | '3'

export interface JobQueryParams extends PageParam {
  pageNum: number
  pageSize: number
  jobName?: string
  jobGroup?: string
  invokeTarget?: string
  status?: JobFlag | ''
}

export interface JobSaveParams {
  id?: number
  jobName: string
  jobGroup: string
  invokeTarget: string
  cronExpression: string
  misfirePolicy: MisfirePolicy
  /** 1允许，0禁止 */
  concurrent: JobFlag
  /** 1正常，0暂停；新增任务由后端默认暂停 */
  status: JobFlag
  remark?: string
}

export interface SysJob extends BaseEntity, JobSaveParams {
  id: number
  nextValidTime: string | null
}
