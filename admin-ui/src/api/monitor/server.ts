import type { ServerMetrics } from '@/types/base/api/monitor/server';
import { requestClient } from '@/utils/request';

// 获取服务信息
export function getServer() {
  return requestClient.get<ServerMetrics>('/monitor/server');
}
