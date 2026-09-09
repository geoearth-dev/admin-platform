<script lang="ts" setup>
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui'

import { useVbenForm } from '@/plugins/vben-ui/form-ui'

defineOptions({
  name: 'FormDrawerDemo',
})

const [Form, formApi] = useVbenForm({
  schema: [
    {
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
      },
      fieldName: 'field1',
      label: '字段1',
      rules: 'required',
    },
    {
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
      },
      fieldName: 'field2',
      label: '字段2',
      rules: 'required',
    },
  ],
  showDefaultActions: false,
})
const [Drawer, drawerApi] = useVbenDrawer({
  onCancel() {
    drawerApi.close()
  },
  onConfirm: async () => {
    await formApi.submit()
    drawerApi.close()
  },
  onOpenChange(isOpen: boolean) {
    if (isOpen) {
      const { values } = drawerApi.getData<Record<string, unknown>>()
      if (values) {
        formApi.setValues(values)
      }
    }
  },
  title: '内嵌表单示例',
})
</script>
<template>
  <Drawer>
    <Form />
  </Drawer>
</template>
