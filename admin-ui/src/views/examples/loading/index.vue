<script lang="ts" setup>
import { refAutoReset } from '@vueuse/core'
import { ElButton, ElCard } from 'element-plus'
import { Page } from '@/components/page'
import { IconifyIcon } from '@/assets/icons'
import { VbenLoading, VbenSpinner } from '@/plugins/vben-ui/shadcn-ui'

const elementLoading = refAutoReset(false, 3000)
const loading = refAutoReset(false, 3000)
const spinning = refAutoReset(false, 3000)
</script>

<template>
  <Page
    title="Loading"
    description="加载遮罩示例，点击后在 3 秒内自动关闭。Vben 加载组件的容器需要 relative 定位。"
  >
    <ElCard header="Element Plus Loading">
      <div
        v-loading="elementLoading"
        element-loading-text="加载中..."
        class="flex h-40 items-center justify-center"
      >
        <ElButton
          type="primary"
          @click="elementLoading = true"
          >显示加载遮罩</ElButton
        >
      </div>
      <template #footer>使用项目入口已注册的 v-loading 指令。</template>
    </ElCard>

    <ElCard
      header="Vben Loading"
      class="mt-4"
    >
      <div class="flex flex-wrap gap-4">
        <div class="relative flex size-40 items-center justify-center">
          <ElButton
            type="primary"
            @click="loading = true"
            >默认动画</ElButton
          >
          <VbenLoading
            :spinning="loading"
            text="正在加载..."
          />
        </div>
        <div class="relative flex size-40 items-center justify-center">
          <ElButton
            type="primary"
            @click="loading = true"
            >自定义动画 1</ElButton
          >
          <VbenLoading :spinning="loading">
            <template #icon
              ><IconifyIcon
                icon="svg-spinners:ring-resize"
                class="size-10 text-primary"
            /></template>
          </VbenLoading>
        </div>
        <div class="relative flex size-40 items-center justify-center">
          <ElButton
            type="primary"
            @click="loading = true"
            >自定义动画 2</ElButton
          >
          <VbenLoading :spinning="loading">
            <template #icon
              ><IconifyIcon
                icon="svg-spinners:bars-scale"
                class="size-10 text-primary"
            /></template>
          </VbenLoading>
        </div>
      </div>
      <template #footer
        >通过 text 设置文字，通过 icon 插槽替换加载图标。</template
      >
    </ElCard>

    <ElCard
      header="Vben Spinner"
      class="mt-4"
    >
      <div
        class="relative flex h-40 items-center justify-center overflow-hidden"
      >
        <ElButton
          type="primary"
          @click="spinning = true"
          >显示 Spinner</ElButton
        >
        <VbenSpinner :spinning="spinning" />
      </div>
      <template #footer>使用本地 VbenSpinner 组件的固定加载动画。</template>
    </ElCard>
  </Page>
</template>
