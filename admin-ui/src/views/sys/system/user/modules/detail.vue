<script lang="ts" setup>
import { computed, ref } from 'vue';

import { $t } from '@/plugins/locale';

import { useDescriptionItems } from '../data';
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui';
import type { SysUser } from '@/types/base/api/system/user';
import { VbenDescriptions } from '@/plugins/vben-ui/shadcn-ui';

const detailData = ref<SysUser>();

const items = computed(() => useDescriptionItems(detailData.value));

const [Drawer, drawerApi] = useVbenDrawer({
  onOpenChange(isOpen) {
    if (isOpen) {
      detailData.value = drawerApi.getData<SysUser>();
    }
  },
});
</script>
<template>
  <Drawer
    :title="$t('common.detail')"
    :footer="false"
    class="w-[760px] max-w-full sm:max-w-[760px]"
  >
    <div v-if="detailData" class="space-y-8 p-3">
      <VbenDescriptions
        v-for="section in items"
        :key="section.key"
        :title="section.title"
        :items="section.items"
        :column="{ xs: 1, sm: 1, md: 2, lg: 2, xl: 2, xxl: 2, xxxl: 2 }"
        bordered
        :label-style="{
          width: '120px',
          whiteSpace: 'nowrap',
          textAlign: 'right',
          fontWeight: 'bold',
        }"
        :content-style="{
          overflowWrap: 'anywhere',
        }"
      >
        <template #title>
          <span class="text-base font-bold text-primary">
            {{ section.title }}
          </span>
        </template>
      </VbenDescriptions>
    </div>
  </Drawer>
</template>
