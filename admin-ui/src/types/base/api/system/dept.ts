import type { BaseEntity } from '../common';

/** 部门查询参数 */
export interface DeptQueryParams {
  /** 部门名称 */
  deptName?: string;
  /** 状态 */
  status?: '0' | '1';
}

/** 保存部门参数，对应后端 DepartmentSaveDTO。 */
export interface DeptSaveParams {
  /** 修改时传入，新增时不传。 */
  id?: number;
  /** 新增必须选择已有部门；现有顶级部门修改时保留 0。 */
  parentId: number;
  deptName: string;
  orderNum: number;
  leader?: string;
  phone?: string;
  email?: string;
  /** 1 启用，0 停用。 */
  status: '0' | '1';
}

/** 保存部门排序参数 */
export interface DeptSortParams {
  deptIds: string;
  orderNums: string;
}

/** 部门信息 */
export interface SysDept extends BaseEntity {
  /** 部门编号 */
  id: number;
  /** 父部门ID */
  parentId?: number;
  /** 父部门名称，详情接口返回。 */
  parentName?: string;
  /** 祖级列表 */
  ancestors?: string;
  /** 部门名称 */
  deptName?: string;
  /** 显示顺序 */
  orderNum?: number;
  /** 负责人 */
  leader?: string;
  /** 联系电话 */
  phone?: string;
  /** 邮箱 */
  email?: string;
  /** 状态（1启用 0停用） */
  status?: '0' | '1';
  /** 子部门 */
  children?: SysDept[];
}
