<script setup lang="ts">
import { cn } from '@/utils/cn';
import type { HTMLAttributes } from 'vue';

const props = defineProps<{ class?: HTMLAttributes['class'] }>();
</script>

<template>
  <div data-slot="sheet-footer" :class="cn('flex flex-row justify-end gap-x-2', props.class)">
    <slot></slot>
  </div>
</template>
