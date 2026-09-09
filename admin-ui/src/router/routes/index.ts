import type { RouteRecordRaw } from 'vue-router'

import { exampleDynamicRouter } from './example/dynamic'
import { examplePublicRouter } from './example/public'
import { systemDynamicRouter } from './system/dynamic'
import { systemPublicRouter } from './system/public'

/**
 * 本地路由直接使用 RouteRecordRaw，component 配置实际组件或懒加载函数。
 * 展示配置放入 meta：title、icon、order、hideInMenu、keepAlive、query 等。
 * 本地访问条件放入 meta.permissions（权限码）或 meta.authority（角色）。
 * 详情页使用 meta.activePath 指定菜单高亮；redirect 必须是真实跳转目标。
 */

// 启动时注册的固定路由；是否允许匿名访问由守卫和 meta.ignoreAccess 决定。
const constantRoutes: RouteRecordRaw[] = [
  ...systemPublicRouter,
  ...examplePublicRouter,
]

// 登录后按权限筛选，并与后台路由一起注册在 Index 布局下。
const dynamicRoutes: RouteRecordRaw[] = [
  ...systemDynamicRouter,
  ...exampleDynamicRouter,
]

export { constantRoutes, dynamicRoutes }
