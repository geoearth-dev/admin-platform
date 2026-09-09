import { requestClient } from '@/utils/request';

// 查询菜单列表
export function listMenu(query: unknown) {
  return requestClient.get('/system/menu/list', query);
}
