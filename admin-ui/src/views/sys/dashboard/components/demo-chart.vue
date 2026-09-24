<script setup lang="ts">
import type { EChartsOption } from 'echarts'
import type { EchartsUIType } from '@/components/echarts'
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { EchartsUI, useEcharts } from '@/components/echarts'
import { useDashboardTheme } from './use-dashboard-theme'

const props = withDefaults(
  defineProps<{ options: EChartsOption; height?: string; label: string }>(),
  { height: '245px' },
)
const theme = useDashboardTheme()
const chart = ref<EchartsUIType>()
const { renderEcharts } = useEcharts(chart)
const themed = computed<EChartsOption>(() => ({
  ...props.options,
  backgroundColor: 'transparent',
  textStyle: {
    color: theme.value.muted,
    fontFamily: 'inherit',
    fontSize: 11,
  },
  tooltip: {
    trigger: 'item',
    ...props.options.tooltip,
    confine: true,
    renderMode: 'richText',
    backgroundColor: theme.value.popover,
    borderColor: theme.value.border,
    textStyle: { color: theme.value.foreground, fontSize: 12 },
  },
}))
async function draw() {
  await nextTick()
  await renderEcharts(themed.value)
}
onMounted(draw)
watch(themed, draw, { flush: 'post' })
</script>
<template>
  <EchartsUI
    ref="chart"
    :height="height"
    :aria-label="label"
    role="img"
  />
</template>
