/** 平铺菜单，供 ApiSelect 和 ApiTreeSelect 示例共用。 */
const menus = [
  { id: 1, parentId: 0, menuName: '系统管理' },
  { id: 11, parentId: 1, menuName: '用户管理' },
  { id: 12, parentId: 1, menuName: '角色管理' },
  { id: 13, parentId: 1, menuName: '菜单管理' },
  { id: 2, parentId: 0, menuName: '业务管理' },
  { id: 21, parentId: 2, menuName: '商品管理' },
  { id: 22, parentId: 2, menuName: '订单管理' },
  { id: 3, parentId: 0, menuName: '统计报表' },
  { id: 31, parentId: 3, menuName: '销售统计' },
]

export async function getExampleMenuApi(params: Record<string, unknown> = {}) {
  const keyword =
    typeof params.menuName === 'string' ? params.menuName.trim() : ''
  return menus
    .filter((menu) => menu.menuName.includes(keyword))
    .map((menu) => ({ ...menu }))
}
