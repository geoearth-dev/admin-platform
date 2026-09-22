<template>
  <el-form class="cron-field">
    <el-form-item>
      <el-radio
        v-model="mode"
        value="every"
      >
        日，允许的通配符[, - * ? / L W]
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
        <el-input-number
          v-model="rangeStart"
          :min="1"
          :max="30"
        />
        -
        <el-input-number
          v-model="rangeEnd"
          :min="(rangeStart ?? 1) + 1"
          :max="31"
        />
        日
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="interval"
      >
        从
        <el-input-number
          v-model="intervalStart"
          :min="1"
          :max="30"
        />
        号开始，每
        <el-input-number
          v-model="intervalStep"
          :min="1"
          :max="31 - (intervalStart ?? 1)"
        />
        日执行一次
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="workday"
      >
        每月
        <el-input-number
          v-model="workdayDate"
          :min="1"
          :max="31"
        />
        号最近的那个工作日
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="lastDay"
      >
        本月最后一天
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="specific"
      >
        指定
        <el-select
          v-model="selectedValues"
          clearable
          placeholder="可多选"
          multiple
          :multiple-limit="10"
        >
          <el-option
            v-for="item in 31"
            :key="item"
            :label="item"
            :value="item"
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
  | 'every'
  | 'unspecified'
  | 'range'
  | 'interval'
  | 'workday'
  | 'lastDay'
  | 'specific'

const props = defineProps<{
  cron: CrontabValue
  check: (value: number, min: number, max: number) => number
}>()

const emit = defineEmits<{
  update: [name: CrontabField, value: string, from: CrontabField]
}>()

const mode = ref<Mode>('every')
const rangeStart = ref<number | undefined>(1)
const rangeEnd = ref<number | undefined>(2)
const intervalStart = ref<number | undefined>(1)
const intervalStep = ref<number | undefined>(1)
const workdayDate = ref<number | undefined>(1)
const selectedValues = ref<number[]>([])
const lastSelectedValue = ref(1)

const rangeExpression = computed(() => {
  const start = props.check(rangeStart.value ?? 1, 1, 30)
  const end = props.check(rangeEnd.value ?? start + 1, start + 1, 31)
  return `${start}-${end}`
})

const intervalExpression = computed(() => {
  const start = props.check(intervalStart.value ?? 1, 1, 30)
  const step = props.check(intervalStep.value ?? 1, 1, 31 - start)
  return `${start}/${step}`
})

const workdayExpression = computed(
  () => `${props.check(workdayDate.value ?? 1, 1, 31)}W`,
)

const selectedExpression = computed(() => selectedValues.value.join(','))

watch(() => props.cron.day, readExpression)
watch(
  [
    mode,
    rangeExpression,
    intervalExpression,
    workdayExpression,
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
  } else if (value.includes('/')) {
    const [start, step] = value.split('/')
    intervalStart.value = Number(start)
    intervalStep.value = Number(step)
    mode.value = 'interval'
  } else if (value.includes('W')) {
    workdayDate.value = Number(value.split('W')[0])
    mode.value = 'workday'
  } else if (value === 'L') {
    mode.value = 'lastDay'
  } else {
    selectedValues.value = [...new Set(value.split(',').map(Number))]
    mode.value = 'specific'
  }
}

function updateExpression(): void {
  if (mode.value === 'unspecified' && props.cron.week === '?') {
    emit('update', 'week', '*', 'day')
  }
  if (mode.value !== 'unspecified' && props.cron.week !== '?') {
    emit('update', 'week', '?', 'day')
  }

  switch (mode.value) {
    case 'every':
      emit('update', 'day', '*', 'day')
      break
    case 'unspecified':
      emit('update', 'day', '?', 'day')
      break
    case 'range':
      emit('update', 'day', rangeExpression.value, 'day')
      break
    case 'interval':
      emit('update', 'day', intervalExpression.value, 'day')
      break
    case 'workday':
      emit('update', 'day', workdayExpression.value, 'day')
      break
    case 'lastDay':
      emit('update', 'day', 'L', 'day')
      break
    case 'specific': {
      // 清空多选时，保留上次选中的第一个值。
      const firstValue = selectedValues.value[0]
      if (firstValue === undefined) {
        selectedValues.value = [lastSelectedValue.value]
      } else {
        lastSelectedValue.value = firstValue
      }
      emit('update', 'day', selectedExpression.value, 'day')
      break
    }
  }
}
</script>
