import type { TreeSelect } from '@/types/base/api/common'
import type {
  MenuQueryParams,
  MenuSaveParams,
  MenuSortParams,
  RoleMenuTreeselectResult,
  SysMenu,
} from '@/types/base/api/system/menu'
import { requestClient } from '@/utils/request'

// 查询菜单列表
export function listMenu(query?: MenuQueryParams): Promise<SysMenu[]> {
  return requestClient.get<SysMenu[]>('/system/menu/list', query)
}

// 查询菜单详细
export function getMenu(menuId: number): Promise<SysMenu> {
  return requestClient.get('/system/menu/' + menuId)
}

// 查询菜单下拉树结构
export function treeselect(): Promise<TreeSelect[]> {
  return requestClient.get('/system/menu/tree-select')
}

// 根据角色ID查询菜单下拉树结构
export function roleMenuTreeselect(
  roleId: number,
): Promise<RoleMenuTreeselectResult> {
  return requestClient.get('/system/menu/role-tree/' + roleId)
}

// 新增菜单
export function addMenu(data: MenuSaveParams): Promise<void> {
  return requestClient.post<void>('/system/menu', data)
}

// 修改菜单
export function updateMenu(data: MenuSaveParams): Promise<void> {
  return requestClient.put<void>('/system/menu', data)
}

// 保存菜单排序
export function updateMenuSort(data: MenuSortParams): Promise<void> {
  return requestClient.put<void>('/system/menu/update-sort', data)
}

// 删除菜单
export function delMenu(menuId: number): Promise<void> {
  return requestClient.delete('/system/menu/' + menuId)
}
