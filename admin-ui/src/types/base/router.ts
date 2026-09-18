import type { Component } from 'vue'
import type { LocationQueryRaw } from 'vue-router'
import type { MenuBadgeOptions } from './menu-record'

/** 路由与菜单共用的展示配置；对应后台 MetaVo 中的字段。 */
export interface RouteDisplayOptions extends MenuBadgeOptions {
  /** 菜单、面包屑和标签页标题，支持国际化 key。 */
  title: string
  /** 本地路由也支持直接传入图标组件。 */
  icon?: Component | string
  /** 菜单或标签页激活时使用的图标。 */
  activeIcon?: string
  /** 当前页面需要激活的菜单路径。 */
  activePath?: string
  /** 是否缓存页面组件。 */
  keepAlive?: boolean
  /** 仅隐藏菜单入口，路由仍然注册。 */
  hideInMenu?: boolean
  /** 不展示子菜单，点击当前菜单时使用其重定向地址。 */
  hideChildrenInMenu?: boolean
  /** 不显示在面包屑中。 */
  hideInBreadcrumb?: boolean
  /** 打开页面时不创建标签页。 */
  hideInTab?: boolean
  /** 同级菜单排序，数值越小越靠前。 */
  order?: number
  /** 固定标签及其顺序。 */
  affixTab?: boolean
  affixTabOrder?: number
  /** 点击菜单时携带的查询参数，接口也使用对象。 */
  query?: LocationQueryRaw
  /** 外链地址，与 iframeSrc 互斥。 */
  link?: string
  /** 内嵌页面地址。 */
  iframeSrc?: string
  /** 在浏览器新窗口打开当前路由。 */
  openInNewWindow?: boolean
  /** 不使用基础布局，仅顶级路由有效。 */
  noBasicLayout?: boolean
  /** 同一路由最多打开标签数，-1 表示不限。 */
  maxNumOfOpenTab?: number
}

// Vue Router 的项目扩展直接放在这里，无需再维护一份 vue-router.d.ts。
declare module 'vue-router' {
  interface RouteMeta extends RouteDisplayOptions {
    /** 满足任意权限码即可；优先于 authority。 */
    permissions?: string[]
    /** 允许访问的角色。 */
    authority?: string[]
    /** 允许匿名访问。 */
    ignoreAccess?: boolean
    /** 保留菜单入口，未授权时显示禁止访问页。 */
    menuVisibleWithForbidden?: boolean
    /** 是否缓存 iframe DOM。 */
    domCached?: boolean
    /** 是否使用完整路径区分标签页，默认 true。 */
    fullPathKey?: boolean
    /** 由路由守卫记录的加载状态。 */
    loaded?: boolean
  }
}

export type { RouteMeta, RouteRecordRaw } from 'vue-router'
