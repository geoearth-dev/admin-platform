<script setup lang="ts">
// oxlint-disable unicorn/no-nested-ternary
import type { VbenFormFieldArrayProps } from '../types'

import { computed } from 'vue'

import { Plus, X } from '@/assets/icons'
import {
  VbenButton,
  VbenIconButton,
  VbenRenderContent,
} from '@/plugins/vben-ui/shadcn-ui'

import { injectRenderFormProps } from '../form-render/context'
import FormField from '../form-render/form-field.vue'
import { createArrayChildSchema } from '../form-render/schema'
import { cloneDeep } from 'es-toolkit/compat'
import { getDefaultValueInZodStack } from '../form-render/helper'
import { getValueByFieldName, setValueByFieldName } from '../field-name'
defineOptions({ name: 'VbenFormFieldArray', inheritAttrs: false })

const props = withDefaults(defineProps<VbenFormFieldArrayProps>(), {
  actionText: '操作',
  addButtonText: '添加一行',
  createRow: undefined,
  disabled: false,
  emptyText: '暂无数据',
  commonConfig: () => ({}),
  globalCommonConfig: () => ({}),
  max: Number.POSITIVE_INFINITY,
  min: 0,
  name: '',
  schema: () => [],
  showIndex: true,
})

const arrayPath = computed(() => props.name)
const formRenderProps = injectRenderFormProps()
const form = formRenderProps.form
if (!form) {
  throw new Error('Form api is required in <VbenFormFieldArray />')
}
const formActions = form
const arrayLength = formActions.useSelector((state) => {
  const value = getValueByFieldName(state.values, props.name)
  return Array.isArray(value) ? value.length : 0
})
const rowIndexes = computed(() =>
  Array.from({ length: arrayLength.value }, (_, index) => index),
)

const canAdd = computed(() => arrayLength.value < props.max)
const canRemove = computed(() => arrayLength.value > props.min)
const gridStyle = computed(() => {
  const columns = [
    ...(props.showIndex ? ['3rem'] : []),
    ...props.schema.map(() => 'minmax(0, 1fr)'),
    '4rem',
  ]
  return {
    gridTemplateColumns: columns.join(' '),
  }
})

function buildDefaultRow(): Record<string, unknown> {
  if (props.createRow) {
    return props.createRow()
  }

  const row: Record<string, unknown> = {}
  props.schema.forEach((col) => {
    let value: unknown = null
    if (Reflect.has(col, 'defaultValue') && col.defaultValue !== undefined) {
      value = cloneDeep(col.defaultValue)
    } else if ('type' in col && col.type === 'array') {
      value = []
    } else {
      value = getDefaultValueInZodStack(col.rules) ?? null
    }
    setValueByFieldName(row, col.fieldName, value)
  })
  return row
}

function addRow() {
  if (props.disabled || !canAdd.value) {
    return
  }
  formActions.pushFieldValue(arrayPath.value, buildDefaultRow())
}

function removeRow(index: number) {
  if (props.disabled || !canRemove.value) {
    return
  }
  void formActions.removeFieldValue(arrayPath.value, index)
}

function rowSchemas(index: number) {
  return props.schema.map((col) =>
    createArrayChildSchema(col, {
      arrayField: arrayPath.value,
      commonConfig: props.commonConfig,
      disabled: props.disabled,
      globalCommonConfig: props.globalCommonConfig,
      index,
    }),
  )
}

const normalizedRowSchemas = computed(() =>
  Array.from({ length: arrayLength.value }, (_, index) => rowSchemas(index)),
)
</script>

<template>
  <div
    class="w-full"
    :class="$attrs.class"
  >
    <div class="border-border/70 overflow-hidden rounded-md border">
      <div
        class="bg-muted/30 border-border hidden border-b px-2 sm:grid"
        :style="gridStyle"
      >
        <div
          v-if="showIndex"
          class="text-muted-foreground px-2 py-2 text-left text-sm font-normal"
        >
          #
        </div>
        <div
          v-for="col in schema"
          :key="col.fieldName"
          class="text-muted-foreground px-2 py-2 text-left text-sm font-normal"
        >
          <VbenRenderContent :content="col.label" />
        </div>
        <div
          class="text-muted-foreground px-2 py-2 text-left text-sm font-normal"
        >
          {{ actionText }}
        </div>
      </div>

      <div
        v-for="index in rowIndexes"
        :key="`${arrayPath}-${index}`"
        class="border-border/60 border-b p-3 last:border-b-0 sm:grid sm:p-0"
        :style="gridStyle"
      >
        <div
          v-if="showIndex"
          class="text-muted-foreground mb-2 text-sm sm:mb-0 sm:px-4 sm:py-3"
        >
          <span class="sm:hidden">#</span>
          {{ index + 1 }}
        </div>

        <template
          v-for="(childSchema, childIndex) in normalizedRowSchemas[index]"
          :key="childSchema.fieldName"
        >
          <div class="min-w-0 py-2 sm:px-2">
            <div
              class="text-muted-foreground mb-1 text-xs font-medium sm:hidden"
            >
              <VbenRenderContent :content="schema?.[childIndex]?.label" />
            </div>
            <FormField
              v-bind="childSchema"
              :class="childSchema.formItemClass"
            />
          </div>
        </template>

        <div class="flex justify-end pt-1 sm:block sm:px-2 sm:py-3">
          <VbenIconButton
            type="button"
            :disabled="disabled || !canRemove"
            :on-click="() => removeRow(index)"
            class="text-muted-foreground hover:text-destructive"
          >
            <X class="size-4" />
          </VbenIconButton>
        </div>
      </div>

      <div
        v-if="arrayLength === 0"
        class="text-muted-foreground py-6 text-center text-sm"
      >
        {{ emptyText }}
      </div>
    </div>

    <VbenButton
      variant="outline"
      size="sm"
      type="button"
      :disabled="disabled || !canAdd"
      class="mt-3 w-full border-dashed"
      @click="addRow"
    >
      <Plus class="mr-1 size-4" />
      {{ addButtonText }}
    </VbenButton>
  </div>
</template>
