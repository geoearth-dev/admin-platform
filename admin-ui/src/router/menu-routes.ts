import type { RouteRecordRaw } from 'vue-router'

/** 补齐目录默认入口；跳过外链、隐藏页和必须提供动态参数的页面。 */
export function completeMenuRedirects(
  routes: RouteRecordRaw[],
  parentPath = '',
): RouteRecordRaw[] {
  return routes.map((route) => {
    const fullPath = (
      route.path.startsWith('/') ? route.path : parentPath + '/' + route.path
    ).replace(/\/{2,}/g, '/')
    const children = completeMenuRedirects(route.children ?? [], fullPath)
    const result = { ...route, children }
    if (
      !result.redirect &&
      !result.component &&
      !result.components &&
      !fullPath.includes(':')
    ) {
      const first = children.find(
        (child) =>
          !child.meta?.hideInMenu &&
          !child.meta?.link &&
          !child.path.includes(':') &&
          !!(child.component || child.components || child.redirect),
      )
      if (first) {
        result.redirect = first.path.startsWith('/')
          ? first.path
          : (fullPath + '/' + first.path).replace(/\/{2,}/g, '/')
      }
    }
    return result
  })
}
