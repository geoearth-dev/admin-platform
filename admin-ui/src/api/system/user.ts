import type { PageResult, TreeSelect } from '@/types/base/api/common';
import type { SysUserRoles } from '@/types/base/api/system/role';
import type { SysUser, UserProfileResult, UserQueryParams } from '@/types/base/api/system/user';
import { requestClient } from '@/utils/request';

// 查询用户列表，分页参数为 pageNum、pageSize。
export function listUser(query: UserQueryParams) {
  return requestClient.get<PageResult<SysUser>>('/system/user/list', query);
}

// 不传 ID 时获取新增表单所需的角色、岗位选项。
export function getUser(userId: number) {
  return requestClient.get<SysUser>(`/system/user/detail/${userId}`);
}

// 新增用户。
export function addUser(data: SysUser) {
  return requestClient.post<void>('/system/user', data);
}

// 修改用户，主键字段为 id。
export function updateUser(data: SysUser) {
  return requestClient.put<void>('/system/user', data);
}

// 删除用户，支持单个 ID 或 ID 数组。
export function deleteUser(userId: number | number[]) {
  const ids = Array.isArray(userId) ? userId.join(',') : userId;
  return requestClient.delete<void>(`/system/user/${ids}`);
}

// 用户密码重置
export function resetUserPwd(userId: number, password: string) {
  return requestClient.put<void>('/system/user/password', { id: userId, password });
}

// 修改用户状态：0 正常，1 停用。
export function changeUserStatus(userId: number, status: '0' | '1') {
  return requestClient.put<void>('/system/user/status', { id: userId, status });
}

// 查询当前用户个人资料。
export function getUserProfile() {
  return requestClient.get<UserProfileResult>('/system/profile');
}

// 修改当前用户个人资料。
export function updateUserProfile(data: SysUser) {
  return requestClient.put<void>('/system/profile', data);
}

// 修改当前用户密码，后端接收 JSON 请求体。
export function updateUserPwd(oldPassword: string, newPassword: string) {
  return requestClient.put<void>('/system/profile/password', { oldPassword, newPassword });
}

// 查询用户及其授权角色。
export function getAuthRole(userId: number) {
  return requestClient.get(`/system/user/${userId}/roles`);
}

// 保存授权角色，roleIds 传数组；传空数组表示清空角色。
export function updateAuthRole(data: SysUserRoles) {
  return requestClient.put<void>(`/system/user/${data.userId}/roles`, {
    roleIds: data.roleIds,
  });
}

// 查询当前权限范围内的部门树。
export function deptTreeSelect() {
  return requestClient.get<TreeSelect[]>('/system/user/dept-tree');
}
