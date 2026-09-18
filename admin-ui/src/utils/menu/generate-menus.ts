import type { NavigationMenu } from '@/types'
import type { Router, RouteRecordRaw } from 'vue-router'
import { constantRoutes } from '@/router/routes'

/** 从已注册的路由生成导航菜单，不修改原始路由树。 */
function generateMenus(
  routes: RouteRecordRaw[],
  router: Router,
): NavigationMenu[] {
  const routePaths = new Map(
    router.getRoutes().map(({ name, path }) => [name, path]),
  )

  function convert(
    items: RouteRecordRaw[],
    parents: string[] = [],
  ): NavigationMenu[] {
    return items
      .filter((route) => !route.meta?.hideInMenu)
      .map((route): NavigationMenu => {
        const path =
          (route.name ? routePaths.get(route.name) : undefined) ?? route.path
        const meta = route.meta
        let targetPath = meta?.link || path
        if (
          meta?.hideChildrenInMenu &&
          route.redirect &&
          typeof route.redirect !== 'function'
        ) {
          targetPath = router.resolve(route.redirect).path
        }
        return {
          name: meta?.title || String(route.name ?? ''),
          path: targetPath,
          icon: meta?.icon,
          activeIcon: meta?.activeIcon,
          badge: meta?.badge,
          badgeType: meta?.badgeType,
          badgeVariants: meta?.badgeVariants,
          query: meta?.query,
          order: meta?.order,
          parent: parents.at(-1),
          parents,
          show: true,
          children: meta?.hideChildrenInMenu
            ? []
            : convert(route.children ?? [], [...parents, path]),
        }
      })
      .sort((a, b) => (a.order ?? 999) - (b.order ?? 999))
  }

  return convert([...constantRoutes, ...routes])
}

export { generateMenus }
