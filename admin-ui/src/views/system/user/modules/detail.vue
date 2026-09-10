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
  <Drawer :footer="false" :title="$t('common.detail')">
    <VbenDescriptions bordered :column="1" :items="items" />
  </Drawer>
</template>
