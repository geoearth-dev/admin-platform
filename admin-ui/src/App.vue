<template>
  <ElConfigProvider :locale="elementLocale">
    <RouterView />
  </ElConfigProvider>
</template>
<script setup lang="ts">
import { ElConfigProvider } from 'element-plus';
import { elementLocale } from '@/plugins/locale';
import { useElementPlusDesignTokens } from './plugins/effects/hooks/use-design-tokens';
useElementPlusDesignTokens();
</script>
