<script lang="ts" setup>
import type { JsonViewerValue } from '@/components/json-viewer';

import { JsonViewer } from '@/components/json-viewer';
import { Page } from '@/components/page';

import { ElCard, ElMessage } from 'element-plus';

import { json1, json2 } from './data';

function handleKeyClick(key: string) {
  ElMessage.info(`点击了Key ${key}`);
}

function handleValueClick(value: JsonViewerValue) {
  ElMessage.info(`点击了Value ${JSON.stringify(value)}`);
}

function handleCopied() {
  ElMessage.success('已复制JSON');
}
</script>
<template>
  <Page title="Json Viewer" description="一个渲染 JSON 结构数据的组件，支持复制、展开等，简单易用">
    <ElCard header="默认配置">
      <JsonViewer :value="json1" />
    </ElCard>
    <ElCard header="可复制、默认展开3层、显示边框、事件处理" class="mt-4">
      <JsonViewer
        :value="json2"
        :expand-depth="3"
        copyable
        :sort="false"
        @key-click="handleKeyClick"
        @value-click="handleValueClick"
        @copied="handleCopied"
        boxed
      />
    </ElCard>
    <ElCard header="预览模式" class="mt-4">
      <JsonViewer :value="json2" copyable preview-mode :show-array-index="false" />
    </ElCard>
  </Page>
</template>
