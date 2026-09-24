<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(
  defineProps<{ color?: string; points?: number[] }>(),
  { color: '#2483ff', points: () => [8, 12, 10, 19, 15, 24, 21, 28] },
)
const path = computed(() => {
  const min = Math.min(...props.points),
    range = Math.max(...props.points) - min || 1
  return props.points
    .map(
      (point, index) =>
        `${index ? 'L' : 'M'}${(index * 100) / Math.max(1, props.points.length - 1)},${36 - ((point - min) / range) * 29}`,
    )
    .join(' ')
})
</script>
<template>
  <svg
    class="demo-sparkline"
    viewBox="0 0 100 42"
    aria-hidden="true"
  >
    <path
      :d="`${path} L100,42 L0,42 Z`"
      :fill="color"
      fill-opacity=".07"
    />
    <path
      :d="path"
      :stroke="color"
      stroke-width="2"
      fill="none"
      stroke-linejoin="round"
      stroke-linecap="round"
    />
  </svg>
</template>
