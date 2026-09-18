import type { PageResult } from '@/types/base/api/common'
import type {
  DictTypeQueryParams,
  DictTypeSaveParams,
  SysDictType,
} from '@/types/base/api/system/dict'
import { requestClient } from '@/utils/request'

/** 分页查询字典类型，日期条件为顶层 beginTime/endTime。 */
export function listType(query: DictTypeQueryParams = {}) {
  return requestClient.get<PageResult<SysDictType>>(
    '/system/dict-type/list',
    query,
  )
}

/** 查询详情，主键为 id。 */
export function getType(id: number) {
  return requestClient.get<SysDictType>(`/system/dict-type/${id}`)
}

export function addType(data: DictTypeSaveParams) {
  return requestClient.post<void>('/system/dict-type', data)
}

export function updateType(data: DictTypeSaveParams) {
  return requestClient.put<void>('/system/dict-type', data)
}

/** 支持单个 id 和批量 id。 */
export function delType(id: number | number[]) {
  const ids = Array.isArray(id) ? id.join(',') : id
  return requestClient.delete<void>(`/system/dict-type/${ids}`)
}

/** 刷新后端缓存；成功后调用 dictStore.cleanDict() 同步清理前端缓存。 */
export function refreshCache() {
  return requestClient.delete<void>('/system/dict-type/cache')
}

/** 保留现有调用名称，对应后端 options 接口。 */
export function optionselect() {
  return requestClient.get<SysDictType[]>('/system/dict-type/options')
}

export function exportType(query: DictTypeQueryParams = {}) {
  return requestClient.download('/system/dict-type/export', {
    method: 'POST',
    params: query,
  })
}
