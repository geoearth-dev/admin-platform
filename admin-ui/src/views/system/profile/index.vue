<script setup lang="ts">
import type { ProfileUpdateParams, UserProfile } from '@/api/example/profile'
import { onMounted, ref } from 'vue'
import { ElAlert, ElButton, ElEmpty, ElSkeleton } from 'element-plus'
import { getUserProfileApi } from '@/api/example/profile'
import { Profile } from '@/components/profile'
import ProfileBase from './base-setting.vue'
import ProfileNotificationSetting from './notification-setting.vue'
import ProfilePasswordSetting from './password-setting.vue'
import ProfileSecuritySetting from './security-setting.vue'

const tabsValue = ref('basic')
const profile = ref<UserProfile>()
const loading = ref(true)
const tabs = [
  { label: '基本设置', value: 'basic' },
  { label: '安全设置', value: 'security' },
  { label: '修改密码', value: 'password' },
  { label: '新消息提醒', value: 'notice' },
]
async function loadProfile() {
  loading.value = true
  try {
    profile.value = await getUserProfileApi()
  } catch {
    // 保留加载失败的重试入口，方便后续替换成真实接口。
    profile.value = undefined
  } finally {
    loading.value = false
  }
}

function onProfileUpdated(values: ProfileUpdateParams) {
  if (profile.value) Object.assign(profile.value, values)
}

onMounted(loadProfile)
</script>

<template>
  <Profile
    v-model="tabsValue"
    title="个人中心"
    :user-info="profile ?? null"
    :tabs="tabs"
  >
    <template #content>
      <ElSkeleton
        v-if="loading"
        :rows="6"
        animated
      />
      <ElEmpty
        v-else-if="!profile"
        description="个人资料加载失败"
      >
        <ElButton
          type="primary"
          @click="loadProfile"
          >重新加载</ElButton
        >
      </ElEmpty>
      <template v-else>
        <ElAlert
          class="mb-6"
          type="info"
          :closable="false"
          title="当前为演示数据，修改仅在本次页面会话中有效，刷新后恢复。"
        />
        <ProfileBase
          v-if="tabsValue === 'basic'"
          :profile="profile"
          @updated="onProfileUpdated"
        />
        <ProfileSecuritySetting v-if="tabsValue === 'security'" />
        <ProfilePasswordSetting v-if="tabsValue === 'password'" />
        <ProfileNotificationSetting v-if="tabsValue === 'notice'" />
      </template>
    </template>
  </Profile>
</template>
