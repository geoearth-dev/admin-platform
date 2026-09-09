<script lang="ts" setup>
import { Fallback } from '@/components/fallback';
import { VbenButton } from '@/plugins/vben-ui/shadcn-ui';
import { X } from '@/assets/icons';
import { useTabs } from '@/plugins/effects/hooks';

const { closeCurrentTab } = useTabs();
</script>

<template>
  <Fallback
    description="当前路由在菜单中不可见"
    status="coming-soon"
    title="被隐藏的子菜单"
    show-back
  >
    <template #action>
      <VbenButton size="lg" @click="closeCurrentTab()">
        <X class="mr-2 size-4" />
        关闭当前标签页
      </VbenButton>
    </template>
  </Fallback>
</template>
