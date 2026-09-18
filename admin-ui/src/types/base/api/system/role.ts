import type { BaseEntity, PageParam, TreeSelect } from '../common';

/** 角色分页查询参数 */
export interface RoleQueryParams extends PageParam {
  /** 角色id */
  id?: number;
  /** 角色名称 */
  roleName?: string;
  /** 角色权限 */
  roleKey?: string;
  /** 状态 */
  status?: string;
  /** 创建开始日期 */
  beginTime?: string;
  /** 创建结束日期 */
  endTime?: string;
}

/** 用户和角色关联信息 */
export interface SysUserRole {
  /** 用户编号 */
  userId: number;
  /** 角色编号 */
  roleId: number;
}

/** 角色信息 */
export interface SysRole extends BaseEntity {
  /** 角色编号 */
  id: number;
  /** 角色名称 */
  roleName: string;
  /** 角色权限 */
  roleKey?: string;
  /** 角色排序  */
  roleSort?: number;
  /** 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限） */
  dataScope?: '1' | '2' | '3' | '4' | '5';
  /** 菜单树勾选是否父子联动（true：联动，false：独立勾选） */
  menuCheckLinked?: boolean;
  /** 部门树勾选是否父子联动（true：联动，false：独立勾选） */
  deptCheckLinked?: boolean;
  /** 角色权限 */
  menuIds?: number[];
  /** 角色权限 */
  deptIds?: number[];
  /** 状态（0正常 1停用） */
  status?: '0' | '1';
}

/** 角色保存参数  */
export interface RoleSaveParams {
  /** 角色编号 */
  id?: number;
  /** 角色名称 */
  roleName: string;
  /** 角色权限标识 */
  roleKey: string;
  /** 显示顺序 */
  roleSort: number;
  /** 数据范围（1：全部；2：自定义；3：本部门；4：本部门及以下；5：仅本人） */
  dataScope?: '1' | '2' | '3' | '4' | '5';
  /** 菜单树勾选是否父子联动（true：联动，false：独立勾选） */
  menuCheckLinked?: boolean;
  /** 部门树勾选是否父子联动（true：联动，false：独立勾选） */
  deptCheckLinked?: boolean;
  /** 角色状态 */
  status?: '0' | '1';
  /** 菜单编号列表 */
  menuIds?: number[];
  /** 部门编号列表 */
  deptIds?: number[];
  /** 备注 */
  remark?: string;
}

/** 角色数据权限参数  */
export interface RoleDataScopeParams {
  id: number;
  dataScope?: '1' | '2' | '3' | '4' | '5';
  deptCheckLinked?: boolean;
  deptIds?: number[];
}

/** 角色部门树响应 */
export interface RoleDeptTreeResult {
  /** 选中部门ID */
  checkedKeys: number[];
  /** 部门树列表 */
  departments: TreeSelect[];
}
