<script lang="ts" setup>
import { ref } from 'vue'

import { Page } from '@/components/page'

import {
  ElButton,
  ElCard,
  ElMessage,
  ElSteps,
  ElStep,
  ElSwitch,
} from 'element-plus'

import { useVbenForm } from '@/plugins/vben-ui/form-ui'

const currentTab = ref(0)
function onFirstSubmit(values: Record<string, unknown>) {
  ElMessage.success({
    message: `form1 values: ${JSON.stringify(values)}`,
  })
  currentTab.value = 1
}
function onSecondReset() {
  currentTab.value = 0
}
function onSecondSubmit(values: Record<string, unknown>) {
  ElMessage.success({
    message: `form2 values: ${JSON.stringify(values)}`,
  })
}

const [FirstForm, firstFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
  },
  handleSubmit: onFirstSubmit,
  layout: 'horizontal',
  resetButtonOptions: {
    show: false,
  },
  schema: [
    {
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
      },
      fieldName: 'formFirst',
      label: '表单1字段',
      rules: 'required',
    },
  ],
  submitButtonOptions: {
    content: '下一步',
  },
  wrapperClass: 'grid-cols-1 md:grid-cols-1 lg:grid-cols-1',
})
const [SecondForm, secondFormApi] = useVbenForm({
  commonConfig: {
    componentProps: {
      class: 'w-full',
    },
  },
  handleReset: onSecondReset,
  handleSubmit: onSecondSubmit,
  layout: 'horizontal',
  resetButtonOptions: {
    content: '上一步',
  },
  schema: [
    {
      component: 'Input',
      componentProps: {
        placeholder: '请输入',
      },
      fieldName: 'formSecond',
      label: '表单2字段',
      rules: 'required',
    },
  ],
  wrapperClass: 'grid-cols-1 md:grid-cols-1 lg:grid-cols-1',
})
const stepsItems = [{ title: '表单1' }, { title: '表单2' }]
const needMerge = ref(true)
async function handleMergeSubmit() {
  const values = await firstFormApi
    .merge(secondFormApi)
    .submitAllForm(needMerge.value)
  ElMessage.success({
    message: `merged form values: ${JSON.stringify(values)}`,
  })
}
</script>

<template>
  <Page
    description="表单组件合并示例：在某些场景下，例如分步表单，需要合并多个表单并统一提交。默认情况下，使用 Object.assign 规则合并表单。如果需要特殊处理数据，可以传入 false。"
    title="表单组件"
  >
    <ElCard header="基础示例">
      <template #header
        ><div class="flex items-center justify-between gap-3">
          <span>基础示例</span>
          <div>
            <ElSwitch
              v-model="needMerge"
              active-text="开启字段合并"
              class="mr-4"
              inactive-text="关闭字段合并"
            />
            <ElButton
              type="primary"
              @click="handleMergeSubmit"
              >合并提交</ElButton
            >
          </div>
        </div></template
      >
      <div class="mx-auto max-w-lg">
        <ElSteps
          :active="currentTab"
          class="steps"
          ><ElStep
            v-for="item in stepsItems"
            :key="item.title"
            :title="item.title"
        /></ElSteps>
        <div class="p-20">
          <FirstForm v-show="currentTab === 0" />
          <SecondForm v-show="currentTab === 1" />
        </div>
      </div>
    </ElCard>
  </Page>
</template>
