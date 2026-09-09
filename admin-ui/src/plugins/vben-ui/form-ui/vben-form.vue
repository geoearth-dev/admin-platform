<script setup lang="ts">
import type { VbenFormProps, VbenFormSlots } from './types'

import { useVbenForm } from './use-vben-form'

defineOptions({ inheritAttrs: false })

const props = withDefaults(defineProps<VbenFormProps>(), {
  actionWrapperClass: '',
  collapsed: false,
  collapsedRows: 1,
  commonConfig: () => ({}),
  layout: 'horizontal',
  resetButtonOptions: () => ({}),
  showCollapseButton: false,
  showDefaultActions: true,
  submitButtonOptions: () => ({}),
  wrapperClass: 'grid-cols-1',
})
const slots = defineSlots<VbenFormSlots>()

// 声明式入口与 useVbenForm 共用同一套生命周期、默认值和表单动作。
const [Form, formApi] = useVbenForm(props)

defineExpose({ formApi })
</script>

<template>
  <Form v-bind="{ ...props, ...$attrs }">
    <template
      v-for="(_, name) in slots"
      :key="name"
      #[name]="slotProps"
    >
      <slot
        :name="name"
        v-bind="slotProps"
      />
    </template>
  </Form>
</template>
