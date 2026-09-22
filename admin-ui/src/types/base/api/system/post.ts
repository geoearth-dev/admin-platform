import type { PageParam, BaseEntity } from '../common';

/** 岗位分页查询参数 */
export interface PostQueryParams extends PageParam {
  /** 岗位编码 */
  postCode?: string;
  /** 岗位名称 */
  postName?: string;
  /** 状态 */
  status?: string;
}

/** 已保存的岗位信息，新增、修改请求使用 PostSaveParams。 */
export interface SysPost extends BaseEntity {
  /** 岗位编号 */
  id: number;
  /** 岗位编码 */
  postCode?: string;
  /** 岗位名称 */
  postName: string;
  /** 岗位排序 */
  postSort?: number;
  /** 状态（0停用 1正常） */
  status?: '0' | '1';
}

/** 新增、修改岗位 请求参数 */
export interface PostSaveParams {
  /** 岗位编号，修改时传入 */
  id?: number;
  /** 岗位编码 */
  postCode: string;
  /** 岗位名称 */
  postName: string;
  /** 显示顺序 */
  postSort: number;
  /** 状态（0停用 1正常） */
  status?: string;
  /** 备注 */
  remark?: string;
}
