import type { BaseEntity, PageParam } from '../common'

export interface GenQueryParams extends PageParam {
  pageNum: number
  pageSize: number
  tableName?: string
  tableComment?: string
  orderByColumn?: 'createTime' | 'updateTime'
  isAsc?: 'ascending' | 'descending'
}
export interface DbTable {
  tableName: string
  tableComment: string
  createTime?: string
  updateTime?: string
}
export type GenFlag = '0' | '1'
export interface GenTable extends BaseEntity, DbTable {
  id: number
  className: string
  tplCategory: 'crud' | 'tree' | 'sub'
  packageName: string
  moduleName: string
  businessName: string
  functionName: string
  functionAuthor: string
  formColNum: 1 | 2 | 3
  genType: '0' | '1'
  genPath: string
  options?: string
  subTableName?: string
  subTableFkName?: string
  treeCode?: string
  treeParentCode?: string
  treeName?: string
  parentMenuId?: number
  parentMenuName?: string
  view: boolean
  columns: GenTableColumn[]
}
export interface GenTableColumn extends BaseEntity {
  id: number
  tableId: number
  columnName: string
  columnComment: string
  columnType: string
  javaType:
    | 'Long'
    | 'String'
    | 'Integer'
    | 'Double'
    | 'BigDecimal'
    | 'Instant'
    | 'LocalDate'
    | 'LocalTime'
    | 'LocalDateTime'
    | 'Date'
    | 'Boolean'
  javaField: string
  isPk: GenFlag
  isIncrement: GenFlag
  isRequired: GenFlag
  isInsert: GenFlag
  isEdit: GenFlag
  isList: GenFlag
  isQuery: GenFlag
  queryType: 'EQ' | 'NE' | 'GT' | 'GE' | 'LT' | 'LE' | 'LIKE' | 'BETWEEN'
  htmlType: string
  dictType?: string
  sort: number
}
export interface GenTableInfoResult {
  info: GenTable
  rows: GenTableColumn[]
  tables: GenTable[]
}
