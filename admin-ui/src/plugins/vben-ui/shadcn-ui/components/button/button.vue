<script setup lang="ts">
import type { VbenButtonProps } from './button';

import { computed } from 'vue';

import { LoaderCircle } from '@/assets/icons';
import { cn } from '@/utils/cn';

import { Primitive } from 'reka-ui';

import { buttonVariants } from '../../ui';

const props = withDefaults(defineProps<VbenButtonProps>(), {
  as: 'button',
  class: '',
  disabled: false,
  loading: false,
  size: 'default',
  variant: 'default',
});

const isDisabled = computed(() => {
  return props.disabled || props.loading;
});
</script>

<template>
  <Primitive
    :as="as"
    :as-child="asChild"
    :class="cn(buttonVariants({ variant, size }), props.class)"
    :disabled="isDisabled"
  >
    <LoaderCircle v-if="loading" class="text-md mr-2 size-4 shrink-0 animate-spin" />
    <slot></slot>
  </Primitive>
</template>
