<script setup lang="ts">
import type { UserProfileResult } from '@/types/base/api/system/user';
import { computed, onMounted, ref } from 'vue';
import { ElButton, ElEmpty, ElSkeleton } from 'element-plus';
import { getUserProfile } from '@/api/system/user';
import { resolveFileUrl } from '@/api/common/file';
import { Profile } from '@/components/profile';
import { useUserStore } from '@/store';
import ProfileAvatar from './avatar-setting.vue';
import ProfileBase from './base-setting.vue';
import ProfileNotificationSetting from './notification-setting.vue';
import ProfilePasswordSetting from './password-setting.vue';
import ProfileSecuritySetting from './security-setting.vue';

const userStore = useUserStore();
const tabsValue = ref('basic');
const profile = ref<UserProfileResult>();
const loading = ref(true);
const displayProfile = computed(() => profile.value ? {
  ...profile.value, avatar: resolveFileUrl(profile.value.avatar ?? ''),
} : null);
const tabs = [
  { label: '基本设置', value: 'basic' },
  { label: '安全设置', value: 'security' },
  { label: '修改密码', value: 'password' },
  { label: '新消息提醒', value: 'notice' },
];

function onProfileUpdated(updated: UserProfileResult) {
  profile.value = updated;
  const current = userStore.userInfo;
  if (current && current.userId === updated.id) {
    userStore.setUserInfo({
      ...current,
      nickName: updated.nickName ?? '',
      email: updated.email ?? '',
      avatar: updated.avatar ?? '',
    });
  }
}

async function loadProfile() {
  loading.value = true;
  try {
    onProfileUpdated(await getUserProfile());
  } catch {
    profile.value = undefined;
  } finally {
    loading.value = false;
  }
}

onMounted(loadProfile);
</script>

<template>
  <Profile v-model="tabsValue" title="个人中心" :user-info="displayProfile" :tabs="tabs">
    <template v-if="profile" #avatar>
      <ProfileAvatar :avatar="profile.avatar" @updated="onProfileUpdated" />
    </template>
    <template #content>
      <ElSkeleton v-if="loading" :rows="6" animated />
      <ElEmpty v-else-if="!profile" description="个人资料加载失败">
        <ElButton type="primary" @click="loadProfile">重新加载</ElButton>
      </ElEmpty>
      <template v-else>
        <ProfileBase v-if="tabsValue === 'basic'" :profile="profile" @updated="onProfileUpdated" />
        <ProfileSecuritySetting v-if="tabsValue === 'security'" />
        <ProfilePasswordSetting v-if="tabsValue === 'password'" />
        <ProfileNotificationSetting v-if="tabsValue === 'notice'" />
      </template>
    </template>
  </Profile>
</template>
