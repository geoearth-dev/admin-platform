<script setup lang="ts">
import type { NotificationSettings } from '@/api/example/profile'
import type { FormSchemaItem, SettingChange } from '@/components/profile'
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElSkeleton } from 'element-plus'
import {
  getNotificationSettingsApi,
  updateNotificationSettingsApi,
} from '@/api/example/profile'
import { ProfileNotificationSetting } from '@/components/profile'

const settings = ref<NotificationSettings>()
const saving = ref(false)
const formSchema = computed<FormSchemaItem[]>(() =>
  settings.value
    ? [
        {
          fieldName: 'accountPassword',
          label: '账户消息',
          description: '账户相关消息将以站内信的形式通知',
          value: settings.value.accountPassword,
        },
        {
          fieldName: 'systemMessage',
          label: '系统消息',
          description: '系统消息将以站内信的形式通知',
          value: settings.value.systemMessage,
        },
        {
          fieldName: 'todoTask',
          label: '待办任务',
          description: '待办任务将以站内信的形式通知',
          value: settings.value.todoTask,
        },
      ]
    : [],
)

onMounted(async () => {
  settings.value = await getNotificationSettingsApi()
})
async function handleChange({ fieldName, value }: SettingChange) {
  if (saving.value) return
  saving.value = true
  try {
    settings.value = await updateNotificationSettingsApi({ [fieldName]: value })
    ElMessage.success('演示消息设置已更新')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <section>
    <h2 class="mb-6 text-lg font-semibold">新消息提醒</h2>
    <ElSkeleton
      v-if="!settings"
      :rows="3"
      animated
    />
    <ProfileNotificationSetting
      v-else
      :form-schema="formSchema"
      :disabled="saving"
      @change="handleChange"
    />
  </section>
</template>
