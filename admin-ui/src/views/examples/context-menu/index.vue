<script setup lang="ts">
import { Page } from '@/components/page'
import { VbenContextMenu } from '@/plugins/vben-ui/shadcn-ui'

import { ElButton, ElCard, ElMessage } from 'element-plus'

const needHidden = (role: string) => {
  return role === 'user'
}

const contextMenus = () => {
  return [
    {
      text: '刷新',
      key: 'refresh',
      handler: () => {
        ElMessage.success('刷新成功')
      },
      hidden: needHidden('admin'),
    },
    {
      text: '关闭当前',
      key: 'close-current',
      handler: () => {
        ElMessage.success('关闭当前')
      },
      hidden: needHidden('user'),
    },
    {
      text: '关闭其他',
      key: 'close-other',
      handler: () => {
        ElMessage.success('关闭其他')
      },
    },
    {
      text: '关闭所有',
      key: 'close-all',
      handler: () => {
        ElMessage.success('关闭所有')
      },
    },
  ]
}
</script>

<template>
  <Page title="Context Menu 上下文菜单">
    <ElCard header="基本使用">
      <div>一共四个菜单（刷新、关闭当前、关闭其他、关闭所有）</div>
      <br />
      <br />
      <VbenContextMenu
        :menus="contextMenus"
        :modal="true"
        item-class="pr-6"
      >
        <ElButton> 右键点击我打开上下文菜单(有隐藏项) </ElButton>
      </VbenContextMenu>
    </ElCard>
  </Page>
</template>
