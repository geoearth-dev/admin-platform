<script lang="ts" setup>
import { ref } from 'vue';

import { EllipsisText } from '@/components/ellipsis-text';
import { Page } from '@/components/page';

import { ElCard } from 'element-plus';

import DocButton from '../doc-button.vue';

const longText = `清晨的阳光越过山脊，沿着蜿蜒的河流洒向城市。街道两旁的树木随风轻轻摇动，远处的列车穿过桥梁，带着旅人驶向新的目的地。午后，云层缓缓散开，山间的小路露出清晰的轮廓，人们沿着步道走进森林，在溪水边停下脚步，听水流经过岩石的声音。傍晚时分，湖面映出橙色的天空，岸边的灯光逐渐亮起，白天热闹的广场也慢慢安静下来。有人记录沿途的风景，有人整理一天的见闻，把细小而平常的瞬间留在笔记里。夜色降临后，城市与远山融入同一片星空，旅途中的故事仍在继续。每一次出发都能遇见不同的景色，而那些值得回望的片段，常常就藏在不经意的停留之间。`;

const text = ref(longText);
</script>

<template>
  <Page description="用于多行文本省略，支持点击展开和自定义内容。" title="文本省略组件示例">
    <template #extra>
      <DocButton class="mb-2" path="/components/common-ui/vben-ellipsis-text" />
    </template>
    <ElCard class="mb-4" header="基本使用">
      <EllipsisText :max-width="500">{{ text }}</EllipsisText>
    </ElCard>

    <ElCard class="mb-4" header="多行省略">
      <EllipsisText :line="2">{{ text }}</EllipsisText>
    </ElCard>

    <ElCard class="mb-4" header="点击展开">
      <EllipsisText :line="3" expand>{{ text }}</EllipsisText>
    </ElCard>
    <ElCard class="mb-4" header="自定义内容">
      <EllipsisText :max-width="240">
        行程提醒：周六上午九点在公园东门集合，沿湖步行后前往观景台。
        <template #tooltip>
          <div style="text-align: center">
            行程提醒<br />集合时间：周六上午九点<br />集合地点：公园东门<br />
            游览路线：沿湖步道 → 观景台
          </div>
        </template>
      </EllipsisText>
    </ElCard>
  </Page>
</template>
