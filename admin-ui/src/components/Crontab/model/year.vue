<template>
  <el-form>
    <el-form-item>
      <el-radio
        value="omit"
        v-model="mode"
      >
        不填，允许的通配符[, - * /]
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        value="every"
        v-model="mode"
      >
        每年
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        value="range"
        v-model="mode"
      >
        周期从
        <el-input-number
          v-model="rangeStart"
          :min="currentYear"
          :max="maxYear - 1"
        />
        -
        <el-input-number
          v-model="rangeEnd"
          :min="(rangeStart ?? currentYear) + 1"
          :max="maxYear"
        />
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        value="interval"
        v-model="mode"
      >
        从
        <el-input-number
          v-model="intervalStart"
          :min="currentYear"
          :max="maxYear - 1"
        />
        年开始，每
        <el-input-number
          v-model="intervalStep"
          :min="1"
          :max="maxYear - (intervalStart ?? currentYear)"
        />
        年执行一次
      </el-radio>
    </el-form-item>

    <el-form-item>
      <el-radio
        value="specific"
        v-model="mode"
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
            v-for="item in 9"
            :key="item"
            :value="item - 1 + currentYear"
            :label="item - 1 + currentYear"
          />
        </el-select>
      </el-radio>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { CrontabField, CrontabValue } from '../type'

type Mode = 'omit' | 'every' | 'range' | 'interval' | 'specific'

const props = defineProps<{
  cron: CrontabValue
  check: (value: number, min: number, max: number) => number
}>()

const emit = defineEmits<{
  update: [name: CrontabField, value: string, from: CrontabField]
}>()

const currentYear = new Date().getFullYear()
const maxYear = 2099
const mode = ref<Mode>('omit')
const rangeStart = ref<number | undefined>(currentYear)
const rangeEnd = ref<number | undefined>(currentYear + 1)
const intervalStart = ref<number | undefined>(currentYear)
const intervalStep = ref<number | undefined>(1)
const selectedValues = ref<number[]>([])
const lastSelectedValue = ref(currentYear)

const rangeExpression = computed(() => {
  const start = props.check(
    rangeStart.value ?? currentYear,
    currentYear,
    maxYear - 1,
  )
  const end = props.check(rangeEnd.value ?? start + 1, start + 1, maxYear)
  return `${start}-${end}`
})

const intervalExpression = computed(() => {
  const start = props.check(
    intervalStart.value ?? currentYear,
    currentYear,
    maxYear - 1,
  )
  const step = props.check(intervalStep.value ?? 1, 1, maxYear - start)
  return `${start}/${step}`
})

const selectedExpression = computed(() => selectedValues.value.join(','))

watch(() => props.cron.year, readExpression)
watch(
  [mode, rangeExpression, intervalExpression, selectedExpression],
  updateExpression,
)

function readExpression(value: string): void {
  if (value === '') {
    mode.value = 'omit'
  } else if (value === '*') {
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
    case 'omit':
      emit('update', 'year', '', 'year')
      break
    case 'every':
      emit('update', 'year', '*', 'year')
      break
    case 'range':
      emit('update', 'year', rangeExpression.value, 'year')
      break
    case 'interval':
      emit('update', 'year', intervalExpression.value, 'year')
      break
    case 'specific': {
      // 清空多选时，保留上次选中的第一个值。
      const firstValue = selectedValues.value[0]
      if (firstValue === undefined) {
        selectedValues.value = [lastSelectedValue.value]
      } else {
        lastSelectedValue.value = firstValue
      }
      emit('update', 'year', selectedExpression.value, 'year')
      break
    }
  }
}
</script>

<style lang="scss" scoped>
.el-input-number--small,
.el-select,
.el-select--small {
  margin: 0 0.2rem;
}
.el-select,
.el-select--small {
  width: 18.8rem;
}
</style>
