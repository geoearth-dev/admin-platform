/**后端统一响应结构 */
export interface AjaxResult<T> {
  /**
   * 200 表示成功 其他表示失败
   */
  code: number;
  data: T;
  message: string;
}
/**后端分页统一响应 */
export interface PageResult<T> {
  /**当前页数据 */
  records: Array<T>;
  /**总记录数 */
  total: number;
  /**当前页码 */
  pageNum: number;
  /**每页数量 */
  pageSize: number;
  /**总页数 */
  pages: number;
}
/** 分页参数类型 */
export interface PageParam extends BaseEntity {
  /** 当前记录起始索引 */
  pageNum?: number;
  /** 每页显示记录数 */
  pageSize?: number;
}
/** * 可排序的分页参数 */
export interface SortPageParam extends PageParam {
  sortingFields: Array<SortField>;
}
/** 排序字段 */
export interface SortField {
  field: string;
  order: string;
}

/** Entity基类 */
export interface BaseEntity {
  /** 搜索值 */
  searchValue?: string;
  /** 创建者id */
  creatorId?: string;
  /** 创建者 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 更新者id */
  updaterId?: string;
  /** 更新者 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 备注 */
  remark?: string;
  /** 请求参数 */
  params?: Record<string, unknown>;
}

/** Treeselect树结构类型 */
export interface TreeSelect extends Record<string, unknown> {
  /** 节点ID */
  id: number;
  /** 节点名称 */
  label: string;
  /** 节点禁用 */
  disabled?: boolean;
  /** 子节点 */
  children?: TreeSelect[];
}
