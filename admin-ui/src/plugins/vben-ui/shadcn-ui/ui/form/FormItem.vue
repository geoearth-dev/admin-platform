<script lang="ts" setup>
import type { HTMLAttributes } from 'vue';

import { provide } from 'vue';


import { useId } from 'reka-ui';

import { FORM_ITEM_INJECTION_KEY } from './injectionKeys';
import { cn } from '@/utils/cn';

const props = defineProps<{
  class?: HTMLAttributes['class'];
}>();

const id = useId();
provide(FORM_ITEM_INJECTION_KEY, id);
</script>

<template>
  <div data-slot="form-item" :class="cn(props.class)">
    <slot></slot>
  </div>
</template>
