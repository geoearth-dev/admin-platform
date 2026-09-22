import { requestClient } from '@/utils/request';
import type { ApiResult, PageResult, TreeSelect } from '@/types/base/api/common';
import type { SysRole } from '@/types/base/api/system/role';
import type {
  SysUser,
  UserProfileResult,
  UserProfileUpdateParams,
  UserQueryParams,
  UserSaveParams,
} from '@/types/base/api/system/user';

// 查询用户列表，分页参数为 pageNum、pageSize。
export function listUser(query: UserQueryParams) {
  return requestClient.get<PageResult<SysUser>>('/system/user/list', query);
}
// 导出用户，返回 Excel 文件内容。
export function exportUser(query?: UserQueryParams) {
  return requestClient.download('/system/user/export', {
    method: 'POST',
    params: query,
  });
}
// 导出角色，返回 Excel 文件内容。
export function downloadUserTemplate() {
  return requestClient.download('/system/user/import-template');
}
/** 导入结果放在 message 中，所以保留完整响应 */
export async function importUser(file: File, updateSupport: boolean) {
  const data = new FormData();
  data.append('file', file);

  const response = await requestClient.raw<ApiResult<null>, FormData>({
    url: '/system/user/import',
    method: 'POST',
    data,
    params: { updateSupport },
    timeout: 120_000,
    silent: true,
  });

  return response.data.message;
}
// 不传 ID 时获取新增表单所需的角色、岗位选项。
export function getUser(userId: number) {
  return requestClient.get<SysUser>(`/system/user/detail/${userId}`);
}

// 新增用户。
export function addUser(data: UserSaveParams) {
  return requestClient.post<void>('/system/user', data);
}

// 修改用户，主键字段为 id。
export function updateUser(data: UserSaveParams) {
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

/** 状态：0 禁用，1 启用 */
export function changeUserStatus(userId: number, status: string) {
  return requestClient.put<void>('/system/user/status', { id: userId, status });
}

// 修改当前用户个人资料。
export function updateUserProfile(data: UserProfileUpdateParams) {
  return requestClient.put<UserProfileResult>('/system/profile', data, { silent: true });
}

export function uploadUserAvatar(file: File) {
  return requestClient.upload<UserProfileResult>('/system/profile/avatar', file, {
    silent: true,
    timeout: 120_000,
  });
}

// 修改当前用户密码，后端接收 JSON 请求体。
export function updateUserPwd(oldPassword: string, newPassword: string) {
  return requestClient.put<void>('/system/profile/password', { oldPassword, newPassword }, { silent: true });
}

// 查询用户及其授权角色。
export function getAuthRole(userId: number) {
  return requestClient.get<Array<SysRole>>(`/system/user/${userId}/roles`);
}

// 保存授权角色，roleIds 传数组；传空数组表示清空角色。
export function updateAuthRole(userId: number, roleIds: number[]) {
  return requestClient.put<void>(`/system/user/${userId}/roles`, {
    roleIds: roleIds,
  });
}

// 查询当前权限范围内的部门树。
export function deptTreeSelect() {
  return requestClient.get<TreeSelect[]>('/system/user/dept-tree');
}

// 查询当前用户个人资料。
export function getUserProfile() {
  return requestClient.get<UserProfileResult>('/system/profile');
}
