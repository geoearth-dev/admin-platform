import type { LocationQueryRaw } from 'vue-router'
import type { RouteDisplayOptions } from '../../router'
import type { BaseEntity, TreeSelect } from '../common'
import type { MenuBadgeType, MenuBadgeVariant } from '../../menu-record'

export const MenuTypes = [
  'catalog',
  'menu',
  'embedded',
  'link',
  'button',
] as const
export type MenuType = (typeof MenuTypes)[number]

/** 菜单查询参数，与 SysMenuController.list 保持一致。 */
export interface MenuQueryParams {
  menuName?: string
  /** 1 启用，0 停用；菜单接口使用数字。 */
  status?: 0 | 1
  hideInMenu?: boolean
}

/** 菜单保存参数，对应后端 MenuSaveDTO。 */
export interface MenuSaveParams {
  /** 修改时传入，新增时不传。 */
  id?: number
  menuName: string
  /** 顶级菜单为 0。 */
  parentId: number
  /** 显示顺序。 */
  order: number
  /** catalog 目录、menu 菜单、embedded 内嵌、link 外链、button 按钮。 */
  menuType: MenuType
  /** 系统内部路由地址。 */
  path?: string | null
  component?: string | null
  /** 路由查询参数对象；null 表示清空。 */
  query?: LocationQueryRaw | null
  routeName?: string | null
  /** 外部网址，与 iframeSrc 互斥。 */
  link?: string | null
  iframeSrc?: string | null
  keepAlive: boolean
  hideInMenu: boolean
  /** 1 启用，0 停用。 */
  status: 0 | 1
  perms?: string | null
  icon?: string | null
  /** 激活图标 */
  activeIcon?: string | null
  /** 激活菜单路径 */
  activePath?: string | null
  /** 固定标签 */
  affixTab?: boolean
  /** 固定标签顺序 */
  affixTabOrder?: number
  /** 徽标内容 */
  badge?: string | null
  /** 徽标类型 dot/normal */
  badgeType?: MenuBadgeType | null
  /** 徽标样式 */
  badgeVariants?: MenuBadgeVariant | null
  /** 隐藏子菜单 */
  hideChildrenInMenu?: boolean
  /** 隐藏面包屑 */
  hideInBreadcrumb?: boolean
  /** 隐藏标签 */
  hideInTab?: boolean
  /** 内部重定向路径 */
  redirect?: string | null
  /** 新窗口打开 */
  openInNewWindow?: boolean
  /** 不使用基础布局 */
  noBasicLayout?: boolean
  /** 同一路由最多标签数 -1不限 */
  maxNumOfOpenTab?: number
}

/** 菜单查询结果；列表接口返回平铺数组，通过 parentId 组成树。 */
export interface SysMenu extends BaseEntity, MenuSaveParams {
  id: number
  parentName?: string
  children?: SysMenu[]
}

/** 保存菜单排序参数。 */
export interface MenuSortParams {
  menuIds: string
  orderNums: string
}

export interface RoleMenuTreeselectResult {
  checkedKeys: number[]
  menus: TreeSelect[]
}

/** 登录后下发的菜单路由，对应后台 RouterVo；按钮不生成路由。 */
export interface MenuRoute {
  /** 唯一路由名称。 */
  name: string
  /** 顶级以 / 开头，子路由可以使用相对路径。 */
  path: string
  redirect?: string
  /** src/views 下的组件路径或 IFrameView；目录、外链可以省略。 */
  component?: string
  meta: MenuRouteMeta
  children?: MenuRoute[]
}

/** 接口返回的路由配置：图标只能是字符串，不能传 Vue 组件。 */
export interface MenuRouteMeta extends RouteDisplayOptions {
  icon?: string
  badgeVariants?: MenuBadgeVariant
  keepAlive: boolean
  hideInMenu: boolean
}
