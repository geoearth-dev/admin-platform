import type { PageResult, TreeSelect } from '@/types/base/api/common';
import type {
  RoleDataScopeParams,
  RoleDeptTreeResult,
  RoleQueryParams,
  RoleSaveParams,
  SysRole,
  SysUserRole,
} from '@/types/base/api/system/role';
import type { AuthUserQueryParams, SysUser } from '@/types/base/api/system/user';
import { requestClient } from '@/utils/request';

// 查询角色列表
export function listRole(query: RoleQueryParams) {
  return requestClient.get<PageResult<SysRole>>('/system/role/list', query);
}

// 导出角色，返回 Excel 文件内容。
export function exportRole(query: RoleQueryParams) {
  return requestClient.download('/system/role/export', {
    method: 'POST',
    params: query,
    timeout: 120_000,
  });
}

// 查询角色选择框列表
export function roleOptions() {
  return requestClient.get<SysRole[]>('/system/role/options');
}

// 查询角色详细
export function getRole(roleId: number) {
  return requestClient.get<SysRole>('/system/role/' + roleId);
}
// 新增角色
export function addRole(data: RoleSaveParams) {
  return requestClient.post<void>('/system/role', data);
}

// 修改角色
export function updateRole(data: RoleSaveParams) {
  return requestClient.put<void>('/system/role', data);
}

// 角色数据权限
export function dataScope(data: RoleDataScopeParams) {
  return requestClient.put<void>('/system/role/data-scope', data);
}

// 角色状态修改
export function changeRoleStatus(roleId: number, status: string) {
  return requestClient.put<void>('/system/role/status', {
    id: roleId,
    status,
  });
}

// 删除角色
export function deleteRole(roleId: number | number[]) {
  const ids = Array.isArray(roleId) ? roleId.join(',') : roleId;
  return requestClient.delete<void>(`/system/role/${ids}`);
}

// 查询角色已授权用户列表
export function allocatedUserList(query: AuthUserQueryParams) {
  return requestClient.get<PageResult<SysUser>>('/system/role/users/allocated', query);
}

// 查询角色未授权用户列表
export function unallocatedUserList(query: AuthUserQueryParams) {
  return requestClient.get<PageResult<SysUser>>('/system/role/users/unallocated', query);
}

// 取消用户授权角色
export function authUserCancel(data: SysUserRole) {
  return requestClient.delete<void>('/system/role/users', { data });
}

// 批量取消用户授权角色
export function authUserCancelAll(roleId: number, userIds: number[]) {
  return requestClient.delete<void>(`/system/role/${roleId}/users`, {
    data: userIds,
  });
}

// 授权用户选择
export function authUserSelectAll(roleId: number, userIds: number[]) {
  return requestClient.post<void>(`/system/role/${roleId}/users`, userIds);
}

// 根据角色ID查询部门树结构
export function deptTreeSelect(roleId: number): Promise<RoleDeptTreeResult> {
  return requestClient.get<RoleDeptTreeResult>('/system/role/dept-tree/' + roleId);
}

// 角色表单使用菜单树接口，不依赖菜单管理页的列表权限。
export function roleMenuTreeSelect() {
  return requestClient.get<TreeSelect[]>('/system/menu/tree-select');
}

export function getRoleMenuTree(roleId: number) {
  return requestClient.get<{ checkedKeys: number[]; menus: TreeSelect[] }>(
    `/system/menu/role-tree/${roleId}`,
  );
}
