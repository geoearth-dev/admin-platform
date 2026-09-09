import type { Router } from 'vue-router';
import { LOGIN_PATH } from '@/constants/core';
import { $t } from '@/plugins/locale';
import { preferences } from '@/plugins/preference';
import { useAccessStore, useAuthStore } from '@/store';
import { startProgress, stopProgress } from '@/utils/nprogress';
import { useTitle } from '@vueuse/core';
import { generateAccess } from './access';

// import { traverseTreeValues } from '@/utils/tree';
/**
 * 通用守卫配置
 * @param router
 */
function setupCommonGuard(router: Router) {
  // 记录已经加载的页面
  const loadedPaths = new Set<string>();

  router.beforeEach((to) => {
    to.meta.loaded = loadedPaths.has(to.path);

    // 页面加载进度条
    if (!to.meta.loaded && true) {
      startProgress();
    }
    return true;
  });

  router.afterEach((to) => {
    // 记录页面是否加载,如果已经加载，后续的页面切换动画等效果不在重复执行

    loadedPaths.add(to.path);

    // 关闭页面加载进度条
    stopProgress();
  });
}

/**
 * 权限访问守卫配置
 * @param router
 */
function setupAccessGuard(router: Router) {
  router.beforeEach(async (to) => {
    // 动态更新标题
    if (preferences.app.dynamicTitle) {
      const routeTitle = to.meta.title as string;
      const pageTitle = (routeTitle ? `${$t(routeTitle)} - ` : '') + preferences.app.name;
      useTitle(pageTitle);
    }

    const accessStore = useAccessStore();
    const authStore = useAuthStore();

    const isLoginRoute = to.path === LOGIN_PATH;
    const isPublicRoute = Boolean(to.meta.ignoreAccess);
    /**
     * 第一次进入系统时，尝试通过 Refresh Cookie 恢复会话。
     * 登录页和公共页面无需探测登录状态。
     */
    if (!authStore.sessionInitialized && !isLoginRoute && !isPublicRoute) {
      await authStore.restoreSession();
    }

    /**
     * 没有登录。
     */
    if (!authStore.isAuthenticated) {
      if (isLoginRoute || isPublicRoute) {
        return true;
      }
      return {
        path: LOGIN_PATH,
        query: to.path === '/' ? {} : { redirect: to.fullPath },
        replace: true,
      };
    }
    // 仅标记这一次导航是否完成了动态路由初始化。
    let accessInitializedNow = false;
    /**
     * 已登录时，先生成动态路由。
     *
     */
    if (!accessStore.isAccessChecked) {
      // 生成菜单和路由
      const { accessibleMenus, accessibleRoutes } = await generateAccess();
      // 保存菜单信息和路由信息
      accessStore.setAccessMenus(accessibleMenus);
      accessStore.setAccessRoutes(accessibleRoutes);
      accessStore.setIsAccessChecked(true);
      accessInitializedNow = true;
    }

    /**
     * 已登录用户访问登录页或者根路径，跳转目标首页。
     *
     */
    if (isLoginRoute || to.path === '/') {
      const redirect = to.query.redirect;
      return {
        path: typeof redirect === 'string' ? redirect : preferences.app.defaultHomePath,
        replace: true,
      };
    }
    /**
     * 当前目标可能是刚注册的动态路由，需要重新解析一次。
     */
    if (accessInitializedNow) {
      return {
        ...router.resolve(to.fullPath),
        replace: true,
      };
    }
  });
}

function setupRouterGuard(router: Router) {
  /** 通用 */
  setupCommonGuard(router);
  /** 权限访问 */
  setupAccessGuard(router);
}

export { setupRouterGuard };
