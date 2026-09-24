<script setup lang="ts">
import { computed, ref } from 'vue'
import {
  ElDialog,
  ElInput,
  ElCheckbox,
  ElSelect,
  ElOption,
  ElTable,
  ElTableColumn,
} from 'element-plus'
import { useI18n } from '@/plugins/locale'
import {
  Button,
  Card,
  Tabs,
  TabsList,
  TabsTrigger,
  VbenCountToAnimator,
  VbenIcon,
} from '@/plugins/vben-ui/shadcn-ui'
import DemoHero from '../components/demo-hero.vue'
import DemoPanel from '../components/demo-panel.vue'
import '../dashboard-demo.css'

const { t, locale } = useI18n()
const text = (key: string) => t(`dashboardDemo.${key}`)
const metrics = [
  {
    key: 'todayTasks',
    value: '8',
    change: '+2',
    tone: 'hsl(var(--primary))',
    icon: 'lucide:square-check',
    up: false,
  },
  {
    key: 'approvals',
    value: '12',
    change: '-3',
    tone: 'hsl(var(--warning))',
    icon: 'lucide:clock-3',
    up: true,
  },
  {
    key: 'unread',
    value: '6',
    change: '+2',
    tone: 'hsl(var(--primary))',
    icon: 'lucide:mail',
    up: false,
  },
  {
    key: 'online',
    value: '892',
    change: '+68',
    tone: 'hsl(var(--success))',
    icon: 'lucide:users-round',
    up: true,
  },
  {
    key: 'systemStatus',
    value: '',
    change: '',
    tone: 'hsl(var(--primary))',
    icon: 'lucide:shield-check',
    up: true,
  },
]
const quickActions = [
  {
    key: 'newUser',
    icon: 'lucide:user-round-plus',
    tone: 'hsl(var(--primary))',
  },
  { key: 'publish', icon: 'lucide:megaphone', tone: 'hsl(var(--success))' },
  {
    key: 'newTicket',
    icon: 'lucide:clipboard-list',
    tone: 'hsl(var(--warning))',
  },
  {
    key: 'newProject',
    icon: 'lucide:square-plus',
    tone: 'hsl(var(--primary))',
  },
  { key: 'export', icon: 'lucide:download', tone: 'hsl(var(--primary))' },
  { key: 'settings', icon: 'lucide:settings', tone: '#9a54fa' },
  { key: 'sendMessage', icon: 'lucide:send', tone: 'hsl(var(--primary))' },
  { key: 'logs', icon: 'lucide:scroll-text', tone: '#9a54fa' },
  {
    key: 'apps',
    icon: 'lucide:layout-grid',
    tone: 'hsl(var(--muted-foreground))',
  },
]
const tasks = ref([
  {
    key: 'leave',
    priority: 'high',
    tone: 'hsl(var(--destructive))',
    hours: 1,
    done: false,
  },
  {
    key: 'server',
    priority: 'medium',
    tone: 'hsl(var(--warning))',
    hours: 2,
    done: false,
  },
  {
    key: 'budget',
    priority: 'medium',
    tone: 'hsl(var(--warning))',
    hours: 3,
    done: false,
  },
  {
    key: 'release',
    priority: 'low',
    tone: 'hsl(var(--primary))',
    hours: 4,
    done: false,
  },
  {
    key: 'feedback',
    priority: 'medium',
    tone: 'hsl(var(--warning))',
    hours: 5,
    done: false,
  },
  {
    key: 'report',
    priority: 'low',
    tone: 'hsl(var(--primary))',
    hours: 0,
    done: false,
  },
])
const remaining = computed(
  () => tasks.value.filter((task) => !task.done).length,
)
const progressTab = ref(0)
const period = ref('week')
const progressTabs = ['approvalTab', 'ticketTab', 'taskTab']
const progressCounts = [
  [12, 36, 8, 6],
  [8, 24, 4, 3],
  [10, 18, 6, 2],
]
const progressRows = [
  {
    key: 'pending',
    icon: 'lucide:user-round-check',
    tone: 'hsl(var(--warning))',
  },
  {
    key: 'approved',
    icon: 'lucide:clipboard-check',
    tone: 'hsl(var(--success))',
  },
  { key: 'initiated', icon: 'lucide:send', tone: '#8b5cf6' },
  { key: 'copied', icon: 'lucide:mail', tone: 'hsl(var(--primary))' },
]
const feed = [
  { key: 'backup', time: '10:45', tone: 'hsl(var(--primary))', today: true },
  { key: 'release', time: '09:30', tone: 'hsl(var(--success))', today: true },
  { key: 'leave', time: '09:12', tone: 'hsl(var(--primary))', today: true },
  { key: 'user', time: '08:50', tone: 'hsl(var(--primary))', today: true },
  { key: 'cpu', time: '08:20', tone: 'hsl(var(--destructive))', today: true },
  { key: 'ticket', time: '18:30', tone: 'hsl(var(--success))', today: false },
  { key: 'report', time: '17:20', tone: 'hsl(var(--primary))', today: false },
  { key: 'scan', time: '16:10', tone: 'hsl(var(--warning))', today: false },
]
const projects = [
  {
    key: 'cloud',
    team: 'product',
    status: 'inProgress',
    color: 'hsl(var(--success))',
    tone: 'hsl(var(--primary))',
    icon: 'lucide:clipboard-list',
  },
  {
    key: 'admin',
    team: 'tech',
    status: 'inProgress',
    color: 'hsl(var(--success))',
    tone: 'hsl(var(--primary))',
    icon: 'lucide:clipboard-check',
  },
  {
    key: 'mobile',
    team: 'mobile',
    status: 'testing',
    color: 'hsl(var(--primary))',
    tone: 'hsl(var(--warning))',
    icon: 'lucide:smartphone',
  },
  {
    key: 'data',
    team: 'data',
    status: 'planning',
    color: '#9a54fa',
    tone: '#9a54fa',
    icon: 'lucide:database',
  },
  {
    key: 'security',
    team: 'ops',
    status: 'inProgress',
    color: 'hsl(var(--success))',
    tone: 'hsl(var(--destructive))',
    icon: 'lucide:shield-check',
  },
]
const dayOffset = ref(0)
const scheduleDate = computed(() =>
  new Intl.DateTimeFormat(locale.value, {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  }).format(new Date(2024, 4, 20 + dayOffset.value)),
)
const events = ref([
  {
    key: 'standup',
    name: '',
    time: '09:30 - 10:00',
    location: 'a',
    active: false,
    day: 0,
  },
  {
    key: 'review',
    name: '',
    time: '10:00 - 11:00',
    location: '',
    active: true,
    day: 0,
  },
  {
    key: 'summary',
    name: '',
    time: '14:00 - 15:00',
    location: 'b',
    active: false,
    day: 0,
  },
  {
    key: 'optimize',
    name: '',
    time: '15:30 - 16:00',
    location: 'online',
    active: false,
    day: 0,
  },
  {
    key: 'client',
    name: '',
    time: '16:30 - 17:00',
    location: 'tencent',
    active: false,
    day: 0,
  },
])
const visibleEvents = computed(() =>
  events.value.filter((event) => event.day === dayOffset.value),
)
const records = [
  {
    time: '2024-05-20 14:32:18',
    name: 'zhang',
    module: 'users',
    action: 'add',
  },
  {
    time: '2024-05-20 11:21:06',
    name: 'li',
    module: 'settings',
    action: 'settings',
  },
  {
    time: '2024-05-20 10:15:33',
    name: 'wang',
    module: 'menus',
    action: 'menu',
  },
  {
    time: '2024-05-19 18:44:21',
    name: 'admin',
    module: 'roles',
    action: 'role',
  },
  {
    time: '2024-05-19 16:32:12',
    name: 'zhao',
    module: 'tickets',
    action: 'ticket',
  },
  {
    time: '2024-05-19 14:20:05',
    name: 'sun',
    module: 'notices',
    action: 'notice',
  },
]
const dialog = ref(false)
const dialogKey = ref('dialog.title')
const dialogDetail = ref('')
function showDetail(key: string, detail = '') {
  dialogKey.value = key
  dialogDetail.value = detail
  dialog.value = true
}
const newEventDialog = ref(false)
const newEventName = ref('')
function addEvent() {
  if (!newEventName.value.trim()) return
  events.value.push({
    key: '',
    name: newEventName.value.trim(),
    time: '17:30 - 18:00',
    location: 'online',
    active: false,
    day: dayOffset.value,
  })
  newEventName.value = ''
  newEventDialog.value = false
}
</script>

