<template>
  <el-form class="cron-field cron-field--min">
    <el-form-item>
      <el-radio
        v-model="mode"
        value="every"
      >
        分钟，允许的通配符[, - * /]
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
          :min="0"
          :max="58"
        />
        -
        <el-input-number
          v-model="rangeEnd"
          :min="(rangeStart ?? 0) + 1"
          :max="59"
        />
        分钟
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
          :min="0"
          :max="58"
        />
        分钟开始， 每
        <el-input-number
          v-model="intervalStep"
          :min="1"
          :max="59 - (intervalStart ?? 0)"
        />
        分钟执行一次
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
          :multiple-limit="10"
        >
          <el-option
            v-for="item in 60"
            :key="item"
            :label="item - 1"
            :value="item - 1"
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
const rangeStart = ref<number | undefined>(0)
const rangeEnd = ref<number | undefined>(1)
const intervalStart = ref<number | undefined>(0)
const intervalStep = ref<number | undefined>(1)
const selectedValues = ref<number[]>([])
const lastSelectedValue = ref(0)

const rangeExpression = computed(() => {
  const start = props.check(rangeStart.value ?? 0, 0, 58)
  const end = props.check(rangeEnd.value ?? start + 1, start + 1, 59)
  return `${start}-${end}`
})

const intervalExpression = computed(() => {
  const start = props.check(intervalStart.value ?? 0, 0, 58)
  const step = props.check(intervalStep.value ?? 1, 1, 59 - start)
  return `${start}/${step}`
})

const selectedExpression = computed(() => selectedValues.value.join(','))

watch(() => props.cron.min, readExpression)
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
      emit('update', 'min', '*', 'min')
      break
    case 'range':
      emit('update', 'min', rangeExpression.value, 'min')
      break
    case 'interval':
      emit('update', 'min', intervalExpression.value, 'min')
      break
    case 'specific': {
      // 清空多选时，保留上次选中的第一个值。
      const firstValue = selectedValues.value[0]
      if (firstValue === undefined) {
        selectedValues.value = [lastSelectedValue.value]
      } else {
        lastSelectedValue.value = firstValue
      }
      emit('update', 'min', selectedExpression.value, 'min')
      break
    }
  }
}
</script>
