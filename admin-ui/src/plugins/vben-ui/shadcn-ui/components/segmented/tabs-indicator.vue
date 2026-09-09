<script setup lang="ts">
import type { TabsIndicatorProps } from 'reka-ui';

import { computed, type ClassValue } from 'vue';

import { cn } from '@/utils/cn';

import { TabsIndicator, useForwardProps } from 'reka-ui';

const props = defineProps<TabsIndicatorProps & { class?: ClassValue }>();

const delegatedProps = computed(() => {
  const { ...delegated } = props;

  return delegated;
});

const forwardedProps = useForwardProps(delegatedProps);
</script>

<template>
  <TabsIndicator
    v-bind="forwardedProps"
    :class="
      cn(
        'absolute bottom-0 left-0 z-10 size-full translate-x-(--reka-tabs-indicator-position) rounded-full py-1 transition-[width,transform] duration-300',
        props.class,
      )
    "
  >
    <div
      class="bg-background text-foreground inline-flex size-full items-center justify-center rounded-md text-sm font-medium whitespace-nowrap focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:outline-hidden disabled:pointer-events-none disabled:opacity-50"
    >
      <slot></slot>
    </div>
  </TabsIndicator>
</template>
