<template>
  <div
    class="app-container cache-page cache-monitor"
    v-loading="loading"
    element-loading-text="正在加载缓存监控数据，请稍候！"
  >
    <div class="cache-toolbar">
      <el-button
        :icon="Refresh"
        :loading="loading"
        @click="getList"
        >刷新</el-button
      >
    </div>
    <el-row
      v-if="cache"
      :gutter="16"
    >
      <el-col
        :span="24"
        class="cache-card"
      >
        <el-card shadow="never">
          <template #header>
            <div class="cache-card-title">
              <Monitor class="cache-card-icon" /><span>基本信息</span>
            </div>
          </template>
          <el-descriptions
            :column="descriptionColumns"
            border
          >
            <el-descriptions-item
              v-for="item in basicInfo"
              :key="item.label"
              :label="item.label"
            >
              {{ item.value }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col
        :xs="24"
        :lg="12"
        class="cache-card"
      >
        <el-card shadow="never">
          <template #header>
            <div class="cache-card-title">
              <PieChart class="cache-card-icon" /><span>命令统计</span>
            </div>
          </template>
          <EchartsUI
            ref="commandChartRef"
            height="360px"
          />
        </el-card>
      </el-col>
      <el-col
        :xs="24"
        :lg="12"
        class="cache-card"
      >
        <el-card shadow="never">
          <template #header>
            <div class="cache-card-title">
              <Odometer class="cache-card-icon" /><span>内存信息</span>
            </div>
          </template>
          <EchartsUI
            ref="memoryChartRef"
            height="320px"
          />
          <div class="memory-caption">
            <span
              >历史峰值：{{ cache.info.used_memory_peak_human ?? '—' }}</span
            >
            <span>{{ memoryCaption }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty
      v-else-if="!loading"
      description="缓存监控数据加载失败"
    >
      <el-button
        type="primary"
        @click="getList"
        >重新加载</el-button
      >
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import type { CacheMetrics } from '@/types/base/api/monitor/cache'
import type { EchartsUIType } from '@/components/echarts'
import { getCache } from '@/api/monitor/cache'
import { EchartsUI, useEcharts } from '@/components/echarts'
import echarts from '@/components/echarts/echarts'
import { Monitor, Odometer, PieChart, Refresh } from '@element-plus/icons-vue'
import {
  ElDescriptions,
  ElDescriptionsItem,
  ElEmpty,
  vLoading,
} from 'element-plus'
import { GaugeChart } from 'echarts/charts'
import { useWindowSize } from '@vueuse/core'
import { computed, onMounted, ref, watch } from 'vue'

defineOptions({ name: 'Cache' })
echarts.use([GaugeChart])

const cache = ref<CacheMetrics | null>(null)
const loading = ref(true)
const commandChartRef = ref<EchartsUIType>()
const memoryChartRef = ref<EchartsUIType>()
const { renderEcharts: renderCommands, isActive } = useEcharts(commandChartRef)
const { renderEcharts: renderMemory } = useEcharts(memoryChartRef)
const { width } = useWindowSize()
const descriptionColumns = computed(() =>
  width.value < 768 ? 1 : width.value < 1280 ? 2 : 4,
)

const basicInfo = computed(() => {
  if (!cache.value) return []
  const { info, dbSize } = cache.value
  const modes: Record<string, string> = {
    standalone: '单机',
    cluster: '集群',
    sentinel: '哨兵',
  }
  const cpuTime =
    info.used_cpu_user !== undefined && info.used_cpu_sys !== undefined
      ? (Number(info.used_cpu_user) + Number(info.used_cpu_sys)).toFixed(2) +
        ' 秒'
      : '—'
  return [
    { label: 'Redis 版本', value: info.redis_version ?? '—' },
    {
      label: '运行模式',
      value: modes[info.redis_mode ?? ''] ?? info.redis_mode ?? '—',
    },
    { label: '端口', value: info.tcp_port ?? '—' },
    { label: '客户端数', value: info.connected_clients ?? '—' },
    { label: '运行时间（天）', value: info.uptime_in_days ?? '—' },
    { label: '使用内存', value: info.used_memory_human ?? '—' },
    { label: 'CPU 累计耗时', value: cpuTime },
    {
      label: '内存上限',
      value: info.maxmemory === '0' ? '未限制' : (info.maxmemory_human ?? '—'),
    },
    {
      label: 'AOF 持久化',
      value:
        info.aof_enabled === '1'
          ? '开启'
          : info.aof_enabled === '0'
            ? '关闭'
            : '—',
    },
    {
      label: '最近 RDB 保存',
      value:
        info.rdb_last_bgsave_status === 'ok'
          ? '成功'
          : info.rdb_last_bgsave_status === 'err'
            ? '失败'
            : (info.rdb_last_bgsave_status ?? '—'),
    },
    { label: '当前数据库键数', value: dbSize },
    {
      label: '网络入 / 出',
      value: `${info.instantaneous_input_kbps ?? '—'} / ${info.instantaneous_output_kbps ?? '—'} KB/s`,
    },
  ]
})

const memoryCaption = computed(() => {
  const info = cache.value?.info
  if (!info || info.maxmemory === undefined) return '暂未获取内存上限'
  return Number(info.maxmemory) > 0
    ? `配置上限：${info.maxmemory_human}`
    : '未设置内存上限，仪表盘按历史峰值展示'
})

function renderCharts() {
  if (!cache.value || !isActive.value) return
  const { info, commandStats } = cache.value
  const commands = commandStats
    .filter((item) => item.calls > 0)
    .map((item) => ({ name: item.name, value: item.calls }))
    .sort((a, b) => b.value - a.value)
  void renderCommands({
    title: commands.length
      ? []
      : {
          text: '暂无命令统计',
          left: 'center',
          top: 'center',
          textStyle: { fontSize: 14, fontWeight: 'normal' },
        },
    tooltip: { trigger: 'item', renderMode: 'richText' },
    legend: { type: 'scroll', bottom: 0 },
    series: [
      {
        name: '命令调用次数',
        type: 'pie',
        radius: ['38%', '66%'],
        center: ['50%', '45%'],
        label: { show: false },
        emphasis: { label: { show: true, formatter: '{b}\n{d}%' } },
        data: commands,
      },
    ],
  })

  const bytesPerMb = 1024 * 1024
  const used = Number(info.used_memory ?? 0)
  const limit = Number(info.maxmemory ?? 0)
  const peak = Number(info.used_memory_peak ?? used)
  const scale = Math.max(limit > 0 ? limit : peak, used, bytesPerMb)
  void renderMemory({
    title:
      info.used_memory === undefined
        ? {
            text: '暂无内存信息',
            left: 'center',
            top: 'center',
            textStyle: { fontSize: 14, fontWeight: 'normal' },
          }
        : [],
    tooltip: { trigger: 'item', renderMode: 'richText' },
    series:
      info.used_memory === undefined
        ? []
        : [
            {
              name: '已用内存（MB）',
              type: 'gauge',
              min: 0,
              max: Math.ceil(scale / bytesPerMb),
              splitNumber: 5,
              radius: '85%',
              progress: { show: true, width: 12 },
              axisLine: { lineStyle: { width: 12 } },
              axisTick: { show: false },
              splitLine: { length: 10, lineStyle: { width: 1 } },
              axisLabel: {
                distance: 18,
                formatter: (value: number) =>
                  String(Math.round(value * 10) / 10),
              },
              title: { offsetCenter: [0, '70%'], fontSize: 14 },
              detail: {
                formatter: () =>
                  info.used_memory_human ??
                  `${(used / bytesPerMb).toFixed(2)} MB`,
                fontSize: 24,
                offsetCenter: [0, '35%'],
                valueAnimation: true,
              },
              data: [{ value: used / bytesPerMb, name: '当前内存占用' }],
            },
          ],
  })
}

async function getList() {
  loading.value = true
  try {
    cache.value = await getCache()
  } catch {
    cache.value = null
  } finally {
    loading.value = false
  }
}

watch([cache, isActive], renderCharts, { flush: 'post' })
onMounted(getList)
</script>

<style scoped src="./cache.css"></style>
<style scoped>
.memory-caption {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px 20px;
  min-height: 40px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

:deep(.el-descriptions__content) {
  overflow-wrap: anywhere;
  font-variant-numeric: tabular-nums;
}
</style>
