import type { PageResult } from '@/types/base/api/common';
import type { GenQueryParams, GenTable, GenTableInfoResult } from '@/types/base/api/tool/gen';
import { requestClient } from '@/utils/request';

// 查询生成表数据
export function listTable(params: GenQueryParams) {
  return requestClient.get<PageResult<GenTable>>('/tool/gen/list', params);
}

// 查询db数据库列表
export function listDbTable(params: GenQueryParams) {
  return requestClient.get<PageResult<GenTable>>('/tool/gen/db/list', params);
}

// 查询表详细信息
export function getGenTable(tableId: number) {
  return requestClient.get<GenTableInfoResult>('/tool/gen/' + tableId);
}

// 修改代码生成信息
export function updateGenTable(data: GenTable) {
  return requestClient.put<void>('/tool/gen', data);
}

// 导入表
export function importTable(data: any) {
  return requestClient.post<void>('/tool/gen/importTable', data);
}

// 创建表
export function createTable(data: any): Promise<AjaxResult> {
  return request({
    url: '/tool/gen/createTable',
    method: 'post',
    params: data,
  });
}

// 预览生成代码
export function previewTable(tableId: number): Promise<AjaxResult<any>> {
  return request({
    url: '/tool/gen/preview/' + tableId,
    method: 'get',
  });
}

// 删除表数据
export function delTable(tableId: number | number[]): Promise<AjaxResult> {
  return request({
    url: '/tool/gen/' + tableId,
    method: 'delete',
  });
}

// 生成代码（自定义路径）
export function genCode(tableName: string): Promise<AjaxResult> {
  return request({
    url: '/tool/gen/genCode/' + tableName,
    method: 'get',
  });
}

// 同步数据库
export function synchDb(tableName: string): Promise<AjaxResult> {
  return request({
    url: '/tool/gen/synchDb/' + tableName,
    method: 'get',
  });
}
