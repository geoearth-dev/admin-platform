import type { PageResult } from '@/types/base/api/common';
import type { ConfigQueryParams, ConfigSaveParams, SysConfig } from '@/types/base/api/system/config';
import { requestClient } from '@/utils/request';

// 查询参数列表
export function listConfig(query: ConfigQueryParams = {}) {
  return requestClient.get<PageResult<SysConfig>>('/system/config/list', query);
}

// 查询参数详细
export function getConfig(id: number) {
  return requestClient.get<SysConfig>(`/system/config/${id}`);
}

// 根据参数键名查询参数值
export function getConfigKey(configKey: string) {
  return requestClient.get<string>(`/system/config/configKey/${encodeURIComponent(configKey)}`);
}

// 新增参数配置
export function addConfig(data: ConfigSaveParams) {
  return requestClient.post<void>('/system/config', data);
}

// 修改参数配置
export function updateConfig(data: ConfigSaveParams) {
  return requestClient.put<void>('/system/config', data);
}

// 删除参数配置
export function delConfig(id: number | number[]) {
  const ids = Array.isArray(id) ? id.join(',') : id;
  return requestClient.delete<void>(`/system/config/${ids}`);
}

// 刷新参数缓存
export function refreshCache() {
  return requestClient.delete<void>('/system/config/refreshCache');
}

// 导出参数配置，返回 Excel 文件内容。
export function exportConfig(query: ConfigQueryParams = {}) {
  return requestClient.download('/system/config/export', {
    method: 'POST',
    params: query,
  });
}
