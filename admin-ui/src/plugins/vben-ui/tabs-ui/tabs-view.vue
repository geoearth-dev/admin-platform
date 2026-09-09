<script setup lang="ts">
import type { TabsEmits, TabsProps } from './types';

import { ChevronsLeft, ChevronsRight } from '@/assets/icons';

import { Tabs, TabsChrome } from './components';
import { useTabsDrag } from './use-tabs-drag';
import { useTabsViewScroll } from './use-tabs-view-scroll';
import { useForwardPropsEmits } from 'reka-ui';
import { VbenScrollbar } from '@/plugins/vben-ui/shadcn-ui';
defineOptions({
  name: 'TabsView',
});

const props = withDefaults(defineProps<TabsProps>(), {
  contentClass: 'vben-tabs-content',
  draggable: true,
  styleType: 'chrome',
  wheelable: true,
});

const emit = defineEmits<TabsEmits>();

const forward = useForwardPropsEmits(props, emit);

const {
  handleScrollAt,
  handleWheel,
  scrollDirection,
  scrollIsAtLeft,
  scrollIsAtRight,
  showScrollButton,
} = useTabsViewScroll(props);

function onWheel(e: WheelEvent) {
  if (props.wheelable) {
    handleWheel(e);
    e.stopPropagation();
    e.preventDefault();
  }
}

useTabsDrag(props, emit);
</script>

<template>
  <div class="flex h-full flex-1 overflow-hidden">
    <!-- 左侧滚动按钮 -->
    <span
      v-show="showScrollButton"
      :class="{
        'cursor-pointer text-muted-foreground hover:bg-muted': !scrollIsAtLeft,
        'pointer-events-none opacity-30': scrollIsAtLeft,
      }"
      class="border-r px-2"
      @click="scrollDirection('left')"
    >
      <ChevronsLeft class="size-4 h-full" />
    </span>

    <div
      :class="{
        'pt-0.75': styleType === 'chrome',
      }"
      class="size-full flex-1 overflow-hidden"
    >
      <VbenScrollbar
        ref="scrollbarRef"
        :shadow-bottom="false"
        :shadow-top="false"
        class="h-full"
        horizontal
        scroll-bar-class="z-10 hidden "
        shadow
        shadow-left
        shadow-right
        @scroll-at="handleScrollAt"
        @wheel="onWheel"
      >
        <TabsChrome v-if="styleType === 'chrome'" v-bind="{ ...forward, ...$attrs, ...$props }" />

        <Tabs v-else v-bind="{ ...forward, ...$attrs, ...$props }" />
      </VbenScrollbar>
    </div>

    <!-- 右侧滚动按钮 -->
    <span
      v-show="showScrollButton"
      :class="{
        'cursor-pointer text-muted-foreground hover:bg-muted': !scrollIsAtRight,
        'pointer-events-none opacity-30': scrollIsAtRight,
      }"
      class="cursor-pointer border-l px-2 text-muted-foreground hover:bg-muted"
      @click="scrollDirection('right')"
    >
      <ChevronsRight class="size-4 h-full" />
    </span>
  </div>
</template>
