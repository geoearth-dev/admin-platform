import type { BaseEntity, PageParam } from '../common'

/** 字典状态：1 正常，0 停用。字典键值 dictValue 本身不限定为状态值。 */
export type DictStatus = '0' | '1'

/** 对应 DictTypePageReqDTO。 */
export interface DictTypeQueryParams extends PageParam {
  dictName?: string
  dictType?: string
  status?: DictStatus
  /** 日期格式 yyyy-MM-dd。 */
  beginTime?: string
  /** 日期格式 yyyy-MM-dd。 */
  endTime?: string
}

export interface SysDictType extends BaseEntity {
  id: number
  dictName: string
  dictType: string
  status: DictStatus
}

/** 对应 DictTypeSaveDTO；修改时携带 id。 */
export interface DictTypeSaveParams {
  id?: number
  dictName: string
  dictType: string
  status?: DictStatus
  remark?: string
}

/** 对应 DictDataPageReqDTO。 */
export interface DictDataQueryParams extends PageParam {
  dictType?: string
  dictLabel?: string
  status?: DictStatus
}

export interface SysDictData extends BaseEntity {
  id: number
  dictSort: number
  dictLabel: string
  dictValue: string
  dictType: string
  cssClass?: string | null
  listClass?: string | null
  isDefault?: 'N' | 'Y'
  default?: boolean
  status: DictStatus
}

/** 对应 DictDataSaveDTO；修改时携带 id。 */
export interface DictDataSaveParams {
  id?: number
  dictSort?: number
  dictLabel: string
  dictValue: string
  dictType: string
  cssClass?: string
  listClass?: string
  isDefault?: 'N' | 'Y'
  status?: DictStatus
  remark?: string
}
