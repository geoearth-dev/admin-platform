<script setup lang="ts">
import type { EChartsOption } from 'echarts'
import { computed, ref } from 'vue'
import {
  ElDatePicker,
  ElDialog,
  ElSelect,
  ElOption,
  ElTable,
  ElTableColumn,
} from 'element-plus'
import { MapChart } from 'echarts/charts'
import { VisualMapComponent } from 'echarts/components'
import echarts from '@/components/echarts/echarts'
import { useI18n } from '@/plugins/locale'
import { useDashboardTheme } from '../components/use-dashboard-theme'
import {
  Button,
  Card,
  Tabs,
  TabsList,
  TabsTrigger,
  VbenCountToAnimator,
  VbenIcon,
} from '@/plugins/vben-ui/shadcn-ui'
// Local map geometry: https://geo.datav.aliyun.com/areas_v3/bound/100000_full.json
import chinaMap from '../assets/china.json'
import DemoHero from '../components/demo-hero.vue'
import DemoPanel from '../components/demo-panel.vue'
import DemoChart from '../components/demo-chart.vue'
import DemoSparkline from '../components/demo-sparkline.vue'
import '../dashboard-demo.css'

echarts.use([MapChart, VisualMapComponent])
echarts.registerMap(
  'admin-demo-china',
  chinaMap as unknown as Parameters<typeof echarts.registerMap>[1],
)
const { t, locale } = useI18n()
const theme = useDashboardTheme()
const text = (key: string) => t(`dashboardDemo.${key}`)
const number = (value: number) =>
  new Intl.NumberFormat(locale.value).format(value)
