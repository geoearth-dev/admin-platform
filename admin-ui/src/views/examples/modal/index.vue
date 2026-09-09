<script lang="ts" setup>
import { onBeforeUnmount } from 'vue'

import {
  alert,
  clearAllAlerts,
  confirm,
  prompt,
  useVbenModal,
} from '@/plugins/vben-ui/popup-ui'
import { Page } from '@/components/page'

import { ElButton, ElCard, ElMessage } from 'element-plus'

import DocButton from '../doc-button.vue'
import AutoHeightDemo from './auto-height-demo.vue'
import BaseDemo from './base-demo.vue'
import BlurDemo from './blur-demo.vue'
import DragDemo from './drag-demo.vue'
import DynamicDemo from './dynamic-demo.vue'
import FormModalDemo from './form-modal-demo.vue'
import InContentModalDemo from './in-content-demo.vue'
import NestedDemo from './nested-demo.vue'
import SharedDataDemo from './shared-data-demo.vue'

defineOptions({ name: 'ModalExample' })

const [BaseModal, baseModalApi] = useVbenModal({
  // 连接抽离的组件
  connectedComponent: BaseDemo,
})

const [InContentModal, inContentModalApi] = useVbenModal({
  // 连接抽离的组件
  connectedComponent: InContentModalDemo,
})

const [AutoHeightModal, autoHeightModalApi] = useVbenModal({
  connectedComponent: AutoHeightDemo,
})

const [DragModal, dragModalApi] = useVbenModal({
  connectedComponent: DragDemo,
})

const [DynamicModal, dynamicModalApi] = useVbenModal({
  connectedComponent: DynamicDemo,
})

const [SharedDataModal, sharedModalApi] = useVbenModal({
  connectedComponent: SharedDataDemo,
})

const [FormModal, formModalApi] = useVbenModal({
  connectedComponent: FormModalDemo,
})

const [NestedModal, nestedModalApi] = useVbenModal({
  connectedComponent: NestedDemo,
})

const [BlurModal, blurModalApi] = useVbenModal({
  connectedComponent: BlurDemo,
})

function openBaseModal() {
  baseModalApi.open()
}

function openInContentModal() {
  inContentModalApi.open()
}

function openAutoHeightModal() {
  autoHeightModalApi.open()
}

function openDragModal() {
  dragModalApi.open()
}

function openDynamicModal() {
  dynamicModalApi.open()
}

function openSharedModal() {
  sharedModalApi
    .setData({
      content: '外部传递的数据 content',
      payload: '外部传递的数据 payload',
    })
    .open()
}

function openNestedModal() {
  nestedModalApi.open()
}

function openBlurModal() {
  blurModalApi.open()
}

function handleUpdateTitle() {
  dynamicModalApi.setState({ title: '外部动态标题' }).open()
}

function openFormModal() {
  formModalApi
    .setData({
      // 表单值
      values: { field1: 'abc', field2: '123', field3: '1' },
    })
    .open()
}

function openAlert() {
  alert({
    content: '这是一个弹窗',
    icon: 'success',
  }).then(() => {
    ElMessage.info('用户关闭了弹窗')
  })
}

onBeforeUnmount(() => {
  // 清除所有弹窗
  clearAllAlerts()
})

function openConfirm() {
  confirm({
    beforeClose({ isConfirm }) {
      if (!isConfirm) return
      // 这里可以做一些异步操作
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve(true)
        }, 1000)
      })
    },
    centered: false,
    content: '这是一个确认弹窗',
    icon: 'question',
  })
    .then(() => {
      ElMessage.success('用户确认了操作')
    })
    .catch(() => {
      ElMessage.error('用户取消了操作')
    })
}

async function openPrompt() {
  prompt<string>({
    async beforeClose({ isConfirm, value }) {
      if (isConfirm && value === '芝士') {
        ElMessage.error('不能吃芝士')
        return false
      }
    },
    componentProps: { placeholder: '不能吃芝士...' },
    content: '中午吃了什么？',
    icon: 'question',
    overlayBlur: 3,
  })
    .then((res) => {
      ElMessage.success(`用户输入了：${res}`)
    })
    .catch(() => {
      ElMessage.error('用户取消了输入')
    })
}
</script>

