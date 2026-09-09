<script setup lang="ts">
import type { MenuProps } from './types';

import { Menu } from './components';
import SubMenu from './sub-menu.vue';
import type { MenuRecordRaw } from '@/types/index.ts';
import { useForwardProps } from 'reka-ui';

interface Props extends MenuProps {
  menus: MenuRecordRaw[];
}

defineOptions({
  name: 'MenuView',
});

const props = withDefaults(defineProps<Props>(), {
  collapse: false,
});

const forward = useForwardProps(props);
</script>

<template>
  <Menu v-bind="forward">
    <template v-for="menu in menus" :key="menu.path">
      <SubMenu :menu="menu" />
    </template>
  </Menu>
</template>
