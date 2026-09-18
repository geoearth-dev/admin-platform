import type { OnlineQueryParams, SysUserOnline } from '@/types/base/api/monitor/online';
import { requestClient } from '@/utils/request';

// 查询在线用户列表
export function list(params: OnlineQueryParams = {}) {
  return requestClient.get<SysUserOnline[]>('/monitor/online-session/list', params);
}

// 强退用户
export function forceLogout(tokenId: string) {
  return requestClient.delete<void>(`/monitor/online-session/${tokenId}`);
}
