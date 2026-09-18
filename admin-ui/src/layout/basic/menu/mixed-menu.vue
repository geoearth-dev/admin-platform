<script lang="ts" setup>
import type { NormalMenuProps } from '@/plugins/vben-ui/menu-ui';
import type { NavigationMenu } from '@/types';
import { findMenuByPath } from '@/utils/find-menu-by-path';

import { onBeforeMount } from 'vue';
import { useRoute } from 'vue-router';

const props = defineProps<NormalMenuProps>();

const emit = defineEmits<{
  defaultSelect: [NavigationMenu, NavigationMenu?];
  enter: [NavigationMenu];
  select: [NavigationMenu];
}>();

const route = useRoute();

onBeforeMount(() => {
  const menu = findMenuByPath(props.menus || [], route.path);
  if (menu) {
    const rootMenu = (props.menus || []).find((item) => item.path === menu.parents?.[0]);
    emit('defaultSelect', menu, rootMenu);
  }
});
</script>

<template>
  <NormalMenu
    :active-path="activePath"
    :collapse="collapse"
    :menus="menus"
    :rounded="rounded"
    :theme="theme"
    @enter="(menu: NavigationMenu) => emit('enter', menu)"
    @select="(menu: NavigationMenu) => emit('select', menu)"
  />
</template>
