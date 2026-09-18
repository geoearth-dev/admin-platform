<template>
  <el-form>
    <el-form-item>
      <el-radio
        v-model="mode"
        value="every"
      >
        周，允许的通配符[, - * ? / L #]
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="unspecified"
      >
        不指定
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="range"
      >
        周期从
        <el-select
          clearable
          v-model="rangeStart"
        >
          <el-option
            v-for="(item, index) of weekOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
            :disabled="item.value === 7"
            >{{ item.label }}</el-option
          >
        </el-select>
        -
        <el-select
          clearable
          v-model="rangeEnd"
        >
          <el-option
            v-for="(item, index) of weekOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
            :disabled="item.value <= (rangeStart ?? 2)"
            >{{ item.label }}</el-option
          >
        </el-select>
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="nthWeekday"
      >
        第
        <el-input-number
          v-model="weekOccurrence"
          :min="1"
          :max="4"
        />
        周的
        <el-select
          clearable
          v-model="nthWeekday"
        >
          <el-option
            v-for="item in weekOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="lastWeekday"
      >
        本月最后一个
        <el-select
          clearable
          v-model="lastWeekday"
        >
          <el-option
            v-for="item in weekOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="specific"
      >
        指定
        <el-select
          class="multiselect"
          clearable
          v-model="selectedValues"
          placeholder="可多选"
          multiple
          :multiple-limit="6"
        >
          <el-option
            v-for="item in weekOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-radio>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { CrontabField, CrontabValue } from '../type'

type Mode =
  'every' | 'unspecified' | 'range' | 'nthWeekday' | 'lastWeekday' | 'specific'

const props = defineProps<{
  cron: CrontabValue
  check: (value: number, min: number, max: number) => number
}>()

const emit = defineEmits<{
  update: [name: CrontabField, value: string, from: CrontabField]
}>()

const mode = ref<Mode>('unspecified')
const rangeStart = ref<number | undefined>(2)
const rangeEnd = ref<number | undefined>(3)
const weekOccurrence = ref<number | undefined>(1)
const nthWeekday = ref<number | undefined>(2)
const lastWeekday = ref<number | undefined>(2)
const selectedValues = ref<number[]>([])
const lastSelectedValue = ref(2)

const weekOptions = [
  { value: 1, label: '星期日' },
  { value: 2, label: '星期一' },
  { value: 3, label: '星期二' },
  { value: 4, label: '星期三' },
  { value: 5, label: '星期四' },
  { value: 6, label: '星期五' },
  { value: 7, label: '星期六' },
]

const rangeExpression = computed(() => {
  const start = props.check(rangeStart.value ?? 2, 1, 6)
  const end = props.check(rangeEnd.value ?? start + 1, start + 1, 7)
  return `${start}-${end}`
})

const nthWeekdayExpression = computed(() => {
  const occurrence = props.check(weekOccurrence.value ?? 1, 1, 4)
  const weekday = props.check(nthWeekday.value ?? 2, 1, 7)
  return `${weekday}#${occurrence}`
})

const lastWeekdayExpression = computed(
  () => `${props.check(lastWeekday.value ?? 2, 1, 7)}L`,
)

const selectedExpression = computed(() => selectedValues.value.join(','))

watch(() => props.cron.week, readExpression)
watch(
  [
    mode,
    rangeExpression,
    nthWeekdayExpression,
    lastWeekdayExpression,
    selectedExpression,
  ],
  updateExpression,
)

function readExpression(value: string): void {
  if (value === '*') {
    mode.value = 'every'
  } else if (value === '?') {
    mode.value = 'unspecified'
  } else if (value.includes('-')) {
    const [start, end] = value.split('-')
    rangeStart.value = Number(start)
    rangeEnd.value = Number(end)
    mode.value = 'range'
  } else if (value.includes('#')) {
    const [weekday, occurrence] = value.split('#')
    weekOccurrence.value = Number(occurrence)
    nthWeekday.value = Number(weekday)
    mode.value = 'nthWeekday'
  } else if (value.includes('L')) {
    lastWeekday.value = Number(value.split('L')[0])
    mode.value = 'lastWeekday'
  } else {
    selectedValues.value = [...new Set(value.split(',').map(Number))]
    mode.value = 'specific'
  }
}

function updateExpression(): void {
  if (mode.value === 'unspecified' && props.cron.day === '?') {
    emit('update', 'day', '*', 'week')
  }
  if (mode.value !== 'unspecified' && props.cron.day !== '?') {
    emit('update', 'day', '?', 'week')
  }

  switch (mode.value) {
    case 'every':
      emit('update', 'week', '*', 'week')
      break
    case 'unspecified':
      emit('update', 'week', '?', 'week')
      break
    case 'range':
      emit('update', 'week', rangeExpression.value, 'week')
      break
    case 'nthWeekday':
      emit('update', 'week', nthWeekdayExpression.value, 'week')
      break
    case 'lastWeekday':
      emit('update', 'week', lastWeekdayExpression.value, 'week')
      break
    case 'specific': {
      // 清空多选时，保留上次选中的第一个值。
      const firstValue = selectedValues.value[0]
      if (firstValue === undefined) {
        selectedValues.value = [lastSelectedValue.value]
      } else {
        lastSelectedValue.value = firstValue
      }
      emit('update', 'week', selectedExpression.value, 'week')
      break
    }
  }
}
</script>

<style lang="scss" scoped>
.el-input-number--small,
.el-select,
.el-select--small {
  margin: 0 0.5rem;
}
.el-select,
.el-select--small {
  width: 8rem;
}
.el-select.multiselect,
.el-select--small.multiselect {
  width: 17.8rem;
}
</style>
