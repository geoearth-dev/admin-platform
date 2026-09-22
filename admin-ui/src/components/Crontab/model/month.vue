<template>
  <el-form class="cron-field">
    <el-form-item>
      <el-radio
        v-model="mode"
        value="every"
      >
        月，允许的通配符[, - * /]
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
          :max="11"
        />
        -
        <el-input-number
          v-model="rangeEnd"
          :min="(rangeStart ?? 1) + 1"
          :max="12"
        />
        月
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
          :max="11"
        />
        月开始，每
        <el-input-number
          v-model="intervalStep"
          :min="1"
          :max="12 - (intervalStart ?? 1)"
        />
        月执行一次
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        v-model="mode"
        value="specific"
      >
        指定
        <el-select
          clearable
          v-model="selectedValues"
          placeholder="可多选"
          multiple
          :multiple-limit="8"
        >
          <el-option
            v-for="item in monthOptions"
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

type Mode = 'every' | 'range' | 'interval' | 'specific'

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
const selectedValues = ref<number[]>([])
const lastSelectedValue = ref(1)

const monthOptions = [
  { value: 1, label: '一月' },
  { value: 2, label: '二月' },
  { value: 3, label: '三月' },
  { value: 4, label: '四月' },
  { value: 5, label: '五月' },
  { value: 6, label: '六月' },
  { value: 7, label: '七月' },
  { value: 8, label: '八月' },
  { value: 9, label: '九月' },
  { value: 10, label: '十月' },
  { value: 11, label: '十一月' },
  { value: 12, label: '十二月' },
]

const rangeExpression = computed(() => {
  const start = props.check(rangeStart.value ?? 1, 1, 11)
  const end = props.check(rangeEnd.value ?? start + 1, start + 1, 12)
  return `${start}-${end}`
})

const intervalExpression = computed(() => {
  const start = props.check(intervalStart.value ?? 1, 1, 11)
  const step = props.check(intervalStep.value ?? 1, 1, 12 - start)
  return `${start}/${step}`
})

const selectedExpression = computed(() => selectedValues.value.join(','))

watch(() => props.cron.month, readExpression)
watch(
  [mode, rangeExpression, intervalExpression, selectedExpression],
  updateExpression,
)

function readExpression(value: string): void {
  if (value === '*') {
    mode.value = 'every'
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
  } else {
    selectedValues.value = [...new Set(value.split(',').map(Number))]
    mode.value = 'specific'
  }
}

function updateExpression(): void {
  switch (mode.value) {
    case 'every':
      emit('update', 'month', '*', 'month')
      break
    case 'range':
      emit('update', 'month', rangeExpression.value, 'month')
      break
    case 'interval':
      emit('update', 'month', intervalExpression.value, 'month')
      break
    case 'specific': {
      // 清空多选时，保留上次选中的第一个值。
      const firstValue = selectedValues.value[0]
      if (firstValue === undefined) {
        selectedValues.value = [lastSelectedValue.value]
      } else {
        lastSelectedValue.value = firstValue
      }
      emit('update', 'month', selectedExpression.value, 'month')
      break
    }
  }
}
</script>
