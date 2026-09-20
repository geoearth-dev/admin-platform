import type { PageResult } from '@/types/base/api/common'
import type {
  DbTable,
  GenQueryParams,
  GenTable,
  GenTableInfoResult,
} from '@/types/base/api/tool/gen'
import { requestClient } from '@/utils/request'

export const listTable = (params: GenQueryParams) =>
  requestClient.get<PageResult<GenTable>>('/tool/gen/list', params)
export const listDbTable = (params: GenQueryParams) =>
  requestClient.get<PageResult<DbTable>>('/tool/gen/db/list', params)
export const getGenTable = (id: number) =>
  requestClient.get<GenTableInfoResult>(`/tool/gen/${id}`)
export const updateGenTable = (data: GenTable) =>
  requestClient.put<void>('/tool/gen', data)
export const importTable = (tables: string[]) =>
  requestClient.post<void>('/tool/gen/importTable', { tables })
export const createTable = (sql: string) =>
  requestClient.post<void>('/tool/gen/createTable', { sql })
export const previewTable = (id: number) =>
  requestClient.get<Record<string, string>>(`/tool/gen/preview/${id}`)
export const delTable = (ids: number[]) =>
  requestClient.delete<void>(`/tool/gen/${ids.join(',')}`)
export const genCode = (tableName: string) =>
  requestClient.get<void>(`/tool/gen/genCode/${encodeURIComponent(tableName)}`)
export const synchDb = (tableName: string) =>
  requestClient.get<void>(`/tool/gen/synchDb/${encodeURIComponent(tableName)}`)
export const downloadCode = (tables: string[]) =>
  requestClient.download('/tool/gen/batchGenCode', {
    params: { tables: tables.join(',') },
  })