<template>
  <div class="dashboard-demo demo-workspace">
    <DemoHero />
    <div class="demo-metrics">
      <Card
        v-for="metric in metrics"
        :key="metric.key"
        class="demo-metric"
        ><Button
          variant="ghost"
          class="demo-metric-button"
          @click="showDetail(metric.key, metric.value || text('normal'))"
        >
          <span
            class="demo-icon-tile"
            :style="{ '--tone': metric.tone }"
            ><VbenIcon :icon="metric.icon"
          /></span>
          <div class="demo-metric-copy">
            <div class="demo-metric-label">{{ text(metric.key) }}</div>
            <div
              class="demo-metric-value"
              :style="!metric.value ? { fontSize: '21px' } : {}"
            >
              <VbenCountToAnimator
                v-if="metric.value"
                :end-val="Number(metric.value)"
                :duration="1500"
              /><span v-else>{{ text('normal') }}</span>
            </div>
            <div class="demo-metric-sub">
              <template v-if="metric.change"
                ><span>{{ text('yesterday') }}</span
                ><span :class="metric.up ? 'demo-up' : 'demo-down'"
                  >{{ metric.change }}
                  {{ metric.change.startsWith('-') ? '↓' : '↑' }}</span
                ></template
              ><span v-else>{{ text('uptime') }}</span>
            </div>
          </div>
          <VbenIcon
            icon="lucide:chevron-right"
            class="demo-chevron"
          /> </Button
      ></Card>
    </div>
    <div class="demo-work-main">
      <DemoPanel :title="text('quick')"
        ><template #action
          ><Button
            variant="link"
            size="sm"
            class="demo-link demo-muted"
            @click="showDetail('customize')"
          >
            {{ text('customize')
            }}<VbenIcon icon="lucide:chevron-down" /></Button
        ></template>
        <div class="demo-quick-grid">
          <Button
            variant="ghost"
            size="sm"
            v-for="action in quickActions"
            :key="action.key"
            class="demo-quick"
            :style="{ '--tone': action.tone }"
            @click="showDetail(action.key)"
          >
            <VbenIcon :icon="action.icon" /><span>{{ text(action.key) }}</span>
          </Button>
        </div></DemoPanel
      >
      <DemoPanel :title="`${text('todo')}  ${remaining}`"
        ><template #action
          ><Button
            variant="link"
            size="sm"
            class="demo-link"
            @click="
              showDetail(
                'todo',
                tasks
                  .filter((task) => !task.done)
                  .map((task) => text(`tasks.${task.key}`))
                  .join('\n'),
              )
            "
          >
            {{ text('viewAll') }}<VbenIcon icon="lucide:arrow-right" /></Button
        ></template>
        <div
          v-for="task in tasks"
          :key="task.key"
          class="demo-todo"
          :class="{ done: task.done }"
        >
          <ElCheckbox
            v-model="task.done"
            class="demo-todo-check"
            >{{ text(`tasks.${task.key}`) }}</ElCheckbox
          ><span
            class="demo-pill"
            :style="{ '--tone': task.tone }"
            >{{ text(task.priority) }}</span
          ><time class="demo-todo-time">{{
            task.hours
              ? t('dashboardDemo.agoHours', { count: task.hours })
              : `${text('today')} 10:30`
          }}</time>
        </div></DemoPanel
      >
      <DemoPanel :title="text('progress')"
        ><template #action
          ><ElSelect
            size="small"
            v-model="period"
            class="demo-select"
            :aria-label="text('progress')"
          >
            <ElOption
              value="week"
              :label="text('week')"
            />
            <ElOption
              value="month"
              :label="text('month')"
            /> </ElSelect
        ></template>
        <Tabs
          v-model="progressTab"
          class="mb-2"
          ><TabsList class="w-full"
            ><TabsTrigger
              v-for="(tab, index) in progressTabs"
              :key="tab"
              :value="index"
              class="text-xs"
              >{{ text(tab) }} ({{ [12, 8, 10][index] }})</TabsTrigger
            ></TabsList
          ></Tabs
        >
        <div
          v-for="(row, index) in progressRows"
          :key="row.key"
          class="demo-progress-row"
        >
          <span
            class="demo-mini-icon"
            :style="{ '--tone': row.tone }"
            ><VbenIcon :icon="row.icon" /></span
          ><span>{{ text(row.key) }}</span
          ><span
            class="demo-pill"
            :style="{
              '--tone':
                index === 0
                  ? 'hsl(var(--destructive))'
                  : 'hsl(var(--muted-foreground))',
            }"
            >{{
              (progressCounts[progressTab]?.[index] || 0) *
              (period === 'month' ? 4 : 1)
            }}</span
          >
        </div></DemoPanel
      >
      <DemoPanel :title="text('activityTitle')"
        ><template #action
          ><Button
            variant="link"
            size="sm"
            class="demo-link"
            @click="
              showDetail(
                'activityTitle',
                feed.map((item) => text(`activity.${item.key}`)).join('\n'),
              )
            "
          >
            {{ text('all') }}<VbenIcon icon="lucide:chevron-right" /></Button
        ></template>
        <div
          v-for="item in feed"
          :key="item.key"
          class="demo-feed"
        >
          <span
            class="demo-feed-dot"
            :style="{ '--tone': item.tone }"
          /><span class="demo-feed-copy">{{
            text(`activity.${item.key}`)
          }}</span
          ><time
            >{{ text(item.today ? 'today' : 'yesterdayWord') }}
            {{ item.time }}</time
          >
        </div></DemoPanel
      >
    </div>
    <div class="demo-work-bottom">
      <DemoPanel :title="text('projectsTitle')"
        ><template #action
          ><Button
            variant="link"
            size="sm"
            class="demo-link"
            @click="
              showDetail(
                'projectsTitle',
                projects
                  .map((project) => text(`projects.${project.key}`))
                  .join('\n'),
              )
            "
          >
            {{ text('allProjects')
            }}<VbenIcon icon="lucide:arrow-right" /></Button
        ></template>
        <div
          v-for="project in projects"
          :key="project.key"
          class="demo-project-row"
        >
          <span
            class="demo-mini-icon"
            :style="{ '--tone': project.tone }"
            ><VbenIcon :icon="project.icon"
          /></span>
          <div class="demo-project-copy">
            <strong>{{ text(`projects.${project.key}`) }}</strong
            ><small>{{ text(`teams.${project.team}`) }}</small>
          </div>
          <span
            class="demo-pill"
            :style="{ '--tone': project.color }"
            >{{ text(project.status) }}</span
          ><Button
            variant="link"
            size="sm"
            class="demo-link demo-muted"
            :aria-label="text(`projects.${project.key}`)"
            @click="
              showDetail(
                `projects.${project.key}`,
                text(`teams.${project.team}`),
              )
            "
          >
            <VbenIcon icon="lucide:ellipsis" />
          </Button></div
      ></DemoPanel>
      <DemoPanel :title="text('schedule')"
        ><template #action
          ><Button
            variant="link"
            size="sm"
            class="demo-link demo-muted"
            :aria-label="text('yesterdayWord')"
            @click="dayOffset--"
          >
            <VbenIcon icon="lucide:chevron-left" /></Button
          ><span
            class="demo-muted"
            style="font-size: 11px"
            >{{ scheduleDate }}</span
          ><Button
            variant="link"
            size="sm"
            class="demo-link demo-muted"
            :aria-label="text('date')"
            @click="dayOffset++"
          >
            <VbenIcon icon="lucide:chevron-right" /></Button
          ><Button
            variant="default"
            size="sm"
            class="demo-button primary"
            @click="newEventDialog = true"
          >
            <VbenIcon icon="lucide:plus" />{{ text('new') }}
          </Button></template
        >
        <div
          v-for="(event, index) in visibleEvents"
          :key="`${event.key}-${index}`"
          class="demo-schedule-row"
          :class="{ current: event.active }"
        >
          <time>{{ event.time }}</time
          ><span class="demo-schedule-dot" /><span>{{
            event.name || text(`events.${event.key}`)
          }}</span
          ><span
            v-if="event.active"
            class="demo-pill"
            >{{ text('inProgress') }}</span
          ><span
            v-else
            class="demo-schedule-location"
            >{{ text(`locations.${event.location}`) }}</span
          >
        </div>
        <div
          v-if="!visibleEvents.length"
          class="demo-muted"
          style="padding: 70px 0; text-align: center"
        >
          {{ text('dialog.empty') }}
        </div></DemoPanel
      >
      <DemoPanel :title="text('operations')"
        ><template #action
          ><Button
            variant="link"
            size="sm"
            class="demo-link"
            @click="
              showDetail(
                'operations',
                records
                  .map(
                    (record) =>
                      `${record.time} · ${text(`operation.${record.action}`)}`,
                  )
                  .join('\n'),
              )
            "
          >
            {{ text('more') }}<VbenIcon icon="lucide:chevron-right" /></Button
        ></template>
        <div class="demo-table-wrap">
          <ElTable
            :data="records"
            size="small"
            show-overflow-tooltip
          >
            <ElTableColumn
              prop="time"
              :label="text('columns.time')"
              min-width="160"
            />
            <ElTableColumn
              :label="text('columns.user')"
              min-width="100"
              ><template #default="{ row }">{{
                row.name === 'admin'
                  ? 'admin'
                  : row.name
                    ? text(`names.${row.name}`)
                    : ''
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
              :label="text('columns.content')"
              min-width="190"
              ><template #default="{ row }">{{
                row.action ? text(`operation.${row.action}`) : ''
              }}</template></ElTableColumn
            >
            <ElTableColumn
              :label="text('columns.result')"
              width="85"
              ><template #default
                ><i class="demo-status-dot" />{{ text('success') }}</template
              ></ElTableColumn
            ></ElTable
          >
        </div></DemoPanel
      >
    </div>
    <ElDialog
      v-model="dialog"
      :title="text(dialogKey)"
      width="min(520px, 92vw)"
      ><div class="demo-dialog-content">
        <p>{{ dialogDetail }}</p>
        <p class="mt-4 text-muted-foreground">{{ text('dialog.body') }}</p>
      </div>
      <template #footer
        ><Button
          variant="default"
          size="sm"
          class="demo-button primary"
          @click="dialog = false"
        >
          {{ text('dialog.close') }}
        </Button></template
      ></ElDialog
    >
    <ElDialog
      v-model="newEventDialog"
      :title="text('schedule')"
      width="min(440px, 92vw)"
      ><label
        class="mb-2 block"
        for="demo-event-name"
        >{{ text('dialog.name') }}</label
      ><ElInput
        id="demo-event-name"
        v-model="newEventName"
        :placeholder="text('dialog.required')"
        maxlength="60"
        @keyup.enter="addEvent"
      />
      <p class="mt-3 text-xs text-muted-foreground">
        {{ text('dialog.body') }}
      </p>
      <template #footer
        ><Button
          variant="default"
          size="sm"
          class="demo-button primary"
          :disabled="!newEventName.trim()"
          @click="addEvent"
        >
          {{ text('dialog.save') }}
        </Button></template
      ></ElDialog
    >
  </div>
</template>
