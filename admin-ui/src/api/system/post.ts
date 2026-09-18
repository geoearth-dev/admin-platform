import type { PageResult } from '@/types/base/api/common';
import type { PostQueryParams, PostSaveParams, SysPost } from '@/types/base/api/system/post';
import { requestClient } from '@/utils/request';

// 查询岗位列表
export function listPost(query: PostQueryParams) {
  return requestClient.get<PageResult<SysPost>>('/system/post/list', query);
}

// 查询岗位详细
export function getPost(postId: number) {
  return requestClient.get<SysPost>(`/system/post/${postId}`);
}

// 新增岗位
export function addPost(data: PostSaveParams) {
  return requestClient.post<void>('/system/post', data);
}

// 修改岗位
export function updatePost(data: PostSaveParams) {
  return requestClient.put<void>('/system/post', data);
}

// 删除岗位
export function delPost(postId: number | number[]) {
  const ids = Array.isArray(postId) ? postId.join(',') : postId;
  return requestClient.delete<void>(`/system/post/${ids}`);
}

// 查询岗位选择框列表
export function postOptions() {
  return requestClient.get<SysPost[]>('/system/post/options');
}

// 导出岗位，返回 Excel 文件内容。
export function exportPost(query: PostQueryParams) {
  return requestClient.download('/system/post/export', {
    method: 'POST',
    params: query,
  });
}
