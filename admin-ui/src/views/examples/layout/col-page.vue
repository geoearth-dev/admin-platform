<script lang="ts" setup>
import { reactive } from 'vue'

import { ColPage } from '@/components/col-page'
import { IconifyIcon } from '@/assets/icons'

import {
  ElAlert,
  ElButton,
  ElCard,
  ElCheckbox,
  ElSlider,
  ElTag,
  ElTooltip,
} from 'element-plus'

const props = reactive({
  leftCollapsedWidth: 5,
  leftCollapsible: true,
  leftMaxWidth: 50,
  leftMinWidth: 20,
  leftWidth: 30,
  resizable: true,
  rightWidth: 70,
  splitHandle: true,
  splitLine: true,
})
</script>
<template>
  <ColPage
    auto-content-height
    description="ColPage 是一个双列布局组件，支持左侧折叠、拖拽调整宽度等功能。"
    v-bind="props"
    title="ColPage 双列布局组件"
  >
    <template #title>
      <span class="mr-2 text-2xl font-bold">ColPage 双列布局组件</span>
      <ElTag type="danger">Alpha</ElTag>
    </template>
    <template #left="{ isCollapsed, expand }">
      <div
        v-if="isCollapsed"
        @click="expand"
      >
        <ElTooltip content="点击展开左侧">
          <ElButton
            circle
            type="primary"
            class="flex-center"
          >
            <template #icon>
              <IconifyIcon
                class="text-2xl"
                icon="bi:arrow-right"
              />
            </template>
          </ElButton>
        </ElTooltip>
      </div>
      <div
        v-else
        :style="{ minWidth: '200px' }"
        class="mr-2 rounded-(--radius) border border-border bg-card p-2"
      >
        <p>这里是左侧内容</p>
        <p>这里是左侧内容</p>
        <p>这里是左侧内容</p>
        <p>这里是左侧内容</p>
        <p>这里是左侧内容</p>
      </div>
    </template>
    <ElCard
      class="ml-2"
      header="基本使用"
    >
      <div class="flex flex-col gap-2">
        <div class="flex gap-2">
          <ElCheckbox v-model="props.resizable">可拖动调整宽度</ElCheckbox>
          <ElCheckbox v-model="props.splitLine">显示拖动分隔线</ElCheckbox>
          <ElCheckbox v-model="props.splitHandle">显示拖动手柄</ElCheckbox>
          <ElCheckbox v-model="props.leftCollapsible"> 左侧可折叠 </ElCheckbox>
        </div>
        <div class="flex items-center gap-2">
          <span>左侧最小宽度百分比：</span>
          <ElSlider
            v-model="props.leftMinWidth"
            :max="props.leftMaxWidth - 1"
            :min="1"
            class="w-25"
          />
          <span>左侧最大宽度百分比：</span>
          <ElSlider
            v-model="props.leftMaxWidth"
            :max="100"
            :min="props.leftMinWidth + 1"
            class="w-25"
          />
        </div>
        <ElAlert
          title="实验性的组件"
          show-icon
          type="warning"
        >
          <template #description>
            <p>
              双列布局组件是一个在Page组件上扩展的相对基础的布局组件，支持左侧折叠（当拖拽导致左侧宽度比最小宽度还要小时，还可以进入折叠状态）、拖拽调整宽度等功能。
            </p>
            <p>以上宽度设置的数值是百分比，最小值为1，最大值为100。</p>
            <p class="font-bold text-red-600">
              这是一个实验性的组件，用法可能会发生变动，也可能最终不会被采用。在其用法正式出现在文档中之前，不建议在生产环境中使用。
            </p>
          </template>
        </ElAlert>
      </div>
    </ElCard>
  </ColPage>
</template>
