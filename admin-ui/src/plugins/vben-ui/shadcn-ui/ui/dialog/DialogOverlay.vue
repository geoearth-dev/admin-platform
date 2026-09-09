<script setup lang="ts">
import { inject } from 'vue';

import { cn } from '@/utils/cn.ts';
import { useScrollLock } from '@/plugins/composables';
import type { ClassType } from '@/types';

const props = withDefaults(
  defineProps<{
    class?: ClassType;
    overlayBlur?: number;
    position?: 'absolute' | 'fixed';
    zIndex?: number;
  }>(),
  {
    position: 'fixed',
  },
);

useScrollLock();
const dismissableModalId = inject('DISMISSABLE_MODAL_ID', undefined);
</script>

<template>
  <div
    :data-dismissable-modal="dismissableModalId"
    :style="{
      ...(zIndex ? { zIndex } : {}),
      position,
      backdropFilter: overlayBlur && overlayBlur > 0 ? `blur(${overlayBlur}px)` : 'none',
    }"
    :class="cn('z-popup bg-overlay inset-0 fixed', props.class)"
  ></div>
</template>
