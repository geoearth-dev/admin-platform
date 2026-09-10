import type { DeptQueryParams, SysDept } from '@/types/base/api/system/dept';
import { requestClient } from '@/utils/request';

// 查询部门列表，返回数组，不分页。
export function listDept(query?: DeptQueryParams) {
  return requestClient.get<SysDept[]>('/system/dept/list', query);
}

// 查询部门列表，排除指定部门及其全部子部门。
export function listDeptExcludeChild(deptId: number) {
  return requestClient.get<SysDept[]>(`/system/dept/list/exclude/${deptId}`);
}

// 查询部门详情。
export function getDept(deptId: number) {
  return requestClient.get<SysDept>(`/system/dept/${deptId}`);
}

// 新增部门，部门名称和显示顺序必填。
export function addDept(data: SysDept) {
  return requestClient.post<void>('/system/dept', data);
}

// 修改部门，主键字段为 id。
export function updateDept(data: SysDept) {
  return requestClient.put<void>('/system/dept', data);
}

// 删除单个部门。
export function delDept(deptId: number) {
  return requestClient.delete<void>(`/system/dept/${deptId}`);
}
