import { getRouters } from '@/api/admin/auth/router';
import router from '.';
import type { RouteRecordRaw } from 'vue-router';
import type { RouterVo } from '@/types';
import type { Component } from 'vue';
import { generateMenus } from '@/utils/generate/generate-menus';
import { useAccessStore, useUserStore } from '@/store';
import { dynamicRoutes } from './routes';

const IFrameView = () => import('@/layout/iframe/iframe-view.vue');

interface GenerateAccessResult {
  accessibleRoutes: RouteRecordRaw[];
  accessibleMenus: MenuRecordRaw[];
}
/**
 * 页面组件映射。
 * ../views/system/admin/user/index.vue
 */
const pageMap = import.meta.glob<Component>('../views/**/*.vue', {
  import: 'default',
});

let removeRouteCallbacks: Array<() => void> = [];
/** 移除本模块注册的动态路由，保留公共路由。 */
export function resetAccessRoutes(): void {
  removeRouteCallbacks.forEach((removeRoute) => removeRoute());
  removeRouteCallbacks = [];
}

/**
 * 过滤本地动态路由。
 *
 * 权限码使用 meta.permissions，角色使用 meta.authority。
 * 父级不通过时移除整个分支，通过后继续检查子路由。
 * 未配置权限或角色时，允许所有已登录用户访问。
 */
function filterDynamicRoutes(routes: RouteRecordRaw[]): RouteRecordRaw[] {
  const accessStore = useAccessStore();
  const userStore = useUserStore();

  const permissions = new Set(accessStore.permissions);
  const roles = new Set(userStore.userRoles);

  function hasAccess(route: RouteRecordRaw): boolean {
    // 与若依一致：配置了权限码，就优先按权限码判断。
    if (route.meta?.permissions) {
      return route.meta.permissions.some(
        (permission) => permissions.has('*:*:*') || permissions.has(permission),
      );
    }

    if (route.meta?.authority) {
      return route.meta.authority.some((role) => roles.has('admin') || roles.has(role));
    }

    // 例如没有单独配置权限的 Dashboard。
    return true;
  }

  function filterRoutes(items: RouteRecordRaw[]): RouteRecordRaw[] {
    return items.filter(hasAccess).flatMap<RouteRecordRaw>((route) => {
      if (route.children) {
        const children = filterRoutes(route.children);
        // 纯目录的子路由全部被过滤后，不保留空菜单。
        if (
          route.children.length > 0 &&
          children.length === 0 &&
          !route.component &&
          !route.components &&
          !route.redirect
        ) {
          return [];
        }
        return [
          {
            ...route,
            meta: route.meta ? { ...route.meta } : undefined,
            children,
          },
        ];
      }
      return [{ ...route, meta: route.meta ? { ...route.meta } : undefined }];
    });
  }

  // 复制路由树，避免后续菜单处理改动本地配置，影响下一次登录。
  return filterRoutes(routes);
}

/** 将后台组件路径转换成实际的懒加载组件。 */
function resolveComponent(route: RouterVo): RouteRecordRaw['component'] {
  const { component, meta } = route;

  // 外链由菜单点击逻辑打开，不需要内部页面组件。
  if (meta.link) {
    return undefined;
  }

  // iframe 页面统一使用 iframe 容器。
  if (meta.iframeSrc) {
    return IFrameView;
  }

  // 纯目录没有组件。
  if (!component) {
    return undefined;
  }

  /**
   * 后台 component 约定为相对于 src/views 的路径。
   *
   * 例如：
   * system/admin/user/index
   * system/admin/user/index.vue
   */
  const viewPath = component.replace(/^\/+/, '').replace(/\.vue$/, '');

  const page = pageMap[`../views/${viewPath}.vue`];

  if (!page) {
    // throw new Error(`路由 ${route.name} 的组件不存在：${component}`)
  }

  return page;
}
/** 把后台 RouterVo 转换为 Vue Router 路由，保留原始树结构。 */
function convertRoute(source: RouterVo): RouteRecordRaw {
  return {
    name: source.name,
    path: source.path,
    redirect: source.redirect,
    component: resolveComponent(source),
    meta: { ...source.meta },
    children: (source.children ?? []).map(convertRoute),
  };
}

async function generateAccess(): Promise<GenerateAccessResult> {
  const menuRoutes = await getRouters();
  const backendRoutes = menuRoutes.map(convertRoute);

  const localRoutes = filterDynamicRoutes(dynamicRoutes);

  const accessibleRoutes = [...backendRoutes, ...localRoutes];

  // 用于重新初始化权限时清理旧路由。
  resetAccessRoutes();

  try {
    for (const route of accessibleRoutes) {
      const removeRoute = router.addRoute('Index', route);
      removeRouteCallbacks.push(removeRoute);
    }

    // 注册后才能取得子路由最终的完整路径。
    const accessibleMenus = generateMenus(accessibleRoutes, router);

    return { accessibleRoutes, accessibleMenus };
  } catch (error) {
    // 初始化失败时，清理本次已经注册的路由。
    resetAccessRoutes();
    throw error;
  }
}

export { generateAccess };