const selectedRange = ref('30')
const dateRange = ref<[string, string]>(['2024-04-20', '2024-05-20'])
const customRange = ref(false)
const periodDays = computed(() =>
  Math.max(
    1,
    Math.round(
      (new Date(dateRange.value[1]).getTime() -
        new Date(dateRange.value[0]).getTime()) /
        86400000,
    ),
  ),
)
const factor = computed(() => periodDays.value / 30)
function setRange(days: string) {
  if (days === 'custom') {
    customRange.value = true
    return
  }
  selectedRange.value = days
  const end = new Date('2024-05-20T12:00:00')
  const start = new Date(end)
  start.setDate(start.getDate() - Number(days))
  dateRange.value = [formatDay(start), formatDay(end)]
}
function formatDay(date: Date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
const visits = computed(() => Math.round(124386 * factor.value))
const activeUsers = computed(() => Math.round(8942 * factor.value))
const metricCards = computed(() => [
  {
    key: 'visits',
    value: visits.value,
    delta: '+12.5%',
    comparison: '+13,789',
    tone: theme.value.primary,
    icon: 'lucide:chart-no-axes-column-increasing',
    bad: false,
  },
  {
    key: 'active',
    value: activeUsers.value,
    delta: '+8.3%',
    comparison: '+682',
    tone: theme.value.success,
    icon: 'lucide:users-round',
    bad: false,
  },
  {
    key: 'conversion',
    value: 6.24,
    decimals: 2,
    suffix: '%',
    delta: '-1.2%',
    comparison: '-0.08%',
    tone: '#9653fa',
    icon: 'lucide:filter',
    bad: true,
  },
  {
    key: 'alerts',
    value: 23,
    delta: '+53.3%',
    comparison: '+8',
    tone: theme.value.warning,
    icon: 'lucide:bell-ring',
    bad: true,
  },
  {
    key: 'health',
    value: 99.98,
    decimals: 2,
    suffix: '%',
    delta: '+0.01%',
    comparison: '',
    tone: theme.value.success,
    icon: 'lucide:shield-plus',
    bad: false,
  },
])
const granularity = ref('day')
const moduleMetric = ref('visits')
const regionMetric = ref('visits')
const sourceMetric = ref('visits')
const resourcePeriod = ref('7')
const palette = computed(() => [
  theme.value.primary,
  theme.value.success,
  theme.value.warning,
  '#8b63ef',
  '#a8ceff',
  '#74b7ff',
])
const axisColor = computed(() => theme.value.muted)
const gridColor = computed(() => theme.value.border)
const baseTrend = [
  7200, 10800, 8700, 12300, 10500, 13800, 12200, 15300, 14900, 18686,
]
const chartDates = computed(() =>
  baseTrend.map((_, index) => {
    const date = new Date(`${dateRange.value[0]}T12:00:00`)
    date.setDate(date.getDate() + Math.round((periodDays.value * index) / 9))
    return formatDay(date).slice(5)
  }),
)
const trendOptions = computed<EChartsOption>(() => {
  let current = baseTrend.map((value) => Math.round(value * factor.value))
  let previous = baseTrend.map((value, index) =>
    Math.round(value * (0.68 + index * 0.015) * factor.value),
  )
  let labels = chartDates.value
  if (granularity.value === 'week') {
    const sumGroups = (values: number[]) =>
      [0, 3, 6, 9].map((index) =>
        values.slice(index, index + 3).reduce((sum, value) => sum + value, 0),
      )
    current = sumGroups(current)
    previous = sumGroups(previous)
    labels = labels.filter((_, index) => index % 3 === 0)
  }
  return {
    color: [theme.value.primary, theme.value.primarySoft],
    tooltip: { trigger: 'axis' },
    legend: {
      top: 0,
      icon: 'circle',
      itemWidth: 7,
      itemHeight: 7,
      textStyle: { color: axisColor.value, fontSize: 11 },
    },
    grid: { left: 8, right: 8, top: 37, bottom: 5, containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: labels,
      axisLine: { lineStyle: { color: gridColor.value } },
      axisTick: { show: false },
      axisLabel: { color: axisColor.value, fontSize: 10 },
    },
    yAxis: {
      type: 'value',
      axisLabel: { color: axisColor.value, fontSize: 10 },
      splitLine: { lineStyle: { color: gridColor.value } },
    },
    series: [
      {
        name: text('analytics.current'),
        type: 'line',
        smooth: 0.35,
        symbolSize: 6,
        lineStyle: { width: 2.5 },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: theme.value.primaryArea },
              { offset: 1, color: 'transparent' },
            ],
          },
        },
        data: current,
      },
      {
        name: text('analytics.previous'),
        type: 'line',
        smooth: 0.35,
        symbolSize: 5,
        lineStyle: { type: 'dotted', width: 2 },
        data: previous,
      },
    ],
  }
})
const moduleKeys = [
  'workspace',
  'users',
  'analytics',
  'monitor',
  'audit',
  'messages',
  'settings',
]
const moduleCounts = [32438, 24892, 18760, 11302, 8421, 6538, 4215]
const moduleOptions = computed<EChartsOption>(() => ({
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  grid: { top: 28, bottom: 8, left: 4, right: 4, containLabel: true },
  xAxis: {
    type: 'category',
    data: moduleKeys.map((key) => text(`modules.${key}`)),
    axisLine: { show: false },
    axisTick: { show: false },
    axisLabel: {
      color: axisColor.value,
      fontSize: 9,
      interval: 0,
      width: 44,
      overflow: 'truncate',
    },
  },
  yAxis: { type: 'value', show: false },
  series: [
    {
      type: 'bar',
      name: text(
        moduleMetric.value === 'visits'
          ? 'analytics.visits'
          : 'analytics.userCount',
      ),
      barMaxWidth: 34,
      label: {
        show: true,
        position: 'top',
        fontSize: 10,
        color: axisColor.value,
      },
      itemStyle: {
        borderRadius: [3, 3, 0, 0],
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: theme.value.primarySoft },
            { offset: 1, color: theme.value.primary },
          ],
        },
      },
      data: moduleCounts.map((value) =>
        Math.round(
          value * factor.value * (moduleMetric.value === 'visits' ? 1 : 0.12),
        ),
      ),
    },
  ],
}))
const regions = [
  'east',
  'south',
  'north',
  'central',
  'southwest',
  'northwest',
  'northeast',
]
const regionPercent = computed(() =>
  regionMetric.value === 'visits'
    ? [32.5, 18.7, 16.3, 12.6, 8.4, 6.8, 4.7]
    : [29.4, 20.3, 15.2, 13.1, 9.8, 7.4, 4.8],
)
const provinceGroups: Record<string, number> = {
  上海市: 0,
  江苏省: 0,
  浙江省: 0,
  安徽省: 0,
  福建省: 0,
  江西省: 0,
  山东省: 0,
  台湾省: 0,
  广东省: 1,
  广西壮族自治区: 1,
  海南省: 1,
  香港特别行政区: 1,
  澳门特别行政区: 1,
  北京市: 2,
  天津市: 2,
  河北省: 2,
  山西省: 2,
  内蒙古自治区: 2,
  河南省: 3,
  湖北省: 3,
  湖南省: 3,
  重庆市: 4,
  四川省: 4,
  贵州省: 4,
  云南省: 4,
  西藏自治区: 4,
  陕西省: 5,
  甘肃省: 5,
  青海省: 5,
  宁夏回族自治区: 5,
  新疆维吾尔自治区: 5,
  辽宁省: 6,
  吉林省: 6,
  黑龙江省: 6,
}
const mapOptions = computed<EChartsOption>(() => ({
  tooltip: { show: false },
  visualMap: {
    show: false,
    min: 0,
    max: 33,
    inRange: {
      color: [
        theme.value.primaryFaint,
        theme.value.primarySoft,
        theme.value.primary,
      ],
    },
  },
  series: [
    {
      type: 'map',
      map: 'admin-demo-china',
      roam: false,
      left: '1%',
      right: '1%',
      top: '5%',
      bottom: '3%',
      selectedMode: false,
      label: { show: false },
      itemStyle: {
        borderColor: theme.value.card,
        borderWidth: 0.6,
      },
      emphasis: { disabled: true },
      data: Object.entries(provinceGroups).map(([name, group]) => ({
        name,
        value: regionPercent.value[group] ?? 0,
      })),
    },
  ],
}))
const channels = ['direct', 'search', 'referral', 'social', 'internal', 'other']
const channelShares = [38.2, 24.1, 16.8, 11.6, 6.3, 3]
const sourceTotal = computed(() =>
  sourceMetric.value === 'visits' ? visits.value : activeUsers.value,
)
const sourceOptions = computed<EChartsOption>(() => ({
  color: palette.value,
  title: {
    text: number(sourceTotal.value),
    subtext: text(
      sourceMetric.value === 'visits'
        ? 'analytics.visits'
        : 'analytics.userCount',
    ),
    left: 'center',
    top: '36%',
    itemGap: 2,
    textStyle: {
      color: theme.value.foreground,
      fontSize: 17,
      fontWeight: 600,
    },
    subtextStyle: { fontSize: 10, color: axisColor.value },
  },
  series: [
    {
      type: 'pie',
      radius: ['61%', '88%'],
      center: ['50%', '49%'],
      label: { show: false },
      itemStyle: {
        borderWidth: 1,
        borderColor: theme.value.card,
      },
      data: channels.map((key, index) => ({
        name: text(`channels.${key}`),
        value: Math.round(
          (sourceTotal.value * (channelShares[index] || 0)) / 100,
        ),
      })),
    },
  ],
}))
const alerts = computed(() => [
  {
    key: 'database',
    detail: 'databaseDetail',
    tone: theme.value.destructive,
    time: 12,
    hours: false,
  },
  {
    key: 'api',
    detail: 'apiDetail',
    tone: theme.value.destructive,
    time: 28,
    hours: false,
  },
  {
    key: 'disk',
    detail: 'diskDetail',
    tone: theme.value.warning,
    time: 1,
    hours: true,
  },
  {
    key: 'login',
    detail: 'loginDetail',
    tone: theme.value.warning,
    time: 2,
    hours: true,
  },
  {
    key: 'sync',
    detail: 'syncDetail',
    tone: theme.value.destructive,
    time: 3,
    hours: true,
  },
])
const resources = computed(() => [
  {
    key: 'cpu',
    value: resourcePeriod.value === '7' ? 68 : 62,
    tone: theme.value.primary,
    points: [8, 17, 12, 9, 16, 15, 13, 16],
  },
  {
    key: 'memory',
    value: resourcePeriod.value === '7' ? 52 : 49,
    tone: theme.value.success,
    points: [9, 13, 10, 14, 11, 18, 17, 15],
  },
  {
    key: 'storage',
    value: resourcePeriod.value === '7' ? 36 : 32,
    tone: '#9653fa',
    points: [9, 14, 11, 16, 21, 14, 20, 18],
  },
])
const details = [
  {
    time: '2024-05-20 14:32:18',
    user: 'zhang',
    role: 'manager',
    module: 'analytics',
    page: 'trends',
    ip: '192.168.1.102',
    channel: 'direct',
    duration: '03:12',
  },
  {
    time: '2024-05-20 11:21:06',
    user: 'li',
    role: 'admin',
    module: 'users',
    page: 'users',
    ip: '192.168.1.88',
    channel: 'search',
    duration: '05:36',
  },
  {
    time: '2024-05-20 10:15:33',
    user: 'wang',
    role: 'operator',
    module: 'workspace',
    page: 'todo',
    ip: '192.168.1.56',
    channel: 'internal',
    duration: '01:28',
  },
  {
    time: '2024-05-19 18:44:21',
    user: 'admin',
    role: 'super',
    module: 'monitor',
    page: 'server',
    ip: '192.168.1.23',
    channel: 'direct',
    duration: '12:17',
  },
  {
    time: '2024-05-19 16:32:12',
    user: 'zhao',
    role: 'developer',
    module: 'audit',
    page: 'logs',
    ip: '192.168.1.67',
    channel: 'social',
    duration: '02:45',
  },
]
const dialog = ref(false)
const dialogTitle = ref('')
const dialogContent = ref('')
function showDetail(title: string, content: string) {
  dialogTitle.value = title
  dialogContent.value = content
  dialog.value = true
}
function showRecord(row: (typeof details)[number]) {
  showDetail(
    text('analytics.details'),
    `${row.time}\n${row.user === 'admin' ? 'admin' : row.user ? text(`names.${row.user}`) : ''} · ${row.role ? text(`roles.${row.role}`) : ''}\n${row.module ? text(`modules.${row.module}`) : ''} / ${row.page ? text(`pages.${row.page}`) : ''}\nIP: ${row.ip}\n${row.channel ? text(`channels.${row.channel}`) : ''} · ${row.duration}`,
  )
}
function exportData() {
  const columns = [
    'time',
    'user',
    'role',
    'module',
    'visitedPage',
    'ip',
    'channel',
    'duration',
  ]
  const rows = [
    columns.map((key) => text(`columns.${key}`)),
    ...details.map((row) => [
      row.time,
      row.user === 'admin'
        ? 'admin'
        : row.user
          ? text(`names.${row.user}`)
          : '',
      row.role ? text(`roles.${row.role}`) : '',
      row.module ? text(`modules.${row.module}`) : '',
      row.page ? text(`pages.${row.page}`) : '',
      row.ip,
      row.channel ? text(`channels.${row.channel}`) : '',
      row.duration,
    ]),
  ]
  const csv =
    '\uFEFF' +
    rows
      .map((row) =>
        row.map((value) => `"${value.replaceAll('"', '""')}"`).join(','),
      )
      .join('\r\n')
  const url = URL.createObjectURL(
    new Blob([csv], { type: 'text/csv;charset=utf-8' }),
  )
  const link = document.createElement('a')
  link.href = url
  link.download = 'demo-visits.csv'
  link.click()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}
