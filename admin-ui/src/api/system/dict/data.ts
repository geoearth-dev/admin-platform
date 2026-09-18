import type { PageResult } from '@/types/base/api/common'
import type {
  DictDataQueryParams,
  DictDataSaveParams,
  SysDictData,
} from '@/types/base/api/system/dict'
import { requestClient } from '@/utils/request'

/** 分页查询字典数据，返回 records/total。 */
export function listData(query: DictDataQueryParams = {}) {
  return requestClient.get<PageResult<SysDictData>>(
    '/system/dict-data/list',
    query,
  )
}

/** 查询详情，主键为 id。 */
export function getData(id: number) {
  return requestClient.get<SysDictData>(`/system/dict-data/${id}`)
}

/** 按类型获取正常状态的字典数据，返回数组，不分页。 */
export function getDicts(dictType: string) {
  return requestClient.get<SysDictData[]>(
    `/system/dict-data/type/${encodeURIComponent(dictType)}`,
  )
}

export function addData(data: DictDataSaveParams) {
  return requestClient.post<void>('/system/dict-data', data)
}

export function updateData(data: DictDataSaveParams) {
  return requestClient.put<void>('/system/dict-data', data)
}

/** 支持单个 id 和批量 id。 */
export function delData(id: number | number[]) {
  const ids = Array.isArray(id) ? id.join(',') : id
  return requestClient.delete<void>(`/system/dict-data/${ids}`)
}

/** 导出匹配查询条件的全部数据。 */
export function exportData(query: DictDataQueryParams = {}) {
  return requestClient.download('/system/dict-data/export', {
    method: 'POST',
    params: query,
  })
}
