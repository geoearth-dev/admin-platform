<script lang="ts" setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';

import { Page } from '@/components/page';
import { useTabs } from '@/plugins/effects/hooks';

const route = useRoute();

const { setTabTitle } = useTabs();

const index = computed(() => {
  return route.params?.id ?? -1;
});

setTabTitle(`No.${index.value} - 详情信息`);
</script>

<template>
  <Page :title="`标签页${index}详情页`">
    <template #description> {{ index }} - 详情页内容在此 </template>
  </Page>
</template>
