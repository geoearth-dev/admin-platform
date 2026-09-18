import type { LocationQueryRaw } from 'vue-router'
import type {
  MenuSaveParams,
  MenuType,
  SysMenu,
} from '@/types/base/api/system/menu'

/** 只有编辑框使用 JSON 文本；接口和其余代码使用 query 对象。 */
export interface MenuFormValues extends Omit<MenuSaveParams, 'query'> {
  query?: string | null
  linkSrc?: string
}

/** 链接输入仅作为表单字段；保存时按类型写入 link 或 iframeSrc。 */
export function toMenuSaveParams(
  values: MenuFormValues,
  id?: number,
): MenuSaveParams {
  const button = values.menuType === 'button'
  const internal = values.menuType === 'menu'
  const page = internal || values.menuType === 'embedded'
  const route = !button
  const tree = values.menuType === 'catalog' || internal
  const text = (value?: string | null) => value?.trim() || null
  return {
    id,
    menuType: values.menuType,
    menuName: values.menuName.trim(),
    parentId: values.parentId,
    order: values.order,
    status: values.status,
    path: button ? '' : text(values.path),
    routeName: button ? null : text(values.routeName),
    component: internal
      ? text(values.component)
          ?.replace(/^\/+/, '')
          .replace(/\.vue$/, '')
      : null,
    link: values.menuType === 'link' ? text(values.linkSrc) : null,
    iframeSrc: values.menuType === 'embedded' ? text(values.linkSrc) : null,
    perms: text(values.perms),
    icon: route ? text(values.icon) : null,
    activeIcon: route ? text(values.activeIcon) : null,
    activePath: page ? text(values.activePath) : null,
    query: route ? parseMenuQuery(values.query) : null,
    redirect: tree ? text(values.redirect) : null,
    badgeType: route ? values.badgeType || null : null,
    badge: route && values.badgeType === 'normal' ? text(values.badge) : null,
    badgeVariants:
      route && values.badgeType ? values.badgeVariants || 'default' : null,
    keepAlive: internal && !!values.keepAlive,
    hideInMenu: route && !!values.hideInMenu,
    hideChildrenInMenu: tree && !!values.hideChildrenInMenu,
    hideInBreadcrumb: route && !!values.hideInBreadcrumb,
    hideInTab: page && !!values.hideInTab,
    affixTab: page && !!values.affixTab,
    affixTabOrder: page && values.affixTab ? (values.affixTabOrder ?? 0) : 0,
    noBasicLayout: page && values.parentId === 0 && !!values.noBasicLayout,
    openInNewWindow: page && !!values.openInNewWindow,
    maxNumOfOpenTab: page ? (values.maxNumOfOpenTab ?? -1) : -1,
  }
}

export function toMenuFormValues(
  menu?: SysMenu,
  parentId = 0,
  menuType: MenuType = 'catalog',
): MenuFormValues {
  return {
    ...menu,
    parentId: menu?.parentId ?? parentId,
    menuType: menu?.menuType ?? menuType,
    menuName: menu?.menuName ?? '',
    path: menu?.path ?? '',
    routeName: menu?.routeName ?? '',
    component: menu?.component ?? '',
    linkSrc: menu?.link || menu?.iframeSrc || '',
    query: menu?.query ? JSON.stringify(menu.query, null, 2) : '',
    status: menu?.status ?? 1,
    order: menu?.order ?? 0,
    keepAlive: menu?.keepAlive ?? false,
    hideInMenu: menu?.hideInMenu ?? false,
    affixTab: menu?.affixTab ?? false,
    affixTabOrder: menu?.affixTabOrder ?? 0,
    hideChildrenInMenu: menu?.hideChildrenInMenu ?? false,
    hideInBreadcrumb: menu?.hideInBreadcrumb ?? false,
    hideInTab: menu?.hideInTab ?? false,
    noBasicLayout: menu?.noBasicLayout ?? false,
    openInNewWindow: menu?.openInNewWindow ?? false,
    maxNumOfOpenTab: menu?.maxNumOfOpenTab ?? -1,
  }
}

/** 搜索保留祖先；接口仍返回平铺数组给 VXE 转换成树。 */
export function filterMenuTree(
  menus: SysMenu[],
  name: string,
  status?: 0 | 1,
  title: (value: string) => string = (value) => value,
) {
  const keyword = name.trim().toLowerCase()
  const included = new Set<number>()
  const nodes = new Map(menus.map((menu) => [menu.id, menu]))
  for (const menu of menus) {
    if (status !== undefined && menu.status !== status) continue
    if (
      keyword &&
      ![
        menu.menuName,
        title(menu.menuName),
        menu.routeName ?? '',
        menu.path ?? '',
        menu.perms ?? '',
      ].some((value) => value.toLowerCase().includes(keyword))
    )
      continue
    let node: SysMenu | undefined = menu
    while (node && !included.has(node.id)) {
      included.add(node.id)
      node = nodes.get(node.parentId)
    }
  }
  return menus.filter((menu) => included.has(menu.id))
}

/** 将编辑框文本转换成 Vue Router 支持的查询参数。 */
export function parseMenuQuery(text?: string | null): LocationQueryRaw | null {
  if (!text?.trim()) return null
  const query: unknown = JSON.parse(text)
  const isScalar = (value: unknown) =>
    value === null || typeof value === 'string' || typeof value === 'number'
  if (
    query === null ||
    typeof query !== 'object' ||
    Array.isArray(query) ||
    !Object.values(query).every(
      (value) =>
        isScalar(value) || (Array.isArray(value) && value.every(isScalar)),
    )
  ) {
    throw new Error(
      '查询参数必须是对象，值只能为字符串、数字、null 或这些值组成的数组',
    )
  }
  return query as LocationQueryRaw
}