<template>
  <Page
    auto-content-height
    description="弹窗组件常用于在不离开当前页面的情况下，显示额外的信息、表单或操作提示，更多api请查看组件文档。"
    title="弹窗组件示例"
  >
    <template #extra>
      <DocButton path="/components/common-ui/vben-modal" />
    </template>
    <BaseModal />
    <InContentModal />
    <AutoHeightModal />
    <DragModal />
    <DynamicModal />
    <SharedDataModal />
    <FormModal />
    <NestedModal />
    <BlurModal />
    <div class="flex w-full flex-wrap gap-2.5">
      <ElCard
        class="w-75"
        header="基本使用"
      >
        <p>一个基础的弹窗示例</p>
        <template #footer>
          <ElButton
            type="primary"
            @click="openBaseModal"
            >打开弹窗</ElButton
          >
        </template>
      </ElCard>

      <ElCard
        class="w-75"
        header="指定容器+关闭后不销毁"
      >
        <p>在内容区域打开弹窗的示例</p>
        <template #footer>
          <ElButton
            type="primary"
            @click="openInContentModal"
            >打开弹窗</ElButton
          >
        </template>
      </ElCard>

      <ElCard
        class="w-75"
        header="内容高度自适应"
      >
        <p>可根据内容并自动调整高度</p>
        <template #footer>
          <ElButton
            type="primary"
            @click="openAutoHeightModal"
          >
            打开弹窗
          </ElButton>
        </template>
      </ElCard>

      <ElCard
        class="w-75"
        header="可拖拽示例"
      >
        <p>配置 draggable 可开启拖拽功能</p>
        <template #footer>
          <ElButton
            type="primary"
            @click="openDragModal"
          >
            打开弹窗
          </ElButton>
        </template>
      </ElCard>

      <ElCard
        class="w-75"
        header="动态配置示例"
      >
        <p>通过 setState 动态调整弹窗数据</p>
        <template #header
          ><div class="flex items-center justify-between gap-3">
            <span>动态配置示例</span>
            <div>
              <ElButton
                link
                type="primary"
                @click="openDynamicModal"
                >打开弹窗</ElButton
              >
            </div>
          </div></template
        >
        <template #footer>
          <ElButton
            type="primary"
            @click="handleUpdateTitle"
          >
            外部修改标题并打开
          </ElButton>
        </template>
      </ElCard>

      <ElCard
        class="w-75"
        header="内外数据共享示例"
      >
        <p>通过共享 sharedData 来进行数据交互</p>
        <template #footer>
          <ElButton
            type="primary"
            @click="openSharedModal"
          >
            打开弹窗并传递数据
          </ElButton>
        </template>
      </ElCard>

      <ElCard
        class="w-75"
        header="表单弹窗示例"
      >
        <p>弹窗与表单结合</p>
        <template #footer>
          <ElButton
            type="primary"
            @click="openFormModal"
          >
            打开表单弹窗
          </ElButton>
        </template>
      </ElCard>

      <ElCard
        class="w-75"
        header="嵌套弹窗示例"
      >
        <p>在已经打开的弹窗中再次打开弹窗</p>
        <template #footer>
          <ElButton
            type="primary"
            @click="openNestedModal"
            >打开嵌套弹窗</ElButton
          >
        </template>
      </ElCard>

      <ElCard
        class="w-75"
        header="遮罩模糊示例"
      >
        <p>遮罩层应用类似毛玻璃的模糊效果</p>
        <template #footer>
          <ElButton
            type="primary"
            @click="openBlurModal"
            >打开弹窗</ElButton
          >
        </template>
      </ElCard>
      <ElCard
        class="w-75"
        header="轻量提示弹窗"
      >
        <template #header
          ><div class="flex items-center justify-between gap-3">
            <span>轻量提示弹窗</span>
            <div>
              <DocButton path="/components/common-ui/vben-alert" />
            </div></div
        ></template>
        <p>通过快捷方法创建动态提示弹窗，适合一些轻量的提示和确认、输入等</p>
        <template #footer>
          <ElButton
            type="primary"
            @click="openAlert"
            >Alert</ElButton
          >
          <ElButton
            type="primary"
            @click="openConfirm"
            >Confirm</ElButton
          >
          <ElButton
            type="primary"
            @click="openPrompt"
            >Prompt</ElButton
          >
        </template>
      </ElCard>
    </div>
  </Page>
</template>
