import type { MenuRoute } from '@/types/base/api/system/menu'
import { requestClient } from '@/utils/request'

/** 获取当前用户可访问的菜单路由。 */
export function getRouters(): Promise<MenuRoute[]> {
  return requestClient.get<MenuRoute[]>('/auth/getRouters')
}
