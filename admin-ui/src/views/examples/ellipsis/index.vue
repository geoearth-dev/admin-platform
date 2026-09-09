<script lang="ts" setup>
import { ref } from 'vue';

import { EllipsisText } from '@/components/ellipsis-text';
import { Page } from '@/components/page';

import { ElCard } from 'element-plus';

import DocButton from '../doc-button.vue';

const longText = `Vben Admin 是一个基于 Vue3.0、Vite、 TypeScript 的后台解决方案，目标是为开发中大型项目提供开箱即用的解决方案。包括二次封装组件、utils、hooks、动态菜单、权限校验、多主题配置、按钮级别权限控制等功能。项目会使用前端较新的技术栈，可以作为项目的启动模版，以帮助你快速搭建企业级中后台产品原型。也可以作为一个示例，用于学习 vue3、vite、ts 等主流技术。该项目会持续跟进最新技术，并将其应用在项目中。Vben Admin 是一个基于 Vue3.0、Vite、 TypeScript 的后台解决方案，目标是为开发中大型项目提供开箱即用的解决方案。包括二次封装组件、utils、hooks、动态菜单、权限校验、多主题配置、按钮级别权限控制等功能。项目会使用前端较新的技术栈，可以作为项目的启动模版，以帮助你快速搭建企业级中后台产品原型。也可以作为一个示例，用于学习 vue3、vite、ts 等主流技术。该项目会持续跟进最新技术，并将其应用在项目中。Vben Admin 是一个基于 Vue3.0、Vite、 TypeScript 的后台解决方案，目标是为开发中大型项目提供开箱即用的解决方案。包括二次封装组件、utils、hooks、动态菜单、权限校验、多主题配置、按钮级别权限控制等功能。项目会使用前端较新的技术栈，可以作为项目的启动模版，以帮助你快速搭建企业级中后台产品原型。也可以作为一个示例，用于学习 vue3、vite、ts 等主流技术。该项目会持续跟进最新技术，并将其应用在项目中。Vben Admin 是一个基于 Vue3.0、Vite、 TypeScript 的后台解决方案，目标是为开发中大型项目提供开箱即用的解决方案。包括二次封装组件、utils、hooks、动态菜单、权限校验、多主题配置、按钮级别权限控制等功能。项目会使用前端较新的技术栈，可以作为项目的启动模版，以帮助你快速搭建企业级中后台产品原型。也可以作为一个示例，用于学习 vue3、vite、ts 等主流技术。该项目会持续跟进最新技术，并将其应用在项目中。`;

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
        住在我心里孤独的 孤独的海怪 痛苦之王 开始厌倦 深海的光 停滞的海浪
        <template #tooltip>
          <div style="text-align: center">
            《秦皇岛》<br />住在我心里孤独的<br />孤独的海怪 痛苦之王<br />开始厌倦 深海的光
            停滞的海浪
          </div>
        </template>
      </EllipsisText>
    </ElCard>
  </Page>
</template>
