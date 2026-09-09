<script lang="ts" setup>
import { useVbenModal } from '@/plugins/vben-ui/popup-ui'

import { ElMessage } from 'element-plus'

import { useVbenForm } from '@/plugins/vben-ui/form-ui'

defineOptions({
  name: 'FormModelDemo',
})

const [Form, formApi] = useVbenForm({
  handleSubmit: onSubmit,
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
    {
      component: 'Select',
      componentProps: {
        class: 'w-full',
        options: [
          { label: '选项1', value: '1' },
          { label: '选项2', value: '2' },
        ],
        placeholder: '请输入',
      },
      fieldName: 'field3',
      label: '字段3',
      rules: 'required',
    },
  ],
  showDefaultActions: false,
})

const [Modal, modalApi] = useVbenModal({
  fullscreenButton: false,
  onCancel() {
    modalApi.close()
  },
  onConfirm: async () => {
    await formApi.validateAndSubmit()
    // modalApi.close();
  },
  onOpenChange(isOpen: boolean) {
    if (isOpen) {
      const { values } = modalApi.getData<Record<string, unknown>>()
      if (values) {
        formApi.setValues(values)
      }
    }
  },
  title: '内嵌表单示例',
})

function onSubmit(values: Record<string, unknown>) {
  const notice = ElMessage.info({
    message: '正在提交中...',
    duration: 0,
  })
  modalApi.lock()
  setTimeout(() => {
    modalApi.close()
    notice.close()
    ElMessage.success({
      message: `提交成功：${JSON.stringify(values)}`,
      duration: 2000,
    })
  }, 3000)
}
</script>
<template>
  <Modal>
    <Form />
  </Modal>
</template>
