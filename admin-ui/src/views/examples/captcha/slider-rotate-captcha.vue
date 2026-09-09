<script setup lang="ts">
import { computed } from 'vue';

import { Page } from '@/components/page';
import { SliderRotateCaptcha } from '@/components/captcha';
import { preferences } from '@/plugins/preference';
import { useUserStore } from '@/store';

import { ElCard, ElMessage } from 'element-plus';

const userStore = useUserStore();
function handleSuccess() {
  ElMessage.success('success!');
}

const avatar = computed(() => {
  return userStore.userInfo?.avatar || preferences.app.defaultAvatar;
});
</script>

<template>
  <Page description="用于前端简单的拖动校验场景" title="滑块旋转校验">
    <ElCard class="mb-5" header="基本示例">
      <div class="flex-center p-4">
        <SliderRotateCaptcha :src="avatar" @success="handleSuccess" />
      </div>
    </ElCard>
  </Page>
</template>
