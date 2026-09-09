<script setup lang="ts">
import { computed, unref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { transformComponent, useLayoutHook } from '../hooks';
import { preferences } from '@/plugins/preference';
import { useTabbarStore, getTabKey } from '@/store';
import { storeToRefs } from 'pinia';

const route = useRoute();

const tabbarStore = useTabbarStore();

const { getTabs, getCachedRoutes, getExcludeCachedTabs } = storeToRefs(tabbarStore);
const { removeCachedRoute } = tabbarStore;

const { getEnabledTransition, getTransitionName } = useLayoutHook();

/**
 * 是否启用tab
 */
const enableTabbar = computed(() => preferences.tabbar.enable);

const computedCachedRouteKeys = computed(() => {
  if (!unref(enableTabbar)) {
    return [];
  }
  return unref(getTabs)
    .filter((item) => item.meta.domCached)
    .map((item) => getTabKey(item));
});

/**
 * 监听缓存路由变化，删除不存在的缓存路由
 */
watch(computedCachedRouteKeys, (keys) => {
  unref(getCachedRoutes).forEach((item) => {
    if (!keys.includes(item.key)) {
      removeCachedRoute(item.key);
    }
  });
});

/**
 * 所有缓存的route
 */
const computedCachedRoutes = computed(() => {
  if (!unref(enableTabbar)) {
    return [];
  }

  // 刷新期间，临时排除对应路由，让页面卸载后重新创建。
  const excludedRouteNames = unref(getExcludeCachedTabs);

  return [...unref(getCachedRoutes).values()].filter((item) => {
    const routeName = item.route.name;

    // Vue Router 的 name 可能是 string、symbol 或 undefined。
    // 当前排除列表只保存字符串名称。
    return typeof routeName !== 'string' || !excludedRouteNames.includes(routeName);
  });
});

/**
 * 是否显示
 */
const computedShowView = computed(() => unref(computedCachedRoutes).length > 0);

const computedCurrentRouteKey = computed(() => {
  return getTabKey(route);
});
</script>

<template>
  <template v-if="computedShowView">
    <template v-for="item in computedCachedRoutes" :key="item.key">
      <Transition
        v-if="getEnabledTransition"
        appear
        mode="out-in"
        :name="getTransitionName(item.route)"
      >
        <component
          v-show="item.key === computedCurrentRouteKey"
          :is="transformComponent(item.component, item.route)"
        />
      </Transition>
      <template v-else>
        <component
          v-show="item.key === computedCurrentRouteKey"
          :is="transformComponent(item.component, item.route)"
        />
      </template>
    </template>
  </template>
</template>

<style scoped></style>
