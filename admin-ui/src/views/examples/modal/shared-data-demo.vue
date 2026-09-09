<script lang="ts" setup>
import { ref } from 'vue'

import { useVbenModal } from '@/plugins/vben-ui/popup-ui'

import { ElMessage } from 'element-plus'

const data = ref()

const [Modal, modalApi] = useVbenModal({
  onCancel() {
    modalApi.close()
  },
  onConfirm() {
    ElMessage.info('onConfirm')
    // modalApi.close();
  },
  onOpenChange(isOpen: boolean) {
    if (isOpen) {
      data.value = modalApi.getData<Record<string, unknown>>()
    }
  },
})
</script>
<template>
  <Modal title="数据共享示例">
    <div class="flex-col-center">外部传递数据： {{ data }}</div>
  </Modal>
</template>