</script>

<template>
  <div class="dashboard-demo demo-analytics">
    <DemoHero analytics
      ><template #controls
        ><ElDatePicker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          format="YYYY-MM-DD"
          size="small"
          :clearable="false"
          :start-placeholder="text('analytics.dateStart')"
          :end-placeholder="text('analytics.dateEnd')"
          style="width: 224px"
          @change="selectedRange = 'custom'"
        />
        <div class="flex gap-1">
          <Button
            v-for="range in ['7', '30', '90', 'custom']"
            :key="range"
            size="sm"
            :variant="selectedRange === range ? 'default' : 'outline'"
            @click="setRange(range)"
            >{{
              text(
                range === 'custom'
                  ? 'analytics.custom'
                  : `analytics.days${range}`,
              )
            }}</Button
          >
        </div></template
      ></DemoHero
    >
    <div class="demo-metrics">
      <Card
        v-for="(card, index) in metricCards"
        :key="card.key"
        class="demo-metric demo-analytics-metric"
      >
        <span
          class="demo-icon-tile"
          :style="{ '--tone': card.tone }"
          ><VbenIcon :icon="card.icon"
        /></span>
        <div class="demo-metric-copy">
          <div class="demo-metric-label">
            {{ text(`analytics.${card.key}`) }}
          </div>
          <div class="demo-metric-value">
            <VbenCountToAnimator
              :end-val="card.value"
              :decimals="card.decimals ?? 0"
              :suffix="card.suffix ?? ''"
              :duration="1500"
            />
          </div>
          <div class="demo-metric-sub">
            <span :class="card.bad ? 'demo-down' : 'demo-up'"
              >{{ card.delta.startsWith('-') ? '↓' : '↑' }}
              {{ card.delta }}</span
            ><span>{{
              card.comparison
                ? `${text('analytics.vsPrevious')} ${card.comparison}`
                : text('normal')
            }}</span>
          </div>
        </div>
        <DemoSparkline
          :color="
            card.bad ? (index === 2 ? '#9653fa' : theme.destructive) : card.tone
          "
          :points="
            index % 2
              ? [8, 6, 16, 22, 19, 25, 23, 28]
              : [7, 16, 13, 23, 17, 24, 21, 29]
          "
        />
      </Card>
    </div>
    <div class="demo-analytics-top">
      <DemoPanel :title="text('analytics.trend')"
        ><template #action
          ><ElSelect
            size="small"
            v-model="granularity"
            class="demo-select"
            :aria-label="text('analytics.trend')"
          >
            <ElOption
              value="day"
              :label="text('analytics.byDay')"
            />
            <ElOption
              value="week"
              :label="text('analytics.byWeek')"
            /> </ElSelect></template
        ><DemoChart
          :options="trendOptions"
          :label="text('analytics.trend')"
      /></DemoPanel>
      <DemoPanel :title="text('analytics.distribution')"
        ><template #action
          ><Tabs v-model="moduleMetric"
            ><TabsList class="h-8"
              ><TabsTrigger
                v-for="metric in ['visits', 'users']"
                :key="metric"
                :value="metric"
                class="text-xs"
                >{{
                  text(
                    metric === 'visits'
                      ? 'columns.visits'
                      : 'analytics.userCount',
                  )
                }}</TabsTrigger
              ></TabsList
            ></Tabs
          ></template
        ><DemoChart
          :options="moduleOptions"
          :label="text('analytics.distribution')"
      /></DemoPanel>
      <DemoPanel :title="text('analytics.regions')"
        ><template #action
          ><Tabs v-model="regionMetric"
            ><TabsList class="h-8"
              ><TabsTrigger
                v-for="metric in ['visits', 'users']"
                :key="metric"
                :value="metric"
                class="text-xs"
                >{{
                  text(
                    metric === 'visits'
                      ? 'columns.visits'
                      : 'analytics.userCount',
                  )
                }}</TabsTrigger
              ></TabsList
            ></Tabs
          ></template
        >
        <div class="demo-map-layout">
          <DemoChart
            :options="mapOptions"
            :label="text('analytics.regions')"
            height="245px"
          />
          <div class="demo-region-list">
            <div
              v-for="(region, index) in regions"
              :key="region"
              class="demo-region-row"
            >
              <i
                class="demo-legend-swatch"
                :style="{ background: `rgba(35,129,255,${1 - index * 0.1})` }"
              /><span>{{ text(`regions.${region}`) }}</span
              ><b>{{ regionPercent[index] }}%</b>
            </div>
          </div>
        </div></DemoPanel
      >
    </div>
    <div class="demo-analytics-mid">
      <DemoPanel :title="text('analytics.sources')"
        ><template #action
          ><ElSelect
            size="small"
            v-model="sourceMetric"
            class="demo-select"
            :aria-label="text('analytics.sources')"
          >
            <ElOption
              value="visits"
              :label="text('columns.visits')"
            />
            <ElOption
              value="users"
              :label="text('analytics.userCount')"
            /> </ElSelect
        ></template>
        <div class="demo-source-layout">
          <DemoChart
            :options="sourceOptions"
            :label="text('analytics.sources')"
            height="184px"
          />
          <div class="demo-source-list">
            <div
              v-for="(channel, index) in channels"
              :key="channel"
              class="demo-source-row"
            >
              <i
                class="demo-legend-swatch"
                :style="{ background: palette[index] }"
              /><span>{{ text(`channels.${channel}`) }}</span
              ><b>{{ channelShares[index] }}%</b
              ><span>{{
                number(
                  Math.round((sourceTotal * (channelShares[index] || 0)) / 100),
                )
              }}</span>
            </div>
          </div>
        </div></DemoPanel
      >
      <DemoPanel :title="text('analytics.topPages')"
        ><div class="demo-table-wrap">
          <ElTable
            :data="moduleKeys.slice(0, 5).map((key, index) => ({ key, index }))"
            size="small"
            show-overflow-tooltip
          >
            <ElTableColumn
              label="#"
              width="35"
              ><template #default="{ row }"
                ><span
                  class="demo-rank-number"
                  :class="{ top: row.index < 3 }"
                  >{{ row.index + 1 }}</span
                ></template
              ></ElTableColumn
            >
            <ElTableColumn
              :label="text('columns.page')"
              min-width="80"
              ><template #default="{ row }">{{
                row.key ? text(`modules.${row.key}`) : ''
              }}</template></ElTableColumn
            >
            <ElTableColumn
              :label="text('columns.visits')"
              min-width="75"
              ><template #default="{ row }">{{
                number(Math.round((moduleCounts[row.index] || 0) * factor))
              }}</template></ElTableColumn
            >
            <ElTableColumn
              :label="text('columns.change')"
              min-width="85"
              ><template #default="{ row }"
                ><span :class="row.index === 3 ? 'demo-down' : 'demo-up'">{{
                  row.index === 3
                    ? '↓ -3.2%'
                    : `↑ +${[12.5, 8.7, 6.1, 0, 4.8][row.index]}%`
                }}</span></template
              ></ElTableColumn
            ></ElTable
          >
        </div></DemoPanel
      >
      <DemoPanel :title="text('analytics.anomalies')"
        ><template #action
          ><Button
            variant="link"
            size="sm"
            class="demo-link"
            @click="
              showDetail(
                text('analytics.anomalies'),
                alerts.map((alert) => text(`alerts.${alert.key}`)).join('\n'),
              )
            "
          >
            {{ text('viewAll')
            }}<VbenIcon icon="lucide:chevron-right" /></Button
        ></template>
        <div
          v-for="alert in alerts"
          :key="alert.key"
          class="demo-alert-row"
        >
          <span
            class="demo-mini-icon"
            :style="{ '--tone': alert.tone }"
            ><VbenIcon icon="lucide:bell-ring"
          /></span>
          <div class="demo-alert-copy">
            <strong>{{ text(`alerts.${alert.key}`) }}</strong>
            <p>{{ text(`alerts.${alert.detail}`) }}</p>
          </div>
          <time class="demo-alert-time">{{
            t(`dashboardDemo.${alert.hours ? 'agoHours' : 'agoMinutes'}`, {
              count: alert.time,
            })
          }}</time>
        </div></DemoPanel
      >
      <DemoPanel :title="text('analytics.resources')"
        ><template #action
          ><ElSelect
            size="small"
            v-model="resourcePeriod"
            class="demo-select"
            :aria-label="text('analytics.resources')"
          >
            <ElOption
              value="7"
              :label="text('analytics.days7')"
            />
            <ElOption
              value="30"
              :label="text('analytics.days30')"
            /> </ElSelect
        ></template>
        <div class="demo-resource-grid">
          <div
            v-for="resource in resources"
            :key="resource.key"
          >
            <div
              class="demo-resource-ring"
              :style="{ '--tone': resource.tone, '--value': resource.value }"
              role="img"
              :aria-label="`${text(`analytics.${resource.key}`)} ${resource.value}%`"
            >
              <div class="demo-resource-center">
                <strong>{{ resource.value }}%</strong
                ><small>{{ text(`analytics.${resource.key}`) }}</small>
              </div>
            </div>
            <DemoSparkline
              :color="resource.tone"
              :points="resource.points"
            />
          </div></div
      ></DemoPanel>
    </div>
    <DemoPanel :title="text('analytics.details')"
      ><template #action
        ><Button
          variant="outline"
          size="sm"
          class="demo-button"
          @click="exportData"
        >
          <VbenIcon
            icon="lucide:download"
            style="color: var(--demo-blue)"
          />{{ text('analytics.export') }}
        </Button></template
      >
      <div class="demo-table-wrap">
        <ElTable
          :data="details"
          size="small"
          show-overflow-tooltip
        >
          <ElTableColumn
            type="index"
            label="#"
            width="40"
          />
          <ElTableColumn
            prop="time"
            :label="text('columns.time')"
            min-width="155"
          />
          <ElTableColumn
            :label="text('columns.user')"
            min-width="100"
            ><template #default="{ row }">{{
              row.user === 'admin'
                ? 'admin'
                : row.user
                  ? text(`names.${row.user}`)
                  : ''
            }}</template></ElTableColumn
          >
          <ElTableColumn
            :label="text('columns.role')"
            min-width="115"
            ><template #default="{ row }">{{
              row.role ? text(`roles.${row.role}`) : ''
            }}</template></ElTableColumn
          >
          <ElTableColumn
            :label="text('columns.module')"
            min-width="110"
            ><template #default="{ row }">{{
              row.module ? text(`modules.${row.module}`) : ''
            }}</template></ElTableColumn
          >
          <ElTableColumn
            :label="text('columns.visitedPage')"
            min-width="125"
            ><template #default="{ row }">{{
              row.page ? text(`pages.${row.page}`) : ''
            }}</template></ElTableColumn
          >
          <ElTableColumn
            prop="ip"
            :label="text('columns.ip')"
            min-width="120"
          />
          <ElTableColumn
            :label="text('columns.channel')"
            min-width="100"
            ><template #default="{ row }">{{
              row.channel ? text(`channels.${row.channel}`) : ''
            }}</template></ElTableColumn
          >
          <ElTableColumn
            prop="duration"
            :label="text('columns.duration')"
            min-width="80"
          />
          <ElTableColumn
            :label="text('columns.actions')"
            width="80"
            fixed="right"
            ><template #default="{ $index }"
              ><Button
                variant="link"
                size="sm"
                @click="showRecord(details[$index]!)"
                >{{ text('analytics.view') }}</Button
              ></template
            ></ElTableColumn
          ></ElTable
        >
      </div>
      <div class="demo-table-footer">
        <span>{{
          t('dashboardDemo.analytics.records', { count: details.length })
        }}</span
        ><span>{{ text('sample') }}</span>
      </div></DemoPanel
    >
    <ElDialog
      v-model="dialog"
      :title="dialogTitle"
      width="min(560px, 92vw)"
      ><div class="demo-dialog-content">{{ dialogContent }}</div>
      <p class="mt-4 text-xs text-muted-foreground">
        {{ text('dialog.body') }}
      </p></ElDialog
    >
    <ElDialog
      v-model="customRange"
      :title="text('analytics.custom')"
      width="min(480px, 92vw)"
      ><ElDatePicker
        v-model="dateRange"
        type="daterange"
        value-format="YYYY-MM-DD"
        :clearable="false"
        :start-placeholder="text('analytics.dateStart')"
        :end-placeholder="text('analytics.dateEnd')"
        style="max-width: 100%"
        @change="selectedRange = 'custom'"
      />
      <p class="mt-4 text-xs text-muted-foreground">
        {{ text('dialog.body') }}
      </p></ElDialog
    >
  </div>
</template>
