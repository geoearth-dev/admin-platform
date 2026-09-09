<script lang="ts" setup>
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui'

import { ElButton, ElMessage } from 'element-plus'

const [Drawer, drawerApi] = useVbenDrawer({
  onCancel() {
    drawerApi.close()
  },
  onClosed() {
    drawerApi.setState({ overlayBlur: 0, placement: 'right' })
  },
  onConfirm() {
    ElMessage.info('onConfirm')
    // drawerApi.close();
  },
})

function lockDrawer() {
  drawerApi.lock()
  setTimeout(() => {
    drawerApi.unlock()
  }, 3000)
}
</script>
<template>
  <Drawer
    title="基础抽屉示例"
    title-tooltip="标题提示内容"
  >
    <template #extra> extra </template>
    base demo
    <ElButton
      type="primary"
      @click="lockDrawer"
      >锁定抽屉状态</ElButton
    >
    <!-- <template #prepend-footer> slot </template> -->
    <!-- <template #append-footer> prepend slot </template> -->
    <!-- <template #center-footer> center slot </template> -->
  </Drawer>
</template>
