<script setup lang="ts">
import { Page } from '@/components/page'
import { SliderTranslateCaptcha } from '@/components/captcha'

import { ElCard, ElMessage } from 'element-plus'

function handleSuccess() {
  ElMessage.success('success!')
}
</script>

<template>
  <Page
    description="用于前端简单的拼图滑块水平拖动校验场景"
    title="拼图滑块校验"
  >
    <ElCard
      class="mb-5"
      header="基本示例"
    >
      <div class="flex-center p-4">
        <SliderTranslateCaptcha
          src="https://unpkg.com/@vbenjs/static-source@0.1.7/source/pro-avatar.webp"
          :canvas-width="420"
          :canvas-height="420"
          @success="handleSuccess"
        />
      </div>
    </ElCard>
  </Page>
</template>
