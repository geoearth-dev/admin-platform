import type { ComponentOptions, VNode } from 'vue';
import type {
  RouteLocationNormalizedLoaded,
  RouteLocationNormalizedLoadedGeneric,
} from 'vue-router';

import { computed } from 'vue';
import { usePreferences, preferences } from '@/plugins/preference';

/**
 * 转换组件，自动添加 name
 * @param component
 * @param route
 */
export function transformComponent(component: VNode, route: RouteLocationNormalizedLoadedGeneric) {
  // 组件视图未找到，如果有设置后备视图，则返回后备视图，如果没有，则抛出错误
  if (!component) {
    console.error('Component view not found，please check the route configuration');
    return undefined;
  }

  const routeName = route.name;
  // 如果组件没有 name，则直接返回
  if (typeof routeName !== 'string' || !routeName) {
    return component;
  }
  const componentType = component.type;
  // 只给对象形式的组件补充名称
  if (typeof componentType !== 'object' || componentType === null) {
    return component;
  }
  const options = componentType as ComponentOptions;
  if (!options.name) {
    options.name = routeName;
  }
  return component;
}

/**
 * Layout相关hook
 */
export function useLayoutHook() {
  const { keepAlive } = usePreferences();
  /**
   * 是否使用动画
   */
  const getEnabledTransition = computed(() => {
    const { transition } = preferences;
    const transitionName = transition.name;
    return transitionName && transition.enable;
  });

  /**
   * 获取路由过渡动画
   */
  function getTransitionName(_route: RouteLocationNormalizedLoaded) {
    // 如果偏好设置未设置，则不使用动画
    const { tabbar, transition } = preferences;
    const transitionName = transition.name;
    if (!transitionName || !transition.enable) {
      return;
    }

    // 标签页未启用或者未开启缓存，则使用全局配置动画
    if (!tabbar.enable || !keepAlive) {
      return transitionName;
    }

    // 如果页面已经加载过，则不使用动画
    // if (route.meta.loaded) {
    //   return;
    // }
    // 已经打开且已经加载过的页面不使用动画
    // const inTabs = getCachedTabs.value.includes(route.name as string);

    // return inTabs && route.meta.loaded ? undefined : transitionName;
    return transitionName;
  }

  return {
    getEnabledTransition,
    getTransitionName,
  };
}
