<script lang="ts" setup>
import { openWindow } from '@/utils/window'

import { Button } from '@/plugins/vben-ui/shadcn-ui'

const VBEN_DOC_URL = 'https://doc.vben.pro'

const props = defineProps<{ path: string }>()

function handleClick() {
  // 如果没有.html，打开页面时可能会出现404
  const path =
    VBEN_DOC_URL +
    (props.path.toLowerCase().endsWith('.html')
      ? props.path
      : `${props.path}.html`)
  openWindow(path)
}
</script>

<template>
  <Button @click="handleClick">查看组件文档</Button>
</template>
