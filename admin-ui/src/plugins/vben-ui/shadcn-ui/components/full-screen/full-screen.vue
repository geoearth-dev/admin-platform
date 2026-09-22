<script lang="ts" setup>
import { Maximize, Minimize } from '@/assets/icons';

import { useFullscreen } from '@vueuse/core';

import { VbenIconButton } from '../button';

defineOptions({ name: 'FullScreen' });

withDefaults(
  defineProps<{
    tooltip?: string;
  }>(),
  {
    tooltip: '',
  },
);

const { isFullscreen, toggle } = useFullscreen();
</script>
<template>
  <VbenIconButton
    :tooltip="tooltip || undefined"
    class="hover:animate-[shrink_0.3s_ease-in-out]"
    @click="toggle"
  >
    <Minimize v-if="isFullscreen" class="text-foreground size-4" />
    <Maximize v-else class="text-foreground size-4" />
  </VbenIconButton>
</template>
