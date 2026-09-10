import type { MenuQueryParams } from '@/types/base/api/system/menu';
import { requestClient } from '@/utils/request';

// 查询菜单列表
export function listMenu(query?: MenuQueryParams) {
  return requestClient.get('/system/menu/list', query);
}
